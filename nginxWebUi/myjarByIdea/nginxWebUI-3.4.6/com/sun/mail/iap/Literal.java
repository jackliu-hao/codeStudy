package com.sun.mail.iap;

import java.io.IOException;
import java.io.OutputStream;

public interface Literal {
   int size();

   void writeTo(OutputStream var1) throws IOException;
}
