/*    */ package cn.hutool.extra.compress.archiver;
/*    */ 
/*    */ import cn.hutool.core.lang.Filter;
/*    */ import java.io.Closeable;
/*    */ import java.io.File;
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
/*    */ public interface Archiver
/*    */   extends Closeable
/*    */ {
/*    */   default Archiver add(File file) {
/* 23 */     return add(file, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default Archiver add(File file, Filter<File> filter) {
/* 34 */     return add(file, "/", filter);
/*    */   }
/*    */   
/*    */   Archiver add(File paramFile, String paramString, Filter<File> paramFilter);
/*    */   
/*    */   Archiver finish();
/*    */   
/*    */   void close();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\compress\archiver\Archiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */