package org.jglr.ns.vm;

public class NSVirtualMachineError extends Error
{

    private static final long serialVersionUID = -4946128697946105886L;

    public NSVirtualMachineError(String message)
    {
        super(message);
    }

    public NSVirtualMachineError(String message, Throwable t)
    {
        super(message, t);
    }
}
