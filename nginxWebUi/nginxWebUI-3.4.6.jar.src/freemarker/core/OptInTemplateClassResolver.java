/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OptInTemplateClassResolver
/*     */   implements TemplateClassResolver
/*     */ {
/*     */   private final Set allowedClasses;
/*     */   private final List trustedTemplatePrefixes;
/*     */   private final Set trustedTemplateNames;
/*     */   
/*     */   public OptInTemplateClassResolver(Set allowedClasses, List trustedTemplates) {
/*  70 */     this.allowedClasses = (allowedClasses != null) ? allowedClasses : Collections.EMPTY_SET;
/*  71 */     if (trustedTemplates != null) {
/*  72 */       this.trustedTemplateNames = new HashSet();
/*  73 */       this.trustedTemplatePrefixes = new ArrayList();
/*     */       
/*  75 */       Iterator<String> it = trustedTemplates.iterator();
/*  76 */       while (it.hasNext()) {
/*  77 */         String li = it.next();
/*  78 */         if (li.startsWith("/")) li = li.substring(1); 
/*  79 */         if (li.endsWith("*")) {
/*  80 */           this.trustedTemplatePrefixes.add(li.substring(0, li.length() - 1)); continue;
/*     */         } 
/*  82 */         this.trustedTemplateNames.add(li);
/*     */       } 
/*     */     } else {
/*     */       
/*  86 */       this.trustedTemplateNames = Collections.EMPTY_SET;
/*  87 */       this.trustedTemplatePrefixes = Collections.EMPTY_LIST;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class resolve(String className, Environment env, Template template) throws TemplateException {
/*  94 */     String templateName = safeGetTemplateName(template);
/*     */     
/*  96 */     if (templateName != null && (this.trustedTemplateNames
/*  97 */       .contains(templateName) || 
/*  98 */       hasMatchingPrefix(templateName))) {
/*  99 */       return TemplateClassResolver.SAFER_RESOLVER.resolve(className, env, template);
/*     */     }
/* 101 */     if (!this.allowedClasses.contains(className)) {
/* 102 */       throw new _MiscTemplateException(env, new Object[] { "Instantiating ", className, " is not allowed in the template for security reasons. (If you run into this problem when using ?new in a template, you may want to check the \"", "new_builtin_class_resolver", "\" setting in the FreeMarker configuration.)" });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 109 */       return ClassUtil.forName(className);
/* 110 */     } catch (ClassNotFoundException e) {
/* 111 */       throw new _MiscTemplateException(e, env);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String safeGetTemplateName(Template template) {
/* 122 */     if (template == null) return null;
/*     */     
/* 124 */     String name = template.getName();
/* 125 */     if (name == null) return null;
/*     */ 
/*     */     
/* 128 */     String decodedName = name;
/* 129 */     if (decodedName.indexOf('%') != -1) {
/* 130 */       decodedName = StringUtil.replace(decodedName, "%2e", ".", false, false);
/* 131 */       decodedName = StringUtil.replace(decodedName, "%2E", ".", false, false);
/* 132 */       decodedName = StringUtil.replace(decodedName, "%2f", "/", false, false);
/* 133 */       decodedName = StringUtil.replace(decodedName, "%2F", "/", false, false);
/* 134 */       decodedName = StringUtil.replace(decodedName, "%5c", "\\", false, false);
/* 135 */       decodedName = StringUtil.replace(decodedName, "%5C", "\\", false, false);
/*     */     } 
/* 137 */     int dotDotIdx = decodedName.indexOf("..");
/* 138 */     if (dotDotIdx != -1) {
/* 139 */       int before = (dotDotIdx - 1 >= 0) ? decodedName.charAt(dotDotIdx - 1) : -1;
/* 140 */       int after = (dotDotIdx + 2 < decodedName.length()) ? decodedName.charAt(dotDotIdx + 2) : -1;
/* 141 */       if ((before == -1 || before == 47 || before == 92) && (after == -1 || after == 47 || after == 92))
/*     */       {
/* 143 */         return null;
/*     */       }
/*     */     } 
/*     */     
/* 147 */     return name.startsWith("/") ? name.substring(1) : name;
/*     */   }
/*     */   
/*     */   private boolean hasMatchingPrefix(String name) {
/* 151 */     for (int i = 0; i < this.trustedTemplatePrefixes.size(); i++) {
/* 152 */       String prefix = this.trustedTemplatePrefixes.get(i);
/* 153 */       if (name.startsWith(prefix)) return true; 
/*     */     } 
/* 155 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\OptInTemplateClassResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */