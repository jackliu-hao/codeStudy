package org.antlr.v4.runtime;

public interface TokenSource {
   Token nextToken();

   int getLine();

   int getCharPositionInLine();

   CharStream getInputStream();

   String getSourceName();

   void setTokenFactory(TokenFactory<?> var1);

   TokenFactory<?> getTokenFactory();
}
