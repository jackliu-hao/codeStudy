package freemarker.ext.util;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;

public interface ModelFactory {
   TemplateModel create(Object var1, ObjectWrapper var2);
}
