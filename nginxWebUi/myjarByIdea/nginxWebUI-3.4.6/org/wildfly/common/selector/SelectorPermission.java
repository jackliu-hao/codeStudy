package org.wildfly.common.selector;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;
import java.security.Permission;
import org.wildfly.common.Assert;

public final class SelectorPermission extends BasicPermission {
   private static final long serialVersionUID = -7156787601824624014L;
   private static final int ACTION_GET = 1;
   private static final int ACTION_SET = 2;
   private static final int ACTION_CHANGE = 4;
   private final int actions;

   public SelectorPermission(String name, String actions) {
      super(name);
      Assert.checkNotNullParam("name", name);
      Assert.checkNotNullParam("actions", actions);
      String[] actionArray = actions.split("\\s*,\\s*");
      int q = 0;
      String[] var5 = actionArray;
      int var6 = actionArray.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String action = var5[var7];
         if (action.equalsIgnoreCase("get")) {
            q |= 1;
         } else if (action.equalsIgnoreCase("set")) {
            q |= 2;
         } else if (action.equalsIgnoreCase("change")) {
            q |= 4;
         } else if (action.equals("*")) {
            q |= 7;
            break;
         }
      }

      this.actions = q;
   }

   public String getActions() {
      int maskedActions = this.actions & 7;
      switch (maskedActions) {
         case 0:
            return "";
         case 1:
            return "get";
         case 2:
            return "set";
         case 3:
            return "get,set";
         case 4:
            return "change";
         case 5:
            return "get,change";
         case 6:
            return "set,change";
         case 7:
            return "get,set,change";
         default:
            throw Assert.impossibleSwitchCase(maskedActions);
      }
   }

   public boolean implies(Permission p) {
      return p instanceof SelectorPermission && this.implies((SelectorPermission)p);
   }

   public boolean implies(SelectorPermission p) {
      return p != null && (p.actions & this.actions) == p.actions && super.implies(p);
   }

   public boolean equals(Object p) {
      return p instanceof SelectorPermission && this.equals((SelectorPermission)p);
   }

   public boolean equals(SelectorPermission p) {
      return p != null && p.actions == this.actions && super.equals(p);
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      int actions = this.actions;
      if ((actions & 7) != actions) {
         throw new InvalidObjectException("Invalid permission actions");
      }
   }
}
