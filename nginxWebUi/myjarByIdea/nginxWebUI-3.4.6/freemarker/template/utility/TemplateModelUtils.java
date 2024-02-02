package freemarker.template.utility;

import freemarker.core.CollectionAndSequence;
import freemarker.core._MessageUtil;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class TemplateModelUtils {
   private TemplateModelUtils() {
   }

   public static final TemplateHashModelEx2.KeyValuePairIterator getKeyValuePairIterator(TemplateHashModelEx hash) throws TemplateModelException {
      return (TemplateHashModelEx2.KeyValuePairIterator)(hash instanceof TemplateHashModelEx2 ? ((TemplateHashModelEx2)hash).keyValuePairIterator() : new TemplateHashModelExKeyValuePairIterator(hash));
   }

   public static TemplateHashModel wrapAsHashUnion(ObjectWrapper objectWrapper, Object... hashLikeObjects) throws TemplateModelException {
      return wrapAsHashUnion(objectWrapper, Arrays.asList(hashLikeObjects));
   }

   public static TemplateHashModel wrapAsHashUnion(ObjectWrapper objectWrapper, List<?> hashLikeObjects) throws TemplateModelException {
      NullArgumentException.check("hashLikeObjects", hashLikeObjects);
      List<TemplateHashModel> hashes = new ArrayList(hashLikeObjects.size());
      boolean allTHMEx = true;
      Iterator var4 = hashLikeObjects.iterator();

      while(var4.hasNext()) {
         Object hashLikeObject = var4.next();
         if (hashLikeObject != null) {
            TemplateModel tm;
            if (hashLikeObject instanceof TemplateModel) {
               tm = (TemplateModel)hashLikeObject;
            } else {
               tm = objectWrapper.wrap(hashLikeObject);
            }

            if (!(tm instanceof TemplateHashModelEx)) {
               allTHMEx = false;
               if (!(tm instanceof TemplateHashModel)) {
                  throw new TemplateModelException("One of the objects of the hash union is not hash-like: " + ClassUtil.getFTLTypeDescription(tm));
               }
            }

            hashes.add((TemplateHashModel)tm);
         }
      }

      return (TemplateHashModel)(hashes.isEmpty() ? Constants.EMPTY_HASH : (hashes.size() == 1 ? (TemplateHashModel)hashes.get(0) : (allTHMEx ? new HashExUnionModel(hashes) : new HashUnionModel(hashes))));
   }

   private static final class HashExUnionModel extends AbstractHashUnionModel<TemplateHashModelEx> implements TemplateHashModelEx {
      private CollectionAndSequence keys;
      private CollectionAndSequence values;

      private HashExUnionModel(List<? extends TemplateHashModelEx> hashes) {
         super(hashes);
      }

      public int size() throws TemplateModelException {
         this.initKeys();
         return this.keys.size();
      }

      public TemplateCollectionModel keys() throws TemplateModelException {
         this.initKeys();
         return this.keys;
      }

      public TemplateCollectionModel values() throws TemplateModelException {
         this.initValues();
         return this.values;
      }

      private void initKeys() throws TemplateModelException {
         if (this.keys == null) {
            Set<String> keySet = new HashSet();
            SimpleSequence keySeq = new SimpleSequence(_TemplateAPI.SAFE_OBJECT_WRAPPER);
            Iterator var3 = this.hashes.iterator();

            while(var3.hasNext()) {
               TemplateHashModelEx hash = (TemplateHashModelEx)var3.next();
               addKeys(keySet, keySeq, hash);
            }

            this.keys = new CollectionAndSequence(keySeq);
         }

      }

      private static void addKeys(Set<String> keySet, SimpleSequence keySeq, TemplateHashModelEx hash) throws TemplateModelException {
         TemplateModelIterator it = hash.keys().iterator();

         while(it.hasNext()) {
            TemplateScalarModel tsm = (TemplateScalarModel)it.next();
            if (keySet.add(tsm.getAsString())) {
               keySeq.add(tsm);
            }
         }

      }

      private void initValues() throws TemplateModelException {
         if (this.values == null) {
            SimpleSequence seq = new SimpleSequence(this.size(), _TemplateAPI.SAFE_OBJECT_WRAPPER);
            int ln = this.keys.size();

            for(int i = 0; i < ln; ++i) {
               seq.add(this.get(((TemplateScalarModel)this.keys.get(i)).getAsString()));
            }

            this.values = new CollectionAndSequence(seq);
         }

      }

      // $FF: synthetic method
      HashExUnionModel(List x0, Object x1) {
         this(x0);
      }
   }

   private static class HashUnionModel extends AbstractHashUnionModel<TemplateHashModel> {
      HashUnionModel(List<? extends TemplateHashModel> hashes) {
         super(hashes);
      }
   }

   private abstract static class AbstractHashUnionModel<T extends TemplateHashModel> implements TemplateHashModel {
      protected final List<? extends T> hashes;

      public AbstractHashUnionModel(List<? extends T> hashes) {
         this.hashes = hashes;
      }

      public TemplateModel get(String key) throws TemplateModelException {
         for(int i = this.hashes.size() - 1; i >= 0; --i) {
            TemplateModel value = ((TemplateHashModel)this.hashes.get(i)).get(key);
            if (value != null) {
               return value;
            }
         }

         return null;
      }

      public boolean isEmpty() throws TemplateModelException {
         Iterator var1 = this.hashes.iterator();

         TemplateHashModel hash;
         do {
            if (!var1.hasNext()) {
               return true;
            }

            hash = (TemplateHashModel)var1.next();
         } while(hash.isEmpty());

         return false;
      }
   }

   private static class TemplateHashModelExKeyValuePairIterator implements TemplateHashModelEx2.KeyValuePairIterator {
      private final TemplateHashModelEx hash;
      private final TemplateModelIterator keyIter;

      private TemplateHashModelExKeyValuePairIterator(TemplateHashModelEx hash) throws TemplateModelException {
         this.hash = hash;
         this.keyIter = hash.keys().iterator();
      }

      public boolean hasNext() throws TemplateModelException {
         return this.keyIter.hasNext();
      }

      public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
         final TemplateModel key = this.keyIter.next();
         if (!(key instanceof TemplateScalarModel)) {
            throw _MessageUtil.newKeyValuePairListingNonStringKeyExceptionMessage(key, this.hash);
         } else {
            return new TemplateHashModelEx2.KeyValuePair() {
               public TemplateModel getKey() throws TemplateModelException {
                  return key;
               }

               public TemplateModel getValue() throws TemplateModelException {
                  return TemplateHashModelExKeyValuePairIterator.this.hash.get(((TemplateScalarModel)key).getAsString());
               }
            };
         }
      }

      // $FF: synthetic method
      TemplateHashModelExKeyValuePairIterator(TemplateHashModelEx x0, Object x1) throws TemplateModelException {
         this(x0);
      }
   }
}
