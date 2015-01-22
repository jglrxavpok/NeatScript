package org.jglr.ns;

public enum NSKeywords
{
    IF("if"), ELSE("else", true), ELSEIF("elseif", true),
    DELETE("delete"), // TODO: Make its implementation, requires pointers first
    THEN("then", true), END("end", true),
    TRUE("true", 2000), FALSE("false", 2000),
    NAMESPACE("namespace"),
    FUNCTION_DEF("function", true),
    CODE_BLOCK_START("{", true), CODE_BLOCK_END("}", true),
    RETURN("return"), WHILE("while"),
    COMMENT_START("//"), FIELD("field"),
    SELF("self", 2000, "this");

    private String   keyword;
    private int      precedence;
    private boolean  newLabel;
    private String[] aliases;

    NSKeywords(String keyword, String... aliases)
    {
        this(keyword, 2, aliases);
    }

    NSKeywords(String keyword, int precedence, String... aliases)
    {
        this(keyword, precedence, false, aliases);
    }

    NSKeywords(String keyword, boolean newLabel, String... aliases)
    {
        this(keyword, 2, newLabel, aliases);
    }

    NSKeywords(String keyword, int precedence, boolean newLabel, String... aliases)
    {
        this.keyword = keyword;
        this.precedence = precedence;
        this.newLabel = newLabel;
        this.aliases = aliases;
    }

    public int precedence()
    {
        return precedence;
    }

    public String raw()
    {
        return keyword;
    }

    public boolean createsNewLabel()
    {
        return newLabel;
    }

    public NSKeywords raw(String raw)
    {
        this.keyword = raw;
        return this;
    }

    public boolean isAt(int index, String text)
    {
        for(String alias : aliases)
        {
            if(text.indexOf(alias, index) == index)
            {
                return true;
            }
        }
        return text.indexOf(raw(), index) == index;
    }

    public boolean is(String raw)
    {
        if(raw().equals(raw))
            return true;
        for(String alias : aliases)
            if(alias.equals(raw))
                return true;
        return false;
    }
}
