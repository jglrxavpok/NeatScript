package org.jglr.ns.types;

import org.jglr.ns.*;

public class NSClassType extends NSType {

    private NSClass clazz;

    public NSClassType(String name) {
        super(name, NSTypes.OBJECT_TYPE);
    }

    @Override
    public NSObject emptyObject() {
        return new NSObject(this, null);
    }

    @Override
    public boolean isCastable(NSType type) {
        return type == this;
    }

    @Override
    public Object cast(Object value, NSType type) {
        if (type == this)
            return value;
        return super.cast(value, type);
    }

    public NSType init(NSObject object) {
        for (NSField field : clazz.fields()) {
            object.field(field.name(), field.value());
        }
        return this;
    }

    public NSClassType typeClass(NSClass clazz) {
        this.clazz = clazz;
        return this;
    }

    public NSClass typeClass() {
        return clazz;
    }

    @Override
    public void initType() {
        supertype(NSTypes.fromID(clazz.superclass()));
    }

}
