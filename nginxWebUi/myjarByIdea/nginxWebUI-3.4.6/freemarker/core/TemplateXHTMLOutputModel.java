package freemarker.core;

public class TemplateXHTMLOutputModel extends TemplateXMLOutputModel {
   protected TemplateXHTMLOutputModel(String plainTextContent, String markupContent) {
      super(plainTextContent, markupContent);
   }

   public XHTMLOutputFormat getOutputFormat() {
      return XHTMLOutputFormat.INSTANCE;
   }
}
