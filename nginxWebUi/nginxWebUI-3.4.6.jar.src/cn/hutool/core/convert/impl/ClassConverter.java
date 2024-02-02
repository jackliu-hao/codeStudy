/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.util.ClassLoaderUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClassConverter
/*    */   extends AbstractConverter<Class<?>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final boolean isInitialized;
/*    */   
/*    */   public ClassConverter() {
/* 21 */     this(true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClassConverter(boolean isInitialized) {
/* 31 */     this.isInitialized = isInitialized;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Class<?> convertInternal(Object value) {
/* 36 */     return ClassLoaderUtil.loadClass(convertToStr(value), this.isInitialized);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\ClassConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */