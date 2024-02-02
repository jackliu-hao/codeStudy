package freemarker.cache;

import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import java.io.IOException;

public class ConditionalTemplateConfigurationFactory extends TemplateConfigurationFactory {
   private final TemplateSourceMatcher matcher;
   private final TemplateConfiguration templateConfiguration;
   private final TemplateConfigurationFactory templateConfigurationFactory;

   public ConditionalTemplateConfigurationFactory(TemplateSourceMatcher matcher, TemplateConfigurationFactory templateConfigurationFactory) {
      this.matcher = matcher;
      this.templateConfiguration = null;
      this.templateConfigurationFactory = templateConfigurationFactory;
   }

   public ConditionalTemplateConfigurationFactory(TemplateSourceMatcher matcher, TemplateConfiguration templateConfiguration) {
      this.matcher = matcher;
      this.templateConfiguration = templateConfiguration;
      this.templateConfigurationFactory = null;
   }

   public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
      if (this.matcher.matches(sourceName, templateSource)) {
         return this.templateConfigurationFactory != null ? this.templateConfigurationFactory.get(sourceName, templateSource) : this.templateConfiguration;
      } else {
         return null;
      }
   }

   protected void setConfigurationOfChildren(Configuration cfg) {
      if (this.templateConfiguration != null) {
         this.templateConfiguration.setParentConfiguration(cfg);
      }

      if (this.templateConfigurationFactory != null) {
         this.templateConfigurationFactory.setConfiguration(cfg);
      }

   }
}
