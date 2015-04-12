package org.jglr.ns;

import java.io.*;
import java.util.*;

import org.jglr.ns.compiler.*;
import org.jglr.ns.compiler.VariablePointer.VariablePointerMode;
import org.jglr.ns.compiler.refactor.KeywordToken;
import org.jglr.ns.compiler.refactor.NSCodeToken;
import org.jglr.ns.compiler.refactor.NSTokenType;
import org.jglr.ns.compiler.refactor.OperatorToken;
import org.jglr.ns.insns.*;
import org.jglr.ns.types.*;

public class NSCompiler implements NSOps {

    private NSPreprocessor preprocessor;
    private Stack<Boolean> constantStack;
    private int index;
    private String source;
    private int line;
    private int labelID;
    private String labelBase;
    private NSType pendingType;
    private int varId;
    private int currentVariable;
    private VariablePointer varPointer;
    private HashMap<String, Integer> varName2Id;
    private String namespace; // FIXME: USE ME PLZ
    private NSFuncDef currentMethodDef;
    private boolean inFunctionDef;
    private NSFuncDef rootMethod;
    private NSClass clazz;
    private Stack<NSType> typeStack;
    private HashMap<String, NSType> varName2Type;
    private Stack<CompilerState> compilerStates;
    private Stack<LoopStartingPoint> loopStartStack;
    private boolean inComment = false;
    private Map<String, NSVariable> localMap;
    private Stack<Map<String, NSVariable>> locals;

    public NSCompiler() {
        NSOps.initAllNames();
        NSTypes.initAllTypes();
        varPointer = new VariablePointer();
        varPointer.pushMode(VariablePointerMode.VARIABLE);
        this.namespace = "std";
        labelBase = "L";
        varName2Id = new HashMap<>();
        varName2Type = new HashMap<>();
        compilerStates = new Stack<>();
        loopStartStack = new Stack<>();
        constantStack = new Stack<>();
        localMap = new HashMap<>();
        locals = new Stack<>();
        rootMethod = currentMethodDef = (NSFuncDef) new NSFuncDef().name(NSFuncDef.ROOT_ID);
        varId = 1; // We start at 1 because 0 represents 'self'

        preprocessor = new NSPreprocessor();
    }

    public NSClass compile(String name, String source) throws NSCompilerException, IOException {
        return compile(new NSSourceFile(name, new ByteArrayInputStream(source.getBytes())));
    }

    public NSClass compile(NSSourceFile source) throws NSCompilerException, IOException {
        this.clazz = new NSClass(source.name().substring(0, source.name().indexOf("."))).sourceFile(source.name());
        clazz.rootMethod(rootMethod.owner(clazz.name()));
        this.source = source.content();
        this.source = preprocessor.preprocess(source);
        this.index = 0;
        this.line = 0;
        NSCodeToken token;
        ArrayList<NSCodeToken> tokenList = new ArrayList<NSCodeToken>();
        List<NSInsn> finalInstructions = null;
        int lineNumber = 1;
        while ((token = nextToken()) != null) {
            finalInstructions = currentMethodDef.instructions();
            if (finalInstructions.isEmpty())
                finalInstructions.add(new LineNumberInsn(1));

            if (token.type == NSTokenType.NEW_LINE) {
                finalInstructions.add(new LineNumberInsn(++lineNumber));
            } else if (token.type == NSTokenType.KEYWORD && token.content.equals(NSKeywords.COMMENT_START.raw())) {
                inComment = true;
            } else if (!inComment) {
                if(token.type == NSTokenType.KEYWORD && ((KeywordToken)token).keyword() == NSKeywords.ELIF) {
                    makeInstructions(tokenList, finalInstructions);
                    typeStack.clear();
                    if (!tokenList.isEmpty()) {
                        throwCompilerException("Missing semicolon.");
                    }

                    popLabelID();
                    tokenList.add(token);
                } else if (token.createsNewLabel()) {
                    if (token.type != NSTokenType.INSTRUCTION_END)
                        tokenList.add(token);
                    makeInstructions(tokenList, finalInstructions);
                    typeStack.clear();
                    if (!tokenList.isEmpty()) {
                        throwCompilerException("Missing semicolon.");
                    }
                } else
                    tokenList.add(token);
            }
        }
        setupLocalEndLbl(localMap, true);
        if (!tokenList.isEmpty()) {
            throwCompilerError("Instruction stack isn't empty. Problem while reading the source code.", null);
        }
        // We analyse the function calls to find if the owner is this class or another
        Label currentLabel = null;
        for (NSAbstractMethod m : clazz.methods()) {
            if (m instanceof NSFuncDef) {
                NSFuncDef method = (NSFuncDef) m;
                for (NSInsn insn : method.instructions()) {
                    if (insn.getOpcode() == LABEL) {
                        LabelInsn labelInsn = (LabelInsn)insn;
                        currentLabel = labelInsn.label();
                    } else if (insn.getOpcode() == FUNCTION_CALL) {
                        FunctionCallInsn callInsn = (FunctionCallInsn) insn;
                        if(callInsn.functionOwner().equals(FunctionCallInsn.UNKNOWN_YET)) {
                            NSAbstractMethod calledMethod = null;
                            try {
                                calledMethod = clazz.method(callInsn.functionName(), callInsn.types());
                            } catch (Exception e) {
                                // We ignore this exception as there's one only if the method doesn't exist
                            }

                            if (calledMethod != null) {
                                callInsn.functionOwner(clazz.name());
                            } else {
                                // TODO: Imports maybe ?
                                callInsn.functionOwner("std");
                                if (!callInsn.types().isEmpty()) {
                                    NSType type = callInsn.types().get(0);
                                    if (type.functions().containsKey(callInsn.functionName())) {
                                        callInsn.functionOwner(type.getID());
                                    } else
                                        ; // TODO: Check for user-defined types
                                }
                            }
                        }
                    }

                }
                if(currentLabel != null)
                    while(currentLabel.id().contains(SUB_LABEL_SEPARATOR)) {
                        Label newLabel = new Label(currentLabel.id().substring(0, currentLabel.id().indexOf(SUB_LABEL_SEPARATOR)));
                        method.instructions().add(new LabelInsn(newLabel));
                        currentLabel = newLabel;
                    }
            }
        }

        return clazz;
    }

    private String preprocess(NSSourceFile source) throws IOException {
        String content = source.content();
        
        return content;
    }

    private NSCodeToken nextToken() throws NSCompilerException {
        try {
            char[] chars = source.toCharArray();
            StringBuffer buffer = new StringBuffer();
            boolean inString = false;
            boolean inNumber = false;
            NSTokenType type = null;
            for (; index < chars.length; index++) {
                boolean append = true;
                boolean breakAfter = false;
                NSCodeToken op = null;
                if (chars[index] == '\"') {
                    inString = !inString;
                    append = false;
                    if (!inString) {
                        breakAfter = true;
                        type = NSTokenType.STRING;
                    }
                } else if(chars[index] == '\r') {
                    index++;
                    return nextToken();
                } else if (!inString && chars[index] == '\n') {
                    if (!buffer.toString().isEmpty()) {
                        if(isKeyword(buffer))
                            return createKeywordInsn(buffer);
                        if(isOperator(buffer)) {
                            return createOperatorInsn(buffer);
                        }
                        return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                    }
                    index++;
                    line++;
                    inComment = false;
                    return new NSCodeToken("", NSTokenType.NEW_LINE);
                } else if (!inString && !inComment) {
                    if (chars[index] >= '0' && chars[index] <= '9') {
                        if (buffer.toString().isEmpty())
                            inNumber = true;
                    } else if (!(chars[index] == '.' && inNumber) && (op = getOperator(buffer, chars)) != null) {
                        return op;
                    } else if (chars[index] == ' ') {
                        if (!buffer.toString().isEmpty()) {
                            if(isKeyword(buffer))
                                return createKeywordInsn(buffer);
                            if(isOperator(buffer)) {
                                return createOperatorInsn(buffer);
                            }
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        index++;
                        return nextToken();
                    } else if (chars[index] == '\t') {
                        if (!buffer.toString().isEmpty()) {
                            if(isKeyword(buffer))
                                return createKeywordInsn(buffer);
                            if(isOperator(buffer)) {
                                return createOperatorInsn(buffer);
                            }
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        index++;
                        return nextToken();
                    } else if (chars[index] == ',') {
                        if (!buffer.toString().isEmpty()) {
                            if(isKeyword(buffer))
                                return createKeywordInsn(buffer);
                            if(isOperator(buffer)) {
                                return createOperatorInsn(buffer);
                            }
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        index++;
                        return nextToken();
                    } else if (chars[index] == '(') {
                        if (!buffer.toString().isEmpty()) {
                            if(isKeyword(buffer))
                                return createKeywordInsn(buffer);
                        }
                        if(isOperator(buffer)) {
                            return createOperatorInsn(buffer);
                        }
                        index++;
                        return new NSCodeToken(buffer.toString(), NSTokenType.OPEN_PARENTHESIS);
                    } else if (chars[index] == ')') {
                        if (!buffer.toString().isEmpty()) {
                            if(isKeyword(buffer))
                                return createKeywordInsn(buffer);
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        if(isOperator(buffer)) {
                            return createOperatorInsn(buffer);
                        }

                        index++;
                        return new NSCodeToken("", NSTokenType.CLOSE_PARENTHESIS);
                    } else if (chars[index] == ';') {
                        if (!buffer.toString().isEmpty()) {
                            if(isKeyword(buffer))
                                return createKeywordInsn(buffer);
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        breakAfter = true;
                        type = NSTokenType.INSTRUCTION_END;
                    }
                }

                if (append) {
                    buffer.append(chars[index]);
                }
                if (breakAfter) {
                    index++;
                    break;
                }
            }
            if (index >= chars.length - 1 && buffer.length() == 0 && type == null)
                return null;
            if (type == null) {
                if (inString) {
                    throwCompilerException("Missing end of string : \"" + buffer.toString() + "\"");
                }
                if (!buffer.toString().isEmpty())
                    if(isKeyword(buffer))
                        return createKeywordInsn(buffer);
                throwCompilerException("Type is null. Content is \"" + buffer.toString() + "\"");
            }
            return new NSCodeToken(buffer.toString(), type);
        } catch (Exception e) {
            if (e instanceof NSCompilerException)
                throw e;
            throw new NSCompilerException("Error while compiling.", e);
        }
    }

    private NSCodeToken createOperatorInsn(StringBuffer buffer) {
        for (NSOperator operator : NSOperator.values()) {
            if (operator.toString().equals(buffer.toString())) {
                index += operator.toString().length(); // We offset the index by the length of the operator
                return new OperatorToken(operator);
            }
        }
        return null;
    }

    private boolean isOperator(StringBuffer buffer) {
        for (NSOperator operator : NSOperator.values()) {
            if (operator.toString().equals(buffer.toString())) {
                return true;
            }
        }
        return false;
    }

    private NSCodeToken createKeywordInsn(StringBuffer buffer) {
        for (NSKeywords keyword : NSKeywords.values()) {
            if (keyword.raw().equals(buffer.toString())) {
                return new KeywordToken(keyword);
            }
        }
        return null;
    }

    private boolean isKeyword(StringBuffer buffer) {
        String raw = buffer.toString();
        for (NSKeywords keyword : NSKeywords.values()) {
            if(keyword.raw().equals(raw)) {
                return true;
            }
        }
        return false;
    }

    private NSCodeToken getOperator(StringBuffer buffer, char[] chars) {
        @SuppressWarnings("unchecked")
        ArrayList<NSOperator> operators = (ArrayList<NSOperator>) NSOperator.list().clone();
        NSOperator foundOperator = null;
        Collections.sort(operators, (a, b) -> -Integer.compare(a.name().length(), b.name().length())); // We want the operators from longer to shorter
        if (buffer.toString().isEmpty())
            for (NSKeywords keyword : NSKeywords.values()) {
                if (source.indexOf(keyword.raw(), index) == index) {
                    index += keyword.raw().length();
                    return new KeywordToken(keyword);
                }
            }
        for (NSOperator operator : operators) {
            if (source.indexOf(operator.toString(), index) == index) {
                if (foundOperator == null)
                    foundOperator = operator;
                else if (foundOperator.toString().length() < operator.toString().length())
                    foundOperator = operator;
            }
        }
        if (foundOperator == null)
            return null;
        if (!buffer.toString().isEmpty()) {
            for (NSKeywords keyword : NSKeywords.values()) {
                if (keyword.raw().equals(buffer.toString())) {
                    return new KeywordToken(keyword);
                }
            }
            for (NSOperator operator : operators) {
                if (operator.toString().equals(buffer.toString())) {
                    index += operator.toString().length(); // We offset the index by the length of the operator
                    return new OperatorToken(operator);
                }
            }
            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
        }
        index += foundOperator.toString().length(); // We offset the index by the length of the operator
        return new OperatorToken(foundOperator);
    }

    public ArrayList<NSCodeToken> toRPN(List<NSCodeToken> nSCodeTokens) {
        ArrayList<NSCodeToken> outputQueue = new ArrayList<NSCodeToken>();
        Stack<NSCodeToken> operatorStack = new Stack<NSCodeToken>();
        for (NSCodeToken nSCodeToken : nSCodeTokens) {
            if (nSCodeToken.type == NSTokenType.OPEN_PARENTHESIS || nSCodeToken.type == NSTokenType.KEYWORD) {
                operatorStack.push(nSCodeToken);
            } else if (nSCodeToken.type == NSTokenType.CLOSE_PARENTHESIS) {
                while (!operatorStack.isEmpty()) {
                    NSCodeToken operator = operatorStack.pop();
                    if (operator.type == NSTokenType.OPEN_PARENTHESIS) {
                        if (!operator.content.isEmpty()) {
                            outputQueue.add(new NSCodeToken(operator.content, NSTokenType.FUNCTION_CALL));
                        }
                        break;
                    } else if (operator.type == NSTokenType.KEYWORD) {
                        outputQueue.add(operator);
                        break;
                    } else {
                        outputQueue.add(operator);
                    }
                }
            } else {
                if (isTokenOperator(nSCodeToken)) {
                    while (!operatorStack.isEmpty()) {
                        NSCodeToken operator = operatorStack.peek();
                        if (getPrecedence(operator) >= getPrecedence(nSCodeToken)) {
                            outputQueue.add(operatorStack.pop());
                        } else {
                            break;
                        }
                    }
                    operatorStack.push(nSCodeToken);
                } else {
                    outputQueue.add(nSCodeToken);
                }
            }
        }
        while (!operatorStack.isEmpty()) {
            NSCodeToken operator = operatorStack.pop();
            if (operator.type == NSTokenType.OPEN_PARENTHESIS) {
                if (!operator.content.isEmpty()) {
                    outputQueue.add(new NSCodeToken(operator.content, NSTokenType.FUNCTION_CALL));
                }
                continue;
            } else if (operator.type == NSTokenType.CLOSE_PARENTHESIS) {
                continue;
            }
            outputQueue.add(operator);
        }
        return outputQueue;
    }

    public boolean isTokenOperator(NSCodeToken nSCodeToken) {
        return (nSCodeToken.type == NSTokenType.OPERATOR && nSCodeToken instanceof OperatorToken)
                || (nSCodeToken.type == NSTokenType.KEYWORD && nSCodeToken instanceof KeywordToken);
    }

    public int getPrecedence(NSCodeToken nSCodeToken) {
        if (nSCodeToken.type == NSTokenType.OPERATOR) {
            OperatorToken operator = (OperatorToken) nSCodeToken;
            return operator.operator().precedence();
        } else if (nSCodeToken.type == NSTokenType.KEYWORD) {
            KeywordToken keyword = (KeywordToken) nSCodeToken;
            return keyword.keyword().precedence();
        }
        return 0;
    }

    private void throwCompilerError(String string, Exception cause) throws NSCompilerError {
        throw new NSCompilerError(string, cause);
    }

    private void throwCompilerException(String string) throws NSCompilerException {
        throw new NSCompilerException(string + " (at line " + line + ")");
    }

    private void makeInstructions(ArrayList<NSCodeToken> tokenList, List<NSInsn> insnList) throws NSCompilerException {
        insnList.add(new LabelInsn(new Label(nextLabelID())));
        pendingType = null;
        typeStack = new Stack<>();
        if (tokenList.size() == 1) {
            NSCodeToken token = tokenList.remove(tokenList.size() - 1);
            if (token.type == NSTokenType.STRING) {
                insnList.add(new LoadConstantInsn(token.content));
                List<NSType> types = new ArrayList<>();
                types.add(NSTypes.STRING_TYPE);
                insnList.add(new FunctionCallInsn("print").functionOwner("std").types(types));
            } else if (token.type == NSTokenType.WORD && !inFunctionDef) {
                int vindex = -1;
                if (varName2Id.containsKey(token.content)) {
                    vindex = varName2Id.get(token.content);
                }
                if (vindex >= 0) {
                    insnList.add(new NSVarInsn(VAR_LOAD, vindex));
                    List<NSType> types = new ArrayList<>();
                    types.add(NSTypes.STRING_TYPE);
                    insnList.add(new FunctionCallInsn("print").functionOwner("std").types(types));
                } else {
                    throwCompilerException("Unexpected symbol: " + token.content);
                }
            } else {
                handleToken(token, 0, new ArrayList<>(), insnList);
            }
        } else {
            List<NSCodeToken> rpnList = toRPN(tokenList);
            tokenList.clear();
            int index = 0;
            for (NSCodeToken nSCodeToken : rpnList) {
                try {
                    handleToken(nSCodeToken, index++, rpnList, insnList);
                } catch(Exception e) {
                    throwCompilerError("Error while parsing code", e);
                }
            }
        }
    }

    private void handleToken(NSCodeToken token, int tokenIndex, List<NSCodeToken> tokenList, List<NSInsn> insnList)
            throws NSCompilerException {
        System.out.println(">> " + token.type.name() + " : " + token.content);
        switch (token.type) {
            case WORD:
                NSType foundType = getType(token.content);
                if(foundType != null) {
                    pendingType = foundType;
                    if (inFunctionDef) {
                        currentMethodDef.types().add(foundType);
                    }
                    return;
                }
                boolean justCreated = false;
                if (isNumber(token.content)) {
                    try {
                        int value = Integer.parseInt(token.content);
                        loadIntInsn(insnList, value, typeStack);
                    } catch (Exception e) { // That means we have a number that is not an integer
                        float value = Float.parseFloat(token.content);
                        loadFloatInsn(insnList, value, typeStack);
                    }
                } else {
                    int varIndex = -1;
                    if (pendingType != null) {
                        if (inFunctionDef) {

                            currentMethodDef.paramNames().add(token.content);
                            varIndex = nextVarIndex();
                            NSVariable variable = new NSVariable(pendingType, token.content, varIndex, pendingType.emptyObject());
                            variable.startLabel(new Label(getPreviousLabelID()));
                            localMap.put(token.content, variable);
                            varName2Type.put(token.content, pendingType);
                            varName2Id.put(token.content, varIndex);

                            System.out.println("NEW PARAM: "+variable.name()+", "+variable.type().getID()+", "+variable.varIndex());

                        } else {
                            varIndex = nextVarIndex();
                            NSVariable variable = new NSVariable(pendingType, token.content, varIndex, pendingType.emptyObject());
                            variable.startLabel(new Label(getPreviousLabelID()));
                            insnList.add(new NewVarInsn(variable));
                            localMap.put(token.content, variable);
                            varName2Type.put(token.content, pendingType);
                            varName2Id.put(token.content, varIndex);
                            System.out.println(token.content);
                        }
                        justCreated = true;
                        pendingType = null;
                    } else {
                        if (varName2Id.containsKey(token.content)) {
                            varIndex = varName2Id.get(token.content);
                            System.out.println("Loaded "+varIndex+" <= "+token.content);
                        }
                    }
                    if (!inFunctionDef) {
                        if (clazz.field(token.content) != null) {
                            NSType type = varName2Type.get(token.content); // FIXME: Use something else than varName2Type, this code relies on a glitch
                            if(!justCreated) {
                                insnList.add(new NSFieldInsn(FIELD_LOAD, clazz.name(), token.content));
                                typeStack.add(type);
                                constantStack.push(false);
                            }
                            varPointer.pushField(new FieldInfo(token.content, clazz.name(), type));
                        } else {
                            varPointer.pushVariable(varIndex);
                            if(!justCreated) {
                                insnList.add(new NSVarInsn(VAR_LOAD, varIndex));
                                typeStack.push(varName2Type.get(token.content));
                                constantStack.push(false);
                            }
                        }
                    }
                }
                break;

            case STRING:
                insnList.add(new LoadConstantInsn(token.content));
                typeStack.push(NSTypes.STRING_TYPE);
                constantStack.push(true);
                break;

            case OPERATOR:
                NSOperator operator = ((OperatorToken) token).operator();
                switch(operator) {
                    case MEMBER_ACCESS:
                        NSInsn previous = insnList.get(insnList.size() - 1);
                        if (previous.getOpcode() == FUNCTION_CALL) {
                            FunctionCallInsn callInsn = (FunctionCallInsn) previous;
                            NSType previousType = typeStack.pop();
                            constantStack.pop();
                            callInsn.functionOwner(previousType.getID());
                            System.out.println(">>>>>>>> "+callInsn.functionOwner()+"."+callInsn.functionName());
                            HashMap<String, NSAbstractMethod> functions = previousType.functions();
                            NSType returnType = functions.get(callInsn.functionName()).returnType();
                            typeStack.push(returnType);
                            constantStack.push(false);
                        } else if (previous.getOpcode() == VAR_LOAD) { // VAR_LOAD -1 if everything's okay
                            String id = "Object";
                            varPointer.popValue(); // We remove the invalid variable on top of the variable stack
                            if (varPointer.isVar()) {
                                if (varPointer.peekVarId() == -1) {
                                    throwCompilerException("Tried to access a field of a non-existing field/variable.");
                                }
                                id = varName2Type.get(nameOf(varPointer.peekVarId())).getID();
                            } else {
                                if (varPointer.peekField() == null) {
                                    throwCompilerException("Tried to access a field of a non-existing field/variable.");
                                }
                                id = varPointer.peekField().type().getID();
                            }
                            String fieldName = tokenList.get(tokenIndex - 1).content;
                            insnList.set(insnList.size() - 1, new NSFieldInsn(GET_FIELD, id, fieldName));
                            System.err.println("{{{ content = " + fieldName + " ; id = " + id);
                            constantStack.pop();
                            typeStack.pop();
                            typeStack.push(NSTypes.fromIDOrDummy(id).emptyObject().field(fieldName).type());
                            constantStack.push(true);
                        }
                        break;

                    case ASSIGNMENT:
                        if (varPointer.isVar()) {
                            if (varPointer.peekVarId() == -1) {
                                throwCompilerException("Tried to assign a value to an object that is not a field or a variable.");
                            } else {
                                insnList.add(new NSVarInsn(VAR_STORE, varPointer.peekVarId()));
                                typeStack.pop();
                                constantStack.pop();
                            }
                            varPointer.popValue();
                        } else if (varPointer.isField()) {
                            if (varPointer.peekField() == null) {
                                throwCompilerException("Tried to assign a value to an object that is not a field or a variable.");
                            } else {
                                FieldInfo infos = varPointer.peekField();
                                insnList.add(new NSFieldInsn(FIELD_SAVE, infos.owner(), infos.name()));
                                typeStack.pop();
                                constantStack.pop();
                            }
                            varPointer.popValue();
                        } else
                            throwCompilerException("Tried to assign a value to an object that is not a variable.");
                        break;

                    case INCREMENT:
                    case DECREMENT:
                        if (varPointer.isVar()) {
                            if (varPointer.peekVarId() == -1) {
                                throwCompilerException("Invalid argument for operator ++/--");
                            } else {
                                insnList.add(new NSLoadIntInsn(1));
                                insnList.add(new OperatorInsn(operator == NSOperator.INCREMENT ? NSOperator.PLUS : NSOperator.MINUS));
                                insnList.add(new NSVarInsn(VAR_STORE, varPointer.peekVarId()));
                                insnList.add(new NSVarInsn(VAR_LOAD, varPointer.peekVarId()));
                            }
                            varPointer.popValue();
                        } else if (varPointer.isField()) {
                            if (varPointer.peekField() == null) {
                                throwCompilerException("Invalid argument for operator ++/--");
                            } else {
                                insnList.add(new NSLoadIntInsn(1));
                                insnList.add(new OperatorInsn(operator == NSOperator.INCREMENT ? NSOperator.PLUS : NSOperator.MINUS));
                                FieldInfo infos = varPointer.peekField();
                                insnList.add(new NSFieldInsn(FIELD_LOAD, infos.owner(), infos.name()));
                                insnList.add(new NSFieldInsn(FIELD_SAVE, infos.owner(), infos.name()));
                            }
                            varPointer.popValue();
                        }
                        break;

                    case RANGE:
                        typeStack.pop();
                        typeStack.pop();
                        loadClassInstanceInsn(insnList, "Range", NSTypes.FLOAT_TYPE, NSTypes.FLOAT_TYPE);
                        typeStack.push(NSTypes.RANGE_TYPE);
                        break;

                    default:
                        System.out.println(">>>>>>>>>> "+operator.name());
                        if(!constantStack.pop())
                            varPointer.popValue();
                        NSType lastType = typeStack.pop();
                        if(!constantStack.pop())
                            varPointer.popValue();
                        NSType type = typeStack.pop(); // We get the type right before the last type loaded
                        try {
                            NSObject result = type.operation(type.emptyObject(), lastType.emptyObject(), operator);
                            typeStack.push(result.type());
                            constantStack.push(true);
                            insnList.add(new OperatorInsn(operator));
                        } catch(ArithmeticException ar) {
                            typeStack.push(type);
                            constantStack.push(true);
                            insnList.add(new OperatorInsn(operator));
                          // Everything's fine, the world's not burning down.
                        } catch(Exception e) {
                            e.printStackTrace();
                            throwCompilerError("Error while handling operation ("+type.getID()+" "+operator+" "+lastType.getID()+")", e);
                        }
                        break;
                    }
                    break;

            case FUNCTION_CALL:
                if (inFunctionDef) {
                    currentMethodDef.name(token.content);
                } else {
                    insnList.add(new FunctionCallInsn(token.content).functionOwner(FunctionCallInsn.UNKNOWN_YET).types(typeStack));
                }
                break;

            case KEYWORD:
                KeywordToken keyword = (KeywordToken) token;
                switch (keyword.keyword()) {
                    case IF:
                        typeStack.pop();
                        insnList.add(new StackInsn(STACK_PUSH));
                        insnList.add(new StackInsn(STACK_PEEK));
                        insnList.add(new LabelInsn(IF_NOT_GOTO, new Label(getCurrentLabelID())));
                        pushLabelID();
                        loopStartStack.push(new LoopStartingPoint(getCurrentLabelID(), NSKeywords.IF));
                        break;

                    case ELSE:
                        popLabelID();
                        checkMatchingLoopStart(loopStartStack.pop(), "else");
                        insnList.add(new LabelInsn(new Label(nextLabelID())));
                        insnList.add(new StackInsn(STACK_PEEK));
                        insnList.add(new LabelInsn(IF_GOTO, new Label(getCurrentLabelID())));
                        pushLabelID();
                        loopStartStack.push(new LoopStartingPoint(getCurrentLabelID(), NSKeywords.ELSE));
                        break;

                    case ELIF:
                        // popLabelID();
                        checkMatchingLoopStart(loopStartStack.pop(), "elif");
                        // insnList.add(new LabelInsn(new Label(nextLabelID())));
                        insnList.add(new StackInsn(STACK_PEEK));
                        insnList.add(new LabelInsn(IF_GOTO, new Label(getCurrentLabelID())));

                        typeStack.pop();
                        insnList.add(new StackInsn(STACK_PUSH));
                        insnList.add(new StackInsn(STACK_PEEK));
                        insnList.add(new LabelInsn(IF_NOT_GOTO, new Label(getCurrentLabelID())));

                        pushLabelID();
                        loopStartStack.push(new LoopStartingPoint(getCurrentLabelID(), NSKeywords.ELIF));
                        break;

                    case END:
                        insnList.add(new StackInsn(STACK_POP));
                        insnList.add(new NSBaseInsn(POP));
                        LoopStartingPoint point = loopStartStack.pop();
                        if (point.type() == NSKeywords.WHILE || point.type() == NSKeywords.UNTIL)
                            insnList.add(new LabelInsn(GOTO, new Label(point.labelID())));
                        popLabelID();
                        break;

                    case UNTIL:
                    case WHILE:
                        loopStartStack.push(new LoopStartingPoint(getPreviousLabelID(), NSKeywords.WHILE));

                        typeStack.pop();
                        insnList.add(new StackInsn(STACK_PUSH));
                        insnList.add(new StackInsn(STACK_PEEK));
                        if(keyword.keyword() == NSKeywords.WHILE)
                            insnList.add(new LabelInsn(IF_NOT_GOTO, new Label(getCurrentLabelID())));
                        else
                            insnList.add(new LabelInsn(IF_GOTO, new Label(getCurrentLabelID())));
                        pushLabelID();
                        break;

                    case TRUE:
                        loadBoolInsn(insnList, true, typeStack);
                        break;

                    case FALSE:
                        loadBoolInsn(insnList, false, typeStack);
                        break;

                    case NAMESPACE:
                        NSInsn prev = insnList.get(insnList.size() - 1);
                        if (prev.getOpcode() == LOAD_CONSTANT) {
                            LoadConstantInsn loadInsn = (LoadConstantInsn) prev;
                            Object cst = loadInsn.getConstant();
                            if (cst instanceof String) {
                                this.namespace = (String) cst;
                            }
                            insnList.remove(insnList.size() - 1);
                        } else {
                            throwCompilerException("Unexepected namespace identifier. Only string literals are allowed");
                        }
                        break;

                    case FUNCTION_DEF:
                        inFunctionDef = true;
                        pushState();
                        currentMethodDef = (NSFuncDef) new NSFuncDef().owner(clazz.name());
                        clazz.methods().add(currentMethodDef);
                        break;

                    case CODE_BLOCK_START:
                        if (inFunctionDef) {
                            inFunctionDef = false;
                            varId = 1;
                            varName2Id.clear();
                            for (int i = 0; i < currentMethodDef.paramNames().size(); i++) {
                                String name = currentMethodDef.paramNames().get(i);
                                NSType type = currentMethodDef.types().get(i);
                                varName2Id.put(name, varId);
                                varName2Type.put(name, type);
                            }
                        } else
                            throwCompilerException("You must be defining a method to use a code block starting point");
                        break;

                    case CODE_BLOCK_END:
                        setupLocalEndLbl(localMap);
                        popState();
                        break;

                    case RETURN:
                        if (typeStack != null) {
                            insnList.add(new NSBaseInsn(RETURN_VALUE));
                        } else
                            insnList.add(new NSBaseInsn(RETURN));
                        break;

                    case FIELD:
                        NSCodeToken typeToken = tokenList.get(tokenIndex - 2);
                        NSCodeToken nameToken = tokenList.get(tokenIndex - 1);
                        if (typeToken.type != NSTokenType.WORD || nameToken.type != NSTokenType.WORD)
                            throwCompilerException("Unexepected tokens after 'field': '" + typeToken.content + "' '" + nameToken.content + "'");
                        insnList.remove(insnList.size() - 2); // We remove the last NEW_VAR
                        insnList.remove(insnList.size() - 1); // We remove the last VAR_LOAD
                        int oldId = rollbackVarIndex(); // We remove the var id created for this variable
                        String oldName = removeName(oldId);
                        varName2Type.remove(oldName);
                        // NSType type = typeStack.pop(); // We remove the type pushed onto the stack

                        // Start of creating the field
                        NSType type = getType(typeToken.content);
                        clazz.field(type, nameToken.content);
                        varPointer.pushField(new FieldInfo(nameToken.content, clazz.name(), type));
                        break;

                    case IN:
                        // TODO
                        break;

                    default:
                        break;
                }
                break;

        default:
            break;
        }
    }

    private void loadClassInstanceInsn(List<NSInsn> insnList, String className, NSType... types) {
        insnList.add(new FunctionCallInsn("$", className).types(Arrays.asList(types)));
    }

    private void checkMatchingLoopStart(LoopStartingPoint startingPoint, String branching) throws NSCompilerException {
        if(startingPoint.type() != NSKeywords.IF && startingPoint.type() != NSKeywords.ELIF)
            throwCompilerException(branching+" branching can only be used after an 'if' or an 'elif' condition");
    }

    private void loadBoolInsn(List<NSInsn> insnList, boolean value, Stack<NSType> typeStack) {
        insnList.add(new LoadConstantInsn(value));
        typeStack.push(NSTypes.BOOL_TYPE);
        constantStack.push(true);
    }

    private void loadFloatInsn(List<NSInsn> insnList, float value, Stack<NSType> stack) {
        insnList.add(new NSLoadFloatInsn(value));
        stack.push(NSTypes.FLOAT_TYPE);
        constantStack.push(true);
    }

    private void loadIntInsn(List<NSInsn> insnList, int value, Stack<NSType> stack) {
        insnList.add(new NSLoadIntInsn(value));
        stack.push(NSTypes.INT_TYPE);
        constantStack.push(true);
    }

    private NSType getType(String id) {
        for (NSType type : NSTypes.list()) {
            if (type.getID().equals(id))
                return type;
        }
        return null;
    }

    private String nameOf(int asVarId) {
        Optional<String> name = varName2Id.entrySet().stream()
                .filter(entry -> entry.getValue() == asVarId)
                .map(entry -> entry.getKey())
                .findAny();
        return name.get();
    }

    private String removeName(int oldId) {
        final String[] name = {null};
        varName2Id.entrySet().removeIf(entry -> {
            name[0] = entry.getKey();
            return entry.getValue() == oldId;
        });
        return name[0];
    }

    private int rollbackVarIndex() {
        int copy = varId;
        varId--;
        return copy;
    }

    private boolean isNumber(String content) {
        Scanner sc = new Scanner(content);
        if(sc.hasNextInt() || sc.hasNextFloat()) {
            sc.next();
            return !sc.hasNext();
        }
        return false;
    }

    private int nextVarIndex() {
        return varId++;
    }

    private static final String SUB_LABEL_SEPARATOR = ".";

    private void popLabelID() throws NSCompilerException {
        setupLocalEndLbl(localMap);
        labelBase = labelBase.substring(0, labelBase.lastIndexOf(SUB_LABEL_SEPARATOR));
        int min = labelBase.lastIndexOf(SUB_LABEL_SEPARATOR) + 1;
        if (min < 0) {
            throwCompilerException("Unmatched bracket");
        }
        if(min == 0) {
            min = 1;
        }
        labelID = Integer.parseInt(labelBase.substring(min));
        labelBase = labelBase.substring(0, labelBase.length() - ("" + labelID).length());


        localMap = locals.pop();
    }

    private void setupLocalEndLbl(Map<String, NSVariable> map) {
        setupLocalEndLbl(map, false);
    }

    private void setupLocalEndLbl(Map<String, NSVariable> map, boolean includeLast) {
        map.entrySet().forEach(entry ->
        {
            String id;
            if(includeLast) {
                id = getCurrentLabelID();
            } else {
                id = getPreviousLabelID();
            }
            Label currentLabel = new Label(id);
            entry.getValue().endLabel(currentLabel);
        });
    }

    private void pushLabelID() {
        labelBase += labelID + SUB_LABEL_SEPARATOR;
        labelID = 0;

        locals.push(localMap);
        localMap = new HashMap<>();
    }

    private String getPreviousLabelID() {
        return labelBase + (labelID - 1);
    }

    private String getCurrentLabelID() {
        return labelBase + labelID;
    }

    private String nextLabelID() {
        return labelBase + (labelID++);
    }

    private void pushState() {
        compilerStates.push(new CompilerState(varName2Id, varName2Type, varId, labelBase, labelID, currentMethodDef));
        varName2Id.clear();
        varName2Type.clear();
        varId = 1;
        labelBase = "L";
        labelID = 0;
    }

    private void popState() {
        CompilerState state = compilerStates.pop();
        varName2Id = state.varNamesToIds();
        varName2Type = state.varNamesToTypes();
        varId = state.varID();
        labelID = state.labelID();
        labelBase = state.labelBase();
        currentMethodDef = state.currentMethodDef();
    }
}
