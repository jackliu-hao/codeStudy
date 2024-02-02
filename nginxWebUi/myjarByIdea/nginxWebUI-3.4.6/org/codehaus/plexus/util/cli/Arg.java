package org.codehaus.plexus.util.cli;

import java.io.File;

public interface Arg {
   void setValue(String var1);

   void setLine(String var1);

   void setFile(File var1);

   String[] getParts();
}
