package org.jglr.ns;

import java.util.*;

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
    private String                   namespace;

    public NSCompiler()
    {
        NSOps.initAllNames();
        this.namespace = "std";
        labelBase = "L";
        varName2Id = new HashMap<>();
    }

    public List<NSInsn> compile(String source) throws NSCompilerException
    {
        this.source = source;
        this.index = 0;
        this.line = 0;
        NSCodeToken nSCodeToken;
        ArrayList<NSCodeToken> tokenList = new ArrayList<NSCodeToken>();
        ArrayList<NSInsn> finalInstructions = new ArrayList<>();
        finalInstructions.add(new LineNumberInsn(1));
        int lineNumber = 1;
        while((nSCodeToken = nextToken()) != null)
        {
            System.out.println(">> " + nSCodeToken.type.name() + " : " + nSCodeToken.content);

            if(nSCodeToken.type == NSTokenType.NEW_LINE)
            {
                finalInstructions.add(new LineNumberInsn( ++lineNumber));
            }
            else if(nSCodeToken.createsNewLabel())
            {
                if(nSCodeToken.type != NSTokenType.INSTRUCTION_END)
                    tokenList.add(nSCodeToken);
                Collection<NSInsn> insns = makeInstructions(tokenList);
                finalInstructions.addAll(insns);
                if(!tokenList.isEmpty())
                {
                    throwCompilerException("Missing semicolon.");
                }
            }
            else
                tokenList.add(nSCodeToken);
        }
        if(!tokenList.isEmpty())
        {
            throwCompilerError("Instruction stack isn't empty. Problem while reading the source code.");
        }
        return finalInstructions;
    }

    private NSCodeToken nextToken() throws NSCompilerException
    {
        try
        {
            char[] chars = source.toCharArray();
            StringBuffer buffer = new StringBuffer();
            boolean inString = false;
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
                else if(!inString)
                {
                    if((op = getOperator(buffer, chars)) != null)
                        return op;
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
                    else if(chars[index] == '\n' || chars[index] == '\r')
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

                        return new NSCodeToken("", NSTokenType.NEW_LINE);
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
                return -Integer.compare(o1.name().length(), o2.name().length()); // We want the operators from longer to shorter
            }
        });
        for(NSOperator operator : operators)
        {
            if(source.indexOf(operator.toString(), index) == index)
            {
                foundOperator = operator;
                break;
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

    private Collection<NSInsn> makeInstructions(ArrayList<NSCodeToken> tokenList) throws NSCompilerException
    {
        ArrayList<NSInsn> insnList = new ArrayList<>();
        insnList.add(new LabelInsn(new Label(nextLabelID())));
        pendingType = null;
        if(tokenList.size() == 1)
        {
            NSCodeToken token = tokenList.remove(tokenList.size() - 1);
            if(token.type == NSTokenType.STRING)
            {
                insnList.add(new LoadConstantInsn(token.content));
                insnList.add(new FunctionCallInsn("print").functionOwner(namespace));
            }
            else if(token.type == NSTokenType.WORD)
            {
                int vindex = -1;
                if(varName2Id.containsKey(token.content))
                {
                    vindex = varName2Id.get(token.content);
                }
                if(vindex >= 0)
                {
                    insnList.add(new NSVarInsn(VAR_LOAD, vindex));
                    insnList.add(new FunctionCallInsn("print").functionOwner(namespace));
                }
                else
                {
                    throwCompilerException("Unexpected symbol: " + token.content);
                }
            }
            else
            {
                handleToken(token, insnList);
            }
        }
        else
        {
            List<NSCodeToken> rpnList = toRPN(tokenList);
            tokenList.clear();
            for(NSCodeToken nSCodeToken : rpnList)
            {
                handleToken(nSCodeToken, insnList);
            }
        }
        return insnList;
    }

    private void handleToken(NSCodeToken token, ArrayList<NSInsn> insnList) throws NSCompilerException
    {
        switch(token.type)
        {
            case WORD:
            {
                for(NSType type : NSTypes.list())
                {
                    if(type.getID().equals(token.content))
                    {
                        pendingType = type;
                        return;
                    }
                }
                int vindex = 0;
                if(pendingType != null)
                {
                    vindex = nextVarIndex();
                    insnList.add(new NewVarInsn(pendingType, token.content, vindex));
                    varName2Id.put(token.content, vindex);
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
                this.currentVariable = vindex;
                insnList.add(new NSVarInsn(VAR_LOAD, vindex));
            }
                break;

            case STRING:
            {
                insnList.add(new LoadConstantInsn(token.content));
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
                    }
                    currentVariable = -1;
                }
                else
                    insnList.add(new OperatorInsn(operator));
            }
                break;

            case FUNCTION_CALL:
            {
                insnList.add(new FunctionCallInsn(token.content).functionOwner(namespace));
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
                    }
                        break;

                    case ELSE:
                    {
                        popLabelID();
                        insnList.add(new LabelInsn(new Label(nextLabelID())));
                        insnList.add(new StackInsn(STACK_PEEK));
                        insnList.add(new LabelInsn(IF_GOTO, new Label(getCurrentLabelID())));
                        pushLabelID();
                    }
                        break;

                    case END:
                    {
                        insnList.add(new StackInsn(STACK_POP));
                        popLabelID();
                    }
                        break;

                    case TRUE:
                    {
                        insnList.add(new LoadConstantInsn(true));
                    }
                        break;

                    case FALSE:
                    {
                        insnList.add(new LoadConstantInsn(false));
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

                    default:
                        break;
                }
            }
                break;

            default:
                break;
        }
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

    private String getCurrentLabelID()
    {
        return labelBase + labelID;
    }

    private String nextLabelID()
    {
        return labelBase + (labelID++ );
    }
}
