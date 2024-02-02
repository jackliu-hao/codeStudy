package org.antlr.v4.runtime.tree;

public abstract class AbstractParseTreeVisitor<T> implements ParseTreeVisitor<T> {
   public T visit(ParseTree tree) {
      return tree.accept(this);
   }

   public T visitChildren(RuleNode node) {
      T result = this.defaultResult();
      int n = node.getChildCount();

      for(int i = 0; i < n && this.shouldVisitNextChild(node, result); ++i) {
         ParseTree c = node.getChild(i);
         T childResult = c.accept(this);
         result = this.aggregateResult(result, childResult);
      }

      return result;
   }

   public T visitTerminal(TerminalNode node) {
      return this.defaultResult();
   }

   public T visitErrorNode(ErrorNode node) {
      return this.defaultResult();
   }

   protected T defaultResult() {
      return null;
   }

   protected T aggregateResult(T aggregate, T nextResult) {
      return nextResult;
   }

   protected boolean shouldVisitNextChild(RuleNode node, T currentResult) {
      return true;
   }
}
