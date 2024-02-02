/*    */ package ch.qos.logback.core.joran.spi;
/*    */ 
/*    */ import ch.qos.logback.core.spi.ContextAwareBase;
/*    */ import java.io.File;
/*    */ import java.net.URL;
/*    */ import java.net.URLDecoder;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConfigurationWatchList
/*    */   extends ContextAwareBase
/*    */ {
/*    */   URL mainURL;
/* 30 */   List<File> fileWatchList = new ArrayList<File>();
/* 31 */   List<Long> lastModifiedList = new ArrayList<Long>();
/*    */   
/*    */   public ConfigurationWatchList buildClone() {
/* 34 */     ConfigurationWatchList out = new ConfigurationWatchList();
/* 35 */     out.mainURL = this.mainURL;
/* 36 */     out.fileWatchList = new ArrayList<File>(this.fileWatchList);
/* 37 */     out.lastModifiedList = new ArrayList<Long>(this.lastModifiedList);
/* 38 */     return out;
/*    */   }
/*    */   
/*    */   public void clear() {
/* 42 */     this.mainURL = null;
/* 43 */     this.lastModifiedList.clear();
/* 44 */     this.fileWatchList.clear();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setMainURL(URL mainURL) {
/* 53 */     this.mainURL = mainURL;
/* 54 */     if (mainURL != null)
/* 55 */       addAsFileToWatch(mainURL); 
/*    */   }
/*    */   
/*    */   private void addAsFileToWatch(URL url) {
/* 59 */     File file = convertToFile(url);
/* 60 */     if (file != null) {
/* 61 */       this.fileWatchList.add(file);
/* 62 */       this.lastModifiedList.add(Long.valueOf(file.lastModified()));
/*    */     } 
/*    */   }
/*    */   
/*    */   public void addToWatchList(URL url) {
/* 67 */     addAsFileToWatch(url);
/*    */   }
/*    */   
/*    */   public URL getMainURL() {
/* 71 */     return this.mainURL;
/*    */   }
/*    */   
/*    */   public List<File> getCopyOfFileWatchList() {
/* 75 */     return new ArrayList<File>(this.fileWatchList);
/*    */   }
/*    */   
/*    */   public boolean changeDetected() {
/* 79 */     int len = this.fileWatchList.size();
/* 80 */     for (int i = 0; i < len; i++) {
/* 81 */       long lastModified = ((Long)this.lastModifiedList.get(i)).longValue();
/* 82 */       File file = this.fileWatchList.get(i);
/* 83 */       if (lastModified != file.lastModified()) {
/* 84 */         return true;
/*    */       }
/*    */     } 
/* 87 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   File convertToFile(URL url) {
/* 93 */     String protocol = url.getProtocol();
/* 94 */     if ("file".equals(protocol)) {
/* 95 */       return new File(URLDecoder.decode(url.getFile()));
/*    */     }
/* 97 */     addInfo("URL [" + url + "] is not of type file");
/* 98 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\ConfigurationWatchList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */