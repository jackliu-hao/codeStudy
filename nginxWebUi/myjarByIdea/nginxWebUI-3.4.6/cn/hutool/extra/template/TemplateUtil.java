package cn.hutool.extra.template;

import cn.hutool.extra.template.engine.TemplateFactory;

public class TemplateUtil {
   public static TemplateEngine createEngine() {
      return TemplateFactory.create();
   }

   public static TemplateEngine createEngine(TemplateConfig config) {
      return TemplateFactory.create(config);
   }
}
