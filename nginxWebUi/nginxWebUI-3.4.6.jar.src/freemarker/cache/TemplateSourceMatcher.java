package freemarker.cache;

import java.io.IOException;

public abstract class TemplateSourceMatcher {
  abstract boolean matches(String paramString, Object paramObject) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateSourceMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */