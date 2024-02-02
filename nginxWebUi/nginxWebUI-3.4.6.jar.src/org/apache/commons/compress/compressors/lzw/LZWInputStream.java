/*     */ package org.apache.commons.compress.compressors.lzw;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteOrder;
/*     */ import org.apache.commons.compress.MemoryLimitException;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.BitInputStream;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class LZWInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   protected static final int DEFAULT_CODE_SIZE = 9;
/*     */   protected static final int UNUSED_PREFIX = -1;
/*  42 */   private final byte[] oneByte = new byte[1];
/*     */   
/*     */   protected final BitInputStream in;
/*  45 */   private int clearCode = -1;
/*  46 */   private int codeSize = 9;
/*     */   private byte previousCodeFirstChar;
/*  48 */   private int previousCode = -1;
/*     */   private int tableSize;
/*     */   private int[] prefixes;
/*     */   private byte[] characters;
/*     */   private byte[] outputStack;
/*     */   private int outputStackLocation;
/*     */   
/*     */   protected LZWInputStream(InputStream inputStream, ByteOrder byteOrder) {
/*  56 */     this.in = new BitInputStream(inputStream, byteOrder);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  61 */     this.in.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  66 */     int ret = read(this.oneByte);
/*  67 */     if (ret < 0) {
/*  68 */       return ret;
/*     */     }
/*  70 */     return 0xFF & this.oneByte[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  75 */     if (len == 0) {
/*  76 */       return 0;
/*     */     }
/*  78 */     int bytesRead = readFromStack(b, off, len);
/*  79 */     while (len - bytesRead > 0) {
/*  80 */       int result = decompressNextSymbol();
/*  81 */       if (result < 0) {
/*  82 */         if (bytesRead > 0) {
/*  83 */           count(bytesRead);
/*  84 */           return bytesRead;
/*     */         } 
/*  86 */         return result;
/*     */       } 
/*  88 */       bytesRead += readFromStack(b, off + bytesRead, len - bytesRead);
/*     */     } 
/*  90 */     count(bytesRead);
/*  91 */     return bytesRead;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/*  99 */     return this.in.getBytesRead();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract int decompressNextSymbol() throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract int addEntry(int paramInt, byte paramByte) throws IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setClearCode(int codeSize) {
/* 124 */     this.clearCode = 1 << codeSize - 1;
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
/*     */   protected void initializeTables(int maxCodeSize, int memoryLimitInKb) throws MemoryLimitException {
/* 138 */     if (maxCodeSize <= 0) {
/* 139 */       throw new IllegalArgumentException("maxCodeSize is " + maxCodeSize + ", must be bigger than 0");
/*     */     }
/*     */ 
/*     */     
/* 143 */     if (memoryLimitInKb > -1) {
/* 144 */       int maxTableSize = 1 << maxCodeSize;
/*     */       
/* 146 */       long memoryUsageInBytes = maxTableSize * 6L;
/* 147 */       long memoryUsageInKb = memoryUsageInBytes >> 10L;
/*     */       
/* 149 */       if (memoryUsageInKb > memoryLimitInKb) {
/* 150 */         throw new MemoryLimitException(memoryUsageInKb, memoryLimitInKb);
/*     */       }
/*     */     } 
/* 153 */     initializeTables(maxCodeSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initializeTables(int maxCodeSize) {
/* 162 */     if (maxCodeSize <= 0) {
/* 163 */       throw new IllegalArgumentException("maxCodeSize is " + maxCodeSize + ", must be bigger than 0");
/*     */     }
/*     */     
/* 166 */     int maxTableSize = 1 << maxCodeSize;
/* 167 */     this.prefixes = new int[maxTableSize];
/* 168 */     this.characters = new byte[maxTableSize];
/* 169 */     this.outputStack = new byte[maxTableSize];
/* 170 */     this.outputStackLocation = maxTableSize;
/* 171 */     int max = 256;
/* 172 */     for (int i = 0; i < 256; i++) {
/* 173 */       this.prefixes[i] = -1;
/* 174 */       this.characters[i] = (byte)i;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int readNextCode() throws IOException {
/* 184 */     if (this.codeSize > 31) {
/* 185 */       throw new IllegalArgumentException("Code size must not be bigger than 31");
/*     */     }
/* 187 */     return (int)this.in.readBits(this.codeSize);
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
/*     */   protected int addEntry(int previousCode, byte character, int maxTableSize) {
/* 199 */     if (this.tableSize < maxTableSize) {
/* 200 */       this.prefixes[this.tableSize] = previousCode;
/* 201 */       this.characters[this.tableSize] = character;
/* 202 */       return this.tableSize++;
/*     */     } 
/* 204 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int addRepeatOfPreviousCode() throws IOException {
/* 214 */     if (this.previousCode == -1)
/*     */     {
/* 216 */       throw new IOException("The first code can't be a reference to its preceding code");
/*     */     }
/* 218 */     return addEntry(this.previousCode, this.previousCodeFirstChar);
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
/*     */   protected int expandCodeToOutputStack(int code, boolean addedUnfinishedEntry) throws IOException {
/* 231 */     for (int entry = code; entry >= 0; entry = this.prefixes[entry]) {
/* 232 */       this.outputStack[--this.outputStackLocation] = this.characters[entry];
/*     */     }
/* 234 */     if (this.previousCode != -1 && !addedUnfinishedEntry) {
/* 235 */       addEntry(this.previousCode, this.outputStack[this.outputStackLocation]);
/*     */     }
/* 237 */     this.previousCode = code;
/* 238 */     this.previousCodeFirstChar = this.outputStack[this.outputStackLocation];
/* 239 */     return this.outputStackLocation;
/*     */   }
/*     */   
/*     */   private int readFromStack(byte[] b, int off, int len) {
/* 243 */     int remainingInStack = this.outputStack.length - this.outputStackLocation;
/* 244 */     if (remainingInStack > 0) {
/* 245 */       int maxLength = Math.min(remainingInStack, len);
/* 246 */       System.arraycopy(this.outputStack, this.outputStackLocation, b, off, maxLength);
/* 247 */       this.outputStackLocation += maxLength;
/* 248 */       return maxLength;
/*     */     } 
/* 250 */     return 0;
/*     */   }
/*     */   
/*     */   protected int getCodeSize() {
/* 254 */     return this.codeSize;
/*     */   }
/*     */   
/*     */   protected void resetCodeSize() {
/* 258 */     setCodeSize(9);
/*     */   }
/*     */   
/*     */   protected void setCodeSize(int cs) {
/* 262 */     this.codeSize = cs;
/*     */   }
/*     */   
/*     */   protected void incrementCodeSize() {
/* 266 */     this.codeSize++;
/*     */   }
/*     */   
/*     */   protected void resetPreviousCode() {
/* 270 */     this.previousCode = -1;
/*     */   }
/*     */   
/*     */   protected int getPrefix(int offset) {
/* 274 */     return this.prefixes[offset];
/*     */   }
/*     */   
/*     */   protected void setPrefix(int offset, int value) {
/* 278 */     this.prefixes[offset] = value;
/*     */   }
/*     */   
/*     */   protected int getPrefixesLength() {
/* 282 */     return this.prefixes.length;
/*     */   }
/*     */   
/*     */   protected int getClearCode() {
/* 286 */     return this.clearCode;
/*     */   }
/*     */   
/*     */   protected int getTableSize() {
/* 290 */     return this.tableSize;
/*     */   }
/*     */   
/*     */   protected void setTableSize(int newSize) {
/* 294 */     this.tableSize = newSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lzw\LZWInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */