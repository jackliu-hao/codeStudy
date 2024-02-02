package freemarker.core;

public class TemplateRTFOutputModel extends CommonTemplateMarkupOutputModel<TemplateRTFOutputModel> {
   protected TemplateRTFOutputModel(String plainTextContent, String markupContent) {
      super(plainTextContent, markupContent);
   }

   public RTFOutputFormat getOutputFormat() {
      return RTFOutputFormat.INSTANCE;
   }
}
