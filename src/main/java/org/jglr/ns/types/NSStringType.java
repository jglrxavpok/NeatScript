package org.jglr.ns.types;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.funcs.*;

public class NSStringType extends NSType
{

    public NSStringType()
    {
        super("String", NSTypes.OBJECT_TYPE);
        newFunction("length", new NSNativeFunc("length")
        {

            @Override
            public void run(Stack<NSObject> vars)
            {
                NSObject object = vars.pop();
                String str = (String) object.value();
                int length = str.length();
                vars.push(new NSObject(NSTypes.INT_TYPE, length));
            }
        });
    }

    @Override
    public NSObject operation(NSObject a, NSObject b, NSOperator operator)
    {
        if(operator == NSOperator.PLUS)
        {
            return new NSObject(NSTypes.STRING_TYPE, (String) a.value() + b.value());
        }
        else if(operator == NSOperator.MINUS)
        {
            return new NSObject(NSTypes.STRING_TYPE, ((String) a.value()).replace(String.valueOf(b.value()), ""));
        }
        else if(operator == NSOperator.EQUALITY_CHECK)
        {
            return new NSObject(NSTypes.BOOL_TYPE, a.value().equals(b.value()));
        }
        else if(operator == NSOperator.NON_EQUALITY_CHECK)
        {
            return new NSObject(NSTypes.BOOL_TYPE, !a.value().equals(b.value()));
        }
        return super.operation(a, b, operator);
    }

    @Override
    public boolean isCastable(NSType type)
    {
        return type == this;
    }

    @Override
    public Object cast(Object value, NSType type)
    {
        if(type == this)
            return value;
        return super.cast(value, type);
    }

    public NSType init(NSObject object)
    {
        //        if(object.value() == null)
        //        {
        //            object.field("size", new NSObject(NSTypes.INT_TYPE, 0));
        //            return this;
        //        }
        //        object.field("size", new NSObject(NSTypes.INT_TYPE, ((String) object.value()).length()));
        super.init(object);
        return this;
    }

    @Override
    public NSObject emptyObject()
    {
        return new NSObject(this, "");
    }

}
