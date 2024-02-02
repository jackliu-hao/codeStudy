package cn.hutool.core.lang;

import java.io.Serializable;

public interface EnumItem<E extends EnumItem<E>> extends Serializable {
   String name();

   default String text() {
      return this.name();
   }

   int intVal();

   default E[] items() {
      return (EnumItem[])((EnumItem[])this.getClass().getEnumConstants());
   }

   default E fromInt(Integer intVal) {
      if (intVal == null) {
         return null;
      } else {
         E[] vs = this.items();
         EnumItem[] var3 = vs;
         int var4 = vs.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            E enumItem = var3[var5];
            if (enumItem.intVal() == intVal) {
               return enumItem;
            }
         }

         return null;
      }
   }

   default E fromStr(String strVal) {
      if (strVal == null) {
         return null;
      } else {
         E[] vs = this.items();
         EnumItem[] var3 = vs;
         int var4 = vs.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            E enumItem = var3[var5];
            if (strVal.equalsIgnoreCase(enumItem.name())) {
               return enumItem;
            }
         }

         return null;
      }
   }
}
