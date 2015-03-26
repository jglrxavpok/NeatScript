package org.jglr.ns;

import java.util.*;

import org.jglr.ns.types.*;

public class NSNoSuchMethodException extends Exception {

    public NSNoSuchMethodException(String className, String methodName, List<NSType> types) {
        super(createMessage(className, methodName, types));
    }

    public NSNoSuchMethodException(String className, String methodName, List<NSType> types, Throwable t) {
        super(createMessage(className, methodName, types), t);
    }

    private static String createMessage(String className, String methodName, List<NSType> types) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Method not found: ");
        buffer.append(className);
        buffer.append("::");
        buffer.append(methodName);
        buffer.append("(");
        int index = 0;
        for (NSType type : types) {
            if (index++ != 0)
                buffer.append(",");
            buffer.append(type.getID());
        }
        buffer.append(")");
        return buffer.toString();
    }

    private static final long serialVersionUID = 2787259347953377237L;

}
