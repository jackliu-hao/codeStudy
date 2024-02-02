package org.codehaus.plexus.util;

import java.io.File;

public interface Scanner {
   void setIncludes(String[] var1);

   void setExcludes(String[] var1);

   void addDefaultExcludes();

   void scan();

   String[] getIncludedFiles();

   String[] getIncludedDirectories();

   File getBasedir();
}
