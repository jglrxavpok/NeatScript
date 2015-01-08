package org.jglr.ns;

import java.util.*;

import org.jglr.ns.types.*;

public class NSObject
{

    private NSType                    type;
    private Object                    object;
    private HashMap<String, NSObject> fields;

    public NSObject(NSType type)
    {
        this(type, true);
    }

    public NSObject(NSType type, Object object)
    {
        this(type, object, true);
    }

    public NSObject(NSType type, boolean init)
    {
        this(type, null, init);
    }

    public NSObject(NSType type, Object value, boolean init)
    {
        this.type = type;
        this.object = value;
        fields = new HashMap<>();
        if(init)
            type.init(this);
    }

    public NSType type()
    {
        return type;
    }

    public NSObject value(Object object)
    {
        this.object = object;
        return this;
    }

    public Object value()
    {
        return object;
    }

    public NSObject copy()
    {
        return new NSObject(type, object);
    }

    public NSObject field(String fieldName, NSObject value)
    {
        fields.put(fieldName, value);
        return this;
    }

    public NSObject field(String fieldName)
    {
        NSObject object = fields.get(fieldName);
        return object;
    }

    public Object castedValue(NSType type)
    {
        return this.type.cast(object, type);
    }

}
