package com.anu.ast;

import com.anu.token.Token;
import java.util.List;

public class FunctionStatement extends Statement {

    private final Token name;
    private final List<Token> parameters;
    private final List<Statement> body;

    public FunctionStatement(Token name,
                             List<Token> parameters,
                             List<Statement> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public Token getName() {
        return name;
    }

    public List<Token> getParameters() {
        return parameters;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visitFunctionStatement(this);
    }
}