package org.jglr.ns.types;

public class NSRangeType extends NSClassType {

    public NSRangeType() {
        super("Range");
    }

    public void initType() {
        typeClass();
        super.initType();
    }
}
