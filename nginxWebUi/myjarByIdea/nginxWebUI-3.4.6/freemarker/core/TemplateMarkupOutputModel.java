package freemarker.core;

import freemarker.template.TemplateModel;

public interface TemplateMarkupOutputModel<MO extends TemplateMarkupOutputModel<MO>> extends TemplateModel {
   MarkupOutputFormat<MO> getOutputFormat();
}
