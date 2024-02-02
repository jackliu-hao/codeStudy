/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NumberUtil
/*     */ {
/*  32 */   private static final BigDecimal BIG_DECIMAL_INT_MIN = BigDecimal.valueOf(-2147483648L);
/*  33 */   private static final BigDecimal BIG_DECIMAL_INT_MAX = BigDecimal.valueOf(2147483647L);
/*  34 */   private static final BigInteger BIG_INTEGER_INT_MIN = BIG_DECIMAL_INT_MIN.toBigInteger();
/*  35 */   private static final BigInteger BIG_INTEGER_INT_MAX = BIG_DECIMAL_INT_MAX.toBigInteger();
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isInfinite(Number num) {
/*  40 */     if (num instanceof Double)
/*  41 */       return ((Double)num).isInfinite(); 
/*  42 */     if (num instanceof Float)
/*  43 */       return ((Float)num).isInfinite(); 
/*  44 */     if (hasTypeThatIsKnownToNotSupportInfiniteAndNaN(num)) {
/*  45 */       return false;
/*     */     }
/*  47 */     throw new UnsupportedNumberClassException(num.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isNaN(Number num) {
/*  52 */     if (num instanceof Double)
/*  53 */       return ((Double)num).isNaN(); 
/*  54 */     if (num instanceof Float)
/*  55 */       return ((Float)num).isNaN(); 
/*  56 */     if (hasTypeThatIsKnownToNotSupportInfiniteAndNaN(num)) {
/*  57 */       return false;
/*     */     }
/*  59 */     throw new UnsupportedNumberClassException(num.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getSignum(Number num) throws ArithmeticException {
/*  68 */     if (num instanceof Integer) {
/*  69 */       int n = num.intValue();
/*  70 */       return (n > 0) ? 1 : ((n == 0) ? 0 : -1);
/*  71 */     }  if (num instanceof BigDecimal) {
/*  72 */       BigDecimal n = (BigDecimal)num;
/*  73 */       return n.signum();
/*  74 */     }  if (num instanceof Double) {
/*  75 */       double n = num.doubleValue();
/*  76 */       if (n > 0.0D) return 1; 
/*  77 */       if (n == 0.0D) return 0; 
/*  78 */       if (n < 0.0D) return -1; 
/*  79 */       throw new ArithmeticException("The signum of " + n + " is not defined.");
/*  80 */     }  if (num instanceof Float) {
/*  81 */       float n = num.floatValue();
/*  82 */       if (n > 0.0F) return 1; 
/*  83 */       if (n == 0.0F) return 0; 
/*  84 */       if (n < 0.0F) return -1; 
/*  85 */       throw new ArithmeticException("The signum of " + n + " is not defined.");
/*  86 */     }  if (num instanceof Long) {
/*  87 */       long n = num.longValue();
/*  88 */       return (n > 0L) ? 1 : ((n == 0L) ? 0 : -1);
/*  89 */     }  if (num instanceof Short) {
/*  90 */       short n = num.shortValue();
/*  91 */       return (n > 0) ? 1 : ((n == 0) ? 0 : -1);
/*  92 */     }  if (num instanceof Byte) {
/*  93 */       byte n = num.byteValue();
/*  94 */       return (n > 0) ? 1 : ((n == 0) ? 0 : -1);
/*  95 */     }  if (num instanceof BigInteger) {
/*  96 */       BigInteger n = (BigInteger)num;
/*  97 */       return n.signum();
/*     */     } 
/*  99 */     throw new UnsupportedNumberClassException(num.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIntegerBigDecimal(BigDecimal bd) {
/* 111 */     return (bd.scale() <= 0 || bd
/* 112 */       .setScale(0, 1).compareTo(bd) == 0);
/*     */   }
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
/*     */   public static boolean hasTypeThatIsKnownToNotSupportInfiniteAndNaN(Number num) {
/* 125 */     return (num instanceof Integer || num instanceof BigDecimal || num instanceof Long || num instanceof Short || num instanceof Byte || num instanceof BigInteger);
/*     */   }
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
/*     */   public static int toIntExact(Number num) {
/* 138 */     if (num instanceof Integer || num instanceof Short || num instanceof Byte)
/* 139 */       return num.intValue(); 
/* 140 */     if (num instanceof Long) {
/* 141 */       long n = num.longValue();
/* 142 */       int result = (int)n;
/* 143 */       if (n != result) {
/* 144 */         throw newLossyConverionException(num, Integer.class);
/*     */       }
/* 146 */       return result;
/* 147 */     }  if (num instanceof Double || num instanceof Float) {
/* 148 */       double n = num.doubleValue();
/* 149 */       if (n % 1.0D != 0.0D || n < -2.147483648E9D || n > 2.147483647E9D) {
/* 150 */         throw newLossyConverionException(num, Integer.class);
/*     */       }
/* 152 */       return (int)n;
/* 153 */     }  if (num instanceof BigDecimal) {
/*     */       
/* 155 */       BigDecimal n = (BigDecimal)num;
/* 156 */       if (!isIntegerBigDecimal(n) || n
/* 157 */         .compareTo(BIG_DECIMAL_INT_MAX) > 0 || n.compareTo(BIG_DECIMAL_INT_MIN) < 0) {
/* 158 */         throw newLossyConverionException(num, Integer.class);
/*     */       }
/* 160 */       return n.intValue();
/* 161 */     }  if (num instanceof BigInteger) {
/* 162 */       BigInteger n = (BigInteger)num;
/* 163 */       if (n.compareTo(BIG_INTEGER_INT_MAX) > 0 || n.compareTo(BIG_INTEGER_INT_MIN) < 0) {
/* 164 */         throw newLossyConverionException(num, Integer.class);
/*     */       }
/* 166 */       return n.intValue();
/*     */     } 
/* 168 */     throw new UnsupportedNumberClassException(num.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   private static ArithmeticException newLossyConverionException(Number fromValue, Class toType) {
/* 173 */     return new ArithmeticException("Can't convert " + fromValue + " to type " + 
/* 174 */         ClassUtil.getShortClassName(toType) + " without loss.");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\NumberUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */