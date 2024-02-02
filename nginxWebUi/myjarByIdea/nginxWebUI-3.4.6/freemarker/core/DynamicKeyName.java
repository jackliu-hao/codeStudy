package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class DynamicKeyName extends Expression {
   private static final int UNKNOWN_RESULT_SIZE = -1;
   private final Expression keyExpression;
   private final Expression target;
   private boolean lazilyGeneratedResultEnabled;
   private static Class[] NUMERICAL_KEY_LHO_EXPECTED_TYPES;

   DynamicKeyName(Expression target, Expression keyExpression) {
      this.target = target;
      this.keyExpression = keyExpression;
      target.enableLazilyGeneratedResult();
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel targetModel = this.target.eval(env);
      if (targetModel == null) {
         if (env.isClassicCompatible()) {
            return null;
         } else {
            throw InvalidReferenceException.getInstance(this.target, env);
         }
      } else {
         TemplateModel keyModel = this.keyExpression.eval(env);
         if (keyModel == null) {
            if (env.isClassicCompatible()) {
               keyModel = TemplateScalarModel.EMPTY_STRING;
            } else {
               this.keyExpression.assertNonNull((TemplateModel)null, env);
            }
         }

         if (keyModel instanceof TemplateNumberModel) {
            int index = this.keyExpression.modelToNumber(keyModel, env).intValue();
            return this.dealWithNumericalKey(targetModel, index, env);
         } else if (keyModel instanceof TemplateScalarModel) {
            String key = EvalUtil.modelToString((TemplateScalarModel)keyModel, this.keyExpression, env);
            return this.dealWithStringKey(targetModel, key, env);
         } else if (keyModel instanceof RangeModel) {
            return this.dealWithRangeKey(targetModel, (RangeModel)keyModel, env);
         } else {
            throw new UnexpectedTypeException(this.keyExpression, keyModel, "number, range, or string", new Class[]{TemplateNumberModel.class, TemplateScalarModel.class, Range.class}, env);
         }
      }
   }

   private TemplateModel dealWithNumericalKey(TemplateModel targetModel, int index, Environment env) throws TemplateException {
      int curIndex;
      if (targetModel instanceof TemplateSequenceModel) {
         TemplateSequenceModel tsm = (TemplateSequenceModel)targetModel;

         try {
            curIndex = tsm.size();
         } catch (Exception var7) {
            curIndex = Integer.MAX_VALUE;
         }

         return index < curIndex ? tsm.get(index) : null;
      } else if (targetModel instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)targetModel).isSequence()) {
         if (index < 0) {
            return null;
         } else {
            TemplateModelIterator iter = ((LazilyGeneratedCollectionModel)targetModel).iterator();

            for(curIndex = 0; iter.hasNext(); ++curIndex) {
               TemplateModel next = iter.next();
               if (index == curIndex) {
                  return next;
               }
            }

            return null;
         }
      } else {
         try {
            String s = this.target.evalAndCoerceToPlainText(env);

            try {
               return new SimpleScalar(s.substring(index, index + 1));
            } catch (IndexOutOfBoundsException var8) {
               if (index < 0) {
                  throw new _MiscTemplateException(new Object[]{"Negative index not allowed: ", index});
               } else if (index >= s.length()) {
                  throw new _MiscTemplateException(new Object[]{"String index out of range: The index was ", index, " (0-based), but the length of the string is only ", s.length(), "."});
               } else {
                  throw new RuntimeException("Can't explain exception", var8);
               }
            }
         } catch (NonStringException var9) {
            throw new UnexpectedTypeException(this.target, targetModel, "sequence or string or something automatically convertible to string (number, date or boolean)", NUMERICAL_KEY_LHO_EXPECTED_TYPES, targetModel instanceof TemplateHashModel ? "You had a numerical value inside the []. Currently that's only supported for sequences (lists) and strings. To get a Map item with a non-string key, use myMap?api.get(myKey)." : null, env);
         }
      }
   }

   private TemplateModel dealWithStringKey(TemplateModel targetModel, String key, Environment env) throws TemplateException {
      if (targetModel instanceof TemplateHashModel) {
         return ((TemplateHashModel)targetModel).get(key);
      } else {
         throw new NonHashException(this.target, targetModel, env);
      }
   }

   private TemplateModel dealWithRangeKey(TemplateModel targetModel, RangeModel range, Environment env) throws TemplateException {
      TemplateSequenceModel targetSeq;
      LazilyGeneratedCollectionModel targetLazySeq;
      String targetStr;
      if (targetModel instanceof TemplateSequenceModel) {
         targetSeq = (TemplateSequenceModel)targetModel;
         targetLazySeq = null;
         targetStr = null;
      } else if (targetModel instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)targetModel).isSequence()) {
         targetSeq = null;
         targetLazySeq = (LazilyGeneratedCollectionModel)targetModel;
         targetStr = null;
      } else {
         targetSeq = null;
         targetLazySeq = null;

         try {
            targetStr = this.target.evalAndCoerceToPlainText(env);
         } catch (NonStringException var18) {
            throw new UnexpectedTypeException(this.target, this.target.eval(env), "sequence or string or something automatically convertible to string (number, date or boolean)", NUMERICAL_KEY_LHO_EXPECTED_TYPES, env);
         }
      }

      int rangeSize = range.size();
      boolean rightUnbounded = range.isRightUnbounded();
      boolean rightAdaptive = range.isRightAdaptive();
      if (!rightUnbounded && rangeSize == 0) {
         return this.emptyResult(targetSeq != null);
      } else {
         int firstIdx = range.getBegining();
         if (firstIdx < 0) {
            throw new _MiscTemplateException(this.keyExpression, new Object[]{"Negative range start index (", firstIdx, ") isn't allowed for a range used for slicing."});
         } else {
            int step = range.getStep();
            int targetSize;
            boolean targetSizeKnown;
            if (targetStr != null) {
               targetSize = targetStr.length();
               targetSizeKnown = true;
            } else if (targetSeq != null) {
               targetSize = targetSeq.size();
               targetSizeKnown = true;
            } else if (targetLazySeq instanceof TemplateCollectionModelEx) {
               targetSize = ((TemplateCollectionModelEx)targetLazySeq).size();
               targetSizeKnown = true;
            } else {
               targetSize = Integer.MAX_VALUE;
               targetSizeKnown = false;
            }

            if (targetSizeKnown) {
               label200: {
                  if (rightAdaptive && step == 1) {
                     if (firstIdx <= targetSize) {
                        break label200;
                     }
                  } else if (firstIdx < targetSize) {
                     break label200;
                  }

                  throw new _MiscTemplateException(this.keyExpression, new Object[]{"Range start index ", firstIdx, " is out of bounds, because the sliced ", targetStr != null ? "string" : "sequence", " has only ", targetSize, " ", targetStr != null ? "character(s)" : "element(s)", ". ", "(Note that indices are 0-based)."});
               }
            }

            int resultSize;
            int exclEndIdx;
            if (!rightUnbounded) {
               exclEndIdx = firstIdx + (rangeSize - 1) * step;
               if (exclEndIdx < 0) {
                  if (!rightAdaptive) {
                     throw new _MiscTemplateException(this.keyExpression, new Object[]{"Negative range end index (", exclEndIdx, ") isn't allowed for a range used for slicing."});
                  }

                  resultSize = firstIdx + 1;
               } else if (targetSizeKnown && exclEndIdx >= targetSize) {
                  if (!rightAdaptive) {
                     throw new _MiscTemplateException(this.keyExpression, new Object[]{"Range end index ", exclEndIdx, " is out of bounds, because the sliced ", targetStr != null ? "string" : "sequence", " has only ", targetSize, " ", targetStr != null ? "character(s)" : "element(s)", ". (Note that indices are 0-based)."});
                  }

                  resultSize = Math.abs(targetSize - firstIdx);
               } else {
                  resultSize = rangeSize;
               }
            } else {
               resultSize = targetSizeKnown ? targetSize - firstIdx : -1;
            }

            if (resultSize == 0) {
               return this.emptyResult(targetSeq != null);
            } else if (targetSeq != null) {
               ArrayList<TemplateModel> resultList = new ArrayList(resultSize);
               int srcIdx = firstIdx;

               for(int i = 0; i < resultSize; ++i) {
                  resultList.add(targetSeq.get(srcIdx));
                  srcIdx += step;
               }

               return new SimpleSequence(resultList, _TemplateAPI.SAFE_OBJECT_WRAPPER);
            } else if (targetLazySeq != null) {
               if (step == 1) {
                  return this.getStep1RangeFromIterator(targetLazySeq.iterator(), range, resultSize, targetSizeKnown);
               } else if (step == -1) {
                  return this.getStepMinus1RangeFromIterator(targetLazySeq.iterator(), range, resultSize);
               } else {
                  throw new AssertionError();
               }
            } else {
               if (step < 0 && resultSize > 1) {
                  if (!range.isAffectedByStringSlicingBug() || resultSize != 2) {
                     throw new _MiscTemplateException(this.keyExpression, new Object[]{"Decreasing ranges aren't allowed for slicing strings (as it would give reversed text). The index range was: first = ", firstIdx, ", last = ", firstIdx + (resultSize - 1) * step});
                  }

                  exclEndIdx = firstIdx;
               } else {
                  exclEndIdx = firstIdx + resultSize;
               }

               return new SimpleScalar(targetStr.substring(firstIdx, exclEndIdx));
            }
         }
      }
   }

   private TemplateModel getStep1RangeFromIterator(final TemplateModelIterator targetIter, RangeModel range, int resultSize, boolean targetSizeKnown) throws TemplateModelException {
      final int firstIdx = range.getBegining();
      final int lastIdx = firstIdx + (range.size() - 1);
      final boolean rightAdaptive = range.isRightAdaptive();
      final boolean rightUnbounded = range.isRightUnbounded();
      if (this.lazilyGeneratedResultEnabled) {
         TemplateModelIterator iterator = new TemplateModelIterator() {
            private boolean elementsBeforeFirsIndexWereSkipped;
            private int nextIdx;

            public TemplateModel next() throws TemplateModelException {
               this.ensureElementsBeforeFirstIndexWereSkipped();
               if (!rightUnbounded && this.nextIdx > lastIdx) {
                  throw new _TemplateModelException(new Object[]{"Iterator has no more elements (at index ", this.nextIdx, ")"});
               } else if (!rightAdaptive && !targetIter.hasNext()) {
                  throw DynamicKeyName.this.newRangeEndOutOfBoundsException(this.nextIdx, lastIdx);
               } else {
                  TemplateModel result = targetIter.next();
                  ++this.nextIdx;
                  return result;
               }
            }

            public boolean hasNext() throws TemplateModelException {
               this.ensureElementsBeforeFirstIndexWereSkipped();
               return (rightUnbounded || this.nextIdx <= lastIdx) && (!rightAdaptive || targetIter.hasNext());
            }

            public void ensureElementsBeforeFirstIndexWereSkipped() throws TemplateModelException {
               if (!this.elementsBeforeFirsIndexWereSkipped) {
                  DynamicKeyName.this.skipElementsBeforeFirstIndex(targetIter, firstIdx);
                  this.nextIdx = firstIdx;
                  this.elementsBeforeFirsIndexWereSkipped = true;
               }
            }
         };
         return (TemplateModel)(resultSize != -1 && targetSizeKnown ? new LazilyGeneratedCollectionModelWithAlreadyKnownSize(iterator, resultSize, true) : new LazilyGeneratedCollectionModelWithUnknownSize(iterator, true));
      } else {
         List<TemplateModel> resultList = resultSize != -1 ? new ArrayList(resultSize) : new ArrayList();
         this.skipElementsBeforeFirstIndex(targetIter, firstIdx);

         for(int nextIdx = firstIdx; rightUnbounded || nextIdx <= lastIdx; ++nextIdx) {
            if (!targetIter.hasNext()) {
               if (!rightAdaptive) {
                  throw this.newRangeEndOutOfBoundsException(nextIdx, lastIdx);
               }
               break;
            }

            resultList.add(targetIter.next());
         }

         return new SimpleSequence(resultList, _TemplateAPI.SAFE_OBJECT_WRAPPER);
      }
   }

   private void skipElementsBeforeFirstIndex(TemplateModelIterator targetIter, int firstIdx) throws TemplateModelException {
      for(int nextIdx = 0; nextIdx < firstIdx; ++nextIdx) {
         if (!targetIter.hasNext()) {
            throw new _TemplateModelException(this.keyExpression, new Object[]{"Range start index ", firstIdx, " is out of bounds, as the sliced sequence only has ", nextIdx, " elements."});
         }

         targetIter.next();
      }

   }

   private _TemplateModelException newRangeEndOutOfBoundsException(int nextIdx, int lastIdx) {
      return new _TemplateModelException(this.keyExpression, new Object[]{"Range end index ", lastIdx, " is out of bounds, as sliced sequence only has ", nextIdx, " elements."});
   }

   private TemplateModel getStepMinus1RangeFromIterator(TemplateModelIterator targetIter, RangeModel range, int resultSize) throws TemplateException {
      int highIndex = range.getBegining();
      int lowIndex = Math.max(highIndex - (range.size() - 1), 0);
      TemplateModel[] resultElements = new TemplateModel[highIndex - lowIndex + 1];
      int srcIdx = 0;

      int dstIdx;
      for(dstIdx = resultElements.length - 1; srcIdx <= highIndex && targetIter.hasNext(); ++srcIdx) {
         TemplateModel element = targetIter.next();
         if (srcIdx >= lowIndex) {
            resultElements[dstIdx--] = element;
         }
      }

      if (dstIdx != -1) {
         throw new _MiscTemplateException(this, "Range top index " + highIndex + " (0-based) is outside the sliced sequence of length " + srcIdx + ".");
      } else {
         return new SimpleSequence(Arrays.asList(resultElements), _TemplateAPI.SAFE_OBJECT_WRAPPER);
      }
   }

   private TemplateModel emptyResult(boolean seq) {
      return (TemplateModel)(seq ? (_TemplateAPI.getTemplateLanguageVersionAsInt((TemplateObject)this) < _TemplateAPI.VERSION_INT_2_3_21 ? new SimpleSequence(_TemplateAPI.SAFE_OBJECT_WRAPPER) : Constants.EMPTY_SEQUENCE) : TemplateScalarModel.EMPTY_STRING);
   }

   void enableLazilyGeneratedResult() {
      this.lazilyGeneratedResultEnabled = true;
   }

   public String getCanonicalForm() {
      return this.target.getCanonicalForm() + "[" + this.keyExpression.getCanonicalForm() + "]";
   }

   String getNodeTypeSymbol() {
      return "...[...]";
   }

   boolean isLiteral() {
      return this.constantValue != null || this.target.isLiteral() && this.keyExpression.isLiteral();
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      return idx == 0 ? this.target : this.keyExpression;
   }

   ParameterRole getParameterRole(int idx) {
      return idx == 0 ? ParameterRole.LEFT_HAND_OPERAND : ParameterRole.ENCLOSED_OPERAND;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new DynamicKeyName(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.keyExpression.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
   }

   static {
      NUMERICAL_KEY_LHO_EXPECTED_TYPES = new Class[1 + NonStringException.STRING_COERCABLE_TYPES.length];
      NUMERICAL_KEY_LHO_EXPECTED_TYPES[0] = TemplateSequenceModel.class;

      for(int i = 0; i < NonStringException.STRING_COERCABLE_TYPES.length; ++i) {
         NUMERICAL_KEY_LHO_EXPECTED_TYPES[i + 1] = NonStringException.STRING_COERCABLE_TYPES[i];
      }

   }
}
