package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

public class XHTMLOutputFormat extends XMLOutputFormat {
   public static final XHTMLOutputFormat INSTANCE = new XHTMLOutputFormat();

   protected XHTMLOutputFormat() {
   }

   public String getName() {
      return "XHTML";
   }

   public String getMimeType() {
      return "application/xhtml+xml";
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

   protected TemplateXHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
      return new TemplateXHTMLOutputModel(plainTextContent, markupContent);
   }
}
