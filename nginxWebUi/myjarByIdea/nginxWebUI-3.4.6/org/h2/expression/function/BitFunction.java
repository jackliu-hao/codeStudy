package org.h2.expression.function;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.aggregate.Aggregate;
import org.h2.expression.aggregate.AggregateType;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBinary;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;
import org.h2.value.ValueVarbinary;

public final class BitFunction extends Function1_2 {
   public static final int BITAND = 0;
   public static final int BITOR = 1;
   public static final int BITXOR = 2;
   public static final int BITNOT = 3;
   public static final int BITNAND = 4;
   public static final int BITNOR = 5;
   public static final int BITXNOR = 6;
   public static final int BITGET = 7;
   public static final int BITCOUNT = 8;
   public static final int LSHIFT = 9;
   public static final int RSHIFT = 10;
   public static final int ULSHIFT = 11;
   public static final int URSHIFT = 12;
   public static final int ROTATELEFT = 13;
   public static final int ROTATERIGHT = 14;
   private static final String[] NAMES = new String[]{"BITAND", "BITOR", "BITXOR", "BITNOT", "BITNAND", "BITNOR", "BITXNOR", "BITGET", "BITCOUNT", "LSHIFT", "RSHIFT", "ULSHIFT", "URSHIFT", "ROTATELEFT", "ROTATERIGHT"};
   private final int function;

   public BitFunction(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.function = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      switch (this.function) {
         case 7:
            return bitGet(var2, var3);
         case 8:
            return bitCount(var2);
         case 9:
            return shift(var2, var3.getLong(), false);
         case 10:
            long var4 = var3.getLong();
            return shift(var2, var4 != Long.MIN_VALUE ? -var4 : Long.MAX_VALUE, false);
         case 11:
            return shift(var2, var3.getLong(), true);
         case 12:
            return shift(var2, -var3.getLong(), true);
         case 13:
            return rotate(var2, var3.getLong(), false);
         case 14:
            return rotate(var2, var3.getLong(), true);
         default:
            return getBitwise(this.function, this.type, var2, var3);
      }
   }

   private static ValueBoolean bitGet(Value var0, Value var1) {
      long var2 = var1.getLong();
      boolean var4;
      if (var2 >= 0L) {
         switch (var0.getValueType()) {
            case 5:
            case 6:
               byte[] var5 = var0.getBytesNoCopy();
               int var6 = (int)(var2 & 7L);
               var2 >>>= 3;
               var4 = var2 < (long)var5.length && (var5[(int)var2] & 1 << var6) != 0;
               break;
            case 7:
            case 8:
            default:
               throw DbException.getInvalidValueException("bit function parameter", var0.getTraceSQL());
            case 9:
               var4 = var2 < 8L && (var0.getByte() & 1 << (int)var2) != 0;
               break;
            case 10:
               var4 = var2 < 16L && (var0.getShort() & 1 << (int)var2) != 0;
               break;
            case 11:
               var4 = var2 < 32L && (var0.getInt() & 1 << (int)var2) != 0;
               break;
            case 12:
               var4 = (var0.getLong() & 1L << (int)var2) != 0L;
         }
      } else {
         var4 = false;
      }

      return ValueBoolean.get(var4);
   }

   private static ValueBigint bitCount(Value var0) {
      long var1;
      switch (var0.getValueType()) {
         case 5:
         case 6:
            byte[] var3 = var0.getBytesNoCopy();
            int var4 = var3.length;
            var1 = 0L;
            int var5 = var4 >>> 3;

            int var6;
            for(var6 = 0; var6 < var5; ++var6) {
               var1 += (long)Long.bitCount(Bits.readLong(var3, var6));
            }

            for(var6 = var5 << 3; var6 < var4; ++var6) {
               var1 += (long)Integer.bitCount(var3[var6] & 255);
            }

            return ValueBigint.get(var1);
         case 7:
         case 8:
         default:
            throw DbException.getInvalidValueException("bit function parameter", var0.getTraceSQL());
         case 9:
            var1 = (long)Integer.bitCount(var0.getByte() & 255);
            break;
         case 10:
            var1 = (long)Integer.bitCount(var0.getShort() & '\uffff');
            break;
         case 11:
            var1 = (long)Integer.bitCount(var0.getInt());
            break;
         case 12:
            var1 = (long)Long.bitCount(var0.getLong());
      }

      return ValueBigint.get(var1);
   }

   private static Value shift(Value var0, long var1, boolean var3) {
      if (var1 == 0L) {
         return var0;
      } else {
         int var4 = var0.getValueType();
         switch (var4) {
            case 5:
            case 6:
               byte[] var16 = var0.getBytesNoCopy();
               int var6 = var16.length;
               if (var6 == 0) {
                  return var0;
               }

               byte[] var7 = new byte[var6];
               if (var1 > -8L * (long)var6 && var1 < 8L * (long)var6) {
                  int var8;
                  int var9;
                  int var10;
                  int var11;
                  int var12;
                  if (var1 > 0L) {
                     var8 = (int)(var1 >> 3);
                     var9 = (int)var1 & 7;
                     if (var9 == 0) {
                        System.arraycopy(var16, var8, var7, 0, var6 - var8);
                     } else {
                        var10 = 8 - var9;
                        var11 = 0;
                        var12 = var8;
                        --var6;

                        while(var12 < var6) {
                           var7[var11++] = (byte)(var16[var12++] << var9 | (var16[var12] & 255) >>> var10);
                        }

                        var7[var11] = (byte)(var16[var12] << var9);
                     }
                  } else {
                     var1 = -var1;
                     var8 = (int)(var1 >> 3);
                     var9 = (int)var1 & 7;
                     if (var9 == 0) {
                        System.arraycopy(var16, 0, var7, var8, var6 - var8);
                     } else {
                        var10 = 8 - var9;
                        var12 = 0;
                        var11 = var8 + 1;

                        for(var7[var8] = (byte)((var16[var12] & 255) >>> var9); var11 < var6; var7[var11++] = (byte)(var16[var12++] << var10 | (var16[var12] & 255) >>> var9)) {
                        }
                     }
                  }
               }

               return (Value)(var4 == 5 ? ValueBinary.getNoCopy(var7) : ValueVarbinary.getNoCopy(var7));
            case 7:
            case 8:
            default:
               throw DbException.getInvalidValueException("bit function parameter", var0.getTraceSQL());
            case 9:
               byte var15;
               if (var1 < 8L) {
                  var15 = var0.getByte();
                  if (var1 > -8L) {
                     if (var1 > 0L) {
                        var15 = (byte)(var15 << (int)var1);
                     } else if (var3) {
                        var15 = (byte)((var15 & 255) >>> (int)(-var1));
                     } else {
                        var15 = (byte)(var15 >> (int)(-var1));
                     }
                  } else if (var3) {
                     var15 = 0;
                  } else {
                     var15 = (byte)(var15 >> 7);
                  }
               } else {
                  var15 = 0;
               }

               return ValueTinyint.get(var15);
            case 10:
               short var14;
               if (var1 < 16L) {
                  var14 = var0.getShort();
                  if (var1 > -16L) {
                     if (var1 > 0L) {
                        var14 = (short)(var14 << (int)var1);
                     } else if (var3) {
                        var14 = (short)((var14 & '\uffff') >>> (int)(-var1));
                     } else {
                        var14 = (short)(var14 >> (int)(-var1));
                     }
                  } else if (var3) {
                     var14 = 0;
                  } else {
                     var14 = (short)(var14 >> 15);
                  }
               } else {
                  var14 = 0;
               }

               return ValueSmallint.get(var14);
            case 11:
               int var13;
               if (var1 < 32L) {
                  var13 = var0.getInt();
                  if (var1 > -32L) {
                     if (var1 > 0L) {
                        var13 <<= (int)var1;
                     } else if (var3) {
                        var13 >>>= (int)(-var1);
                     } else {
                        var13 >>= (int)(-var1);
                     }
                  } else if (var3) {
                     var13 = 0;
                  } else {
                     var13 >>= 31;
                  }
               } else {
                  var13 = 0;
               }

               return ValueInteger.get(var13);
            case 12:
               long var5;
               if (var1 < 64L) {
                  var5 = var0.getLong();
                  if (var1 > -64L) {
                     if (var1 > 0L) {
                        var5 <<= (int)var1;
                     } else if (var3) {
                        var5 >>>= (int)(-var1);
                     } else {
                        var5 >>= (int)(-var1);
                     }
                  } else if (var3) {
                     var5 = 0L;
                  } else {
                     var5 >>= 63;
                  }
               } else {
                  var5 = 0L;
               }

               return ValueBigint.get(var5);
         }
      }
   }

   private static Value rotate(Value var0, long var1, boolean var3) {
      int var4 = var0.getValueType();
      int var5;
      int var6;
      switch (var4) {
         case 5:
         case 6:
            byte[] var15 = var0.getBytesNoCopy();
            var6 = var15.length;
            if (var6 == 0) {
               return var0;
            } else {
               long var7 = (long)(var6 << 3);
               var1 %= var7;
               if (var3) {
                  var1 = -var1;
               }

               if (var1 == 0L) {
                  return var0;
               } else {
                  if (var1 < 0L) {
                     var1 += var7;
                  }

                  byte[] var9 = new byte[var6];
                  int var10 = (int)(var1 >> 3);
                  int var11 = (int)var1 & 7;
                  if (var11 == 0) {
                     System.arraycopy(var15, var10, var9, 0, var6 - var10);
                     System.arraycopy(var15, 0, var9, var6 - var10, var10);
                  } else {
                     int var12 = 8 - var11;
                     int var13 = 0;

                     for(int var14 = var10; var13 < var6; var9[var13++] = (byte)(var15[var14] << var11 | (var15[var14 = (var14 + 1) % var6] & 255) >>> var12)) {
                     }
                  }

                  return (Value)(var4 == 5 ? ValueBinary.getNoCopy(var9) : ValueVarbinary.getNoCopy(var9));
               }
            }
         case 7:
         case 8:
         default:
            throw DbException.getInvalidValueException("bit function parameter", var0.getTraceSQL());
         case 9:
            var5 = (int)var1;
            if (var3) {
               var5 = -var5;
            }

            if ((var5 &= 7) == 0) {
               return var0;
            }

            var6 = var0.getByte() & 255;
            return ValueTinyint.get((byte)(var6 << var5 | var6 >>> 8 - var5));
         case 10:
            var5 = (int)var1;
            if (var3) {
               var5 = -var5;
            }

            if ((var5 &= 15) == 0) {
               return var0;
            }

            var6 = var0.getShort() & '\uffff';
            return ValueSmallint.get((short)(var6 << var5 | var6 >>> 16 - var5));
         case 11:
            var5 = (int)var1;
            if (var3) {
               var5 = -var5;
            }

            if ((var5 &= 31) == 0) {
               return var0;
            }

            return ValueInteger.get(Integer.rotateLeft(var0.getInt(), var5));
         case 12:
            var5 = (int)var1;
            if (var3) {
               var5 = -var5;
            }

            if ((var5 &= 63) == 0) {
               return var0;
            } else {
               return ValueBigint.get(Long.rotateLeft(var0.getLong(), var5));
            }
      }
   }

   public static Value getBitwise(int var0, TypeInfo var1, Value var2, Value var3) {
      return var1.getValueType() < 9 ? getBinaryString(var0, var1, var2, var3) : getNumeric(var0, var1, var2, var3);
   }

   private static Value getBinaryString(int var0, TypeInfo var1, Value var2, Value var3) {
      byte[] var4;
      if (var0 == 3) {
         var4 = var2.getBytes();
         int var13 = 0;

         for(int var14 = var4.length; var13 < var14; ++var13) {
            var4[var13] = (byte)(~var4[var13]);
         }
      } else {
         byte[] var5 = var2.getBytesNoCopy();
         byte[] var6 = var3.getBytesNoCopy();
         int var7 = var5.length;
         int var8 = var6.length;
         int var9;
         int var10;
         if (var7 <= var8) {
            var9 = var7;
            var10 = var8;
         } else {
            var9 = var8;
            var10 = var7;
            byte[] var11 = var5;
            var5 = var6;
            var6 = var11;
         }

         int var15 = (int)var1.getPrecision();
         if (var9 > var15) {
            var9 = var15;
            var10 = var15;
         } else if (var10 > var15) {
            var10 = var15;
         }

         var4 = new byte[var10];
         int var12 = 0;
         switch (var0) {
            case 0:
               while(var12 < var9) {
                  var4[var12] = (byte)(var5[var12] & var6[var12]);
                  ++var12;
               }

               return (Value)(var1.getValueType() == 5 ? ValueBinary.getNoCopy(var4) : ValueVarbinary.getNoCopy(var4));
            case 1:
               while(var12 < var9) {
                  var4[var12] = (byte)(var5[var12] | var6[var12]);
                  ++var12;
               }

               System.arraycopy(var6, var12, var4, var12, var10 - var12);
               break;
            case 2:
               while(var12 < var9) {
                  var4[var12] = (byte)(var5[var12] ^ var6[var12]);
                  ++var12;
               }

               System.arraycopy(var6, var12, var4, var12, var10 - var12);
               break;
            case 3:
            default:
               throw DbException.getInternalError("function=" + var0);
            case 4:
               while(var12 < var9) {
                  var4[var12] = (byte)(~(var5[var12] & var6[var12]));
                  ++var12;
               }

               Arrays.fill(var4, var12, var10, (byte)-1);
               break;
            case 5:
               while(var12 < var9) {
                  var4[var12] = (byte)(~(var5[var12] | var6[var12]));
                  ++var12;
               }

               while(var12 < var10) {
                  var4[var12] = (byte)(~var6[var12]);
                  ++var12;
               }

               return (Value)(var1.getValueType() == 5 ? ValueBinary.getNoCopy(var4) : ValueVarbinary.getNoCopy(var4));
            case 6:
               while(var12 < var9) {
                  var4[var12] = (byte)(~(var5[var12] ^ var6[var12]));
                  ++var12;
               }

               while(var12 < var10) {
                  var4[var12] = (byte)(~var6[var12]);
                  ++var12;
               }
         }
      }

      return (Value)(var1.getValueType() == 5 ? ValueBinary.getNoCopy(var4) : ValueVarbinary.getNoCopy(var4));
   }

   private static Value getNumeric(int var0, TypeInfo var1, Value var2, Value var3) {
      long var4 = var2.getLong();
      switch (var0) {
         case 0:
            var4 &= var3.getLong();
            break;
         case 1:
            var4 |= var3.getLong();
            break;
         case 2:
            var4 ^= var3.getLong();
            break;
         case 3:
            var4 = ~var4;
            break;
         case 4:
            var4 = ~(var4 & var3.getLong());
            break;
         case 5:
            var4 = ~(var4 | var3.getLong());
            break;
         case 6:
            var4 = ~(var4 ^ var3.getLong());
            break;
         default:
            throw DbException.getInternalError("function=" + var0);
      }

      switch (var1.getValueType()) {
         case 9:
            return ValueTinyint.get((byte)((int)var4));
         case 10:
            return ValueSmallint.get((short)((int)var4));
         case 11:
            return ValueInteger.get((int)var4);
         case 12:
            return ValueBigint.get(var4);
         default:
            throw DbException.getInternalError();
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      switch (this.function) {
         case 3:
            return this.optimizeNot(var1);
         case 4:
         case 5:
         case 6:
         default:
            this.type = getCommonType(this.left, this.right);
            break;
         case 7:
            this.type = TypeInfo.TYPE_BOOLEAN;
            break;
         case 8:
            this.type = TypeInfo.TYPE_BIGINT;
            break;
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
            this.type = checkArgType(this.left);
      }

      return (Expression)(!this.left.isConstant() || this.right != null && !this.right.isConstant() ? this : TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type));
   }

   private Expression optimizeNot(SessionLocal var1) {
      this.type = checkArgType(this.left);
      if (this.left.isConstant()) {
         return TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type);
      } else if (this.left instanceof BitFunction) {
         BitFunction var4 = (BitFunction)this.left;
         int var5 = var4.function;
         switch (var5) {
            case 0:
            case 1:
            case 2:
               var5 += 4;
               break;
            case 3:
               return var4.left;
            case 4:
            case 5:
            case 6:
               var5 -= 4;
               break;
            default:
               return this;
         }

         return (new BitFunction(var4.left, var4.right, var5)).optimize(var1);
      } else if (this.left instanceof Aggregate) {
         Aggregate var2 = (Aggregate)this.left;
         AggregateType var3;
         switch (var2.getAggregateType()) {
            case BIT_AND_AGG:
               var3 = AggregateType.BIT_NAND_AGG;
               break;
            case BIT_OR_AGG:
               var3 = AggregateType.BIT_NOR_AGG;
               break;
            case BIT_XOR_AGG:
               var3 = AggregateType.BIT_XNOR_AGG;
               break;
            case BIT_NAND_AGG:
               var3 = AggregateType.BIT_AND_AGG;
               break;
            case BIT_NOR_AGG:
               var3 = AggregateType.BIT_OR_AGG;
               break;
            case BIT_XNOR_AGG:
               var3 = AggregateType.BIT_XOR_AGG;
               break;
            default:
               return this;
         }

         return (new Aggregate(var3, new Expression[]{var2.getSubexpression(0)}, var2.getSelect(), var2.isDistinct())).optimize(var1);
      } else {
         return this;
      }
   }

   private static TypeInfo getCommonType(Expression var0, Expression var1) {
      TypeInfo var2 = checkArgType(var0);
      TypeInfo var3 = checkArgType(var1);
      int var4 = var2.getValueType();
      int var5 = var3.getValueType();
      boolean var6 = DataType.isBinaryStringType(var4);
      if (var6 != DataType.isBinaryStringType(var5)) {
         throw DbException.getInvalidValueException("bit function parameters", var3.getSQL(var2.getSQL(new StringBuilder(), 3).append(" vs "), 3).toString());
      } else if (!var6) {
         return TypeInfo.getTypeInfo(Math.max(var4, var5));
      } else {
         long var7;
         if (var4 == 5) {
            var7 = var2.getDeclaredPrecision();
            if (var5 == 5) {
               var7 = Math.max(var7, var3.getDeclaredPrecision());
            }
         } else if (var5 == 5) {
            var4 = 5;
            var7 = var3.getDeclaredPrecision();
         } else {
            long var9 = var2.getDeclaredPrecision();
            long var11 = var3.getDeclaredPrecision();
            var7 = var9 > 0L && var11 > 0L ? Math.max(var9, var11) : -1L;
         }

         return TypeInfo.getTypeInfo(var4, var7, 0, (ExtTypeInfo)null);
      }
   }

   public static TypeInfo checkArgType(Expression var0) {
      TypeInfo var1 = var0.getType();
      switch (var1.getValueType()) {
         case 0:
         case 5:
         case 6:
         case 9:
         case 10:
         case 11:
         case 12:
            return var1;
         case 1:
         case 2:
         case 3:
         case 4:
         case 7:
         case 8:
         default:
            throw DbException.getInvalidExpressionTypeException("bit function argument", var0);
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
