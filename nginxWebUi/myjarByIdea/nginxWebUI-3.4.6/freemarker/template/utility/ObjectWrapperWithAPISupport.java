package freemarker.template.utility;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public interface ObjectWrapperWithAPISupport extends ObjectWrapper {
   TemplateHashModel wrapAsAPI(Object var1) throws TemplateModelException;
}
