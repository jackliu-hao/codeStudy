package com.sun.mail.iap;

import java.io.IOException;
import java.io.OutputStream;

public interface Literal {
  int size();
  
  void writeTo(OutputStream paramOutputStream) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\iap\Literal.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */