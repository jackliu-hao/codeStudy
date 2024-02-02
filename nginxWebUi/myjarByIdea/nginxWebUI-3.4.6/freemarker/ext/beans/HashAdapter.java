package freemarker.ext.beans;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.UndeclaredThrowableException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HashAdapter extends AbstractMap implements TemplateModelAdapter {
   private final BeansWrapper wrapper;
   private final TemplateHashModel model;
   private Set entrySet;

   HashAdapter(TemplateHashModel model, BeansWrapper wrapper) {
      this.model = model;
      this.wrapper = wrapper;
   }

   public TemplateModel getTemplateModel() {
      return this.model;
   }

   public boolean isEmpty() {
      try {
         return this.model.isEmpty();
      } catch (TemplateModelException var2) {
         throw new UndeclaredThrowableException(var2);
      }
   }

   public int size() {
      try {
         return this.getModelEx().size();
      } catch (TemplateModelException var2) {
         throw new UndeclaredThrowableException(var2);
      }
   }

   public Object get(Object key) {
      try {
         return this.wrapper.unwrap(this.model.get(String.valueOf(key)));
      } catch (TemplateModelException var3) {
         throw new UndeclaredThrowableException(var3);
      }
   }

   public boolean containsKey(Object key) {
      return this.get(key) != null ? true : super.containsKey(key);
   }

   public Set entrySet() {
      return this.entrySet != null ? this.entrySet : (this.entrySet = new AbstractSet() {
         public Iterator iterator() {
            final TemplateModelIterator i;
            try {
               i = HashAdapter.this.getModelEx().keys().iterator();
            } catch (TemplateModelException var3) {
               throw new UndeclaredThrowableException(var3);
            }

            return new Iterator() {
               public boolean hasNext() {
                  try {
                     return i.hasNext();
                  } catch (TemplateModelException var2) {
                     throw new UndeclaredThrowableException(var2);
                  }
               }

               public Object next() {
                  final Object key;
                  try {
                     key = HashAdapter.this.wrapper.unwrap(i.next());
                  } catch (TemplateModelException var3) {
                     throw new UndeclaredThrowableException(var3);
                  }

                  return new Map.Entry() {
                     public Object getKey() {
                        return key;
                     }

                     public Object getValue() {
                        return HashAdapter.this.get(key);
                     }

                     public Object setValue(Object value) {
                        throw new UnsupportedOperationException();
                     }

                     public boolean equals(Object o) {
                        if (!(o instanceof Map.Entry)) {
                           return false;
                        } else {
                           Map.Entry e = (Map.Entry)o;
                           Object k1 = this.getKey();
                           Object k2 = e.getKey();
                           if (k1 == k2 || k1 != null && k1.equals(k2)) {
                              Object v1 = this.getValue();
                              Object v2 = e.getValue();
                              if (v1 == v2 || v1 != null && v1.equals(v2)) {
                                 return true;
                              }
                           }

                           return false;
                        }
                     }

                     public int hashCode() {
                        Object value = this.getValue();
                        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
                     }
                  };
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }

         public int size() {
            try {
               return HashAdapter.this.getModelEx().size();
            } catch (TemplateModelException var2) {
               throw new UndeclaredThrowableException(var2);
            }
         }
      });
   }

   private TemplateHashModelEx getModelEx() {
      if (this.model instanceof TemplateHashModelEx) {
         return (TemplateHashModelEx)this.model;
      } else {
         throw new UnsupportedOperationException("Operation supported only on TemplateHashModelEx. " + this.model.getClass().getName() + " does not implement it though.");
      }
   }
}
