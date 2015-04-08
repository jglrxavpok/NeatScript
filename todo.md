Priority
=================

1. Variable scope
------------

```
Int index;
until index == 5 then
    String text = "Some text";
    print(text);
end
```

Should **not** fail due to an error about variable 'text' already existing:
``Variable with name 'text' already exists (at line *, op: #* NEW_VAR * String text)``

This problem happens because of variables having no scope yet.

2. For loops
------------

For loops must be implemented asap too. For reminder they are specified as such (cf. syntax.md):
```
for {Type} {name} = {value}; {condition}; {step} then
    {statements}
end
```
