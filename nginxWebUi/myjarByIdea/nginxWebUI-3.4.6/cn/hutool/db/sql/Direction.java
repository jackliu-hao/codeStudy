package cn.hutool.db.sql;

import cn.hutool.core.util.StrUtil;

public enum Direction {
   ASC,
   DESC;

   public static Direction fromString(String value) throws IllegalArgumentException {
      if (StrUtil.isEmpty(value)) {
         return null;
      } else {
         if (1 == value.length()) {
            if ("A".equalsIgnoreCase(value)) {
               return ASC;
            }

            if ("D".equalsIgnoreCase(value)) {
               return DESC;
            }
         }

         try {
            return valueOf(value.toUpperCase());
         } catch (Exception var2) {
            throw new IllegalArgumentException(StrUtil.format("Invalid value [{}] for orders given! Has to be either 'desc' or 'asc' (case insensitive).", new Object[]{value}), var2);
         }
      }
   }
}
