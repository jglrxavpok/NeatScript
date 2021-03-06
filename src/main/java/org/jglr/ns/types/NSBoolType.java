package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSBoolType extends NSType {

    public final NSObject TRUE;
    public final NSObject FALSE;

    public NSBoolType() {
        super("Bool", NSTypes.OBJECT_TYPE);
        TRUE = new NSObject(this, true);
        FALSE = new NSObject(this, false);
    }

    @Override
    public NSObject operation(NSObject a, NSObject b, NSOperator operator) {
        if (operator == NSOperator.EQUALITY_CHECK) {
            if (a == TRUE || b == TRUE || a == FALSE || b == FALSE) {
                return a == b ? TRUE : FALSE;
            }
            if (a.value() == null && b.value() == null)
                return TRUE;
            else if (a.value() == null && b.value() != null || a.value() != null && b.value() == null)
                return FALSE;
            else if (a.value().equals(b.value()))
                return TRUE;
            else if (a.type() != b.type())
                return TRUE;
            else
                return FALSE;
        }
        return null;
    }

    @Override
    public NSObject emptyObject() {
        return FALSE;
    }

    @Override
    public void initType() {
        // TODO Implement

    }

}
