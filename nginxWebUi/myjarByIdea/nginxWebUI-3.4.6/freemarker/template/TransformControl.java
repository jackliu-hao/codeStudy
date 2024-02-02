package freemarker.template;

import java.io.IOException;

public interface TransformControl {
   int REPEAT_EVALUATION = 0;
   int END_EVALUATION = 1;
   int SKIP_BODY = 0;
   int EVALUATE_BODY = 1;

   int onStart() throws TemplateModelException, IOException;

   int afterBody() throws TemplateModelException, IOException;

   void onError(Throwable var1) throws Throwable;
}
