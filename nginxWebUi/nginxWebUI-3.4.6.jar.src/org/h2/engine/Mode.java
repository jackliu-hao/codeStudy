/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Pattern;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.DataType;
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
/*     */ public class Mode
/*     */ {
/*     */   public enum ModeEnum
/*     */   {
/*  25 */     REGULAR, STRICT, LEGACY, DB2, Derby, MariaDB, MSSQLServer, HSQLDB, MySQL, Oracle, PostgreSQL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum UniqueIndexNullsHandling
/*     */   {
/*  37 */     ALLOW_DUPLICATES_WITH_ANY_NULL,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  43 */     ALLOW_DUPLICATES_WITH_ALL_NULLS,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  49 */     FORBID_ANY_DUPLICATES;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum ExpressionNames
/*     */   {
/*  59 */     OPTIMIZED_SQL,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  64 */     ORIGINAL_SQL,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     EMPTY,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     NUMBER,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  79 */     C_NUMBER,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     POSTGRESQL_STYLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum ViewExpressionNames
/*     */   {
/*  94 */     AS_IS,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     EXCEPTION,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     MYSQL_STYLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum CharPadding
/*     */   {
/* 115 */     ALWAYS,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 122 */     IN_RESULT_SETS,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 127 */     NEVER;
/*     */   }
/*     */   
/* 130 */   private static final HashMap<String, Mode> MODES = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean aliasColumnName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean convertOnlyToSmallerScale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean indexDefinitionInCreateTable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean squareBracketQuotedNames;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean systemColumns;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 171 */   public UniqueIndexNullsHandling uniqueIndexNullsHandling = UniqueIndexNullsHandling.ALLOW_DUPLICATES_WITH_ANY_NULL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean treatEmptyStringsAsNull;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean sysDummy1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowPlusForStringConcat;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean logIsLogBase10;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean swapLogFunctionParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean regexpReplaceBackslashReferences;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean swapConvertFunctionParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isolationLevelInSelectOrInsertStatement;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean onDuplicateKeyUpdate;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean replaceInto;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean insertOnConflict;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Pattern supportedClientInfoPropertiesRegEx;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportPoundSymbolForColumnNames;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowEmptyInPredicate;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 247 */   public CharPadding charPadding = CharPadding.ALWAYS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowDB2TimestampFormat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean discardWithTableHints;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean dateTimeValueWithinTransaction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean zeroExLiteralsAreBinaryStrings;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowUnrelatedOrderByExpressionsInDistinctQueries;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean alterTableExtensionsMySQL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean alterTableModifyColumn;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean truncateTableRestartIdentity;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean decimalSequences;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowEmptySchemaValuesAsDefaultSchema;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allNumericTypesHavePrecision;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean forBitData;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean charAndByteLengthUnits;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean nextvalAndCurrvalPseudoColumns;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean nextValueReturnsDifferentValues;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean updateSequenceOnManualIdentityInsertion;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean takeInsertedIdentity;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean takeGeneratedSequenceValue;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean identityColumnsHaveDefaultOnNull;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mergeWhere;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowUsingFromClauseInUpdateStatement;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean createUniqueConstraintForReferencedColumns;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 379 */   public ExpressionNames expressionNames = ExpressionNames.OPTIMIZED_SQL;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 384 */   public ViewExpressionNames viewExpressionNames = ViewExpressionNames.AS_IS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean topInSelect;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean topInDML;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean limit;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean minusIsExcept;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean identityDataType;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean serialDataTypes;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean identityClause;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean autoIncrementClause;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 431 */   public Set<String> disallowedTypes = Collections.emptySet();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 436 */   public HashMap<String, DataType> typeByNameMap = new HashMap<>();
/*     */ 
/*     */   
/*     */   public boolean groupByColumnIndex;
/*     */ 
/*     */   
/*     */   public boolean numericWithBooleanComparison;
/*     */ 
/*     */   
/*     */   private final String name;
/*     */ 
/*     */   
/*     */   private final ModeEnum modeEnum;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 453 */     Mode mode = new Mode(ModeEnum.REGULAR);
/* 454 */     mode.allowEmptyInPredicate = true;
/* 455 */     mode.dateTimeValueWithinTransaction = true;
/* 456 */     mode.topInSelect = true;
/* 457 */     mode.limit = true;
/* 458 */     mode.minusIsExcept = true;
/* 459 */     mode.identityDataType = true;
/* 460 */     mode.serialDataTypes = true;
/* 461 */     mode.autoIncrementClause = true;
/* 462 */     add(mode);
/*     */     
/* 464 */     mode = new Mode(ModeEnum.STRICT);
/* 465 */     mode.dateTimeValueWithinTransaction = true;
/* 466 */     add(mode);
/*     */     
/* 468 */     mode = new Mode(ModeEnum.LEGACY);
/*     */     
/* 470 */     mode.allowEmptyInPredicate = true;
/* 471 */     mode.dateTimeValueWithinTransaction = true;
/* 472 */     mode.topInSelect = true;
/* 473 */     mode.limit = true;
/* 474 */     mode.minusIsExcept = true;
/* 475 */     mode.identityDataType = true;
/* 476 */     mode.serialDataTypes = true;
/* 477 */     mode.autoIncrementClause = true;
/*     */     
/* 479 */     mode.identityClause = true;
/* 480 */     mode.updateSequenceOnManualIdentityInsertion = true;
/* 481 */     mode.takeInsertedIdentity = true;
/* 482 */     mode.identityColumnsHaveDefaultOnNull = true;
/* 483 */     mode.nextvalAndCurrvalPseudoColumns = true;
/*     */     
/* 485 */     mode.topInDML = true;
/* 486 */     mode.mergeWhere = true;
/*     */     
/* 488 */     mode.createUniqueConstraintForReferencedColumns = true;
/*     */     
/* 490 */     mode.numericWithBooleanComparison = true;
/* 491 */     add(mode);
/*     */     
/* 493 */     mode = new Mode(ModeEnum.DB2);
/* 494 */     mode.aliasColumnName = true;
/* 495 */     mode.sysDummy1 = true;
/* 496 */     mode.isolationLevelInSelectOrInsertStatement = true;
/*     */ 
/*     */ 
/*     */     
/* 500 */     mode
/* 501 */       .supportedClientInfoPropertiesRegEx = Pattern.compile("ApplicationName|ClientAccountingInformation|ClientUser|ClientCorrelationToken");
/*     */     
/* 503 */     mode.allowDB2TimestampFormat = true;
/* 504 */     mode.forBitData = true;
/* 505 */     mode.takeInsertedIdentity = true;
/* 506 */     mode.expressionNames = ExpressionNames.NUMBER;
/* 507 */     mode.viewExpressionNames = ViewExpressionNames.EXCEPTION;
/* 508 */     mode.limit = true;
/* 509 */     mode.minusIsExcept = true;
/* 510 */     mode.numericWithBooleanComparison = true;
/* 511 */     add(mode);
/*     */     
/* 513 */     mode = new Mode(ModeEnum.Derby);
/* 514 */     mode.aliasColumnName = true;
/* 515 */     mode.uniqueIndexNullsHandling = UniqueIndexNullsHandling.FORBID_ANY_DUPLICATES;
/* 516 */     mode.sysDummy1 = true;
/* 517 */     mode.isolationLevelInSelectOrInsertStatement = true;
/*     */     
/* 519 */     mode.supportedClientInfoPropertiesRegEx = null;
/* 520 */     mode.forBitData = true;
/* 521 */     mode.takeInsertedIdentity = true;
/* 522 */     mode.expressionNames = ExpressionNames.NUMBER;
/* 523 */     mode.viewExpressionNames = ViewExpressionNames.EXCEPTION;
/* 524 */     add(mode);
/*     */     
/* 526 */     mode = new Mode(ModeEnum.HSQLDB);
/* 527 */     mode.allowPlusForStringConcat = true;
/* 528 */     mode.identityColumnsHaveDefaultOnNull = true;
/*     */ 
/*     */     
/* 531 */     mode.supportedClientInfoPropertiesRegEx = null;
/* 532 */     mode.expressionNames = ExpressionNames.C_NUMBER;
/* 533 */     mode.topInSelect = true;
/* 534 */     mode.limit = true;
/* 535 */     mode.minusIsExcept = true;
/* 536 */     mode.numericWithBooleanComparison = true;
/* 537 */     add(mode);
/*     */     
/* 539 */     mode = new Mode(ModeEnum.MSSQLServer);
/* 540 */     mode.aliasColumnName = true;
/* 541 */     mode.squareBracketQuotedNames = true;
/* 542 */     mode.uniqueIndexNullsHandling = UniqueIndexNullsHandling.FORBID_ANY_DUPLICATES;
/* 543 */     mode.allowPlusForStringConcat = true;
/* 544 */     mode.swapLogFunctionParameters = true;
/* 545 */     mode.swapConvertFunctionParameters = true;
/* 546 */     mode.supportPoundSymbolForColumnNames = true;
/* 547 */     mode.discardWithTableHints = true;
/*     */ 
/*     */     
/* 550 */     mode.supportedClientInfoPropertiesRegEx = null;
/* 551 */     mode.zeroExLiteralsAreBinaryStrings = true;
/* 552 */     mode.truncateTableRestartIdentity = true;
/* 553 */     mode.takeInsertedIdentity = true;
/* 554 */     DataType dataType = DataType.createNumeric(19, 4);
/* 555 */     dataType.type = 13;
/* 556 */     dataType.sqlType = 2;
/* 557 */     dataType.specialPrecisionScale = true;
/* 558 */     mode.typeByNameMap.put("MONEY", dataType);
/* 559 */     dataType = DataType.createNumeric(10, 4);
/* 560 */     dataType.type = 13;
/* 561 */     dataType.sqlType = 2;
/* 562 */     dataType.specialPrecisionScale = true;
/* 563 */     mode.typeByNameMap.put("SMALLMONEY", dataType);
/* 564 */     mode.typeByNameMap.put("UNIQUEIDENTIFIER", DataType.getDataType(39));
/* 565 */     mode.allowEmptySchemaValuesAsDefaultSchema = true;
/* 566 */     mode.expressionNames = ExpressionNames.EMPTY;
/* 567 */     mode.viewExpressionNames = ViewExpressionNames.EXCEPTION;
/* 568 */     mode.topInSelect = true;
/* 569 */     mode.topInDML = true;
/* 570 */     mode.identityClause = true;
/* 571 */     mode.numericWithBooleanComparison = true;
/* 572 */     add(mode);
/*     */     
/* 574 */     mode = new Mode(ModeEnum.MariaDB);
/* 575 */     mode.indexDefinitionInCreateTable = true;
/* 576 */     mode.regexpReplaceBackslashReferences = true;
/* 577 */     mode.onDuplicateKeyUpdate = true;
/* 578 */     mode.replaceInto = true;
/* 579 */     mode.charPadding = CharPadding.NEVER;
/* 580 */     mode.supportedClientInfoPropertiesRegEx = Pattern.compile(".*");
/* 581 */     mode.zeroExLiteralsAreBinaryStrings = true;
/* 582 */     mode.allowUnrelatedOrderByExpressionsInDistinctQueries = true;
/* 583 */     mode.alterTableExtensionsMySQL = true;
/* 584 */     mode.alterTableModifyColumn = true;
/* 585 */     mode.truncateTableRestartIdentity = true;
/* 586 */     mode.allNumericTypesHavePrecision = true;
/* 587 */     mode.nextValueReturnsDifferentValues = true;
/* 588 */     mode.updateSequenceOnManualIdentityInsertion = true;
/* 589 */     mode.takeInsertedIdentity = true;
/* 590 */     mode.identityColumnsHaveDefaultOnNull = true;
/* 591 */     mode.expressionNames = ExpressionNames.ORIGINAL_SQL;
/* 592 */     mode.viewExpressionNames = ViewExpressionNames.MYSQL_STYLE;
/* 593 */     mode.limit = true;
/* 594 */     mode.autoIncrementClause = true;
/* 595 */     mode.typeByNameMap.put("YEAR", DataType.getDataType(10));
/* 596 */     mode.groupByColumnIndex = true;
/* 597 */     mode.numericWithBooleanComparison = true;
/* 598 */     add(mode);
/*     */     
/* 600 */     mode = new Mode(ModeEnum.MySQL);
/* 601 */     mode.indexDefinitionInCreateTable = true;
/* 602 */     mode.regexpReplaceBackslashReferences = true;
/* 603 */     mode.onDuplicateKeyUpdate = true;
/* 604 */     mode.replaceInto = true;
/* 605 */     mode.charPadding = CharPadding.NEVER;
/*     */ 
/*     */     
/* 608 */     mode
/* 609 */       .supportedClientInfoPropertiesRegEx = Pattern.compile(".*");
/* 610 */     mode.zeroExLiteralsAreBinaryStrings = true;
/* 611 */     mode.allowUnrelatedOrderByExpressionsInDistinctQueries = true;
/* 612 */     mode.alterTableExtensionsMySQL = true;
/* 613 */     mode.alterTableModifyColumn = true;
/* 614 */     mode.truncateTableRestartIdentity = true;
/* 615 */     mode.allNumericTypesHavePrecision = true;
/* 616 */     mode.updateSequenceOnManualIdentityInsertion = true;
/* 617 */     mode.takeInsertedIdentity = true;
/* 618 */     mode.identityColumnsHaveDefaultOnNull = true;
/* 619 */     mode.createUniqueConstraintForReferencedColumns = true;
/* 620 */     mode.expressionNames = ExpressionNames.ORIGINAL_SQL;
/* 621 */     mode.viewExpressionNames = ViewExpressionNames.MYSQL_STYLE;
/* 622 */     mode.limit = true;
/* 623 */     mode.autoIncrementClause = true;
/* 624 */     mode.typeByNameMap.put("YEAR", DataType.getDataType(10));
/* 625 */     mode.groupByColumnIndex = true;
/* 626 */     mode.numericWithBooleanComparison = true;
/* 627 */     add(mode);
/*     */     
/* 629 */     mode = new Mode(ModeEnum.Oracle);
/* 630 */     mode.aliasColumnName = true;
/* 631 */     mode.convertOnlyToSmallerScale = true;
/* 632 */     mode.uniqueIndexNullsHandling = UniqueIndexNullsHandling.ALLOW_DUPLICATES_WITH_ALL_NULLS;
/* 633 */     mode.treatEmptyStringsAsNull = true;
/* 634 */     mode.regexpReplaceBackslashReferences = true;
/* 635 */     mode.supportPoundSymbolForColumnNames = true;
/*     */ 
/*     */     
/* 638 */     mode
/* 639 */       .supportedClientInfoPropertiesRegEx = Pattern.compile(".*\\..*");
/* 640 */     mode.alterTableModifyColumn = true;
/* 641 */     mode.decimalSequences = true;
/* 642 */     mode.charAndByteLengthUnits = true;
/* 643 */     mode.nextvalAndCurrvalPseudoColumns = true;
/* 644 */     mode.mergeWhere = true;
/* 645 */     mode.minusIsExcept = true;
/* 646 */     mode.expressionNames = ExpressionNames.ORIGINAL_SQL;
/* 647 */     mode.viewExpressionNames = ViewExpressionNames.EXCEPTION;
/* 648 */     mode.typeByNameMap.put("BINARY_FLOAT", DataType.getDataType(14));
/* 649 */     mode.typeByNameMap.put("BINARY_DOUBLE", DataType.getDataType(15));
/* 650 */     dataType = DataType.createDate(19, 19, "DATE", false, 0, 0);
/* 651 */     dataType.type = 20;
/* 652 */     dataType.sqlType = 93;
/* 653 */     dataType.specialPrecisionScale = true;
/* 654 */     mode.typeByNameMap.put("DATE", dataType);
/* 655 */     add(mode);
/*     */     
/* 657 */     mode = new Mode(ModeEnum.PostgreSQL);
/* 658 */     mode.aliasColumnName = true;
/* 659 */     mode.systemColumns = true;
/* 660 */     mode.logIsLogBase10 = true;
/* 661 */     mode.regexpReplaceBackslashReferences = true;
/* 662 */     mode.insertOnConflict = true;
/*     */ 
/*     */ 
/*     */     
/* 666 */     mode
/* 667 */       .supportedClientInfoPropertiesRegEx = Pattern.compile("ApplicationName");
/* 668 */     mode.charPadding = CharPadding.IN_RESULT_SETS;
/* 669 */     mode.nextValueReturnsDifferentValues = true;
/* 670 */     mode.takeGeneratedSequenceValue = true;
/* 671 */     mode.expressionNames = ExpressionNames.POSTGRESQL_STYLE;
/* 672 */     mode.allowUsingFromClauseInUpdateStatement = true;
/* 673 */     mode.limit = true;
/* 674 */     mode.serialDataTypes = true;
/*     */     
/* 676 */     HashSet<String> hashSet = new HashSet();
/* 677 */     hashSet.add("NUMBER");
/* 678 */     hashSet.add("TINYINT");
/* 679 */     hashSet.add("BLOB");
/* 680 */     hashSet.add("VARCHAR_IGNORECASE");
/* 681 */     mode.disallowedTypes = hashSet;
/* 682 */     dataType = DataType.getDataType(38);
/* 683 */     mode.typeByNameMap.put("JSONB", dataType);
/* 684 */     dataType = DataType.createNumeric(19, 2);
/* 685 */     dataType.type = 13;
/* 686 */     dataType.sqlType = 2;
/* 687 */     dataType.specialPrecisionScale = true;
/* 688 */     mode.typeByNameMap.put("MONEY", dataType);
/* 689 */     dataType = DataType.getDataType(11);
/* 690 */     mode.typeByNameMap.put("OID", dataType);
/* 691 */     mode.dateTimeValueWithinTransaction = true;
/* 692 */     mode.groupByColumnIndex = true;
/* 693 */     add(mode);
/*     */   }
/*     */   
/*     */   private Mode(ModeEnum paramModeEnum) {
/* 697 */     this.name = paramModeEnum.name();
/* 698 */     this.modeEnum = paramModeEnum;
/*     */   }
/*     */   
/*     */   private static void add(Mode paramMode) {
/* 702 */     MODES.put(StringUtils.toUpperEnglish(paramMode.name), paramMode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mode getInstance(String paramString) {
/* 712 */     return MODES.get(StringUtils.toUpperEnglish(paramString));
/*     */   }
/*     */   
/*     */   public static Mode getRegular() {
/* 716 */     return getInstance(ModeEnum.REGULAR.name());
/*     */   }
/*     */   
/*     */   public String getName() {
/* 720 */     return this.name;
/*     */   }
/*     */   
/*     */   public ModeEnum getEnum() {
/* 724 */     return this.modeEnum;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 729 */     return this.name;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\Mode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */