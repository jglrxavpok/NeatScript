package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSFloatType extends NSType
{

    @Override
    public String getID()
    {
        return "Float";
    }

    @Override
    public NSObject emptyObject()
    {
        return new NSObject(this, 0f);
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
            int castedValue = (int) value;
            return castedValue;
        }
        return super.cast(value, type);
    }

}
