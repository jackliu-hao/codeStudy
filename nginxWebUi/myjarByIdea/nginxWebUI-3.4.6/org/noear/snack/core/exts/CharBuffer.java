package org.noear.snack.core.exts;

public class CharBuffer {
   private char[] buffer;
   private int length;
   public boolean isString;

   public CharBuffer() {
      this(5120);
   }

   public CharBuffer(int capacity) {
      this.isString = false;
      this.buffer = new char[capacity];
      this.length = 0;
   }

   public void append(char c) {
      if (this.length == this.buffer.length) {
         char[] newbuf = new char[this.buffer.length * 2];
         System.arraycopy(this.buffer, 0, newbuf, 0, this.buffer.length);
         this.buffer = newbuf;
      }

      this.buffer[this.length++] = c;
   }

   public char charAt(int idx) {
      return this.buffer[idx];
   }

   public int length() {
      return this.length;
   }

   public void setLength(int len) {
      this.length = len;
      this.isString = false;
   }

   public void clear() {
      this.length = 0;
   }

   public String toString() {
      return new String(this.buffer, 0, this.length);
   }

   public void trimLast() {
      while(this.length > 0 && this.buffer[this.length - 1] == ' ') {
         --this.length;
      }

   }
}
