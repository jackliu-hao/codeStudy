package cn.hutool.core.annotation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.TableMap;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class CombinationAnnotationElement implements AnnotatedElement, Serializable {
   private static final long serialVersionUID = 1L;
   private static final Set<Class<? extends Annotation>> META_ANNOTATIONS = CollUtil.newHashSet((Object[])(Target.class, Retention.class, Inherited.class, Documented.class, SuppressWarnings.class, Override.class, Deprecated.class));
   private Map<Class<? extends Annotation>, Annotation> annotationMap;
   private Map<Class<? extends Annotation>, Annotation> declaredAnnotationMap;
   private final Predicate<Annotation> predicate;

   public static CombinationAnnotationElement of(AnnotatedElement element, Predicate<Annotation> predicate) {
      return new CombinationAnnotationElement(element, predicate);
   }

   public CombinationAnnotationElement(AnnotatedElement element) {
      this(element, (Predicate)null);
   }

   public CombinationAnnotationElement(AnnotatedElement element, Predicate<Annotation> predicate) {
      this.predicate = predicate;
      this.init(element);
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
      return this.annotationMap.containsKey(annotationClass);
   }

   public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      Annotation annotation = (Annotation)this.annotationMap.get(annotationClass);
      return annotation == null ? null : annotation;
   }

   public Annotation[] getAnnotations() {
      Collection<Annotation> annotations = this.annotationMap.values();
      return (Annotation[])annotations.toArray(new Annotation[0]);
   }

   public Annotation[] getDeclaredAnnotations() {
      Collection<Annotation> annotations = this.declaredAnnotationMap.values();
      return (Annotation[])annotations.toArray(new Annotation[0]);
   }

   private void init(AnnotatedElement element) {
      Annotation[] declaredAnnotations = element.getDeclaredAnnotations();
      this.declaredAnnotationMap = new TableMap();
      this.parseDeclared(declaredAnnotations);
      Annotation[] annotations = element.getAnnotations();
      if (Arrays.equals(declaredAnnotations, annotations)) {
         this.annotationMap = this.declaredAnnotationMap;
      } else {
         this.annotationMap = new TableMap();
         this.parse(annotations);
      }

   }

   private void parseDeclared(Annotation[] annotations) {
      Annotation[] var3 = annotations;
      int var4 = annotations.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Annotation annotation = var3[var5];
         Class<? extends Annotation> annotationType = annotation.annotationType();
         if (!META_ANNOTATIONS.contains(annotationType)) {
            if (this.test(annotation)) {
               this.declaredAnnotationMap.put(annotationType, annotation);
            }

            this.parseDeclared(annotationType.getDeclaredAnnotations());
         }
      }

   }

   private void parse(Annotation[] annotations) {
      Annotation[] var3 = annotations;
      int var4 = annotations.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Annotation annotation = var3[var5];
         Class<? extends Annotation> annotationType = annotation.annotationType();
         if (!META_ANNOTATIONS.contains(annotationType)) {
            if (this.test(annotation)) {
               this.annotationMap.put(annotationType, annotation);
            }

            this.parse(annotationType.getAnnotations());
         }
      }

   }

   private boolean test(Annotation annotation) {
      return null == this.predicate || this.predicate.test(annotation);
   }
}
