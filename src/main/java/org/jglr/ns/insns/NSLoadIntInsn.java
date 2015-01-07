package org.jglr.ns.insns;

public class NSLoadIntInsn extends NSInsn
{

    private int value;

    public NSLoadIntInsn(int value)
    {
        super(ILOAD);
        this.value = value;
    }

    public int value()
    {
        return value;
    }

}
