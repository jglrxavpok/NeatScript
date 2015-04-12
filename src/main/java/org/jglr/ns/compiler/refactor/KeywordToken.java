package org.jglr.ns.compiler.refactor;

import org.jglr.ns.NSKeywords;

public class KeywordToken extends NSCodeToken {

    private NSKeywords keyword;

    public KeywordToken(NSKeywords keyword) {
        super(keyword.raw(), NSTokenType.KEYWORD);
        this.keyword = keyword;
    }

    public NSKeywords keyword() {
        return keyword;
    }

    public boolean createsNewLabel() {
        return super.createsNewLabel() || keyword.createsNewLabel();
    }
}
