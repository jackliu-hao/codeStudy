/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   protected final SegmentHeader segmentHeader;
/*     */   final int effort;
/*  38 */   private static final int[] effortThresholds = new int[] { 0, 0, 1000, 500, 100, 100, 100, 100, 100, 0 };
/*     */ 
/*     */ 
/*     */   
/*     */   private long[] canonicalLargest;
/*     */ 
/*     */   
/*     */   private long[] canonicalSmallest;
/*     */ 
/*     */ 
/*     */   
/*     */   public BandSet(int effort, SegmentHeader header) {
/*  50 */     this.effort = effort;
/*  51 */     this.segmentHeader = header;
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
/*     */   public byte[] encodeScalar(int[] band, BHSDCodec codec) throws Pack200Exception {
/*  72 */     return codec.encode(band);
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
/*     */   public byte[] encodeScalar(int value, BHSDCodec codec) throws Pack200Exception {
/*  84 */     return codec.encode(value);
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
/*     */   public byte[] encodeBandInt(String name, int[] ints, BHSDCodec defaultCodec) throws Pack200Exception {
/*  99 */     byte[] encodedBand = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 104 */     if (this.effort > 1 && ints.length >= effortThresholds[this.effort]) {
/* 105 */       BandAnalysisResults results = analyseBand(name, ints, defaultCodec);
/* 106 */       Codec betterCodec = results.betterCodec;
/* 107 */       encodedBand = results.encodedBand;
/* 108 */       if (betterCodec != null) {
/* 109 */         if (betterCodec instanceof BHSDCodec) {
/* 110 */           int[] specifierBand = CodecEncoding.getSpecifier(betterCodec, defaultCodec);
/* 111 */           int specifier = specifierBand[0];
/* 112 */           if (specifierBand.length > 1) {
/* 113 */             for (int i = 1; i < specifierBand.length; i++) {
/* 114 */               this.segmentHeader.appendBandCodingSpecifier(specifierBand[i]);
/*     */             }
/*     */           }
/* 117 */           if (defaultCodec.isSigned()) {
/* 118 */             specifier = -1 - specifier;
/*     */           } else {
/* 120 */             specifier += defaultCodec.getL();
/*     */           } 
/* 122 */           byte[] specifierEncoded = defaultCodec.encode(new int[] { specifier });
/* 123 */           byte[] band = new byte[specifierEncoded.length + encodedBand.length];
/* 124 */           System.arraycopy(specifierEncoded, 0, band, 0, specifierEncoded.length);
/* 125 */           System.arraycopy(encodedBand, 0, band, specifierEncoded.length, encodedBand.length);
/* 126 */           return band;
/*     */         } 
/* 128 */         if (betterCodec instanceof PopulationCodec) {
/* 129 */           int[] extraSpecifierInfo = results.extraMetadata;
/* 130 */           for (int i = 0; i < extraSpecifierInfo.length; i++) {
/* 131 */             this.segmentHeader.appendBandCodingSpecifier(extraSpecifierInfo[i]);
/*     */           }
/* 133 */           return encodedBand;
/*     */         } 
/* 135 */         if (betterCodec instanceof RunCodec);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 142 */     if (ints.length > 0) {
/* 143 */       if (encodedBand == null) {
/* 144 */         encodedBand = defaultCodec.encode(ints);
/*     */       }
/* 146 */       int first = ints[0];
/* 147 */       if (defaultCodec.getB() != 1) {
/* 148 */         if (defaultCodec.isSigned() && first >= -256 && first <= -1) {
/* 149 */           int specifier = -1 - CodecEncoding.getSpecifierForDefaultCodec(defaultCodec);
/* 150 */           byte[] specifierEncoded = defaultCodec.encode(new int[] { specifier });
/* 151 */           byte[] band = new byte[specifierEncoded.length + encodedBand.length];
/* 152 */           System.arraycopy(specifierEncoded, 0, band, 0, specifierEncoded.length);
/* 153 */           System.arraycopy(encodedBand, 0, band, specifierEncoded.length, encodedBand.length);
/* 154 */           return band;
/*     */         } 
/* 156 */         if (!defaultCodec.isSigned() && first >= defaultCodec.getL() && first <= defaultCodec.getL() + 255) {
/* 157 */           int specifier = CodecEncoding.getSpecifierForDefaultCodec(defaultCodec) + defaultCodec.getL();
/* 158 */           byte[] specifierEncoded = defaultCodec.encode(new int[] { specifier });
/* 159 */           byte[] band = new byte[specifierEncoded.length + encodedBand.length];
/* 160 */           System.arraycopy(specifierEncoded, 0, band, 0, specifierEncoded.length);
/* 161 */           System.arraycopy(encodedBand, 0, band, specifierEncoded.length, encodedBand.length);
/* 162 */           return band;
/*     */         } 
/*     */       } 
/* 165 */       return encodedBand;
/*     */     } 
/* 167 */     return new byte[0];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private BandAnalysisResults analyseBand(String name, int[] band, BHSDCodec defaultCodec) throws Pack200Exception {
/* 173 */     BandAnalysisResults results = new BandAnalysisResults();
/*     */     
/* 175 */     if (this.canonicalLargest == null) {
/* 176 */       this.canonicalLargest = new long[116];
/* 177 */       this.canonicalSmallest = new long[116];
/* 178 */       for (int i = 1; i < this.canonicalLargest.length; i++) {
/* 179 */         this.canonicalLargest[i] = CodecEncoding.getCanonicalCodec(i).largest();
/* 180 */         this.canonicalSmallest[i] = CodecEncoding.getCanonicalCodec(i).smallest();
/*     */       } 
/*     */     } 
/* 183 */     BandData bandData = new BandData(band);
/*     */ 
/*     */     
/* 186 */     byte[] encoded = defaultCodec.encode(band);
/* 187 */     results.encodedBand = encoded;
/*     */ 
/*     */     
/* 190 */     if (encoded.length <= band.length + 23 - 2 * this.effort) {
/* 191 */       return results;
/*     */     }
/*     */ 
/*     */     
/* 195 */     if (!bandData.anyNegatives() && bandData.largest <= Codec.BYTE1.largest()) {
/* 196 */       results.encodedBand = Codec.BYTE1.encode(band);
/* 197 */       results.betterCodec = Codec.BYTE1;
/* 198 */       return results;
/*     */     } 
/*     */ 
/*     */     
/* 202 */     if (this.effort > 3 && !name.equals("POPULATION")) {
/* 203 */       int numDistinctValues = bandData.numDistinctValues();
/* 204 */       float distinctValuesAsProportion = numDistinctValues / band.length;
/*     */ 
/*     */       
/* 207 */       if (numDistinctValues < 100 || distinctValuesAsProportion < 0.02D || (this.effort > 6 && distinctValuesAsProportion < 0.04D)) {
/*     */         
/* 209 */         encodeWithPopulationCodec(name, band, defaultCodec, bandData, results);
/* 210 */         if (timeToStop(results)) {
/* 211 */           return results;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 216 */     List<BHSDCodec[]> codecFamiliesToTry = new ArrayList();
/*     */ 
/*     */     
/* 219 */     if (bandData.mainlyPositiveDeltas() && bandData.mainlySmallDeltas()) {
/* 220 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs2);
/*     */     }
/*     */     
/* 223 */     if (bandData.wellCorrelated()) {
/* 224 */       if (bandData.mainlyPositiveDeltas()) {
/* 225 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs1);
/* 226 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs3);
/* 227 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs4);
/* 228 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs5);
/* 229 */         codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs1);
/* 230 */         codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs3);
/* 231 */         codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs4);
/* 232 */         codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs5);
/* 233 */         codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs2);
/*     */       } else {
/* 235 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs1);
/* 236 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs3);
/* 237 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs2);
/* 238 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs4);
/* 239 */         codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs5);
/* 240 */         codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaSignedCodecs1);
/* 241 */         codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaSignedCodecs2);
/*     */       } 
/* 243 */     } else if (bandData.anyNegatives()) {
/* 244 */       codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaSignedCodecs1);
/* 245 */       codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaSignedCodecs2);
/* 246 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs1);
/* 247 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs2);
/* 248 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs3);
/* 249 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs4);
/* 250 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaSignedCodecs5);
/*     */     } else {
/* 252 */       codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs1);
/* 253 */       codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs3);
/* 254 */       codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs4);
/* 255 */       codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs5);
/* 256 */       codecFamiliesToTry.add(CanonicalCodecFamilies.nonDeltaUnsignedCodecs2);
/* 257 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs1);
/* 258 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs3);
/* 259 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs4);
/* 260 */       codecFamiliesToTry.add(CanonicalCodecFamilies.deltaUnsignedCodecs5);
/*     */     } 
/* 262 */     if (name.equalsIgnoreCase("cpint")) {
/* 263 */       System.out.print("");
/*     */     }
/*     */     
/* 266 */     for (Iterator<BHSDCodec> iterator = codecFamiliesToTry.iterator(); iterator.hasNext(); ) {
/* 267 */       BHSDCodec[] family = (BHSDCodec[])iterator.next();
/* 268 */       tryCodecs(name, band, defaultCodec, bandData, results, encoded, family);
/* 269 */       if (timeToStop(results)) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 274 */     return results;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean timeToStop(BandAnalysisResults results) {
/* 280 */     if (this.effort > 6) {
/* 281 */       return (results.numCodecsTried >= this.effort * 2);
/*     */     }
/* 283 */     return (results.numCodecsTried >= this.effort);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void tryCodecs(String name, int[] band, BHSDCodec defaultCodec, BandData bandData, BandAnalysisResults results, byte[] encoded, BHSDCodec[] potentialCodecs) throws Pack200Exception {
/* 291 */     for (int i = 0; i < potentialCodecs.length; i++) {
/* 292 */       BHSDCodec potential = potentialCodecs[i];
/* 293 */       if (potential.equals(defaultCodec)) {
/*     */         return;
/*     */       }
/*     */       
/* 297 */       if (potential.isDelta()) {
/* 298 */         if (potential.largest() >= bandData.largestDelta && potential.smallest() <= bandData.smallestDelta && potential
/* 299 */           .largest() >= bandData.largest && potential.smallest() <= bandData.smallest) {
/*     */           
/* 301 */           byte[] encoded2 = potential.encode(band);
/* 302 */           results.numCodecsTried++;
/* 303 */           byte[] specifierEncoded = defaultCodec.encode(CodecEncoding.getSpecifier(potential, null));
/* 304 */           int saved = encoded.length - encoded2.length - specifierEncoded.length;
/* 305 */           if (saved > results.saved) {
/* 306 */             results.betterCodec = potential;
/* 307 */             results.encodedBand = encoded2;
/* 308 */             results.saved = saved;
/*     */           } 
/*     */         } 
/* 311 */       } else if (potential.largest() >= bandData.largest && potential.smallest() <= bandData.smallest) {
/* 312 */         byte[] encoded2 = potential.encode(band);
/* 313 */         results.numCodecsTried++;
/* 314 */         byte[] specifierEncoded = defaultCodec.encode(CodecEncoding.getSpecifier(potential, null));
/* 315 */         int saved = encoded.length - encoded2.length - specifierEncoded.length;
/* 316 */         if (saved > results.saved) {
/* 317 */           results.betterCodec = potential;
/* 318 */           results.encodedBand = encoded2;
/* 319 */           results.saved = saved;
/*     */         } 
/*     */       } 
/* 322 */       if (timeToStop(results)) {
/*     */         return;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void encodeWithPopulationCodec(String name, int[] band, BHSDCodec defaultCodec, BandData bandData, BandAnalysisResults results) throws Pack200Exception {
/*     */     byte[] tokensEncoded;
/* 356 */     results.numCodecsTried = results.numCodecsTried + 3;
/* 357 */     Map distinctValues = bandData.distinctValues;
/*     */     
/* 359 */     List<Integer> favoured = new ArrayList();
/* 360 */     for (Iterator<Integer> iterator = distinctValues.keySet().iterator(); iterator.hasNext(); ) {
/* 361 */       Integer value = iterator.next();
/* 362 */       Integer count = (Integer)distinctValues.get(value);
/* 363 */       if (count.intValue() > 2 || distinctValues.size() < 256) {
/* 364 */         favoured.add(value);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 369 */     if (distinctValues.size() > 255) {
/* 370 */       Collections.sort(favoured, (arg0, arg1) -> ((Integer)distinctValues.get(arg1)).compareTo((Integer)distinctValues.get(arg0)));
/*     */     }
/*     */ 
/*     */     
/* 374 */     IntList unfavoured = new IntList();
/* 375 */     Map<Object, Object> favouredToIndex = new HashMap<>();
/* 376 */     for (int i = 0; i < favoured.size(); i++) {
/* 377 */       Integer value = favoured.get(i);
/* 378 */       favouredToIndex.put(value, Integer.valueOf(i));
/*     */     } 
/*     */     
/* 381 */     int[] tokens = new int[band.length];
/* 382 */     for (int j = 0; j < band.length; j++) {
/* 383 */       Integer favouredIndex = (Integer)favouredToIndex.get(Integer.valueOf(band[j]));
/* 384 */       if (favouredIndex == null) {
/* 385 */         tokens[j] = 0;
/* 386 */         unfavoured.add(band[j]);
/*     */       } else {
/* 388 */         tokens[j] = favouredIndex.intValue() + 1;
/*     */       } 
/*     */     } 
/* 391 */     favoured.add(favoured.get(favoured.size() - 1));
/* 392 */     int[] favouredBand = integerListToArray(favoured);
/* 393 */     int[] unfavouredBand = unfavoured.toArray();
/*     */ 
/*     */     
/* 396 */     BandAnalysisResults favouredResults = analyseBand("POPULATION", favouredBand, defaultCodec);
/* 397 */     BandAnalysisResults unfavouredResults = analyseBand("POPULATION", unfavouredBand, defaultCodec);
/*     */     
/* 399 */     int tdefL = 0;
/* 400 */     int l = 0;
/* 401 */     Codec tokenCodec = null;
/*     */     
/* 403 */     int k = favoured.size() - 1;
/* 404 */     if (k < 256) {
/* 405 */       tdefL = 1;
/* 406 */       tokensEncoded = Codec.BYTE1.encode(tokens);
/*     */     } else {
/* 408 */       BandAnalysisResults tokenResults = analyseBand("POPULATION", tokens, defaultCodec);
/* 409 */       tokenCodec = tokenResults.betterCodec;
/* 410 */       tokensEncoded = tokenResults.encodedBand;
/* 411 */       if (tokenCodec == null) {
/* 412 */         tokenCodec = defaultCodec;
/*     */       }
/* 414 */       l = ((BHSDCodec)tokenCodec).getL();
/* 415 */       int h = ((BHSDCodec)tokenCodec).getH();
/* 416 */       int s = ((BHSDCodec)tokenCodec).getS();
/* 417 */       int b = ((BHSDCodec)tokenCodec).getB();
/* 418 */       int d = ((BHSDCodec)tokenCodec).isDelta() ? 1 : 0;
/* 419 */       if (s == 0 && d == 0) {
/* 420 */         boolean canUseTDefL = true;
/* 421 */         if (b > 1) {
/* 422 */           BHSDCodec oneLowerB = new BHSDCodec(b - 1, h);
/* 423 */           if (oneLowerB.largest() >= k) {
/* 424 */             canUseTDefL = false;
/*     */           }
/*     */         } 
/* 427 */         if (canUseTDefL) {
/* 428 */           switch (l) {
/*     */             case 4:
/* 430 */               tdefL = 1;
/*     */               break;
/*     */             case 8:
/* 433 */               tdefL = 2;
/*     */               break;
/*     */             case 16:
/* 436 */               tdefL = 3;
/*     */               break;
/*     */             case 32:
/* 439 */               tdefL = 4;
/*     */               break;
/*     */             case 64:
/* 442 */               tdefL = 5;
/*     */               break;
/*     */             case 128:
/* 445 */               tdefL = 6;
/*     */               break;
/*     */             case 192:
/* 448 */               tdefL = 7;
/*     */               break;
/*     */             case 224:
/* 451 */               tdefL = 8;
/*     */               break;
/*     */             case 240:
/* 454 */               tdefL = 9;
/*     */               break;
/*     */             case 248:
/* 457 */               tdefL = 10;
/*     */               break;
/*     */             case 252:
/* 460 */               tdefL = 11;
/*     */               break;
/*     */           } 
/*     */         
/*     */         }
/*     */       } 
/*     */     } 
/* 467 */     byte[] favouredEncoded = favouredResults.encodedBand;
/* 468 */     byte[] unfavouredEncoded = unfavouredResults.encodedBand;
/* 469 */     Codec favouredCodec = favouredResults.betterCodec;
/* 470 */     Codec unfavouredCodec = unfavouredResults.betterCodec;
/*     */     
/* 472 */     int specifier = 141 + ((favouredCodec == null) ? 1 : 0) + 4 * tdefL + ((unfavouredCodec == null) ? 2 : 0);
/* 473 */     IntList extraBandMetadata = new IntList(3);
/* 474 */     if (favouredCodec != null) {
/* 475 */       int[] specifiers = CodecEncoding.getSpecifier(favouredCodec, null);
/* 476 */       for (int m = 0; m < specifiers.length; m++) {
/* 477 */         extraBandMetadata.add(specifiers[m]);
/*     */       }
/*     */     } 
/* 480 */     if (tdefL == 0) {
/* 481 */       int[] specifiers = CodecEncoding.getSpecifier(tokenCodec, null);
/* 482 */       for (int m = 0; m < specifiers.length; m++) {
/* 483 */         extraBandMetadata.add(specifiers[m]);
/*     */       }
/*     */     } 
/* 486 */     if (unfavouredCodec != null) {
/* 487 */       int[] specifiers = CodecEncoding.getSpecifier(unfavouredCodec, null);
/* 488 */       for (int m = 0; m < specifiers.length; m++) {
/* 489 */         extraBandMetadata.add(specifiers[m]);
/*     */       }
/*     */     } 
/* 492 */     int[] extraMetadata = extraBandMetadata.toArray();
/* 493 */     byte[] extraMetadataEncoded = Codec.UNSIGNED5.encode(extraMetadata);
/* 494 */     if (defaultCodec.isSigned()) {
/* 495 */       specifier = -1 - specifier;
/*     */     } else {
/* 497 */       specifier += defaultCodec.getL();
/*     */     } 
/* 499 */     byte[] firstValueEncoded = defaultCodec.encode(new int[] { specifier });
/* 500 */     int totalBandLength = firstValueEncoded.length + favouredEncoded.length + tokensEncoded.length + unfavouredEncoded.length;
/*     */ 
/*     */     
/* 503 */     if (totalBandLength + extraMetadataEncoded.length < results.encodedBand.length) {
/* 504 */       results.saved = results.saved + results.encodedBand.length - totalBandLength + extraMetadataEncoded.length;
/* 505 */       byte[] encodedBand = new byte[totalBandLength];
/* 506 */       System.arraycopy(firstValueEncoded, 0, encodedBand, 0, firstValueEncoded.length);
/* 507 */       System.arraycopy(favouredEncoded, 0, encodedBand, firstValueEncoded.length, favouredEncoded.length);
/* 508 */       System.arraycopy(tokensEncoded, 0, encodedBand, firstValueEncoded.length + favouredEncoded.length, tokensEncoded.length);
/*     */       
/* 510 */       System.arraycopy(unfavouredEncoded, 0, encodedBand, firstValueEncoded.length + favouredEncoded.length + tokensEncoded.length, unfavouredEncoded.length);
/*     */       
/* 512 */       results.encodedBand = encodedBand;
/* 513 */       results.extraMetadata = extraMetadata;
/* 514 */       if (l != 0) {
/* 515 */         results.betterCodec = new PopulationCodec(favouredCodec, l, unfavouredCodec);
/*     */       } else {
/* 517 */         results.betterCodec = new PopulationCodec(favouredCodec, tokenCodec, unfavouredCodec);
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
/*     */   protected byte[] encodeFlags(String name, long[] flags, BHSDCodec loCodec, BHSDCodec hiCodec, boolean haveHiFlags) throws Pack200Exception {
/* 535 */     if (!haveHiFlags) {
/* 536 */       int[] arrayOfInt = new int[flags.length];
/* 537 */       for (int j = 0; j < flags.length; j++) {
/* 538 */         arrayOfInt[j] = (int)flags[j];
/*     */       }
/* 540 */       return encodeBandInt(name, arrayOfInt, loCodec);
/*     */     } 
/* 542 */     int[] hiBits = new int[flags.length];
/* 543 */     int[] loBits = new int[flags.length];
/* 544 */     for (int i = 0; i < flags.length; i++) {
/* 545 */       long l = flags[i];
/* 546 */       hiBits[i] = (int)(l >> 32L);
/* 547 */       loBits[i] = (int)l;
/*     */     } 
/* 549 */     byte[] hi = encodeBandInt(name, hiBits, hiCodec);
/* 550 */     byte[] lo = encodeBandInt(name, loBits, loCodec);
/* 551 */     byte[] total = new byte[hi.length + lo.length];
/* 552 */     System.arraycopy(hi, 0, total, 0, hi.length);
/* 553 */     System.arraycopy(lo, 0, total, hi.length + 1, lo.length);
/* 554 */     return total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] integerListToArray(List integerList) {
/* 564 */     int[] array = new int[integerList.size()];
/* 565 */     for (int i = 0; i < array.length; i++) {
/* 566 */       array[i] = ((Integer)integerList.get(i)).intValue();
/*     */     }
/* 568 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long[] longListToArray(List longList) {
/* 578 */     long[] array = new long[longList.size()];
/* 579 */     for (int i = 0; i < array.length; i++) {
/* 580 */       array[i] = ((Long)longList.get(i)).longValue();
/*     */     }
/* 582 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] cpEntryListToArray(List list) {
/* 592 */     int[] array = new int[list.size()];
/* 593 */     for (int i = 0; i < array.length; i++) {
/* 594 */       array[i] = ((ConstantPoolEntry)list.get(i)).getIndex();
/* 595 */       if (array[i] < 0) {
/* 596 */         throw new RuntimeException("Index should be > 0");
/*     */       }
/*     */     } 
/* 599 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] cpEntryOrNullListToArray(List<ConstantPoolEntry> theList) {
/* 609 */     int[] array = new int[theList.size()];
/* 610 */     for (int j = 0; j < array.length; j++) {
/* 611 */       ConstantPoolEntry cpEntry = theList.get(j);
/* 612 */       array[j] = (cpEntry == null) ? 0 : (cpEntry.getIndex() + 1);
/* 613 */       if (cpEntry != null && cpEntry.getIndex() < 0) {
/* 614 */         throw new RuntimeException("Index should be > 0");
/*     */       }
/*     */     } 
/* 617 */     return array;
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] encodeFlags(String name, long[][] flags, BHSDCodec loCodec, BHSDCodec hiCodec, boolean haveHiFlags) throws Pack200Exception {
/* 622 */     return encodeFlags(name, flatten(flags), loCodec, hiCodec, haveHiFlags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long[] flatten(long[][] flags) {
/* 629 */     int totalSize = 0;
/* 630 */     for (int i = 0; i < flags.length; i++) {
/* 631 */       totalSize += (flags[i]).length;
/*     */     }
/* 633 */     long[] flatArray = new long[totalSize];
/* 634 */     int index = 0;
/* 635 */     for (int j = 0; j < flags.length; j++) {
/* 636 */       for (int k = 0; k < (flags[j]).length; k++) {
/* 637 */         flatArray[index] = flags[j][k];
/* 638 */         index++;
/*     */       } 
/*     */     } 
/* 641 */     return flatArray;
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract void pack(OutputStream paramOutputStream) throws IOException, Pack200Exception;
/*     */ 
/*     */   
/*     */   public class BandData
/*     */   {
/*     */     private final int[] band;
/* 651 */     private int smallest = Integer.MAX_VALUE;
/* 652 */     private int largest = Integer.MIN_VALUE;
/*     */     
/*     */     private int smallestDelta;
/*     */     private int largestDelta;
/* 656 */     private int deltaIsAscending = 0;
/* 657 */     private int smallDeltaCount = 0;
/*     */     
/* 659 */     private double averageAbsoluteDelta = 0.0D;
/* 660 */     private double averageAbsoluteValue = 0.0D;
/*     */ 
/*     */ 
/*     */     
/*     */     private Map distinctValues;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BandData(int[] band) {
/* 670 */       this.band = band;
/* 671 */       Integer one = Integer.valueOf(1);
/* 672 */       for (int i = 0; i < band.length; i++) {
/* 673 */         if (band[i] < this.smallest) {
/* 674 */           this.smallest = band[i];
/*     */         }
/* 676 */         if (band[i] > this.largest) {
/* 677 */           this.largest = band[i];
/*     */         }
/* 679 */         if (i != 0) {
/* 680 */           int delta = band[i] - band[i - 1];
/* 681 */           if (delta < this.smallestDelta) {
/* 682 */             this.smallestDelta = delta;
/*     */           }
/* 684 */           if (delta > this.largestDelta) {
/* 685 */             this.largestDelta = delta;
/*     */           }
/* 687 */           if (delta >= 0) {
/* 688 */             this.deltaIsAscending++;
/*     */           }
/* 690 */           this.averageAbsoluteDelta += Math.abs(delta) / (band.length - 1);
/* 691 */           if (Math.abs(delta) < 256) {
/* 692 */             this.smallDeltaCount++;
/*     */           }
/*     */         } else {
/* 695 */           this.smallestDelta = band[0];
/* 696 */           this.largestDelta = band[0];
/*     */         } 
/* 698 */         this.averageAbsoluteValue += Math.abs(band[i]) / band.length;
/* 699 */         if (BandSet.this.effort > 3) {
/* 700 */           if (this.distinctValues == null) {
/* 701 */             this.distinctValues = new HashMap<>();
/*     */           }
/* 703 */           Integer value = Integer.valueOf(band[i]);
/* 704 */           Integer count = (Integer)this.distinctValues.get(value);
/* 705 */           if (count == null) {
/* 706 */             count = one;
/*     */           } else {
/* 708 */             count = Integer.valueOf(count.intValue() + 1);
/*     */           } 
/* 710 */           this.distinctValues.put(value, count);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean mainlySmallDeltas() {
/* 722 */       return (this.smallDeltaCount / this.band.length > 0.7F);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean wellCorrelated() {
/* 732 */       return (this.averageAbsoluteDelta * 3.1D < this.averageAbsoluteValue);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean mainlyPositiveDeltas() {
/* 742 */       return (this.deltaIsAscending / this.band.length > 0.95F);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean anyNegatives() {
/* 751 */       return (this.smallest < 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int numDistinctValues() {
/* 760 */       if (this.distinctValues == null) {
/* 761 */         return this.band.length;
/*     */       }
/* 763 */       return this.distinctValues.size();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public class BandAnalysisResults
/*     */   {
/* 774 */     private int numCodecsTried = 0;
/*     */ 
/*     */     
/* 777 */     private int saved = 0;
/*     */     private int[] extraMetadata;
/*     */     private byte[] encodedBand;
/*     */     private Codec betterCodec;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\BandSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */