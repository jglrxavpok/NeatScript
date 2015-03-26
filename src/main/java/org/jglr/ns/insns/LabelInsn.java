package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.vm.*;

public class LabelInsn extends NSInsn {
    private Label label;

    public LabelInsn(int opcode, Label label) {
        super(opcode);
        this.label = label;
    }

    public LabelInsn(Label label) {
        this(LABEL, label);
    }

    public Label label() {
        return label;
    }

    public String toString() {
        return super.toString() + " " + label.id();
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException {
        out.writeUTF(label.id());
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException {
        label = new Label(in.readUTF());
        return this;
    }

}
