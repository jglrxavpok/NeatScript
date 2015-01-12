package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSObjectType extends NSType
{

    public NSObjectType()
    {
        super("Object", null);
    }

    public NSType supertype()
    {
        return this;
    }

    public NSType supertype(NSType supertype)
    {
        return this;
    }

    @Override
    public NSObject emptyObject()
    {
        return new NSObject(this, new Object());
    }

}
