package sample.query.expression.model;

public interface Expr {

    public ExprType getType();

    public StringBuilder string(StringBuilder sb);
}
