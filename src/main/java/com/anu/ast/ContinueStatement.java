package com.anu.ast;

public class ContinueStatement extends Statement {

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitContinueStatement(this);
    }
}