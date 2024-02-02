/*    */ package org.noear.solon.serialization.snack3;
/*    */ 
/*    */ import org.noear.snack.core.NodeEncoder;
/*    */ import org.noear.snack.core.Options;
/*    */ import org.noear.solon.core.handle.Render;
/*    */ import org.noear.solon.serialization.StringSerializerRender;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SnackRenderTypedFactory
/*    */   extends SnackRenderFactoryBase
/*    */ {
/* 15 */   public static final SnackRenderTypedFactory global = new SnackRenderTypedFactory();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 20 */   private final Options config = Options.serialize();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
/* 28 */     this.config.addEncoder(clz, encoder);
/*    */   }
/*    */ 
/*    */   
/*    */   public Render create() {
/* 33 */     return (Render)new StringSerializerRender(true, new SnackSerializer(this.config));
/*    */   }
/*    */ 
/*    */   
/*    */   protected Options config() {
/* 38 */     return this.config;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\snack3\SnackRenderTypedFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */