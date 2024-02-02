package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

public interface TokenStream extends IntStream {
   Token LT(int var1);

   Token get(int var1);

   TokenSource getTokenSource();

   String getText(Interval var1);

   String getText();

   String getText(RuleContext var1);

   String getText(Token var1, Token var2);
}
