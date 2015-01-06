package org.jglr.ns.vm;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.compiler.*;
import org.jglr.ns.funcs.*;
import org.jglr.ns.types.*;

public class NSVirtualMachine
{

    private NSClass                       entry;
    private NSInterpreter                 interpreter;
    private Stack<StackTraceElement>      stackTrace;
    private HashMap<String, NSClass>      classes;
    private NSClassLoader                 classLoader;
    private NSClass                       currentClass;
    private NSAbstractMethod              currentMethod;
    private HashMap<String, NSNativeFunc> functions;

    public NSVirtualMachine()
    {
        this.interpreter = new NSInterpreter(this);
        stackTrace = new Stack<>();
        classes = new HashMap<>();
        classLoader = new BaseClassLoader();

        functions = new HashMap<>();
        functions.put("print", new NSNativeFunc("print")
        {

            @Override
            public void run(Stack<NSObject> vars)
            {
                NSObject var = vars.pop();
                System.out.println(var.value() + " | " + var.type());
            }
        });
    }

    public void entryPoint(NSClass clazz)
    {
        this.entry = clazz;
        this.currentClass = entry;
        classes.put(clazz.name(), clazz);
    }

    public void launch() throws NSClassNotFoundException, NSNoSuchMethodException, NSVirtualMachineException
    {
        if(entry.rootMethod() == null)
        {
            throwVMError("No entry point found in " + entry);
        }
        else
        {
            methodCall(entry.rootMethod(), null);
        }
    }

    /**
     * Calls a given method
     * @param def
     *           The method to call
     * @param valueStack 
     *                  Can be null if <code>def</code> is an instance of {@link NSFuncDef}
     * @param parameters
     *                  The parameters passed to the method
     * @throws NSClassNotFoundException
     * @throws NSNoSuchMethodException
     * @throws NSVirtualMachineException
     */
    public void methodCall(NSAbstractMethod def, Stack<NSObject> valueStack) throws NSClassNotFoundException, NSNoSuchMethodException, NSVirtualMachineException
    {
        NSClass oldClass = currentClass;
        String owner = def.owner();
        NSClass ownerClass = getOrLoad(owner);
        NSAbstractMethod func = ownerClass.method(def.name(), def.types());
        this.currentMethod = func;
        pushTrace();
        this.currentClass = ownerClass;
        try
        {
            if(func instanceof NSFuncDef)
            {
                NSVariable[] vars = new NSVariable[def.paramNames().size()];
                for(int i = vars.length - 1; i >= 0; i-- )
                {
                    NSObject object = valueStack.pop();
                    vars[i] = new NSVariable(object.type(), func.paramNames().get(i), i).value(object);
                }
                interpreter.interpret(((NSFuncDef) func).instructions(), vars);
            }
            else
            {
                func.run(valueStack);
            }
        }
        catch(Exception e)
        {
            popTrace();
            pushTrace();
            throwVMException(e);
        }
        currentClass = oldClass;
        popTrace();
    }

    private void throwVMException(Exception e) throws NSVirtualMachineException
    {
        throw new NSVirtualMachineException(e.getMessage(), generateStackTrace(), e);
    }

    private void pushTrace()
    {
        String declaringClass = currentClass.name();
        String methodName = currentMethod.name();
        String fileName = currentClass.sourceFile();
        int lineNumber = interpreter.lineNumber();
        StackTraceElement item = new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
        stackTrace.push(item);
    }

    private void popTrace()
    {
        stackTrace.pop();
    }

    public NSClass getOrLoad(String owner) throws NSClassNotFoundException
    {
        if(isLoaded(owner))
        {
            return classes.get(owner);
        }
        return loadClass(owner);
    }

    private NSClass loadClass(String classID) throws NSClassNotFoundException
    {
        return classLoader.loadClass(classID);
    }

    private boolean isLoaded(String owner)
    {
        return classes.containsKey(owner) && classes.get(owner) != null;
    }

    private StackTraceElement[] generateStackTrace()
    {
        return stackTrace.toArray(new StackTraceElement[0]);
    }

    private void throwVMException(String string) throws NSVirtualMachineException
    {
        throw new NSVirtualMachineException(string, generateStackTrace(), true);
    }

    private void throwVMError(String string) throws NSVirtualMachineError
    {
        throw new NSVirtualMachineError(string);
    }

    public void callStdFunction(String functionName, Stack<NSObject> valuesStack) throws NSNoSuchMethodException
    {
        if(functions.containsKey(functionName))
        {
            functions.get(functionName).run(valuesStack);
        }
        else
            throw new NSNoSuchMethodException("std", functionName, new ArrayList<NSType>());
    }

}
