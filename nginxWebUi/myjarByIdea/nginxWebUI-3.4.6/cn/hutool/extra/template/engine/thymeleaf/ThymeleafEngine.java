package cn.hutool.extra.template.engine.thymeleaf;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.DefaultTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class ThymeleafEngine implements TemplateEngine {
   org.thymeleaf.TemplateEngine engine;
   TemplateConfig config;

   public ThymeleafEngine() {
   }

   public ThymeleafEngine(TemplateConfig config) {
      this.init(config);
   }

   public ThymeleafEngine(org.thymeleaf.TemplateEngine engine) {
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

   private void init(org.thymeleaf.TemplateEngine engine) {
      this.engine = engine;
   }

   public Template getTemplate(String resource) {
      if (null == this.engine) {
         this.init(TemplateConfig.DEFAULT);
      }

      return ThymeleafTemplate.wrap(this.engine, resource, null == this.config ? null : this.config.getCharset());
   }

   private static org.thymeleaf.TemplateEngine createEngine(TemplateConfig config) {
      if (null == config) {
         config = new TemplateConfig();
      }

      Object resolver;
      switch (config.getResourceMode()) {
         case CLASSPATH:
            ClassLoaderTemplateResolver classLoaderResolver = new ClassLoaderTemplateResolver();
            classLoaderResolver.setCharacterEncoding(config.getCharsetStr());
            classLoaderResolver.setTemplateMode(TemplateMode.HTML);
            classLoaderResolver.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), "/"));
            resolver = classLoaderResolver;
            break;
         case FILE:
            FileTemplateResolver fileResolver = new FileTemplateResolver();
            fileResolver.setCharacterEncoding(config.getCharsetStr());
            fileResolver.setTemplateMode(TemplateMode.HTML);
            fileResolver.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), "/"));
            resolver = fileResolver;
            break;
         case WEB_ROOT:
            FileTemplateResolver webRootResolver = new FileTemplateResolver();
            webRootResolver.setCharacterEncoding(config.getCharsetStr());
            webRootResolver.setTemplateMode(TemplateMode.HTML);
            webRootResolver.setPrefix(StrUtil.addSuffixIfNot(FileUtil.getAbsolutePath(FileUtil.file(FileUtil.getWebRoot(), config.getPath())), "/"));
            resolver = webRootResolver;
            break;
         case STRING:
            resolver = new StringTemplateResolver();
            break;
         default:
            resolver = new DefaultTemplateResolver();
      }

      org.thymeleaf.TemplateEngine engine = new org.thymeleaf.TemplateEngine();
      engine.setTemplateResolver((ITemplateResolver)resolver);
      return engine;
   }
}
