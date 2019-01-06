package sample.query.expression.visitor;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import sample.query.expression.parser.QueryParser;
import lombok.val;

@RunWith(Parameterized.class)
public class StrVisitorTests {

    @Parameters(name = "{index}: query [{0}] => expected [{1}]")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // equal to
                {
                        "total_pv = 3",
                        Arrays.asList(new String[] { "text:total_pv", "=", "double:3.0" })
                },

                {
                        "total_pv = -3",
                        Arrays.asList(new String[] { "text:total_pv", "=", "double:-3.0" })
                },

                {
                        "total_pv = +5.4",
                        Arrays.asList(new String[] { "text:total_pv", "=", "double:5.4" })
                },

                {
                        "total_pv = test",
                        Arrays.asList(new String[] { "text:total_pv", "=", "text:test" })
                },

                // not equal to
                {
                        "total_pv != 1.8",
                        Arrays.asList(new String[] { "text:total_pv", "!=", "double:1.8" })
                },

                // greater operators
                {
                        "total_pv > 2.3",
                        Arrays.asList(new String[] { "text:total_pv", ">", "double:2.3" })

                },

                {
                        "total_pv >= 2.3",
                        Arrays.asList(new String[] { "text:total_pv", ">=", "double:2.3" })
                },

                // less operators
                {
                        "total_pv < 3.6",
                        Arrays.asList(new String[] { "text:total_pv", "<", "double:3.6" })
                },

                {
                        "total_pv <= 3.6",
                        Arrays.asList(new String[] { "text:total_pv", "<=", "double:3.6" })
                },

                // logical operators
                {
                        "total_pv != +33 AND smartphone_pv < 55",
                        Arrays.asList(new String[] {
                                "text:total_pv", "!=", "double:33.0",
                                "AND",
                                "text:smartphone_pv", "<", "double:55.0"
                        })
                },

                {
                        "total_pv != +33 OR smartphone_pv < 55",
                        Arrays.asList(new String[] {
                                "text:total_pv", "!=", "double:33.0",
                                "OR",
                                "text:smartphone_pv", "<", "double:55.0" })
                },

                {
                        "NOT (total_pv != +33 OR smartphone_pv < 55)",
                        Arrays.asList(new String[] {
                                "NOT",
                                "(",
                                "text:total_pv", "!=", "double:33.0",
                                "OR",
                                "text:smartphone_pv", "<", "double:55.0",
                                ")" })
                },

                // bracket expression
                {
                        "(key1 >= 3.3 AND key2 <= 5) OR key3 != 9.9",
                        Arrays.asList(new String[] {
                                "(",
                                "text:key1", ">=", "double:3.3",
                                "AND",
                                "text:key2", "<=", "double:5.0",
                                ")",
                                "OR",
                                "text:key3", "!=", "double:9.9"
                        })
                },

                {
                        "total_pv != +33 AND (smartphone_pv < 55 OR twenties_feature >= -3.3)",
                        Arrays.asList(new String[] {
                                "text:total_pv", "!=", "double:33.0",
                                "AND",
                                "(",
                                "text:smartphone_pv", "<", "double:55.0",
                                "OR",
                                "text:twenties_feature", ">=", "double:-3.3",
                                ")" })
                },

                {
                        "(total_pv != +33 AND (smartphone_pv < 55 OR twenties_feature >= -3.3))",
                        Arrays.asList(new String[] {
                                "(", "text:total_pv", "!=", "double:33.0",
                                "AND",
                                "(",
                                "text:smartphone_pv", "<", "double:55.0",
                                "OR",
                                "text:twenties_feature", ">=", "double:-3.3",
                                ")",
                                ")" })
                },

                {
                        "(total_pv != +33 OR ((smartphone_pv < 55 AND twenties_feature >= -3.3)))",
                        Arrays.asList(new String[] {
                                "(",
                                "text:total_pv", "!=", "double:33.0",
                                "OR",
                                "(",
                                "(",
                                "text:smartphone_pv", "<", "double:55.0",
                                "AND",
                                "text:twenties_feature", ">=", "double:-3.3",
                                ")",
                                ")",
                                ")" })
                },

                {
                        "total_pv != +33 AND (smartphone_pv < 55 OR "
                                + "(twenties_feature >= -3.3 AND kyusyu_feature <= 1.1)) OR shiga_ub = 10",
                        Arrays.asList(new String[] {
                                "text:total_pv", "!=", "double:33.0",
                                "AND",
                                "(",
                                "text:smartphone_pv", "<", "double:55.0",
                                "OR",
                                "(",
                                "text:twenties_feature", ">=", "double:-3.3",
                                "AND",
                                "text:kyusyu_feature", "<=", "double:1.1",
                                ")",
                                ")",
                                "OR",
                                "text:shiga_ub", "=", "double:10.0" })
                },
        });
    }

    private String query;
    private List<String> expected;

    public StrVisitorTests(String query, List<String> expected) {
        this.query = query;
        this.expected = expected;
    }

    @Test
    public void testVisit() {
        val expr = QueryParser.parse(this.query);
        val actual = new StrVisitor().visit(expr);
        assertEquals(this.expected, actual);
    }
}
