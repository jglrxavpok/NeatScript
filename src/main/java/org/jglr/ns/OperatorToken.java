package org.jglr.ns;

public class OperatorToken extends NSCodeToken
{

    private NSOperator operator;

    public OperatorToken(NSOperator operator)
    {
        super(operator.toString(), NSTokenType.OPERATOR);
        this.operator = operator;
    }

    public NSOperator operator()
    {
        return operator;
    }

}
