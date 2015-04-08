Priority
=================

1. Null
------------
```null``` should be a constant, like ```true``` and ```false```

Though, ``null`` must be able to be used as an object and functions should be able to be linked to this object, example:
 ``null.toString()``

3. For loops
------------

For loops must be implemented asap too. For reminder they are specified as such [(cf. syntax file)](syntax.md):
```
for {Type} {name} = {value}; {condition}; {step} then
    {statements}
end
```

Far future
==================

1. Pointers-like objects, "References"
------------------

Allow to modify a variable directly.

Possible syntax, heavily inspired from C:
```
{Type} {variableName};
// Whatever
{Type}* {pointerName} = &{variableName};
```

2. Inline string formatting
------------------
Allow for such things as:
```
String other = "Test";
String text = "o's value is #{o}"
print(text); // Should output "o's value is Test"
```
