package com.anu.ast;

public class BreakStatement extends Statement {

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitBreakStatement(this);
    }
}