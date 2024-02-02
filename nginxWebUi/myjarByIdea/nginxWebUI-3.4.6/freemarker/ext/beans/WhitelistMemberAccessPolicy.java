package freemarker.ext.beans;

import java.lang.reflect.Method;
import java.util.Collection;

public class WhitelistMemberAccessPolicy extends MemberSelectorListMemberAccessPolicy {
   private static final Method TO_STRING_METHOD;
   private final boolean toStringAlwaysExposed;

   public WhitelistMemberAccessPolicy(Collection<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> memberSelectors) {
      super(memberSelectors, MemberSelectorListMemberAccessPolicy.ListType.WHITELIST, TemplateAccessible.class);
      this.toStringAlwaysExposed = this.forClass(Object.class).isMethodExposed(TO_STRING_METHOD);
   }

   public boolean isToStringAlwaysExposed() {
      return this.toStringAlwaysExposed;
   }

   static {
      try {
         TO_STRING_METHOD = Object.class.getMethod("toString");
      } catch (NoSuchMethodException var1) {
         throw new IllegalStateException(var1);
      }
   }
}
