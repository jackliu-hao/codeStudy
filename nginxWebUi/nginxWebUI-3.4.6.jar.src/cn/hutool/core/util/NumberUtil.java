/*      */ package cn.hutool.core.util;
/*      */ 
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.math.Calculator;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.math.RoundingMode;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.NumberFormat;
/*      */ import java.text.ParseException;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class NumberUtil
/*      */ {
/*      */   private static final int DEFAULT_DIV_SCALE = 10;
/*   49 */   private static final long[] FACTORIALS = new long[] { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double add(float v1, float v2) {
/*   62 */     return add(new String[] { Float.toString(v1), Float.toString(v2) }).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double add(float v1, double v2) {
/*   73 */     return add(new String[] { Float.toString(v1), Double.toString(v2) }).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double add(double v1, float v2) {
/*   84 */     return add(new String[] { Double.toString(v1), Float.toString(v2) }).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double add(double v1, double v2) {
/*   95 */     return add(new String[] { Double.toString(v1), Double.toString(v2) }).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double add(Double v1, Double v2) {
/*  108 */     return add(v1, v2).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal add(Number v1, Number v2) {
/*  120 */     return add(new Number[] { v1, v2 });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal add(Number... values) {
/*  132 */     if (ArrayUtil.isEmpty(values)) {
/*  133 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  136 */     Number value = values[0];
/*  137 */     BigDecimal result = toBigDecimal(value);
/*  138 */     for (int i = 1; i < values.length; i++) {
/*  139 */       value = values[i];
/*  140 */       if (null != value) {
/*  141 */         result = result.add(toBigDecimal(value));
/*      */       }
/*      */     } 
/*  144 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal add(String... values) {
/*  156 */     if (ArrayUtil.isEmpty(values)) {
/*  157 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  160 */     String value = values[0];
/*  161 */     BigDecimal result = toBigDecimal(value);
/*  162 */     for (int i = 1; i < values.length; i++) {
/*  163 */       value = values[i];
/*  164 */       if (StrUtil.isNotBlank(value)) {
/*  165 */         result = result.add(toBigDecimal(value));
/*      */       }
/*      */     } 
/*  168 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal add(BigDecimal... values) {
/*  180 */     if (ArrayUtil.isEmpty(values)) {
/*  181 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  184 */     BigDecimal value = values[0];
/*  185 */     BigDecimal result = toBigDecimal(value);
/*  186 */     for (int i = 1; i < values.length; i++) {
/*  187 */       value = values[i];
/*  188 */       if (null != value) {
/*  189 */         result = result.add(value);
/*      */       }
/*      */     } 
/*  192 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double sub(float v1, float v2) {
/*  203 */     return sub(new String[] { Float.toString(v1), Float.toString(v2) }).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double sub(float v1, double v2) {
/*  214 */     return sub(new String[] { Float.toString(v1), Double.toString(v2) }).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double sub(double v1, float v2) {
/*  225 */     return sub(new String[] { Double.toString(v1), Float.toString(v2) }).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double sub(double v1, double v2) {
/*  236 */     return sub(new String[] { Double.toString(v1), Double.toString(v2) }).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double sub(Double v1, Double v2) {
/*  248 */     return sub(v1, v2).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal sub(Number v1, Number v2) {
/*  260 */     return sub(new Number[] { v1, v2 });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal sub(Number... values) {
/*  272 */     if (ArrayUtil.isEmpty(values)) {
/*  273 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  276 */     Number value = values[0];
/*  277 */     BigDecimal result = toBigDecimal(value);
/*  278 */     for (int i = 1; i < values.length; i++) {
/*  279 */       value = values[i];
/*  280 */       if (null != value) {
/*  281 */         result = result.subtract(toBigDecimal(value));
/*      */       }
/*      */     } 
/*  284 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal sub(String... values) {
/*  296 */     if (ArrayUtil.isEmpty(values)) {
/*  297 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  300 */     String value = values[0];
/*  301 */     BigDecimal result = toBigDecimal(value);
/*  302 */     for (int i = 1; i < values.length; i++) {
/*  303 */       value = values[i];
/*  304 */       if (StrUtil.isNotBlank(value)) {
/*  305 */         result = result.subtract(toBigDecimal(value));
/*      */       }
/*      */     } 
/*  308 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal sub(BigDecimal... values) {
/*  320 */     if (ArrayUtil.isEmpty(values)) {
/*  321 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  324 */     BigDecimal value = values[0];
/*  325 */     BigDecimal result = toBigDecimal(value);
/*  326 */     for (int i = 1; i < values.length; i++) {
/*  327 */       value = values[i];
/*  328 */       if (null != value) {
/*  329 */         result = result.subtract(value);
/*      */       }
/*      */     } 
/*  332 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double mul(float v1, float v2) {
/*  343 */     return mul(Float.toString(v1), Float.toString(v2)).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double mul(float v1, double v2) {
/*  354 */     return mul(Float.toString(v1), Double.toString(v2)).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double mul(double v1, float v2) {
/*  365 */     return mul(Double.toString(v1), Float.toString(v2)).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double mul(double v1, double v2) {
/*  376 */     return mul(Double.toString(v1), Double.toString(v2)).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double mul(Double v1, Double v2) {
/*  389 */     return mul(v1, v2).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal mul(Number v1, Number v2) {
/*  401 */     return mul(new Number[] { v1, v2 });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal mul(Number... values) {
/*  413 */     if (ArrayUtil.isEmpty(values) || ArrayUtil.hasNull(values)) {
/*  414 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  417 */     Number value = values[0];
/*  418 */     BigDecimal result = new BigDecimal(value.toString());
/*  419 */     for (int i = 1; i < values.length; i++) {
/*  420 */       value = values[i];
/*  421 */       result = result.multiply(new BigDecimal(value.toString()));
/*      */     } 
/*  423 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal mul(String v1, String v2) {
/*  435 */     return mul(new BigDecimal(v1), new BigDecimal(v2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal mul(String... values) {
/*  447 */     if (ArrayUtil.isEmpty(values) || ArrayUtil.hasNull(values)) {
/*  448 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  451 */     BigDecimal result = new BigDecimal(values[0]);
/*  452 */     for (int i = 1; i < values.length; i++) {
/*  453 */       result = result.multiply(new BigDecimal(values[i]));
/*      */     }
/*      */     
/*  456 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal mul(BigDecimal... values) {
/*  468 */     if (ArrayUtil.isEmpty(values) || ArrayUtil.hasNull(values)) {
/*  469 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/*  472 */     BigDecimal result = values[0];
/*  473 */     for (int i = 1; i < values.length; i++) {
/*  474 */       result = result.multiply(values[i]);
/*      */     }
/*  476 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(float v1, float v2) {
/*  487 */     return div(v1, v2, 10);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(float v1, double v2) {
/*  498 */     return div(v1, v2, 10);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(double v1, float v2) {
/*  509 */     return div(v1, v2, 10);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(double v1, double v2) {
/*  520 */     return div(v1, v2, 10);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(Double v1, Double v2) {
/*  531 */     return div(v1, v2, 10);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal div(Number v1, Number v2) {
/*  543 */     return div(v1, v2, 10);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal div(String v1, String v2) {
/*  554 */     return div(v1, v2, 10);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(float v1, float v2, int scale) {
/*  566 */     return div(v1, v2, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(float v1, double v2, int scale) {
/*  578 */     return div(v1, v2, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(double v1, float v2, int scale) {
/*  590 */     return div(v1, v2, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(double v1, double v2, int scale) {
/*  602 */     return div(v1, v2, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(Double v1, Double v2, int scale) {
/*  614 */     return div(v1, v2, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal div(Number v1, Number v2, int scale) {
/*  627 */     return div(v1, v2, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal div(String v1, String v2, int scale) {
/*  639 */     return div(v1, v2, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(float v1, float v2, int scale, RoundingMode roundingMode) {
/*  652 */     return div(Float.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(float v1, double v2, int scale, RoundingMode roundingMode) {
/*  665 */     return div(Float.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(double v1, float v2, int scale, RoundingMode roundingMode) {
/*  678 */     return div(Double.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(double v1, double v2, int scale, RoundingMode roundingMode) {
/*  691 */     return div(Double.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double div(Double v1, Double v2, int scale, RoundingMode roundingMode) {
/*  705 */     return div(v1, v2, scale, roundingMode).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal div(Number v1, Number v2, int scale, RoundingMode roundingMode) {
/*  719 */     if (v1 instanceof BigDecimal && v2 instanceof BigDecimal) {
/*  720 */       return div((BigDecimal)v1, (BigDecimal)v2, scale, roundingMode);
/*      */     }
/*  722 */     return div(StrUtil.toStringOrNull(v1), StrUtil.toStringOrNull(v2), scale, roundingMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal div(String v1, String v2, int scale, RoundingMode roundingMode) {
/*  735 */     return div(toBigDecimal(v1), toBigDecimal(v2), scale, roundingMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale, RoundingMode roundingMode) {
/*  749 */     Assert.notNull(v2, "Divisor must be not null !", new Object[0]);
/*  750 */     if (null == v1) {
/*  751 */       return BigDecimal.ZERO;
/*      */     }
/*  753 */     if (scale < 0) {
/*  754 */       scale = -scale;
/*      */     }
/*  756 */     return v1.divide(v2, scale, roundingMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int ceilDiv(int v1, int v2) {
/*  768 */     return (int)Math.ceil(v1 / v2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal round(double v, int scale) {
/*  783 */     return round(v, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String roundStr(double v, int scale) {
/*  796 */     return round(v, scale).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal round(String numberStr, int scale) {
/*  809 */     return round(numberStr, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal round(BigDecimal number, int scale) {
/*  823 */     return round(number, scale, RoundingMode.HALF_UP);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String roundStr(String numberStr, int scale) {
/*  837 */     return round(numberStr, scale).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal round(double v, int scale, RoundingMode roundingMode) {
/*  850 */     return round(Double.toString(v), scale, roundingMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String roundStr(double v, int scale, RoundingMode roundingMode) {
/*  864 */     return round(v, scale, roundingMode).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal round(String numberStr, int scale, RoundingMode roundingMode) {
/*  877 */     Assert.notBlank(numberStr);
/*  878 */     if (scale < 0) {
/*  879 */       scale = 0;
/*      */     }
/*  881 */     return round(toBigDecimal(numberStr), scale, roundingMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal round(BigDecimal number, int scale, RoundingMode roundingMode) {
/*  894 */     if (null == number) {
/*  895 */       number = BigDecimal.ZERO;
/*      */     }
/*  897 */     if (scale < 0) {
/*  898 */       scale = 0;
/*      */     }
/*  900 */     if (null == roundingMode) {
/*  901 */       roundingMode = RoundingMode.HALF_UP;
/*      */     }
/*      */     
/*  904 */     return number.setScale(scale, roundingMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String roundStr(String numberStr, int scale, RoundingMode roundingMode) {
/*  918 */     return round(numberStr, scale, roundingMode).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal roundHalfEven(Number number, int scale) {
/*  942 */     return roundHalfEven(toBigDecimal(number), scale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal roundHalfEven(BigDecimal value, int scale) {
/*  966 */     return round(value, scale, RoundingMode.HALF_EVEN);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal roundDown(Number number, int scale) {
/*  978 */     return roundDown(toBigDecimal(number), scale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal roundDown(BigDecimal value, int scale) {
/*  990 */     return round(value, scale, RoundingMode.DOWN);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String decimalFormat(String pattern, double value) {
/* 1014 */     Assert.isTrue(isValid(value), "value is NaN or Infinite!", new Object[0]);
/* 1015 */     return (new DecimalFormat(pattern)).format(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String decimalFormat(String pattern, long value) {
/* 1038 */     return (new DecimalFormat(pattern)).format(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String decimalFormat(String pattern, Object value) {
/* 1061 */     return decimalFormat(pattern, value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String decimalFormat(String pattern, Object value, RoundingMode roundingMode) {
/* 1085 */     if (value instanceof Number) {
/* 1086 */       Assert.isTrue(isValidNumber((Number)value), "value is NaN or Infinite!", new Object[0]);
/*      */     }
/* 1088 */     DecimalFormat decimalFormat = new DecimalFormat(pattern);
/* 1089 */     if (null != roundingMode) {
/* 1090 */       decimalFormat.setRoundingMode(roundingMode);
/*      */     }
/* 1092 */     return decimalFormat.format(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String decimalFormatMoney(double value) {
/* 1103 */     return decimalFormat(",##0.00", value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatPercent(double number, int scale) {
/* 1115 */     NumberFormat format = NumberFormat.getPercentInstance();
/* 1116 */     format.setMaximumFractionDigits(scale);
/* 1117 */     return format.format(number);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNumber(CharSequence str) {
/* 1137 */     if (StrUtil.isBlank(str)) {
/* 1138 */       return false;
/*      */     }
/* 1140 */     char[] chars = str.toString().toCharArray();
/* 1141 */     int sz = chars.length;
/* 1142 */     boolean hasExp = false;
/* 1143 */     boolean hasDecPoint = false;
/* 1144 */     boolean allowSigns = false;
/* 1145 */     boolean foundDigit = false;
/*      */     
/* 1147 */     int start = (chars[0] == '-' || chars[0] == '+') ? 1 : 0;
/* 1148 */     if (sz > start + 1 && 
/* 1149 */       chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
/* 1150 */       int j = start + 2;
/* 1151 */       if (j == sz) {
/* 1152 */         return false;
/*      */       }
/*      */       
/* 1155 */       for (; j < chars.length; j++) {
/* 1156 */         if ((chars[j] < '0' || chars[j] > '9') && (chars[j] < 'a' || chars[j] > 'f') && (chars[j] < 'A' || chars[j] > 'F')) {
/* 1157 */           return false;
/*      */         }
/*      */       } 
/* 1160 */       return true;
/*      */     } 
/*      */     
/* 1163 */     sz--;
/*      */     
/* 1165 */     int i = start;
/*      */ 
/*      */     
/* 1168 */     while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
/* 1169 */       if (chars[i] >= '0' && chars[i] <= '9') {
/* 1170 */         foundDigit = true;
/* 1171 */         allowSigns = false;
/*      */       }
/* 1173 */       else if (chars[i] == '.') {
/* 1174 */         if (hasDecPoint || hasExp)
/*      */         {
/* 1176 */           return false;
/*      */         }
/* 1178 */         hasDecPoint = true;
/* 1179 */       } else if (chars[i] == 'e' || chars[i] == 'E') {
/*      */         
/* 1181 */         if (hasExp)
/*      */         {
/* 1183 */           return false;
/*      */         }
/* 1185 */         if (false == foundDigit) {
/* 1186 */           return false;
/*      */         }
/* 1188 */         hasExp = true;
/* 1189 */         allowSigns = true;
/* 1190 */       } else if (chars[i] == '+' || chars[i] == '-') {
/* 1191 */         if (!allowSigns) {
/* 1192 */           return false;
/*      */         }
/* 1194 */         allowSigns = false;
/* 1195 */         foundDigit = false;
/*      */       } else {
/* 1197 */         return false;
/*      */       } 
/* 1199 */       i++;
/*      */     } 
/* 1201 */     if (i < chars.length) {
/* 1202 */       if (chars[i] >= '0' && chars[i] <= '9')
/*      */       {
/* 1204 */         return true;
/*      */       }
/* 1206 */       if (chars[i] == 'e' || chars[i] == 'E')
/*      */       {
/* 1208 */         return false;
/*      */       }
/* 1210 */       if (chars[i] == '.') {
/* 1211 */         if (hasDecPoint || hasExp)
/*      */         {
/* 1213 */           return false;
/*      */         }
/*      */         
/* 1216 */         return foundDigit;
/*      */       } 
/* 1218 */       if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
/* 1219 */         return foundDigit;
/*      */       }
/* 1221 */       if (chars[i] == 'l' || chars[i] == 'L')
/*      */       {
/* 1223 */         return (foundDigit && !hasExp);
/*      */       }
/*      */       
/* 1226 */       return false;
/*      */     } 
/*      */ 
/*      */     
/* 1230 */     return (false == allowSigns && foundDigit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isInteger(String s) {
/* 1241 */     if (StrUtil.isBlank(s)) {
/* 1242 */       return false;
/*      */     }
/*      */     try {
/* 1245 */       Integer.parseInt(s);
/* 1246 */     } catch (NumberFormatException e) {
/* 1247 */       return false;
/*      */     } 
/* 1249 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isLong(String s) {
/* 1261 */     if (StrUtil.isBlank(s)) {
/* 1262 */       return false;
/*      */     }
/*      */     try {
/* 1265 */       Long.parseLong(s);
/* 1266 */     } catch (NumberFormatException e) {
/* 1267 */       return false;
/*      */     } 
/* 1269 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isDouble(String s) {
/* 1279 */     if (StrUtil.isBlank(s)) {
/* 1280 */       return false;
/*      */     }
/*      */     try {
/* 1283 */       Double.parseDouble(s);
/* 1284 */       return s.contains(".");
/* 1285 */     } catch (NumberFormatException numberFormatException) {
/*      */ 
/*      */       
/* 1288 */       return true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isPrimes(int n) {
/* 1299 */     Assert.isTrue((n > 1), "The number must be > 1", new Object[0]);
/* 1300 */     for (int i = 2; i <= Math.sqrt(n); i++) {
/* 1301 */       if (n % i == 0) {
/* 1302 */         return false;
/*      */       }
/*      */     } 
/* 1305 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] generateRandomNumber(int begin, int end, int size) {
/* 1320 */     int[] seed = ArrayUtil.range(begin, end);
/* 1321 */     return generateRandomNumber(begin, end, size, seed);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] generateRandomNumber(int begin, int end, int size, int[] seed) {
/* 1335 */     if (begin > end) {
/* 1336 */       int temp = begin;
/* 1337 */       begin = end;
/* 1338 */       end = temp;
/*      */     } 
/*      */     
/* 1341 */     Assert.isTrue((end - begin >= size), "Size is larger than range between begin and end!", new Object[0]);
/* 1342 */     Assert.isTrue((seed.length >= size), "Size is larger than seed size!", new Object[0]);
/*      */     
/* 1344 */     int[] ranArr = new int[size];
/*      */     
/* 1346 */     for (int i = 0; i < size; i++) {
/*      */       
/* 1348 */       int j = RandomUtil.randomInt(seed.length - i);
/*      */       
/* 1350 */       ranArr[i] = seed[j];
/*      */       
/* 1352 */       seed[j] = seed[seed.length - 1 - i];
/*      */     } 
/* 1354 */     return ranArr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Integer[] generateBySet(int begin, int end, int size) {
/* 1366 */     if (begin > end) {
/* 1367 */       int temp = begin;
/* 1368 */       begin = end;
/* 1369 */       end = temp;
/*      */     } 
/*      */     
/* 1372 */     if (end - begin < size) {
/* 1373 */       throw new UtilException("Size is larger than range between begin and end!");
/*      */     }
/*      */     
/* 1376 */     Set<Integer> set = new HashSet<>(size, 1.0F);
/* 1377 */     while (set.size() < size) {
/* 1378 */       set.add(Integer.valueOf(begin + RandomUtil.randomInt(end - begin)));
/*      */     }
/*      */     
/* 1381 */     return set.<Integer>toArray(new Integer[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] range(int stop) {
/* 1394 */     return range(0, stop);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] range(int start, int stop) {
/* 1405 */     return range(start, stop, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] range(int start, int stop, int step) {
/* 1417 */     if (start < stop) {
/* 1418 */       step = Math.abs(step);
/* 1419 */     } else if (start > stop) {
/* 1420 */       step = -Math.abs(step);
/*      */     } else {
/* 1422 */       return new int[] { start };
/*      */     } 
/*      */     
/* 1425 */     int size = Math.abs((stop - start) / step) + 1;
/* 1426 */     int[] values = new int[size];
/* 1427 */     int index = 0; int i;
/* 1428 */     for (i = start; (step > 0) ? (i <= stop) : (i >= stop); i += step) {
/* 1429 */       values[index] = i;
/* 1430 */       index++;
/*      */     } 
/* 1432 */     return values;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Collection<Integer> appendRange(int start, int stop, Collection<Integer> values) {
/* 1444 */     return appendRange(start, stop, 1, values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Collection<Integer> appendRange(int start, int stop, int step, Collection<Integer> values) {
/* 1457 */     if (start < stop) {
/* 1458 */       step = Math.abs(step);
/* 1459 */     } else if (start > stop) {
/* 1460 */       step = -Math.abs(step);
/*      */     } else {
/* 1462 */       values.add(Integer.valueOf(start));
/* 1463 */       return values;
/*      */     } 
/*      */     int i;
/* 1466 */     for (i = start; (step > 0) ? (i <= stop) : (i >= stop); i += step) {
/* 1467 */       values.add(Integer.valueOf(i));
/*      */     }
/* 1469 */     return values;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger factorial(BigInteger n) {
/* 1485 */     if (n.equals(BigInteger.ZERO)) {
/* 1486 */       return BigInteger.ONE;
/*      */     }
/* 1488 */     return factorial(n, BigInteger.ZERO);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger factorial(BigInteger start, BigInteger end) {
/* 1503 */     Assert.notNull(start, "Factorial start must be not null!", new Object[0]);
/* 1504 */     Assert.notNull(end, "Factorial end must be not null!", new Object[0]);
/* 1505 */     if (start.compareTo(BigInteger.ZERO) < 0 || end.compareTo(BigInteger.ZERO) < 0) {
/* 1506 */       throw new IllegalArgumentException(StrUtil.format("Factorial start and end both must be > 0, but got start={}, end={}", new Object[] { start, end }));
/*      */     }
/*      */     
/* 1509 */     if (start.equals(BigInteger.ZERO)) {
/* 1510 */       start = BigInteger.ONE;
/*      */     }
/*      */     
/* 1513 */     if (end.compareTo(BigInteger.ONE) < 0) {
/* 1514 */       end = BigInteger.ONE;
/*      */     }
/*      */     
/* 1517 */     BigInteger result = start;
/* 1518 */     end = end.add(BigInteger.ONE);
/* 1519 */     while (start.compareTo(end) > 0) {
/* 1520 */       start = start.subtract(BigInteger.ONE);
/* 1521 */       result = result.multiply(start);
/*      */     } 
/* 1523 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long factorial(long start, long end) {
/* 1539 */     if (start < 0L || end < 0L) {
/* 1540 */       throw new IllegalArgumentException(StrUtil.format("Factorial start and end both must be >= 0, but got start={}, end={}", new Object[] { Long.valueOf(start), Long.valueOf(end) }));
/*      */     }
/* 1542 */     if (0L == start || start == end) {
/* 1543 */       return 1L;
/*      */     }
/* 1545 */     if (start < end) {
/* 1546 */       return 0L;
/*      */     }
/* 1548 */     return factorialMultiplyAndCheck(start, factorial(start - 1L, end));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static long factorialMultiplyAndCheck(long a, long b) {
/* 1559 */     if (a <= Long.MAX_VALUE / b) {
/* 1560 */       return a * b;
/*      */     }
/* 1562 */     throw new IllegalArgumentException(StrUtil.format("Overflow in multiplication: {} * {}", new Object[] { Long.valueOf(a), Long.valueOf(b) }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long factorial(long n) {
/* 1575 */     if (n < 0L || n > 20L) {
/* 1576 */       throw new IllegalArgumentException(StrUtil.format("Factorial must have n >= 0 and n <= 20 for n!, but got n = {}", new Object[] { Long.valueOf(n) }));
/*      */     }
/* 1578 */     return FACTORIALS[(int)n];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long sqrt(long x) {
/* 1589 */     long y = 0L;
/* 1590 */     long b = 4611686018427387904L;
/* 1591 */     while (b > 0L) {
/* 1592 */       if (x >= y + b) {
/* 1593 */         x -= y + b;
/* 1594 */         y >>= 1L;
/* 1595 */         y += b;
/*      */       } else {
/* 1597 */         y >>= 1L;
/*      */       } 
/* 1599 */       b >>= 2L;
/*      */     } 
/* 1601 */     return y;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int processMultiple(int selectNum, int minNum) {
/* 1614 */     int result = mathSubNode(selectNum, minNum) / mathNode(selectNum - minNum);
/* 1615 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int divisor(int m, int n) {
/* 1626 */     while (m % n != 0) {
/* 1627 */       int temp = m % n;
/* 1628 */       m = n;
/* 1629 */       n = temp;
/*      */     } 
/* 1631 */     return n;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int multiple(int m, int n) {
/* 1642 */     return m * n / divisor(m, n);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getBinaryStr(Number number) {
/* 1652 */     if (number instanceof Long)
/* 1653 */       return Long.toBinaryString(((Long)number).longValue()); 
/* 1654 */     if (number instanceof Integer) {
/* 1655 */       return Integer.toBinaryString(((Integer)number).intValue());
/*      */     }
/* 1657 */     return Long.toBinaryString(number.longValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int binaryToInt(String binaryStr) {
/* 1668 */     return Integer.parseInt(binaryStr, 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long binaryToLong(String binaryStr) {
/* 1678 */     return Long.parseLong(binaryStr, 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compare(char x, char y) {
/* 1693 */     return Character.compare(x, y);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compare(double x, double y) {
/* 1706 */     return Double.compare(x, y);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compare(int x, int y) {
/* 1719 */     return Integer.compare(x, y);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compare(long x, long y) {
/* 1732 */     return Long.compare(x, y);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compare(short x, short y) {
/* 1745 */     return Short.compare(x, y);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compare(byte x, byte y) {
/* 1758 */     return Byte.compare(x, y);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGreater(BigDecimal bigNum1, BigDecimal bigNum2) {
/* 1770 */     Assert.notNull(bigNum1);
/* 1771 */     Assert.notNull(bigNum2);
/* 1772 */     return (bigNum1.compareTo(bigNum2) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGreaterOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
/* 1784 */     Assert.notNull(bigNum1);
/* 1785 */     Assert.notNull(bigNum2);
/* 1786 */     return (bigNum1.compareTo(bigNum2) >= 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isLess(BigDecimal bigNum1, BigDecimal bigNum2) {
/* 1798 */     Assert.notNull(bigNum1);
/* 1799 */     Assert.notNull(bigNum2);
/* 1800 */     return (bigNum1.compareTo(bigNum2) < 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isLessOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
/* 1812 */     Assert.notNull(bigNum1);
/* 1813 */     Assert.notNull(bigNum2);
/* 1814 */     return (bigNum1.compareTo(bigNum2) <= 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equals(double num1, double num2) {
/* 1828 */     return (Double.doubleToLongBits(num1) == Double.doubleToLongBits(num2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equals(float num1, float num2) {
/* 1842 */     return (Float.floatToIntBits(num1) == Float.floatToIntBits(num2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equals(long num1, long num2) {
/* 1855 */     return (num1 == num2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
/* 1869 */     if (bigNum1 == bigNum2)
/*      */     {
/* 1871 */       return true;
/*      */     }
/* 1873 */     if (bigNum1 == null || bigNum2 == null) {
/* 1874 */       return false;
/*      */     }
/* 1876 */     return (0 == bigNum1.compareTo(bigNum2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equals(char c1, char c2, boolean ignoreCase) {
/* 1890 */     return CharUtil.equals(c1, c2, ignoreCase);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Comparable<? super T>> T min(T[] numberArray) {
/* 1903 */     return ArrayUtil.min(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long min(long... numberArray) {
/* 1915 */     return ArrayUtil.min(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int min(int... numberArray) {
/* 1927 */     return ArrayUtil.min(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short min(short... numberArray) {
/* 1939 */     return ArrayUtil.min(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double min(double... numberArray) {
/* 1951 */     return ArrayUtil.min(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static float min(float... numberArray) {
/* 1963 */     return ArrayUtil.min(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal min(BigDecimal... numberArray) {
/* 1975 */     return ArrayUtil.<BigDecimal>min(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Comparable<? super T>> T max(T[] numberArray) {
/* 1988 */     return ArrayUtil.max(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long max(long... numberArray) {
/* 2000 */     return ArrayUtil.max(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int max(int... numberArray) {
/* 2012 */     return ArrayUtil.max(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short max(short... numberArray) {
/* 2024 */     return ArrayUtil.max(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double max(double... numberArray) {
/* 2036 */     return ArrayUtil.max(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static float max(float... numberArray) {
/* 2048 */     return ArrayUtil.max(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal max(BigDecimal... numberArray) {
/* 2060 */     return ArrayUtil.<BigDecimal>max(numberArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Number number, String defaultValue) {
/* 2073 */     return (null == number) ? defaultValue : toStr(number);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Number number) {
/* 2084 */     return toStr(number, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Number number, boolean isStripTrailingZeros) {
/* 2096 */     Assert.notNull(number, "Number is null !", new Object[0]);
/*      */ 
/*      */     
/* 2099 */     if (number instanceof BigDecimal) {
/* 2100 */       return toStr((BigDecimal)number, isStripTrailingZeros);
/*      */     }
/*      */     
/* 2103 */     Assert.isTrue(isValidNumber(number), "Number is non-finite!", new Object[0]);
/*      */     
/* 2105 */     String string = number.toString();
/* 2106 */     if (isStripTrailingZeros && 
/* 2107 */       string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
/* 2108 */       while (string.endsWith("0")) {
/* 2109 */         string = string.substring(0, string.length() - 1);
/*      */       }
/* 2111 */       if (string.endsWith(".")) {
/* 2112 */         string = string.substring(0, string.length() - 1);
/*      */       }
/*      */     } 
/*      */     
/* 2116 */     return string;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(BigDecimal bigDecimal) {
/* 2128 */     return toStr(bigDecimal, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(BigDecimal bigDecimal, boolean isStripTrailingZeros) {
/* 2141 */     Assert.notNull(bigDecimal, "BigDecimal is null !", new Object[0]);
/* 2142 */     if (isStripTrailingZeros) {
/* 2143 */       bigDecimal = bigDecimal.stripTrailingZeros();
/*      */     }
/* 2145 */     return bigDecimal.toPlainString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal toBigDecimal(Number number) {
/* 2158 */     if (null == number) {
/* 2159 */       return BigDecimal.ZERO;
/*      */     }
/*      */     
/* 2162 */     if (number instanceof BigDecimal)
/* 2163 */       return (BigDecimal)number; 
/* 2164 */     if (number instanceof Long)
/* 2165 */       return new BigDecimal(((Long)number).longValue()); 
/* 2166 */     if (number instanceof Integer)
/* 2167 */       return new BigDecimal(((Integer)number).intValue()); 
/* 2168 */     if (number instanceof BigInteger) {
/* 2169 */       return new BigDecimal((BigInteger)number);
/*      */     }
/*      */ 
/*      */     
/* 2173 */     return toBigDecimal(number.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal toBigDecimal(String numberStr) {
/* 2185 */     if (StrUtil.isBlank(numberStr)) {
/* 2186 */       return BigDecimal.ZERO;
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 2191 */       Number number = parseNumber(numberStr);
/* 2192 */       if (number instanceof BigDecimal) {
/* 2193 */         return (BigDecimal)number;
/*      */       }
/* 2195 */       return new BigDecimal(number.toString());
/*      */     }
/* 2197 */     catch (Exception exception) {
/*      */ 
/*      */ 
/*      */       
/* 2201 */       return new BigDecimal(numberStr);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger toBigInteger(Number number) {
/* 2213 */     if (null == number) {
/* 2214 */       return BigInteger.ZERO;
/*      */     }
/*      */     
/* 2217 */     if (number instanceof BigInteger)
/* 2218 */       return (BigInteger)number; 
/* 2219 */     if (number instanceof Long) {
/* 2220 */       return BigInteger.valueOf(((Long)number).longValue());
/*      */     }
/*      */     
/* 2223 */     return toBigInteger(Long.valueOf(number.longValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger toBigInteger(String number) {
/* 2235 */     return StrUtil.isBlank(number) ? BigInteger.ZERO : new BigInteger(number);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int count(int total, int part) {
/* 2247 */     return (total % part == 0) ? (total / part) : (total / part + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal null2Zero(BigDecimal decimal) {
/* 2259 */     return (decimal == null) ? BigDecimal.ZERO : decimal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int zero2One(int value) {
/* 2270 */     return (0 == value) ? 1 : value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger newBigInteger(String str) {
/* 2282 */     str = StrUtil.trimToNull(str);
/* 2283 */     if (null == str) {
/* 2284 */       return null;
/*      */     }
/*      */     
/* 2287 */     int pos = 0;
/* 2288 */     int radix = 10;
/* 2289 */     boolean negate = false;
/* 2290 */     if (str.startsWith("-")) {
/* 2291 */       negate = true;
/* 2292 */       pos = 1;
/*      */     } 
/* 2294 */     if (str.startsWith("0x", pos) || str.startsWith("0X", pos)) {
/*      */       
/* 2296 */       radix = 16;
/* 2297 */       pos += 2;
/* 2298 */     } else if (str.startsWith("#", pos)) {
/*      */       
/* 2300 */       radix = 16;
/* 2301 */       pos++;
/* 2302 */     } else if (str.startsWith("0", pos) && str.length() > pos + 1) {
/*      */       
/* 2304 */       radix = 8;
/* 2305 */       pos++;
/*      */     } 
/*      */     
/* 2308 */     if (pos > 0) {
/* 2309 */       str = str.substring(pos);
/*      */     }
/* 2311 */     BigInteger value = new BigInteger(str, radix);
/* 2312 */     return negate ? value.negate() : value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isBeside(long number1, long number2) {
/* 2325 */     return (Math.abs(number1 - number2) == 1L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isBeside(int number1, int number2) {
/* 2338 */     return (Math.abs(number1 - number2) == 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int partValue(int total, int partCount) {
/* 2351 */     return partValue(total, partCount, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int partValue(int total, int partCount, boolean isPlusOneWhenHasRem) {
/* 2365 */     int partValue = total / partCount;
/* 2366 */     if (isPlusOneWhenHasRem && total % partCount > 0) {
/* 2367 */       partValue++;
/*      */     }
/* 2369 */     return partValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal pow(Number number, int n) {
/* 2381 */     return pow(toBigDecimal(number), n);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal pow(BigDecimal number, int n) {
/* 2393 */     return number.pow(n);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isPowerOfTwo(long n) {
/* 2404 */     return (n > 0L && (n & n - 1L) == 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int parseInt(String number) throws NumberFormatException {
/* 2425 */     if (StrUtil.isBlank(number)) {
/* 2426 */       return 0;
/*      */     }
/*      */     
/* 2429 */     if (StrUtil.startWithIgnoreCase(number, "0x"))
/*      */     {
/* 2431 */       return Integer.parseInt(number.substring(2), 16);
/*      */     }
/*      */     
/*      */     try {
/* 2435 */       return Integer.parseInt(number);
/* 2436 */     } catch (NumberFormatException e) {
/* 2437 */       return parseNumber(number).intValue();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long parseLong(String number) {
/* 2458 */     if (StrUtil.isBlank(number)) {
/* 2459 */       return 0L;
/*      */     }
/*      */     
/* 2462 */     if (number.startsWith("0x"))
/*      */     {
/* 2464 */       return Long.parseLong(number.substring(2), 16);
/*      */     }
/*      */     
/*      */     try {
/* 2468 */       return Long.parseLong(number);
/* 2469 */     } catch (NumberFormatException e) {
/* 2470 */       return parseNumber(number).longValue();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static float parseFloat(String number) {
/* 2489 */     if (StrUtil.isBlank(number)) {
/* 2490 */       return 0.0F;
/*      */     }
/*      */     
/*      */     try {
/* 2494 */       return Float.parseFloat(number);
/* 2495 */     } catch (NumberFormatException e) {
/* 2496 */       return parseNumber(number).floatValue();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double parseDouble(String number) {
/* 2515 */     if (StrUtil.isBlank(number)) {
/* 2516 */       return 0.0D;
/*      */     }
/*      */     
/*      */     try {
/* 2520 */       return Double.parseDouble(number);
/* 2521 */     } catch (NumberFormatException e) {
/* 2522 */       return parseNumber(number).doubleValue();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Number parseNumber(String numberStr) throws NumberFormatException {
/* 2536 */     if (StrUtil.startWithIgnoreCase(numberStr, "0x"))
/*      */     {
/* 2538 */       return Long.valueOf(Long.parseLong(numberStr.substring(2), 16));
/*      */     }
/*      */     
/*      */     try {
/* 2542 */       NumberFormat format = NumberFormat.getInstance();
/* 2543 */       if (format instanceof DecimalFormat)
/*      */       {
/*      */         
/* 2546 */         ((DecimalFormat)format).setParseBigDecimal(true);
/*      */       }
/* 2548 */       return format.parse(numberStr);
/* 2549 */     } catch (ParseException e) {
/* 2550 */       NumberFormatException nfe = new NumberFormatException(e.getMessage());
/* 2551 */       nfe.initCause(e);
/* 2552 */       throw nfe;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] toBytes(int value) {
/* 2565 */     byte[] result = new byte[4];
/*      */     
/* 2567 */     result[0] = (byte)(value >> 24);
/* 2568 */     result[1] = (byte)(value >> 16);
/* 2569 */     result[2] = (byte)(value >> 8);
/* 2570 */     result[3] = (byte)value;
/*      */     
/* 2572 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int toInt(byte[] bytes) {
/* 2584 */     return (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | bytes[3] & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] toUnsignedByteArray(BigInteger value) {
/* 2598 */     byte[] bytes = value.toByteArray();
/*      */     
/* 2600 */     if (bytes[0] == 0) {
/* 2601 */       byte[] tmp = new byte[bytes.length - 1];
/* 2602 */       System.arraycopy(bytes, 1, tmp, 0, tmp.length);
/*      */       
/* 2604 */       return tmp;
/*      */     } 
/*      */     
/* 2607 */     return bytes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] toUnsignedByteArray(int length, BigInteger value) {
/* 2619 */     byte[] bytes = value.toByteArray();
/* 2620 */     if (bytes.length == length) {
/* 2621 */       return bytes;
/*      */     }
/*      */     
/* 2624 */     int start = (bytes[0] == 0) ? 1 : 0;
/* 2625 */     int count = bytes.length - start;
/*      */     
/* 2627 */     if (count > length) {
/* 2628 */       throw new IllegalArgumentException("standard length exceeded for value");
/*      */     }
/*      */     
/* 2631 */     byte[] tmp = new byte[length];
/* 2632 */     System.arraycopy(bytes, start, tmp, tmp.length - count, count);
/* 2633 */     return tmp;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger fromUnsignedByteArray(byte[] buf) {
/* 2644 */     return new BigInteger(1, buf);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger fromUnsignedByteArray(byte[] buf, int off, int length) {
/* 2656 */     byte[] mag = buf;
/* 2657 */     if (off != 0 || length != buf.length) {
/* 2658 */       mag = new byte[length];
/* 2659 */       System.arraycopy(buf, off, mag, 0, length);
/*      */     } 
/* 2661 */     return new BigInteger(1, mag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValidNumber(Number number) {
/* 2674 */     if (number instanceof Double)
/* 2675 */       return (false == ((Double)number).isInfinite() && false == ((Double)number).isNaN()); 
/* 2676 */     if (number instanceof Float) {
/* 2677 */       return (false == ((Float)number).isInfinite() && false == ((Float)number).isNaN());
/*      */     }
/* 2679 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValid(double number) {
/* 2691 */     return (false == ((Double.isNaN(number) || Double.isInfinite(number)) ? true : false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValid(float number) {
/* 2703 */     return (false == ((Float.isNaN(number) || Float.isInfinite(number)) ? true : false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double calculate(String expression) {
/* 2718 */     return Calculator.conversion(expression);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double toDouble(Number value) {
/* 2730 */     if (value instanceof Float) {
/* 2731 */       return Double.parseDouble(value.toString());
/*      */     }
/* 2733 */     return value.doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isOdd(int num) {
/* 2746 */     return ((num & 0x1) == 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEven(int num) {
/* 2758 */     return (false == isOdd(num));
/*      */   }
/*      */ 
/*      */   
/*      */   private static int mathSubNode(int selectNum, int minNum) {
/* 2763 */     if (selectNum == minNum) {
/* 2764 */       return 1;
/*      */     }
/* 2766 */     return selectNum * mathSubNode(selectNum - 1, minNum);
/*      */   }
/*      */ 
/*      */   
/*      */   private static int mathNode(int selectNum) {
/* 2771 */     if (selectNum == 0) {
/* 2772 */       return 1;
/*      */     }
/* 2774 */     return selectNum * mathNode(selectNum - 1);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\NumberUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */