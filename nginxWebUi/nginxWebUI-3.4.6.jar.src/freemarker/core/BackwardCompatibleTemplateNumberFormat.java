package freemarker.core;

abstract class BackwardCompatibleTemplateNumberFormat extends TemplateNumberFormat {
  abstract String format(Number paramNumber) throws UnformattableValueException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BackwardCompatibleTemplateNumberFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */