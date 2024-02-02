/*    */ package org.noear.solon.aspect.integration;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.aspect.AspectUtil;
/*    */ import org.noear.solon.aspect.annotation.Dao;
/*    */ import org.noear.solon.aspect.annotation.Repository;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.extend.aspect.annotation.Dao;
/*    */ import org.noear.solon.extend.aspect.annotation.Repository;
/*    */ import org.noear.solon.extend.aspect.annotation.Service;
/*    */ 
/*    */ public class XPluginImp implements Plugin {
/*    */   public void start(AopContext context) {
/* 14 */     context.beanBuilderAdd(Dao.class, (clz, bw, anno) -> {
/*    */           String beanName = Utils.annoAlias(anno.value(), anno.name());
/*    */           
/*    */           AspectUtil.binding(bw, beanName, anno.typed());
/*    */         });
/*    */     
/* 20 */     context.beanBuilderAdd(Service.class, (clz, bw, anno) -> {
/*    */           String beanName = Utils.annoAlias(anno.value(), anno.name());
/*    */           
/*    */           AspectUtil.binding(bw, beanName, anno.typed());
/*    */         });
/*    */     
/* 26 */     context.beanBuilderAdd(Repository.class, (clz, bw, anno) -> {
/*    */           String beanName = Utils.annoAlias(anno.value(), anno.name());
/*    */ 
/*    */           
/*    */           AspectUtil.binding(bw, beanName, anno.typed());
/*    */         });
/*    */     
/* 33 */     startOld(context);
/*    */   }
/*    */   
/*    */   private void startOld(AopContext context) {
/* 37 */     context.beanBuilderAdd(Dao.class, (clz, bw, anno) -> {
/*    */           String beanName = Utils.annoAlias(anno.value(), anno.name());
/*    */           
/*    */           AspectUtil.binding(bw, beanName, anno.typed());
/*    */         });
/*    */     
/* 43 */     context.beanBuilderAdd(Service.class, (clz, bw, anno) -> {
/*    */           String beanName = Utils.annoAlias(anno.value(), anno.name());
/*    */           
/*    */           AspectUtil.binding(bw, beanName, anno.typed());
/*    */         });
/*    */     
/* 49 */     context.beanBuilderAdd(Repository.class, (clz, bw, anno) -> {
/*    */           String beanName = Utils.annoAlias(anno.value(), anno.name());
/*    */           AspectUtil.binding(bw, beanName, anno.typed());
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\aspect\integration\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */