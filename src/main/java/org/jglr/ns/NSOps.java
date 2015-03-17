package org.jglr.ns;

import java.lang.reflect.*;
import java.util.*;

import org.jglr.ns.types.*;

public interface NSOps
{
    /**
     * Loads a given value on the stack
     */
    public static final int               LOAD_CONSTANT    = 0x0;

    /**
     * Contains informations about an operator
     */
    public static final int               OPERATOR         = 0x1;

    /**
     * Contains informations about the current line number
     */
    public static final int               LINE_NUMBER      = 0x2;

    /**
     * Calls a function
     */
    public static final int               FUNCTION_CALL    = 0x3;

    /**
     * Sets a new label
     */
    public static final int               LABEL            = 0x4;

    /**
     * Jump instruction to given label
     */
    public static final int               GOTO             = 0x5;

    /**
     * If the value on the values stack is not equal to {@link NSBoolType#TRUE}, jump to given label
     */
    public static final int               IF_NOT_GOTO      = 0x6;

    /**
     * If the value on the values stack is equal to {@link NSBoolType#TRUE}, jump to given label
     */
    public static final int               IF_GOTO          = 0x7;

    /**
     * Pushes the value on top of the values stack to the global stack 
     */
    public static final int               STACK_PUSH       = 0x8;

    /**
     * Peeks on the value on top of the global stack 
     */
    public static final int               STACK_PEEK       = 0x9;

    /**
     * Pops the value on top of the global stack and pushes it to the values stack
     */
    public static final int               STACK_POP        = 0xA;

    /**
     * Creates a new variable with given {@link NSType} and name. 
     */
    public static final int               NEW_VAR          = 0xB;

    /**
     * Loads a variable based on its index 
     */
    public static final int               VAR_LOAD         = 0xC;

    /**
     * Stores the value on top of the values stack in a variable referenced by its index
     */
    public static final int               VAR_STORE        = 0xD;

    /**
     * Loads a field based on its name
     */
    public static final int               GET_FIELD        = 0xE;

    /**
     * Pops the value on top of the values stack 
     */
    public static final int               POP              = 0xF;

    /**
     * Immediately break out of the current method
     */
    public static final int               RETURN           = 0x10;

    /**
     * Immediately break out of the current method and add a value to the values stack
     */
    public static final int               RETURN_VALUE     = 0x11;

    /**
     * Pushes an integer to the values stack
     */
    public static final int               ILOAD            = 0x12;

    /**
     * Pushes an float to the values stack
     */
    public static final int               FLOAD            = 0x13;

    /**
     * Loads a field onto the values stack
     */
    public static final int               FIELD_LOAD       = 0x14;

    /**
     * Saves into field from the values stack
     */
    public static final int               FIELD_SAVE       = 0x15;

    @Deprecated
    public static final int               GET_STATIC_FIELD = -1;

    static final HashMap<Integer, String> names            = new HashMap<>();

    public static void initAllNames()
    {
        if(names.isEmpty())
        {
            Field[] fields = NSOps.class.getFields();
            for(Field field : fields)
            {
                if(field.getType() == Integer.TYPE)
                {
                    String name = field.getName();
                    try
                    {
                        names.put(field.getInt(null), name);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String name(int opcode)
    {
        String name = "UNNAMED_" + opcode;
        if(names.containsKey(opcode))
            name = names.get(opcode);
        return name;
    }
}
