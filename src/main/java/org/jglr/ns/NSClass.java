package org.jglr.ns;

import java.util.*;

import org.jglr.ns.compiler.*;
import org.jglr.ns.types.*;

public class NSClass
{

    private String                 name;
    private List<NSAbstractMethod> methodsDef;
    private NSAbstractMethod       rootMethod;
    private String                 sourceFile;

    public NSClass(String name)
    {
        this.name = name;
        methodsDef = new ArrayList<NSAbstractMethod>();
    }

    public NSAbstractMethod rootMethod()
    {
        return rootMethod;
    }

    public NSClass rootMethod(NSAbstractMethod method)
    {
        rootMethod = method;
        if(!methodsDef.contains(method))
            methodsDef.add(method);
        return this;
    }

    public List<NSAbstractMethod> methods()
    {
        return methodsDef;
    }

    public String name()
    {
        return name;
    }

    public String toString()
    {
        return "class " + name;
    }

    public NSAbstractMethod method(String methodName, List<NSType> types) throws NSNoSuchMethodException
    {
        methodSearch: for(NSAbstractMethod method : methodsDef)
        {
            if(method.name().equals(methodName))
            {
                if(types.size() != method.types().size())
                {
                    continue;
                }
                for(int i = 0; i < types.size(); i++ )
                {
                    if(!types.get(i).getID().equals(method.types().get(i).getID()))
                    {
                        continue methodSearch;
                    }
                }
                return method;
            }
        }
        throw new NSNoSuchMethodException(name, methodName, types);
    }

    public NSClass sourceFile(String sourceFile)
    {
        this.sourceFile = sourceFile;
        return this;
    }

    public String sourceFile()
    {
        if(sourceFile == null)
            return "_dynamic_";
        return sourceFile;
    }
}
