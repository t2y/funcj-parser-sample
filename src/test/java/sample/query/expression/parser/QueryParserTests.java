package sample.query.expression.parser;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import lombok.val;

@RunWith(Parameterized.class)
public class QueryParserTests {

    enum Type {
        PARSE, EXCEPTION
    };

    @Parameters(name = "test-{index}: {0} query [{1}] => expected [{2}]")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                /*
                 normal parseing tests
                 */
                // misc
                { Type.PARSE, "abc", "abc" },
                { Type.PARSE, "key1 AND val1", "key1ANDval1" },
                { Type.PARSE, "NOT (male)", "NOT male" },

                // double
                { Type.PARSE, "3 = 3", "3.0 = 3.0" },
                { Type.PARSE, "5.5 = 5.5", "5.5 = 5.5" },

                // text data
                { Type.PARSE, "total-pv = 3", "total-pv = 3.0" },
                { Type.PARSE, "total-pv = test-data", "total-pv = test-data" },
                { Type.PARSE, "total_-pv = __-__", "total_-pv = __-__" },
                { Type.PARSE, "pv1 = pv123", "pv1 = pv123" },
                { Type.PARSE, "pv3,# = pv5$", "pv3,# = pv5$" },

                // not equal to
                { Type.PARSE, "total_pv != 1.8", "total_pv != 1.8" },

                // signed expression
                { Type.PARSE, "total_pv = 3", "total_pv = 3.0" },
                { Type.PARSE, "total_pv = -3", "total_pv = -3.0" },
                { Type.PARSE, "total_pv = +5.4", "total_pv = 5.4" },

                // greater operators
                { Type.PARSE, "total_pv > 2.3", "total_pv > 2.3" },
                { Type.PARSE, "total_pv >= 2.3", "total_pv >= 2.3" },

                // less operators
                { Type.PARSE, "total_pv < 3.6", "total_pv < 3.6" },
                { Type.PARSE, "total_pv <= 3.6", "total_pv <= 3.6" },

                // logical operators
                { Type.PARSE, "total_pv != +33 AND smartphone_pv < 55", "(total_pv != 33.0 AND smartphone_pv < 55.0)" },
                { Type.PARSE, "total_pv != +33 OR smartphone_pv < 55", "(total_pv != 33.0 OR smartphone_pv < 55.0)" },
                {
                        Type.PARSE, "NOT (total_pv != +33 OR smartphone_pv < 55)",
                        "NOT (total_pv != 33.0 OR smartphone_pv < 55.0)" },
                {
                        Type.PARSE, "total_pv != +33 AND NOT (smartphone_pv < 55 OR twenties_feature >= -3.3)",
                        "(total_pv != 33.0 AND NOT(smartphone_pv < 55.0 OR twenties_feature >= -3.3))"
                },

                // bracket expression
                {
                        Type.PARSE,
                        "total_pv != +33 AND (smartphone_pv < 55 OR twenties_feature >= -3.3)",
                        "(total_pv != 33.0 AND (smartphone_pv < 55.0 OR twenties_feature >= -3.3))"
                },
                {
                        Type.PARSE,
                        "(total_pv != +33 AND (smartphone_pv < 55 OR twenties_feature >= -3.3))",
                        "(total_pv != 33.0 AND (smartphone_pv < 55.0 OR twenties_feature >= -3.3))"
                },
                {
                        Type.PARSE,
                        "(total_pv != +33 OR ((smartphone_pv < 55 AND twenties_feature >= -3.3)))",
                        "(total_pv != 33.0 OR (smartphone_pv < 55.0 AND twenties_feature >= -3.3))"
                },
                {
                        Type.PARSE,
                        "total_pv != +33 AND (smartphone_pv < 55 OR "
                                + "(twenties_feature >= -3.3 AND kyusyu_feature <= 1.1)) OR shiga_ub = 10",
                        "((total_pv != 33.0 AND (smartphone_pv < 55.0 OR "
                                + "(twenties_feature >= -3.3 AND kyusyu_feature <= 1.1))) OR shiga_ub = 10.0)"
                },

                // complex expression
                {
                        Type.PARSE,
                        "total_pv != +33 AND (smartphone_pv < 55 OR "
                                + "(twenties_feature >= -3.3 AND kyusyu_feature <= 1.1)) "
                                + "OR "
                                + "(NOT (shiga_ub = 10 AND sizuoka_feature > 3.5) OR kyoto_ub != 888)",

                        "((total_pv != 33.0 AND (smartphone_pv < 55.0 OR "
                                + "(twenties_feature >= -3.3 AND kyusyu_feature <= 1.1))) "
                                + "OR "
                                + "(NOT(shiga_ub = 10.0 AND sizuoka_feature > 3.5) OR kyoto_ub != 888.0))"
                },

                /*
                 parsing error tests
                 */
                { Type.EXCEPTION, "",
                        "Failure at position 0, expected=0 \" # $ ' ( + , - N ? _ <nonZeroDigit> <letterOrDigit>" },
                { Type.EXCEPTION, "key1 == val1",
                        "Failure at position 5, expected=0 \" # $ ' ( + , - N ? _ <nonZeroDigit> <letterOrDigit>" },
                { Type.EXCEPTION, "NOT total_pv != +33 OR smartphone_pv < 55", "Failure at position 3, expected=(" },
                { Type.EXCEPTION, "key1 > +val1", "Failure at position 6, expected=0 <nonZeroDigit>" },
                { Type.EXCEPTION, "33key > 5", "Failure at position 2, expected=." },
                { Type.EXCEPTION, "key.33 = 5", "Failure at position 3, expected=<empty>" },
                { Type.EXCEPTION, "(total-pv != 33", "Failure at position 13, expected=)" },
                { Type.EXCEPTION, "((key1 > 1 AND key2 < 3 OR key3 <= 5)", "Failure at position 27, expected=)" },
                { Type.EXCEPTION, "key1 > 3.3.3", "Failure at position 8, expected=e E" },
        });
    }

    private Type type;
    private String query;
    private String expected;

    public QueryParserTests(Type type, String query, String expected) {
        this.type = type;
        this.query = query;
        this.expected = expected;
    }

    @Test
    public void testParse() {
        Assume.assumeTrue(this.type == Type.PARSE);
        val actual = QueryParser.parse(this.query).toString();
        assertEquals(this.expected, actual);
    }

    @Test
    public void testParseError() {
        Assume.assumeTrue(this.type == Type.EXCEPTION);
        try {
            val actual = QueryParser.parse(this.query).toString();
            assert false : "expects RuntimeException occurred, but no error: " + actual;
        } catch (RuntimeException e) {
            assertEquals(this.expected, e.getMessage());
        }
    }
}
