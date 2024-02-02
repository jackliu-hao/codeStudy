package org.antlr.v4.runtime;

public interface WritableToken extends Token {
   void setText(String var1);

   void setType(int var1);

   void setLine(int var1);

   void setCharPositionInLine(int var1);

   void setChannel(int var1);

   void setTokenIndex(int var1);
}
