package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.tokens.Token;

public interface Scanner {
  boolean checkToken(Token.ID... paramVarArgs);
  
  Token peekToken();
  
  Token getToken();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\scanner\Scanner.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */