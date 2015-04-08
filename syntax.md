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

Variable naming
===================
NeatScript imposes some restrictions to variable naming:
* Variable names cannot contain a space in them.
* Variable names cannot contain an operator in them, go to section [Operators](#operators) to see the operators' list
* Variable names cannot contain only digits, those are called numbers.

There are also guidelines, you are not obliged to follow them _but please be consistent_:
* Variable names usually starts with a lowercase character.
* Variable names should follow camelCase case.
* Initialisms of any length may be camelCase but they are recommended to be kept in capitals letters (e.g., ``initIPAddressViaXML``)

Branching
==================
* If

```
if {condition} then
    {statements}
end
```

Execute ``{statements}`` only if ``{condition}`` is equal to ``true``

* Else

```
if {condition} then
    {statements}
else then
    {other statements}
end
```

* Elif, contraction of 'Else if'

```
if {condition} then
    {statements}
elif {other condition} then
    {other statements}
end
```

Execute ``{other statements}`` only if ``{condition}`` is equal to ``false`` AND that ``{other condition}`` is equal to true.

Can only be used with an ``if``!

Operators
==================
``a + b`` The plus operator, adds 'b' to 'a'

``a - b`` The minus operator, subtracts 'b' from 'a'

``a * b`` The multiplication operator, multiplies 'a' by 'b'

``a / b`` The division operator, divides 'a' by 'b'

``a % b`` The modulo operator, divides 'a' by 'b' and returns the remainder
 
``v++`` The increment operator, increments **variable** 'v' by 1
 
``v--`` The decrement operator, decrements **variable** 'v' by 1

``a << n`` The left shift operator, left shift 'a' bits by the amount 'n'

``a >> n`` The signed right shift operator, right shift 'a' bits by the amount 'n'

``a >>> n`` The unsigned right shift operator, right shift 'a' bits by the amount 'n'. The result is always positive.

``a & b`` Both the bitwise operator and the logical operator 'AND'

``a | b`` Both the bitwise operator and the logical operator 'OR' (inclusive)

``a ^ b`` Both the bitwise operator and the logical operator 'OR' (exclusive)

``a < b`` Checks that 'a' is smaller than 'b'

``a > b`` Checks that 'a' is greater than 'b'

``a <= b`` Checks that 'a' is smaller or equal to 'b'

``a >= b`` Checks that 'a' is greater or equal to 'b'

``v = a`` Give **variable** 'v' the value 'a'

``a == b`` Checks that 'a' is equal to 'b'

``a != b`` Checks that 'a' is not equal to 'b'

``o.{field or function call}`` Calls a function on object 'o' or get a field of object 'o'

Loops
==================
* While loop:

```
while {condition} then
    {statements}
end
```

While ``{condition}`` is ``true``, ``{statements}`` is executed.

``{condition}`` is evaluated each time we iterate over the loop.

* Until loop:

```
until {condition} then
    {statements}
end
```

While ``{condition}`` is ``false``, ``{statements}`` is executed.

``{condition}`` is evaluated each time we iterate over the loop.

* For loop (NYI):

```
for {Type} {name} = {value}; {condition}; {step} then
    {statements}
end
```
