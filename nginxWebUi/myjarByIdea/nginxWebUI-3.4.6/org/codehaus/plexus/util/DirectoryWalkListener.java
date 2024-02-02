package org.codehaus.plexus.util;

import java.io.File;

public interface DirectoryWalkListener {
   void directoryWalkStarting(File var1);

   void directoryWalkStep(int var1, File var2);

   void directoryWalkFinished();

   void debug(String var1);
}
