package org.jglr.ns.insns;

public class NSLoadFloatInsn extends NSInsn
{

    private float value;

    public NSLoadFloatInsn(float value)
    {
        super(FLOAD);
        this.value = value;
    }

    public float value()
    {
        return value;
    }

}
