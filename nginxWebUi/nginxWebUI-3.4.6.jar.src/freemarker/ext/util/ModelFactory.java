package freemarker.ext.util;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;

public interface ModelFactory {
  TemplateModel create(Object paramObject, ObjectWrapper paramObjectWrapper);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ex\\util\ModelFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */