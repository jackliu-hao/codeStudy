package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CommonToken;

public class TokenTagToken extends CommonToken {
   private final String tokenName;
   private final String label;

   public TokenTagToken(String tokenName, int type) {
      this(tokenName, type, (String)null);
   }

   public TokenTagToken(String tokenName, int type, String label) {
      super(type);
      this.tokenName = tokenName;
      this.label = label;
   }

   public final String getTokenName() {
      return this.tokenName;
   }

   public final String getLabel() {
      return this.label;
   }

   public String getText() {
      return this.label != null ? "<" + this.label + ":" + this.tokenName + ">" : "<" + this.tokenName + ">";
   }

   public String toString() {
      return this.tokenName + ":" + this.type;
   }
}
