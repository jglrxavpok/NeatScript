package org.jglr.ns.types;

import java.util.*;

import org.jglr.ns.*;

public interface NSTypes
{
    public static final NSType     STRING_TYPE = new NSStringType();
    public static final NSBoolType BOOL_TYPE   = new NSBoolType();

    public static NSType getPriorityType(NSObject value, NSType a, NSType b)
    {
        // TODO: True Priority System
        if(value != null)
            return value.type(); // If we have a value to operate on, choose that value's type
        if(a == STRING_TYPE) // Otherwise we arbitrary decide of the type.
        {
            if(b == BOOL_TYPE)
            {
                return a;
            }
        }
        else if(b == STRING_TYPE)
        {
            if(a == BOOL_TYPE)
            {
                return b;
            }
        }
        return a;
    }

    static final List<NSType> list = new ArrayList<>();

    public static List<NSType> list()
    {
        if(list.isEmpty())
        {
            list.add(STRING_TYPE);
            list.add(BOOL_TYPE);
        }
        return list;
    }
}
