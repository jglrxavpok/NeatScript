package org.jglr.ns.compiler;

import java.util.*;

import org.jglr.ns.types.*;

public class NSFuncDef
{

    private String       name;
    private List<NSType> types;
    private List<String> paramNames;

    public NSFuncDef()
    {
        types = new ArrayList<>();
        paramNames = new ArrayList<>();
    }

    public NSFuncDef name(String name)
    {
        this.name = name;
        return this;
    }

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
        buffer.append(name);
        buffer.append('(');
        for(int i = 0; i < types.size(); i++ )
        {
            buffer.append(types.get(i).getID());
            buffer.append(" ");
            buffer.append(paramNames.get(i));
        }
        buffer.append(')');
        return buffer.toString();
    }

}
