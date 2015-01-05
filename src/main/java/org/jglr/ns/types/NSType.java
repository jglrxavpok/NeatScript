package org.jglr.ns.types;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.funcs.*;

public abstract class NSType
{

    private HashMap<String, NSFunc> functions;

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

    public NSType init(NSObject object)
    {
        return this;
    }

    public void newFunction(String id, NSFunc function)
    {
        if(functions == null)
            functions = new HashMap<>();
        functions.put(id, function);
    }

    public HashMap<String, NSFunc> functions()
    {
        return functions;
    }
}
