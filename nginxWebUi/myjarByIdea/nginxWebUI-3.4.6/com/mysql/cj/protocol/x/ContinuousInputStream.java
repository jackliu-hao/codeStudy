package com.mysql.cj.protocol.x;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class ContinuousInputStream extends FilterInputStream {
   private Queue<InputStream> inputStreams = new LinkedList();
   private boolean closed = false;

   protected ContinuousInputStream(InputStream in) {
      super(in);
   }

   public int available() throws IOException {
      this.ensureOpen();
      int available = super.available();
      return available == 0 && this.nextInLine() ? this.available() : available;
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         super.close();
         Iterator var1 = this.inputStreams.iterator();

         while(var1.hasNext()) {
            InputStream is = (InputStream)var1.next();
            is.close();
         }
      }

   }

   public int read() throws IOException {
      this.ensureOpen();
      int read = super.read();
      if (read >= 0) {
         return read;
      } else {
         return this.nextInLine() ? this.read() : read;
      }
   }

   public int read(byte[] b) throws IOException {
      this.ensureOpen();
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      int toRead = Math.min(len, this.available());
      int read = super.read(b, off, toRead);
      if (read > 0) {
         return read;
      } else {
         return this.nextInLine() ? this.read(b, off, len) : read;
      }
   }

   protected boolean addInputStream(InputStream newIn) {
      return this.inputStreams.offer(newIn);
   }

   private boolean nextInLine() throws IOException {
      InputStream nextInputStream = (InputStream)this.inputStreams.poll();
      if (nextInputStream != null) {
         super.close();
         this.in = nextInputStream;
         return true;
      } else {
         return false;
      }
   }

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }
}
