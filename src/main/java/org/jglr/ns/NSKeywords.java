package org.jglr.ns;

public enum NSKeywords
{
    IF("if"),
    ELSE("else", true),
    ELIF("elif"),
    DELETE("delete"),
    THEN("then", true),
    END("end", true),
    TRUE("true", 2000),
    FALSE("false", 2000),
    NAMESPACE("namespace"),
    FUNCTION_DEF("function", true),
    CODE_BLOCK_START("{", true),
    CODE_BLOCK_END("}", true),
    RETURN("return"),
    WHILE("while"),
    UNTIL("until"),
    FIELD("field"),
    COMMENT_START("//"),
    NULL("null");

    private String keyword;
    private int precedence;
    private boolean newLabel;

    NSKeywords(String keyword) {
        this(keyword, 2);
    }

    NSKeywords(String keyword, int precedence) {
        this(keyword, precedence, false);
    }

    NSKeywords(String keyword, boolean newLabel) {
        this(keyword, 2, newLabel);
    }

    NSKeywords(String keyword, int precedence, boolean newLabel) {
        this.keyword = keyword;
        this.precedence = precedence;
        this.newLabel = newLabel;
    }

    public int precedence() {
        return precedence;
    }

    public String raw() {
        return keyword;
    }

    public boolean createsNewLabel() {
        return newLabel;
    }

    public NSKeywords raw(String raw) {
        this.keyword = raw;
        return this;
    }
}
