/*     */ package org.noear.snack;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.Date;
/*     */ import org.noear.snack.core.Feature;
/*     */ import org.noear.snack.core.utils.DateUtil;
/*     */ import org.noear.snack.exception.SnackException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OValue
/*     */ {
/*     */   protected String _string;
/*     */   protected boolean _bool;
/*     */   protected Date _date;
/*     */   protected Number _number;
/*     */   protected ONode _n;
/*     */   private OValueType _type;
/*     */   
/*     */   public OValue(ONode n) {
/*  28 */     this._type = OValueType.Null;
/*     */     this._n = n;
/*     */   }
/*     */ 
/*     */   
/*     */   public OValueType type() {
/*  34 */     return this._type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(Object val) {
/*  42 */     if (val == null) {
/*  43 */       this._type = OValueType.Null;
/*     */       
/*     */       return;
/*     */     } 
/*  47 */     if (val instanceof String) {
/*  48 */       setString((String)val);
/*     */       
/*     */       return;
/*     */     } 
/*  52 */     if (val instanceof Date) {
/*  53 */       setDate((Date)val);
/*     */       
/*     */       return;
/*     */     } 
/*  57 */     if (val instanceof Number) {
/*  58 */       setNumber((Number)val);
/*     */       
/*     */       return;
/*     */     } 
/*  62 */     if (val instanceof Boolean) {
/*  63 */       setBool(((Boolean)val).booleanValue());
/*     */       
/*     */       return;
/*     */     } 
/*  67 */     throw new SnackException("unsupport type class" + val.getClass().getName());
/*     */   }
/*     */   
/*     */   public void setNull() {
/*  71 */     this._type = OValueType.Null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNumber(Number val) {
/*  76 */     this._type = OValueType.Number;
/*  77 */     this._number = val;
/*     */   }
/*     */   
/*     */   public void setString(String val) {
/*  81 */     this._type = OValueType.String;
/*  82 */     this._string = val;
/*     */   }
/*     */   
/*     */   public void setBool(boolean val) {
/*  86 */     this._type = OValueType.Boolean;
/*  87 */     this._bool = val;
/*     */   }
/*     */   
/*     */   public void setDate(Date val) {
/*  91 */     this._type = OValueType.DateTime;
/*  92 */     this._date = val;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getRaw() {
/*  97 */     switch (this._type) {
/*     */       case String:
/*  99 */         return this._string;
/*     */       case DateTime:
/* 101 */         return this._date;
/*     */       case Boolean:
/* 103 */         return Boolean.valueOf(this._bool);
/*     */       case Number:
/* 105 */         return this._number;
/*     */     } 
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRawString() {
/* 115 */     return this._string;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getRawBoolean() {
/* 122 */     return this._bool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getRawDate() {
/* 129 */     return this._date;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Number getRawNumber() {
/* 136 */     return this._number;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNull() {
/* 142 */     return (this._type == OValueType.Null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getChar() {
/* 149 */     switch (this._type) {
/*     */       case Number:
/* 151 */         return (char)(int)this._number.longValue();
/*     */       case String:
/* 153 */         if (this._string == null || this._string.length() == 0) {
/* 154 */           return Character.MIN_VALUE;
/*     */         }
/* 156 */         return this._string.charAt(0);
/*     */       
/*     */       case Boolean:
/* 159 */         return this._bool ? '1' : '0';
/*     */       case DateTime:
/* 161 */         return Character.MIN_VALUE;
/*     */     } 
/* 163 */     return Character.MIN_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short getShort() {
/* 171 */     return (short)(int)getLong();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getInt() {
/* 178 */     return (int)getLong();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLong() {
/* 185 */     switch (this._type) {
/*     */       case Number:
/* 187 */         return this._number.longValue();
/*     */       case String:
/* 189 */         if (this._string == null || this._string.length() == 0) {
/* 190 */           return 0L;
/*     */         }
/* 192 */         return Long.parseLong(this._string);
/*     */ 
/*     */       
/*     */       case Boolean:
/* 196 */         return this._bool ? 1L : 0L;
/*     */       case DateTime:
/* 198 */         return this._date.getTime();
/*     */     } 
/* 200 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat() {
/* 205 */     return (float)getDouble();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDouble() {
/* 212 */     switch (this._type) {
/*     */       case Number:
/* 214 */         return this._number.doubleValue();
/*     */       case String:
/* 216 */         if (this._string == null || this._string.length() == 0) {
/* 217 */           return 0.0D;
/*     */         }
/* 219 */         return Double.parseDouble(this._string);
/*     */ 
/*     */       
/*     */       case Boolean:
/* 223 */         return this._bool ? 1.0D : 0.0D;
/*     */       case DateTime:
/* 225 */         return this._date.getTime();
/*     */     } 
/* 227 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getString() {
/* 235 */     switch (this._type) {
/*     */       case String:
/* 237 */         return this._string;
/*     */       case Number:
/* 239 */         if (this._number instanceof java.math.BigInteger)
/* 240 */           return this._number.toString(); 
/* 241 */         if (this._number instanceof BigDecimal) {
/* 242 */           return ((BigDecimal)this._number).toPlainString();
/*     */         }
/* 244 */         return String.valueOf(this._number);
/*     */ 
/*     */       
/*     */       case Boolean:
/* 248 */         return String.valueOf(this._bool);
/*     */       case DateTime:
/* 250 */         return String.valueOf(this._date);
/*     */     } 
/* 252 */     if (this._n._o.hasFeature(Feature.StringNullAsEmpty)) {
/* 253 */       return "";
/*     */     }
/* 255 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getBoolean() {
/* 265 */     switch (this._type) {
/*     */       case Boolean:
/* 267 */         return this._bool;
/*     */       case Number:
/* 269 */         return (this._number.intValue() > 0);
/*     */       case String:
/* 271 */         return ("true".equals(this._string) || "True".equals(this._string));
/*     */       case DateTime:
/* 273 */         return false;
/*     */     } 
/* 275 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getDate() {
/* 283 */     switch (this._type) {
/*     */       case DateTime:
/* 285 */         return this._date;
/*     */       case String:
/* 287 */         return parseDate(this._string);
/*     */       case Number:
/* 289 */         if (this._number instanceof Long) {
/* 290 */           return new Date(this._number.longValue());
/*     */         }
/* 292 */         return null;
/*     */     } 
/*     */ 
/*     */     
/* 296 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Date parseDate(String dateString) {
/*     */     try {
/* 305 */       return DateUtil.parse(dateString);
/* 306 */     } catch (ParseException ex) {
/* 307 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 313 */     return getString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 319 */     if (this == o) {
/* 320 */       return true;
/*     */     }
/*     */     
/* 323 */     if (o == null) {
/* 324 */       return isNull();
/*     */     }
/*     */     
/* 327 */     if (o instanceof OValue) {
/* 328 */       OValue o2 = (OValue)o;
/* 329 */       switch (this._type) {
/*     */         case String:
/* 331 */           return this._string.equals(o2._string);
/*     */         case DateTime:
/* 333 */           return this._date.equals(o2._date);
/*     */         case Boolean:
/* 335 */           return (this._bool == o2._bool);
/*     */         case Number:
/* 337 */           if (this._number instanceof java.math.BigInteger)
/* 338 */             return toString().equals(o2.toString()); 
/* 339 */           if (this._number instanceof BigDecimal)
/* 340 */             return toString().equals(o2.toString()); 
/* 341 */           if (this._number instanceof Double || this._number instanceof Float) {
/* 342 */             return (getDouble() == o2.getDouble());
/*     */           }
/* 344 */           return (getLong() == o2.getLong());
/*     */       } 
/*     */ 
/*     */       
/* 348 */       return (isNull() && o2.isNull());
/*     */     } 
/*     */ 
/*     */     
/* 352 */     switch (this._type) {
/*     */       case String:
/* 354 */         return this._string.equals(o);
/*     */       case DateTime:
/* 356 */         return this._date.equals(o);
/*     */       case Boolean:
/* 358 */         if (o instanceof Boolean) {
/* 359 */           return (this._bool == ((Boolean)o).booleanValue());
/*     */         }
/* 361 */         return false;
/*     */ 
/*     */       
/*     */       case Number:
/* 365 */         if (o instanceof Number) {
/* 366 */           Number o2 = (Number)o;
/*     */           
/* 368 */           if (this._number instanceof java.math.BigInteger)
/* 369 */             return toString().equals(o2.toString()); 
/* 370 */           if (this._number instanceof BigDecimal)
/* 371 */             return toString().equals(o2.toString()); 
/* 372 */           if (this._number instanceof Double || this._number instanceof Float) {
/* 373 */             return (this._number.doubleValue() == o2.doubleValue());
/*     */           }
/* 375 */           return (this._number.longValue() == o2.longValue());
/*     */         } 
/*     */         
/* 378 */         return false;
/*     */     } 
/*     */ 
/*     */     
/* 382 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 389 */     switch (this._type) {
/*     */       case String:
/* 391 */         return this._string.hashCode();
/*     */       case DateTime:
/* 393 */         return this._date.hashCode();
/*     */       case Boolean:
/* 395 */         return Boolean.hashCode(this._bool);
/*     */       case Number:
/* 397 */         return this._number.hashCode();
/*     */     } 
/* 399 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\OValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */