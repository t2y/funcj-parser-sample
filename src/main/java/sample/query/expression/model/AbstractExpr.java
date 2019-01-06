package sample.query.expression.model;

public abstract class AbstractExpr implements Expr {

    public String toString() {
        return string(new StringBuilder()).toString();
    }
}
