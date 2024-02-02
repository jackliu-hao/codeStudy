/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.FilterReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InterpolationFilterReader
/*     */   extends FilterReader
/*     */ {
/*  69 */   private String replaceData = null;
/*     */ 
/*     */   
/*  72 */   private int replaceIndex = -1;
/*     */ 
/*     */   
/*  75 */   private int previousIndex = -1;
/*     */ 
/*     */   
/*  78 */   private Map variables = new HashMap();
/*     */ 
/*     */   
/*     */   private String beginToken;
/*     */ 
/*     */   
/*     */   private String endToken;
/*     */ 
/*     */   
/*     */   private int beginTokenLength;
/*     */ 
/*     */   
/*     */   private int endTokenLength;
/*     */ 
/*     */   
/*  93 */   private static String DEFAULT_BEGIN_TOKEN = "${";
/*     */ 
/*     */   
/*  96 */   private static String DEFAULT_END_TOKEN = "}";
/*     */ 
/*     */   
/*     */   public InterpolationFilterReader(Reader in, Map variables, String beginToken, String endToken) {
/* 100 */     super(in);
/*     */     
/* 102 */     this.variables = variables;
/* 103 */     this.beginToken = beginToken;
/* 104 */     this.endToken = endToken;
/*     */     
/* 106 */     this.beginTokenLength = beginToken.length();
/* 107 */     this.endTokenLength = endToken.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public InterpolationFilterReader(Reader in, Map variables) {
/* 112 */     this(in, variables, DEFAULT_BEGIN_TOKEN, DEFAULT_END_TOKEN);
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
/*     */   public long skip(long n) throws IOException {
/* 128 */     if (n < 0L)
/*     */     {
/* 130 */       throw new IllegalArgumentException("skip value is negative");
/*     */     }
/*     */     
/* 133 */     for (long i = 0L; i < n; i++) {
/*     */       
/* 135 */       if (read() == -1)
/*     */       {
/* 137 */         return i;
/*     */       }
/*     */     } 
/* 140 */     return n;
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
/*     */   public int read(char[] cbuf, int off, int len) throws IOException {
/* 163 */     for (int i = 0; i < len; i++) {
/*     */       
/* 165 */       int ch = read();
/* 166 */       if (ch == -1) {
/*     */         
/* 168 */         if (i == 0)
/*     */         {
/* 170 */           return -1;
/*     */         }
/*     */ 
/*     */         
/* 174 */         return i;
/*     */       } 
/*     */       
/* 177 */       cbuf[off + i] = (char)ch;
/*     */     } 
/* 179 */     return len;
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
/*     */   public int read() throws IOException {
/* 194 */     if (this.replaceIndex != -1 && this.replaceIndex < this.replaceData.length()) {
/*     */       
/* 196 */       int i = this.replaceData.charAt(this.replaceIndex++);
/* 197 */       if (this.replaceIndex >= this.replaceData.length())
/*     */       {
/* 199 */         this.replaceIndex = -1;
/*     */       }
/* 201 */       return i;
/*     */     } 
/*     */     
/* 204 */     int ch = -1;
/* 205 */     if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
/*     */       
/* 207 */       ch = this.endToken.charAt(this.previousIndex++);
/*     */     }
/*     */     else {
/*     */       
/* 211 */       ch = this.in.read();
/*     */     } 
/*     */     
/* 214 */     if (ch == this.beginToken.charAt(0)) {
/*     */       
/* 216 */       StringBuffer key = new StringBuffer();
/*     */       
/* 218 */       int beginTokenMatchPos = 1;
/*     */ 
/*     */       
/*     */       while (true) {
/* 222 */         if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
/*     */           
/* 224 */           ch = this.endToken.charAt(this.previousIndex++);
/*     */         }
/*     */         else {
/*     */           
/* 228 */           ch = this.in.read();
/*     */         } 
/* 230 */         if (ch != -1) {
/*     */           
/* 232 */           key.append((char)ch);
/*     */           
/* 234 */           if (beginTokenMatchPos < this.beginTokenLength && ch != this.beginToken.charAt(beginTokenMatchPos++)) {
/*     */ 
/*     */             
/* 237 */             ch = -1;
/*     */ 
/*     */ 
/*     */             
/*     */             break;
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 246 */           if (ch == this.endToken.charAt(0))
/*     */             break;  continue;
/*     */         }  break;
/* 249 */       }  if (ch != -1 && this.endTokenLength > 1) {
/*     */         
/* 251 */         int endTokenMatchPos = 1;
/*     */ 
/*     */         
/*     */         while (true) {
/* 255 */           if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
/*     */             
/* 257 */             ch = this.endToken.charAt(this.previousIndex++);
/*     */           }
/*     */           else {
/*     */             
/* 261 */             ch = this.in.read();
/*     */           } 
/*     */           
/* 264 */           if (ch != -1) {
/*     */             
/* 266 */             key.append((char)ch);
/*     */             
/* 268 */             if (ch != this.endToken.charAt(endTokenMatchPos++)) {
/*     */               
/* 270 */               ch = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               break;
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 280 */             if (endTokenMatchPos >= this.endTokenLength)
/*     */               break;  continue;
/*     */           } 
/*     */           break;
/*     */         } 
/*     */       } 
/* 286 */       if (ch == -1) {
/*     */         
/* 288 */         this.replaceData = key.toString();
/* 289 */         this.replaceIndex = 0;
/* 290 */         return this.beginToken.charAt(0);
/*     */       } 
/*     */       
/* 293 */       String variableKey = key.substring(this.beginTokenLength - 1, key.length() - this.endTokenLength);
/*     */       
/* 295 */       Object o = this.variables.get(variableKey);
/* 296 */       if (o != null) {
/*     */         
/* 298 */         String value = o.toString();
/* 299 */         if (value.length() != 0) {
/*     */           
/* 301 */           this.replaceData = value;
/* 302 */           this.replaceIndex = 0;
/*     */         } 
/* 304 */         return read();
/*     */       } 
/*     */ 
/*     */       
/* 308 */       this.previousIndex = 0;
/* 309 */       this.replaceData = key.substring(0, key.length() - this.endTokenLength);
/* 310 */       this.replaceIndex = 0;
/* 311 */       return this.beginToken.charAt(0);
/*     */     } 
/*     */ 
/*     */     
/* 315 */     return ch;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\InterpolationFilterReader.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */