/*     */ package org.noear.snack.core;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ 
/*     */ public class Options
/*     */ {
/*  13 */   public static int features_def = Feature.of(new Feature[] { Feature.OrderedField, Feature.WriteDateUseTicks, Feature.TransferCompatible, Feature.StringNullAsEmpty, Feature.QuoteFieldNames });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  20 */   public static int features_serialize = Feature.of(new Feature[] { Feature.OrderedField, Feature.WriteDateUseTicks, Feature.BrowserCompatible, Feature.WriteClassName, Feature.QuoteFieldNames });
/*     */   
/*     */   private int features;
/*     */   
/*     */   private final Map<Class<?>, NodeEncoderEntity> encoderMap;
/*     */   private final Map<Class<?>, NodeDecoderEntity> decoderMap;
/*     */   private String dateFormat;
/*     */   private String typePropertyName;
/*     */   private TimeZone timeZone;
/*     */   
/*     */   public static final Options def() {
/*  31 */     return new Options(features_def);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final Options serialize() {
/*  38 */     return new Options(features_serialize);
/*     */   } public Options(int features) {
/*     */     this();
/*     */     this.features = features;
/*     */   } public static Options of(Feature... features) {
/*     */     return (new Options()).add(features);
/*     */   } public Options add(Feature... features) {
/*     */     for (Feature f : features)
/*     */       this.features = Feature.config(this.features, f, true); 
/*     */     return this;
/*     */   } public Options remove(Feature... features) {
/*     */     for (Feature f : features)
/*     */       this.features = Feature.config(this.features, f, false); 
/*     */     return this;
/*     */   } @Deprecated
/*     */   public Options sub(Feature... features) {
/*     */     return remove(features);
/*     */   } public final int getFeatures() {
/*     */     return this.features;
/*     */   } public Options() {
/*  58 */     this.features = DEFAULTS.DEF_FEATURES;
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
/* 118 */     this.encoderMap = new LinkedHashMap<>();
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
/* 137 */     this.decoderMap = new LinkedHashMap<>();
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
/* 155 */     this.dateFormat = DEFAULTS.DEF_DATETIME_FORMAT;
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
/* 173 */     this.typePropertyName = "@type";
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
/* 190 */     this.timeZone = DEFAULTS.DEF_TIME_ZONE;
/*     */   } public final void setFeatures(Feature... features) {
/*     */     this.features = Feature.of(features);
/*     */   } public final boolean hasFeature(Feature feature) {
/*     */     return Feature.isEnabled(this.features, feature);
/*     */   } public TimeZone getTimeZone() {
/* 196 */     return this.timeZone; } public Options build(Consumer<Options> custom) { custom.accept(this);
/*     */     return this; } public Collection<NodeEncoderEntity> encoders() { return Collections.unmodifiableCollection(this.encoderMap.values()); }
/*     */   public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) { this.encoderMap.put(clz, new NodeEncoderEntity<>(clz, encoder)); }
/*     */   public Collection<NodeDecoderEntity> decoders() {
/*     */     return Collections.unmodifiableCollection(this.decoderMap.values());
/*     */   }
/*     */   public void setTimeZone(TimeZone timeZone) {
/* 203 */     this.timeZone = timeZone;
/*     */   }
/*     */   
/*     */   public <T> void addDecoder(Class<T> clz, NodeDecoder<T> decoder) {
/*     */     this.decoderMap.put(clz, new NodeDecoderEntity<>(clz, decoder));
/*     */   }
/*     */   
/*     */   public String getDateFormat() {
/*     */     return this.dateFormat;
/*     */   }
/*     */   
/*     */   public void setDateFormat(String dateFormat) {
/*     */     this.dateFormat = dateFormat;
/*     */   }
/*     */   
/*     */   public String getTypePropertyName() {
/*     */     return this.typePropertyName;
/*     */   }
/*     */   
/*     */   public void setTypePropertyName(String typePropertyName) {
/*     */     this.typePropertyName = typePropertyName;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\Options.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */