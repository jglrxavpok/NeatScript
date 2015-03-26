package org.jglr.ns;

import java.util.*;

public class HashMapWithDefault<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 5995791692010816132L;
    private V defaultValue;

    public void setDefault(V value) {
        defaultValue = value;
    }

    public V getDefault() {
        return defaultValue;
    }

    public V get(Object key) {
        if (this.containsKey(key))
            return super.get(key);
        else
            return defaultValue;
    }

    public Object clone() {
        HashMapWithDefault<K, V> clone = new HashMapWithDefault<>();
        clone.setDefault(defaultValue);
        clone.putAll(this);
        return clone;
    }
}
