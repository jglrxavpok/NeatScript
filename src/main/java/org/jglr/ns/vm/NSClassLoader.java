package org.jglr.ns.vm;

import org.jglr.ns.*;

public abstract class NSClassLoader {

    private NSClassParser classParser;
    private NSVirtualMachine vm;

    public NSClassLoader(NSVirtualMachine vm, NSClassParser parser) {
        this.vm = vm;
        this.classParser = parser;
    }

    public NSVirtualMachine vm() {
        return vm;
    }

    public NSClassParser classParser() {
        return classParser;
    }

    public abstract NSClass loadClass(String className) throws NSClassNotFoundException;

}
