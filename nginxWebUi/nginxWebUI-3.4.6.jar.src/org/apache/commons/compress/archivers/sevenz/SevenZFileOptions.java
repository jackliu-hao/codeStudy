/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SevenZFileOptions
/*     */ {
/*     */   private static final int DEFAUL_MEMORY_LIMIT_IN_KB = 2147483647;
/*     */   private static final boolean DEFAULT_USE_DEFAULTNAME_FOR_UNNAMED_ENTRIES = false;
/*     */   private static final boolean DEFAULT_TRY_TO_RECOVER_BROKEN_ARCHIVES = false;
/*     */   private final int maxMemoryLimitInKb;
/*     */   private final boolean useDefaultNameForUnnamedEntries;
/*     */   private final boolean tryToRecoverBrokenArchives;
/*     */   
/*     */   private SevenZFileOptions(int maxMemoryLimitInKb, boolean useDefaultNameForUnnamedEntries, boolean tryToRecoverBrokenArchives) {
/*  37 */     this.maxMemoryLimitInKb = maxMemoryLimitInKb;
/*  38 */     this.useDefaultNameForUnnamedEntries = useDefaultNameForUnnamedEntries;
/*  39 */     this.tryToRecoverBrokenArchives = tryToRecoverBrokenArchives;
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
/*  50 */   public static final SevenZFileOptions DEFAULT = new SevenZFileOptions(2147483647, false, false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder() {
/*  59 */     return new Builder();
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
/*     */   public int getMaxMemoryLimitInKb() {
/*  72 */     return this.maxMemoryLimitInKb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getUseDefaultNameForUnnamedEntries() {
/*  82 */     return this.useDefaultNameForUnnamedEntries;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getTryToRecoverBrokenArchives() {
/*  91 */     return this.tryToRecoverBrokenArchives;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/* 100 */     private int maxMemoryLimitInKb = Integer.MAX_VALUE;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean useDefaultNameForUnnamedEntries = false;
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean tryToRecoverBrokenArchives = false;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withMaxMemoryLimitInKb(int maxMemoryLimitInKb) {
/* 115 */       this.maxMemoryLimitInKb = maxMemoryLimitInKb;
/* 116 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withUseDefaultNameForUnnamedEntries(boolean useDefaultNameForUnnamedEntries) {
/* 128 */       this.useDefaultNameForUnnamedEntries = useDefaultNameForUnnamedEntries;
/* 129 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withTryToRecoverBrokenArchives(boolean tryToRecoverBrokenArchives) {
/* 147 */       this.tryToRecoverBrokenArchives = tryToRecoverBrokenArchives;
/* 148 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SevenZFileOptions build() {
/* 157 */       return new SevenZFileOptions(this.maxMemoryLimitInKb, this.useDefaultNameForUnnamedEntries, this.tryToRecoverBrokenArchives);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\SevenZFileOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */