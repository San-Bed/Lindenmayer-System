# Lindenmayer-System

This program draws random geometric structures with a [LSystem](https://en.wikipedia.org/wiki/L-system). The system is initialized with a JSON file specifying the keys. An example is:

```
{
    "alphabet": ["F", "[", "]", "+", "-"],
    "rules": {"F" : [“F-F","FF+”,"F[+F]F[-F]F"]},
    "axiom": "F",
    "actions": {"F":"draw", "[":"push","]":"pop", "+":"turnR", "-":"turnL"},
    "parameters" : {"step": 2, "angle":22.5, "start":[0,0,90]}
}
```

We generate sequences by applying rules on the axiom for a specified number of times. If a symbol has multiples rules, one is chosen randomly. This allows to create multiple unique structures. For instance,

```
F → F-F
F-F → FF+-F-F
FF+-F-F → F[+F]F[-F]FF-F+-F-F-FF+
```
The resulting drawing is then translated in PostScript and it produces a EPS file.  

# How to Run it

```
% java -cp build/classes:lib/json-java.jar lindenmayer.Plotter sierpinski.json n
% epstopdf test/buisson5.eps 
```
where ** n ** is the number of rounds of rewriting applied to the initial sequence (i.e. the axiom).

# What I Learned 

* How to use the package org.json and its classes (JSONObject and JSONArray)
* How to parse a JSON file to initialize a program
* PostScript language and eps files
