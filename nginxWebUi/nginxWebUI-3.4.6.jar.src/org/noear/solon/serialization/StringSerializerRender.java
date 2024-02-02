/*    */ package org.noear.solon.serialization;
/*    */ 
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Render;
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
/*    */ public class StringSerializerRender
/*    */   implements Render
/*    */ {
/*    */   StringSerializer serializer;
/*    */   boolean typed;
/*    */   
/*    */   public StringSerializerRender(boolean typed, StringSerializer serializer) {
/* 24 */     this.typed = typed;
/* 25 */     this.serializer = serializer;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 30 */     return getClass().getSimpleName() + "#" + this.serializer.getClass().getSimpleName();
/*    */   }
/*    */ 
/*    */   
/*    */   public String renderAndReturn(Object data, Context ctx) throws Throwable {
/* 35 */     return this.serializer.serialize(data);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void render(Object obj, Context ctx) throws Throwable {
/* 43 */     String txt = null;
/*    */     
/* 45 */     if (this.typed) {
/*    */ 
/*    */       
/* 48 */       txt = this.serializer.serialize(obj);
/*    */     }
/*    */     else {
/*    */       
/* 52 */       if (obj == null) {
/*    */         return;
/*    */       }
/*    */       
/* 56 */       if (obj instanceof Throwable) {
/* 57 */         throw (Throwable)obj;
/*    */       }
/*    */       
/* 60 */       if (obj instanceof String) {
/* 61 */         txt = (String)obj;
/*    */       } else {
/* 63 */         txt = this.serializer.serialize(obj);
/*    */       } 
/*    */     } 
/*    */     
/* 67 */     ctx.attrSet("output", txt);
/*    */     
/* 69 */     output(ctx, obj, txt);
/*    */   }
/*    */   
/*    */   protected void output(Context ctx, Object obj, String txt) {
/* 73 */     if (obj instanceof String && !ctx.accept().contains("/json")) {
/* 74 */       ctx.output(txt);
/*    */     } else {
/* 76 */       ctx.outputAsJson(txt);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\StringSerializerRender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */