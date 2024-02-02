/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.AnnotationDefaultAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.AnnotationsAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.RuntimeVisibleorInvisibleAnnotationsAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.RuntimeVisibleorInvisibleParameterAnnotationsAttribute;
/*     */ 
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
/*     */ {
/*     */   private final String type;
/*     */   private final CpBands cpBands;
/*     */   private static CPUTF8 rvaUTF8;
/*     */   private static CPUTF8 riaUTF8;
/*     */   private static CPUTF8 rvpaUTF8;
/*     */   private static CPUTF8 ripaUTF8;
/*     */   private List attributes;
/*     */   public int[] param_NB;
/*     */   public int[] anno_N;
/*     */   public CPUTF8[][] type_RS;
/*     */   public int[][] pair_N;
/*     */   public CPUTF8[] name_RU;
/*     */   public int[] T;
/*     */   public CPInteger[] caseI_KI;
/*     */   public CPDouble[] caseD_KD;
/*     */   public CPFloat[] caseF_KF;
/*     */   public CPLong[] caseJ_KJ;
/*     */   public CPUTF8[] casec_RS;
/*     */   public String[] caseet_RS;
/*     */   public String[] caseec_RU;
/*     */   
/*     */   public static void setRvaAttributeName(CPUTF8 cpUTF8Value) {
/*  51 */     rvaUTF8 = cpUTF8Value;
/*     */   }
/*     */   public CPUTF8[] cases_RU; public int[] casearray_N; public CPUTF8[] nesttype_RS; public int[] nestpair_N; public CPUTF8[] nestname_RU; private int caseI_KI_Index; private int caseD_KD_Index; private int caseF_KF_Index; private int caseJ_KJ_Index; private int casec_RS_Index; private int caseet_RS_Index; private int caseec_RU_Index; private int cases_RU_Index; private int casearray_N_Index; private int T_index; private int nesttype_RS_Index; private int nestpair_N_Index; private Iterator nestname_RU_Iterator; private int anno_N_Index; private int pair_N_Index;
/*     */   public static void setRiaAttributeName(CPUTF8 cpUTF8Value) {
/*  55 */     riaUTF8 = cpUTF8Value;
/*     */   }
/*     */   
/*     */   public static void setRvpaAttributeName(CPUTF8 cpUTF8Value) {
/*  59 */     rvpaUTF8 = cpUTF8Value;
/*     */   }
/*     */   
/*     */   public static void setRipaAttributeName(CPUTF8 cpUTF8Value) {
/*  63 */     ripaUTF8 = cpUTF8Value;
/*     */   }
/*     */   
/*     */   public MetadataBandGroup(String type, CpBands cpBands) {
/*  67 */     this.type = type;
/*  68 */     this.cpBands = cpBands;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getAttributes() {
/* 124 */     if (this.attributes == null) {
/* 125 */       this.attributes = new ArrayList();
/* 126 */       if (this.name_RU != null) {
/* 127 */         Iterator name_RU_Iterator = Arrays.<CPUTF8>asList(this.name_RU).iterator();
/* 128 */         if (!this.type.equals("AD")) {
/* 129 */           this.T_index = 0;
/*     */         }
/* 131 */         this.caseI_KI_Index = 0;
/* 132 */         this.caseD_KD_Index = 0;
/* 133 */         this.caseF_KF_Index = 0;
/* 134 */         this.caseJ_KJ_Index = 0;
/* 135 */         this.casec_RS_Index = 0;
/* 136 */         this.caseet_RS_Index = 0;
/* 137 */         this.caseec_RU_Index = 0;
/* 138 */         this.cases_RU_Index = 0;
/* 139 */         this.casearray_N_Index = 0;
/* 140 */         this.nesttype_RS_Index = 0;
/* 141 */         this.nestpair_N_Index = 0;
/* 142 */         this.nestname_RU_Iterator = Arrays.<CPUTF8>asList(this.nestname_RU).iterator();
/* 143 */         if (this.type.equals("RVA") || this.type.equals("RIA")) {
/* 144 */           for (int i = 0; i < this.anno_N.length; i++) {
/* 145 */             this.attributes.add(getAttribute(this.anno_N[i], this.type_RS[i], this.pair_N[i], name_RU_Iterator));
/*     */           }
/* 147 */         } else if (this.type.equals("RVPA") || this.type.equals("RIPA")) {
/* 148 */           this.anno_N_Index = 0;
/* 149 */           this.pair_N_Index = 0;
/* 150 */           for (int i = 0; i < this.param_NB.length; i++) {
/* 151 */             this.attributes.add(getParameterAttribute(this.param_NB[i], name_RU_Iterator));
/*     */           }
/*     */         } 
/* 154 */       } else if (this.type.equals("AD")) {
/* 155 */         for (int i = 0; i < this.T.length; i++) {
/* 156 */           this.attributes.add(new AnnotationDefaultAttribute(new AnnotationsAttribute.ElementValue(this.T[i], getNextValue(this.T[i]))));
/*     */         }
/*     */       } 
/*     */     } 
/* 160 */     return this.attributes;
/*     */   }
/*     */ 
/*     */   
/*     */   private Attribute getAttribute(int numAnnotations, CPUTF8[] types, int[] pairCounts, Iterator namesIterator) {
/* 165 */     AnnotationsAttribute.Annotation[] annotations = new AnnotationsAttribute.Annotation[numAnnotations];
/* 166 */     for (int i = 0; i < numAnnotations; i++) {
/* 167 */       annotations[i] = getAnnotation(types[i], pairCounts[i], namesIterator);
/*     */     }
/* 169 */     return (Attribute)new RuntimeVisibleorInvisibleAnnotationsAttribute(this.type.equals("RVA") ? rvaUTF8 : riaUTF8, annotations);
/*     */   }
/*     */   
/*     */   private Attribute getParameterAttribute(int numParameters, Iterator namesIterator) {
/* 173 */     RuntimeVisibleorInvisibleParameterAnnotationsAttribute.ParameterAnnotation[] parameter_annotations = new RuntimeVisibleorInvisibleParameterAnnotationsAttribute.ParameterAnnotation[numParameters];
/* 174 */     for (int i = 0; i < numParameters; i++) {
/* 175 */       int numAnnotations = this.anno_N[this.anno_N_Index++];
/* 176 */       int[] pairCounts = this.pair_N[this.pair_N_Index++];
/* 177 */       AnnotationsAttribute.Annotation[] annotations = new AnnotationsAttribute.Annotation[numAnnotations];
/* 178 */       for (int j = 0; j < annotations.length; j++) {
/* 179 */         annotations[j] = getAnnotation(this.type_RS[this.anno_N_Index - 1][j], pairCounts[j], namesIterator);
/*     */       }
/* 181 */       parameter_annotations[i] = new RuntimeVisibleorInvisibleParameterAnnotationsAttribute.ParameterAnnotation(annotations);
/*     */     } 
/* 183 */     return (Attribute)new RuntimeVisibleorInvisibleParameterAnnotationsAttribute(this.type.equals("RVPA") ? rvpaUTF8 : ripaUTF8, parameter_annotations);
/*     */   }
/*     */   
/*     */   private AnnotationsAttribute.Annotation getAnnotation(CPUTF8 type, int pairCount, Iterator<CPUTF8> namesIterator)
/*     */   {
/* 188 */     CPUTF8[] elementNames = new CPUTF8[pairCount];
/* 189 */     AnnotationsAttribute.ElementValue[] elementValues = new AnnotationsAttribute.ElementValue[pairCount];
/* 190 */     for (int j = 0; j < elementNames.length; j++) {
/* 191 */       elementNames[j] = namesIterator.next();
/* 192 */       int t = this.T[this.T_index++];
/* 193 */       elementValues[j] = new AnnotationsAttribute.ElementValue(t, getNextValue(t));
/*     */     } 
/* 195 */     return new AnnotationsAttribute.Annotation(pairCount, type, elementNames, elementValues); } private Object getNextValue(int t) { String enumString; int arraySize; AnnotationsAttribute.ElementValue[] nestedArray;
/*     */     int i;
/*     */     CPUTF8 type;
/*     */     int numPairs;
/* 199 */     switch (t) {
/*     */       case 66:
/*     */       case 67:
/*     */       case 73:
/*     */       case 83:
/*     */       case 90:
/* 205 */         return this.caseI_KI[this.caseI_KI_Index++];
/*     */       case 68:
/* 207 */         return this.caseD_KD[this.caseD_KD_Index++];
/*     */       case 70:
/* 209 */         return this.caseF_KF[this.caseF_KF_Index++];
/*     */       case 74:
/* 211 */         return this.caseJ_KJ[this.caseJ_KJ_Index++];
/*     */       case 99:
/* 213 */         return this.casec_RS[this.casec_RS_Index++];
/*     */ 
/*     */       
/*     */       case 101:
/* 217 */         enumString = this.caseet_RS[this.caseet_RS_Index++] + ":" + this.caseec_RU[this.caseec_RU_Index++];
/* 218 */         return this.cpBands.cpNameAndTypeValue(enumString);
/*     */       case 115:
/* 220 */         return this.cases_RU[this.cases_RU_Index++];
/*     */       case 91:
/* 222 */         arraySize = this.casearray_N[this.casearray_N_Index++];
/* 223 */         nestedArray = new AnnotationsAttribute.ElementValue[arraySize];
/* 224 */         for (i = 0; i < arraySize; i++) {
/* 225 */           int nextT = this.T[this.T_index++];
/* 226 */           nestedArray[i] = new AnnotationsAttribute.ElementValue(nextT, getNextValue(nextT));
/*     */         } 
/* 228 */         return nestedArray;
/*     */       case 64:
/* 230 */         type = this.nesttype_RS[this.nesttype_RS_Index++];
/* 231 */         numPairs = this.nestpair_N[this.nestpair_N_Index++];
/*     */         
/* 233 */         return getAnnotation(type, numPairs, this.nestname_RU_Iterator);
/*     */     } 
/* 235 */     return null; }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\MetadataBandGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */