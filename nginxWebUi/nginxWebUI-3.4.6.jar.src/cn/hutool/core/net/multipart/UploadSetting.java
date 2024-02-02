/*     */ package cn.hutool.core.net.multipart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UploadSetting
/*     */ {
/*  12 */   protected long maxFileSize = -1L;
/*     */   
/*  14 */   protected int memoryThreshold = 8192;
/*     */ 
/*     */ 
/*     */   
/*     */   protected String tmpUploadPath;
/*     */ 
/*     */ 
/*     */   
/*     */   protected String[] fileExts;
/*     */ 
/*     */   
/*     */   protected boolean isAllowFileExts = true;
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxFileSize() {
/*  30 */     return this.maxFileSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxFileSize(long maxFileSize) {
/*  39 */     this.maxFileSize = maxFileSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMemoryThreshold() {
/*  46 */     return this.memoryThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMemoryThreshold(int memoryThreshold) {
/*  56 */     this.memoryThreshold = memoryThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTmpUploadPath() {
/*  63 */     return this.tmpUploadPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTmpUploadPath(String tmpUploadPath) {
/*  72 */     this.tmpUploadPath = tmpUploadPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getFileExts() {
/*  79 */     return this.fileExts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFileExts(String[] fileExts) {
/*  89 */     this.fileExts = fileExts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAllowFileExts() {
/*  98 */     return this.isAllowFileExts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowFileExts(boolean isAllowFileExts) {
/* 107 */     this.isAllowFileExts = isAllowFileExts;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\multipart\UploadSetting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */