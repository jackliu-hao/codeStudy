package org.noear.snack.core.exts;

public class CharReader {
   private String chars;
   private int _length;
   private int _next = 0;
   private char _val;

   public CharReader(String s) {
      this.chars = s;
      this._length = s.length();
   }

   public boolean read() {
      if (this._next >= this._length) {
         return false;
      } else {
         this._val = this.chars.charAt(this._next++);
         return true;
      }
   }

   public char next() {
      return this.read() ? this._val : '\u0000';
   }

   public int length() {
      return this._length;
   }

   public char value() {
      return this._val;
   }
}
