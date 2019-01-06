package sample.query.expression.visitor;

import sample.query.expression.visitor.NodeVisitor.NodeType;

public interface Node {

    public NodeType getType();

    public Object getValue();

    public int getDepth();
}
