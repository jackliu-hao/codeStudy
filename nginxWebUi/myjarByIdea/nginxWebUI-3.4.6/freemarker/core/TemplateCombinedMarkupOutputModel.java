package freemarker.core;

public final class TemplateCombinedMarkupOutputModel extends CommonTemplateMarkupOutputModel<TemplateCombinedMarkupOutputModel> {
   private final CombinedMarkupOutputFormat outputFormat;

   TemplateCombinedMarkupOutputModel(String plainTextContent, String markupContent, CombinedMarkupOutputFormat outputFormat) {
      super(plainTextContent, markupContent);
      this.outputFormat = outputFormat;
   }

   public CombinedMarkupOutputFormat getOutputFormat() {
      return this.outputFormat;
   }
}
