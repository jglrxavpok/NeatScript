package org.jglr.ns.types;

import org.jglr.ns.*;
import org.jglr.ns.nativeclasses.*;

public class NSStringType extends NSClassType {

    public NSStringType() {
        super("String");
    }

    @Override
    public void initType() {
        typeClass(new NSStringClass());
        super.initType();
    }

    @Override
    public NSObject operation(NSObject a, NSObject b, NSOperator operator) {
        if (operator == NSOperator.PLUS) {
            return new NSObject(this, (String) a.value() + b.value());
        } else if (operator == NSOperator.MINUS) {
            return new NSObject(this, ((String) a.value()).replace(String.valueOf(b.value()), ""));
        } else if (operator == NSOperator.EQUALITY_CHECK) {
            return new NSObject(NSTypes.BOOL_TYPE, a.value().equals(b.value()));
        } else if (operator == NSOperator.NON_EQUALITY_CHECK) {
            return new NSObject(NSTypes.BOOL_TYPE, !a.value().equals(b.value()));
        }
        return super.operation(a, b, operator);
    }

    @Override
    public NSObject emptyObject() {
        return new NSObject(this, "");
    }

}
