/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*  34 */   private final Set innerClasses = new TreeSet();
/*     */   private final CpBands cpBands;
/*  36 */   private int bit16Count = 0;
/*     */   
/*  38 */   private final Map outerToInner = new HashMap<>();
/*     */   
/*     */   public IcBands(SegmentHeader segmentHeader, CpBands cpBands, int effort) {
/*  41 */     super(effort, segmentHeader);
/*  42 */     this.cpBands = cpBands;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finaliseBands() {
/*  50 */     this.segmentHeader.setIc_count(this.innerClasses.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/*  55 */     PackingUtils.log("Writing internal class bands...");
/*  56 */     int[] ic_this_class = new int[this.innerClasses.size()];
/*  57 */     int[] ic_flags = new int[this.innerClasses.size()];
/*  58 */     int[] ic_outer_class = new int[this.bit16Count];
/*  59 */     int[] ic_name = new int[this.bit16Count];
/*     */     
/*  61 */     int index2 = 0;
/*  62 */     List<IcTuple> innerClassesList = new ArrayList(this.innerClasses);
/*  63 */     for (int i = 0; i < ic_this_class.length; i++) {
/*  64 */       IcTuple icTuple = innerClassesList.get(i);
/*  65 */       ic_this_class[i] = icTuple.C.getIndex();
/*  66 */       ic_flags[i] = icTuple.F;
/*  67 */       if ((icTuple.F & 0x10000) != 0) {
/*  68 */         ic_outer_class[index2] = (icTuple.C2 == null) ? 0 : (icTuple.C2.getIndex() + 1);
/*  69 */         ic_name[index2] = (icTuple.N == null) ? 0 : (icTuple.N.getIndex() + 1);
/*  70 */         index2++;
/*     */       } 
/*     */     } 
/*  73 */     byte[] encodedBand = encodeBandInt("ic_this_class", ic_this_class, Codec.UDELTA5);
/*  74 */     out.write(encodedBand);
/*  75 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_this_class[" + ic_this_class.length + "]");
/*     */     
/*  77 */     encodedBand = encodeBandInt("ic_flags", ic_flags, Codec.UNSIGNED5);
/*  78 */     out.write(encodedBand);
/*  79 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_flags[" + ic_flags.length + "]");
/*     */     
/*  81 */     encodedBand = encodeBandInt("ic_outer_class", ic_outer_class, Codec.DELTA5);
/*  82 */     out.write(encodedBand);
/*  83 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_outer_class[" + ic_outer_class.length + "]");
/*     */     
/*  85 */     encodedBand = encodeBandInt("ic_name", ic_name, Codec.DELTA5);
/*  86 */     out.write(encodedBand);
/*  87 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_name[" + ic_name.length + "]");
/*     */   }
/*     */   
/*     */   public void addInnerClass(String name, String outerName, String innerName, int flags) {
/*  91 */     if (outerName != null || innerName != null) {
/*  92 */       if (namesArePredictable(name, outerName, innerName)) {
/*  93 */         IcTuple innerClass = new IcTuple(this.cpBands.getCPClass(name), flags, null, null);
/*  94 */         addToMap(outerName, innerClass);
/*  95 */         this.innerClasses.add(innerClass);
/*     */       } else {
/*  97 */         flags |= 0x10000;
/*     */         
/*  99 */         IcTuple icTuple = new IcTuple(this.cpBands.getCPClass(name), flags, this.cpBands.getCPClass(outerName), this.cpBands.getCPUtf8(innerName));
/* 100 */         boolean added = this.innerClasses.add(icTuple);
/* 101 */         if (added) {
/* 102 */           this.bit16Count++;
/* 103 */           addToMap(outerName, icTuple);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 107 */       IcTuple innerClass = new IcTuple(this.cpBands.getCPClass(name), flags, null, null);
/* 108 */       addToMap(getOuter(name), innerClass);
/* 109 */       this.innerClasses.add(innerClass);
/*     */     } 
/*     */   }
/*     */   
/*     */   public List getInnerClassesForOuter(String outerClassName) {
/* 114 */     return (List)this.outerToInner.get(outerClassName);
/*     */   }
/*     */   
/*     */   private String getOuter(String name) {
/* 118 */     return name.substring(0, name.lastIndexOf('$'));
/*     */   }
/*     */   
/*     */   private void addToMap(String outerName, IcTuple icTuple) {
/* 122 */     List<IcTuple> tuples = (List)this.outerToInner.get(outerName);
/* 123 */     if (tuples == null) {
/* 124 */       tuples = new ArrayList();
/* 125 */       this.outerToInner.put(outerName, tuples);
/* 126 */       tuples.add(icTuple);
/*     */     } else {
/* 128 */       for (Iterator<IcTuple> iterator = tuples.iterator(); iterator.hasNext(); ) {
/* 129 */         IcTuple icT = iterator.next();
/* 130 */         if (icTuple.equals(icT)) {
/*     */           return;
/*     */         }
/*     */       } 
/* 134 */       tuples.add(icTuple);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean namesArePredictable(String name, String outerName, String innerName) {
/* 140 */     return (name.equals(outerName + '$' + innerName) && innerName.indexOf('$') == -1);
/*     */   }
/*     */   
/*     */   class IcTuple
/*     */     implements Comparable {
/*     */     protected CPClass C;
/*     */     protected int F;
/*     */     protected CPClass C2;
/*     */     protected CPUTF8 N;
/*     */     
/*     */     public IcTuple(CPClass C, int F, CPClass C2, CPUTF8 N) {
/* 151 */       this.C = C;
/* 152 */       this.F = F;
/* 153 */       this.C2 = C2;
/* 154 */       this.N = N;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 159 */       if (o instanceof IcTuple) {
/* 160 */         IcTuple icT = (IcTuple)o;
/* 161 */         return (this.C.equals(icT.C) && this.F == icT.F && ((this.C2 != null) ? this.C2.equals(icT.C2) : (icT.C2 == null)) && ((this.N != null) ? this.N
/* 162 */           .equals(icT.N) : (icT.N == null)));
/*     */       } 
/* 164 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 169 */       return this.C.toString();
/*     */     }
/*     */ 
/*     */     
/*     */     public int compareTo(Object arg0) {
/* 174 */       return this.C.compareTo(((IcTuple)arg0).C);
/*     */     }
/*     */     
/*     */     public boolean isAnonymous() {
/* 178 */       String className = this.C.toString();
/* 179 */       String innerName = className.substring(className.lastIndexOf('$') + 1);
/* 180 */       return Character.isDigit(innerName.charAt(0));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public IcTuple getIcTuple(CPClass inner) {
/* 186 */     for (Iterator<IcTuple> iterator = this.innerClasses.iterator(); iterator.hasNext(); ) {
/* 187 */       IcTuple icTuple = iterator.next();
/* 188 */       if (icTuple.C.equals(inner)) {
/* 189 */         return icTuple;
/*     */       }
/*     */     } 
/* 192 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\IcBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */