/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VocabularyImpl
/*     */   implements Vocabulary
/*     */ {
/*  41 */   private static final String[] EMPTY_NAMES = new String[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   public static final VocabularyImpl EMPTY_VOCABULARY = new VocabularyImpl(EMPTY_NAMES, EMPTY_NAMES, EMPTY_NAMES);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] literalNames;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] symbolicNames;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] displayNames;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int maxTokenType;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VocabularyImpl(String[] literalNames, String[] symbolicNames) {
/*  75 */     this(literalNames, symbolicNames, null);
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
/*     */   public VocabularyImpl(String[] literalNames, String[] symbolicNames, String[] displayNames) {
/*  96 */     this.literalNames = (literalNames != null) ? literalNames : EMPTY_NAMES;
/*  97 */     this.symbolicNames = (symbolicNames != null) ? symbolicNames : EMPTY_NAMES;
/*  98 */     this.displayNames = (displayNames != null) ? displayNames : EMPTY_NAMES;
/*     */     
/* 100 */     this.maxTokenType = Math.max(this.displayNames.length, Math.max(this.literalNames.length, this.symbolicNames.length)) - 1;
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
/*     */   public static Vocabulary fromTokenNames(String[] tokenNames) {
/* 120 */     if (tokenNames == null || tokenNames.length == 0) {
/* 121 */       return EMPTY_VOCABULARY;
/*     */     }
/*     */     
/* 124 */     String[] literalNames = Arrays.<String>copyOf(tokenNames, tokenNames.length);
/* 125 */     String[] symbolicNames = Arrays.<String>copyOf(tokenNames, tokenNames.length);
/* 126 */     for (int i = 0; i < tokenNames.length; i++) {
/* 127 */       String tokenName = tokenNames[i];
/* 128 */       if (tokenName == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 132 */       if (!tokenName.isEmpty()) {
/* 133 */         char firstChar = tokenName.charAt(0);
/* 134 */         if (firstChar == '\'') {
/* 135 */           symbolicNames[i] = null;
/*     */           continue;
/*     */         } 
/* 138 */         if (Character.isUpperCase(firstChar)) {
/* 139 */           literalNames[i] = null;
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } 
/*     */       
/* 145 */       literalNames[i] = null;
/* 146 */       symbolicNames[i] = null;
/*     */       continue;
/*     */     } 
/* 149 */     return new VocabularyImpl(literalNames, symbolicNames, tokenNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxTokenType() {
/* 154 */     return this.maxTokenType;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLiteralName(int tokenType) {
/* 159 */     if (tokenType >= 0 && tokenType < this.literalNames.length) {
/* 160 */       return this.literalNames[tokenType];
/*     */     }
/*     */     
/* 163 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSymbolicName(int tokenType) {
/* 168 */     if (tokenType >= 0 && tokenType < this.symbolicNames.length) {
/* 169 */       return this.symbolicNames[tokenType];
/*     */     }
/*     */     
/* 172 */     if (tokenType == -1) {
/* 173 */       return "EOF";
/*     */     }
/*     */     
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayName(int tokenType) {
/* 181 */     if (tokenType >= 0 && tokenType < this.displayNames.length) {
/* 182 */       String displayName = this.displayNames[tokenType];
/* 183 */       if (displayName != null) {
/* 184 */         return displayName;
/*     */       }
/*     */     } 
/*     */     
/* 188 */     String literalName = getLiteralName(tokenType);
/* 189 */     if (literalName != null) {
/* 190 */       return literalName;
/*     */     }
/*     */     
/* 193 */     String symbolicName = getSymbolicName(tokenType);
/* 194 */     if (symbolicName != null) {
/* 195 */       return symbolicName;
/*     */     }
/*     */     
/* 198 */     return Integer.toString(tokenType);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\VocabularyImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */