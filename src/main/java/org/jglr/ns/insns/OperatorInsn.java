package org.jglr.ns.insns;

import org.jglr.ns.*;

public class OperatorInsn extends NSInsn
{

    private NSOperator operator;

    public OperatorInsn(NSOperator operator)
    {
        super(OPERATOR);
        this.operator = operator;
    }

    public NSOperator operator()
    {
        return operator;
    }

    public String toString()
    {
        return super.toString() + " " + operator.toString();
    }

}
