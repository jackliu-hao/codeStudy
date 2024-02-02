/*     */ package org.apache.http.message;
/*     */ 
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.NameValuePair;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicHeaderElement
/*     */   implements HeaderElement, Cloneable
/*     */ {
/*     */   private final String name;
/*     */   private final String value;
/*     */   private final NameValuePair[] parameters;
/*     */   
/*     */   public BasicHeaderElement(String name, String value, NameValuePair[] parameters) {
/*  59 */     this.name = (String)Args.notNull(name, "Name");
/*  60 */     this.value = value;
/*  61 */     if (parameters != null) {
/*  62 */       this.parameters = parameters;
/*     */     } else {
/*  64 */       this.parameters = new NameValuePair[0];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicHeaderElement(String name, String value) {
/*  75 */     this(name, value, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  80 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValue() {
/*  85 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public NameValuePair[] getParameters() {
/*  90 */     return (NameValuePair[])this.parameters.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getParameterCount() {
/*  95 */     return this.parameters.length;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NameValuePair getParameter(int index) {
/* 101 */     return this.parameters[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public NameValuePair getParameterByName(String name) {
/* 106 */     Args.notNull(name, "Name");
/* 107 */     NameValuePair found = null;
/* 108 */     for (NameValuePair current : this.parameters) {
/* 109 */       if (current.getName().equalsIgnoreCase(name)) {
/* 110 */         found = current;
/*     */         break;
/*     */       } 
/*     */     } 
/* 114 */     return found;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 119 */     if (this == object) {
/* 120 */       return true;
/*     */     }
/* 122 */     if (object instanceof HeaderElement) {
/* 123 */       BasicHeaderElement that = (BasicHeaderElement)object;
/* 124 */       return (this.name.equals(that.name) && LangUtils.equals(this.value, that.value) && LangUtils.equals((Object[])this.parameters, (Object[])that.parameters));
/*     */     } 
/*     */ 
/*     */     
/* 128 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 133 */     int hash = 17;
/* 134 */     hash = LangUtils.hashCode(hash, this.name);
/* 135 */     hash = LangUtils.hashCode(hash, this.value);
/* 136 */     for (NameValuePair parameter : this.parameters) {
/* 137 */       hash = LangUtils.hashCode(hash, parameter);
/*     */     }
/* 139 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 144 */     StringBuilder buffer = new StringBuilder();
/* 145 */     buffer.append(this.name);
/* 146 */     if (this.value != null) {
/* 147 */       buffer.append("=");
/* 148 */       buffer.append(this.value);
/*     */     } 
/* 150 */     for (NameValuePair parameter : this.parameters) {
/* 151 */       buffer.append("; ");
/* 152 */       buffer.append(parameter);
/*     */     } 
/* 154 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 161 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHeaderElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */