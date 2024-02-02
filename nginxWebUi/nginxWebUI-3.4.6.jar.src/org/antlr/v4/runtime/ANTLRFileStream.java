/*    */ package org.antlr.v4.runtime;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.antlr.v4.runtime.misc.Utils;
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
/*    */ public class ANTLRFileStream
/*    */   extends ANTLRInputStream
/*    */ {
/*    */   protected String fileName;
/*    */   
/*    */   public ANTLRFileStream(String fileName) throws IOException {
/* 44 */     this(fileName, (String)null);
/*    */   }
/*    */   
/*    */   public ANTLRFileStream(String fileName, String encoding) throws IOException {
/* 48 */     this.fileName = fileName;
/* 49 */     load(fileName, encoding);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void load(String fileName, String encoding) throws IOException {
/* 55 */     this.data = Utils.readFile(fileName, encoding);
/* 56 */     this.n = this.data.length;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSourceName() {
/* 61 */     return this.fileName;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\ANTLRFileStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */