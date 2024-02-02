/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.Reader;
/*    */ import java.io.Serializable;
/*    */ import java.nio.charset.Charset;
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
/*    */ public class LineIter
/*    */   extends ComputeIter<String>
/*    */   implements IterableIter<String>, Closeable, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final BufferedReader bufferedReader;
/*    */   
/*    */   public LineIter(InputStream in, Charset charset) throws IllegalArgumentException {
/* 50 */     this(IoUtil.getReader(in, charset));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LineIter(Reader reader) throws IllegalArgumentException {
/* 60 */     Assert.notNull(reader, "Reader must not be null", new Object[0]);
/* 61 */     this.bufferedReader = IoUtil.getReader(reader);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected String computeNext() {
/*    */     try {
/*    */       while (true) {
/* 69 */         String line = this.bufferedReader.readLine();
/* 70 */         if (line == null)
/* 71 */           return null; 
/* 72 */         if (isValidLine(line)) {
/* 73 */           return line;
/*    */         }
/*    */       }
/*    */     
/* 77 */     } catch (IOException ioe) {
/* 78 */       close();
/* 79 */       throw new IORuntimeException(ioe);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {
/* 88 */     finish();
/* 89 */     IoUtil.close(this.bufferedReader);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean isValidLine(String line) {
/* 99 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\LineIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */