package org.jglr.ns.compiler;

import java.io.*;

import org.jglr.ns.*;
import org.jglr.ns.funcs.*;
import org.jglr.ns.insns.*;

public class NSClassWriter
{

    public NSClassWriter()
    {

    }

    public byte[] writeClass(NSClass clazz) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeInt(0xDEAFCAFE | 0xBADA55);
        out.writeUTF(clazz.name());
        out.writeUTF(clazz.sourceFile());
        out.writeUTF(clazz.superclass());
        int nMethods = clazz.methods().size();
        out.writeInt(nMethods);
        for(int i = 0; i < nMethods; i++ )
            writeMethod(out, clazz.methods().get(i));
        out.flush();
        out.close();
        return baos.toByteArray();
    }

    private void writeMethod(DataOutputStream out, NSAbstractMethod method) throws IOException
    {
        if(method instanceof NSNativeFunc)
        {
            out.writeBoolean(true); // We indicate that this method is native and the parser won't have to check for opcodes
        }
        else
        {
            out.writeBoolean(false);
            NSFuncDef def = (NSFuncDef) method;
            int n = def.instructions().size();
            out.writeUTF(method.name());
            int paramSize = def.paramNames().size();
            for(int i = 0; i < paramSize; i++ )
            {
                String name = def.paramNames().get(i);
                out.writeUTF(name);
                out.writeUTF(def.types().get(i).getID());
            }
            out.writeInt(n);
            for(int i = 0; i < n; i++ )
            {
                NSInsn insn = def.instructions().get(i);
                out.writeInt(insn.getOpcode());
                insn.write(out);
            }
        }
    }
}
