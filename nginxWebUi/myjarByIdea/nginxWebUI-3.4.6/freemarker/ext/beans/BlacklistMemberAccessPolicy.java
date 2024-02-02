package freemarker.ext.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

public class BlacklistMemberAccessPolicy extends MemberSelectorListMemberAccessPolicy {
   private final boolean toStringAlwaysExposed;

   public BlacklistMemberAccessPolicy(Collection<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> memberSelectors) {
      super(memberSelectors, MemberSelectorListMemberAccessPolicy.ListType.BLACKLIST, (Class)null);
      boolean toStringBlacklistedAnywhere = false;
      Iterator var3 = memberSelectors.iterator();

      while(var3.hasNext()) {
         MemberSelectorListMemberAccessPolicy.MemberSelector memberSelector = (MemberSelectorListMemberAccessPolicy.MemberSelector)var3.next();
         Method method = memberSelector.getMethod();
         if (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0) {
            toStringBlacklistedAnywhere = true;
            break;
         }
      }

      this.toStringAlwaysExposed = !toStringBlacklistedAnywhere;
   }

   public boolean isToStringAlwaysExposed() {
      return this.toStringAlwaysExposed;
   }
}
