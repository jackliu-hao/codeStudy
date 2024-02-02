package freemarker.template;

public interface TemplateModelIterator {
  TemplateModel next() throws TemplateModelException;
  
  boolean hasNext() throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateModelIterator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */