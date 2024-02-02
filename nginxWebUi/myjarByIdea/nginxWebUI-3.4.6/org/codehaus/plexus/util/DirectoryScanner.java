package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class DirectoryScanner extends AbstractScanner {
   protected File basedir;
   protected Vector filesIncluded;
   protected Vector filesNotIncluded;
   protected Vector filesExcluded;
   protected Vector dirsIncluded;
   protected Vector dirsNotIncluded;
   protected Vector dirsExcluded;
   protected Vector filesDeselected;
   protected Vector dirsDeselected;
   protected boolean haveSlowResults = false;
   private boolean followSymlinks = true;
   protected boolean everythingIncluded = true;

   public void setBasedir(String basedir) {
      this.setBasedir(new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
   }

   public void setBasedir(File basedir) {
      this.basedir = basedir;
   }

   public File getBasedir() {
      return this.basedir;
   }

   public void setFollowSymlinks(boolean followSymlinks) {
      this.followSymlinks = followSymlinks;
   }

   public boolean isEverythingIncluded() {
      return this.everythingIncluded;
   }

   public void scan() throws IllegalStateException {
      if (this.basedir == null) {
         throw new IllegalStateException("No basedir set");
      } else if (!this.basedir.exists()) {
         throw new IllegalStateException("basedir " + this.basedir + " does not exist");
      } else if (!this.basedir.isDirectory()) {
         throw new IllegalStateException("basedir " + this.basedir + " is not a directory");
      } else {
         this.setupDefaultFilters();
         this.filesIncluded = new Vector();
         this.filesNotIncluded = new Vector();
         this.filesExcluded = new Vector();
         this.filesDeselected = new Vector();
         this.dirsIncluded = new Vector();
         this.dirsNotIncluded = new Vector();
         this.dirsExcluded = new Vector();
         this.dirsDeselected = new Vector();
         if (this.isIncluded("")) {
            if (!this.isExcluded("")) {
               if (this.isSelected("", this.basedir)) {
                  this.dirsIncluded.addElement("");
               } else {
                  this.dirsDeselected.addElement("");
               }
            } else {
               this.dirsExcluded.addElement("");
            }
         } else {
            this.dirsNotIncluded.addElement("");
         }

         this.scandir(this.basedir, "", true);
      }
   }

   protected void slowScan() {
      if (!this.haveSlowResults) {
         String[] excl = new String[this.dirsExcluded.size()];
         this.dirsExcluded.copyInto(excl);
         String[] notIncl = new String[this.dirsNotIncluded.size()];
         this.dirsNotIncluded.copyInto(notIncl);

         int i;
         for(i = 0; i < excl.length; ++i) {
            if (!this.couldHoldIncluded(excl[i])) {
               this.scandir(new File(this.basedir, excl[i]), excl[i] + File.separator, false);
            }
         }

         for(i = 0; i < notIncl.length; ++i) {
            if (!this.couldHoldIncluded(notIncl[i])) {
               this.scandir(new File(this.basedir, notIncl[i]), notIncl[i] + File.separator, false);
            }
         }

         this.haveSlowResults = true;
      }
   }

   protected void scandir(File dir, String vpath, boolean fast) {
      String[] newfiles = dir.list();
      if (newfiles == null) {
         newfiles = new String[0];
      }

      if (!this.followSymlinks) {
         Vector noLinks = new Vector();

         for(int i = 0; i < newfiles.length; ++i) {
            try {
               if (this.isSymbolicLink(dir, newfiles[i])) {
                  String name = vpath + newfiles[i];
                  File file = new File(dir, newfiles[i]);
                  if (file.isDirectory()) {
                     this.dirsExcluded.addElement(name);
                  } else {
                     this.filesExcluded.addElement(name);
                  }
               } else {
                  noLinks.addElement(newfiles[i]);
               }
            } catch (IOException var9) {
               String msg = "IOException caught while checking for links, couldn't get cannonical path!";
               System.err.println(msg);
               noLinks.addElement(newfiles[i]);
            }
         }

         newfiles = new String[noLinks.size()];
         noLinks.copyInto(newfiles);
      }

      for(int i = 0; i < newfiles.length; ++i) {
         String name = vpath + newfiles[i];
         File file = new File(dir, newfiles[i]);
         if (file.isDirectory()) {
            if (this.isIncluded(name)) {
               if (!this.isExcluded(name)) {
                  if (this.isSelected(name, file)) {
                     this.dirsIncluded.addElement(name);
                     if (fast) {
                        this.scandir(file, name + File.separator, fast);
                     }
                  } else {
                     this.everythingIncluded = false;
                     this.dirsDeselected.addElement(name);
                     if (fast && this.couldHoldIncluded(name)) {
                        this.scandir(file, name + File.separator, fast);
                     }
                  }
               } else {
                  this.everythingIncluded = false;
                  this.dirsExcluded.addElement(name);
                  if (fast && this.couldHoldIncluded(name)) {
                     this.scandir(file, name + File.separator, fast);
                  }
               }
            } else {
               this.everythingIncluded = false;
               this.dirsNotIncluded.addElement(name);
               if (fast && this.couldHoldIncluded(name)) {
                  this.scandir(file, name + File.separator, fast);
               }
            }

            if (!fast) {
               this.scandir(file, name + File.separator, fast);
            }
         } else if (file.isFile()) {
            if (this.isIncluded(name)) {
               if (!this.isExcluded(name)) {
                  if (this.isSelected(name, file)) {
                     this.filesIncluded.addElement(name);
                  } else {
                     this.everythingIncluded = false;
                     this.filesDeselected.addElement(name);
                  }
               } else {
                  this.everythingIncluded = false;
                  this.filesExcluded.addElement(name);
               }
            } else {
               this.everythingIncluded = false;
               this.filesNotIncluded.addElement(name);
            }
         }
      }

   }

   protected boolean isSelected(String name, File file) {
      return true;
   }

   public String[] getIncludedFiles() {
      String[] files = new String[this.filesIncluded.size()];
      this.filesIncluded.copyInto(files);
      return files;
   }

   public String[] getNotIncludedFiles() {
      this.slowScan();
      String[] files = new String[this.filesNotIncluded.size()];
      this.filesNotIncluded.copyInto(files);
      return files;
   }

   public String[] getExcludedFiles() {
      this.slowScan();
      String[] files = new String[this.filesExcluded.size()];
      this.filesExcluded.copyInto(files);
      return files;
   }

   public String[] getDeselectedFiles() {
      this.slowScan();
      String[] files = new String[this.filesDeselected.size()];
      this.filesDeselected.copyInto(files);
      return files;
   }

   public String[] getIncludedDirectories() {
      String[] directories = new String[this.dirsIncluded.size()];
      this.dirsIncluded.copyInto(directories);
      return directories;
   }

   public String[] getNotIncludedDirectories() {
      this.slowScan();
      String[] directories = new String[this.dirsNotIncluded.size()];
      this.dirsNotIncluded.copyInto(directories);
      return directories;
   }

   public String[] getExcludedDirectories() {
      this.slowScan();
      String[] directories = new String[this.dirsExcluded.size()];
      this.dirsExcluded.copyInto(directories);
      return directories;
   }

   public String[] getDeselectedDirectories() {
      this.slowScan();
      String[] directories = new String[this.dirsDeselected.size()];
      this.dirsDeselected.copyInto(directories);
      return directories;
   }

   public boolean isSymbolicLink(File parent, String name) throws IOException {
      File resolvedParent = new File(parent.getCanonicalPath());
      File toTest = new File(resolvedParent, name);
      return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
   }
}
