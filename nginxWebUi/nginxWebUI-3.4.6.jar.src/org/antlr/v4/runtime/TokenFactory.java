package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Pair;

public interface TokenFactory<Symbol extends Token> {
  Symbol create(Pair<TokenSource, CharStream> paramPair, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  Symbol create(int paramInt, String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\TokenFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */