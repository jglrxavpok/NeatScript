package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSFloatType extends NSType
{

    public NSFloatType()
    {
        super("Float", NSTypes.OBJECT_TYPE);
    }

    @Override
    public NSObject emptyObject()
    {
        return new NSObject(this, 0f);
    }

    public NSObject operation(NSObject a, NSObject b, NSOperator operator)
    {
        switch(operator)
        {
            case PLUS:
                return new NSObject(this, (float) ((float) a.value() + (float) b.castedValue(this)));

            case MINUS:
                return new NSObject(this, (float) ((float) a.value() - (float) b.castedValue(this)));

            case TIMES:
                return new NSObject(this, (float) ((float) a.value() * (float) b.castedValue(this)));

            case DIVIDE:
                return new NSObject(this, (float) ((float) a.value() / (float) b.castedValue(this)));

            case GREATER_THAN:
                return new NSObject(NSTypes.BOOL_TYPE, (float) a.value() > (float) b.castedValue(this));

            case LESS_THAN:
                return new NSObject(NSTypes.BOOL_TYPE, (float) a.value() < (float) b.castedValue(this));

            case GEQUAL:
                return new NSObject(NSTypes.BOOL_TYPE, (float) a.value() >= (float) b.castedValue(this));

            case LEQUAL:
                return new NSObject(NSTypes.BOOL_TYPE, (float) a.value() <= (float) b.castedValue(this));

            default:
                break;
        }
        return super.operation(a, b, operator);
    }

    @Override
    public boolean isCastable(NSType type)
    {
        return type == this || type == NSTypes.INT_TYPE;
    }

    @Override
    public Object cast(Object value, NSType type)
    {
        if(type == this)
            return value;
        if(type == NSTypes.INT_TYPE)
        {
            int castedValue = (int) (float) value; // We cast to an int before because the JVM doesn't translate 
                                                   // Integer and Float to their respective primitives types
            return castedValue;
        }
        return super.cast(value, type);
    }

}
