package org.jglr.ns;

import java.io.*;
import java.util.*;

import org.jglr.ns.insns.*;
import org.jglr.ns.types.*;
import org.junit.*;

public class NeatScript implements NSOps, NSTypes
{

    @Test()
    public void testCompile() throws NSCompilerException
    {
        String source = read(NeatScript.class.getResourceAsStream("/test.sp"));

        List<NSInsn> insns = new NSCompiler().compile(source);
        Assert.assertTrue("Compilation failed", !insns.isEmpty());
        new NSInterpreter().interpret(insns);
    }

    private static String read(InputStream in)
    {
        byte[] buffer = new byte[2048];
        int i;
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while((i = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, i);
            }
            out.flush();
            out.close();
            return new String(out.toByteArray(), "UTF-8");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
