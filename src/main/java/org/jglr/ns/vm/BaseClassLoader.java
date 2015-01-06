package org.jglr.ns.vm;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.funcs.*;
import org.jglr.ns.types.*;

public class BaseClassLoader extends NSClassLoader
{

    @Override
    public NSClass loadClass(String className) throws NSClassNotFoundException
    {
        if(className.equals("String"))
        {
            NSNativeClass clazz = new NSNativeClass("String");
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
        return null;
    }
}
