package org.codehaus.plexus.util.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamFeeder extends Thread {
   private InputStream input;
   private OutputStream output;
   private boolean done;

   public StreamFeeder(InputStream input, OutputStream output) {
      this.input = input;
      this.output = output;
   }

   public void run() {
      try {
         this.feed();
      } catch (Throwable var12) {
      } finally {
         this.close();
         synchronized(this) {
            this.done = true;
            this.notifyAll();
         }
      }

   }

   public void close() {
      if (this.input != null) {
         synchronized(this.input) {
            try {
               this.input.close();
            } catch (IOException var7) {
            }

            this.input = null;
         }
      }

      if (this.output != null) {
         synchronized(this.output) {
            try {
               this.output.close();
            } catch (IOException var5) {
            }

            this.output = null;
         }
      }

   }

   public boolean isDone() {
      return this.done;
   }

   private void feed() throws IOException {
      int data = this.input.read();

      while(!this.done && data != -1) {
         synchronized(this.output) {
            this.output.write(data);
            data = this.input.read();
         }
      }

   }
}
