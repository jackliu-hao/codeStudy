package freemarker.core;

import freemarker.template.TemplateModel;

public interface TemplateMarkupOutputModel<MO extends TemplateMarkupOutputModel<MO>> extends TemplateModel {
  MarkupOutputFormat<MO> getOutputFormat();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateMarkupOutputModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */