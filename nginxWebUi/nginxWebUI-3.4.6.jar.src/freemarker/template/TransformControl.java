package freemarker.template;

import java.io.IOException;

public interface TransformControl {
  public static final int REPEAT_EVALUATION = 0;
  
  public static final int END_EVALUATION = 1;
  
  public static final int SKIP_BODY = 0;
  
  public static final int EVALUATE_BODY = 1;
  
  int onStart() throws TemplateModelException, IOException;
  
  int afterBody() throws TemplateModelException, IOException;
  
  void onError(Throwable paramThrowable) throws Throwable;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TransformControl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */