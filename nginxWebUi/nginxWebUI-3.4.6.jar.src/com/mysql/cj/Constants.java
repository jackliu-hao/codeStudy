/*    */ package com.mysql.cj;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Constants
/*    */ {
/* 44 */   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 49 */   public static final String MILLIS_I18N = Messages.getString("Milliseconds");
/*    */   
/* 51 */   public static final byte[] SLASH_STAR_SPACE_AS_BYTES = new byte[] { 47, 42, 32 };
/*    */   
/* 53 */   public static final byte[] SPACE_STAR_SLASH_SPACE_AS_BYTES = new byte[] { 32, 42, 47, 32 };
/*    */   
/* 55 */   public static final String JVM_VENDOR = System.getProperty("java.vendor");
/* 56 */   public static final String JVM_VERSION = System.getProperty("java.version");
/*    */   
/* 58 */   public static final String OS_NAME = System.getProperty("os.name");
/* 59 */   public static final String OS_ARCH = System.getProperty("os.arch");
/* 60 */   public static final String OS_VERSION = System.getProperty("os.version");
/* 61 */   public static final String PLATFORM_ENCODING = System.getProperty("file.encoding");
/*    */   
/*    */   public static final String CJ_NAME = "MySQL Connector/J";
/*    */   
/*    */   public static final String CJ_FULL_NAME = "mysql-connector-java-8.0.28";
/*    */   public static final String CJ_REVISION = "7ff2161da3899f379fb3171b6538b191b1c5c7e2";
/*    */   public static final String CJ_VERSION = "8.0.28";
/*    */   public static final String CJ_MAJOR_VERSION = "8";
/*    */   public static final String CJ_MINOR_VERSION = "0";
/*    */   public static final String CJ_LICENSE = "GPL";
/* 71 */   public static final BigInteger BIG_INTEGER_ZERO = BigInteger.valueOf(0L);
/* 72 */   public static final BigInteger BIG_INTEGER_ONE = BigInteger.valueOf(1L);
/* 73 */   public static final BigInteger BIG_INTEGER_NEGATIVE_ONE = BigInteger.valueOf(-1L);
/* 74 */   public static final BigInteger BIG_INTEGER_MIN_BYTE_VALUE = BigInteger.valueOf(-128L);
/* 75 */   public static final BigInteger BIG_INTEGER_MAX_BYTE_VALUE = BigInteger.valueOf(127L);
/* 76 */   public static final BigInteger BIG_INTEGER_MIN_SHORT_VALUE = BigInteger.valueOf(-32768L);
/* 77 */   public static final BigInteger BIG_INTEGER_MAX_SHORT_VALUE = BigInteger.valueOf(32767L);
/* 78 */   public static final BigInteger BIG_INTEGER_MIN_INTEGER_VALUE = BigInteger.valueOf(-2147483648L);
/* 79 */   public static final BigInteger BIG_INTEGER_MAX_INTEGER_VALUE = BigInteger.valueOf(2147483647L);
/* 80 */   public static final BigInteger BIG_INTEGER_MIN_LONG_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
/* 81 */   public static final BigInteger BIG_INTEGER_MAX_LONG_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
/*    */   
/* 83 */   public static final BigDecimal BIG_DECIMAL_ZERO = BigDecimal.valueOf(0L);
/* 84 */   public static final BigDecimal BIG_DECIMAL_ONE = BigDecimal.valueOf(1L);
/* 85 */   public static final BigDecimal BIG_DECIMAL_NEGATIVE_ONE = BigDecimal.valueOf(-1L);
/* 86 */   public static final BigDecimal BIG_DECIMAL_MIN_BYTE_VALUE = BigDecimal.valueOf(-128L);
/* 87 */   public static final BigDecimal BIG_DECIMAL_MAX_BYTE_VALUE = BigDecimal.valueOf(127L);
/* 88 */   public static final BigDecimal BIG_DECIMAL_MIN_SHORT_VALUE = BigDecimal.valueOf(-32768L);
/* 89 */   public static final BigDecimal BIG_DECIMAL_MAX_SHORT_VALUE = BigDecimal.valueOf(32767L);
/* 90 */   public static final BigDecimal BIG_DECIMAL_MIN_INTEGER_VALUE = BigDecimal.valueOf(-2147483648L);
/* 91 */   public static final BigDecimal BIG_DECIMAL_MAX_INTEGER_VALUE = BigDecimal.valueOf(2147483647L);
/* 92 */   public static final BigDecimal BIG_DECIMAL_MIN_LONG_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
/* 93 */   public static final BigDecimal BIG_DECIMAL_MAX_LONG_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);
/* 94 */   public static final BigDecimal BIG_DECIMAL_MAX_DOUBLE_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);
/* 95 */   public static final BigDecimal BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE = BigDecimal.valueOf(-1.7976931348623157E308D);
/* 96 */   public static final BigDecimal BIG_DECIMAL_MAX_FLOAT_VALUE = BigDecimal.valueOf(3.4028234663852886E38D);
/* 97 */   public static final BigDecimal BIG_DECIMAL_MAX_NEGATIVE_FLOAT_VALUE = BigDecimal.valueOf(-3.4028234663852886E38D);
/*    */   public static final int UNSIGNED_BYTE_MAX_VALUE = 255;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\Constants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */