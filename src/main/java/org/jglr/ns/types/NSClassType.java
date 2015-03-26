package org.jglr.ns.types;

import org.jglr.ns.*;
import org.jglr.ns.vm.*;

public class NSClassType extends NSType {

    private NSClass clazz;

    public NSClassType(NSClass clazz) throws NSClassNotFoundException {
        this(clazz, false);
    }

    public NSClassType(NSClass clazz, boolean dummy) throws NSClassNotFoundException {
        super(clazz.name(), dummy ? NSTypes.OBJECT_TYPE : NSVirtualMachine.instance().getType(clazz.superclass()));
        this.clazz = clazz;
    }

    @Override
    public NSObject emptyObject() {
        return new NSObject(this, null);
    }

    public NSType init(NSObject object) {
        for (NSField field : clazz.fields()) {
            object.field(field.name(), field.value());
        }
        return this;
    }

}
