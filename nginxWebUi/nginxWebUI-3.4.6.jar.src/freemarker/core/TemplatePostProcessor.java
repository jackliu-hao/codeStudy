package freemarker.core;

import freemarker.template.Template;

abstract class TemplatePostProcessor {
  public abstract void postProcess(Template paramTemplate) throws TemplatePostProcessorException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplatePostProcessor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */