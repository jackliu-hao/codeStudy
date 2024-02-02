package freemarker.cache;

import java.io.IOException;
import java.io.Reader;

public interface TemplateLoader {
   Object findTemplateSource(String var1) throws IOException;

   long getLastModified(Object var1);

   Reader getReader(Object var1, String var2) throws IOException;

   void closeTemplateSource(Object var1) throws IOException;
}
