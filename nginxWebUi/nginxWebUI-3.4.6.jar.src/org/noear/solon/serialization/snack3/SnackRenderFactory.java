/*    */ package org.noear.solon.serialization.snack3;
/*    */ 
/*    */ import org.noear.snack.core.Feature;
/*    */ import org.noear.snack.core.Options;
/*    */ import org.noear.solon.core.handle.Render;
/*    */ import org.noear.solon.serialization.StringSerializerRender;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SnackRenderFactory
/*    */   extends SnackRenderFactoryBase
/*    */ {
/* 15 */   public static final SnackRenderFactory global = new SnackRenderFactory();
/*    */ 
/*    */ 
/*    */   
/* 19 */   private final Options config = Options.def();
/*    */ 
/*    */ 
/*    */   
/*    */   public Render create() {
/* 24 */     return (Render)new StringSerializerRender(false, new SnackSerializer(this.config));
/*    */   }
/*    */ 
/*    */   
/*    */   protected Options config() {
/* 29 */     return this.config;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setFeatures(Feature... features) {
/* 36 */     this.config.setFeatures(features);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\snack3\SnackRenderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */