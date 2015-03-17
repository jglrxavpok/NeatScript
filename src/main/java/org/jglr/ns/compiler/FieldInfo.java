package org.jglr.ns.compiler;

public class FieldInfo
{

    private String owner;
    private String name;

    public FieldInfo(String name, String owner)
    {
        name(name);
        owner(owner);
    }

    public FieldInfo owner(String owner)
    {
        this.owner = owner;
        return this;
    }

    public FieldInfo name(String name)
    {
        this.name = name;
        return this;
    }

    public String name()
    {
        return name;
    }

    public String owner()
    {
        return owner;
    }
}
