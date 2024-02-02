/*     */ package javax.activation;
/*     */ 
/*     */ import java.io.File;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class FileTypeMap
/*     */ {
/*  50 */   private static FileTypeMap defaultMap = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setDefaultFileTypeMap(FileTypeMap map) {
/*  86 */     SecurityManager security = System.getSecurityManager();
/*  87 */     if (security != null)
/*     */       
/*     */       try {
/*  90 */         security.checkSetFactory();
/*  91 */       } catch (SecurityException ex) {
/*     */ 
/*     */ 
/*     */         
/*  95 */         if (FileTypeMap.class.getClassLoader() != map.getClass().getClassLoader())
/*     */         {
/*  97 */           throw ex;
/*     */         }
/*     */       }  
/* 100 */     defaultMap = map;
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
/*     */   public static FileTypeMap getDefaultFileTypeMap() {
/* 114 */     if (defaultMap == null)
/* 115 */       defaultMap = new MimetypesFileTypeMap(); 
/* 116 */     return defaultMap;
/*     */   }
/*     */   
/*     */   public abstract String getContentType(File paramFile);
/*     */   
/*     */   public abstract String getContentType(String paramString);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\FileTypeMap.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */