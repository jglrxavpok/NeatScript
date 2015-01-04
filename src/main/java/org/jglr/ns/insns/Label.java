package org.jglr.ns.insns;

public class Label
{
    private String id;

    public Label(String id)
    {
        this.id = id;
    }

    public String id()
    {
        return id;
    }

    public boolean equals(Object o)
    {
        if(o instanceof Label)
        {
            Label label = (Label) o;
            return label.id.equals(id);
        }
        return false;
    }
}
