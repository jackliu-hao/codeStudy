package com.github.odiszapc.nginxparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class NgxBlock extends NgxAbstractEntry implements Iterable<NgxEntry> {
   private Collection<NgxEntry> entries = new ArrayList();

   public NgxBlock() {
      super();
   }

   public Collection<NgxEntry> getEntries() {
      return this.entries;
   }

   public void addEntry(NgxEntry entry) {
      this.entries.add(entry);
   }

   public String toString() {
      return super.toString() + " {";
   }

   public Iterator<NgxEntry> iterator() {
      return this.getEntries().iterator();
   }

   public void remove(NgxEntry itemToRemove) {
      if (null == itemToRemove) {
         throw new NullPointerException("Item can not be null");
      } else {
         Iterator<NgxEntry> it = this.entries.iterator();

         while(it.hasNext()) {
            NgxEntry entry = (NgxEntry)it.next();
            switch (NgxEntryType.fromClass(entry.getClass())) {
               case PARAM:
                  if (entry.equals(itemToRemove)) {
                     it.remove();
                  }
                  break;
               case BLOCK:
                  if (entry.equals(itemToRemove)) {
                     it.remove();
                  } else {
                     NgxBlock block = (NgxBlock)entry;
                     block.remove(itemToRemove);
                  }
            }
         }

      }
   }

   public void removeAll(Iterable<NgxEntry> itemsToRemove) {
      if (null == itemsToRemove) {
         throw new NullPointerException("Items can not be null");
      } else {
         Iterator i$ = itemsToRemove.iterator();

         while(i$.hasNext()) {
            NgxEntry itemToRemove = (NgxEntry)i$.next();
            this.remove(itemToRemove);
         }

      }
   }

   public <T extends NgxEntry> T find(Class<T> clazz, String... params) {
      List<NgxEntry> all = this.findAll(clazz, new ArrayList(), params);
      return all.isEmpty() ? null : (NgxEntry)all.get(0);
   }

   public NgxBlock findBlock(String... params) {
      NgxEntry entry = this.find(NgxConfig.BLOCK, params);
      return null == entry ? null : (NgxBlock)entry;
   }

   public NgxParam findParam(String... params) {
      NgxEntry entry = this.find(NgxConfig.PARAM, params);
      return null == entry ? null : (NgxParam)entry;
   }

   public <T extends NgxEntry> List<NgxEntry> findAll(Class<T> clazz, String... params) {
      return this.findAll(clazz, new ArrayList(), params);
   }

   public <T extends NgxEntry> List<NgxEntry> findAll(Class<T> clazz, List<NgxEntry> result, String... params) {
      List<NgxEntry> res = new ArrayList();
      if (0 == params.length) {
         return res;
      } else {
         String head = params[0];
         String[] tail = params.length > 1 ? (String[])Arrays.copyOfRange(params, 1, params.length) : new String[0];
         Iterator i$ = this.getEntries().iterator();

         while(i$.hasNext()) {
            NgxEntry entry = (NgxEntry)i$.next();
            switch (NgxEntryType.fromClass(entry.getClass())) {
               case PARAM:
                  NgxParam param = (NgxParam)entry;
                  if (param.getName().equals(head) && param.getClass() == clazz) {
                     res.add(param);
                  }
                  break;
               case BLOCK:
                  NgxBlock block = (NgxBlock)entry;
                  if (tail.length > 0) {
                     if (block.getName().equals(head)) {
                        res.addAll(block.findAll(clazz, result, tail));
                     }
                  } else if (block.getName().equals(head) && clazz.equals(NgxBlock.class)) {
                     res.add(block);
                  }
            }
         }

         return res;
      }
   }
}
