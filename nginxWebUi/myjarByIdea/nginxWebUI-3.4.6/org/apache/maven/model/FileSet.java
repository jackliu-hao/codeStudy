package org.apache.maven.model;

import java.io.Serializable;

public class FileSet extends PatternSet implements Serializable, Cloneable {
   private String directory;

   public FileSet clone() {
      try {
         FileSet copy = (FileSet)super.clone();
         return copy;
      } catch (Exception var2) {
         throw (RuntimeException)(new UnsupportedOperationException(this.getClass().getName() + " does not support clone()")).initCause(var2);
      }
   }

   public String getDirectory() {
      return this.directory;
   }

   public void setDirectory(String directory) {
      this.directory = directory;
   }

   public String toString() {
      return "FileSet {directory: " + this.getDirectory() + ", " + super.toString() + "}";
   }
}
