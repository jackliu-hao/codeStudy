/*    */ package freemarker.template.utility;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public final class NullWriter
/*    */   extends Writer
/*    */ {
/* 32 */   public static final NullWriter INSTANCE = new NullWriter();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(char[] cbuf, int off, int len) throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(int c) throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(char[] cbuf) throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(String str) throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(String str, int off, int len) throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Writer append(CharSequence csq) throws IOException {
/* 75 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Writer append(CharSequence csq, int start, int end) throws IOException {
/* 81 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Writer append(char c) throws IOException {
/* 87 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\NullWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */