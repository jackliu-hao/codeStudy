/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.xnio._private.Messages;
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
/*     */ public final class Property
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -4958518978461712277L;
/*     */   private final String key;
/*     */   private final Object value;
/*     */   
/*     */   private Property(String key, Object value) {
/*  39 */     if (key == null) {
/*  40 */       throw Messages.msg.nullParameter("key");
/*     */     }
/*  42 */     if (value == null) {
/*  43 */       throw Messages.msg.nullParameter("value");
/*     */     }
/*  45 */     this.key = key;
/*  46 */     this.value = value;
/*     */   }
/*     */   
/*     */   private Property(String key, String value) {
/*  50 */     this(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getKey() {
/*  59 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue() {
/*  68 */     return this.value;
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
/*     */   public String toString() {
/*  88 */     return "(" + this.key + "=>" + this.value.toString() + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  98 */     return this.key.hashCode() * 7 + this.value.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 109 */     return (obj instanceof Property && equals((Property)obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Property other) {
/* 119 */     return (this.key.equals(other.key) && this.value.equals(other.value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Property of(String key, Object value) {
/* 130 */     return new Property(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Property of(String key, String value) {
/* 141 */     return new Property(key, value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Property.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */