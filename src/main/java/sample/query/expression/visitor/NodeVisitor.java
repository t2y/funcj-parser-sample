package sample.query.expression.visitor;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.google.common.math.DoubleMath;

import sample.query.expression.model.Model;
import sample.query.expression.model.Model.BinaryOpExpr;
import sample.query.expression.model.Model.BracketExpr;
import sample.query.expression.model.Model.DoubleExpr;
import sample.query.expression.model.Model.TextExpr;
import sample.query.expression.model.Model.UnaryOpExpr;
import lombok.Value;
import lombok.val;

public class NodeVisitor implements Visitor<Node> {

    public enum NodeType {
        LONG, DOUBLE, TEXT,

        OPEN_BRACKET, CLOSE_BRACKET,

        UNARY_OP, BINARY_OP, RELATIONAL_OP, LOGICAL_OP;
    }

    private int depth = 0;

    @Value
    public class LongNode implements Node {
        private final NodeType type;
        private final Long value;
        private final int depth;
    }

    @Value
    public class DoubleNode implements Node {
        private final NodeType type;
        private final Double value;
        private final int depth;
    }

    @Value
    public class StringNode implements Node {
        private final NodeType type;
        private final String value;
        private final int depth;
    }

    @Override
    public void visitDoubleExpr(DoubleExpr expr, List<Node> nodes) {
        final Node numNode;
        val value = Double.valueOf(expr.getValue());
        if (DoubleMath.isMathematicalInteger(value)) {
            numNode = new LongNode(NodeType.LONG, value.longValue(), this.depth);
        } else {
            numNode = new DoubleNode(NodeType.DOUBLE, value, this.depth);
        }
        nodes.add(numNode);
    }

    @Override
    public void visitTextExpr(TextExpr expr, List<Node> nodes) {
        nodes.add(new StringNode(NodeType.TEXT, expr.getText(), this.depth));
    }

    @Override
    public void visitBracketExpr(BracketExpr expr, List<Node> nodes) {
        this.depth++;
        val nested = visit(expr.getExpr());
        nodes.add(new StringNode(NodeType.OPEN_BRACKET, "(", this.depth));
        nodes.addAll(nested.stream().collect(toList()));
        nodes.add(new StringNode(NodeType.CLOSE_BRACKET, ")", this.depth));
        this.depth--;
    }

    @Override
    public void visitUnaryOpExpr(UnaryOpExpr expr, List<Node> nodes) {
        val nested = visit(expr.getExpr());

        final Node opNode;
        val op = expr.getOp();
        val opCode = op.getCode();
        if (Model.LogicalUnaryOp.contains(op)) {
            opNode = new StringNode(NodeType.LOGICAL_OP, opCode, this.depth);
        } else {
            opNode = new StringNode(NodeType.UNARY_OP, opCode, this.depth);
        }

        nodes.add(opNode);
        nodes.addAll(nested);
    }

    @Override
    public void visitBinaryOpExpr(BinaryOpExpr expr, List<Node> nodes) {
        val lhs = visit(expr.getLhs());
        val rhs = visit(expr.getRhs());

        final Node opNode;
        val op = expr.getOp();
        val opCode = op.getCode();
        if (Model.LogicalBinaryOp.contains(op)) {
            opNode = new StringNode(NodeType.LOGICAL_OP, opCode, this.depth);
        } else if (Model.ComparableOp.contains(op)) {
            opNode = new StringNode(NodeType.RELATIONAL_OP, opCode, this.depth);
        } else {
            opNode = new StringNode(NodeType.BINARY_OP, opCode, this.depth);
        }

        nodes.addAll(lhs.stream().collect(toList()));
        nodes.add(opNode);
        nodes.addAll(rhs.stream().collect(toList()));
    }
}