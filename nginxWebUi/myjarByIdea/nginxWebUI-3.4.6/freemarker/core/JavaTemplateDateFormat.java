package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class JavaTemplateDateFormat extends TemplateDateFormat {
   private final DateFormat javaDateFormat;

   public JavaTemplateDateFormat(DateFormat javaDateFormat) {
      this.javaDateFormat = javaDateFormat;
   }

   public String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
      return this.javaDateFormat.format(TemplateFormatUtil.getNonNullDate(dateModel));
   }

   public Date parse(String s, int dateType) throws UnparsableValueException {
      try {
         return this.javaDateFormat.parse(s);
      } catch (java.text.ParseException var4) {
         throw new UnparsableValueException(var4.getMessage(), var4);
      }
   }

   public String getDescription() {
      return this.javaDateFormat instanceof SimpleDateFormat ? ((SimpleDateFormat)this.javaDateFormat).toPattern() : this.javaDateFormat.toString();
   }

   public boolean isLocaleBound() {
      return true;
   }

   public boolean isTimeZoneBound() {
      return true;
   }
}
