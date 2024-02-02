package org.noear.snack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import org.noear.snack.core.Feature;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.exception.SnackException;

public class OValue {
   protected String _string;
   protected boolean _bool;
   protected Date _date;
   protected Number _number;
   protected ONode _n;
   private OValueType _type;

   public OValue(ONode n) {
      this._type = OValueType.Null;
      this._n = n;
   }

   public OValueType type() {
      return this._type;
   }

   public void set(Object val) {
      if (val == null) {
         this._type = OValueType.Null;
      } else if (val instanceof String) {
         this.setString((String)val);
      } else if (val instanceof Date) {
         this.setDate((Date)val);
      } else if (val instanceof Number) {
         this.setNumber((Number)val);
      } else if (val instanceof Boolean) {
         this.setBool((Boolean)val);
      } else {
         throw new SnackException("unsupport type class" + val.getClass().getName());
      }
   }

   public void setNull() {
      this._type = OValueType.Null;
   }

   public void setNumber(Number val) {
      this._type = OValueType.Number;
      this._number = val;
   }

   public void setString(String val) {
      this._type = OValueType.String;
      this._string = val;
   }

   public void setBool(boolean val) {
      this._type = OValueType.Boolean;
      this._bool = val;
   }

   public void setDate(Date val) {
      this._type = OValueType.DateTime;
      this._date = val;
   }

   public Object getRaw() {
      switch (this._type) {
         case String:
            return this._string;
         case DateTime:
            return this._date;
         case Boolean:
            return this._bool;
         case Number:
            return this._number;
         default:
            return null;
      }
   }

   public String getRawString() {
      return this._string;
   }

   public boolean getRawBoolean() {
      return this._bool;
   }

   public Date getRawDate() {
      return this._date;
   }

   public Number getRawNumber() {
      return this._number;
   }

   public boolean isNull() {
      return this._type == OValueType.Null;
   }

   public char getChar() {
      switch (this._type) {
         case String:
            if (this._string != null && this._string.length() != 0) {
               return this._string.charAt(0);
            }

            return '\u0000';
         case DateTime:
            return '\u0000';
         case Boolean:
            return (char)(this._bool ? '1' : '0');
         case Number:
            return (char)((int)this._number.longValue());
         default:
            return '\u0000';
      }
   }

   public short getShort() {
      return (short)((int)this.getLong());
   }

   public int getInt() {
      return (int)this.getLong();
   }

   public long getLong() {
      switch (this._type) {
         case String:
            if (this._string != null && this._string.length() != 0) {
               return Long.parseLong(this._string);
            }

            return 0L;
         case DateTime:
            return this._date.getTime();
         case Boolean:
            return this._bool ? 1L : 0L;
         case Number:
            return this._number.longValue();
         default:
            return 0L;
      }
   }

   public float getFloat() {
      return (float)this.getDouble();
   }

   public double getDouble() {
      switch (this._type) {
         case String:
            if (this._string != null && this._string.length() != 0) {
               return Double.parseDouble(this._string);
            }

            return 0.0;
         case DateTime:
            return (double)this._date.getTime();
         case Boolean:
            return this._bool ? 1.0 : 0.0;
         case Number:
            return this._number.doubleValue();
         default:
            return 0.0;
      }
   }

   public String getString() {
      switch (this._type) {
         case String:
            return this._string;
         case DateTime:
            return String.valueOf(this._date);
         case Boolean:
            return String.valueOf(this._bool);
         case Number:
            if (this._number instanceof BigInteger) {
               return this._number.toString();
            } else {
               if (this._number instanceof BigDecimal) {
                  return ((BigDecimal)this._number).toPlainString();
               }

               return String.valueOf(this._number);
            }
         default:
            return this._n._o.hasFeature(Feature.StringNullAsEmpty) ? "" : null;
      }
   }

   public boolean getBoolean() {
      switch (this._type) {
         case String:
            return "true".equals(this._string) || "True".equals(this._string);
         case DateTime:
            return false;
         case Boolean:
            return this._bool;
         case Number:
            return this._number.intValue() > 0;
         default:
            return false;
      }
   }

   public Date getDate() {
      switch (this._type) {
         case String:
            return this.parseDate(this._string);
         case DateTime:
            return this._date;
         case Boolean:
         default:
            return null;
         case Number:
            return this._number instanceof Long ? new Date(this._number.longValue()) : null;
      }
   }

   private Date parseDate(String dateString) {
      try {
         return DateUtil.parse(dateString);
      } catch (ParseException var3) {
         return null;
      }
   }

   public String toString() {
      return this.getString();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o == null) {
         return this.isNull();
      } else if (o instanceof OValue) {
         OValue o2 = (OValue)o;
         switch (this._type) {
            case String:
               return this._string.equals(o2._string);
            case DateTime:
               return this._date.equals(o2._date);
            case Boolean:
               return this._bool == o2._bool;
            case Number:
               if (this._number instanceof BigInteger) {
                  return this.toString().equals(o2.toString());
               } else if (this._number instanceof BigDecimal) {
                  return this.toString().equals(o2.toString());
               } else {
                  if (!(this._number instanceof Double) && !(this._number instanceof Float)) {
                     return this.getLong() == o2.getLong();
                  }

                  return this.getDouble() == o2.getDouble();
               }
            default:
               return this.isNull() && o2.isNull();
         }
      } else {
         switch (this._type) {
            case String:
               return this._string.equals(o);
            case DateTime:
               return this._date.equals(o);
            case Boolean:
               if (o instanceof Boolean) {
                  return this._bool == (Boolean)o;
               }

               return false;
            case Number:
               if (o instanceof Number) {
                  Number o2 = (Number)o;
                  if (this._number instanceof BigInteger) {
                     return this.toString().equals(o2.toString());
                  }

                  if (this._number instanceof BigDecimal) {
                     return this.toString().equals(o2.toString());
                  }

                  if (!(this._number instanceof Double) && !(this._number instanceof Float)) {
                     return this._number.longValue() == o2.longValue();
                  }

                  return this._number.doubleValue() == o2.doubleValue();
               }

               return false;
            default:
               return false;
         }
      }
   }

   public int hashCode() {
      switch (this._type) {
         case String:
            return this._string.hashCode();
         case DateTime:
            return this._date.hashCode();
         case Boolean:
            return Boolean.hashCode(this._bool);
         case Number:
            return this._number.hashCode();
         default:
            return 0;
      }
   }
}
