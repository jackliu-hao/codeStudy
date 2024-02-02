/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Os
/*     */ {
/*     */   public static final String FAMILY_DOS = "dos";
/*     */   public static final String FAMILY_MAC = "mac";
/*     */   public static final String FAMILY_NETWARE = "netware";
/*     */   public static final String FAMILY_OS2 = "os/2";
/*     */   public static final String FAMILY_TANDEM = "tandem";
/*     */   public static final String FAMILY_UNIX = "unix";
/*     */   public static final String FAMILY_WINDOWS = "windows";
/*     */   public static final String FAMILY_WIN9X = "win9x";
/*     */   public static final String FAMILY_ZOS = "z/os";
/*     */   public static final String FAMILY_OS400 = "os/400";
/*     */   public static final String FAMILY_OPENVMS = "openvms";
/*  97 */   private static final Set validFamilies = setValidFamilies();
/*     */ 
/*     */   
/* 100 */   private static final String PATH_SEP = System.getProperty("path.separator");
/*     */   
/* 102 */   public static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
/*     */   
/* 104 */   public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);
/*     */   
/* 106 */   public static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);
/*     */ 
/*     */   
/* 109 */   public static final String OS_FAMILY = getOsFamily();
/*     */ 
/*     */ 
/*     */   
/*     */   private String family;
/*     */ 
/*     */ 
/*     */   
/*     */   private String name;
/*     */ 
/*     */ 
/*     */   
/*     */   private String version;
/*     */ 
/*     */   
/*     */   private String arch;
/*     */ 
/*     */ 
/*     */   
/*     */   public Os() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public Os(String family) {
/* 133 */     setFamily(family);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Set setValidFamilies() {
/* 141 */     Set valid = new HashSet();
/* 142 */     valid.add("dos");
/* 143 */     valid.add("mac");
/* 144 */     valid.add("netware");
/* 145 */     valid.add("os/2");
/* 146 */     valid.add("tandem");
/* 147 */     valid.add("unix");
/* 148 */     valid.add("windows");
/* 149 */     valid.add("win9x");
/* 150 */     valid.add("z/os");
/* 151 */     valid.add("os/400");
/* 152 */     valid.add("openvms");
/*     */     
/* 154 */     return valid;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFamily(String f) {
/* 178 */     this.family = f.toLowerCase(Locale.US);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setName(String name) {
/* 188 */     this.name = name.toLowerCase(Locale.US);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setArch(String arch) {
/* 198 */     this.arch = arch.toLowerCase(Locale.US);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVersion(String version) {
/* 208 */     this.version = version.toLowerCase(Locale.US);
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
/*     */   public boolean eval() throws Exception {
/* 220 */     return isOs(this.family, this.name, this.arch, this.version);
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
/*     */   public static boolean isFamily(String family) {
/* 233 */     return isOs(family, null, null, null);
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
/*     */   public static boolean isName(String name) {
/* 246 */     return isOs(null, name, null, null);
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
/*     */   public static boolean isArch(String arch) {
/* 259 */     return isOs(null, null, arch, null);
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
/*     */   public static boolean isVersion(String version) {
/* 272 */     return isOs(null, null, null, version);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isOs(String family, String name, String arch, String version) {
/* 292 */     boolean retValue = false;
/*     */     
/* 294 */     if (family != null || name != null || arch != null || version != null) {
/*     */ 
/*     */       
/* 297 */       boolean isFamily = true;
/* 298 */       boolean isName = true;
/* 299 */       boolean isArch = true;
/* 300 */       boolean isVersion = true;
/*     */       
/* 302 */       if (family != null)
/*     */       {
/* 304 */         if (family.equalsIgnoreCase("windows")) {
/*     */           
/* 306 */           isFamily = (OS_NAME.indexOf("windows") > -1);
/*     */         }
/* 308 */         else if (family.equalsIgnoreCase("os/2")) {
/*     */           
/* 310 */           isFamily = (OS_NAME.indexOf("os/2") > -1);
/*     */         }
/* 312 */         else if (family.equalsIgnoreCase("netware")) {
/*     */           
/* 314 */           isFamily = (OS_NAME.indexOf("netware") > -1);
/*     */         }
/* 316 */         else if (family.equalsIgnoreCase("dos")) {
/*     */           
/* 318 */           isFamily = (PATH_SEP.equals(";") && !isFamily("netware"));
/*     */         }
/* 320 */         else if (family.equalsIgnoreCase("mac")) {
/*     */           
/* 322 */           isFamily = (OS_NAME.indexOf("mac") > -1);
/*     */         }
/* 324 */         else if (family.equalsIgnoreCase("tandem")) {
/*     */           
/* 326 */           isFamily = (OS_NAME.indexOf("nonstop_kernel") > -1);
/*     */         }
/* 328 */         else if (family.equalsIgnoreCase("unix")) {
/*     */           
/* 330 */           isFamily = (PATH_SEP.equals(":") && !isFamily("openvms") && (!isFamily("mac") || OS_NAME.endsWith("x")));
/*     */         
/*     */         }
/* 333 */         else if (family.equalsIgnoreCase("win9x")) {
/*     */           
/* 335 */           isFamily = (isFamily("windows") && (OS_NAME.indexOf("95") >= 0 || OS_NAME.indexOf("98") >= 0 || OS_NAME.indexOf("me") >= 0 || OS_NAME.indexOf("ce") >= 0));
/*     */ 
/*     */         
/*     */         }
/* 339 */         else if (family.equalsIgnoreCase("z/os")) {
/*     */           
/* 341 */           isFamily = (OS_NAME.indexOf("z/os") > -1 || OS_NAME.indexOf("os/390") > -1);
/*     */         }
/* 343 */         else if (family.equalsIgnoreCase("os/400")) {
/*     */           
/* 345 */           isFamily = (OS_NAME.indexOf("os/400") > -1);
/*     */         }
/* 347 */         else if (family.equalsIgnoreCase("openvms")) {
/*     */           
/* 349 */           isFamily = (OS_NAME.indexOf("openvms") > -1);
/*     */         }
/*     */         else {
/*     */           
/* 353 */           isFamily = (OS_NAME.indexOf(family.toLowerCase(Locale.US)) > -1);
/*     */         } 
/*     */       }
/* 356 */       if (name != null)
/*     */       {
/* 358 */         isName = name.toLowerCase(Locale.US).equals(OS_NAME);
/*     */       }
/* 360 */       if (arch != null)
/*     */       {
/* 362 */         isArch = arch.toLowerCase(Locale.US).equals(OS_ARCH);
/*     */       }
/* 364 */       if (version != null)
/*     */       {
/* 366 */         isVersion = version.toLowerCase(Locale.US).equals(OS_VERSION);
/*     */       }
/* 368 */       retValue = (isFamily && isName && isArch && isVersion);
/*     */     } 
/* 370 */     return retValue;
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
/*     */   private static String getOsFamily() {
/* 384 */     Set families = null;
/* 385 */     if (!validFamilies.isEmpty()) {
/*     */       
/* 387 */       families = validFamilies;
/*     */     }
/*     */     else {
/*     */       
/* 391 */       families = setValidFamilies();
/*     */     } 
/* 393 */     Iterator iter = families.iterator();
/* 394 */     while (iter.hasNext()) {
/*     */       
/* 396 */       String fam = iter.next();
/* 397 */       if (isFamily(fam))
/*     */       {
/* 399 */         return fam;
/*     */       }
/*     */     } 
/* 402 */     return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValidFamily(String theFamily) {
/* 428 */     return validFamilies.contains(theFamily);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set getValidFamilies() {
/* 437 */     return new HashSet(validFamilies);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\Os.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */