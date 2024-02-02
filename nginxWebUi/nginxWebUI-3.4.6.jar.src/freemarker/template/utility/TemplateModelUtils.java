/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.core.CollectionAndSequence;
/*     */ import freemarker.core._MessageUtil;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public final class TemplateModelUtils
/*     */ {
/*     */   public static final TemplateHashModelEx2.KeyValuePairIterator getKeyValuePairIterator(TemplateHashModelEx hash) throws TemplateModelException {
/*  67 */     return (hash instanceof TemplateHashModelEx2) ? ((TemplateHashModelEx2)hash).keyValuePairIterator() : new TemplateHashModelExKeyValuePairIterator(hash);
/*     */   }
/*     */   
/*     */   private static class TemplateHashModelExKeyValuePairIterator
/*     */     implements TemplateHashModelEx2.KeyValuePairIterator
/*     */   {
/*     */     private final TemplateHashModelEx hash;
/*     */     private final TemplateModelIterator keyIter;
/*     */     
/*     */     private TemplateHashModelExKeyValuePairIterator(TemplateHashModelEx hash) throws TemplateModelException {
/*  77 */       this.hash = hash;
/*  78 */       this.keyIter = hash.keys().iterator();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() throws TemplateModelException {
/*  83 */       return this.keyIter.hasNext();
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
/*  88 */       final TemplateModel key = this.keyIter.next();
/*  89 */       if (!(key instanceof TemplateScalarModel)) {
/*  90 */         throw _MessageUtil.newKeyValuePairListingNonStringKeyExceptionMessage(key, this.hash);
/*     */       }
/*     */       
/*  93 */       return new TemplateHashModelEx2.KeyValuePair()
/*     */         {
/*     */           public TemplateModel getKey() throws TemplateModelException
/*     */           {
/*  97 */             return key;
/*     */           }
/*     */ 
/*     */           
/*     */           public TemplateModel getValue() throws TemplateModelException {
/* 102 */             return TemplateModelUtils.TemplateHashModelExKeyValuePairIterator.this.hash.get(((TemplateScalarModel)key).getAsString());
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemplateHashModel wrapAsHashUnion(ObjectWrapper objectWrapper, Object... hashLikeObjects) throws TemplateModelException {
/* 117 */     return wrapAsHashUnion(objectWrapper, Arrays.asList(hashLikeObjects));
/*     */   }
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
/*     */   public static TemplateHashModel wrapAsHashUnion(ObjectWrapper objectWrapper, List<?> hashLikeObjects) throws TemplateModelException {
/* 160 */     NullArgumentException.check("hashLikeObjects", hashLikeObjects);
/*     */     
/* 162 */     List<TemplateHashModel> hashes = new ArrayList<>(hashLikeObjects.size());
/*     */     
/* 164 */     boolean allTHMEx = true;
/* 165 */     for (Object hashLikeObject : hashLikeObjects) {
/* 166 */       TemplateModel tm; if (hashLikeObject == null) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 171 */       if (hashLikeObject instanceof TemplateModel) {
/* 172 */         tm = (TemplateModel)hashLikeObject;
/*     */       } else {
/* 174 */         tm = objectWrapper.wrap(hashLikeObject);
/*     */       } 
/*     */       
/* 177 */       if (!(tm instanceof TemplateHashModelEx)) {
/* 178 */         allTHMEx = false;
/* 179 */         if (!(tm instanceof TemplateHashModel)) {
/* 180 */           throw new TemplateModelException("One of the objects of the hash union is not hash-like: " + 
/*     */               
/* 182 */               ClassUtil.getFTLTypeDescription(tm));
/*     */         }
/*     */       } 
/*     */       
/* 186 */       hashes.add((TemplateHashModel)tm);
/*     */     } 
/*     */     
/* 189 */     return hashes.isEmpty() ? (TemplateHashModel)Constants.EMPTY_HASH : (
/* 190 */       (hashes.size() == 1) ? hashes.get(0) : (allTHMEx ? new HashExUnionModel(hashes) : new HashUnionModel(hashes)));
/*     */   }
/*     */   
/*     */   private static abstract class AbstractHashUnionModel<T extends TemplateHashModel>
/*     */     implements TemplateHashModel
/*     */   {
/*     */     protected final List<? extends T> hashes;
/*     */     
/*     */     public AbstractHashUnionModel(List<? extends T> hashes) {
/* 199 */       this.hashes = hashes;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 204 */       for (int i = this.hashes.size() - 1; i >= 0; i--) {
/* 205 */         TemplateModel value = ((TemplateHashModel)this.hashes.get(i)).get(key);
/* 206 */         if (value != null) {
/* 207 */           return value;
/*     */         }
/*     */       } 
/* 210 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() throws TemplateModelException {
/* 215 */       for (TemplateHashModel hash : this.hashes) {
/* 216 */         if (!hash.isEmpty()) {
/* 217 */           return false;
/*     */         }
/*     */       } 
/* 220 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class HashUnionModel
/*     */     extends AbstractHashUnionModel<TemplateHashModel> {
/*     */     HashUnionModel(List<? extends TemplateHashModel> hashes) {
/* 227 */       super(hashes);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class HashExUnionModel
/*     */     extends AbstractHashUnionModel<TemplateHashModelEx> implements TemplateHashModelEx {
/*     */     private CollectionAndSequence keys;
/*     */     private CollectionAndSequence values;
/*     */     
/*     */     private HashExUnionModel(List<? extends TemplateHashModelEx> hashes) {
/* 237 */       super(hashes);
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 242 */       initKeys();
/* 243 */       return this.keys.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel keys() throws TemplateModelException {
/* 248 */       initKeys();
/* 249 */       return (TemplateCollectionModel)this.keys;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel values() throws TemplateModelException {
/* 254 */       initValues();
/* 255 */       return (TemplateCollectionModel)this.values;
/*     */     }
/*     */     
/*     */     private void initKeys() throws TemplateModelException {
/* 259 */       if (this.keys == null) {
/* 260 */         Set<String> keySet = new HashSet<>();
/* 261 */         SimpleSequence keySeq = new SimpleSequence((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 262 */         for (TemplateHashModelEx hash : this.hashes) {
/* 263 */           addKeys(keySet, keySeq, hash);
/*     */         }
/* 265 */         this.keys = new CollectionAndSequence((TemplateSequenceModel)keySeq);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private static void addKeys(Set<String> keySet, SimpleSequence keySeq, TemplateHashModelEx hash) throws TemplateModelException {
/* 271 */       TemplateModelIterator it = hash.keys().iterator();
/* 272 */       while (it.hasNext()) {
/* 273 */         TemplateScalarModel tsm = (TemplateScalarModel)it.next();
/* 274 */         if (keySet.add(tsm.getAsString()))
/*     */         {
/*     */           
/* 277 */           keySeq.add(tsm);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     private void initValues() throws TemplateModelException {
/* 283 */       if (this.values == null) {
/* 284 */         SimpleSequence seq = new SimpleSequence(size(), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*     */ 
/*     */         
/* 287 */         int ln = this.keys.size();
/* 288 */         for (int i = 0; i < ln; i++) {
/* 289 */           seq.add(get(((TemplateScalarModel)this.keys.get(i)).getAsString()));
/*     */         }
/* 291 */         this.values = new CollectionAndSequence((TemplateSequenceModel)seq);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\TemplateModelUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */