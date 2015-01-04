package org.jglr.ns.funcs;

import java.util.*;

import org.jglr.ns.*;

public abstract class NSFunc
{

	private String name;

	public NSFunc(String name)
	{
		this.name = name;
	}

	public String name()
	{
		return name;
	}

	public abstract void run(Stack<NSObject> vars);
}
