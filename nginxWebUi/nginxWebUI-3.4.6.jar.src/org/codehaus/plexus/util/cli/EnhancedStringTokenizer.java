/*     */ package org.codehaus.plexus.util.cli;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class EnhancedStringTokenizer
/*     */ {
/*  30 */   private StringTokenizer cst = null;
/*     */   
/*     */   String cdelim;
/*     */   
/*     */   final boolean cdelimSingleChar;
/*     */   
/*     */   final char cdelimChar;
/*     */   
/*     */   boolean creturnDelims;
/*     */   
/*  40 */   String lastToken = null;
/*     */   
/*     */   boolean delimLast = true;
/*     */ 
/*     */   
/*     */   public EnhancedStringTokenizer(String str) {
/*  46 */     this(str, " \t\n\r\f", false);
/*     */   }
/*     */ 
/*     */   
/*     */   public EnhancedStringTokenizer(String str, String delim) {
/*  51 */     this(str, delim, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public EnhancedStringTokenizer(String str, String delim, boolean returnDelims) {
/*  56 */     this.cst = new StringTokenizer(str, delim, true);
/*  57 */     this.cdelim = delim;
/*  58 */     this.creturnDelims = returnDelims;
/*  59 */     this.cdelimSingleChar = (delim.length() == 1);
/*  60 */     this.cdelimChar = delim.charAt(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMoreTokens() {
/*  65 */     return this.cst.hasMoreTokens();
/*     */   }
/*     */ 
/*     */   
/*     */   private String internalNextToken() {
/*  70 */     if (this.lastToken != null) {
/*     */       
/*  72 */       String last = this.lastToken;
/*  73 */       this.lastToken = null;
/*  74 */       return last;
/*     */     } 
/*     */     
/*  77 */     String token = this.cst.nextToken();
/*  78 */     if (isDelim(token)) {
/*     */       
/*  80 */       if (this.delimLast) {
/*     */         
/*  82 */         this.lastToken = token;
/*  83 */         return "";
/*     */       } 
/*     */ 
/*     */       
/*  87 */       this.delimLast = true;
/*  88 */       return token;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  93 */     this.delimLast = false;
/*  94 */     return token;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String nextToken() {
/* 100 */     String token = internalNextToken();
/* 101 */     if (this.creturnDelims)
/*     */     {
/* 103 */       return token;
/*     */     }
/* 105 */     if (isDelim(token))
/*     */     {
/* 107 */       return hasMoreTokens() ? internalNextToken() : "";
/*     */     }
/*     */ 
/*     */     
/* 111 */     return token;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isDelim(String str) {
/* 117 */     if (str.length() == 1) {
/*     */       
/* 119 */       char ch = str.charAt(0);
/* 120 */       if (this.cdelimSingleChar) {
/*     */         
/* 122 */         if (this.cdelimChar == ch)
/*     */         {
/* 124 */           return true;
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 129 */       else if (this.cdelim.indexOf(ch) >= 0) {
/*     */         
/* 131 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 135 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\EnhancedStringTokenizer.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */