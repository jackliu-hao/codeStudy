package cn.hutool.extra.template.engine.freemarker;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import java.io.IOException;

public class FreemarkerEngine implements TemplateEngine {
   private Configuration cfg;

   public FreemarkerEngine() {
   }

   public FreemarkerEngine(TemplateConfig config) {
      this.init(config);
   }

   public FreemarkerEngine(Configuration freemarkerCfg) {
      this.init(freemarkerCfg);
   }

   public TemplateEngine init(TemplateConfig config) {
      if (null == config) {
         config = TemplateConfig.DEFAULT;
      }

      this.init(createCfg(config));
      return this;
   }

   private void init(Configuration freemarkerCfg) {
      this.cfg = freemarkerCfg;
   }

   public Template getTemplate(String resource) {
      if (null == this.cfg) {
         this.init(TemplateConfig.DEFAULT);
      }

      try {
         return FreemarkerTemplate.wrap(this.cfg.getTemplate(resource));
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      } catch (Exception var4) {
         throw new TemplateException(var4);
      }
   }

   private static Configuration createCfg(TemplateConfig config) {
      if (null == config) {
         config = new TemplateConfig();
      }

      Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
      cfg.setLocalizedLookup(false);
      cfg.setDefaultEncoding(config.getCharset().toString());
      switch (config.getResourceMode()) {
         case CLASSPATH:
            cfg.setTemplateLoader(new ClassTemplateLoader(ClassUtil.getClassLoader(), config.getPath()));
            break;
         case FILE:
            try {
               cfg.setTemplateLoader(new FileTemplateLoader(FileUtil.file(config.getPath())));
               break;
            } catch (IOException var4) {
               throw new IORuntimeException(var4);
            }
         case WEB_ROOT:
            try {
               cfg.setTemplateLoader(new FileTemplateLoader(FileUtil.file(FileUtil.getWebRoot(), config.getPath())));
               break;
            } catch (IOException var3) {
               throw new IORuntimeException(var3);
            }
         case STRING:
            cfg.setTemplateLoader(new SimpleStringTemplateLoader());
      }

      return cfg;
   }
}
