package org.antlr.v4.runtime;

public interface Token {
   int INVALID_TYPE = 0;
   int EPSILON = -2;
   int MIN_USER_TOKEN_TYPE = 1;
   int EOF = -1;
   int DEFAULT_CHANNEL = 0;
   int HIDDEN_CHANNEL = 1;
   int MIN_USER_CHANNEL_VALUE = 2;

   String getText();

   int getType();

   int getLine();

   int getCharPositionInLine();

   int getChannel();

   int getTokenIndex();

   int getStartIndex();

   int getStopIndex();

   TokenSource getTokenSource();

   CharStream getInputStream();
}
