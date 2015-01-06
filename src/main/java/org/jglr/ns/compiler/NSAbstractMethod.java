package org.jglr.ns.compiler;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.types.*;

public abstract class NSAbstractMethod
{
    private String       name;
    private List<NSType> types;
    private List<String> paramNames;
    private String       owner;

    public NSAbstractMethod()
    {
        types = new ArrayList<>();
        paramNames = new ArrayList<>();
    }

    public NSAbstractMethod name(String name)
    {
        this.name = name;
        return this;
    }

    public abstract void run(Stack<NSObject> vars);

    public List<String> paramNames()
    {
        return paramNames;
    }

    public String name()
    {
        return name;
    }

    public List<NSType> types()
    {
        return types;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(name());
        buffer.append('(');
        for(int i = 0; i < types().size(); i++ )
        {
            buffer.append(types().get(i).getID());
            buffer.append(" ");
            buffer.append(paramNames().get(i));
        }
        buffer.append(')');
        return buffer.toString();
    }

    public NSAbstractMethod owner(String owner)
    {
        this.owner = owner;
        return this;
    }

    public String owner()
    {
        return owner;
    }
}
