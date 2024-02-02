package freemarker.template;

import java.io.IOException;
import java.io.Writer;

public interface TemplateDirectiveBody {
   void render(Writer var1) throws TemplateException, IOException;
}
