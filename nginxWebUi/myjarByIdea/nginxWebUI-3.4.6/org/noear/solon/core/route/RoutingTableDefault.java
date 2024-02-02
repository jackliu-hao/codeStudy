package org.noear.solon.core.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.noear.solon.core.handle.MethodType;

public class RoutingTableDefault<T> implements RoutingTable<T> {
   private List<Routing<T>> table = new ArrayList();

   public void add(Routing<T> routing) {
      this.table.add(routing);
   }

   public void add(int index, Routing<T> routing) {
      this.table.add(index, routing);
   }

   public void remove(String pathPrefix) {
      this.table.removeIf((l) -> {
         return l.path().startsWith(pathPrefix);
      });
   }

   public Collection<Routing<T>> getAll() {
      return Collections.unmodifiableList(this.table);
   }

   public T matchOne(String path, MethodType method) {
      Iterator var3 = this.table.iterator();

      Routing l;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         l = (Routing)var3.next();
      } while(!l.matches(method, path));

      return l.target();
   }

   public List<T> matchAll(String path, MethodType method) {
      return (List)this.table.stream().filter((l) -> {
         return l.matches(method, path);
      }).sorted(Comparator.comparingInt((l) -> {
         return l.index();
      })).map((l) -> {
         return l.target();
      }).collect(Collectors.toList());
   }

   public void clear() {
      this.table.clear();
   }
}
