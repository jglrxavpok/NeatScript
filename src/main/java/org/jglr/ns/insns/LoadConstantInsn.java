package org.jglr.ns.insns;

import java.io.*;

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

    @Override
    public NSInsn write(DataOutput out) throws IOException
    {
        if(value == null)
            out.writeUTF("Nothing");
        else
            out.writeUTF(value.getClass().getSimpleName());
        out.writeUTF(value.toString());

        return this;
    }

    @Override
    public NSInsn read(DataInput in) throws IOException
    {
        String type = in.readUTF();
        String value = in.readUTF();
        switch(type)
        {
            case "String":
                this.value = value;
                break;

            case "Float":
                this.value = Float.parseFloat(value);
                break;

            case "Integer":
                this.value = Integer.parseInt(value);
                break;

            case "Nothing":
                this.value = null;
                break;
        }

        return this;
    }
}
