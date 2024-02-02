/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MetadataBandGroup
/*     */   extends BandSet
/*     */ {
/*     */   public static final int CONTEXT_CLASS = 0;
/*     */   public static final int CONTEXT_FIELD = 1;
/*     */   public static final int CONTEXT_METHOD = 2;
/*     */   private final String type;
/*  35 */   private int numBackwardsCalls = 0;
/*     */   
/*  37 */   public IntList param_NB = new IntList();
/*  38 */   public IntList anno_N = new IntList();
/*  39 */   public List type_RS = new ArrayList();
/*  40 */   public IntList pair_N = new IntList();
/*  41 */   public List name_RU = new ArrayList();
/*  42 */   public List T = new ArrayList();
/*  43 */   public List caseI_KI = new ArrayList();
/*  44 */   public List caseD_KD = new ArrayList();
/*  45 */   public List caseF_KF = new ArrayList();
/*  46 */   public List caseJ_KJ = new ArrayList();
/*  47 */   public List casec_RS = new ArrayList();
/*  48 */   public List caseet_RS = new ArrayList();
/*  49 */   public List caseec_RU = new ArrayList();
/*  50 */   public List cases_RU = new ArrayList();
/*  51 */   public IntList casearray_N = new IntList();
/*  52 */   public List nesttype_RS = new ArrayList();
/*  53 */   public IntList nestpair_N = new IntList();
/*  54 */   public List nestname_RU = new ArrayList();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final CpBands cpBands;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int context;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MetadataBandGroup(String type, int context, CpBands cpBands, SegmentHeader segmentHeader, int effort) {
/*  70 */     super(effort, segmentHeader);
/*  71 */     this.type = type;
/*  72 */     this.cpBands = cpBands;
/*  73 */     this.context = context;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/*  83 */     PackingUtils.log("Writing metadata band group...");
/*  84 */     if (hasContent()) {
/*     */       String contextStr;
/*  86 */       if (this.context == 0) {
/*  87 */         contextStr = "Class";
/*  88 */       } else if (this.context == 1) {
/*  89 */         contextStr = "Field";
/*     */       } else {
/*  91 */         contextStr = "Method";
/*     */       } 
/*  93 */       byte[] encodedBand = null;
/*  94 */       if (!this.type.equals("AD")) {
/*  95 */         if (this.type.indexOf('P') != -1) {
/*     */           
/*  97 */           encodedBand = encodeBandInt(contextStr + "_" + this.type + " param_NB", this.param_NB.toArray(), Codec.BYTE1);
/*  98 */           out.write(encodedBand);
/*  99 */           PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " anno_N[" + this.param_NB
/* 100 */               .size() + "]");
/*     */         } 
/* 102 */         encodedBand = encodeBandInt(contextStr + "_" + this.type + " anno_N", this.anno_N.toArray(), Codec.UNSIGNED5);
/* 103 */         out.write(encodedBand);
/* 104 */         PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " anno_N[" + this.anno_N
/* 105 */             .size() + "]");
/*     */         
/* 107 */         encodedBand = encodeBandInt(contextStr + "_" + this.type + " type_RS", cpEntryListToArray(this.type_RS), Codec.UNSIGNED5);
/*     */         
/* 109 */         out.write(encodedBand);
/* 110 */         PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " type_RS[" + this.type_RS
/* 111 */             .size() + "]");
/*     */         
/* 113 */         encodedBand = encodeBandInt(contextStr + "_" + this.type + " pair_N", this.pair_N.toArray(), Codec.UNSIGNED5);
/* 114 */         out.write(encodedBand);
/* 115 */         PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " pair_N[" + this.pair_N
/* 116 */             .size() + "]");
/*     */         
/* 118 */         encodedBand = encodeBandInt(contextStr + "_" + this.type + " name_RU", cpEntryListToArray(this.name_RU), Codec.UNSIGNED5);
/*     */         
/* 120 */         out.write(encodedBand);
/* 121 */         PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " name_RU[" + this.name_RU
/* 122 */             .size() + "]");
/*     */       } 
/* 124 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " T", tagListToArray(this.T), Codec.BYTE1);
/* 125 */       out.write(encodedBand);
/*     */       
/* 127 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " T[" + this.T.size() + "]");
/*     */       
/* 129 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " caseI_KI", cpEntryListToArray(this.caseI_KI), Codec.UNSIGNED5);
/*     */       
/* 131 */       out.write(encodedBand);
/* 132 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseI_KI[" + this.caseI_KI
/* 133 */           .size() + "]");
/*     */       
/* 135 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " caseD_KD", cpEntryListToArray(this.caseD_KD), Codec.UNSIGNED5);
/*     */       
/* 137 */       out.write(encodedBand);
/* 138 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseD_KD[" + this.caseD_KD
/* 139 */           .size() + "]");
/*     */       
/* 141 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " caseF_KF", cpEntryListToArray(this.caseF_KF), Codec.UNSIGNED5);
/*     */       
/* 143 */       out.write(encodedBand);
/* 144 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseF_KF[" + this.caseF_KF
/* 145 */           .size() + "]");
/*     */       
/* 147 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " caseJ_KJ", cpEntryListToArray(this.caseJ_KJ), Codec.UNSIGNED5);
/*     */       
/* 149 */       out.write(encodedBand);
/* 150 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseJ_KJ[" + this.caseJ_KJ
/* 151 */           .size() + "]");
/*     */       
/* 153 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " casec_RS", cpEntryListToArray(this.casec_RS), Codec.UNSIGNED5);
/*     */       
/* 155 */       out.write(encodedBand);
/* 156 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " casec_RS[" + this.casec_RS
/* 157 */           .size() + "]");
/*     */       
/* 159 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " caseet_RS", cpEntryListToArray(this.caseet_RS), Codec.UNSIGNED5);
/*     */       
/* 161 */       out.write(encodedBand);
/* 162 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseet_RS[" + this.caseet_RS
/* 163 */           .size() + "]");
/*     */       
/* 165 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " caseec_RU", cpEntryListToArray(this.caseec_RU), Codec.UNSIGNED5);
/*     */       
/* 167 */       out.write(encodedBand);
/* 168 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " caseec_RU[" + this.caseec_RU
/* 169 */           .size() + "]");
/*     */       
/* 171 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " cases_RU", cpEntryListToArray(this.cases_RU), Codec.UNSIGNED5);
/*     */       
/* 173 */       out.write(encodedBand);
/* 174 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " cases_RU[" + this.cases_RU
/* 175 */           .size() + "]");
/*     */       
/* 177 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " casearray_N", this.casearray_N.toArray(), Codec.UNSIGNED5);
/*     */       
/* 179 */       out.write(encodedBand);
/* 180 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " casearray_N[" + this.casearray_N
/* 181 */           .size() + "]");
/*     */       
/* 183 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " nesttype_RS", cpEntryListToArray(this.nesttype_RS), Codec.UNSIGNED5);
/*     */       
/* 185 */       out.write(encodedBand);
/* 186 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " nesttype_RS[" + this.nesttype_RS
/* 187 */           .size() + "]");
/*     */       
/* 189 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " nestpair_N", this.nestpair_N.toArray(), Codec.UNSIGNED5);
/* 190 */       out.write(encodedBand);
/* 191 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " nestpair_N[" + this.nestpair_N
/* 192 */           .size() + "]");
/*     */       
/* 194 */       encodedBand = encodeBandInt(contextStr + "_" + this.type + " nestname_RU", cpEntryListToArray(this.nestname_RU), Codec.UNSIGNED5);
/*     */       
/* 196 */       out.write(encodedBand);
/* 197 */       PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + contextStr + "_" + this.type + " nestname_RU[" + this.nestname_RU
/* 198 */           .size() + "]");
/*     */     } 
/*     */   }
/*     */   
/*     */   private int[] tagListToArray(List t2) {
/* 203 */     int[] ints = new int[t2.size()];
/* 204 */     for (int i = 0; i < ints.length; i++) {
/* 205 */       ints[i] = ((String)t2.get(i)).charAt(0);
/*     */     }
/* 207 */     return ints;
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
/*     */   public void addParameterAnnotation(int numParams, int[] annoN, IntList pairN, List typeRS, List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
/* 228 */     this.param_NB.add(numParams);
/* 229 */     for (int i = 0; i < annoN.length; i++) {
/* 230 */       this.anno_N.add(annoN[i]);
/*     */     }
/* 232 */     this.pair_N.addAll(pairN);
/* 233 */     for (Iterator<String> iterator1 = typeRS.iterator(); iterator1.hasNext(); ) {
/* 234 */       String desc = iterator1.next();
/* 235 */       this.type_RS.add(this.cpBands.getCPSignature(desc));
/*     */     } 
/* 237 */     for (Iterator<String> iterator = nameRU.iterator(); iterator.hasNext(); ) {
/* 238 */       String name = iterator.next();
/* 239 */       this.name_RU.add(this.cpBands.getCPUtf8(name));
/*     */     } 
/* 241 */     Iterator<Integer> valuesIterator = values.iterator();
/* 242 */     for (Iterator<String> iterator6 = t.iterator(); iterator6.hasNext(); ) {
/* 243 */       String tag = iterator6.next();
/* 244 */       this.T.add(tag);
/* 245 */       if (tag.equals("B") || tag.equals("C") || tag.equals("I") || tag.equals("S") || tag.equals("Z")) {
/* 246 */         Integer value = valuesIterator.next();
/* 247 */         this.caseI_KI.add(this.cpBands.getConstant(value)); continue;
/* 248 */       }  if (tag.equals("D")) {
/* 249 */         Double value = (Double)valuesIterator.next();
/* 250 */         this.caseD_KD.add(this.cpBands.getConstant(value)); continue;
/* 251 */       }  if (tag.equals("F")) {
/* 252 */         Float value = (Float)valuesIterator.next();
/* 253 */         this.caseF_KF.add(this.cpBands.getConstant(value)); continue;
/* 254 */       }  if (tag.equals("J")) {
/* 255 */         Long value = (Long)valuesIterator.next();
/* 256 */         this.caseJ_KJ.add(this.cpBands.getConstant(value)); continue;
/* 257 */       }  if (tag.equals("c")) {
/* 258 */         String value = (String)valuesIterator.next();
/* 259 */         this.casec_RS.add(this.cpBands.getCPSignature(value)); continue;
/* 260 */       }  if (tag.equals("e")) {
/* 261 */         String value = (String)valuesIterator.next();
/* 262 */         String value2 = (String)valuesIterator.next();
/* 263 */         this.caseet_RS.add(this.cpBands.getCPSignature(value));
/* 264 */         this.caseec_RU.add(this.cpBands.getCPUtf8(value2)); continue;
/* 265 */       }  if (tag.equals("s")) {
/* 266 */         String value = (String)valuesIterator.next();
/* 267 */         this.cases_RU.add(this.cpBands.getCPUtf8(value));
/*     */       } 
/*     */     } 
/*     */     
/* 271 */     for (Iterator<Integer> iterator5 = caseArrayN.iterator(); iterator5.hasNext(); ) {
/* 272 */       int arraySize = ((Integer)iterator5.next()).intValue();
/* 273 */       this.casearray_N.add(arraySize);
/* 274 */       this.numBackwardsCalls += arraySize;
/*     */     } 
/* 276 */     for (Iterator<String> iterator4 = nestTypeRS.iterator(); iterator4.hasNext(); ) {
/* 277 */       String type = iterator4.next();
/* 278 */       this.nesttype_RS.add(this.cpBands.getCPSignature(type));
/*     */     } 
/* 280 */     for (Iterator<String> iterator3 = nestNameRU.iterator(); iterator3.hasNext(); ) {
/* 281 */       String name = iterator3.next();
/* 282 */       this.nestname_RU.add(this.cpBands.getCPUtf8(name));
/*     */     } 
/* 284 */     for (Iterator<Integer> iterator2 = nestPairN.iterator(); iterator2.hasNext(); ) {
/* 285 */       Integer numPairs = iterator2.next();
/* 286 */       this.nestpair_N.add(numPairs.intValue());
/* 287 */       this.numBackwardsCalls += numPairs.intValue();
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
/*     */   public void addAnnotation(String desc, List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
/* 305 */     this.type_RS.add(this.cpBands.getCPSignature(desc));
/* 306 */     this.pair_N.add(nameRU.size());
/*     */     
/* 308 */     for (Iterator<String> iterator = nameRU.iterator(); iterator.hasNext(); ) {
/* 309 */       String name = iterator.next();
/* 310 */       this.name_RU.add(this.cpBands.getCPUtf8(name));
/*     */     } 
/*     */     
/* 313 */     Iterator<Integer> valuesIterator = values.iterator();
/* 314 */     for (Iterator<String> iterator5 = t.iterator(); iterator5.hasNext(); ) {
/* 315 */       String tag = iterator5.next();
/* 316 */       this.T.add(tag);
/* 317 */       if (tag.equals("B") || tag.equals("C") || tag.equals("I") || tag.equals("S") || tag.equals("Z")) {
/* 318 */         Integer value = valuesIterator.next();
/* 319 */         this.caseI_KI.add(this.cpBands.getConstant(value)); continue;
/* 320 */       }  if (tag.equals("D")) {
/* 321 */         Double value = (Double)valuesIterator.next();
/* 322 */         this.caseD_KD.add(this.cpBands.getConstant(value)); continue;
/* 323 */       }  if (tag.equals("F")) {
/* 324 */         Float value = (Float)valuesIterator.next();
/* 325 */         this.caseF_KF.add(this.cpBands.getConstant(value)); continue;
/* 326 */       }  if (tag.equals("J")) {
/* 327 */         Long value = (Long)valuesIterator.next();
/* 328 */         this.caseJ_KJ.add(this.cpBands.getConstant(value)); continue;
/* 329 */       }  if (tag.equals("c")) {
/* 330 */         String value = (String)valuesIterator.next();
/* 331 */         this.casec_RS.add(this.cpBands.getCPSignature(value)); continue;
/* 332 */       }  if (tag.equals("e")) {
/* 333 */         String value = (String)valuesIterator.next();
/* 334 */         String value2 = (String)valuesIterator.next();
/* 335 */         this.caseet_RS.add(this.cpBands.getCPSignature(value));
/* 336 */         this.caseec_RU.add(this.cpBands.getCPUtf8(value2)); continue;
/* 337 */       }  if (tag.equals("s")) {
/* 338 */         String value = (String)valuesIterator.next();
/* 339 */         this.cases_RU.add(this.cpBands.getCPUtf8(value));
/*     */       } 
/*     */     } 
/*     */     
/* 343 */     for (Iterator<Integer> iterator4 = caseArrayN.iterator(); iterator4.hasNext(); ) {
/* 344 */       int arraySize = ((Integer)iterator4.next()).intValue();
/* 345 */       this.casearray_N.add(arraySize);
/* 346 */       this.numBackwardsCalls += arraySize;
/*     */     } 
/* 348 */     for (Iterator<String> iterator3 = nestTypeRS.iterator(); iterator3.hasNext(); ) {
/* 349 */       String type = iterator3.next();
/* 350 */       this.nesttype_RS.add(this.cpBands.getCPSignature(type));
/*     */     } 
/* 352 */     for (Iterator<String> iterator2 = nestNameRU.iterator(); iterator2.hasNext(); ) {
/* 353 */       String name = iterator2.next();
/* 354 */       this.nestname_RU.add(this.cpBands.getCPUtf8(name));
/*     */     } 
/* 356 */     for (Iterator<Integer> iterator1 = nestPairN.iterator(); iterator1.hasNext(); ) {
/* 357 */       Integer numPairs = iterator1.next();
/* 358 */       this.nestpair_N.add(numPairs.intValue());
/* 359 */       this.numBackwardsCalls += numPairs.intValue();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasContent() {
/* 369 */     return (this.type_RS.size() > 0);
/*     */   }
/*     */   
/*     */   public int numBackwardsCalls() {
/* 373 */     return this.numBackwardsCalls;
/*     */   }
/*     */   
/*     */   public void incrementAnnoN() {
/* 377 */     this.anno_N.increment(this.anno_N.size() - 1);
/*     */   }
/*     */   
/*     */   public void newEntryInAnnoN() {
/* 381 */     this.anno_N.add(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeLatest() {
/* 388 */     int latest = this.anno_N.remove(this.anno_N.size() - 1);
/* 389 */     for (int i = 0; i < latest; i++) {
/* 390 */       this.type_RS.remove(this.type_RS.size() - 1);
/* 391 */       int pairs = this.pair_N.remove(this.pair_N.size() - 1);
/* 392 */       for (int j = 0; j < pairs; j++) {
/* 393 */         removeOnePair();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void removeOnePair() {
/* 402 */     String tag = this.T.remove(this.T.size() - 1);
/* 403 */     if (tag.equals("B") || tag.equals("C") || tag.equals("I") || tag.equals("S") || tag.equals("Z")) {
/* 404 */       this.caseI_KI.remove(this.caseI_KI.size() - 1);
/* 405 */     } else if (tag.equals("D")) {
/* 406 */       this.caseD_KD.remove(this.caseD_KD.size() - 1);
/* 407 */     } else if (tag.equals("F")) {
/* 408 */       this.caseF_KF.remove(this.caseF_KF.size() - 1);
/* 409 */     } else if (tag.equals("J")) {
/* 410 */       this.caseJ_KJ.remove(this.caseJ_KJ.size() - 1);
/* 411 */     } else if (tag.equals("C")) {
/* 412 */       this.casec_RS.remove(this.casec_RS.size() - 1);
/* 413 */     } else if (tag.equals("e")) {
/* 414 */       this.caseet_RS.remove(this.caseet_RS.size() - 1);
/* 415 */       this.caseec_RU.remove(this.caseet_RS.size() - 1);
/* 416 */     } else if (tag.equals("s")) {
/* 417 */       this.cases_RU.remove(this.cases_RU.size() - 1);
/* 418 */     } else if (tag.equals("[")) {
/* 419 */       int arraySize = this.casearray_N.remove(this.casearray_N.size() - 1);
/* 420 */       this.numBackwardsCalls -= arraySize;
/* 421 */       for (int k = 0; k < arraySize; k++) {
/* 422 */         removeOnePair();
/*     */       }
/* 424 */     } else if (tag.equals("@")) {
/* 425 */       this.nesttype_RS.remove(this.nesttype_RS.size() - 1);
/* 426 */       int numPairs = this.nestpair_N.remove(this.nestpair_N.size() - 1);
/* 427 */       this.numBackwardsCalls -= numPairs;
/* 428 */       for (int i = 0; i < numPairs; i++)
/* 429 */         removeOnePair(); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\MetadataBandGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */