/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.harmony.pack200.BHSDCodec;
/*     */ import org.apache.commons.compress.harmony.pack200.Codec;
/*     */ import org.apache.commons.compress.harmony.pack200.CodecEncoding;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*     */ import org.apache.commons.compress.harmony.pack200.PopulationCodec;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPFieldRef;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethodRef;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPString;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BandSet
/*     */ {
/*     */   protected Segment segment;
/*     */   protected SegmentHeader header;
/*     */   
/*     */   public abstract void read(InputStream paramInputStream) throws IOException, Pack200Exception;
/*     */   
/*     */   public abstract void unpack() throws IOException, Pack200Exception;
/*     */   
/*     */   public void unpack(InputStream in) throws IOException, Pack200Exception {
/*  50 */     read(in);
/*  51 */     unpack();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BandSet(Segment segment) {
/*  59 */     this.segment = segment;
/*  60 */     this.header = segment.getSegmentHeader();
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
/*     */   public int[] decodeBandInt(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/*     */     int[] band;
/*     */     Codec codec1;
/*  81 */     BHSDCodec bHSDCodec = codec;
/*  82 */     if (codec.getB() == 1 || count == 0) {
/*  83 */       return codec.decodeInts(count, in);
/*     */     }
/*  85 */     int[] getFirst = codec.decodeInts(1, in);
/*  86 */     if (getFirst.length == 0) {
/*  87 */       return getFirst;
/*     */     }
/*  89 */     int first = getFirst[0];
/*  90 */     if (codec.isSigned() && first >= -256 && first <= -1) {
/*     */       
/*  92 */       codec1 = CodecEncoding.getCodec(-1 - first, this.header.getBandHeadersInputStream(), (Codec)codec);
/*  93 */       band = codec1.decodeInts(count, in);
/*  94 */     } else if (!codec.isSigned() && first >= codec.getL() && first <= codec.getL() + 255) {
/*     */       
/*  96 */       codec1 = CodecEncoding.getCodec(first - codec.getL(), this.header.getBandHeadersInputStream(), (Codec)codec);
/*  97 */       band = codec1.decodeInts(count, in);
/*     */     } else {
/*     */       
/* 100 */       band = codec.decodeInts(count - 1, in, first);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 107 */     if (codec1 instanceof PopulationCodec) {
/* 108 */       PopulationCodec popCodec = (PopulationCodec)codec1;
/* 109 */       int[] favoured = (int[])popCodec.getFavoured().clone();
/* 110 */       Arrays.sort(favoured);
/* 111 */       for (int i = 0; i < band.length; i++) {
/* 112 */         boolean favouredValue = (Arrays.binarySearch(favoured, band[i]) > -1);
/* 113 */         Codec theCodec = favouredValue ? popCodec.getFavouredCodec() : popCodec.getUnfavouredCodec();
/* 114 */         if (theCodec instanceof BHSDCodec && ((BHSDCodec)theCodec).isDelta()) {
/* 115 */           BHSDCodec bhsd = (BHSDCodec)theCodec;
/* 116 */           long cardinality = bhsd.cardinality();
/* 117 */           while (band[i] > bhsd.largest()) {
/* 118 */             band[i] = (int)(band[i] - cardinality);
/*     */           }
/* 120 */           while (band[i] < bhsd.smallest()) {
/* 121 */             band[i] = (int)(band[i] + cardinality);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 126 */     return band;
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
/*     */   public int[][] decodeBandInt(String name, InputStream in, BHSDCodec defaultCodec, int[] counts) throws IOException, Pack200Exception {
/* 142 */     int[][] result = new int[counts.length][];
/* 143 */     int totalCount = 0;
/* 144 */     for (int i = 0; i < counts.length; i++) {
/* 145 */       totalCount += counts[i];
/*     */     }
/* 147 */     int[] twoDResult = decodeBandInt(name, in, defaultCodec, totalCount);
/* 148 */     int index = 0;
/* 149 */     for (int j = 0; j < result.length; j++) {
/* 150 */       result[j] = new int[counts[j]];
/* 151 */       for (int k = 0; k < (result[j]).length; k++) {
/* 152 */         result[j][k] = twoDResult[index];
/* 153 */         index++;
/*     */       } 
/*     */     } 
/* 156 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] parseFlags(String name, InputStream in, int count, BHSDCodec codec, boolean hasHi) throws IOException, Pack200Exception {
/* 161 */     return parseFlags(name, in, new int[] { count }, hasHi ? codec : null, codec)[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public long[][] parseFlags(String name, InputStream in, int[] counts, BHSDCodec codec, boolean hasHi) throws IOException, Pack200Exception {
/* 166 */     return parseFlags(name, in, counts, hasHi ? codec : null, codec);
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] parseFlags(String name, InputStream in, int count, BHSDCodec hiCodec, BHSDCodec loCodec) throws IOException, Pack200Exception {
/* 171 */     return parseFlags(name, in, new int[] { count }, hiCodec, loCodec)[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public long[][] parseFlags(String name, InputStream in, int[] counts, BHSDCodec hiCodec, BHSDCodec loCodec) throws IOException, Pack200Exception {
/* 176 */     int lo[], count = counts.length;
/* 177 */     if (count == 0) {
/* 178 */       return new long[][] { {} };
/*     */     }
/* 180 */     int sum = 0;
/* 181 */     long[][] result = new long[count][];
/* 182 */     for (int i = 0; i < count; i++) {
/* 183 */       result[i] = new long[counts[i]];
/* 184 */       sum += counts[i];
/*     */     } 
/* 186 */     int[] hi = null;
/*     */     
/* 188 */     if (hiCodec != null) {
/* 189 */       hi = decodeBandInt(name, in, hiCodec, sum);
/* 190 */       lo = decodeBandInt(name, in, loCodec, sum);
/*     */     } else {
/* 192 */       lo = decodeBandInt(name, in, loCodec, sum);
/*     */     } 
/*     */     
/* 195 */     int index = 0;
/* 196 */     for (int j = 0; j < result.length; j++) {
/* 197 */       for (int k = 0; k < (result[j]).length; k++) {
/* 198 */         if (hi != null) {
/* 199 */           result[j][k] = hi[index] << 32L | lo[index] & 0xFFFFFFFFL;
/*     */         } else {
/* 201 */           result[j][k] = lo[index];
/*     */         } 
/* 203 */         index++;
/*     */       } 
/*     */     } 
/* 206 */     return result;
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
/*     */   public String[] parseReferences(String name, InputStream in, BHSDCodec codec, int count, String[] reference) throws IOException, Pack200Exception {
/* 226 */     return parseReferences(name, in, codec, new int[] { count }, reference)[0];
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
/*     */   public String[][] parseReferences(String name, InputStream in, BHSDCodec codec, int[] counts, String[] reference) throws IOException, Pack200Exception {
/* 247 */     int count = counts.length;
/* 248 */     if (count == 0) {
/* 249 */       return new String[][] { {} };
/*     */     }
/* 251 */     String[][] result = new String[count][];
/* 252 */     int sum = 0;
/* 253 */     for (int i = 0; i < count; i++) {
/* 254 */       result[i] = new String[counts[i]];
/* 255 */       sum += counts[i];
/*     */     } 
/*     */     
/* 258 */     String[] result1 = new String[sum];
/* 259 */     int[] indices = decodeBandInt(name, in, codec, sum);
/* 260 */     for (int i1 = 0; i1 < sum; i1++) {
/* 261 */       int index = indices[i1];
/* 262 */       if (index < 0 || index >= reference.length) {
/* 263 */         throw new Pack200Exception("Something has gone wrong during parsing references, index = " + index + ", array size = " + reference.length);
/*     */       }
/*     */       
/* 266 */       result1[i1] = reference[index];
/*     */     } 
/* 268 */     String[] refs = result1;
/*     */     
/* 270 */     int pos = 0;
/* 271 */     for (int j = 0; j < count; j++) {
/* 272 */       int num = counts[j];
/* 273 */       result[j] = new String[num];
/* 274 */       System.arraycopy(refs, pos, result[j], 0, num);
/* 275 */       pos += num;
/*     */     } 
/* 277 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPInteger[] parseCPIntReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 282 */     int[] reference = this.segment.getCpBands().getCpInt();
/* 283 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 284 */     CPInteger[] result = new CPInteger[indices.length];
/* 285 */     for (int i1 = 0; i1 < count; i1++) {
/* 286 */       int index = indices[i1];
/* 287 */       if (index < 0 || index >= reference.length) {
/* 288 */         throw new Pack200Exception("Something has gone wrong during parsing references, index = " + index + ", array size = " + reference.length);
/*     */       }
/*     */       
/* 291 */       result[i1] = this.segment.getCpBands().cpIntegerValue(index);
/*     */     } 
/* 293 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPDouble[] parseCPDoubleReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 298 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 299 */     CPDouble[] result = new CPDouble[indices.length];
/* 300 */     for (int i1 = 0; i1 < count; i1++) {
/* 301 */       int index = indices[i1];
/* 302 */       result[i1] = this.segment.getCpBands().cpDoubleValue(index);
/*     */     } 
/* 304 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPFloat[] parseCPFloatReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 309 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 310 */     CPFloat[] result = new CPFloat[indices.length];
/* 311 */     for (int i1 = 0; i1 < count; i1++) {
/* 312 */       int index = indices[i1];
/* 313 */       result[i1] = this.segment.getCpBands().cpFloatValue(index);
/*     */     } 
/* 315 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPLong[] parseCPLongReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 320 */     long[] reference = this.segment.getCpBands().getCpLong();
/* 321 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 322 */     CPLong[] result = new CPLong[indices.length];
/* 323 */     for (int i1 = 0; i1 < count; i1++) {
/* 324 */       int index = indices[i1];
/* 325 */       if (index < 0 || index >= reference.length) {
/* 326 */         throw new Pack200Exception("Something has gone wrong during parsing references, index = " + index + ", array size = " + reference.length);
/*     */       }
/*     */       
/* 329 */       result[i1] = this.segment.getCpBands().cpLongValue(index);
/*     */     } 
/* 331 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPUTF8[] parseCPUTF8References(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 336 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 337 */     CPUTF8[] result = new CPUTF8[indices.length];
/* 338 */     for (int i1 = 0; i1 < count; i1++) {
/* 339 */       int index = indices[i1];
/* 340 */       result[i1] = this.segment.getCpBands().cpUTF8Value(index);
/*     */     } 
/* 342 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPUTF8[][] parseCPUTF8References(String name, InputStream in, BHSDCodec codec, int[] counts) throws IOException, Pack200Exception {
/* 347 */     CPUTF8[][] result = new CPUTF8[counts.length][];
/* 348 */     int sum = 0;
/* 349 */     for (int i = 0; i < counts.length; i++) {
/* 350 */       result[i] = new CPUTF8[counts[i]];
/* 351 */       sum += counts[i];
/*     */     } 
/* 353 */     CPUTF8[] result1 = new CPUTF8[sum];
/* 354 */     int[] indices = decodeBandInt(name, in, codec, sum);
/* 355 */     for (int i1 = 0; i1 < sum; i1++) {
/* 356 */       int index = indices[i1];
/* 357 */       result1[i1] = this.segment.getCpBands().cpUTF8Value(index);
/*     */     } 
/* 359 */     CPUTF8[] refs = result1;
/* 360 */     int pos = 0;
/* 361 */     for (int j = 0; j < counts.length; j++) {
/* 362 */       int num = counts[j];
/* 363 */       result[j] = new CPUTF8[num];
/* 364 */       System.arraycopy(refs, pos, result[j], 0, num);
/* 365 */       pos += num;
/*     */     } 
/* 367 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPString[] parseCPStringReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 372 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 373 */     CPString[] result = new CPString[indices.length];
/* 374 */     for (int i1 = 0; i1 < count; i1++) {
/* 375 */       int index = indices[i1];
/* 376 */       result[i1] = this.segment.getCpBands().cpStringValue(index);
/*     */     } 
/* 378 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPInterfaceMethodRef[] parseCPInterfaceMethodRefReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 383 */     CpBands cpBands = this.segment.getCpBands();
/* 384 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 385 */     CPInterfaceMethodRef[] result = new CPInterfaceMethodRef[indices.length];
/* 386 */     for (int i1 = 0; i1 < count; i1++) {
/* 387 */       int index = indices[i1];
/* 388 */       result[i1] = cpBands.cpIMethodValue(index);
/*     */     } 
/* 390 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPMethodRef[] parseCPMethodRefReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 395 */     CpBands cpBands = this.segment.getCpBands();
/* 396 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 397 */     CPMethodRef[] result = new CPMethodRef[indices.length];
/* 398 */     for (int i1 = 0; i1 < count; i1++) {
/* 399 */       int index = indices[i1];
/* 400 */       result[i1] = cpBands.cpMethodValue(index);
/*     */     } 
/* 402 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPFieldRef[] parseCPFieldRefReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 407 */     CpBands cpBands = this.segment.getCpBands();
/* 408 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 409 */     CPFieldRef[] result = new CPFieldRef[indices.length];
/* 410 */     for (int i1 = 0; i1 < count; i1++) {
/* 411 */       int index = indices[i1];
/* 412 */       result[i1] = cpBands.cpFieldValue(index);
/*     */     } 
/* 414 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPNameAndType[] parseCPDescriptorReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 419 */     CpBands cpBands = this.segment.getCpBands();
/* 420 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 421 */     CPNameAndType[] result = new CPNameAndType[indices.length];
/* 422 */     for (int i1 = 0; i1 < count; i1++) {
/* 423 */       int index = indices[i1];
/* 424 */       result[i1] = cpBands.cpNameAndTypeValue(index);
/*     */     } 
/* 426 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPUTF8[] parseCPSignatureReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 431 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 432 */     CPUTF8[] result = new CPUTF8[indices.length];
/* 433 */     for (int i1 = 0; i1 < count; i1++) {
/* 434 */       int index = indices[i1];
/* 435 */       result[i1] = this.segment.getCpBands().cpSignatureValue(index);
/*     */     } 
/* 437 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected CPUTF8[][] parseCPSignatureReferences(String name, InputStream in, BHSDCodec codec, int[] counts) throws IOException, Pack200Exception {
/* 442 */     CPUTF8[][] result = new CPUTF8[counts.length][];
/* 443 */     int sum = 0;
/* 444 */     for (int i = 0; i < counts.length; i++) {
/* 445 */       result[i] = new CPUTF8[counts[i]];
/* 446 */       sum += counts[i];
/*     */     } 
/* 448 */     CPUTF8[] result1 = new CPUTF8[sum];
/* 449 */     int[] indices = decodeBandInt(name, in, codec, sum);
/* 450 */     for (int i1 = 0; i1 < sum; i1++) {
/* 451 */       int index = indices[i1];
/* 452 */       result1[i1] = this.segment.getCpBands().cpSignatureValue(index);
/*     */     } 
/* 454 */     CPUTF8[] refs = result1;
/* 455 */     int pos = 0;
/* 456 */     for (int j = 0; j < counts.length; j++) {
/* 457 */       int num = counts[j];
/* 458 */       result[j] = new CPUTF8[num];
/* 459 */       System.arraycopy(refs, pos, result[j], 0, num);
/* 460 */       pos += num;
/*     */     } 
/* 462 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public CPClass[] parseCPClassReferences(String name, InputStream in, BHSDCodec codec, int count) throws IOException, Pack200Exception {
/* 467 */     int[] indices = decodeBandInt(name, in, codec, count);
/* 468 */     CPClass[] result = new CPClass[indices.length];
/* 469 */     for (int i1 = 0; i1 < count; i1++) {
/* 470 */       int index = indices[i1];
/* 471 */       result[i1] = this.segment.getCpBands().cpClassValue(index);
/*     */     } 
/* 473 */     return result;
/*     */   }
/*     */   
/*     */   protected String[] getReferences(int[] ints, String[] reference) {
/* 477 */     String[] result = new String[ints.length];
/* 478 */     for (int i = 0; i < result.length; i++) {
/* 479 */       result[i] = reference[ints[i]];
/*     */     }
/* 481 */     return result;
/*     */   }
/*     */   
/*     */   protected String[][] getReferences(int[][] ints, String[] reference) {
/* 485 */     String[][] result = new String[ints.length][];
/* 486 */     for (int i = 0; i < result.length; i++) {
/* 487 */       result[i] = new String[(ints[i]).length];
/* 488 */       for (int j = 0; j < (result[i]).length; j++) {
/* 489 */         result[i][j] = reference[ints[i][j]];
/*     */       }
/*     */     } 
/*     */     
/* 493 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\BandSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */