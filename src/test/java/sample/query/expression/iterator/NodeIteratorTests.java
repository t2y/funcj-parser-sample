package sample.query.expression.iterator;

import static java.util.Spliterators.*;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import sample.query.expression.parser.QueryParser;
import sample.query.expression.visitor.Node;
import sample.query.expression.visitor.NodeVisitor;
import sample.query.expression.visitor.NodeVisitor.NodeType;
import lombok.val;

@RunWith(Parameterized.class)
public class NodeIteratorTests {

    @Parameters(name = "{index}: query [{0}] => expected [{1}]")
    public static Iterable<Object[]> data() {
        val nv = new NodeVisitor();
        return Arrays.asList(new Object[][] {
                {
                        "key1 >= 3.3 AND key2 <= 5",
                        Arrays.asList(new Node[] {
                                nv.new StringNode(NodeType.TEXT, "key1", 0),
                                nv.new StringNode(NodeType.RELATIONAL_OP, ">=", 0),
                                nv.new DoubleNode(NodeType.DOUBLE, 3.3, 0),
                                nv.new StringNode(NodeType.LOGICAL_OP, "AND", 0),
                                nv.new StringNode(NodeType.TEXT, "key2", 0),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "<=", 0),
                                nv.new LongNode(NodeType.LONG, 5L, 0),
                        })
                },

                {
                        "(key1 >= 3.3 AND key2 <= 5) OR key3 != 9.9",
                        Arrays.asList(new Node[] {
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 1),
                                nv.new StringNode(NodeType.TEXT, "key1", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, ">=", 1),
                                nv.new DoubleNode(NodeType.DOUBLE, 3.3, 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "AND", 1),
                                nv.new StringNode(NodeType.TEXT, "key2", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "<=", 1),
                                nv.new LongNode(NodeType.LONG, 5L, 1),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "OR", 0),
                                nv.new StringNode(NodeType.TEXT, "key3", 0),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "!=", 0),
                                nv.new DoubleNode(NodeType.DOUBLE, 9.9, 0),
                        })
                },

                {
                        "(key1 >= 3.3 AND key2 <= -5) OR (key3 != -9.9 AND key4 < +88)",
                        Arrays.asList(new Node[] {
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 1),
                                nv.new StringNode(NodeType.TEXT, "key1", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, ">=", 1),
                                nv.new DoubleNode(NodeType.DOUBLE, 3.3, 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "AND", 1),
                                nv.new StringNode(NodeType.TEXT, "key2", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "<=", 1),
                                nv.new LongNode(NodeType.LONG, -5L, 1),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "OR", 0),
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 1),
                                nv.new StringNode(NodeType.TEXT, "key3", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "!=", 1),
                                nv.new DoubleNode(NodeType.DOUBLE, -9.9, 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "AND", 1),
                                nv.new StringNode(NodeType.TEXT, "key4", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "<", 1),
                                nv.new LongNode(NodeType.LONG, 88L, 1),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 1),
                        })
                },

                {
                        "(key1 >= 3.3 AND key2 <= -5) AND (key3 != -9.9 OR key4 < +88) OR NOT (key5 > 7)",
                        Arrays.asList(new Node[] {
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 1),
                                nv.new StringNode(NodeType.TEXT, "key1", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, ">=", 1),
                                nv.new DoubleNode(NodeType.DOUBLE, 3.3, 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "AND", 1),
                                nv.new StringNode(NodeType.TEXT, "key2", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "<=", 1),
                                nv.new LongNode(NodeType.LONG, -5L, 1),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "AND", 0),
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 1),
                                nv.new StringNode(NodeType.TEXT, "key3", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "!=", 1),
                                nv.new DoubleNode(NodeType.DOUBLE, -9.9, 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "OR", 1),
                                nv.new StringNode(NodeType.TEXT, "key4", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "<", 1),
                                nv.new LongNode(NodeType.LONG, 88L, 1),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "OR", 0),
                                nv.new StringNode(NodeType.LOGICAL_OP, "NOT", 0),
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 1),
                                nv.new StringNode(NodeType.TEXT, "key5", 1),
                                nv.new StringNode(NodeType.RELATIONAL_OP, ">", 1),
                                nv.new LongNode(NodeType.LONG, 7L, 1),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 1),
                        })
                },

                {
                        "((key1 >= 3.3 AND key2 <= 5 AND (key3 != 8 OR key4 > 0) OR NOT (male))) OR key5 < 6.7",
                        Arrays.asList(new Node[] {
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 1),
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 2),
                                nv.new StringNode(NodeType.TEXT, "key1", 2),
                                nv.new StringNode(NodeType.RELATIONAL_OP, ">=", 2),
                                nv.new DoubleNode(NodeType.DOUBLE, 3.3, 2),
                                nv.new StringNode(NodeType.LOGICAL_OP, "AND", 2),
                                nv.new StringNode(NodeType.TEXT, "key2", 2),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "<=", 2),
                                nv.new LongNode(NodeType.LONG, 5L, 2),
                                nv.new StringNode(NodeType.LOGICAL_OP, "AND", 2),
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 3),
                                nv.new StringNode(NodeType.TEXT, "key3", 3),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "!=", 3),
                                nv.new LongNode(NodeType.LONG, 8L, 3),
                                nv.new StringNode(NodeType.LOGICAL_OP, "OR", 3),
                                nv.new StringNode(NodeType.TEXT, "key4", 3),
                                nv.new StringNode(NodeType.RELATIONAL_OP, ">", 3),
                                nv.new LongNode(NodeType.LONG, 0L, 3),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 3),
                                nv.new StringNode(NodeType.LOGICAL_OP, "OR", 2),
                                nv.new StringNode(NodeType.LOGICAL_OP, "NOT", 2),
                                nv.new StringNode(NodeType.OPEN_BRACKET, "(", 3),
                                nv.new StringNode(NodeType.TEXT, "male", 3),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 3),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 2),
                                nv.new StringNode(NodeType.CLOSE_BRACKET, ")", 1),
                                nv.new StringNode(NodeType.LOGICAL_OP, "OR", 0),
                                nv.new StringNode(NodeType.TEXT, "key5", 0),
                                nv.new StringNode(NodeType.RELATIONAL_OP, "<", 0),
                                nv.new DoubleNode(NodeType.DOUBLE, 6.7, 0),
                        })
                },
        });
    }

    private String query;
    private List<Node> expected;

    public NodeIteratorTests(String query, List<Node> expected) {
        this.query = query;
        this.expected = expected;
    }

    @Test
    public void testIter() {
        val expr = QueryParser.parse(this.query);
        val iter = new NodeIterator(expr).iterator();
        val spliterator = spliteratorUnknownSize(iter, Spliterator.ORDERED);
        val actual = StreamSupport.stream(spliterator, false).collect(toList());
        assertEquals(this.expected, actual);
    }
}
