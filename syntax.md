Shortcuts
==================
* NYI: Not yet implemented


Variable definition
===================
* Uninitialized variable creation:
``{Type} {name};``

_Equivalent to creating a variable with a value equal to 'null'_

* Initialized variable creation:
``{Type} {name} = {value};``


Branching
==================
* If
``if {condition} then
    {statements}
end``
Execute ``{statements}`` only if ``{condition}`` is equal to ``true``

* Else
``if {condition} then
    {statements}
else then
    {other statements}
end``
Execute ``{other statements}`` only if ``{condition}`` is equal to ``false``.
Can only be used with an ``if``

Loops
==================
* While loop:
``while {condition} then
    {statements}
end``

While ``{condition}`` is ``true``, ``{statements}`` is executed.
``{condition}`` is evaluated each time we iterate over the loop.

* Until loop (NYI):
``until {condition} then
    {statements}
end``

While ``{condition}`` is ``false``, ``{statements}`` is executed.
``{condition}`` is evaluated each time we iterate over the loop.

* For loop (NYI):

``for {Type} {name} = {value}; {condition}; {step} then
    {statements}
end``
