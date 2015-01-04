package org.jglr.ns;

import java.lang.reflect.*;
import java.util.*;

public interface NSOps
{
    public static final int               LOAD_CONSTANT = 0;
    public static final int               OPERATOR      = 1;
    public static final int               LINE_NUMBER   = 2;
    public static final int               FUNCTION_CALL = 3;
    public static final int               LABEL         = 4;
    public static final int               GOTO          = 5;
    public static final int               IF_NOT_GOTO   = 6;
    public static final int               IF_GOTO       = 7;
    public static final int               STACK_PUSH    = 8;
    public static final int               STACK_PEEK    = 9;
    public static final int               STACK_POP     = 10;

    static final HashMap<Integer, String> names         = new HashMap<>();

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
