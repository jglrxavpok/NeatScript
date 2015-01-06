package org.jglr.ns.vm;

public class NSClassNotFoundException extends Exception
{

    private static final long serialVersionUID = 68376209416432717L;

    public NSClassNotFoundException(String className)
    {
        super(className);
    }

    public NSClassNotFoundException(String className, Throwable t)
    {
        super(className, t);
    }
}
