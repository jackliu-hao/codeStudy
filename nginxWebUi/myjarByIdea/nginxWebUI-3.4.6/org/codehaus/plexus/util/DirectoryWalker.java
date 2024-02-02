package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class DirectoryWalker {
   private File baseDir;
   private int baseDirOffset;
   private Stack dirStack;
   private List excludes = new ArrayList();
   private List includes = new ArrayList();
   private boolean isCaseSensitive = true;
   private List listeners = new ArrayList();
   private boolean debugEnabled = false;

   public void addDirectoryWalkListener(DirectoryWalkListener listener) {
      this.listeners.add(listener);
   }

   public void addExclude(String exclude) {
      this.excludes.add(this.fixPattern(exclude));
   }

   public void addInclude(String include) {
      this.includes.add(this.fixPattern(include));
   }

   public void addSCMExcludes() {
      String[] scmexcludes = DirectoryScanner.DEFAULTEXCLUDES;

      for(int i = 0; i < scmexcludes.length; ++i) {
         this.addExclude(scmexcludes[i]);
      }

   }

   private void fireStep(File file) {
      DirStackEntry dsEntry = (DirStackEntry)this.dirStack.peek();
      int percentage = dsEntry.getPercentage();
      Iterator it = this.listeners.iterator();

      while(it.hasNext()) {
         DirectoryWalkListener listener = (DirectoryWalkListener)it.next();
         listener.directoryWalkStep(percentage, file);
      }

   }

   private void fireWalkFinished() {
      Iterator it = this.listeners.iterator();

      while(it.hasNext()) {
         DirectoryWalkListener listener = (DirectoryWalkListener)it.next();
         listener.directoryWalkFinished();
      }

   }

   private void fireWalkStarting() {
      Iterator it = this.listeners.iterator();

      while(it.hasNext()) {
         DirectoryWalkListener listener = (DirectoryWalkListener)it.next();
         listener.directoryWalkStarting(this.baseDir);
      }

   }

   private void fireDebugMessage(String message) {
      Iterator it = this.listeners.iterator();

      while(it.hasNext()) {
         DirectoryWalkListener listener = (DirectoryWalkListener)it.next();
         listener.debug(message);
      }

   }

   private String fixPattern(String pattern) {
      String cleanPattern = pattern;
      if (File.separatorChar != '/') {
         cleanPattern = pattern.replace('/', File.separatorChar);
      }

      if (File.separatorChar != '\\') {
         cleanPattern = cleanPattern.replace('\\', File.separatorChar);
      }

      return cleanPattern;
   }

   public void setDebugMode(boolean debugEnabled) {
      this.debugEnabled = debugEnabled;
   }

   public File getBaseDir() {
      return this.baseDir;
   }

   public List getExcludes() {
      return this.excludes;
   }

   public List getIncludes() {
      return this.includes;
   }

   private boolean isExcluded(String name) {
      return this.isMatch(this.excludes, name);
   }

   private boolean isIncluded(String name) {
      return this.isMatch(this.includes, name);
   }

   private boolean isMatch(List patterns, String name) {
      Iterator it = patterns.iterator();

      String pattern;
      do {
         if (!it.hasNext()) {
            return false;
         }

         pattern = (String)it.next();
      } while(!SelectorUtils.matchPath(pattern, name, this.isCaseSensitive));

      return true;
   }

   private String relativeToBaseDir(File file) {
      return file.getAbsolutePath().substring(this.baseDirOffset + 1);
   }

   public void removeDirectoryWalkListener(DirectoryWalkListener listener) {
      this.listeners.remove(listener);
   }

   public void scan() {
      if (this.baseDir == null) {
         throw new IllegalStateException("Scan Failure.  BaseDir not specified.");
      } else if (!this.baseDir.exists()) {
         throw new IllegalStateException("Scan Failure.  BaseDir does not exist.");
      } else if (!this.baseDir.isDirectory()) {
         throw new IllegalStateException("Scan Failure.  BaseDir is not a directory.");
      } else {
         if (this.includes.isEmpty()) {
            this.addInclude("**");
         }

         if (this.debugEnabled) {
            StringBuffer dbg = new StringBuffer();
            dbg.append("DirectoryWalker Scan");
            dbg.append("\n  Base Dir: ").append(this.baseDir.getAbsolutePath());
            dbg.append("\n  Includes: ");
            Iterator it = this.includes.iterator();

            String exclude;
            while(it.hasNext()) {
               exclude = (String)it.next();
               dbg.append("\n    - \"").append(exclude).append("\"");
            }

            dbg.append("\n  Excludes: ");
            it = this.excludes.iterator();

            while(it.hasNext()) {
               exclude = (String)it.next();
               dbg.append("\n    - \"").append(exclude).append("\"");
            }

            this.fireDebugMessage(dbg.toString());
         }

         this.fireWalkStarting();
         this.dirStack = new Stack();
         this.scanDir(this.baseDir);
         this.fireWalkFinished();
      }
   }

   private void scanDir(File dir) {
      File[] files = dir.listFiles();
      if (files != null) {
         DirStackEntry curStackEntry = new DirStackEntry(dir, files.length);
         if (this.dirStack.isEmpty()) {
            curStackEntry.percentageOffset = 0.0;
            curStackEntry.percentageSize = 100.0;
         } else {
            DirStackEntry previousStackEntry = (DirStackEntry)this.dirStack.peek();
            curStackEntry.percentageOffset = previousStackEntry.getNextPercentageOffset();
            curStackEntry.percentageSize = previousStackEntry.getNextPercentageSize();
         }

         this.dirStack.push(curStackEntry);

         for(int idx = 0; idx < files.length; ++idx) {
            curStackEntry.index = idx;
            String name = this.relativeToBaseDir(files[idx]);
            if (this.isExcluded(name)) {
               this.fireDebugMessage(name + " is excluded.");
            } else if (files[idx].isDirectory()) {
               this.scanDir(files[idx]);
            } else if (this.isIncluded(name)) {
               this.fireStep(files[idx]);
            }
         }

         this.dirStack.pop();
      }
   }

   public void setBaseDir(File baseDir) {
      this.baseDir = baseDir;
      this.baseDirOffset = baseDir.getAbsolutePath().length();
   }

   public void setExcludes(List entries) {
      this.excludes.clear();
      if (entries != null) {
         Iterator it = entries.iterator();

         while(it.hasNext()) {
            String pattern = (String)it.next();
            this.excludes.add(this.fixPattern(pattern));
         }
      }

   }

   public void setIncludes(List entries) {
      this.includes.clear();
      if (entries != null) {
         Iterator it = entries.iterator();

         while(it.hasNext()) {
            String pattern = (String)it.next();
            this.includes.add(this.fixPattern(pattern));
         }
      }

   }

   class DirStackEntry {
      public int count;
      public File dir;
      public int index;
      public double percentageOffset;
      public double percentageSize;

      public DirStackEntry(File d, int length) {
         this.dir = d;
         this.count = length;
      }

      public double getNextPercentageOffset() {
         return this.percentageOffset + (double)this.index * (this.percentageSize / (double)this.count);
      }

      public double getNextPercentageSize() {
         return this.percentageSize / (double)this.count;
      }

      public int getPercentage() {
         double percentageWithinDir = (double)this.index / (double)this.count;
         return (int)Math.floor(this.percentageOffset + percentageWithinDir * this.percentageSize);
      }

      public String toString() {
         return "DirStackEntry[dir=" + this.dir.getAbsolutePath() + ",count=" + this.count + ",index=" + this.index + ",percentageOffset=" + this.percentageOffset + ",percentageSize=" + this.percentageSize + ",percentage()=" + this.getPercentage() + ",getNextPercentageOffset()=" + this.getNextPercentageOffset() + ",getNextPercentageSize()=" + this.getNextPercentageSize() + "]";
      }
   }
}
