package com.mysql.cj.protocol.x;

import java.io.FilterOutputStream;
import java.io.OutputStream;

public class ReusableOutputStream extends FilterOutputStream {
   protected ReusableOutputStream(OutputStream out) {
      super(out);
   }

   public OutputStream setOutputStream(OutputStream newOut) {
      OutputStream previousOut = this.out;
      this.out = newOut;
      return previousOut;
   }
}
