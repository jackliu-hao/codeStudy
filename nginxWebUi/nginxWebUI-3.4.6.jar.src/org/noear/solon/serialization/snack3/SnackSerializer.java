/*    */ package org.noear.solon.serialization.snack3;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.noear.snack.ONode;
/*    */ import org.noear.snack.core.Options;
/*    */ import org.noear.solon.serialization.StringSerializer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SnackSerializer
/*    */   implements StringSerializer
/*    */ {
/*    */   final Options options;
/*    */   
/*    */   public SnackSerializer(Options options) {
/* 19 */     this.options = options;
/*    */   }
/*    */ 
/*    */   
/*    */   public String serialize(Object obj) throws IOException {
/* 24 */     return ONode.loadObj(obj, this.options).toJson();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\snack3\SnackSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */