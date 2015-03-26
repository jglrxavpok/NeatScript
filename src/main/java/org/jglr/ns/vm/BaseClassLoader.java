package org.jglr.ns.vm;

import java.io.*;
import java.util.*;

import org.jglr.ns.*;

public class BaseClassLoader extends NSClassLoader {

    private HashMap<String, NSClass> classes;

    public BaseClassLoader(NSVirtualMachine vm, NSClassParser parser) {
        super(vm, parser);
        classes = new HashMap<>();
    }

    BaseClassLoader addNativeClass(NSNativeClass nat) {
        classes.put(nat.name(), nat);
        return this;
    }

    @Override
    public NSClass loadClass(String className) throws NSClassNotFoundException {
        if (classes.containsKey(className))
            return classes.get(className);
        System.out.println("Loading " + className);
        InputStream input = BaseClassLoader.class.getResourceAsStream("/" + className + ".nsc");
        byte[] buffer = new byte[65565];
        int n;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while ((n = input.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, n);
            }
            baos.flush();
            baos.close();
            NSClass clazz = classParser().parseClass(baos.toByteArray());
            classes.put(className, clazz);
            if (!clazz.superclass().equals("Object"))
                vm().getOrLoad(className);
            return clazz;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
