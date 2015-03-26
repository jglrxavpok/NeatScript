package org.jglr.ns;

public class NSNoSuchFieldException extends Exception {

    private static final long serialVersionUID = -7228560325435660106L;

    public NSNoSuchFieldException(NSClass clazz, String name) {
        super("Field " + name + " not found in " + clazz);
    }

}
