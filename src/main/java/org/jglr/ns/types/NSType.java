package org.jglr.ns.types;

import org.jglr.ns.*;

public abstract class NSType
{

    public boolean isCastable(NSType type)
    {
        return false;
    }

    public Object cast(Object value, NSType type)
    {
        throw new UnsupportedOperationException(getID() + " is not castable to " + type.getID());
    }

    public abstract String getID();

    public NSObject operation(NSObject a, NSObject b, NSOperator operator)
    {
        if(operator == NSOperator.EQUALITY_CHECK)
        {
            if(a.value() == null && b.value() == null)
                return NSTypes.BOOL_TYPE.TRUE;
            else if(a.value() == null && b.value() != null || a.value() != null && b.value() == null)
                return NSTypes.BOOL_TYPE.FALSE;
            else if(a.value().equals(b.value()))
            {
                return NSTypes.BOOL_TYPE.TRUE;
            }
            else
                return NSTypes.BOOL_TYPE.FALSE;
        }
        return null;
    }
}
