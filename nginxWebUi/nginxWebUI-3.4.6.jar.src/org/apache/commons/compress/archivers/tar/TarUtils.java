/*     */ package org.apache.commons.compress.archivers.tar;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*     */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*     */ import org.apache.commons.compress.utils.IOUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TarUtils
/*     */ {
/*     */   private static final int BYTE_MASK = 255;
/*  54 */   static final ZipEncoding DEFAULT_ENCODING = ZipEncodingHelper.getZipEncoding(null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   static final ZipEncoding FALLBACK_ENCODING = new ZipEncoding() {
/*     */       public boolean canEncode(String name) {
/*  62 */         return true;
/*     */       }
/*     */       
/*     */       public ByteBuffer encode(String name) {
/*  66 */         int length = name.length();
/*  67 */         byte[] buf = new byte[length];
/*     */ 
/*     */         
/*  70 */         for (int i = 0; i < length; i++) {
/*  71 */           buf[i] = (byte)name.charAt(i);
/*     */         }
/*  73 */         return ByteBuffer.wrap(buf);
/*     */       }
/*     */ 
/*     */       
/*     */       public String decode(byte[] buffer) {
/*  78 */         int length = buffer.length;
/*  79 */         StringBuilder result = new StringBuilder(length);
/*     */         
/*  81 */         for (byte b : buffer) {
/*  82 */           if (b == 0) {
/*     */             break;
/*     */           }
/*  85 */           result.append((char)(b & 0xFF));
/*     */         } 
/*     */         
/*  88 */         return result.toString();
/*     */       }
/*     */     };
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
/*     */   
/*     */   public static long parseOctal(byte[] buffer, int offset, int length) {
/* 118 */     long result = 0L;
/* 119 */     int end = offset + length;
/* 120 */     int start = offset;
/*     */     
/* 122 */     if (length < 2) {
/* 123 */       throw new IllegalArgumentException("Length " + length + " must be at least 2");
/*     */     }
/*     */     
/* 126 */     if (buffer[start] == 0) {
/* 127 */       return 0L;
/*     */     }
/*     */ 
/*     */     
/* 131 */     while (start < end && 
/* 132 */       buffer[start] == 32)
/*     */     {
/*     */       
/* 135 */       start++;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 142 */     byte trailer = buffer[end - 1];
/* 143 */     while (start < end && (trailer == 0 || trailer == 32)) {
/* 144 */       end--;
/* 145 */       trailer = buffer[end - 1];
/*     */     } 
/*     */     
/* 148 */     for (; start < end; start++) {
/* 149 */       byte currentByte = buffer[start];
/*     */       
/* 151 */       if (currentByte < 48 || currentByte > 55) {
/* 152 */         throw new IllegalArgumentException(
/* 153 */             exceptionMessage(buffer, offset, length, start, currentByte));
/*     */       }
/* 155 */       result = (result << 3L) + (currentByte - 48);
/*     */     } 
/*     */ 
/*     */     
/* 159 */     return result;
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
/*     */   public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
/* 182 */     if ((buffer[offset] & 0x80) == 0) {
/* 183 */       return parseOctal(buffer, offset, length);
/*     */     }
/* 185 */     boolean negative = (buffer[offset] == -1);
/* 186 */     if (length < 9) {
/* 187 */       return parseBinaryLong(buffer, offset, length, negative);
/*     */     }
/* 189 */     return parseBinaryBigInteger(buffer, offset, length, negative);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
/* 195 */     if (length >= 9) {
/* 196 */       throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 201 */     long val = 0L;
/* 202 */     for (int i = 1; i < length; i++) {
/* 203 */       val = (val << 8L) + (buffer[offset + i] & 0xFF);
/*     */     }
/* 205 */     if (negative) {
/*     */       
/* 207 */       val--;
/* 208 */       val ^= (long)Math.pow(2.0D, (length - 1) * 8.0D) - 1L;
/*     */     } 
/* 210 */     return negative ? -val : val;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
/* 217 */     byte[] remainder = new byte[length - 1];
/* 218 */     System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
/* 219 */     BigInteger val = new BigInteger(remainder);
/* 220 */     if (negative)
/*     */     {
/* 222 */       val = val.add(BigInteger.valueOf(-1L)).not();
/*     */     }
/* 224 */     if (val.bitLength() > 63) {
/* 225 */       throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 230 */     return negative ? -val.longValue() : val.longValue();
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
/*     */   
/*     */   public static boolean parseBoolean(byte[] buffer, int offset) {
/* 244 */     return (buffer[offset] == 1);
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
/*     */   private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
/* 257 */     String string = new String(buffer, offset, length);
/*     */     
/* 259 */     string = string.replace("\000", "{NUL}");
/* 260 */     return "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
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
/*     */   
/*     */   public static String parseName(byte[] buffer, int offset, int length) {
/*     */     try {
/* 275 */       return parseName(buffer, offset, length, DEFAULT_ENCODING);
/* 276 */     } catch (IOException ex) {
/*     */       try {
/* 278 */         return parseName(buffer, offset, length, FALLBACK_ENCODING);
/* 279 */       } catch (IOException ex2) {
/*     */         
/* 281 */         throw new RuntimeException(ex2);
/*     */       } 
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
/* 304 */     int len = 0;
/* 305 */     for (int i = offset; len < length && buffer[i] != 0; i++) {
/* 306 */       len++;
/*     */     }
/* 308 */     if (len > 0) {
/* 309 */       byte[] b = new byte[len];
/* 310 */       System.arraycopy(buffer, offset, b, 0, len);
/* 311 */       return encoding.decode(b);
/*     */     } 
/* 313 */     return "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TarArchiveStructSparse parseSparse(byte[] buffer, int offset) {
/* 324 */     long sparseOffset = parseOctalOrBinary(buffer, offset, 12);
/* 325 */     long sparseNumbytes = parseOctalOrBinary(buffer, offset + 12, 12);
/*     */     
/* 327 */     return new TarArchiveStructSparse(sparseOffset, sparseNumbytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static List<TarArchiveStructSparse> readSparseStructs(byte[] buffer, int offset, int entries) throws IOException {
/* 335 */     List<TarArchiveStructSparse> sparseHeaders = new ArrayList<>();
/* 336 */     for (int i = 0; i < entries; i++) {
/*     */       
/*     */       try {
/* 339 */         TarArchiveStructSparse sparseHeader = parseSparse(buffer, offset + i * 24);
/*     */         
/* 341 */         if (sparseHeader.getOffset() < 0L) {
/* 342 */           throw new IOException("Corrupted TAR archive, sparse entry with negative offset");
/*     */         }
/* 344 */         if (sparseHeader.getNumbytes() < 0L) {
/* 345 */           throw new IOException("Corrupted TAR archive, sparse entry with negative numbytes");
/*     */         }
/* 347 */         sparseHeaders.add(sparseHeader);
/* 348 */       } catch (IllegalArgumentException ex) {
/*     */         
/* 350 */         throw new IOException("Corrupted TAR archive, sparse entry is invalid", ex);
/*     */       } 
/*     */     } 
/* 353 */     return Collections.unmodifiableList(sparseHeaders);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
/*     */     try {
/* 373 */       return formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
/* 374 */     } catch (IOException ex) {
/*     */       try {
/* 376 */         return formatNameBytes(name, buf, offset, length, FALLBACK_ENCODING);
/*     */       }
/* 378 */       catch (IOException ex2) {
/*     */         
/* 380 */         throw new RuntimeException(ex2);
/*     */       } 
/*     */     } 
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
/*     */   public static int formatNameBytes(String name, byte[] buf, int offset, int length, ZipEncoding encoding) throws IOException {
/* 407 */     int len = name.length();
/* 408 */     ByteBuffer b = encoding.encode(name);
/* 409 */     while (b.limit() > length && len > 0) {
/* 410 */       b = encoding.encode(name.substring(0, --len));
/*     */     }
/* 412 */     int limit = b.limit() - b.position();
/* 413 */     System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);
/*     */ 
/*     */     
/* 416 */     for (int i = limit; i < length; i++) {
/* 417 */       buf[offset + i] = 0;
/*     */     }
/*     */     
/* 420 */     return offset + length;
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
/*     */   
/*     */   public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
/* 434 */     int remaining = length;
/* 435 */     remaining--;
/* 436 */     if (value == 0L) {
/* 437 */       buffer[offset + remaining--] = 48;
/*     */     } else {
/* 439 */       long val = value;
/* 440 */       for (; remaining >= 0 && val != 0L; remaining--) {
/*     */         
/* 442 */         buffer[offset + remaining] = (byte)(48 + (byte)(int)(val & 0x7L));
/* 443 */         val >>>= 3L;
/*     */       } 
/*     */       
/* 446 */       if (val != 0L) {
/* 447 */         throw new IllegalArgumentException(value + "=" + 
/* 448 */             Long.toOctalString(value) + " will not fit in octal number buffer of length " + length);
/*     */       }
/*     */     } 
/*     */     
/* 452 */     for (; remaining >= 0; remaining--) {
/* 453 */       buffer[offset + remaining] = 48;
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int formatOctalBytes(long value, byte[] buf, int offset, int length) {
/* 473 */     int idx = length - 2;
/* 474 */     formatUnsignedOctalString(value, buf, offset, idx);
/*     */     
/* 476 */     buf[offset + idx++] = 32;
/* 477 */     buf[offset + idx] = 0;
/*     */     
/* 479 */     return offset + length;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length) {
/* 498 */     int idx = length - 1;
/*     */     
/* 500 */     formatUnsignedOctalString(value, buf, offset, idx);
/* 501 */     buf[offset + idx] = 32;
/*     */     
/* 503 */     return offset + length;
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
/*     */   public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length) {
/* 527 */     long maxAsOctalChar = (length == 8) ? 2097151L : 8589934591L;
/*     */     
/* 529 */     boolean negative = (value < 0L);
/* 530 */     if (!negative && value <= maxAsOctalChar) {
/* 531 */       return formatLongOctalBytes(value, buf, offset, length);
/*     */     }
/*     */     
/* 534 */     if (length < 9) {
/* 535 */       formatLongBinary(value, buf, offset, length, negative);
/*     */     } else {
/* 537 */       formatBigIntegerBinary(value, buf, offset, length, negative);
/*     */     } 
/*     */     
/* 540 */     buf[offset] = (byte)(negative ? 255 : 128);
/* 541 */     return offset + length;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void formatLongBinary(long value, byte[] buf, int offset, int length, boolean negative) {
/* 547 */     int bits = (length - 1) * 8;
/* 548 */     long max = 1L << bits;
/* 549 */     long val = Math.abs(value);
/* 550 */     if (val < 0L || val >= max) {
/* 551 */       throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field.");
/*     */     }
/*     */     
/* 554 */     if (negative) {
/* 555 */       val ^= max - 1L;
/* 556 */       val++;
/* 557 */       val |= 255L << bits;
/*     */     } 
/* 559 */     for (int i = offset + length - 1; i >= offset; i--) {
/* 560 */       buf[i] = (byte)(int)val;
/* 561 */       val >>= 8L;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void formatBigIntegerBinary(long value, byte[] buf, int offset, int length, boolean negative) {
/* 569 */     BigInteger val = BigInteger.valueOf(value);
/* 570 */     byte[] b = val.toByteArray();
/* 571 */     int len = b.length;
/* 572 */     if (len > length - 1) {
/* 573 */       throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field.");
/*     */     }
/*     */     
/* 576 */     int off = offset + length - len;
/* 577 */     System.arraycopy(b, 0, buf, off, len);
/* 578 */     byte fill = (byte)(negative ? 255 : 0);
/* 579 */     for (int i = offset + 1; i < off; i++) {
/* 580 */       buf[i] = fill;
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
/* 600 */     int idx = length - 2;
/* 601 */     formatUnsignedOctalString(value, buf, offset, idx);
/*     */     
/* 603 */     buf[offset + idx++] = 0;
/* 604 */     buf[offset + idx] = 32;
/*     */     
/* 606 */     return offset + length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long computeCheckSum(byte[] buf) {
/* 616 */     long sum = 0L;
/*     */     
/* 618 */     for (byte element : buf) {
/* 619 */       sum += (0xFF & element);
/*     */     }
/*     */     
/* 622 */     return sum;
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
/*     */   public static boolean verifyCheckSum(byte[] header) {
/* 651 */     long storedSum = parseOctal(header, 148, 8);
/* 652 */     long unsignedSum = 0L;
/* 653 */     long signedSum = 0L;
/*     */     
/* 655 */     for (int i = 0; i < header.length; i++) {
/* 656 */       byte b = header[i];
/* 657 */       if (148 <= i && i < 156) {
/* 658 */         b = 32;
/*     */       }
/* 660 */       unsignedSum += (0xFF & b);
/* 661 */       signedSum += b;
/*     */     } 
/* 663 */     return (storedSum == unsignedSum || storedSum == signedSum);
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
/*     */   @Deprecated
/*     */   protected static Map<String, String> parsePaxHeaders(InputStream inputStream, List<TarArchiveStructSparse> sparseHeaders, Map<String, String> globalPaxHeaders) throws IOException {
/* 693 */     return parsePaxHeaders(inputStream, sparseHeaders, globalPaxHeaders, -1L);
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
/*     */   protected static Map<String, String> parsePaxHeaders(InputStream inputStream, List<TarArchiveStructSparse> sparseHeaders, Map<String, String> globalPaxHeaders, long headerSize) throws IOException {
/*     */     int ch;
/* 724 */     Map<String, String> headers = new HashMap<>(globalPaxHeaders);
/* 725 */     Long offset = null;
/*     */     
/* 727 */     int totalRead = 0;
/*     */     
/*     */     do {
/* 730 */       int len = 0;
/* 731 */       int read = 0;
/* 732 */       while ((ch = inputStream.read()) != -1) {
/* 733 */         read++;
/* 734 */         totalRead++;
/* 735 */         if (ch == 10) {
/*     */           break;
/*     */         }
/* 738 */         if (ch == 32) {
/*     */           
/* 740 */           ByteArrayOutputStream coll = new ByteArrayOutputStream();
/* 741 */           while ((ch = inputStream.read()) != -1) {
/* 742 */             read++;
/* 743 */             totalRead++;
/* 744 */             if (totalRead < 0 || (headerSize >= 0L && totalRead >= headerSize)) {
/*     */               break;
/*     */             }
/* 747 */             if (ch == 61) {
/* 748 */               String keyword = coll.toString("UTF-8");
/*     */               
/* 750 */               int restLen = len - read;
/* 751 */               if (restLen <= 1) {
/* 752 */                 headers.remove(keyword); break;
/* 753 */               }  if (headerSize >= 0L && restLen > headerSize - totalRead) {
/* 754 */                 throw new IOException("Paxheader value size " + restLen + " exceeds size of header record");
/*     */               }
/*     */               
/* 757 */               byte[] rest = IOUtils.readRange(inputStream, restLen);
/* 758 */               int got = rest.length;
/* 759 */               if (got != restLen) {
/* 760 */                 throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got);
/*     */               }
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 766 */               totalRead += restLen;
/*     */               
/* 768 */               if (rest[restLen - 1] != 10) {
/* 769 */                 throw new IOException("Failed to read Paxheader.Value should end with a newline");
/*     */               }
/*     */               
/* 772 */               String value = new String(rest, 0, restLen - 1, StandardCharsets.UTF_8);
/*     */               
/* 774 */               headers.put(keyword, value);
/*     */ 
/*     */               
/* 777 */               if (keyword.equals("GNU.sparse.offset")) {
/* 778 */                 if (offset != null)
/*     */                 {
/* 780 */                   sparseHeaders.add(new TarArchiveStructSparse(offset.longValue(), 0L));
/*     */                 }
/*     */                 try {
/* 783 */                   offset = Long.valueOf(value);
/* 784 */                 } catch (NumberFormatException ex) {
/* 785 */                   throw new IOException("Failed to read Paxheader.GNU.sparse.offset contains a non-numeric value");
/*     */                 } 
/*     */                 
/* 788 */                 if (offset.longValue() < 0L) {
/* 789 */                   throw new IOException("Failed to read Paxheader.GNU.sparse.offset contains negative value");
/*     */                 }
/*     */               } 
/*     */ 
/*     */ 
/*     */               
/* 795 */               if (keyword.equals("GNU.sparse.numbytes")) {
/* 796 */                 long numbytes; if (offset == null) {
/* 797 */                   throw new IOException("Failed to read Paxheader.GNU.sparse.offset is expected before GNU.sparse.numbytes shows up.");
/*     */                 }
/*     */ 
/*     */                 
/*     */                 try {
/* 802 */                   numbytes = Long.parseLong(value);
/* 803 */                 } catch (NumberFormatException ex) {
/* 804 */                   throw new IOException("Failed to read Paxheader.GNU.sparse.numbytes contains a non-numeric value.");
/*     */                 } 
/*     */                 
/* 807 */                 if (numbytes < 0L) {
/* 808 */                   throw new IOException("Failed to read Paxheader.GNU.sparse.numbytes contains negative value");
/*     */                 }
/*     */                 
/* 811 */                 sparseHeaders.add(new TarArchiveStructSparse(offset.longValue(), numbytes));
/* 812 */                 offset = null;
/*     */               } 
/*     */               
/*     */               break;
/*     */             } 
/* 817 */             coll.write((byte)ch);
/*     */           } 
/*     */           
/*     */           break;
/*     */         } 
/*     */         
/* 823 */         if (ch < 48 || ch > 57) {
/* 824 */           throw new IOException("Failed to read Paxheader. Encountered a non-number while reading length");
/*     */         }
/*     */         
/* 827 */         len *= 10;
/* 828 */         len += ch - 48;
/*     */       } 
/* 830 */     } while (ch != -1);
/*     */ 
/*     */ 
/*     */     
/* 834 */     if (offset != null)
/*     */     {
/* 836 */       sparseHeaders.add(new TarArchiveStructSparse(offset.longValue(), 0L));
/*     */     }
/* 838 */     return headers;
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected static List<TarArchiveStructSparse> parsePAX01SparseHeaders(String sparseMap) {
/*     */     try {
/* 855 */       return parseFromPAX01SparseHeaders(sparseMap);
/* 856 */     } catch (IOException ex) {
/* 857 */       throw new RuntimeException(ex.getMessage(), ex);
/*     */     } 
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
/*     */ 
/*     */   
/*     */   protected static List<TarArchiveStructSparse> parseFromPAX01SparseHeaders(String sparseMap) throws IOException {
/* 873 */     List<TarArchiveStructSparse> sparseHeaders = new ArrayList<>();
/* 874 */     String[] sparseHeaderStrings = sparseMap.split(",");
/* 875 */     if (sparseHeaderStrings.length % 2 == 1) {
/* 876 */       throw new IOException("Corrupted TAR archive. Bad format in GNU.sparse.map PAX Header");
/*     */     }
/*     */     
/* 879 */     for (int i = 0; i < sparseHeaderStrings.length; i += 2) {
/*     */       long sparseOffset, sparseNumbytes;
/*     */       try {
/* 882 */         sparseOffset = Long.parseLong(sparseHeaderStrings[i]);
/* 883 */       } catch (NumberFormatException ex) {
/* 884 */         throw new IOException("Corrupted TAR archive. Sparse struct offset contains a non-numeric value");
/*     */       } 
/*     */       
/* 887 */       if (sparseOffset < 0L) {
/* 888 */         throw new IOException("Corrupted TAR archive. Sparse struct offset contains negative value");
/*     */       }
/*     */ 
/*     */       
/*     */       try {
/* 893 */         sparseNumbytes = Long.parseLong(sparseHeaderStrings[i + 1]);
/* 894 */       } catch (NumberFormatException ex) {
/* 895 */         throw new IOException("Corrupted TAR archive. Sparse struct numbytes contains a non-numeric value");
/*     */       } 
/*     */       
/* 898 */       if (sparseNumbytes < 0L) {
/* 899 */         throw new IOException("Corrupted TAR archive. Sparse struct numbytes contains negative value");
/*     */       }
/*     */       
/* 902 */       sparseHeaders.add(new TarArchiveStructSparse(sparseOffset, sparseNumbytes));
/*     */     } 
/*     */     
/* 905 */     return Collections.unmodifiableList(sparseHeaders);
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected static List<TarArchiveStructSparse> parsePAX1XSparseHeaders(InputStream inputStream, int recordSize) throws IOException {
/* 921 */     List<TarArchiveStructSparse> sparseHeaders = new ArrayList<>();
/* 922 */     long bytesRead = 0L;
/*     */     
/* 924 */     long[] readResult = readLineOfNumberForPax1X(inputStream);
/* 925 */     long sparseHeadersCount = readResult[0];
/* 926 */     if (sparseHeadersCount < 0L)
/*     */     {
/* 928 */       throw new IOException("Corrupted TAR archive. Negative value in sparse headers block");
/*     */     }
/* 930 */     bytesRead += readResult[1];
/* 931 */     while (sparseHeadersCount-- > 0L) {
/* 932 */       readResult = readLineOfNumberForPax1X(inputStream);
/* 933 */       long sparseOffset = readResult[0];
/* 934 */       if (sparseOffset < 0L) {
/* 935 */         throw new IOException("Corrupted TAR archive. Sparse header block offset contains negative value");
/*     */       }
/*     */       
/* 938 */       bytesRead += readResult[1];
/*     */       
/* 940 */       readResult = readLineOfNumberForPax1X(inputStream);
/* 941 */       long sparseNumbytes = readResult[0];
/* 942 */       if (sparseNumbytes < 0L) {
/* 943 */         throw new IOException("Corrupted TAR archive. Sparse header block numbytes contains negative value");
/*     */       }
/*     */       
/* 946 */       bytesRead += readResult[1];
/* 947 */       sparseHeaders.add(new TarArchiveStructSparse(sparseOffset, sparseNumbytes));
/*     */     } 
/*     */ 
/*     */     
/* 951 */     long bytesToSkip = recordSize - bytesRead % recordSize;
/* 952 */     IOUtils.skip(inputStream, bytesToSkip);
/* 953 */     return sparseHeaders;
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
/*     */   private static long[] readLineOfNumberForPax1X(InputStream inputStream) throws IOException {
/* 966 */     long result = 0L;
/* 967 */     long bytesRead = 0L;
/*     */     int number;
/* 969 */     while ((number = inputStream.read()) != 10) {
/* 970 */       bytesRead++;
/* 971 */       if (number == -1) {
/* 972 */         throw new IOException("Unexpected EOF when reading parse information of 1.X PAX format");
/*     */       }
/* 974 */       if (number < 48 || number > 57) {
/* 975 */         throw new IOException("Corrupted TAR archive. Non-numeric value in sparse headers block");
/*     */       }
/* 977 */       result = result * 10L + (number - 48);
/*     */     } 
/* 979 */     bytesRead++;
/*     */     
/* 981 */     return new long[] { result, bytesRead };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\tar\TarUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */