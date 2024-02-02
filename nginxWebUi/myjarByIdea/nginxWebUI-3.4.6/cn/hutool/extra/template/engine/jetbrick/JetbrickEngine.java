package cn.hutool.extra.template.engine.jetbrick;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import java.util.Properties;
import jetbrick.template.JetEngine;

public class JetbrickEngine implements TemplateEngine {
   private JetEngine engine;

   public JetbrickEngine() {
   }

   public JetbrickEngine(TemplateConfig config) {
      this.init(config);
   }

   public JetbrickEngine(JetEngine engine) {
      this.init(engine);
   }

   public TemplateEngine init(TemplateConfig config) {
      this.init(createEngine(config));
      return this;
   }

   private void init(JetEngine engine) {
      this.engine = engine;
   }

   public Template getTemplate(String resource) {
      if (null == this.engine) {
         this.init(TemplateConfig.DEFAULT);
      }

      return JetbrickTemplate.wrap(this.engine.getTemplate(resource));
   }

   private static JetEngine createEngine(TemplateConfig config) {
      if (null == config) {
         config = TemplateConfig.DEFAULT;
      }

      Properties props = new Properties();
      props.setProperty("jetx.input.encoding", config.getCharsetStr());
      props.setProperty("jetx.output.encoding", config.getCharsetStr());
      props.setProperty("jetx.template.loaders", "$loader");
      switch (config.getResourceMode()) {
         case CLASSPATH:
            props.setProperty("$loader", "jetbrick.template.loader.ClasspathResourceLoader");
            props.setProperty("$loader.root", config.getPath());
            break;
         case FILE:
            props.setProperty("$loader", "jetbrick.template.loader.FileSystemResourceLoader");
            props.setProperty("$loader.root", config.getPath());
            break;
         case WEB_ROOT:
            props.setProperty("$loader", "jetbrick.template.loader.ServletResourceLoader");
            props.setProperty("$loader.root", config.getPath());
            break;
         case STRING:
            props.setProperty("$loader", "cn.hutool.extra.template.engine.jetbrick.loader.StringResourceLoader");
            props.setProperty("$loader.charset", config.getCharsetStr());
            break;
         default:
            return JetEngine.create();
      }

      return JetEngine.create(props);
   }
}
