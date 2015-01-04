package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSStringType extends NSType
{

    @Override
    public String getID()
    {
        return "String";
    }

    @Override
    public NSObject operation(NSObject a, NSObject b, NSOperator operator)
    {
        if(operator == NSOperator.PLUS)
        {
            return new NSObject(NSTypes.STRING_TYPE).value(a.value() + (String) b.value());
        }
        else if(operator == NSOperator.MINUS)
        {
            return new NSObject(NSTypes.STRING_TYPE).value(((String) a.value()).replace((String) b.value(), ""));
        }
        else if(operator == NSOperator.EQUALITY_CHECK)
        {
            return new NSObject(NSTypes.BOOL_TYPE).value(a.value().equals(b.value()));
        }
        throw new UnsupportedOperationException("Operator not supported: " + operator.toString());
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

}
