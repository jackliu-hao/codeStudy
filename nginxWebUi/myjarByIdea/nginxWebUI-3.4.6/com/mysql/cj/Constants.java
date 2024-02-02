package com.mysql.cj;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Constants {
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   public static final String MILLIS_I18N = Messages.getString("Milliseconds");
   public static final byte[] SLASH_STAR_SPACE_AS_BYTES = new byte[]{47, 42, 32};
   public static final byte[] SPACE_STAR_SLASH_SPACE_AS_BYTES = new byte[]{32, 42, 47, 32};
   public static final String JVM_VENDOR = System.getProperty("java.vendor");
   public static final String JVM_VERSION = System.getProperty("java.version");
   public static final String OS_NAME = System.getProperty("os.name");
   public static final String OS_ARCH = System.getProperty("os.arch");
   public static final String OS_VERSION = System.getProperty("os.version");
   public static final String PLATFORM_ENCODING = System.getProperty("file.encoding");
   public static final String CJ_NAME = "MySQL Connector/J";
   public static final String CJ_FULL_NAME = "mysql-connector-java-8.0.28";
   public static final String CJ_REVISION = "7ff2161da3899f379fb3171b6538b191b1c5c7e2";
   public static final String CJ_VERSION = "8.0.28";
   public static final String CJ_MAJOR_VERSION = "8";
   public static final String CJ_MINOR_VERSION = "0";
   public static final String CJ_LICENSE = "GPL";
   public static final BigInteger BIG_INTEGER_ZERO = BigInteger.valueOf(0L);
   public static final BigInteger BIG_INTEGER_ONE = BigInteger.valueOf(1L);
   public static final BigInteger BIG_INTEGER_NEGATIVE_ONE = BigInteger.valueOf(-1L);
   public static final BigInteger BIG_INTEGER_MIN_BYTE_VALUE = BigInteger.valueOf(-128L);
   public static final BigInteger BIG_INTEGER_MAX_BYTE_VALUE = BigInteger.valueOf(127L);
   public static final BigInteger BIG_INTEGER_MIN_SHORT_VALUE = BigInteger.valueOf(-32768L);
   public static final BigInteger BIG_INTEGER_MAX_SHORT_VALUE = BigInteger.valueOf(32767L);
   public static final BigInteger BIG_INTEGER_MIN_INTEGER_VALUE = BigInteger.valueOf(-2147483648L);
   public static final BigInteger BIG_INTEGER_MAX_INTEGER_VALUE = BigInteger.valueOf(2147483647L);
   public static final BigInteger BIG_INTEGER_MIN_LONG_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
   public static final BigInteger BIG_INTEGER_MAX_LONG_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
   public static final BigDecimal BIG_DECIMAL_ZERO = BigDecimal.valueOf(0L);
   public static final BigDecimal BIG_DECIMAL_ONE = BigDecimal.valueOf(1L);
   public static final BigDecimal BIG_DECIMAL_NEGATIVE_ONE = BigDecimal.valueOf(-1L);
   public static final BigDecimal BIG_DECIMAL_MIN_BYTE_VALUE = BigDecimal.valueOf(-128L);
   public static final BigDecimal BIG_DECIMAL_MAX_BYTE_VALUE = BigDecimal.valueOf(127L);
   public static final BigDecimal BIG_DECIMAL_MIN_SHORT_VALUE = BigDecimal.valueOf(-32768L);
   public static final BigDecimal BIG_DECIMAL_MAX_SHORT_VALUE = BigDecimal.valueOf(32767L);
   public static final BigDecimal BIG_DECIMAL_MIN_INTEGER_VALUE = BigDecimal.valueOf(-2147483648L);
   public static final BigDecimal BIG_DECIMAL_MAX_INTEGER_VALUE = BigDecimal.valueOf(2147483647L);
   public static final BigDecimal BIG_DECIMAL_MIN_LONG_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
   public static final BigDecimal BIG_DECIMAL_MAX_LONG_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);
   public static final BigDecimal BIG_DECIMAL_MAX_DOUBLE_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);
   public static final BigDecimal BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE = BigDecimal.valueOf(-1.7976931348623157E308);
   public static final BigDecimal BIG_DECIMAL_MAX_FLOAT_VALUE = BigDecimal.valueOf(3.4028234663852886E38);
   public static final BigDecimal BIG_DECIMAL_MAX_NEGATIVE_FLOAT_VALUE = BigDecimal.valueOf(-3.4028234663852886E38);
   public static final int UNSIGNED_BYTE_MAX_VALUE = 255;

   private Constants() {
   }
}
