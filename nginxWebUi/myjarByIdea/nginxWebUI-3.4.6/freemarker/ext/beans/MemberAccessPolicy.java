package freemarker.ext.beans;

public interface MemberAccessPolicy {
   ClassMemberAccessPolicy forClass(Class<?> var1);

   boolean isToStringAlwaysExposed();
}
