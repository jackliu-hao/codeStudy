package cn.hutool.core.lang.ansi;

import cn.hutool.core.lang.Assert;

public final class Ansi8BitColor implements AnsiElement {
   private static final String PREFIX_FORE = "38;5;";
   private static final String PREFIX_BACK = "48;5;";
   private final String prefix;
   private final int code;

   public static Ansi8BitColor foreground(int code) {
      return new Ansi8BitColor("38;5;", code);
   }

   public static Ansi8BitColor background(int code) {
      return new Ansi8BitColor("48;5;", code);
   }

   private Ansi8BitColor(String prefix, int code) {
      Assert.isTrue(code >= 0 && code <= 255, "Code must be between 0 and 255");
      this.prefix = prefix;
      this.code = code;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         Ansi8BitColor other = (Ansi8BitColor)obj;
         return this.prefix.equals(other.prefix) && this.code == other.code;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.prefix.hashCode() * 31 + this.code;
   }

   public String toString() {
      return this.prefix + this.code;
   }
}
