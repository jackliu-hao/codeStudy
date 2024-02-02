/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.compress.harmony.pack200.Codec;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantPoolEntry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IcBands
/*     */   extends BandSet
/*     */ {
/*     */   private IcTuple[] icAll;
/*     */   private final String[] cpUTF8;
/*     */   private final String[] cpClass;
/*     */   private Map thisClassToTuple;
/*     */   private Map outerClassToTuples;
/*     */   
/*     */   public IcBands(Segment segment) {
/*  53 */     super(segment);
/*  54 */     this.cpClass = segment.getCpBands().getCpClass();
/*  55 */     this.cpUTF8 = segment.getCpBands().getCpUTF8();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(InputStream in) throws IOException, Pack200Exception {
/*  66 */     int innerClassCount = this.header.getInnerClassCount();
/*  67 */     int[] icThisClassInts = decodeBandInt("ic_this_class", in, Codec.UDELTA5, innerClassCount);
/*  68 */     String[] icThisClass = getReferences(icThisClassInts, this.cpClass);
/*  69 */     int[] icFlags = decodeBandInt("ic_flags", in, Codec.UNSIGNED5, innerClassCount);
/*  70 */     int outerClasses = SegmentUtils.countBit16(icFlags);
/*  71 */     int[] icOuterClassInts = decodeBandInt("ic_outer_class", in, Codec.DELTA5, outerClasses);
/*  72 */     String[] icOuterClass = new String[outerClasses];
/*  73 */     for (int i = 0; i < icOuterClass.length; i++) {
/*  74 */       if (icOuterClassInts[i] == 0) {
/*  75 */         icOuterClass[i] = null;
/*     */       } else {
/*  77 */         icOuterClass[i] = this.cpClass[icOuterClassInts[i] - 1];
/*     */       } 
/*     */     } 
/*  80 */     int[] icNameInts = decodeBandInt("ic_name", in, Codec.DELTA5, outerClasses);
/*  81 */     String[] icName = new String[outerClasses];
/*  82 */     for (int j = 0; j < icName.length; j++) {
/*  83 */       if (icNameInts[j] == 0) {
/*  84 */         icName[j] = null;
/*     */       } else {
/*  86 */         icName[j] = this.cpUTF8[icNameInts[j] - 1];
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  91 */     this.icAll = new IcTuple[icThisClass.length];
/*  92 */     int index = 0;
/*  93 */     for (int k = 0; k < icThisClass.length; k++) {
/*  94 */       String icTupleC = icThisClass[k];
/*  95 */       int icTupleF = icFlags[k];
/*  96 */       String icTupleC2 = null;
/*  97 */       String icTupleN = null;
/*  98 */       int cIndex = icThisClassInts[k];
/*  99 */       int c2Index = -1;
/* 100 */       int nIndex = -1;
/* 101 */       if ((icFlags[k] & 0x10000) != 0) {
/* 102 */         icTupleC2 = icOuterClass[index];
/* 103 */         icTupleN = icName[index];
/* 104 */         c2Index = icOuterClassInts[index] - 1;
/* 105 */         nIndex = icNameInts[index] - 1;
/* 106 */         index++;
/*     */       } 
/* 108 */       this.icAll[k] = new IcTuple(icTupleC, icTupleF, icTupleC2, icTupleN, cIndex, c2Index, nIndex, k);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void unpack() throws IOException, Pack200Exception {
/* 114 */     IcTuple[] allTuples = getIcTuples();
/* 115 */     this.thisClassToTuple = new HashMap<>(allTuples.length);
/* 116 */     this.outerClassToTuples = new HashMap<>(allTuples.length);
/* 117 */     for (int index = 0; index < allTuples.length; index++) {
/*     */       
/* 119 */       IcTuple tuple = allTuples[index];
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 124 */       Object result = this.thisClassToTuple.put(tuple.thisClassString(), tuple);
/* 125 */       if (result != null) {
/* 126 */         throw new Error("Collision detected in <thisClassString, IcTuple> mapping. There are at least two inner clases with the same name.");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 134 */       if ((!tuple.isAnonymous() && !tuple.outerIsAnonymous()) || tuple.nestedExplicitFlagSet()) {
/*     */ 
/*     */         
/* 137 */         String key = tuple.outerClassString();
/* 138 */         List<IcTuple> bucket = (List)this.outerClassToTuples.get(key);
/* 139 */         if (bucket == null) {
/* 140 */           bucket = new ArrayList();
/* 141 */           this.outerClassToTuples.put(key, bucket);
/*     */         } 
/* 143 */         bucket.add(tuple);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public IcTuple[] getIcTuples() {
/* 149 */     return this.icAll;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IcTuple[] getRelevantIcTuples(String className, ClassConstantPool cp) {
/* 160 */     Set<IcTuple> relevantTuplesContains = new HashSet();
/* 161 */     List<IcTuple> relevantTuples = new ArrayList();
/*     */     
/* 163 */     List<IcTuple> relevantCandidates = (List)this.outerClassToTuples.get(className);
/* 164 */     if (relevantCandidates != null) {
/* 165 */       for (int index = 0; index < relevantCandidates.size(); index++) {
/* 166 */         IcTuple tuple = relevantCandidates.get(index);
/* 167 */         relevantTuplesContains.add(tuple);
/* 168 */         relevantTuples.add(tuple);
/*     */       } 
/*     */     }
/*     */     
/* 172 */     List<ConstantPoolEntry> entries = cp.entries();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 178 */     for (int eIndex = 0; eIndex < entries.size(); eIndex++) {
/* 179 */       ConstantPoolEntry entry = entries.get(eIndex);
/* 180 */       if (entry instanceof CPClass) {
/* 181 */         CPClass clazz = (CPClass)entry;
/* 182 */         IcTuple relevant = (IcTuple)this.thisClassToTuple.get(clazz.name);
/* 183 */         if (relevant != null && relevantTuplesContains.add(relevant)) {
/* 184 */           relevantTuples.add(relevant);
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
/* 195 */     ArrayList<IcTuple> tuplesToScan = new ArrayList<>(relevantTuples);
/* 196 */     ArrayList<IcTuple> tuplesToAdd = new ArrayList();
/*     */     
/* 198 */     while (tuplesToScan.size() > 0) {
/*     */       
/* 200 */       tuplesToAdd.clear(); int index;
/* 201 */       for (index = 0; index < tuplesToScan.size(); index++) {
/* 202 */         IcTuple aRelevantTuple = tuplesToScan.get(index);
/* 203 */         IcTuple relevant = (IcTuple)this.thisClassToTuple.get(aRelevantTuple.outerClassString());
/* 204 */         if (relevant != null && !aRelevantTuple.outerIsAnonymous()) {
/* 205 */           tuplesToAdd.add(relevant);
/*     */         }
/*     */       } 
/*     */       
/* 209 */       tuplesToScan.clear();
/* 210 */       for (index = 0; index < tuplesToAdd.size(); index++) {
/* 211 */         IcTuple tuple = tuplesToAdd.get(index);
/* 212 */         if (relevantTuplesContains.add(tuple)) {
/* 213 */           relevantTuples.add(tuple);
/* 214 */           tuplesToScan.add(tuple);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 223 */     Collections.sort(relevantTuples, (arg0, arg1) -> {
/*     */           Integer index1 = Integer.valueOf(((IcTuple)arg0).getTupleIndex());
/*     */           
/*     */           Integer index2 = Integer.valueOf(((IcTuple)arg1).getTupleIndex());
/*     */           return index1.compareTo(index2);
/*     */         });
/* 229 */     IcTuple[] relevantTuplesArray = new IcTuple[relevantTuples.size()];
/* 230 */     for (int i = 0; i < relevantTuplesArray.length; i++) {
/* 231 */       relevantTuplesArray[i] = relevantTuples.get(i);
/*     */     }
/*     */     
/* 234 */     return relevantTuplesArray;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\IcBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */