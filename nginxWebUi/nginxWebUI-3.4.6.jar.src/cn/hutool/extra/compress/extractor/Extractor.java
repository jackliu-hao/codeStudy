/*    */ package cn.hutool.extra.compress.extractor;
/*    */ 
/*    */ import cn.hutool.core.lang.Filter;
/*    */ import java.io.Closeable;
/*    */ import java.io.File;
/*    */ import org.apache.commons.compress.archivers.ArchiveEntry;
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
/*    */ public interface Extractor
/*    */   extends Closeable
/*    */ {
/*    */   default void extract(File targetDir) {
/* 23 */     extract(targetDir, null);
/*    */   }
/*    */   
/*    */   void extract(File paramFile, Filter<ArchiveEntry> paramFilter);
/*    */   
/*    */   void close();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\compress\extractor\Extractor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */