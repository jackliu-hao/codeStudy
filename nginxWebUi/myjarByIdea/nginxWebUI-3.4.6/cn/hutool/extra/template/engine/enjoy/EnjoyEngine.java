package cn.hutool.extra.template.engine.enjoy;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import com.jfinal.template.Engine;
import com.jfinal.template.source.FileSourceFactory;
import java.io.File;

public class EnjoyEngine implements TemplateEngine {
   private Engine engine;
   private TemplateConfig.ResourceMode resourceMode;

   public EnjoyEngine() {
   }

   public EnjoyEngine(TemplateConfig config) {
      this.init(config);
   }

   public EnjoyEngine(Engine engine) {
      this.init(engine);
   }

   public TemplateEngine init(TemplateConfig config) {
      if (null == config) {
         config = TemplateConfig.DEFAULT;
      }

      this.resourceMode = config.getResourceMode();
      this.init(createEngine(config));
      return this;
   }

   private void init(Engine engine) {
      this.engine = engine;
   }

   public Template getTemplate(String resource) {
      if (null == this.engine) {
         this.init(TemplateConfig.DEFAULT);
      }

      return ObjectUtil.equal(TemplateConfig.ResourceMode.STRING, this.resourceMode) ? EnjoyTemplate.wrap(this.engine.getTemplateByString(resource)) : EnjoyTemplate.wrap(this.engine.getTemplate(resource));
   }

   private static Engine createEngine(TemplateConfig config) {
      Engine engine = Engine.create("Hutool-Enjoy-Engine-" + IdUtil.fastSimpleUUID());
      engine.setEncoding(config.getCharsetStr());
      switch (config.getResourceMode()) {
         case STRING:
         default:
            break;
         case CLASSPATH:
            engine.setToClassPathSourceFactory();
            engine.setBaseTemplatePath(config.getPath());
            break;
         case FILE:
            engine.setSourceFactory(new FileSourceFactory());
            engine.setBaseTemplatePath(config.getPath());
            break;
         case WEB_ROOT:
            engine.setSourceFactory(new FileSourceFactory());
            File root = FileUtil.file(FileUtil.getWebRoot(), config.getPath());
            engine.setBaseTemplatePath(FileUtil.getAbsolutePath(root));
      }

      return engine;
   }
}
