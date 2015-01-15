package org.jglr.ns;

import java.io.*;
import java.util.*;

import org.jglr.ns.compiler.*;
import org.jglr.ns.insns.*;
import org.jglr.ns.types.*;

public class NSCompiler implements NSOps
{

    private int                      index;
    private String                   source;
    private int                      line;
    private int                      labelID;
    private String                   labelBase;
    private NSType                   pendingType;
    private int                      varId;
    private int                      currentVariable;
    private HashMap<String, Integer> varName2Id;
    private String                   namespace;        // FIXME: USE ME PLZ
    private NSFuncDef                currentMethodDef;
    private boolean                  inFunctionDef;
    private NSFuncDef                rootMethod;
    private NSClass                  clazz;
    private Stack<NSType>            typeStack;
    private HashMap<String, NSType>  varName2Type;
    private Stack<CompilerState>     states;
    private Stack<LoopStartingPoint> loopStartStack;
    private boolean                  inComment = false;

    public NSCompiler()
    {
        NSOps.initAllNames();
        this.namespace = "std";
        labelBase = "L";
        varName2Id = new HashMap<>();
        varName2Type = new HashMap<>();
        states = new Stack<>();
        loopStartStack = new Stack<>();
        rootMethod = currentMethodDef = (NSFuncDef) new NSFuncDef().name(NSFuncDef.ROOT_ID);
    }

    public NSClass compile(String name, String source) throws NSCompilerException, IOException
    {
        return compile(new NSSourceFile(name, new ByteArrayInputStream(source.getBytes())));
    }

    public NSClass compile(NSSourceFile source) throws NSCompilerException, IOException
    {
        this.clazz = new NSClass(source.name().substring(0, source.name().indexOf("."))).sourceFile(source.name());
        clazz.rootMethod(rootMethod.owner(clazz.name()));
        this.source = source.content();
        this.index = 0;
        this.line = 0;
        NSCodeToken nSCodeToken;
        ArrayList<NSCodeToken> tokenList = new ArrayList<NSCodeToken>();
        List<NSInsn> finalInstructions = null;
        int lineNumber = 1;
        while((nSCodeToken = nextToken()) != null)
        {
            finalInstructions = currentMethodDef.instructions();
            if(finalInstructions.isEmpty())
                finalInstructions.add(new LineNumberInsn(1));

            if(nSCodeToken.type == NSTokenType.NEW_LINE)
            {
                finalInstructions.add(new LineNumberInsn( ++lineNumber));
            }
            else if(nSCodeToken.type == NSTokenType.KEYWORD && nSCodeToken.content.equals(NSKeywords.COMMENT_START.raw()))
            {
                inComment = true;
                System.out.println(">>>> COMMENT");
            }
            else if(!inComment)
            {
                if(nSCodeToken.createsNewLabel())
                {
                    if(nSCodeToken.type != NSTokenType.INSTRUCTION_END)
                        tokenList.add(nSCodeToken);
                    makeInstructions(tokenList, finalInstructions);
                    if(!tokenList.isEmpty())
                    {
                        throwCompilerException("Missing semicolon.");
                    }
                }
                else
                    tokenList.add(nSCodeToken);
            }
        }
        if(!tokenList.isEmpty())
        {
            throwCompilerError("Instruction stack isn't empty. Problem while reading the source code.");
        }
        // We analyse the function calls to find if the owner is this class or another
        for(NSAbstractMethod m : clazz.methods())
        {
            if(m instanceof NSFuncDef)
            {
                NSFuncDef method = (NSFuncDef) m;
                for(NSInsn insn : method.instructions())
                {
                    if(insn.getOpcode() == FUNCTION_CALL)
                    {
                        FunctionCallInsn callInsn = (FunctionCallInsn) insn;
                        NSAbstractMethod calledMethod = null;
                        try
                        {
                            calledMethod = clazz.method(callInsn.functionName(), callInsn.types());
                        }
                        catch(Exception e)
                        {
                            ; // We ignore this exception as there's one only if the method doesn't exist
                        }

                        if(calledMethod != null)
                        {
                            callInsn.functionOwner(clazz.name());
                        }
                        else
                        {
                            // TODO: Imports maybe ?
                            callInsn.functionOwner("std");
                            if(!callInsn.types().isEmpty())
                            {
                                NSType type = callInsn.types().get(0);
                                if(type.functions().containsKey(callInsn.functionName()))
                                {
                                    callInsn.functionOwner(type.getID());
                                }
                                else
                                    ; // TODO: Check for user-defined types
                            }
                        }
                    }
                }
            }
        }
        return clazz;
    }

    private NSCodeToken nextToken() throws NSCompilerException
    {
        try
        {
            char[] chars = source.toCharArray();
            StringBuffer buffer = new StringBuffer();
            boolean inString = false;
            boolean inNumber = false;
            NSTokenType type = null;
            for(; index < chars.length; index++ )
            {
                boolean append = true;
                boolean breakAfter = false;
                NSCodeToken op = null;
                if(chars[index] == '\"')
                {
                    inString = !inString;
                    append = false;
                    if(!inString)
                    {
                        breakAfter = true;
                        type = NSTokenType.STRING;
                    }
                }
                else if(!inString && (chars[index] == '\n' || chars[index] == '\r'))
                {
                    if(!buffer.toString().isEmpty())
                    {
                        for(NSKeywords keyword : NSKeywords.values())
                        {
                            if(keyword.raw().equals(buffer.toString()))
                            {
                                return new KeywordToken(keyword);
                            }
                        }
                        for(NSOperator operator : NSOperator.values())
                        {
                            if(operator.toString().equals(buffer.toString()))
                            {
                                index += operator.toString().length(); // We offset the index by the length of the operator
                                return new OperatorToken(operator);
                            }
                        }

                        return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                    }
                    index++ ;
                    line++ ;
                    inComment = false;
                    return new NSCodeToken("", NSTokenType.NEW_LINE);
                }
                else if(!inString && !inComment)
                {
                    if(chars[index] >= '0' && chars[index] <= '9')
                    {
                        if(buffer.toString().isEmpty())
                            inNumber = true;
                    }
                    else if(!(chars[index] == '.' && inNumber) && (op = getOperator(buffer, chars)) != null)
                    {
                        return op;
                    }
                    else if(chars[index] == ' ')
                    {
                        if(!buffer.toString().isEmpty())
                        {
                            for(NSKeywords keyword : NSKeywords.values())
                            {
                                if(keyword.raw().equals(buffer.toString()))
                                {
                                    return new KeywordToken(keyword);
                                }
                            }
                            for(NSOperator operator : NSOperator.values())
                            {
                                if(operator.toString().equals(buffer.toString()))
                                {
                                    index += operator.toString().length(); // We offset the index by the length of the operator
                                    return new OperatorToken(operator);
                                }
                            }
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        index++ ;
                        return nextToken();
                    }
                    else if(chars[index] == '\t')
                    {
                        if(!buffer.toString().isEmpty())
                        {
                            for(NSKeywords keyword : NSKeywords.values())
                            {
                                if(keyword.raw().equals(buffer.toString()))
                                {
                                    return new KeywordToken(keyword);
                                }
                            }
                            for(NSOperator operator : NSOperator.values())
                            {
                                if(operator.toString().equals(buffer.toString()))
                                {
                                    index += operator.toString().length(); // We offset the index by the length of the operator
                                    return new OperatorToken(operator);
                                }
                            }
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        index++ ;
                        return nextToken();
                    }
                    else if(chars[index] == ',')
                    {
                        if(!buffer.toString().isEmpty())
                        {
                            for(NSKeywords keyword : NSKeywords.values())
                            {
                                if(keyword.raw().equals(buffer.toString()))
                                {
                                    return new KeywordToken(keyword);
                                }
                            }
                            for(NSOperator operator : NSOperator.values())
                            {
                                if(operator.toString().equals(buffer.toString()))
                                {
                                    index += operator.toString().length(); // We offset the index by the length of the operator
                                    return new OperatorToken(operator);
                                }
                            }
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        index++ ;
                        return nextToken();
                    }
                    else if(chars[index] == '(')
                    {
                        if(!buffer.toString().isEmpty())
                        {
                            for(NSKeywords keyword : NSKeywords.values())
                            {
                                if(keyword.raw().equals(buffer.toString()))
                                {
                                    return new KeywordToken(keyword);
                                }
                            }
                        }
                        for(NSOperator operator : NSOperator.values())
                        {
                            if(operator.toString().equals(buffer.toString()))
                            {
                                index += operator.toString().length(); // We offset the index by the length of the operator
                                return new OperatorToken(operator);
                            }
                        }
                        index++ ;
                        return new NSCodeToken(buffer.toString(), NSTokenType.OPEN_PARENTHESIS);
                    }
                    else if(chars[index] == ')')
                    {
                        if(!buffer.toString().isEmpty())
                        {
                            for(NSKeywords keyword : NSKeywords.values())
                            {
                                if(keyword.raw().equals(buffer.toString()))
                                {
                                    return new KeywordToken(keyword);
                                }
                            }
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        for(NSOperator operator : NSOperator.values())
                        {
                            if(operator.toString().equals(buffer.toString()))
                            {
                                index += operator.toString().length(); // We offset the index by the length of the operator
                                return new OperatorToken(operator);
                            }
                        }

                        index++ ;
                        return new NSCodeToken("", NSTokenType.CLOSE_PARENTHESIS);
                    }
                    else if(chars[index] == ';')
                    {
                        if(!buffer.toString().isEmpty())
                        {
                            for(NSKeywords keyword : NSKeywords.values())
                            {
                                if(keyword.raw().equals(buffer.toString()))
                                {
                                    return new KeywordToken(keyword);
                                }
                            }
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        breakAfter = true;
                        type = NSTokenType.INSTRUCTION_END;
                    }
                }

                if(append)
                {
                    buffer.append(chars[index]);
                }
                if(breakAfter)
                {
                    index++ ;
                    break;
                }
            }
            if(index >= chars.length - 1 && buffer.length() == 0 && type == null)
                return null;
            if(type == null)
            {
                if(inString)
                {
                    throwCompilerException("Missing end of string : \"" + buffer.toString() + "\"");
                }
                if(!buffer.toString().isEmpty())
                    for(NSKeywords keyword : NSKeywords.values())
                    {
                        if(keyword.raw().equals(buffer.toString()))
                        {
                            return new KeywordToken(keyword);
                        }
                    }
                throwCompilerException("Type is null. Content is \"" + buffer.toString() + "\"");
            }
            return new NSCodeToken(buffer.toString(), type);
        }
        catch(Exception e)
        {
            if(e instanceof NSCompilerException)
                throw e;
            throw new NSCompilerException("Error while compiling.", e);
        }
    }

    private NSCodeToken getOperator(StringBuffer buffer, char[] chars)
    {
        @SuppressWarnings("unchecked")
        ArrayList<NSOperator> operators = (ArrayList<NSOperator>) NSOperator.list().clone();
        NSOperator foundOperator = null;
        Collections.sort(operators, new Comparator<NSOperator>()
        {

            @Override
            public int compare(NSOperator o1, NSOperator o2)
            {
                return Integer.compare(o2.name().length(), o1.name().length()); // We want the operators from longer to shorter
            }
        });
        if(buffer.toString().isEmpty())
            for(NSKeywords keyword : NSKeywords.values())
            {
                if(source.indexOf(keyword.raw(), index) == index)
                {
                    index += keyword.raw().length();
                    return new KeywordToken(keyword);
                }
            }
        for(NSOperator operator : operators)
        {
            if(source.indexOf(operator.toString(), index) == index)
            {
                if(foundOperator == null)
                    foundOperator = operator;
                else if(foundOperator.toString().length() < operator.toString().length())
                    foundOperator = operator;
            }
        }
        if(foundOperator == null)
            return null;
        if(!buffer.toString().isEmpty())
        {
            for(NSKeywords keyword : NSKeywords.values())
            {
                if(keyword.raw().equals(buffer.toString()))
                {
                    return new KeywordToken(keyword);
                }
            }
            for(NSOperator operator : operators)
            {
                if(operator.toString().equals(buffer.toString()))
                {
                    index += operator.toString().length(); // We offset the index by the length of the operator
                    return new OperatorToken(operator);
                }
            }
            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
        }
        index += foundOperator.toString().length(); // We offset the index by the length of the operator
        return new OperatorToken(foundOperator);
    }

    public ArrayList<NSCodeToken> toRPN(List<NSCodeToken> nSCodeTokens)
    {
        ArrayList<NSCodeToken> outputQueue = new ArrayList<NSCodeToken>();
        Stack<NSCodeToken> operatorStack = new Stack<NSCodeToken>();
        for(NSCodeToken nSCodeToken : nSCodeTokens)
        {
            if(nSCodeToken.type == NSTokenType.OPEN_PARENTHESIS || nSCodeToken.type == NSTokenType.KEYWORD)
            {
                operatorStack.push(nSCodeToken);
            }
            else if(nSCodeToken.type == NSTokenType.CLOSE_PARENTHESIS)
            {
                while(!operatorStack.isEmpty())
                {
                    NSCodeToken operator = operatorStack.pop();
                    if(operator.type == NSTokenType.OPEN_PARENTHESIS)
                    {
                        if(!operator.content.isEmpty())
                        {
                            outputQueue.add(new NSCodeToken(operator.content, NSTokenType.FUNCTION_CALL));
                        }
                        break;
                    }
                    else if(operator.type == NSTokenType.KEYWORD)
                    {
                        outputQueue.add(operator);
                        break;
                    }
                    else
                    {
                        outputQueue.add(operator);
                    }
                }
            }
            else
            {
                if(isTokenOperator(nSCodeToken))
                {
                    while(!operatorStack.isEmpty())
                    {
                        NSCodeToken operator = operatorStack.peek();
                        if(getPrecedence(operator) >= getPrecedence(nSCodeToken))
                        {
                            outputQueue.add(operatorStack.pop());
                        }
                        else
                        {
                            break;
                        }
                    }
                    operatorStack.push(nSCodeToken);
                }
                else
                {
                    outputQueue.add(nSCodeToken);
                }
            }
        }
        while(!operatorStack.isEmpty())
        {
            NSCodeToken operator = operatorStack.pop();
            if(operator.type == NSTokenType.OPEN_PARENTHESIS)
            {
                if(!operator.content.isEmpty())
                {
                    outputQueue.add(new NSCodeToken(operator.content, NSTokenType.FUNCTION_CALL));
                }
                continue;
            }
            else if(operator.type == NSTokenType.CLOSE_PARENTHESIS)
            {
                continue;
            }
            outputQueue.add(operator);
        }
        return outputQueue;
    }

    public boolean isTokenOperator(NSCodeToken nSCodeToken)
    {
        return (nSCodeToken.type == NSTokenType.OPERATOR && nSCodeToken instanceof OperatorToken)
                || (nSCodeToken.type == NSTokenType.KEYWORD && nSCodeToken instanceof KeywordToken);
    }

    public int getPrecedence(NSCodeToken nSCodeToken)
    {
        if(nSCodeToken.type == NSTokenType.OPERATOR)
        {
            OperatorToken operator = (OperatorToken) nSCodeToken;
            return operator.operator().precedence();
        }
        else if(nSCodeToken.type == NSTokenType.KEYWORD)
        {
            KeywordToken keyword = (KeywordToken) nSCodeToken;
            return keyword.keyword().precedence();
        }
        return 0;
    }

    private void throwCompilerError(String string) throws NSCompilerError
    {
        throw new NSCompilerError(string);
    }

    private void throwCompilerException(String string) throws NSCompilerException
    {
        throw new NSCompilerException(string + " (at line " + line + ")");
    }

    private void makeInstructions(ArrayList<NSCodeToken> tokenList, List<NSInsn> insnList) throws NSCompilerException
    {
        insnList.add(new LabelInsn(new Label(nextLabelID())));
        pendingType = null;
        typeStack = new Stack<>();
        if(tokenList.size() == 1)
        {
            NSCodeToken token = tokenList.remove(tokenList.size() - 1);
            if(token.type == NSTokenType.STRING)
            {
                insnList.add(new LoadConstantInsn(token.content));
                List<NSType> types = new ArrayList<>();
                types.add(NSTypes.STRING_TYPE);
                insnList.add(new FunctionCallInsn("print").functionOwner("std").types(types));
            }
            else if(token.type == NSTokenType.WORD && !inFunctionDef)
            {
                int vindex = -1;
                if(varName2Id.containsKey(token.content))
                {
                    vindex = varName2Id.get(token.content);
                }
                if(vindex >= 0)
                {
                    insnList.add(new NSVarInsn(VAR_LOAD, vindex));
                    List<NSType> types = new ArrayList<>();
                    types.add(NSTypes.STRING_TYPE);
                    insnList.add(new FunctionCallInsn("print").functionOwner("std").types(types));
                }
                else
                {
                    throwCompilerException("Unexpected symbol: " + token.content);
                }
            }
            else
            {
                handleToken(token, 0, new ArrayList<NSCodeToken>(), insnList);
            }
        }
        else
        {
            List<NSCodeToken> rpnList = toRPN(tokenList);
            tokenList.clear();
            int index = 0;
            for(NSCodeToken nSCodeToken : rpnList)
            {
                handleToken(nSCodeToken, index++ , rpnList, insnList);
            }
        }
    }

    private void handleToken(NSCodeToken token, int tokenIndex, List<NSCodeToken> tokenList, List<NSInsn> insnList) throws NSCompilerException
    {
        System.out.println(">> " + token.type.name() + " : " + token.content);
        switch(token.type)
        {
            case WORD:
            {
                for(NSType type : NSTypes.list())
                {
                    if(type.getID().equals(token.content))
                    {
                        pendingType = type;
                        if(inFunctionDef)
                        {
                            currentMethodDef.types().add(type);
                        }
                        return;
                    }
                }
                if(isNumber(token.content))
                {
                    try
                    {
                        int value = Integer.parseInt(token.content);
                        insnList.add(new NSLoadIntInsn(value));
                        typeStack.push(NSTypes.INT_TYPE);
                        return;
                    }
                    catch(Exception e)
                    {
                        ;
                    }

                    float value = Float.parseFloat(token.content);
                    insnList.add(new NSLoadFloatInsn(value));
                    typeStack.push(NSTypes.FLOAT_TYPE);
                }
                else
                {
                    int vindex = -1;
                    if(pendingType != null)
                    {
                        if(inFunctionDef)
                        {
                            currentMethodDef.paramNames().add(token.content);
                        }
                        else
                        {
                            vindex = nextVarIndex();
                            insnList.add(new NewVarInsn(pendingType, token.content, vindex));
                            varName2Type.put(token.content, pendingType);
                            varName2Id.put(token.content, vindex);
                        }
                        pendingType = null;
                    }
                    else
                    {
                        vindex = -1;
                        if(varName2Id.containsKey(token.content))
                        {
                            vindex = varName2Id.get(token.content);
                        }
                    }
                    if(!inFunctionDef)
                    {
                        this.currentVariable = vindex;
                        insnList.add(new NSVarInsn(VAR_LOAD, vindex));
                        typeStack.push(varName2Type.get(token.content));
                    }
                }
            }
                break;

            case STRING:
            {
                insnList.add(new LoadConstantInsn(token.content));
                typeStack.push(NSTypes.STRING_TYPE);
            }
                break;

            case OPERATOR:
            {
                NSOperator operator = ((OperatorToken) token).operator();
                if(operator == NSOperator.MEMBER_ACCESS)
                {
                    NSInsn previous = insnList.get(insnList.size() - 1);
                    if(previous.getOpcode() == FUNCTION_CALL)
                    {
                        FunctionCallInsn callInsn = (FunctionCallInsn) previous;
                        callInsn.functionOwner(FunctionCallInsn.PREVIOUS);
                    }
                    else if(previous.getOpcode() == VAR_LOAD)
                    {
                        insnList.set(insnList.size() - 1, new LoadFieldInsn(tokenList.get(tokenIndex - 1).content));
                        typeStack.pop();
                    }
                }
                else if(operator == NSOperator.ASSIGNEMENT)
                {
                    if(currentVariable == -1)
                    {
                        throwCompilerException("Tried to assign a value to an object that is not a variable.");
                    }
                    else
                    {
                        insnList.add(new NSVarInsn(VAR_STORE, currentVariable));
                        typeStack.pop();
                    }
                    currentVariable = -1;
                }
                else if(operator == NSOperator.INCREMENT || operator == NSOperator.DECREMENT)
                {
                    if(currentVariable == -1)
                    {
                        throwCompilerException("Invalid argument for operator ++/--");
                    }
                    else
                    {
                        insnList.add(new NSLoadIntInsn(1));
                        insnList.add(new OperatorInsn(operator == NSOperator.INCREMENT ? NSOperator.PLUS : NSOperator.MINUS));
                        insnList.add(new NSVarInsn(VAR_STORE, currentVariable));
                        insnList.add(new NSVarInsn(VAR_LOAD, currentVariable));
                    }
                }
                else
                {
                    NSType lastType = typeStack.pop();
                    NSType type = typeStack.pop(); // We get the type right before the last type loaded
                    NSObject result = type.operation(type.emptyObject(), lastType.emptyObject(), operator);
                    typeStack.push(result.type());
                    insnList.add(new OperatorInsn(operator));
                }
            }
                break;

            case FUNCTION_CALL:
            {
                if(inFunctionDef)
                {
                    currentMethodDef.name(token.content);
                }
                else
                {
                    insnList.add(new FunctionCallInsn(token.content).functionOwner(FunctionCallInsn.UNKNOWN_YET).types(typeStack));
                }
            }
                break;

            case KEYWORD:
            {
                KeywordToken keyword = (KeywordToken) token;
                switch(keyword.keyword())
                {
                    case IF:
                    {
                        insnList.add(new StackInsn(STACK_PUSH));
                        insnList.add(new StackInsn(STACK_PEEK));
                        insnList.add(new LabelInsn(IF_NOT_GOTO, new Label(getCurrentLabelID())));
                        pushLabelID();
                        loopStartStack.push(new LoopStartingPoint(getCurrentLabelID(), NSKeywords.IF));
                    }
                        break;

                    case ELSE:
                    {
                        popLabelID();
                        insnList.add(new LabelInsn(new Label(nextLabelID())));
                        insnList.add(new StackInsn(STACK_PEEK));
                        insnList.add(new LabelInsn(IF_GOTO, new Label(getCurrentLabelID())));
                        pushLabelID();
                        loopStartStack.push(new LoopStartingPoint(getCurrentLabelID(), NSKeywords.ELSE));
                    }
                        break;

                    case END:
                    {
                        insnList.add(new StackInsn(STACK_POP));
                        insnList.add(new NSBaseInsn(POP));
                        LoopStartingPoint point = loopStartStack.pop();
                        if(point.type() == NSKeywords.WHILE)
                        {
                            insnList.add(new LabelInsn(GOTO, new Label(point.labelID())));
                        }
                        else
                            ;
                        popLabelID();
                    }
                        break;

                    case TRUE:
                    {
                        insnList.add(new LoadConstantInsn(true));
                        typeStack.push(NSTypes.BOOL_TYPE);
                    }
                        break;

                    case FALSE:
                    {
                        insnList.add(new LoadConstantInsn(false));
                        typeStack.push(NSTypes.BOOL_TYPE);
                    }
                        break;

                    case NAMESPACE:
                    {
                        NSInsn prev = insnList.get(insnList.size() - 1);
                        if(prev.getOpcode() == LOAD_CONSTANT)
                        {
                            LoadConstantInsn loadInsn = (LoadConstantInsn) prev;
                            Object cst = loadInsn.getConstant();
                            if(cst instanceof String)
                            {
                                this.namespace = (String) cst;
                            }
                            insnList.remove(insnList.size() - 1);
                        }
                        else
                        {
                            throwCompilerException("Unexepected namespace identifier. Only string literals are allowed");
                        }
                    }
                        break;

                    case WHILE:
                    {
                        loopStartStack.push(new LoopStartingPoint(getPreviousLabelID(), NSKeywords.WHILE));
                        insnList.add(new StackInsn(STACK_PUSH));
                        insnList.add(new StackInsn(STACK_PEEK));
                        insnList.add(new LabelInsn(IF_NOT_GOTO, new Label(getCurrentLabelID())));
                        pushLabelID();
                    }
                        break;

                    case FUNCTION_DEF:
                    {
                        inFunctionDef = true;
                        pushState();
                        currentMethodDef = (NSFuncDef) new NSFuncDef().owner(clazz.name());
                        clazz.methods().add(currentMethodDef);
                    }
                        break;

                    case CODE_BLOCK_START:
                    {
                        if(inFunctionDef)
                        {
                            //                            System.out.println("%%% NEW FUNCTION: " + currentMethodDef.toString());
                            inFunctionDef = false;
                            varId = 0;
                            varName2Id.clear(); // TODO: Might need to push it before clearing it to save root method variables
                            for(int i = 0; i < currentMethodDef.paramNames().size(); i++ )
                            {
                                String name = currentMethodDef.paramNames().get(i);
                                NSType type = currentMethodDef.types().get(i);
                                varName2Id.put(name, varId);
                                varName2Type.put(name, type);
                                //                                currentMethodDef.instructions().add(new NewVarInsn(type, name, nextVarIndex()));
                            }
                        }
                        else
                            throwCompilerException("You must be defining a method to use a code block starting point");
                    }
                        break;

                    case CODE_BLOCK_END:
                    {
                        popState();
                    }
                        break;

                    case RETURN:
                    {
                        if(typeStack != null)
                        {
                            insnList.add(new NSBaseInsn(RETURN_VALUE));
                        }
                        else
                            insnList.add(new NSBaseInsn(RETURN));
                    }
                        break;

                    default:
                        break;
                }
            }
                break;

            default:
                break;
        }
    }

    private boolean isNumber(String content)
    {
        try
        {
            Integer.parseInt(content);
            return true;
        }
        catch(Exception e)
        {
            ;
        }

        try
        {
            Float.parseFloat(content);
            return true;
        }
        catch(Exception e)
        {
            ;
        }
        return false;
    }

    private int nextVarIndex()
    {
        return varId++ ;
    }

    private static final String SUB_LABEL_SEPARATOR = "-";

    private void popLabelID()
    {
        labelBase = labelBase.substring(0, labelBase.lastIndexOf(SUB_LABEL_SEPARATOR));
        int min = labelBase.lastIndexOf(SUB_LABEL_SEPARATOR) + 1;
        if(min <= 0)
            min = 1;
        labelID = Integer.parseInt(labelBase.substring(min));
        labelBase = labelBase.substring(0, labelBase.length() - ("" + labelID).length());
    }

    private void pushLabelID()
    {
        labelBase += labelID + SUB_LABEL_SEPARATOR;
        labelID = 0;
    }

    private String getPreviousLabelID()
    {
        return labelBase + (labelID - 1);
    }

    private String getCurrentLabelID()
    {
        return labelBase + labelID;
    }

    private String nextLabelID()
    {
        return labelBase + (labelID++ );
    }

    private void pushState()
    {
        states.push(new CompilerState(varName2Id, varName2Type, varId, labelBase, labelID, currentMethodDef));
        varName2Id.clear();
        varName2Type.clear();
        varId = 0;
        labelBase = "L";
        labelID = 0;
    }

    private void popState()
    {
        CompilerState state = states.pop();
        varName2Id = state.varNamesToIds();
        varName2Type = state.varNamesToTypes();
        varId = state.varID();
        labelID = state.labelID();
        labelBase = state.labelBase();
        currentMethodDef = state.currentMethodDef();
    }
}
