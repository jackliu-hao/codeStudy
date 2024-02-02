package org.h2.mvstore;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.h2.jdbc.JdbcException;
import org.h2.util.StringUtils;

public final class DataUtils {
   public static final int ERROR_READING_FAILED = 1;
   public static final int ERROR_WRITING_FAILED = 2;
   public static final int ERROR_INTERNAL = 3;
   public static final int ERROR_CLOSED = 4;
   public static final int ERROR_UNSUPPORTED_FORMAT = 5;
   public static final int ERROR_FILE_CORRUPT = 6;
   public static final int ERROR_FILE_LOCKED = 7;
   public static final int ERROR_SERIALIZATION = 8;
   public static final int ERROR_CHUNK_NOT_FOUND = 9;
   public static final int ERROR_BLOCK_NOT_FOUND = 50;
   public static final int ERROR_TRANSACTION_CORRUPT = 100;
   public static final int ERROR_TRANSACTION_LOCKED = 101;
   public static final int ERROR_TOO_MANY_OPEN_TRANSACTIONS = 102;
   public static final int ERROR_TRANSACTION_ILLEGAL_STATE = 103;
   public static final int ERROR_TRANSACTION_TOO_BIG = 104;
   public static final int ERROR_TRANSACTIONS_DEADLOCK = 105;
   public static final int ERROR_UNKNOWN_DATA_TYPE = 106;
   public static final int PAGE_TYPE_LEAF = 0;
   public static final int PAGE_TYPE_NODE = 1;
   public static final int PAGE_COMPRESSED = 2;
   public static final int PAGE_COMPRESSED_HIGH = 6;
   public static final int PAGE_HAS_PAGE_NO = 8;
   public static final int MAX_VAR_INT_LEN = 5;
   public static final int MAX_VAR_LONG_LEN = 10;
   public static final int COMPRESSED_VAR_INT_MAX = 2097151;
   public static final long COMPRESSED_VAR_LONG_MAX = 562949953421311L;
   public static final int PAGE_LARGE = 2097152;
   public static final String META_CHUNK = "chunk.";
   public static final String META_ROOT = "root.";
   public static final String META_NAME = "name.";
   public static final String META_MAP = "map.";

   public static int getVarIntLen(int var0) {
      if ((var0 & -128) == 0) {
         return 1;
      } else if ((var0 & -16384) == 0) {
         return 2;
      } else if ((var0 & -2097152) == 0) {
         return 3;
      } else {
         return (var0 & -268435456) == 0 ? 4 : 5;
      }
   }

   public static int getVarLongLen(long var0) {
      int var2 = 1;

      while(true) {
         var0 >>>= 7;
         if (var0 == 0L) {
            return var2;
         }

         ++var2;
      }
   }

   public static int readVarInt(ByteBuffer var0) {
      byte var1 = var0.get();
      return var1 >= 0 ? var1 : readVarIntRest(var0, var1);
   }

   private static int readVarIntRest(ByteBuffer var0, int var1) {
      int var2 = var1 & 127;
      byte var3 = var0.get();
      if (var3 >= 0) {
         return var2 | var3 << 7;
      } else {
         var2 |= (var3 & 127) << 7;
         var3 = var0.get();
         if (var3 >= 0) {
            return var2 | var3 << 14;
         } else {
            var2 |= (var3 & 127) << 14;
            var3 = var0.get();
            if (var3 >= 0) {
               return var2 | var3 << 21;
            } else {
               var2 |= (var3 & 127) << 21 | var0.get() << 28;
               return var2;
            }
         }
      }
   }

   public static long readVarLong(ByteBuffer var0) {
      long var1 = (long)var0.get();
      if (var1 >= 0L) {
         return var1;
      } else {
         var1 &= 127L;

         for(int var3 = 7; var3 < 64; var3 += 7) {
            long var4 = (long)var0.get();
            var1 |= (var4 & 127L) << var3;
            if (var4 >= 0L) {
               break;
            }
         }

         return var1;
      }
   }

   public static void writeVarInt(OutputStream var0, int var1) throws IOException {
      while((var1 & -128) != 0) {
         var0.write((byte)(var1 | 128));
         var1 >>>= 7;
      }

      var0.write((byte)var1);
   }

   public static void writeVarInt(ByteBuffer var0, int var1) {
      while((var1 & -128) != 0) {
         var0.put((byte)(var1 | 128));
         var1 >>>= 7;
      }

      var0.put((byte)var1);
   }

   public static void writeStringData(ByteBuffer var0, String var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1.charAt(var3);
         if (var4 < 128) {
            var0.put((byte)var4);
         } else if (var4 >= 2048) {
            var0.put((byte)(224 | var4 >> 12));
            var0.put((byte)(var4 >> 6 & 63));
            var0.put((byte)(var4 & 63));
         } else {
            var0.put((byte)(192 | var4 >> 6));
            var0.put((byte)(var4 & 63));
         }
      }

   }

   public static String readString(ByteBuffer var0) {
      return readString(var0, readVarInt(var0));
   }

   public static String readString(ByteBuffer var0, int var1) {
      char[] var2 = new char[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         int var4 = var0.get() & 255;
         if (var4 < 128) {
            var2[var3] = (char)var4;
         } else if (var4 >= 224) {
            var2[var3] = (char)(((var4 & 15) << 12) + ((var0.get() & 63) << 6) + (var0.get() & 63));
         } else {
            var2[var3] = (char)(((var4 & 31) << 6) + (var0.get() & 63));
         }
      }

      return new String(var2);
   }

   public static void writeVarLong(ByteBuffer var0, long var1) {
      while((var1 & -128L) != 0L) {
         var0.put((byte)((int)(var1 | 128L)));
         var1 >>>= 7;
      }

      var0.put((byte)((int)var1));
   }

   public static void writeVarLong(OutputStream var0, long var1) throws IOException {
      while((var1 & -128L) != 0L) {
         var0.write((byte)((int)(var1 | 128L)));
         var1 >>>= 7;
      }

      var0.write((byte)((int)var1));
   }

   public static void copyWithGap(Object var0, Object var1, int var2, int var3) {
      if (var3 > 0) {
         System.arraycopy(var0, 0, var1, 0, var3);
      }

      if (var3 < var2) {
         System.arraycopy(var0, var3, var1, var3 + 1, var2 - var3);
      }

   }

   public static void copyExcept(Object var0, Object var1, int var2, int var3) {
      if (var3 > 0 && var2 > 0) {
         System.arraycopy(var0, 0, var1, 0, var3);
      }

      if (var3 < var2) {
         System.arraycopy(var0, var3 + 1, var1, var3, var2 - var3 - 1);
      }

   }

   public static void readFully(FileChannel var0, long var1, ByteBuffer var3) {
      try {
         do {
            int var4 = var0.read(var3, var1);
            if (var4 < 0) {
               throw new EOFException();
            }

            var1 += (long)var4;
         } while(var3.remaining() > 0);

         var3.rewind();
      } catch (IOException var9) {
         long var5;
         try {
            var5 = var0.size();
         } catch (IOException var8) {
            var5 = -1L;
         }

         throw newMVStoreException(1, "Reading from file {0} failed at {1} (length {2}), read {3}, remaining {4}", var0, var1, var5, var3.position(), var3.remaining(), var9);
      }
   }

   public static void writeFully(FileChannel var0, long var1, ByteBuffer var3) {
      try {
         int var4 = 0;

         do {
            int var5 = var0.write(var3, var1 + (long)var4);
            var4 += var5;
         } while(var3.remaining() > 0);

      } catch (IOException var6) {
         throw newMVStoreException(2, "Writing to {0} failed; length {1} at {2}", var0, var3.remaining(), var1, var6);
      }
   }

   public static int encodeLength(int var0) {
      if (var0 <= 32) {
         return 0;
      } else {
         int var1 = Integer.numberOfLeadingZeros(var0);
         int var2 = var0 << var1 + 1;
         var1 += var1;
         if ((var2 & Integer.MIN_VALUE) != 0) {
            --var1;
         }

         if (var2 << 1 != 0) {
            --var1;
         }

         var1 = Math.min(31, 52 - var1);
         return var1;
      }
   }

   public static int getPageChunkId(long var0) {
      return (int)(var0 >>> 38);
   }

   public static int getPageMapId(long var0) {
      return (int)(var0 >>> 38);
   }

   public static int getPageMaxLength(long var0) {
      int var2 = (int)(var0 >> 1 & 31L);
      return decodePageLength(var2);
   }

   public static int decodePageLength(int var0) {
      return var0 == 31 ? 2097152 : 2 + (var0 & 1) << (var0 >> 1) + 4;
   }

   public static int getPageOffset(long var0) {
      return (int)(var0 >> 6);
   }

   public static int getPageType(long var0) {
      return (int)var0 & 1;
   }

   public static boolean isLeafPosition(long var0) {
      return getPageType(var0) == 0;
   }

   public static boolean isPageSaved(long var0) {
      return (var0 & -2L) != 0L;
   }

   static boolean isPageRemoved(long var0) {
      return var0 == 1L;
   }

   public static long getPagePos(int var0, int var1, int var2, int var3) {
      long var4 = (long)var0 << 38;
      var4 |= (long)var1 << 6;
      var4 |= (long)(encodeLength(var2) << 1);
      var4 |= (long)var3;
      return var4;
   }

   public static long getPagePos(int var0, long var1) {
      return var1 & 274877906943L | (long)var0 << 38;
   }

   public static long getTocElement(int var0, int var1, int var2, int var3) {
      long var4 = (long)var0 << 38;
      var4 |= (long)var1 << 6;
      var4 |= (long)(encodeLength(var2) << 1);
      var4 |= (long)var3;
      return var4;
   }

   public static short getCheckValue(int var0) {
      return (short)(var0 >> 16 ^ var0);
   }

   public static StringBuilder appendMap(StringBuilder var0, HashMap<String, ?> var1) {
      Object[] var2 = var1.keySet().toArray();
      Arrays.sort(var2);
      Object[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         String var7 = (String)var6;
         Object var8 = var1.get(var7);
         if (var8 instanceof Long) {
            appendMap(var0, var7, (Long)var8);
         } else if (var8 instanceof Integer) {
            appendMap(var0, var7, (Integer)var8);
         } else {
            appendMap(var0, var7, var8.toString());
         }
      }

      return var0;
   }

   private static StringBuilder appendMapKey(StringBuilder var0, String var1) {
      if (var0.length() > 0) {
         var0.append(',');
      }

      return var0.append(var1).append(':');
   }

   public static void appendMap(StringBuilder var0, String var1, String var2) {
      appendMapKey(var0, var1);
      if (var2.indexOf(44) < 0 && var2.indexOf(34) < 0) {
         var0.append(var2);
      } else {
         var0.append('"');
         int var3 = 0;

         for(int var4 = var2.length(); var3 < var4; ++var3) {
            char var5 = var2.charAt(var3);
            if (var5 == '"') {
               var0.append('\\');
            }

            var0.append(var5);
         }

         var0.append('"');
      }

   }

   public static void appendMap(StringBuilder var0, String var1, long var2) {
      appendMapKey(var0, var1).append(Long.toHexString(var2));
   }

   public static void appendMap(StringBuilder var0, String var1, int var2) {
      appendMapKey(var0, var1).append(Integer.toHexString(var2));
   }

   private static int parseMapValue(StringBuilder var0, String var1, int var2, int var3) {
      label32:
      while(true) {
         if (var2 < var3) {
            char var4 = var1.charAt(var2++);
            if (var4 != ',') {
               if (var4 == '"') {
                  while(true) {
                     if (var2 >= var3) {
                        continue label32;
                     }

                     var4 = var1.charAt(var2++);
                     if (var4 == '\\') {
                        if (var2 == var3) {
                           throw newMVStoreException(6, "Not a map: {0}", var1);
                        }

                        var4 = var1.charAt(var2++);
                     } else if (var4 == '"') {
                        continue label32;
                     }

                     var0.append(var4);
                  }
               }

               var0.append(var4);
               continue;
            }
         }

         return var2;
      }
   }

   public static HashMap<String, String> parseMap(String var0) {
      HashMap var1 = new HashMap();
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;
      int var4 = var0.length();

      while(var3 < var4) {
         int var5 = var3;
         var3 = var0.indexOf(58, var3);
         if (var3 < 0) {
            throw newMVStoreException(6, "Not a map: {0}", var0);
         }

         String var6 = var0.substring(var5, var3++);
         var3 = parseMapValue(var2, var0, var3, var4);
         var1.put(var6, var2.toString());
         var2.setLength(0);
      }

      return var1;
   }

   static HashMap<String, String> parseChecksummedMap(byte[] var0) {
      int var1 = 0;

      int var2;
      for(var2 = var0.length; var1 < var2 && var0[var1] <= 32; ++var1) {
      }

      while(var1 < var2 && var0[var2 - 1] <= 32) {
         --var2;
      }

      String var3 = new String(var0, var1, var2 - var1, StandardCharsets.ISO_8859_1);
      HashMap var4 = new HashMap();
      StringBuilder var5 = new StringBuilder();
      int var6 = 0;
      int var7 = var3.length();

      while(var6 < var7) {
         int var8 = var6;
         var6 = var3.indexOf(58, var6);
         if (var6 < 0) {
            return null;
         }

         if (var6 - var8 == 8 && var3.regionMatches(var8, "fletcher", 0, 8)) {
            parseMapValue(var5, var3, var6 + 1, var7);
            int var10 = (int)Long.parseLong(var5.toString(), 16);
            if (var10 == getFletcher32(var0, var1, var8 - 1)) {
               return var4;
            }

            return null;
         }

         String var9 = var3.substring(var8, var6++);
         var6 = parseMapValue(var5, var3, var6, var7);
         var4.put(var9, var5.toString());
         var5.setLength(0);
      }

      return null;
   }

   public static String getMapName(String var0) {
      return getFromMap(var0, "name");
   }

   public static String getFromMap(String var0, String var1) {
      int var2 = var1.length();
      int var3 = 0;
      int var4 = var0.length();

      label48:
      while(var3 < var4) {
         int var5 = var3;
         var3 = var0.indexOf(58, var3);
         if (var3 < 0) {
            throw newMVStoreException(6, "Not a map: {0}", var0);
         }

         if (var3++ - var5 == var2 && var0.regionMatches(var5, var1, 0, var2)) {
            StringBuilder var7 = new StringBuilder();
            parseMapValue(var7, var0, var3, var4);
            return var7.toString();
         }

         while(true) {
            char var6;
            do {
               if (var3 >= var4) {
                  continue label48;
               }

               var6 = var0.charAt(var3++);
               if (var6 == ',') {
                  continue label48;
               }
            } while(var6 != '"');

            while(var3 < var4) {
               var6 = var0.charAt(var3++);
               if (var6 == '\\') {
                  if (var3++ == var4) {
                     throw newMVStoreException(6, "Not a map: {0}", var0);
                  }
               } else if (var6 == '"') {
                  break;
               }
            }
         }
      }

      return null;
   }

   public static int getFletcher32(byte[] var0, int var1, int var2) {
      int var3 = 65535;
      int var4 = 65535;
      int var5 = var1;

      int var7;
      for(int var6 = var1 + (var2 & -2); var5 < var6; var4 = (var4 & '\uffff') + (var4 >>> 16)) {
         int var8;
         for(var7 = Math.min(var5 + 720, var6); var5 < var7; var4 += var3 += var8) {
            var8 = (var0[var5++] & 255) << 8 | var0[var5++] & 255;
         }

         var3 = (var3 & '\uffff') + (var3 >>> 16);
      }

      if ((var2 & 1) != 0) {
         var7 = (var0[var5] & 255) << 8;
         var4 += var3 += var7;
      }

      var3 = (var3 & '\uffff') + (var3 >>> 16);
      var4 = (var4 & '\uffff') + (var4 >>> 16);
      return var4 << 16 | var3;
   }

   public static void checkArgument(boolean var0, String var1, Object... var2) {
      if (!var0) {
         throw newIllegalArgumentException(var1, var2);
      }
   }

   public static IllegalArgumentException newIllegalArgumentException(String var0, Object... var1) {
      return (IllegalArgumentException)initCause(new IllegalArgumentException(formatMessage(0, var0, var1)), var1);
   }

   public static UnsupportedOperationException newUnsupportedOperationException(String var0) {
      return new UnsupportedOperationException(formatMessage(0, var0));
   }

   public static MVStoreException newMVStoreException(int var0, String var1, Object... var2) {
      return (MVStoreException)initCause(new MVStoreException(var0, formatMessage(var0, var1, var2)), var2);
   }

   private static <T extends Exception> T initCause(T var0, Object... var1) {
      int var2 = var1.length;
      if (var2 > 0) {
         Object var3 = var1[var2 - 1];
         if (var3 instanceof Throwable) {
            var0.initCause((Throwable)var3);
         }
      }

      return var0;
   }

   public static String formatMessage(int var0, String var1, Object... var2) {
      var2 = (Object[])var2.clone();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Object var4 = var2[var3];
         if (!(var4 instanceof Exception)) {
            String var5 = var4 == null ? "null" : var4.toString();
            if (var5.length() > 1000) {
               var5 = var5.substring(0, 1000) + "...";
            }

            var2[var3] = var5;
         }
      }

      return MessageFormat.format(var1, var2) + " [" + 2 + "." + 1 + "." + 210 + "/" + var0 + "]";
   }

   public static long readHexLong(Map<String, ?> var0, String var1, long var2) {
      Object var4 = var0.get(var1);
      if (var4 == null) {
         return var2;
      } else if (var4 instanceof Long) {
         return (Long)var4;
      } else {
         try {
            return parseHexLong((String)var4);
         } catch (NumberFormatException var6) {
            throw newMVStoreException(6, "Error parsing the value {0}", var4, var6);
         }
      }
   }

   public static long parseHexLong(String var0) {
      try {
         return var0.length() == 16 ? Long.parseLong(var0.substring(0, 8), 16) << 32 | Long.parseLong(var0.substring(8, 16), 16) : Long.parseLong(var0, 16);
      } catch (NumberFormatException var2) {
         throw newMVStoreException(6, "Error parsing the value {0}", var0, var2);
      }
   }

   public static int parseHexInt(String var0) {
      try {
         return (int)Long.parseLong(var0, 16);
      } catch (NumberFormatException var2) {
         throw newMVStoreException(6, "Error parsing the value {0}", var0, var2);
      }
   }

   static int readHexInt(Map<String, ?> var0, String var1, int var2) {
      Object var3 = var0.get(var1);
      if (var3 == null) {
         return var2;
      } else if (var3 instanceof Integer) {
         return (Integer)var3;
      } else {
         try {
            return (int)Long.parseLong((String)var3, 16);
         } catch (NumberFormatException var5) {
            throw newMVStoreException(6, "Error parsing the value {0}", var3, var5);
         }
      }
   }

   static byte[] parseHexBytes(Map<String, ?> var0, String var1) {
      Object var2 = var0.get(var1);
      return var2 == null ? null : StringUtils.convertHexToBytes((String)var2);
   }

   static int getConfigParam(Map<String, ?> var0, String var1, int var2) {
      Object var3 = var0.get(var1);
      if (var3 instanceof Number) {
         return ((Number)var3).intValue();
      } else {
         if (var3 != null) {
            try {
               return Integer.decode(var3.toString());
            } catch (NumberFormatException var5) {
            }
         }

         return var2;
      }
   }

   public static IOException convertToIOException(Throwable var0) {
      if (var0 instanceof IOException) {
         return (IOException)var0;
      } else {
         if (var0 instanceof JdbcException && var0.getCause() != null) {
            var0 = var0.getCause();
         }

         return new IOException(var0.toString(), var0);
      }
   }
}
