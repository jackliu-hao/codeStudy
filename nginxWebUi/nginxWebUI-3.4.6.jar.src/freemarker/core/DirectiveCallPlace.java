package freemarker.core;

import freemarker.template.Template;
import freemarker.template.utility.ObjectFactory;

public interface DirectiveCallPlace {
  Template getTemplate();
  
  int getBeginColumn();
  
  int getBeginLine();
  
  int getEndColumn();
  
  int getEndLine();
  
  Object getOrCreateCustomData(Object paramObject, ObjectFactory paramObjectFactory) throws CallPlaceCustomDataInitializationException;
  
  boolean isNestedOutputCacheable();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\DirectiveCallPlace.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */