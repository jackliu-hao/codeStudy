/*     */ package org.yaml.snakeyaml;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import org.yaml.snakeyaml.error.YAMLException;
/*     */ import org.yaml.snakeyaml.serializer.AnchorGenerator;
/*     */ import org.yaml.snakeyaml.serializer.NumberAnchorGenerator;
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
/*     */ public class DumperOptions
/*     */ {
/*     */   public enum ScalarStyle
/*     */   {
/*  39 */     DOUBLE_QUOTED((String)Character.valueOf('"')), SINGLE_QUOTED((String)Character.valueOf('\'')), LITERAL((String)Character.valueOf('|')),
/*  40 */     FOLDED((String)Character.valueOf('>')), PLAIN(null);
/*     */     private Character styleChar;
/*     */     
/*     */     ScalarStyle(Character style) {
/*  44 */       this.styleChar = style;
/*     */     }
/*     */     
/*     */     public Character getChar() {
/*  48 */       return this.styleChar;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/*  53 */       return "Scalar style: '" + this.styleChar + "'";
/*     */     }
/*     */     
/*     */     public static ScalarStyle createStyle(Character style) {
/*  57 */       if (style == null) {
/*  58 */         return PLAIN;
/*     */       }
/*  60 */       switch (style.charValue()) {
/*     */         case '"':
/*  62 */           return DOUBLE_QUOTED;
/*     */         case '\'':
/*  64 */           return SINGLE_QUOTED;
/*     */         case '|':
/*  66 */           return LITERAL;
/*     */         case '>':
/*  68 */           return FOLDED;
/*     */       } 
/*  70 */       throw new YAMLException("Unknown scalar style character: " + style);
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
/*     */   public enum FlowStyle
/*     */   {
/*  85 */     FLOW((String)Boolean.TRUE), BLOCK((String)Boolean.FALSE), AUTO(null);
/*     */     
/*     */     private Boolean styleBoolean;
/*     */     
/*     */     FlowStyle(Boolean flowStyle) {
/*  90 */       this.styleBoolean = flowStyle;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public static FlowStyle fromBoolean(Boolean flowStyle) {
/* 100 */       return (flowStyle == null) ? AUTO : (flowStyle.booleanValue() ? FLOW : BLOCK);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Boolean getStyleBoolean() {
/* 106 */       return this.styleBoolean;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 111 */       return "Flow style: '" + this.styleBoolean + "'";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum LineBreak
/*     */   {
/* 119 */     WIN("\r\n"), MAC("\r"), UNIX("\n");
/*     */     
/*     */     private String lineBreak;
/*     */     
/*     */     LineBreak(String lineBreak) {
/* 124 */       this.lineBreak = lineBreak;
/*     */     }
/*     */     
/*     */     public String getString() {
/* 128 */       return this.lineBreak;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 133 */       return "Line break: " + name();
/*     */     }
/*     */     
/*     */     public static LineBreak getPlatformLineBreak() {
/* 137 */       String platformLineBreak = System.getProperty("line.separator");
/* 138 */       for (LineBreak lb : values()) {
/* 139 */         if (lb.lineBreak.equals(platformLineBreak)) {
/* 140 */           return lb;
/*     */         }
/*     */       } 
/* 143 */       return UNIX;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Version
/*     */   {
/* 151 */     V1_0((String)new Integer[] { Integer.valueOf(1), Integer.valueOf(0) }), V1_1((String)new Integer[] { Integer.valueOf(1), Integer.valueOf(1) });
/*     */     
/*     */     private Integer[] version;
/*     */     
/*     */     Version(Integer[] version) {
/* 156 */       this.version = version;
/*     */     }
/*     */     
/* 159 */     public int major() { return this.version[0].intValue(); } public int minor() {
/* 160 */       return this.version[1].intValue();
/*     */     }
/*     */     public String getRepresentation() {
/* 163 */       return this.version[0] + "." + this.version[1];
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 168 */       return "Version: " + getRepresentation();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum NonPrintableStyle
/*     */   {
/* 176 */     BINARY,
/*     */ 
/*     */ 
/*     */     
/* 180 */     ESCAPE;
/*     */   }
/*     */   
/* 183 */   private ScalarStyle defaultStyle = ScalarStyle.PLAIN;
/* 184 */   private FlowStyle defaultFlowStyle = FlowStyle.AUTO;
/*     */   private boolean canonical = false;
/*     */   private boolean allowUnicode = true;
/*     */   private boolean allowReadOnlyProperties = false;
/* 188 */   private int indent = 2;
/* 189 */   private int indicatorIndent = 0;
/*     */   private boolean indentWithIndicator = false;
/* 191 */   private int bestWidth = 80;
/*     */   private boolean splitLines = true;
/* 193 */   private LineBreak lineBreak = LineBreak.UNIX;
/*     */   private boolean explicitStart = false;
/*     */   private boolean explicitEnd = false;
/* 196 */   private TimeZone timeZone = null;
/* 197 */   private int maxSimpleKeyLength = 128;
/*     */   private boolean processComments = false;
/* 199 */   private NonPrintableStyle nonPrintableStyle = NonPrintableStyle.BINARY;
/*     */   
/* 201 */   private Version version = null;
/* 202 */   private Map<String, String> tags = null;
/* 203 */   private Boolean prettyFlow = Boolean.valueOf(false);
/* 204 */   private AnchorGenerator anchorGenerator = (AnchorGenerator)new NumberAnchorGenerator(0);
/*     */   
/*     */   public boolean isAllowUnicode() {
/* 207 */     return this.allowUnicode;
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
/*     */   public void setAllowUnicode(boolean allowUnicode) {
/* 221 */     this.allowUnicode = allowUnicode;
/*     */   }
/*     */   
/*     */   public ScalarStyle getDefaultScalarStyle() {
/* 225 */     return this.defaultStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultScalarStyle(ScalarStyle defaultStyle) {
/* 236 */     if (defaultStyle == null) {
/* 237 */       throw new NullPointerException("Use ScalarStyle enum.");
/*     */     }
/* 239 */     this.defaultStyle = defaultStyle;
/*     */   }
/*     */   
/*     */   public void setIndent(int indent) {
/* 243 */     if (indent < 1) {
/* 244 */       throw new YAMLException("Indent must be at least 1");
/*     */     }
/* 246 */     if (indent > 10) {
/* 247 */       throw new YAMLException("Indent must be at most 10");
/*     */     }
/* 249 */     this.indent = indent;
/*     */   }
/*     */   
/*     */   public int getIndent() {
/* 253 */     return this.indent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndicatorIndent(int indicatorIndent) {
/* 261 */     if (indicatorIndent < 0) {
/* 262 */       throw new YAMLException("Indicator indent must be non-negative.");
/*     */     }
/* 264 */     if (indicatorIndent > 9) {
/* 265 */       throw new YAMLException("Indicator indent must be at most Emitter.MAX_INDENT-1: 9");
/*     */     }
/* 267 */     this.indicatorIndent = indicatorIndent;
/*     */   }
/*     */   
/*     */   public int getIndicatorIndent() {
/* 271 */     return this.indicatorIndent;
/*     */   }
/*     */   
/*     */   public boolean getIndentWithIndicator() {
/* 275 */     return this.indentWithIndicator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndentWithIndicator(boolean indentWithIndicator) {
/* 283 */     this.indentWithIndicator = indentWithIndicator;
/*     */   }
/*     */   
/*     */   public void setVersion(Version version) {
/* 287 */     this.version = version;
/*     */   }
/*     */   
/*     */   public Version getVersion() {
/* 291 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCanonical(boolean canonical) {
/* 301 */     this.canonical = canonical;
/*     */   }
/*     */   
/*     */   public boolean isCanonical() {
/* 305 */     return this.canonical;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPrettyFlow(boolean prettyFlow) {
/* 316 */     this.prettyFlow = Boolean.valueOf(prettyFlow);
/*     */   }
/*     */   
/*     */   public boolean isPrettyFlow() {
/* 320 */     return this.prettyFlow.booleanValue();
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
/*     */   public void setWidth(int bestWidth) {
/* 332 */     this.bestWidth = bestWidth;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 336 */     return this.bestWidth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSplitLines(boolean splitLines) {
/* 347 */     this.splitLines = splitLines;
/*     */   }
/*     */   
/*     */   public boolean getSplitLines() {
/* 351 */     return this.splitLines;
/*     */   }
/*     */   
/*     */   public LineBreak getLineBreak() {
/* 355 */     return this.lineBreak;
/*     */   }
/*     */   
/*     */   public void setDefaultFlowStyle(FlowStyle defaultFlowStyle) {
/* 359 */     if (defaultFlowStyle == null) {
/* 360 */       throw new NullPointerException("Use FlowStyle enum.");
/*     */     }
/* 362 */     this.defaultFlowStyle = defaultFlowStyle;
/*     */   }
/*     */   
/*     */   public FlowStyle getDefaultFlowStyle() {
/* 366 */     return this.defaultFlowStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLineBreak(LineBreak lineBreak) {
/* 376 */     if (lineBreak == null) {
/* 377 */       throw new NullPointerException("Specify line break.");
/*     */     }
/* 379 */     this.lineBreak = lineBreak;
/*     */   }
/*     */   
/*     */   public boolean isExplicitStart() {
/* 383 */     return this.explicitStart;
/*     */   }
/*     */   
/*     */   public void setExplicitStart(boolean explicitStart) {
/* 387 */     this.explicitStart = explicitStart;
/*     */   }
/*     */   
/*     */   public boolean isExplicitEnd() {
/* 391 */     return this.explicitEnd;
/*     */   }
/*     */   
/*     */   public void setExplicitEnd(boolean explicitEnd) {
/* 395 */     this.explicitEnd = explicitEnd;
/*     */   }
/*     */   
/*     */   public Map<String, String> getTags() {
/* 399 */     return this.tags;
/*     */   }
/*     */   
/*     */   public void setTags(Map<String, String> tags) {
/* 403 */     this.tags = tags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAllowReadOnlyProperties() {
/* 413 */     return this.allowReadOnlyProperties;
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
/*     */   public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
/* 425 */     this.allowReadOnlyProperties = allowReadOnlyProperties;
/*     */   }
/*     */   
/*     */   public TimeZone getTimeZone() {
/* 429 */     return this.timeZone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTimeZone(TimeZone timeZone) {
/* 438 */     this.timeZone = timeZone;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnchorGenerator getAnchorGenerator() {
/* 443 */     return this.anchorGenerator;
/*     */   }
/*     */   
/*     */   public void setAnchorGenerator(AnchorGenerator anchorGenerator) {
/* 447 */     this.anchorGenerator = anchorGenerator;
/*     */   }
/*     */   
/*     */   public int getMaxSimpleKeyLength() {
/* 451 */     return this.maxSimpleKeyLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxSimpleKeyLength(int maxSimpleKeyLength) {
/* 460 */     if (maxSimpleKeyLength > 1024) {
/* 461 */       throw new YAMLException("The simple key must not span more than 1024 stream characters. See https://yaml.org/spec/1.1/#id934537");
/*     */     }
/* 463 */     this.maxSimpleKeyLength = maxSimpleKeyLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProcessComments(boolean processComments) {
/* 473 */     this.processComments = processComments;
/*     */   }
/*     */   
/*     */   public boolean isProcessComments() {
/* 477 */     return this.processComments;
/*     */   }
/*     */   
/*     */   public NonPrintableStyle getNonPrintableStyle() {
/* 481 */     return this.nonPrintableStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNonPrintableStyle(NonPrintableStyle style) {
/* 490 */     this.nonPrintableStyle = style;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\DumperOptions.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */