/*     */ package org.h2.value;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.store.LobStorageInterface;
/*     */ import org.h2.store.RangeInputStream;
/*     */ import org.h2.store.RangeReader;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.lob.LobData;
/*     */ import org.h2.value.lob.LobDataInMemory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ValueLob
/*     */   extends Value
/*     */ {
/*     */   static final int BLOCK_COMPARISON_SIZE = 512;
/*     */   private TypeInfo type;
/*     */   final LobData lobData;
/*     */   long octetLength;
/*     */   long charLength;
/*     */   private int hash;
/*     */   
/*     */   private static void rangeCheckUnknown(long paramLong1, long paramLong2) {
/*  39 */     if (paramLong1 < 0L) {
/*  40 */       throw DbException.getInvalidValueException("offset", Long.valueOf(paramLong1 + 1L));
/*     */     }
/*  42 */     if (paramLong2 < 0L) {
/*  43 */       throw DbException.getInvalidValueException("length", Long.valueOf(paramLong2));
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
/*     */   protected static InputStream rangeInputStream(InputStream paramInputStream, long paramLong1, long paramLong2, long paramLong3) {
/*  58 */     if (paramLong3 > 0L) {
/*  59 */       rangeCheck(paramLong1 - 1L, paramLong2, paramLong3);
/*     */     } else {
/*  61 */       rangeCheckUnknown(paramLong1 - 1L, paramLong2);
/*     */     } 
/*     */     try {
/*  64 */       return (InputStream)new RangeInputStream(paramInputStream, paramLong1 - 1L, paramLong2);
/*  65 */     } catch (IOException iOException) {
/*  66 */       throw DbException.getInvalidValueException("offset", Long.valueOf(paramLong1));
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
/*     */   static Reader rangeReader(Reader paramReader, long paramLong1, long paramLong2, long paramLong3) {
/*  80 */     if (paramLong3 > 0L) {
/*  81 */       rangeCheck(paramLong1 - 1L, paramLong2, paramLong3);
/*     */     } else {
/*  83 */       rangeCheckUnknown(paramLong1 - 1L, paramLong2);
/*     */     } 
/*     */     try {
/*  86 */       return (Reader)new RangeReader(paramReader, paramLong1 - 1L, paramLong2);
/*  87 */     } catch (IOException iOException) {
/*  88 */       throw DbException.getInvalidValueException("offset", Long.valueOf(paramLong1));
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
/*     */   ValueLob(LobData paramLobData, long paramLong1, long paramLong2) {
/* 112 */     this.lobData = paramLobData;
/* 113 */     this.octetLength = paramLong1;
/* 114 */     this.charLength = paramLong2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String createTempLobFileName(DataHandler paramDataHandler) throws IOException {
/* 124 */     String str = paramDataHandler.getDatabasePath();
/* 125 */     if (str.isEmpty()) {
/* 126 */       str = SysProperties.PREFIX_TEMP_FILE;
/*     */     }
/* 128 */     return FileUtils.createTempFile(str, ".temp.db", true);
/*     */   }
/*     */   
/*     */   static int getBufferSize(DataHandler paramDataHandler, long paramLong) {
/* 132 */     if (paramLong < 0L || paramLong > 2147483647L) {
/* 133 */       paramLong = 2147483647L;
/*     */     }
/* 135 */     int i = paramDataHandler.getMaxLengthInplaceLob();
/* 136 */     long l = 4096L;
/* 137 */     if (l < paramLong && l <= i) {
/*     */ 
/*     */       
/* 140 */       l = Math.min(paramLong, i + 1L);
/*     */ 
/*     */       
/* 143 */       l = MathUtils.roundUpLong(l, 4096L);
/*     */     } 
/* 145 */     l = Math.min(paramLong, l);
/* 146 */     l = MathUtils.convertLongToInt(l);
/* 147 */     if (l < 0L) {
/* 148 */       l = 2147483647L;
/*     */     }
/* 150 */     return (int)l;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLinkedToTable() {
/* 160 */     return this.lobData.isLinkedToTable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove() {
/* 168 */     this.lobData.remove(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ValueLob copy(DataHandler paramDataHandler, int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 183 */     TypeInfo typeInfo = this.type;
/* 184 */     if (typeInfo == null) {
/* 185 */       int i = getValueType();
/* 186 */       this.type = typeInfo = new TypeInfo(i, (i == 3) ? this.charLength : this.octetLength, 0, null);
/*     */     } 
/* 188 */     return typeInfo;
/*     */   }
/*     */   
/*     */   DbException getStringTooLong(long paramLong) {
/* 192 */     return DbException.getValueTooLongException("CHARACTER VARYING", readString(81), paramLong);
/*     */   }
/*     */   
/*     */   String readString(int paramInt) {
/*     */     try {
/* 197 */       return IOUtils.readStringAndClose(getReader(), paramInt);
/* 198 */     } catch (IOException iOException) {
/* 199 */       throw DbException.convertIOException(iOException, toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader() {
/* 205 */     return IOUtils.getReader(getInputStream());
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes() {
/* 210 */     if (this.lobData instanceof LobDataInMemory) {
/* 211 */       return Utils.cloneByteArray(getSmall());
/*     */     }
/* 213 */     return getBytesInternal();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytesNoCopy() {
/* 218 */     if (this.lobData instanceof LobDataInMemory) {
/* 219 */       return getSmall();
/*     */     }
/* 221 */     return getBytesInternal();
/*     */   }
/*     */   
/*     */   private byte[] getSmall() {
/* 225 */     byte[] arrayOfByte = ((LobDataInMemory)this.lobData).getSmall();
/* 226 */     int i = arrayOfByte.length;
/* 227 */     if (i > 1048576) {
/* 228 */       throw DbException.getValueTooLongException("BINARY VARYING", StringUtils.convertBytesToHex(arrayOfByte, 41), i);
/*     */     }
/* 230 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   abstract byte[] getBytesInternal();
/*     */   
/*     */   DbException getBinaryTooLong(long paramLong) {
/* 236 */     return DbException.getValueTooLongException("BINARY VARYING", StringUtils.convertBytesToHex(readBytes(41)), paramLong);
/*     */   }
/*     */ 
/*     */   
/*     */   byte[] readBytes(int paramInt) {
/*     */     try {
/* 242 */       return IOUtils.readBytesAndClose(getInputStream(), paramInt);
/* 243 */     } catch (IOException iOException) {
/* 244 */       throw DbException.convertIOException(iOException, toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 250 */     if (this.hash == 0) {
/* 251 */       int i = getValueType();
/* 252 */       long l = (i == 3) ? this.charLength : this.octetLength;
/* 253 */       if (l > 4096L)
/*     */       {
/*     */         
/* 256 */         return (int)(l ^ l >>> 32L);
/*     */       }
/* 258 */       this.hash = Utils.getByteArrayHash(getBytesNoCopy());
/*     */     } 
/* 260 */     return this.hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 265 */     if (!(paramObject instanceof ValueLob))
/* 266 */       return false; 
/* 267 */     ValueLob valueLob = (ValueLob)paramObject;
/* 268 */     if (hashCode() != valueLob.hashCode())
/* 269 */       return false; 
/* 270 */     return (compareTypeSafe((Value)paramObject, null, null) == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 275 */     return this.lobData.getMemory();
/*     */   }
/*     */   
/*     */   public LobData getLobData() {
/* 279 */     return this.lobData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueLob copyToResult() {
/* 288 */     if (this.lobData instanceof org.h2.value.lob.LobDataDatabase) {
/* 289 */       LobStorageInterface lobStorageInterface = this.lobData.getDataHandler().getLobStorage();
/* 290 */       if (!lobStorageInterface.isReadOnly()) {
/* 291 */         return lobStorageInterface.copyLob(this, -3);
/*     */       }
/*     */     } 
/* 294 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueLob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */