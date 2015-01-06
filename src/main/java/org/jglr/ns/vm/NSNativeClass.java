package org.jglr.ns.vm;

import org.jglr.ns.*;
import org.jglr.ns.compiler.*;

public class NSNativeClass extends NSClass
{

    public NSNativeClass(String name)
    {
        super(name);
    }

    public NSFuncDef rootMethod()
    {
        throw new UnsupportedOperationException("A native class doens't have a root method");
    }

    public NSClass rootMethod(NSFuncDef method)
    {
        throw new UnsupportedOperationException("A native class doens't have a root method");
    }

    public NSClass sourceFile(String sourceFile)
    {
        throw new UnsupportedOperationException("A native class doens't have a source file");
    }

    public String sourceFile()
    {
        return name() + ", native class";
    }

}
