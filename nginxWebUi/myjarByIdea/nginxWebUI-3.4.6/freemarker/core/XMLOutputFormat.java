package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

public class XMLOutputFormat extends CommonMarkupOutputFormat<TemplateXMLOutputModel> {
   public static final XMLOutputFormat INSTANCE = new XMLOutputFormat();

   protected XMLOutputFormat() {
   }

   public String getName() {
      return "XML";
   }

   public String getMimeType() {
      return "application/xml";
   }

   public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
      StringUtil.XMLEnc(textToEsc, out);
   }

   public String escapePlainText(String plainTextContent) {
      return StringUtil.XMLEnc(plainTextContent);
   }

   public boolean isLegacyBuiltInBypassed(String builtInName) {
      return builtInName.equals("xml");
   }

   protected TemplateXMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
      return new TemplateXMLOutputModel(plainTextContent, markupContent);
   }
}
