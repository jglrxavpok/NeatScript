package org.jglr.ns.types;

import org.jglr.ns.*;
import org.jglr.ns.types.*;

public class NSDummyType extends NSType {

    public NSDummyType(String id) {
        super(id, NSTypes.OBJECT_TYPE);
    }

    @Override
    public NSObject emptyObject() {
        return new NSObject(this, new Object());
    }

}
