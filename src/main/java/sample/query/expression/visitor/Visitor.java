package sample.query.expression.visitor;

import java.util.ArrayList;
import java.util.List;

import sample.query.expression.model.Expr;
import sample.query.expression.model.Model.BinaryOpExpr;
import sample.query.expression.model.Model.BracketExpr;
import sample.query.expression.model.Model.DoubleExpr;
import sample.query.expression.model.Model.TextExpr;
import sample.query.expression.model.Model.UnaryOpExpr;
import lombok.val;

public interface Visitor<T> {

    public void visitDoubleExpr(DoubleExpr expr, List<T> nodes);

    public void visitTextExpr(TextExpr expr, List<T> nodes);

    public void visitBracketExpr(BracketExpr expr, List<T> nodes);

    public void visitUnaryOpExpr(UnaryOpExpr expr, List<T> nodes);

    public void visitBinaryOpExpr(BinaryOpExpr expr, List<T> nodes);

    public default List<T> visit(Expr expr) {
        val nodes = new ArrayList<T>();
        switch (expr.getType()) {
        case DOUBLE_EXPR:
            visitDoubleExpr((DoubleExpr) expr, nodes);
            break;
        case TEXT_EXPR:
            visitTextExpr((TextExpr) expr, nodes);
            break;
        case BRACKET_EXPR:
            visitBracketExpr((BracketExpr) expr, nodes);
            break;
        case UNARY_OP_EXPR:
            visitUnaryOpExpr((UnaryOpExpr) expr, nodes);
            break;
        case BINARY_OP_EXPR:
            visitBinaryOpExpr((BinaryOpExpr) expr, nodes);
            break;
        default:
            throw new RuntimeException("unknown: " + expr.getType().toString());
        }
        return nodes;
    }
}
