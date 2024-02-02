package org.wildfly.common.context;

import java.security.Permission;
import java.security.PermissionCollection;
import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;
import org.wildfly.common.annotation.NotNull;

public final class ContextPermission extends Permission {
   private static final long serialVersionUID = 2149744699461086708L;
   private static final int ACTION_GET = 1;
   private static final int ACTION_GET_PRIV_SUP = 2;
   private static final int ACTION_GET_GLOBAL_DEF = 4;
   private static final int ACTION_SET_GLOBAL_DEF = 8;
   private static final int ACTION_SET_GLOBAL_DEF_SUP = 16;
   private static final int ACTION_GET_THREAD_DEF = 32;
   private static final int ACTION_SET_THREAD_DEF = 64;
   private static final int ACTION_SET_THREAD_DEF_SUP = 128;
   private static final int ACTION_GET_CLASSLOADER_DEF = 256;
   private static final int ACTION_SET_CLASSLOADER_DEF = 512;
   private static final int ACTION_SET_CLASSLOADER_DEF_SUP = 1024;
   private static final int ACTION_ALL = 2047;
   static final String STR_GET = "get";
   static final String STR_GET_PRIV_SUP = "getPrivilegedSupplier";
   static final String STR_GET_GLOBAL_DEF = "getGlobalDefault";
   static final String STR_SET_GLOBAL_DEF = "setGlobalDefault";
   static final String STR_SET_GLOBAL_DEF_SUP = "setGlobalDefaultSupplier";
   static final String STR_GET_THREAD_DEF = "getThreadDefault";
   static final String STR_SET_THREAD_DEF = "setThreadDefault";
   static final String STR_SET_THREAD_DEF_SUP = "setThreadDefaultSupplier";
   static final String STR_GET_CLASSLOADER_DEF = "getClassLoaderDefault";
   static final String STR_SET_CLASSLOADER_DEF = "setClassLoaderDefault";
   static final String STR_SET_CLASSLOADER_DEF_SUP = "setClassLoaderDefaultSupplier";
   private final transient int actionBits;
   private transient String actionString;

   public ContextPermission(String name, String actions) {
      super(name);
      Assert.checkNotNullParam("name", name);
      Assert.checkNotNullParam("actions", actions);
      this.actionBits = parseActions(actions);
   }

   ContextPermission(String name, int actionBits) {
      super(name);
      Assert.checkNotNullParam("name", name);
      this.actionBits = actionBits & 2047;
   }

   private static int parseActions(String actions) throws IllegalArgumentException {
      int bits = 0;
      int start = 0;
      int idx = actions.indexOf(44);
      if (idx == -1) {
         return parseAction(actions);
      } else {
         do {
            bits |= parseAction(actions.substring(start, idx));
            start = idx + 1;
            idx = actions.indexOf(44, start);
         } while(idx != -1);

         bits |= parseAction(actions.substring(start));
         return bits;
      }
   }

   private static int parseAction(String action) {
      switch (action.trim()) {
         case "get":
            return 1;
         case "getPrivilegedSupplier":
            return 2;
         case "getGlobalDefault":
            return 4;
         case "setGlobalDefault":
            return 8;
         case "setGlobalDefaultSupplier":
            return 16;
         case "getThreadDefault":
            return 32;
         case "setThreadDefault":
            return 64;
         case "setThreadDefaultSupplier":
            return 128;
         case "getClassLoaderDefault":
            return 256;
         case "setClassLoaderDefault":
            return 512;
         case "setClassLoaderDefaultSupplier":
            return 1024;
         case "*":
            return 2047;
         case "":
            return 0;
         default:
            throw CommonMessages.msg.invalidPermissionAction(action);
      }
   }

   public boolean implies(Permission permission) {
      return permission instanceof ContextPermission && this.implies((ContextPermission)permission);
   }

   public boolean implies(ContextPermission permission) {
      return this == permission || permission != null && isSet(this.actionBits, permission.actionBits) && this.impliesName(permission.getName());
   }

   private boolean impliesName(String otherName) {
      String myName = this.getName();
      return myName.equals("*") || myName.equals(otherName);
   }

   static boolean isSet(int mask, int test) {
      return (mask & test) == test;
   }

   public boolean equals(Object obj) {
      return obj instanceof ContextPermission && this.equals((ContextPermission)obj);
   }

   public boolean equals(ContextPermission permission) {
      return this == permission || permission != null && this.actionBits == permission.actionBits && this.getName().equals(permission.getName());
   }

   public int hashCode() {
      return this.getName().hashCode() * 17 + this.actionBits;
   }

   public String getActions() {
      String actionString = this.actionString;
      if (actionString == null) {
         int actionBits = this.actionBits;
         if (isSet(actionBits, 2047)) {
            return this.actionString = "*";
         } else if (actionBits == 0) {
            return this.actionString = "";
         } else {
            StringBuilder b = new StringBuilder();
            if (isSet(actionBits, 1)) {
               b.append("get").append(',');
            }

            if (isSet(actionBits, 2)) {
               b.append("getPrivilegedSupplier").append(',');
            }

            if (isSet(actionBits, 4)) {
               b.append("getGlobalDefault").append(',');
            }

            if (isSet(actionBits, 8)) {
               b.append("setGlobalDefault").append(',');
            }

            if (isSet(actionBits, 16)) {
               b.append("setGlobalDefaultSupplier").append(',');
            }

            if (isSet(actionBits, 32)) {
               b.append("getThreadDefault").append(',');
            }

            if (isSet(actionBits, 64)) {
               b.append("setThreadDefault").append(',');
            }

            if (isSet(actionBits, 128)) {
               b.append("setThreadDefaultSupplier").append(',');
            }

            if (isSet(actionBits, 256)) {
               b.append("getClassLoaderDefault").append(',');
            }

            if (isSet(actionBits, 512)) {
               b.append("setClassLoaderDefault").append(',');
            }

            if (isSet(actionBits, 1024)) {
               b.append("setClassLoaderDefaultSupplier").append(',');
            }

            assert b.length() > 0;

            b.setLength(b.length() - 1);
            return this.actionString = b.toString();
         }
      } else {
         return actionString;
      }
   }

   @NotNull
   public ContextPermission withActions(String actions) {
      return this.withActionBits(parseActions(actions));
   }

   ContextPermission withActionBits(int actionBits) {
      return isSet(this.actionBits, actionBits) ? this : new ContextPermission(this.getName(), this.actionBits | actionBits);
   }

   @NotNull
   public ContextPermission withoutActions(String actions) {
      return this.withoutActionBits(parseActions(actions));
   }

   ContextPermission withoutActionBits(int actionBits) {
      return (actionBits & this.actionBits) == 0 ? this : new ContextPermission(this.getName(), this.actionBits & ~actionBits);
   }

   public PermissionCollection newPermissionCollection() {
      return new ContextPermissionCollection();
   }

   int getActionBits() {
      return this.actionBits;
   }
}
