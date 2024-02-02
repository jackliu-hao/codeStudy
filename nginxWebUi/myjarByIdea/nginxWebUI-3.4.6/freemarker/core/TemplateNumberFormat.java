package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

public abstract class TemplateNumberFormat extends TemplateValueFormat {
   public abstract String formatToPlainText(TemplateNumberModel var1) throws TemplateValueFormatException, TemplateModelException;

   public Object format(TemplateNumberModel numberModel) throws TemplateValueFormatException, TemplateModelException {
      return this.formatToPlainText(numberModel);
   }

   public abstract boolean isLocaleBound();

   public final Object parse(String s) throws TemplateValueFormatException {
      throw new ParsingNotSupportedException("Number formats currenly don't support parsing");
   }
}
