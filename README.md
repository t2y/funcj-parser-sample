# funcj-parser-sample

Sample parser library for simple query expression.

This parser requires [funcj.parser](https://github.com/typemeta/funcj/tree/master/parser).

## How to build/test

```bash
$ gradle build
```

```bash
$ gradle test --info
```

## How to use

### use the parsed result directly

`QueryParser.parse()` occures `RuntimeException` if parsing query has failed.

```java
String query = "(key1 >= 3.3 AND key2 <= 5) OR key3 != 9.9";
try {
    Expr result = QueryParser.parse(query);
    // something to do
} catch (RuntimeException e) {
    throw new MyParserError(e.getMessage());
}
```

### use Node object with NodeVisitor/NodeIterator

#### NodeVisitor

```java
String query = "(key1 >= 3.3 AND key2 <= 5) OR key3 != 9.9";
try {
    Expr result = QueryParser.parse(query);
    List<Node> nodes = new NodeVisitor().visit(result);
} catch (RuntimeException e) {
    throw new MyParserError(e.getMessage());
}
```

#### NodeIterator

`NodeIterator` uses `NodeVisitor` internally.

It might be convenient when the iteration should be controlled.

```java
String query = "(key1 >= 3.3 AND key2 <= 5) OR key3 != 9.9";
Expr result;
try {
    result = QueryParser.parse(query);
} catch (RuntimeException e) {
    throw new RuntimeException(e.getMessage());
}

Iterator<Node> iter = new NodeIterator(result).iterator();
while (iter.hasNext()) {
    Node node = iter.next();
    if (node.getType() == NodeType.CLOSE_BRACKET) {
        break;
    }
    // something to do
}
```

`NodeIterator` also works with the enhanced for loop, like this.

```java
for (Node node : new NodeIterator(result)) {
    System.out.println(node);
}
```

The output is here.

```
NodeVisitor.StringNode(type=OPEN_BRACKET, value=(, depth=1)
NodeVisitor.StringNode(type=TEXT, value=key1, depth=1)
NodeVisitor.StringNode(type=RELATIONAL_OP, value=>=, depth=1)
NodeVisitor.DoubleNode(type=DOUBLE, value=3.3, depth=1)
NodeVisitor.StringNode(type=LOGICAL_OP, value=AND, depth=1)
NodeVisitor.StringNode(type=TEXT, value=key2, depth=1)
NodeVisitor.StringNode(type=RELATIONAL_OP, value=<=, depth=1)
NodeVisitor.LongNode(type=LONG, value=5, depth=1)
NodeVisitor.StringNode(type=CLOSE_BRACKET, value=), depth=1)
NodeVisitor.StringNode(type=LOGICAL_OP, value=OR, depth=0)
NodeVisitor.StringNode(type=TEXT, value=key3, depth=0)
NodeVisitor.StringNode(type=RELATIONAL_OP, value=!=, depth=0)
NodeVisitor.DoubleNode(type=DOUBLE, value=9.9, depth=0)
```

### use own requirement with user defined Visitor

You can extend the parsed result by implementing some methods on `Visitor` interface.

```java
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
```

Define your class with some visitor methods to what you want to do.

```java
public class MyVisitor implements Visitor<MyNode> {
    ...
}
```
