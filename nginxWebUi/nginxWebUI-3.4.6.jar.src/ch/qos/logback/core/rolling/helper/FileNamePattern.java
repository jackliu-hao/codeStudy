/*     */ package ch.qos.logback.core.rolling.helper;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.pattern.Converter;
/*     */ import ch.qos.logback.core.pattern.ConverterUtil;
/*     */ import ch.qos.logback.core.pattern.parser.Node;
/*     */ import ch.qos.logback.core.pattern.parser.Parser;
/*     */ import ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil;
/*     */ import ch.qos.logback.core.pattern.util.IEscapeUtil;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.spi.ScanException;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class FileNamePattern
/*     */   extends ContextAwareBase
/*     */ {
/*  40 */   static final Map<String, String> CONVERTER_MAP = new HashMap<String, String>();
/*     */   static {
/*  42 */     CONVERTER_MAP.put("i", IntegerTokenConverter.class.getName());
/*  43 */     CONVERTER_MAP.put("d", DateTokenConverter.class.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   String pattern;
/*     */   Converter<Object> headTokenConverter;
/*     */   
/*     */   public FileNamePattern(String patternArg, Context contextArg) {
/*  51 */     setPattern(FileFilterUtil.slashify(patternArg));
/*  52 */     setContext(contextArg);
/*  53 */     parse();
/*  54 */     ConverterUtil.startConverters(this.headTokenConverter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void parse() {
/*     */     try {
/*  64 */       String patternForParsing = escapeRightParantesis(this.pattern);
/*  65 */       Parser<Object> p = new Parser(patternForParsing, (IEscapeUtil)new AlmostAsIsEscapeUtil());
/*  66 */       p.setContext(this.context);
/*  67 */       Node t = p.parse();
/*  68 */       this.headTokenConverter = p.compile(t, CONVERTER_MAP);
/*     */     }
/*  70 */     catch (ScanException sce) {
/*  71 */       addError("Failed to parse pattern \"" + this.pattern + "\".", (Throwable)sce);
/*     */     } 
/*     */   }
/*     */   
/*     */   String escapeRightParantesis(String in) {
/*  76 */     return this.pattern.replace(")", "\\)");
/*     */   }
/*     */   
/*     */   public String toString() {
/*  80 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  85 */     int prime = 31;
/*  86 */     int result = 1;
/*  87 */     result = 31 * result + ((this.pattern == null) ? 0 : this.pattern.hashCode());
/*  88 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  94 */     if (this == obj)
/*  95 */       return true; 
/*  96 */     if (obj == null)
/*  97 */       return false; 
/*  98 */     if (getClass() != obj.getClass())
/*  99 */       return false; 
/* 100 */     FileNamePattern other = (FileNamePattern)obj;
/* 101 */     if (this.pattern == null) {
/* 102 */       if (other.pattern != null)
/* 103 */         return false; 
/* 104 */     } else if (!this.pattern.equals(other.pattern)) {
/* 105 */       return false;
/* 106 */     }  return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public DateTokenConverter<Object> getPrimaryDateTokenConverter() {
/* 111 */     Converter<Object> p = this.headTokenConverter;
/*     */     
/* 113 */     while (p != null) {
/* 114 */       if (p instanceof DateTokenConverter) {
/* 115 */         DateTokenConverter<Object> dtc = (DateTokenConverter<Object>)p;
/*     */         
/* 117 */         if (dtc.isPrimary()) {
/* 118 */           return dtc;
/*     */         }
/*     */       } 
/* 121 */       p = p.getNext();
/*     */     } 
/*     */     
/* 124 */     return null;
/*     */   }
/*     */   
/*     */   public IntegerTokenConverter getIntegerTokenConverter() {
/* 128 */     Converter<Object> p = this.headTokenConverter;
/*     */     
/* 130 */     while (p != null) {
/* 131 */       if (p instanceof IntegerTokenConverter) {
/* 132 */         return (IntegerTokenConverter)p;
/*     */       }
/*     */       
/* 135 */       p = p.getNext();
/*     */     } 
/* 137 */     return null;
/*     */   }
/*     */   
/*     */   public boolean hasIntegerTokenCOnverter() {
/* 141 */     IntegerTokenConverter itc = getIntegerTokenConverter();
/* 142 */     return (itc != null);
/*     */   }
/*     */   
/*     */   public String convertMultipleArguments(Object... objectList) {
/* 146 */     StringBuilder buf = new StringBuilder();
/* 147 */     Converter<Object> c = this.headTokenConverter;
/* 148 */     while (c != null) {
/* 149 */       if (c instanceof MonoTypedConverter) {
/* 150 */         MonoTypedConverter monoTyped = (MonoTypedConverter)c;
/* 151 */         for (Object o : objectList) {
/* 152 */           if (monoTyped.isApplicable(o)) {
/* 153 */             buf.append(c.convert(o));
/*     */           }
/*     */         } 
/*     */       } else {
/* 157 */         buf.append(c.convert(objectList));
/*     */       } 
/* 159 */       c = c.getNext();
/*     */     } 
/* 161 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String convert(Object o) {
/* 165 */     StringBuilder buf = new StringBuilder();
/* 166 */     Converter<Object> p = this.headTokenConverter;
/* 167 */     while (p != null) {
/* 168 */       buf.append(p.convert(o));
/* 169 */       p = p.getNext();
/*     */     } 
/* 171 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String convertInt(int i) {
/* 175 */     return convert(Integer.valueOf(i));
/*     */   }
/*     */   
/*     */   public void setPattern(String pattern) {
/* 179 */     if (pattern != null)
/*     */     {
/* 181 */       this.pattern = pattern.trim();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getPattern() {
/* 186 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toRegexForFixedDate(Date date) {
/* 196 */     StringBuilder buf = new StringBuilder();
/* 197 */     Converter<Object> p = this.headTokenConverter;
/* 198 */     while (p != null) {
/* 199 */       if (p instanceof ch.qos.logback.core.pattern.LiteralConverter) {
/* 200 */         buf.append(p.convert(null));
/* 201 */       } else if (p instanceof IntegerTokenConverter) {
/* 202 */         buf.append("(\\d{1,5})");
/* 203 */       } else if (p instanceof DateTokenConverter) {
/* 204 */         buf.append(p.convert(date));
/*     */       } 
/* 206 */       p = p.getNext();
/*     */     } 
/* 208 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toRegex() {
/* 215 */     StringBuilder buf = new StringBuilder();
/* 216 */     Converter<Object> p = this.headTokenConverter;
/* 217 */     while (p != null) {
/* 218 */       if (p instanceof ch.qos.logback.core.pattern.LiteralConverter) {
/* 219 */         buf.append(p.convert(null));
/* 220 */       } else if (p instanceof IntegerTokenConverter) {
/* 221 */         buf.append("\\d{1,2}");
/* 222 */       } else if (p instanceof DateTokenConverter) {
/* 223 */         DateTokenConverter<Object> dtc = (DateTokenConverter<Object>)p;
/* 224 */         buf.append(dtc.toRegex());
/*     */       } 
/* 226 */       p = p.getNext();
/*     */     } 
/* 228 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\FileNamePattern.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */