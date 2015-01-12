package org.jglr.ns.vm;

import org.jglr.ns.*;

public abstract class NSClassLoader
{

    private NSClassParser classParser;

    public NSClassLoader(NSClassParser parser)
    {
        this.classParser = parser;
    }

    public NSClassParser classParser()
    {
        return classParser;
    }

    public abstract NSClass loadClass(String className) throws NSClassNotFoundException;

}
