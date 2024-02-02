package com.sun.jna.platform;

import com.sun.jna.platform.win32.FlagEnum;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EnumUtils {
   public static final int UNINITIALIZED = -1;

   public static <E extends Enum<E>> int toInteger(E val) {
      E[] vals = (Enum[])((Enum[])val.getClass().getEnumConstants());

      for(int idx = 0; idx < vals.length; ++idx) {
         if (vals[idx] == val) {
            return idx;
         }
      }

      throw new IllegalArgumentException();
   }

   public static <E extends Enum<E>> E fromInteger(int idx, Class<E> clazz) {
      if (idx == -1) {
         return null;
      } else {
         E[] vals = (Enum[])clazz.getEnumConstants();
         return vals[idx];
      }
   }

   public static <T extends FlagEnum> Set<T> setFromInteger(int flags, Class<T> clazz) {
      T[] vals = (FlagEnum[])clazz.getEnumConstants();
      Set<T> result = new HashSet();
      FlagEnum[] var4 = vals;
      int var5 = vals.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         T val = var4[var6];
         if ((flags & val.getFlag()) != 0) {
            result.add(val);
         }
      }

      return result;
   }

   public static <T extends FlagEnum> int setToInteger(Set<T> set) {
      int sum = 0;

      FlagEnum t;
      for(Iterator var2 = set.iterator(); var2.hasNext(); sum |= t.getFlag()) {
         t = (FlagEnum)var2.next();
      }

      return sum;
   }
}
