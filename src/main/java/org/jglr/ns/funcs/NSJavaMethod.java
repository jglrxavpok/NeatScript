package org.jglr.ns.funcs;

import java.util.*;
import java.util.function.*;

import org.jglr.ns.*;
import org.jglr.ns.types.*;

public class NSJavaMethod extends NSNativeFunc
{

    private Consumer<Stack<NSObject>> method;

    public NSJavaMethod(String name, Consumer<Stack<NSObject>> method, String[] paramNames, NSType[] types)
    {
        super(name);
        for(NSType t : types)
        {
            types().add(t);
        }
        for(String n : paramNames)
        {
            paramNames().add(n);
        }
        this.method = method;
    }

    @Override
    public void run(Stack<NSObject> vars)
    {
        method.accept(vars);
    }

}
