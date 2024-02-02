/*      */ package oshi.util;
/*      */ 
/*      */ import java.math.BigInteger;
/*      */ import java.net.InetAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.time.LocalTime;
/*      */ import java.time.OffsetDateTime;
/*      */ import java.time.format.DateTimeFormatter;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ import oshi.annotation.concurrent.ThreadSafe;
/*      */ import oshi.util.tuples.Pair;
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
/*      */ @ThreadSafe
/*      */ public final class ParseUtil
/*      */ {
/*   57 */   private static final Logger LOG = LoggerFactory.getLogger(ParseUtil.class);
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String DEFAULT_LOG_MSG = "{} didn't parse. Returning default. {}";
/*      */ 
/*      */   
/*   64 */   private static final Pattern HERTZ_PATTERN = Pattern.compile("(\\d+(.\\d+)?) ?([kMGT]?Hz).*");
/*   65 */   private static final Pattern BYTES_PATTERN = Pattern.compile("(\\d+) ?([kMGT]?B).*");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   70 */   private static final Pattern VALID_HEX = Pattern.compile("[0-9a-fA-F]+");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   75 */   private static final Pattern DHMS = Pattern.compile("(?:(\\d+)-)?(?:(\\d+):)??(?:(\\d+):)?(\\d+)(?:\\.(\\d+))?");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   81 */   private static final Pattern UUID_PATTERN = Pattern.compile(".*([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}).*");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   87 */   private static final Pattern VENDOR_PRODUCT_ID = Pattern.compile(".*(?:VID|VEN)_(\\p{XDigit}{4})&(?:PID|DEV)_(\\p{XDigit}{4}).*");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   92 */   private static final Pattern LSPCI_MACHINE_READABLE = Pattern.compile("(.+)\\s\\[(.*?)\\]");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   97 */   private static final Pattern LSPCI_MEMORY_SIZE = Pattern.compile(".+\\s\\[size=(\\d+)([kKMGT])\\]");
/*      */   
/*      */   private static final String HZ = "Hz";
/*      */   
/*      */   private static final String KHZ = "kHz";
/*      */   
/*      */   private static final String MHZ = "MHz";
/*      */   
/*      */   private static final String GHZ = "GHz";
/*      */   
/*      */   private static final String THZ = "THz";
/*      */   
/*      */   private static final String PHZ = "PHz";
/*      */   
/*      */   private static final Map<String, Long> multipliers;
/*      */   
/*      */   private static final long EPOCH_DIFF = 11644473600000L;
/*  114 */   private static final int TZ_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());
/*      */ 
/*      */   
/*  117 */   public static final Pattern whitespacesColonWhitespace = Pattern.compile("\\s+:\\s");
/*      */ 
/*      */   
/*  120 */   public static final Pattern whitespaces = Pattern.compile("\\s+");
/*      */ 
/*      */   
/*  123 */   public static final Pattern notDigits = Pattern.compile("[^0-9]+");
/*      */ 
/*      */   
/*  126 */   public static final Pattern startWithNotDigits = Pattern.compile("^[^0-9]*");
/*      */ 
/*      */   
/*  129 */   public static final Pattern slash = Pattern.compile("\\/"); private static final long[] POWERS_OF_TEN;
/*      */   
/*      */   static {
/*  132 */     multipliers = new HashMap<>();
/*  133 */     multipliers.put("Hz", Long.valueOf(1L));
/*  134 */     multipliers.put("kHz", Long.valueOf(1000L));
/*  135 */     multipliers.put("MHz", Long.valueOf(1000000L));
/*  136 */     multipliers.put("GHz", Long.valueOf(1000000000L));
/*  137 */     multipliers.put("THz", Long.valueOf(1000000000000L));
/*  138 */     multipliers.put("PHz", Long.valueOf(1000000000000000L));
/*      */ 
/*      */ 
/*      */     
/*  142 */     POWERS_OF_TEN = new long[] { 1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  148 */     HEX_ARRAY = "0123456789ABCDEF".toCharArray();
/*      */ 
/*      */     
/*  151 */     CIM_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSSSSZZZZZ", Locale.US);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final char[] HEX_ARRAY;
/*      */ 
/*      */   
/*      */   private static final DateTimeFormatter CIM_FORMAT;
/*      */ 
/*      */ 
/*      */   
/*      */   public static long parseHertz(String hertz) {
/*  165 */     Matcher matcher = HERTZ_PATTERN.matcher(hertz.trim());
/*  166 */     if (matcher.find() && matcher.groupCount() == 3) {
/*      */       
/*  168 */       double value = Double.valueOf(matcher.group(1)).doubleValue() * ((Long)multipliers.getOrDefault(matcher.group(3), Long.valueOf(-1L))).longValue();
/*  169 */       if (value >= 0.0D) {
/*  170 */         return (long)value;
/*      */       }
/*      */     } 
/*  173 */     return -1L;
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
/*      */   public static int parseLastInt(String s, int i) {
/*      */     try {
/*  187 */       String ls = parseLastString(s);
/*  188 */       if (ls.toLowerCase().startsWith("0x")) {
/*  189 */         return Integer.decode(ls).intValue();
/*      */       }
/*  191 */       return Integer.parseInt(ls);
/*      */     }
/*  193 */     catch (NumberFormatException e) {
/*  194 */       LOG.trace("{} didn't parse. Returning default. {}", s, e);
/*  195 */       return i;
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
/*      */   public static long parseLastLong(String s, long li) {
/*      */     try {
/*  210 */       String ls = parseLastString(s);
/*  211 */       if (ls.toLowerCase().startsWith("0x")) {
/*  212 */         return Long.decode(ls).longValue();
/*      */       }
/*  214 */       return Long.parseLong(ls);
/*      */     }
/*  216 */     catch (NumberFormatException e) {
/*  217 */       LOG.trace("{} didn't parse. Returning default. {}", s, e);
/*  218 */       return li;
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
/*      */   public static double parseLastDouble(String s, double d) {
/*      */     try {
/*  233 */       return Double.parseDouble(parseLastString(s));
/*  234 */     } catch (NumberFormatException e) {
/*  235 */       LOG.trace("{} didn't parse. Returning default. {}", s, e);
/*  236 */       return d;
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
/*      */   public static String parseLastString(String s) {
/*  248 */     String[] ss = whitespaces.split(s);
/*  249 */     if (ss.length < 1) {
/*  250 */       return s;
/*      */     }
/*  252 */     return ss[ss.length - 1];
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
/*      */   public static String byteArrayToHexString(byte[] bytes) {
/*  266 */     char[] hexChars = new char[bytes.length * 2];
/*  267 */     for (int j = 0; j < bytes.length; j++) {
/*  268 */       int v = bytes[j] & 0xFF;
/*  269 */       hexChars[j * 2] = HEX_ARRAY[v >>> 4];
/*  270 */       hexChars[j * 2 + 1] = HEX_ARRAY[v & 0xF];
/*      */     } 
/*  272 */     return new String(hexChars);
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
/*      */   public static byte[] hexStringToByteArray(String digits) {
/*  284 */     int len = digits.length();
/*      */     
/*  286 */     if (!VALID_HEX.matcher(digits).matches() || (len & 0x1) != 0) {
/*  287 */       LOG.warn("Invalid hexadecimal string: {}", digits);
/*  288 */       return new byte[0];
/*      */     } 
/*  290 */     byte[] data = new byte[len / 2];
/*  291 */     for (int i = 0; i < len; i += 2) {
/*  292 */       data[i / 2] = 
/*  293 */         (byte)(Character.digit(digits.charAt(i), 16) << 4 | Character.digit(digits.charAt(i + 1), 16));
/*      */     }
/*  295 */     return data;
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
/*      */   public static byte[] asciiStringToByteArray(String text, int length) {
/*  311 */     return Arrays.copyOf(text.getBytes(StandardCharsets.US_ASCII), length);
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
/*      */   public static byte[] longToByteArray(long value, int valueSize, int length) {
/*  328 */     long val = value;
/*      */     
/*  330 */     byte[] b = new byte[8];
/*  331 */     for (int i = 7; i >= 0 && val != 0L; i--) {
/*  332 */       b[i] = (byte)(int)val;
/*  333 */       val >>>= 8L;
/*      */     } 
/*      */ 
/*      */     
/*  337 */     return Arrays.copyOfRange(b, 8 - valueSize, 8 + length - valueSize);
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
/*      */   public static long strToLong(String str, int size) {
/*  351 */     return byteArrayToLong(str.getBytes(StandardCharsets.US_ASCII), size);
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
/*      */   public static long byteArrayToLong(byte[] bytes, int size) {
/*  364 */     if (size > 8) {
/*  365 */       throw new IllegalArgumentException("Can't convert more than 8 bytes.");
/*      */     }
/*  367 */     if (size > bytes.length) {
/*  368 */       throw new IllegalArgumentException("Size can't be larger than array length.");
/*      */     }
/*  370 */     long total = 0L;
/*  371 */     for (int i = 0; i < size; i++) {
/*  372 */       total = total << 8L | (bytes[i] & 0xFF);
/*      */     }
/*  374 */     return total;
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
/*      */   public static float byteArrayToFloat(byte[] bytes, int size, int fpBits) {
/*  391 */     return (float)byteArrayToLong(bytes, size) / (1 << fpBits);
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
/*      */   public static long unsignedIntToLong(int unsignedValue) {
/*  411 */     long longValue = unsignedValue;
/*  412 */     return longValue & 0xFFFFFFFFL;
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
/*      */   public static long unsignedLongToSignedLong(long unsignedValue) {
/*  425 */     return unsignedValue & Long.MAX_VALUE;
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
/*      */   public static String hexStringToString(String hexString) {
/*  439 */     if (hexString.length() % 2 > 0) {
/*  440 */       return hexString;
/*      */     }
/*      */     
/*  443 */     StringBuilder sb = new StringBuilder();
/*      */     try {
/*  445 */       for (int pos = 0; pos < hexString.length(); pos += 2) {
/*  446 */         int charAsInt = Integer.parseInt(hexString.substring(pos, pos + 2), 16);
/*  447 */         if (charAsInt < 32 || charAsInt > 127) {
/*  448 */           return hexString;
/*      */         }
/*  450 */         sb.append((char)charAsInt);
/*      */       } 
/*  452 */     } catch (NumberFormatException e) {
/*  453 */       LOG.trace("{} didn't parse. Returning default. {}", hexString, e);
/*      */       
/*  455 */       return hexString;
/*      */     } 
/*  457 */     return sb.toString();
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
/*      */   public static int parseIntOrDefault(String s, int defaultInt) {
/*      */     try {
/*  471 */       return Integer.parseInt(s);
/*  472 */     } catch (NumberFormatException e) {
/*  473 */       LOG.trace("{} didn't parse. Returning default. {}", s, e);
/*  474 */       return defaultInt;
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
/*      */   public static long parseLongOrDefault(String s, long defaultLong) {
/*      */     try {
/*  489 */       return Long.parseLong(s);
/*  490 */     } catch (NumberFormatException e) {
/*  491 */       LOG.trace("{} didn't parse. Returning default. {}", s, e);
/*  492 */       return defaultLong;
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
/*      */   public static long parseUnsignedLongOrDefault(String s, long defaultLong) {
/*      */     try {
/*  509 */       return (new BigInteger(s)).longValue();
/*  510 */     } catch (NumberFormatException e) {
/*  511 */       LOG.trace("{} didn't parse. Returning default. {}", s, e);
/*  512 */       return defaultLong;
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
/*      */   public static double parseDoubleOrDefault(String s, double defaultDouble) {
/*      */     try {
/*  527 */       return Double.parseDouble(s);
/*  528 */     } catch (NumberFormatException e) {
/*  529 */       LOG.trace("{} didn't parse. Returning default. {}", s, e);
/*  530 */       return defaultDouble;
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
/*      */   public static long parseDHMSOrDefault(String s, long defaultLong) {
/*  545 */     Matcher m = DHMS.matcher(s);
/*  546 */     if (m.matches()) {
/*  547 */       long milliseconds = 0L;
/*  548 */       if (m.group(1) != null) {
/*  549 */         milliseconds += parseLongOrDefault(m.group(1), 0L) * 86400000L;
/*      */       }
/*  551 */       if (m.group(2) != null) {
/*  552 */         milliseconds += parseLongOrDefault(m.group(2), 0L) * 3600000L;
/*      */       }
/*  554 */       if (m.group(3) != null) {
/*  555 */         milliseconds += parseLongOrDefault(m.group(3), 0L) * 60000L;
/*      */       }
/*  557 */       milliseconds += parseLongOrDefault(m.group(4), 0L) * 1000L;
/*  558 */       milliseconds += (long)(1000.0D * parseDoubleOrDefault("0." + m.group(5), 0.0D));
/*  559 */       return milliseconds;
/*      */     } 
/*  561 */     return defaultLong;
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
/*      */   public static String parseUuidOrDefault(String s, String defaultStr) {
/*  574 */     Matcher m = UUID_PATTERN.matcher(s.toLowerCase());
/*  575 */     if (m.matches()) {
/*  576 */       return m.group(1);
/*      */     }
/*  578 */     return defaultStr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getSingleQuoteStringValue(String line) {
/*  589 */     return getStringBetween(line, '\'');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getDoubleQuoteStringValue(String line) {
/*  600 */     return getStringBetween(line, '"');
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
/*      */   public static String getStringBetween(String line, char c) {
/*  619 */     int firstOcc = line.indexOf(c);
/*  620 */     if (firstOcc < 0) {
/*  621 */       return "";
/*      */     }
/*  623 */     return line.substring(firstOcc + 1, line.lastIndexOf(c)).trim();
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
/*      */   public static int getFirstIntValue(String line) {
/*  635 */     return getNthIntValue(line, 1);
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
/*      */   public static int getNthIntValue(String line, int n) {
/*  650 */     String[] split = notDigits.split(startWithNotDigits.matcher(line).replaceFirst(""));
/*  651 */     if (split.length >= n) {
/*  652 */       return parseIntOrDefault(split[n - 1], 0);
/*      */     }
/*  654 */     return 0;
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
/*      */   public static String removeMatchingString(String original, String toRemove) {
/*  667 */     if (original == null || original.isEmpty() || toRemove == null || toRemove.isEmpty()) {
/*  668 */       return original;
/*      */     }
/*      */     
/*  671 */     int matchIndex = original.indexOf(toRemove, 0);
/*  672 */     if (matchIndex == -1) {
/*  673 */       return original;
/*      */     }
/*      */     
/*  676 */     StringBuilder buffer = new StringBuilder(original.length() - toRemove.length());
/*  677 */     int currIndex = 0;
/*      */     do {
/*  679 */       buffer.append(original.substring(currIndex, matchIndex));
/*  680 */       currIndex = matchIndex + toRemove.length();
/*  681 */       matchIndex = original.indexOf(toRemove, currIndex);
/*  682 */     } while (matchIndex != -1);
/*      */     
/*  684 */     buffer.append(original.substring(currIndex));
/*  685 */     return buffer.toString();
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
/*      */   public static long[] parseStringToLongArray(String s, int[] indices, int length, char delimiter) {
/*  720 */     long[] parsed = new long[indices.length];
/*      */ 
/*      */     
/*  723 */     int charIndex = s.length();
/*  724 */     int parsedIndex = indices.length - 1;
/*  725 */     int stringIndex = length - 1;
/*      */     
/*  727 */     int power = 0;
/*      */     
/*  729 */     boolean delimCurrent = false;
/*  730 */     boolean numeric = true;
/*  731 */     boolean numberFound = false;
/*  732 */     boolean dashSeen = false;
/*  733 */     while (--charIndex > 0 && parsedIndex >= 0) {
/*  734 */       int c = s.charAt(charIndex);
/*  735 */       if (c == delimiter) {
/*      */         
/*  737 */         if (!numberFound && numeric) {
/*  738 */           numberFound = true;
/*      */         }
/*  740 */         if (!delimCurrent) {
/*  741 */           if (numberFound && indices[parsedIndex] == stringIndex--) {
/*  742 */             parsedIndex--;
/*      */           }
/*  744 */           delimCurrent = true;
/*  745 */           power = 0;
/*  746 */           dashSeen = false;
/*  747 */           numeric = true;
/*      */         }  continue;
/*  749 */       }  if (indices[parsedIndex] != stringIndex || c == 43 || !numeric) {
/*      */         
/*  751 */         delimCurrent = false; continue;
/*  752 */       }  if (c >= 48 && c <= 57 && !dashSeen) {
/*  753 */         if (power > 18 || (power == 17 && c == 57 && parsed[parsedIndex] > 223372036854775807L)) {
/*  754 */           parsed[parsedIndex] = Long.MAX_VALUE;
/*      */         } else {
/*  756 */           parsed[parsedIndex] = parsed[parsedIndex] + (c - 48) * POWERS_OF_TEN[power++];
/*      */         } 
/*  758 */         delimCurrent = false; continue;
/*  759 */       }  if (c == 45) {
/*  760 */         parsed[parsedIndex] = parsed[parsedIndex] * -1L;
/*  761 */         delimCurrent = false;
/*  762 */         dashSeen = true;
/*      */         
/*      */         continue;
/*      */       } 
/*  766 */       if (numberFound) {
/*  767 */         LOG.error("Illegal character parsing string '{}' to long array: {}", s, Character.valueOf(s.charAt(charIndex)));
/*  768 */         return new long[indices.length];
/*      */       } 
/*  770 */       parsed[parsedIndex] = 0L;
/*  771 */       numeric = false;
/*      */     } 
/*      */     
/*  774 */     if (parsedIndex > 0) {
/*  775 */       LOG.error("Not enough fields in string '{}' parsing to long array: {}", s, Integer.valueOf(indices.length - parsedIndex));
/*  776 */       return new long[indices.length];
/*      */     } 
/*  778 */     return parsed;
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
/*      */   public static int countStringToLongArray(String s, char delimiter) {
/*  799 */     int charIndex = s.length();
/*  800 */     int numbers = 0;
/*      */ 
/*      */     
/*  803 */     boolean delimCurrent = false;
/*  804 */     boolean numeric = true;
/*  805 */     boolean dashSeen = false;
/*  806 */     while (--charIndex > 0) {
/*  807 */       int c = s.charAt(charIndex);
/*  808 */       if (c == delimiter) {
/*  809 */         if (!delimCurrent) {
/*  810 */           if (numeric) {
/*  811 */             numbers++;
/*      */           }
/*  813 */           delimCurrent = true;
/*  814 */           dashSeen = false;
/*  815 */           numeric = true;
/*      */         }  continue;
/*  817 */       }  if (c == 43 || !numeric) {
/*      */         
/*  819 */         delimCurrent = false; continue;
/*  820 */       }  if (c >= 48 && c <= 57 && !dashSeen) {
/*  821 */         delimCurrent = false; continue;
/*  822 */       }  if (c == 45) {
/*  823 */         delimCurrent = false;
/*  824 */         dashSeen = true;
/*      */         continue;
/*      */       } 
/*  827 */       if (numbers > 0) {
/*  828 */         return numbers;
/*      */       }
/*      */       
/*  831 */       numeric = false;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  836 */     return numbers + 1;
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
/*      */   public static String getTextBetweenStrings(String text, String before, String after) {
/*  853 */     String result = "";
/*      */     
/*  855 */     if (text.indexOf(before) >= 0 && text.indexOf(after) >= 0) {
/*  856 */       result = text.substring(text.indexOf(before) + before.length(), text.length());
/*  857 */       result = result.substring(0, result.indexOf(after));
/*      */     } 
/*  859 */     return result;
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
/*      */   public static long filetimeToUtcMs(long filetime, boolean local) {
/*  874 */     return filetime / 10000L - 11644473600000L - (local ? TZ_OFFSET : 0L);
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
/*      */   public static String parseMmDdYyyyToYyyyMmDD(String dateString) {
/*      */     try {
/*  888 */       return String.format("%s-%s-%s", new Object[] { dateString.substring(6, 10), dateString.substring(0, 2), dateString
/*  889 */             .substring(3, 5) });
/*  890 */     } catch (StringIndexOutOfBoundsException e) {
/*  891 */       return dateString;
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
/*      */   public static OffsetDateTime parseCimDateTimeToOffset(String cimDateTime) {
/*      */     try {
/*  910 */       int tzInMinutes = Integer.parseInt(cimDateTime.substring(22));
/*      */       
/*  912 */       LocalTime offsetAsLocalTime = LocalTime.MIDNIGHT.plusMinutes(tzInMinutes);
/*  913 */       return OffsetDateTime.parse(cimDateTime
/*  914 */           .substring(0, 22) + offsetAsLocalTime.format(DateTimeFormatter.ISO_LOCAL_TIME), CIM_FORMAT);
/*      */     }
/*  916 */     catch (IndexOutOfBoundsException|NumberFormatException|java.time.format.DateTimeParseException e) {
/*      */ 
/*      */       
/*  919 */       LOG.trace("Unable to parse {} to CIM DateTime.", cimDateTime);
/*  920 */       return Constants.UNIX_EPOCH;
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
/*      */   public static boolean filePathStartsWith(List<String> prefixList, String path) {
/*  935 */     for (String match : prefixList) {
/*  936 */       if (path.equals(match) || path.startsWith(match + "/")) {
/*  937 */         return true;
/*      */       }
/*      */     } 
/*  940 */     return false;
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
/*      */   public static long parseDecimalMemorySizeToBinary(String size) {
/*  953 */     String[] mem = whitespaces.split(size);
/*  954 */     if (mem.length < 2) {
/*      */       
/*  956 */       Matcher matcher = BYTES_PATTERN.matcher(size.trim());
/*  957 */       if (matcher.find() && matcher.groupCount() == 2) {
/*  958 */         mem = new String[2];
/*  959 */         mem[0] = matcher.group(1);
/*  960 */         mem[1] = matcher.group(2);
/*      */       } 
/*      */     } 
/*  963 */     long capacity = parseLongOrDefault(mem[0], 0L);
/*  964 */     if (mem.length == 2 && mem[1].length() > 1) {
/*  965 */       switch (mem[1].charAt(0)) {
/*      */         case 'T':
/*  967 */           capacity <<= 40L;
/*      */           break;
/*      */         case 'G':
/*  970 */           capacity <<= 30L;
/*      */           break;
/*      */         case 'M':
/*  973 */           capacity <<= 20L;
/*      */           break;
/*      */         case 'K':
/*      */         case 'k':
/*  977 */           capacity <<= 10L;
/*      */           break;
/*      */       } 
/*      */ 
/*      */     
/*      */     }
/*  983 */     return capacity;
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
/*      */   public static Pair<String, String> parsePnPDeviceIdToVendorProductId(String pnpDeviceId) {
/*  996 */     Matcher m = VENDOR_PRODUCT_ID.matcher(pnpDeviceId);
/*  997 */     if (m.matches()) {
/*  998 */       String vendorId = "0x" + m.group(1).toLowerCase();
/*  999 */       String productId = "0x" + m.group(2).toLowerCase();
/* 1000 */       return new Pair(vendorId, productId);
/*      */     } 
/* 1002 */     return null;
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
/*      */   public static long parseLshwResourceString(String resources) {
/* 1015 */     long bytes = 0L;
/*      */     
/* 1017 */     String[] resourceArray = whitespaces.split(resources);
/* 1018 */     for (String r : resourceArray) {
/*      */       
/* 1020 */       if (r.startsWith("memory:")) {
/*      */         
/* 1022 */         String[] mem = r.substring(7).split("-");
/* 1023 */         if (mem.length == 2) {
/*      */           
/*      */           try {
/* 1026 */             bytes += Long.parseLong(mem[1], 16) - Long.parseLong(mem[0], 16) + 1L;
/* 1027 */           } catch (NumberFormatException e) {
/* 1028 */             LOG.trace("{} didn't parse. Returning default. {}", r, e);
/*      */           } 
/*      */         }
/*      */       } 
/*      */     } 
/* 1033 */     return bytes;
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
/*      */   public static Pair<String, String> parseLspciMachineReadable(String line) {
/* 1045 */     Matcher matcher = LSPCI_MACHINE_READABLE.matcher(line);
/* 1046 */     if (matcher.matches()) {
/* 1047 */       return new Pair(matcher.group(1), matcher.group(2));
/*      */     }
/* 1049 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long parseLspciMemorySize(String line) {
/* 1060 */     Matcher matcher = LSPCI_MEMORY_SIZE.matcher(line);
/* 1061 */     if (matcher.matches()) {
/* 1062 */       return parseDecimalMemorySizeToBinary(matcher.group(1) + " " + matcher.group(2) + "B");
/*      */     }
/* 1064 */     return 0L;
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
/*      */   public static List<Integer> parseHyphenatedIntList(String str) {
/* 1078 */     List<Integer> result = new ArrayList<>();
/* 1079 */     for (String s : whitespaces.split(str)) {
/* 1080 */       if (s.contains("-")) {
/* 1081 */         int first = getFirstIntValue(s);
/* 1082 */         int last = getNthIntValue(s, 2);
/* 1083 */         for (int i = first; i <= last; i++) {
/* 1084 */           result.add(Integer.valueOf(i));
/*      */         }
/*      */       } else {
/* 1087 */         int only = parseIntOrDefault(s, -1);
/* 1088 */         if (only >= 0) {
/* 1089 */           result.add(Integer.valueOf(only));
/*      */         }
/*      */       } 
/*      */     } 
/* 1093 */     return result;
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
/*      */   public static String parseUtAddrV6toIP(int[] utAddrV6) {
/* 1107 */     if (utAddrV6.length != 4) {
/* 1108 */       throw new IllegalArgumentException("ut_addr_v6 must have exactly 4 elements");
/*      */     }
/*      */     
/* 1111 */     if (utAddrV6[1] == 0 && utAddrV6[2] == 0 && utAddrV6[3] == 0) {
/*      */       
/* 1113 */       if (utAddrV6[0] == 0) {
/* 1114 */         return "::";
/*      */       }
/*      */       
/* 1117 */       byte[] ipv4 = ByteBuffer.allocate(4).putInt(utAddrV6[0]).array();
/*      */       try {
/* 1119 */         return InetAddress.getByAddress(ipv4).getHostAddress();
/* 1120 */       } catch (UnknownHostException e) {
/*      */         
/* 1122 */         return "unknown";
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1127 */     byte[] ipv6 = ByteBuffer.allocate(16).putInt(utAddrV6[0]).putInt(utAddrV6[1]).putInt(utAddrV6[2]).putInt(utAddrV6[3]).array();
/*      */     try {
/* 1129 */       return InetAddress.getByAddress(ipv6).getHostAddress()
/* 1130 */         .replaceAll("((?:(?:^|:)0+\\b){2,}):?(?!\\S*\\b\\1:0+\\b)(\\S*)", "::$2");
/* 1131 */     } catch (UnknownHostException e) {
/*      */       
/* 1133 */       return "unknown";
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
/*      */   public static long hexStringToLong(String hexString, long defaultValue) {
/*      */     try {
/* 1148 */       return (new BigInteger(hexString, 16)).longValue();
/* 1149 */     } catch (NumberFormatException e) {
/* 1150 */       LOG.trace("{} didn't parse. Returning default. {}", hexString, e);
/*      */       
/* 1152 */       return defaultValue;
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
/*      */   public static String removeLeadingDots(String dotPrefixedStr) {
/* 1164 */     int pos = 0;
/* 1165 */     while (pos < dotPrefixedStr.length() && dotPrefixedStr.charAt(pos) == '.') {
/* 1166 */       pos++;
/*      */     }
/* 1168 */     return (pos < dotPrefixedStr.length()) ? dotPrefixedStr.substring(pos) : "";
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\ParseUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */