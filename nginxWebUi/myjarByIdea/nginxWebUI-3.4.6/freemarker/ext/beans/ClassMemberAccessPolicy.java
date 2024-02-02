package freemarker.ext.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassMemberAccessPolicy {
   boolean isMethodExposed(Method var1);

   boolean isConstructorExposed(Constructor<?> var1);

   boolean isFieldExposed(Field var1);
}
