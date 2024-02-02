package freemarker.core;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template._TemplateAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

final class HashLiteral extends Expression {
   private final List<? extends Expression> keys;
   private final List<? extends Expression> values;
   private final int size;

   HashLiteral(List<? extends Expression> keys, List<? extends Expression> values) {
      this.keys = keys;
      this.values = values;
      this.size = keys.size();
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      return new SequenceHash(env);
   }

   public String getCanonicalForm() {
      StringBuilder buf = new StringBuilder("{");

      for(int i = 0; i < this.size; ++i) {
         Expression key = (Expression)this.keys.get(i);
         Expression value = (Expression)this.values.get(i);
         buf.append(key.getCanonicalForm());
         buf.append(": ");
         buf.append(value.getCanonicalForm());
         if (i != this.size - 1) {
            buf.append(", ");
         }
      }

      buf.append("}");
      return buf.toString();
   }

   String getNodeTypeSymbol() {
      return "{...}";
   }

   boolean isLiteral() {
      if (this.constantValue != null) {
         return true;
      } else {
         for(int i = 0; i < this.size; ++i) {
            Expression key = (Expression)this.keys.get(i);
            Expression value = (Expression)this.values.get(i);
            if (!key.isLiteral() || !value.isLiteral()) {
               return false;
            }
         }

         return true;
      }
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      List<Expression> clonedKeys = new ArrayList(this.keys.size());
      Iterator var5 = this.keys.iterator();

      while(var5.hasNext()) {
         Expression key = (Expression)var5.next();
         clonedKeys.add(key.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
      }

      List<Expression> clonedValues = new ArrayList(this.values.size());
      Iterator var9 = this.values.iterator();

      while(var9.hasNext()) {
         Expression value = (Expression)var9.next();
         clonedValues.add(value.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
      }

      return new HashLiteral(clonedKeys, clonedValues);
   }

   int getParameterCount() {
      return this.size * 2;
   }

   Object getParameterValue(int idx) {
      this.checkIndex(idx);
      return idx % 2 == 0 ? (Expression)this.keys.get(idx / 2) : (Expression)this.values.get(idx / 2);
   }

   ParameterRole getParameterRole(int idx) {
      this.checkIndex(idx);
      return idx % 2 == 0 ? ParameterRole.ITEM_KEY : ParameterRole.ITEM_VALUE;
   }

   private void checkIndex(int idx) {
      if (idx >= this.size * 2) {
         throw new IndexOutOfBoundsException();
      }
   }

   private class SequenceHash implements TemplateHashModelEx2 {
      private HashMap<String, TemplateModel> map;
      private TemplateCollectionModel keyCollection;
      private TemplateCollectionModel valueCollection;

      SequenceHash(Environment env) throws TemplateException {
         if (_TemplateAPI.getTemplateLanguageVersionAsInt((TemplateObject)HashLiteral.this) >= _TemplateAPI.VERSION_INT_2_3_21) {
            this.map = new LinkedHashMap();

            for(int ix = 0; ix < HashLiteral.this.size; ++ix) {
               Expression keyExpx = (Expression)HashLiteral.this.keys.get(ix);
               Expression valExpx = (Expression)HashLiteral.this.values.get(ix);
               String key = keyExpx.evalAndCoerceToPlainText(env);
               TemplateModel value = valExpx.eval(env);
               if (env == null || !env.isClassicCompatible()) {
                  valExpx.assertNonNull(value, env);
               }

               this.map.put(key, value);
            }
         } else {
            this.map = new HashMap();
            SimpleSequence keyList = new SimpleSequence(HashLiteral.this.size, _TemplateAPI.SAFE_OBJECT_WRAPPER);
            SimpleSequence valueList = new SimpleSequence(HashLiteral.this.size, _TemplateAPI.SAFE_OBJECT_WRAPPER);

            for(int i = 0; i < HashLiteral.this.size; ++i) {
               Expression keyExp = (Expression)HashLiteral.this.keys.get(i);
               Expression valExp = (Expression)HashLiteral.this.values.get(i);
               String keyx = keyExp.evalAndCoerceToPlainText(env);
               TemplateModel valuex = valExp.eval(env);
               if (env == null || !env.isClassicCompatible()) {
                  valExp.assertNonNull(valuex, env);
               }

               this.map.put(keyx, valuex);
               keyList.add(keyx);
               valueList.add(valuex);
            }

            this.keyCollection = new CollectionAndSequence(keyList);
            this.valueCollection = new CollectionAndSequence(valueList);
         }

      }

      public int size() {
         return HashLiteral.this.size;
      }

      public TemplateCollectionModel keys() {
         if (this.keyCollection == null) {
            this.keyCollection = new CollectionAndSequence(new SimpleSequence(this.map.keySet(), _TemplateAPI.SAFE_OBJECT_WRAPPER));
         }

         return this.keyCollection;
      }

      public TemplateCollectionModel values() {
         if (this.valueCollection == null) {
            this.valueCollection = new CollectionAndSequence(new SimpleSequence(this.map.values(), _TemplateAPI.SAFE_OBJECT_WRAPPER));
         }

         return this.valueCollection;
      }

      public TemplateModel get(String key) {
         return (TemplateModel)this.map.get(key);
      }

      public boolean isEmpty() {
         return HashLiteral.this.size == 0;
      }

      public String toString() {
         return HashLiteral.this.getCanonicalForm();
      }

      public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
         return new TemplateHashModelEx2.KeyValuePairIterator() {
            private final TemplateModelIterator keyIterator = SequenceHash.this.keys().iterator();
            private final TemplateModelIterator valueIterator = SequenceHash.this.values().iterator();

            public boolean hasNext() throws TemplateModelException {
               return this.keyIterator.hasNext();
            }

            public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
               return new TemplateHashModelEx2.KeyValuePair() {
                  private final TemplateModel key;
                  private final TemplateModel value;

                  {
                     this.key = keyIterator.next();
                     this.value = valueIterator.next();
                  }

                  public TemplateModel getKey() {
                     return this.key;
                  }

                  public TemplateModel getValue() {
                     return this.value;
                  }
               };
            }
         };
      }
   }
}
