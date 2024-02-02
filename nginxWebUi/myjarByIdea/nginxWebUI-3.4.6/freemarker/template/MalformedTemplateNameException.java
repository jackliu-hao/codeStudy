package freemarker.template;

import freemarker.template.utility.StringUtil;
import java.io.IOException;

public class MalformedTemplateNameException extends IOException {
   private final String templateName;
   private final String malformednessDescription;

   public MalformedTemplateNameException(String templateName, String malformednessDescription) {
      super("Malformed template name, " + StringUtil.jQuote(templateName) + ": " + malformednessDescription);
      this.templateName = templateName;
      this.malformednessDescription = malformednessDescription;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getMalformednessDescription() {
      return this.malformednessDescription;
   }
}
