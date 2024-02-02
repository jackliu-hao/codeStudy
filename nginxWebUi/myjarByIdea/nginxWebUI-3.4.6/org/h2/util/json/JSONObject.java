package org.h2.util.json;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class JSONObject extends JSONValue {
   private final ArrayList<AbstractMap.SimpleImmutableEntry<String, JSONValue>> members = new ArrayList();

   JSONObject() {
   }

   void addMember(String var1, JSONValue var2) {
      this.members.add(new AbstractMap.SimpleImmutableEntry(var1, var2));
   }

   public void addTo(JSONTarget<?> var1) {
      var1.startObject();
      Iterator var2 = this.members.iterator();

      while(var2.hasNext()) {
         AbstractMap.SimpleImmutableEntry var3 = (AbstractMap.SimpleImmutableEntry)var2.next();
         var1.member((String)var3.getKey());
         ((JSONValue)var3.getValue()).addTo(var1);
      }

      var1.endObject();
   }

   public Map.Entry<String, JSONValue>[] getMembers() {
      return (Map.Entry[])this.members.toArray(new Map.Entry[0]);
   }

   public JSONValue getFirst(String var1) {
      Iterator var2 = this.members.iterator();

      AbstractMap.SimpleImmutableEntry var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (AbstractMap.SimpleImmutableEntry)var2.next();
      } while(!var1.equals(var3.getKey()));

      return (JSONValue)var3.getValue();
   }
}
