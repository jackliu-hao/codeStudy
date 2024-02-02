/*     */ package io.undertow.attribute;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.ServiceLoader;
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
/*     */ public class ExchangeAttributeParser
/*     */ {
/*     */   private final List<ExchangeAttributeBuilder> builders;
/*     */   private final List<ExchangeAttributeWrapper> wrappers;
/*     */   
/*     */   ExchangeAttributeParser(ClassLoader classLoader, List<ExchangeAttributeWrapper> wrappers) {
/*  45 */     this.wrappers = wrappers;
/*  46 */     ServiceLoader<ExchangeAttributeBuilder> loader = ServiceLoader.load(ExchangeAttributeBuilder.class, classLoader);
/*  47 */     List<ExchangeAttributeBuilder> builders = new ArrayList<>();
/*  48 */     for (ExchangeAttributeBuilder instance : loader) {
/*  49 */       builders.add(instance);
/*     */     }
/*     */     
/*  52 */     Collections.sort(builders, new Comparator<ExchangeAttributeBuilder>()
/*     */         {
/*     */           public int compare(ExchangeAttributeBuilder o1, ExchangeAttributeBuilder o2) {
/*  55 */             return Integer.compare(o2.priority(), o1.priority());
/*     */           }
/*     */         });
/*  58 */     this.builders = Collections.unmodifiableList(builders);
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
/*     */   public ExchangeAttribute parse(String valueString) {
/*  75 */     List<ExchangeAttribute> attributes = new ArrayList<>();
/*  76 */     int pos = 0;
/*  77 */     int state = 0;
/*  78 */     for (int i = 0; i < valueString.length(); i++) {
/*  79 */       char c = valueString.charAt(i);
/*  80 */       switch (state) {
/*     */         case 0:
/*  82 */           if (c == '%' || c == '$') {
/*  83 */             if (pos != i) {
/*  84 */               attributes.add(wrap(parseSingleToken(valueString.substring(pos, i))));
/*  85 */               pos = i;
/*     */             } 
/*  87 */             if (c == '%') {
/*  88 */               state = 1; break;
/*     */             } 
/*  90 */             state = 3;
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         case 1:
/*  96 */           if (c == '{') {
/*  97 */             state = 2; break;
/*  98 */           }  if (c == '%') {
/*     */             
/* 100 */             attributes.add(wrap(new ConstantExchangeAttribute("%")));
/* 101 */             pos = i + 1;
/* 102 */             state = 0; break;
/*     */           } 
/* 104 */           attributes.add(wrap(parseSingleToken(valueString.substring(pos, i + 1))));
/* 105 */           pos = i + 1;
/* 106 */           state = 0;
/*     */           break;
/*     */ 
/*     */         
/*     */         case 2:
/* 111 */           if (c == '}') {
/* 112 */             attributes.add(wrap(parseSingleToken(valueString.substring(pos, i + 1))));
/* 113 */             pos = i + 1;
/* 114 */             state = 0;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 3:
/* 119 */           if (c == '{') {
/* 120 */             state = 4; break;
/* 121 */           }  if (c == '$') {
/*     */             
/* 123 */             attributes.add(wrap(new ConstantExchangeAttribute("$")));
/* 124 */             pos = i + 1;
/* 125 */             state = 0; break;
/*     */           } 
/* 127 */           attributes.add(wrap(parseSingleToken(valueString.substring(pos, i + 1))));
/* 128 */           pos = i + 1;
/* 129 */           state = 0;
/*     */           break;
/*     */ 
/*     */         
/*     */         case 4:
/* 134 */           if (c == '}') {
/* 135 */             attributes.add(wrap(parseSingleToken(valueString.substring(pos, i + 1))));
/* 136 */             pos = i + 1;
/* 137 */             state = 0;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 144 */     switch (state) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 3:
/* 148 */         if (pos != valueString.length()) {
/* 149 */           attributes.add(wrap(parseSingleToken(valueString.substring(pos))));
/*     */         }
/*     */         break;
/*     */       
/*     */       case 2:
/*     */       case 4:
/* 155 */         throw UndertowMessages.MESSAGES.mismatchedBraces(valueString);
/*     */     } 
/*     */     
/* 158 */     if (attributes.size() == 1) {
/* 159 */       return attributes.get(0);
/*     */     }
/* 161 */     return new CompositeExchangeAttribute(attributes.<ExchangeAttribute>toArray(new ExchangeAttribute[attributes.size()]));
/*     */   }
/*     */   
/*     */   public ExchangeAttribute parseSingleToken(String token) {
/* 165 */     for (ExchangeAttributeBuilder builder : this.builders) {
/* 166 */       ExchangeAttribute res = builder.build(token);
/* 167 */       if (res != null) {
/* 168 */         return res;
/*     */       }
/*     */     } 
/* 171 */     if (token.startsWith("%")) {
/* 172 */       UndertowLogger.ROOT_LOGGER.unknownVariable(token);
/*     */     }
/* 174 */     return new ConstantExchangeAttribute(token);
/*     */   }
/*     */   
/*     */   private ExchangeAttribute wrap(ExchangeAttribute attribute) {
/* 178 */     ExchangeAttribute res = attribute;
/* 179 */     for (ExchangeAttributeWrapper w : this.wrappers) {
/* 180 */       res = w.wrap(res);
/*     */     }
/* 182 */     return res;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ExchangeAttributeParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */