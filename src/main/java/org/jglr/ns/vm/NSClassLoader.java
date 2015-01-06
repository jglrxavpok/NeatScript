package org.jglr.ns.vm;

import org.jglr.ns.*;

public abstract class NSClassLoader
{

    public abstract NSClass loadClass(String className) throws NSClassNotFoundException;

}
