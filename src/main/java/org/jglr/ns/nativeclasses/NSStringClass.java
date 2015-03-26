package org.jglr.ns.nativeclasses;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.types.*;
import org.jglr.ns.vm.*;

public class NSStringClass extends NSNativeClass {

    public NSStringClass() {
        super("String");
        javaMethod("length", this::length, NSTypes.STRING_TYPE);
        field(new NSField(NSTypes.INT_TYPE, "size"));
    }

    public void length(Stack<NSObject> objects) {
        NSObject object = objects.pop();
        objects.push(object.field("size"));
    }

    @Override
    public NSObject init(NSObject object) {
        if (object.value() == null) {
            object.field("size", new NSObject(NSTypes.INT_TYPE, 0));
            return object;
        }
        object.field("size", new NSObject(NSTypes.INT_TYPE, ((String) object.value()).length()));
        return object;
    }

}
