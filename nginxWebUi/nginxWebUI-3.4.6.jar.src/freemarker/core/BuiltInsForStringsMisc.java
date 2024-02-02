/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.MalformedTemplateNameException;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import java.io.StringReader;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BuiltInsForStringsMisc
/*     */ {
/*     */   static class booleanBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/*     */       boolean b;
/*  41 */       if (s.equals("true")) {
/*  42 */         b = true;
/*  43 */       } else if (s.equals("false")) {
/*  44 */         b = false;
/*  45 */       } else if (s.equals(env.getTrueStringValue())) {
/*  46 */         b = true;
/*  47 */       } else if (s.equals(env.getFalseStringValue())) {
/*  48 */         b = false;
/*     */       } else {
/*  50 */         throw new _MiscTemplateException(this, env, new Object[] { "Can't convert this string to boolean: ", new _DelayedJQuote(s) });
/*     */       } 
/*     */       
/*  53 */       return b ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class evalBI
/*     */     extends OutputFormatBoundBuiltIn
/*     */   {
/*     */     protected TemplateModel calculateResult(Environment env) throws TemplateException {
/*  61 */       return calculateResult(BuiltInForString.getTargetString(this.target, env), env);
/*     */     }
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/*  65 */       Template parentTemplate = getTemplate();
/*     */       
/*  67 */       Expression exp = null;
/*     */       try {
/*     */         try {
/*  70 */           ParserConfiguration pCfg = parentTemplate.getParserConfiguration();
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  75 */           SimpleCharStream simpleCharStream = new SimpleCharStream(new StringReader("(" + s + ")"), -1000000000, 1, s.length() + 2);
/*  76 */           simpleCharStream.setTabSize(pCfg.getTabSize());
/*  77 */           FMParserTokenManager tkMan = new FMParserTokenManager(simpleCharStream);
/*     */           
/*  79 */           tkMan.SwitchTo(2);
/*     */ 
/*     */           
/*  82 */           if (pCfg.getOutputFormat() != this.outputFormat)
/*     */           {
/*  84 */             pCfg = new _ParserConfigurationWithInheritedFormat(pCfg, this.outputFormat, Integer.valueOf(this.autoEscapingPolicy));
/*     */           }
/*     */           
/*  87 */           FMParser parser = new FMParser(parentTemplate, false, tkMan, pCfg);
/*     */ 
/*     */           
/*  90 */           exp = parser.Expression();
/*  91 */         } catch (TokenMgrError e) {
/*  92 */           throw e.toParseException(parentTemplate);
/*     */         } 
/*  94 */       } catch (ParseException e) {
/*  95 */         throw new _MiscTemplateException(this, env, new Object[] { "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---", "\n\nThe failing expression:" });
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 103 */         return exp.eval(env);
/* 104 */       } catch (TemplateException e) {
/* 105 */         throw new _MiscTemplateException(e, new Object[] { this, env, "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessageWithoutStackTop(e), "\n---end-message---", "\n\nThe failing expression:" });
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class evalJsonBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/*     */       try {
/* 120 */         return JSONParser.parse(s);
/* 121 */       } catch (JSONParseException e) {
/* 122 */         throw new _MiscTemplateException(this, env, new Object[] { "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---", "\n\nThe failing expression:" });
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class numberBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/*     */       try {
/* 136 */         return (TemplateModel)new SimpleNumber(env.getArithmeticEngine().toNumber(s));
/* 137 */       } catch (NumberFormatException nfe) {
/* 138 */         throw NonNumericalException.newMalformedNumberException(this, s, env);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static class absolute_template_nameBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 146 */       return (TemplateModel)new AbsoluteTemplateNameResult(s, env);
/*     */     }
/*     */     
/*     */     private class AbsoluteTemplateNameResult implements TemplateScalarModel, TemplateMethodModelEx {
/*     */       private final String pathToResolve;
/*     */       private final Environment env;
/*     */       
/*     */       public AbsoluteTemplateNameResult(String pathToResolve, Environment env) {
/* 154 */         this.pathToResolve = pathToResolve;
/* 155 */         this.env = env;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 160 */         BuiltInsForStringsMisc.absolute_template_nameBI.this.checkMethodArgCount(args, 1);
/* 161 */         return resolvePath(BuiltInsForStringsMisc.absolute_template_nameBI.this.getStringMethodArg(args, 0));
/*     */       }
/*     */ 
/*     */       
/*     */       public String getAsString() throws TemplateModelException {
/* 166 */         return resolvePath(BuiltInsForStringsMisc.absolute_template_nameBI.this.getTemplate().getName());
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       private String resolvePath(String basePath) throws TemplateModelException {
/*     */         try {
/* 174 */           return this.env.rootBasedToAbsoluteTemplateName(this.env.toFullTemplateName(basePath, this.pathToResolve));
/* 175 */         } catch (MalformedTemplateNameException e) {
/* 176 */           throw new _TemplateModelException(e, new Object[] { "Can't resolve ", new _DelayedJQuote(this.pathToResolve), "to absolute template name using base ", new _DelayedJQuote(basePath), "; see cause exception" });
/*     */         } 
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForStringsMisc.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */