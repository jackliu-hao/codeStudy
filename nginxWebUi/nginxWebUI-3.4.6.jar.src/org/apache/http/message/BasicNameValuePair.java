/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.LangUtils;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class BasicNameValuePair
/*     */   implements NameValuePair, Cloneable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -6437800749411518984L;
/*     */   private final String name;
/*     */   private final String value;
/*     */   
/*     */   public BasicNameValuePair(String name, String value) {
/*  59 */     this.name = (String)Args.notNull(name, "Name");
/*  60 */     this.value = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  65 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValue() {
/*  70 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  77 */     if (this.value == null) {
/*  78 */       return this.name;
/*     */     }
/*  80 */     int len = this.name.length() + 1 + this.value.length();
/*  81 */     StringBuilder buffer = new StringBuilder(len);
/*  82 */     buffer.append(this.name);
/*  83 */     buffer.append("=");
/*  84 */     buffer.append(this.value);
/*  85 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/*  90 */     if (this == object) {
/*  91 */       return true;
/*     */     }
/*  93 */     if (object instanceof NameValuePair) {
/*  94 */       BasicNameValuePair that = (BasicNameValuePair)object;
/*  95 */       return (this.name.equals(that.name) && LangUtils.equals(this.value, that.value));
/*     */     } 
/*     */     
/*  98 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 103 */     int hash = 17;
/* 104 */     hash = LangUtils.hashCode(hash, this.name);
/* 105 */     hash = LangUtils.hashCode(hash, this.value);
/* 106 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 111 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicNameValuePair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */