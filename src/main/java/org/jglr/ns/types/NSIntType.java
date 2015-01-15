package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSIntType extends NSType
{

    public NSIntType()
    {
        super("Int", NSTypes.OBJECT_TYPE);
    }

    public NSObject operation(NSObject a, NSObject b, NSOperator operator)
    {
        switch(operator)
        {
            case PLUS:
            {
                return new NSObject(this, (int) ((int) a.value() + (float) b.castedValue(NSTypes.FLOAT_TYPE)));
            }

            case MINUS:
                return new NSObject(this, (int) ((int) a.value() - (float) b.castedValue(NSTypes.FLOAT_TYPE)));

            case TIMES:
                return new NSObject(this, (int) ((int) a.value() * (float) b.castedValue(NSTypes.FLOAT_TYPE)));

            case DIVIDE:
                return new NSObject(this, (int) ((int) a.value() / (float) b.castedValue(NSTypes.FLOAT_TYPE)));

            case GREATER_THAN:
                return (int) a.value() > (float) b.castedValue(NSTypes.FLOAT_TYPE) ? NSTypes.BOOL_TYPE.TRUE : NSTypes.BOOL_TYPE.FALSE;

            case LESS_THAN:
                return (int) a.value() < (float) b.castedValue(NSTypes.FLOAT_TYPE) ? NSTypes.BOOL_TYPE.TRUE : NSTypes.BOOL_TYPE.FALSE;

            case GEQUAL:
                return (int) a.value() >= (float) b.castedValue(NSTypes.FLOAT_TYPE) ? NSTypes.BOOL_TYPE.TRUE : NSTypes.BOOL_TYPE.FALSE;

            case LEQUAL:
                return (int) a.value() <= (float) b.castedValue(NSTypes.FLOAT_TYPE) ? NSTypes.BOOL_TYPE.TRUE : NSTypes.BOOL_TYPE.FALSE;

            case LEFT_SHIFT:
                return new NSObject(this, (int) ((int) a.value() << (int) b.castedValue(this)));

            case RIGHT_SHIFT:
                return new NSObject(this, (int) ((int) a.value() >> (int) b.castedValue(this)));

            case UNSIGNED_RIGHT_SHIFT:
                return new NSObject(this, (int) ((int) a.value() >>> (int) b.castedValue(this)));

            default:
                break;
        }
        return super.operation(a, b, operator);
    }

    @Override
    public NSObject emptyObject()
    {
        return new NSObject(this, 0);
    }

    @Override
    public boolean isCastable(NSType type)
    {
        return type == this || type == NSTypes.FLOAT_TYPE;
    }

    @Override
    public Object cast(Object value, NSType type)
    {
        if(type == this)
        {
            return value;
        }
        if(type == NSTypes.FLOAT_TYPE)
        {
            float castedValue = (float) (int) value; // We cast to an int before because the JVM doesn't translate 
                                                     // Integer and Float to their respective primitives types
            return castedValue;
        }
        return super.cast(value, type);
    }
}
