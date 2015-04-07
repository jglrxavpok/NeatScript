package org.jglr.ns.nativeclasses;

import org.jglr.ns.NSObject;
import org.jglr.ns.types.NSTypes;
import org.jglr.ns.vm.*;

import java.util.Stack;

public class NSIntClass extends NSNativeClass {

    public NSIntClass() {
        super("Int");
        javaMethod("toString", this::convertToString, NSTypes.STRING_TYPE, new String[0]);
    }

    private void convertToString(Stack<NSObject> objects) {
        NSObject object = objects.pop();
        objects.push(new NSObject(NSTypes.STRING_TYPE, object.value().toString()));
    }

}
