package org.h2.bnf;

import java.util.HashMap;

public class RuleFixed implements Rule {
   public static final int YMD = 0;
   public static final int HMS = 1;
   public static final int NANOS = 2;
   public static final int ANY_EXCEPT_SINGLE_QUOTE = 3;
   public static final int ANY_EXCEPT_DOUBLE_QUOTE = 4;
   public static final int ANY_UNTIL_EOL = 5;
   public static final int ANY_UNTIL_END = 6;
   public static final int ANY_WORD = 7;
   public static final int ANY_EXCEPT_2_DOLLAR = 8;
   public static final int HEX_START = 10;
   public static final int CONCAT = 11;
   public static final int AZ_UNDERSCORE = 12;
   public static final int AF = 13;
   public static final int DIGIT = 14;
   public static final int OPEN_BRACKET = 15;
   public static final int CLOSE_BRACKET = 16;
   public static final int JSON_TEXT = 17;
   private final int type;

   RuleFixed(int var1) {
      this.type = var1;
   }

   public void accept(BnfVisitor var1) {
      var1.visitRuleFixed(this.type);
   }

   public void setLinks(HashMap<String, RuleHead> var1) {
   }

   public boolean autoComplete(Sentence var1) {
      String var2;
      String var3;
      boolean var4;
      var1.stopIfRequired();
      var2 = var1.getQuery();
      var3 = var2;
      var4 = false;
      label191:
      switch (this.type) {
         case 0:
            while(var3.length() > 0 && "0123456789-".indexOf(var3.charAt(0)) >= 0) {
               var3 = var3.substring(1);
            }

            if (var3.length() == 0) {
               var1.add("2006-01-01", "1", 1);
            }

            var4 = true;
            break;
         case 1:
            while(var3.length() > 0 && "0123456789:".indexOf(var3.charAt(0)) >= 0) {
               var3 = var3.substring(1);
            }

            if (var3.length() == 0) {
               var1.add("12:00:00", "1", 1);
            }
            break;
         case 2:
            while(var3.length() > 0 && Character.isDigit(var3.charAt(0))) {
               var3 = var3.substring(1);
            }

            if (var3.length() == 0) {
               var1.add("nanoseconds", "0", 1);
            }

            var4 = true;
            break;
         case 3:
            while(true) {
               while(var3.length() <= 0 || var3.charAt(0) == '\'') {
                  if (!var3.startsWith("''")) {
                     if (var3.length() == 0) {
                        var1.add("anything", "Hello World", 1);
                        var1.add("'", "'", 1);
                     }
                     break label191;
                  }

                  var3 = var3.substring(2);
               }

               var3 = var3.substring(1);
            }
         case 4:
            while(true) {
               while(var3.length() <= 0 || var3.charAt(0) == '"') {
                  if (!var3.startsWith("\"\"")) {
                     if (var3.length() == 0) {
                        var1.add("anything", "identifier", 1);
                        var1.add("\"", "\"", 1);
                     }
                     break label191;
                  }

                  var3 = var3.substring(2);
               }

               var3 = var3.substring(1);
            }
         case 5:
         case 6:
         case 9:
         default:
            throw new AssertionError("type=" + this.type);
         case 7:
         case 17:
            while(var3.length() > 0 && !Bnf.startWithSpace(var3)) {
               var3 = var3.substring(1);
            }

            if (var3.length() == 0) {
               var1.add("anything", "anything", 1);
            }
            break;
         case 8:
            while(var3.length() > 0 && !var3.startsWith("$$")) {
               var3 = var3.substring(1);
            }

            if (var3.length() == 0) {
               var1.add("anything", "Hello World", 1);
               var1.add("$$", "$$", 1);
            }
            break;
         case 10:
            if (!var2.startsWith("0X") && !var2.startsWith("0x")) {
               if ("0".equals(var2)) {
                  var1.add("0x", "x", 1);
               } else if (var2.length() == 0) {
                  var1.add("0x", "0x", 1);
               }
            } else {
               var3 = var2.substring(2);
            }
            break;
         case 11:
            if (var2.equals("|")) {
               var1.add("||", "|", 1);
            } else if (var2.startsWith("||")) {
               var3 = var2.substring(2);
            } else if (var2.length() == 0) {
               var1.add("||", "||", 1);
            }

            var4 = true;
            break;
         case 12:
            if (var2.length() > 0 && (Character.isLetter(var2.charAt(0)) || var2.charAt(0) == '_')) {
               var3 = var2.substring(1);
            }

            if (var3.length() == 0) {
               var1.add("character", "A", 1);
            }
            break;
         case 13:
            if (var2.length() > 0) {
               char var5 = Character.toUpperCase(var2.charAt(0));
               if (var5 >= 'A' && var5 <= 'F') {
                  var3 = var2.substring(1);
               }
            }

            if (var3.length() == 0) {
               var1.add("hex character", "0A", 1);
            }
            break;
         case 14:
            if (var2.length() > 0 && Character.isDigit(var2.charAt(0))) {
               var3 = var2.substring(1);
            }

            if (var3.length() == 0) {
               var1.add("digit", "1", 1);
            }
            break;
         case 15:
            if (var2.length() == 0) {
               var1.add("[", "[", 1);
            } else if (var2.charAt(0) == '[') {
               var3 = var2.substring(1);
            }

            var4 = true;
            break;
         case 16:
            if (var2.length() == 0) {
               var1.add("]", "]", 1);
            } else if (var2.charAt(0) == ']') {
               var3 = var2.substring(1);
            }

            var4 = true;
      }

      if (var3.equals(var2)) {
         return false;
      } else {
         if (var4) {
            while(Bnf.startWithSpace(var3)) {
               var3 = var3.substring(1);
            }
         }

         var1.setQuery(var3);
         return true;
      }
   }

   public String toString() {
      return "#" + this.type;
   }
}
