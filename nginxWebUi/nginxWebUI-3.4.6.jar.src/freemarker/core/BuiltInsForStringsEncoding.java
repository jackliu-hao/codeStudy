/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.UnsupportedEncodingException;
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
/*     */ class BuiltInsForStringsEncoding
/*     */ {
/*     */   static class htmlBI
/*     */     extends BuiltInForLegacyEscaping
/*     */     implements ICIChainMember
/*     */   {
/*     */     static class BIBeforeICI2d3d20
/*     */       extends BuiltInForLegacyEscaping
/*     */     {
/*     */       TemplateModel calculateResult(String s, Environment env) {
/*  41 */         return (TemplateModel)new SimpleScalar(StringUtil.HTMLEnc(s));
/*     */       }
/*     */     }
/*     */     
/*  45 */     private final BIBeforeICI2d3d20 prevICIObj = new BIBeforeICI2d3d20();
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) {
/*  49 */       return (TemplateModel)new SimpleScalar(StringUtil.XHTMLEnc(s));
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMinimumICIVersion() {
/*  54 */       return _TemplateAPI.VERSION_INT_2_3_20;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getPreviousICIChainMember() {
/*  59 */       return this.prevICIObj;
/*     */     }
/*     */   }
/*     */   
/*     */   static class j_stringBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/*  67 */       return (TemplateModel)new SimpleScalar(StringUtil.javaStringEnc(s));
/*     */     }
/*     */   }
/*     */   
/*     */   static class js_stringBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/*  74 */       return (TemplateModel)new SimpleScalar(StringUtil.javaScriptStringEnc(s));
/*     */     }
/*     */   }
/*     */   
/*     */   static class json_stringBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/*  81 */       return (TemplateModel)new SimpleScalar(StringUtil.jsonStringEnc(s));
/*     */     }
/*     */   }
/*     */   
/*     */   static class rtfBI
/*     */     extends BuiltInForLegacyEscaping {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/*  88 */       return (TemplateModel)new SimpleScalar(StringUtil.RTFEnc(s));
/*     */     }
/*     */   }
/*     */   
/*     */   static class urlBI
/*     */     extends BuiltInForString {
/*     */     static class UrlBIResult
/*     */       extends BuiltInsForStringsEncoding.AbstractUrlBIResult {
/*     */       protected UrlBIResult(BuiltIn parent, String target, Environment env) {
/*  97 */         super(parent, target, env);
/*     */       }
/*     */ 
/*     */       
/*     */       protected String encodeWithCharset(String cs) throws UnsupportedEncodingException {
/* 102 */         return StringUtil.URLEnc(this.targetAsString, cs);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 109 */       return (TemplateModel)new UrlBIResult(this, s, env);
/*     */     }
/*     */   }
/*     */   
/*     */   static class urlPathBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     static class UrlPathBIResult
/*     */       extends BuiltInsForStringsEncoding.AbstractUrlBIResult {
/*     */       protected UrlPathBIResult(BuiltIn parent, String target, Environment env) {
/* 119 */         super(parent, target, env);
/*     */       }
/*     */ 
/*     */       
/*     */       protected String encodeWithCharset(String cs) throws UnsupportedEncodingException {
/* 124 */         return StringUtil.URLPathEnc(this.targetAsString, cs);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 131 */       return (TemplateModel)new UrlPathBIResult(this, s, env);
/*     */     }
/*     */   }
/*     */   
/*     */   static class xhtmlBI
/*     */     extends BuiltInForLegacyEscaping
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 139 */       return (TemplateModel)new SimpleScalar(StringUtil.XHTMLEnc(s));
/*     */     }
/*     */   }
/*     */   
/*     */   static class xmlBI
/*     */     extends BuiltInForLegacyEscaping {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 146 */       return (TemplateModel)new SimpleScalar(StringUtil.XMLEnc(s));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static abstract class AbstractUrlBIResult
/*     */     implements TemplateScalarModel, TemplateMethodModel
/*     */   {
/*     */     protected final BuiltIn parent;
/*     */     
/*     */     protected final String targetAsString;
/*     */     
/*     */     private final Environment env;
/*     */     private String cachedResult;
/*     */     
/*     */     protected AbstractUrlBIResult(BuiltIn parent, String target, Environment env) {
/* 162 */       this.parent = parent;
/* 163 */       this.targetAsString = target;
/* 164 */       this.env = env;
/*     */     }
/*     */ 
/*     */     
/*     */     protected abstract String encodeWithCharset(String param1String) throws UnsupportedEncodingException;
/*     */     
/*     */     public Object exec(List<String> args) throws TemplateModelException {
/* 171 */       this.parent.checkMethodArgCount(args.size(), 1);
/*     */       try {
/* 173 */         return new SimpleScalar(encodeWithCharset(args.get(0)));
/* 174 */       } catch (UnsupportedEncodingException e) {
/* 175 */         throw new _TemplateModelException(e, "Failed to execute URL encoding.");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public String getAsString() throws TemplateModelException {
/* 181 */       if (this.cachedResult == null) {
/* 182 */         String cs = this.env.getEffectiveURLEscapingCharset();
/* 183 */         if (cs == null) {
/* 184 */           throw new _TemplateModelException(new Object[] { "To do URL encoding, the framework that encloses FreeMarker must specify the \"", "output_encoding", "\" setting or the \"", "url_escaping_charset", "\" setting, so ask the programmers to set them. Or, as a last chance, you can set the url_encoding_charset setting in the template, e.g. <#setting ", "url_escaping_charset", "='ISO-8859-1'>, or give the charset explicitly to the built-in, e.g. foo?url('ISO-8859-1')." });
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 195 */           this.cachedResult = encodeWithCharset(cs);
/* 196 */         } catch (UnsupportedEncodingException e) {
/* 197 */           throw new _TemplateModelException(e, "Failed to execute URL encoding.");
/*     */         } 
/*     */       } 
/* 200 */       return this.cachedResult;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForStringsEncoding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */