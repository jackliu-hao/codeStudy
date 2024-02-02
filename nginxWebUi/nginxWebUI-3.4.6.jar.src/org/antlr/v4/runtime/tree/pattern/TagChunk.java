/*     */ package org.antlr.v4.runtime.tree.pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class TagChunk
/*     */   extends Chunk
/*     */ {
/*     */   private final String tag;
/*     */   private final String label;
/*     */   
/*     */   public TagChunk(String tag) {
/*  68 */     this(null, tag);
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
/*     */   public TagChunk(String label, String tag) {
/*  84 */     if (tag == null || tag.isEmpty()) {
/*  85 */       throw new IllegalArgumentException("tag cannot be null or empty");
/*     */     }
/*     */     
/*  88 */     this.label = label;
/*  89 */     this.tag = tag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getTag() {
/*  99 */     return this.tag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getLabel() {
/* 110 */     return this.label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 120 */     if (this.label != null) {
/* 121 */       return this.label + ":" + this.tag;
/*     */     }
/*     */     
/* 124 */     return this.tag;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\pattern\TagChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */