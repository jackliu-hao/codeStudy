package freemarker.core;

public class TemplateXMLOutputModel extends CommonTemplateMarkupOutputModel<TemplateXMLOutputModel> {
   protected TemplateXMLOutputModel(String plainTextContent, String markupContent) {
      super(plainTextContent, markupContent);
   }

   public XMLOutputFormat getOutputFormat() {
      return XMLOutputFormat.INSTANCE;
   }
}
