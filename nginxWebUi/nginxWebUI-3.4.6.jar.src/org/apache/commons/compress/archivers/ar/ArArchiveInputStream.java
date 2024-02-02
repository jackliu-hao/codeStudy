/*     */ package org.apache.commons.compress.archivers.ar;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*     */ import org.apache.commons.compress.utils.ArchiveUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArArchiveInputStream
/*     */   extends ArchiveInputStream
/*     */ {
/*     */   private final InputStream input;
/*     */   private long offset;
/*     */   private boolean closed;
/*     */   private ArArchiveEntry currentEntry;
/*     */   private byte[] namebuffer;
/*  56 */   private long entryOffset = -1L;
/*     */   
/*     */   private static final int NAME_OFFSET = 0;
/*     */   
/*     */   private static final int NAME_LEN = 16;
/*     */   
/*     */   private static final int LAST_MODIFIED_OFFSET = 16;
/*     */   
/*     */   private static final int LAST_MODIFIED_LEN = 12;
/*     */   private static final int USER_ID_OFFSET = 28;
/*     */   private static final int USER_ID_LEN = 6;
/*     */   private static final int GROUP_ID_OFFSET = 34;
/*     */   private static final int GROUP_ID_LEN = 6;
/*     */   private static final int FILE_MODE_OFFSET = 40;
/*     */   private static final int FILE_MODE_LEN = 8;
/*     */   private static final int LENGTH_OFFSET = 48;
/*     */   private static final int LENGTH_LEN = 10;
/*  73 */   private final byte[] metaData = new byte[58];
/*     */ 
/*     */ 
/*     */   
/*     */   static final String BSD_LONGNAME_PREFIX = "#1/";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArArchiveInputStream(InputStream pInput) {
/*  83 */     this.input = pInput;
/*  84 */     this.closed = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArArchiveEntry getNextArEntry() throws IOException {
/*  95 */     if (this.currentEntry != null) {
/*  96 */       long entryEnd = this.entryOffset + this.currentEntry.getLength();
/*  97 */       long skipped = IOUtils.skip(this.input, entryEnd - this.offset);
/*  98 */       trackReadBytes(skipped);
/*  99 */       this.currentEntry = null;
/*     */     } 
/*     */     
/* 102 */     if (this.offset == 0L) {
/* 103 */       byte[] arrayOfByte1 = ArchiveUtils.toAsciiBytes("!<arch>\n");
/* 104 */       byte[] arrayOfByte2 = IOUtils.readRange(this.input, arrayOfByte1.length);
/* 105 */       int j = arrayOfByte2.length;
/* 106 */       trackReadBytes(j);
/* 107 */       if (j != arrayOfByte1.length) {
/* 108 */         throw new IOException("Failed to read header. Occurred at byte: " + getBytesRead());
/*     */       }
/* 110 */       if (!Arrays.equals(arrayOfByte1, arrayOfByte2)) {
/* 111 */         throw new IOException("Invalid header " + ArchiveUtils.toAsciiString(arrayOfByte2));
/*     */       }
/*     */     } 
/*     */     
/* 115 */     if (this.offset % 2L != 0L) {
/* 116 */       if (this.input.read() < 0)
/*     */       {
/* 118 */         return null;
/*     */       }
/* 120 */       trackReadBytes(1L);
/*     */     } 
/*     */ 
/*     */     
/* 124 */     int read = IOUtils.readFully(this.input, this.metaData);
/* 125 */     trackReadBytes(read);
/* 126 */     if (read == 0) {
/* 127 */       return null;
/*     */     }
/* 129 */     if (read < this.metaData.length) {
/* 130 */       throw new IOException("Truncated ar archive");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 135 */     byte[] expected = ArchiveUtils.toAsciiBytes("`\n");
/* 136 */     byte[] realized = IOUtils.readRange(this.input, expected.length);
/* 137 */     int i = realized.length;
/* 138 */     trackReadBytes(i);
/* 139 */     if (i != expected.length) {
/* 140 */       throw new IOException("Failed to read entry trailer. Occurred at byte: " + getBytesRead());
/*     */     }
/* 142 */     if (!Arrays.equals(expected, realized)) {
/* 143 */       throw new IOException("Invalid entry trailer. not read the content? Occurred at byte: " + getBytesRead());
/*     */     }
/*     */ 
/*     */     
/* 147 */     this.entryOffset = this.offset;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     String temp = ArchiveUtils.toAsciiString(this.metaData, 0, 16).trim();
/* 153 */     if (isGNUStringTable(temp)) {
/* 154 */       this.currentEntry = readGNUStringTable(this.metaData, 48, 10);
/* 155 */       return getNextArEntry();
/*     */     } 
/*     */     
/* 158 */     long len = asLong(this.metaData, 48, 10);
/* 159 */     if (temp.endsWith("/")) {
/* 160 */       temp = temp.substring(0, temp.length() - 1);
/* 161 */     } else if (isGNULongName(temp)) {
/* 162 */       int off = Integer.parseInt(temp.substring(1));
/* 163 */       temp = getExtendedName(off);
/* 164 */     } else if (isBSDLongName(temp)) {
/* 165 */       temp = getBSDLongName(temp);
/*     */ 
/*     */ 
/*     */       
/* 169 */       int nameLen = temp.length();
/* 170 */       len -= nameLen;
/* 171 */       this.entryOffset += nameLen;
/*     */     } 
/*     */     
/* 174 */     if (len < 0L) {
/* 175 */       throw new IOException("broken archive, entry with negative size");
/*     */     }
/*     */     
/* 178 */     this
/*     */ 
/*     */ 
/*     */       
/* 182 */       .currentEntry = new ArArchiveEntry(temp, len, asInt(this.metaData, 28, 6, true), asInt(this.metaData, 34, 6, true), asInt(this.metaData, 40, 8, 8), asLong(this.metaData, 16, 12));
/* 183 */     return this.currentEntry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getExtendedName(int offset) throws IOException {
/* 194 */     if (this.namebuffer == null) {
/* 195 */       throw new IOException("Cannot process GNU long filename as no // record was found");
/*     */     }
/* 197 */     for (int i = offset; i < this.namebuffer.length; i++) {
/* 198 */       if (this.namebuffer[i] == 10 || this.namebuffer[i] == 0) {
/* 199 */         if (this.namebuffer[i - 1] == 47) {
/* 200 */           i--;
/*     */         }
/* 202 */         return ArchiveUtils.toAsciiString(this.namebuffer, offset, i - offset);
/*     */       } 
/*     */     } 
/* 205 */     throw new IOException("Failed to read entry: " + offset);
/*     */   }
/*     */   
/*     */   private long asLong(byte[] byteArray, int offset, int len) {
/* 209 */     return Long.parseLong(ArchiveUtils.toAsciiString(byteArray, offset, len).trim());
/*     */   }
/*     */   
/*     */   private int asInt(byte[] byteArray, int offset, int len) {
/* 213 */     return asInt(byteArray, offset, len, 10, false);
/*     */   }
/*     */   
/*     */   private int asInt(byte[] byteArray, int offset, int len, boolean treatBlankAsZero) {
/* 217 */     return asInt(byteArray, offset, len, 10, treatBlankAsZero);
/*     */   }
/*     */   
/*     */   private int asInt(byte[] byteArray, int offset, int len, int base) {
/* 221 */     return asInt(byteArray, offset, len, base, false);
/*     */   }
/*     */   
/*     */   private int asInt(byte[] byteArray, int offset, int len, int base, boolean treatBlankAsZero) {
/* 225 */     String string = ArchiveUtils.toAsciiString(byteArray, offset, len).trim();
/* 226 */     if (string.isEmpty() && treatBlankAsZero) {
/* 227 */       return 0;
/*     */     }
/* 229 */     return Integer.parseInt(string, base);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveEntry getNextEntry() throws IOException {
/* 240 */     return getNextArEntry();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 250 */     if (!this.closed) {
/* 251 */       this.closed = true;
/* 252 */       this.input.close();
/*     */     } 
/* 254 */     this.currentEntry = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 264 */     if (len == 0) {
/* 265 */       return 0;
/*     */     }
/* 267 */     if (this.currentEntry == null) {
/* 268 */       throw new IllegalStateException("No current ar entry");
/*     */     }
/* 270 */     long entryEnd = this.entryOffset + this.currentEntry.getLength();
/* 271 */     if (len < 0 || this.offset >= entryEnd) {
/* 272 */       return -1;
/*     */     }
/* 274 */     int toRead = (int)Math.min(len, entryEnd - this.offset);
/* 275 */     int ret = this.input.read(b, off, toRead);
/* 276 */     trackReadBytes(ret);
/* 277 */     return ret;
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 293 */     return (length >= 8 && signature[0] == 33 && signature[1] == 60 && signature[2] == 97 && signature[3] == 114 && signature[4] == 99 && signature[5] == 104 && signature[6] == 62 && signature[7] == 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 301 */   private static final int BSD_LONGNAME_PREFIX_LEN = "#1/"
/* 302 */     .length();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String BSD_LONGNAME_PATTERN = "^#1/\\d+";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String GNU_STRING_TABLE_NAME = "//";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String GNU_LONGNAME_PATTERN = "^/\\d+";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isBSDLongName(String name) {
/* 329 */     return (name != null && name.matches("^#1/\\d+"));
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
/*     */   private String getBSDLongName(String bsdLongName) throws IOException {
/* 342 */     int nameLen = Integer.parseInt(bsdLongName.substring(BSD_LONGNAME_PREFIX_LEN));
/* 343 */     byte[] name = IOUtils.readRange(this.input, nameLen);
/* 344 */     int read = name.length;
/* 345 */     trackReadBytes(read);
/* 346 */     if (read != nameLen) {
/* 347 */       throw new EOFException();
/*     */     }
/* 349 */     return ArchiveUtils.toAsciiString(name);
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
/*     */   private static boolean isGNUStringTable(String name) {
/* 372 */     return "//".equals(name);
/*     */   }
/*     */   
/*     */   private void trackReadBytes(long read) {
/* 376 */     count(read);
/* 377 */     if (read > 0L) {
/* 378 */       this.offset += read;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ArArchiveEntry readGNUStringTable(byte[] length, int offset, int len) throws IOException {
/* 388 */     int bufflen = asInt(length, offset, len);
/* 389 */     this.namebuffer = IOUtils.readRange(this.input, bufflen);
/* 390 */     int read = this.namebuffer.length;
/* 391 */     trackReadBytes(read);
/* 392 */     if (read != bufflen) {
/* 393 */       throw new IOException("Failed to read complete // record: expected=" + bufflen + " read=" + read);
/*     */     }
/*     */     
/* 396 */     return new ArArchiveEntry("//", bufflen);
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
/*     */   private boolean isGNULongName(String name) {
/* 408 */     return (name != null && name.matches("^/\\d+"));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\ar\ArArchiveInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */