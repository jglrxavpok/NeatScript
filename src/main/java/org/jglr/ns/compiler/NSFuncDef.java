package org.jglr.ns.compiler;

import java.util.*;

import org.jglr.ns.*;
import org.jglr.ns.insns.*;

public class NSFuncDef extends NSAbstractMethod {

    public static final String ROOT_ID = "$";
    private List<NSInsn> insns;

    public NSFuncDef() {
        super();
        insns = new ArrayList<NSInsn>();
    }

    public List<NSInsn> instructions() {
        return insns;
    }

    @Override
    public final void run(Stack<NSObject> vars) {
        ; // A user defined function is not allowed to directly use the values stack
    }

}
