package com.github.odiszapc.nginxparser;

public class NgxParam extends NgxAbstractEntry {
   public NgxParam() {
      super();
   }

   public String toString() {
      String s = super.toString();
      return s.isEmpty() ? s : s + ";";
   }
}
