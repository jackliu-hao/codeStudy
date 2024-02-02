/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtraFieldUtils
/*     */ {
/*     */   private static final int WORD = 4;
/*  42 */   private static final Map<ZipShort, Class<?>> implementations = new ConcurrentHashMap<>(); static {
/*  43 */     register(AsiExtraField.class);
/*  44 */     register(X5455_ExtendedTimestamp.class);
/*  45 */     register(X7875_NewUnix.class);
/*  46 */     register(JarMarker.class);
/*  47 */     register(UnicodePathExtraField.class);
/*  48 */     register(UnicodeCommentExtraField.class);
/*  49 */     register(Zip64ExtendedInformationExtraField.class);
/*  50 */     register(X000A_NTFS.class);
/*  51 */     register(X0014_X509Certificates.class);
/*  52 */     register(X0015_CertificateIdForFile.class);
/*  53 */     register(X0016_CertificateIdForCentralDirectory.class);
/*  54 */     register(X0017_StrongEncryptionHeader.class);
/*  55 */     register(X0019_EncryptionRecipientCertificateList.class);
/*  56 */     register(ResourceAlignmentExtraField.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void register(Class<?> c) {
/*     */     try {
/*  68 */       ZipExtraField ze = (ZipExtraField)c.newInstance();
/*  69 */       implementations.put(ze.getHeaderId(), c);
/*  70 */     } catch (ClassCastException cc) {
/*  71 */       throw new RuntimeException(c + " doesn't implement ZipExtraField");
/*  72 */     } catch (InstantiationException ie) {
/*  73 */       throw new RuntimeException(c + " is not a concrete class");
/*  74 */     } catch (IllegalAccessException ie) {
/*  75 */       throw new RuntimeException(c + "'s no-arg constructor is not public");
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
/*     */   public static ZipExtraField createExtraField(ZipShort headerId) throws InstantiationException, IllegalAccessException {
/*  89 */     ZipExtraField field = createExtraFieldNoDefault(headerId);
/*  90 */     if (field != null) {
/*  91 */       return field;
/*     */     }
/*  93 */     UnrecognizedExtraField u = new UnrecognizedExtraField();
/*  94 */     u.setHeaderId(headerId);
/*  95 */     return u;
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
/*     */   public static ZipExtraField createExtraFieldNoDefault(ZipShort headerId) throws InstantiationException, IllegalAccessException {
/* 109 */     Class<?> c = implementations.get(headerId);
/* 110 */     if (c != null) {
/* 111 */       return (ZipExtraField)c.newInstance();
/*     */     }
/* 113 */     return null;
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
/*     */   public static ZipExtraField[] parse(byte[] data) throws ZipException {
/* 125 */     return parse(data, true, UnparseableExtraField.THROW);
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
/*     */   public static ZipExtraField[] parse(byte[] data, boolean local) throws ZipException {
/* 139 */     return parse(data, local, UnparseableExtraField.THROW);
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
/*     */   public static ZipExtraField[] parse(byte[] data, boolean local, final UnparseableExtraField onUnparseableData) throws ZipException {
/* 158 */     return parse(data, local, new ExtraFieldParsingBehavior()
/*     */         {
/*     */           public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException
/*     */           {
/* 162 */             return onUnparseableData.onUnparseableExtraField(data, off, len, local, claimedLength);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public ZipExtraField createExtraField(ZipShort headerId) throws ZipException, InstantiationException, IllegalAccessException {
/* 168 */             return ExtraFieldUtils.createExtraField(headerId);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) throws ZipException {
/* 174 */             return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
/*     */           }
/*     */         });
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
/*     */   public static ZipExtraField[] parse(byte[] data, boolean local, ExtraFieldParsingBehavior parsingBehavior) throws ZipException {
/* 194 */     List<ZipExtraField> v = new ArrayList<>();
/* 195 */     int start = 0;
/* 196 */     int dataLength = data.length;
/*     */     
/* 198 */     while (start <= dataLength - 4) {
/* 199 */       ZipShort headerId = new ZipShort(data, start);
/* 200 */       int length = (new ZipShort(data, start + 2)).getValue();
/* 201 */       if (start + 4 + length > dataLength) {
/* 202 */         ZipExtraField field = parsingBehavior.onUnparseableExtraField(data, start, dataLength - start, local, length);
/*     */         
/* 204 */         if (field != null) {
/* 205 */           v.add(field);
/*     */         }
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/*     */       try {
/* 213 */         ZipExtraField ze = Objects.<ZipExtraField>requireNonNull(parsingBehavior.createExtraField(headerId), "createExtraField must not return null");
/*     */         
/* 215 */         v.add(Objects.requireNonNull(parsingBehavior.fill(ze, data, start + 4, length, local), "fill must not return null"));
/*     */         
/* 217 */         start += length + 4;
/* 218 */       } catch (InstantiationException|IllegalAccessException ie) {
/* 219 */         throw (ZipException)(new ZipException(ie.getMessage())).initCause(ie);
/*     */       } 
/*     */     } 
/*     */     
/* 223 */     return v.<ZipExtraField>toArray(EMPTY_ZIP_EXTRA_FIELD_ARRAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] mergeLocalFileDataData(ZipExtraField[] data) {
/* 232 */     int dataLength = data.length;
/* 233 */     boolean lastIsUnparseableHolder = (dataLength > 0 && data[dataLength - 1] instanceof UnparseableExtraFieldData);
/*     */     
/* 235 */     int regularExtraFieldCount = lastIsUnparseableHolder ? (dataLength - 1) : dataLength;
/*     */ 
/*     */     
/* 238 */     int sum = 4 * regularExtraFieldCount;
/* 239 */     for (ZipExtraField element : data) {
/* 240 */       sum += element.getLocalFileDataLength().getValue();
/*     */     }
/*     */     
/* 243 */     byte[] result = new byte[sum];
/* 244 */     int start = 0;
/* 245 */     for (int i = 0; i < regularExtraFieldCount; i++) {
/* 246 */       System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
/*     */       
/* 248 */       System.arraycopy(data[i].getLocalFileDataLength().getBytes(), 0, result, start + 2, 2);
/*     */       
/* 250 */       start += 4;
/* 251 */       byte[] local = data[i].getLocalFileDataData();
/* 252 */       if (local != null) {
/* 253 */         System.arraycopy(local, 0, result, start, local.length);
/* 254 */         start += local.length;
/*     */       } 
/*     */     } 
/* 257 */     if (lastIsUnparseableHolder) {
/* 258 */       byte[] local = data[dataLength - 1].getLocalFileDataData();
/* 259 */       if (local != null) {
/* 260 */         System.arraycopy(local, 0, result, start, local.length);
/*     */       }
/*     */     } 
/* 263 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] mergeCentralDirectoryData(ZipExtraField[] data) {
/* 272 */     int dataLength = data.length;
/* 273 */     boolean lastIsUnparseableHolder = (dataLength > 0 && data[dataLength - 1] instanceof UnparseableExtraFieldData);
/*     */     
/* 275 */     int regularExtraFieldCount = lastIsUnparseableHolder ? (dataLength - 1) : dataLength;
/*     */ 
/*     */     
/* 278 */     int sum = 4 * regularExtraFieldCount;
/* 279 */     for (ZipExtraField element : data) {
/* 280 */       sum += element.getCentralDirectoryLength().getValue();
/*     */     }
/* 282 */     byte[] result = new byte[sum];
/* 283 */     int start = 0;
/* 284 */     for (int i = 0; i < regularExtraFieldCount; i++) {
/* 285 */       System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
/*     */       
/* 287 */       System.arraycopy(data[i].getCentralDirectoryLength().getBytes(), 0, result, start + 2, 2);
/*     */       
/* 289 */       start += 4;
/* 290 */       byte[] central = data[i].getCentralDirectoryData();
/* 291 */       if (central != null) {
/* 292 */         System.arraycopy(central, 0, result, start, central.length);
/* 293 */         start += central.length;
/*     */       } 
/*     */     } 
/* 296 */     if (lastIsUnparseableHolder) {
/* 297 */       byte[] central = data[dataLength - 1].getCentralDirectoryData();
/* 298 */       if (central != null) {
/* 299 */         System.arraycopy(central, 0, result, start, central.length);
/*     */       }
/*     */     } 
/* 302 */     return result;
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
/*     */   public static ZipExtraField fillExtraField(ZipExtraField ze, byte[] data, int off, int len, boolean local) throws ZipException {
/*     */     try {
/* 325 */       if (local) {
/* 326 */         ze.parseFromLocalFileData(data, off, len);
/*     */       } else {
/* 328 */         ze.parseFromCentralDirectoryData(data, off, len);
/*     */       } 
/* 330 */       return ze;
/* 331 */     } catch (ArrayIndexOutOfBoundsException aiobe) {
/* 332 */       throw (ZipException)(new ZipException("Failed to parse corrupt ZIP extra field of type " + 
/* 333 */           Integer.toHexString(ze.getHeaderId().getValue()))).initCause(aiobe);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class UnparseableExtraField
/*     */     implements UnparseableExtraFieldBehavior
/*     */   {
/*     */     public static final int THROW_KEY = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final int SKIP_KEY = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final int READ_KEY = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 363 */     public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 370 */     public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 377 */     public static final UnparseableExtraField READ = new UnparseableExtraField(2);
/*     */     
/*     */     private final int key;
/*     */ 
/*     */     
/*     */     private UnparseableExtraField(int k) {
/* 383 */       this.key = k;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getKey() {
/* 390 */       return this.key;
/*     */     }
/*     */     
/*     */     public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException {
/*     */       UnparseableExtraFieldData field;
/* 395 */       switch (this.key) {
/*     */         case 0:
/* 397 */           throw new ZipException("Bad extra field starting at " + off + ".  Block length of " + claimedLength + " bytes exceeds remaining data of " + (len - 4) + " bytes.");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 2:
/* 404 */           field = new UnparseableExtraFieldData();
/* 405 */           if (local) {
/* 406 */             field.parseFromLocalFileData(data, off, len);
/*     */           } else {
/* 408 */             field.parseFromCentralDirectoryData(data, off, len);
/*     */           } 
/* 410 */           return field;
/*     */         case 1:
/* 412 */           return null;
/*     */       } 
/* 414 */       throw new ZipException("Unknown UnparseableExtraField key: " + this.key);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 420 */   static final ZipExtraField[] EMPTY_ZIP_EXTRA_FIELD_ARRAY = new ZipExtraField[0];
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ExtraFieldUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */