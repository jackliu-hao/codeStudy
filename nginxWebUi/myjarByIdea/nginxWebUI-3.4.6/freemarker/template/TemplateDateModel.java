package freemarker.template;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public interface TemplateDateModel extends TemplateModel {
   int UNKNOWN = 0;
   int TIME = 1;
   int DATE = 2;
   int DATETIME = 3;
   List TYPE_NAMES = Collections.unmodifiableList(Arrays.asList("UNKNOWN", "TIME", "DATE", "DATETIME"));

   Date getAsDate() throws TemplateModelException;

   int getDateType();
}
