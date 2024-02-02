package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonInclude {
   Include value() default JsonInclude.Include.ALWAYS;

   Include content() default JsonInclude.Include.ALWAYS;

   Class<?> valueFilter() default Void.class;

   Class<?> contentFilter() default Void.class;

   public static class Value implements JacksonAnnotationValue<JsonInclude>, Serializable {
      private static final long serialVersionUID = 1L;
      protected static final Value EMPTY;
      protected final Include _valueInclusion;
      protected final Include _contentInclusion;
      protected final Class<?> _valueFilter;
      protected final Class<?> _contentFilter;

      public Value(JsonInclude src) {
         this(src.value(), src.content(), src.valueFilter(), src.contentFilter());
      }

      protected Value(Include vi, Include ci, Class<?> valueFilter, Class<?> contentFilter) {
         this._valueInclusion = vi == null ? JsonInclude.Include.USE_DEFAULTS : vi;
         this._contentInclusion = ci == null ? JsonInclude.Include.USE_DEFAULTS : ci;
         this._valueFilter = valueFilter == Void.class ? null : valueFilter;
         this._contentFilter = contentFilter == Void.class ? null : contentFilter;
      }

      public static Value empty() {
         return EMPTY;
      }

      public static Value merge(Value base, Value overrides) {
         return base == null ? overrides : base.withOverrides(overrides);
      }

      public static Value mergeAll(Value... values) {
         Value result = null;
         Value[] var2 = values;
         int var3 = values.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Value curr = var2[var4];
            if (curr != null) {
               result = result == null ? curr : result.withOverrides(curr);
            }
         }

         return result;
      }

      protected Object readResolve() {
         return this._valueInclusion == JsonInclude.Include.USE_DEFAULTS && this._contentInclusion == JsonInclude.Include.USE_DEFAULTS && this._valueFilter == null && this._contentFilter == null ? EMPTY : this;
      }

      public Value withOverrides(Value overrides) {
         if (overrides != null && overrides != EMPTY) {
            Include vi = overrides._valueInclusion;
            Include ci = overrides._contentInclusion;
            Class<?> vf = overrides._valueFilter;
            Class<?> cf = overrides._contentFilter;
            boolean viDiff = vi != this._valueInclusion && vi != JsonInclude.Include.USE_DEFAULTS;
            boolean ciDiff = ci != this._contentInclusion && ci != JsonInclude.Include.USE_DEFAULTS;
            boolean filterDiff = vf != this._valueFilter || cf != this._valueFilter;
            if (viDiff) {
               return ciDiff ? new Value(vi, ci, vf, cf) : new Value(vi, this._contentInclusion, vf, cf);
            } else if (ciDiff) {
               return new Value(this._valueInclusion, ci, vf, cf);
            } else {
               return filterDiff ? new Value(this._valueInclusion, this._contentInclusion, vf, cf) : this;
            }
         } else {
            return this;
         }
      }

      public static Value construct(Include valueIncl, Include contentIncl) {
         return valueIncl != JsonInclude.Include.USE_DEFAULTS && valueIncl != null || contentIncl != JsonInclude.Include.USE_DEFAULTS && contentIncl != null ? new Value(valueIncl, contentIncl, (Class)null, (Class)null) : EMPTY;
      }

      public static Value construct(Include valueIncl, Include contentIncl, Class<?> valueFilter, Class<?> contentFilter) {
         if (valueFilter == Void.class) {
            valueFilter = null;
         }

         if (contentFilter == Void.class) {
            contentFilter = null;
         }

         return (valueIncl == JsonInclude.Include.USE_DEFAULTS || valueIncl == null) && (contentIncl == JsonInclude.Include.USE_DEFAULTS || contentIncl == null) && valueFilter == null && contentFilter == null ? EMPTY : new Value(valueIncl, contentIncl, valueFilter, contentFilter);
      }

      public static Value from(JsonInclude src) {
         if (src == null) {
            return EMPTY;
         } else {
            Include vi = src.value();
            Include ci = src.content();
            if (vi == JsonInclude.Include.USE_DEFAULTS && ci == JsonInclude.Include.USE_DEFAULTS) {
               return EMPTY;
            } else {
               Class<?> vf = src.valueFilter();
               if (vf == Void.class) {
                  vf = null;
               }

               Class<?> cf = src.contentFilter();
               if (cf == Void.class) {
                  cf = null;
               }

               return new Value(vi, ci, vf, cf);
            }
         }
      }

      public Value withValueInclusion(Include incl) {
         return incl == this._valueInclusion ? this : new Value(incl, this._contentInclusion, this._valueFilter, this._contentFilter);
      }

      public Value withValueFilter(Class<?> filter) {
         Include incl;
         if (filter != null && filter != Void.class) {
            incl = JsonInclude.Include.CUSTOM;
         } else {
            incl = JsonInclude.Include.USE_DEFAULTS;
            filter = null;
         }

         return construct(incl, this._contentInclusion, filter, this._contentFilter);
      }

      public Value withContentFilter(Class<?> filter) {
         Include incl;
         if (filter != null && filter != Void.class) {
            incl = JsonInclude.Include.CUSTOM;
         } else {
            incl = JsonInclude.Include.USE_DEFAULTS;
            filter = null;
         }

         return construct(this._valueInclusion, incl, this._valueFilter, filter);
      }

      public Value withContentInclusion(Include incl) {
         return incl == this._contentInclusion ? this : new Value(this._valueInclusion, incl, this._valueFilter, this._contentFilter);
      }

      public Class<JsonInclude> valueFor() {
         return JsonInclude.class;
      }

      public Include getValueInclusion() {
         return this._valueInclusion;
      }

      public Include getContentInclusion() {
         return this._contentInclusion;
      }

      public Class<?> getValueFilter() {
         return this._valueFilter;
      }

      public Class<?> getContentFilter() {
         return this._contentFilter;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder(80);
         sb.append("JsonInclude.Value(value=").append(this._valueInclusion).append(",content=").append(this._contentInclusion);
         if (this._valueFilter != null) {
            sb.append(",valueFilter=").append(this._valueFilter.getName()).append(".class");
         }

         if (this._contentFilter != null) {
            sb.append(",contentFilter=").append(this._contentFilter.getName()).append(".class");
         }

         return sb.append(')').toString();
      }

      public int hashCode() {
         return (this._valueInclusion.hashCode() << 2) + this._contentInclusion.hashCode();
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
            return other._valueInclusion == this._valueInclusion && other._contentInclusion == this._contentInclusion && other._valueFilter == this._valueFilter && other._contentFilter == this._contentFilter;
         }
      }

      static {
         EMPTY = new Value(JsonInclude.Include.USE_DEFAULTS, JsonInclude.Include.USE_DEFAULTS, (Class)null, (Class)null);
      }
   }

   public static enum Include {
      ALWAYS,
      NON_NULL,
      NON_ABSENT,
      NON_EMPTY,
      NON_DEFAULT,
      CUSTOM,
      USE_DEFAULTS;
   }
}
