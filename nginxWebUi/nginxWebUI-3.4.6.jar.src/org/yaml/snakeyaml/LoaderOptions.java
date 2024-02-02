/*     */ package org.yaml.snakeyaml;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoaderOptions
/*     */ {
/*     */   private boolean allowDuplicateKeys = true;
/*     */   private boolean wrappedToRootException = false;
/*  22 */   private int maxAliasesForCollections = 50;
/*     */   private boolean allowRecursiveKeys = false;
/*     */   private boolean processComments = false;
/*     */   private boolean enumCaseSensitive = true;
/*     */   
/*     */   public boolean isAllowDuplicateKeys() {
/*  28 */     return this.allowDuplicateKeys;
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
/*     */   public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
/*  47 */     this.allowDuplicateKeys = allowDuplicateKeys;
/*     */   }
/*     */   
/*     */   public boolean isWrappedToRootException() {
/*  51 */     return this.wrappedToRootException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWrappedToRootException(boolean wrappedToRootException) {
/*  62 */     this.wrappedToRootException = wrappedToRootException;
/*     */   }
/*     */   
/*     */   public int getMaxAliasesForCollections() {
/*  66 */     return this.maxAliasesForCollections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxAliasesForCollections(int maxAliasesForCollections) {
/*  74 */     this.maxAliasesForCollections = maxAliasesForCollections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowRecursiveKeys(boolean allowRecursiveKeys) {
/*  84 */     this.allowRecursiveKeys = allowRecursiveKeys;
/*     */   }
/*     */   
/*     */   public boolean getAllowRecursiveKeys() {
/*  88 */     return this.allowRecursiveKeys;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProcessComments(boolean processComments) {
/*  97 */     this.processComments = processComments;
/*     */   }
/*     */   
/*     */   public boolean isProcessComments() {
/* 101 */     return this.processComments;
/*     */   }
/*     */   
/*     */   public boolean isEnumCaseSensitive() {
/* 105 */     return this.enumCaseSensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnumCaseSensitive(boolean enumCaseSensitive) {
/* 115 */     this.enumCaseSensitive = enumCaseSensitive;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\LoaderOptions.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */