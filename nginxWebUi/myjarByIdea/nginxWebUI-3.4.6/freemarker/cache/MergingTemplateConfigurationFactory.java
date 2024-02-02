package freemarker.cache;

import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import java.io.IOException;

public class MergingTemplateConfigurationFactory extends TemplateConfigurationFactory {
   private final TemplateConfigurationFactory[] templateConfigurationFactories;

   public MergingTemplateConfigurationFactory(TemplateConfigurationFactory... templateConfigurationFactories) {
      this.templateConfigurationFactories = templateConfigurationFactories;
   }

   public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
      TemplateConfiguration mergedTC = null;
      TemplateConfiguration resultTC = null;
      TemplateConfigurationFactory[] var5 = this.templateConfigurationFactories;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         TemplateConfigurationFactory tcf = var5[var7];
         TemplateConfiguration tc = tcf.get(sourceName, templateSource);
         if (tc != null) {
            if (resultTC == null) {
               resultTC = tc;
            } else {
               if (mergedTC == null) {
                  Configuration cfg = this.getConfiguration();
                  if (cfg == null) {
                     throw new IllegalStateException("The TemplateConfigurationFactory wasn't associated to a Configuration yet.");
                  }

                  mergedTC = new TemplateConfiguration();
                  mergedTC.setParentConfiguration(cfg);
                  mergedTC.merge(resultTC);
                  resultTC = mergedTC;
               }

               mergedTC.merge(tc);
            }
         }
      }

      return resultTC;
   }

   protected void setConfigurationOfChildren(Configuration cfg) {
      TemplateConfigurationFactory[] var2 = this.templateConfigurationFactories;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TemplateConfigurationFactory templateConfigurationFactory = var2[var4];
         templateConfigurationFactory.setConfiguration(cfg);
      }

   }
}
