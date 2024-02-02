package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

public class RuleTagToken implements Token {
   private final String ruleName;
   private final int bypassTokenType;
   private final String label;

   public RuleTagToken(String ruleName, int bypassTokenType) {
      this(ruleName, bypassTokenType, (String)null);
   }

   public RuleTagToken(String ruleName, int bypassTokenType, String label) {
      if (ruleName != null && !ruleName.isEmpty()) {
         this.ruleName = ruleName;
         this.bypassTokenType = bypassTokenType;
         this.label = label;
      } else {
         throw new IllegalArgumentException("ruleName cannot be null or empty.");
      }
   }

   public final String getRuleName() {
      return this.ruleName;
   }

   public final String getLabel() {
      return this.label;
   }

   public int getChannel() {
      return 0;
   }

   public String getText() {
      return this.label != null ? "<" + this.label + ":" + this.ruleName + ">" : "<" + this.ruleName + ">";
   }

   public int getType() {
      return this.bypassTokenType;
   }

   public int getLine() {
      return 0;
   }

   public int getCharPositionInLine() {
      return -1;
   }

   public int getTokenIndex() {
      return -1;
   }

   public int getStartIndex() {
      return -1;
   }

   public int getStopIndex() {
      return -1;
   }

   public TokenSource getTokenSource() {
      return null;
   }

   public CharStream getInputStream() {
      return null;
   }

   public String toString() {
      return this.ruleName + ":" + this.bypassTokenType;
   }
}
