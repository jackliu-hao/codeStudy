package freemarker.core;

import freemarker.template.Template;
import freemarker.template.utility.ObjectFactory;

public interface DirectiveCallPlace {
   Template getTemplate();

   int getBeginColumn();

   int getBeginLine();

   int getEndColumn();

   int getEndLine();

   Object getOrCreateCustomData(Object var1, ObjectFactory var2) throws CallPlaceCustomDataInitializationException;

   boolean isNestedOutputCacheable();
}
