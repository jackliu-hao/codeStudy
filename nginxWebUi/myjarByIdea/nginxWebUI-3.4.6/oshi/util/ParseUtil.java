package oshi.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ParseUtil {
   private static final Logger LOG = LoggerFactory.getLogger(ParseUtil.class);
   private static final String DEFAULT_LOG_MSG = "{} didn't parse. Returning default. {}";
   private static final Pattern HERTZ_PATTERN = Pattern.compile("(\\d+(.\\d+)?) ?([kMGT]?Hz).*");
   private static final Pattern BYTES_PATTERN = Pattern.compile("(\\d+) ?([kMGT]?B).*");
   private static final Pattern VALID_HEX = Pattern.compile("[0-9a-fA-F]+");
   private static final Pattern DHMS = Pattern.compile("(?:(\\d+)-)?(?:(\\d+):)??(?:(\\d+):)?(\\d+)(?:\\.(\\d+))?");
   private static final Pattern UUID_PATTERN = Pattern.compile(".*([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}).*");
   private static final Pattern VENDOR_PRODUCT_ID = Pattern.compile(".*(?:VID|VEN)_(\\p{XDigit}{4})&(?:PID|DEV)_(\\p{XDigit}{4}).*");
   private static final Pattern LSPCI_MACHINE_READABLE = Pattern.compile("(.+)\\s\\[(.*?)\\]");
   private static final Pattern LSPCI_MEMORY_SIZE = Pattern.compile(".+\\s\\[size=(\\d+)([kKMGT])\\]");
   private static final String HZ = "Hz";
   private static final String KHZ = "kHz";
   private static final String MHZ = "MHz";
   private static final String GHZ = "GHz";
   private static final String THZ = "THz";
   private static final String PHZ = "PHz";
   private static final Map<String, Long> multipliers = new HashMap();
   private static final long EPOCH_DIFF = 11644473600000L;
   private static final int TZ_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());
   public static final Pattern whitespacesColonWhitespace = Pattern.compile("\\s+:\\s");
   public static final Pattern whitespaces = Pattern.compile("\\s+");
   public static final Pattern notDigits = Pattern.compile("[^0-9]+");
   public static final Pattern startWithNotDigits = Pattern.compile("^[^0-9]*");
   public static final Pattern slash = Pattern.compile("\\/");
   private static final long[] POWERS_OF_TEN;
   private static final char[] HEX_ARRAY;
   private static final DateTimeFormatter CIM_FORMAT;

   private ParseUtil() {
   }

   public static long parseHertz(String hertz) {
      Matcher matcher = HERTZ_PATTERN.matcher(hertz.trim());
      if (matcher.find() && matcher.groupCount() == 3) {
         double value = Double.valueOf(matcher.group(1)) * (double)(Long)multipliers.getOrDefault(matcher.group(3), -1L);
         if (value >= 0.0) {
            return (long)value;
         }
      }

      return -1L;
   }

   public static int parseLastInt(String s, int i) {
      try {
         String ls = parseLastString(s);
         return ls.toLowerCase().startsWith("0x") ? Integer.decode(ls) : Integer.parseInt(ls);
      } catch (NumberFormatException var3) {
         LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)s, (Object)var3);
         return i;
      }
   }

   public static long parseLastLong(String s, long li) {
      try {
         String ls = parseLastString(s);
         return ls.toLowerCase().startsWith("0x") ? Long.decode(ls) : Long.parseLong(ls);
      } catch (NumberFormatException var4) {
         LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)s, (Object)var4);
         return li;
      }
   }

   public static double parseLastDouble(String s, double d) {
      try {
         return Double.parseDouble(parseLastString(s));
      } catch (NumberFormatException var4) {
         LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)s, (Object)var4);
         return d;
      }
   }

   public static String parseLastString(String s) {
      String[] ss = whitespaces.split(s);
      return ss.length < 1 ? s : ss[ss.length - 1];
   }

   public static String byteArrayToHexString(byte[] bytes) {
      char[] hexChars = new char[bytes.length * 2];

      for(int j = 0; j < bytes.length; ++j) {
         int v = bytes[j] & 255;
         hexChars[j * 2] = HEX_ARRAY[v >>> 4];
         hexChars[j * 2 + 1] = HEX_ARRAY[v & 15];
      }

      return new String(hexChars);
   }

   public static byte[] hexStringToByteArray(String digits) {
      int len = digits.length();
      if (VALID_HEX.matcher(digits).matches() && (len & 1) == 0) {
         byte[] data = new byte[len / 2];

         for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)(Character.digit(digits.charAt(i), 16) << 4 | Character.digit(digits.charAt(i + 1), 16));
         }

         return data;
      } else {
         LOG.warn((String)"Invalid hexadecimal string: {}", (Object)digits);
         return new byte[0];
      }
   }

   public static byte[] asciiStringToByteArray(String text, int length) {
      return Arrays.copyOf(text.getBytes(StandardCharsets.US_ASCII), length);
   }

   public static byte[] longToByteArray(long value, int valueSize, int length) {
      long val = value;
      byte[] b = new byte[8];

      for(int i = 7; i >= 0 && val != 0L; --i) {
         b[i] = (byte)((int)val);
         val >>>= 8;
      }

      return Arrays.copyOfRange(b, 8 - valueSize, 8 + length - valueSize);
   }

   public static long strToLong(String str, int size) {
      return byteArrayToLong(str.getBytes(StandardCharsets.US_ASCII), size);
   }

   public static long byteArrayToLong(byte[] bytes, int size) {
      if (size > 8) {
         throw new IllegalArgumentException("Can't convert more than 8 bytes.");
      } else if (size > bytes.length) {
         throw new IllegalArgumentException("Size can't be larger than array length.");
      } else {
         long total = 0L;

         for(int i = 0; i < size; ++i) {
            total = total << 8 | (long)(bytes[i] & 255);
         }

         return total;
      }
   }

   public static float byteArrayToFloat(byte[] bytes, int size, int fpBits) {
      return (float)byteArrayToLong(bytes, size) / (float)(1 << fpBits);
   }

   public static long unsignedIntToLong(int unsignedValue) {
      long longValue = (long)unsignedValue;
      return longValue & 4294967295L;
   }

   public static long unsignedLongToSignedLong(long unsignedValue) {
      return unsignedValue & Long.MAX_VALUE;
   }

   public static String hexStringToString(String hexString) {
      if (hexString.length() % 2 > 0) {
         return hexString;
      } else {
         StringBuilder sb = new StringBuilder();

         try {
            for(int pos = 0; pos < hexString.length(); pos += 2) {
               int charAsInt = Integer.parseInt(hexString.substring(pos, pos + 2), 16);
               if (charAsInt < 32 || charAsInt > 127) {
                  return hexString;
               }

               sb.append((char)charAsInt);
            }
         } catch (NumberFormatException var4) {
            LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)hexString, (Object)var4);
            return hexString;
         }

         return sb.toString();
      }
   }

   public static int parseIntOrDefault(String s, int defaultInt) {
      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException var3) {
         LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)s, (Object)var3);
         return defaultInt;
      }
   }

   public static long parseLongOrDefault(String s, long defaultLong) {
      try {
         return Long.parseLong(s);
      } catch (NumberFormatException var4) {
         LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)s, (Object)var4);
         return defaultLong;
      }
   }

   public static long parseUnsignedLongOrDefault(String s, long defaultLong) {
      try {
         return (new BigInteger(s)).longValue();
      } catch (NumberFormatException var4) {
         LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)s, (Object)var4);
         return defaultLong;
      }
   }

   public static double parseDoubleOrDefault(String s, double defaultDouble) {
      try {
         return Double.parseDouble(s);
      } catch (NumberFormatException var4) {
         LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)s, (Object)var4);
         return defaultDouble;
      }
   }

   public static long parseDHMSOrDefault(String s, long defaultLong) {
      Matcher m = DHMS.matcher(s);
      if (m.matches()) {
         long milliseconds = 0L;
         if (m.group(1) != null) {
            milliseconds += parseLongOrDefault(m.group(1), 0L) * 86400000L;
         }

         if (m.group(2) != null) {
            milliseconds += parseLongOrDefault(m.group(2), 0L) * 3600000L;
         }

         if (m.group(3) != null) {
            milliseconds += parseLongOrDefault(m.group(3), 0L) * 60000L;
         }

         milliseconds += parseLongOrDefault(m.group(4), 0L) * 1000L;
         milliseconds += (long)(1000.0 * parseDoubleOrDefault("0." + m.group(5), 0.0));
         return milliseconds;
      } else {
         return defaultLong;
      }
   }

   public static String parseUuidOrDefault(String s, String defaultStr) {
      Matcher m = UUID_PATTERN.matcher(s.toLowerCase());
      return m.matches() ? m.group(1) : defaultStr;
   }

   public static String getSingleQuoteStringValue(String line) {
      return getStringBetween(line, '\'');
   }

   public static String getDoubleQuoteStringValue(String line) {
      return getStringBetween(line, '"');
   }

   public static String getStringBetween(String line, char c) {
      int firstOcc = line.indexOf(c);
      return firstOcc < 0 ? "" : line.substring(firstOcc + 1, line.lastIndexOf(c)).trim();
   }

   public static int getFirstIntValue(String line) {
      return getNthIntValue(line, 1);
   }

   public static int getNthIntValue(String line, int n) {
      String[] split = notDigits.split(startWithNotDigits.matcher(line).replaceFirst(""));
      return split.length >= n ? parseIntOrDefault(split[n - 1], 0) : 0;
   }

   public static String removeMatchingString(String original, String toRemove) {
      if (original != null && !original.isEmpty() && toRemove != null && !toRemove.isEmpty()) {
         int matchIndex = original.indexOf(toRemove, 0);
         if (matchIndex == -1) {
            return original;
         } else {
            StringBuilder buffer = new StringBuilder(original.length() - toRemove.length());
            int currIndex = 0;

            do {
               buffer.append(original.substring(currIndex, matchIndex));
               currIndex = matchIndex + toRemove.length();
               matchIndex = original.indexOf(toRemove, currIndex);
            } while(matchIndex != -1);

            buffer.append(original.substring(currIndex));
            return buffer.toString();
         }
      } else {
         return original;
      }
   }

   public static long[] parseStringToLongArray(String s, int[] indices, int length, char delimiter) {
      long[] parsed = new long[indices.length];
      int charIndex = s.length();
      int parsedIndex = indices.length - 1;
      int stringIndex = length - 1;
      int power = 0;
      boolean delimCurrent = false;
      boolean numeric = true;
      boolean numberFound = false;
      boolean dashSeen = false;

      while(true) {
         --charIndex;
         if (charIndex <= 0 || parsedIndex < 0) {
            if (parsedIndex > 0) {
               LOG.error((String)"Not enough fields in string '{}' parsing to long array: {}", (Object)s, (Object)(indices.length - parsedIndex));
               return new long[indices.length];
            } else {
               return parsed;
            }
         }

         int c = s.charAt(charIndex);
         if (c == delimiter) {
            if (!numberFound && numeric) {
               numberFound = true;
            }

            if (!delimCurrent) {
               if (numberFound && indices[parsedIndex] == stringIndex--) {
                  --parsedIndex;
               }

               delimCurrent = true;
               power = 0;
               dashSeen = false;
               numeric = true;
            }
         } else if (indices[parsedIndex] == stringIndex && c != '+' && numeric) {
            if (c >= '0' && c <= '9' && !dashSeen) {
               if (power <= 18 && (power != 17 || c != '9' || parsed[parsedIndex] <= 223372036854775807L)) {
                  parsed[parsedIndex] += (long)(c - 48) * POWERS_OF_TEN[power++];
               } else {
                  parsed[parsedIndex] = Long.MAX_VALUE;
               }

               delimCurrent = false;
            } else if (c == '-') {
               parsed[parsedIndex] *= -1L;
               delimCurrent = false;
               dashSeen = true;
            } else {
               if (numberFound) {
                  LOG.error((String)"Illegal character parsing string '{}' to long array: {}", (Object)s, (Object)s.charAt(charIndex));
                  return new long[indices.length];
               }

               parsed[parsedIndex] = 0L;
               numeric = false;
            }
         } else {
            delimCurrent = false;
         }
      }
   }

   public static int countStringToLongArray(String s, char delimiter) {
      int charIndex = s.length();
      int numbers = 0;
      boolean delimCurrent = false;
      boolean numeric = true;
      boolean dashSeen = false;

      while(true) {
         while(true) {
            --charIndex;
            if (charIndex <= 0) {
               return numbers + 1;
            }

            int c = s.charAt(charIndex);
            if (c == delimiter) {
               if (!delimCurrent) {
                  if (numeric) {
                     ++numbers;
                  }

                  delimCurrent = true;
                  dashSeen = false;
                  numeric = true;
               }
            } else if (c != '+' && numeric) {
               if (c >= '0' && c <= '9' && !dashSeen) {
                  delimCurrent = false;
               } else if (c == '-') {
                  delimCurrent = false;
                  dashSeen = true;
               } else {
                  if (numbers > 0) {
                     return numbers;
                  }

                  numeric = false;
               }
            } else {
               delimCurrent = false;
            }
         }
      }
   }

   public static String getTextBetweenStrings(String text, String before, String after) {
      String result = "";
      if (text.indexOf(before) >= 0 && text.indexOf(after) >= 0) {
         result = text.substring(text.indexOf(before) + before.length(), text.length());
         result = result.substring(0, result.indexOf(after));
      }

      return result;
   }

   public static long filetimeToUtcMs(long filetime, boolean local) {
      return filetime / 10000L - 11644473600000L - (local ? (long)TZ_OFFSET : 0L);
   }

   public static String parseMmDdYyyyToYyyyMmDD(String dateString) {
      try {
         return String.format("%s-%s-%s", dateString.substring(6, 10), dateString.substring(0, 2), dateString.substring(3, 5));
      } catch (StringIndexOutOfBoundsException var2) {
         return dateString;
      }
   }

   public static OffsetDateTime parseCimDateTimeToOffset(String cimDateTime) {
      try {
         int tzInMinutes = Integer.parseInt(cimDateTime.substring(22));
         LocalTime offsetAsLocalTime = LocalTime.MIDNIGHT.plusMinutes((long)tzInMinutes);
         return OffsetDateTime.parse(cimDateTime.substring(0, 22) + offsetAsLocalTime.format(DateTimeFormatter.ISO_LOCAL_TIME), CIM_FORMAT);
      } catch (NumberFormatException | DateTimeParseException | IndexOutOfBoundsException var3) {
         LOG.trace((String)"Unable to parse {} to CIM DateTime.", (Object)cimDateTime);
         return Constants.UNIX_EPOCH;
      }
   }

   public static boolean filePathStartsWith(List<String> prefixList, String path) {
      Iterator var2 = prefixList.iterator();

      String match;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         match = (String)var2.next();
      } while(!path.equals(match) && !path.startsWith(match + "/"));

      return true;
   }

   public static long parseDecimalMemorySizeToBinary(String size) {
      String[] mem = whitespaces.split(size);
      if (mem.length < 2) {
         Matcher matcher = BYTES_PATTERN.matcher(size.trim());
         if (matcher.find() && matcher.groupCount() == 2) {
            mem = new String[]{matcher.group(1), matcher.group(2)};
         }
      }

      long capacity = parseLongOrDefault(mem[0], 0L);
      if (mem.length == 2 && mem[1].length() > 1) {
         switch (mem[1].charAt(0)) {
            case 'G':
               capacity <<= 30;
               break;
            case 'K':
            case 'k':
               capacity <<= 10;
               break;
            case 'M':
               capacity <<= 20;
               break;
            case 'T':
               capacity <<= 40;
         }
      }

      return capacity;
   }

   public static Pair<String, String> parsePnPDeviceIdToVendorProductId(String pnpDeviceId) {
      Matcher m = VENDOR_PRODUCT_ID.matcher(pnpDeviceId);
      if (m.matches()) {
         String vendorId = "0x" + m.group(1).toLowerCase();
         String productId = "0x" + m.group(2).toLowerCase();
         return new Pair(vendorId, productId);
      } else {
         return null;
      }
   }

   public static long parseLshwResourceString(String resources) {
      long bytes = 0L;
      String[] resourceArray = whitespaces.split(resources);
      String[] var4 = resourceArray;
      int var5 = resourceArray.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String r = var4[var6];
         if (r.startsWith("memory:")) {
            String[] mem = r.substring(7).split("-");
            if (mem.length == 2) {
               try {
                  bytes += Long.parseLong(mem[1], 16) - Long.parseLong(mem[0], 16) + 1L;
               } catch (NumberFormatException var10) {
                  LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)r, (Object)var10);
               }
            }
         }
      }

      return bytes;
   }

   public static Pair<String, String> parseLspciMachineReadable(String line) {
      Matcher matcher = LSPCI_MACHINE_READABLE.matcher(line);
      return matcher.matches() ? new Pair(matcher.group(1), matcher.group(2)) : null;
   }

   public static long parseLspciMemorySize(String line) {
      Matcher matcher = LSPCI_MEMORY_SIZE.matcher(line);
      return matcher.matches() ? parseDecimalMemorySizeToBinary(matcher.group(1) + " " + matcher.group(2) + "B") : 0L;
   }

   public static List<Integer> parseHyphenatedIntList(String str) {
      List<Integer> result = new ArrayList();
      String[] var2 = whitespaces.split(str);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String s = var2[var4];
         int only;
         if (s.contains("-")) {
            only = getFirstIntValue(s);
            int last = getNthIntValue(s, 2);

            for(int i = only; i <= last; ++i) {
               result.add(i);
            }
         } else {
            only = parseIntOrDefault(s, -1);
            if (only >= 0) {
               result.add(only);
            }
         }
      }

      return result;
   }

   public static String parseUtAddrV6toIP(int[] utAddrV6) {
      if (utAddrV6.length != 4) {
         throw new IllegalArgumentException("ut_addr_v6 must have exactly 4 elements");
      } else {
         byte[] ipv4;
         if (utAddrV6[1] == 0 && utAddrV6[2] == 0 && utAddrV6[3] == 0) {
            if (utAddrV6[0] == 0) {
               return "::";
            } else {
               ipv4 = ByteBuffer.allocate(4).putInt(utAddrV6[0]).array();

               try {
                  return InetAddress.getByAddress(ipv4).getHostAddress();
               } catch (UnknownHostException var3) {
                  return "unknown";
               }
            }
         } else {
            ipv4 = ByteBuffer.allocate(16).putInt(utAddrV6[0]).putInt(utAddrV6[1]).putInt(utAddrV6[2]).putInt(utAddrV6[3]).array();

            try {
               return InetAddress.getByAddress(ipv4).getHostAddress().replaceAll("((?:(?:^|:)0+\\b){2,}):?(?!\\S*\\b\\1:0+\\b)(\\S*)", "::$2");
            } catch (UnknownHostException var4) {
               return "unknown";
            }
         }
      }
   }

   public static long hexStringToLong(String hexString, long defaultValue) {
      try {
         return (new BigInteger(hexString, 16)).longValue();
      } catch (NumberFormatException var4) {
         LOG.trace((String)"{} didn't parse. Returning default. {}", (Object)hexString, (Object)var4);
         return defaultValue;
      }
   }

   public static String removeLeadingDots(String dotPrefixedStr) {
      int pos;
      for(pos = 0; pos < dotPrefixedStr.length() && dotPrefixedStr.charAt(pos) == '.'; ++pos) {
      }

      return pos < dotPrefixedStr.length() ? dotPrefixedStr.substring(pos) : "";
   }

   static {
      multipliers.put("Hz", 1L);
      multipliers.put("kHz", 1000L);
      multipliers.put("MHz", 1000000L);
      multipliers.put("GHz", 1000000000L);
      multipliers.put("THz", 1000000000000L);
      multipliers.put("PHz", 1000000000000000L);
      POWERS_OF_TEN = new long[]{1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L};
      HEX_ARRAY = "0123456789ABCDEF".toCharArray();
      CIM_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSSSSZZZZZ", Locale.US);
   }
}
