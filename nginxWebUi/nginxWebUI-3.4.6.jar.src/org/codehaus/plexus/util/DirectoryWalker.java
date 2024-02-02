/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DirectoryWalker
/*     */ {
/*     */   private File baseDir;
/*     */   private int baseDirOffset;
/*     */   private Stack dirStack;
/*     */   private List excludes;
/*     */   private List includes;
/*     */   
/*     */   class DirStackEntry
/*     */   {
/*     */     public int count;
/*     */     public File dir;
/*     */     public int index;
/*     */     public double percentageOffset;
/*     */     public double percentageSize;
/*     */     private final DirectoryWalker this$0;
/*     */     
/*     */     public DirStackEntry(DirectoryWalker this$0, File d, int length) {
/*  68 */       this.this$0 = this$0;
/*  69 */       this.dir = d;
/*  70 */       this.count = length;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double getNextPercentageOffset() {
/*  81 */       return this.percentageOffset + this.index * this.percentageSize / this.count;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double getNextPercentageSize() {
/*  92 */       return this.percentageSize / this.count;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPercentage() {
/* 103 */       double percentageWithinDir = this.index / this.count;
/* 104 */       return (int)Math.floor(this.percentageOffset + percentageWithinDir * this.percentageSize);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 109 */       return "DirStackEntry[dir=" + this.dir.getAbsolutePath() + ",count=" + this.count + ",index=" + this.index + ",percentageOffset=" + this.percentageOffset + ",percentageSize=" + this.percentageSize + ",percentage()=" + getPercentage() + ",getNextPercentageOffset()=" + getNextPercentageOffset() + ",getNextPercentageSize()=" + getNextPercentageSize() + "]";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isCaseSensitive = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List listeners;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean debugEnabled = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DirectoryWalker() {
/* 134 */     this.includes = new ArrayList();
/* 135 */     this.excludes = new ArrayList();
/* 136 */     this.listeners = new ArrayList();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addDirectoryWalkListener(DirectoryWalkListener listener) {
/* 141 */     this.listeners.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addExclude(String exclude) {
/* 146 */     this.excludes.add(fixPattern(exclude));
/*     */   }
/*     */ 
/*     */   
/*     */   public void addInclude(String include) {
/* 151 */     this.includes.add(fixPattern(include));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addSCMExcludes() {
/* 159 */     String[] scmexcludes = DirectoryScanner.DEFAULTEXCLUDES;
/* 160 */     for (int i = 0; i < scmexcludes.length; i++)
/*     */     {
/* 162 */       addExclude(scmexcludes[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void fireStep(File file) {
/* 168 */     DirStackEntry dsEntry = this.dirStack.peek();
/* 169 */     int percentage = dsEntry.getPercentage();
/* 170 */     Iterator it = this.listeners.iterator();
/* 171 */     while (it.hasNext()) {
/*     */       
/* 173 */       DirectoryWalkListener listener = it.next();
/* 174 */       listener.directoryWalkStep(percentage, file);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void fireWalkFinished() {
/* 180 */     Iterator it = this.listeners.iterator();
/* 181 */     while (it.hasNext()) {
/*     */       
/* 183 */       DirectoryWalkListener listener = it.next();
/* 184 */       listener.directoryWalkFinished();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void fireWalkStarting() {
/* 190 */     Iterator it = this.listeners.iterator();
/* 191 */     while (it.hasNext()) {
/*     */       
/* 193 */       DirectoryWalkListener listener = it.next();
/* 194 */       listener.directoryWalkStarting(this.baseDir);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void fireDebugMessage(String message) {
/* 200 */     Iterator it = this.listeners.iterator();
/* 201 */     while (it.hasNext()) {
/*     */       
/* 203 */       DirectoryWalkListener listener = it.next();
/* 204 */       listener.debug(message);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private String fixPattern(String pattern) {
/* 210 */     String cleanPattern = pattern;
/*     */     
/* 212 */     if (File.separatorChar != '/')
/*     */     {
/* 214 */       cleanPattern = cleanPattern.replace('/', File.separatorChar);
/*     */     }
/*     */     
/* 217 */     if (File.separatorChar != '\\')
/*     */     {
/* 219 */       cleanPattern = cleanPattern.replace('\\', File.separatorChar);
/*     */     }
/*     */     
/* 222 */     return cleanPattern;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDebugMode(boolean debugEnabled) {
/* 227 */     this.debugEnabled = debugEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File getBaseDir() {
/* 235 */     return this.baseDir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getExcludes() {
/* 243 */     return this.excludes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getIncludes() {
/* 251 */     return this.includes;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isExcluded(String name) {
/* 256 */     return isMatch(this.excludes, name);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isIncluded(String name) {
/* 261 */     return isMatch(this.includes, name);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isMatch(List patterns, String name) {
/* 266 */     Iterator it = patterns.iterator();
/* 267 */     while (it.hasNext()) {
/*     */       
/* 269 */       String pattern = it.next();
/* 270 */       if (SelectorUtils.matchPath(pattern, name, this.isCaseSensitive))
/*     */       {
/* 272 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 276 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private String relativeToBaseDir(File file) {
/* 281 */     return file.getAbsolutePath().substring(this.baseDirOffset + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeDirectoryWalkListener(DirectoryWalkListener listener) {
/* 291 */     this.listeners.remove(listener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void scan() {
/* 299 */     if (this.baseDir == null)
/*     */     {
/* 301 */       throw new IllegalStateException("Scan Failure.  BaseDir not specified.");
/*     */     }
/*     */     
/* 304 */     if (!this.baseDir.exists())
/*     */     {
/* 306 */       throw new IllegalStateException("Scan Failure.  BaseDir does not exist.");
/*     */     }
/*     */     
/* 309 */     if (!this.baseDir.isDirectory())
/*     */     {
/* 311 */       throw new IllegalStateException("Scan Failure.  BaseDir is not a directory.");
/*     */     }
/*     */     
/* 314 */     if (this.includes.isEmpty())
/*     */     {
/*     */       
/* 317 */       addInclude("**");
/*     */     }
/*     */     
/* 320 */     if (this.debugEnabled) {
/*     */ 
/*     */       
/* 323 */       StringBuffer dbg = new StringBuffer();
/* 324 */       dbg.append("DirectoryWalker Scan");
/* 325 */       dbg.append("\n  Base Dir: ").append(this.baseDir.getAbsolutePath());
/* 326 */       dbg.append("\n  Includes: ");
/* 327 */       Iterator it = this.includes.iterator();
/* 328 */       while (it.hasNext()) {
/*     */         
/* 330 */         String include = it.next();
/* 331 */         dbg.append("\n    - \"").append(include).append("\"");
/*     */       } 
/* 333 */       dbg.append("\n  Excludes: ");
/* 334 */       it = this.excludes.iterator();
/* 335 */       while (it.hasNext()) {
/*     */         
/* 337 */         String exclude = it.next();
/* 338 */         dbg.append("\n    - \"").append(exclude).append("\"");
/*     */       } 
/* 340 */       fireDebugMessage(dbg.toString());
/*     */     } 
/*     */     
/* 343 */     fireWalkStarting();
/* 344 */     this.dirStack = new Stack();
/* 345 */     scanDir(this.baseDir);
/* 346 */     fireWalkFinished();
/*     */   }
/*     */ 
/*     */   
/*     */   private void scanDir(File dir) {
/* 351 */     File[] files = dir.listFiles();
/*     */     
/* 353 */     if (files == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 358 */     DirStackEntry curStackEntry = new DirStackEntry(this, dir, files.length);
/* 359 */     if (this.dirStack.isEmpty()) {
/*     */       
/* 361 */       curStackEntry.percentageOffset = 0.0D;
/* 362 */       curStackEntry.percentageSize = 100.0D;
/*     */     }
/*     */     else {
/*     */       
/* 366 */       DirStackEntry previousStackEntry = this.dirStack.peek();
/* 367 */       curStackEntry.percentageOffset = previousStackEntry.getNextPercentageOffset();
/* 368 */       curStackEntry.percentageSize = previousStackEntry.getNextPercentageSize();
/*     */     } 
/*     */     
/* 371 */     this.dirStack.push(curStackEntry);
/*     */     
/* 373 */     for (int idx = 0; idx < files.length; idx++) {
/*     */       
/* 375 */       curStackEntry.index = idx;
/* 376 */       String name = relativeToBaseDir(files[idx]);
/*     */       
/* 378 */       if (isExcluded(name)) {
/*     */         
/* 380 */         fireDebugMessage(name + " is excluded.");
/*     */ 
/*     */       
/*     */       }
/* 384 */       else if (files[idx].isDirectory()) {
/*     */         
/* 386 */         scanDir(files[idx]);
/*     */ 
/*     */       
/*     */       }
/* 390 */       else if (isIncluded(name)) {
/*     */         
/* 392 */         fireStep(files[idx]);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 397 */     this.dirStack.pop();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBaseDir(File baseDir) {
/* 405 */     this.baseDir = baseDir;
/* 406 */     this.baseDirOffset = baseDir.getAbsolutePath().length();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExcludes(List entries) {
/* 414 */     this.excludes.clear();
/* 415 */     if (entries != null) {
/*     */       
/* 417 */       Iterator it = entries.iterator();
/* 418 */       while (it.hasNext()) {
/*     */         
/* 420 */         String pattern = it.next();
/* 421 */         this.excludes.add(fixPattern(pattern));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncludes(List entries) {
/* 431 */     this.includes.clear();
/* 432 */     if (entries != null) {
/*     */       
/* 434 */       Iterator it = entries.iterator();
/* 435 */       while (it.hasNext()) {
/*     */         
/* 437 */         String pattern = it.next();
/* 438 */         this.includes.add(fixPattern(pattern));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\DirectoryWalker.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */