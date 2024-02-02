/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.ext.beans._MethodUtil;
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.lang.reflect.Member;
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
/*     */ public class _ErrorDescriptionBuilder
/*     */ {
/*  41 */   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
/*     */   
/*     */   private final String description;
/*     */   private final Object[] descriptionParts;
/*     */   private Expression blamed;
/*     */   private boolean showBlamer;
/*     */   private Object tip;
/*     */   private Object[] tips;
/*     */   private Template template;
/*     */   
/*     */   public _ErrorDescriptionBuilder(String description) {
/*  52 */     this.description = description;
/*  53 */     this.descriptionParts = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _ErrorDescriptionBuilder(Object... descriptionParts) {
/*  62 */     this.descriptionParts = descriptionParts;
/*  63 */     this.description = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  68 */     return toString((TemplateElement)null, true);
/*     */   }
/*     */   
/*     */   public String toString(TemplateElement parentElement, boolean showTips) {
/*  72 */     if (this.blamed == null && this.tips == null && this.tip == null && this.descriptionParts == null) return this.description;
/*     */     
/*  74 */     StringBuilder sb = new StringBuilder(200);
/*     */     
/*  76 */     if (parentElement != null && this.blamed != null && this.showBlamer) {
/*     */       try {
/*  78 */         Blaming blaming = findBlaming(parentElement, this.blamed, 0);
/*  79 */         if (blaming != null) {
/*  80 */           sb.append("For ");
/*  81 */           String nss = blaming.blamer.getNodeTypeSymbol();
/*  82 */           char q = (nss.indexOf('"') == -1) ? '"' : '`';
/*  83 */           sb.append(q).append(nss).append(q);
/*  84 */           sb.append(" ").append(blaming.roleOfblamed).append(": ");
/*     */         } 
/*  86 */       } catch (Throwable e) {
/*     */ 
/*     */         
/*  89 */         LOG.error("Error when searching blamer for better error message.", e);
/*     */       } 
/*     */     }
/*     */     
/*  93 */     if (this.description != null) {
/*  94 */       sb.append(this.description);
/*     */     } else {
/*  96 */       appendParts(sb, this.descriptionParts);
/*     */     } 
/*     */     
/*  99 */     String extraTip = null;
/* 100 */     if (this.blamed != null) {
/*     */       
/* 102 */       for (int idx = sb.length() - 1; idx >= 0 && Character.isWhitespace(sb.charAt(idx)); idx--) {
/* 103 */         sb.deleteCharAt(idx);
/*     */       }
/*     */       
/* 106 */       char lastChar = (sb.length() > 0) ? sb.charAt(sb.length() - 1) : Character.MIN_VALUE;
/* 107 */       if (lastChar != '\000') {
/* 108 */         sb.append('\n');
/*     */       }
/* 110 */       if (lastChar != ':') {
/* 111 */         sb.append("The blamed expression:\n");
/*     */       }
/*     */       
/* 114 */       String[] lines = splitToLines(this.blamed.toString());
/* 115 */       for (int i = 0; i < lines.length; i++) {
/* 116 */         sb.append((i == 0) ? "==> " : "\n    ");
/* 117 */         sb.append(lines[i]);
/*     */       } 
/*     */       
/* 120 */       sb.append("  [");
/* 121 */       sb.append(this.blamed.getStartLocation());
/* 122 */       sb.append(']');
/*     */ 
/*     */       
/* 125 */       if (containsSingleInterpolatoinLiteral(this.blamed, 0)) {
/* 126 */         extraTip = "It has been noticed that you are using ${...} as the sole content of a quoted string. That does nothing but forcably converts the value inside ${...} to string (as it inserts it into the enclosing string). If that's not what you meant, just remove the quotation marks, ${ and }; you don't need them. If you indeed wanted to convert to string, use myExpression?string instead.";
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 134 */     if (showTips) {
/* 135 */       Object[] allTips; int allTipsLen = ((this.tips != null) ? this.tips.length : 0) + ((this.tip != null) ? 1 : 0) + ((extraTip != null) ? 1 : 0);
/*     */       
/* 137 */       if (this.tips != null && allTipsLen == this.tips.length) {
/* 138 */         allTips = this.tips;
/*     */       } else {
/* 140 */         allTips = new Object[allTipsLen];
/* 141 */         int dst = 0;
/* 142 */         if (this.tip != null) allTips[dst++] = this.tip; 
/* 143 */         if (this.tips != null) {
/* 144 */           for (int i = 0; i < this.tips.length; i++) {
/* 145 */             allTips[dst++] = this.tips[i];
/*     */           }
/*     */         }
/* 148 */         if (extraTip != null) allTips[dst++] = extraTip; 
/*     */       } 
/* 150 */       if (allTips != null && allTips.length > 0) {
/* 151 */         sb.append("\n\n");
/* 152 */         for (int i = 0; i < allTips.length; i++) {
/* 153 */           if (i != 0) sb.append('\n'); 
/* 154 */           sb.append("----").append('\n');
/* 155 */           sb.append("Tip: ");
/* 156 */           Object tip = allTips[i];
/* 157 */           if (!(tip instanceof Object[])) {
/* 158 */             sb.append(allTips[i]);
/*     */           } else {
/* 160 */             appendParts(sb, (Object[])tip);
/*     */           } 
/*     */         } 
/* 163 */         sb.append('\n').append("----");
/*     */       } 
/*     */     } 
/*     */     
/* 167 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private boolean containsSingleInterpolatoinLiteral(Expression exp, int recursionDepth) {
/* 171 */     if (exp == null) return false;
/*     */ 
/*     */     
/* 174 */     if (recursionDepth > 20) return false;
/*     */     
/* 176 */     if (exp instanceof StringLiteral && ((StringLiteral)exp).isSingleInterpolationLiteral()) return true;
/*     */     
/* 178 */     int paramCnt = exp.getParameterCount();
/* 179 */     for (int i = 0; i < paramCnt; i++) {
/* 180 */       Object paramValue = exp.getParameterValue(i);
/* 181 */       if (paramValue instanceof Expression) {
/* 182 */         boolean result = containsSingleInterpolatoinLiteral((Expression)paramValue, recursionDepth + 1);
/* 183 */         if (result) return true;
/*     */       
/*     */       } 
/*     */     } 
/* 187 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private Blaming findBlaming(TemplateObject parent, Expression blamed, int recursionDepth) {
/* 192 */     if (recursionDepth > 50) return null;
/*     */     
/* 194 */     int paramCnt = parent.getParameterCount();
/* 195 */     for (int i = 0; i < paramCnt; i++) {
/* 196 */       Object paramValue = parent.getParameterValue(i);
/* 197 */       if (paramValue == blamed) {
/* 198 */         Blaming blaming = new Blaming();
/* 199 */         blaming.blamer = parent;
/* 200 */         blaming.roleOfblamed = parent.getParameterRole(i);
/* 201 */         return blaming;
/* 202 */       }  if (paramValue instanceof TemplateObject) {
/* 203 */         Blaming blaming = findBlaming((TemplateObject)paramValue, blamed, recursionDepth + 1);
/* 204 */         if (blaming != null) return blaming; 
/*     */       } 
/*     */     } 
/* 207 */     return null;
/*     */   }
/*     */   
/*     */   private void appendParts(StringBuilder sb, Object[] parts) {
/* 211 */     Template template = (this.template != null) ? this.template : ((this.blamed != null) ? this.blamed.getTemplate() : null);
/* 212 */     for (int i = 0; i < parts.length; i++) {
/* 213 */       Object partObj = parts[i];
/* 214 */       if (partObj instanceof Object[]) {
/* 215 */         appendParts(sb, (Object[])partObj);
/*     */       } else {
/*     */         
/* 218 */         String partStr = tryToString(partObj);
/* 219 */         if (partStr == null) {
/* 220 */           partStr = "null";
/*     */         }
/*     */         
/* 223 */         if (template != null) {
/* 224 */           if (partStr.length() > 4 && partStr
/* 225 */             .charAt(0) == '<' && (partStr
/*     */             
/* 227 */             .charAt(1) == '#' || partStr.charAt(1) == '@' || (partStr
/* 228 */             .charAt(1) == '/' && (partStr.charAt(2) == '#' || partStr.charAt(2) == '@'))) && partStr
/*     */             
/* 230 */             .charAt(partStr.length() - 1) == '>') {
/* 231 */             if (template.getActualTagSyntax() == 2) {
/* 232 */               sb.append('[');
/* 233 */               sb.append(partStr.substring(1, partStr.length() - 1));
/* 234 */               sb.append(']');
/*     */             } else {
/* 236 */               sb.append(partStr);
/*     */             } 
/*     */           } else {
/* 239 */             sb.append(partStr);
/*     */           } 
/*     */         } else {
/* 242 */           sb.append(partStr);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(Object partObj) {
/* 252 */     return toString(partObj, false);
/*     */   }
/*     */   
/*     */   public static String tryToString(Object partObj) {
/* 256 */     return toString(partObj, true);
/*     */   }
/*     */   
/*     */   private static String toString(Object partObj, boolean suppressToStringException) {
/*     */     String partStr;
/* 261 */     if (partObj == null)
/* 262 */       return null; 
/* 263 */     if (partObj instanceof Class) {
/* 264 */       partStr = ClassUtil.getShortClassName((Class)partObj);
/* 265 */     } else if (partObj instanceof java.lang.reflect.Method || partObj instanceof java.lang.reflect.Constructor) {
/* 266 */       partStr = _MethodUtil.toString((Member)partObj);
/*     */     } else {
/* 268 */       partStr = suppressToStringException ? StringUtil.tryToString(partObj) : partObj.toString();
/*     */     } 
/* 270 */     return partStr;
/*     */   }
/*     */   
/*     */   private String[] splitToLines(String s) {
/* 274 */     s = StringUtil.replace(s, "\r\n", "\n");
/* 275 */     s = StringUtil.replace(s, "\r", "\n");
/* 276 */     String[] lines = StringUtil.split(s, '\n');
/* 277 */     return lines;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _ErrorDescriptionBuilder template(Template template) {
/* 284 */     this.template = template;
/* 285 */     return this;
/*     */   }
/*     */   
/*     */   public _ErrorDescriptionBuilder blame(Expression blamedExpr) {
/* 289 */     this.blamed = blamedExpr;
/* 290 */     return this;
/*     */   }
/*     */   
/*     */   public _ErrorDescriptionBuilder showBlamer(boolean showBlamer) {
/* 294 */     this.showBlamer = showBlamer;
/* 295 */     return this;
/*     */   }
/*     */   
/*     */   public _ErrorDescriptionBuilder tip(String tip) {
/* 299 */     tip(tip);
/* 300 */     return this;
/*     */   }
/*     */   
/*     */   public _ErrorDescriptionBuilder tip(Object... tip) {
/* 304 */     tip(tip);
/* 305 */     return this;
/*     */   }
/*     */   
/*     */   private _ErrorDescriptionBuilder tip(Object tip) {
/* 309 */     if (tip == null) {
/* 310 */       return this;
/*     */     }
/*     */     
/* 313 */     if (this.tip == null) {
/* 314 */       this.tip = tip;
/*     */     }
/* 316 */     else if (this.tips == null) {
/* 317 */       this.tips = new Object[] { tip };
/*     */     } else {
/* 319 */       int origTipsLen = this.tips.length;
/*     */       
/* 321 */       Object[] newTips = new Object[origTipsLen + 1];
/* 322 */       for (int i = 0; i < origTipsLen; i++) {
/* 323 */         newTips[i] = this.tips[i];
/*     */       }
/* 325 */       newTips[origTipsLen] = tip;
/* 326 */       this.tips = newTips;
/*     */     } 
/*     */     
/* 329 */     return this;
/*     */   }
/*     */   
/*     */   public _ErrorDescriptionBuilder tips(Object... tips) {
/* 333 */     if (tips == null || tips.length == 0) {
/* 334 */       return this;
/*     */     }
/*     */     
/* 337 */     if (this.tips == null) {
/* 338 */       this.tips = tips;
/*     */     } else {
/* 340 */       int origTipsLen = this.tips.length;
/* 341 */       int additionalTipsLen = tips.length;
/*     */       
/* 343 */       Object[] newTips = new Object[origTipsLen + additionalTipsLen]; int i;
/* 344 */       for (i = 0; i < origTipsLen; i++) {
/* 345 */         newTips[i] = this.tips[i];
/*     */       }
/* 347 */       for (i = 0; i < additionalTipsLen; i++) {
/* 348 */         newTips[origTipsLen + i] = tips[i];
/*     */       }
/* 350 */       this.tips = newTips;
/*     */     } 
/* 352 */     return this;
/*     */   }
/*     */   
/*     */   private static class Blaming {
/*     */     TemplateObject blamer;
/*     */     ParameterRole roleOfblamed;
/*     */     
/*     */     private Blaming() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_ErrorDescriptionBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */