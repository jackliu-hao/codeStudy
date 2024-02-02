package org.h2.expression.function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class RegexpFunction extends FunctionN {
   public static final int REGEXP_LIKE = 0;
   public static final int REGEXP_REPLACE = 1;
   public static final int REGEXP_SUBSTR = 2;
   private static final String[] NAMES = new String[]{"REGEXP_LIKE", "REGEXP_REPLACE", "REGEXP_SUBSTR"};
   private final int function;

   public RegexpFunction(int var1) {
      super(new Expression[var1 == 0 ? 3 : 6]);
      this.function = var1;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.args[0].getValue(var1);
      Value var3 = this.args[1].getValue(var1);
      int var4 = this.args.length;
      Value var5;
      Value var6;
      Value var7;
      Object var12;
      String var14;
      int var17;
      switch (this.function) {
         case 0:
            var5 = var4 >= 3 ? this.args[2].getValue(var1) : null;
            if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE && var5 != ValueNull.INSTANCE) {
               var14 = var3.getString();
               String var16 = var5 != null ? var5.getString() : null;
               var17 = makeRegexpFlags(var16, false);

               try {
                  var12 = ValueBoolean.get(Pattern.compile(var14, var17).matcher(var2.getString()).find());
                  break;
               } catch (PatternSyntaxException var11) {
                  throw DbException.get(22025, var11, var14);
               }
            }

            return ValueNull.INSTANCE;
         case 1:
            String var13 = var2.getString();
            if (var1.getMode().getEnum() == Mode.ModeEnum.Oracle) {
               var14 = this.args[2].getValue(var1).getString();
               int var15 = var4 >= 4 ? this.args[3].getValue(var1).getInt() : 1;
               var17 = var4 >= 5 ? this.args[4].getValue(var1).getInt() : 0;
               String var9 = var4 >= 6 ? this.args[5].getValue(var1).getString() : null;
               if (var13 == null) {
                  var12 = ValueNull.INSTANCE;
               } else {
                  String var10 = var3.getString();
                  var12 = regexpReplace(var1, var13, var10 != null ? var10 : "", var14 != null ? var14 : "", var15, var17, var9);
               }
            } else {
               if (var4 > 4) {
                  throw DbException.get(7001, (String[])(this.getName(), "3..4"));
               }

               var6 = this.args[2].getValue(var1);
               var7 = var4 == 4 ? this.args[3].getValue(var1) : null;
               if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE && var6 != ValueNull.INSTANCE && var7 != ValueNull.INSTANCE) {
                  var12 = regexpReplace(var1, var13, var3.getString(), var6.getString(), 1, 0, var7 != null ? var7.getString() : null);
               } else {
                  var12 = ValueNull.INSTANCE;
               }
            }
            break;
         case 2:
            var5 = var4 >= 3 ? this.args[2].getValue(var1) : null;
            var6 = var4 >= 4 ? this.args[3].getValue(var1) : null;
            var7 = var4 >= 5 ? this.args[4].getValue(var1) : null;
            Value var8 = var4 >= 6 ? this.args[5].getValue(var1) : null;
            var12 = regexpSubstr(var2, var3, var5, var6, var7, var8, var1);
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var12;
   }

   private static Value regexpReplace(SessionLocal var0, String var1, String var2, String var3, int var4, int var5, String var6) {
      Mode var7 = var0.getMode();
      int var9;
      if (var7.regexpReplaceBackslashReferences && (var3.indexOf(92) >= 0 || var3.indexOf(36) >= 0)) {
         StringBuilder var8 = new StringBuilder();

         for(var9 = 0; var9 < var3.length(); ++var9) {
            char var10 = var3.charAt(var9);
            if (var10 == '$') {
               var8.append('\\');
            } else if (var10 == '\\') {
               ++var9;
               if (var9 < var3.length()) {
                  var10 = var3.charAt(var9);
                  var8.append((char)(var10 >= '0' && var10 <= '9' ? '$' : '\\'));
               }
            }

            var8.append(var10);
         }

         var3 = var8.toString();
      }

      boolean var15 = var7.getEnum() == Mode.ModeEnum.PostgreSQL;
      var9 = makeRegexpFlags(var6, var15);
      if (var15 && (var6 == null || var6.isEmpty() || !var6.contains("g"))) {
         var5 = 1;
      }

      try {
         Matcher var16 = Pattern.compile(var2, var9).matcher(var1).region(var4 - 1, var1.length());
         if (var5 == 0) {
            return ValueVarchar.get(var16.replaceAll(var3), var0);
         } else {
            StringBuffer var11 = new StringBuffer();

            for(int var12 = 1; var16.find(); ++var12) {
               if (var12 == var5) {
                  var16.appendReplacement(var11, var3);
                  break;
               }
            }

            var16.appendTail(var11);
            return ValueVarchar.get(var11.toString(), var0);
         }
      } catch (PatternSyntaxException var13) {
         throw DbException.get(22025, var13, var2);
      } catch (IllegalArgumentException | StringIndexOutOfBoundsException var14) {
         throw DbException.get(22025, var14, var3);
      }
   }

   private static Value regexpSubstr(Value var0, Value var1, Value var2, Value var3, Value var4, Value var5, SessionLocal var6) {
      if (var0 != ValueNull.INSTANCE && var1 != ValueNull.INSTANCE && var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE && var5 != ValueNull.INSTANCE) {
         String var7 = var1.getString();
         int var8 = var2 != null ? var2.getInt() - 1 : 0;
         int var9 = var3 != null ? var3.getInt() : 1;
         String var10 = var4 != null ? var4.getString() : null;
         int var11 = var5 != null ? var5.getInt() : 0;
         int var12 = makeRegexpFlags(var10, false);

         try {
            Matcher var13 = Pattern.compile(var7, var12).matcher(var0.getString());
            boolean var14 = var13.find(var8);

            for(int var15 = 1; var15 < var9 && var14; ++var15) {
               var14 = var13.find();
            }

            return (Value)(!var14 ? ValueNull.INSTANCE : ValueVarchar.get(var13.group(var11), var6));
         } catch (PatternSyntaxException var16) {
            throw DbException.get(22025, var16, var7);
         } catch (IndexOutOfBoundsException var17) {
            return ValueNull.INSTANCE;
         }
      } else {
         return ValueNull.INSTANCE;
      }
   }

   private static int makeRegexpFlags(String var0, boolean var1) {
      int var2 = 64;
      if (var0 != null) {
         for(int var3 = 0; var3 < var0.length(); ++var3) {
            switch (var0.charAt(var3)) {
               case 'c':
                  var2 &= -3;
                  break;
               case 'd':
               case 'e':
               case 'f':
               case 'h':
               case 'j':
               case 'k':
               case 'l':
               default:
                  throw DbException.get(90008, var0);
               case 'g':
                  if (!var1) {
                     throw DbException.get(90008, var0);
                  }
                  break;
               case 'i':
                  var2 |= 2;
                  break;
               case 'm':
                  var2 |= 8;
                  break;
               case 'n':
                  var2 |= 32;
            }
         }
      }

      return var2;
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      byte var3;
      byte var4;
      switch (this.function) {
         case 0:
            var3 = 2;
            var4 = 3;
            this.type = TypeInfo.TYPE_BOOLEAN;
            break;
         case 1:
            var3 = 3;
            var4 = 6;
            this.type = TypeInfo.TYPE_VARCHAR;
            break;
         case 2:
            var3 = 2;
            var4 = 6;
            this.type = TypeInfo.TYPE_VARCHAR;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      int var5 = this.args.length;
      if (var5 >= var3 && var5 <= var4) {
         return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
      } else {
         throw DbException.get(7001, (String[])(this.getName(), var3 + ".." + var4));
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
