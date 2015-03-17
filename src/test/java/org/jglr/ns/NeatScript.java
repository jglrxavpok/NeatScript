package org.jglr.ns;

import java.io.*;

import org.jglr.ns.compiler.*;
import org.jglr.ns.types.*;
import org.jglr.ns.vm.*;
import org.junit.*;

public class NeatScript implements NSOps, NSTypes
{
    @Test
    public void testCompile() throws NSCompilerException, IOException, NSClassNotFoundException, NSNoSuchMethodException, NSVirtualMachineException
    {
        NSSourceFile source = new NSSourceFile("TestClass.ns", NeatScript.class.getResourceAsStream("/test.ns"));
        NSClass clazz = new NSCompiler().compile(source);
        NSClassWriter writer = new NSClassWriter();
        byte[] rawBytecode = writer.writeClass(clazz);
        FileOutputStream out = new FileOutputStream(new File(".", "TestClass.nsc"));
        out.write(rawBytecode);
        out.flush();
        out.close();

        Assert.assertTrue("Compilation failed", clazz != null);
        NSVirtualMachine vm = new NSVirtualMachine();
        vm.entryPoint(clazz);
        vm.launch();
    }
}
