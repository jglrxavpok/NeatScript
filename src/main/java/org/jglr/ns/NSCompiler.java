package org.jglr.ns;

import java.util.*;

import org.jglr.ns.insns.*;

public class NSCompiler implements NSOps
{

    private int    index;
    private String source;
    private int    line;
    private int    labelID;
    private String labelBase;

    public NSCompiler()
    {
        NSOps.initAllNames();
        labelBase = "L";
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
                    if(chars[index] == ' ')
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
                            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
                        }
                        index++ ;
                        line++ ;

                        return new NSCodeToken("", NSTokenType.NEW_LINE);
                    }
                    else
                    {
                        NSCodeToken op = getOperator(buffer, chars);
                        if(op != null)
                            return op;
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
        int ii = index;
        @SuppressWarnings("unchecked")
        ArrayList<NSOperator> operators = (ArrayList<NSOperator>) NSOperator.list().clone();
        ArrayList<NSOperator> toExclude = new ArrayList<>();
        NSOperator foundOperator = null;
        for(; ii < chars.length; ii++ )
        {
            if(operators.size() == 0)
                return null;
            if(operators.size() == 1)
            {
                if(operators.get(0).toString().equals(source.substring(index, ii)))
                {
                    foundOperator = operators.get(0);
                    break;
                }
                else
                    return null;
            }
            for(NSOperator operator : operators)
            {
                if(operator.toString().length() <= ii - index // If the operator is too 'small' 
                        ||
                        operator.toString().charAt(ii - index) != chars[index]) // If the operator identifier does not contain the current character
                    toExclude.add(operator);
            }
            operators.removeAll(toExclude);
            toExclude.clear();
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
            return new NSCodeToken(buffer.toString(), NSTokenType.WORD);
        }
        index += (ii - index); // We offset the index by the length of the operator
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
        if(tokenList.size() == 1)
        {
            NSCodeToken nSCodeToken = tokenList.remove(tokenList.size() - 1);
            if(nSCodeToken.type == NSTokenType.STRING || nSCodeToken.type == NSTokenType.WORD)
            {
                insnList.add(new LoadConstantInsn(nSCodeToken.content));
                insnList.add(new FunctionCallInsn("print"));
            }
            else
            {
                handleToken(nSCodeToken, insnList);
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

    private void handleToken(NSCodeToken nSCodeToken, ArrayList<NSInsn> insnList)
    {
        switch(nSCodeToken.type)
        {
            case STRING:
            {
                insnList.add(new LoadConstantInsn(nSCodeToken.content));
            }
                break;

            case OPERATOR:
            {
                insnList.add(new OperatorInsn(((OperatorToken) nSCodeToken).operator()));
            }
                break;

            case FUNCTION_CALL:
            {
                insnList.add(new FunctionCallInsn(nSCodeToken.content));
            }
                break;

            case KEYWORD:
            {
                KeywordToken keyword = (KeywordToken) nSCodeToken;
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

                    default:
                        break;
                }
            }
                break;

            default:
                break;
        }
    }

    private static final String SUB_LABEL_SEPARATOR = "-";

    private void popLabelID()
    {
        labelBase = labelBase.substring(0, labelBase.lastIndexOf(SUB_LABEL_SEPARATOR));
        int min = labelBase.lastIndexOf(SUB_LABEL_SEPARATOR) + 1;
        if(min <= 0)
            min = 1;
        labelID = Integer.parseInt(labelBase.substring(min));
        labelBase = labelBase.substring(0, labelBase.length() - 1);
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
