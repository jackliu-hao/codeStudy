/*    */ package cn.hutool.extra.compress.extractor;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
/*    */ import org.apache.commons.compress.archivers.sevenz.SevenZFile;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Seven7EntryInputStream
/*    */   extends InputStream
/*    */ {
/*    */   private final SevenZFile sevenZFile;
/*    */   private final long size;
/* 19 */   private long readSize = 0L;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Seven7EntryInputStream(SevenZFile sevenZFile, SevenZArchiveEntry entry) {
/* 28 */     this.sevenZFile = sevenZFile;
/* 29 */     this.size = entry.getSize();
/*    */   }
/*    */ 
/*    */   
/*    */   public int available() throws IOException {
/*    */     try {
/* 35 */       return Math.toIntExact(this.size);
/* 36 */     } catch (ArithmeticException e) {
/* 37 */       throw new IOException("Entry size is too large!(max than Integer.MAX)", e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getReadSize() {
/* 48 */     return this.readSize;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 53 */     this.readSize++;
/* 54 */     return this.sevenZFile.read();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\compress\extractor\Seven7EntryInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */