/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class PathTemplate
/*     */   implements Comparable<PathTemplate>
/*     */ {
/*     */   private final String templateString;
/*     */   private final boolean template;
/*     */   private final String base;
/*     */   final List<Part> parts;
/*     */   private final Set<String> parameterNames;
/*     */   private final boolean trailingSlash;
/*     */   
/*     */   private PathTemplate(String templateString, boolean template, String base, List<Part> parts, Set<String> parameterNames, boolean trailingSlash) {
/*  53 */     this.templateString = templateString;
/*  54 */     this.template = template;
/*  55 */     this.base = base;
/*  56 */     this.parts = parts;
/*  57 */     this.parameterNames = Collections.unmodifiableSet(parameterNames);
/*  58 */     this.trailingSlash = trailingSlash;
/*     */   }
/*     */   
/*     */   public static PathTemplate create(String inputPath) {
/*     */     Part part;
/*  63 */     if (inputPath == null) {
/*  64 */       throw UndertowMessages.MESSAGES.pathMustBeSpecified();
/*     */     }
/*     */ 
/*     */     
/*  68 */     if (!inputPath.startsWith("/")) {
/*  69 */       return create("/" + inputPath);
/*     */     }
/*     */ 
/*     */     
/*  73 */     String path = inputPath;
/*     */     
/*  75 */     int state = 0;
/*  76 */     String base = "";
/*  77 */     List<Part> parts = new ArrayList<>();
/*  78 */     int stringStart = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  86 */     for (int i = 0; i < path.length(); i++) {
/*  87 */       int c = path.charAt(i);
/*  88 */       switch (state) {
/*     */         case 0:
/*  90 */           if (c == 47) {
/*  91 */             state = 1; break;
/*  92 */           }  if (c == 42) {
/*  93 */             base = path.substring(0, i + 1);
/*  94 */             stringStart = i;
/*  95 */             state = 5; break;
/*     */           } 
/*  97 */           state = 0;
/*     */           break;
/*     */ 
/*     */         
/*     */         case 1:
/* 102 */           if (c == 123) {
/* 103 */             base = path.substring(0, i);
/* 104 */             stringStart = i + 1;
/* 105 */             state = 2; break;
/* 106 */           }  if (c == 42) {
/* 107 */             base = path.substring(0, i + 1);
/* 108 */             stringStart = i;
/* 109 */             state = 5; break;
/* 110 */           }  if (c != 47) {
/* 111 */             state = 0;
/*     */           }
/*     */           break;
/*     */         
/*     */         case 2:
/* 116 */           if (c == 125) {
/* 117 */             Part part1 = new Part(true, path.substring(stringStart, i));
/* 118 */             parts.add(part1);
/* 119 */             stringStart = i;
/* 120 */             state = 3;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 3:
/* 125 */           if (c == 47) {
/* 126 */             state = 4; break;
/*     */           } 
/* 128 */           throw UndertowMessages.MESSAGES.couldNotParseUriTemplate(path, i);
/*     */ 
/*     */ 
/*     */         
/*     */         case 4:
/* 133 */           if (c == 123) {
/* 134 */             stringStart = i + 1;
/* 135 */             state = 2; break;
/* 136 */           }  if (c != 47) {
/* 137 */             stringStart = i;
/* 138 */             state = 5;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 5:
/* 143 */           if (c == 47) {
/* 144 */             Part part1 = new Part(false, path.substring(stringStart, i));
/* 145 */             parts.add(part1);
/* 146 */             stringStart = i + 1;
/* 147 */             state = 4;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 153 */     boolean trailingSlash = false;
/* 154 */     switch (state) {
/*     */       case 1:
/* 156 */         trailingSlash = true;
/*     */       
/*     */       case 0:
/* 159 */         base = path;
/*     */         break;
/*     */       
/*     */       case 2:
/* 163 */         throw UndertowMessages.MESSAGES.couldNotParseUriTemplate(path, path.length());
/*     */       
/*     */       case 4:
/* 166 */         trailingSlash = true;
/*     */         break;
/*     */       
/*     */       case 5:
/* 170 */         part = new Part(false, path.substring(stringStart));
/* 171 */         parts.add(part);
/*     */         break;
/*     */     } 
/*     */     
/* 175 */     Set<String> templates = new HashSet<>();
/* 176 */     for (Part part1 : parts) {
/* 177 */       if (part1.template) {
/* 178 */         templates.add(part1.part);
/*     */       }
/*     */     } 
/* 181 */     return new PathTemplate(path, (state > 1 && !base.contains("*")), base, parts, templates, trailingSlash);
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
/*     */   public boolean matches(String path, Map<String, String> pathParameters) {
/* 197 */     if (!this.template && this.base.contains("*")) {
/* 198 */       int indexOf = this.base.indexOf("*");
/* 199 */       String startBase = this.base.substring(0, indexOf);
/* 200 */       if (!path.startsWith(startBase)) {
/* 201 */         return false;
/*     */       }
/* 203 */       pathParameters.put("*", path.substring(indexOf, path.length()));
/* 204 */       return true;
/*     */     } 
/*     */ 
/*     */     
/* 208 */     if (!path.startsWith(this.base)) {
/* 209 */       return false;
/*     */     }
/* 211 */     int baseLength = this.base.length();
/* 212 */     if (!this.template) {
/* 213 */       return (path.length() == baseLength);
/*     */     }
/* 215 */     if (this.trailingSlash)
/*     */     {
/*     */ 
/*     */       
/* 219 */       if (path.charAt(path.length() - 1) != '/') {
/* 220 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 224 */     int currentPartPosition = 0;
/* 225 */     Part current = this.parts.get(currentPartPosition);
/* 226 */     int stringStart = baseLength;
/*     */     int i;
/* 228 */     for (i = baseLength; i < path.length(); i++) {
/* 229 */       char currentChar = path.charAt(i);
/* 230 */       if (currentChar == '?' || current.part.equals("*"))
/*     */         break; 
/* 232 */       if (currentChar == '/') {
/* 233 */         String str = path.substring(stringStart, i);
/* 234 */         if (current.template) {
/* 235 */           pathParameters.put(current.part, str);
/* 236 */         } else if (!str.equals(current.part)) {
/* 237 */           pathParameters.clear();
/* 238 */           return false;
/*     */         } 
/* 240 */         currentPartPosition++;
/* 241 */         if (currentPartPosition == this.parts.size())
/*     */         {
/* 243 */           return (i == path.length() - 1);
/*     */         }
/* 245 */         current = this.parts.get(currentPartPosition);
/* 246 */         stringStart = i + 1;
/*     */       } 
/*     */     } 
/* 249 */     if (currentPartPosition + 1 != this.parts.size()) {
/* 250 */       pathParameters.clear();
/* 251 */       return false;
/*     */     } 
/*     */     
/* 254 */     String result = path.substring(stringStart, i);
/* 255 */     if (current.part.equals("*")) {
/* 256 */       pathParameters.put(current.part, path.substring(stringStart, path.length()));
/* 257 */       return true;
/*     */     } 
/* 259 */     if (current.template) {
/* 260 */       pathParameters.put(current.part, result);
/* 261 */     } else if (!result.equals(current.part)) {
/* 262 */       pathParameters.clear();
/* 263 */       return false;
/*     */     } 
/* 265 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 270 */     if (this == o) return true; 
/* 271 */     if (!(o instanceof PathTemplate)) return false;
/*     */     
/* 273 */     PathTemplate that = (PathTemplate)o;
/*     */     
/* 275 */     return (compareTo(that) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 281 */     int result = (getTemplateString() != null) ? getTemplateString().hashCode() : 0;
/* 282 */     result = 31 * result + (this.template ? 1 : 0);
/* 283 */     result = 31 * result + ((getBase() != null) ? getBase().hashCode() : 0);
/* 284 */     result = 31 * result + ((this.parts != null) ? this.parts.hashCode() : 0);
/* 285 */     result = 31 * result + ((getParameterNames() != null) ? getParameterNames().hashCode() : 0);
/* 286 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(PathTemplate o) {
/* 295 */     if (this.template && !o.template)
/* 296 */       return 1; 
/* 297 */     if (o.template && !this.template) {
/* 298 */       return -1;
/*     */     }
/*     */     
/* 301 */     int res = this.base.compareTo(o.base);
/* 302 */     if (res > 0)
/*     */     {
/* 304 */       return -1; } 
/* 305 */     if (res < 0)
/* 306 */       return 1; 
/* 307 */     if (!this.template)
/*     */     {
/* 309 */       return 0;
/*     */     }
/*     */ 
/*     */     
/* 313 */     int i = 0;
/*     */     while (true) {
/* 315 */       if (this.parts.size() == i) {
/* 316 */         if (o.parts.size() == i) {
/* 317 */           return this.base.compareTo(o.base);
/*     */         }
/* 319 */         return 1;
/* 320 */       }  if (o.parts.size() == i)
/*     */       {
/* 322 */         return -1;
/*     */       }
/* 324 */       Part thisPath = this.parts.get(i);
/* 325 */       Part otherPart = o.parts.get(i);
/* 326 */       if (thisPath.template && !otherPart.template)
/*     */       {
/* 328 */         return 1; } 
/* 329 */       if (!thisPath.template && otherPart.template)
/* 330 */         return -1; 
/* 331 */       if (!thisPath.template) {
/* 332 */         int r = thisPath.part.compareTo(otherPart.part);
/* 333 */         if (r != 0) {
/* 334 */           return r;
/*     */         }
/*     */       } 
/* 337 */       i++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getBase() {
/* 342 */     return this.base;
/*     */   }
/*     */   
/*     */   public String getTemplateString() {
/* 346 */     return this.templateString;
/*     */   }
/*     */   
/*     */   public Set<String> getParameterNames() {
/* 350 */     return this.parameterNames;
/*     */   }
/*     */   
/*     */   private static class Part {
/*     */     final boolean template;
/*     */     final String part;
/*     */     
/*     */     private Part(boolean template, String part) {
/* 358 */       this.template = template;
/* 359 */       this.part = part;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 364 */       return "Part{template=" + this.template + ", part='" + this.part + '\'' + '}';
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 373 */     return "PathTemplate{template=" + this.template + ", base='" + this.base + '\'' + ", parts=" + this.parts + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\PathTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */