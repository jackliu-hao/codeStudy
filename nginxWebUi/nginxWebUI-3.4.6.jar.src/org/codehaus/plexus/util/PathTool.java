/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathTool
/*     */ {
/*     */   public static final String getRelativePath(String basedir, String filename) {
/*  65 */     basedir = uppercaseDrive(basedir);
/*  66 */     filename = uppercaseDrive(filename);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     if (basedir == null || basedir.length() == 0 || filename == null || filename.length() == 0 || !filename.startsWith(basedir))
/*     */     {
/*     */       
/*  75 */       return "";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     String separator = determineSeparator(filename);
/*  84 */     basedir = StringUtils.chompLast(basedir, separator);
/*  85 */     filename = StringUtils.chompLast(filename, separator);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  92 */     String relativeFilename = filename.substring(basedir.length());
/*     */     
/*  94 */     return determineRelativePath(relativeFilename, separator);
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
/*     */   public static final String getRelativePath(String filename) {
/* 117 */     filename = uppercaseDrive(filename);
/*     */     
/* 119 */     if (filename == null || filename.length() == 0)
/*     */     {
/* 121 */       return "";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 131 */     String separator = determineSeparator(filename);
/* 132 */     filename = StringUtils.chompLast(filename, separator);
/* 133 */     if (!filename.startsWith(separator))
/*     */     {
/* 135 */       filename = separator + filename;
/*     */     }
/*     */     
/* 138 */     return determineRelativePath(filename, separator);
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
/*     */   public static final String getDirectoryComponent(String filename) {
/* 163 */     if (filename == null || filename.length() == 0)
/*     */     {
/* 165 */       return "";
/*     */     }
/*     */     
/* 168 */     String separator = determineSeparator(filename);
/* 169 */     String directory = StringUtils.chomp(filename, separator);
/*     */     
/* 171 */     if (filename.equals(directory))
/*     */     {
/* 173 */       return ".";
/*     */     }
/*     */     
/* 176 */     return directory;
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
/*     */   public static final String calculateLink(String link, String relativePath) {
/* 197 */     if (link.startsWith("/site/"))
/*     */     {
/* 199 */       return link.substring(5);
/*     */     }
/*     */ 
/*     */     
/* 203 */     if (link.startsWith("/absolute/"))
/*     */     {
/* 205 */       return link.substring(10);
/*     */     }
/*     */ 
/*     */     
/* 209 */     if (link.indexOf(":") >= 0)
/*     */     {
/* 211 */       return link;
/*     */     }
/*     */ 
/*     */     
/* 215 */     if (relativePath.equals(".")) {
/*     */       
/* 217 */       if (link.startsWith("/"))
/*     */       {
/* 219 */         return link.substring(1);
/*     */       }
/*     */       
/* 222 */       return link;
/*     */     } 
/*     */ 
/*     */     
/* 226 */     if (relativePath.endsWith("/") && link.startsWith("/"))
/*     */     {
/* 228 */       return relativePath + "." + link.substring(1);
/*     */     }
/*     */     
/* 231 */     if (relativePath.endsWith("/") || link.startsWith("/"))
/*     */     {
/* 233 */       return relativePath + link;
/*     */     }
/*     */     
/* 236 */     return relativePath + "/" + link;
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
/*     */   public static final String getRelativeWebPath(String oldPath, String newPath) {
/* 258 */     if (StringUtils.isEmpty(oldPath) || StringUtils.isEmpty(newPath))
/*     */     {
/* 260 */       return "";
/*     */     }
/*     */     
/* 263 */     String resultPath = buildRelativePath(newPath, oldPath, '/');
/*     */     
/* 265 */     if (newPath.endsWith("/") && !resultPath.endsWith("/"))
/*     */     {
/* 267 */       return resultPath + "/";
/*     */     }
/*     */     
/* 270 */     return resultPath;
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
/*     */   public static final String getRelativeFilePath(String oldPath, String newPath) {
/* 296 */     if (StringUtils.isEmpty(oldPath) || StringUtils.isEmpty(newPath))
/*     */     {
/* 298 */       return "";
/*     */     }
/*     */ 
/*     */     
/* 302 */     String fromPath = (new File(oldPath)).getPath();
/* 303 */     String toPath = (new File(newPath)).getPath();
/*     */ 
/*     */     
/* 306 */     if (toPath.matches("^\\[a-zA-Z]:"))
/*     */     {
/* 308 */       toPath = toPath.substring(1);
/*     */     }
/* 310 */     if (fromPath.matches("^\\[a-zA-Z]:"))
/*     */     {
/* 312 */       fromPath = fromPath.substring(1);
/*     */     }
/*     */ 
/*     */     
/* 316 */     if (fromPath.startsWith(":", 1))
/*     */     {
/* 318 */       fromPath = Character.toLowerCase(fromPath.charAt(0)) + fromPath.substring(1);
/*     */     }
/* 320 */     if (toPath.startsWith(":", 1))
/*     */     {
/* 322 */       toPath = Character.toLowerCase(toPath.charAt(0)) + toPath.substring(1);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 327 */     if (toPath.startsWith(":", 1) && fromPath.startsWith(":", 1) && !toPath.substring(0, 1).equals(fromPath.substring(0, 1)))
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 332 */       return null;
/*     */     }
/*     */     
/* 335 */     if ((toPath.startsWith(":", 1) && !fromPath.startsWith(":", 1)) || (!toPath.startsWith(":", 1) && fromPath.startsWith(":", 1)))
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 340 */       return null;
/*     */     }
/*     */     
/* 343 */     String resultPath = buildRelativePath(toPath, fromPath, File.separatorChar);
/*     */     
/* 345 */     if (newPath.endsWith(File.separator) && !resultPath.endsWith(File.separator))
/*     */     {
/* 347 */       return resultPath + File.separator;
/*     */     }
/*     */     
/* 350 */     return resultPath;
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
/*     */   private static final String determineRelativePath(String filename, String separator) {
/* 371 */     if (filename.length() == 0)
/*     */     {
/* 373 */       return "";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 382 */     int slashCount = StringUtils.countMatches(filename, separator) - 1;
/* 383 */     if (slashCount <= 0)
/*     */     {
/* 385 */       return ".";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 393 */     StringBuffer sb = new StringBuffer();
/* 394 */     for (int i = 0; i < slashCount; i++)
/*     */     {
/* 396 */       sb.append("../");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 403 */     return StringUtils.chop(sb.toString());
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
/*     */   private static final String determineSeparator(String filename) {
/* 418 */     int forwardCount = StringUtils.countMatches(filename, "/");
/* 419 */     int backwardCount = StringUtils.countMatches(filename, "\\");
/*     */     
/* 421 */     return (forwardCount >= backwardCount) ? "/" : "\\";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final String uppercaseDrive(String path) {
/* 431 */     if (path == null)
/*     */     {
/* 433 */       return null;
/*     */     }
/* 435 */     if (path.length() >= 2 && path.charAt(1) == ':')
/*     */     {
/* 437 */       path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
/*     */     }
/* 439 */     return path;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String buildRelativePath(String toPath, String fromPath, char separatorChar) {
/* 445 */     StringTokenizer toTokeniser = new StringTokenizer(toPath, String.valueOf(separatorChar));
/* 446 */     StringTokenizer fromTokeniser = new StringTokenizer(fromPath, String.valueOf(separatorChar));
/*     */     
/* 448 */     int count = 0;
/*     */ 
/*     */     
/* 451 */     while (toTokeniser.hasMoreTokens() && fromTokeniser.hasMoreTokens()) {
/*     */       
/* 453 */       if (separatorChar == '\\') {
/*     */         
/* 455 */         if (!fromTokeniser.nextToken().equalsIgnoreCase(toTokeniser.nextToken()))
/*     */         {
/*     */           break;
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 462 */       else if (!fromTokeniser.nextToken().equals(toTokeniser.nextToken())) {
/*     */         break;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 468 */       count++;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 474 */     toTokeniser = new StringTokenizer(toPath, String.valueOf(separatorChar));
/* 475 */     fromTokeniser = new StringTokenizer(fromPath, String.valueOf(separatorChar));
/*     */     
/* 477 */     while (count-- > 0) {
/*     */       
/* 479 */       fromTokeniser.nextToken();
/* 480 */       toTokeniser.nextToken();
/*     */     } 
/*     */     
/* 483 */     String relativePath = "";
/*     */ 
/*     */     
/* 486 */     while (fromTokeniser.hasMoreTokens()) {
/*     */       
/* 488 */       fromTokeniser.nextToken();
/*     */       
/* 490 */       relativePath = relativePath + "..";
/*     */       
/* 492 */       if (fromTokeniser.hasMoreTokens())
/*     */       {
/* 494 */         relativePath = relativePath + separatorChar;
/*     */       }
/*     */     } 
/*     */     
/* 498 */     if (relativePath.length() != 0 && toTokeniser.hasMoreTokens())
/*     */     {
/* 500 */       relativePath = relativePath + separatorChar;
/*     */     }
/*     */ 
/*     */     
/* 504 */     while (toTokeniser.hasMoreTokens()) {
/*     */       
/* 506 */       relativePath = relativePath + toTokeniser.nextToken();
/*     */       
/* 508 */       if (toTokeniser.hasMoreTokens())
/*     */       {
/* 510 */         relativePath = relativePath + separatorChar;
/*     */       }
/*     */     } 
/* 513 */     return relativePath;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\PathTool.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */