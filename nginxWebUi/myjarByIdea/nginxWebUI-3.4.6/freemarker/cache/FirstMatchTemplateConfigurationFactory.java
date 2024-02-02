package freemarker.cache;

import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import freemarker.template.utility.StringUtil;
import java.io.IOException;

public class FirstMatchTemplateConfigurationFactory extends TemplateConfigurationFactory {
   private final TemplateConfigurationFactory[] templateConfigurationFactories;
   private boolean allowNoMatch;
   private String noMatchErrorDetails;

   public FirstMatchTemplateConfigurationFactory(TemplateConfigurationFactory... templateConfigurationFactories) {
      this.templateConfigurationFactories = templateConfigurationFactories;
   }

   public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
      TemplateConfigurationFactory[] var3 = this.templateConfigurationFactories;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TemplateConfigurationFactory tcf = var3[var5];
         TemplateConfiguration tc = tcf.get(sourceName, templateSource);
         if (tc != null) {
            return tc;
         }
      }

      if (!this.allowNoMatch) {
         throw new TemplateConfigurationFactoryException(FirstMatchTemplateConfigurationFactory.class.getSimpleName() + " has found no matching choice for source name " + StringUtil.jQuote(sourceName) + ". " + (this.noMatchErrorDetails != null ? "Error details: " + this.noMatchErrorDetails : "(Set the noMatchErrorDetails property of the factory bean to give a more specific error message. Set allowNoMatch to true if this shouldn't be an error.)"));
      } else {
         return null;
      }
   }

   public boolean getAllowNoMatch() {
      return this.allowNoMatch;
   }

   public void setAllowNoMatch(boolean allowNoMatch) {
      this.allowNoMatch = allowNoMatch;
   }

   public String getNoMatchErrorDetails() {
      return this.noMatchErrorDetails;
   }

   public void setNoMatchErrorDetails(String noMatchErrorDetails) {
      this.noMatchErrorDetails = noMatchErrorDetails;
   }

   public FirstMatchTemplateConfigurationFactory allowNoMatch(boolean allow) {
      this.setAllowNoMatch(allow);
      return this;
   }

   public FirstMatchTemplateConfigurationFactory noMatchErrorDetails(String message) {
      this.setNoMatchErrorDetails(message);
      return this;
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
