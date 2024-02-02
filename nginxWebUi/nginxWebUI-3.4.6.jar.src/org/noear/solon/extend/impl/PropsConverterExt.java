/*    */ package org.noear.solon.extend.impl;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Properties;
/*    */ import org.noear.snack.ONode;
/*    */ import org.noear.snack.core.Feature;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.PropsConverter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PropsConverterExt
/*    */   extends PropsConverter
/*    */ {
/*    */   public <T> T convert(Properties props, T target, Class<T> targetClz, Type<T> targetType) {
/* 19 */     if (target == null) {
/*    */       
/*    */       try {
/* 22 */         Constructor<T> constructor = targetClz.getConstructor(new Class[] { Properties.class });
/* 23 */         if (constructor != null) {
/* 24 */           return constructor.newInstance(new Object[] { props });
/*    */         }
/* 26 */       } catch (NoSuchMethodException noSuchMethodException) {
/*    */       
/* 28 */       } catch (Throwable e) {
/* 29 */         e = Utils.throwableUnwrap(e);
/* 30 */         if (e instanceof RuntimeException) {
/* 31 */           throw (RuntimeException)e;
/*    */         }
/* 33 */         throw new RuntimeException(e);
/*    */       } 
/*    */ 
/*    */       
/* 37 */       if (targetType == null) {
/* 38 */         targetType = targetClz;
/*    */       }
/*    */       
/* 41 */       return (T)ONode.loadObj(props, new Feature[] { Feature.UseSetter }).toObject(targetType);
/*    */     } 
/* 43 */     return (T)ONode.loadObj(props, new Feature[] { Feature.UseSetter }).bindTo(target);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\extend\impl\PropsConverterExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */