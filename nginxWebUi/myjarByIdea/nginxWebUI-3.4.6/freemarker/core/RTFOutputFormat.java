package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

public class RTFOutputFormat extends CommonMarkupOutputFormat<TemplateRTFOutputModel> {
   public static final RTFOutputFormat INSTANCE = new RTFOutputFormat();

   protected RTFOutputFormat() {
   }

   public String getName() {
      return "RTF";
   }

   public String getMimeType() {
      return "application/rtf";
   }

   public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
      StringUtil.RTFEnc(textToEsc, out);
   }

   public String escapePlainText(String plainTextContent) {
      return StringUtil.RTFEnc(plainTextContent);
   }

   public boolean isLegacyBuiltInBypassed(String builtInName) {
      return builtInName.equals("rtf");
   }

   protected TemplateRTFOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
      return new TemplateRTFOutputModel(plainTextContent, markupContent);
   }
}
