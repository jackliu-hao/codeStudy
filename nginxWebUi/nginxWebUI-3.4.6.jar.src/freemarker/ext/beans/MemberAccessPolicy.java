package freemarker.ext.beans;

public interface MemberAccessPolicy {
  ClassMemberAccessPolicy forClass(Class<?> paramClass);
  
  boolean isToStringAlwaysExposed();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\MemberAccessPolicy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */