package freemarker.core;

import freemarker.template.Template;

abstract class TemplatePostProcessor {
   public abstract void postProcess(Template var1) throws TemplatePostProcessorException;
}
