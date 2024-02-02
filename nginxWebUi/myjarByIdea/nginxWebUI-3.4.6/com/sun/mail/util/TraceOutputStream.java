package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

public class TraceOutputStream extends FilterOutputStream {
   private boolean trace = false;
   private boolean quote = false;
   private OutputStream traceOut;

   public TraceOutputStream(OutputStream out, MailLogger logger) {
      super(out);
      this.trace = logger.isLoggable(Level.FINEST);
      this.traceOut = new LogOutputStream(logger);
   }

   public TraceOutputStream(OutputStream out, OutputStream traceOut) {
      super(out);
      this.traceOut = traceOut;
   }

   public void setTrace(boolean trace) {
      this.trace = trace;
   }

   public void setQuote(boolean quote) {
      this.quote = quote;
   }

   public void write(int b) throws IOException {
      if (this.trace) {
         if (this.quote) {
            this.writeByte(b);
         } else {
            this.traceOut.write(b);
         }
      }

      this.out.write(b);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (this.trace) {
         if (this.quote) {
            for(int i = 0; i < len; ++i) {
               this.writeByte(b[off + i]);
            }
         } else {
            this.traceOut.write(b, off, len);
         }
      }

      this.out.write(b, off, len);
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
