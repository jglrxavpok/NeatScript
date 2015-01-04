package org.jglr.ns;

import java.util.*;

import org.jglr.ns.funcs.*;
import org.jglr.ns.insns.*;
import org.jglr.ns.types.*;

public class NSInterpreter implements NSOps, NSTypes
{

    private HashMap<String, NSFunc> functions;
    private int                     lineNumber;
    private Stack<NSObject>         heapStack;

    public NSInterpreter()
    {
        NSOps.initAllNames();
        functions = new HashMap<>();
        functions.put("print", new NSFunc("print")
        {

            @Override
            public void run(Stack<NSObject> vars)
            {
                NSObject var = vars.pop();
                System.out.println(var.value() + " | " + var.type());
            }
        });

        functions.put("prettify", new NSFunc("prettify")
        {

            @Override
            public void run(Stack<NSObject> vars)
            {
                NSObject var = vars.pop();
                NSObject newVar = var.copy();
                newVar.value("$$ " + newVar.value() + " $$");
                vars.push(newVar);
            }
        });

        heapStack = new Stack<>();
    }

    public void interpret(List<NSInsn> insns)
    {
        Stack<NSObject> vars = new Stack<>();
        lineNumber = 0;
        for(NSInsn insn : insns)
        {
            System.out.println("$ " + insn.toString());
        }
        for(int index = 0; index < insns.size(); index++ )
        {
            NSInsn insn = insns.get(index);
            if(insn.getOpcode() == LINE_NUMBER)
            {
                LineNumberInsn line = (LineNumberInsn) insn;
                lineNumber = line.number();
            }
            else if(insn.getOpcode() == LOAD_CONSTANT)
            {
                LoadConstantInsn load = (LoadConstantInsn) insn;
                Object cst = load.getConstant();
                NSType type = null;
                if(cst instanceof String)
                {
                    type = STRING_TYPE;
                }
                else if(cst instanceof Boolean)
                    type = BOOL_TYPE; // TODO: True type verification
                vars.push(new NSObject(type).value(cst));
            }
            else if(insn.getOpcode() == IF_NOT_GOTO || insn.getOpcode() == IF_GOTO)
            {
                LabelInsn jumpInsn = (LabelInsn) insn;
                NSObject value = vars.pop();
                NSObject result = NSTypes.BOOL_TYPE.operation(value, NSTypes.BOOL_TYPE.TRUE, NSOperator.EQUALITY_CHECK);
                if(result == (insn.getOpcode() == IF_GOTO ? NSTypes.BOOL_TYPE.TRUE : NSTypes.BOOL_TYPE.FALSE))
                {
                    index = gotoLabel(insns, index, jumpInsn.label()) - 1;
                    System.out.println("[DEBUG] Jumping to label " + jumpInsn.label().id());
                }
            }
            else if(insn.getOpcode() == STACK_PUSH)
            {
                heapStack.push(vars.pop());
            }
            else if(insn.getOpcode() == STACK_PEEK)
            {
                vars.push(heapStack.peek());
            }
            else if(insn.getOpcode() == STACK_POP)
            {
                vars.push(heapStack.pop());
            }
            else if(insn.getOpcode() == OPERATOR)
            {
                NSObject b = vars.pop();
                NSObject a = vars.pop();
                OperatorInsn operator = (OperatorInsn) insn;
                NSType type = NSTypes.getPriorityType(a, a.type(), b.type());
                NSObject newVar = type.operation(a, b, operator.operator());
                vars.push(newVar);
            }
            else if(insn.getOpcode() == FUNCTION_CALL)
            {
                FunctionCallInsn callInsn = (FunctionCallInsn) insn;
                if(functions.containsKey(callInsn.functionName()))
                    functions.get(callInsn.functionName()).run(vars);
                else
                {
                    throwRuntimeException("Function " + callInsn.functionName() + " doesn't exist.");
                }
            }
        }
    }

    private int gotoLabel(List<NSInsn> insns, int startIndex, Label label)
    {
        for(int i = startIndex; i < insns.size(); i++ )
        {
            NSInsn insn = insns.get(i);
            if(insn.getOpcode() == LABEL)
            {
                LabelInsn labelInsn = (LabelInsn) insn;
                if(labelInsn.label().equals(label))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    private void throwRuntimeException(String string)
    {
        throw new RuntimeException(string + " (at line " + lineNumber + ")");
    }

}
