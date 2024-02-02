package freemarker.ext.util;

import freemarker.template.TemplateModel;

public interface WrapperTemplateModel extends TemplateModel {
   Object getWrappedObject();
}
