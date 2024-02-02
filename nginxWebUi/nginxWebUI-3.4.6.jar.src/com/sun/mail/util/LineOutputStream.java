/*    */ package com.sun.mail.util;
/*    */ 
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
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
/*    */ public class LineOutputStream
/*    */   extends FilterOutputStream
/*    */ {
/* 60 */   private static byte[] newline = new byte[2]; static {
/* 61 */     newline[0] = 13;
/* 62 */     newline[1] = 10;
/*    */   }
/*    */   
/*    */   public LineOutputStream(OutputStream out) {
/* 66 */     super(out);
/*    */   }
/*    */   
/*    */   public void writeln(String s) throws IOException {
/* 70 */     byte[] bytes = ASCIIUtility.getBytes(s);
/* 71 */     this.out.write(bytes);
/* 72 */     this.out.write(newline);
/*    */   }
/*    */   
/*    */   public void writeln() throws IOException {
/* 76 */     this.out.write(newline);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\LineOutputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */