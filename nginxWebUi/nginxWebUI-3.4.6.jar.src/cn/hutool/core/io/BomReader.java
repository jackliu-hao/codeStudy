/*    */ package cn.hutool.core.io;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.Reader;
/*    */ import java.io.UnsupportedEncodingException;
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
/*    */ public class BomReader
/*    */   extends Reader
/*    */ {
/*    */   private InputStreamReader reader;
/*    */   
/*    */   public BomReader(InputStream in) {
/* 41 */     Assert.notNull(in, "InputStream must be not null!", new Object[0]);
/* 42 */     BOMInputStream bin = (in instanceof BOMInputStream) ? (BOMInputStream)in : new BOMInputStream(in);
/*    */     try {
/* 44 */       this.reader = new InputStreamReader(bin, bin.getCharset());
/* 45 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {}
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int read(char[] cbuf, int off, int len) throws IOException {
/* 51 */     return this.reader.read(cbuf, off, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 56 */     this.reader.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\BomReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */