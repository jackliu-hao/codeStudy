package org.noear.snack.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

public class Options {
   public static int features_def;
   public static int features_serialize;
   private int features;
   private final Map<Class<?>, NodeEncoderEntity> encoderMap;
   private final Map<Class<?>, NodeDecoderEntity> decoderMap;
   private String dateFormat;
   private String typePropertyName;
   private TimeZone timeZone;

   public static final Options def() {
      return new Options(features_def);
   }

   public static final Options serialize() {
      return new Options(features_serialize);
   }

   public Options() {
      this.features = DEFAULTS.DEF_FEATURES;
      this.encoderMap = new LinkedHashMap();
      this.decoderMap = new LinkedHashMap();
      this.dateFormat = DEFAULTS.DEF_DATETIME_FORMAT;
      this.typePropertyName = "@type";
      this.timeZone = DEFAULTS.DEF_TIME_ZONE;
   }

   public Options(int features) {
      this();
      this.features = features;
   }

   public static Options of(Feature... features) {
      return (new Options()).add(features);
   }

   public Options add(Feature... features) {
      Feature[] var2 = features;
      int var3 = features.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Feature f = var2[var4];
         this.features = Feature.config(this.features, f, true);
      }

      return this;
   }

   public Options remove(Feature... features) {
      Feature[] var2 = features;
      int var3 = features.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Feature f = var2[var4];
         this.features = Feature.config(this.features, f, false);
      }

      return this;
   }

   /** @deprecated */
   @Deprecated
   public Options sub(Feature... features) {
      return this.remove(features);
   }

   public final int getFeatures() {
      return this.features;
   }

   public final void setFeatures(Feature... features) {
      this.features = Feature.of(features);
   }

   public final boolean hasFeature(Feature feature) {
      return Feature.isEnabled(this.features, feature);
   }

   public Options build(Consumer<Options> custom) {
      custom.accept(this);
      return this;
   }

   public Collection<NodeEncoderEntity> encoders() {
      return Collections.unmodifiableCollection(this.encoderMap.values());
   }

   public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
      this.encoderMap.put(clz, new NodeEncoderEntity(clz, encoder));
   }

   public Collection<NodeDecoderEntity> decoders() {
      return Collections.unmodifiableCollection(this.decoderMap.values());
   }

   public <T> void addDecoder(Class<T> clz, NodeDecoder<T> decoder) {
      this.decoderMap.put(clz, new NodeDecoderEntity(clz, decoder));
   }

   public String getDateFormat() {
      return this.dateFormat;
   }

   public void setDateFormat(String dateFormat) {
      this.dateFormat = dateFormat;
   }

   public String getTypePropertyName() {
      return this.typePropertyName;
   }

   public void setTypePropertyName(String typePropertyName) {
      this.typePropertyName = typePropertyName;
   }

   public TimeZone getTimeZone() {
      return this.timeZone;
   }

   public void setTimeZone(TimeZone timeZone) {
      this.timeZone = timeZone;
   }

   static {
      features_def = Feature.of(Feature.OrderedField, Feature.WriteDateUseTicks, Feature.TransferCompatible, Feature.StringNullAsEmpty, Feature.QuoteFieldNames);
      features_serialize = Feature.of(Feature.OrderedField, Feature.WriteDateUseTicks, Feature.BrowserCompatible, Feature.WriteClassName, Feature.QuoteFieldNames);
   }
}
