/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ abstract class BuiltIn
/*     */   extends Expression
/*     */   implements Cloneable
/*     */ {
/*     */   protected Expression target;
/*     */   protected String key;
/*  86 */   static final Set<String> CAMEL_CASE_NAMES = new TreeSet<>();
/*  87 */   static final Set<String> SNAKE_CASE_NAMES = new TreeSet<>();
/*     */   static final int NUMBER_OF_BIS = 291;
/*  89 */   static final HashMap<String, BuiltIn> BUILT_INS_BY_NAME = new HashMap<>(437, 1.0F);
/*     */   
/*     */   static final String BI_NAME_SNAKE_CASE_WITH_ARGS = "with_args";
/*     */   
/*     */   static final String BI_NAME_CAMEL_CASE_WITH_ARGS = "withArgs";
/*     */   
/*     */   static final String BI_NAME_SNAKE_CASE_WITH_ARGS_LAST = "with_args_last";
/*     */   static final String BI_NAME_CAMEL_CASE_WITH_ARGS_LAST = "withArgsLast";
/*     */   
/*     */   static {
/*  99 */     putBI("abs", new BuiltInsForNumbers.absBI());
/* 100 */     putBI("absolute_template_name", "absoluteTemplateName", new BuiltInsForStringsMisc.absolute_template_nameBI());
/* 101 */     putBI("ancestors", new BuiltInsForNodes.ancestorsBI());
/* 102 */     putBI("api", new BuiltInsForMultipleTypes.apiBI());
/* 103 */     putBI("boolean", new BuiltInsForStringsMisc.booleanBI());
/* 104 */     putBI("byte", new BuiltInsForNumbers.byteBI());
/* 105 */     putBI("c", new BuiltInsForMultipleTypes.cBI());
/* 106 */     putBI("cap_first", "capFirst", new BuiltInsForStringsBasic.cap_firstBI());
/* 107 */     putBI("capitalize", new BuiltInsForStringsBasic.capitalizeBI());
/* 108 */     putBI("ceiling", new BuiltInsForNumbers.ceilingBI());
/* 109 */     putBI("children", new BuiltInsForNodes.childrenBI());
/* 110 */     putBI("chop_linebreak", "chopLinebreak", new BuiltInsForStringsBasic.chop_linebreakBI());
/* 111 */     putBI("contains", new BuiltInsForStringsBasic.containsBI());
/* 112 */     putBI("date", new BuiltInsForMultipleTypes.dateBI(2));
/* 113 */     putBI("date_if_unknown", "dateIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(2));
/* 114 */     putBI("datetime", new BuiltInsForMultipleTypes.dateBI(3));
/* 115 */     putBI("datetime_if_unknown", "datetimeIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(3));
/* 116 */     putBI("default", new BuiltInsForExistenceHandling.defaultBI());
/* 117 */     putBI("double", new BuiltInsForNumbers.doubleBI());
/* 118 */     putBI("drop_while", "dropWhile", new BuiltInsForSequences.drop_whileBI());
/* 119 */     putBI("ends_with", "endsWith", new BuiltInsForStringsBasic.ends_withBI());
/* 120 */     putBI("ensure_ends_with", "ensureEndsWith", new BuiltInsForStringsBasic.ensure_ends_withBI());
/* 121 */     putBI("ensure_starts_with", "ensureStartsWith", new BuiltInsForStringsBasic.ensure_starts_withBI());
/* 122 */     putBI("esc", new BuiltInsForOutputFormatRelated.escBI());
/* 123 */     putBI("eval", new BuiltInsForStringsMisc.evalBI());
/* 124 */     putBI("eval_json", "evalJson", new BuiltInsForStringsMisc.evalJsonBI());
/* 125 */     putBI("exists", new BuiltInsForExistenceHandling.existsBI());
/* 126 */     putBI("filter", new BuiltInsForSequences.filterBI());
/* 127 */     putBI("first", new BuiltInsForSequences.firstBI());
/* 128 */     putBI("float", new BuiltInsForNumbers.floatBI());
/* 129 */     putBI("floor", new BuiltInsForNumbers.floorBI());
/* 130 */     putBI("chunk", new BuiltInsForSequences.chunkBI());
/* 131 */     putBI("counter", new BuiltInsForLoopVariables.counterBI());
/* 132 */     putBI("item_cycle", "itemCycle", new BuiltInsForLoopVariables.item_cycleBI());
/* 133 */     putBI("has_api", "hasApi", new BuiltInsForMultipleTypes.has_apiBI());
/* 134 */     putBI("has_content", "hasContent", new BuiltInsForExistenceHandling.has_contentBI());
/* 135 */     putBI("has_next", "hasNext", new BuiltInsForLoopVariables.has_nextBI());
/* 136 */     putBI("html", new BuiltInsForStringsEncoding.htmlBI());
/* 137 */     putBI("if_exists", "ifExists", new BuiltInsForExistenceHandling.if_existsBI());
/* 138 */     putBI("index", new BuiltInsForLoopVariables.indexBI());
/* 139 */     putBI("index_of", "indexOf", new BuiltInsForStringsBasic.index_ofBI(false));
/* 140 */     putBI("int", new BuiltInsForNumbers.intBI());
/* 141 */     putBI("interpret", new Interpret());
/* 142 */     putBI("is_boolean", "isBoolean", new BuiltInsForMultipleTypes.is_booleanBI());
/* 143 */     putBI("is_collection", "isCollection", new BuiltInsForMultipleTypes.is_collectionBI());
/* 144 */     putBI("is_collection_ex", "isCollectionEx", new BuiltInsForMultipleTypes.is_collection_exBI());
/* 145 */     BuiltInsForMultipleTypes.is_dateLikeBI bi = new BuiltInsForMultipleTypes.is_dateLikeBI();
/* 146 */     putBI("is_date", "isDate", bi);
/* 147 */     putBI("is_date_like", "isDateLike", bi);
/* 148 */     putBI("is_date_only", "isDateOnly", new BuiltInsForMultipleTypes.is_dateOfTypeBI(2));
/* 149 */     putBI("is_even_item", "isEvenItem", new BuiltInsForLoopVariables.is_even_itemBI());
/* 150 */     putBI("is_first", "isFirst", new BuiltInsForLoopVariables.is_firstBI());
/* 151 */     putBI("is_last", "isLast", new BuiltInsForLoopVariables.is_lastBI());
/* 152 */     putBI("is_unknown_date_like", "isUnknownDateLike", new BuiltInsForMultipleTypes.is_dateOfTypeBI(0));
/* 153 */     putBI("is_datetime", "isDatetime", new BuiltInsForMultipleTypes.is_dateOfTypeBI(3));
/* 154 */     putBI("is_directive", "isDirective", new BuiltInsForMultipleTypes.is_directiveBI());
/* 155 */     putBI("is_enumerable", "isEnumerable", new BuiltInsForMultipleTypes.is_enumerableBI());
/* 156 */     putBI("is_hash_ex", "isHashEx", new BuiltInsForMultipleTypes.is_hash_exBI());
/* 157 */     putBI("is_hash", "isHash", new BuiltInsForMultipleTypes.is_hashBI());
/* 158 */     putBI("is_infinite", "isInfinite", new BuiltInsForNumbers.is_infiniteBI());
/* 159 */     putBI("is_indexable", "isIndexable", new BuiltInsForMultipleTypes.is_indexableBI());
/* 160 */     putBI("is_macro", "isMacro", new BuiltInsForMultipleTypes.is_macroBI());
/* 161 */     putBI("is_markup_output", "isMarkupOutput", new BuiltInsForMultipleTypes.is_markup_outputBI());
/* 162 */     putBI("is_method", "isMethod", new BuiltInsForMultipleTypes.is_methodBI());
/* 163 */     putBI("is_nan", "isNan", new BuiltInsForNumbers.is_nanBI());
/* 164 */     putBI("is_node", "isNode", new BuiltInsForMultipleTypes.is_nodeBI());
/* 165 */     putBI("is_number", "isNumber", new BuiltInsForMultipleTypes.is_numberBI());
/* 166 */     putBI("is_odd_item", "isOddItem", new BuiltInsForLoopVariables.is_odd_itemBI());
/* 167 */     putBI("is_sequence", "isSequence", new BuiltInsForMultipleTypes.is_sequenceBI());
/* 168 */     putBI("is_string", "isString", new BuiltInsForMultipleTypes.is_stringBI());
/* 169 */     putBI("is_time", "isTime", new BuiltInsForMultipleTypes.is_dateOfTypeBI(1));
/* 170 */     putBI("is_transform", "isTransform", new BuiltInsForMultipleTypes.is_transformBI());
/*     */     
/* 172 */     putBI("iso_utc", "isoUtc", new BuiltInsForDates.iso_utc_or_local_BI(null, 6, true));
/*     */     
/* 174 */     putBI("iso_utc_fz", "isoUtcFZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.TRUE, 6, true));
/*     */     
/* 176 */     putBI("iso_utc_nz", "isoUtcNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 6, true));
/*     */ 
/*     */     
/* 179 */     putBI("iso_utc_ms", "isoUtcMs", new BuiltInsForDates.iso_utc_or_local_BI(null, 7, true));
/*     */     
/* 181 */     putBI("iso_utc_ms_nz", "isoUtcMsNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 7, true));
/*     */ 
/*     */     
/* 184 */     putBI("iso_utc_m", "isoUtcM", new BuiltInsForDates.iso_utc_or_local_BI(null, 5, true));
/*     */     
/* 186 */     putBI("iso_utc_m_nz", "isoUtcMNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 5, true));
/*     */ 
/*     */     
/* 189 */     putBI("iso_utc_h", "isoUtcH", new BuiltInsForDates.iso_utc_or_local_BI(null, 4, true));
/*     */     
/* 191 */     putBI("iso_utc_h_nz", "isoUtcHNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 4, true));
/*     */ 
/*     */     
/* 194 */     putBI("iso_local", "isoLocal", new BuiltInsForDates.iso_utc_or_local_BI(null, 6, false));
/*     */     
/* 196 */     putBI("iso_local_nz", "isoLocalNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 6, false));
/*     */ 
/*     */     
/* 199 */     putBI("iso_local_ms", "isoLocalMs", new BuiltInsForDates.iso_utc_or_local_BI(null, 7, false));
/*     */     
/* 201 */     putBI("iso_local_ms_nz", "isoLocalMsNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 7, false));
/*     */ 
/*     */     
/* 204 */     putBI("iso_local_m", "isoLocalM", new BuiltInsForDates.iso_utc_or_local_BI(null, 5, false));
/*     */     
/* 206 */     putBI("iso_local_m_nz", "isoLocalMNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 5, false));
/*     */ 
/*     */     
/* 209 */     putBI("iso_local_h", "isoLocalH", new BuiltInsForDates.iso_utc_or_local_BI(null, 4, false));
/*     */     
/* 211 */     putBI("iso_local_h_nz", "isoLocalHNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 4, false));
/*     */ 
/*     */     
/* 214 */     putBI("iso", new BuiltInsForDates.iso_BI(null, 6));
/*     */     
/* 216 */     putBI("iso_nz", "isoNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 6));
/*     */ 
/*     */     
/* 219 */     putBI("iso_ms", "isoMs", new BuiltInsForDates.iso_BI(null, 7));
/*     */     
/* 221 */     putBI("iso_ms_nz", "isoMsNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 7));
/*     */ 
/*     */     
/* 224 */     putBI("iso_m", "isoM", new BuiltInsForDates.iso_BI(null, 5));
/*     */     
/* 226 */     putBI("iso_m_nz", "isoMNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 5));
/*     */ 
/*     */     
/* 229 */     putBI("iso_h", "isoH", new BuiltInsForDates.iso_BI(null, 4));
/*     */     
/* 231 */     putBI("iso_h_nz", "isoHNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 4));
/*     */ 
/*     */     
/* 234 */     putBI("j_string", "jString", new BuiltInsForStringsEncoding.j_stringBI());
/* 235 */     putBI("join", new BuiltInsForSequences.joinBI());
/* 236 */     putBI("js_string", "jsString", new BuiltInsForStringsEncoding.js_stringBI());
/* 237 */     putBI("json_string", "jsonString", new BuiltInsForStringsEncoding.json_stringBI());
/* 238 */     putBI("keep_after", "keepAfter", new BuiltInsForStringsBasic.keep_afterBI());
/* 239 */     putBI("keep_before", "keepBefore", new BuiltInsForStringsBasic.keep_beforeBI());
/* 240 */     putBI("keep_after_last", "keepAfterLast", new BuiltInsForStringsBasic.keep_after_lastBI());
/* 241 */     putBI("keep_before_last", "keepBeforeLast", new BuiltInsForStringsBasic.keep_before_lastBI());
/* 242 */     putBI("keys", new BuiltInsForHashes.keysBI());
/* 243 */     putBI("last_index_of", "lastIndexOf", new BuiltInsForStringsBasic.index_ofBI(true));
/* 244 */     putBI("last", new BuiltInsForSequences.lastBI());
/* 245 */     putBI("left_pad", "leftPad", new BuiltInsForStringsBasic.padBI(true));
/* 246 */     putBI("length", new BuiltInsForStringsBasic.lengthBI());
/* 247 */     putBI("long", new BuiltInsForNumbers.longBI());
/* 248 */     putBI("lower_abc", "lowerAbc", new BuiltInsForNumbers.lower_abcBI());
/* 249 */     putBI("lower_case", "lowerCase", new BuiltInsForStringsBasic.lower_caseBI());
/* 250 */     putBI("map", new BuiltInsForSequences.mapBI());
/* 251 */     putBI("namespace", new BuiltInsForMultipleTypes.namespaceBI());
/* 252 */     putBI("new", new NewBI());
/* 253 */     putBI("markup_string", "markupString", new BuiltInsForMarkupOutputs.markup_stringBI());
/* 254 */     putBI("node_name", "nodeName", new BuiltInsForNodes.node_nameBI());
/* 255 */     putBI("node_namespace", "nodeNamespace", new BuiltInsForNodes.node_namespaceBI());
/* 256 */     putBI("node_type", "nodeType", new BuiltInsForNodes.node_typeBI());
/* 257 */     putBI("no_esc", "noEsc", new BuiltInsForOutputFormatRelated.no_escBI());
/* 258 */     putBI("max", new BuiltInsForSequences.maxBI());
/* 259 */     putBI("min", new BuiltInsForSequences.minBI());
/* 260 */     putBI("number", new BuiltInsForStringsMisc.numberBI());
/* 261 */     putBI("number_to_date", "numberToDate", new BuiltInsForNumbers.number_to_dateBI(2));
/* 262 */     putBI("number_to_time", "numberToTime", new BuiltInsForNumbers.number_to_dateBI(1));
/* 263 */     putBI("number_to_datetime", "numberToDatetime", new BuiltInsForNumbers.number_to_dateBI(3));
/* 264 */     putBI("parent", new BuiltInsForNodes.parentBI());
/* 265 */     putBI("previous_sibling", "previousSibling", new BuiltInsForNodes.previousSiblingBI());
/* 266 */     putBI("next_sibling", "nextSibling", new BuiltInsForNodes.nextSiblingBI());
/* 267 */     putBI("item_parity", "itemParity", new BuiltInsForLoopVariables.item_parityBI());
/* 268 */     putBI("item_parity_cap", "itemParityCap", new BuiltInsForLoopVariables.item_parity_capBI());
/* 269 */     putBI("reverse", new BuiltInsForSequences.reverseBI());
/* 270 */     putBI("right_pad", "rightPad", new BuiltInsForStringsBasic.padBI(false));
/* 271 */     putBI("root", new BuiltInsForNodes.rootBI());
/* 272 */     putBI("round", new BuiltInsForNumbers.roundBI());
/* 273 */     putBI("remove_ending", "removeEnding", new BuiltInsForStringsBasic.remove_endingBI());
/* 274 */     putBI("remove_beginning", "removeBeginning", new BuiltInsForStringsBasic.remove_beginningBI());
/* 275 */     putBI("rtf", new BuiltInsForStringsEncoding.rtfBI());
/* 276 */     putBI("seq_contains", "seqContains", new BuiltInsForSequences.seq_containsBI());
/* 277 */     putBI("seq_index_of", "seqIndexOf", new BuiltInsForSequences.seq_index_ofBI(true));
/* 278 */     putBI("seq_last_index_of", "seqLastIndexOf", new BuiltInsForSequences.seq_index_ofBI(false));
/* 279 */     putBI("sequence", new BuiltInsForSequences.sequenceBI());
/* 280 */     putBI("short", new BuiltInsForNumbers.shortBI());
/* 281 */     putBI("size", new BuiltInsForMultipleTypes.sizeBI());
/* 282 */     putBI("sort_by", "sortBy", new BuiltInsForSequences.sort_byBI());
/* 283 */     putBI("sort", new BuiltInsForSequences.sortBI());
/* 284 */     putBI("split", new BuiltInsForStringsBasic.split_BI());
/* 285 */     putBI("switch", new BuiltInsWithLazyConditionals.switch_BI());
/* 286 */     putBI("starts_with", "startsWith", new BuiltInsForStringsBasic.starts_withBI());
/* 287 */     putBI("string", new BuiltInsForMultipleTypes.stringBI());
/* 288 */     putBI("substring", new BuiltInsForStringsBasic.substringBI());
/* 289 */     putBI("take_while", "takeWhile", new BuiltInsForSequences.take_whileBI());
/* 290 */     putBI("then", new BuiltInsWithLazyConditionals.then_BI());
/* 291 */     putBI("time", new BuiltInsForMultipleTypes.dateBI(1));
/* 292 */     putBI("time_if_unknown", "timeIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(1));
/* 293 */     putBI("trim", new BuiltInsForStringsBasic.trimBI());
/* 294 */     putBI("truncate", new BuiltInsForStringsBasic.truncateBI());
/* 295 */     putBI("truncate_w", "truncateW", new BuiltInsForStringsBasic.truncate_wBI());
/* 296 */     putBI("truncate_c", "truncateC", new BuiltInsForStringsBasic.truncate_cBI());
/* 297 */     putBI("truncate_m", "truncateM", new BuiltInsForStringsBasic.truncate_mBI());
/* 298 */     putBI("truncate_w_m", "truncateWM", new BuiltInsForStringsBasic.truncate_w_mBI());
/* 299 */     putBI("truncate_c_m", "truncateCM", new BuiltInsForStringsBasic.truncate_c_mBI());
/* 300 */     putBI("uncap_first", "uncapFirst", new BuiltInsForStringsBasic.uncap_firstBI());
/* 301 */     putBI("upper_abc", "upperAbc", new BuiltInsForNumbers.upper_abcBI());
/* 302 */     putBI("upper_case", "upperCase", new BuiltInsForStringsBasic.upper_caseBI());
/* 303 */     putBI("url", new BuiltInsForStringsEncoding.urlBI());
/* 304 */     putBI("url_path", "urlPath", new BuiltInsForStringsEncoding.urlPathBI());
/* 305 */     putBI("values", new BuiltInsForHashes.valuesBI());
/* 306 */     putBI("web_safe", "webSafe", BUILT_INS_BY_NAME.get("html"));
/* 307 */     putBI("with_args", "withArgs", new BuiltInsForCallables.with_argsBI());
/*     */     
/* 309 */     putBI("with_args_last", "withArgsLast", new BuiltInsForCallables.with_args_lastBI());
/*     */     
/* 311 */     putBI("word_list", "wordList", new BuiltInsForStringsBasic.word_listBI());
/* 312 */     putBI("xhtml", new BuiltInsForStringsEncoding.xhtmlBI());
/* 313 */     putBI("xml", new BuiltInsForStringsEncoding.xmlBI());
/* 314 */     putBI("matches", new BuiltInsForStringsRegexp.matchesBI());
/* 315 */     putBI("groups", new BuiltInsForStringsRegexp.groupsBI());
/* 316 */     putBI("replace", new BuiltInsForStringsRegexp.replace_reBI());
/*     */ 
/*     */     
/* 319 */     if (291 < BUILT_INS_BY_NAME.size()) {
/* 320 */       throw new AssertionError("Update NUMBER_OF_BIS! Should be: " + BUILT_INS_BY_NAME.size());
/*     */     }
/*     */   }
/*     */   
/*     */   private static void putBI(String name, BuiltIn bi) {
/* 325 */     BUILT_INS_BY_NAME.put(name, bi);
/* 326 */     SNAKE_CASE_NAMES.add(name);
/* 327 */     CAMEL_CASE_NAMES.add(name);
/*     */   }
/*     */   
/*     */   private static void putBI(String nameSnakeCase, String nameCamelCase, BuiltIn bi) {
/* 331 */     BUILT_INS_BY_NAME.put(nameSnakeCase, bi);
/* 332 */     BUILT_INS_BY_NAME.put(nameCamelCase, bi);
/* 333 */     SNAKE_CASE_NAMES.add(nameSnakeCase);
/* 334 */     CAMEL_CASE_NAMES.add(nameCamelCase);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static BuiltIn newBuiltIn(int incompatibleImprovements, Expression target, Token keyTk, FMParserTokenManager tokenManager) throws ParseException {
/* 345 */     String key = keyTk.image;
/* 346 */     BuiltIn bi = BUILT_INS_BY_NAME.get(key);
/* 347 */     if (bi == null) {
/* 348 */       StringBuilder buf = (new StringBuilder("Unknown built-in: ")).append(StringUtil.jQuote(key)).append(". ");
/*     */       
/* 350 */       buf.append("Help (latest version): https://freemarker.apache.org/docs/ref_builtins.html; you're using FreeMarker ")
/*     */         
/* 352 */         .append(Configuration.getVersion()).append(".\nThe alphabetical list of built-ins:");
/*     */       
/* 354 */       List<Comparable> names = new ArrayList(BUILT_INS_BY_NAME.keySet().size());
/* 355 */       names.addAll(BUILT_INS_BY_NAME.keySet());
/* 356 */       Collections.sort(names);
/* 357 */       char lastLetter = Character.MIN_VALUE;
/*     */ 
/*     */ 
/*     */       
/* 361 */       int namingConvention = tokenManager.namingConvention;
/* 362 */       int shownNamingConvention = (namingConvention != 10) ? namingConvention : 11;
/*     */ 
/*     */ 
/*     */       
/* 366 */       boolean first = true;
/* 367 */       for (Iterator<Comparable> it = names.iterator(); it.hasNext(); ) {
/* 368 */         String correctName = (String)it.next();
/* 369 */         int correctNameNamingConvetion = _CoreStringUtils.getIdentifierNamingConvention(correctName);
/* 370 */         if ((shownNamingConvention == 12) ? (correctNameNamingConvetion != 11) : (correctNameNamingConvetion != 12)) {
/*     */ 
/*     */           
/* 373 */           if (first) {
/* 374 */             first = false;
/*     */           } else {
/* 376 */             buf.append(", ");
/*     */           } 
/*     */           
/* 379 */           char firstChar = correctName.charAt(0);
/* 380 */           if (firstChar != lastLetter) {
/* 381 */             lastLetter = firstChar;
/* 382 */             buf.append('\n');
/*     */           } 
/* 384 */           buf.append(correctName);
/*     */         } 
/*     */       } 
/*     */       
/* 388 */       throw new ParseException(buf.toString(), null, keyTk);
/*     */     } 
/*     */     
/* 391 */     while (bi instanceof ICIChainMember && incompatibleImprovements < ((ICIChainMember)bi)
/* 392 */       .getMinimumICIVersion()) {
/* 393 */       bi = (BuiltIn)((ICIChainMember)bi).getPreviousICIChainMember();
/*     */     }
/*     */     
/*     */     try {
/* 397 */       bi = (BuiltIn)bi.clone();
/* 398 */     } catch (CloneNotSupportedException e) {
/* 399 */       throw new InternalError();
/*     */     } 
/* 401 */     bi.key = key;
/* 402 */     bi.setTarget(target);
/* 403 */     return bi;
/*     */   }
/*     */   
/*     */   protected void setTarget(Expression target) {
/* 407 */     this.target = target;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/* 412 */     return this.target.getCanonicalForm() + "?" + this.key;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 417 */     return "?" + this.key;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/* 422 */     return false;
/*     */   }
/*     */   
/*     */   protected final void checkMethodArgCount(List args, int expectedCnt) throws TemplateModelException {
/* 426 */     checkMethodArgCount(args.size(), expectedCnt);
/*     */   }
/*     */   
/*     */   protected final void checkMethodArgCount(int argCnt, int expectedCnt) throws TemplateModelException {
/* 430 */     if (argCnt != expectedCnt) {
/* 431 */       throw _MessageUtil.newArgCntError("?" + this.key, argCnt, expectedCnt);
/*     */     }
/*     */   }
/*     */   
/*     */   protected final void checkMethodArgCount(List args, int minCnt, int maxCnt) throws TemplateModelException {
/* 436 */     checkMethodArgCount(args.size(), minCnt, maxCnt);
/*     */   }
/*     */   
/*     */   protected final void checkMethodArgCount(int argCnt, int minCnt, int maxCnt) throws TemplateModelException {
/* 440 */     if (argCnt < minCnt || argCnt > maxCnt) {
/* 441 */       throw _MessageUtil.newArgCntError("?" + this.key, argCnt, minCnt, maxCnt);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String getOptStringMethodArg(List args, int argIdx) throws TemplateModelException {
/* 451 */     return (args.size() > argIdx) ? getStringMethodArg(args, argIdx) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String getStringMethodArg(List<TemplateModel> args, int argIdx) throws TemplateModelException {
/* 459 */     TemplateModel arg = args.get(argIdx);
/* 460 */     if (!(arg instanceof TemplateScalarModel)) {
/* 461 */       throw _MessageUtil.newMethodArgMustBeStringException("?" + this.key, argIdx, arg);
/*     */     }
/* 463 */     return EvalUtil.modelToString((TemplateScalarModel)arg, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Number getOptNumberMethodArg(List args, int argIdx) throws TemplateModelException {
/* 472 */     return (args.size() > argIdx) ? getNumberMethodArg(args, argIdx) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Number getNumberMethodArg(List<TemplateModel> args, int argIdx) throws TemplateModelException {
/* 480 */     TemplateModel arg = args.get(argIdx);
/* 481 */     if (!(arg instanceof TemplateNumberModel)) {
/* 482 */       throw _MessageUtil.newMethodArgMustBeNumberException("?" + this.key, argIdx, arg);
/*     */     }
/* 484 */     return EvalUtil.modelToNumber((TemplateNumberModel)arg, null);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final TemplateModelException newMethodArgInvalidValueException(int argIdx, Object[] details) {
/* 489 */     return _MessageUtil.newMethodArgInvalidValueException("?" + this.key, argIdx, details);
/*     */   }
/*     */   
/*     */   protected final TemplateModelException newMethodArgsInvalidValueException(Object[] details) {
/* 493 */     return _MessageUtil.newMethodArgsInvalidValueException("?" + this.key, details);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*     */     try {
/* 500 */       BuiltIn clone = (BuiltIn)clone();
/* 501 */       clone.target = this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
/* 502 */       return clone;
/* 503 */     } catch (CloneNotSupportedException e) {
/* 504 */       throw new RuntimeException("Internal error: " + e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 510 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 515 */     switch (idx) { case 0:
/* 516 */         return this.target;
/* 517 */       case 1: return this.key; }
/* 518 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 524 */     switch (idx) { case 0:
/* 525 */         return ParameterRole.LEFT_HAND_OPERAND;
/* 526 */       case 1: return ParameterRole.RIGHT_HAND_OPERAND; }
/* 527 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltIn.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */