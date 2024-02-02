package org.noear.solon.aspect.integration;

import org.noear.solon.Utils;
import org.noear.solon.aspect.AspectUtil;
import org.noear.solon.aspect.annotation.Dao;
import org.noear.solon.aspect.annotation.Repository;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      context.beanBuilderAdd(Dao.class, (clz, bw, anno) -> {
         String beanName = Utils.annoAlias(anno.value(), anno.name());
         AspectUtil.binding(bw, beanName, anno.typed());
      });
      context.beanBuilderAdd(Service.class, (clz, bw, anno) -> {
         String beanName = Utils.annoAlias(anno.value(), anno.name());
         AspectUtil.binding(bw, beanName, anno.typed());
      });
      context.beanBuilderAdd(Repository.class, (clz, bw, anno) -> {
         String beanName = Utils.annoAlias(anno.value(), anno.name());
         AspectUtil.binding(bw, beanName, anno.typed());
      });
      this.startOld(context);
   }

   private void startOld(AopContext context) {
      context.beanBuilderAdd(org.noear.solon.extend.aspect.annotation.Dao.class, (clz, bw, anno) -> {
         String beanName = Utils.annoAlias(anno.value(), anno.name());
         AspectUtil.binding(bw, beanName, anno.typed());
      });
      context.beanBuilderAdd(org.noear.solon.extend.aspect.annotation.Service.class, (clz, bw, anno) -> {
         String beanName = Utils.annoAlias(anno.value(), anno.name());
         AspectUtil.binding(bw, beanName, anno.typed());
      });
      context.beanBuilderAdd(org.noear.solon.extend.aspect.annotation.Repository.class, (clz, bw, anno) -> {
         String beanName = Utils.annoAlias(anno.value(), anno.name());
         AspectUtil.binding(bw, beanName, anno.typed());
      });
   }
}
