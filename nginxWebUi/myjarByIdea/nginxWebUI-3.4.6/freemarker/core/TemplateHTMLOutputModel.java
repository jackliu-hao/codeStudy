package freemarker.core;

public class TemplateHTMLOutputModel extends CommonTemplateMarkupOutputModel<TemplateHTMLOutputModel> {
   protected TemplateHTMLOutputModel(String plainTextContent, String markupContent) {
      super(plainTextContent, markupContent);
   }

   public HTMLOutputFormat getOutputFormat() {
      return HTMLOutputFormat.INSTANCE;
   }
}
