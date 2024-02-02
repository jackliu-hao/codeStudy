package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import java.text.NumberFormat;

final class JavaTemplateNumberFormat extends BackwardCompatibleTemplateNumberFormat {
   private final String formatString;
   private final NumberFormat javaNumberFormat;

   public JavaTemplateNumberFormat(NumberFormat javaNumberFormat, String formatString) {
      this.formatString = formatString;
      this.javaNumberFormat = javaNumberFormat;
   }

   public String formatToPlainText(TemplateNumberModel numberModel) throws UnformattableValueException, TemplateModelException {
      Number number = TemplateFormatUtil.getNonNullNumber(numberModel);
      return this.format(number);
   }

   public boolean isLocaleBound() {
      return true;
   }

   String format(Number number) throws UnformattableValueException {
      try {
         return this.javaNumberFormat.format(number);
      } catch (ArithmeticException var3) {
         throw new UnformattableValueException("This format can't format the " + number + " number. Reason: " + var3.getMessage(), var3);
      }
   }

   public NumberFormat getJavaNumberFormat() {
      return this.javaNumberFormat;
   }

   public String getDescription() {
      return this.formatString;
   }
}
