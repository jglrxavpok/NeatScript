package org.jglr.ns;

public enum NSOperator
{
    PLUS("+", 4),
    MINUS("-", 4),
    TIMES("*", 3),
    DIVIDE("/", 3),
    MODULO("%", 3),
    AND("&", 8),
    BIT_XOR("^", 9),
    BIT_OR("|", 10),
    LEFT_SHIFT("<<", 5),
    RIGHT_SHIFT(">>", 5),
    LESS_THAN("<", 6),
    GREATER_THAN(">", 6),
    GEQUAL(">=", 6),
    LEQUAL("<=", 6),
    EQUAL("=", 14),
    UNSIGNED_RIGHT_SHIFT(">>>", 5);

    private int    precedence;
    private String string;

    NSOperator(String str, int precedence)
    {
        this.string = str;
        this.precedence = precedence;
    }

    public String toString()
    {
        return string;
    }

    public int precedence()
    {
        return precedence;
    }

}
