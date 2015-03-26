package org.jglr.ns.compiler;

import org.jglr.ns.*;

public class LoopStartingPoint {

    private String labelID;
    private NSKeywords type;

    public LoopStartingPoint(String labelID, NSKeywords type) {
        this.type = type;
        this.labelID = labelID;
    }

    public NSKeywords type() {
        return type;
    }

    public String labelID() {
        return labelID;
    }

}
