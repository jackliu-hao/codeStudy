package freemarker.ext.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TypeFlags {
   static final int WIDENED_NUMERICAL_UNWRAPPING_HINT = 1;
   static final int BYTE = 4;
   static final int SHORT = 8;
   static final int INTEGER = 16;
   static final int LONG = 32;
   static final int FLOAT = 64;
   static final int DOUBLE = 128;
   static final int BIG_INTEGER = 256;
   static final int BIG_DECIMAL = 512;
   static final int UNKNOWN_NUMERICAL_TYPE = 1024;
   static final int ACCEPTS_NUMBER = 2048;
   static final int ACCEPTS_DATE = 4096;
   static final int ACCEPTS_STRING = 8192;
   static final int ACCEPTS_BOOLEAN = 16384;
   static final int ACCEPTS_MAP = 32768;
   static final int ACCEPTS_LIST = 65536;
   static final int ACCEPTS_SET = 131072;
   static final int ACCEPTS_ARRAY = 262144;
   static final int CHARACTER = 524288;
   static final int ACCEPTS_ANY_OBJECT = 522240;
   static final int MASK_KNOWN_INTEGERS = 316;
   static final int MASK_KNOWN_NONINTEGERS = 704;
   static final int MASK_ALL_KNOWN_NUMERICALS = 1020;
   static final int MASK_ALL_NUMERICALS = 2044;

   static int classToTypeFlags(Class pClass) {
      if (pClass == Object.class) {
         return 522240;
      } else if (pClass == String.class) {
         return 8192;
      } else if (pClass.isPrimitive()) {
         if (pClass == Integer.TYPE) {
            return 2064;
         } else if (pClass == Long.TYPE) {
            return 2080;
         } else if (pClass == Double.TYPE) {
            return 2176;
         } else if (pClass == Float.TYPE) {
            return 2112;
         } else if (pClass == Byte.TYPE) {
            return 2052;
         } else if (pClass == Short.TYPE) {
            return 2056;
         } else if (pClass == Character.TYPE) {
            return 524288;
         } else {
            return pClass == Boolean.TYPE ? 16384 : 0;
         }
      } else if (Number.class.isAssignableFrom(pClass)) {
         if (pClass == Integer.class) {
            return 2064;
         } else if (pClass == Long.class) {
            return 2080;
         } else if (pClass == Double.class) {
            return 2176;
         } else if (pClass == Float.class) {
            return 2112;
         } else if (pClass == Byte.class) {
            return 2052;
         } else if (pClass == Short.class) {
            return 2056;
         } else if (BigDecimal.class.isAssignableFrom(pClass)) {
            return 2560;
         } else {
            return BigInteger.class.isAssignableFrom(pClass) ? 2304 : 3072;
         }
      } else if (pClass.isArray()) {
         return 262144;
      } else {
         int flags = 0;
         if (pClass.isAssignableFrom(String.class)) {
            flags |= 8192;
         }

         if (pClass.isAssignableFrom(Date.class)) {
            flags |= 4096;
         }

         if (pClass.isAssignableFrom(Boolean.class)) {
            flags |= 16384;
         }

         if (pClass.isAssignableFrom(Map.class)) {
            flags |= 32768;
         }

         if (pClass.isAssignableFrom(List.class)) {
            flags |= 65536;
         }

         if (pClass.isAssignableFrom(Set.class)) {
            flags |= 131072;
         }

         if (pClass == Character.class) {
            flags |= 524288;
         }

         return flags;
      }
   }
}
