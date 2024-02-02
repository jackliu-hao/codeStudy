package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.TimeZone;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonFormat {
   String DEFAULT_LOCALE = "##default";
   String DEFAULT_TIMEZONE = "##default";

   String pattern() default "";

   Shape shape() default JsonFormat.Shape.ANY;

   String locale() default "##default";

   String timezone() default "##default";

   OptBoolean lenient() default OptBoolean.DEFAULT;

   Feature[] with() default {};

   Feature[] without() default {};

   public static class Value implements JacksonAnnotationValue<JsonFormat>, Serializable {
      private static final long serialVersionUID = 1L;
      private static final Value EMPTY = new Value();
      private final String _pattern;
      private final Shape _shape;
      private final Locale _locale;
      private final String _timezoneStr;
      private final Boolean _lenient;
      private final Features _features;
      private transient TimeZone _timezone;

      public Value() {
         this("", JsonFormat.Shape.ANY, (String)"", (String)"", (Features)JsonFormat.Features.empty(), (Boolean)null);
      }

      public Value(JsonFormat ann) {
         this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(), JsonFormat.Features.construct(ann), ann.lenient().asBoolean());
      }

      public Value(String p, Shape sh, String localeStr, String tzStr, Features f, Boolean lenient) {
         this(p, sh, localeStr != null && localeStr.length() != 0 && !"##default".equals(localeStr) ? new Locale(localeStr) : null, tzStr != null && tzStr.length() != 0 && !"##default".equals(tzStr) ? tzStr : null, (TimeZone)null, f, lenient);
      }

      public Value(String p, Shape sh, Locale l, TimeZone tz, Features f, Boolean lenient) {
         this._pattern = p == null ? "" : p;
         this._shape = sh == null ? JsonFormat.Shape.ANY : sh;
         this._locale = l;
         this._timezone = tz;
         this._timezoneStr = null;
         this._features = f == null ? JsonFormat.Features.empty() : f;
         this._lenient = lenient;
      }

      public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz, Features f, Boolean lenient) {
         this._pattern = p == null ? "" : p;
         this._shape = sh == null ? JsonFormat.Shape.ANY : sh;
         this._locale = l;
         this._timezone = tz;
         this._timezoneStr = tzStr;
         this._features = f == null ? JsonFormat.Features.empty() : f;
         this._lenient = lenient;
      }

      /** @deprecated */
      @Deprecated
      public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz, Features f) {
         this(p, sh, l, tzStr, tz, f, (Boolean)null);
      }

      /** @deprecated */
      @Deprecated
      public Value(String p, Shape sh, String localeStr, String tzStr, Features f) {
         this(p, sh, (String)localeStr, (String)tzStr, (Features)f, (Boolean)null);
      }

      /** @deprecated */
      @Deprecated
      public Value(String p, Shape sh, Locale l, TimeZone tz, Features f) {
         this(p, sh, (Locale)l, (TimeZone)tz, (Features)f, (Boolean)null);
      }

      public static final Value empty() {
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

      public static final Value from(JsonFormat ann) {
         return ann == null ? EMPTY : new Value(ann);
      }

      public final Value withOverrides(Value overrides) {
         if (overrides != null && overrides != EMPTY && overrides != this) {
            if (this == EMPTY) {
               return overrides;
            } else {
               String p = overrides._pattern;
               if (p == null || p.isEmpty()) {
                  p = this._pattern;
               }

               Shape sh = overrides._shape;
               if (sh == JsonFormat.Shape.ANY) {
                  sh = this._shape;
               }

               Locale l = overrides._locale;
               if (l == null) {
                  l = this._locale;
               }

               Features f = this._features;
               if (f == null) {
                  f = overrides._features;
               } else {
                  f = f.withOverrides(overrides._features);
               }

               Boolean lenient = overrides._lenient;
               if (lenient == null) {
                  lenient = this._lenient;
               }

               String tzStr = overrides._timezoneStr;
               TimeZone tz;
               if (tzStr != null && !tzStr.isEmpty()) {
                  tz = overrides._timezone;
               } else {
                  tzStr = this._timezoneStr;
                  tz = this._timezone;
               }

               return new Value(p, sh, l, tzStr, tz, f, lenient);
            }
         } else {
            return this;
         }
      }

      public static Value forPattern(String p) {
         return new Value(p, (Shape)null, (Locale)null, (String)null, (TimeZone)null, JsonFormat.Features.empty(), (Boolean)null);
      }

      public static Value forShape(Shape sh) {
         return new Value("", sh, (Locale)null, (String)null, (TimeZone)null, JsonFormat.Features.empty(), (Boolean)null);
      }

      public static Value forLeniency(boolean lenient) {
         return new Value("", (Shape)null, (Locale)null, (String)null, (TimeZone)null, JsonFormat.Features.empty(), lenient);
      }

      public Value withPattern(String p) {
         return new Value(p, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
      }

      public Value withShape(Shape s) {
         return s == this._shape ? this : new Value(this._pattern, s, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
      }

      public Value withLocale(Locale l) {
         return new Value(this._pattern, this._shape, l, this._timezoneStr, this._timezone, this._features, this._lenient);
      }

      public Value withTimeZone(TimeZone tz) {
         return new Value(this._pattern, this._shape, this._locale, (String)null, tz, this._features, this._lenient);
      }

      public Value withLenient(Boolean lenient) {
         return lenient == this._lenient ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, lenient);
      }

      public Value withFeature(Feature f) {
         Features newFeats = this._features.with(f);
         return newFeats == this._features ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
      }

      public Value withoutFeature(Feature f) {
         Features newFeats = this._features.without(f);
         return newFeats == this._features ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
      }

      public Class<JsonFormat> valueFor() {
         return JsonFormat.class;
      }

      public String getPattern() {
         return this._pattern;
      }

      public Shape getShape() {
         return this._shape;
      }

      public Locale getLocale() {
         return this._locale;
      }

      public Boolean getLenient() {
         return this._lenient;
      }

      public boolean isLenient() {
         return Boolean.TRUE.equals(this._lenient);
      }

      public String timeZoneAsString() {
         return this._timezone != null ? this._timezone.getID() : this._timezoneStr;
      }

      public TimeZone getTimeZone() {
         TimeZone tz = this._timezone;
         if (tz == null) {
            if (this._timezoneStr == null) {
               return null;
            }

            tz = TimeZone.getTimeZone(this._timezoneStr);
            this._timezone = tz;
         }

         return tz;
      }

      public boolean hasShape() {
         return this._shape != JsonFormat.Shape.ANY;
      }

      public boolean hasPattern() {
         return this._pattern != null && this._pattern.length() > 0;
      }

      public boolean hasLocale() {
         return this._locale != null;
      }

      public boolean hasTimeZone() {
         return this._timezone != null || this._timezoneStr != null && !this._timezoneStr.isEmpty();
      }

      public boolean hasLenient() {
         return this._lenient != null;
      }

      public Boolean getFeature(Feature f) {
         return this._features.get(f);
      }

      public Features getFeatures() {
         return this._features;
      }

      public String toString() {
         return String.format("JsonFormat.Value(pattern=%s,shape=%s,lenient=%s,locale=%s,timezone=%s,features=%s)", this._pattern, this._shape, this._lenient, this._locale, this._timezoneStr, this._features);
      }

      public int hashCode() {
         int hash = this._timezoneStr == null ? 1 : this._timezoneStr.hashCode();
         if (this._pattern != null) {
            hash ^= this._pattern.hashCode();
         }

         hash += this._shape.hashCode();
         if (this._lenient != null) {
            hash ^= this._lenient.hashCode();
         }

         if (this._locale != null) {
            hash += this._locale.hashCode();
         }

         hash ^= this._features.hashCode();
         return hash;
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
            if (this._shape == other._shape && this._features.equals(other._features)) {
               return _equal(this._lenient, other._lenient) && _equal(this._timezoneStr, other._timezoneStr) && _equal(this._pattern, other._pattern) && _equal(this._timezone, other._timezone) && _equal(this._locale, other._locale);
            } else {
               return false;
            }
         }
      }

      private static <T> boolean _equal(T value1, T value2) {
         if (value1 == null) {
            return value2 == null;
         } else {
            return value2 == null ? false : value1.equals(value2);
         }
      }
   }

   public static class Features {
      private final int _enabled;
      private final int _disabled;
      private static final Features EMPTY = new Features(0, 0);

      private Features(int e, int d) {
         this._enabled = e;
         this._disabled = d;
      }

      public static Features empty() {
         return EMPTY;
      }

      public static Features construct(JsonFormat f) {
         return construct(f.with(), f.without());
      }

      public static Features construct(Feature[] enabled, Feature[] disabled) {
         int e = 0;
         Feature[] var3 = enabled;
         int var4 = enabled.length;

         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            Feature f = var3[var5];
            e |= 1 << f.ordinal();
         }

         int d = 0;
         Feature[] var9 = disabled;
         var5 = disabled.length;

         for(int var10 = 0; var10 < var5; ++var10) {
            Feature f = var9[var10];
            d |= 1 << f.ordinal();
         }

         return new Features(e, d);
      }

      public Features withOverrides(Features overrides) {
         if (overrides == null) {
            return this;
         } else {
            int overrideD = overrides._disabled;
            int overrideE = overrides._enabled;
            if (overrideD == 0 && overrideE == 0) {
               return this;
            } else if (this._enabled == 0 && this._disabled == 0) {
               return overrides;
            } else {
               int newE = this._enabled & ~overrideD | overrideE;
               int newD = this._disabled & ~overrideE | overrideD;
               return newE == this._enabled && newD == this._disabled ? this : new Features(newE, newD);
            }
         }
      }

      public Features with(Feature... features) {
         int e = this._enabled;
         Feature[] var3 = features;
         int var4 = features.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Feature f = var3[var5];
            e |= 1 << f.ordinal();
         }

         return e == this._enabled ? this : new Features(e, this._disabled);
      }

      public Features without(Feature... features) {
         int d = this._disabled;
         Feature[] var3 = features;
         int var4 = features.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Feature f = var3[var5];
            d |= 1 << f.ordinal();
         }

         return d == this._disabled ? this : new Features(this._enabled, d);
      }

      public Boolean get(Feature f) {
         int mask = 1 << f.ordinal();
         if ((this._disabled & mask) != 0) {
            return Boolean.FALSE;
         } else {
            return (this._enabled & mask) != 0 ? Boolean.TRUE : null;
         }
      }

      public String toString() {
         return this == EMPTY ? "EMPTY" : String.format("(enabled=0x%x,disabled=0x%x)", this._enabled, this._disabled);
      }

      public int hashCode() {
         return this._disabled + this._enabled;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (o.getClass() != this.getClass()) {
            return false;
         } else {
            Features other = (Features)o;
            return other._enabled == this._enabled && other._disabled == this._disabled;
         }
      }
   }

   public static enum Feature {
      ACCEPT_SINGLE_VALUE_AS_ARRAY,
      ACCEPT_CASE_INSENSITIVE_PROPERTIES,
      ACCEPT_CASE_INSENSITIVE_VALUES,
      WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
      WRITE_DATES_WITH_ZONE_ID,
      WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED,
      WRITE_SORTED_MAP_ENTRIES,
      ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
   }

   public static enum Shape {
      ANY,
      NATURAL,
      SCALAR,
      ARRAY,
      OBJECT,
      NUMBER,
      NUMBER_FLOAT,
      NUMBER_INT,
      STRING,
      BOOLEAN,
      BINARY;

      public boolean isNumeric() {
         return this == NUMBER || this == NUMBER_INT || this == NUMBER_FLOAT;
      }

      public boolean isStructured() {
         return this == OBJECT || this == ARRAY;
      }
   }
}
