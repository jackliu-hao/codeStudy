package freemarker.core;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.StringUtil;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BuiltInsForStringsBasic {
   private BuiltInsForStringsBasic() {
   }

   static class word_listBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) {
         SimpleSequence result = new SimpleSequence(_TemplateAPI.SAFE_OBJECT_WRAPPER);
         StringTokenizer st = new StringTokenizer(s);

         while(st.hasMoreTokens()) {
            result.add(st.nextToken());
         }

         return result;
      }
   }

   static class upper_caseBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) {
         return new SimpleScalar(s.toUpperCase(env.getLocale()));
      }
   }

   static class uncap_firstBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) {
         int i = 0;

         int ln;
         for(ln = s.length(); i < ln && Character.isWhitespace(s.charAt(i)); ++i) {
         }

         if (i < ln) {
            StringBuilder b = new StringBuilder(s);
            b.setCharAt(i, Character.toLowerCase(s.charAt(i)));
            s = b.toString();
         }

         return new SimpleScalar(s);
      }
   }

   static class truncate_c_mBI extends AbstractTruncateBI {
      protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
         return algorithm.truncateCM(s, maxLength, terminator, terminatorLength, env);
      }

      protected boolean allowMarkupTerminator() {
         return true;
      }
   }

   static class truncate_w_mBI extends AbstractTruncateBI {
      protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
         return algorithm.truncateWM(s, maxLength, terminator, terminatorLength, env);
      }

      protected boolean allowMarkupTerminator() {
         return true;
      }
   }

   static class truncate_mBI extends AbstractTruncateBI {
      protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
         return algorithm.truncateM(s, maxLength, terminator, terminatorLength, env);
      }

      protected boolean allowMarkupTerminator() {
         return true;
      }
   }

   static class truncate_cBI extends AbstractTruncateBI {
      protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
         return algorithm.truncateC(s, maxLength, (TemplateScalarModel)terminator, terminatorLength, env);
      }

      protected boolean allowMarkupTerminator() {
         return false;
      }
   }

   static class truncate_wBI extends AbstractTruncateBI {
      protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
         return algorithm.truncateW(s, maxLength, (TemplateScalarModel)terminator, terminatorLength, env);
      }

      protected boolean allowMarkupTerminator() {
         return false;
      }
   }

   static class truncateBI extends AbstractTruncateBI {
      protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
         return algorithm.truncate(s, maxLength, (TemplateScalarModel)terminator, terminatorLength, env);
      }

      protected boolean allowMarkupTerminator() {
         return false;
      }
   }

   abstract static class AbstractTruncateBI extends BuiltInForString {
      TemplateModel calculateResult(final String s, final Environment env) {
         return new TemplateMethodModelEx() {
            public Object exec(List args) throws TemplateModelException {
               int argCount = args.size();
               AbstractTruncateBI.this.checkMethodArgCount(argCount, 1, 3);
               int maxLength = AbstractTruncateBI.this.getNumberMethodArg(args, 0).intValue();
               if (maxLength < 0) {
                  throw new _TemplateModelException(new Object[]{"?", AbstractTruncateBI.this.key, "(...) argument #1 can't be negative."});
               } else {
                  TemplateModel terminator;
                  Integer terminatorLength;
                  if (argCount > 1) {
                     terminator = (TemplateModel)args.get(1);
                     if (!(terminator instanceof TemplateScalarModel)) {
                        if (!AbstractTruncateBI.this.allowMarkupTerminator()) {
                           throw _MessageUtil.newMethodArgMustBeStringException("?" + AbstractTruncateBI.this.key, 1, terminator);
                        }

                        if (!(terminator instanceof TemplateMarkupOutputModel)) {
                           throw _MessageUtil.newMethodArgMustBeStringOrMarkupOutputException("?" + AbstractTruncateBI.this.key, 1, terminator);
                        }
                     }

                     Number terminatorLengthNum = AbstractTruncateBI.this.getOptNumberMethodArg(args, 2);
                     terminatorLength = terminatorLengthNum != null ? terminatorLengthNum.intValue() : null;
                     if (terminatorLength != null && terminatorLength < 0) {
                        throw new _TemplateModelException(new Object[]{"?", AbstractTruncateBI.this.key, "(...) argument #3 can't be negative."});
                     }
                  } else {
                     terminator = null;
                     terminatorLength = null;
                  }

                  try {
                     TruncateBuiltinAlgorithm algorithm = env.getTruncateBuiltinAlgorithm();
                     return AbstractTruncateBI.this.truncate(algorithm, s, maxLength, terminator, terminatorLength, env);
                  } catch (TemplateException var7) {
                     throw new _TemplateModelException(AbstractTruncateBI.this, var7, env, "Truncation failed; see cause exception");
                  }
               }
            }
         };
      }

      protected abstract TemplateModel truncate(TruncateBuiltinAlgorithm var1, String var2, int var3, TemplateModel var4, Integer var5, Environment var6) throws TemplateException;

      protected abstract boolean allowMarkupTerminator();
   }

   static class trimBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) {
         return new SimpleScalar(s.trim());
      }
   }

   static class substringBI extends BuiltInForString {
      TemplateModel calculateResult(final String s, Environment env) throws TemplateException {
         return new TemplateMethodModelEx() {
            public Object exec(List args) throws TemplateModelException {
               int argCount = args.size();
               substringBI.this.checkMethodArgCount(argCount, 1, 2);
               int beginIdx = substringBI.this.getNumberMethodArg(args, 0).intValue();
               int len = s.length();
               if (beginIdx < 0) {
                  throw this.newIndexLessThan0Exception(0, beginIdx);
               } else if (beginIdx > len) {
                  throw this.newIndexGreaterThanLengthException(0, beginIdx, len);
               } else if (argCount > 1) {
                  int endIdx = substringBI.this.getNumberMethodArg(args, 1).intValue();
                  if (endIdx < 0) {
                     throw this.newIndexLessThan0Exception(1, endIdx);
                  } else if (endIdx > len) {
                     throw this.newIndexGreaterThanLengthException(1, endIdx, len);
                  } else if (beginIdx > endIdx) {
                     throw _MessageUtil.newMethodArgsInvalidValueException("?" + substringBI.this.key, "The begin index argument, ", beginIdx, ", shouldn't be greater than the end index argument, ", endIdx, ".");
                  } else {
                     return new SimpleScalar(s.substring(beginIdx, endIdx));
                  }
               } else {
                  return new SimpleScalar(s.substring(beginIdx));
               }
            }

            private TemplateModelException newIndexGreaterThanLengthException(int argIdx, int idx, int len) throws TemplateModelException {
               return _MessageUtil.newMethodArgInvalidValueException("?" + substringBI.this.key, argIdx, "The index mustn't be greater than the length of the string, ", len, ", but it was ", idx, ".");
            }

            private TemplateModelException newIndexLessThan0Exception(int argIdx, int idx) throws TemplateModelException {
               return _MessageUtil.newMethodArgInvalidValueException("?" + substringBI.this.key, argIdx, "The index must be at least 0, but was ", idx, ".");
            }
         };
      }
   }

   static class starts_withBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new BIMethod(s);
      }

      private class BIMethod implements TemplateMethodModelEx {
         private String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            starts_withBI.this.checkMethodArgCount(args, 1);
            return this.s.startsWith(starts_withBI.this.getStringMethodArg(args, 0)) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class split_BI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
         return new SplitMethod(s);
      }

      class SplitMethod implements TemplateMethodModel {
         private String s;

         SplitMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            split_BI.this.checkMethodArgCount(argCnt, 1, 2);
            String splitString = (String)args.get(0);
            long flags = argCnt > 1 ? RegexpHelper.parseFlagString((String)args.get(1)) : 0L;
            String[] result = null;
            if ((flags & 4294967296L) == 0L) {
               RegexpHelper.checkNonRegexpFlags(split_BI.this.key, flags);
               result = StringUtil.split(this.s, splitString, (flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) != 0L);
            } else {
               Pattern pattern = RegexpHelper.getPattern(splitString, (int)flags);
               result = pattern.split(this.s);
            }

            return ObjectWrapper.DEFAULT_WRAPPER.wrap(result);
         }
      }
   }

   static class remove_endingBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new BIMethod(s);
      }

      private class BIMethod implements TemplateMethodModelEx {
         private String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            remove_endingBI.this.checkMethodArgCount(args, 1);
            String suffix = remove_endingBI.this.getStringMethodArg(args, 0);
            return new SimpleScalar(this.s.endsWith(suffix) ? this.s.substring(0, this.s.length() - suffix.length()) : this.s);
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class remove_beginningBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new BIMethod(s);
      }

      private class BIMethod implements TemplateMethodModelEx {
         private String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            remove_beginningBI.this.checkMethodArgCount(args, 1);
            String prefix = remove_beginningBI.this.getStringMethodArg(args, 0);
            return new SimpleScalar(this.s.startsWith(prefix) ? this.s.substring(prefix.length()) : this.s);
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class padBI extends BuiltInForString {
      private final boolean leftPadder;

      padBI(boolean leftPadder) {
         this.leftPadder = leftPadder;
      }

      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new BIMethod(s);
      }

      private class BIMethod implements TemplateMethodModelEx {
         private final String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            padBI.this.checkMethodArgCount(argCnt, 1, 2);
            int width = padBI.this.getNumberMethodArg(args, 0).intValue();
            if (argCnt > 1) {
               String filling = padBI.this.getStringMethodArg(args, 1);

               try {
                  return new SimpleScalar(padBI.this.leftPadder ? StringUtil.leftPad(this.s, width, filling) : StringUtil.rightPad(this.s, width, filling));
               } catch (IllegalArgumentException var6) {
                  if (filling.length() == 0) {
                     throw new _TemplateModelException(new Object[]{"?", padBI.this.key, "(...) argument #2 can't be a 0-length string."});
                  } else {
                     throw new _TemplateModelException(var6, new Object[]{"?", padBI.this.key, "(...) failed: ", var6});
                  }
               }
            } else {
               return new SimpleScalar(padBI.this.leftPadder ? StringUtil.leftPad(this.s, width) : StringUtil.rightPad(this.s, width));
            }
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class lower_caseBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) {
         return new SimpleScalar(s.toLowerCase(env.getLocale()));
      }
   }

   static class lengthBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new SimpleNumber(s.length());
      }
   }

   static class keep_before_lastBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
         return new KeepUntilMethod(s);
      }

      class KeepUntilMethod implements TemplateMethodModelEx {
         private String s;

         KeepUntilMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            keep_before_lastBI.this.checkMethodArgCount(argCnt, 1, 2);
            String separatorString = keep_before_lastBI.this.getStringMethodArg(args, 0);
            long flags = argCnt > 1 ? RegexpHelper.parseFlagString(keep_before_lastBI.this.getStringMethodArg(args, 1)) : 0L;
            int stopIndex;
            if ((flags & 4294967296L) == 0L) {
               RegexpHelper.checkOnlyHasNonRegexpFlags(keep_before_lastBI.this.key, flags, true);
               if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
                  stopIndex = this.s.lastIndexOf(separatorString);
               } else {
                  stopIndex = this.s.toLowerCase().lastIndexOf(separatorString.toLowerCase());
               }
            } else if (separatorString.length() == 0) {
               stopIndex = this.s.length();
            } else {
               Pattern pattern = RegexpHelper.getPattern(separatorString, (int)flags);
               Matcher matcher = pattern.matcher(this.s);
               if (matcher.find()) {
                  for(stopIndex = matcher.start(); matcher.find(stopIndex + 1); stopIndex = matcher.start()) {
                  }
               } else {
                  stopIndex = -1;
               }
            }

            return stopIndex == -1 ? new SimpleScalar(this.s) : new SimpleScalar(this.s.substring(0, stopIndex));
         }
      }
   }

   static class keep_beforeBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
         return new KeepUntilMethod(s);
      }

      class KeepUntilMethod implements TemplateMethodModelEx {
         private String s;

         KeepUntilMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            keep_beforeBI.this.checkMethodArgCount(argCnt, 1, 2);
            String separatorString = keep_beforeBI.this.getStringMethodArg(args, 0);
            long flags = argCnt > 1 ? RegexpHelper.parseFlagString(keep_beforeBI.this.getStringMethodArg(args, 1)) : 0L;
            int stopIndex;
            if ((flags & 4294967296L) == 0L) {
               RegexpHelper.checkOnlyHasNonRegexpFlags(keep_beforeBI.this.key, flags, true);
               if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
                  stopIndex = this.s.indexOf(separatorString);
               } else {
                  stopIndex = this.s.toLowerCase().indexOf(separatorString.toLowerCase());
               }
            } else {
               Pattern pattern = RegexpHelper.getPattern(separatorString, (int)flags);
               Matcher matcher = pattern.matcher(this.s);
               if (matcher.find()) {
                  stopIndex = matcher.start();
               } else {
                  stopIndex = -1;
               }
            }

            return stopIndex == -1 ? new SimpleScalar(this.s) : new SimpleScalar(this.s.substring(0, stopIndex));
         }
      }
   }

   static class keep_after_lastBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
         return new KeepAfterMethod(s);
      }

      class KeepAfterMethod implements TemplateMethodModelEx {
         private String s;

         KeepAfterMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            keep_after_lastBI.this.checkMethodArgCount(argCnt, 1, 2);
            String separatorString = keep_after_lastBI.this.getStringMethodArg(args, 0);
            long flags = argCnt > 1 ? RegexpHelper.parseFlagString(keep_after_lastBI.this.getStringMethodArg(args, 1)) : 0L;
            int startIndex;
            if ((flags & 4294967296L) == 0L) {
               RegexpHelper.checkOnlyHasNonRegexpFlags(keep_after_lastBI.this.key, flags, true);
               if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
                  startIndex = this.s.lastIndexOf(separatorString);
               } else {
                  startIndex = this.s.toLowerCase().lastIndexOf(separatorString.toLowerCase());
               }

               if (startIndex >= 0) {
                  startIndex += separatorString.length();
               }
            } else if (separatorString.length() == 0) {
               startIndex = this.s.length();
            } else {
               Pattern pattern = RegexpHelper.getPattern(separatorString, (int)flags);
               Matcher matcher = pattern.matcher(this.s);
               if (matcher.find()) {
                  for(startIndex = matcher.end(); matcher.find(matcher.start() + 1); startIndex = matcher.end()) {
                  }
               } else {
                  startIndex = -1;
               }
            }

            return startIndex == -1 ? TemplateScalarModel.EMPTY_STRING : new SimpleScalar(this.s.substring(startIndex));
         }
      }
   }

   static class keep_afterBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
         return new KeepAfterMethod(s);
      }

      class KeepAfterMethod implements TemplateMethodModelEx {
         private String s;

         KeepAfterMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            keep_afterBI.this.checkMethodArgCount(argCnt, 1, 2);
            String separatorString = keep_afterBI.this.getStringMethodArg(args, 0);
            long flags = argCnt > 1 ? RegexpHelper.parseFlagString(keep_afterBI.this.getStringMethodArg(args, 1)) : 0L;
            int startIndex;
            if ((flags & 4294967296L) == 0L) {
               RegexpHelper.checkOnlyHasNonRegexpFlags(keep_afterBI.this.key, flags, true);
               if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
                  startIndex = this.s.indexOf(separatorString);
               } else {
                  startIndex = this.s.toLowerCase().indexOf(separatorString.toLowerCase());
               }

               if (startIndex >= 0) {
                  startIndex += separatorString.length();
               }
            } else {
               Pattern pattern = RegexpHelper.getPattern(separatorString, (int)flags);
               Matcher matcher = pattern.matcher(this.s);
               if (matcher.find()) {
                  startIndex = matcher.end();
               } else {
                  startIndex = -1;
               }
            }

            return startIndex == -1 ? TemplateScalarModel.EMPTY_STRING : new SimpleScalar(this.s.substring(startIndex));
         }
      }
   }

   static class index_ofBI extends BuiltIn {
      private final boolean findLast;

      index_ofBI(boolean findLast) {
         this.findLast = findLast;
      }

      TemplateModel _eval(Environment env) throws TemplateException {
         return new BIMethod(this.target.evalAndCoerceToStringOrUnsupportedMarkup(env, "For sequences/collections (lists and such) use \"?seq_index_of\" instead."));
      }

      private class BIMethod implements TemplateMethodModelEx {
         private final String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            int argCnt = args.size();
            index_ofBI.this.checkMethodArgCount(argCnt, 1, 2);
            String subStr = index_ofBI.this.getStringMethodArg(args, 0);
            if (argCnt > 1) {
               int startIdx = index_ofBI.this.getNumberMethodArg(args, 1).intValue();
               return new SimpleNumber(index_ofBI.this.findLast ? this.s.lastIndexOf(subStr, startIdx) : this.s.indexOf(subStr, startIdx));
            } else {
               return new SimpleNumber(index_ofBI.this.findLast ? this.s.lastIndexOf(subStr) : this.s.indexOf(subStr));
            }
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class ensure_starts_withBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new BIMethod(s);
      }

      private class BIMethod implements TemplateMethodModelEx {
         private String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            ensure_starts_withBI.this.checkMethodArgCount(args, 1, 3);
            String checkedPrefix = ensure_starts_withBI.this.getStringMethodArg(args, 0);
            boolean startsWithPrefix;
            String addedPrefix;
            if (args.size() > 1) {
               addedPrefix = ensure_starts_withBI.this.getStringMethodArg(args, 1);
               long flags = args.size() > 2 ? RegexpHelper.parseFlagString(ensure_starts_withBI.this.getStringMethodArg(args, 2)) : 4294967296L;
               if ((flags & 4294967296L) == 0L) {
                  RegexpHelper.checkOnlyHasNonRegexpFlags(ensure_starts_withBI.this.key, flags, true);
                  if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
                     startsWithPrefix = this.s.startsWith(checkedPrefix);
                  } else {
                     startsWithPrefix = this.s.toLowerCase().startsWith(checkedPrefix.toLowerCase());
                  }
               } else {
                  Pattern pattern = RegexpHelper.getPattern(checkedPrefix, (int)flags);
                  Matcher matcher = pattern.matcher(this.s);
                  startsWithPrefix = matcher.lookingAt();
               }
            } else {
               startsWithPrefix = this.s.startsWith(checkedPrefix);
               addedPrefix = checkedPrefix;
            }

            return new SimpleScalar(startsWithPrefix ? this.s : addedPrefix + this.s);
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class ensure_ends_withBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new BIMethod(s);
      }

      private class BIMethod implements TemplateMethodModelEx {
         private String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            ensure_ends_withBI.this.checkMethodArgCount(args, 1);
            String suffix = ensure_ends_withBI.this.getStringMethodArg(args, 0);
            return new SimpleScalar(this.s.endsWith(suffix) ? this.s : this.s + suffix);
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class ends_withBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new BIMethod(s);
      }

      private class BIMethod implements TemplateMethodModelEx {
         private String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            ends_withBI.this.checkMethodArgCount(args, 1);
            return this.s.endsWith(ends_withBI.this.getStringMethodArg(args, 0)) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class containsBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         return new BIMethod(this.target.evalAndCoerceToStringOrUnsupportedMarkup(env, "For sequences/collections (lists and such) use \"?seq_contains\" instead."));
      }

      private class BIMethod implements TemplateMethodModelEx {
         private final String s;

         private BIMethod(String s) {
            this.s = s;
         }

         public Object exec(List args) throws TemplateModelException {
            containsBI.this.checkMethodArgCount(args, 1);
            return this.s.indexOf(containsBI.this.getStringMethodArg(args, 0)) != -1 ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
         }

         // $FF: synthetic method
         BIMethod(String x1, Object x2) {
            this(x1);
         }
      }
   }

   static class chop_linebreakBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) {
         return new SimpleScalar(StringUtil.chomp(s));
      }
   }

   static class capitalizeBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) {
         return new SimpleScalar(StringUtil.capitalize(s));
      }
   }

   static class cap_firstBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) {
         int i = 0;

         int ln;
         for(ln = s.length(); i < ln && Character.isWhitespace(s.charAt(i)); ++i) {
         }

         if (i < ln) {
            StringBuilder b = new StringBuilder(s);
            b.setCharAt(i, Character.toUpperCase(s.charAt(i)));
            s = b.toString();
         }

         return new SimpleScalar(s);
      }
   }
}
