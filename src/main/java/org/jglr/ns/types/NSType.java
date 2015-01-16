package org.jglr.ns.types;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.funcs.*;

public abstract class NSType
{

    private HashMap<String, NSNativeFunc> functions;
    private String                        id;
    private NSType                        supertype;

    public NSType(String id, NSType supertype)
    {
        this.id = id;
        functions = new HashMap<>();
        this.supertype = supertype;
        if(supertype != null)
            functions.putAll(supertype.functions());
    }

    public NSType supertype()
    {
        return supertype;
    }

    public boolean isCastable(NSType type)
    {
        if(type == this)
            return true;
        if(supertype != null)
        {
            if(supertype.isCastable(type))
                return true;
        }
        return false;
    }

    public Object cast(Object value, NSType type)
    {
        if(isCastable(type))
            return value;
        throw new UnsupportedOperationException(getID() + " is not castable to " + type.getID());
    }

    public String getID()
    {
        return id;
    }

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
        else if(operator == NSOperator.NON_EQUALITY_CHECK)
        {
            if(a.value() == null && b.value() == null)
                return NSTypes.BOOL_TYPE.FALSE;
            else if(a.value() == null && b.value() != null || a.value() != null && b.value() == null)
                return NSTypes.BOOL_TYPE.TRUE;
            else if(a.value().equals(b.value()))
            {
                return NSTypes.BOOL_TYPE.FALSE;
            }
            else
                return NSTypes.BOOL_TYPE.TRUE;
        }
        if(supertype != null)
            return supertype.operation(a, b, operator);
        throw new UnsupportedOperationException(a.type().getID() + " " + operator.toString() + " " + a.type().getID());
    }

    public NSType init(NSObject object)
    {
        if(supertype != null)
            supertype.init(object);
        return this;
    }

    public void newFunction(String id, NSNativeFunc function)
    {
        functions.put(id, function);
    }

    public HashMap<String, NSNativeFunc> functions()
    {
        return functions;
    }

    public abstract NSObject emptyObject();

    public void setID(String string)
    {
        this.id = string;
    }
}
