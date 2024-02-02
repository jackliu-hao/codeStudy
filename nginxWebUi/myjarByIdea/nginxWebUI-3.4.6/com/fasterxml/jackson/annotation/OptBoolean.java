package com.fasterxml.jackson.annotation;

public enum OptBoolean {
   TRUE,
   FALSE,
   DEFAULT;

   public Boolean asBoolean() {
      if (this == DEFAULT) {
         return null;
      } else {
         return this == TRUE ? Boolean.TRUE : Boolean.FALSE;
      }
   }

   public boolean asPrimitive() {
      return this == TRUE;
   }

   public static OptBoolean fromBoolean(Boolean b) {
      if (b == null) {
         return DEFAULT;
      } else {
         return b ? TRUE : FALSE;
      }
   }

   public static boolean equals(Boolean b1, Boolean b2) {
      if (b1 == null) {
         return b2 == null;
      } else {
         return b1.equals(b2);
      }
   }
}
