/*     */ package freemarker.ext.servlet;
/*     */ 
/*     */ import freemarker.cache.ClassTemplateLoader;
/*     */ import freemarker.cache.FileTemplateLoader;
/*     */ import freemarker.cache.MultiTemplateLoader;
/*     */ import freemarker.cache.TemplateLoader;
/*     */ import freemarker.cache.WebappTemplateLoader;
/*     */ import freemarker.core._ObjectBuilderSettingEvaluator;
/*     */ import freemarker.core._SettingEvaluationEnvironment;
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.servlet.ServletContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class InitParamParser
/*     */ {
/*     */   static final String TEMPLATE_PATH_PREFIX_CLASS = "class://";
/*     */   static final String TEMPLATE_PATH_PREFIX_CLASSPATH = "classpath:";
/*     */   static final String TEMPLATE_PATH_PREFIX_FILE = "file://";
/*     */   static final String TEMPLATE_PATH_SETTINGS_BI_NAME = "settings";
/*  50 */   private static final Logger LOG = Logger.getLogger("freemarker.servlet");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static TemplateLoader createTemplateLoader(String templatePath, Configuration cfg, Class classLoaderClass, ServletContext srvCtx) throws IOException {
/*     */     WebappTemplateLoader webappTemplateLoader;
/*  59 */     int settingAssignmentsStart = findTemplatePathSettingAssignmentsStart(templatePath);
/*     */     
/*  61 */     String pureTemplatePath = ((settingAssignmentsStart == -1) ? templatePath : templatePath.substring(0, settingAssignmentsStart)).trim();
/*     */ 
/*     */     
/*  64 */     if (pureTemplatePath.startsWith("class://"))
/*  65 */     { String packagePath = pureTemplatePath.substring("class://".length());
/*  66 */       packagePath = normalizeToAbsolutePackagePath(packagePath);
/*  67 */       ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(classLoaderClass, packagePath); }
/*  68 */     else if (pureTemplatePath.startsWith("classpath:"))
/*     */     
/*  70 */     { String packagePath = pureTemplatePath.substring("classpath:".length());
/*  71 */       packagePath = normalizeToAbsolutePackagePath(packagePath);
/*     */       
/*  73 */       ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
/*  74 */       if (classLoader == null) {
/*  75 */         LOG.warn("No Thread Context Class Loader was found. Falling back to the class loader of " + classLoaderClass
/*  76 */             .getName() + ".");
/*  77 */         classLoader = classLoaderClass.getClassLoader();
/*     */       } 
/*     */       
/*  80 */       ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(classLoader, packagePath); }
/*  81 */     else if (pureTemplatePath.startsWith("file://"))
/*  82 */     { String filePath = pureTemplatePath.substring("file://".length());
/*  83 */       FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File(filePath)); }
/*  84 */     else if (pureTemplatePath.startsWith("[") && cfg
/*  85 */       .getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_22)
/*  86 */     { if (!pureTemplatePath.endsWith("]"))
/*     */       {
/*  88 */         throw new TemplatePathParsingException("Failed to parse template path; closing \"]\" is missing.");
/*     */       }
/*  90 */       String commaSepItems = pureTemplatePath.substring(1, pureTemplatePath.length() - 1).trim();
/*  91 */       List<String> listItems = parseCommaSeparatedTemplatePaths(commaSepItems);
/*  92 */       TemplateLoader[] templateLoaders = new TemplateLoader[listItems.size()];
/*  93 */       for (int i = 0; i < listItems.size(); i++) {
/*  94 */         String pathItem = listItems.get(i);
/*  95 */         templateLoaders[i] = createTemplateLoader(pathItem, cfg, classLoaderClass, srvCtx);
/*     */       } 
/*  97 */       MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaders); }
/*  98 */     else { if (pureTemplatePath.startsWith("{") && cfg
/*  99 */         .getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_22) {
/* 100 */         throw new TemplatePathParsingException("Template paths starting with \"{\" are reseved for future purposes");
/*     */       }
/* 102 */       webappTemplateLoader = new WebappTemplateLoader(srvCtx, pureTemplatePath); }
/*     */ 
/*     */     
/* 105 */     if (settingAssignmentsStart != -1) {
/*     */       try {
/* 107 */         int nextPos = _ObjectBuilderSettingEvaluator.configureBean(templatePath, templatePath
/* 108 */             .indexOf('(', settingAssignmentsStart) + 1, webappTemplateLoader, 
/* 109 */             _SettingEvaluationEnvironment.getCurrent());
/* 110 */         if (nextPos != templatePath.length()) {
/* 111 */           throw new TemplatePathParsingException("Template path should end after the setting list in: " + templatePath);
/*     */         }
/*     */       }
/* 114 */       catch (Exception e) {
/* 115 */         throw new TemplatePathParsingException("Failed to set properties in: " + templatePath, e);
/*     */       } 
/*     */     }
/*     */     
/* 119 */     return (TemplateLoader)webappTemplateLoader;
/*     */   }
/*     */   
/*     */   static String normalizeToAbsolutePackagePath(String path) {
/* 123 */     while (path.startsWith("/")) {
/* 124 */       path = path.substring(1);
/*     */     }
/* 126 */     return "/" + path;
/*     */   }
/*     */   
/*     */   static List parseCommaSeparatedList(String value) throws ParseException {
/* 130 */     List<String> valuesList = new ArrayList();
/* 131 */     String[] values = StringUtil.split(value, ',');
/* 132 */     for (int i = 0; i < values.length; i++) {
/* 133 */       String s = values[i].trim();
/* 134 */       if (s.length() != 0) {
/* 135 */         valuesList.add(s);
/* 136 */       } else if (i != values.length - 1) {
/* 137 */         throw new ParseException("Missing list item berfore a comma", -1);
/*     */       } 
/*     */     } 
/* 140 */     return valuesList;
/*     */   }
/*     */   
/*     */   static List parseCommaSeparatedPatterns(String value) throws ParseException {
/* 144 */     List<String> values = parseCommaSeparatedList(value);
/* 145 */     List<Pattern> patterns = new ArrayList(values.size());
/* 146 */     for (int i = 0; i < values.size(); i++) {
/* 147 */       patterns.add(Pattern.compile(values.get(i)));
/*     */     }
/* 149 */     return patterns;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static List parseCommaSeparatedTemplatePaths(String commaSepItems) {
/* 158 */     List<String> listItems = new ArrayList();
/* 159 */     while (commaSepItems.length() != 0) {
/* 160 */       int itemSettingAssignmentsStart = findTemplatePathSettingAssignmentsStart(commaSepItems);
/* 161 */       int pureItemEnd = (itemSettingAssignmentsStart != -1) ? itemSettingAssignmentsStart : commaSepItems.length();
/* 162 */       int prevComaIdx = commaSepItems.lastIndexOf(',', pureItemEnd - 1);
/* 163 */       int itemStart = (prevComaIdx != -1) ? (prevComaIdx + 1) : 0;
/* 164 */       String item = commaSepItems.substring(itemStart).trim();
/* 165 */       if (item.length() != 0) {
/* 166 */         listItems.add(0, item);
/* 167 */       } else if (listItems.size() > 0) {
/* 168 */         throw new TemplatePathParsingException("Missing list item before a comma");
/*     */       } 
/* 170 */       commaSepItems = (prevComaIdx != -1) ? commaSepItems.substring(0, prevComaIdx).trim() : "";
/*     */     } 
/* 172 */     return listItems;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int findTemplatePathSettingAssignmentsStart(String s) {
/* 179 */     int pos = s.length() - 1;
/*     */ 
/*     */     
/* 182 */     while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
/* 183 */       pos--;
/*     */     }
/*     */ 
/*     */     
/* 187 */     if (pos < 0 || s.charAt(pos) != ')') return -1; 
/* 188 */     pos--;
/*     */ 
/*     */     
/* 191 */     int parLevel = 1;
/* 192 */     int mode = 0;
/* 193 */     while (parLevel > 0) {
/* 194 */       if (pos < 0) return -1; 
/* 195 */       char c = s.charAt(pos);
/* 196 */       switch (mode) {
/*     */         case 0:
/* 198 */           switch (c) { case '(':
/* 199 */               parLevel--; break;
/* 200 */             case ')': parLevel++; break;
/* 201 */             case '\'': mode = 1; break;
/* 202 */             case '"': mode = 2; break; }
/*     */           
/*     */           break;
/*     */         case 1:
/* 206 */           if (c == '\'' && (pos <= 0 || s.charAt(pos - 1) != '\\')) {
/* 207 */             mode = 0;
/*     */           }
/*     */           break;
/*     */         case 2:
/* 211 */           if (c == '"' && (pos <= 0 || s.charAt(pos - 1) != '\\')) {
/* 212 */             mode = 0;
/*     */           }
/*     */           break;
/*     */       } 
/* 216 */       pos--;
/*     */     } 
/*     */ 
/*     */     
/* 220 */     while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
/* 221 */       pos--;
/*     */     }
/*     */     
/* 224 */     int biNameEnd = pos + 1;
/*     */ 
/*     */     
/* 227 */     while (pos >= 0 && Character.isJavaIdentifierPart(s.charAt(pos))) {
/* 228 */       pos--;
/*     */     }
/*     */     
/* 231 */     int biNameStart = pos + 1;
/* 232 */     if (biNameStart == biNameEnd) {
/* 233 */       return -1;
/*     */     }
/* 235 */     String biName = s.substring(biNameStart, biNameEnd);
/*     */ 
/*     */     
/* 238 */     while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
/* 239 */       pos--;
/*     */     }
/*     */ 
/*     */     
/* 243 */     if (pos < 0 || s.charAt(pos) != '?') return -1;
/*     */     
/* 245 */     if (!biName.equals("settings")) {
/* 246 */       throw new TemplatePathParsingException(
/* 247 */           StringUtil.jQuote(biName) + " is unexpected after the \"?\". Expected \"" + "settings" + "\".");
/*     */     }
/*     */ 
/*     */     
/* 251 */     return pos;
/*     */   }
/*     */   
/*     */   private static final class TemplatePathParsingException
/*     */     extends RuntimeException {
/*     */     public TemplatePathParsingException(String message, Throwable cause) {
/* 257 */       super(message, cause);
/*     */     }
/*     */     
/*     */     public TemplatePathParsingException(String message) {
/* 261 */       super(message);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\servlet\InitParamParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */