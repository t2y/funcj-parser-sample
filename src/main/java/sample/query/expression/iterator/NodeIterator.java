package sample.query.expression.iterator;

import java.util.Iterator;

import sample.query.expression.model.Expr;
import sample.query.expression.visitor.Node;
import sample.query.expression.visitor.NodeVisitor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.val;

@ToString
@AllArgsConstructor
public class NodeIterator implements Iterable<Node> {
    private final Expr expr;

    @Override
    public Iterator<Node> iterator() {
        val nodes = new NodeVisitor().visit(this.expr);
        return new Iterator<Node>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return !(currentIndex == nodes.size());
            }

            @Override
            public Node next() {
                val node = nodes.get(currentIndex);
                currentIndex++;
                return node;
            }
        };
    }
}
