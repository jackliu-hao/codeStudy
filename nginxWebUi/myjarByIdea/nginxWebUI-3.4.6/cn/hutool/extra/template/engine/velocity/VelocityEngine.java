package cn.hutool.extra.template.engine.velocity;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;

public class VelocityEngine implements TemplateEngine {
   private org.apache.velocity.app.VelocityEngine engine;
   private TemplateConfig config;

   public VelocityEngine() {
   }

   public VelocityEngine(TemplateConfig config) {
      this.init(config);
   }

   public VelocityEngine(org.apache.velocity.app.VelocityEngine engine) {
      this.init(engine);
   }

   public TemplateEngine init(TemplateConfig config) {
      if (null == config) {
         config = TemplateConfig.DEFAULT;
      }

      this.config = config;
      this.init(createEngine(config));
      return this;
   }

   private void init(org.apache.velocity.app.VelocityEngine engine) {
      this.engine = engine;
   }

   public org.apache.velocity.app.VelocityEngine getRawEngine() {
      return this.engine;
   }

   public Template getTemplate(String resource) {
      if (null == this.engine) {
         this.init(TemplateConfig.DEFAULT);
      }

      String charsetStr = null;
      if (null != this.config) {
         String root = this.config.getPath();
         charsetStr = this.config.getCharsetStr();
         TemplateConfig.ResourceMode resourceMode = this.config.getResourceMode();
         if (TemplateConfig.ResourceMode.CLASSPATH == resourceMode || TemplateConfig.ResourceMode.WEB_ROOT == resourceMode) {
            resource = StrUtil.addPrefixIfNot(resource, StrUtil.addSuffixIfNot(root, "/"));
         }
      }

      return VelocityTemplate.wrap(this.engine.getTemplate(resource, charsetStr));
   }

   private static org.apache.velocity.app.VelocityEngine createEngine(TemplateConfig config) {
      if (null == config) {
         config = new TemplateConfig();
      }

      org.apache.velocity.app.VelocityEngine ve = new org.apache.velocity.app.VelocityEngine();
      String charsetStr = config.getCharset().toString();
      ve.setProperty("resource.default_encoding", charsetStr);
      ve.setProperty("resource.loader.file.cache", true);
      switch (config.getResourceMode()) {
         case CLASSPATH:
            ve.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            break;
         case FILE:
            String path = config.getPath();
            if (null != path) {
               ve.setProperty("resource.loader.file.path", path);
            }
            break;
         case WEB_ROOT:
            ve.setProperty("resource.loader", "webapp");
            ve.setProperty("webapp.resource.loader.class", "org.apache.velocity.tools.view.servlet.WebappLoader");
            ve.setProperty("webapp.resource.loader.path", StrUtil.nullToDefault(config.getPath(), "/"));
            break;
         case STRING:
            ve.setProperty("resource.loader", "str");
            ve.setProperty("str.resource.loader.class", SimpleStringResourceLoader.class.getName());
      }

      ve.init();
      return ve;
   }
}
