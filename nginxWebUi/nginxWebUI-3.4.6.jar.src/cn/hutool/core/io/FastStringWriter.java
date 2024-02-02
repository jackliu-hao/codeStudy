/*    */ package cn.hutool.core.io;
/*    */ 
/*    */ import cn.hutool.core.text.StrBuilder;
/*    */ import java.io.Writer;
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
/*    */ public final class FastStringWriter
/*    */   extends Writer
/*    */ {
/*    */   private final StrBuilder builder;
/*    */   
/*    */   public FastStringWriter() {
/* 21 */     this(16);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FastStringWriter(int initialSize) {
/* 30 */     if (initialSize < 0) {
/* 31 */       initialSize = 16;
/*    */     }
/* 33 */     this.builder = new StrBuilder(initialSize);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(int c) {
/* 39 */     this.builder.append((char)c);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(String str) {
/* 45 */     this.builder.append(str);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(String str, int off, int len) {
/* 51 */     this.builder.append(str, off, off + len);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(char[] cbuf) {
/* 57 */     this.builder.append(cbuf, 0, cbuf.length);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(char[] cbuf, int off, int len) {
/* 63 */     if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0)
/*    */     {
/* 65 */       throw new IndexOutOfBoundsException(); } 
/* 66 */     if (len == 0) {
/*    */       return;
/*    */     }
/* 69 */     this.builder.append(cbuf, off, len);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void flush() {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 87 */     return this.builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\FastStringWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */