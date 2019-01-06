package sample.query.expression.model;

import java.util.EnumSet;

import org.typemeta.funcj.functions.Functions;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Model {

    public static Expr doubleExpr(double value) {
        return new DoubleExpr(ExprType.DOUBLE_EXPR, value);
    }

    @Getter
    @AllArgsConstructor
    public static class DoubleExpr extends AbstractExpr {
        private final ExprType type;
        private final double value;

        @Override
        public StringBuilder string(StringBuilder sb) {
            return sb.append(value);
        }
    }

    public static Expr textExpr(String text) {
        return new TextExpr(ExprType.TEXT_EXPR, text);
    }

    @Getter
    @AllArgsConstructor
    public static class TextExpr extends AbstractExpr {
        private final ExprType type;
        private final String text;

        @Override
        public StringBuilder string(StringBuilder sb) {
            return sb.append(text);
        }
    }

    public static Expr bracketExpr(Expr expr) {
        return new BracketExpr(ExprType.BRACKET_EXPR, expr);
    }

    @Getter
    @AllArgsConstructor
    public static class BracketExpr extends AbstractExpr {
        private final ExprType type;
        private final Expr expr;

        @Override
        public StringBuilder string(StringBuilder sb) {
            return sb.append(expr);
        }
    }

    public static Expr unaryOpExpr(UnaryOp op, Expr expr) {
        return new UnaryOpExpr(ExprType.UNARY_OP_EXPR, op, expr);
    }

    @Getter
    @AllArgsConstructor
    public enum UnaryOp {
        NOT("NOT");

        private final String code;

        @Override
        public String toString() {
            return this.code;
        }
    }

    public static final EnumSet<UnaryOp> LogicalUnaryOp = EnumSet.of(UnaryOp.NOT);

    @Getter
    @AllArgsConstructor
    public static class UnaryOpExpr extends AbstractExpr {
        private final ExprType type;
        private final UnaryOp op;
        private final Expr expr;

        @Override
        public StringBuilder string(StringBuilder sb) {
            sb.append(op);
            sb.append(expr);
            return sb;
        }

        @Override
        public String toString() {
            return this.op.getCode() + " " + this.expr.toString();
        }
    }

    public static Expr binaryOpExpr(Expr lhs, BinaryOp op, Expr rhs) {
        return new BinaryOpExpr(ExprType.BINARY_OP_EXPR, lhs, op, rhs);
    }

    @Getter
    public enum BinaryOp {
        AND("AND"), OR("OR"),

        EQUAL_TO("="), NOT_EQUAL_TO("!="),

        GREATER_THAN(">"), GREATER_THAN_OR_EQUAL_TO(">="),

        LESS_THAN("<"), LESS_THAN_OR_EQUAL_TO("<=");

        private final String code;

        BinaryOp(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return " " + String.valueOf(this.code) + " ";
        }

        public Functions.Op2<Expr> ctor() {
            return (lhs, rhs) -> new BinaryOpExpr(ExprType.BINARY_OP_EXPR, lhs, this, rhs);
        }
    }

    public static final EnumSet<BinaryOp> LogicalBinaryOp = EnumSet.of(BinaryOp.AND, BinaryOp.OR);
    public static final EnumSet<BinaryOp> ComparableOp = EnumSet.of(
            BinaryOp.EQUAL_TO, BinaryOp.NOT_EQUAL_TO,
            BinaryOp.GREATER_THAN, BinaryOp.GREATER_THAN_OR_EQUAL_TO,
            BinaryOp.LESS_THAN, BinaryOp.LESS_THAN_OR_EQUAL_TO);

    @Getter
    @AllArgsConstructor
    public static class BinaryOpExpr extends AbstractExpr {
        private final ExprType type;
        private final Expr lhs;
        private final BinaryOp op;
        private final Expr rhs;

        @Override
        public StringBuilder string(StringBuilder sb) {
            if (LogicalBinaryOp.contains(op)) {
                sb.append('(');
            }
            lhs.string(sb);
            sb.append(op);
            rhs.string(sb);
            if (LogicalBinaryOp.contains(op)) {
                sb.append(')');
            }
            return sb;
        }
    }
}
