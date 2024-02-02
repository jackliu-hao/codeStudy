/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ class BuiltInsForStringsRegexp
/*     */ {
/*     */   static class groupsBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/*  50 */       TemplateModel targetModel = this.target.eval(env);
/*  51 */       assertNonNull(targetModel, env);
/*  52 */       if (targetModel instanceof BuiltInsForStringsRegexp.RegexMatchModel)
/*  53 */         return ((BuiltInsForStringsRegexp.RegexMatchModel)targetModel).getGroups(); 
/*  54 */       if (targetModel instanceof BuiltInsForStringsRegexp.RegexMatchModel.MatchWithGroups) {
/*  55 */         return (TemplateModel)((BuiltInsForStringsRegexp.RegexMatchModel.MatchWithGroups)targetModel).groupsSeq;
/*     */       }
/*  57 */       throw new UnexpectedTypeException(this.target, targetModel, "regular expression matcher", new Class[] { BuiltInsForStringsRegexp.RegexMatchModel.class, BuiltInsForStringsRegexp.RegexMatchModel.MatchWithGroups.class }, env);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class matchesBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     class MatcherBuilder
/*     */       implements TemplateMethodModel
/*     */     {
/*     */       String matchString;
/*     */       
/*     */       MatcherBuilder(String matchString) throws TemplateModelException {
/*  71 */         this.matchString = matchString;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List<String> args) throws TemplateModelException {
/*  76 */         int argCnt = args.size();
/*  77 */         BuiltInsForStringsRegexp.matchesBI.this.checkMethodArgCount(argCnt, 1, 2);
/*     */         
/*  79 */         String patternString = args.get(0);
/*  80 */         long flags = (argCnt > 1) ? RegexpHelper.parseFlagString(args.get(1)) : 0L;
/*  81 */         if ((flags & 0x200000000L) != 0L) {
/*  82 */           RegexpHelper.logFlagWarning("?" + BuiltInsForStringsRegexp.matchesBI.this.key + " doesn't support the \"f\" flag.");
/*     */         }
/*  84 */         Pattern pattern = RegexpHelper.getPattern(patternString, (int)flags);
/*  85 */         return new BuiltInsForStringsRegexp.RegexMatchModel(pattern, this.matchString);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
/*  91 */       return (TemplateModel)new MatcherBuilder(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class replace_reBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     class ReplaceMethod
/*     */       implements TemplateMethodModel
/*     */     {
/*     */       ReplaceMethod(String s) {
/* 102 */         this.s = s;
/*     */       }
/*     */       private String s;
/*     */       public Object exec(List<String> args) throws TemplateModelException {
/*     */         String result;
/* 107 */         int argCnt = args.size();
/* 108 */         BuiltInsForStringsRegexp.replace_reBI.this.checkMethodArgCount(argCnt, 2, 3);
/* 109 */         String arg1 = args.get(0);
/* 110 */         String arg2 = args.get(1);
/* 111 */         long flags = (argCnt > 2) ? RegexpHelper.parseFlagString(args.get(2)) : 0L;
/*     */         
/* 113 */         if ((flags & 0x100000000L) == 0L) {
/* 114 */           RegexpHelper.checkNonRegexpFlags("replace", flags);
/* 115 */           result = StringUtil.replace(this.s, arg1, arg2, ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) != 0L), ((flags & 0x200000000L) != 0L));
/*     */         }
/*     */         else {
/*     */           
/* 119 */           Pattern pattern = RegexpHelper.getPattern(arg1, (int)flags);
/* 120 */           Matcher matcher = pattern.matcher(this.s);
/*     */ 
/*     */           
/* 123 */           result = ((flags & 0x200000000L) != 0L) ? matcher.replaceFirst(arg2) : matcher.replaceAll(arg2);
/*     */         } 
/* 125 */         return new SimpleScalar(result);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
/* 132 */       return (TemplateModel)new ReplaceMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class RegexMatchModel implements TemplateBooleanModel, TemplateCollectionModel, TemplateSequenceModel {
/*     */     final Pattern pattern;
/*     */     final String input;
/*     */     private Matcher firedEntireInputMatcher;
/*     */     private Boolean entireInputMatched;
/*     */     private TemplateSequenceModel entireInputMatchGroups;
/*     */     private ArrayList matchingInputParts;
/*     */     
/*     */     static class MatchWithGroups implements TemplateScalarModel {
/*     */       MatchWithGroups(String input, Matcher matcher) {
/* 146 */         this.matchedInputPart = input.substring(matcher.start(), matcher.end());
/* 147 */         int grpCount = matcher.groupCount() + 1;
/* 148 */         this.groupsSeq = new SimpleSequence(grpCount, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 149 */         for (int i = 0; i < grpCount; i++)
/* 150 */           this.groupsSeq.add(matcher.group(i)); 
/*     */       }
/*     */       final String matchedInputPart;
/*     */       final SimpleSequence groupsSeq;
/*     */       
/*     */       public String getAsString() {
/* 156 */         return this.matchedInputPart;
/*     */       }
/*     */     }
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
/*     */     RegexMatchModel(Pattern pattern, String input) {
/* 170 */       this.pattern = pattern;
/* 171 */       this.input = input;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int i) throws TemplateModelException {
/* 176 */       ArrayList<TemplateModel> matchingInputParts = this.matchingInputParts;
/* 177 */       if (matchingInputParts == null) {
/* 178 */         matchingInputParts = getMatchingInputPartsAndStoreResults();
/*     */       }
/* 180 */       return matchingInputParts.get(i);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean getAsBoolean() {
/* 185 */       Boolean result = this.entireInputMatched;
/* 186 */       return (result != null) ? result.booleanValue() : isEntrieInputMatchesAndStoreResults();
/*     */     }
/*     */     
/*     */     TemplateModel getGroups() {
/* 190 */       TemplateSequenceModel entireInputMatchGroups = this.entireInputMatchGroups;
/* 191 */       if (entireInputMatchGroups == null) {
/* 192 */         Matcher t = this.firedEntireInputMatcher;
/* 193 */         if (t == null) {
/* 194 */           isEntrieInputMatchesAndStoreResults();
/* 195 */           t = this.firedEntireInputMatcher;
/*     */         } 
/* 197 */         final Matcher firedEntireInputMatcher = t;
/*     */         
/* 199 */         entireInputMatchGroups = new TemplateSequenceModel()
/*     */           {
/*     */             public TemplateModel get(int i) throws TemplateModelException
/*     */             {
/*     */               try {
/* 204 */                 return (TemplateModel)new SimpleScalar(firedEntireInputMatcher.group(i));
/* 205 */               } catch (Exception e) {
/* 206 */                 throw new _TemplateModelException(e, "Failed to read regular expression match group");
/*     */               } 
/*     */             }
/*     */ 
/*     */             
/*     */             public int size() throws TemplateModelException {
/*     */               try {
/* 213 */                 return firedEntireInputMatcher.groupCount() + 1;
/* 214 */               } catch (Exception e) {
/* 215 */                 throw new _TemplateModelException(e, "Failed to get regular expression match group count");
/*     */               } 
/*     */             }
/*     */           };
/*     */         
/* 220 */         this.entireInputMatchGroups = entireInputMatchGroups;
/*     */       } 
/* 222 */       return (TemplateModel)entireInputMatchGroups;
/*     */     }
/*     */     
/*     */     private ArrayList getMatchingInputPartsAndStoreResults() throws TemplateModelException {
/* 226 */       ArrayList<MatchWithGroups> matchingInputParts = new ArrayList();
/*     */       
/* 228 */       Matcher matcher = this.pattern.matcher(this.input);
/* 229 */       while (matcher.find()) {
/* 230 */         matchingInputParts.add(new MatchWithGroups(this.input, matcher));
/*     */       }
/*     */       
/* 233 */       this.matchingInputParts = matchingInputParts;
/* 234 */       return matchingInputParts;
/*     */     }
/*     */     
/*     */     private boolean isEntrieInputMatchesAndStoreResults() {
/* 238 */       Matcher matcher = this.pattern.matcher(this.input);
/* 239 */       boolean matches = matcher.matches();
/* 240 */       this.firedEntireInputMatcher = matcher;
/* 241 */       this.entireInputMatched = Boolean.valueOf(matches);
/* 242 */       return matches;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModelIterator iterator() {
/* 247 */       final ArrayList matchingInputParts = this.matchingInputParts;
/* 248 */       if (matchingInputParts == null) {
/* 249 */         final Matcher matcher = this.pattern.matcher(this.input);
/* 250 */         return new TemplateModelIterator()
/*     */           {
/* 252 */             private int nextIdx = 0;
/* 253 */             boolean hasFindInfo = matcher.find();
/*     */ 
/*     */             
/*     */             public boolean hasNext() {
/* 257 */               ArrayList matchingInputParts = BuiltInsForStringsRegexp.RegexMatchModel.this.matchingInputParts;
/* 258 */               if (matchingInputParts == null) {
/* 259 */                 return this.hasFindInfo;
/*     */               }
/* 261 */               return (this.nextIdx < matchingInputParts.size());
/*     */             }
/*     */ 
/*     */ 
/*     */             
/*     */             public TemplateModel next() throws TemplateModelException {
/* 267 */               ArrayList<TemplateModel> matchingInputParts = BuiltInsForStringsRegexp.RegexMatchModel.this.matchingInputParts;
/* 268 */               if (matchingInputParts == null) {
/* 269 */                 if (!this.hasFindInfo) {
/* 270 */                   throw new _TemplateModelException("There were no more regular expression matches");
/*     */                 }
/* 272 */                 BuiltInsForStringsRegexp.RegexMatchModel.MatchWithGroups result = new BuiltInsForStringsRegexp.RegexMatchModel.MatchWithGroups(BuiltInsForStringsRegexp.RegexMatchModel.this.input, matcher);
/* 273 */                 this.nextIdx++;
/* 274 */                 this.hasFindInfo = matcher.find();
/* 275 */                 return (TemplateModel)result;
/*     */               } 
/*     */               try {
/* 278 */                 return matchingInputParts.get(this.nextIdx++);
/* 279 */               } catch (IndexOutOfBoundsException e) {
/* 280 */                 throw new _TemplateModelException(e, "There were no more regular expression matches");
/*     */               } 
/*     */             }
/*     */           };
/*     */       } 
/*     */ 
/*     */       
/* 287 */       return new TemplateModelIterator()
/*     */         {
/* 289 */           private int nextIdx = 0;
/*     */ 
/*     */           
/*     */           public boolean hasNext() {
/* 293 */             return (this.nextIdx < matchingInputParts.size());
/*     */           }
/*     */ 
/*     */           
/*     */           public TemplateModel next() throws TemplateModelException {
/*     */             try {
/* 299 */               return matchingInputParts.get(this.nextIdx++);
/* 300 */             } catch (IndexOutOfBoundsException e) {
/* 301 */               throw new _TemplateModelException(e, "There were no more regular expression matches");
/*     */             } 
/*     */           }
/*     */         };
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 310 */       ArrayList matchingInputParts = this.matchingInputParts;
/* 311 */       if (matchingInputParts == null) {
/* 312 */         matchingInputParts = getMatchingInputPartsAndStoreResults();
/*     */       }
/* 314 */       return matchingInputParts.size();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForStringsRegexp.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */