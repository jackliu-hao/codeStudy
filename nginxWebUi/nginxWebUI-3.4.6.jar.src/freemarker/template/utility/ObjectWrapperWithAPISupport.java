package freemarker.template.utility;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public interface ObjectWrapperWithAPISupport extends ObjectWrapper {
  TemplateHashModel wrapAsAPI(Object paramObject) throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\ObjectWrapperWithAPISupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */