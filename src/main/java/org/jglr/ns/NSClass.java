package org.jglr.ns;

import java.util.*;

import com.google.common.collect.*;

import org.jglr.ns.compiler.*;
import org.jglr.ns.types.*;

public class NSClass {

    private String name;
    private List<NSAbstractMethod> methodsDef;
    private NSAbstractMethod rootMethod;
    private String sourceFile;
    private String superclass;
    private List<NSField> fields;

    public NSClass(String name) {
        this.name = name;
        methodsDef = new ArrayList<NSAbstractMethod>();
        fields = Lists.newArrayList();
        superclass("Object");
    }

    public List<NSField> fields() {
        return fields;
    }

    public boolean hasField(String name) {
        for (NSField field : fields) {
            if (field.name().equals(name))
                return true;
        }
        return false;
    }

    public String superclass() {
        return superclass;
    }

    public NSClass superclass(String superclass) {
        this.superclass = superclass;
        return this;
    }

    public NSAbstractMethod rootMethod() {
        return rootMethod;
    }

    public NSClass rootMethod(NSAbstractMethod method) {
        rootMethod = method;
        if (!methodsDef.contains(method))
            methodsDef.add(method);
        return this;
    }

    public List<NSAbstractMethod> methods() {
        return methodsDef;
    }

    public String name() {
        return name;
    }

    public String toString() {
        return "class " + name;
    }

    public NSAbstractMethod method(String methodName, List<NSType> types) throws NSNoSuchMethodException {
        methodSearch: for (NSAbstractMethod method : methodsDef) {
            if (method.name().equals(methodName)) {
                if (types.size() != method.types().size()) {
                    continue;
                }
                for (int i = 0; i < types.size(); i++) {
                    if (!types.get(i).getID().equals(method.types().get(i).getID())) {
                        continue methodSearch;
                    }
                }
                return method;
            }
        }
        throw new NSNoSuchMethodException(name, methodName, types);
    }

    public NSClass sourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public String sourceFile() {
        if (sourceFile == null)
            return "_dynamic_";
        return sourceFile;
    }

    public NSField field(String name) {
        for (NSField f : fields) {
            if (f.name().equals(name))
                return f;
        }
        System.out.println("No field " + name + " in " + this);
        return null;
    }

    public NSClass field(NSType type, String name) {
        return field(new NSField(type, name));
    }

    public NSClass field(NSField field) {
        fields.add(field);
        return this;
    }

    public boolean hasMethod(String methodName, List<NSType> types) {
        methodSearch: for (NSAbstractMethod method : methodsDef) {
            if (method.name().equals(methodName)) {
                if (types.size() != method.types().size()) {
                    continue;
                }
                for (int i = 0; i < types.size(); i++) {
                    if (!types.get(i).getID().equals(method.types().get(i).getID())) {
                        continue methodSearch;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public NSObject init(NSObject object) {
        return object;
    }
}
