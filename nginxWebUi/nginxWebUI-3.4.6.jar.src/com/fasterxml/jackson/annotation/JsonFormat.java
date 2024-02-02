/*     */ package com.fasterxml.jackson.annotation;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.ElementType;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.annotation.Target;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
/*     */ @Retention(RetentionPolicy.RUNTIME)
/*     */ @JacksonAnnotation
/*     */ public @interface JsonFormat
/*     */ {
/*     */   public static final String DEFAULT_LOCALE = "##default";
/*     */   public static final String DEFAULT_TIMEZONE = "##default";
/*     */   
/*     */   String pattern() default "";
/*     */   
/*     */   Shape shape() default Shape.ANY;
/*     */   
/*     */   String locale() default "##default";
/*     */   
/*     */   String timezone() default "##default";
/*     */   
/*     */   OptBoolean lenient() default OptBoolean.DEFAULT;
/*     */   
/*     */   Feature[] with() default {};
/*     */   
/*     */   Feature[] without() default {};
/*     */   
/*     */   public enum Shape
/*     */   {
/* 156 */     ANY,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 167 */     NATURAL,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 173 */     SCALAR,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 178 */     ARRAY,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     OBJECT,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 190 */     NUMBER,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 195 */     NUMBER_FLOAT,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 201 */     NUMBER_INT,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 206 */     STRING,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 212 */     BOOLEAN,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 220 */     BINARY;
/*     */ 
/*     */     
/*     */     public boolean isNumeric() {
/* 224 */       return (this == NUMBER || this == NUMBER_INT || this == NUMBER_FLOAT);
/*     */     }
/*     */     
/*     */     public boolean isStructured() {
/* 228 */       return (this == OBJECT || this == ARRAY);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Feature
/*     */   {
/* 251 */     ACCEPT_SINGLE_VALUE_AS_ARRAY,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 262 */     ACCEPT_CASE_INSENSITIVE_PROPERTIES,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 272 */     ACCEPT_CASE_INSENSITIVE_VALUES,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 278 */     WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 284 */     WRITE_DATES_WITH_ZONE_ID,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 291 */     WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 298 */     WRITE_SORTED_MAP_ENTRIES,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 313 */     ADJUST_DATES_TO_CONTEXT_TIME_ZONE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Features
/*     */   {
/*     */     private final int _enabled;
/*     */ 
/*     */     
/*     */     private final int _disabled;
/*     */ 
/*     */     
/* 326 */     private static final Features EMPTY = new Features(0, 0);
/*     */     
/*     */     private Features(int e, int d) {
/* 329 */       this._enabled = e;
/* 330 */       this._disabled = d;
/*     */     }
/*     */     
/*     */     public static Features empty() {
/* 334 */       return EMPTY;
/*     */     }
/*     */     
/*     */     public static Features construct(JsonFormat f) {
/* 338 */       return construct(f.with(), f.without());
/*     */     }
/*     */ 
/*     */     
/*     */     public static Features construct(JsonFormat.Feature[] enabled, JsonFormat.Feature[] disabled) {
/* 343 */       int e = 0;
/* 344 */       for (JsonFormat.Feature f : enabled) {
/* 345 */         e |= 1 << f.ordinal();
/*     */       }
/* 347 */       int d = 0;
/* 348 */       for (JsonFormat.Feature f : disabled) {
/* 349 */         d |= 1 << f.ordinal();
/*     */       }
/* 351 */       return new Features(e, d);
/*     */     }
/*     */ 
/*     */     
/*     */     public Features withOverrides(Features overrides) {
/* 356 */       if (overrides == null) {
/* 357 */         return this;
/*     */       }
/* 359 */       int overrideD = overrides._disabled;
/* 360 */       int overrideE = overrides._enabled;
/* 361 */       if (overrideD == 0 && overrideE == 0) {
/* 362 */         return this;
/*     */       }
/* 364 */       if (this._enabled == 0 && this._disabled == 0) {
/* 365 */         return overrides;
/*     */       }
/*     */       
/* 368 */       int newE = this._enabled & (overrideD ^ 0xFFFFFFFF) | overrideE;
/* 369 */       int newD = this._disabled & (overrideE ^ 0xFFFFFFFF) | overrideD;
/*     */ 
/*     */       
/* 372 */       if (newE == this._enabled && newD == this._disabled) {
/* 373 */         return this;
/*     */       }
/*     */       
/* 376 */       return new Features(newE, newD);
/*     */     }
/*     */     
/*     */     public Features with(JsonFormat.Feature... features) {
/* 380 */       int e = this._enabled;
/* 381 */       for (JsonFormat.Feature f : features) {
/* 382 */         e |= 1 << f.ordinal();
/*     */       }
/* 384 */       return (e == this._enabled) ? this : new Features(e, this._disabled);
/*     */     }
/*     */     
/*     */     public Features without(JsonFormat.Feature... features) {
/* 388 */       int d = this._disabled;
/* 389 */       for (JsonFormat.Feature f : features) {
/* 390 */         d |= 1 << f.ordinal();
/*     */       }
/* 392 */       return (d == this._disabled) ? this : new Features(this._enabled, d);
/*     */     }
/*     */     
/*     */     public Boolean get(JsonFormat.Feature f) {
/* 396 */       int mask = 1 << f.ordinal();
/* 397 */       if ((this._disabled & mask) != 0) {
/* 398 */         return Boolean.FALSE;
/*     */       }
/* 400 */       if ((this._enabled & mask) != 0) {
/* 401 */         return Boolean.TRUE;
/*     */       }
/* 403 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 408 */       if (this == EMPTY) {
/* 409 */         return "EMPTY";
/*     */       }
/* 411 */       return String.format("(enabled=0x%x,disabled=0x%x)", new Object[] { Integer.valueOf(this._enabled), Integer.valueOf(this._disabled) });
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 416 */       return this._disabled + this._enabled;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 421 */       if (o == this) return true; 
/* 422 */       if (o == null) return false; 
/* 423 */       if (o.getClass() != getClass()) return false; 
/* 424 */       Features other = (Features)o;
/* 425 */       return (other._enabled == this._enabled && other._disabled == this._disabled);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Value
/*     */     implements JacksonAnnotationValue<JsonFormat>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */     
/* 439 */     private static final Value EMPTY = new Value();
/*     */ 
/*     */     
/*     */     private final String _pattern;
/*     */ 
/*     */     
/*     */     private final JsonFormat.Shape _shape;
/*     */ 
/*     */     
/*     */     private final Locale _locale;
/*     */ 
/*     */     
/*     */     private final String _timezoneStr;
/*     */     
/*     */     private final Boolean _lenient;
/*     */     
/*     */     private final JsonFormat.Features _features;
/*     */     
/*     */     private transient TimeZone _timezone;
/*     */ 
/*     */     
/*     */     public Value() {
/* 461 */       this("", JsonFormat.Shape.ANY, "", "", JsonFormat.Features.empty(), (Boolean)null);
/*     */     }
/*     */     
/*     */     public Value(JsonFormat ann) {
/* 465 */       this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(), 
/* 466 */           JsonFormat.Features.construct(ann), ann.lenient().asBoolean());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value(String p, JsonFormat.Shape sh, String localeStr, String tzStr, JsonFormat.Features f, Boolean lenient) {
/* 475 */       this(p, sh, (localeStr == null || localeStr
/* 476 */           .length() == 0 || "##default".equals(localeStr)) ? null : new Locale(localeStr), (tzStr == null || tzStr
/*     */           
/* 478 */           .length() == 0 || "##default".equals(tzStr)) ? null : tzStr, null, f, lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value(String p, JsonFormat.Shape sh, Locale l, TimeZone tz, JsonFormat.Features f, Boolean lenient) {
/* 489 */       this._pattern = (p == null) ? "" : p;
/* 490 */       this._shape = (sh == null) ? JsonFormat.Shape.ANY : sh;
/* 491 */       this._locale = l;
/* 492 */       this._timezone = tz;
/* 493 */       this._timezoneStr = null;
/* 494 */       this._features = (f == null) ? JsonFormat.Features.empty() : f;
/* 495 */       this._lenient = lenient;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value(String p, JsonFormat.Shape sh, Locale l, String tzStr, TimeZone tz, JsonFormat.Features f, Boolean lenient) {
/* 504 */       this._pattern = (p == null) ? "" : p;
/* 505 */       this._shape = (sh == null) ? JsonFormat.Shape.ANY : sh;
/* 506 */       this._locale = l;
/* 507 */       this._timezone = tz;
/* 508 */       this._timezoneStr = tzStr;
/* 509 */       this._features = (f == null) ? JsonFormat.Features.empty() : f;
/* 510 */       this._lenient = lenient;
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public Value(String p, JsonFormat.Shape sh, Locale l, String tzStr, TimeZone tz, JsonFormat.Features f) {
/* 515 */       this(p, sh, l, tzStr, tz, f, null);
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public Value(String p, JsonFormat.Shape sh, String localeStr, String tzStr, JsonFormat.Features f) {
/* 520 */       this(p, sh, localeStr, tzStr, f, (Boolean)null);
/*     */     }
/*     */     @Deprecated
/*     */     public Value(String p, JsonFormat.Shape sh, Locale l, TimeZone tz, JsonFormat.Features f) {
/* 524 */       this(p, sh, l, tz, f, (Boolean)null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Value empty() {
/* 531 */       return EMPTY;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value merge(Value base, Value overrides) {
/* 547 */       return (base == null) ? overrides : base
/* 548 */         .withOverrides(overrides);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value mergeAll(Value... values) {
/* 556 */       Value result = null;
/* 557 */       for (Value curr : values) {
/* 558 */         if (curr != null) {
/* 559 */           result = (result == null) ? curr : result.withOverrides(curr);
/*     */         }
/*     */       } 
/* 562 */       return result;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final Value from(JsonFormat ann) {
/* 569 */       return (ann == null) ? EMPTY : new Value(ann);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public final Value withOverrides(Value overrides) {
/*     */       TimeZone tz;
/* 576 */       if (overrides == null || overrides == EMPTY || overrides == this) {
/* 577 */         return this;
/*     */       }
/* 579 */       if (this == EMPTY) {
/* 580 */         return overrides;
/*     */       }
/* 582 */       String p = overrides._pattern;
/* 583 */       if (p == null || p.isEmpty()) {
/* 584 */         p = this._pattern;
/*     */       }
/* 586 */       JsonFormat.Shape sh = overrides._shape;
/* 587 */       if (sh == JsonFormat.Shape.ANY) {
/* 588 */         sh = this._shape;
/*     */       }
/* 590 */       Locale l = overrides._locale;
/* 591 */       if (l == null) {
/* 592 */         l = this._locale;
/*     */       }
/* 594 */       JsonFormat.Features f = this._features;
/* 595 */       if (f == null) {
/* 596 */         f = overrides._features;
/*     */       } else {
/* 598 */         f = f.withOverrides(overrides._features);
/*     */       } 
/* 600 */       Boolean lenient = overrides._lenient;
/* 601 */       if (lenient == null) {
/* 602 */         lenient = this._lenient;
/*     */       }
/*     */ 
/*     */       
/* 606 */       String tzStr = overrides._timezoneStr;
/*     */ 
/*     */       
/* 609 */       if (tzStr == null || tzStr.isEmpty()) {
/* 610 */         tzStr = this._timezoneStr;
/* 611 */         tz = this._timezone;
/*     */       } else {
/* 613 */         tz = overrides._timezone;
/*     */       } 
/* 615 */       return new Value(p, sh, l, tzStr, tz, f, lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value forPattern(String p) {
/* 622 */       return new Value(p, null, null, null, null, JsonFormat.Features.empty(), null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value forShape(JsonFormat.Shape sh) {
/* 629 */       return new Value("", sh, null, null, null, JsonFormat.Features.empty(), null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value forLeniency(boolean lenient) {
/* 636 */       return new Value("", null, null, null, null, JsonFormat.Features.empty(), 
/* 637 */           Boolean.valueOf(lenient));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withPattern(String p) {
/* 644 */       return new Value(p, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withShape(JsonFormat.Shape s) {
/* 652 */       if (s == this._shape) {
/* 653 */         return this;
/*     */       }
/* 655 */       return new Value(this._pattern, s, this._locale, this._timezoneStr, this._timezone, this._features, this._lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withLocale(Locale l) {
/* 663 */       return new Value(this._pattern, this._shape, l, this._timezoneStr, this._timezone, this._features, this._lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withTimeZone(TimeZone tz) {
/* 671 */       return new Value(this._pattern, this._shape, this._locale, null, tz, this._features, this._lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withLenient(Boolean lenient) {
/* 679 */       if (lenient == this._lenient) {
/* 680 */         return this;
/*     */       }
/* 682 */       return new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, this._features, lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withFeature(JsonFormat.Feature f) {
/* 690 */       JsonFormat.Features newFeats = this._features.with(new JsonFormat.Feature[] { f });
/* 691 */       return (newFeats == this._features) ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withoutFeature(JsonFormat.Feature f) {
/* 700 */       JsonFormat.Features newFeats = this._features.without(new JsonFormat.Feature[] { f });
/* 701 */       return (newFeats == this._features) ? this : new Value(this._pattern, this._shape, this._locale, this._timezoneStr, this._timezone, newFeats, this._lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Class<JsonFormat> valueFor() {
/* 708 */       return JsonFormat.class;
/*     */     }
/*     */     
/* 711 */     public String getPattern() { return this._pattern; }
/* 712 */     public JsonFormat.Shape getShape() { return this._shape; } public Locale getLocale() {
/* 713 */       return this._locale;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Boolean getLenient() {
/* 723 */       return this._lenient;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isLenient() {
/* 737 */       return Boolean.TRUE.equals(this._lenient);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String timeZoneAsString() {
/* 748 */       if (this._timezone != null) {
/* 749 */         return this._timezone.getID();
/*     */       }
/* 751 */       return this._timezoneStr;
/*     */     }
/*     */     
/*     */     public TimeZone getTimeZone() {
/* 755 */       TimeZone tz = this._timezone;
/* 756 */       if (tz == null) {
/* 757 */         if (this._timezoneStr == null) {
/* 758 */           return null;
/*     */         }
/* 760 */         tz = TimeZone.getTimeZone(this._timezoneStr);
/* 761 */         this._timezone = tz;
/*     */       } 
/* 763 */       return tz;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasShape() {
/* 769 */       return (this._shape != JsonFormat.Shape.ANY);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasPattern() {
/* 775 */       return (this._pattern != null && this._pattern.length() > 0);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasLocale() {
/* 781 */       return (this._locale != null);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasTimeZone() {
/* 787 */       return (this._timezone != null || (this._timezoneStr != null && !this._timezoneStr.isEmpty()));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasLenient() {
/* 798 */       return (this._lenient != null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Boolean getFeature(JsonFormat.Feature f) {
/* 811 */       return this._features.get(f);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public JsonFormat.Features getFeatures() {
/* 820 */       return this._features;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 825 */       return String.format("JsonFormat.Value(pattern=%s,shape=%s,lenient=%s,locale=%s,timezone=%s,features=%s)", new Object[] { this._pattern, this._shape, this._lenient, this._locale, this._timezoneStr, this._features });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 831 */       int hash = (this._timezoneStr == null) ? 1 : this._timezoneStr.hashCode();
/* 832 */       if (this._pattern != null) {
/* 833 */         hash ^= this._pattern.hashCode();
/*     */       }
/* 835 */       hash += this._shape.hashCode();
/* 836 */       if (this._lenient != null) {
/* 837 */         hash ^= this._lenient.hashCode();
/*     */       }
/* 839 */       if (this._locale != null) {
/* 840 */         hash += this._locale.hashCode();
/*     */       }
/* 842 */       hash ^= this._features.hashCode();
/* 843 */       return hash;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 848 */       if (o == this) return true; 
/* 849 */       if (o == null) return false; 
/* 850 */       if (o.getClass() != getClass()) return false; 
/* 851 */       Value other = (Value)o;
/*     */       
/* 853 */       if (this._shape != other._shape || 
/* 854 */         !this._features.equals(other._features)) {
/* 855 */         return false;
/*     */       }
/* 857 */       return (_equal(this._lenient, other._lenient) && 
/* 858 */         _equal(this._timezoneStr, other._timezoneStr) && 
/* 859 */         _equal(this._pattern, other._pattern) && 
/* 860 */         _equal(this._timezone, other._timezone) && 
/* 861 */         _equal(this._locale, other._locale));
/*     */     }
/*     */ 
/*     */     
/*     */     private static <T> boolean _equal(T value1, T value2) {
/* 866 */       if (value1 == null) {
/* 867 */         return (value2 == null);
/*     */       }
/* 869 */       if (value2 == null) {
/* 870 */         return false;
/*     */       }
/* 872 */       return value1.equals(value2);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\JsonFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */