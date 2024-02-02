package freemarker.ext.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassMemberAccessPolicy {
  boolean isMethodExposed(Method paramMethod);
  
  boolean isConstructorExposed(Constructor<?> paramConstructor);
  
  boolean isFieldExposed(Field paramField);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ClassMemberAccessPolicy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */