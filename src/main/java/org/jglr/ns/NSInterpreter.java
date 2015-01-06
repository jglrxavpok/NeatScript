package org.jglr.ns;

import java.util.*;

import org.jglr.ns.compiler.*;
import org.jglr.ns.insns.*;
import org.jglr.ns.types.*;
import org.jglr.ns.vm.*;

public class NSInterpreter implements NSOps, NSTypes
{

    private int              lineNumber;
    private NSVirtualMachine vm;

    public NSInterpreter(NSVirtualMachine vm)
    {
        this.vm = vm;
        NSOps.initAllNames();
    }

    public void interpret(List<NSInsn> insns, NSVariable... startVariables) throws NSNoSuchMethodException, NSClassNotFoundException, NSVirtualMachineException
    {
        int index = 0;
        Stack<NSObject> heapStack = new Stack<>();
        HashMap<Integer, NSVariable> variables = new HashMap<>();
        Stack<NSObject> valuesStack = new Stack<>();
        for(int i = 0; i < startVariables.length; i++ )
        {
            variables.put(i, startVariables[i]);
        }
        lineNumber = 0;
        //        for(NSInsn insn : insns)
        //        {
        //            System.out.println("$ " + insn.toString());
        //        }
        for(; index < insns.size(); index++ )
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
                valuesStack.push(new NSObject(type, cst));
            }
            else if(insn.getOpcode() == IF_NOT_GOTO || insn.getOpcode() == IF_GOTO)
            {
                LabelInsn jumpInsn = (LabelInsn) insn;
                NSObject value = valuesStack.pop();
                NSObject result = NSTypes.BOOL_TYPE.operation(value, NSTypes.BOOL_TYPE.TRUE, NSOperator.EQUALITY_CHECK);
                if(result == (insn.getOpcode() == IF_GOTO ? NSTypes.BOOL_TYPE.TRUE : NSTypes.BOOL_TYPE.FALSE))
                {
                    index = gotoLabel(insns, index, jumpInsn.label()) - 1;
                    System.out.println("[DEBUG] Jumping to label " + jumpInsn.label().id());
                }
            }
            else if(insn.getOpcode() == STACK_PUSH)
            {
                heapStack.push(valuesStack.pop());
            }
            else if(insn.getOpcode() == STACK_PEEK)
            {
                valuesStack.push(heapStack.peek());
            }
            else if(insn.getOpcode() == STACK_POP)
            {
                valuesStack.push(heapStack.pop());
            }
            else if(insn.getOpcode() == POP)
            {
                valuesStack.pop();
            }
            else if(insn.getOpcode() == NEW_VAR)
            {
                NewVarInsn varInsn = (NewVarInsn) insn;
                NSVariable variable = new NSVariable(varInsn.type(), varInsn.name(), varInsn.varIndex());
                if(!variables.containsKey(variable.varIndex()))
                {
                    variables.put(variable.varIndex(), variable);
                }
                else
                    throwRuntimeException("Variable name " + variable.name() + " already exists", lineNumber, index, insn);
            }
            else if(insn.getOpcode() == VAR_LOAD)
            {
                NSVarInsn varInsn = (NSVarInsn) insn;
                NSVariable var = variables.get(varInsn.varIndex());
                if(var == null)
                {
                    throwRuntimeException("Tried to load invalid variable index: " + varInsn.varIndex(), lineNumber, index, insn);
                }
                else
                {
                    valuesStack.push(var.value());
                }
            }
            else if(insn.getOpcode() == VAR_STORE)
            {
                NSVarInsn varInsn = (NSVarInsn) insn;
                NSVariable var = variables.get(varInsn.varIndex());
                if(var == null)
                {
                    throwRuntimeException("Tried to store to invalid variable index: " + varInsn.varIndex(), lineNumber, index, insn);
                }
                else
                {
                    var.value(valuesStack.pop());
                }
            }
            else if(insn.getOpcode() == OPERATOR)
            {
                NSObject b = valuesStack.pop();
                NSObject a = valuesStack.pop();
                OperatorInsn operator = (OperatorInsn) insn;
                NSType type = NSTypes.getPriorityType(a, a.type(), b.type());
                NSObject newVar = type.operation(a, b, operator.operator());
                valuesStack.push(newVar);
            }
            else if(insn.getOpcode() == GET_FIELD)
            {
                NSObject var = valuesStack.pop();
                NSObject field = var.field(((LoadFieldInsn) insn).fieldName());
                if(field == null)
                {
                    throwRuntimeException("Unknown field: " + ((LoadFieldInsn) insn).fieldName() + " in type " + var.type().getID(), lineNumber, index, insn);
                }
                else
                    valuesStack.push(field);
            }
            else if(insn.getOpcode() == FUNCTION_CALL)
            {
                FunctionCallInsn callInsn = (FunctionCallInsn) insn;
                String owner = callInsn.functionOwner();
                if(callInsn.functionOwner().equals("std"))
                {
                    vm.callStdFunction(callInsn.functionName(), valuesStack);
                    continue;
                }
                else if(callInsn.functionOwner().equals(FunctionCallInsn.PREVIOUS))
                {
                    NSType type = valuesStack.peek().type();
                    owner = type.getID();
                }
                NSAbstractMethod method = vm.getOrLoad(owner).method(callInsn.functionName(), callInsn.types());
                method.owner(owner);
                vm.methodCall(method, valuesStack);
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

    private void throwRuntimeException(String string, int lineNumber, int index, NSInsn insn)
    {
        throw new RuntimeException(string + " (at line " + lineNumber + ", op: #" + index + " " + insn + ")");
    }

    public int lineNumber()
    {
        return lineNumber;
    }

}
