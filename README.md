# Vex Programming Language

Vex is a tree-walk interpreted programming language written in Java. It implements the complete language pipeline, including lexical analysis, parsing, abstract syntax tree (AST) construction, static scope resolution, and runtime interpretation.
The language supports procedural and object-oriented programming with lexical scoping, first-class functions, closures, inheritance, arrays, and structured control flow.

## Features

- Lexical analysis
- Recursive descent parser
- Abstract Syntax Tree (AST)
- Static scope resolution
- Tree-walk interpreter
- Variables and assignments
- Arithmetic, comparison, and logical operators
- Lexical scoping
- Functions and closures
- Arrays
- Classes and objects
- Constructors (`init`)
- Methods
- Single inheritance
- `this` and `super`
- `if` / `else`
- `while`
- `for`
- `break`
- `continue`
- Native functions
- Runtime error handling

## Architecture

```text
Source Code
     │
     ▼
   Lexer
     │
     ▼
   Parser
     │
     ▼
Abstract Syntax Tree
     │
     ▼
  Resolver
     │
     ▼
 Interpreter
     │
     ▼
Program Output
```
