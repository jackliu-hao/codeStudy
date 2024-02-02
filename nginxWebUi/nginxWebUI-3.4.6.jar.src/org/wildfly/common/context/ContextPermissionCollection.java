/*     */ package org.wildfly.common.context;
/*     */ 
/*     */ import java.security.Permission;
/*     */ import java.security.PermissionCollection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ContextPermissionCollection
/*     */   extends PermissionCollection
/*     */ {
/*     */   private static final long serialVersionUID = -3651721703337368351L;
/*  40 */   private volatile State state = emptyState;
/*     */   
/*  42 */   private static final AtomicReferenceFieldUpdater<ContextPermissionCollection, State> stateUpdater = AtomicReferenceFieldUpdater.newUpdater(ContextPermissionCollection.class, State.class, "state");
/*     */   
/*     */   public void add(Permission permission) throws SecurityException, IllegalArgumentException {
/*  45 */     Assert.checkNotNullParam("permission", permission);
/*  46 */     if (permission instanceof ContextPermission) {
/*  47 */       add((ContextPermission)permission);
/*     */     } else {
/*  49 */       throw CommonMessages.msg.invalidPermissionType(ContextPermission.class, permission.getClass());
/*     */     } 
/*     */   }
/*     */   public void add(ContextPermission contextPermission) throws SecurityException {
/*     */     State oldState, newState;
/*  54 */     Assert.checkNotNullParam("contextPermission", contextPermission);
/*  55 */     if (isReadOnly()) {
/*  56 */       throw CommonMessages.msg.readOnlyPermissionCollection();
/*     */     }
/*  58 */     int actionBits = contextPermission.getActionBits();
/*  59 */     if (actionBits == 0) {
/*     */       return;
/*     */     }
/*     */     
/*  63 */     String name = contextPermission.getName(); do {
/*     */       Map<String, ContextPermission> newPermissions;
/*     */       ContextPermission newGlobalPermission;
/*  66 */       oldState = this.state;
/*  67 */       ContextPermission oldGlobalPermission = oldState.globalPermission;
/*  68 */       int globalActions = (oldGlobalPermission == null) ? 0 : oldGlobalPermission.getActionBits();
/*  69 */       if (oldGlobalPermission != null && oldGlobalPermission.implies(contextPermission)) {
/*     */         return;
/*     */       }
/*     */       
/*  73 */       Map<String, ContextPermission> oldPermissions = oldState.permissions;
/*     */ 
/*     */       
/*  76 */       if (name.equals("*")) {
/*     */         
/*  78 */         if (oldGlobalPermission == null) {
/*  79 */           newGlobalPermission = contextPermission;
/*     */         } else {
/*  81 */           newGlobalPermission = oldGlobalPermission.withActionBits(contextPermission.getActionBits());
/*     */         } 
/*     */ 
/*     */         
/*  85 */         newPermissions = cloneWithout(oldPermissions, newGlobalPermission);
/*     */       } else {
/*  87 */         newGlobalPermission = oldGlobalPermission;
/*     */         
/*  89 */         ContextPermission mapPermission = oldPermissions.get(name);
/*  90 */         if (mapPermission == null)
/*     */         
/*  92 */         { if (oldPermissions.isEmpty()) {
/*     */             
/*  94 */             newPermissions = Collections.singletonMap(name, contextPermission.withoutActionBits(globalActions));
/*     */           } else {
/*     */             
/*  97 */             newPermissions = new HashMap<>(oldPermissions);
/*  98 */             newPermissions.put(name, contextPermission.withoutActionBits(globalActions));
/*     */           }  }
/* 100 */         else { if (((mapPermission.getActionBits() | globalActions) & actionBits) == actionBits) {
/*     */             return;
/*     */           }
/*     */ 
/*     */           
/* 105 */           if (oldPermissions.size() == 1) {
/*     */             
/* 107 */             newPermissions = Collections.singletonMap(name, mapPermission.withActionBits(actionBits & (globalActions ^ 0xFFFFFFFF)));
/*     */           } else {
/*     */             
/* 110 */             newPermissions = new HashMap<>(oldPermissions);
/* 111 */             newPermissions.put(name, mapPermission.withActionBits(actionBits & (globalActions ^ 0xFFFFFFFF)));
/*     */           }  }
/*     */       
/*     */       } 
/* 115 */       newState = new State(newGlobalPermission, newPermissions);
/*     */     }
/* 117 */     while (!stateUpdater.compareAndSet(this, oldState, newState));
/*     */   }
/*     */   private static Map<String, ContextPermission> cloneWithout(Map<String, ContextPermission> oldPermissions, ContextPermission newGlobalPermission) {
/*     */     ContextPermission first, second;
/* 121 */     Iterator<ContextPermission> iterator = oldPermissions.values().iterator();
/*     */     
/*     */     do {
/* 124 */       if (!iterator.hasNext()) {
/* 125 */         return Collections.emptyMap();
/*     */       }
/* 127 */       first = iterator.next();
/* 128 */     } while (newGlobalPermission.implies(first));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     int globalActionBits = newGlobalPermission.getActionBits();
/*     */     
/*     */     do {
/* 136 */       if (!iterator.hasNext()) {
/* 137 */         return Collections.singletonMap(first.getName(), first.withoutActionBits(globalActionBits));
/*     */       }
/* 139 */       second = iterator.next();
/* 140 */     } while (newGlobalPermission.implies(second));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     HashMap<String, ContextPermission> newMap = new HashMap<>();
/* 146 */     newMap.put(first.getName(), first.withoutActionBits(globalActionBits));
/* 147 */     newMap.put(second.getName(), second.withoutActionBits(globalActionBits));
/*     */     
/* 149 */     while (iterator.hasNext()) {
/* 150 */       ContextPermission subsequent = iterator.next();
/* 151 */       if (!newGlobalPermission.implies(subsequent)) {
/* 152 */         newMap.put(subsequent.getName(), subsequent.withoutActionBits(globalActionBits));
/*     */       }
/*     */     } 
/* 155 */     return newMap;
/*     */   }
/*     */   
/*     */   public boolean implies(Permission permission) {
/* 159 */     return (permission instanceof ContextPermission && implies((ContextPermission)permission));
/*     */   }
/*     */   public boolean implies(ContextPermission permission) {
/*     */     int globalBits;
/* 163 */     if (permission == null) return false; 
/* 164 */     State state = this.state;
/* 165 */     ContextPermission globalPermission = state.globalPermission;
/*     */     
/* 167 */     if (globalPermission != null) {
/* 168 */       if (globalPermission.implies(permission)) {
/* 169 */         return true;
/*     */       }
/* 171 */       globalBits = globalPermission.getActionBits();
/*     */     } else {
/* 173 */       globalBits = 0;
/*     */     } 
/* 175 */     int bits = permission.getActionBits();
/* 176 */     String name = permission.getName();
/* 177 */     if (name.equals("*")) {
/* 178 */       return false;
/*     */     }
/* 180 */     ContextPermission ourPermission = (ContextPermission)state.permissions.get(name);
/* 181 */     if (ourPermission == null) {
/* 182 */       return false;
/*     */     }
/* 184 */     int ourBits = ourPermission.getActionBits() | globalBits;
/* 185 */     return ((bits & ourBits) == bits);
/*     */   }
/*     */   
/*     */   public Enumeration<Permission> elements() {
/* 189 */     final State state = this.state;
/* 190 */     final Iterator<ContextPermission> iterator = state.permissions.values().iterator();
/* 191 */     return new Enumeration<Permission>() {
/* 192 */         Permission next = state.globalPermission;
/*     */         
/*     */         public boolean hasMoreElements() {
/* 195 */           if (this.next != null) {
/* 196 */             return true;
/*     */           }
/* 198 */           if (iterator.hasNext()) {
/* 199 */             this.next = iterator.next();
/* 200 */             return true;
/*     */           } 
/* 202 */           return false;
/*     */         }
/*     */         
/*     */         public Permission nextElement() {
/* 206 */           if (!hasMoreElements()) throw new NoSuchElementException(); 
/*     */           try {
/* 208 */             return this.next;
/*     */           } finally {
/* 210 */             this.next = null;
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   static class State {
/*     */     private final ContextPermission globalPermission;
/*     */     private final Map<String, ContextPermission> permissions;
/*     */     
/*     */     State(ContextPermission globalPermission, Map<String, ContextPermission> permissions) {
/* 221 */       this.globalPermission = globalPermission;
/* 222 */       this.permissions = permissions;
/*     */     }
/*     */   }
/*     */   
/* 226 */   private static final State emptyState = new State(null, Collections.emptyMap());
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\context\ContextPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */