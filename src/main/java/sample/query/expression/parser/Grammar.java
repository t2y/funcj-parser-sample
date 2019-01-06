package sample.query.expression.parser;

import static org.typemeta.funcj.parser.Text.*;

import java.util.Optional;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.Op2;
import org.typemeta.funcj.parser.Input;
import org.typemeta.funcj.parser.Parser;
import org.typemeta.funcj.parser.Ref;
import org.typemeta.funcj.parser.Result;

import sample.query.expression.model.Expr;
import sample.query.expression.model.Model;
import sample.query.expression.model.Model.BinaryOp;
import sample.query.expression.model.Model.UnaryOp;

public abstract class Grammar {

    private static final Parser<Chr, Expr> parser;

    private static <A> Parser<Chr, A> pure(A a) {
        return Parser.pure(a);
    }

    private static Functions.Op2<Expr> conditionalOp2(Chr c, Optional<BinaryOp> binOp1, Optional<BinaryOp> binOp2) {
        if (binOp1.isPresent()) {
            return binOp1.get().ctor();
        }
        if (binOp2.isPresent()) {
            return binOp2.get().ctor();
        }
        return null;
    }

    static {
        final Ref<Chr, Expr> expr = Parser.ref();

        final Parser<Chr, Chr> open = chr('(');
        final Parser<Chr, Chr> close = chr(')');
        final Parser<Chr, Chr> symbols = chr('"').or(chr('\''))
                .or(chr('#')).or(chr('$')).or(chr(','))
                .or(chr('-')).or(chr('?')).or(chr('_'));

        final Parser<Chr, BinaryOp> logicalAnd = string("AND").andR(pure(BinaryOp.AND));
        final Parser<Chr, BinaryOp> logicalOr = string("OR").andR(pure(BinaryOp.OR));
        final Parser<Chr, UnaryOp> logicalNot = string("NOT").andR(pure(UnaryOp.NOT));

        final Parser<Chr, Op2<Expr>> logicalOp = logicalAnd.or(logicalOr).map(BinaryOp::ctor);

        final Parser<Chr, BinaryOp> equalTo = chr('=').andR(pure(BinaryOp.EQUAL_TO));
        final Parser<Chr, BinaryOp> notEqualTo = string("!=").andR(pure(BinaryOp.NOT_EQUAL_TO));
        final Parser<Chr, Op2<Expr>> equalOp = notEqualTo.or(equalTo).map(BinaryOp::ctor);

        final Parser<Chr, Op2<Expr>> greaterOp = chr('>')
                .and((chr('=').andR(pure(BinaryOp.GREATER_THAN_OR_EQUAL_TO))).optional())
                .and(pure(BinaryOp.GREATER_THAN).optional())
                .map((c, b1, b2) -> conditionalOp2(c, b1, b2));

        final Parser<Chr, Op2<Expr>> lessOp = chr('<')
                .and((chr('=').andR(pure(BinaryOp.LESS_THAN_OR_EQUAL_TO))).optional())
                .and(pure(BinaryOp.LESS_THAN).optional())
                .map((c, b1, b2) -> conditionalOp2(c, b1, b2));

        final Parser<Chr, Op2<Expr>> relationalOp = equalOp.or(greaterOp).or(lessOp);

        final Parser<Chr, Expr> number = dble.map(Model::doubleExpr);
        final Parser<Chr, Expr> text = alphaNum.or(symbols)
                .many1()
                .map(Chr::listToString)
                .map(Model::textExpr);

        final Parser<Chr, Expr> bracketExpr = open.andR(expr).andL(close).map(Model::bracketExpr);
        final Parser<Chr, Expr> notExpr = logicalNot.and(bracketExpr).map(Model::unaryOpExpr);

        final Parser<Chr, Expr> term = number.or(bracketExpr).or(notExpr).or(text);
        final Parser<Chr, Expr> relationalExpr = term.chainl1(relationalOp);
        final Parser<Chr, Expr> logicalExpr = relationalExpr.chainl1(logicalOp);
        parser = expr.set(logicalExpr);
    }

    public static Result<Chr, Expr> parse(String s) {
        return parser.parse(Input.of(s));
    }
}
