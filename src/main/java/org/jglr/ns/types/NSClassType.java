package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSClassType extends NSType
{

    public NSClassType(String id, NSType supertype)
    {
        super(id, supertype);
    }

    public NSClassType(NSClass clazz)
    {
        super(clazz.name(), NSTypes.OBJECT_TYPE); // TODO: get type from superclass
    }

    @Override
    public NSObject emptyObject()
    {
        return new NSObject(this, null);
    }

}
