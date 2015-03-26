package org.jglr.ns;

public class NSCodeToken {

    public String content;
    public NSTokenType type;

    public NSCodeToken(String string, NSTokenType type) {
        this.content = string;
        this.type = type;
    }

    public boolean createsNewLabel() {
        return type == NSTokenType.INSTRUCTION_END;
    }

}
