package org.jglr.ns.compiler;

import java.util.*;

import org.jglr.ns.types.*;

public class CompilerState
{

    private int                      labelID;
    private String                   labelBase;
    private int                      varId;
    private HashMap<String, NSType>  varName2Type;
    private HashMap<String, Integer> varName2Id;
    private NSFuncDef                currentMethodDef;

    @SuppressWarnings("unchecked")
    public CompilerState(HashMap<String, Integer> varName2Id, HashMap<String, NSType> varName2Type, int varId, String labelBase, int labelID, NSFuncDef currentMethodDef)
    {
        this.varName2Id = (HashMap<String, Integer>) varName2Id.clone();
        this.varName2Type = (HashMap<String, NSType>) varName2Type.clone();
        this.varId = varId;
        this.labelBase = labelBase;
        this.labelID = labelID;
        this.currentMethodDef = currentMethodDef;
    }

    public NSFuncDef currentMethodDef()
    {
        return currentMethodDef;
    }

    public HashMap<String, Integer> varNamesToIds()
    {
        return varName2Id;
    }

    public HashMap<String, NSType> varNamesToTypes()
    {
        return varName2Type;
    }

    public int varID()
    {
        return varId;
    }

    public int labelID()
    {
        return labelID;
    }

    public String labelBase()
    {
        return labelBase;
    }

}
