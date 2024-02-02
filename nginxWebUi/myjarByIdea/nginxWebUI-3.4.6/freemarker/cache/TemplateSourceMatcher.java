package freemarker.cache;

import java.io.IOException;

public abstract class TemplateSourceMatcher {
   abstract boolean matches(String var1, Object var2) throws IOException;
}
