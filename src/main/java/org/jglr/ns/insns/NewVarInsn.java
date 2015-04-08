package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.NSVariable;
import org.jglr.ns.types.*;
import org.jglr.ns.vm.*;

public class NewVarInsn extends NSVarInsn {


    private NSVariable var;
    private String name;
    private NSType type;
    private Label endLabel;
    private Label startLabel;

    public NewVarInsn(NSVariable var) {
        this(var.type(), var.name(), var.varIndex());
        this.var = var;
    }

    public NewVarInsn(NSType type, String name, int varIndex) {
        super(NEW_VAR, varIndex);
        this.name = name;
        this.type = type;
    }

    public NSType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public Label endLabel() {
        return endLabel;
    }

    public Label startLabel() {
        return startLabel;
    }

    public String toString() {
        return super.toString() + " " + type().getID() + " " + name()+ " [" + var.startLabel() + "," + var.endLabel() + "]";
    }

    public NSInsn write(DataOutput output) throws IOException {
        super.write(output);
        output.writeUTF(name);
        output.writeUTF(type.getID());
        if(var != null) {
            startLabel = var.startLabel();
            endLabel = var.endLabel();
        } else {
            startLabel = new Label("L0");
            endLabel = new Label("L-1");
        }
        output.writeUTF(startLabel.id());
        output.writeUTF(endLabel.id());
        return this;
    }

    public NSInsn read(NSVirtualMachine vm, DataInput input) throws IOException {
        super.read(vm, input);
        name = input.readUTF();
        try {
            type = vm.getType(input.readUTF());
        } catch (NSClassNotFoundException e) {
            e.printStackTrace();
        }
        startLabel = new Label(input.readUTF());
        endLabel = new Label(input.readUTF());
        return this;
    }
}
