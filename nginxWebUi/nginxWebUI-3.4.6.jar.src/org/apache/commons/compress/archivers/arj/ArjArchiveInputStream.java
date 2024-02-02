/*     */ package org.apache.commons.compress.archivers.arj;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.zip.CRC32;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveException;
/*     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*     */ import org.apache.commons.compress.utils.BoundedInputStream;
/*     */ import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
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
/*     */ public class ArjArchiveInputStream
/*     */   extends ArchiveInputStream
/*     */ {
/*     */   private static final int ARJ_MAGIC_1 = 96;
/*     */   private static final int ARJ_MAGIC_2 = 234;
/*     */   private final DataInputStream in;
/*     */   private final String charsetName;
/*     */   private final MainHeader mainHeader;
/*     */   private LocalFileHeader currentLocalFileHeader;
/*     */   private InputStream currentInputStream;
/*     */   
/*     */   public ArjArchiveInputStream(InputStream inputStream, String charsetName) throws ArchiveException {
/*  63 */     this.in = new DataInputStream(inputStream);
/*  64 */     this.charsetName = charsetName;
/*     */     try {
/*  66 */       this.mainHeader = readMainHeader();
/*  67 */       if ((this.mainHeader.arjFlags & 0x1) != 0) {
/*  68 */         throw new ArchiveException("Encrypted ARJ files are unsupported");
/*     */       }
/*  70 */       if ((this.mainHeader.arjFlags & 0x4) != 0) {
/*  71 */         throw new ArchiveException("Multi-volume ARJ files are unsupported");
/*     */       }
/*  73 */     } catch (IOException ioException) {
/*  74 */       throw new ArchiveException(ioException.getMessage(), ioException);
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
/*     */   public ArjArchiveInputStream(InputStream inputStream) throws ArchiveException {
/*  86 */     this(inputStream, "CP437");
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  91 */     this.in.close();
/*     */   }
/*     */   
/*     */   private int read8(DataInputStream dataIn) throws IOException {
/*  95 */     int value = dataIn.readUnsignedByte();
/*  96 */     count(1);
/*  97 */     return value;
/*     */   }
/*     */   
/*     */   private int read16(DataInputStream dataIn) throws IOException {
/* 101 */     int value = dataIn.readUnsignedShort();
/* 102 */     count(2);
/* 103 */     return Integer.reverseBytes(value) >>> 16;
/*     */   }
/*     */   
/*     */   private int read32(DataInputStream dataIn) throws IOException {
/* 107 */     int value = dataIn.readInt();
/* 108 */     count(4);
/* 109 */     return Integer.reverseBytes(value);
/*     */   }
/*     */   
/*     */   private String readString(DataInputStream dataIn) throws IOException {
/* 113 */     try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
/*     */       int nextByte;
/* 115 */       while ((nextByte = dataIn.readUnsignedByte()) != 0) {
/* 116 */         buffer.write(nextByte);
/*     */       }
/* 118 */       if (this.charsetName != null) {
/* 119 */         return buffer.toString(this.charsetName);
/*     */       }
/*     */       
/* 122 */       return buffer.toString();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private byte[] readRange(InputStream in, int len) throws IOException {
/* 128 */     byte[] b = IOUtils.readRange(in, len);
/* 129 */     count(b.length);
/* 130 */     if (b.length < len) {
/* 131 */       throw new EOFException();
/*     */     }
/* 133 */     return b;
/*     */   }
/*     */   
/*     */   private byte[] readHeader() throws IOException {
/* 137 */     boolean found = false;
/* 138 */     byte[] basicHeaderBytes = null;
/*     */     while (true) {
/* 140 */       int first = 0;
/* 141 */       int second = read8(this.in);
/*     */       do {
/* 143 */         first = second;
/* 144 */         second = read8(this.in);
/* 145 */       } while (first != 96 && second != 234);
/* 146 */       int basicHeaderSize = read16(this.in);
/* 147 */       if (basicHeaderSize == 0)
/*     */       {
/* 149 */         return null;
/*     */       }
/* 151 */       if (basicHeaderSize <= 2600) {
/* 152 */         basicHeaderBytes = readRange(this.in, basicHeaderSize);
/* 153 */         long basicHeaderCrc32 = read32(this.in) & 0xFFFFFFFFL;
/* 154 */         CRC32 crc32 = new CRC32();
/* 155 */         crc32.update(basicHeaderBytes);
/* 156 */         if (basicHeaderCrc32 == crc32.getValue()) {
/* 157 */           found = true;
/*     */         }
/*     */       } 
/* 160 */       if (found)
/* 161 */         return basicHeaderBytes; 
/*     */     } 
/*     */   }
/*     */   private MainHeader readMainHeader() throws IOException {
/* 165 */     byte[] basicHeaderBytes = readHeader();
/* 166 */     if (basicHeaderBytes == null) {
/* 167 */       throw new IOException("Archive ends without any headers");
/*     */     }
/* 169 */     DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes));
/*     */ 
/*     */     
/* 172 */     int firstHeaderSize = basicHeader.readUnsignedByte();
/* 173 */     byte[] firstHeaderBytes = readRange(basicHeader, firstHeaderSize - 1);
/* 174 */     pushedBackBytes(firstHeaderBytes.length);
/*     */     
/* 176 */     DataInputStream firstHeader = new DataInputStream(new ByteArrayInputStream(firstHeaderBytes));
/*     */ 
/*     */     
/* 179 */     MainHeader hdr = new MainHeader();
/* 180 */     hdr.archiverVersionNumber = firstHeader.readUnsignedByte();
/* 181 */     hdr.minVersionToExtract = firstHeader.readUnsignedByte();
/* 182 */     hdr.hostOS = firstHeader.readUnsignedByte();
/* 183 */     hdr.arjFlags = firstHeader.readUnsignedByte();
/* 184 */     hdr.securityVersion = firstHeader.readUnsignedByte();
/* 185 */     hdr.fileType = firstHeader.readUnsignedByte();
/* 186 */     hdr.reserved = firstHeader.readUnsignedByte();
/* 187 */     hdr.dateTimeCreated = read32(firstHeader);
/* 188 */     hdr.dateTimeModified = read32(firstHeader);
/* 189 */     hdr.archiveSize = 0xFFFFFFFFL & read32(firstHeader);
/* 190 */     hdr.securityEnvelopeFilePosition = read32(firstHeader);
/* 191 */     hdr.fileSpecPosition = read16(firstHeader);
/* 192 */     hdr.securityEnvelopeLength = read16(firstHeader);
/* 193 */     pushedBackBytes(20L);
/* 194 */     hdr.encryptionVersion = firstHeader.readUnsignedByte();
/* 195 */     hdr.lastChapter = firstHeader.readUnsignedByte();
/*     */     
/* 197 */     if (firstHeaderSize >= 33) {
/* 198 */       hdr.arjProtectionFactor = firstHeader.readUnsignedByte();
/* 199 */       hdr.arjFlags2 = firstHeader.readUnsignedByte();
/* 200 */       firstHeader.readUnsignedByte();
/* 201 */       firstHeader.readUnsignedByte();
/*     */     } 
/*     */     
/* 204 */     hdr.name = readString(basicHeader);
/* 205 */     hdr.comment = readString(basicHeader);
/*     */     
/* 207 */     int extendedHeaderSize = read16(this.in);
/* 208 */     if (extendedHeaderSize > 0) {
/* 209 */       hdr.extendedHeaderBytes = readRange(this.in, extendedHeaderSize);
/* 210 */       long extendedHeaderCrc32 = 0xFFFFFFFFL & read32(this.in);
/* 211 */       CRC32 crc32 = new CRC32();
/* 212 */       crc32.update(hdr.extendedHeaderBytes);
/* 213 */       if (extendedHeaderCrc32 != crc32.getValue()) {
/* 214 */         throw new IOException("Extended header CRC32 verification failure");
/*     */       }
/*     */     } 
/*     */     
/* 218 */     return hdr;
/*     */   }
/*     */   
/*     */   private LocalFileHeader readLocalFileHeader() throws IOException {
/* 222 */     byte[] basicHeaderBytes = readHeader();
/* 223 */     if (basicHeaderBytes == null) {
/* 224 */       return null;
/*     */     }
/* 226 */     try (DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes))) {
/*     */       
/* 228 */       int firstHeaderSize = basicHeader.readUnsignedByte();
/* 229 */       byte[] firstHeaderBytes = readRange(basicHeader, firstHeaderSize - 1);
/* 230 */       pushedBackBytes(firstHeaderBytes.length);
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
/*     */   private void readExtraData(int firstHeaderSize, DataInputStream firstHeader, LocalFileHeader localFileHeader) throws IOException {
/* 277 */     if (firstHeaderSize >= 33) {
/* 278 */       localFileHeader.extendedFilePosition = read32(firstHeader);
/* 279 */       if (firstHeaderSize >= 45) {
/* 280 */         localFileHeader.dateTimeAccessed = read32(firstHeader);
/* 281 */         localFileHeader.dateTimeCreated = read32(firstHeader);
/* 282 */         localFileHeader.originalSizeEvenForVolumes = read32(firstHeader);
/* 283 */         pushedBackBytes(12L);
/*     */       } 
/* 285 */       pushedBackBytes(4L);
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 299 */     return (length >= 2 && (0xFF & signature[0]) == 96 && (0xFF & signature[1]) == 234);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getArchiveName() {
/* 309 */     return this.mainHeader.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getArchiveComment() {
/* 317 */     return this.mainHeader.comment;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArjArchiveEntry getNextEntry() throws IOException {
/* 322 */     if (this.currentInputStream != null) {
/*     */       
/* 324 */       IOUtils.skip(this.currentInputStream, Long.MAX_VALUE);
/* 325 */       this.currentInputStream.close();
/* 326 */       this.currentLocalFileHeader = null;
/* 327 */       this.currentInputStream = null;
/*     */     } 
/*     */     
/* 330 */     this.currentLocalFileHeader = readLocalFileHeader();
/* 331 */     if (this.currentLocalFileHeader != null) {
/* 332 */       this.currentInputStream = (InputStream)new BoundedInputStream(this.in, this.currentLocalFileHeader.compressedSize);
/* 333 */       if (this.currentLocalFileHeader.method == 0) {
/* 334 */         this.currentInputStream = (InputStream)new CRC32VerifyingInputStream(this.currentInputStream, this.currentLocalFileHeader.originalSize, this.currentLocalFileHeader.originalCrc32);
/*     */       }
/*     */       
/* 337 */       return new ArjArchiveEntry(this.currentLocalFileHeader);
/*     */     } 
/* 339 */     this.currentInputStream = null;
/* 340 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canReadEntryData(ArchiveEntry ae) {
/* 345 */     return (ae instanceof ArjArchiveEntry && ((ArjArchiveEntry)ae)
/* 346 */       .getMethod() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 351 */     if (len == 0) {
/* 352 */       return 0;
/*     */     }
/* 354 */     if (this.currentLocalFileHeader == null) {
/* 355 */       throw new IllegalStateException("No current arj entry");
/*     */     }
/* 357 */     if (this.currentLocalFileHeader.method != 0) {
/* 358 */       throw new IOException("Unsupported compression method " + this.currentLocalFileHeader.method);
/*     */     }
/* 360 */     return this.currentInputStream.read(b, off, len);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\arj\ArjArchiveInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */