/*     */ package org.h2.util.json;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import org.h2.util.ByteStack;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JSONValidationTargetWithoutUniqueKeys
/*     */   extends JSONValidationTarget
/*     */ {
/*     */   private static final byte OBJECT = 1;
/*     */   private static final byte ARRAY = 2;
/*     */   private JSONItemType type;
/*  33 */   private final ByteStack stack = new ByteStack();
/*     */   private boolean needSeparator;
/*     */   private boolean afterName;
/*     */   
/*     */   public void startObject() {
/*  38 */     beforeValue();
/*  39 */     this.afterName = false;
/*  40 */     this.stack.push((byte)1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void endObject() {
/*  45 */     if (this.afterName || this.stack.poll(-1) != 1) {
/*  46 */       throw new IllegalStateException();
/*     */     }
/*  48 */     afterValue(JSONItemType.OBJECT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void startArray() {
/*  53 */     beforeValue();
/*  54 */     this.afterName = false;
/*  55 */     this.stack.push((byte)2);
/*     */   }
/*     */ 
/*     */   
/*     */   public void endArray() {
/*  60 */     if (this.stack.poll(-1) != 2) {
/*  61 */       throw new IllegalStateException();
/*     */     }
/*  63 */     afterValue(JSONItemType.ARRAY);
/*     */   }
/*     */ 
/*     */   
/*     */   public void member(String paramString) {
/*  68 */     if (this.afterName || this.stack.peek(-1) != 1) {
/*  69 */       throw new IllegalStateException();
/*     */     }
/*  71 */     this.afterName = true;
/*  72 */     beforeValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNull() {
/*  77 */     beforeValue();
/*  78 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueFalse() {
/*  83 */     beforeValue();
/*  84 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueTrue() {
/*  89 */     beforeValue();
/*  90 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNumber(BigDecimal paramBigDecimal) {
/*  95 */     beforeValue();
/*  96 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueString(String paramString) {
/* 101 */     beforeValue();
/* 102 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */   
/*     */   private void beforeValue() {
/* 106 */     if (!this.afterName && this.stack.peek(-1) == 1) {
/* 107 */       throw new IllegalStateException();
/*     */     }
/* 109 */     if (this.needSeparator) {
/* 110 */       if (this.stack.isEmpty()) {
/* 111 */         throw new IllegalStateException();
/*     */       }
/* 113 */       this.needSeparator = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void afterValue(JSONItemType paramJSONItemType) {
/* 118 */     this.needSeparator = true;
/* 119 */     this.afterName = false;
/* 120 */     if (this.stack.isEmpty()) {
/* 121 */       this.type = paramJSONItemType;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPropertyExpected() {
/* 127 */     return (!this.afterName && this.stack.peek(-1) == 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValueSeparatorExpected() {
/* 132 */     return this.needSeparator;
/*     */   }
/*     */ 
/*     */   
/*     */   public JSONItemType getResult() {
/* 137 */     if (!this.stack.isEmpty() || this.type == null) {
/* 138 */       throw new IllegalStateException();
/*     */     }
/* 140 */     return this.type;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONValidationTargetWithoutUniqueKeys.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */