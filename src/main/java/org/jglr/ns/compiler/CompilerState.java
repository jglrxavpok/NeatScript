package org.jglr.ns.compiler;

import org.jglr.ns.*;
import org.jglr.ns.types.*;

public class CompilerState
{

    private int                                 labelID;
    private String                              labelBase;
    private int                                 varId;
    private HashMapWithDefault<String, NSType>  varName2Type;
    private HashMapWithDefault<String, Integer> varName2Id;
    private NSFuncDef                           currentMethodDef;
    private HashMapWithDefault<Integer, String> varId2Name;

    @SuppressWarnings("unchecked")
    public CompilerState(HashMapWithDefault<String, Integer> varName2Id, HashMapWithDefault<Integer, String> varId2Name, HashMapWithDefault<String, NSType> varName2Type, int varId, String labelBase, int labelID, NSFuncDef currentMethodDef)
    {
        this.varName2Id = (HashMapWithDefault<String, Integer>) varName2Id.clone();
        this.varName2Type = (HashMapWithDefault<String, NSType>) varName2Type.clone();
        this.varId2Name = (HashMapWithDefault<Integer, String>) varId2Name.clone();
        this.varId = varId;
        this.labelBase = labelBase;
        this.labelID = labelID;
        this.currentMethodDef = currentMethodDef;
    }

    public NSFuncDef currentMethodDef()
    {
        return currentMethodDef;
    }

    public HashMapWithDefault<String, Integer> varNamesToIds()
    {
        return varName2Id;
    }

    public HashMapWithDefault<Integer, String> varIdsToNames()
    {
        return varId2Name;
    }

    public HashMapWithDefault<String, NSType> varNamesToTypes()
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
