package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

public class TerminalNodeImpl implements TerminalNode {
   public Token symbol;
   public ParseTree parent;

   public TerminalNodeImpl(Token symbol) {
      this.symbol = symbol;
   }

   public ParseTree getChild(int i) {
      return null;
   }

   public Token getSymbol() {
      return this.symbol;
   }

   public ParseTree getParent() {
      return this.parent;
   }

   public Token getPayload() {
      return this.symbol;
   }

   public Interval getSourceInterval() {
      if (this.symbol == null) {
         return Interval.INVALID;
      } else {
         int tokenIndex = this.symbol.getTokenIndex();
         return new Interval(tokenIndex, tokenIndex);
      }
   }

   public int getChildCount() {
      return 0;
   }

   public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      return visitor.visitTerminal(this);
   }

   public String getText() {
      return this.symbol.getText();
   }

   public String toStringTree(Parser parser) {
      return this.toString();
   }

   public String toString() {
      return this.symbol.getType() == -1 ? "<EOF>" : this.symbol.getText();
   }

   public String toStringTree() {
      return this.toString();
   }
}
