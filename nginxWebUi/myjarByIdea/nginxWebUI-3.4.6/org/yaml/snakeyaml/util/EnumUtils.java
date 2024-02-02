package org.yaml.snakeyaml.util;

public class EnumUtils {
   public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> enumType, String name) {
      Enum[] arr$ = (Enum[])enumType.getEnumConstants();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         T constant = arr$[i$];
         if (constant.name().compareToIgnoreCase(name) == 0) {
            return constant;
         }
      }

      throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
   }
}
