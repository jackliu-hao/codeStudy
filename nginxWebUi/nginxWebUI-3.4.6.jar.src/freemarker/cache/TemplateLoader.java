package freemarker.cache;

import java.io.IOException;
import java.io.Reader;

public interface TemplateLoader {
  Object findTemplateSource(String paramString) throws IOException;
  
  long getLastModified(Object paramObject);
  
  Reader getReader(Object paramObject, String paramString) throws IOException;
  
  void closeTemplateSource(Object paramObject) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */