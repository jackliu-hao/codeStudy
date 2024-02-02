package cn.hutool.core.date;

import cn.hutool.core.date.format.FastDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class DatePattern {
   public static final Pattern REGEX_NORM = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}(\\s\\d{1,2}:\\d{1,2}(:\\d{1,2})?)?(.\\d{1,6})?");
   public static final String NORM_YEAR_PATTERN = "yyyy";
   public static final String NORM_MONTH_PATTERN = "yyyy-MM";
   public static final FastDateFormat NORM_MONTH_FORMAT = FastDateFormat.getInstance("yyyy-MM");
   public static final DateTimeFormatter NORM_MONTH_FORMATTER = createFormatter("yyyy-MM");
   public static final String SIMPLE_MONTH_PATTERN = "yyyyMM";
   public static final FastDateFormat SIMPLE_MONTH_FORMAT = FastDateFormat.getInstance("yyyyMM");
   public static final DateTimeFormatter SIMPLE_MONTH_FORMATTER = createFormatter("yyyyMM");
   public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
   public static final FastDateFormat NORM_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
   public static final DateTimeFormatter NORM_DATE_FORMATTER = createFormatter("yyyy-MM-dd");
   public static final String NORM_TIME_PATTERN = "HH:mm:ss";
   public static final FastDateFormat NORM_TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");
   public static final DateTimeFormatter NORM_TIME_FORMATTER = createFormatter("HH:mm:ss");
   public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
   public static final FastDateFormat NORM_DATETIME_MINUTE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm");
   public static final DateTimeFormatter NORM_DATETIME_MINUTE_FORMATTER = createFormatter("yyyy-MM-dd HH:mm");
   public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
   public static final FastDateFormat NORM_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
   public static final DateTimeFormatter NORM_DATETIME_FORMATTER = createFormatter("yyyy-MM-dd HH:mm:ss");
   public static final String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
   public static final FastDateFormat NORM_DATETIME_MS_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");
   public static final DateTimeFormatter NORM_DATETIME_MS_FORMATTER = createFormatter("yyyy-MM-dd HH:mm:ss.SSS");
   public static final String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";
   public static final FastDateFormat ISO8601_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss,SSS");
   public static final DateTimeFormatter ISO8601_FORMATTER = createFormatter("yyyy-MM-dd HH:mm:ss,SSS");
   public static final String CHINESE_DATE_PATTERN = "yyyy年MM月dd日";
   public static final FastDateFormat CHINESE_DATE_FORMAT = FastDateFormat.getInstance("yyyy年MM月dd日");
   public static final DateTimeFormatter CHINESE_DATE_FORMATTER = createFormatter("yyyy年MM月dd日");
   public static final String CHINESE_DATE_TIME_PATTERN = "yyyy年MM月dd日HH时mm分ss秒";
   public static final FastDateFormat CHINESE_DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy年MM月dd日HH时mm分ss秒");
   public static final DateTimeFormatter CHINESE_DATE_TIME_FORMATTER = createFormatter("yyyy年MM月dd日HH时mm分ss秒");
   public static final String PURE_DATE_PATTERN = "yyyyMMdd";
   public static final FastDateFormat PURE_DATE_FORMAT = FastDateFormat.getInstance("yyyyMMdd");
   public static final DateTimeFormatter PURE_DATE_FORMATTER = createFormatter("yyyyMMdd");
   public static final String PURE_TIME_PATTERN = "HHmmss";
   public static final FastDateFormat PURE_TIME_FORMAT = FastDateFormat.getInstance("HHmmss");
   public static final DateTimeFormatter PURE_TIME_FORMATTER = createFormatter("HHmmss");
   public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
   public static final FastDateFormat PURE_DATETIME_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");
   public static final DateTimeFormatter PURE_DATETIME_FORMATTER = createFormatter("yyyyMMddHHmmss");
   public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";
   public static final FastDateFormat PURE_DATETIME_MS_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmssSSS");
   public static final DateTimeFormatter PURE_DATETIME_MS_FORMATTER = createFormatter("yyyyMMddHHmmssSSS");
   public static final String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
   public static final FastDateFormat HTTP_DATETIME_FORMAT;
   public static final String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";
   public static final FastDateFormat JDK_DATETIME_FORMAT;
   public static final String UTC_SIMPLE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
   public static final FastDateFormat UTC_SIMPLE_FORMAT;
   public static final String UTC_SIMPLE_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
   public static final FastDateFormat UTC_SIMPLE_MS_FORMAT;
   public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
   public static final FastDateFormat UTC_FORMAT;
   public static final String UTC_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
   public static final FastDateFormat UTC_WITH_ZONE_OFFSET_FORMAT;
   public static final String UTC_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
   public static final FastDateFormat UTC_WITH_XXX_OFFSET_FORMAT;
   public static final String UTC_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
   public static final FastDateFormat UTC_MS_FORMAT;
   public static final String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
   public static final FastDateFormat UTC_MS_WITH_ZONE_OFFSET_FORMAT;
   public static final String UTC_MS_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
   public static final FastDateFormat UTC_MS_WITH_XXX_OFFSET_FORMAT;

   public static DateTimeFormatter createFormatter(String pattern) {
      return DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).withZone(ZoneId.systemDefault());
   }

   static {
      HTTP_DATETIME_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss z", TimeZone.getTimeZone("GMT"), Locale.US);
      JDK_DATETIME_FORMAT = FastDateFormat.getInstance("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
      UTC_SIMPLE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss", TimeZone.getTimeZone("UTC"));
      UTC_SIMPLE_MS_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS", TimeZone.getTimeZone("UTC"));
      UTC_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC"));
      UTC_WITH_ZONE_OFFSET_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZ", TimeZone.getTimeZone("UTC"));
      UTC_WITH_XXX_OFFSET_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssXXX");
      UTC_MS_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC"));
      UTC_MS_WITH_ZONE_OFFSET_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZ", TimeZone.getTimeZone("UTC"));
      UTC_MS_WITH_XXX_OFFSET_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
   }
}
