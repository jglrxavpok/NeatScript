package org.jglr.ns.insns;

public class LoadFieldInsn extends NSInsn
{

    private String fieldName;

    public LoadFieldInsn(String fieldName)
    {
        this(GET_FIELD, fieldName);
    }

    public LoadFieldInsn(int opcode, String fieldName)
    {
        super(opcode);
        this.fieldName = fieldName;
    }

    public String fieldName()
    {
        return fieldName;
    }

    public String toString()
    {
        return super.toString() + " " + fieldName;
    }

}
