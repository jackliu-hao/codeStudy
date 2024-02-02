package ch.qos.logback.core.subst;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ScanException;
import java.util.List;

public class Parser {
   final List<Token> tokenList;
   int pointer = 0;

   public Parser(List<Token> tokenList) {
      this.tokenList = tokenList;
   }

   public Node parse() throws ScanException {
      return this.tokenList != null && !this.tokenList.isEmpty() ? this.E() : null;
   }

   private Node E() throws ScanException {
      Node t = this.T();
      if (t == null) {
         return null;
      } else {
         Node eOpt = this.Eopt();
         if (eOpt != null) {
            t.append(eOpt);
         }

         return t;
      }
   }

   private Node Eopt() throws ScanException {
      Token next = this.peekAtCurentToken();
      return next == null ? null : this.E();
   }

   private Node T() throws ScanException {
      Token t = this.peekAtCurentToken();
      switch (t.type) {
         case LITERAL:
            this.advanceTokenPointer();
            return this.makeNewLiteralNode(t.payload);
         case CURLY_LEFT:
            this.advanceTokenPointer();
            Node innerNode = this.C();
            Token right = this.peekAtCurentToken();
            this.expectCurlyRight(right);
            this.advanceTokenPointer();
            Node curlyLeft = this.makeNewLiteralNode(CoreConstants.LEFT_ACCOLADE);
            curlyLeft.append(innerNode);
            curlyLeft.append(this.makeNewLiteralNode(CoreConstants.RIGHT_ACCOLADE));
            return curlyLeft;
         case START:
            this.advanceTokenPointer();
            Node v = this.V();
            Token w = this.peekAtCurentToken();
            this.expectCurlyRight(w);
            this.advanceTokenPointer();
            return v;
         default:
            return null;
      }
   }

   private Node makeNewLiteralNode(String s) {
      return new Node(Node.Type.LITERAL, s);
   }

   private Node V() throws ScanException {
      Node e = this.E();
      Node variable = new Node(Node.Type.VARIABLE, e);
      Token t = this.peekAtCurentToken();
      if (this.isDefaultToken(t)) {
         this.advanceTokenPointer();
         Node def = this.E();
         variable.defaultPart = def;
      }

      return variable;
   }

   private Node C() throws ScanException {
      Node e0 = this.E();
      Token t = this.peekAtCurentToken();
      if (this.isDefaultToken(t)) {
         this.advanceTokenPointer();
         Node literal = this.makeNewLiteralNode(":-");
         e0.append(literal);
         Node e1 = this.E();
         e0.append(e1);
      }

      return e0;
   }

   private boolean isDefaultToken(Token t) {
      return t != null && t.type == Token.Type.DEFAULT;
   }

   void advanceTokenPointer() {
      ++this.pointer;
   }

   void expectNotNull(Token t, String expected) {
      if (t == null) {
         throw new IllegalArgumentException("All tokens consumed but was expecting \"" + expected + "\"");
      }
   }

   void expectCurlyRight(Token t) throws ScanException {
      this.expectNotNull(t, "}");
      if (t.type != Token.Type.CURLY_RIGHT) {
         throw new ScanException("Expecting }");
      }
   }

   Token peekAtCurentToken() {
      return this.pointer < this.tokenList.size() ? (Token)this.tokenList.get(this.pointer) : null;
   }
}
