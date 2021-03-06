package org.jglr.ns;

import org.jglr.ns.types.*;

public class NSField extends NSVariable {

    public NSField(NSType type, String name) {
        super(type, name, -42);
    }

    public String toString() {
        return type().getID() + " " + name();
    }
}
