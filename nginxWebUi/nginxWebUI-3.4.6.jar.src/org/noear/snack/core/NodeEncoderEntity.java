/*    */ package org.noear.snack.core;
/*    */ 
/*    */ import org.noear.snack.ONode;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NodeEncoderEntity<T>
/*    */   implements NodeEncoder<T>
/*    */ {
/*    */   private final Class<T> type;
/*    */   private final NodeEncoder<T> encoder;
/*    */   
/*    */   public NodeEncoderEntity(Class<T> type, NodeEncoder<T> encoder) {
/* 16 */     this.type = type;
/* 17 */     this.encoder = encoder;
/*    */   }
/*    */   
/*    */   public boolean isEncodable(Class<?> cls) {
/* 21 */     return this.type.isAssignableFrom(cls);
/*    */   }
/*    */ 
/*    */   
/*    */   public void encode(T source, ONode target) {
/* 26 */     this.encoder.encode(source, target);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\NodeEncoderEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */