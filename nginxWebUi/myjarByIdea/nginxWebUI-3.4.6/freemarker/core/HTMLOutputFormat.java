package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

public class HTMLOutputFormat extends CommonMarkupOutputFormat<TemplateHTMLOutputModel> {
   public static final HTMLOutputFormat INSTANCE = new HTMLOutputFormat();

   protected HTMLOutputFormat() {
   }

   public String getName() {
      return "HTML";
   }

   public String getMimeType() {
      return "text/html";
   }

   public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
      StringUtil.XHTMLEnc(textToEsc, out);
   }

   public String escapePlainText(String plainTextContent) {
      return StringUtil.XHTMLEnc(plainTextContent);
   }

   public boolean isLegacyBuiltInBypassed(String builtInName) {
      return builtInName.equals("html") || builtInName.equals("xml") || builtInName.equals("xhtml");
   }

   protected TemplateHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
      return new TemplateHTMLOutputModel(plainTextContent, markupContent);
   }
}
