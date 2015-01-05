package org.jglr.ns.insns;

public class LoadStaticFieldInsn extends LoadFieldInsn
{

    private String owner;

    public LoadStaticFieldInsn(String owner, String fieldName)
    {
        super(GET_STATIC_FIELD, fieldName);
        this.owner = owner;
    }

    public String owner()
    {
        return owner;
    }

    public String toString()
    {
        return super.toString() + " <= " + owner;
    }
}
