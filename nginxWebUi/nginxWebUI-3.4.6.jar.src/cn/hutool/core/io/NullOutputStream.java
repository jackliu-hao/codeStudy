/*    */ package cn.hutool.core.io;
/*    */ 
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
/*    */ public class NullOutputStream
/*    */   extends OutputStream
/*    */ {
/* 18 */   public static final NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream();
/*    */   
/*    */   public void write(byte[] b, int off, int len) {}
/*    */   
/*    */   public void write(int b) {}
/*    */   
/*    */   public void write(byte[] b) throws IOException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\NullOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */