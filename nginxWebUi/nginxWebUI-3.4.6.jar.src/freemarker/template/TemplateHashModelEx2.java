package freemarker.template;

public interface TemplateHashModelEx2 extends TemplateHashModelEx {
  KeyValuePairIterator keyValuePairIterator() throws TemplateModelException;
  
  public static interface KeyValuePairIterator {
    boolean hasNext() throws TemplateModelException;
    
    TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException;
  }
  
  public static interface KeyValuePair {
    TemplateModel getKey() throws TemplateModelException;
    
    TemplateModel getValue() throws TemplateModelException;
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateHashModelEx2.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */