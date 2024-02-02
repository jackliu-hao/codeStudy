/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
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
/*     */ class BuiltInsForStringsBasic
/*     */ {
/*     */   static class cap_firstBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/*  46 */       int i = 0;
/*  47 */       int ln = s.length();
/*  48 */       while (i < ln && Character.isWhitespace(s.charAt(i))) {
/*  49 */         i++;
/*     */       }
/*  51 */       if (i < ln) {
/*  52 */         StringBuilder b = new StringBuilder(s);
/*  53 */         b.setCharAt(i, Character.toUpperCase(s.charAt(i)));
/*  54 */         s = b.toString();
/*     */       } 
/*  56 */       return (TemplateModel)new SimpleScalar(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class capitalizeBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/*  63 */       return (TemplateModel)new SimpleScalar(StringUtil.capitalize(s));
/*     */     }
/*     */   }
/*     */   
/*     */   static class chop_linebreakBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/*  70 */       return (TemplateModel)new SimpleScalar(StringUtil.chomp(s));
/*     */     }
/*     */   }
/*     */   
/*     */   static class containsBI
/*     */     extends BuiltIn {
/*     */     private class BIMethod
/*     */       implements TemplateMethodModelEx {
/*     */       private final String s;
/*     */       
/*     */       private BIMethod(String s) {
/*  81 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/*  86 */         BuiltInsForStringsBasic.containsBI.this.checkMethodArgCount(args, 1);
/*  87 */         return (this.s.indexOf(BuiltInsForStringsBasic.containsBI.this.getStringMethodArg(args, 0)) != -1) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/*  94 */       return (TemplateModel)new BIMethod(this.target.evalAndCoerceToStringOrUnsupportedMarkup(env, "For sequences/collections (lists and such) use \"?seq_contains\" instead."));
/*     */     }
/*     */   }
/*     */   
/*     */   static class ends_withBI
/*     */     extends BuiltInForString {
/*     */     private class BIMethod
/*     */       implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       private BIMethod(String s) {
/* 105 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 110 */         BuiltInsForStringsBasic.ends_withBI.this.checkMethodArgCount(args, 1);
/* 111 */         return this.s.endsWith(BuiltInsForStringsBasic.ends_withBI.this.getStringMethodArg(args, 0)) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 118 */       return (TemplateModel)new BIMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ensure_ends_withBI
/*     */     extends BuiltInForString {
/*     */     private class BIMethod implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       private BIMethod(String s) {
/* 128 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 133 */         BuiltInsForStringsBasic.ensure_ends_withBI.this.checkMethodArgCount(args, 1);
/* 134 */         String suffix = BuiltInsForStringsBasic.ensure_ends_withBI.this.getStringMethodArg(args, 0);
/* 135 */         return new SimpleScalar(this.s.endsWith(suffix) ? this.s : (this.s + suffix));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 141 */       return (TemplateModel)new BIMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ensure_starts_withBI
/*     */     extends BuiltInForString {
/*     */     private class BIMethod implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       private BIMethod(String s) {
/* 151 */         this.s = s;
/*     */       }
/*     */       public Object exec(List args) throws TemplateModelException {
/*     */         boolean startsWithPrefix;
/*     */         String addedPrefix;
/* 156 */         BuiltInsForStringsBasic.ensure_starts_withBI.this.checkMethodArgCount(args, 1, 3);
/*     */         
/* 158 */         String checkedPrefix = BuiltInsForStringsBasic.ensure_starts_withBI.this.getStringMethodArg(args, 0);
/*     */ 
/*     */ 
/*     */         
/* 162 */         if (args.size() > 1) {
/* 163 */           addedPrefix = BuiltInsForStringsBasic.ensure_starts_withBI.this.getStringMethodArg(args, 1);
/*     */           
/* 165 */           long flags = (args.size() > 2) ? RegexpHelper.parseFlagString(BuiltInsForStringsBasic.ensure_starts_withBI.this.getStringMethodArg(args, 2)) : 4294967296L;
/*     */ 
/*     */           
/* 168 */           if ((flags & 0x100000000L) == 0L) {
/* 169 */             RegexpHelper.checkOnlyHasNonRegexpFlags(BuiltInsForStringsBasic.ensure_starts_withBI.this.key, flags, true);
/* 170 */             if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
/* 171 */               startsWithPrefix = this.s.startsWith(checkedPrefix);
/*     */             } else {
/* 173 */               startsWithPrefix = this.s.toLowerCase().startsWith(checkedPrefix.toLowerCase());
/*     */             } 
/*     */           } else {
/* 176 */             Pattern pattern = RegexpHelper.getPattern(checkedPrefix, (int)flags);
/* 177 */             Matcher matcher = pattern.matcher(this.s);
/* 178 */             startsWithPrefix = matcher.lookingAt();
/*     */           } 
/*     */         } else {
/* 181 */           startsWithPrefix = this.s.startsWith(checkedPrefix);
/* 182 */           addedPrefix = checkedPrefix;
/*     */         } 
/* 184 */         return new SimpleScalar(startsWithPrefix ? this.s : (addedPrefix + this.s));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 190 */       return (TemplateModel)new BIMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class index_ofBI extends BuiltIn {
/*     */     private final boolean findLast;
/*     */     
/*     */     private class BIMethod implements TemplateMethodModelEx {
/*     */       private final String s;
/*     */       
/*     */       private BIMethod(String s) {
/* 201 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 206 */         int argCnt = args.size();
/* 207 */         BuiltInsForStringsBasic.index_ofBI.this.checkMethodArgCount(argCnt, 1, 2);
/* 208 */         String subStr = BuiltInsForStringsBasic.index_ofBI.this.getStringMethodArg(args, 0);
/* 209 */         if (argCnt > 1) {
/* 210 */           int startIdx = BuiltInsForStringsBasic.index_ofBI.this.getNumberMethodArg(args, 1).intValue();
/* 211 */           return new SimpleNumber(BuiltInsForStringsBasic.index_ofBI.this.findLast ? this.s.lastIndexOf(subStr, startIdx) : this.s.indexOf(subStr, startIdx));
/*     */         } 
/* 213 */         return new SimpleNumber(BuiltInsForStringsBasic.index_ofBI.this.findLast ? this.s.lastIndexOf(subStr) : this.s.indexOf(subStr));
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     index_ofBI(boolean findLast) {
/* 221 */       this.findLast = findLast;
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 226 */       return (TemplateModel)new BIMethod(this.target.evalAndCoerceToStringOrUnsupportedMarkup(env, "For sequences/collections (lists and such) use \"?seq_index_of\" instead."));
/*     */     }
/*     */   }
/*     */   
/*     */   static class keep_afterBI
/*     */     extends BuiltInForString {
/*     */     class KeepAfterMethod implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       KeepAfterMethod(String s) {
/* 236 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 241 */         int startIndex, argCnt = args.size();
/* 242 */         BuiltInsForStringsBasic.keep_afterBI.this.checkMethodArgCount(argCnt, 1, 2);
/* 243 */         String separatorString = BuiltInsForStringsBasic.keep_afterBI.this.getStringMethodArg(args, 0);
/* 244 */         long flags = (argCnt > 1) ? RegexpHelper.parseFlagString(BuiltInsForStringsBasic.keep_afterBI.this.getStringMethodArg(args, 1)) : 0L;
/*     */ 
/*     */         
/* 247 */         if ((flags & 0x100000000L) == 0L) {
/* 248 */           RegexpHelper.checkOnlyHasNonRegexpFlags(BuiltInsForStringsBasic.keep_afterBI.this.key, flags, true);
/* 249 */           if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
/* 250 */             startIndex = this.s.indexOf(separatorString);
/*     */           } else {
/* 252 */             startIndex = this.s.toLowerCase().indexOf(separatorString.toLowerCase());
/*     */           } 
/* 254 */           if (startIndex >= 0) {
/* 255 */             startIndex += separatorString.length();
/*     */           }
/*     */         } else {
/* 258 */           Pattern pattern = RegexpHelper.getPattern(separatorString, (int)flags);
/* 259 */           Matcher matcher = pattern.matcher(this.s);
/* 260 */           if (matcher.find()) {
/* 261 */             startIndex = matcher.end();
/*     */           } else {
/* 263 */             startIndex = -1;
/*     */           } 
/*     */         } 
/* 266 */         return (startIndex == -1) ? TemplateScalarModel.EMPTY_STRING : new SimpleScalar(this.s.substring(startIndex));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
/* 272 */       return (TemplateModel)new KeepAfterMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class keep_after_lastBI
/*     */     extends BuiltInForString {
/*     */     class KeepAfterMethod implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       KeepAfterMethod(String s) {
/* 282 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 287 */         int startIndex, argCnt = args.size();
/* 288 */         BuiltInsForStringsBasic.keep_after_lastBI.this.checkMethodArgCount(argCnt, 1, 2);
/* 289 */         String separatorString = BuiltInsForStringsBasic.keep_after_lastBI.this.getStringMethodArg(args, 0);
/* 290 */         long flags = (argCnt > 1) ? RegexpHelper.parseFlagString(BuiltInsForStringsBasic.keep_after_lastBI.this.getStringMethodArg(args, 1)) : 0L;
/*     */ 
/*     */         
/* 293 */         if ((flags & 0x100000000L) == 0L) {
/* 294 */           RegexpHelper.checkOnlyHasNonRegexpFlags(BuiltInsForStringsBasic.keep_after_lastBI.this.key, flags, true);
/* 295 */           if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
/* 296 */             startIndex = this.s.lastIndexOf(separatorString);
/*     */           } else {
/* 298 */             startIndex = this.s.toLowerCase().lastIndexOf(separatorString.toLowerCase());
/*     */           } 
/* 300 */           if (startIndex >= 0) {
/* 301 */             startIndex += separatorString.length();
/*     */           }
/*     */         }
/* 304 */         else if (separatorString.length() == 0) {
/* 305 */           startIndex = this.s.length();
/*     */         } else {
/* 307 */           Pattern pattern = RegexpHelper.getPattern(separatorString, (int)flags);
/* 308 */           Matcher matcher = pattern.matcher(this.s);
/* 309 */           if (matcher.find()) {
/* 310 */             startIndex = matcher.end();
/* 311 */             while (matcher.find(matcher.start() + 1)) {
/* 312 */               startIndex = matcher.end();
/*     */             }
/*     */           } else {
/* 315 */             startIndex = -1;
/*     */           } 
/*     */         } 
/*     */         
/* 319 */         return (startIndex == -1) ? TemplateScalarModel.EMPTY_STRING : new SimpleScalar(this.s.substring(startIndex));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
/* 325 */       return (TemplateModel)new KeepAfterMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class keep_beforeBI
/*     */     extends BuiltInForString {
/*     */     class KeepUntilMethod implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       KeepUntilMethod(String s) {
/* 335 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 340 */         int stopIndex, argCnt = args.size();
/* 341 */         BuiltInsForStringsBasic.keep_beforeBI.this.checkMethodArgCount(argCnt, 1, 2);
/* 342 */         String separatorString = BuiltInsForStringsBasic.keep_beforeBI.this.getStringMethodArg(args, 0);
/* 343 */         long flags = (argCnt > 1) ? RegexpHelper.parseFlagString(BuiltInsForStringsBasic.keep_beforeBI.this.getStringMethodArg(args, 1)) : 0L;
/*     */ 
/*     */         
/* 346 */         if ((flags & 0x100000000L) == 0L) {
/* 347 */           RegexpHelper.checkOnlyHasNonRegexpFlags(BuiltInsForStringsBasic.keep_beforeBI.this.key, flags, true);
/* 348 */           if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
/* 349 */             stopIndex = this.s.indexOf(separatorString);
/*     */           } else {
/* 351 */             stopIndex = this.s.toLowerCase().indexOf(separatorString.toLowerCase());
/*     */           } 
/*     */         } else {
/* 354 */           Pattern pattern = RegexpHelper.getPattern(separatorString, (int)flags);
/* 355 */           Matcher matcher = pattern.matcher(this.s);
/* 356 */           if (matcher.find()) {
/* 357 */             stopIndex = matcher.start();
/*     */           } else {
/* 359 */             stopIndex = -1;
/*     */           } 
/*     */         } 
/* 362 */         return (stopIndex == -1) ? new SimpleScalar(this.s) : new SimpleScalar(this.s.substring(0, stopIndex));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
/* 368 */       return (TemplateModel)new KeepUntilMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class keep_before_lastBI
/*     */     extends BuiltInForString {
/*     */     class KeepUntilMethod
/*     */       implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       KeepUntilMethod(String s) {
/* 379 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 384 */         int stopIndex, argCnt = args.size();
/* 385 */         BuiltInsForStringsBasic.keep_before_lastBI.this.checkMethodArgCount(argCnt, 1, 2);
/* 386 */         String separatorString = BuiltInsForStringsBasic.keep_before_lastBI.this.getStringMethodArg(args, 0);
/* 387 */         long flags = (argCnt > 1) ? RegexpHelper.parseFlagString(BuiltInsForStringsBasic.keep_before_lastBI.this.getStringMethodArg(args, 1)) : 0L;
/*     */ 
/*     */         
/* 390 */         if ((flags & 0x100000000L) == 0L) {
/* 391 */           RegexpHelper.checkOnlyHasNonRegexpFlags(BuiltInsForStringsBasic.keep_before_lastBI.this.key, flags, true);
/* 392 */           if ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) == 0L) {
/* 393 */             stopIndex = this.s.lastIndexOf(separatorString);
/*     */           } else {
/* 395 */             stopIndex = this.s.toLowerCase().lastIndexOf(separatorString.toLowerCase());
/*     */           }
/*     */         
/* 398 */         } else if (separatorString.length() == 0) {
/* 399 */           stopIndex = this.s.length();
/*     */         } else {
/* 401 */           Pattern pattern = RegexpHelper.getPattern(separatorString, (int)flags);
/* 402 */           Matcher matcher = pattern.matcher(this.s);
/* 403 */           if (matcher.find()) {
/* 404 */             stopIndex = matcher.start();
/* 405 */             while (matcher.find(stopIndex + 1)) {
/* 406 */               stopIndex = matcher.start();
/*     */             }
/*     */           } else {
/* 409 */             stopIndex = -1;
/*     */           } 
/*     */         } 
/*     */         
/* 413 */         return (stopIndex == -1) ? new SimpleScalar(this.s) : new SimpleScalar(this.s.substring(0, stopIndex));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
/* 419 */       return (TemplateModel)new KeepUntilMethod(s);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class lengthBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 428 */       return (TemplateModel)new SimpleNumber(s.length());
/*     */     }
/*     */   }
/*     */   
/*     */   static class lower_caseBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 436 */       return (TemplateModel)new SimpleScalar(s.toLowerCase(env.getLocale()));
/*     */     }
/*     */   }
/*     */   
/*     */   static class padBI extends BuiltInForString {
/*     */     private final boolean leftPadder;
/*     */     
/*     */     private class BIMethod implements TemplateMethodModelEx {
/*     */       private final String s;
/*     */       
/*     */       private BIMethod(String s) {
/* 447 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 452 */         int argCnt = args.size();
/* 453 */         BuiltInsForStringsBasic.padBI.this.checkMethodArgCount(argCnt, 1, 2);
/*     */         
/* 455 */         int width = BuiltInsForStringsBasic.padBI.this.getNumberMethodArg(args, 0).intValue();
/*     */         
/* 457 */         if (argCnt > 1) {
/* 458 */           String filling = BuiltInsForStringsBasic.padBI.this.getStringMethodArg(args, 1);
/*     */           try {
/* 460 */             return new SimpleScalar(
/* 461 */                 BuiltInsForStringsBasic.padBI.this.leftPadder ? 
/* 462 */                 StringUtil.leftPad(this.s, width, filling) : 
/* 463 */                 StringUtil.rightPad(this.s, width, filling));
/* 464 */           } catch (IllegalArgumentException e) {
/* 465 */             if (filling.length() == 0) {
/* 466 */               throw new _TemplateModelException(new Object[] { "?", this.this$0.key, "(...) argument #2 can't be a 0-length string." });
/*     */             }
/*     */             
/* 469 */             throw new _TemplateModelException(e, new Object[] { "?", this.this$0.key, "(...) failed: ", e });
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 474 */         return new SimpleScalar(BuiltInsForStringsBasic.padBI.this.leftPadder ? StringUtil.leftPad(this.s, width) : StringUtil.rightPad(this.s, width));
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     padBI(boolean leftPadder) {
/* 482 */       this.leftPadder = leftPadder;
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 487 */       return (TemplateModel)new BIMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class remove_beginningBI
/*     */     extends BuiltInForString {
/*     */     private class BIMethod implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       private BIMethod(String s) {
/* 497 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 502 */         BuiltInsForStringsBasic.remove_beginningBI.this.checkMethodArgCount(args, 1);
/* 503 */         String prefix = BuiltInsForStringsBasic.remove_beginningBI.this.getStringMethodArg(args, 0);
/* 504 */         return new SimpleScalar(this.s.startsWith(prefix) ? this.s.substring(prefix.length()) : this.s);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 510 */       return (TemplateModel)new BIMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class remove_endingBI
/*     */     extends BuiltInForString {
/*     */     private class BIMethod implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       private BIMethod(String s) {
/* 520 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 525 */         BuiltInsForStringsBasic.remove_endingBI.this.checkMethodArgCount(args, 1);
/* 526 */         String suffix = BuiltInsForStringsBasic.remove_endingBI.this.getStringMethodArg(args, 0);
/* 527 */         return new SimpleScalar(this.s.endsWith(suffix) ? this.s.substring(0, this.s.length() - suffix.length()) : this.s);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 533 */       return (TemplateModel)new BIMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class split_BI extends BuiltInForString {
/*     */     class SplitMethod implements TemplateMethodModel {
/*     */       private String s;
/*     */       
/*     */       SplitMethod(String s) {
/* 542 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List<String> args) throws TemplateModelException {
/* 547 */         int argCnt = args.size();
/* 548 */         BuiltInsForStringsBasic.split_BI.this.checkMethodArgCount(argCnt, 1, 2);
/* 549 */         String splitString = args.get(0);
/* 550 */         long flags = (argCnt > 1) ? RegexpHelper.parseFlagString(args.get(1)) : 0L;
/* 551 */         String[] result = null;
/* 552 */         if ((flags & 0x100000000L) == 0L) {
/* 553 */           RegexpHelper.checkNonRegexpFlags(BuiltInsForStringsBasic.split_BI.this.key, flags);
/* 554 */           result = StringUtil.split(this.s, splitString, ((flags & RegexpHelper.RE_FLAG_CASE_INSENSITIVE) != 0L));
/*     */         } else {
/*     */           
/* 557 */           Pattern pattern = RegexpHelper.getPattern(splitString, (int)flags);
/* 558 */           result = pattern.split(this.s);
/*     */         } 
/* 560 */         return ObjectWrapper.DEFAULT_WRAPPER.wrap(result);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateModelException {
/* 566 */       return (TemplateModel)new SplitMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class starts_withBI
/*     */     extends BuiltInForString {
/*     */     private class BIMethod
/*     */       implements TemplateMethodModelEx {
/*     */       private String s;
/*     */       
/*     */       private BIMethod(String s) {
/* 577 */         this.s = s;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 582 */         BuiltInsForStringsBasic.starts_withBI.this.checkMethodArgCount(args, 1);
/* 583 */         return this.s.startsWith(BuiltInsForStringsBasic.starts_withBI.this.getStringMethodArg(args, 0)) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(String s, Environment env) throws TemplateException {
/* 590 */       return (TemplateModel)new BIMethod(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class substringBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(final String s, Environment env) throws TemplateException {
/* 598 */       return (TemplateModel)new TemplateMethodModelEx()
/*     */         {
/*     */           public Object exec(List args) throws TemplateModelException
/*     */           {
/* 602 */             int argCount = args.size();
/* 603 */             BuiltInsForStringsBasic.substringBI.this.checkMethodArgCount(argCount, 1, 2);
/*     */             
/* 605 */             int beginIdx = BuiltInsForStringsBasic.substringBI.this.getNumberMethodArg(args, 0).intValue();
/*     */             
/* 607 */             int len = s.length();
/*     */             
/* 609 */             if (beginIdx < 0)
/* 610 */               throw newIndexLessThan0Exception(0, beginIdx); 
/* 611 */             if (beginIdx > len) {
/* 612 */               throw newIndexGreaterThanLengthException(0, beginIdx, len);
/*     */             }
/*     */             
/* 615 */             if (argCount > 1) {
/* 616 */               int endIdx = BuiltInsForStringsBasic.substringBI.this.getNumberMethodArg(args, 1).intValue();
/* 617 */               if (endIdx < 0)
/* 618 */                 throw newIndexLessThan0Exception(1, endIdx); 
/* 619 */               if (endIdx > len) {
/* 620 */                 throw newIndexGreaterThanLengthException(1, endIdx, len);
/*     */               }
/* 622 */               if (beginIdx > endIdx) {
/* 623 */                 throw _MessageUtil.newMethodArgsInvalidValueException("?" + BuiltInsForStringsBasic.substringBI.this.key, new Object[] { "The begin index argument, ", 
/* 624 */                       Integer.valueOf(beginIdx), ", shouldn't be greater than the end index argument, ", 
/*     */                       
/* 626 */                       Integer.valueOf(endIdx), "." });
/*     */               }
/* 628 */               return new SimpleScalar(s.substring(beginIdx, endIdx));
/*     */             } 
/* 630 */             return new SimpleScalar(s.substring(beginIdx));
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           private TemplateModelException newIndexGreaterThanLengthException(int argIdx, int idx, int len) throws TemplateModelException {
/* 636 */             return _MessageUtil.newMethodArgInvalidValueException("?" + BuiltInsForStringsBasic.substringBI.this.key, argIdx, new Object[] { "The index mustn't be greater than the length of the string, ", 
/*     */ 
/*     */                   
/* 639 */                   Integer.valueOf(len), ", but it was ", 
/* 640 */                   Integer.valueOf(idx), "." });
/*     */           }
/*     */ 
/*     */           
/*     */           private TemplateModelException newIndexLessThan0Exception(int argIdx, int idx) throws TemplateModelException {
/* 645 */             return _MessageUtil.newMethodArgInvalidValueException("?" + BuiltInsForStringsBasic.substringBI.this.key, argIdx, new Object[] { "The index must be at least 0, but was ", 
/*     */                   
/* 647 */                   Integer.valueOf(idx), "." });
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */   
/*     */   static class trimBI
/*     */     extends BuiltInForString
/*     */   {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 657 */       return (TemplateModel)new SimpleScalar(s.trim());
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract class AbstractTruncateBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(final String s, final Environment env) {
/* 664 */       return (TemplateModel)new TemplateMethodModelEx() { public Object exec(List<TemplateModel> args) throws TemplateModelException {
/*     */             TemplateModel terminator;
/*     */             Integer terminatorLength;
/* 667 */             int argCount = args.size();
/* 668 */             BuiltInsForStringsBasic.AbstractTruncateBI.this.checkMethodArgCount(argCount, 1, 3);
/*     */             
/* 670 */             int maxLength = BuiltInsForStringsBasic.AbstractTruncateBI.this.getNumberMethodArg(args, 0).intValue();
/* 671 */             if (maxLength < 0) {
/* 672 */               throw new _TemplateModelException(new Object[] { "?", this.this$0.key, "(...) argument #1 can't be negative." });
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 677 */             if (argCount > 1) {
/* 678 */               terminator = args.get(1);
/* 679 */               if (!(terminator instanceof TemplateScalarModel)) {
/* 680 */                 if (BuiltInsForStringsBasic.AbstractTruncateBI.this.allowMarkupTerminator()) {
/* 681 */                   if (!(terminator instanceof TemplateMarkupOutputModel)) {
/* 682 */                     throw _MessageUtil.newMethodArgMustBeStringOrMarkupOutputException("?" + BuiltInsForStringsBasic.AbstractTruncateBI.this.key, 1, terminator);
/*     */                   }
/*     */                 } else {
/*     */                   
/* 686 */                   throw _MessageUtil.newMethodArgMustBeStringException("?" + BuiltInsForStringsBasic.AbstractTruncateBI.this.key, 1, terminator);
/*     */                 } 
/*     */               }
/*     */ 
/*     */               
/* 691 */               Number terminatorLengthNum = BuiltInsForStringsBasic.AbstractTruncateBI.this.getOptNumberMethodArg(args, 2);
/* 692 */               terminatorLength = (terminatorLengthNum != null) ? Integer.valueOf(terminatorLengthNum.intValue()) : null;
/* 693 */               if (terminatorLength != null && terminatorLength.intValue() < 0) {
/* 694 */                 throw new _TemplateModelException(new Object[] { "?", this.this$0.key, "(...) argument #3 can't be negative." });
/*     */               }
/*     */             } else {
/* 697 */               terminator = null;
/* 698 */               terminatorLength = null;
/*     */             } 
/*     */             try {
/* 701 */               TruncateBuiltinAlgorithm algorithm = env.getTruncateBuiltinAlgorithm();
/* 702 */               return BuiltInsForStringsBasic.AbstractTruncateBI.this.truncate(algorithm, s, maxLength, terminator, terminatorLength, env);
/* 703 */             } catch (TemplateException e) {
/* 704 */               throw new _TemplateModelException(BuiltInsForStringsBasic.AbstractTruncateBI.this, e, env, "Truncation failed; see cause exception");
/*     */             } 
/*     */           } }
/*     */         ;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected abstract TemplateModel truncate(TruncateBuiltinAlgorithm param1TruncateBuiltinAlgorithm, String param1String, int param1Int, TemplateModel param1TemplateModel, Integer param1Integer, Environment param1Environment) throws TemplateException;
/*     */ 
/*     */ 
/*     */     
/*     */     protected abstract boolean allowMarkupTerminator();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class truncateBI
/*     */     extends AbstractTruncateBI
/*     */   {
/*     */     protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 725 */       return (TemplateModel)algorithm.truncate(s, maxLength, (TemplateScalarModel)terminator, terminatorLength, env);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean allowMarkupTerminator() {
/* 730 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class truncate_wBI
/*     */     extends AbstractTruncateBI
/*     */   {
/*     */     protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 740 */       return (TemplateModel)algorithm.truncateW(s, maxLength, (TemplateScalarModel)terminator, terminatorLength, env);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean allowMarkupTerminator() {
/* 745 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class truncate_cBI
/*     */     extends AbstractTruncateBI
/*     */   {
/*     */     protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 755 */       return (TemplateModel)algorithm.truncateC(s, maxLength, (TemplateScalarModel)terminator, terminatorLength, env);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean allowMarkupTerminator() {
/* 760 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class truncate_mBI
/*     */     extends AbstractTruncateBI
/*     */   {
/*     */     protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 770 */       return algorithm.truncateM(s, maxLength, terminator, terminatorLength, env);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean allowMarkupTerminator() {
/* 775 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class truncate_w_mBI
/*     */     extends AbstractTruncateBI
/*     */   {
/*     */     protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 785 */       return algorithm.truncateWM(s, maxLength, terminator, terminatorLength, env);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean allowMarkupTerminator() {
/* 790 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class truncate_c_mBI
/*     */     extends AbstractTruncateBI
/*     */   {
/*     */     protected TemplateModel truncate(TruncateBuiltinAlgorithm algorithm, String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
/* 800 */       return algorithm.truncateCM(s, maxLength, terminator, terminatorLength, env);
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean allowMarkupTerminator() {
/* 805 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   static class uncap_firstBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 812 */       int i = 0;
/* 813 */       int ln = s.length();
/* 814 */       while (i < ln && Character.isWhitespace(s.charAt(i))) {
/* 815 */         i++;
/*     */       }
/* 817 */       if (i < ln) {
/* 818 */         StringBuilder b = new StringBuilder(s);
/* 819 */         b.setCharAt(i, Character.toLowerCase(s.charAt(i)));
/* 820 */         s = b.toString();
/*     */       } 
/* 822 */       return (TemplateModel)new SimpleScalar(s);
/*     */     }
/*     */   }
/*     */   
/*     */   static class upper_caseBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 829 */       return (TemplateModel)new SimpleScalar(s.toUpperCase(env.getLocale()));
/*     */     }
/*     */   }
/*     */   
/*     */   static class word_listBI
/*     */     extends BuiltInForString {
/*     */     TemplateModel calculateResult(String s, Environment env) {
/* 836 */       SimpleSequence result = new SimpleSequence((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 837 */       StringTokenizer st = new StringTokenizer(s);
/* 838 */       while (st.hasMoreTokens()) {
/* 839 */         result.add(st.nextToken());
/*     */       }
/* 841 */       return (TemplateModel)result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForStringsBasic.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */