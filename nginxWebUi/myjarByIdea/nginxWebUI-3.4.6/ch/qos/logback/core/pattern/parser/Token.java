package ch.qos.logback.core.pattern.parser;

class Token {
   static final int PERCENT = 37;
   static final int RIGHT_PARENTHESIS = 41;
   static final int MINUS = 45;
   static final int DOT = 46;
   static final int CURLY_LEFT = 123;
   static final int CURLY_RIGHT = 125;
   static final int LITERAL = 1000;
   static final int FORMAT_MODIFIER = 1002;
   static final int SIMPLE_KEYWORD = 1004;
   static final int COMPOSITE_KEYWORD = 1005;
   static final int OPTION = 1006;
   static final int EOF = Integer.MAX_VALUE;
   static Token EOF_TOKEN = new Token(Integer.MAX_VALUE, "EOF");
   static Token RIGHT_PARENTHESIS_TOKEN = new Token(41);
   static Token BARE_COMPOSITE_KEYWORD_TOKEN = new Token(1005, "BARE");
   static Token PERCENT_TOKEN = new Token(37);
   private final int type;
   private final Object value;

   public Token(int type) {
      this(type, (Object)null);
   }

   public Token(int type, Object value) {
      this.type = type;
      this.value = value;
   }

   public int getType() {
      return this.type;
   }

   public Object getValue() {
      return this.value;
   }

   public String toString() {
      String typeStr = null;
      switch (this.type) {
         case 37:
            typeStr = "%";
            break;
         case 41:
            typeStr = "RIGHT_PARENTHESIS";
            break;
         case 1000:
            typeStr = "LITERAL";
            break;
         case 1002:
            typeStr = "FormatModifier";
            break;
         case 1004:
            typeStr = "SIMPLE_KEYWORD";
            break;
         case 1005:
            typeStr = "COMPOSITE_KEYWORD";
            break;
         case 1006:
            typeStr = "OPTION";
            break;
         default:
            typeStr = "UNKNOWN";
      }

      return this.value == null ? "Token(" + typeStr + ")" : "Token(" + typeStr + ", \"" + this.value + "\")";
   }

   public int hashCode() {
      int result = this.type;
      result = 29 * result + (this.value != null ? this.value.hashCode() : 0);
      return result;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Token)) {
         return false;
      } else {
         Token token = (Token)o;
         if (this.type != token.type) {
            return false;
         } else {
            if (this.value != null) {
               if (!this.value.equals(token.value)) {
                  return false;
               }
            } else if (token.value != null) {
               return false;
            }

            return true;
         }
      }
   }
}
