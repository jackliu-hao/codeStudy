package org.h2.util;

import java.util.HashMap;

public class ParserUtil {
   public static final int KEYWORD = 1;
   public static final int IDENTIFIER = 2;
   public static final int ALL = 3;
   public static final int AND = 4;
   public static final int ANY = 5;
   public static final int ARRAY = 6;
   public static final int AS = 7;
   public static final int ASYMMETRIC = 8;
   public static final int AUTHORIZATION = 9;
   public static final int BETWEEN = 10;
   public static final int CASE = 11;
   public static final int CAST = 12;
   public static final int CHECK = 13;
   public static final int CONSTRAINT = 14;
   public static final int CROSS = 15;
   public static final int CURRENT_CATALOG = 16;
   public static final int CURRENT_DATE = 17;
   public static final int CURRENT_PATH = 18;
   public static final int CURRENT_ROLE = 19;
   public static final int CURRENT_SCHEMA = 20;
   public static final int CURRENT_TIME = 21;
   public static final int CURRENT_TIMESTAMP = 22;
   public static final int CURRENT_USER = 23;
   public static final int DAY = 24;
   public static final int DEFAULT = 25;
   public static final int DISTINCT = 26;
   public static final int ELSE = 27;
   public static final int END = 28;
   public static final int EXCEPT = 29;
   public static final int EXISTS = 30;
   public static final int FALSE = 31;
   public static final int FETCH = 32;
   public static final int FOR = 33;
   public static final int FOREIGN = 34;
   public static final int FROM = 35;
   public static final int FULL = 36;
   public static final int GROUP = 37;
   public static final int HAVING = 38;
   public static final int HOUR = 39;
   public static final int IF = 40;
   public static final int IN = 41;
   public static final int INNER = 42;
   public static final int INTERSECT = 43;
   public static final int INTERVAL = 44;
   public static final int IS = 45;
   public static final int JOIN = 46;
   public static final int KEY = 47;
   public static final int LEFT = 48;
   public static final int LIKE = 49;
   public static final int LIMIT = 50;
   public static final int LOCALTIME = 51;
   public static final int LOCALTIMESTAMP = 52;
   public static final int MINUS = 53;
   public static final int MINUTE = 54;
   public static final int MONTH = 55;
   public static final int NATURAL = 56;
   public static final int NOT = 57;
   public static final int NULL = 58;
   public static final int OFFSET = 59;
   public static final int ON = 60;
   public static final int OR = 61;
   public static final int ORDER = 62;
   public static final int PRIMARY = 63;
   public static final int QUALIFY = 64;
   public static final int RIGHT = 65;
   public static final int ROW = 66;
   public static final int ROWNUM = 67;
   public static final int SECOND = 68;
   public static final int SELECT = 69;
   public static final int SESSION_USER = 70;
   public static final int SET = 71;
   public static final int SOME = 72;
   public static final int SYMMETRIC = 73;
   public static final int SYSTEM_USER = 74;
   public static final int TABLE = 75;
   public static final int TO = 76;
   public static final int TRUE = 77;
   public static final int UESCAPE = 78;
   public static final int UNION = 79;
   public static final int UNIQUE = 80;
   public static final int UNKNOWN = 81;
   public static final int USER = 82;
   public static final int USING = 83;
   public static final int VALUE = 84;
   public static final int VALUES = 85;
   public static final int WHEN = 86;
   public static final int WHERE = 87;
   public static final int WINDOW = 88;
   public static final int WITH = 89;
   public static final int YEAR = 90;
   public static final int _ROWID_ = 91;
   public static final int FIRST_KEYWORD = 3;
   public static final int LAST_KEYWORD = 91;
   private static final HashMap<String, Integer> KEYWORDS;

   private ParserUtil() {
   }

   public static StringBuilder quoteIdentifier(StringBuilder var0, String var1, int var2) {
      if (var1 == null) {
         return var0.append("\"\"");
      } else {
         return (var2 & 1) != 0 && isSimpleIdentifier(var1, false, false) ? var0.append(var1) : StringUtils.quoteIdentifier(var0, var1);
      }
   }

   public static boolean isKeyword(String var0, boolean var1) {
      return getTokenType(var0, var1, false) != 2;
   }

   public static boolean isSimpleIdentifier(String var0, boolean var1, boolean var2) {
      if (var1 && var2) {
         throw new IllegalArgumentException("databaseToUpper && databaseToLower");
      } else {
         int var3 = var0.length();
         if (var3 != 0 && checkLetter(var1, var2, var0.charAt(0))) {
            for(int var4 = 1; var4 < var3; ++var4) {
               char var5 = var0.charAt(var4);
               if (var5 != '_' && (var5 < '0' || var5 > '9') && !checkLetter(var1, var2, var5)) {
                  return false;
               }
            }

            return getTokenType(var0, !var1, true) == 2;
         } else {
            return false;
         }
      }
   }

   private static boolean checkLetter(boolean var0, boolean var1, char var2) {
      if (var0) {
         if (var2 < 'A' || var2 > 'Z') {
            return false;
         }
      } else if (var1) {
         if (var2 < 'a' || var2 > 'z') {
            return false;
         }
      } else if ((var2 < 'A' || var2 > 'Z') && (var2 < 'a' || var2 > 'z')) {
         return false;
      }

      return true;
   }

   public static int getTokenType(String var0, boolean var1, boolean var2) {
      int var3 = var0.length();
      if (var3 > 1 && var3 <= 17) {
         if (var1) {
            var0 = StringUtils.toUpperEnglish(var0);
         }

         Integer var4 = (Integer)KEYWORDS.get(var0);
         if (var4 == null) {
            return 2;
         } else {
            int var5 = var4;
            return var5 == 1 && !var2 ? 2 : var5;
         }
      } else {
         return 2;
      }
   }

   static {
      HashMap var0 = new HashMap(256);
      var0.put("ALL", 3);
      var0.put("AND", 4);
      var0.put("ANY", 5);
      var0.put("ARRAY", 6);
      var0.put("AS", 7);
      var0.put("ASYMMETRIC", 8);
      var0.put("AUTHORIZATION", 9);
      var0.put("BETWEEN", 10);
      var0.put("CASE", 11);
      var0.put("CAST", 12);
      var0.put("CHECK", 13);
      var0.put("CONSTRAINT", 14);
      var0.put("CROSS", 15);
      var0.put("CURRENT_CATALOG", 16);
      var0.put("CURRENT_DATE", 17);
      var0.put("CURRENT_PATH", 18);
      var0.put("CURRENT_ROLE", 19);
      var0.put("CURRENT_SCHEMA", 20);
      var0.put("CURRENT_TIME", 21);
      var0.put("CURRENT_TIMESTAMP", 22);
      var0.put("CURRENT_USER", 23);
      var0.put("DAY", 24);
      var0.put("DEFAULT", 25);
      var0.put("DISTINCT", 26);
      var0.put("ELSE", 27);
      var0.put("END", 28);
      var0.put("EXCEPT", 29);
      var0.put("EXISTS", 30);
      var0.put("FALSE", 31);
      var0.put("FETCH", 32);
      var0.put("FOR", 33);
      var0.put("FOREIGN", 34);
      var0.put("FROM", 35);
      var0.put("FULL", 36);
      var0.put("GROUP", 37);
      var0.put("HAVING", 38);
      var0.put("HOUR", 39);
      var0.put("IF", 40);
      var0.put("IN", 41);
      var0.put("INNER", 42);
      var0.put("INTERSECT", 43);
      var0.put("INTERVAL", 44);
      var0.put("IS", 45);
      var0.put("JOIN", 46);
      var0.put("KEY", 47);
      var0.put("LEFT", 48);
      var0.put("LIKE", 49);
      var0.put("LIMIT", 50);
      var0.put("LOCALTIME", 51);
      var0.put("LOCALTIMESTAMP", 52);
      var0.put("MINUS", 53);
      var0.put("MINUTE", 54);
      var0.put("MONTH", 55);
      var0.put("NATURAL", 56);
      var0.put("NOT", 57);
      var0.put("NULL", 58);
      var0.put("OFFSET", 59);
      var0.put("ON", 60);
      var0.put("OR", 61);
      var0.put("ORDER", 62);
      var0.put("PRIMARY", 63);
      var0.put("QUALIFY", 64);
      var0.put("RIGHT", 65);
      var0.put("ROW", 66);
      var0.put("ROWNUM", 67);
      var0.put("SECOND", 68);
      var0.put("SELECT", 69);
      var0.put("SESSION_USER", 70);
      var0.put("SET", 71);
      var0.put("SOME", 72);
      var0.put("SYMMETRIC", 73);
      var0.put("SYSTEM_USER", 74);
      var0.put("TABLE", 75);
      var0.put("TO", 76);
      var0.put("TRUE", 77);
      var0.put("UESCAPE", 78);
      var0.put("UNION", 79);
      var0.put("UNIQUE", 80);
      var0.put("UNKNOWN", 81);
      var0.put("USER", 82);
      var0.put("USING", 83);
      var0.put("VALUE", 84);
      var0.put("VALUES", 85);
      var0.put("WHEN", 86);
      var0.put("WHERE", 87);
      var0.put("WINDOW", 88);
      var0.put("WITH", 89);
      var0.put("YEAR", 90);
      var0.put("_ROWID_", 91);
      var0.put("BOTH", 1);
      var0.put("FILTER", 1);
      var0.put("GROUPS", 1);
      var0.put("ILIKE", 1);
      var0.put("LEADING", 1);
      var0.put("OVER", 1);
      var0.put("PARTITION", 1);
      var0.put("RANGE", 1);
      var0.put("REGEXP", 1);
      var0.put("ROWS", 1);
      var0.put("TOP", 1);
      var0.put("TRAILING", 1);
      KEYWORDS = var0;
   }
}
