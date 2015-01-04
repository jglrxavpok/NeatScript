package org.jglr.ns.insns;

public class LabelInsn extends NSInsn
{
    private Label label;

    public LabelInsn(int opcode, Label label)
    {
        super(opcode);
        this.label = label;
    }

    public LabelInsn(Label label)
    {
        this(LABEL, label);
    }

    public Label label()
    {
        return label;
    }

    public String toString()
    {
        return super.toString() + " " + label.id();
    }

}
