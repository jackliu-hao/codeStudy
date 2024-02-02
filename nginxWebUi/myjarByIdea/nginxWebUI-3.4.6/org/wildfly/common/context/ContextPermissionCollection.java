package org.wildfly.common.context;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;

final class ContextPermissionCollection extends PermissionCollection {
   private static final long serialVersionUID = -3651721703337368351L;
   private volatile State state;
   private static final AtomicReferenceFieldUpdater<ContextPermissionCollection, State> stateUpdater = AtomicReferenceFieldUpdater.newUpdater(ContextPermissionCollection.class, State.class, "state");
   private static final State emptyState = new State((ContextPermission)null, Collections.emptyMap());

   ContextPermissionCollection() {
      this.state = emptyState;
   }

   public void add(Permission permission) throws SecurityException, IllegalArgumentException {
      Assert.checkNotNullParam("permission", permission);
      if (permission instanceof ContextPermission) {
         this.add((ContextPermission)permission);
      } else {
         throw CommonMessages.msg.invalidPermissionType(ContextPermission.class, permission.getClass());
      }
   }

   public void add(ContextPermission contextPermission) throws SecurityException {
      Assert.checkNotNullParam("contextPermission", contextPermission);
      if (this.isReadOnly()) {
         throw CommonMessages.msg.readOnlyPermissionCollection();
      } else {
         int actionBits = contextPermission.getActionBits();
         if (actionBits != 0) {
            String name = contextPermission.getName();

            State oldState;
            State newState;
            do {
               oldState = this.state;
               ContextPermission oldGlobalPermission = oldState.globalPermission;
               int globalActions = oldGlobalPermission == null ? 0 : oldGlobalPermission.getActionBits();
               if (oldGlobalPermission != null && oldGlobalPermission.implies(contextPermission)) {
                  return;
               }

               Map<String, ContextPermission> oldPermissions = oldState.permissions;
               Object newPermissions;
               ContextPermission newGlobalPermission;
               if (name.equals("*")) {
                  if (oldGlobalPermission == null) {
                     newGlobalPermission = contextPermission;
                  } else {
                     newGlobalPermission = oldGlobalPermission.withActionBits(contextPermission.getActionBits());
                  }

                  newPermissions = cloneWithout(oldPermissions, newGlobalPermission);
               } else {
                  newGlobalPermission = oldGlobalPermission;
                  ContextPermission mapPermission = (ContextPermission)oldPermissions.get(name);
                  if (mapPermission == null) {
                     if (oldPermissions.isEmpty()) {
                        newPermissions = Collections.singletonMap(name, contextPermission.withoutActionBits(globalActions));
                     } else {
                        newPermissions = new HashMap(oldPermissions);
                        ((Map)newPermissions).put(name, contextPermission.withoutActionBits(globalActions));
                     }
                  } else {
                     if (((mapPermission.getActionBits() | globalActions) & actionBits) == actionBits) {
                        return;
                     }

                     if (oldPermissions.size() == 1) {
                        newPermissions = Collections.singletonMap(name, mapPermission.withActionBits(actionBits & ~globalActions));
                     } else {
                        newPermissions = new HashMap(oldPermissions);
                        ((Map)newPermissions).put(name, mapPermission.withActionBits(actionBits & ~globalActions));
                     }
                  }
               }

               newState = new State(newGlobalPermission, (Map)newPermissions);
            } while(!stateUpdater.compareAndSet(this, oldState, newState));

         }
      }
   }

   private static Map<String, ContextPermission> cloneWithout(Map<String, ContextPermission> oldPermissions, ContextPermission newGlobalPermission) {
      Iterator<ContextPermission> iterator = oldPermissions.values().iterator();

      ContextPermission first;
      do {
         if (!iterator.hasNext()) {
            return Collections.emptyMap();
         }

         first = (ContextPermission)iterator.next();
      } while(newGlobalPermission.implies(first));

      int globalActionBits = newGlobalPermission.getActionBits();

      while(iterator.hasNext()) {
         ContextPermission second = (ContextPermission)iterator.next();
         if (!newGlobalPermission.implies(second)) {
            HashMap<String, ContextPermission> newMap = new HashMap();
            newMap.put(first.getName(), first.withoutActionBits(globalActionBits));
            newMap.put(second.getName(), second.withoutActionBits(globalActionBits));

            while(iterator.hasNext()) {
               ContextPermission subsequent = (ContextPermission)iterator.next();
               if (!newGlobalPermission.implies(subsequent)) {
                  newMap.put(subsequent.getName(), subsequent.withoutActionBits(globalActionBits));
               }
            }

            return newMap;
         }
      }

      return Collections.singletonMap(first.getName(), first.withoutActionBits(globalActionBits));
   }

   public boolean implies(Permission permission) {
      return permission instanceof ContextPermission && this.implies((ContextPermission)permission);
   }

   public boolean implies(ContextPermission permission) {
      if (permission == null) {
         return false;
      } else {
         State state = this.state;
         ContextPermission globalPermission = state.globalPermission;
         int globalBits;
         if (globalPermission != null) {
            if (globalPermission.implies(permission)) {
               return true;
            }

            globalBits = globalPermission.getActionBits();
         } else {
            globalBits = 0;
         }

         int bits = permission.getActionBits();
         String name = permission.getName();
         if (name.equals("*")) {
            return false;
         } else {
            ContextPermission ourPermission = (ContextPermission)state.permissions.get(name);
            if (ourPermission == null) {
               return false;
            } else {
               int ourBits = ourPermission.getActionBits() | globalBits;
               return (bits & ourBits) == bits;
            }
         }
      }
   }

   public Enumeration<Permission> elements() {
      final State state = this.state;
      final Iterator<ContextPermission> iterator = state.permissions.values().iterator();
      return new Enumeration<Permission>() {
         Permission next;

         {
            this.next = state.globalPermission;
         }

         public boolean hasMoreElements() {
            if (this.next != null) {
               return true;
            } else if (iterator.hasNext()) {
               this.next = (Permission)iterator.next();
               return true;
            } else {
               return false;
            }
         }

         public Permission nextElement() {
            if (!this.hasMoreElements()) {
               throw new NoSuchElementException();
            } else {
               Permission var1;
               try {
                  var1 = this.next;
               } finally {
                  this.next = null;
               }

               return var1;
            }
         }
      };
   }

   static class State {
      private final ContextPermission globalPermission;
      private final Map<String, ContextPermission> permissions;

      State(ContextPermission globalPermission, Map<String, ContextPermission> permissions) {
         this.globalPermission = globalPermission;
         this.permissions = permissions;
      }
   }
}
