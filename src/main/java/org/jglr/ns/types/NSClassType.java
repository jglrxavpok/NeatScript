package org.jglr.ns.types;

import org.jglr.ns.*;
import org.jglr.ns.vm.*;

public class NSClassType extends NSType
{

    public NSClassType(String id, NSType supertype)
    {
        super(id, supertype);
    }

    public NSClassType(NSClass clazz) throws NSClassNotFoundException
    {
        super(clazz.name(), NSVirtualMachine.instance().getType(clazz.superclass())); // TODO: get type from superclass
    }

    @Override
    public NSObject emptyObject()
    {
        return new NSObject(this, null);
    }

}
