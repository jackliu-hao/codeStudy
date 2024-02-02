package org.apache.maven.model;

public interface InputLocationTracker {
   InputLocation getLocation(Object var1);

   void setLocation(Object var1, InputLocation var2);
}
