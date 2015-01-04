package org.jglr.ns.insns;


public class LineNumberInsn extends NSInsn
{

    private int lineNumber;

    public LineNumberInsn(int line)
    {
        super(LINE_NUMBER);
        this.lineNumber = line;
    }

    public int number()
    {
        return lineNumber;
    }

    public String toString()
    {
        return super.toString() + " " + lineNumber;
    }
}
