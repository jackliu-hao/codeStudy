package org.h2.value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ValueDecfloat extends ValueBigDecimalBase {
   public static final ValueDecfloat ZERO;
   public static final ValueDecfloat ONE;
   public static final ValueDecfloat POSITIVE_INFINITY;
   public static final ValueDecfloat NEGATIVE_INFINITY;
   public static final ValueDecfloat NAN;

   private ValueDecfloat(BigDecimal var1) {
      super(var1);
   }

   public String getString() {
      if (this.value == null) {
         if (this == POSITIVE_INFINITY) {
            return "Infinity";
         } else {
            return this == NEGATIVE_INFINITY ? "-Infinity" : "NaN";
         }
      } else {
         return this.value.toString();
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return (var2 & 4) == 0 ? this.getSQL(var1.append("CAST(")).append(" AS DECFLOAT)") : this.getSQL(var1);
   }

   private StringBuilder getSQL(StringBuilder var1) {
      if (this.value != null) {
         return var1.append(this.value);
      } else if (this == POSITIVE_INFINITY) {
         return var1.append("'Infinity'");
      } else {
         return this == NEGATIVE_INFINITY ? var1.append("'-Infinity'") : var1.append("'NaN'");
      }
   }

   public TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         this.type = var1 = new TypeInfo(16, this.value != null ? (long)this.value.precision() : 1L, 0, (ExtTypeInfo)null);
      }

      return var1;
   }

   public int getValueType() {
      return 16;
   }

   public Value add(Value var1) {
      BigDecimal var2 = ((ValueDecfloat)var1).value;
      if (this.value != null) {
         return (Value)(var2 != null ? get(this.value.add(var2)) : var1);
      } else {
         return var2 == null && this != var1 ? NAN : this;
      }
   }

   public Value subtract(Value var1) {
      BigDecimal var2 = ((ValueDecfloat)var1).value;
      if (this.value != null) {
         if (var2 != null) {
            return get(this.value.subtract(var2));
         } else {
            return var1 == POSITIVE_INFINITY ? NEGATIVE_INFINITY : (var1 == NEGATIVE_INFINITY ? POSITIVE_INFINITY : NAN);
         }
      } else if (var2 != null) {
         return this;
      } else {
         if (this == POSITIVE_INFINITY) {
            if (var1 == NEGATIVE_INFINITY) {
               return POSITIVE_INFINITY;
            }
         } else if (this == NEGATIVE_INFINITY && var1 == POSITIVE_INFINITY) {
            return NEGATIVE_INFINITY;
         }

         return NAN;
      }
   }

   public Value negate() {
      if (this.value != null) {
         return get(this.value.negate());
      } else {
         return this == POSITIVE_INFINITY ? NEGATIVE_INFINITY : (this == NEGATIVE_INFINITY ? POSITIVE_INFINITY : NAN);
      }
   }

   public Value multiply(Value var1) {
      BigDecimal var2 = ((ValueDecfloat)var1).value;
      int var3;
      if (this.value != null) {
         if (var2 != null) {
            return get(this.value.multiply(var2));
         }

         if (var1 == POSITIVE_INFINITY) {
            var3 = this.value.signum();
            if (var3 > 0) {
               return POSITIVE_INFINITY;
            }

            if (var3 < 0) {
               return NEGATIVE_INFINITY;
            }
         } else if (var1 == NEGATIVE_INFINITY) {
            var3 = this.value.signum();
            if (var3 > 0) {
               return NEGATIVE_INFINITY;
            }

            if (var3 < 0) {
               return POSITIVE_INFINITY;
            }
         }
      } else if (var2 != null) {
         if (this == POSITIVE_INFINITY) {
            var3 = var2.signum();
            if (var3 > 0) {
               return POSITIVE_INFINITY;
            }

            if (var3 < 0) {
               return NEGATIVE_INFINITY;
            }
         } else if (this == NEGATIVE_INFINITY) {
            var3 = var2.signum();
            if (var3 > 0) {
               return NEGATIVE_INFINITY;
            }

            if (var3 < 0) {
               return POSITIVE_INFINITY;
            }
         }
      } else if (this == POSITIVE_INFINITY) {
         if (var1 == POSITIVE_INFINITY) {
            return POSITIVE_INFINITY;
         }

         if (var1 == NEGATIVE_INFINITY) {
            return NEGATIVE_INFINITY;
         }
      } else if (this == NEGATIVE_INFINITY) {
         if (var1 == POSITIVE_INFINITY) {
            return NEGATIVE_INFINITY;
         }

         if (var1 == NEGATIVE_INFINITY) {
            return POSITIVE_INFINITY;
         }
      }

      return NAN;
   }

   public Value divide(Value var1, TypeInfo var2) {
      BigDecimal var3 = ((ValueDecfloat)var1).value;
      if (var3 != null && var3.signum() == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         if (this.value != null) {
            if (var3 != null) {
               return divide(this.value, var3, var2);
            }

            if (var1 != NAN) {
               return ZERO;
            }
         } else if (var3 != null && this != NAN) {
            return this == POSITIVE_INFINITY == var3.signum() > 0 ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
         }

         return NAN;
      }
   }

   public static ValueDecfloat divide(BigDecimal var0, BigDecimal var1, TypeInfo var2) {
      int var3 = (int)var2.getPrecision();
      BigDecimal var4 = var0.divide(var1, var0.scale() - var0.precision() + var1.precision() - var1.scale() + var3, RoundingMode.HALF_DOWN);
      int var5 = var4.precision();
      if (var5 > var3) {
         var4 = var4.setScale(var4.scale() - var5 + var3, RoundingMode.HALF_UP);
      }

      return get(var4);
   }

   public Value modulus(Value var1) {
      BigDecimal var2 = ((ValueDecfloat)var1).value;
      if (var2 != null && var2.signum() == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         if (this.value != null) {
            if (var2 != null) {
               return get(this.value.remainder(var2));
            }

            if (var1 != NAN) {
               return this;
            }
         }

         return NAN;
      }
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      BigDecimal var4 = ((ValueDecfloat)var1).value;
      if (this.value != null) {
         if (var4 != null) {
            return this.value.compareTo(var4);
         } else {
            return var1 == NEGATIVE_INFINITY ? 1 : -1;
         }
      } else if (var4 != null) {
         return this == NEGATIVE_INFINITY ? -1 : 1;
      } else if (this == var1) {
         return 0;
      } else if (this == NEGATIVE_INFINITY) {
         return -1;
      } else if (var1 == NEGATIVE_INFINITY) {
         return 1;
      } else {
         return this == POSITIVE_INFINITY ? -1 : 1;
      }
   }

   public int getSignum() {
      if (this.value != null) {
         return this.value.signum();
      } else {
         return this == POSITIVE_INFINITY ? 1 : (this == NEGATIVE_INFINITY ? -1 : 0);
      }
   }

   public BigDecimal getBigDecimal() {
      if (this.value != null) {
         return this.value;
      } else {
         throw this.getDataConversionError(13);
      }
   }

   public float getFloat() {
      if (this.value != null) {
         return this.value.floatValue();
      } else if (this == POSITIVE_INFINITY) {
         return Float.POSITIVE_INFINITY;
      } else {
         return this == NEGATIVE_INFINITY ? Float.NEGATIVE_INFINITY : Float.NaN;
      }
   }

   public double getDouble() {
      if (this.value != null) {
         return this.value.doubleValue();
      } else if (this == POSITIVE_INFINITY) {
         return Double.POSITIVE_INFINITY;
      } else {
         return this == NEGATIVE_INFINITY ? Double.NEGATIVE_INFINITY : Double.NaN;
      }
   }

   public int hashCode() {
      return this.value != null ? this.getClass().hashCode() * 31 + this.value.hashCode() : System.identityHashCode(this);
   }

   public boolean equals(Object var1) {
      if (var1 instanceof ValueDecfloat) {
         BigDecimal var2 = ((ValueDecfloat)var1).value;
         if (this.value != null) {
            return this.value.equals(var2);
         }

         if (var2 == null && this == var1) {
            return true;
         }
      }

      return false;
   }

   public int getMemory() {
      return this.value != null ? this.value.precision() + 120 : 32;
   }

   public boolean isFinite() {
      return this.value != null;
   }

   public static ValueDecfloat get(BigDecimal var0) {
      var0 = var0.stripTrailingZeros();
      if (BigDecimal.ZERO.equals(var0)) {
         return ZERO;
      } else {
         return BigDecimal.ONE.equals(var0) ? ONE : (ValueDecfloat)Value.cache(new ValueDecfloat(var0));
      }
   }

   static {
      ZERO = new ValueDecfloat(BigDecimal.ZERO);
      ONE = new ValueDecfloat(BigDecimal.ONE);
      POSITIVE_INFINITY = new ValueDecfloat((BigDecimal)null);
      NEGATIVE_INFINITY = new ValueDecfloat((BigDecimal)null);
      NAN = new ValueDecfloat((BigDecimal)null);
   }
}
