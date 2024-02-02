package com.google.zxing.common;

import com.google.zxing.FormatException;
import java.util.HashMap;
import java.util.Map;

public enum CharacterSetECI {
   Cp437(new int[]{0, 2}, new String[0]),
   ISO8859_1(new int[]{1, 3}, new String[]{"ISO-8859-1"}),
   ISO8859_2(4, new String[]{"ISO-8859-2"}),
   ISO8859_3(5, new String[]{"ISO-8859-3"}),
   ISO8859_4(6, new String[]{"ISO-8859-4"}),
   ISO8859_5(7, new String[]{"ISO-8859-5"}),
   ISO8859_6(8, new String[]{"ISO-8859-6"}),
   ISO8859_7(9, new String[]{"ISO-8859-7"}),
   ISO8859_8(10, new String[]{"ISO-8859-8"}),
   ISO8859_9(11, new String[]{"ISO-8859-9"}),
   ISO8859_10(12, new String[]{"ISO-8859-10"}),
   ISO8859_11(13, new String[]{"ISO-8859-11"}),
   ISO8859_13(15, new String[]{"ISO-8859-13"}),
   ISO8859_14(16, new String[]{"ISO-8859-14"}),
   ISO8859_15(17, new String[]{"ISO-8859-15"}),
   ISO8859_16(18, new String[]{"ISO-8859-16"}),
   SJIS(20, new String[]{"Shift_JIS"}),
   Cp1250(21, new String[]{"windows-1250"}),
   Cp1251(22, new String[]{"windows-1251"}),
   Cp1252(23, new String[]{"windows-1252"}),
   Cp1256(24, new String[]{"windows-1256"}),
   UnicodeBigUnmarked(25, new String[]{"UTF-16BE", "UnicodeBig"}),
   UTF8(26, new String[]{"UTF-8"}),
   ASCII(new int[]{27, 170}, new String[]{"US-ASCII"}),
   Big5(28),
   GB18030(29, new String[]{"GB2312", "EUC_CN", "GBK"}),
   EUC_KR(30, new String[]{"EUC-KR"});

   private static final Map<Integer, CharacterSetECI> VALUE_TO_ECI = new HashMap();
   private static final Map<String, CharacterSetECI> NAME_TO_ECI = new HashMap();
   private final int[] values;
   private final String[] otherEncodingNames;

   private CharacterSetECI(int value) {
      this(new int[]{value});
   }

   private CharacterSetECI(int value, String... otherEncodingNames) {
      this.values = new int[]{value};
      this.otherEncodingNames = otherEncodingNames;
   }

   private CharacterSetECI(int[] values, String... otherEncodingNames) {
      this.values = values;
      this.otherEncodingNames = otherEncodingNames;
   }

   public int getValue() {
      return this.values[0];
   }

   public static CharacterSetECI getCharacterSetECIByValue(int value) throws FormatException {
      if (value >= 0 && value < 900) {
         return (CharacterSetECI)VALUE_TO_ECI.get(value);
      } else {
         throw FormatException.getFormatInstance();
      }
   }

   public static CharacterSetECI getCharacterSetECIByName(String name) {
      return (CharacterSetECI)NAME_TO_ECI.get(name);
   }

   static {
      CharacterSetECI[] var0;
      int var1 = (var0 = values()).length;

      for(int var2 = 0; var2 < var1; ++var2) {
         CharacterSetECI eci;
         int[] var4;
         int var5 = (var4 = (eci = var0[var2]).values).length;

         int var6;
         for(var6 = 0; var6 < var5; ++var6) {
            int value = var4[var6];
            VALUE_TO_ECI.put(value, eci);
         }

         NAME_TO_ECI.put(eci.name(), eci);
         String[] var8;
         var5 = (var8 = eci.otherEncodingNames).length;

         for(var6 = 0; var6 < var5; ++var6) {
            String name = var8[var6];
            NAME_TO_ECI.put(name, eci);
         }
      }

   }
}
