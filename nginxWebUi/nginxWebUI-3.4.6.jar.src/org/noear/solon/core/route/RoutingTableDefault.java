/*    */ package org.noear.solon.core.route;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ import java.util.stream.Collectors;
/*    */ import org.noear.solon.core.handle.MethodType;
/*    */ 
/*    */ 
/*    */ public class RoutingTableDefault<T>
/*    */   implements RoutingTable<T>
/*    */ {
/* 15 */   private List<Routing<T>> table = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(Routing<T> routing) {
/* 25 */     this.table.add(routing);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(int index, Routing<T> routing) {
/* 36 */     this.table.add(index, routing);
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(String pathPrefix) {
/* 41 */     this.table.removeIf(l -> l.path().startsWith(pathPrefix));
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<Routing<T>> getAll() {
/* 46 */     return Collections.unmodifiableList(this.table);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T matchOne(String path, MethodType method) {
/* 57 */     for (Routing<T> l : this.table) {
/* 58 */       if (l.matches(method, path)) {
/* 59 */         return l.target();
/*    */       }
/*    */     } 
/*    */     
/* 63 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<T> matchAll(String path, MethodType method) {
/* 74 */     return (List<T>)this.table.stream()
/* 75 */       .filter(l -> l.matches(method, path))
/* 76 */       .sorted(Comparator.comparingInt(l -> l.index()))
/* 77 */       .map(l -> l.target())
/* 78 */       .collect(Collectors.toList());
/*    */   }
/*    */ 
/*    */   
/*    */   public void clear() {
/* 83 */     this.table.clear();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\route\RoutingTableDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */