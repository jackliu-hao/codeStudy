package freemarker.core;

import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

public final class CombinedMarkupOutputFormat extends CommonMarkupOutputFormat<TemplateCombinedMarkupOutputModel> {
   private final String name;
   private final MarkupOutputFormat outer;
   private final MarkupOutputFormat inner;

   public CombinedMarkupOutputFormat(MarkupOutputFormat outer, MarkupOutputFormat inner) {
      this((String)null, outer, inner);
   }

   public CombinedMarkupOutputFormat(String name, MarkupOutputFormat outer, MarkupOutputFormat inner) {
      this.name = name != null ? null : outer.getName() + "{" + inner.getName() + "}";
      this.outer = outer;
      this.inner = inner;
   }

   public String getName() {
      return this.name;
   }

   public String getMimeType() {
      return this.outer.getMimeType();
   }

   public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
      this.outer.output(this.inner.escapePlainText(textToEsc), out);
   }

   public String escapePlainText(String plainTextContent) throws TemplateModelException {
      return this.outer.escapePlainText(this.inner.escapePlainText(plainTextContent));
   }

   public boolean isLegacyBuiltInBypassed(String builtInName) throws TemplateModelException {
      return this.outer.isLegacyBuiltInBypassed(builtInName);
   }

   public boolean isAutoEscapedByDefault() {
      return this.outer.isAutoEscapedByDefault();
   }

   public boolean isOutputFormatMixingAllowed() {
      return this.outer.isOutputFormatMixingAllowed();
   }

   public MarkupOutputFormat getOuterOutputFormat() {
      return this.outer;
   }

   public MarkupOutputFormat getInnerOutputFormat() {
      return this.inner;
   }

   protected TemplateCombinedMarkupOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
      return new TemplateCombinedMarkupOutputModel(plainTextContent, markupContent, this);
   }
}
