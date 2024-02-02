package cn.hutool.core.annotation;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class AnnotationUtil {
   public static CombinationAnnotationElement toCombination(AnnotatedElement annotationEle) {
      return annotationEle instanceof CombinationAnnotationElement ? (CombinationAnnotationElement)annotationEle : new CombinationAnnotationElement(annotationEle);
   }

   public static Annotation[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination) {
      return getAnnotations(annotationEle, isToCombination, (Predicate)null);
   }

   public static <T> T[] getCombinationAnnotations(AnnotatedElement annotationEle, Class<T> annotationType) {
      return getAnnotations(annotationEle, true, annotationType);
   }

   public static <T> T[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination, Class<T> annotationType) {
      Annotation[] annotations = getAnnotations(annotationEle, isToCombination, (annotation) -> {
         return null == annotationType || annotationType.isAssignableFrom(annotation.getClass());
      });
      T[] result = ArrayUtil.newArray(annotationType, annotations.length);

      for(int i = 0; i < annotations.length; ++i) {
         result[i] = annotations[i];
      }

      return result;
   }

   public static Annotation[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination, Predicate<Annotation> predicate) {
      if (null == annotationEle) {
         return null;
      } else if (isToCombination) {
         return null == predicate ? toCombination(annotationEle).getAnnotations() : CombinationAnnotationElement.of(annotationEle, predicate).getAnnotations();
      } else {
         Annotation[] result = annotationEle.getAnnotations();
         if (null == predicate) {
            return result;
         } else {
            predicate.getClass();
            return (Annotation[])ArrayUtil.filter(result, predicate::test);
         }
      }
   }

   public static <A extends Annotation> A getAnnotation(AnnotatedElement annotationEle, Class<A> annotationType) {
      return null == annotationEle ? null : toCombination(annotationEle).getAnnotation(annotationType);
   }

   public static boolean hasAnnotation(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) {
      return null != getAnnotation(annotationEle, annotationType);
   }

   public static <T> T getAnnotationValue(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) throws UtilException {
      return getAnnotationValue(annotationEle, annotationType, "value");
   }

   public static <T> T getAnnotationValue(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType, String propertyName) throws UtilException {
      Annotation annotation = getAnnotation(annotationEle, annotationType);
      if (null == annotation) {
         return null;
      } else {
         Method method = ReflectUtil.getMethodOfObj(annotation, propertyName);
         return null == method ? null : ReflectUtil.invoke(annotation, (Method)method);
      }
   }

   public static Map<String, Object> getAnnotationValueMap(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) throws UtilException {
      Annotation annotation = getAnnotation(annotationEle, annotationType);
      if (null == annotation) {
         return null;
      } else {
         Method[] methods = ReflectUtil.getMethods(annotationType, (t) -> {
            if (!ArrayUtil.isEmpty((Object[])t.getParameterTypes())) {
               return false;
            } else {
               String name = t.getName();
               return !"hashCode".equals(name) && !"toString".equals(name) && !"annotationType".equals(name);
            }
         });
         HashMap<String, Object> result = new HashMap(methods.length, 1.0F);
         Method[] var5 = methods;
         int var6 = methods.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Method method = var5[var7];
            result.put(method.getName(), ReflectUtil.invoke(annotation, (Method)method));
         }

         return result;
      }
   }

   public static RetentionPolicy getRetentionPolicy(Class<? extends Annotation> annotationType) {
      Retention retention = (Retention)annotationType.getAnnotation(Retention.class);
      return null == retention ? RetentionPolicy.CLASS : retention.value();
   }

   public static ElementType[] getTargetType(Class<? extends Annotation> annotationType) {
      Target target = (Target)annotationType.getAnnotation(Target.class);
      return null == target ? new ElementType[]{ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE} : target.value();
   }

   public static boolean isDocumented(Class<? extends Annotation> annotationType) {
      return annotationType.isAnnotationPresent(Documented.class);
   }

   public static boolean isInherited(Class<? extends Annotation> annotationType) {
      return annotationType.isAnnotationPresent(Inherited.class);
   }

   public static void setValue(Annotation annotation, String annotationField, Object value) {
      Map memberValues = (Map)ReflectUtil.getFieldValue(Proxy.getInvocationHandler(annotation), (String)"memberValues");
      memberValues.put(annotationField, value);
   }

   public static <T extends Annotation> T getAnnotationAlias(AnnotatedElement annotationEle, Class<T> annotationType) {
      T annotation = getAnnotation(annotationEle, annotationType);
      return (Annotation)Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, new AnnotationProxy(annotation));
   }
}
