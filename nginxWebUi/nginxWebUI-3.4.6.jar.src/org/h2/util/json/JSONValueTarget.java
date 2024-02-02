/*     */ package org.h2.util.json;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayDeque;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JSONValueTarget
/*     */   extends JSONTarget<JSONValue>
/*     */ {
/*  30 */   private final ArrayDeque<JSONValue> stack = new ArrayDeque<>();
/*  31 */   private final ArrayDeque<String> names = new ArrayDeque<>();
/*     */   
/*     */   private boolean needSeparator;
/*     */   
/*     */   public void startObject() {
/*  36 */     beforeValue();
/*  37 */     this.names.push((this.memberName != null) ? this.memberName : "");
/*  38 */     this.memberName = null;
/*  39 */     this.stack.push(new JSONObject());
/*     */   }
/*     */   private String memberName; private JSONValue result;
/*     */   
/*     */   public void endObject() {
/*  44 */     if (this.memberName != null) {
/*  45 */       throw new IllegalStateException();
/*     */     }
/*  47 */     JSONValue jSONValue = this.stack.poll();
/*  48 */     if (!(jSONValue instanceof JSONObject)) {
/*  49 */       throw new IllegalStateException();
/*     */     }
/*  51 */     this.memberName = this.names.pop();
/*  52 */     afterValue(jSONValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public void startArray() {
/*  57 */     beforeValue();
/*  58 */     this.names.push((this.memberName != null) ? this.memberName : "");
/*  59 */     this.memberName = null;
/*  60 */     this.stack.push(new JSONArray());
/*     */   }
/*     */ 
/*     */   
/*     */   public void endArray() {
/*  65 */     JSONValue jSONValue = this.stack.poll();
/*  66 */     if (!(jSONValue instanceof JSONArray)) {
/*  67 */       throw new IllegalStateException();
/*     */     }
/*  69 */     this.memberName = this.names.pop();
/*  70 */     afterValue(jSONValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public void member(String paramString) {
/*  75 */     if (this.memberName != null || !(this.stack.peek() instanceof JSONObject)) {
/*  76 */       throw new IllegalStateException();
/*     */     }
/*  78 */     this.memberName = paramString;
/*  79 */     beforeValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNull() {
/*  84 */     beforeValue();
/*  85 */     afterValue(JSONNull.NULL);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueFalse() {
/*  90 */     beforeValue();
/*  91 */     afterValue(JSONBoolean.FALSE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueTrue() {
/*  96 */     beforeValue();
/*  97 */     afterValue(JSONBoolean.TRUE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueNumber(BigDecimal paramBigDecimal) {
/* 102 */     beforeValue();
/* 103 */     afterValue(new JSONNumber(paramBigDecimal));
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueString(String paramString) {
/* 108 */     beforeValue();
/* 109 */     afterValue(new JSONString(paramString));
/*     */   }
/*     */   
/*     */   private void beforeValue() {
/* 113 */     if (this.memberName == null && this.stack.peek() instanceof JSONObject) {
/* 114 */       throw new IllegalStateException();
/*     */     }
/* 116 */     if (this.needSeparator) {
/* 117 */       if (this.stack.isEmpty()) {
/* 118 */         throw new IllegalStateException();
/*     */       }
/* 120 */       this.needSeparator = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void afterValue(JSONValue paramJSONValue) {
/* 125 */     JSONValue jSONValue = this.stack.peek();
/* 126 */     if (jSONValue == null) {
/* 127 */       this.result = paramJSONValue;
/* 128 */     } else if (jSONValue instanceof JSONObject) {
/* 129 */       ((JSONObject)jSONValue).addMember(this.memberName, paramJSONValue);
/*     */     } else {
/* 131 */       ((JSONArray)jSONValue).addElement(paramJSONValue);
/*     */     } 
/* 133 */     this.needSeparator = true;
/* 134 */     this.memberName = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPropertyExpected() {
/* 139 */     return (this.memberName == null && this.stack.peek() instanceof JSONObject);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValueSeparatorExpected() {
/* 144 */     return this.needSeparator;
/*     */   }
/*     */ 
/*     */   
/*     */   public JSONValue getResult() {
/* 149 */     if (!this.stack.isEmpty() || this.result == null) {
/* 150 */       throw new IllegalStateException();
/*     */     }
/* 152 */     return this.result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONValueTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */