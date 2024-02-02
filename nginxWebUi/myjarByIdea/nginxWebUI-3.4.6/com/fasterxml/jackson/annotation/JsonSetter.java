package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSetter {
   String value() default "";

   Nulls nulls() default Nulls.DEFAULT;

   Nulls contentNulls() default Nulls.DEFAULT;

   public static class Value implements JacksonAnnotationValue<JsonSetter>, Serializable {
      private static final long serialVersionUID = 1L;
      private final Nulls _nulls;
      private final Nulls _contentNulls;
      protected static final Value EMPTY;

      protected Value(Nulls nulls, Nulls contentNulls) {
         this._nulls = nulls;
         this._contentNulls = contentNulls;
      }

      public Class<JsonSetter> valueFor() {
         return JsonSetter.class;
      }

      protected Object readResolve() {
         return _empty(this._nulls, this._contentNulls) ? EMPTY : this;
      }

      public static Value from(JsonSetter src) {
         return src == null ? EMPTY : construct(src.nulls(), src.contentNulls());
      }

      public static Value construct(Nulls nulls, Nulls contentNulls) {
         if (nulls == null) {
            nulls = Nulls.DEFAULT;
         }

         if (contentNulls == null) {
            contentNulls = Nulls.DEFAULT;
         }

         return _empty(nulls, contentNulls) ? EMPTY : new Value(nulls, contentNulls);
      }

      public static Value empty() {
         return EMPTY;
      }

      public static Value merge(Value base, Value overrides) {
         return base == null ? overrides : base.withOverrides(overrides);
      }

      public static Value forValueNulls(Nulls nulls) {
         return construct(nulls, Nulls.DEFAULT);
      }

      public static Value forValueNulls(Nulls nulls, Nulls contentNulls) {
         return construct(nulls, contentNulls);
      }

      public static Value forContentNulls(Nulls nulls) {
         return construct(Nulls.DEFAULT, nulls);
      }

      public Value withOverrides(Value overrides) {
         if (overrides != null && overrides != EMPTY) {
            Nulls nulls = overrides._nulls;
            Nulls contentNulls = overrides._contentNulls;
            if (nulls == Nulls.DEFAULT) {
               nulls = this._nulls;
            }

            if (contentNulls == Nulls.DEFAULT) {
               contentNulls = this._contentNulls;
            }

            return nulls == this._nulls && contentNulls == this._contentNulls ? this : construct(nulls, contentNulls);
         } else {
            return this;
         }
      }

      public Value withValueNulls(Nulls nulls) {
         if (nulls == null) {
            nulls = Nulls.DEFAULT;
         }

         return nulls == this._nulls ? this : construct(nulls, this._contentNulls);
      }

      public Value withValueNulls(Nulls valueNulls, Nulls contentNulls) {
         if (valueNulls == null) {
            valueNulls = Nulls.DEFAULT;
         }

         if (contentNulls == null) {
            contentNulls = Nulls.DEFAULT;
         }

         return valueNulls == this._nulls && contentNulls == this._contentNulls ? this : construct(valueNulls, contentNulls);
      }

      public Value withContentNulls(Nulls nulls) {
         if (nulls == null) {
            nulls = Nulls.DEFAULT;
         }

         return nulls == this._contentNulls ? this : construct(this._nulls, nulls);
      }

      public Nulls getValueNulls() {
         return this._nulls;
      }

      public Nulls getContentNulls() {
         return this._contentNulls;
      }

      public Nulls nonDefaultValueNulls() {
         return this._nulls == Nulls.DEFAULT ? null : this._nulls;
      }

      public Nulls nonDefaultContentNulls() {
         return this._contentNulls == Nulls.DEFAULT ? null : this._contentNulls;
      }

      public String toString() {
         return String.format("JsonSetter.Value(valueNulls=%s,contentNulls=%s)", this._nulls, this._contentNulls);
      }

      public int hashCode() {
         return this._nulls.ordinal() + (this._contentNulls.ordinal() << 2);
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (o.getClass() != this.getClass()) {
            return false;
         } else {
            Value other = (Value)o;
            return other._nulls == this._nulls && other._contentNulls == this._contentNulls;
         }
      }

      private static boolean _empty(Nulls nulls, Nulls contentNulls) {
         return nulls == Nulls.DEFAULT && contentNulls == Nulls.DEFAULT;
      }

      static {
         EMPTY = new Value(Nulls.DEFAULT, Nulls.DEFAULT);
      }
   }
}
