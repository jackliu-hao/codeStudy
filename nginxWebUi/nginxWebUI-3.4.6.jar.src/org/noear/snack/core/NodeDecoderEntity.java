/*    */ package org.noear.snack.core;
/*    */ 
/*    */ import java.lang.reflect.Type;
/*    */ import org.noear.snack.ONode;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NodeDecoderEntity<T>
/*    */   implements NodeDecoder<T>
/*    */ {
/*    */   private final Class<T> type;
/*    */   private final NodeDecoder<T> decoder;
/*    */   
/*    */   public NodeDecoderEntity(Class<T> type, NodeDecoder<T> decoder) {
/* 18 */     this.type = type;
/* 19 */     this.decoder = decoder;
/*    */   }
/*    */   
/*    */   public boolean isDecodable(Class<?> cls) {
/* 23 */     return this.type.isAssignableFrom(cls);
/*    */   }
/*    */ 
/*    */   
/*    */   public T decode(ONode source, Type type) {
/* 28 */     return this.decoder.decode(source, type);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\NodeDecoderEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */