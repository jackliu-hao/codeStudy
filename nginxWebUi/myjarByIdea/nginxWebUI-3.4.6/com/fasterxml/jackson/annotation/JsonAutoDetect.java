package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAutoDetect {
   Visibility getterVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility isGetterVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility setterVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility creatorVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility fieldVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   public static class Value implements JacksonAnnotationValue<JsonAutoDetect>, Serializable {
      private static final long serialVersionUID = 1L;
      private static final Visibility DEFAULT_FIELD_VISIBILITY;
      protected static final Value DEFAULT;
      protected static final Value NO_OVERRIDES;
      protected final Visibility _fieldVisibility;
      protected final Visibility _getterVisibility;
      protected final Visibility _isGetterVisibility;
      protected final Visibility _setterVisibility;
      protected final Visibility _creatorVisibility;

      private Value(Visibility fields, Visibility getters, Visibility isGetters, Visibility setters, Visibility creators) {
         this._fieldVisibility = fields;
         this._getterVisibility = getters;
         this._isGetterVisibility = isGetters;
         this._setterVisibility = setters;
         this._creatorVisibility = creators;
      }

      public static Value defaultVisibility() {
         return DEFAULT;
      }

      public static Value noOverrides() {
         return NO_OVERRIDES;
      }

      public static Value from(JsonAutoDetect src) {
         return construct(src.fieldVisibility(), src.getterVisibility(), src.isGetterVisibility(), src.setterVisibility(), src.creatorVisibility());
      }

      public static Value construct(PropertyAccessor acc, Visibility visibility) {
         Visibility fields = JsonAutoDetect.Visibility.DEFAULT;
         Visibility getters = JsonAutoDetect.Visibility.DEFAULT;
         Visibility isGetters = JsonAutoDetect.Visibility.DEFAULT;
         Visibility setters = JsonAutoDetect.Visibility.DEFAULT;
         Visibility creators = JsonAutoDetect.Visibility.DEFAULT;
         switch (acc) {
            case CREATOR:
               creators = visibility;
               break;
            case FIELD:
               fields = visibility;
               break;
            case GETTER:
               getters = visibility;
               break;
            case IS_GETTER:
               isGetters = visibility;
            case NONE:
            default:
               break;
            case SETTER:
               setters = visibility;
               break;
            case ALL:
               creators = visibility;
               setters = visibility;
               isGetters = visibility;
               getters = visibility;
               fields = visibility;
         }

         return construct(fields, getters, isGetters, setters, creators);
      }

      public static Value construct(Visibility fields, Visibility getters, Visibility isGetters, Visibility setters, Visibility creators) {
         Value v = _predefined(fields, getters, isGetters, setters, creators);
         if (v == null) {
            v = new Value(fields, getters, isGetters, setters, creators);
         }

         return v;
      }

      public Value withFieldVisibility(Visibility v) {
         return construct(v, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
      }

      public Value withGetterVisibility(Visibility v) {
         return construct(this._fieldVisibility, v, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
      }

      public Value withIsGetterVisibility(Visibility v) {
         return construct(this._fieldVisibility, this._getterVisibility, v, this._setterVisibility, this._creatorVisibility);
      }

      public Value withSetterVisibility(Visibility v) {
         return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, v, this._creatorVisibility);
      }

      public Value withCreatorVisibility(Visibility v) {
         return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, v);
      }

      public static Value merge(Value base, Value overrides) {
         return base == null ? overrides : base.withOverrides(overrides);
      }

      public Value withOverrides(Value overrides) {
         if (overrides != null && overrides != NO_OVERRIDES && overrides != this) {
            if (_equals(this, overrides)) {
               return this;
            } else {
               Visibility fields = overrides._fieldVisibility;
               if (fields == JsonAutoDetect.Visibility.DEFAULT) {
                  fields = this._fieldVisibility;
               }

               Visibility getters = overrides._getterVisibility;
               if (getters == JsonAutoDetect.Visibility.DEFAULT) {
                  getters = this._getterVisibility;
               }

               Visibility isGetters = overrides._isGetterVisibility;
               if (isGetters == JsonAutoDetect.Visibility.DEFAULT) {
                  isGetters = this._isGetterVisibility;
               }

               Visibility setters = overrides._setterVisibility;
               if (setters == JsonAutoDetect.Visibility.DEFAULT) {
                  setters = this._setterVisibility;
               }

               Visibility creators = overrides._creatorVisibility;
               if (creators == JsonAutoDetect.Visibility.DEFAULT) {
                  creators = this._creatorVisibility;
               }

               return construct(fields, getters, isGetters, setters, creators);
            }
         } else {
            return this;
         }
      }

      public Class<JsonAutoDetect> valueFor() {
         return JsonAutoDetect.class;
      }

      public Visibility getFieldVisibility() {
         return this._fieldVisibility;
      }

      public Visibility getGetterVisibility() {
         return this._getterVisibility;
      }

      public Visibility getIsGetterVisibility() {
         return this._isGetterVisibility;
      }

      public Visibility getSetterVisibility() {
         return this._setterVisibility;
      }

      public Visibility getCreatorVisibility() {
         return this._creatorVisibility;
      }

      protected Object readResolve() {
         Value v = _predefined(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
         return v == null ? this : v;
      }

      public String toString() {
         return String.format("JsonAutoDetect.Value(fields=%s,getters=%s,isGetters=%s,setters=%s,creators=%s)", this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
      }

      public int hashCode() {
         return 1 + this._fieldVisibility.ordinal() ^ 3 * this._getterVisibility.ordinal() - 7 * this._isGetterVisibility.ordinal() + 11 * this._setterVisibility.ordinal() ^ 13 * this._creatorVisibility.ordinal();
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else {
            return o.getClass() == this.getClass() && _equals(this, (Value)o);
         }
      }

      private static Value _predefined(Visibility fields, Visibility getters, Visibility isGetters, Visibility setters, Visibility creators) {
         if (fields == DEFAULT_FIELD_VISIBILITY) {
            if (getters == DEFAULT._getterVisibility && isGetters == DEFAULT._isGetterVisibility && setters == DEFAULT._setterVisibility && creators == DEFAULT._creatorVisibility) {
               return DEFAULT;
            }
         } else if (fields == JsonAutoDetect.Visibility.DEFAULT && getters == JsonAutoDetect.Visibility.DEFAULT && isGetters == JsonAutoDetect.Visibility.DEFAULT && setters == JsonAutoDetect.Visibility.DEFAULT && creators == JsonAutoDetect.Visibility.DEFAULT) {
            return NO_OVERRIDES;
         }

         return null;
      }

      private static boolean _equals(Value a, Value b) {
         return a._fieldVisibility == b._fieldVisibility && a._getterVisibility == b._getterVisibility && a._isGetterVisibility == b._isGetterVisibility && a._setterVisibility == b._setterVisibility && a._creatorVisibility == b._creatorVisibility;
      }

      static {
         DEFAULT_FIELD_VISIBILITY = JsonAutoDetect.Visibility.PUBLIC_ONLY;
         DEFAULT = new Value(DEFAULT_FIELD_VISIBILITY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
         NO_OVERRIDES = new Value(JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT);
      }
   }

   public static enum Visibility {
      ANY,
      NON_PRIVATE,
      PROTECTED_AND_PUBLIC,
      PUBLIC_ONLY,
      NONE,
      DEFAULT;

      public boolean isVisible(Member m) {
         switch (this) {
            case ANY:
               return true;
            case NONE:
               return false;
            case NON_PRIVATE:
               return !Modifier.isPrivate(m.getModifiers());
            case PROTECTED_AND_PUBLIC:
               if (Modifier.isProtected(m.getModifiers())) {
                  return true;
               }
            case PUBLIC_ONLY:
               return Modifier.isPublic(m.getModifiers());
            default:
               return false;
         }
      }
   }
}
