package org.h2.expression.function;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

public final class StringFunction1 extends Function1 {
   public static final int UPPER = 0;
   public static final int LOWER = 1;
   public static final int ASCII = 2;
   public static final int CHAR = 3;
   public static final int STRINGENCODE = 4;
   public static final int STRINGDECODE = 5;
   public static final int STRINGTOUTF8 = 6;
   public static final int UTF8TOSTRING = 7;
   public static final int HEXTORAW = 8;
   public static final int RAWTOHEX = 9;
   public static final int SPACE = 10;
   public static final int QUOTE_IDENT = 11;
   private static final String[] NAMES = new String[]{"UPPER", "LOWER", "ASCII", "CHAR", "STRINGENCODE", "STRINGDECODE", "STRINGTOUTF8", "UTF8TOSTRING", "HEXTORAW", "RAWTOHEX", "SPACE", "QUOTE_IDENT"};
   private final int function;

   public StringFunction1(Expression var1, int var2) {
      super(var1);
      this.function = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         Object var4;
         switch (this.function) {
            case 0:
               var4 = ValueVarchar.get(var2.getString().toUpperCase(), var1);
               break;
            case 1:
               var4 = ValueVarchar.get(var2.getString().toLowerCase(), var1);
               break;
            case 2:
               String var5 = var2.getString();
               var4 = var5.isEmpty() ? ValueNull.INSTANCE : ValueInteger.get(var5.charAt(0));
               break;
            case 3:
               var4 = ValueVarchar.get(String.valueOf((char)var2.getInt()), var1);
               break;
            case 4:
               var4 = ValueVarchar.get(StringUtils.javaEncode(var2.getString()), var1);
               break;
            case 5:
               var4 = ValueVarchar.get(StringUtils.javaDecode(var2.getString()), var1);
               break;
            case 6:
               var4 = ValueVarbinary.getNoCopy(var2.getString().getBytes(StandardCharsets.UTF_8));
               break;
            case 7:
               var4 = ValueVarchar.get(new String(var2.getBytesNoCopy(), StandardCharsets.UTF_8), var1);
               break;
            case 8:
               var4 = hexToRaw(var2.getString(), var1);
               break;
            case 9:
               var4 = ValueVarchar.get(rawToHex(var2, var1.getMode()), var1);
               break;
            case 10:
               byte[] var3 = new byte[Math.max(0, var2.getInt())];
               Arrays.fill(var3, (byte)32);
               var4 = ValueVarchar.get(new String(var3, StandardCharsets.ISO_8859_1), var1);
               break;
            case 11:
               var4 = ValueVarchar.get(StringUtils.quoteIdentifier(var2.getString()), var1);
               break;
            default:
               throw DbException.getInternalError("function=" + this.function);
         }

         return (Value)var4;
      }
   }

   private static Value hexToRaw(String var0, SessionLocal var1) {
      if (var1.getMode().getEnum() == Mode.ModeEnum.Oracle) {
         return ValueVarbinary.get(StringUtils.convertHexToBytes(var0));
      } else {
         int var2 = var0.length();
         if (var2 % 4 != 0) {
            throw DbException.get(22018, (String)var0);
         } else {
            StringBuilder var3 = new StringBuilder(var2 / 4);

            for(int var4 = 0; var4 < var2; var4 += 4) {
               try {
                  var3.append((char)Integer.parseInt(var0.substring(var4, var4 + 4), 16));
               } catch (NumberFormatException var6) {
                  throw DbException.get(22018, (String)var0);
               }
            }

            return ValueVarchar.get(var3.toString(), var1);
         }
      }
   }

   private static String rawToHex(Value var0, Mode var1) {
      if (DataType.isBinaryStringOrSpecialBinaryType(var0.getValueType())) {
         return StringUtils.convertBytesToHex(var0.getBytesNoCopy());
      } else {
         String var2 = var0.getString();
         if (var1.getEnum() == Mode.ModeEnum.Oracle) {
            return StringUtils.convertBytesToHex(var2.getBytes(StandardCharsets.UTF_8));
         } else {
            int var3 = var2.length();
            StringBuilder var4 = new StringBuilder(4 * var3);

            for(int var5 = 0; var5 < var3; ++var5) {
               String var6 = Integer.toHexString(var2.charAt(var5) & '\uffff');

               for(int var7 = var6.length(); var7 < 4; ++var7) {
                  var4.append('0');
               }

               var4.append(var6);
            }

            return var4.toString();
         }
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      TypeInfo var2;
      switch (this.function) {
         case 0:
         case 1:
         case 4:
         case 10:
         case 11:
            this.type = TypeInfo.TYPE_VARCHAR;
            break;
         case 2:
            this.type = TypeInfo.TYPE_INTEGER;
            break;
         case 3:
            this.type = TypeInfo.getTypeInfo(2, 1L, 0, (ExtTypeInfo)null);
            break;
         case 5:
            var2 = this.arg.getType();
            this.type = DataType.isCharacterStringType(var2.getValueType()) ? TypeInfo.getTypeInfo(2, var2.getPrecision(), 0, (ExtTypeInfo)null) : TypeInfo.TYPE_VARCHAR;
            break;
         case 6:
            this.type = TypeInfo.TYPE_VARBINARY;
            break;
         case 7:
            var2 = this.arg.getType();
            this.type = DataType.isBinaryStringType(var2.getValueType()) ? TypeInfo.getTypeInfo(2, var2.getPrecision(), 0, (ExtTypeInfo)null) : TypeInfo.TYPE_VARCHAR;
            break;
         case 8:
            var2 = this.arg.getType();
            if (var1.getMode().getEnum() == Mode.ModeEnum.Oracle) {
               if (DataType.isCharacterStringType(var2.getValueType())) {
                  this.type = TypeInfo.getTypeInfo(6, var2.getPrecision() / 2L, 0, (ExtTypeInfo)null);
               } else {
                  this.type = TypeInfo.TYPE_VARBINARY;
               }
            } else if (DataType.isCharacterStringType(var2.getValueType())) {
               this.type = TypeInfo.getTypeInfo(2, var2.getPrecision() / 4L, 0, (ExtTypeInfo)null);
            } else {
               this.type = TypeInfo.TYPE_VARCHAR;
            }
            break;
         case 9:
            var2 = this.arg.getType();
            long var3 = var2.getPrecision();
            int var5 = DataType.isBinaryStringOrSpecialBinaryType(var2.getValueType()) ? 2 : (var1.getMode().getEnum() == Mode.ModeEnum.Oracle ? 6 : 4);
            this.type = TypeInfo.getTypeInfo(2, var3 <= Long.MAX_VALUE / (long)var5 ? var3 * (long)var5 : Long.MAX_VALUE, 0, (ExtTypeInfo)null);
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Expression)(this.arg.isConstant() ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
