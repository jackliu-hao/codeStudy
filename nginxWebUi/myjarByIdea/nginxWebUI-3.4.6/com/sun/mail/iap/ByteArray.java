package com.sun.mail.iap;

import java.io.ByteArrayInputStream;

public class ByteArray {
   private byte[] bytes;
   private int start;
   private int count;

   public ByteArray(byte[] b, int start, int count) {
      this.bytes = b;
      this.start = start;
      this.count = count;
   }

   public ByteArray(int size) {
      this(new byte[size], 0, size);
   }

   public byte[] getBytes() {
      return this.bytes;
   }

   public byte[] getNewBytes() {
      byte[] b = new byte[this.count];
      System.arraycopy(this.bytes, this.start, b, 0, this.count);
      return b;
   }

   public int getStart() {
      return this.start;
   }

   public int getCount() {
      return this.count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public ByteArrayInputStream toByteArrayInputStream() {
      return new ByteArrayInputStream(this.bytes, this.start, this.count);
   }

   public void grow(int incr) {
      byte[] nbuf = new byte[this.bytes.length + incr];
      System.arraycopy(this.bytes, 0, nbuf, 0, this.bytes.length);
      this.bytes = nbuf;
   }
}
