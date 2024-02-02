/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import org.apache.commons.compress.harmony.unpack200.Segment;
/*     */ import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OperandManager
/*     */ {
/*     */   int[] bcCaseCount;
/*     */   int[] bcCaseValue;
/*     */   int[] bcByte;
/*     */   int[] bcShort;
/*     */   int[] bcLocal;
/*     */   int[] bcLabel;
/*     */   int[] bcIntRef;
/*     */   int[] bcFloatRef;
/*     */   int[] bcLongRef;
/*     */   int[] bcDoubleRef;
/*     */   int[] bcStringRef;
/*     */   int[] bcClassRef;
/*     */   int[] bcFieldRef;
/*     */   int[] bcMethodRef;
/*     */   int[] bcIMethodRef;
/*     */   int[] bcThisField;
/*     */   int[] bcSuperField;
/*     */   int[] bcThisMethod;
/*     */   int[] bcSuperMethod;
/*     */   int[] bcInitRef;
/*     */   int[] wideByteCodes;
/*     */   int bcCaseCountIndex;
/*     */   int bcCaseValueIndex;
/*     */   int bcByteIndex;
/*     */   int bcShortIndex;
/*     */   int bcLocalIndex;
/*     */   int bcLabelIndex;
/*     */   int bcIntRefIndex;
/*     */   int bcFloatRefIndex;
/*     */   int bcLongRefIndex;
/*     */   int bcDoubleRefIndex;
/*     */   int bcStringRefIndex;
/*     */   int bcClassRefIndex;
/*     */   int bcFieldRefIndex;
/*     */   int bcMethodRefIndex;
/*     */   int bcIMethodRefIndex;
/*     */   int bcThisFieldIndex;
/*     */   int bcSuperFieldIndex;
/*     */   int bcThisMethodIndex;
/*     */   int bcSuperMethodIndex;
/*     */   int bcInitRefIndex;
/*     */   int wideByteCodeIndex;
/*     */   Segment segment;
/*     */   String currentClass;
/*     */   String superClass;
/*     */   String newClass;
/*     */   
/*     */   public OperandManager(int[] bcCaseCount, int[] bcCaseValue, int[] bcByte, int[] bcShort, int[] bcLocal, int[] bcLabel, int[] bcIntRef, int[] bcFloatRef, int[] bcLongRef, int[] bcDoubleRef, int[] bcStringRef, int[] bcClassRef, int[] bcFieldRef, int[] bcMethodRef, int[] bcIMethodRef, int[] bcThisField, int[] bcSuperField, int[] bcThisMethod, int[] bcSuperMethod, int[] bcInitRef, int[] wideByteCodes) {
/*  84 */     this.bcCaseCount = bcCaseCount;
/*  85 */     this.bcCaseValue = bcCaseValue;
/*  86 */     this.bcByte = bcByte;
/*  87 */     this.bcShort = bcShort;
/*  88 */     this.bcLocal = bcLocal;
/*  89 */     this.bcLabel = bcLabel;
/*  90 */     this.bcIntRef = bcIntRef;
/*  91 */     this.bcFloatRef = bcFloatRef;
/*  92 */     this.bcLongRef = bcLongRef;
/*  93 */     this.bcDoubleRef = bcDoubleRef;
/*  94 */     this.bcStringRef = bcStringRef;
/*  95 */     this.bcClassRef = bcClassRef;
/*  96 */     this.bcFieldRef = bcFieldRef;
/*  97 */     this.bcMethodRef = bcMethodRef;
/*  98 */     this.bcIMethodRef = bcIMethodRef;
/*     */     
/* 100 */     this.bcThisField = bcThisField;
/* 101 */     this.bcSuperField = bcSuperField;
/* 102 */     this.bcThisMethod = bcThisMethod;
/* 103 */     this.bcSuperMethod = bcSuperMethod;
/* 104 */     this.bcInitRef = bcInitRef;
/* 105 */     this.wideByteCodes = wideByteCodes;
/*     */   }
/*     */   
/*     */   public int nextCaseCount() {
/* 109 */     return this.bcCaseCount[this.bcCaseCountIndex++];
/*     */   }
/*     */   
/*     */   public int nextCaseValues() {
/* 113 */     return this.bcCaseValue[this.bcCaseValueIndex++];
/*     */   }
/*     */   
/*     */   public int nextByte() {
/* 117 */     return this.bcByte[this.bcByteIndex++];
/*     */   }
/*     */   
/*     */   public int nextShort() {
/* 121 */     return this.bcShort[this.bcShortIndex++];
/*     */   }
/*     */   
/*     */   public int nextLocal() {
/* 125 */     return this.bcLocal[this.bcLocalIndex++];
/*     */   }
/*     */   
/*     */   public int nextLabel() {
/* 129 */     return this.bcLabel[this.bcLabelIndex++];
/*     */   }
/*     */   
/*     */   public int nextIntRef() {
/* 133 */     return this.bcIntRef[this.bcIntRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextFloatRef() {
/* 137 */     return this.bcFloatRef[this.bcFloatRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextLongRef() {
/* 141 */     return this.bcLongRef[this.bcLongRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextDoubleRef() {
/* 145 */     return this.bcDoubleRef[this.bcDoubleRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextStringRef() {
/* 149 */     return this.bcStringRef[this.bcStringRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextClassRef() {
/* 153 */     return this.bcClassRef[this.bcClassRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextFieldRef() {
/* 157 */     return this.bcFieldRef[this.bcFieldRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextMethodRef() {
/* 161 */     return this.bcMethodRef[this.bcMethodRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextIMethodRef() {
/* 165 */     return this.bcIMethodRef[this.bcIMethodRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextThisFieldRef() {
/* 169 */     return this.bcThisField[this.bcThisFieldIndex++];
/*     */   }
/*     */   
/*     */   public int nextSuperFieldRef() {
/* 173 */     return this.bcSuperField[this.bcSuperFieldIndex++];
/*     */   }
/*     */   
/*     */   public int nextThisMethodRef() {
/* 177 */     return this.bcThisMethod[this.bcThisMethodIndex++];
/*     */   }
/*     */   
/*     */   public int nextSuperMethodRef() {
/* 181 */     return this.bcSuperMethod[this.bcSuperMethodIndex++];
/*     */   }
/*     */   
/*     */   public int nextInitRef() {
/* 185 */     return this.bcInitRef[this.bcInitRefIndex++];
/*     */   }
/*     */   
/*     */   public int nextWideByteCode() {
/* 189 */     return this.wideByteCodes[this.wideByteCodeIndex++];
/*     */   }
/*     */   
/*     */   public void setSegment(Segment segment) {
/* 193 */     this.segment = segment;
/*     */   }
/*     */   
/*     */   public SegmentConstantPool globalConstantPool() {
/* 197 */     return this.segment.getConstantPool();
/*     */   }
/*     */   
/*     */   public void setCurrentClass(String string) {
/* 201 */     this.currentClass = string;
/*     */   }
/*     */   
/*     */   public void setSuperClass(String string) {
/* 205 */     this.superClass = string;
/*     */   }
/*     */   
/*     */   public void setNewClass(String string) {
/* 209 */     this.newClass = string;
/*     */   }
/*     */   
/*     */   public String getCurrentClass() {
/* 213 */     if (null == this.currentClass) {
/* 214 */       throw new Error("Current class not set yet");
/*     */     }
/* 216 */     return this.currentClass;
/*     */   }
/*     */   
/*     */   public String getSuperClass() {
/* 220 */     if (null == this.superClass) {
/* 221 */       throw new Error("SuperClass not set yet");
/*     */     }
/* 223 */     return this.superClass;
/*     */   }
/*     */   
/*     */   public String getNewClass() {
/* 227 */     if (null == this.newClass) {
/* 228 */       throw new Error("New class not set yet");
/*     */     }
/* 230 */     return this.newClass;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\OperandManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */