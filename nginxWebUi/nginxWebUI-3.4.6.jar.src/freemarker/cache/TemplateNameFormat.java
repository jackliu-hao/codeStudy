/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.template.MalformedTemplateNameException;
/*     */ import freemarker.template.utility.StringUtil;
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
/*     */ public abstract class TemplateNameFormat
/*     */ {
/*     */   private TemplateNameFormat() {}
/*     */   
/*  46 */   public static final TemplateNameFormat DEFAULT_2_3_0 = new Default020300();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 100 */   public static final TemplateNameFormat DEFAULT_2_4_0 = new Default020400();
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
/*     */   private static final class Default020300
/*     */     extends TemplateNameFormat
/*     */   {
/*     */     private Default020300() {}
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
/*     */     String toRootBasedName(String baseName, String targetName) {
/* 153 */       if (targetName.indexOf("://") > 0)
/* 154 */         return targetName; 
/* 155 */       if (targetName.startsWith("/")) {
/* 156 */         int schemeSepIdx = baseName.indexOf("://");
/* 157 */         if (schemeSepIdx > 0) {
/* 158 */           return baseName.substring(0, schemeSepIdx + 2) + targetName;
/*     */         }
/* 160 */         return targetName.substring(1);
/*     */       } 
/*     */       
/* 163 */       if (!baseName.endsWith("/")) {
/* 164 */         baseName = baseName.substring(0, baseName.lastIndexOf("/") + 1);
/*     */       }
/* 166 */       return baseName + targetName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     String normalizeRootBasedName(String name) throws MalformedTemplateNameException {
/* 173 */       TemplateNameFormat.checkNameHasNoNullCharacter(name);
/*     */ 
/*     */ 
/*     */       
/* 177 */       String path = name;
/*     */       
/*     */       while (true) {
/* 180 */         int parentDirPathLoc = path.indexOf("/../");
/* 181 */         if (parentDirPathLoc == 0)
/*     */         {
/*     */           
/* 184 */           throw TemplateNameFormat.newRootLeavingException(name);
/*     */         }
/* 186 */         if (parentDirPathLoc == -1) {
/* 187 */           if (path.startsWith("../")) {
/* 188 */             throw TemplateNameFormat.newRootLeavingException(name);
/*     */           }
/*     */           break;
/*     */         } 
/* 192 */         int previousSlashLoc = path.lastIndexOf('/', parentDirPathLoc - 1);
/*     */         
/* 194 */         path = path.substring(0, previousSlashLoc + 1) + path.substring(parentDirPathLoc + "/../".length());
/*     */       } 
/*     */       while (true) {
/* 197 */         int currentDirPathLoc = path.indexOf("/./");
/* 198 */         if (currentDirPathLoc == -1) {
/* 199 */           if (path.startsWith("./")) {
/* 200 */             path = path.substring("./".length());
/*     */           }
/*     */           
/*     */           break;
/*     */         } 
/* 205 */         path = path.substring(0, currentDirPathLoc) + path.substring(currentDirPathLoc + "/./".length() - 1);
/*     */       } 
/*     */       
/* 208 */       if (path.length() > 1 && path.charAt(0) == '/') {
/* 209 */         path = path.substring(1);
/*     */       }
/* 211 */       return path;
/*     */     }
/*     */ 
/*     */     
/*     */     String rootBasedNameToAbsoluteName(String name) throws MalformedTemplateNameException {
/* 216 */       if (name.indexOf("://") > 0) {
/* 217 */         return name;
/*     */       }
/* 219 */       if (!name.startsWith("/")) {
/* 220 */         return "/" + name;
/*     */       }
/* 222 */       return name;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 227 */       return "TemplateNameFormat.DEFAULT_2_3_0";
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class Default020400 extends TemplateNameFormat {
/*     */     private Default020400() {}
/*     */     
/*     */     String toRootBasedName(String baseName, String targetName) {
/* 235 */       if (findSchemeSectionEnd(targetName) != 0)
/* 236 */         return targetName; 
/* 237 */       if (targetName.startsWith("/")) {
/* 238 */         String targetNameAsRelative = targetName.substring(1);
/* 239 */         int schemeSectionEnd = findSchemeSectionEnd(baseName);
/* 240 */         if (schemeSectionEnd == 0) {
/* 241 */           return targetNameAsRelative;
/*     */         }
/*     */         
/* 244 */         return baseName.substring(0, schemeSectionEnd) + targetNameAsRelative;
/*     */       } 
/*     */       
/* 247 */       if (!baseName.endsWith("/")) {
/*     */         
/* 249 */         int baseEnd = baseName.lastIndexOf("/") + 1;
/* 250 */         if (baseEnd == 0)
/*     */         {
/* 252 */           baseEnd = findSchemeSectionEnd(baseName);
/*     */         }
/* 254 */         baseName = baseName.substring(0, baseEnd);
/*     */       } 
/* 256 */       return baseName + targetName;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     String normalizeRootBasedName(String name) throws MalformedTemplateNameException {
/*     */       String scheme;
/* 263 */       TemplateNameFormat.checkNameHasNoNullCharacter(name);
/*     */       
/* 265 */       if (name.indexOf('\\') != -1) {
/* 266 */         throw new MalformedTemplateNameException(name, "Backslash (\"\\\") is not allowed in template names. Use slash (\"/\") instead.");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 275 */       int schemeSectionEnd = findSchemeSectionEnd(name);
/* 276 */       if (schemeSectionEnd == 0) {
/* 277 */         scheme = null;
/* 278 */         path = name;
/*     */       } else {
/* 280 */         scheme = name.substring(0, schemeSectionEnd);
/* 281 */         path = name.substring(schemeSectionEnd);
/*     */       } 
/*     */ 
/*     */       
/* 285 */       if (path.indexOf(':') != -1) {
/* 286 */         throw new MalformedTemplateNameException(name, "The ':' character can only be used after the scheme name (if there's any), not in the path part");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 291 */       String path = removeRedundantSlashes(path);
/*     */ 
/*     */       
/* 294 */       path = removeDotSteps(path);
/*     */       
/* 296 */       path = resolveDotDotSteps(path, name);
/*     */       
/* 298 */       path = removeRedundantStarSteps(path);
/*     */       
/* 300 */       return (scheme == null) ? path : (scheme + path);
/*     */     }
/*     */     
/*     */     private int findSchemeSectionEnd(String name) {
/* 304 */       int schemeColonIdx = name.indexOf(":");
/* 305 */       if (schemeColonIdx == -1 || name.lastIndexOf('/', schemeColonIdx - 1) != -1) {
/* 306 */         return 0;
/*     */       }
/*     */       
/* 309 */       if (schemeColonIdx + 2 < name.length() && name
/* 310 */         .charAt(schemeColonIdx + 1) == '/' && name.charAt(schemeColonIdx + 2) == '/') {
/* 311 */         return schemeColonIdx + 3;
/*     */       }
/* 313 */       return schemeColonIdx + 1;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String removeRedundantSlashes(String path) {
/*     */       while (true) {
/* 321 */         String prevName = path;
/* 322 */         path = StringUtil.replace(path, "//", "/");
/* 323 */         if (prevName == path)
/* 324 */           return path.startsWith("/") ? path.substring(1) : path; 
/*     */       } 
/*     */     }
/*     */     private String removeDotSteps(String path) {
/* 328 */       int nextFromIdx = path.length() - 1; while (true) {
/*     */         boolean slashRight;
/* 330 */         int dotIdx = path.lastIndexOf('.', nextFromIdx);
/* 331 */         if (dotIdx < 0) {
/* 332 */           return path;
/*     */         }
/* 334 */         nextFromIdx = dotIdx - 1;
/*     */         
/* 336 */         if (dotIdx != 0 && path.charAt(dotIdx - 1) != '/') {
/*     */           continue;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 342 */         if (dotIdx + 1 == path.length()) {
/* 343 */           slashRight = false;
/* 344 */         } else if (path.charAt(dotIdx + 1) == '/') {
/* 345 */           slashRight = true;
/*     */         } else {
/*     */           continue;
/*     */         } 
/*     */ 
/*     */         
/* 351 */         if (slashRight) {
/* 352 */           path = path.substring(0, dotIdx) + path.substring(dotIdx + 2); continue;
/*     */         } 
/* 354 */         path = path.substring(0, path.length() - 1);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String resolveDotDotSteps(String path, String name) throws MalformedTemplateNameException {
/* 363 */       int nextFromIdx = 0; while (true) {
/*     */         boolean slashRight;
/* 365 */         int previousSlashIdx, dotDotIdx = path.indexOf("..", nextFromIdx);
/* 366 */         if (dotDotIdx < 0) {
/* 367 */           return path;
/*     */         }
/*     */         
/* 370 */         if (dotDotIdx == 0)
/* 371 */           throw TemplateNameFormat.newRootLeavingException(name); 
/* 372 */         if (path.charAt(dotDotIdx - 1) != '/') {
/*     */           
/* 374 */           nextFromIdx = dotDotIdx + 3;
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 380 */         if (dotDotIdx + 2 == path.length()) {
/* 381 */           slashRight = false;
/* 382 */         } else if (path.charAt(dotDotIdx + 2) == '/') {
/* 383 */           slashRight = true;
/*     */         } else {
/*     */           
/* 386 */           nextFromIdx = dotDotIdx + 3;
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 391 */         boolean skippedStarStep = false;
/*     */         
/* 393 */         int searchSlashBacwardsFrom = dotDotIdx - 2;
/*     */         while (true) {
/* 395 */           if (searchSlashBacwardsFrom == -1) {
/* 396 */             throw TemplateNameFormat.newRootLeavingException(name);
/*     */           }
/* 398 */           previousSlashIdx = path.lastIndexOf('/', searchSlashBacwardsFrom);
/* 399 */           if (previousSlashIdx == -1) {
/* 400 */             if (searchSlashBacwardsFrom == 0 && path.charAt(0) == '*')
/*     */             {
/* 402 */               throw TemplateNameFormat.newRootLeavingException(name);
/*     */             }
/*     */             break;
/*     */           } 
/* 406 */           if (path.charAt(previousSlashIdx + 1) == '*' && path.charAt(previousSlashIdx + 2) == '/') {
/* 407 */             skippedStarStep = true;
/* 408 */             searchSlashBacwardsFrom = previousSlashIdx - 1;
/*     */ 
/*     */             
/*     */             continue;
/*     */           } 
/*     */ 
/*     */           
/*     */           break;
/*     */         } 
/*     */ 
/*     */         
/* 419 */         path = path.substring(0, previousSlashIdx + 1) + (skippedStarStep ? "*/" : "") + path.substring(dotDotIdx + (slashRight ? 3 : 2));
/* 420 */         nextFromIdx = previousSlashIdx + 1;
/*     */       } 
/*     */     }
/*     */     
/*     */     private String removeRedundantStarSteps(String path) {
/*     */       String prevName;
/*     */       do {
/* 427 */         int supiciousIdx = path.indexOf("*/*");
/* 428 */         if (supiciousIdx == -1) {
/*     */           break;
/*     */         }
/*     */         
/* 432 */         prevName = path;
/*     */ 
/*     */         
/* 435 */         if ((supiciousIdx != 0 && path.charAt(supiciousIdx - 1) != '/') || (supiciousIdx + 3 != path
/* 436 */           .length() && path.charAt(supiciousIdx + 3) != '/'))
/* 437 */           continue;  path = path.substring(0, supiciousIdx) + path.substring(supiciousIdx + 2);
/*     */       }
/* 439 */       while (prevName != path);
/*     */ 
/*     */       
/* 442 */       if (path.startsWith("*")) {
/* 443 */         if (path.length() == 1) {
/* 444 */           path = "";
/* 445 */         } else if (path.charAt(1) == '/') {
/* 446 */           path = path.substring(2);
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 451 */       return path;
/*     */     }
/*     */ 
/*     */     
/*     */     String rootBasedNameToAbsoluteName(String name) throws MalformedTemplateNameException {
/* 456 */       if (findSchemeSectionEnd(name) != 0) {
/* 457 */         return name;
/*     */       }
/* 459 */       if (!name.startsWith("/")) {
/* 460 */         return "/" + name;
/*     */       }
/* 462 */       return name;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 467 */       return "TemplateNameFormat.DEFAULT_2_4_0";
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkNameHasNoNullCharacter(String name) throws MalformedTemplateNameException {
/* 472 */     if (name.indexOf(false) != -1) {
/* 473 */       throw new MalformedTemplateNameException(name, "Null character (\\u0000) in the name; possible attack attempt");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static MalformedTemplateNameException newRootLeavingException(String name) {
/* 479 */     return new MalformedTemplateNameException(name, "Backing out from the root directory is not allowed");
/*     */   }
/*     */   
/*     */   abstract String toRootBasedName(String paramString1, String paramString2) throws MalformedTemplateNameException;
/*     */   
/*     */   abstract String normalizeRootBasedName(String paramString) throws MalformedTemplateNameException;
/*     */   
/*     */   abstract String rootBasedNameToAbsoluteName(String paramString) throws MalformedTemplateNameException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateNameFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */