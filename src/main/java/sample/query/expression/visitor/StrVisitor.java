package sample.query.expression.visitor;

import static java.util.stream.Collectors.*;

import java.util.List;

import sample.query.expression.model.Model.BinaryOpExpr;
import sample.query.expression.model.Model.BracketExpr;
import sample.query.expression.model.Model.DoubleExpr;
import sample.query.expression.model.Model.TextExpr;
import sample.query.expression.model.Model.UnaryOpExpr;
import lombok.val;

public class StrVisitor implements Visitor<String> {

    @Override
    public void visitDoubleExpr(DoubleExpr expr, List<String> nodes) {
        nodes.add("double:" + expr.toString());
    }

    @Override
    public void visitTextExpr(TextExpr expr, List<String> nodes) {
        nodes.add("text:" + expr.toString());
    }

    @Override
    public void visitBracketExpr(BracketExpr expr, List<String> nodes) {
        val nested = visit(expr.getExpr());
        nodes.add("(");
        nodes.addAll(nested.stream().collect(toList()));
        nodes.add(")");
    }

    @Override
    public void visitUnaryOpExpr(UnaryOpExpr expr, List<String> nodes) {
        val nested = visit(expr.getExpr());
        nodes.add(expr.getOp().getCode());
        nodes.addAll(nested.stream().collect(toList()));
    }

    @Override
    public void visitBinaryOpExpr(BinaryOpExpr expr, List<String> nodes) {
        val lhs = visit(expr.getLhs());
        val rhs = visit(expr.getRhs());
        nodes.addAll(lhs.stream().collect(toList()));
        nodes.add(expr.getOp().getCode());
        nodes.addAll(rhs.stream().collect(toList()));
    }
}
