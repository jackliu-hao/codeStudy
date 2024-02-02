/*    */ package org.noear.solon.core;
/*    */ 
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Properties;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.wrap.ClassWrap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PropsConverter
/*    */ {
/*    */   private static PropsConverter global;
/*    */   
/*    */   public static PropsConverter global() {
/* 17 */     return global;
/*    */   }
/*    */   
/*    */   public static void globalSet(PropsConverter instance) {
/* 21 */     if (instance != null) {
/* 22 */       global = instance;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   static {
/* 28 */     PropsConverter tmp = (PropsConverter)Utils.newInstance("org.noear.solon.extend.impl.PropsConverterExt");
/*    */     
/* 30 */     if (tmp == null) {
/* 31 */       global = new PropsConverter();
/*    */     } else {
/* 33 */       global = tmp;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public <T> T convert(Properties props, T target, Class<T> targetClz, Type targetType) {
/* 46 */     if (target == null) {
/* 47 */       return (T)ClassWrap.get(targetClz).newBy(props);
/*    */     }
/* 49 */     ClassWrap.get(target.getClass()).fill(target, props::getProperty);
/* 50 */     return target;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\PropsConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */