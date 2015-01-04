package org.jglr.ns.insns;

public class LoadConstantInsn extends NSInsn
{

    private Object value;

    public LoadConstantInsn(Object val)
    {
        super(LOAD_CONSTANT);
        this.value = val;
    }

    public Object getConstant()
    {
        return value;
    }

    public String toString()
    {
        String str = value + "";
        if(value instanceof String)
        {
            str = "\"" + str + "\"";
        }
        return super.toString() + " " + str;
    }
}
