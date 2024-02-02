package freemarker.core;

abstract class BackwardCompatibleTemplateNumberFormat extends TemplateNumberFormat {
   abstract String format(Number var1) throws UnformattableValueException;
}
