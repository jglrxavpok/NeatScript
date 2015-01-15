package org.jglr.ns.vm;

import java.io.*;
import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.funcs.*;
import org.jglr.ns.types.*;

public class BaseClassLoader extends NSClassLoader
{

    private HashMap<String, NSClass> classes;

    public BaseClassLoader(NSVirtualMachine vm, NSClassParser parser)
    {
        super(vm, parser);
        classes = new HashMap<>();
    }

    @Override
    public NSClass loadClass(String className) throws NSClassNotFoundException
    {
        if(classes.containsKey(className))
            return classes.get(className);
        if(className.equals(NSTypes.STRING_TYPE.getID()))
        {
            NSNativeClass clazz = new NSNativeClass(NSTypes.STRING_TYPE.getID());
            NSNativeFunc lengthFunc = new NSNativeFunc("length")
            {

                @Override
                public void run(Stack<NSObject> vars)
                {
                    NSObject object = vars.pop();
                    if(object.type().isCastable(NSTypes.STRING_TYPE))
                    {
                        String str = (String) object.type().cast(object.value(), NSTypes.STRING_TYPE);
                        vars.push(new NSObject(NSTypes.STRING_TYPE, str.length() + "")); // TODO: Replace String type by Int type
                    }
                    else
                        throw new RuntimeException(object.type().getID() + " cannot be casted to a String");
                }

            };
            clazz.methods().add(lengthFunc);
            lengthFunc.types().add(NSTypes.STRING_TYPE);
            lengthFunc.paramNames().add("string");
            return clazz;
        }
        else
        {
            InputStream input = BaseClassLoader.class.getResourceAsStream(className + ".nsc");
            byte[] buffer = new byte[65565];
            int n;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try
            {
                while((n = input.read(buffer, 0, buffer.length)) != -1)
                {
                    baos.write(buffer, 0, n);
                }
                baos.flush();
                baos.close();
                NSClass clazz = classParser().parseClass(baos.toByteArray());
                classes.put(className, clazz);
                if(!clazz.superclass().equals("Object"))
                    vm().getOrLoad(className);
                return clazz;
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
