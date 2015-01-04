package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSBoolType extends NSType
{

    public final NSObject TRUE;
    public final NSObject FALSE;

    public NSBoolType()
    {
        TRUE = new NSObject(this).value(true);
        FALSE = new NSObject(this).value(false);
    }

    @Override
    public String getID()
    {
        return "Bool";
    }

    @Override
    public NSObject operation(NSObject a, NSObject b, NSOperator operator)
    {
        if(operator == NSOperator.EQUAL)
        {
            if(a.value() == null && b.value() == null)
                return TRUE;
            else if(a.value() == null && b.value() != null || a.value() != null && b.value() == null)
                return FALSE;
            else if(a.value().equals(b.value()))
                return TRUE;
            else if(a.type() != b.type())
                return TRUE;
            else
                return FALSE;
        }
        return null;
    }

}
