# Vex

Vex is a tree-walk interpreted programming language written in Java. It implements the complete language pipeline — lexical analysis, parsing, abstract syntax tree (AST) construction, static scope resolution, and runtime interpretation.

The language supports both procedural and object-oriented programming, with lexical scoping, first-class functions, closures, single inheritance, arrays, and structured control flow.

## Features

**Language Pipeline**
- Lexical analysis (tokenizer)
- Recursive descent parser
- Abstract Syntax Tree (AST) construction
- Static scope resolution (dedicated resolver pass)
- Tree-walk interpreter

**Core Language**
- Variables and assignments
- Arithmetic, comparison, and logical operators
- Arrays
- Control flow: `if` / `else`, `while`, `for`, `break`, `continue`
- Native functions
- Runtime error handling

**Functions & Scoping**
- First-class functions
- Closures
- Lexical scoping resolved at compile time (not just runtime environment walking)

**Object-Oriented Programming**
- Classes and objects
- Constructors (`init`)
- Methods
- Single inheritance
- `this` and `super` binding

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

Each stage is a distinct pass over the program:

1. **Lexer** — converts raw source text into a stream of tokens.
2. **Parser** — consumes tokens via recursive descent to build an Abstract Syntax Tree.
3. **Resolver** — walks the AST ahead of execution to statically resolve variable scope, correctly handling closures and shadowed bindings before any code runs.
4. **Interpreter** — walks the resolved AST to execute the program, evaluating expressions and executing statements directly.

## Example

```text
class Animal {
  init(name) {
    this.name = name;
  }

  speak() {
    print this.name + " makes a sound.";
  }
}

class Dog < Animal {
  speak() {
    super.speak();
    print this.name + " barks.";
  }
}

var d = Dog("Rex");
d.speak();
```

## Getting Started

```bash
# Clone the repository
git clone https://github.com/AnuSaha545/vex.git
cd vex

# Build
javac -d out src/**/*.java

# Run a Vex script
java -cp out vex.Vex examples/hello.vex
```

## Why a Resolver Pass?

Many tree-walk interpreters resolve variables purely at runtime by walking environment chains, which is simpler but can produce subtly incorrect results with closures and shadowed variables. Vex includes a **separate static resolution pass** that runs after parsing and before interpretation, so variable bindings — including those captured in closures — are resolved once, correctly, and efficiently at compile time.
