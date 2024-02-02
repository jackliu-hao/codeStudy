/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.lang.Opt;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OptConverter
/*    */   extends AbstractConverter<Opt<?>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Opt<?> convertInternal(Object value) {
/* 18 */     return Opt.ofNullable(value);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\OptConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */