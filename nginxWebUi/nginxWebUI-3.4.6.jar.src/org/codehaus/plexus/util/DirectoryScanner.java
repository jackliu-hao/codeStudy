/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DirectoryScanner
/*     */   extends AbstractScanner
/*     */ {
/*     */   protected File basedir;
/*     */   protected Vector filesIncluded;
/*     */   protected Vector filesNotIncluded;
/*     */   protected Vector filesExcluded;
/*     */   protected Vector dirsIncluded;
/*     */   protected Vector dirsNotIncluded;
/*     */   protected Vector dirsExcluded;
/*     */   protected Vector filesDeselected;
/*     */   protected Vector dirsDeselected;
/*     */   protected boolean haveSlowResults = false;
/*     */   private boolean followSymlinks = true;
/*     */   protected boolean everythingIncluded = true;
/*     */   
/*     */   public void setBasedir(String basedir) {
/* 224 */     setBasedir(new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
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
/*     */   public void setBasedir(File basedir) {
/* 237 */     this.basedir = basedir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File getBasedir() {
/* 248 */     return this.basedir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFollowSymlinks(boolean followSymlinks) {
/* 258 */     this.followSymlinks = followSymlinks;
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
/*     */   public boolean isEverythingIncluded() {
/* 270 */     return this.everythingIncluded;
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
/*     */   public void scan() throws IllegalStateException {
/* 284 */     if (this.basedir == null)
/*     */     {
/* 286 */       throw new IllegalStateException("No basedir set");
/*     */     }
/* 288 */     if (!this.basedir.exists())
/*     */     {
/* 290 */       throw new IllegalStateException("basedir " + this.basedir + " does not exist");
/*     */     }
/*     */     
/* 293 */     if (!this.basedir.isDirectory())
/*     */     {
/* 295 */       throw new IllegalStateException("basedir " + this.basedir + " is not a directory");
/*     */     }
/*     */ 
/*     */     
/* 299 */     setupDefaultFilters();
/*     */     
/* 301 */     this.filesIncluded = new Vector();
/* 302 */     this.filesNotIncluded = new Vector();
/* 303 */     this.filesExcluded = new Vector();
/* 304 */     this.filesDeselected = new Vector();
/* 305 */     this.dirsIncluded = new Vector();
/* 306 */     this.dirsNotIncluded = new Vector();
/* 307 */     this.dirsExcluded = new Vector();
/* 308 */     this.dirsDeselected = new Vector();
/*     */     
/* 310 */     if (isIncluded("")) {
/*     */       
/* 312 */       if (!isExcluded("")) {
/*     */         
/* 314 */         if (isSelected("", this.basedir))
/*     */         {
/* 316 */           this.dirsIncluded.addElement("");
/*     */         }
/*     */         else
/*     */         {
/* 320 */           this.dirsDeselected.addElement("");
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 325 */         this.dirsExcluded.addElement("");
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 330 */       this.dirsNotIncluded.addElement("");
/*     */     } 
/* 332 */     scandir(this.basedir, "", true);
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
/*     */   protected void slowScan() {
/* 345 */     if (this.haveSlowResults) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 350 */     String[] excl = new String[this.dirsExcluded.size()];
/* 351 */     this.dirsExcluded.copyInto((Object[])excl);
/*     */     
/* 353 */     String[] notIncl = new String[this.dirsNotIncluded.size()];
/* 354 */     this.dirsNotIncluded.copyInto((Object[])notIncl);
/*     */     int i;
/* 356 */     for (i = 0; i < excl.length; i++) {
/*     */       
/* 358 */       if (!couldHoldIncluded(excl[i]))
/*     */       {
/* 360 */         scandir(new File(this.basedir, excl[i]), excl[i] + File.separator, false);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 365 */     for (i = 0; i < notIncl.length; i++) {
/*     */       
/* 367 */       if (!couldHoldIncluded(notIncl[i]))
/*     */       {
/* 369 */         scandir(new File(this.basedir, notIncl[i]), notIncl[i] + File.separator, false);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 374 */     this.haveSlowResults = true;
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
/*     */   protected void scandir(File dir, String vpath, boolean fast) {
/* 400 */     String[] newfiles = dir.list();
/*     */     
/* 402 */     if (newfiles == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 425 */       newfiles = new String[0];
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 430 */     if (!this.followSymlinks) {
/*     */       
/* 432 */       Vector noLinks = new Vector();
/* 433 */       for (int j = 0; j < newfiles.length; j++) {
/*     */ 
/*     */         
/*     */         try {
/* 437 */           if (isSymbolicLink(dir, newfiles[j])) {
/*     */             
/* 439 */             String name = vpath + newfiles[j];
/* 440 */             File file = new File(dir, newfiles[j]);
/* 441 */             if (file.isDirectory())
/*     */             {
/* 443 */               this.dirsExcluded.addElement(name);
/*     */             }
/*     */             else
/*     */             {
/* 447 */               this.filesExcluded.addElement(name);
/*     */             }
/*     */           
/*     */           } else {
/*     */             
/* 452 */             noLinks.addElement(newfiles[j]);
/*     */           }
/*     */         
/* 455 */         } catch (IOException ioe) {
/*     */           
/* 457 */           String msg = "IOException caught while checking for links, couldn't get cannonical path!";
/*     */ 
/*     */           
/* 460 */           System.err.println(msg);
/* 461 */           noLinks.addElement(newfiles[j]);
/*     */         } 
/*     */       } 
/* 464 */       newfiles = new String[noLinks.size()];
/* 465 */       noLinks.copyInto((Object[])newfiles);
/*     */     } 
/*     */     
/* 468 */     for (int i = 0; i < newfiles.length; i++) {
/*     */       
/* 470 */       String name = vpath + newfiles[i];
/* 471 */       File file = new File(dir, newfiles[i]);
/* 472 */       if (file.isDirectory()) {
/*     */         
/* 474 */         if (isIncluded(name)) {
/*     */           
/* 476 */           if (!isExcluded(name))
/*     */           {
/* 478 */             if (isSelected(name, file))
/*     */             {
/* 480 */               this.dirsIncluded.addElement(name);
/* 481 */               if (fast)
/*     */               {
/* 483 */                 scandir(file, name + File.separator, fast);
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 488 */               this.everythingIncluded = false;
/* 489 */               this.dirsDeselected.addElement(name);
/* 490 */               if (fast && couldHoldIncluded(name))
/*     */               {
/* 492 */                 scandir(file, name + File.separator, fast);
/*     */               }
/*     */             }
/*     */           
/*     */           }
/*     */           else
/*     */           {
/* 499 */             this.everythingIncluded = false;
/* 500 */             this.dirsExcluded.addElement(name);
/* 501 */             if (fast && couldHoldIncluded(name))
/*     */             {
/* 503 */               scandir(file, name + File.separator, fast);
/*     */             }
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 509 */           this.everythingIncluded = false;
/* 510 */           this.dirsNotIncluded.addElement(name);
/* 511 */           if (fast && couldHoldIncluded(name))
/*     */           {
/* 513 */             scandir(file, name + File.separator, fast);
/*     */           }
/*     */         } 
/* 516 */         if (!fast)
/*     */         {
/* 518 */           scandir(file, name + File.separator, fast);
/*     */         }
/*     */       }
/* 521 */       else if (file.isFile()) {
/*     */         
/* 523 */         if (isIncluded(name)) {
/*     */           
/* 525 */           if (!isExcluded(name)) {
/*     */             
/* 527 */             if (isSelected(name, file))
/*     */             {
/* 529 */               this.filesIncluded.addElement(name);
/*     */             }
/*     */             else
/*     */             {
/* 533 */               this.everythingIncluded = false;
/* 534 */               this.filesDeselected.addElement(name);
/*     */             }
/*     */           
/*     */           } else {
/*     */             
/* 539 */             this.everythingIncluded = false;
/* 540 */             this.filesExcluded.addElement(name);
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 545 */           this.everythingIncluded = false;
/* 546 */           this.filesNotIncluded.addElement(name);
/*     */         } 
/*     */       } 
/*     */     } 
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
/*     */   protected boolean isSelected(String name, File file) {
/* 562 */     return true;
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
/*     */   public String[] getIncludedFiles() {
/* 575 */     String[] files = new String[this.filesIncluded.size()];
/* 576 */     this.filesIncluded.copyInto((Object[])files);
/* 577 */     return files;
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
/*     */   public String[] getNotIncludedFiles() {
/* 592 */     slowScan();
/* 593 */     String[] files = new String[this.filesNotIncluded.size()];
/* 594 */     this.filesNotIncluded.copyInto((Object[])files);
/* 595 */     return files;
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
/*     */   public String[] getExcludedFiles() {
/* 611 */     slowScan();
/* 612 */     String[] files = new String[this.filesExcluded.size()];
/* 613 */     this.filesExcluded.copyInto((Object[])files);
/* 614 */     return files;
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
/*     */   public String[] getDeselectedFiles() {
/* 630 */     slowScan();
/* 631 */     String[] files = new String[this.filesDeselected.size()];
/* 632 */     this.filesDeselected.copyInto((Object[])files);
/* 633 */     return files;
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
/*     */   public String[] getIncludedDirectories() {
/* 646 */     String[] directories = new String[this.dirsIncluded.size()];
/* 647 */     this.dirsIncluded.copyInto((Object[])directories);
/* 648 */     return directories;
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
/*     */   public String[] getNotIncludedDirectories() {
/* 663 */     slowScan();
/* 664 */     String[] directories = new String[this.dirsNotIncluded.size()];
/* 665 */     this.dirsNotIncluded.copyInto((Object[])directories);
/* 666 */     return directories;
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
/*     */   public String[] getExcludedDirectories() {
/* 682 */     slowScan();
/* 683 */     String[] directories = new String[this.dirsExcluded.size()];
/* 684 */     this.dirsExcluded.copyInto((Object[])directories);
/* 685 */     return directories;
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
/*     */   public String[] getDeselectedDirectories() {
/* 701 */     slowScan();
/* 702 */     String[] directories = new String[this.dirsDeselected.size()];
/* 703 */     this.dirsDeselected.copyInto((Object[])directories);
/* 704 */     return directories;
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
/*     */   public boolean isSymbolicLink(File parent, String name) throws IOException {
/* 722 */     File resolvedParent = new File(parent.getCanonicalPath());
/* 723 */     File toTest = new File(resolvedParent, name);
/* 724 */     return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\DirectoryScanner.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */