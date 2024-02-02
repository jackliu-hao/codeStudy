package freemarker.cache;

import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import java.io.IOException;

public abstract class TemplateConfigurationFactory {
   private Configuration cfg;

   public abstract TemplateConfiguration get(String var1, Object var2) throws IOException, TemplateConfigurationFactoryException;

   public final void setConfiguration(Configuration cfg) {
      if (this.cfg != null) {
         if (cfg != this.cfg) {
            throw new IllegalStateException("The TemplateConfigurationFactory is already bound to another Configuration");
         }
      } else {
         this.cfg = cfg;
         this.setConfigurationOfChildren(cfg);
      }
   }

   public Configuration getConfiguration() {
      return this.cfg;
   }

   protected abstract void setConfigurationOfChildren(Configuration var1);
}
