/*     */ package org.noear.solon.core.util;
/*     */ 
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Array;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.text.ParseException;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.util.Date;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConvertUtil
/*     */ {
/*     */   public static Object to(AnnotatedElement element, Class<?> type, String key, String val, Context ctx) throws ClassCastException {
/*  33 */     if (String.class == type) {
/*  34 */       return val;
/*     */     }
/*     */     
/*  37 */     if (val.length() == 0) {
/*  38 */       return null;
/*     */     }
/*     */     
/*  41 */     Object rst = null;
/*     */     
/*  43 */     if (rst == null && Date.class == type) {
/*     */       try {
/*  45 */         rst = DateAnalyzer.getGlobal().parse(val);
/*  46 */       } catch (ParseException e) {
/*  47 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */     
/*  51 */     if (rst == null && type.isArray()) {
/*  52 */       String[] ary = null;
/*  53 */       if (ctx == null) {
/*  54 */         ary = val.split(",");
/*     */       } else {
/*  56 */         ary = ctx.paramValues(key);
/*  57 */         if (ary == null || ary.length == 1)
/*     */         {
/*  59 */           ary = val.split(",");
/*     */         }
/*     */       } 
/*     */       
/*  63 */       rst = tryToArray(ary, type);
/*     */     } 
/*     */     
/*  66 */     if (rst == null) {
/*  67 */       rst = tryTo(type, val);
/*     */     }
/*     */     
/*  70 */     if (rst == null) {
/*  71 */       throw new ClassCastException("Unsupported type:" + type.getName());
/*     */     }
/*  73 */     return rst;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object to(Class<?> type, String val) throws ClassCastException {
/*  84 */     if (String.class == type) {
/*  85 */       return val;
/*     */     }
/*     */     
/*  88 */     if (val.length() == 0) {
/*  89 */       return null;
/*     */     }
/*     */     
/*  92 */     Object rst = tryTo(type, val);
/*     */     
/*  94 */     if (rst != null) {
/*  95 */       return rst;
/*     */     }
/*     */     
/*  98 */     if (Date.class == type) {
/*     */       try {
/* 100 */         return DateAnalyzer.getGlobal().parse(val);
/* 101 */       } catch (RuntimeException ex) {
/* 102 */         throw ex;
/* 103 */       } catch (Throwable ex) {
/* 104 */         throw new RuntimeException(ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 109 */     throw new ClassCastException("不支持类型:" + type.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Object tryToArray(String[] ary, Class<?> type) {
/* 115 */     int len = ary.length;
/*     */     
/* 117 */     if (is(String[].class, type))
/* 118 */       return ary; 
/* 119 */     if (is(short[].class, type)) {
/* 120 */       short[] ary2 = new short[len];
/* 121 */       for (int i = 0; i < len; i++) {
/* 122 */         ary2[i] = Short.parseShort(ary[i]);
/*     */       }
/* 124 */       return ary2;
/* 125 */     }  if (is(int[].class, type)) {
/* 126 */       int[] ary2 = new int[len];
/* 127 */       for (int i = 0; i < len; i++) {
/* 128 */         ary2[i] = Integer.parseInt(ary[i]);
/*     */       }
/* 130 */       return ary2;
/* 131 */     }  if (is(long[].class, type)) {
/* 132 */       long[] ary2 = new long[len];
/* 133 */       for (int i = 0; i < len; i++) {
/* 134 */         ary2[i] = Long.parseLong(ary[i]);
/*     */       }
/* 136 */       return ary2;
/* 137 */     }  if (is(float[].class, type)) {
/* 138 */       float[] ary2 = new float[len];
/* 139 */       for (int i = 0; i < len; i++) {
/* 140 */         ary2[i] = Float.parseFloat(ary[i]);
/*     */       }
/* 142 */       return ary2;
/* 143 */     }  if (is(double[].class, type)) {
/* 144 */       double[] ary2 = new double[len];
/* 145 */       for (int i = 0; i < len; i++) {
/* 146 */         ary2[i] = Double.parseDouble(ary[i]);
/*     */       }
/* 148 */       return ary2;
/* 149 */     }  if (is(Object[].class, type)) {
/* 150 */       Class<?> c = type.getComponentType();
/* 151 */       Object[] ary2 = (Object[])Array.newInstance(c, len);
/* 152 */       for (int i = 0; i < len; i++) {
/* 153 */         ary2[i] = tryTo(c, ary[i]);
/*     */       }
/* 155 */       return ary2;
/*     */     } 
/*     */     
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object tryTo(Class<?> type, String val) {
/* 168 */     if (Short.class == type || type == short.class) {
/* 169 */       return Short.valueOf(Short.parseShort(val));
/*     */     }
/*     */     
/* 172 */     if (Integer.class == type || type == int.class) {
/* 173 */       return Integer.valueOf(Integer.parseInt(val));
/*     */     }
/*     */     
/* 176 */     if (Long.class == type || type == long.class) {
/* 177 */       return Long.valueOf(Long.parseLong(val));
/*     */     }
/*     */     
/* 180 */     if (Double.class == type || type == double.class) {
/* 181 */       return Double.valueOf(Double.parseDouble(val));
/*     */     }
/*     */     
/* 184 */     if (Float.class == type || type == float.class) {
/* 185 */       return Float.valueOf(Float.parseFloat(val));
/*     */     }
/*     */     
/* 188 */     if (Boolean.class == type || type == boolean.class) {
/* 189 */       if ("1".equals(val)) {
/* 190 */         return Boolean.valueOf(true);
/*     */       }
/*     */       
/* 193 */       return Boolean.valueOf(Boolean.parseBoolean(val));
/*     */     } 
/*     */     
/* 196 */     if (LocalDate.class == type)
/*     */     {
/* 198 */       return LocalDate.parse(val);
/*     */     }
/*     */     
/* 201 */     if (LocalTime.class == type)
/*     */     {
/* 203 */       return LocalTime.parse(val);
/*     */     }
/*     */     
/* 206 */     if (LocalDateTime.class == type)
/*     */     {
/* 208 */       return LocalDateTime.parse(val);
/*     */     }
/*     */     
/* 211 */     if (BigDecimal.class == type) {
/* 212 */       return new BigDecimal(val);
/*     */     }
/*     */     
/* 215 */     if (BigInteger.class == type) {
/* 216 */       return new BigInteger(val);
/*     */     }
/*     */     
/* 219 */     if (type.isEnum()) {
/* 220 */       return enumOf(type, val);
/*     */     }
/*     */     
/* 223 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T extends Enum<T>> T enumOf(Class<T> enumType, String name) {
/* 230 */     for (Enum enum_ : (Enum[])enumType.getEnumConstants()) {
/* 231 */       if (enum_.name().compareToIgnoreCase(name) == 0) {
/* 232 */         return (T)enum_;
/*     */       }
/*     */     } 
/* 235 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean is(Class<?> s, Class<?> t) {
/* 245 */     return s.isAssignableFrom(t);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\ConvertUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */