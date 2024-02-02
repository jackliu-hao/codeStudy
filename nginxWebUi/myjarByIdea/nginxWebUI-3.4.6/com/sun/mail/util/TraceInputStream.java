package com.sun.mail.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class TraceInputStream extends FilterInputStream {
   private boolean trace = false;
   private boolean quote = false;
   private OutputStream traceOut;

   public TraceInputStream(InputStream in, MailLogger logger) {
      super(in);
      this.trace = logger.isLoggable(Level.FINEST);
      this.traceOut = new LogOutputStream(logger);
   }

   public TraceInputStream(InputStream in, OutputStream traceOut) {
      super(in);
      this.traceOut = traceOut;
   }

   public void setTrace(boolean trace) {
      this.trace = trace;
   }

   public void setQuote(boolean quote) {
      this.quote = quote;
   }

   public int read() throws IOException {
      int b = this.in.read();
      if (this.trace && b != -1) {
         if (this.quote) {
            this.writeByte(b);
         } else {
            this.traceOut.write(b);
         }
      }

      return b;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int count = this.in.read(b, off, len);
      if (this.trace && count != -1) {
         if (this.quote) {
            for(int i = 0; i < count; ++i) {
               this.writeByte(b[off + i]);
            }
         } else {
            this.traceOut.write(b, off, count);
         }
      }

      return count;
   }

   private final void writeByte(int b) throws IOException {
      b &= 255;
      if (b > 127) {
         this.traceOut.write(77);
         this.traceOut.write(45);
         b &= 127;
      }

      if (b == 13) {
         this.traceOut.write(92);
         this.traceOut.write(114);
      } else if (b == 10) {
         this.traceOut.write(92);
         this.traceOut.write(110);
         this.traceOut.write(10);
      } else if (b == 9) {
         this.traceOut.write(92);
         this.traceOut.write(116);
      } else if (b < 32) {
         this.traceOut.write(94);
         this.traceOut.write(64 + b);
      } else {
         this.traceOut.write(b);
      }

   }
}
