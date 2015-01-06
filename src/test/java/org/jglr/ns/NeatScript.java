package org.jglr.ns;

import java.io.*;
import java.util.*;

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
        Assert.assertTrue("Compilation failed", clazz != null);
        NSVirtualMachine vm = new NSVirtualMachine();
        vm.entryPoint(clazz);
        List<NSType> types = new ArrayList<NSType>();
        //        types.add(NSTypes.STRING_TYPE);
        //        vm.methodCall(clazz.method("prettify", types), new NSObject(NSTypes.STRING_TYPE, "TEST"));
        vm.launch();
    }
}
