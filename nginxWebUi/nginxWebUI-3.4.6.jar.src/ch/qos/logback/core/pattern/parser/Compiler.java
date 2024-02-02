/*     */ package ch.qos.logback.core.pattern.parser;
/*     */ 
/*     */ import ch.qos.logback.core.pattern.CompositeConverter;
/*     */ import ch.qos.logback.core.pattern.Converter;
/*     */ import ch.qos.logback.core.pattern.DynamicConverter;
/*     */ import ch.qos.logback.core.pattern.LiteralConverter;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.status.ErrorStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.util.OptionHelper;
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
/*     */ class Compiler<E>
/*     */   extends ContextAwareBase
/*     */ {
/*     */   Converter<E> head;
/*     */   Converter<E> tail;
/*     */   final Node top;
/*     */   final Map converterMap;
/*     */   
/*     */   Compiler(Node top, Map converterMap) {
/*  34 */     this.top = top;
/*  35 */     this.converterMap = converterMap;
/*     */   }
/*     */   
/*     */   Converter<E> compile() {
/*  39 */     this.head = this.tail = null;
/*  40 */     for (Node n = this.top; n != null; n = n.next) {
/*  41 */       CompositeNode cn; CompositeConverter<E> compositeConverter; Compiler<E> childCompiler; Converter<E> childConverter; SimpleKeywordNode kn; DynamicConverter<E> dynaConverter; LiteralConverter literalConverter; switch (n.type) {
/*     */         case 0:
/*  43 */           addToList((Converter<E>)new LiteralConverter((String)n.getValue()));
/*     */           break;
/*     */         case 2:
/*  46 */           cn = (CompositeNode)n;
/*  47 */           compositeConverter = createCompositeConverter(cn);
/*  48 */           if (compositeConverter == null) {
/*  49 */             addError("Failed to create converter for [%" + cn.getValue() + "] keyword");
/*  50 */             addToList((Converter<E>)new LiteralConverter("%PARSER_ERROR[" + cn.getValue() + "]"));
/*     */             break;
/*     */           } 
/*  53 */           compositeConverter.setFormattingInfo(cn.getFormatInfo());
/*  54 */           compositeConverter.setOptionList(cn.getOptions());
/*  55 */           childCompiler = new Compiler(cn.getChildNode(), this.converterMap);
/*  56 */           childCompiler.setContext(this.context);
/*  57 */           childConverter = childCompiler.compile();
/*  58 */           compositeConverter.setChildConverter(childConverter);
/*  59 */           addToList((Converter<E>)compositeConverter);
/*     */           break;
/*     */         case 1:
/*  62 */           kn = (SimpleKeywordNode)n;
/*  63 */           dynaConverter = createConverter(kn);
/*  64 */           if (dynaConverter != null) {
/*  65 */             dynaConverter.setFormattingInfo(kn.getFormatInfo());
/*  66 */             dynaConverter.setOptionList(kn.getOptions());
/*  67 */             addToList((Converter<E>)dynaConverter);
/*     */             
/*     */             break;
/*     */           } 
/*  71 */           literalConverter = new LiteralConverter("%PARSER_ERROR[" + kn.getValue() + "]");
/*  72 */           addStatus((Status)new ErrorStatus("[" + kn.getValue() + "] is not a valid conversion word", this));
/*  73 */           addToList((Converter<E>)literalConverter);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/*  78 */     return this.head;
/*     */   }
/*     */   
/*     */   private void addToList(Converter<E> c) {
/*  82 */     if (this.head == null) {
/*  83 */       this.head = this.tail = c;
/*     */     } else {
/*  85 */       this.tail.setNext(c);
/*  86 */       this.tail = c;
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
/*     */   DynamicConverter<E> createConverter(SimpleKeywordNode kn) {
/*  99 */     String keyword = (String)kn.getValue();
/* 100 */     String converterClassStr = (String)this.converterMap.get(keyword);
/*     */     
/* 102 */     if (converterClassStr != null) {
/*     */       try {
/* 104 */         return (DynamicConverter<E>)OptionHelper.instantiateByClassName(converterClassStr, DynamicConverter.class, this.context);
/* 105 */       } catch (Exception e) {
/* 106 */         addError("Failed to instantiate converter class [" + converterClassStr + "] for keyword [" + keyword + "]", e);
/* 107 */         return null;
/*     */       } 
/*     */     }
/* 110 */     addError("There is no conversion class registered for conversion word [" + keyword + "]");
/* 111 */     return null;
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
/*     */   CompositeConverter<E> createCompositeConverter(CompositeNode cn) {
/* 124 */     String keyword = (String)cn.getValue();
/* 125 */     String converterClassStr = (String)this.converterMap.get(keyword);
/*     */     
/* 127 */     if (converterClassStr != null) {
/*     */       try {
/* 129 */         return (CompositeConverter<E>)OptionHelper.instantiateByClassName(converterClassStr, CompositeConverter.class, this.context);
/* 130 */       } catch (Exception e) {
/* 131 */         addError("Failed to instantiate converter class [" + converterClassStr + "] as a composite converter for keyword [" + keyword + "]", e);
/* 132 */         return null;
/*     */       } 
/*     */     }
/* 135 */     addError("There is no conversion class registered for composite conversion word [" + keyword + "]");
/* 136 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\parser\Compiler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */