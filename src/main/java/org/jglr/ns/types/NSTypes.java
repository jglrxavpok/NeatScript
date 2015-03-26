package org.jglr.ns.types;

import java.util.*;

import org.jglr.ns.*;

public interface NSTypes {
    public static NSType STRING_TYPE = new NSStringType();
    public static NSBoolType BOOL_TYPE = new NSBoolType();
    public static NSFloatType FLOAT_TYPE = new NSFloatType();
    public static NSIntType INT_TYPE = new NSIntType();
    public static NSType OBJECT_TYPE = new NSObjectType();

    public static NSType getPriorityType(NSObject value, NSType a, NSType b) {
        // TODO: True Priority System
        if (value != null)
            return value.type(); // If we have a value to operate on, choose that value's type
        if (a == STRING_TYPE) // Otherwise we arbitrary decide of the type.
        {
            if (b == BOOL_TYPE) {
                return a;
            }
        } else if (b == STRING_TYPE) {
            if (a == BOOL_TYPE) {
                return b;
            }
        }
        return a;
    }

    static final List<NSType> list = new ArrayList<>();

    public static List<NSType> list() {
        if (list.isEmpty()) {
            list.add(STRING_TYPE);
            list.add(BOOL_TYPE);
            list.add(INT_TYPE);
            list.add(FLOAT_TYPE);
            list.add(OBJECT_TYPE);
        }
        return list;
    }

    public static NSType fromID(String id) {
        for (NSType t : list) {
            if (t.getID().equals(id))
                return t;
        }
        return null;
    }

    public static NSType fromIDOrDummy(String id) {
        NSType fromID = fromID(id);
        if (fromID != null)
            return fromID;
        return new NSType(id, OBJECT_TYPE) {
            @Override
            public NSObject emptyObject() {
                return new NSObject(this);
            }
        };
    }
}
