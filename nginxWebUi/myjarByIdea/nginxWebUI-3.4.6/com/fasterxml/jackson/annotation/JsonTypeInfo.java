package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonTypeInfo {
   Id use();

   As include() default JsonTypeInfo.As.PROPERTY;

   String property() default "";

   Class<?> defaultImpl() default JsonTypeInfo.class;

   boolean visible() default false;

   /** @deprecated */
   @Deprecated
   public abstract static class None {
   }

   public static enum As {
      PROPERTY,
      WRAPPER_OBJECT,
      WRAPPER_ARRAY,
      EXTERNAL_PROPERTY,
      EXISTING_PROPERTY;
   }

   public static enum Id {
      NONE((String)null),
      CLASS("@class"),
      MINIMAL_CLASS("@c"),
      NAME("@type"),
      DEDUCTION((String)null),
      CUSTOM((String)null);

      private final String _defaultPropertyName;

      private Id(String defProp) {
         this._defaultPropertyName = defProp;
      }

      public String getDefaultPropertyName() {
         return this._defaultPropertyName;
      }
   }
}
