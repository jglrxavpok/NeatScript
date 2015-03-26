package org.jglr.ns;

import java.io.*;

import org.jglr.ns.compiler.*;
import org.jglr.ns.types.*;
import org.jglr.ns.vm.*;
import org.junit.*;

public class NeatScript implements NSOps, NSTypes {
    @Test
    public void testCompile() throws NSCompilerException, IOException, NSClassNotFoundException, NSNoSuchMethodException,
            NSVirtualMachineException {
        NSSourceFile source = new NSSourceFile("TestClass.ns", NeatScript.class.getResourceAsStream("/test.ns"));

        //  NSSourceFile source2 = new NSSourceFile("SecondTest.ns", NeatScript.class.getResourceAsStream("/test2.ns"));
        NSCompiler compiler = new NSCompiler();
        NSClass clazz = compiler.compile(source);
        //    NSClass clazz2 = compiler.compile(source2);

        NSClassWriter writer = new NSClassWriter();
        byte[] rawBytecode = writer.writeClass(clazz);
        FileOutputStream out = new FileOutputStream(new File(".", "TestClass.nsc"));
        out.write(rawBytecode);
        out.flush();
        out.close();

        //        rawBytecode = writer.writeClass(clazz2);
        //        out = new FileOutputStream(new File(".", "SecondTest.nsc"));
        //        out.write(rawBytecode);
        //        out.flush();
        //        out.close();

        Assert.assertTrue("Compilation failed", clazz != null);
        NSVirtualMachine vm = new NSVirtualMachine();
        //        vm.addClass(clazz2);
        vm.entryPoint(clazz).launch();
    }
}
