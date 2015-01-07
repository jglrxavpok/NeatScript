package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSIntType extends NSType
{

    @Override
    public String getID()
    {
        return "Int";
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
            return value;
        if(type == NSTypes.FLOAT_TYPE)
        {
            float castedValue = (float) value;
            return castedValue;
        }
        return super.cast(value, type);
    }
}
