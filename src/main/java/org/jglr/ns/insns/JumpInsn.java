package org.jglr.ns.insns;

public class JumpInsn extends LabelInsn {
    public JumpInsn(Label label) {
        super(GOTO, label);
    }
}
