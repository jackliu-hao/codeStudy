/*     */ package org.h2.util.json;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JSONValidationTargetWithUniqueKeys
/*     */   extends JSONValidationTarget
/*     */ {
/*  33 */   private final ArrayDeque<Object> stack = new ArrayDeque();
/*  34 */   private final ArrayDeque<String> names = new ArrayDeque<>();
/*     */   
/*     */   private boolean needSeparator;
/*     */   
/*     */   public void startObject() {
/*  39 */     beforeValue();
/*  40 */     this.names.push((this.memberName != null) ? this.memberName : "");
/*  41 */     this.memberName = null;
/*  42 */     this.stack.push(new HashSet());
/*     */   }
/*     */   private String memberName; private JSONItemType type;
/*     */   
/*     */   public void endObject() {
/*  47 */     if (this.memberName != null) {
/*  48 */       throw new IllegalStateException();
/*     */     }
/*  50 */     if (!(this.stack.poll() instanceof HashSet)) {
/*  51 */       throw new IllegalStateException();
/*     */     }
/*  53 */     this.memberName = this.names.pop();
/*  54 */     afterValue(JSONItemType.OBJECT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void startArray() {
/*  59 */     beforeValue();
/*  60 */     this.names.push((this.memberName != null) ? this.memberName : "");
/*  61 */     this.memberName = null;
/*  62 */     this.stack.push(Collections.emptyList());
/*     */   }
/*     */ 
/*     */   
/*     */   public void endArray() {
/*  67 */     if (!(this.stack.poll() instanceof java.util.List)) {
/*  68 */       throw new IllegalStateException();
/*     */     }
/*  70 */     this.memberName = this.names.pop();
/*  71 */     afterValue(JSONItemType.ARRAY);
/*     */   }
/*     */ 
/*     */   
/*     */   public void member(String paramString) {
/*  76 */     if (this.memberName != null || !(this.stack.peek() instanceof HashSet)) {
/*  77 */       throw new IllegalStateException();
/*     */     }
/*  79 */     this.memberName = paramString;
/*  80 */     beforeValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNull() {
/*  85 */     beforeValue();
/*  86 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueFalse() {
/*  91 */     beforeValue();
/*  92 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueTrue() {
/*  97 */     beforeValue();
/*  98 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNumber(BigDecimal paramBigDecimal) {
/* 103 */     beforeValue();
/* 104 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueString(String paramString) {
/* 109 */     beforeValue();
/* 110 */     afterValue(JSONItemType.SCALAR);
/*     */   }
/*     */   
/*     */   private void beforeValue() {
/* 114 */     if (this.memberName == null && this.stack.peek() instanceof HashSet) {
/* 115 */       throw new IllegalStateException();
/*     */     }
/* 117 */     if (this.needSeparator) {
/* 118 */       if (this.stack.isEmpty()) {
/* 119 */         throw new IllegalStateException();
/*     */       }
/* 121 */       this.needSeparator = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void afterValue(JSONItemType paramJSONItemType) {
/* 127 */     Object object = this.stack.peek();
/* 128 */     if (object == null) {
/* 129 */       this.type = paramJSONItemType;
/* 130 */     } else if (object instanceof HashSet && 
/* 131 */       !((HashSet<String>)object).add(this.memberName)) {
/* 132 */       throw new IllegalStateException();
/*     */     } 
/*     */     
/* 135 */     this.needSeparator = true;
/* 136 */     this.memberName = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPropertyExpected() {
/* 141 */     return (this.memberName == null && this.stack.peek() instanceof HashSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValueSeparatorExpected() {
/* 146 */     return this.needSeparator;
/*     */   }
/*     */ 
/*     */   
/*     */   public JSONItemType getResult() {
/* 151 */     if (!this.stack.isEmpty() || this.type == null) {
/* 152 */       throw new IllegalStateException();
/*     */     }
/* 154 */     return this.type;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONValidationTargetWithUniqueKeys.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */