package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BuiltInsForStringsRegexp {
   private BuiltInsForStringsRegexp() {
   }

   static class RegexMatchModel implements TemplateBooleanModel, TemplateCollectionModel, TemplateSequenceModel {
      final Pattern pattern;
      final String input;
      private Matcher firedEntireInputMatcher;
      private Boolean entireInputMatched;
      private TemplateSequenceModel entireInputMatchGroups;
      private ArrayList matchingInputParts;

      RegexMatchModel(Pattern pattern, String input) {
         this.pattern = pattern;
         this.input = input;
      }

      public TemplateModel get(int i) throws TemplateModelException {
         ArrayList matchingInputParts = this.matchingInputParts;
         if (matchingInputParts == null) {
            matchingInputParts = this.getMatchingInputPartsAndStoreResults();
         }

         return (TemplateModel)matchingInputParts.get(i);
      }

      public boolean getAsBoolean() {
         Boolean result = this.entireInputMatched;
         return result != null ? result : this.isEntrieInputMatchesAndStoreResults();
      }

      TemplateModel getGroups() {
         TemplateSequenceModel entireInputMatchGroups = this.entireInputMatchGroups;
         if (entireInputMatchGroups == null) {
            final Matcher t = this.firedEntireInputMatcher;
            if (t == null) {
               this.isEntrieInputMatchesAndStoreResults();
               t = this.firedEntireInputMatcher;
            }

            entireInputMatchGroups = new TemplateSequenceModel() {
               public TemplateModel get(int i) throws TemplateModelException {
                  try {
                     return new SimpleScalar(t.group(i));
                  } catch (Exception var3) {
                     throw new _TemplateModelException(var3, "Failed to read regular expression match group");
                  }
               }

               public int size() throws TemplateModelException {
                  try {
                     return t.groupCount() + 1;
                  } catch (Exception var2) {
                     throw new _TemplateModelException(var2, "Failed to get regular expression match group count");
                  }
               }
            };
            this.entireInputMatchGroups = entireInputMatchGroups;
         }

         return entireInputMatchGroups;
      }

      private ArrayList getMatchingInputPartsAndStoreResults() throws TemplateModelException {
         ArrayList matchingInputParts = new ArrayList();
         Matcher matcher = this.pattern.matcher(this.input);

         while(matcher.find()) {
            matchingInputParts.add(new MatchWithGroups(this.input, matcher));
         }

         this.matchingInputParts = matchingInputParts;
         return matchingInputParts;
      }

      private boolean isEntrieInputMatchesAndStoreResults() {
         Matcher matcher = this.pattern.matcher(this.input);
         boolean matches = matcher.matches();
         this.firedEntireInputMatcher = matcher;
         this.entireInputMatched = matches;
         return matches;
      }

      public TemplateModelIterator iterator() {
         final ArrayList matchingInputParts = this.matchingInputParts;
         if (matchingInputParts == null) {
            final Matcher matcher = this.pattern.matcher(this.input);
            return new TemplateModelIterator() {
               private int nextIdx = 0;
               boolean hasFindInfo = matcher.find();

               public boolean hasNext() {
                  ArrayList matchingInputParts = RegexMatchModel.this.matchingInputParts;
                  if (matchingInputParts == null) {
                     return this.hasFindInfo;
                  } else {
                     return this.nextIdx < matchingInputParts.size();
                  }
               }

               public TemplateModel next() throws TemplateModelException {
                  ArrayList matchingInputParts = RegexMatchModel.this.matchingInputParts;
                  if (matchingInputParts == null) {
                     if (!this.hasFindInfo) {
                        throw new _TemplateModelException("There were no more regular expression matches");
                     } else {
                        MatchWithGroups result = new MatchWithGroups(RegexMatchModel.this.input, matcher);
                        ++this.nextIdx;
                        this.hasFindInfo = matcher.find();
                        return result;
                     }
                  } else {
                     try {
                        return (TemplateModel)matchingInputParts.get(this.nextIdx++);
                     } catch (IndexOutOfBoundsException var3) {
                        throw new _TemplateModelException(var3, "There were no more regular expression matches");
                     }
                  }
               }
            };
         } else {
            return new TemplateModelIterator() {
               private int nextIdx = 0;

               public boolean hasNext() {
                  return this.nextIdx < matchingInputParts.size();
               }

               public TemplateModel next() throws TemplateModelException {
                  try {
                     return (TemplateModel)matchingInputParts.get(this.nextIdx++);
                  } catch (IndexOutOfBoundsException var2) {
                     throw new _TemplateModelException(var2, "There were no more regular expression matches");
                  }
               }
            };
         }
      }

      public int size() throws TemplateModelException {
         ArrayList matchingInputParts = this.matchingInputParts;
         if (matchingInputParts == null) {
            matchingInputParts = this.getMatchingInputPartsAndStoreResults();
         }

         return matchingInputParts.size();
      }

      static class MatchWithGroups implements TemplateScalarModel {
         final String matchedInputPart;
         final SimpleSequence groupsSeq;

         MatchWithGroups(String input, Matcher matcher) {
            this.matchedInputPart = input.substring(matcher.start(), matcher.end());
            int grpCount = matcher.groupCount() + 1;
            this.groupsSeq = new SimpleSequence(grpCount, _TemplateAPI.SAFE_OBJECT_WRAPPER);

            for(int i = 0; i < grpCount; ++i) {
               this.groupsSeq.add(matcher.group(i));
            }

         }

         public String getAsString() {
            return this.matchedInputPart;
         }
      }
   }

   static class replace_reBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
         return new ReplaceMethod(s);
      }

      class ReplaceMethod implements TemplateMethodModel {
         private String s;

         ReplaceMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            replace_reBI.this.checkMethodArgCount(argCnt, 2, 3);
            String arg1 = (String)args.get(0);
            String arg2 = (String)args.get(1);
            long flags = argCnt > 2 ? RegexpHelper.parseFlagString((String)args.get(2)) : 0L;
            String result;
            if ((flags & 4294967296L) == 0L) {
               RegexpHelper.checkNonRegexpFlags("replace", flags);
               result = StringUtil.replace(this.s, arg1, arg2, (flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) != 0L, (flags & 8589934592L) != 0L);
            } else {
               Pattern pattern = RegexpHelper.getPattern(arg1, (int)flags);
               Matcher matcher = pattern.matcher(this.s);
               result = (flags & 8589934592L) != 0L ? matcher.replaceFirst(arg2) : matcher.replaceAll(arg2);
            }

            return new SimpleScalar(result);
         }
      }
   }

   static class matchesBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
         return new MatcherBuilder(s);
      }

      class MatcherBuilder implements TemplateMethodModel {
         String matchString;

         MatcherBuilder(String matchString) throws TemplateModelException {
            this.matchString = matchString;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            matchesBI.this.checkMethodArgCount(argCnt, 1, 2);
            String patternString = (String)args.get(0);
            long flags = argCnt > 1 ? RegexpHelper.parseFlagString((String)args.get(1)) : 0L;
            if ((flags & 8589934592L) != 0L) {
               RegexpHelper.logFlagWarning("?" + matchesBI.this.key + " doesn't support the \"f\" flag.");
            }

            Pattern pattern = RegexpHelper.getPattern(patternString, (int)flags);
            return new RegexMatchModel(pattern, this.matchString);
         }
      }
   }

   static class groupsBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel targetModel = this.target.eval(env);
         this.assertNonNull(targetModel, env);
         if (targetModel instanceof RegexMatchModel) {
            return ((RegexMatchModel)targetModel).getGroups();
         } else if (targetModel instanceof RegexMatchModel.MatchWithGroups) {
            return ((RegexMatchModel.MatchWithGroups)targetModel).groupsSeq;
         } else {
            throw new UnexpectedTypeException(this.target, targetModel, "regular expression matcher", new Class[]{RegexMatchModel.class, RegexMatchModel.MatchWithGroups.class}, env);
         }
      }
   }
}
