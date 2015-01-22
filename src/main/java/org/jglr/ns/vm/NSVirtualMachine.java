package org.jglr.ns.vm;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.compiler.*;
import org.jglr.ns.funcs.*;
import org.jglr.ns.insns.*;
import org.jglr.ns.types.*;

public class NSVirtualMachine
{

    private NSInterpreter                 interpreter;
    private Stack<StackTraceElement>      stackTrace;
    private HashMap<String, NSClass>      classes;
    private NSClassLoader                 classLoader;
    private NSClass                       currentClass;
    private NSAbstractMethod              currentMethod;
    private HashMap<String, NSNativeFunc> functions;
    private HashMap<NSClass, NSVariable>  selfInstances;
    private NSClass                       entry;
    private static NSVirtualMachine       runningInstance;

    public NSVirtualMachine()
    {
        runningInstance = this;
        this.interpreter = new NSInterpreter(this);
        stackTrace = new Stack<>();
        classes = new HashMap<>();
        selfInstances = new HashMap<>();
        classLoader = new BaseClassLoader(this, new NSClassParser(this));

        functions = new HashMap<>();
        functions.put("print", new NSNativeFunc("print")
        {

            @Override
            public void run(Stack<NSObject> vars)
            {
                NSObject var = vars.pop();
                System.out.println(var.value() + " | " + var.type().getID());
            }
        });
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
        methodCall(def, valueStack, false);
    }

    public void methodCall(NSAbstractMethod def, Stack<NSObject> valueStack, boolean selfOnStack) throws NSClassNotFoundException, NSNoSuchMethodException, NSVirtualMachineException
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
                NSVariable[] vars = new NSVariable[def.types().size() + 1];
                for(int i = vars.length - 1; i >= 1; i-- )
                {
                    NSObject object = valueStack.pop();
                    vars[i] = new NSVariable(object.type(), func.paramNames().get(i - 1), i).value(object);
                }
                if(selfOnStack)
                {
                    NSObject self = valueStack.pop();
                    System.out.println("Self: " + self);
                    vars[0] = new NSVariable(self.type(), "self", 0).value(self);
                }
                else
                    vars[0] = getSelfInstance(currentClass);
                NSObject object = interpreter.interpret(currentClass, ((NSFuncDef) func).instructions(), vars);
                if(object != null)
                {
                    valueStack.push(object);
                }
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

    private NSVariable getSelfInstance(NSClass clazz) throws NSClassNotFoundException
    {
        if(selfInstances.containsKey(clazz))
        {
            return selfInstances.get(clazz);
        }
        NSType type = getType(clazz.name());
        NSVariable self = new NSVariable(type, "this", 0);
        self.value(new NSObject(type));
        selfInstances.put(clazz, self);
        return self;
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
        NSClass clazz = classLoader.loadClass(classID);
        printContent(clazz);
        classes.put(classID, clazz);
        return clazz;
    }

    private void printContent(NSClass clazz)
    {
        StringBuffer buffer = new StringBuffer();
        String indent = "";
        buffer.append(clazz.name() + "(" + clazz.sourceFile() + ")");
        indent += " ";
        buffer.append('\n');
        buffer.append('{');
        buffer.append('\n');
        for(NSAbstractMethod method : clazz.methods())
        {
            buffer.append(indent + method.toString());
            indent += " ";
            if(method instanceof NSNativeFunc)
            {
                buffer.append(indent + "Native code");
            }
            else if(method instanceof NSFuncDef)
            {
                NSFuncDef def = (NSFuncDef) method;
                for(NSInsn insn : def.instructions())
                {
                    buffer.append('\n');
                    if(insn.getOpcode() == NSOps.LABEL)
                    {
                        buffer.append(indent + insn.toString());
                    }
                    else
                    {
                        buffer.append(indent + "   " + insn.toString());
                    }
                }
                indent.replaceFirst(" ", "");
                buffer.append('\n');
            }
            indent.replaceFirst(" ", "");
            buffer.append('\n');
        }
        buffer.append('}');
        System.out.println(buffer.toString());
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

    public void newStdFunction(String name, NSNativeFunc method)
    {
        functions.put(name, method);
    }

    public NSType getType(String typeID) throws NSClassNotFoundException
    {
        for(NSType type : NSTypes.list())
        {
            if(type.getID().equals(typeID))
                return type;
        }
        NSType type = new NSClassType(getOrLoad(typeID));
        NSTypes.list().add(type);
        return type;
    }

    public static NSVirtualMachine instance()
    {
        return runningInstance;
    }

    public NSVirtualMachine addClass(NSClass clazz) throws NSClassNotFoundException
    {
        classes.put(clazz.name(), clazz);
        printContent(clazz);
        NSType type = new NSClassType(clazz);
        NSTypes.list().add(type);
        return this;
    }

    public NSVirtualMachine entryPoint(NSClass clazz)
    {
        this.entry = clazz;
        this.currentClass = entry;
        classes.put(clazz.name(), clazz);
        printContent(clazz);
        return this;
    }

    public NSVirtualMachine launch() throws NSClassNotFoundException, NSNoSuchMethodException, NSVirtualMachineException
    {
        if(entry.rootMethod() == null)
        {
            throwVMError("No entry point found in " + entry);
        }
        else
        {
            methodCall(entry.rootMethod(), new Stack<>());
        }
        return this;
    }

    public NSObject getNewInstance(NSClass clazz, List<NSType> types, Stack<NSObject> valuesStack) throws NSClassNotFoundException, NSNoSuchMethodException, NSVirtualMachineException
    {
        NSType type = getType(clazz.name());
        NSObject object = new NSObject(type).value(new Object());
        valuesStack.add(valuesStack.size() - types.size() + 1, object);
        System.out.println("Added " + object + " at index " + (valuesStack.size() - types.size() - 1) + ". Size of stack: " + (valuesStack.size() - 1) + ", size of types: " + types.size());
        types.remove(0);
        methodCall(clazz.method("$", types), valuesStack, true);
        return object;
    }

}
