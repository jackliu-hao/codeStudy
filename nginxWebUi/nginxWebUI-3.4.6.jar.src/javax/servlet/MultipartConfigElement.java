/*     */ package javax.servlet;
/*     */ 
/*     */ import javax.servlet.annotation.MultipartConfig;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultipartConfigElement
/*     */ {
/*     */   private String location;
/*     */   private long maxFileSize;
/*     */   private long maxRequestSize;
/*     */   private int fileSizeThreshold;
/*     */   
/*     */   public MultipartConfigElement(String location) {
/*  63 */     if (location == null) {
/*  64 */       this.location = "";
/*     */     } else {
/*  66 */       this.location = location;
/*     */     } 
/*  68 */     this.maxFileSize = -1L;
/*  69 */     this.maxRequestSize = -1L;
/*  70 */     this.fileSizeThreshold = 0;
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
/*     */   public MultipartConfigElement(String location, long maxFileSize, long maxRequestSize, int fileSizeThreshold) {
/*  85 */     if (location == null) {
/*  86 */       this.location = "";
/*     */     } else {
/*  88 */       this.location = location;
/*     */     } 
/*  90 */     this.maxFileSize = maxFileSize;
/*  91 */     this.maxRequestSize = maxRequestSize;
/*  92 */     this.fileSizeThreshold = fileSizeThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartConfigElement(MultipartConfig annotation) {
/* 101 */     this.location = annotation.location();
/* 102 */     this.fileSizeThreshold = annotation.fileSizeThreshold();
/* 103 */     this.maxFileSize = annotation.maxFileSize();
/* 104 */     this.maxRequestSize = annotation.maxRequestSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLocation() {
/* 113 */     return this.location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxFileSize() {
/* 122 */     return this.maxFileSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxRequestSize() {
/* 131 */     return this.maxRequestSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFileSizeThreshold() {
/* 140 */     return this.fileSizeThreshold;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\MultipartConfigElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */