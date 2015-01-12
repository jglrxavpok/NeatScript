package org.jglr.ns.vm;

import java.io.*;
import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.compiler.*;
import org.jglr.ns.insns.*;
import org.jglr.ns.types.*;

public class NSClassParser
{

    private NSVirtualMachine vm;

    public NSClassParser(NSVirtualMachine vm)
    {
        this.vm = vm;
    }

    public NSClass parseClass(byte[] classData) throws IOException, NSClassNotFoundException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(classData);
        DataInputStream in = new DataInputStream(bais);
        int magicNumber = in.readInt();
        if(magicNumber == (0xDEAFCAFE | 0xBADA55))
        {
            String name = in.readUTF();
            String sourceFile = in.readUTF();
            int nMethods = in.readInt();
            NSClass clazz = new NSClass(name).sourceFile(sourceFile);
            List<NSAbstractMethod> methods = new ArrayList<>();
            for(int i = 0; i < nMethods; i++ )
                readMethod(in, methods, clazz);

            return clazz;
        }
        else
            throw new IllegalArgumentException("Given classData doesn't have the correct magic number: " + magicNumber);
    }

    private void readMethod(DataInputStream in, List<NSAbstractMethod> methods, NSClass clazz) throws IOException, NSClassNotFoundException
    {
        boolean isNative = in.readBoolean();
        if(isNative)
            return;
        String name = in.readUTF();
        NSFuncDef def = new NSFuncDef();
        def.name(name);
        int params = in.readInt();
        for(int i = 0; i < params; i++ )
        {
            String paramName = in.readUTF();
            NSType type = vm.getType(in.readUTF(), clazz);
            def.paramNames().add(paramName);
            def.types().add(type);
        }
        int instructionsNumber = in.readInt();
        List<NSInsn> insns = new ArrayList<NSInsn>();
        for(int i = 0; i < instructionsNumber; i++ )
        {
            int opcode = in.readInt();
            switch(opcode)
            {
                case NSOps.LOAD_CONSTANT:
                    insns.add(new LoadConstantInsn(null).read(in));
                    break;

                case NSOps.OPERATOR:
                    insns.add(new OperatorInsn(null).read(in));
                    break;

                case NSOps.LINE_NUMBER:
                    insns.add(new LineNumberInsn(0).read(in));
                    break;

                case NSOps.FUNCTION_CALL:
                    insns.add(new FunctionCallInsn("").read(in));
                    break;

                case NSOps.LABEL:
                    insns.add(new LabelInsn(null).read(in));
                    break;

                case NSOps.GOTO:
                    insns.add(new JumpInsn(null).read(in));
                    break;

                case NSOps.IF_GOTO:
                    insns.add(new LabelInsn(opcode, null).read(in));
                    break;

                case NSOps.IF_NOT_GOTO:
                    insns.add(new LabelInsn(opcode, null).read(in));
                    break;

                case NSOps.STACK_PUSH:
                    insns.add(new StackInsn(opcode).read(in));
                    break;

                case NSOps.STACK_PEEK:
                    insns.add(new StackInsn(opcode).read(in));
                    break;

                case NSOps.STACK_POP:
                    insns.add(new StackInsn(opcode).read(in));
                    break;

                case NSOps.NEW_VAR:
                    insns.add(new NewVarInsn(null, null, 0).read(in));
                    break;

                case NSOps.VAR_LOAD:
                    insns.add(new NSVarInsn(opcode, 0).read(in));
                    break;

                case NSOps.VAR_STORE:
                    insns.add(new NSVarInsn(opcode, 0).read(in));
                    break;

                case NSOps.GET_FIELD:
                    insns.add(new LoadFieldInsn(opcode, "").read(in));
                    break;

                case NSOps.POP:
                    insns.add(new NSBaseInsn(opcode).read(in));
                    break;

                case NSOps.RETURN:
                    insns.add(new NSBaseInsn(opcode).read(in));
                    break;

                case NSOps.RETURN_VALUE:
                    insns.add(new NSBaseInsn(opcode).read(in));
                    break;

                case NSOps.ILOAD:
                    insns.add(new NSLoadIntInsn(0).read(in));
                    break;

                case NSOps.FLOAD:
                    insns.add(new NSLoadFloatInsn(0).read(in));
                    break;
            }
        }
        def.instructions().addAll(insns);
    }
}
