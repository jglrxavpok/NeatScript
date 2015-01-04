package org.jglr.ns;

import org.jglr.ns.types.*;

public class NSObject
{

    private NSType type;
    private Object       object;

    public NSObject(NSType type)
    {
        this.type = type;
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
        return new NSObject(type).value(object);
    }

}
