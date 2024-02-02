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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface RequestBody
/*    */ {
/*    */   void write(OutputStream paramOutputStream);
/*    */   
/*    */   default void writeClose(OutputStream out) {
/*    */     try {
/* 27 */       write(out);
/*    */     } finally {
/* 29 */       IoUtil.close(out);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\body\RequestBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */