package org.jglr.ns.vm;

public class NSVirtualMachineException extends Exception {

    private static final long serialVersionUID = -4946128697946105886L;

    public NSVirtualMachineException(String message) {
        super(message);
    }

    public NSVirtualMachineException(String message, StackTraceElement[] e, boolean newException) {
        super(message);
        if (newException) {
            Exception exception = new Exception("NeatScript exception");
            exception.setStackTrace(e);
            this.initCause(exception);
        } else
            setStackTrace(e);
    }

    public NSVirtualMachineException(String message, StackTraceElement[] generatedStackTrace, Exception e) {
        super(message, e);
        setStackTrace(generatedStackTrace);
    }
}
