package org.jglr.ns.vm;

import java.util.*;
import java.util.function.*;

import org.jglr.ns.*;
import org.jglr.ns.compiler.*;
import org.jglr.ns.funcs.*;
import org.jglr.ns.types.*;

public class NSNativeClass extends NSClass {

    public NSNativeClass(String name) {
        super(name);
    }

    public NSFuncDef rootMethod() {
        throw new UnsupportedOperationException("A native class doesn't have a root method");
    }

    public NSClass rootMethod(NSFuncDef method) {
        throw new UnsupportedOperationException("A native class doesn't have a root method");
    }

    public NSClass sourceFile(String sourceFile) {
        throw new UnsupportedOperationException("A native class doesn't have a source file");
    }

    public NSNativeClass javaMethod(String name, Consumer<Stack<NSObject>> method, String... types) {
        String[] names = generateNames(types);
        return javaMethod(name, method, names, types);
    }

    private String[] generateNames(String[] types) {
        String[] names = new String[types.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = types[i].toLowerCase() + i;
        }
        return names;
    }

    private String[] generateNames(NSType[] types) {
        String[] names = new String[types.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = types[i].getID().toLowerCase() + i;
        }
        return names;
    }

    public NSNativeClass javaMethod(String name, Consumer<Stack<NSObject>> method, String[] names, String... types) {
        NSType[] typesArray = new NSType[types.length];
        for (int i = 0; i < types.length; i++) {
            typesArray[i] = NSTypes.fromIDOrDummy(name);
        }
        return javaMethod(name, method, names, typesArray);
    }

    public NSNativeClass javaMethod(String name, Consumer<Stack<NSObject>> method, NSType... types) {
        String[] names = generateNames(types);
        return javaMethod(name, method, names, types);
    }

    public NSNativeClass javaMethod(String name, Consumer<Stack<NSObject>> method, String[] names, NSType... types) {
        methods().add(new NSJavaMethod(name, method, names, types));
        return this;
    }

    public String sourceFile() {
        return name() + ", native class";
    }

}
