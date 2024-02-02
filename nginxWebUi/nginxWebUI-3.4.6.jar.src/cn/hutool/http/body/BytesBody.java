/*    */ package cn.hutool.http.body;
/*    */ 
/*    */ import cn.hutool.core.io.IoUtil;
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
/*    */ public class BytesBody
/*    */   implements RequestBody
/*    */ {
/*    */   private final byte[] content;
/*    */   
/*    */   public static BytesBody create(byte[] content) {
/* 23 */     return new BytesBody(content);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BytesBody(byte[] content) {
/* 32 */     this.content = content;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(OutputStream out) {
/* 37 */     IoUtil.write(out, false, this.content);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\body\BytesBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */