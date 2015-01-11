package org.jglr.ns;

import java.io.*;
import java.util.*;

import org.jglr.ns.funcs.*;
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

    @Test
    public void french() throws NSCompilerException, IOException, NSClassNotFoundException, NSNoSuchMethodException, NSVirtualMachineException
    {
        NSTypes.STRING_TYPE.setID("Texte");
        NSTypes.INT_TYPE.setID("Entier");

        NSKeywords.ELSE.raw("sinon");
        NSKeywords.IF.raw("si");
        NSKeywords.THEN.raw("alors");
        NSKeywords.END.raw("fin");
        NSKeywords.FALSE.raw("faux");
        NSKeywords.TRUE.raw("vrai");
        NSKeywords.WHILE.raw("tantQue");
        NSKeywords.RETURN.raw("returne");
        NSSourceFile source = new NSSourceFile("Translated.ns", NeatScript.class.getResourceAsStream("/translated.ns"));
        NSClass clazz = new NSCompiler().compile(source);
        Assert.assertTrue("Compilation failed", clazz != null);
        NSVirtualMachine vm = new NSVirtualMachine();
        vm.newStdFunction("afficher", new NSNativeFunc("afficher")
        {

            @Override
            public void run(Stack<NSObject> vars)
            {
                NSObject var = vars.pop();
                System.out.println(var.value() + " | " + var.type());
            }
        });
        vm.entryPoint(clazz);
        List<NSType> types = new ArrayList<NSType>();
        //        types.add(NSTypes.STRING_TYPE);
        //        vm.methodCall(clazz.method("prettify", types), new NSObject(NSTypes.STRING_TYPE, "TEST"));
        vm.launch();

        NSTypes.STRING_TYPE.setID("String");
        NSTypes.INT_TYPE.setID("Int");

        NSKeywords.ELSE.raw("else");
        NSKeywords.IF.raw("if");
        NSKeywords.THEN.raw("then");
        NSKeywords.END.raw("end");
        NSKeywords.FALSE.raw("false");
        NSKeywords.TRUE.raw("true");
        NSKeywords.WHILE.raw("while");
        NSKeywords.RETURN.raw("return");
    }
}
