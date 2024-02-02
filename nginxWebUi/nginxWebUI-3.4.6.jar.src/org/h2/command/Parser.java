/*      */ package org.h2.command;
/*      */ 
/*      */ import java.nio.charset.Charset;
/*      */ import java.text.Collator;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import org.h2.api.IntervalQualifier;
/*      */ import org.h2.command.ddl.AlterDomainAddConstraint;
/*      */ import org.h2.command.ddl.AlterDomainDropConstraint;
/*      */ import org.h2.command.ddl.AlterDomainExpressions;
/*      */ import org.h2.command.ddl.AlterDomainRename;
/*      */ import org.h2.command.ddl.AlterDomainRenameConstraint;
/*      */ import org.h2.command.ddl.AlterIndexRename;
/*      */ import org.h2.command.ddl.AlterSchemaRename;
/*      */ import org.h2.command.ddl.AlterSequence;
/*      */ import org.h2.command.ddl.AlterTableAddConstraint;
/*      */ import org.h2.command.ddl.AlterTableAlterColumn;
/*      */ import org.h2.command.ddl.AlterTableDropConstraint;
/*      */ import org.h2.command.ddl.AlterTableRename;
/*      */ import org.h2.command.ddl.AlterTableRenameColumn;
/*      */ import org.h2.command.ddl.AlterTableRenameConstraint;
/*      */ import org.h2.command.ddl.AlterUser;
/*      */ import org.h2.command.ddl.AlterView;
/*      */ import org.h2.command.ddl.Analyze;
/*      */ import org.h2.command.ddl.CommandWithColumns;
/*      */ import org.h2.command.ddl.CreateAggregate;
/*      */ import org.h2.command.ddl.CreateConstant;
/*      */ import org.h2.command.ddl.CreateDomain;
/*      */ import org.h2.command.ddl.CreateFunctionAlias;
/*      */ import org.h2.command.ddl.CreateIndex;
/*      */ import org.h2.command.ddl.CreateLinkedTable;
/*      */ import org.h2.command.ddl.CreateRole;
/*      */ import org.h2.command.ddl.CreateSchema;
/*      */ import org.h2.command.ddl.CreateSequence;
/*      */ import org.h2.command.ddl.CreateSynonym;
/*      */ import org.h2.command.ddl.CreateTable;
/*      */ import org.h2.command.ddl.CreateTrigger;
/*      */ import org.h2.command.ddl.CreateUser;
/*      */ import org.h2.command.ddl.CreateView;
/*      */ import org.h2.command.ddl.DeallocateProcedure;
/*      */ import org.h2.command.ddl.DefineCommand;
/*      */ import org.h2.command.ddl.DropAggregate;
/*      */ import org.h2.command.ddl.DropConstant;
/*      */ import org.h2.command.ddl.DropDatabase;
/*      */ import org.h2.command.ddl.DropDomain;
/*      */ import org.h2.command.ddl.DropFunctionAlias;
/*      */ import org.h2.command.ddl.DropIndex;
/*      */ import org.h2.command.ddl.DropRole;
/*      */ import org.h2.command.ddl.DropSchema;
/*      */ import org.h2.command.ddl.DropSequence;
/*      */ import org.h2.command.ddl.DropSynonym;
/*      */ import org.h2.command.ddl.DropTable;
/*      */ import org.h2.command.ddl.DropTrigger;
/*      */ import org.h2.command.ddl.DropUser;
/*      */ import org.h2.command.ddl.DropView;
/*      */ import org.h2.command.ddl.GrantRevoke;
/*      */ import org.h2.command.ddl.PrepareProcedure;
/*      */ import org.h2.command.ddl.SequenceOptions;
/*      */ import org.h2.command.ddl.SetComment;
/*      */ import org.h2.command.ddl.TruncateTable;
/*      */ import org.h2.command.dml.AlterTableSet;
/*      */ import org.h2.command.dml.BackupCommand;
/*      */ import org.h2.command.dml.Call;
/*      */ import org.h2.command.dml.CommandWithValues;
/*      */ import org.h2.command.dml.DataChangeStatement;
/*      */ import org.h2.command.dml.Delete;
/*      */ import org.h2.command.dml.ExecuteImmediate;
/*      */ import org.h2.command.dml.ExecuteProcedure;
/*      */ import org.h2.command.dml.Explain;
/*      */ import org.h2.command.dml.Help;
/*      */ import org.h2.command.dml.Insert;
/*      */ import org.h2.command.dml.Merge;
/*      */ import org.h2.command.dml.MergeUsing;
/*      */ import org.h2.command.dml.NoOperation;
/*      */ import org.h2.command.dml.RunScriptCommand;
/*      */ import org.h2.command.dml.ScriptCommand;
/*      */ import org.h2.command.dml.Set;
/*      */ import org.h2.command.dml.SetClauseList;
/*      */ import org.h2.command.dml.SetSessionCharacteristics;
/*      */ import org.h2.command.dml.SetTypes;
/*      */ import org.h2.command.dml.TransactionCommand;
/*      */ import org.h2.command.dml.Update;
/*      */ import org.h2.command.query.Query;
/*      */ import org.h2.command.query.QueryOrderBy;
/*      */ import org.h2.command.query.Select;
/*      */ import org.h2.command.query.SelectUnion;
/*      */ import org.h2.command.query.TableValueConstructor;
/*      */ import org.h2.constraint.ConstraintActionType;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.ConnectionInfo;
/*      */ import org.h2.engine.Database;
/*      */ import org.h2.engine.DbSettings;
/*      */ import org.h2.engine.IsolationLevel;
/*      */ import org.h2.engine.Mode;
/*      */ import org.h2.engine.Procedure;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.engine.User;
/*      */ import org.h2.expression.ArrayElementReference;
/*      */ import org.h2.expression.BinaryOperation;
/*      */ import org.h2.expression.ConcatenationOperation;
/*      */ import org.h2.expression.Expression;
/*      */ import org.h2.expression.ExpressionColumn;
/*      */ import org.h2.expression.ExpressionWithFlags;
/*      */ import org.h2.expression.Parameter;
/*      */ import org.h2.expression.SearchedCase;
/*      */ import org.h2.expression.SequenceValue;
/*      */ import org.h2.expression.SimpleCase;
/*      */ import org.h2.expression.Subquery;
/*      */ import org.h2.expression.ValueExpression;
/*      */ import org.h2.expression.Wildcard;
/*      */ import org.h2.expression.aggregate.AbstractAggregate;
/*      */ import org.h2.expression.aggregate.Aggregate;
/*      */ import org.h2.expression.aggregate.AggregateType;
/*      */ import org.h2.expression.aggregate.JavaAggregate;
/*      */ import org.h2.expression.aggregate.ListaggArguments;
/*      */ import org.h2.expression.analysis.DataAnalysisOperation;
/*      */ import org.h2.expression.analysis.Window;
/*      */ import org.h2.expression.analysis.WindowFrame;
/*      */ import org.h2.expression.analysis.WindowFrameBound;
/*      */ import org.h2.expression.analysis.WindowFrameBoundType;
/*      */ import org.h2.expression.analysis.WindowFrameExclusion;
/*      */ import org.h2.expression.analysis.WindowFrameUnits;
/*      */ import org.h2.expression.analysis.WindowFunction;
/*      */ import org.h2.expression.analysis.WindowFunctionType;
/*      */ import org.h2.expression.condition.BetweenPredicate;
/*      */ import org.h2.expression.condition.BooleanTest;
/*      */ import org.h2.expression.condition.CompareLike;
/*      */ import org.h2.expression.condition.Comparison;
/*      */ import org.h2.expression.condition.ConditionAndOr;
/*      */ import org.h2.expression.condition.ConditionAndOrN;
/*      */ import org.h2.expression.condition.ConditionIn;
/*      */ import org.h2.expression.condition.ConditionInParameter;
/*      */ import org.h2.expression.condition.ConditionInQuery;
/*      */ import org.h2.expression.condition.ConditionLocalAndGlobal;
/*      */ import org.h2.expression.condition.ConditionNot;
/*      */ import org.h2.expression.condition.ExistsPredicate;
/*      */ import org.h2.expression.condition.IsJsonPredicate;
/*      */ import org.h2.expression.condition.NullPredicate;
/*      */ import org.h2.expression.condition.TypePredicate;
/*      */ import org.h2.expression.condition.UniquePredicate;
/*      */ import org.h2.expression.function.ArrayFunction;
/*      */ import org.h2.expression.function.BitFunction;
/*      */ import org.h2.expression.function.BuiltinFunctions;
/*      */ import org.h2.expression.function.CSVWriteFunction;
/*      */ import org.h2.expression.function.CardinalityExpression;
/*      */ import org.h2.expression.function.CastSpecification;
/*      */ import org.h2.expression.function.CoalesceFunction;
/*      */ import org.h2.expression.function.CompatibilitySequenceValueFunction;
/*      */ import org.h2.expression.function.CompressFunction;
/*      */ import org.h2.expression.function.ConcatFunction;
/*      */ import org.h2.expression.function.CryptFunction;
/*      */ import org.h2.expression.function.CurrentDateTimeValueFunction;
/*      */ import org.h2.expression.function.CurrentGeneralValueSpecification;
/*      */ import org.h2.expression.function.DBObjectFunction;
/*      */ import org.h2.expression.function.DataTypeSQLFunction;
/*      */ import org.h2.expression.function.DateTimeFormatFunction;
/*      */ import org.h2.expression.function.DateTimeFunction;
/*      */ import org.h2.expression.function.DayMonthNameFunction;
/*      */ import org.h2.expression.function.FileFunction;
/*      */ import org.h2.expression.function.HashFunction;
/*      */ import org.h2.expression.function.JavaFunction;
/*      */ import org.h2.expression.function.JsonConstructorFunction;
/*      */ import org.h2.expression.function.LengthFunction;
/*      */ import org.h2.expression.function.MathFunction;
/*      */ import org.h2.expression.function.MathFunction1;
/*      */ import org.h2.expression.function.MathFunction2;
/*      */ import org.h2.expression.function.NullIfFunction;
/*      */ import org.h2.expression.function.RandFunction;
/*      */ import org.h2.expression.function.RegexpFunction;
/*      */ import org.h2.expression.function.SessionControlFunction;
/*      */ import org.h2.expression.function.SetFunction;
/*      */ import org.h2.expression.function.SignalFunction;
/*      */ import org.h2.expression.function.SoundexFunction;
/*      */ import org.h2.expression.function.StringFunction;
/*      */ import org.h2.expression.function.StringFunction1;
/*      */ import org.h2.expression.function.StringFunction2;
/*      */ import org.h2.expression.function.SubstringFunction;
/*      */ import org.h2.expression.function.SysInfoFunction;
/*      */ import org.h2.expression.function.TableInfoFunction;
/*      */ import org.h2.expression.function.ToCharFunction;
/*      */ import org.h2.expression.function.TrimFunction;
/*      */ import org.h2.expression.function.TruncateValueFunction;
/*      */ import org.h2.expression.function.XMLFunction;
/*      */ import org.h2.expression.function.table.ArrayTableFunction;
/*      */ import org.h2.expression.function.table.CSVReadFunction;
/*      */ import org.h2.expression.function.table.JavaTableFunction;
/*      */ import org.h2.expression.function.table.LinkSchemaFunction;
/*      */ import org.h2.expression.function.table.TableFunction;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.mode.FunctionsPostgreSQL;
/*      */ import org.h2.mode.ModeFunction;
/*      */ import org.h2.mode.OnDuplicateKeyValues;
/*      */ import org.h2.schema.Domain;
/*      */ import org.h2.schema.FunctionAlias;
/*      */ import org.h2.schema.Schema;
/*      */ import org.h2.schema.SchemaObject;
/*      */ import org.h2.schema.Sequence;
/*      */ import org.h2.schema.UserAggregate;
/*      */ import org.h2.schema.UserDefinedFunction;
/*      */ import org.h2.table.Column;
/*      */ import org.h2.table.DataChangeDeltaTable;
/*      */ import org.h2.table.DualTable;
/*      */ import org.h2.table.IndexColumn;
/*      */ import org.h2.table.IndexHints;
/*      */ import org.h2.table.Table;
/*      */ import org.h2.table.TableFilter;
/*      */ import org.h2.table.TableView;
/*      */ import org.h2.util.IntervalUtils;
/*      */ import org.h2.util.ParserUtil;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.util.geometry.EWKTUtils;
/*      */ import org.h2.util.json.JSONItemType;
/*      */ import org.h2.value.CompareMode;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.ExtTypeInfo;
/*      */ import org.h2.value.ExtTypeInfoEnum;
/*      */ import org.h2.value.ExtTypeInfoGeometry;
/*      */ import org.h2.value.ExtTypeInfoNumeric;
/*      */ import org.h2.value.ExtTypeInfoRow;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueDate;
/*      */ import org.h2.value.ValueDouble;
/*      */ import org.h2.value.ValueGeometry;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueJson;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueTime;
/*      */ import org.h2.value.ValueTimeTimeZone;
/*      */ import org.h2.value.ValueTimestamp;
/*      */ import org.h2.value.ValueTimestampTimeZone;
/*      */ import org.h2.value.ValueUuid;
/*      */ import org.h2.value.ValueVarchar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Parser
/*      */ {
/*      */   private static final String WITH_STATEMENT_SUPPORTS_LIMITED_SUB_STATEMENTS = "WITH statement supports only SELECT, TABLE, VALUES, CREATE TABLE, INSERT, UPDATE, MERGE or DELETE statements";
/*      */   private final Database database;
/*      */   private final SessionLocal session;
/*      */   private final boolean identifiersToLower;
/*      */   private final boolean identifiersToUpper;
/*      */   private final boolean variableBinary;
/*      */   private final BitSet nonKeywords;
/*      */   ArrayList<Token> tokens;
/*      */   int tokenIndex;
/*      */   Token token;
/*      */   private int currentTokenType;
/*      */   private String currentToken;
/*      */   private String sqlCommand;
/*      */   private CreateView createView;
/*      */   private Prepared currentPrepared;
/*      */   private Select currentSelect;
/*      */   private List<TableView> cteCleanups;
/*      */   private ArrayList<Parameter> parameters;
/*      */   private ArrayList<Parameter> suppliedParameters;
/*      */   private String schemaName;
/*      */   private ArrayList<String> expectedList;
/*      */   private boolean rightsChecked;
/*      */   private boolean recompileAlways;
/*      */   private boolean literalsChecked;
/*      */   private int orderInFrom;
/*      */   private boolean parseDomainConstraint;
/*      */   
/*      */   public static BitSet parseNonKeywords(String[] paramArrayOfString) {
/*  466 */     if (paramArrayOfString.length == 0) {
/*  467 */       return null;
/*      */     }
/*  469 */     BitSet bitSet = new BitSet();
/*  470 */     for (String str : paramArrayOfString) {
/*  471 */       int i = Arrays.binarySearch((Object[])Token.TOKENS, 3, 92, str);
/*  472 */       if (i >= 0) {
/*  473 */         bitSet.set(i);
/*      */       }
/*      */     } 
/*  476 */     return bitSet.isEmpty() ? null : bitSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatNonKeywords(BitSet paramBitSet) {
/*  486 */     if (paramBitSet == null || paramBitSet.isEmpty()) {
/*  487 */       return "";
/*      */     }
/*  489 */     StringBuilder stringBuilder = new StringBuilder();
/*  490 */     for (int i = -1; (i = paramBitSet.nextSetBit(i + 1)) >= 0;) {
/*  491 */       if (i >= 3 && i <= 91) {
/*  492 */         if (stringBuilder.length() > 0) {
/*  493 */           stringBuilder.append(',');
/*      */         }
/*  495 */         stringBuilder.append(Token.TOKENS[i]);
/*      */       } 
/*      */     } 
/*  498 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Parser(SessionLocal paramSessionLocal) {
/*  507 */     this.database = paramSessionLocal.getDatabase();
/*  508 */     DbSettings dbSettings = this.database.getSettings();
/*  509 */     this.identifiersToLower = dbSettings.databaseToLower;
/*  510 */     this.identifiersToUpper = dbSettings.databaseToUpper;
/*  511 */     this.variableBinary = paramSessionLocal.isVariableBinary();
/*  512 */     this.nonKeywords = paramSessionLocal.getNonKeywords();
/*  513 */     this.session = paramSessionLocal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Parser() {
/*  520 */     this.database = null;
/*  521 */     this.identifiersToLower = false;
/*  522 */     this.identifiersToUpper = false;
/*  523 */     this.variableBinary = false;
/*  524 */     this.nonKeywords = null;
/*  525 */     this.session = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Prepared prepare(String paramString) {
/*  535 */     Prepared prepared = parse(paramString, (ArrayList<Token>)null);
/*  536 */     prepared.prepare();
/*  537 */     if (this.currentTokenType != 93) {
/*  538 */       throw getSyntaxError();
/*      */     }
/*  540 */     return prepared;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Command prepareCommand(String paramString) {
/*      */     try {
/*  551 */       Prepared prepared = parse(paramString, (ArrayList<Token>)null);
/*  552 */       if (this.currentTokenType != 115 && this.currentTokenType != 93) {
/*  553 */         addExpected(115);
/*  554 */         throw getSyntaxError();
/*      */       } 
/*      */       try {
/*  557 */         prepared.prepare();
/*  558 */       } catch (Throwable throwable) {
/*  559 */         CommandContainer.clearCTE(this.session, prepared);
/*  560 */         throw throwable;
/*      */       } 
/*  562 */       int i = this.token.start();
/*  563 */       if (i < paramString.length()) {
/*  564 */         paramString = paramString.substring(0, i);
/*      */       }
/*  566 */       CommandContainer commandContainer = new CommandContainer(this.session, paramString, prepared);
/*  567 */       while (this.currentTokenType == 115) {
/*  568 */         read();
/*      */       }
/*  570 */       if (this.currentTokenType != 93) {
/*  571 */         int j = this.token.start();
/*  572 */         return prepareCommandList(commandContainer, prepared, paramString, this.sqlCommand.substring(j), getRemainingTokens(j));
/*      */       } 
/*  574 */       return commandContainer;
/*  575 */     } catch (DbException dbException) {
/*  576 */       throw dbException.addSQL(this.sqlCommand);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private CommandList prepareCommandList(CommandContainer paramCommandContainer, Prepared paramPrepared, String paramString1, String paramString2, ArrayList<Token> paramArrayList) {
/*      */     try {
/*  583 */       ArrayList<Prepared> arrayList = Utils.newSmallArrayList();
/*      */       while (true) {
/*  585 */         if (paramPrepared instanceof DefineCommand)
/*      */         {
/*  587 */           return new CommandList(this.session, paramString1, paramCommandContainer, arrayList, this.parameters, paramString2);
/*      */         }
/*  589 */         this.suppliedParameters = this.parameters;
/*      */         try {
/*  591 */           paramPrepared = parse(paramString2, paramArrayList);
/*  592 */         } catch (DbException dbException) {
/*      */           
/*  594 */           if (dbException.getErrorCode() == 90123) {
/*  595 */             throw dbException;
/*      */           }
/*  597 */           return new CommandList(this.session, paramString1, paramCommandContainer, arrayList, this.parameters, paramString2);
/*      */         } 
/*  599 */         arrayList.add(paramPrepared);
/*  600 */         if (this.currentTokenType != 115 && this.currentTokenType != 93) {
/*  601 */           addExpected(115);
/*  602 */           throw getSyntaxError();
/*      */         } 
/*  604 */         while (this.currentTokenType == 115) {
/*  605 */           read();
/*      */         }
/*  607 */         if (this.currentTokenType == 93) {
/*      */           break;
/*      */         }
/*  610 */         int i = this.token.start();
/*  611 */         paramString2 = this.sqlCommand.substring(i);
/*  612 */         paramArrayList = getRemainingTokens(i);
/*      */       } 
/*  614 */       return new CommandList(this.session, paramString1, paramCommandContainer, arrayList, this.parameters, null);
/*  615 */     } catch (Throwable throwable) {
/*  616 */       paramCommandContainer.clearCTE();
/*  617 */       throw throwable;
/*      */     } 
/*      */   }
/*      */   
/*      */   private ArrayList<Token> getRemainingTokens(int paramInt) {
/*  622 */     List<Token> list = this.tokens.subList(this.tokenIndex, this.tokens.size());
/*  623 */     ArrayList<Token> arrayList = new ArrayList<>(list);
/*  624 */     list.clear();
/*  625 */     this.tokens.add(new Token.EndOfInputToken(paramInt));
/*  626 */     for (Token token : arrayList) {
/*  627 */       token.subtractFromStart(paramInt);
/*      */     }
/*  629 */     return arrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Prepared parse(String paramString, ArrayList<Token> paramArrayList) {
/*      */     Prepared prepared;
/*  640 */     initialize(paramString, paramArrayList, false);
/*      */ 
/*      */     
/*      */     try {
/*  644 */       prepared = parse(paramString, false);
/*  645 */     } catch (DbException dbException) {
/*  646 */       if (dbException.getErrorCode() == 42000) {
/*      */         
/*  648 */         resetTokenIndex();
/*  649 */         prepared = parse(paramString, true);
/*      */       } else {
/*  651 */         throw dbException.addSQL(paramString);
/*      */       } 
/*      */     } 
/*  654 */     prepared.setPrepareAlways(this.recompileAlways);
/*  655 */     prepared.setParameterList(this.parameters);
/*  656 */     return prepared;
/*      */   }
/*      */   
/*      */   private Prepared parse(String paramString, boolean paramBoolean) { Prepared prepared;
/*  660 */     if (paramBoolean) {
/*  661 */       this.expectedList = new ArrayList<>();
/*      */     } else {
/*  663 */       this.expectedList = null;
/*      */     } 
/*  665 */     this.parameters = (this.suppliedParameters != null) ? this.suppliedParameters : Utils.newSmallArrayList();
/*  666 */     this.currentSelect = null;
/*  667 */     this.currentPrepared = null;
/*  668 */     this.createView = null;
/*  669 */     this.cteCleanups = null;
/*  670 */     this.recompileAlways = false;
/*  671 */     read();
/*      */     
/*      */     try {
/*  674 */       prepared = parsePrepared();
/*  675 */       prepared.setCteCleanups(this.cteCleanups);
/*  676 */     } catch (Throwable throwable) {
/*  677 */       if (this.cteCleanups != null) {
/*  678 */         CommandContainer.clearCTE(this.session, this.cteCleanups);
/*      */       }
/*  680 */       throw throwable;
/*      */     } 
/*  682 */     return prepared; } private Prepared parsePrepared() {
/*      */     Call call;
/*      */     Query query;
/*      */     Prepared prepared;
/*  686 */     int i = this.tokenIndex;
/*  687 */     NoOperation noOperation = null;
/*  688 */     switch (this.currentTokenType) {
/*      */       case 93:
/*      */       case 115:
/*  691 */         noOperation = new NoOperation(this.session);
/*  692 */         setSQL((Prepared)noOperation, i);
/*  693 */         return (Prepared)noOperation;
/*      */ 
/*      */       
/*      */       case 92:
/*  697 */         readParameter().setValue((Value)ValueNull.INSTANCE);
/*  698 */         read(95);
/*  699 */         i = this.tokenIndex;
/*  700 */         read("CALL");
/*  701 */         call = parseCall();
/*      */         break;
/*      */       case 69:
/*      */       case 75:
/*      */       case 85:
/*      */       case 105:
/*  707 */         query = parseQuery();
/*      */         break;
/*      */       case 89:
/*  710 */         read();
/*  711 */         prepared = parseWithStatementOrQuery(i);
/*      */         break;
/*      */       case 71:
/*  714 */         read();
/*  715 */         prepared = parseSet();
/*      */         break;
/*      */       case 2:
/*  718 */         if (this.token.isQuoted()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  727 */         switch (this.currentToken.charAt(0) & 0xFFDF) {
/*      */           case 65:
/*  729 */             if (readIf("ALTER")) {
/*  730 */               prepared = parseAlter(); break;
/*  731 */             }  if (readIf("ANALYZE")) {
/*  732 */               prepared = parseAnalyze();
/*      */             }
/*      */             break;
/*      */           case 66:
/*  736 */             if (readIf("BACKUP")) {
/*  737 */               prepared = parseBackup(); break;
/*  738 */             }  if (readIf("BEGIN")) {
/*  739 */               TransactionCommand transactionCommand = parseBegin();
/*      */             }
/*      */             break;
/*      */           case 67:
/*  743 */             if (readIf("COMMIT")) {
/*  744 */               TransactionCommand transactionCommand = parseCommit(); break;
/*  745 */             }  if (readIf("CREATE")) {
/*  746 */               prepared = parseCreate(); break;
/*  747 */             }  if (readIf("CALL")) {
/*  748 */               Call call1 = parseCall(); break;
/*  749 */             }  if (readIf("CHECKPOINT")) {
/*  750 */               TransactionCommand transactionCommand = parseCheckpoint(); break;
/*  751 */             }  if (readIf("COMMENT")) {
/*  752 */               prepared = parseComment();
/*      */             }
/*      */             break;
/*      */           case 68:
/*  756 */             if (readIf("DELETE")) {
/*  757 */               Delete delete = parseDelete(i); break;
/*  758 */             }  if (readIf("DROP")) {
/*  759 */               prepared = parseDrop(); break;
/*  760 */             }  if (readIf("DECLARE")) {
/*      */               
/*  762 */               prepared = parseCreate(); break;
/*  763 */             }  if (this.database.getMode().getEnum() != Mode.ModeEnum.MSSQLServer && readIf("DEALLOCATE"))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  769 */               DeallocateProcedure deallocateProcedure = parseDeallocate();
/*      */             }
/*      */             break;
/*      */           case 69:
/*  773 */             if (readIf("EXPLAIN")) {
/*  774 */               Explain explain = parseExplain(); break;
/*  775 */             }  if (this.database.getMode().getEnum() != Mode.ModeEnum.MSSQLServer) {
/*  776 */               if (readIf("EXECUTE"))
/*  777 */                 prepared = parseExecutePostgre(); 
/*      */               break;
/*      */             } 
/*  780 */             if (readIf("EXEC") || readIf("EXECUTE")) {
/*  781 */               prepared = parseExecuteSQLServer();
/*      */             }
/*      */             break;
/*      */           
/*      */           case 71:
/*  786 */             if (readIf("GRANT")) {
/*  787 */               GrantRevoke grantRevoke = parseGrantRevoke(49);
/*      */             }
/*      */             break;
/*      */           case 72:
/*  791 */             if (readIf("HELP")) {
/*  792 */               prepared = parseHelp();
/*      */             }
/*      */             break;
/*      */           case 73:
/*  796 */             if (readIf("INSERT")) {
/*  797 */               Insert insert = parseInsert(i);
/*      */             }
/*      */             break;
/*      */           case 77:
/*  801 */             if (readIf("MERGE")) {
/*  802 */               prepared = parseMerge(i);
/*      */             }
/*      */             break;
/*      */           case 80:
/*  806 */             if (readIf("PREPARE")) {
/*  807 */               prepared = parsePrepare();
/*      */             }
/*      */             break;
/*      */           case 82:
/*  811 */             if (readIf("ROLLBACK")) {
/*  812 */               TransactionCommand transactionCommand = parseRollback(); break;
/*  813 */             }  if (readIf("REVOKE")) {
/*  814 */               GrantRevoke grantRevoke = parseGrantRevoke(50); break;
/*  815 */             }  if (readIf("RUNSCRIPT")) {
/*  816 */               RunScriptCommand runScriptCommand = parseRunScript(); break;
/*  817 */             }  if (readIf("RELEASE")) {
/*  818 */               prepared = parseReleaseSavepoint(); break;
/*  819 */             }  if ((this.database.getMode()).replaceInto && readIf("REPLACE")) {
/*  820 */               Merge merge = parseReplace(i);
/*      */             }
/*      */             break;
/*      */           case 83:
/*  824 */             if (readIf("SAVEPOINT")) {
/*  825 */               TransactionCommand transactionCommand = parseSavepoint(); break;
/*  826 */             }  if (readIf("SCRIPT")) {
/*  827 */               ScriptCommand scriptCommand = parseScript(); break;
/*  828 */             }  if (readIf("SHUTDOWN")) {
/*  829 */               TransactionCommand transactionCommand = parseShutdown(); break;
/*  830 */             }  if (readIf("SHOW")) {
/*  831 */               prepared = parseShow();
/*      */             }
/*      */             break;
/*      */           case 84:
/*  835 */             if (readIf("TRUNCATE")) {
/*  836 */               prepared = parseTruncate();
/*      */             }
/*      */             break;
/*      */           case 85:
/*  840 */             if (readIf("UPDATE")) {
/*  841 */               Update update = parseUpdate(i); break;
/*  842 */             }  if (readIf("USE"))
/*  843 */               prepared = parseUse(); 
/*      */             break;
/*      */         } 
/*      */         break;
/*      */     } 
/*  848 */     if (prepared == null) {
/*  849 */       throw getSyntaxError();
/*      */     }
/*  851 */     if (this.parameters != null) {
/*  852 */       byte b; int j; for (b = 0, j = this.parameters.size(); b < j; b++) {
/*  853 */         if (this.parameters.get(b) == null) {
/*  854 */           this.parameters.set(b, new Parameter(b));
/*      */         }
/*      */       } 
/*      */     } 
/*  858 */     boolean bool = readIf(111);
/*  859 */     if (bool)
/*      */       while (true) {
/*  861 */         int j = (int)readLong() - 1;
/*  862 */         if (j < 0 || j >= this.parameters.size()) {
/*  863 */           throw getSyntaxError();
/*      */         }
/*  865 */         parameter = this.parameters.get(j);
/*  866 */         if (parameter == null) {
/*  867 */           throw getSyntaxError();
/*      */         }
/*  869 */         read(116);
/*  870 */         Expression expression = readExpression();
/*  871 */         expression = expression.optimize(this.session);
/*  872 */         parameter.setValue(expression.getValue(this.session));
/*  873 */         if (!readIf(109)) {
/*  874 */           read(112);
/*  875 */           for (Parameter parameter : this.parameters) {
/*  876 */             parameter.checkSet();
/*      */           }
/*  878 */           this.parameters.clear(); break;
/*      */         } 
/*  880 */       }   if (bool || prepared.getSQL() == null) {
/*  881 */       setSQL(prepared, i);
/*      */     }
/*  883 */     return prepared;
/*      */   }
/*      */   
/*      */   private DbException getSyntaxError() {
/*  887 */     if (this.expectedList == null || this.expectedList.isEmpty()) {
/*  888 */       return DbException.getSyntaxError(this.sqlCommand, this.token.start());
/*      */     }
/*  890 */     return DbException.getSyntaxError(this.sqlCommand, this.token.start(), String.join(", ", (Iterable)this.expectedList));
/*      */   }
/*      */   
/*      */   private Prepared parseBackup() {
/*  894 */     BackupCommand backupCommand = new BackupCommand(this.session);
/*  895 */     read(76);
/*  896 */     backupCommand.setFileName(readExpression());
/*  897 */     return (Prepared)backupCommand;
/*      */   }
/*      */   
/*      */   private Prepared parseAnalyze() {
/*  901 */     Analyze analyze = new Analyze(this.session);
/*  902 */     if (readIf(75)) {
/*  903 */       Table table = readTableOrView();
/*  904 */       analyze.setTable(table);
/*      */     } 
/*  906 */     if (readIf("SAMPLE_SIZE")) {
/*  907 */       analyze.setTop(readNonNegativeInt());
/*      */     }
/*  909 */     return (Prepared)analyze;
/*      */   }
/*      */ 
/*      */   
/*      */   private TransactionCommand parseBegin() {
/*  914 */     if (!readIf("WORK")) {
/*  915 */       readIf("TRANSACTION");
/*      */     }
/*  917 */     return new TransactionCommand(this.session, 83);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private TransactionCommand parseCommit() {
/*  923 */     if (readIf("TRANSACTION")) {
/*  924 */       TransactionCommand transactionCommand1 = new TransactionCommand(this.session, 78);
/*  925 */       transactionCommand1.setTransactionName(readIdentifier());
/*  926 */       return transactionCommand1;
/*      */     } 
/*  928 */     TransactionCommand transactionCommand = new TransactionCommand(this.session, 71);
/*  929 */     readIf("WORK");
/*  930 */     return transactionCommand;
/*      */   }
/*      */   
/*      */   private TransactionCommand parseShutdown() {
/*  934 */     byte b = 80;
/*  935 */     if (readIf("IMMEDIATELY")) {
/*  936 */       b = 81;
/*  937 */     } else if (readIf("COMPACT")) {
/*  938 */       b = 82;
/*  939 */     } else if (readIf("DEFRAG")) {
/*  940 */       b = 84;
/*      */     } else {
/*  942 */       readIf("SCRIPT");
/*      */     } 
/*  944 */     return new TransactionCommand(this.session, b);
/*      */   }
/*      */   
/*      */   private TransactionCommand parseRollback() {
/*      */     TransactionCommand transactionCommand;
/*  949 */     if (readIf("TRANSACTION")) {
/*  950 */       transactionCommand = new TransactionCommand(this.session, 79);
/*  951 */       transactionCommand.setTransactionName(readIdentifier());
/*  952 */       return transactionCommand;
/*      */     } 
/*  954 */     readIf("WORK");
/*  955 */     if (readIf(76)) {
/*  956 */       read("SAVEPOINT");
/*  957 */       transactionCommand = new TransactionCommand(this.session, 75);
/*  958 */       transactionCommand.setSavepointName(readIdentifier());
/*      */     } else {
/*  960 */       transactionCommand = new TransactionCommand(this.session, 72);
/*      */     } 
/*  962 */     return transactionCommand;
/*      */   }
/*      */   
/*      */   private Prepared parsePrepare() {
/*  966 */     if (readIf("COMMIT")) {
/*  967 */       TransactionCommand transactionCommand = new TransactionCommand(this.session, 77);
/*  968 */       transactionCommand.setTransactionName(readIdentifier());
/*  969 */       return (Prepared)transactionCommand;
/*      */     } 
/*  971 */     return parsePrepareProcedure();
/*      */   }
/*      */   
/*      */   private Prepared parsePrepareProcedure() {
/*  975 */     if (this.database.getMode().getEnum() == Mode.ModeEnum.MSSQLServer) {
/*  976 */       throw getSyntaxError();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  983 */     String str = readIdentifier();
/*  984 */     if (readIf(105)) {
/*  985 */       ArrayList<Column> arrayList = Utils.newSmallArrayList();
/*  986 */       for (byte b = 0;; b++) {
/*  987 */         Column column = parseColumnForTable("C" + b, true);
/*  988 */         arrayList.add(column);
/*  989 */         if (!readIfMore()) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*  994 */     read(7);
/*  995 */     Prepared prepared = parsePrepared();
/*  996 */     PrepareProcedure prepareProcedure = new PrepareProcedure(this.session);
/*  997 */     prepareProcedure.setProcedureName(str);
/*  998 */     prepareProcedure.setPrepared(prepared);
/*  999 */     return (Prepared)prepareProcedure;
/*      */   }
/*      */   
/*      */   private TransactionCommand parseSavepoint() {
/* 1003 */     TransactionCommand transactionCommand = new TransactionCommand(this.session, 74);
/* 1004 */     transactionCommand.setSavepointName(readIdentifier());
/* 1005 */     return transactionCommand;
/*      */   }
/*      */   
/*      */   private Prepared parseReleaseSavepoint() {
/* 1009 */     NoOperation noOperation = new NoOperation(this.session);
/* 1010 */     readIf("SAVEPOINT");
/* 1011 */     readIdentifier();
/* 1012 */     return (Prepared)noOperation;
/*      */   }
/*      */   
/*      */   private Schema findSchema(String paramString) {
/* 1016 */     if (paramString == null) {
/* 1017 */       return null;
/*      */     }
/* 1019 */     Schema schema = this.database.findSchema(paramString);
/* 1020 */     if (schema == null && 
/* 1021 */       equalsToken("SESSION", paramString))
/*      */     {
/* 1023 */       schema = this.database.getSchema(this.session.getCurrentSchemaName());
/*      */     }
/*      */     
/* 1026 */     return schema;
/*      */   }
/*      */   
/*      */   private Schema getSchema(String paramString) {
/* 1030 */     if (paramString == null) {
/* 1031 */       return null;
/*      */     }
/* 1033 */     Schema schema = findSchema(paramString);
/* 1034 */     if (schema == null) {
/* 1035 */       throw DbException.get(90079, paramString);
/*      */     }
/* 1037 */     return schema;
/*      */   }
/*      */   
/*      */   private Schema getSchema() {
/* 1041 */     return getSchema(this.schemaName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Schema getSchemaWithDefault() {
/* 1064 */     if (this.schemaName == null) {
/* 1065 */       this.schemaName = this.session.getCurrentSchemaName();
/*      */     }
/* 1067 */     return getSchema(this.schemaName);
/*      */   }
/*      */   
/*      */   private Column readTableColumn(TableFilter paramTableFilter) {
/* 1071 */     String str = readIdentifier();
/* 1072 */     if (readIf(110)) {
/* 1073 */       str = readTableColumn(paramTableFilter, str);
/*      */     }
/* 1075 */     return paramTableFilter.getTable().getColumn(str);
/*      */   }
/*      */   
/*      */   private String readTableColumn(TableFilter paramTableFilter, String paramString) {
/* 1079 */     String str = readIdentifier();
/* 1080 */     if (readIf(110)) {
/* 1081 */       String str1 = paramString;
/* 1082 */       paramString = str;
/* 1083 */       str = readIdentifier();
/* 1084 */       if (readIf(110)) {
/* 1085 */         checkDatabaseName(str1);
/* 1086 */         str1 = paramString;
/* 1087 */         paramString = str;
/* 1088 */         str = readIdentifier();
/*      */       } 
/* 1090 */       if (!equalsToken(str1, paramTableFilter.getTable().getSchema().getName())) {
/* 1091 */         throw DbException.get(90079, str1);
/*      */       }
/*      */     } 
/* 1094 */     if (!equalsToken(paramString, paramTableFilter.getTableAlias())) {
/* 1095 */       throw DbException.get(42102, paramString);
/*      */     }
/* 1097 */     return str;
/*      */   }
/*      */   
/*      */   private Update parseUpdate(int paramInt) {
/* 1101 */     Update update = new Update(this.session);
/* 1102 */     this.currentPrepared = (Prepared)update;
/* 1103 */     Expression expression = null;
/* 1104 */     if ((this.database.getMode()).topInDML && readIf("TOP")) {
/* 1105 */       read(105);
/* 1106 */       expression = readTerm().optimize(this.session);
/* 1107 */       read(106);
/*      */     } 
/* 1109 */     TableFilter tableFilter = readSimpleTableFilter();
/* 1110 */     update.setTableFilter(tableFilter);
/* 1111 */     update.setSetClauseList(readUpdateSetClause(tableFilter));
/* 1112 */     if ((this.database.getMode()).allowUsingFromClauseInUpdateStatement && readIf(35)) {
/* 1113 */       TableFilter tableFilter1 = readTablePrimary();
/* 1114 */       update.setFromTableFilter(tableFilter1);
/*      */     } 
/* 1116 */     if (readIf(87)) {
/* 1117 */       update.setCondition(readExpression());
/*      */     }
/* 1119 */     if (expression == null) {
/*      */ 
/*      */       
/* 1122 */       readIfOrderBy();
/* 1123 */       expression = readFetchOrLimit();
/*      */     } 
/* 1125 */     update.setFetch(expression);
/* 1126 */     setSQL((Prepared)update, paramInt);
/* 1127 */     return update;
/*      */   }
/*      */   
/*      */   private SetClauseList readUpdateSetClause(TableFilter paramTableFilter) {
/* 1131 */     read(71);
/* 1132 */     SetClauseList setClauseList = new SetClauseList(paramTableFilter.getTable());
/*      */     while (true) {
/* 1134 */       if (readIf(105))
/* 1135 */       { ArrayList<Column> arrayList = Utils.newSmallArrayList();
/*      */         while (true) {
/* 1137 */           arrayList.add(readTableColumn(paramTableFilter));
/* 1138 */           if (!readIfMore())
/* 1139 */           { read(95);
/* 1140 */             setClauseList.addMultiple(arrayList, readExpression()); break; } 
/*      */         }  }
/* 1142 */       else { Column column = readTableColumn(paramTableFilter);
/* 1143 */         read(95);
/* 1144 */         setClauseList.addSingle(column, readExpressionOrDefault()); }
/*      */       
/* 1146 */       if (!readIf(109))
/* 1147 */         return setClauseList; 
/*      */     } 
/*      */   }
/*      */   private TableFilter readSimpleTableFilter() {
/* 1151 */     return new TableFilter(this.session, readTableOrView(), readFromAlias(null), this.rightsChecked, this.currentSelect, 0, null);
/*      */   }
/*      */   
/*      */   private Delete parseDelete(int paramInt) {
/* 1155 */     Delete delete = new Delete(this.session);
/* 1156 */     Expression expression = null;
/* 1157 */     if ((this.database.getMode()).topInDML && readIf("TOP")) {
/* 1158 */       expression = readTerm().optimize(this.session);
/*      */     }
/* 1160 */     this.currentPrepared = (Prepared)delete;
/* 1161 */     if (!readIf(35) && this.database.getMode().getEnum() == Mode.ModeEnum.MySQL) {
/* 1162 */       readIdentifierWithSchema();
/* 1163 */       read(35);
/*      */     } 
/* 1165 */     delete.setTableFilter(readSimpleTableFilter());
/* 1166 */     if (readIf(87)) {
/* 1167 */       delete.setCondition(readExpression());
/*      */     }
/* 1169 */     if (expression == null) {
/* 1170 */       expression = readFetchOrLimit();
/*      */     }
/* 1172 */     delete.setFetch(expression);
/* 1173 */     setSQL((Prepared)delete, paramInt);
/* 1174 */     return delete;
/*      */   }
/*      */   private Expression readFetchOrLimit() {
/*      */     Expression expression;
/* 1178 */     ValueExpression valueExpression = null;
/* 1179 */     if (readIf(32)) {
/* 1180 */       if (!readIf("FIRST")) {
/* 1181 */         read("NEXT");
/*      */       }
/* 1183 */       if (readIf(66) || readIf("ROWS")) {
/* 1184 */         valueExpression = ValueExpression.get((Value)ValueInteger.get(1));
/*      */       } else {
/* 1186 */         expression = readExpression().optimize(this.session);
/* 1187 */         if (!readIf(66)) {
/* 1188 */           read("ROWS");
/*      */         }
/*      */       } 
/* 1191 */       read("ONLY");
/* 1192 */     } else if ((this.database.getMode()).limit && readIf(50)) {
/* 1193 */       expression = readTerm().optimize(this.session);
/*      */     } 
/* 1195 */     return expression;
/*      */   }
/*      */   
/*      */   private IndexColumn[] parseIndexColumnList() {
/* 1199 */     ArrayList<IndexColumn> arrayList = Utils.newSmallArrayList();
/*      */     while (true) {
/* 1201 */       arrayList.add(new IndexColumn(readIdentifier(), parseSortType()));
/* 1202 */       if (!readIfMore())
/* 1203 */         return arrayList.<IndexColumn>toArray(new IndexColumn[0]); 
/*      */     } 
/*      */   }
/*      */   private int parseSortType() {
/* 1207 */     int i = (!readIf("ASC") && readIf("DESC")) ? 1 : 0;
/* 1208 */     if (readIf("NULLS")) {
/* 1209 */       if (readIf("FIRST")) {
/* 1210 */         i |= 0x2;
/*      */       } else {
/* 1212 */         read("LAST");
/* 1213 */         i |= 0x4;
/*      */       } 
/*      */     }
/* 1216 */     return i;
/*      */   }
/*      */   
/*      */   private String[] parseColumnList() {
/* 1220 */     ArrayList<String> arrayList = Utils.newSmallArrayList();
/*      */     while (true) {
/* 1222 */       arrayList.add(readIdentifier());
/* 1223 */       if (!readIfMore())
/* 1224 */         return arrayList.<String>toArray(new String[0]); 
/*      */     } 
/*      */   }
/*      */   private Column[] parseColumnList(Table paramTable) {
/* 1228 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/* 1229 */     HashSet<Column> hashSet = new HashSet();
/* 1230 */     if (!readIf(106)) {
/*      */       do {
/* 1232 */         Column column = parseColumn(paramTable);
/* 1233 */         if (!hashSet.add(column)) {
/* 1234 */           throw DbException.get(42121, column.getTraceSQL());
/*      */         }
/* 1236 */         arrayList.add(column);
/* 1237 */       } while (readIfMore());
/*      */     }
/* 1239 */     return arrayList.<Column>toArray(new Column[0]);
/*      */   }
/*      */   
/*      */   private Column parseColumn(Table paramTable) {
/* 1243 */     if (this.currentTokenType == 91) {
/* 1244 */       read();
/* 1245 */       return paramTable.getRowIdColumn();
/*      */     } 
/* 1247 */     return paramTable.getColumn(readIdentifier());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean readIfMore() {
/* 1256 */     if (readIf(109)) {
/* 1257 */       return true;
/*      */     }
/* 1259 */     read(106);
/* 1260 */     return false;
/*      */   }
/*      */   
/*      */   private Prepared parseHelp() {
/* 1264 */     HashSet<String> hashSet = new HashSet();
/* 1265 */     while (this.currentTokenType != 93) {
/* 1266 */       hashSet.add(StringUtils.toUpperEnglish(this.currentToken));
/* 1267 */       read();
/*      */     } 
/* 1269 */     return (Prepared)new Help(this.session, hashSet.<String>toArray(new String[0]));
/*      */   }
/*      */   
/*      */   private Prepared parseShow() {
/* 1273 */     ArrayList<Value> arrayList = Utils.newSmallArrayList();
/* 1274 */     StringBuilder stringBuilder = new StringBuilder("SELECT ");
/* 1275 */     if (readIf("CLIENT_ENCODING")) {
/*      */       
/* 1277 */       stringBuilder.append("'UNICODE' CLIENT_ENCODING");
/* 1278 */     } else if (readIf("DEFAULT_TRANSACTION_ISOLATION")) {
/*      */       
/* 1280 */       stringBuilder.append("'read committed' DEFAULT_TRANSACTION_ISOLATION");
/* 1281 */     } else if (readIf("TRANSACTION")) {
/*      */       
/* 1283 */       read("ISOLATION");
/* 1284 */       read("LEVEL");
/* 1285 */       stringBuilder.append("LOWER(ISOLATION_LEVEL) TRANSACTION_ISOLATION FROM INFORMATION_SCHEMA.SESSIONS WHERE SESSION_ID = SESSION_ID()");
/*      */     }
/* 1287 */     else if (readIf("DATESTYLE")) {
/*      */       
/* 1289 */       stringBuilder.append("'ISO' DATESTYLE");
/* 1290 */     } else if (readIf("SEARCH_PATH")) {
/*      */       
/* 1292 */       String[] arrayOfString = this.session.getSchemaSearchPath();
/* 1293 */       StringBuilder stringBuilder1 = new StringBuilder();
/* 1294 */       if (arrayOfString != null) {
/* 1295 */         for (byte b = 0; b < arrayOfString.length; b++) {
/* 1296 */           if (b > 0) {
/* 1297 */             stringBuilder1.append(", ");
/*      */           }
/* 1299 */           ParserUtil.quoteIdentifier(stringBuilder1, arrayOfString[b], 1);
/*      */         } 
/*      */       }
/* 1302 */       StringUtils.quoteStringSQL(stringBuilder, stringBuilder1.toString());
/* 1303 */       stringBuilder.append(" SEARCH_PATH");
/* 1304 */     } else if (readIf("SERVER_VERSION")) {
/*      */       
/* 1306 */       stringBuilder.append("'8.2.23' SERVER_VERSION");
/* 1307 */     } else if (readIf("SERVER_ENCODING")) {
/*      */       
/* 1309 */       stringBuilder.append("'UTF8' SERVER_ENCODING");
/* 1310 */     } else if (readIf("SSL")) {
/*      */       
/* 1312 */       stringBuilder.append("'off' SSL");
/* 1313 */     } else if (readIf("TABLES")) {
/*      */       
/* 1315 */       String str = this.database.getMainSchema().getName();
/* 1316 */       if (readIf(35)) {
/* 1317 */         str = readIdentifier();
/*      */       }
/* 1319 */       stringBuilder.append("TABLE_NAME, TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=? ORDER BY TABLE_NAME");
/*      */ 
/*      */       
/* 1322 */       arrayList.add(ValueVarchar.get(str));
/* 1323 */     } else if (readIf("COLUMNS")) {
/*      */       
/* 1325 */       read(35);
/* 1326 */       String str1 = readIdentifierWithSchema();
/* 1327 */       String str2 = getSchema().getName();
/* 1328 */       arrayList.add(ValueVarchar.get(str1));
/* 1329 */       if (readIf(35)) {
/* 1330 */         str2 = readIdentifier();
/*      */       }
/* 1332 */       stringBuilder.append("C.COLUMN_NAME FIELD, ");
/* 1333 */       boolean bool1 = this.session.isOldInformationSchema();
/* 1334 */       stringBuilder.append(bool1 ? "C.COLUMN_TYPE" : "DATA_TYPE_SQL(?2, ?1, 'TABLE', C.DTD_IDENTIFIER)");
/*      */ 
/*      */       
/* 1337 */       stringBuilder.append(" TYPE, C.IS_NULLABLE \"NULL\", CASE (SELECT MAX(I.INDEX_TYPE_NAME) FROM INFORMATION_SCHEMA.INDEXES I ");
/*      */ 
/*      */ 
/*      */       
/* 1341 */       if (!bool1) {
/* 1342 */         stringBuilder.append("JOIN INFORMATION_SCHEMA.INDEX_COLUMNS IC ");
/*      */       }
/* 1344 */       stringBuilder.append("WHERE I.TABLE_SCHEMA=C.TABLE_SCHEMA AND I.TABLE_NAME=C.TABLE_NAME ");
/*      */       
/* 1346 */       if (bool1) {
/* 1347 */         stringBuilder.append("AND I.COLUMN_NAME=C.COLUMN_NAME");
/*      */       } else {
/* 1349 */         stringBuilder.append("AND IC.TABLE_SCHEMA=C.TABLE_SCHEMA AND IC.TABLE_NAME=C.TABLE_NAME AND IC.INDEX_SCHEMA=I.INDEX_SCHEMA AND IC.INDEX_NAME=I.INDEX_NAME AND IC.COLUMN_NAME=C.COLUMN_NAME");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1355 */       stringBuilder.append(")WHEN 'PRIMARY KEY' THEN 'PRI' WHEN 'UNIQUE INDEX' THEN 'UNI' ELSE '' END `KEY`, COALESCE(COLUMN_DEFAULT, 'NULL') `DEFAULT` FROM INFORMATION_SCHEMA.COLUMNS C WHERE C.TABLE_NAME=?1 AND C.TABLE_SCHEMA=?2 ORDER BY C.ORDINAL_POSITION");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1362 */       arrayList.add(ValueVarchar.get(str2));
/* 1363 */     } else if (readIf("DATABASES") || readIf("SCHEMAS")) {
/*      */       
/* 1365 */       stringBuilder.append("SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA");
/* 1366 */     } else if (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && readIf("ALL")) {
/*      */       
/* 1368 */       stringBuilder.append("NAME, SETTING FROM PG_CATALOG.PG_SETTINGS");
/*      */     } 
/* 1370 */     boolean bool = this.session.getAllowLiterals();
/*      */ 
/*      */     
/*      */     try {
/* 1374 */       this.session.setAllowLiterals(true);
/* 1375 */       return prepare(this.session, stringBuilder.toString(), arrayList);
/*      */     } finally {
/* 1377 */       this.session.setAllowLiterals(bool);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private static Prepared prepare(SessionLocal paramSessionLocal, String paramString, ArrayList<Value> paramArrayList) {
/* 1383 */     Prepared prepared = paramSessionLocal.prepare(paramString);
/* 1384 */     ArrayList<Parameter> arrayList = prepared.getParameters();
/* 1385 */     if (arrayList != null) {
/* 1386 */       byte b; int i; for (b = 0, i = arrayList.size(); b < i; b++) {
/* 1387 */         Parameter parameter = arrayList.get(b);
/* 1388 */         parameter.setValue(paramArrayList.get(b));
/*      */       } 
/*      */     } 
/* 1391 */     return prepared;
/*      */   }
/*      */   
/*      */   private boolean isDerivedTable() {
/* 1395 */     int i = this.tokenIndex;
/* 1396 */     byte b = 0;
/* 1397 */     while (((Token)this.tokens.get(i)).tokenType() == 105) {
/* 1398 */       b++;
/* 1399 */       i++;
/*      */     } 
/* 1401 */     boolean bool = isDirectQuery(i);
/* 1402 */     if (bool && b > 0) {
/* 1403 */       i = scanToCloseParen(i + 1);
/* 1404 */       if (i < 0) {
/* 1405 */         bool = false;
/*      */       } else {
/*      */         
/*      */         while (true) {
/* 1409 */           switch (((Token)this.tokens.get(i)).tokenType()) {
/*      */             case 93:
/*      */             case 115:
/* 1412 */               bool = false;
/*      */               break;
/*      */             case 105:
/* 1415 */               i = scanToCloseParen(i + 1);
/* 1416 */               if (i < 0) {
/* 1417 */                 bool = false;
/*      */                 break;
/*      */               } 
/*      */               continue;
/*      */             case 106:
/* 1422 */               if (--b == 0) {
/*      */                 break;
/*      */               }
/* 1425 */               i++;
/*      */               continue;
/*      */             case 46:
/* 1428 */               bool = false;
/*      */               break;
/*      */           } 
/* 1431 */           i++;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1435 */     return bool;
/*      */   }
/*      */   
/*      */   private boolean isQuery() {
/* 1439 */     int i = this.tokenIndex;
/* 1440 */     byte b = 0;
/* 1441 */     while (((Token)this.tokens.get(i)).tokenType() == 105) {
/* 1442 */       b++;
/* 1443 */       i++;
/*      */     } 
/* 1445 */     boolean bool = isDirectQuery(i);
/* 1446 */     if (bool && b > 0) {
/* 1447 */       i++;
/*      */       do {
/* 1449 */         i = scanToCloseParen(i);
/* 1450 */         if (i < 0) {
/* 1451 */           bool = false;
/*      */           break;
/*      */         } 
/* 1454 */         switch (((Token)this.tokens.get(i)).tokenType()) {
/*      */           default:
/* 1456 */             bool = false; break;
/*      */           case 29:
/*      */           case 32:
/*      */           case 43:
/*      */           case 50:
/*      */           case 53:
/*      */           case 59:
/*      */           case 62:
/*      */           case 79:
/*      */           case 93:
/*      */           case 106:
/*      */           case 115:
/*      */             break;
/*      */         } 
/* 1470 */       } while (--b > 0);
/*      */     } 
/* 1472 */     return bool;
/*      */   }
/*      */   
/*      */   private int scanToCloseParen(int paramInt) {
/* 1476 */     byte b = 0; while (true) {
/* 1477 */       switch (((Token)this.tokens.get(paramInt)).tokenType()) {
/*      */         case 93:
/*      */         case 115:
/* 1480 */           return -1;
/*      */         case 105:
/* 1482 */           b++;
/*      */           break;
/*      */         case 106:
/* 1485 */           if (--b < 0)
/* 1486 */             return paramInt + 1; 
/*      */           break;
/*      */       } 
/* 1489 */       paramInt++;
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isQueryQuick() {
/* 1494 */     int i = this.tokenIndex;
/* 1495 */     while (((Token)this.tokens.get(i)).tokenType() == 105) {
/* 1496 */       i++;
/*      */     }
/* 1498 */     return isDirectQuery(i);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isDirectQuery(int paramInt) {
/* 1503 */     switch (((Token)this.tokens.get(paramInt)).tokenType())
/*      */     { case 69:
/*      */       case 85:
/*      */       case 89:
/* 1507 */         bool = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1515 */         return bool;case 75: bool = (((Token)this.tokens.get(paramInt + 1)).tokenType() != 105) ? true : false; return bool; }  boolean bool = false; return bool;
/*      */   }
/*      */   
/*      */   private Prepared parseMerge(int paramInt) {
/* 1519 */     read("INTO");
/* 1520 */     TableFilter tableFilter = readSimpleTableFilter();
/* 1521 */     if (readIf(83)) {
/* 1522 */       return (Prepared)parseMergeUsing(tableFilter, paramInt);
/*      */     }
/* 1524 */     return parseMergeInto(tableFilter, paramInt);
/*      */   }
/*      */   
/*      */   private Prepared parseMergeInto(TableFilter paramTableFilter, int paramInt) {
/* 1528 */     Merge merge = new Merge(this.session, false);
/* 1529 */     this.currentPrepared = (Prepared)merge;
/* 1530 */     merge.setTable(paramTableFilter.getTable());
/* 1531 */     Table table = merge.getTable();
/* 1532 */     if (readIf(105)) {
/* 1533 */       if (isQueryQuick()) {
/* 1534 */         merge.setQuery(parseQuery());
/* 1535 */         read(106);
/* 1536 */         return (Prepared)merge;
/*      */       } 
/* 1538 */       merge.setColumns(parseColumnList(table));
/*      */     } 
/* 1540 */     if (readIf(47)) {
/* 1541 */       read(105);
/* 1542 */       merge.setKeys(parseColumnList(table));
/*      */     } 
/* 1544 */     if (readIf(85)) {
/* 1545 */       parseValuesForCommand((CommandWithValues)merge);
/*      */     } else {
/* 1547 */       merge.setQuery(parseQuery());
/*      */     } 
/* 1549 */     setSQL((Prepared)merge, paramInt);
/* 1550 */     return (Prepared)merge;
/*      */   }
/*      */   
/*      */   private MergeUsing parseMergeUsing(TableFilter paramTableFilter, int paramInt) {
/* 1554 */     MergeUsing mergeUsing = new MergeUsing(this.session, paramTableFilter);
/* 1555 */     this.currentPrepared = (Prepared)mergeUsing;
/* 1556 */     mergeUsing.setSourceTableFilter(readTableReference());
/* 1557 */     read(60);
/* 1558 */     Expression expression = readExpression();
/* 1559 */     mergeUsing.setOnCondition(expression);
/*      */     
/* 1561 */     read(86);
/*      */     do {
/* 1563 */       boolean bool = readIf("MATCHED");
/* 1564 */       if (bool) {
/* 1565 */         parseWhenMatched(mergeUsing);
/*      */       } else {
/* 1567 */         parseWhenNotMatched(mergeUsing);
/*      */       } 
/* 1569 */     } while (readIf(86));
/*      */     
/* 1571 */     setSQL((Prepared)mergeUsing, paramInt);
/* 1572 */     return mergeUsing;
/*      */   }
/*      */   private void parseWhenMatched(MergeUsing paramMergeUsing) {
/*      */     MergeUsing.WhenMatchedThenDelete whenMatchedThenDelete;
/* 1576 */     Expression expression = readIf(4) ? readExpression() : null;
/* 1577 */     read("THEN");
/*      */     
/* 1579 */     if (readIf("UPDATE")) {
/* 1580 */       paramMergeUsing.getClass(); MergeUsing.WhenMatchedThenUpdate whenMatchedThenUpdate2 = new MergeUsing.WhenMatchedThenUpdate(paramMergeUsing);
/* 1581 */       whenMatchedThenUpdate2.setSetClauseList(readUpdateSetClause(paramMergeUsing.getTargetTableFilter()));
/* 1582 */       MergeUsing.WhenMatchedThenUpdate whenMatchedThenUpdate1 = whenMatchedThenUpdate2;
/*      */     } else {
/* 1584 */       read("DELETE");
/* 1585 */       paramMergeUsing.getClass(); whenMatchedThenDelete = new MergeUsing.WhenMatchedThenDelete(paramMergeUsing);
/*      */     } 
/* 1587 */     if (expression == null && (this.database.getMode()).mergeWhere && readIf(87)) {
/* 1588 */       expression = readExpression();
/*      */     }
/* 1590 */     whenMatchedThenDelete.setAndCondition(expression);
/* 1591 */     paramMergeUsing.addWhen((MergeUsing.When)whenMatchedThenDelete);
/*      */   }
/*      */   
/*      */   private void parseWhenNotMatched(MergeUsing paramMergeUsing) {
/* 1595 */     read(57);
/* 1596 */     read("MATCHED");
/* 1597 */     Expression expression = readIf(4) ? readExpression() : null;
/* 1598 */     read("THEN");
/* 1599 */     read("INSERT");
/* 1600 */     Column[] arrayOfColumn = readIf(105) ? parseColumnList(paramMergeUsing.getTargetTableFilter().getTable()) : null;
/* 1601 */     Boolean bool = readIfOverriding();
/* 1602 */     read(85);
/* 1603 */     read(105);
/* 1604 */     ArrayList<Expression> arrayList = Utils.newSmallArrayList();
/* 1605 */     if (!readIf(106)) {
/*      */       do {
/* 1607 */         arrayList.add(readExpressionOrDefault());
/* 1608 */       } while (readIfMore());
/*      */     }
/* 1610 */     paramMergeUsing.getClass();
/* 1611 */     MergeUsing.WhenNotMatched whenNotMatched = new MergeUsing.WhenNotMatched(paramMergeUsing, arrayOfColumn, bool, arrayList.<Expression>toArray(new Expression[0]));
/* 1612 */     whenNotMatched.setAndCondition(expression);
/* 1613 */     paramMergeUsing.addWhen((MergeUsing.When)whenNotMatched);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Insert parseInsert(int paramInt) {
/*      */     // Byte code:
/*      */     //   0: new org/h2/command/dml/Insert
/*      */     //   3: dup
/*      */     //   4: aload_0
/*      */     //   5: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   8: invokespecial <init> : (Lorg/h2/engine/SessionLocal;)V
/*      */     //   11: astore_2
/*      */     //   12: aload_0
/*      */     //   13: aload_2
/*      */     //   14: putfield currentPrepared : Lorg/h2/command/Prepared;
/*      */     //   17: aload_0
/*      */     //   18: getfield database : Lorg/h2/engine/Database;
/*      */     //   21: invokevirtual getMode : ()Lorg/h2/engine/Mode;
/*      */     //   24: astore_3
/*      */     //   25: aload_3
/*      */     //   26: getfield onDuplicateKeyUpdate : Z
/*      */     //   29: ifeq -> 47
/*      */     //   32: aload_0
/*      */     //   33: ldc_w 'IGNORE'
/*      */     //   36: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   39: ifeq -> 47
/*      */     //   42: aload_2
/*      */     //   43: iconst_1
/*      */     //   44: invokevirtual setIgnore : (Z)V
/*      */     //   47: aload_0
/*      */     //   48: ldc_w 'INTO'
/*      */     //   51: invokespecial read : (Ljava/lang/String;)V
/*      */     //   54: aload_0
/*      */     //   55: invokespecial readTableOrView : ()Lorg/h2/table/Table;
/*      */     //   58: astore #4
/*      */     //   60: aload_2
/*      */     //   61: aload #4
/*      */     //   63: invokevirtual setTable : (Lorg/h2/table/Table;)V
/*      */     //   66: aconst_null
/*      */     //   67: astore #5
/*      */     //   69: aload_0
/*      */     //   70: bipush #105
/*      */     //   72: invokespecial readIf : (I)Z
/*      */     //   75: ifeq -> 115
/*      */     //   78: aload_0
/*      */     //   79: invokespecial isQueryQuick : ()Z
/*      */     //   82: ifeq -> 101
/*      */     //   85: aload_2
/*      */     //   86: aload_0
/*      */     //   87: invokespecial parseQuery : ()Lorg/h2/command/query/Query;
/*      */     //   90: invokevirtual setQuery : (Lorg/h2/command/query/Query;)V
/*      */     //   93: aload_0
/*      */     //   94: bipush #106
/*      */     //   96: invokespecial read : (I)V
/*      */     //   99: aload_2
/*      */     //   100: areturn
/*      */     //   101: aload_0
/*      */     //   102: aload #4
/*      */     //   104: invokespecial parseColumnList : (Lorg/h2/table/Table;)[Lorg/h2/table/Column;
/*      */     //   107: astore #5
/*      */     //   109: aload_2
/*      */     //   110: aload #5
/*      */     //   112: invokevirtual setColumns : ([Lorg/h2/table/Column;)V
/*      */     //   115: aload_0
/*      */     //   116: invokespecial readIfOverriding : ()Ljava/lang/Boolean;
/*      */     //   119: astore #6
/*      */     //   121: aload_2
/*      */     //   122: aload #6
/*      */     //   124: invokevirtual setOverridingSystem : (Ljava/lang/Boolean;)V
/*      */     //   127: iconst_0
/*      */     //   128: istore #7
/*      */     //   130: aload_0
/*      */     //   131: ldc_w 'DIRECT'
/*      */     //   134: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   137: ifeq -> 148
/*      */     //   140: iconst_1
/*      */     //   141: istore #7
/*      */     //   143: aload_2
/*      */     //   144: iconst_1
/*      */     //   145: invokevirtual setInsertFromSelect : (Z)V
/*      */     //   148: aload_0
/*      */     //   149: ldc_w 'SORTED'
/*      */     //   152: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   155: ifeq -> 161
/*      */     //   158: iconst_1
/*      */     //   159: istore #7
/*      */     //   161: iload #7
/*      */     //   163: ifne -> 235
/*      */     //   166: aload #6
/*      */     //   168: ifnonnull -> 197
/*      */     //   171: aload_0
/*      */     //   172: bipush #25
/*      */     //   174: invokespecial readIf : (I)Z
/*      */     //   177: ifeq -> 197
/*      */     //   180: aload_0
/*      */     //   181: bipush #85
/*      */     //   183: invokespecial read : (I)V
/*      */     //   186: aload_2
/*      */     //   187: iconst_0
/*      */     //   188: anewarray org/h2/expression/Expression
/*      */     //   191: invokevirtual addRow : ([Lorg/h2/expression/Expression;)V
/*      */     //   194: goto -> 243
/*      */     //   197: aload_0
/*      */     //   198: bipush #85
/*      */     //   200: invokespecial readIf : (I)Z
/*      */     //   203: ifeq -> 214
/*      */     //   206: aload_0
/*      */     //   207: aload_2
/*      */     //   208: invokespecial parseValuesForCommand : (Lorg/h2/command/dml/CommandWithValues;)V
/*      */     //   211: goto -> 243
/*      */     //   214: aload_0
/*      */     //   215: bipush #71
/*      */     //   217: invokespecial readIf : (I)Z
/*      */     //   220: ifeq -> 235
/*      */     //   223: aload_0
/*      */     //   224: aload_2
/*      */     //   225: aload #4
/*      */     //   227: aload #5
/*      */     //   229: invokespecial parseInsertSet : (Lorg/h2/command/dml/Insert;Lorg/h2/table/Table;[Lorg/h2/table/Column;)V
/*      */     //   232: goto -> 243
/*      */     //   235: aload_2
/*      */     //   236: aload_0
/*      */     //   237: invokespecial parseQuery : ()Lorg/h2/command/query/Query;
/*      */     //   240: invokevirtual setQuery : (Lorg/h2/command/query/Query;)V
/*      */     //   243: aload_3
/*      */     //   244: getfield onDuplicateKeyUpdate : Z
/*      */     //   247: ifne -> 264
/*      */     //   250: aload_3
/*      */     //   251: getfield insertOnConflict : Z
/*      */     //   254: ifne -> 264
/*      */     //   257: aload_3
/*      */     //   258: getfield isolationLevelInSelectOrInsertStatement : Z
/*      */     //   261: ifeq -> 272
/*      */     //   264: aload_0
/*      */     //   265: aload_2
/*      */     //   266: aload #4
/*      */     //   268: aload_3
/*      */     //   269: invokespecial parseInsertCompatibility : (Lorg/h2/command/dml/Insert;Lorg/h2/table/Table;Lorg/h2/engine/Mode;)V
/*      */     //   272: aload_0
/*      */     //   273: aload_2
/*      */     //   274: iload_1
/*      */     //   275: invokespecial setSQL : (Lorg/h2/command/Prepared;I)V
/*      */     //   278: aload_2
/*      */     //   279: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #1617	-> 0
/*      */     //   #1618	-> 12
/*      */     //   #1619	-> 17
/*      */     //   #1620	-> 25
/*      */     //   #1621	-> 42
/*      */     //   #1623	-> 47
/*      */     //   #1624	-> 54
/*      */     //   #1625	-> 60
/*      */     //   #1626	-> 66
/*      */     //   #1627	-> 69
/*      */     //   #1628	-> 78
/*      */     //   #1629	-> 85
/*      */     //   #1630	-> 93
/*      */     //   #1631	-> 99
/*      */     //   #1633	-> 101
/*      */     //   #1634	-> 109
/*      */     //   #1636	-> 115
/*      */     //   #1637	-> 121
/*      */     //   #1638	-> 127
/*      */     //   #1639	-> 130
/*      */     //   #1640	-> 140
/*      */     //   #1641	-> 143
/*      */     //   #1643	-> 148
/*      */     //   #1644	-> 158
/*      */     //   #1647	-> 161
/*      */     //   #1648	-> 166
/*      */     //   #1649	-> 180
/*      */     //   #1650	-> 186
/*      */     //   #1651	-> 194
/*      */     //   #1653	-> 197
/*      */     //   #1654	-> 206
/*      */     //   #1655	-> 211
/*      */     //   #1657	-> 214
/*      */     //   #1658	-> 223
/*      */     //   #1659	-> 232
/*      */     //   #1662	-> 235
/*      */     //   #1664	-> 243
/*      */     //   #1665	-> 264
/*      */     //   #1667	-> 272
/*      */     //   #1668	-> 278
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Boolean readIfOverriding() {
/* 1672 */     Boolean bool = null;
/* 1673 */     if (readIf("OVERRIDING")) {
/* 1674 */       if (readIf(82)) {
/* 1675 */         bool = Boolean.FALSE;
/*      */       } else {
/* 1677 */         read("SYSTEM");
/* 1678 */         bool = Boolean.TRUE;
/*      */       } 
/* 1680 */       read(84);
/*      */     } 
/* 1682 */     return bool;
/*      */   }
/*      */   
/*      */   private void parseInsertSet(Insert paramInsert, Table paramTable, Column[] paramArrayOfColumn) {
/* 1686 */     if (paramArrayOfColumn != null) {
/* 1687 */       throw getSyntaxError();
/*      */     }
/* 1689 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/* 1690 */     ArrayList<Expression> arrayList1 = Utils.newSmallArrayList();
/*      */     while (true) {
/* 1692 */       arrayList.add(parseColumn(paramTable));
/* 1693 */       read(95);
/* 1694 */       arrayList1.add(readExpressionOrDefault());
/* 1695 */       if (!readIf(109)) {
/* 1696 */         paramInsert.setColumns(arrayList.<Column>toArray(new Column[0]));
/* 1697 */         paramInsert.addRow(arrayList1.<Expression>toArray(new Expression[0]));
/*      */         return;
/*      */       } 
/*      */     }  } private void parseInsertCompatibility(Insert paramInsert, Table paramTable, Mode paramMode) {
/* 1701 */     if (paramMode.onDuplicateKeyUpdate && 
/* 1702 */       readIf(60)) {
/* 1703 */       read("DUPLICATE");
/* 1704 */       read(47);
/* 1705 */       read("UPDATE");
/*      */       do {
/* 1707 */         String str = readIdentifier();
/* 1708 */         if (readIf(110)) {
/* 1709 */           String str1 = str;
/* 1710 */           String str2 = readIdentifier();
/* 1711 */           if (readIf(110)) {
/* 1712 */             if (!paramTable.getSchema().getName().equals(str1)) {
/* 1713 */               throw DbException.get(90080);
/*      */             }
/* 1715 */             str = readIdentifier();
/*      */           } else {
/* 1717 */             str = str2;
/* 1718 */             str2 = str1;
/*      */           } 
/* 1720 */           if (!paramTable.getName().equals(str2)) {
/* 1721 */             throw DbException.get(42102, str2);
/*      */           }
/*      */         } 
/* 1724 */         Column column = paramTable.getColumn(str);
/* 1725 */         read(95);
/* 1726 */         paramInsert.addAssignmentForDuplicate(column, readExpressionOrDefault());
/* 1727 */       } while (readIf(109));
/*      */     } 
/*      */     
/* 1730 */     if (paramMode.insertOnConflict && 
/* 1731 */       readIf(60)) {
/* 1732 */       read("CONFLICT");
/* 1733 */       read("DO");
/* 1734 */       read("NOTHING");
/* 1735 */       paramInsert.setIgnore(true);
/*      */     } 
/*      */     
/* 1738 */     if (paramMode.isolationLevelInSelectOrInsertStatement) {
/* 1739 */       parseIsolationClause();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Merge parseReplace(int paramInt) {
/* 1747 */     Merge merge = new Merge(this.session, true);
/* 1748 */     this.currentPrepared = (Prepared)merge;
/* 1749 */     read("INTO");
/* 1750 */     Table table = readTableOrView();
/* 1751 */     merge.setTable(table);
/* 1752 */     if (readIf(105)) {
/* 1753 */       if (isQueryQuick()) {
/* 1754 */         merge.setQuery(parseQuery());
/* 1755 */         read(106);
/* 1756 */         return merge;
/*      */       } 
/* 1758 */       merge.setColumns(parseColumnList(table));
/*      */     } 
/* 1760 */     if (readIf(85)) {
/* 1761 */       parseValuesForCommand((CommandWithValues)merge);
/*      */     } else {
/* 1763 */       merge.setQuery(parseQuery());
/*      */     } 
/* 1765 */     setSQL((Prepared)merge, paramInt);
/* 1766 */     return merge;
/*      */   }
/*      */   
/*      */   private void parseValuesForCommand(CommandWithValues paramCommandWithValues) {
/* 1770 */     ArrayList<Expression> arrayList = Utils.newSmallArrayList(); do {
/*      */       boolean bool;
/* 1772 */       arrayList.clear();
/*      */       
/* 1774 */       if (readIf(66)) {
/* 1775 */         read(105);
/* 1776 */         bool = true;
/*      */       } else {
/* 1778 */         bool = readIf(105);
/*      */       } 
/* 1780 */       if (bool) {
/* 1781 */         if (!readIf(106)) {
/*      */           do {
/* 1783 */             arrayList.add(readExpressionOrDefault());
/* 1784 */           } while (readIfMore());
/*      */         }
/*      */       } else {
/* 1787 */         arrayList.add(readExpressionOrDefault());
/*      */       } 
/* 1789 */       paramCommandWithValues.addRow(arrayList.<Expression>toArray(new Expression[0]));
/* 1790 */     } while (readIf(109));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TableFilter readTablePrimary() {
/*      */     // Byte code:
/*      */     //   0: aconst_null
/*      */     //   1: astore_2
/*      */     //   2: aload_0
/*      */     //   3: bipush #105
/*      */     //   5: invokespecial readIf : (I)Z
/*      */     //   8: ifeq -> 40
/*      */     //   11: aload_0
/*      */     //   12: invokespecial isDerivedTable : ()Z
/*      */     //   15: ifeq -> 23
/*      */     //   18: aload_0
/*      */     //   19: invokespecial readDerivedTableWithCorrelation : ()Lorg/h2/table/TableFilter;
/*      */     //   22: areturn
/*      */     //   23: aload_0
/*      */     //   24: invokespecial readTableReference : ()Lorg/h2/table/TableFilter;
/*      */     //   27: astore_3
/*      */     //   28: aload_0
/*      */     //   29: bipush #106
/*      */     //   31: invokespecial read : (I)V
/*      */     //   34: aload_0
/*      */     //   35: aload_3
/*      */     //   36: invokespecial readCorrelation : (Lorg/h2/table/TableFilter;)Lorg/h2/table/TableFilter;
/*      */     //   39: areturn
/*      */     //   40: aload_0
/*      */     //   41: bipush #85
/*      */     //   43: invokespecial readIf : (I)Z
/*      */     //   46: ifeq -> 96
/*      */     //   49: aload_0
/*      */     //   50: invokespecial parseValues : ()Lorg/h2/command/query/TableValueConstructor;
/*      */     //   53: astore_3
/*      */     //   54: aload_0
/*      */     //   55: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   58: aload_0
/*      */     //   59: getfield sqlCommand : Ljava/lang/String;
/*      */     //   62: invokevirtual getNextSystemIdentifier : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   65: astore_2
/*      */     //   66: aload_3
/*      */     //   67: aload_2
/*      */     //   68: aconst_null
/*      */     //   69: aload_0
/*      */     //   70: getfield parameters : Ljava/util/ArrayList;
/*      */     //   73: aload_0
/*      */     //   74: getfield createView : Lorg/h2/command/ddl/CreateView;
/*      */     //   77: ifnull -> 84
/*      */     //   80: iconst_1
/*      */     //   81: goto -> 85
/*      */     //   84: iconst_0
/*      */     //   85: aload_0
/*      */     //   86: getfield currentSelect : Lorg/h2/command/query/Select;
/*      */     //   89: invokevirtual toTable : (Ljava/lang/String;[Lorg/h2/table/Column;Ljava/util/ArrayList;ZLorg/h2/command/query/Query;)Lorg/h2/table/Table;
/*      */     //   92: astore_1
/*      */     //   93: goto -> 465
/*      */     //   96: aload_0
/*      */     //   97: bipush #75
/*      */     //   99: invokespecial readIf : (I)Z
/*      */     //   102: ifeq -> 140
/*      */     //   105: aload_0
/*      */     //   106: bipush #105
/*      */     //   108: invokespecial read : (I)V
/*      */     //   111: aload_0
/*      */     //   112: iconst_1
/*      */     //   113: invokespecial readTableFunction : (I)Lorg/h2/expression/function/table/ArrayTableFunction;
/*      */     //   116: astore_3
/*      */     //   117: new org/h2/table/FunctionTable
/*      */     //   120: dup
/*      */     //   121: aload_0
/*      */     //   122: getfield database : Lorg/h2/engine/Database;
/*      */     //   125: invokevirtual getMainSchema : ()Lorg/h2/schema/Schema;
/*      */     //   128: aload_0
/*      */     //   129: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   132: aload_3
/*      */     //   133: invokespecial <init> : (Lorg/h2/schema/Schema;Lorg/h2/engine/SessionLocal;Lorg/h2/expression/function/table/TableFunction;)V
/*      */     //   136: astore_1
/*      */     //   137: goto -> 465
/*      */     //   140: aload_0
/*      */     //   141: getfield token : Lorg/h2/command/Token;
/*      */     //   144: invokevirtual isQuoted : ()Z
/*      */     //   147: istore_3
/*      */     //   148: aload_0
/*      */     //   149: invokespecial readIdentifier : ()Ljava/lang/String;
/*      */     //   152: astore #4
/*      */     //   154: aload_0
/*      */     //   155: getfield tokenIndex : I
/*      */     //   158: istore #5
/*      */     //   160: aload_0
/*      */     //   161: aconst_null
/*      */     //   162: putfield schemaName : Ljava/lang/String;
/*      */     //   165: aload_0
/*      */     //   166: bipush #110
/*      */     //   168: invokespecial readIf : (I)Z
/*      */     //   171: ifeq -> 185
/*      */     //   174: aload_0
/*      */     //   175: aload #4
/*      */     //   177: invokespecial readIdentifierWithSchema2 : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   180: astore #4
/*      */     //   182: goto -> 214
/*      */     //   185: iload_3
/*      */     //   186: ifne -> 214
/*      */     //   189: aload_0
/*      */     //   190: bipush #75
/*      */     //   192: invokespecial readIf : (I)Z
/*      */     //   195: ifeq -> 214
/*      */     //   198: aload_0
/*      */     //   199: aload_0
/*      */     //   200: aload #4
/*      */     //   202: invokespecial upperName : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   205: iload #5
/*      */     //   207: invokespecial readDataChangeDeltaTable : (Ljava/lang/String;I)Lorg/h2/table/Table;
/*      */     //   210: astore_1
/*      */     //   211: goto -> 465
/*      */     //   214: aload_0
/*      */     //   215: getfield schemaName : Ljava/lang/String;
/*      */     //   218: ifnonnull -> 227
/*      */     //   221: aconst_null
/*      */     //   222: astore #6
/*      */     //   224: goto -> 276
/*      */     //   227: aload_0
/*      */     //   228: aload_0
/*      */     //   229: getfield schemaName : Ljava/lang/String;
/*      */     //   232: invokespecial findSchema : (Ljava/lang/String;)Lorg/h2/schema/Schema;
/*      */     //   235: astore #6
/*      */     //   237: aload #6
/*      */     //   239: ifnonnull -> 276
/*      */     //   242: aload_0
/*      */     //   243: aload #4
/*      */     //   245: invokespecial isDualTable : (Ljava/lang/String;)Z
/*      */     //   248: ifeq -> 266
/*      */     //   251: new org/h2/table/DualTable
/*      */     //   254: dup
/*      */     //   255: aload_0
/*      */     //   256: getfield database : Lorg/h2/engine/Database;
/*      */     //   259: invokespecial <init> : (Lorg/h2/engine/Database;)V
/*      */     //   262: astore_1
/*      */     //   263: goto -> 465
/*      */     //   266: ldc 90079
/*      */     //   268: aload_0
/*      */     //   269: getfield schemaName : Ljava/lang/String;
/*      */     //   272: invokestatic get : (ILjava/lang/String;)Lorg/h2/message/DbException;
/*      */     //   275: athrow
/*      */     //   276: aload_0
/*      */     //   277: bipush #105
/*      */     //   279: invokespecial readIf : (I)Z
/*      */     //   282: istore #7
/*      */     //   284: iload #7
/*      */     //   286: ifeq -> 314
/*      */     //   289: aload_0
/*      */     //   290: ldc_w 'INDEX'
/*      */     //   293: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   296: ifeq -> 314
/*      */     //   299: aload_0
/*      */     //   300: aconst_null
/*      */     //   301: invokespecial readIdentifierWithSchema : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   304: pop
/*      */     //   305: aload_0
/*      */     //   306: bipush #106
/*      */     //   308: invokespecial read : (I)V
/*      */     //   311: iconst_0
/*      */     //   312: istore #7
/*      */     //   314: iload #7
/*      */     //   316: ifeq -> 458
/*      */     //   319: aload_0
/*      */     //   320: getfield database : Lorg/h2/engine/Database;
/*      */     //   323: invokevirtual getMainSchema : ()Lorg/h2/schema/Schema;
/*      */     //   326: astore #8
/*      */     //   328: aload_0
/*      */     //   329: aload #4
/*      */     //   331: ldc_w 'SYSTEM_RANGE'
/*      */     //   334: invokespecial equalsToken : (Ljava/lang/String;Ljava/lang/String;)Z
/*      */     //   337: ifne -> 352
/*      */     //   340: aload_0
/*      */     //   341: aload #4
/*      */     //   343: ldc_w 'GENERATE_SERIES'
/*      */     //   346: invokespecial equalsToken : (Ljava/lang/String;Ljava/lang/String;)Z
/*      */     //   349: ifeq -> 433
/*      */     //   352: aload_0
/*      */     //   353: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   356: astore #9
/*      */     //   358: aload_0
/*      */     //   359: bipush #109
/*      */     //   361: invokespecial read : (I)V
/*      */     //   364: aload_0
/*      */     //   365: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   368: astore #10
/*      */     //   370: aload_0
/*      */     //   371: bipush #109
/*      */     //   373: invokespecial readIf : (I)Z
/*      */     //   376: ifeq -> 410
/*      */     //   379: aload_0
/*      */     //   380: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   383: astore #11
/*      */     //   385: aload_0
/*      */     //   386: bipush #106
/*      */     //   388: invokespecial read : (I)V
/*      */     //   391: new org/h2/table/RangeTable
/*      */     //   394: dup
/*      */     //   395: aload #8
/*      */     //   397: aload #9
/*      */     //   399: aload #10
/*      */     //   401: aload #11
/*      */     //   403: invokespecial <init> : (Lorg/h2/schema/Schema;Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;)V
/*      */     //   406: astore_1
/*      */     //   407: goto -> 430
/*      */     //   410: aload_0
/*      */     //   411: bipush #106
/*      */     //   413: invokespecial read : (I)V
/*      */     //   416: new org/h2/table/RangeTable
/*      */     //   419: dup
/*      */     //   420: aload #8
/*      */     //   422: aload #9
/*      */     //   424: aload #10
/*      */     //   426: invokespecial <init> : (Lorg/h2/schema/Schema;Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;)V
/*      */     //   429: astore_1
/*      */     //   430: goto -> 455
/*      */     //   433: new org/h2/table/FunctionTable
/*      */     //   436: dup
/*      */     //   437: aload #8
/*      */     //   439: aload_0
/*      */     //   440: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   443: aload_0
/*      */     //   444: aload #4
/*      */     //   446: aload #6
/*      */     //   448: invokespecial readTableFunction : (Ljava/lang/String;Lorg/h2/schema/Schema;)Lorg/h2/expression/function/table/TableFunction;
/*      */     //   451: invokespecial <init> : (Lorg/h2/schema/Schema;Lorg/h2/engine/SessionLocal;Lorg/h2/expression/function/table/TableFunction;)V
/*      */     //   454: astore_1
/*      */     //   455: goto -> 465
/*      */     //   458: aload_0
/*      */     //   459: aload #4
/*      */     //   461: invokespecial readTableOrView : (Ljava/lang/String;)Lorg/h2/table/Table;
/*      */     //   464: astore_1
/*      */     //   465: aconst_null
/*      */     //   466: astore_3
/*      */     //   467: aconst_null
/*      */     //   468: astore #4
/*      */     //   470: aload_0
/*      */     //   471: invokespecial readIfUseIndex : ()Z
/*      */     //   474: ifeq -> 487
/*      */     //   477: aload_0
/*      */     //   478: aload_1
/*      */     //   479: invokespecial parseIndexHints : (Lorg/h2/table/Table;)Lorg/h2/table/IndexHints;
/*      */     //   482: astore #4
/*      */     //   484: goto -> 516
/*      */     //   487: aload_0
/*      */     //   488: aload_2
/*      */     //   489: invokespecial readFromAlias : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   492: astore_2
/*      */     //   493: aload_2
/*      */     //   494: ifnull -> 516
/*      */     //   497: aload_0
/*      */     //   498: invokespecial readDerivedColumnNames : ()Ljava/util/ArrayList;
/*      */     //   501: astore_3
/*      */     //   502: aload_0
/*      */     //   503: invokespecial readIfUseIndex : ()Z
/*      */     //   506: ifeq -> 516
/*      */     //   509: aload_0
/*      */     //   510: aload_1
/*      */     //   511: invokespecial parseIndexHints : (Lorg/h2/table/Table;)Lorg/h2/table/IndexHints;
/*      */     //   514: astore #4
/*      */     //   516: aload_0
/*      */     //   517: aload_1
/*      */     //   518: aload_2
/*      */     //   519: aload_3
/*      */     //   520: aload #4
/*      */     //   522: invokespecial buildTableFilter : (Lorg/h2/table/Table;Ljava/lang/String;Ljava/util/ArrayList;Lorg/h2/table/IndexHints;)Lorg/h2/table/TableFilter;
/*      */     //   525: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #1795	-> 0
/*      */     //   #1796	-> 2
/*      */     //   #1797	-> 11
/*      */     //   #1799	-> 18
/*      */     //   #1802	-> 23
/*      */     //   #1803	-> 28
/*      */     //   #1804	-> 34
/*      */     //   #1806	-> 40
/*      */     //   #1807	-> 49
/*      */     //   #1808	-> 54
/*      */     //   #1809	-> 66
/*      */     //   #1810	-> 93
/*      */     //   #1812	-> 105
/*      */     //   #1813	-> 111
/*      */     //   #1814	-> 117
/*      */     //   #1815	-> 137
/*      */     //   #1816	-> 140
/*      */     //   #1817	-> 148
/*      */     //   #1818	-> 154
/*      */     //   #1819	-> 160
/*      */     //   #1820	-> 165
/*      */     //   #1821	-> 174
/*      */     //   #1822	-> 185
/*      */     //   #1823	-> 198
/*      */     //   #1824	-> 211
/*      */     //   #1827	-> 214
/*      */     //   #1828	-> 221
/*      */     //   #1830	-> 227
/*      */     //   #1831	-> 237
/*      */     //   #1832	-> 242
/*      */     //   #1833	-> 251
/*      */     //   #1834	-> 263
/*      */     //   #1836	-> 266
/*      */     //   #1839	-> 276
/*      */     //   #1840	-> 284
/*      */     //   #1843	-> 299
/*      */     //   #1844	-> 305
/*      */     //   #1845	-> 311
/*      */     //   #1847	-> 314
/*      */     //   #1848	-> 319
/*      */     //   #1849	-> 328
/*      */     //   #1850	-> 346
/*      */     //   #1851	-> 352
/*      */     //   #1852	-> 358
/*      */     //   #1853	-> 364
/*      */     //   #1854	-> 370
/*      */     //   #1855	-> 379
/*      */     //   #1856	-> 385
/*      */     //   #1857	-> 391
/*      */     //   #1858	-> 407
/*      */     //   #1859	-> 410
/*      */     //   #1860	-> 416
/*      */     //   #1862	-> 430
/*      */     //   #1863	-> 433
/*      */     //   #1865	-> 455
/*      */     //   #1866	-> 458
/*      */     //   #1869	-> 465
/*      */     //   #1870	-> 467
/*      */     //   #1871	-> 470
/*      */     //   #1872	-> 477
/*      */     //   #1874	-> 487
/*      */     //   #1875	-> 493
/*      */     //   #1876	-> 497
/*      */     //   #1877	-> 502
/*      */     //   #1878	-> 509
/*      */     //   #1882	-> 516
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TableFilter readCorrelation(TableFilter paramTableFilter) {
/* 1886 */     String str = readFromAlias(null);
/* 1887 */     if (str != null) {
/* 1888 */       paramTableFilter.setAlias(str);
/* 1889 */       ArrayList<String> arrayList = readDerivedColumnNames();
/* 1890 */       if (arrayList != null) {
/* 1891 */         paramTableFilter.setDerivedColumns(arrayList);
/*      */       }
/*      */     } 
/* 1894 */     return paramTableFilter;
/*      */   } private TableFilter readDerivedTableWithCorrelation() {
/*      */     Table table;
/*      */     String str;
/* 1898 */     Query query = parseQueryExpression();
/* 1899 */     read(106);
/*      */ 
/*      */     
/* 1902 */     ArrayList<String> arrayList = null;
/* 1903 */     IndexHints indexHints = null;
/* 1904 */     if (readIfUseIndex()) {
/* 1905 */       str = this.session.getNextSystemIdentifier(this.sqlCommand);
/* 1906 */       table = query.toTable(str, null, this.parameters, (this.createView != null), (Query)this.currentSelect);
/* 1907 */       indexHints = parseIndexHints(table);
/*      */     } else {
/* 1909 */       str = readFromAlias(null);
/* 1910 */       if (str != null) {
/* 1911 */         arrayList = readDerivedColumnNames();
/* 1912 */         Column[] arrayOfColumn = null;
/* 1913 */         if (arrayList != null) {
/* 1914 */           query.init();
/*      */ 
/*      */           
/* 1917 */           arrayOfColumn = (Column[])TableView.createQueryColumnTemplateList(arrayList.<String>toArray(new String[0]), query, new String[1]).toArray((Object[])new Column[0]);
/*      */         } 
/* 1919 */         table = query.toTable(str, arrayOfColumn, this.parameters, (this.createView != null), (Query)this.currentSelect);
/* 1920 */         if (readIfUseIndex()) {
/* 1921 */           indexHints = parseIndexHints(table);
/*      */         }
/*      */       } else {
/* 1924 */         str = this.session.getNextSystemIdentifier(this.sqlCommand);
/* 1925 */         table = query.toTable(str, null, this.parameters, (this.createView != null), (Query)this.currentSelect);
/*      */       } 
/*      */     } 
/* 1928 */     return buildTableFilter(table, str, arrayList, indexHints);
/*      */   }
/*      */ 
/*      */   
/*      */   private TableFilter buildTableFilter(Table paramTable, String paramString, ArrayList<String> paramArrayList, IndexHints paramIndexHints) {
/* 1933 */     if ((this.database.getMode()).discardWithTableHints) {
/* 1934 */       discardWithTableHints();
/*      */     }
/*      */     
/* 1937 */     if (paramString == null && paramTable.isView() && paramTable.isTableExpression()) {
/* 1938 */       paramString = paramTable.getName();
/*      */     }
/* 1940 */     TableFilter tableFilter = new TableFilter(this.session, paramTable, paramString, this.rightsChecked, this.currentSelect, this.orderInFrom++, paramIndexHints);
/*      */     
/* 1942 */     if (paramArrayList != null) {
/* 1943 */       tableFilter.setDerivedColumns(paramArrayList);
/*      */     }
/* 1945 */     return tableFilter;
/*      */   }
/*      */   private Table readDataChangeDeltaTable(String paramString, int paramInt) {
/*      */     Merge merge;
/* 1949 */     read(105);
/* 1950 */     int i = this.tokenIndex;
/*      */     
/* 1952 */     DataChangeDeltaTable.ResultOption resultOption = DataChangeDeltaTable.ResultOption.FINAL;
/* 1953 */     switch (paramString) {
/*      */       case "OLD":
/* 1955 */         resultOption = DataChangeDeltaTable.ResultOption.OLD;
/* 1956 */         if (readIf("UPDATE")) {
/* 1957 */           Update update = parseUpdate(i); break;
/* 1958 */         }  if (readIf("DELETE")) {
/* 1959 */           Delete delete = parseDelete(i); break;
/* 1960 */         }  if (readIf("MERGE")) {
/* 1961 */           DataChangeStatement dataChangeStatement = (DataChangeStatement)parseMerge(i); break;
/* 1962 */         }  if ((this.database.getMode()).replaceInto && readIf("REPLACE")) {
/* 1963 */           merge = parseReplace(i); break;
/*      */         } 
/* 1965 */         throw getSyntaxError();
/*      */ 
/*      */       
/*      */       case "NEW":
/* 1969 */         resultOption = DataChangeDeltaTable.ResultOption.NEW;
/*      */       
/*      */       case "FINAL":
/* 1972 */         if (readIf("INSERT")) {
/* 1973 */           Insert insert = parseInsert(i); break;
/* 1974 */         }  if (readIf("UPDATE")) {
/* 1975 */           Update update = parseUpdate(i); break;
/* 1976 */         }  if (readIf("MERGE")) {
/* 1977 */           DataChangeStatement dataChangeStatement = (DataChangeStatement)parseMerge(i); break;
/* 1978 */         }  if ((this.database.getMode()).replaceInto && readIf("REPLACE")) {
/* 1979 */           merge = parseReplace(i); break;
/*      */         } 
/* 1981 */         throw getSyntaxError();
/*      */ 
/*      */       
/*      */       default:
/* 1985 */         setTokenIndex(paramInt);
/* 1986 */         addExpected("OLD TABLE");
/* 1987 */         addExpected("NEW TABLE");
/* 1988 */         addExpected("FINAL TABLE");
/* 1989 */         throw getSyntaxError();
/*      */     } 
/* 1991 */     read(106);
/* 1992 */     if (this.currentSelect != null)
/*      */     {
/* 1994 */       this.currentSelect.setNeverLazy(true);
/*      */     }
/* 1996 */     return (Table)new DataChangeDeltaTable(getSchemaWithDefault(), this.session, (DataChangeStatement)merge, resultOption);
/*      */   }
/*      */   
/*      */   private TableFunction readTableFunction(String paramString, Schema paramSchema) {
/* 2000 */     if (paramSchema == null) {
/* 2001 */       switch (upperName(paramString)) {
/*      */         case "UNNEST":
/* 2003 */           return (TableFunction)readUnnestFunction();
/*      */         case "TABLE_DISTINCT":
/* 2005 */           return (TableFunction)readTableFunction(2);
/*      */         case "CSVREAD":
/* 2007 */           this.recompileAlways = true;
/* 2008 */           return (TableFunction)readParameters(new CSVReadFunction());
/*      */         case "LINK_SCHEMA":
/* 2010 */           this.recompileAlways = true;
/* 2011 */           return (TableFunction)readParameters(new LinkSchemaFunction());
/*      */       } 
/*      */     }
/* 2014 */     FunctionAlias functionAlias = getFunctionAliasWithinPath(paramString, paramSchema);
/* 2015 */     if (!functionAlias.isDeterministic()) {
/* 2016 */       this.recompileAlways = true;
/*      */     }
/* 2018 */     ArrayList<Expression> arrayList = Utils.newSmallArrayList();
/* 2019 */     if (!readIf(106)) {
/*      */       do {
/* 2021 */         arrayList.add(readExpression());
/* 2022 */       } while (readIfMore());
/*      */     }
/* 2024 */     return (TableFunction)new JavaTableFunction(functionAlias, arrayList.<Expression>toArray(new Expression[0]));
/*      */   }
/*      */   
/*      */   private boolean readIfUseIndex() {
/* 2028 */     int i = this.tokenIndex;
/* 2029 */     if (!readIf("USE")) {
/* 2030 */       return false;
/*      */     }
/* 2032 */     if (!readIf("INDEX")) {
/* 2033 */       setTokenIndex(i);
/* 2034 */       return false;
/*      */     } 
/* 2036 */     return true;
/*      */   }
/*      */   
/*      */   private IndexHints parseIndexHints(Table paramTable) {
/* 2040 */     read(105);
/* 2041 */     LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
/* 2042 */     if (!readIf(106)) {
/*      */       do {
/* 2044 */         String str = readIdentifierWithSchema();
/* 2045 */         Index index = paramTable.getIndex(str);
/* 2046 */         linkedHashSet.add(index.getName());
/* 2047 */       } while (readIfMore());
/*      */     }
/* 2049 */     return IndexHints.createUseIndexHints(linkedHashSet);
/*      */   }
/*      */   
/*      */   private String readFromAlias(String paramString) {
/* 2053 */     if (readIf(7) || isIdentifier()) {
/* 2054 */       paramString = readIdentifier();
/*      */     }
/* 2056 */     return paramString;
/*      */   }
/*      */   
/*      */   private ArrayList<String> readDerivedColumnNames() {
/* 2060 */     if (readIf(105)) {
/* 2061 */       ArrayList<String> arrayList = new ArrayList();
/*      */       while (true) {
/* 2063 */         arrayList.add(readIdentifier());
/* 2064 */         if (!readIfMore())
/* 2065 */           return arrayList; 
/*      */       } 
/* 2067 */     }  return null;
/*      */   }
/*      */   
/*      */   private void discardWithTableHints() {
/* 2071 */     if (readIf(89)) {
/* 2072 */       read(105);
/*      */       do {
/* 2074 */         discardTableHint();
/* 2075 */       } while (readIfMore());
/*      */     } 
/*      */   }
/*      */   
/*      */   private void discardTableHint() {
/* 2080 */     if (readIf("INDEX")) {
/* 2081 */       if (readIf(105)) {
/*      */         do {
/* 2083 */           readExpression();
/* 2084 */         } while (readIfMore());
/*      */       } else {
/* 2086 */         read(95);
/* 2087 */         readExpression();
/*      */       } 
/*      */     } else {
/* 2090 */       readExpression();
/*      */     } 
/*      */   }
/*      */   
/*      */   private Prepared parseTruncate() {
/* 2095 */     read(75);
/* 2096 */     Table table = readTableOrView();
/* 2097 */     boolean bool = (this.database.getMode()).truncateTableRestartIdentity;
/* 2098 */     if (readIf("CONTINUE")) {
/* 2099 */       read("IDENTITY");
/* 2100 */       bool = false;
/* 2101 */     } else if (readIf("RESTART")) {
/* 2102 */       read("IDENTITY");
/* 2103 */       bool = true;
/*      */     } 
/* 2105 */     TruncateTable truncateTable = new TruncateTable(this.session);
/* 2106 */     truncateTable.setTable(table);
/* 2107 */     truncateTable.setRestart(bool);
/* 2108 */     return (Prepared)truncateTable;
/*      */   }
/*      */   
/*      */   private boolean readIfExists(boolean paramBoolean) {
/* 2112 */     if (readIf(40)) {
/* 2113 */       read(30);
/* 2114 */       paramBoolean = true;
/*      */     } 
/* 2116 */     return paramBoolean;
/*      */   }
/*      */   private Prepared parseComment() {
/*      */     String str;
/* 2120 */     byte b = 0;
/* 2121 */     read(60);
/* 2122 */     boolean bool = false;
/* 2123 */     if (readIf(75) || readIf("VIEW")) {
/* 2124 */       b = 0;
/* 2125 */     } else if (readIf("COLUMN")) {
/* 2126 */       bool = true;
/* 2127 */       b = 0;
/* 2128 */     } else if (readIf("CONSTANT")) {
/* 2129 */       b = 11;
/* 2130 */     } else if (readIf(14)) {
/* 2131 */       b = 5;
/* 2132 */     } else if (readIf("ALIAS")) {
/* 2133 */       b = 9;
/* 2134 */     } else if (readIf("INDEX")) {
/* 2135 */       b = 1;
/* 2136 */     } else if (readIf("ROLE")) {
/* 2137 */       b = 7;
/* 2138 */     } else if (readIf("SCHEMA")) {
/* 2139 */       b = 10;
/* 2140 */     } else if (readIf("SEQUENCE")) {
/* 2141 */       b = 3;
/* 2142 */     } else if (readIf("TRIGGER")) {
/* 2143 */       b = 4;
/* 2144 */     } else if (readIf(82)) {
/* 2145 */       b = 2;
/* 2146 */     } else if (readIf("DOMAIN")) {
/* 2147 */       b = 12;
/*      */     } else {
/* 2149 */       throw getSyntaxError();
/*      */     } 
/* 2151 */     SetComment setComment = new SetComment(this.session);
/*      */     
/* 2153 */     if (bool) {
/*      */ 
/*      */       
/* 2156 */       str = readIdentifier();
/* 2157 */       String str1 = null;
/* 2158 */       read(110);
/* 2159 */       boolean bool1 = (this.database.getMode()).allowEmptySchemaValuesAsDefaultSchema;
/* 2160 */       String str2 = (bool1 && this.currentTokenType == 110) ? null : readIdentifier();
/* 2161 */       if (readIf(110)) {
/* 2162 */         str1 = str;
/* 2163 */         str = str2;
/* 2164 */         str2 = (bool1 && this.currentTokenType == 110) ? null : readIdentifier();
/* 2165 */         if (readIf(110)) {
/* 2166 */           checkDatabaseName(str1);
/* 2167 */           str1 = str;
/* 2168 */           str = str2;
/* 2169 */           str2 = readIdentifier();
/*      */         } 
/*      */       } 
/* 2172 */       if (str2 == null || str == null) {
/* 2173 */         throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "table.column");
/*      */       }
/* 2175 */       this.schemaName = (str1 != null) ? str1 : this.session.getCurrentSchemaName();
/* 2176 */       setComment.setColumn(true);
/* 2177 */       setComment.setColumnName(str2);
/*      */     } else {
/* 2179 */       str = readIdentifierWithSchema();
/*      */     } 
/* 2181 */     setComment.setSchemaName(this.schemaName);
/* 2182 */     setComment.setObjectName(str);
/* 2183 */     setComment.setObjectType(b);
/* 2184 */     read(45);
/* 2185 */     setComment.setCommentExpression(readExpression());
/* 2186 */     return (Prepared)setComment;
/*      */   }
/*      */   
/*      */   private Prepared parseDrop() {
/* 2190 */     if (readIf(75)) {
/* 2191 */       boolean bool = readIfExists(false);
/* 2192 */       DropTable dropTable = new DropTable(this.session);
/*      */       while (true)
/* 2194 */       { String str = readIdentifierWithSchema();
/* 2195 */         dropTable.addTable(getSchema(), str);
/* 2196 */         if (!readIf(109))
/* 2197 */         { bool = readIfExists(bool);
/* 2198 */           dropTable.setIfExists(bool);
/* 2199 */           if (readIf("CASCADE")) {
/* 2200 */             dropTable.setDropAction(ConstraintActionType.CASCADE);
/* 2201 */             readIf("CONSTRAINTS");
/* 2202 */           } else if (readIf("RESTRICT")) {
/* 2203 */             dropTable.setDropAction(ConstraintActionType.RESTRICT);
/* 2204 */           } else if (readIf("IGNORE")) {
/*      */             
/* 2206 */             dropTable.setDropAction(ConstraintActionType.SET_DEFAULT);
/*      */           } 
/* 2208 */           return (Prepared)dropTable; }  } 
/* 2209 */     }  if (readIf("INDEX")) {
/* 2210 */       boolean bool = readIfExists(false);
/* 2211 */       String str = readIdentifierWithSchema();
/* 2212 */       DropIndex dropIndex = new DropIndex(this.session, getSchema());
/* 2213 */       dropIndex.setIndexName(str);
/* 2214 */       bool = readIfExists(bool);
/* 2215 */       dropIndex.setIfExists(bool);
/*      */       
/* 2217 */       if (readIf(60)) {
/* 2218 */         readIdentifierWithSchema();
/*      */       }
/* 2220 */       return (Prepared)dropIndex;
/* 2221 */     }  if (readIf(82)) {
/* 2222 */       boolean bool = readIfExists(false);
/* 2223 */       DropUser dropUser = new DropUser(this.session);
/* 2224 */       dropUser.setUserName(readIdentifier());
/* 2225 */       bool = readIfExists(bool);
/* 2226 */       readIf("CASCADE");
/* 2227 */       dropUser.setIfExists(bool);
/* 2228 */       return (Prepared)dropUser;
/* 2229 */     }  if (readIf("SEQUENCE")) {
/* 2230 */       boolean bool = readIfExists(false);
/* 2231 */       String str = readIdentifierWithSchema();
/* 2232 */       DropSequence dropSequence = new DropSequence(this.session, getSchema());
/* 2233 */       dropSequence.setSequenceName(str);
/* 2234 */       bool = readIfExists(bool);
/* 2235 */       dropSequence.setIfExists(bool);
/* 2236 */       return (Prepared)dropSequence;
/* 2237 */     }  if (readIf("CONSTANT")) {
/* 2238 */       boolean bool = readIfExists(false);
/* 2239 */       String str = readIdentifierWithSchema();
/* 2240 */       DropConstant dropConstant = new DropConstant(this.session, getSchema());
/* 2241 */       dropConstant.setConstantName(str);
/* 2242 */       bool = readIfExists(bool);
/* 2243 */       dropConstant.setIfExists(bool);
/* 2244 */       return (Prepared)dropConstant;
/* 2245 */     }  if (readIf("TRIGGER")) {
/* 2246 */       boolean bool = readIfExists(false);
/* 2247 */       String str = readIdentifierWithSchema();
/* 2248 */       DropTrigger dropTrigger = new DropTrigger(this.session, getSchema());
/* 2249 */       dropTrigger.setTriggerName(str);
/* 2250 */       bool = readIfExists(bool);
/* 2251 */       dropTrigger.setIfExists(bool);
/* 2252 */       return (Prepared)dropTrigger;
/* 2253 */     }  if (readIf("VIEW")) {
/* 2254 */       boolean bool = readIfExists(false);
/* 2255 */       String str = readIdentifierWithSchema();
/* 2256 */       DropView dropView = new DropView(this.session, getSchema());
/* 2257 */       dropView.setViewName(str);
/* 2258 */       bool = readIfExists(bool);
/* 2259 */       dropView.setIfExists(bool);
/* 2260 */       ConstraintActionType constraintActionType = parseCascadeOrRestrict();
/* 2261 */       if (constraintActionType != null) {
/* 2262 */         dropView.setDropAction(constraintActionType);
/*      */       }
/* 2264 */       return (Prepared)dropView;
/* 2265 */     }  if (readIf("ROLE")) {
/* 2266 */       boolean bool = readIfExists(false);
/* 2267 */       DropRole dropRole = new DropRole(this.session);
/* 2268 */       dropRole.setRoleName(readIdentifier());
/* 2269 */       bool = readIfExists(bool);
/* 2270 */       dropRole.setIfExists(bool);
/* 2271 */       return (Prepared)dropRole;
/* 2272 */     }  if (readIf("ALIAS")) {
/* 2273 */       boolean bool = readIfExists(false);
/* 2274 */       String str = readIdentifierWithSchema();
/*      */       
/* 2276 */       DropFunctionAlias dropFunctionAlias = new DropFunctionAlias(this.session, getSchema());
/* 2277 */       dropFunctionAlias.setAliasName(str);
/* 2278 */       bool = readIfExists(bool);
/* 2279 */       dropFunctionAlias.setIfExists(bool);
/* 2280 */       return (Prepared)dropFunctionAlias;
/* 2281 */     }  if (readIf("SCHEMA")) {
/* 2282 */       boolean bool = readIfExists(false);
/* 2283 */       DropSchema dropSchema = new DropSchema(this.session);
/* 2284 */       dropSchema.setSchemaName(readIdentifier());
/* 2285 */       bool = readIfExists(bool);
/* 2286 */       dropSchema.setIfExists(bool);
/* 2287 */       ConstraintActionType constraintActionType = parseCascadeOrRestrict();
/* 2288 */       if (constraintActionType != null) {
/* 2289 */         dropSchema.setDropAction(constraintActionType);
/*      */       }
/* 2291 */       return (Prepared)dropSchema;
/* 2292 */     }  if (readIf(3)) {
/* 2293 */       read("OBJECTS");
/* 2294 */       DropDatabase dropDatabase = new DropDatabase(this.session);
/* 2295 */       dropDatabase.setDropAllObjects(true);
/* 2296 */       if (readIf("DELETE")) {
/* 2297 */         read("FILES");
/* 2298 */         dropDatabase.setDeleteFiles(true);
/*      */       } 
/* 2300 */       return (Prepared)dropDatabase;
/* 2301 */     }  if (readIf("DOMAIN") || readIf("TYPE") || readIf("DATATYPE"))
/* 2302 */       return (Prepared)parseDropDomain(); 
/* 2303 */     if (readIf("AGGREGATE"))
/* 2304 */       return (Prepared)parseDropAggregate(); 
/* 2305 */     if (readIf("SYNONYM")) {
/* 2306 */       boolean bool = readIfExists(false);
/* 2307 */       String str = readIdentifierWithSchema();
/* 2308 */       DropSynonym dropSynonym = new DropSynonym(this.session, getSchema());
/* 2309 */       dropSynonym.setSynonymName(str);
/* 2310 */       bool = readIfExists(bool);
/* 2311 */       dropSynonym.setIfExists(bool);
/* 2312 */       return (Prepared)dropSynonym;
/*      */     } 
/* 2314 */     throw getSyntaxError();
/*      */   }
/*      */   
/*      */   private DropDomain parseDropDomain() {
/* 2318 */     boolean bool = readIfExists(false);
/* 2319 */     String str = readIdentifierWithSchema();
/* 2320 */     DropDomain dropDomain = new DropDomain(this.session, getSchema());
/* 2321 */     dropDomain.setDomainName(str);
/* 2322 */     bool = readIfExists(bool);
/* 2323 */     dropDomain.setIfDomainExists(bool);
/* 2324 */     ConstraintActionType constraintActionType = parseCascadeOrRestrict();
/* 2325 */     if (constraintActionType != null) {
/* 2326 */       dropDomain.setDropAction(constraintActionType);
/*      */     }
/* 2328 */     return dropDomain;
/*      */   }
/*      */   
/*      */   private DropAggregate parseDropAggregate() {
/* 2332 */     boolean bool = readIfExists(false);
/* 2333 */     String str = readIdentifierWithSchema();
/* 2334 */     DropAggregate dropAggregate = new DropAggregate(this.session, getSchema());
/* 2335 */     dropAggregate.setName(str);
/* 2336 */     bool = readIfExists(bool);
/* 2337 */     dropAggregate.setIfExists(bool);
/* 2338 */     return dropAggregate;
/*      */   }
/*      */   
/*      */   private TableFilter readTableReference() {
/* 2342 */     for (TableFilter tableFilter1 = readTablePrimary(), tableFilter2 = tableFilter1;; tableFilter2 = tableFilter) {
/* 2343 */       TableFilter tableFilter; Expression expression; switch (this.currentTokenType) {
/*      */         case 65:
/* 2345 */           read();
/* 2346 */           readIf("OUTER");
/* 2347 */           read(46);
/*      */           
/* 2349 */           tableFilter = readTableReference();
/* 2350 */           expression = readJoinSpecification(tableFilter1, tableFilter, true);
/* 2351 */           addJoin(tableFilter, tableFilter1, true, expression);
/* 2352 */           tableFilter1 = tableFilter;
/*      */           break;
/*      */         
/*      */         case 48:
/* 2356 */           read();
/* 2357 */           readIf("OUTER");
/* 2358 */           read(46);
/* 2359 */           tableFilter = readTableReference();
/* 2360 */           expression = readJoinSpecification(tableFilter1, tableFilter, false);
/* 2361 */           addJoin(tableFilter1, tableFilter, true, expression);
/*      */           break;
/*      */         
/*      */         case 36:
/* 2365 */           read();
/* 2366 */           throw getSyntaxError();
/*      */         case 42:
/* 2368 */           read();
/* 2369 */           read(46);
/* 2370 */           tableFilter = readTableReference();
/* 2371 */           expression = readJoinSpecification(tableFilter1, tableFilter, false);
/* 2372 */           addJoin(tableFilter1, tableFilter, false, expression);
/*      */           break;
/*      */         
/*      */         case 46:
/* 2376 */           read();
/* 2377 */           tableFilter = readTableReference();
/* 2378 */           expression = readJoinSpecification(tableFilter1, tableFilter, false);
/* 2379 */           addJoin(tableFilter1, tableFilter, false, expression);
/*      */           break;
/*      */         
/*      */         case 15:
/* 2383 */           read();
/* 2384 */           read(46);
/* 2385 */           tableFilter = readTablePrimary();
/* 2386 */           addJoin(tableFilter1, tableFilter, false, null);
/*      */           break;
/*      */         
/*      */         case 56:
/* 2390 */           read();
/* 2391 */           read(46);
/* 2392 */           tableFilter = readTablePrimary();
/* 2393 */           expression = null;
/* 2394 */           for (Column column1 : tableFilter2.getTable().getColumns()) {
/* 2395 */             Column column2 = tableFilter.getColumn(tableFilter2.getColumnName(column1), true);
/* 2396 */             if (column2 != null) {
/* 2397 */               expression = addJoinColumn(expression, tableFilter2, tableFilter, column1, column2, false);
/*      */             }
/*      */           } 
/* 2400 */           addJoin(tableFilter1, tableFilter, false, expression);
/*      */           break;
/*      */         
/*      */         default:
/* 2404 */           if (this.expectedList != null)
/*      */           {
/* 2406 */             addMultipleExpected(new int[] { 65, 48, 42, 46, 15, 56 });
/*      */           }
/* 2408 */           return tableFilter1;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private Expression readJoinSpecification(TableFilter paramTableFilter1, TableFilter paramTableFilter2, boolean paramBoolean) {
/* 2414 */     Expression expression = null;
/* 2415 */     if (readIf(60)) {
/* 2416 */       expression = readExpression();
/* 2417 */     } else if (readIf(83)) {
/* 2418 */       read(105);
/*      */       do {
/* 2420 */         String str = readIdentifier();
/* 2421 */         expression = addJoinColumn(expression, paramTableFilter1, paramTableFilter2, paramTableFilter1.getColumn(str, false), paramTableFilter2
/* 2422 */             .getColumn(str, false), paramBoolean);
/* 2423 */       } while (readIfMore());
/*      */     } 
/* 2425 */     return expression;
/*      */   }
/*      */   private Expression addJoinColumn(Expression paramExpression, TableFilter paramTableFilter1, TableFilter paramTableFilter2, Column paramColumn1, Column paramColumn2, boolean paramBoolean) {
/*      */     Comparison comparison1;
/*      */     ConditionAndOr conditionAndOr;
/* 2430 */     if (paramBoolean) {
/* 2431 */       paramTableFilter1.addCommonJoinColumns(paramColumn1, paramColumn2, paramTableFilter2);
/* 2432 */       paramTableFilter2.addCommonJoinColumnToExclude(paramColumn2);
/*      */     } else {
/* 2434 */       paramTableFilter1.addCommonJoinColumns(paramColumn1, paramColumn1, paramTableFilter1);
/* 2435 */       paramTableFilter2.addCommonJoinColumnToExclude(paramColumn2);
/*      */     } 
/*      */     
/* 2438 */     ExpressionColumn expressionColumn1 = new ExpressionColumn(this.database, paramTableFilter1.getSchemaName(), paramTableFilter1.getTableAlias(), paramTableFilter1.getColumnName(paramColumn1));
/*      */     
/* 2440 */     ExpressionColumn expressionColumn2 = new ExpressionColumn(this.database, paramTableFilter2.getSchemaName(), paramTableFilter2.getTableAlias(), paramTableFilter2.getColumnName(paramColumn2));
/* 2441 */     Comparison comparison2 = new Comparison(0, (Expression)expressionColumn1, (Expression)expressionColumn2, false);
/* 2442 */     if (paramExpression == null) {
/* 2443 */       comparison1 = comparison2;
/*      */     } else {
/* 2445 */       conditionAndOr = new ConditionAndOr(0, (Expression)comparison1, (Expression)comparison2);
/*      */     } 
/* 2447 */     return (Expression)conditionAndOr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addJoin(TableFilter paramTableFilter1, TableFilter paramTableFilter2, boolean paramBoolean, Expression paramExpression) {
/* 2461 */     if (paramTableFilter2.getJoin() != null) {
/* 2462 */       String str = "SYSTEM_JOIN_" + this.token.start();
/*      */       
/* 2464 */       TableFilter tableFilter = new TableFilter(this.session, (Table)new DualTable(this.database), str, this.rightsChecked, this.currentSelect, paramTableFilter2.getOrderInFrom(), null);
/*      */       
/* 2466 */       tableFilter.setNestedJoin(paramTableFilter2);
/* 2467 */       paramTableFilter2 = tableFilter;
/*      */     } 
/* 2469 */     paramTableFilter1.addJoin(paramTableFilter2, paramBoolean, paramExpression);
/*      */   }
/*      */   
/*      */   private Prepared parseExecutePostgre() {
/* 2473 */     if (readIf("IMMEDIATE")) {
/* 2474 */       return (Prepared)new ExecuteImmediate(this.session, readExpression());
/*      */     }
/* 2476 */     ExecuteProcedure executeProcedure = new ExecuteProcedure(this.session);
/* 2477 */     String str = readIdentifier();
/* 2478 */     Procedure procedure = this.session.getProcedure(str);
/* 2479 */     if (procedure == null) {
/* 2480 */       throw DbException.get(90077, str);
/*      */     }
/*      */     
/* 2483 */     executeProcedure.setProcedure(procedure);
/* 2484 */     if (readIf(105)) {
/* 2485 */       for (byte b = 0;; b++) {
/* 2486 */         executeProcedure.setExpression(b, readExpression());
/* 2487 */         if (!readIfMore()) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     }
/* 2492 */     return (Prepared)executeProcedure;
/*      */   }
/*      */   
/*      */   private Prepared parseExecuteSQLServer() {
/* 2496 */     Call call = new Call(this.session);
/* 2497 */     this.currentPrepared = (Prepared)call;
/* 2498 */     String str1 = null;
/* 2499 */     String str2 = readIdentifier();
/* 2500 */     if (readIf(110)) {
/* 2501 */       str1 = str2;
/* 2502 */       str2 = readIdentifier();
/* 2503 */       if (readIf(110)) {
/* 2504 */         checkDatabaseName(str1);
/* 2505 */         str1 = str2;
/* 2506 */         str2 = readIdentifier();
/*      */       } 
/*      */     } 
/* 2509 */     FunctionAlias functionAlias = getFunctionAliasWithinPath(str2, (str1 != null) ? this.database
/* 2510 */         .getSchema(str1) : null);
/*      */     
/* 2512 */     ArrayList<Expression> arrayList = Utils.newSmallArrayList();
/* 2513 */     if (this.currentTokenType != 115 && this.currentTokenType != 93) {
/*      */       do {
/* 2515 */         arrayList.add(readExpression());
/* 2516 */       } while (readIf(109));
/*      */     }
/* 2518 */     Expression[] arrayOfExpression = arrayList.<Expression>toArray(new Expression[0]);
/* 2519 */     call.setExpression((Expression)new JavaFunction(functionAlias, arrayOfExpression));
/* 2520 */     return (Prepared)call;
/*      */   }
/*      */   
/*      */   private FunctionAlias getFunctionAliasWithinPath(String paramString, Schema paramSchema) {
/* 2524 */     UserDefinedFunction userDefinedFunction = findUserDefinedFunctionWithinPath(paramSchema, paramString);
/* 2525 */     if (userDefinedFunction instanceof FunctionAlias) {
/* 2526 */       return (FunctionAlias)userDefinedFunction;
/*      */     }
/* 2528 */     throw DbException.get(90022, paramString);
/*      */   }
/*      */   
/*      */   private DeallocateProcedure parseDeallocate() {
/* 2532 */     readIf("PLAN");
/* 2533 */     DeallocateProcedure deallocateProcedure = new DeallocateProcedure(this.session);
/* 2534 */     deallocateProcedure.setProcedureName(readIdentifier());
/* 2535 */     return deallocateProcedure;
/*      */   }
/*      */   private Explain parseExplain() {
/*      */     Query query;
/* 2539 */     Explain explain = new Explain(this.session);
/* 2540 */     if (readIf("ANALYZE")) {
/* 2541 */       explain.setExecuteCommand(true);
/*      */     }
/* 2543 */     else if (readIf("PLAN")) {
/* 2544 */       readIf(33);
/*      */     } 
/*      */     
/* 2547 */     switch (this.currentTokenType)
/*      */     { case 69:
/*      */       case 75:
/*      */       case 85:
/*      */       case 89:
/*      */       case 105:
/* 2553 */         query = parseQuery();
/* 2554 */         query.setNeverLazy(true);
/* 2555 */         explain.setCommand((Prepared)query);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2571 */         return explain; }  int i = this.tokenIndex; if (readIf("DELETE")) { explain.setCommand((Prepared)parseDelete(i)); } else if (readIf("UPDATE")) { explain.setCommand((Prepared)parseUpdate(i)); } else if (readIf("INSERT")) { explain.setCommand((Prepared)parseInsert(i)); } else if (readIf("MERGE")) { explain.setCommand(parseMerge(i)); } else { throw getSyntaxError(); }  return explain;
/*      */   }
/*      */   
/*      */   private Query parseQuery() {
/* 2575 */     int i = this.parameters.size();
/* 2576 */     Query query = parseQueryExpression();
/* 2577 */     int j = this.parameters.size();
/* 2578 */     ArrayList arrayList = new ArrayList(j);
/* 2579 */     for (int k = i; k < j; k++) {
/* 2580 */       arrayList.add(this.parameters.get(k));
/*      */     }
/* 2582 */     query.setParameterList(arrayList);
/* 2583 */     query.init();
/* 2584 */     return query;
/*      */   }
/*      */   
/*      */   private Prepared parseWithStatementOrQuery(int paramInt) {
/* 2588 */     int i = this.parameters.size();
/* 2589 */     Prepared prepared = parseWith();
/* 2590 */     int j = this.parameters.size();
/* 2591 */     ArrayList<Parameter> arrayList = new ArrayList(j);
/* 2592 */     for (int k = i; k < j; k++) {
/* 2593 */       arrayList.add(this.parameters.get(k));
/*      */     }
/* 2595 */     prepared.setParameterList(arrayList);
/* 2596 */     if (prepared instanceof Query) {
/* 2597 */       Query query = (Query)prepared;
/* 2598 */       query.init();
/*      */     } 
/* 2600 */     setSQL(prepared, paramInt);
/* 2601 */     return prepared;
/*      */   }
/*      */   
/*      */   private Query parseQueryExpression() {
/*      */     Query query;
/* 2606 */     if (readIf(89)) {
/*      */       try {
/* 2608 */         query = (Query)parseWith();
/* 2609 */       } catch (ClassCastException classCastException) {
/* 2610 */         throw DbException.get(42000, "WITH statement supports only query in this context");
/*      */       } 
/*      */       
/* 2613 */       query.setNeverLazy(true);
/*      */     } else {
/* 2615 */       query = parseQueryExpressionBodyAndEndOfQuery();
/*      */     } 
/* 2617 */     return query;
/*      */   }
/*      */   
/*      */   private Query parseQueryExpressionBodyAndEndOfQuery() {
/* 2621 */     int i = this.tokenIndex;
/* 2622 */     Query query = parseQueryExpressionBody();
/* 2623 */     parseEndOfQuery(query);
/* 2624 */     setSQL((Prepared)query, i);
/* 2625 */     return query;
/*      */   }
/*      */   private Query parseQueryExpressionBody() {
/*      */     SelectUnion selectUnion;
/* 2629 */     Query query = parseQueryTerm();
/*      */     while (true) {
/*      */       SelectUnion.UnionType unionType;
/* 2632 */       if (readIf(79)) {
/* 2633 */         if (readIf(3)) {
/* 2634 */           unionType = SelectUnion.UnionType.UNION_ALL;
/*      */         } else {
/* 2636 */           readIf(26);
/* 2637 */           unionType = SelectUnion.UnionType.UNION;
/*      */         } 
/* 2639 */       } else if (readIf(29) || readIf(53)) {
/* 2640 */         unionType = SelectUnion.UnionType.EXCEPT;
/*      */       } else {
/*      */         break;
/*      */       } 
/* 2644 */       selectUnion = new SelectUnion(this.session, unionType, query, parseQueryTerm());
/*      */     } 
/* 2646 */     return (Query)selectUnion;
/*      */   }
/*      */   private Query parseQueryTerm() {
/*      */     SelectUnion selectUnion;
/* 2650 */     Query query = parseQueryPrimary();
/* 2651 */     while (readIf(43)) {
/* 2652 */       selectUnion = new SelectUnion(this.session, SelectUnion.UnionType.INTERSECT, query, parseQueryPrimary());
/*      */     }
/* 2654 */     return (Query)selectUnion;
/*      */   }
/*      */   
/*      */   private void parseEndOfQuery(Query paramQuery) {
/* 2658 */     if (readIf(62)) {
/* 2659 */       read("BY");
/* 2660 */       Select select = this.currentSelect;
/* 2661 */       if (paramQuery instanceof Select) {
/* 2662 */         this.currentSelect = (Select)paramQuery;
/*      */       }
/* 2664 */       ArrayList<QueryOrderBy> arrayList = Utils.newSmallArrayList();
/*      */       while (true) {
/* 2666 */         boolean bool = (this.currentTokenType == 94) ? true : false;
/* 2667 */         QueryOrderBy queryOrderBy = new QueryOrderBy();
/* 2668 */         Expression expression = readExpression();
/* 2669 */         if (bool && expression instanceof ValueExpression && expression.getType().getValueType() == 11) {
/* 2670 */           queryOrderBy.columnIndexExpr = expression;
/* 2671 */         } else if (expression instanceof Parameter) {
/* 2672 */           this.recompileAlways = true;
/* 2673 */           queryOrderBy.columnIndexExpr = expression;
/*      */         } else {
/* 2675 */           queryOrderBy.expression = expression;
/*      */         } 
/* 2677 */         queryOrderBy.sortType = parseSortType();
/* 2678 */         arrayList.add(queryOrderBy);
/* 2679 */         if (!readIf(109))
/* 2680 */         { paramQuery.setOrder(arrayList);
/* 2681 */           this.currentSelect = select; break; } 
/*      */       } 
/* 2683 */     }  if (paramQuery.getFetch() == null) {
/*      */       
/* 2685 */       Select select = this.currentSelect;
/* 2686 */       this.currentSelect = null;
/* 2687 */       boolean bool = false;
/*      */       
/* 2689 */       if (readIf(59)) {
/* 2690 */         bool = true;
/* 2691 */         paramQuery.setOffset(readExpression().optimize(this.session));
/* 2692 */         if (!readIf(66)) {
/* 2693 */           readIf("ROWS");
/*      */         }
/*      */       } 
/* 2696 */       if (readIf(32)) {
/* 2697 */         bool = true;
/* 2698 */         if (!readIf("FIRST")) {
/* 2699 */           read("NEXT");
/*      */         }
/* 2701 */         if (readIf(66) || readIf("ROWS")) {
/* 2702 */           paramQuery.setFetch((Expression)ValueExpression.get((Value)ValueInteger.get(1)));
/*      */         } else {
/* 2704 */           paramQuery.setFetch(readExpression().optimize(this.session));
/* 2705 */           if (readIf("PERCENT")) {
/* 2706 */             paramQuery.setFetchPercent(true);
/*      */           }
/* 2708 */           if (!readIf(66)) {
/* 2709 */             read("ROWS");
/*      */           }
/*      */         } 
/* 2712 */         if (readIf(89)) {
/* 2713 */           read("TIES");
/* 2714 */           paramQuery.setWithTies(true);
/*      */         } else {
/* 2716 */           read("ONLY");
/*      */         } 
/*      */       } 
/*      */       
/* 2720 */       if (!bool && (this.database.getMode()).limit && readIf(50)) {
/* 2721 */         Expression expression = readExpression().optimize(this.session);
/* 2722 */         if (readIf(59)) {
/* 2723 */           paramQuery.setOffset(readExpression().optimize(this.session));
/* 2724 */         } else if (readIf(109)) {
/*      */           
/* 2726 */           Expression expression1 = expression;
/* 2727 */           expression = readExpression().optimize(this.session);
/* 2728 */           paramQuery.setOffset(expression1);
/*      */         } 
/* 2730 */         paramQuery.setFetch(expression);
/*      */       } 
/* 2732 */       this.currentSelect = select;
/*      */     } 
/* 2734 */     if (readIf(33)) {
/* 2735 */       if (readIf("UPDATE")) {
/* 2736 */         if (readIf("OF")) {
/*      */           do {
/* 2738 */             readIdentifierWithSchema();
/* 2739 */           } while (readIf(109));
/* 2740 */         } else if (readIf("NOWAIT")) {
/*      */         
/*      */         } 
/* 2743 */         paramQuery.setForUpdate(true);
/* 2744 */       } else if (readIf("READ") || readIf(32)) {
/* 2745 */         read("ONLY");
/*      */       } 
/*      */     }
/* 2748 */     if ((this.database.getMode()).isolationLevelInSelectOrInsertStatement) {
/* 2749 */       parseIsolationClause();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void parseIsolationClause() {
/* 2757 */     if (readIf(89)) {
/* 2758 */       if (readIf("RR") || readIf("RS")) {
/*      */         
/* 2760 */         if (readIf("USE")) {
/* 2761 */           read(4);
/* 2762 */           read("KEEP");
/* 2763 */           if (readIf("SHARE") || readIf("UPDATE") || 
/* 2764 */             readIf("EXCLUSIVE"));
/*      */ 
/*      */           
/* 2767 */           read("LOCKS");
/*      */         } 
/* 2769 */       } else if (readIf("CS") || readIf("UR")) {
/*      */       
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private Query parseQueryPrimary() {
/* 2776 */     if (readIf(105)) {
/* 2777 */       Query query = parseQueryExpressionBodyAndEndOfQuery();
/* 2778 */       read(106);
/* 2779 */       return query;
/*      */     } 
/* 2781 */     int i = this.tokenIndex;
/* 2782 */     if (readIf(69))
/* 2783 */       return (Query)parseSelect(i); 
/* 2784 */     if (readIf(75)) {
/* 2785 */       return parseExplicitTable(i);
/*      */     }
/* 2787 */     read(85);
/* 2788 */     return (Query)parseValues();
/*      */   }
/*      */   
/*      */   private void parseSelectFromPart(Select paramSelect) {
/*      */     do {
/* 2793 */       TableFilter tableFilter = readTableReference();
/* 2794 */       paramSelect.addTableFilter(tableFilter, true);
/* 2795 */       boolean bool = false;
/*      */       while (true) {
/* 2797 */         TableFilter tableFilter1 = tableFilter.getNestedJoin();
/* 2798 */         if (tableFilter1 != null) {
/* 2799 */           tableFilter1.visit(paramTableFilter -> paramSelect.addTableFilter(paramTableFilter, false));
/*      */         }
/* 2801 */         TableFilter tableFilter2 = tableFilter.getJoin();
/* 2802 */         if (tableFilter2 == null) {
/*      */           break;
/*      */         }
/* 2805 */         bool |= tableFilter2.isJoinOuter();
/* 2806 */         if (bool) {
/* 2807 */           paramSelect.addTableFilter(tableFilter2, false);
/*      */         } else {
/*      */           
/* 2810 */           Expression expression = tableFilter2.getJoinCondition();
/* 2811 */           if (expression != null) {
/* 2812 */             paramSelect.addCondition(expression);
/*      */           }
/* 2814 */           tableFilter2.removeJoinCondition();
/* 2815 */           tableFilter.removeJoin();
/* 2816 */           paramSelect.addTableFilter(tableFilter2, true);
/*      */         } 
/* 2818 */         tableFilter = tableFilter2;
/*      */       } 
/* 2820 */     } while (readIf(109));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void parseSelectExpressions(Select paramSelect) {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield database : Lorg/h2/engine/Database;
/*      */     //   4: invokevirtual getMode : ()Lorg/h2/engine/Mode;
/*      */     //   7: getfield topInSelect : Z
/*      */     //   10: ifeq -> 88
/*      */     //   13: aload_0
/*      */     //   14: ldc 'TOP'
/*      */     //   16: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   19: ifeq -> 88
/*      */     //   22: aload_0
/*      */     //   23: getfield currentSelect : Lorg/h2/command/query/Select;
/*      */     //   26: astore_2
/*      */     //   27: aload_0
/*      */     //   28: aconst_null
/*      */     //   29: putfield currentSelect : Lorg/h2/command/query/Select;
/*      */     //   32: aload_1
/*      */     //   33: aload_0
/*      */     //   34: invokespecial readTerm : ()Lorg/h2/expression/Expression;
/*      */     //   37: aload_0
/*      */     //   38: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   41: invokevirtual optimize : (Lorg/h2/engine/SessionLocal;)Lorg/h2/expression/Expression;
/*      */     //   44: invokevirtual setFetch : (Lorg/h2/expression/Expression;)V
/*      */     //   47: aload_0
/*      */     //   48: ldc_w 'PERCENT'
/*      */     //   51: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   54: ifeq -> 62
/*      */     //   57: aload_1
/*      */     //   58: iconst_1
/*      */     //   59: invokevirtual setFetchPercent : (Z)V
/*      */     //   62: aload_0
/*      */     //   63: bipush #89
/*      */     //   65: invokespecial readIf : (I)Z
/*      */     //   68: ifeq -> 83
/*      */     //   71: aload_0
/*      */     //   72: ldc_w 'TIES'
/*      */     //   75: invokespecial read : (Ljava/lang/String;)V
/*      */     //   78: aload_1
/*      */     //   79: iconst_1
/*      */     //   80: invokevirtual setWithTies : (Z)V
/*      */     //   83: aload_0
/*      */     //   84: aload_2
/*      */     //   85: putfield currentSelect : Lorg/h2/command/query/Select;
/*      */     //   88: aload_0
/*      */     //   89: bipush #26
/*      */     //   91: invokespecial readIf : (I)Z
/*      */     //   94: ifeq -> 157
/*      */     //   97: aload_0
/*      */     //   98: bipush #60
/*      */     //   100: invokespecial readIf : (I)Z
/*      */     //   103: ifeq -> 150
/*      */     //   106: aload_0
/*      */     //   107: bipush #105
/*      */     //   109: invokespecial read : (I)V
/*      */     //   112: invokestatic newSmallArrayList : ()Ljava/util/ArrayList;
/*      */     //   115: astore_2
/*      */     //   116: aload_2
/*      */     //   117: aload_0
/*      */     //   118: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   121: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   124: pop
/*      */     //   125: aload_0
/*      */     //   126: invokespecial readIfMore : ()Z
/*      */     //   129: ifne -> 116
/*      */     //   132: aload_1
/*      */     //   133: aload_2
/*      */     //   134: iconst_0
/*      */     //   135: anewarray org/h2/expression/Expression
/*      */     //   138: invokevirtual toArray : ([Ljava/lang/Object;)[Ljava/lang/Object;
/*      */     //   141: checkcast [Lorg/h2/expression/Expression;
/*      */     //   144: invokevirtual setDistinct : ([Lorg/h2/expression/Expression;)V
/*      */     //   147: goto -> 163
/*      */     //   150: aload_1
/*      */     //   151: invokevirtual setDistinct : ()V
/*      */     //   154: goto -> 163
/*      */     //   157: aload_0
/*      */     //   158: iconst_3
/*      */     //   159: invokespecial readIf : (I)Z
/*      */     //   162: pop
/*      */     //   163: invokestatic newSmallArrayList : ()Ljava/util/ArrayList;
/*      */     //   166: astore_2
/*      */     //   167: aload_0
/*      */     //   168: bipush #108
/*      */     //   170: invokespecial readIf : (I)Z
/*      */     //   173: ifeq -> 190
/*      */     //   176: aload_2
/*      */     //   177: aload_0
/*      */     //   178: aconst_null
/*      */     //   179: aconst_null
/*      */     //   180: invokespecial parseWildcard : (Ljava/lang/String;Ljava/lang/String;)Lorg/h2/expression/Wildcard;
/*      */     //   183: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   186: pop
/*      */     //   187: goto -> 353
/*      */     //   190: aload_0
/*      */     //   191: getfield currentTokenType : I
/*      */     //   194: lookupswitch default -> 303, 32 -> 300, 35 -> 300, 37 -> 300, 38 -> 300, 59 -> 300, 62 -> 300, 64 -> 300, 87 -> 300, 88 -> 300, 93 -> 300, 106 -> 300, 115 -> 300
/*      */     //   300: goto -> 353
/*      */     //   303: aload_0
/*      */     //   304: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   307: astore_3
/*      */     //   308: aload_0
/*      */     //   309: bipush #7
/*      */     //   311: invokespecial readIf : (I)Z
/*      */     //   314: ifne -> 324
/*      */     //   317: aload_0
/*      */     //   318: invokespecial isIdentifier : ()Z
/*      */     //   321: ifeq -> 347
/*      */     //   324: new org/h2/expression/Alias
/*      */     //   327: dup
/*      */     //   328: aload_3
/*      */     //   329: aload_0
/*      */     //   330: invokespecial readIdentifier : ()Ljava/lang/String;
/*      */     //   333: aload_0
/*      */     //   334: getfield database : Lorg/h2/engine/Database;
/*      */     //   337: invokevirtual getMode : ()Lorg/h2/engine/Mode;
/*      */     //   340: getfield aliasColumnName : Z
/*      */     //   343: invokespecial <init> : (Lorg/h2/expression/Expression;Ljava/lang/String;Z)V
/*      */     //   346: astore_3
/*      */     //   347: aload_2
/*      */     //   348: aload_3
/*      */     //   349: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   352: pop
/*      */     //   353: aload_0
/*      */     //   354: bipush #109
/*      */     //   356: invokespecial readIf : (I)Z
/*      */     //   359: ifne -> 167
/*      */     //   362: aload_1
/*      */     //   363: aload_2
/*      */     //   364: invokevirtual setExpressions : (Ljava/util/ArrayList;)V
/*      */     //   367: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #2824	-> 0
/*      */     //   #2825	-> 22
/*      */     //   #2827	-> 27
/*      */     //   #2832	-> 32
/*      */     //   #2833	-> 47
/*      */     //   #2834	-> 57
/*      */     //   #2836	-> 62
/*      */     //   #2837	-> 71
/*      */     //   #2838	-> 78
/*      */     //   #2840	-> 83
/*      */     //   #2842	-> 88
/*      */     //   #2843	-> 97
/*      */     //   #2844	-> 106
/*      */     //   #2845	-> 112
/*      */     //   #2847	-> 116
/*      */     //   #2848	-> 125
/*      */     //   #2849	-> 132
/*      */     //   #2850	-> 147
/*      */     //   #2851	-> 150
/*      */     //   #2854	-> 157
/*      */     //   #2856	-> 163
/*      */     //   #2858	-> 167
/*      */     //   #2859	-> 176
/*      */     //   #2861	-> 190
/*      */     //   #2874	-> 300
/*      */     //   #2876	-> 303
/*      */     //   #2877	-> 308
/*      */     //   #2878	-> 324
/*      */     //   #2880	-> 347
/*      */     //   #2883	-> 353
/*      */     //   #2884	-> 362
/*      */     //   #2885	-> 367
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Select parseSelect(int paramInt) {
/* 2888 */     Select select1 = new Select(this.session, this.currentSelect);
/* 2889 */     Select select2 = this.currentSelect;
/* 2890 */     Prepared prepared = this.currentPrepared;
/* 2891 */     this.currentSelect = select1;
/* 2892 */     this.currentPrepared = (Prepared)select1;
/* 2893 */     parseSelectExpressions(select1);
/* 2894 */     if (!readIf(35)) {
/*      */       
/* 2896 */       TableFilter tableFilter = new TableFilter(this.session, (Table)new DualTable(this.database), null, this.rightsChecked, this.currentSelect, 0, null);
/*      */       
/* 2898 */       select1.addTableFilter(tableFilter, true);
/*      */     } else {
/* 2900 */       parseSelectFromPart(select1);
/*      */     } 
/* 2902 */     if (readIf(87)) {
/* 2903 */       select1.addCondition(readExpressionWithGlobalConditions());
/*      */     }
/*      */ 
/*      */     
/* 2907 */     this.currentSelect = select2;
/* 2908 */     if (readIf(37)) {
/* 2909 */       read("BY");
/* 2910 */       select1.setGroupQuery();
/* 2911 */       ArrayList<Expression> arrayList = Utils.newSmallArrayList();
/*      */       while (true) {
/* 2913 */         if (isToken(105) && isOrdinaryGroupingSet()) {
/* 2914 */           if (!readIf(106)) {
/*      */             do {
/* 2916 */               arrayList.add(readExpression());
/* 2917 */             } while (readIfMore());
/*      */           }
/*      */         } else {
/* 2920 */           Expression expression = readExpression();
/* 2921 */           if ((this.database.getMode()).groupByColumnIndex && expression instanceof ValueExpression && expression
/* 2922 */             .getType().getValueType() == 11) {
/* 2923 */             ArrayList<Expression> arrayList1 = select1.getExpressions();
/* 2924 */             for (Expression expression1 : arrayList1) {
/* 2925 */               if (expression1 instanceof Wildcard) {
/* 2926 */                 throw getSyntaxError();
/*      */               }
/*      */             } 
/* 2929 */             int i = expression.getValue(this.session).getInt();
/* 2930 */             if (i < 1 || i > arrayList1.size()) {
/* 2931 */               throw DbException.get(90157, new String[] { Integer.toString(i), 
/* 2932 */                     Integer.toString(arrayList1.size()) });
/*      */             }
/* 2934 */             arrayList.add(arrayList1.get(i - 1));
/*      */           } else {
/* 2936 */             arrayList.add(expression);
/*      */           } 
/*      */         } 
/* 2939 */         if (!readIf(109)) {
/* 2940 */           if (!arrayList.isEmpty())
/* 2941 */             select1.setGroupBy(arrayList);  break;
/*      */         } 
/*      */       } 
/* 2944 */     }  this.currentSelect = select1;
/* 2945 */     if (readIf(38)) {
/* 2946 */       select1.setGroupQuery();
/* 2947 */       select1.setHaving(readExpressionWithGlobalConditions());
/*      */     } 
/* 2949 */     if (readIf(88)) {
/*      */       do {
/* 2951 */         int i = this.token.start();
/* 2952 */         String str = readIdentifier();
/* 2953 */         read(7);
/* 2954 */         Window window = readWindowSpecification();
/* 2955 */         if (!this.currentSelect.addWindow(str, window)) {
/* 2956 */           throw DbException.getSyntaxError(this.sqlCommand, i, "unique identifier");
/*      */         }
/* 2958 */       } while (readIf(109));
/*      */     }
/* 2960 */     if (readIf(64)) {
/* 2961 */       select1.setWindowQuery();
/* 2962 */       select1.setQualify(readExpressionWithGlobalConditions());
/*      */     } 
/* 2964 */     select1.setParameterList(this.parameters);
/* 2965 */     this.currentSelect = select2;
/* 2966 */     this.currentPrepared = prepared;
/* 2967 */     setSQL((Prepared)select1, paramInt);
/* 2968 */     return select1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isOrdinaryGroupingSet() {
/* 2979 */     int i = scanToCloseParen(this.tokenIndex + 1);
/* 2980 */     if (i < 0)
/*      */     {
/* 2982 */       return false;
/*      */     }
/* 2984 */     switch (((Token)this.tokens.get(i)).tokenType()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 29:
/*      */       case 32:
/*      */       case 33:
/*      */       case 38:
/*      */       case 43:
/*      */       case 50:
/*      */       case 53:
/*      */       case 59:
/*      */       case 62:
/*      */       case 64:
/*      */       case 79:
/*      */       case 88:
/*      */       case 93:
/*      */       case 106:
/*      */       case 109:
/*      */       case 115:
/* 3006 */         setTokenIndex(this.tokenIndex + 1);
/* 3007 */         return true;
/*      */     } 
/* 3009 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private Query parseExplicitTable(int paramInt) {
/* 3014 */     Table table = readTableOrView();
/* 3015 */     Select select = new Select(this.session, this.currentSelect);
/* 3016 */     TableFilter tableFilter = new TableFilter(this.session, table, null, this.rightsChecked, select, this.orderInFrom++, null);
/*      */     
/* 3018 */     select.addTableFilter(tableFilter, true);
/* 3019 */     select.setExplicitTable();
/* 3020 */     setSQL((Prepared)select, paramInt);
/* 3021 */     return (Query)select;
/*      */   }
/*      */   private void setSQL(Prepared paramPrepared, int paramInt) {
/*      */     ArrayList<Token> arrayList;
/* 3025 */     String str = this.sqlCommand;
/* 3026 */     int i = ((Token)this.tokens.get(paramInt)).start();
/* 3027 */     int j = this.token.start();
/* 3028 */     while (i < j && str.charAt(i) <= ' ') {
/* 3029 */       i++;
/*      */     }
/* 3031 */     while (i < j && str.charAt(j - 1) <= ' ') {
/* 3032 */       j--;
/*      */     }
/* 3034 */     str = str.substring(i, j);
/*      */     
/* 3036 */     if (paramInt == 0 && this.currentTokenType == 93) {
/* 3037 */       arrayList = this.tokens;
/* 3038 */       if (i != 0) {
/* 3039 */         byte b; int k; for (b = 0, k = arrayList.size() - 1; b < k; b++) {
/* 3040 */           ((Token)arrayList.get(b)).subtractFromStart(i);
/*      */         }
/*      */       } 
/* 3043 */       this.token.setStart(str.length());
/* 3044 */       this.sqlCommand = str;
/*      */     } else {
/* 3046 */       List<Token> list = this.tokens.subList(paramInt, this.tokenIndex);
/* 3047 */       arrayList = new ArrayList(list.size() + 1);
/* 3048 */       for (int k = paramInt; k < this.tokenIndex; k++) {
/* 3049 */         Token token = ((Token)this.tokens.get(k)).clone();
/* 3050 */         token.subtractFromStart(i);
/* 3051 */         arrayList.add(token);
/*      */       } 
/* 3053 */       arrayList.add(new Token.EndOfInputToken(str.length()));
/*      */     } 
/* 3055 */     paramPrepared.setSQL(str, arrayList);
/*      */   }
/*      */   
/*      */   private Expression readExpressionOrDefault() {
/* 3059 */     if (readIf(25)) {
/* 3060 */       return (Expression)ValueExpression.DEFAULT;
/*      */     }
/* 3062 */     return readExpression();
/*      */   }
/*      */   
/*      */   private Expression readExpressionWithGlobalConditions() {
/* 3066 */     Expression expression = readCondition();
/* 3067 */     if (readIf(4)) {
/* 3068 */       expression = readAnd((Expression)new ConditionAndOr(0, expression, readCondition()));
/* 3069 */     } else if (readIf("_LOCAL_AND_GLOBAL_")) {
/* 3070 */       expression = readAnd((Expression)new ConditionLocalAndGlobal(expression, readCondition()));
/*      */     } 
/* 3072 */     return readExpressionPart2(expression);
/*      */   }
/*      */   
/*      */   private Expression readExpression() {
/* 3076 */     return readExpressionPart2(readAnd(readCondition()));
/*      */   }
/*      */   
/*      */   private Expression readExpressionPart2(Expression paramExpression) {
/* 3080 */     if (!readIf(61)) {
/* 3081 */       return paramExpression;
/*      */     }
/* 3083 */     Expression expression = readAnd(readCondition());
/* 3084 */     if (!readIf(61)) {
/* 3085 */       return (Expression)new ConditionAndOr(1, paramExpression, expression);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 3090 */     ArrayList<Expression> arrayList = new ArrayList();
/* 3091 */     arrayList.add(paramExpression);
/* 3092 */     arrayList.add(expression);
/*      */     while (true) {
/* 3094 */       arrayList.add(readAnd(readCondition()));
/*      */       
/* 3096 */       if (!readIf(61))
/* 3097 */         return (Expression)new ConditionAndOrN(1, arrayList); 
/*      */     } 
/*      */   }
/*      */   
/* 3101 */   private Expression readAnd(Expression paramExpression) { if (!readIf(4)) {
/* 3102 */       return paramExpression;
/*      */     }
/* 3104 */     Expression expression = readCondition();
/* 3105 */     if (!readIf(4)) {
/* 3106 */       return (Expression)new ConditionAndOr(0, paramExpression, expression);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 3111 */     ArrayList<Expression> arrayList = new ArrayList();
/* 3112 */     arrayList.add(paramExpression);
/* 3113 */     arrayList.add(expression);
/*      */     while (true) {
/* 3115 */       arrayList.add(readCondition());
/*      */       
/* 3117 */       if (!readIf(4))
/* 3118 */         return (Expression)new ConditionAndOrN(0, arrayList); 
/*      */     }  } private Expression readCondition() {
/*      */     Query query;
/*      */     Expression expression1;
/* 3122 */     switch (this.currentTokenType) {
/*      */       case 57:
/* 3124 */         read();
/* 3125 */         return (Expression)new ConditionNot(readCondition());
/*      */       case 30:
/* 3127 */         read();
/* 3128 */         read(105);
/* 3129 */         query = parseQuery();
/*      */ 
/*      */         
/* 3132 */         read(106);
/* 3133 */         return (Expression)new ExistsPredicate(query);
/*      */       
/*      */       case 80:
/* 3136 */         read();
/* 3137 */         read(105);
/* 3138 */         query = parseQuery();
/* 3139 */         read(106);
/* 3140 */         return (Expression)new UniquePredicate(query);
/*      */     } 
/*      */     
/* 3143 */     int i = this.tokenIndex;
/* 3144 */     if (readIf("INTERSECTS")) {
/* 3145 */       if (readIf(105)) {
/* 3146 */         Expression expression3 = readConcat();
/* 3147 */         read(109);
/* 3148 */         Expression expression4 = readConcat();
/* 3149 */         read(106);
/* 3150 */         return (Expression)new Comparison(8, expression3, expression4, false);
/*      */       } 
/* 3152 */       setTokenIndex(i);
/*      */     } 
/*      */     
/* 3155 */     if (this.expectedList != null) {
/* 3156 */       addMultipleExpected(new int[] { 57, 30, 80 });
/* 3157 */       addExpected("INTERSECTS");
/*      */     } 
/*      */     
/* 3160 */     Expression expression2 = readConcat();
/*      */     do {
/* 3162 */       expression1 = expression2;
/*      */ 
/*      */       
/* 3165 */       int j = this.tokenIndex;
/* 3166 */       boolean bool = readIf(57);
/* 3167 */       if (bool && isToken(58)) {
/*      */         
/* 3169 */         setTokenIndex(j);
/*      */         break;
/*      */       } 
/* 3172 */       expression2 = readConditionRightHandSide(expression1, bool, false);
/* 3173 */     } while (expression2 != null);
/* 3174 */     return expression1;
/*      */   }
/*      */   
/*      */   private Expression readConditionRightHandSide(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2) { Expression expression;
/* 3178 */     if (!paramBoolean1 && readIf(45))
/* 3179 */     { paramExpression = readConditionIs(paramExpression, paramBoolean2); }
/*      */     else
/* 3181 */     { BetweenPredicate betweenPredicate; boolean bool; Expression expression1; switch (this.currentTokenType)
/*      */       { case 10:
/* 3183 */           read();
/* 3184 */           bool = readIf(73);
/* 3185 */           if (!bool) {
/* 3186 */             readIf(8);
/*      */           }
/* 3188 */           expression1 = readConcat();
/* 3189 */           read(4);
/* 3190 */           betweenPredicate = new BetweenPredicate(paramExpression, paramBoolean1, paramBoolean2, bool, expression1, readConcat());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3227 */           return (Expression)betweenPredicate;case 41: read(); expression = readInPredicate((Expression)betweenPredicate, paramBoolean1, paramBoolean2); return expression;case 49: read(); expression = readLikePredicate(expression, CompareLike.LikeType.LIKE, paramBoolean1, paramBoolean2); return expression; }  if (readIf("ILIKE")) { expression = readLikePredicate(expression, CompareLike.LikeType.ILIKE, paramBoolean1, paramBoolean2); } else { CompareLike compareLike; if (readIf("REGEXP")) { Expression expression2 = readConcat(); this.recompileAlways = true; compareLike = new CompareLike(this.database, expression, paramBoolean1, paramBoolean2, expression2, null, CompareLike.LikeType.REGEXP); } else { if (paramBoolean1) { if (paramBoolean2) return null;  if (this.expectedList != null) addMultipleExpected(new int[] { 10, 41, 49 });  throw getSyntaxError(); }  int i = getCompareType(this.currentTokenType); if (i < 0) return null;  read(); expression = readComparison((Expression)compareLike, i, paramBoolean2); }  }  }  return expression; } private Expression readConditionIs(Expression paramExpression, boolean paramBoolean) { NullPredicate nullPredicate; Expression expression;
/*      */     BooleanTest booleanTest;
/*      */     TypePredicate typePredicate;
/*      */     Comparison comparison;
/* 3231 */     boolean bool = readIf(57);
/* 3232 */     switch (this.currentTokenType)
/*      */     { case 58:
/* 3234 */         read();
/* 3235 */         nullPredicate = new NullPredicate(paramExpression, bool, paramBoolean);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3276 */         return (Expression)nullPredicate;case 26: read(); read(35); expression = readComparison((Expression)nullPredicate, bool ? 6 : 7, paramBoolean); return expression;case 77: read(); booleanTest = new BooleanTest(expression, bool, paramBoolean, Boolean.valueOf(true)); return (Expression)booleanTest;case 31: read(); booleanTest = new BooleanTest((Expression)booleanTest, bool, paramBoolean, Boolean.valueOf(false)); return (Expression)booleanTest;case 81: read(); booleanTest = new BooleanTest((Expression)booleanTest, bool, paramBoolean, null); return (Expression)booleanTest; }  if (readIf("OF")) { typePredicate = readTypePredicate((Expression)booleanTest, bool, paramBoolean); } else { IsJsonPredicate isJsonPredicate; if (readIf("JSON")) { isJsonPredicate = readJsonPredicate((Expression)typePredicate, bool, paramBoolean); } else { if (this.expectedList != null) addMultipleExpected(new int[] { 58, 26, 77, 31, 81 });  if (paramBoolean || !this.session.isQuirksMode()) throw getSyntaxError();  comparison = new Comparison(bool ? 7 : 6, (Expression)isJsonPredicate, readConcat(), false); }  }  return (Expression)comparison; }
/*      */ 
/*      */   
/*      */   private TypePredicate readTypePredicate(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2) {
/* 3280 */     read(105);
/* 3281 */     ArrayList<TypeInfo> arrayList = Utils.newSmallArrayList();
/*      */     while (true) {
/* 3283 */       arrayList.add(parseDataType());
/* 3284 */       if (!readIfMore())
/* 3285 */         return new TypePredicate(paramExpression, paramBoolean1, paramBoolean2, arrayList.<TypeInfo>toArray(new TypeInfo[0])); 
/*      */     } 
/*      */   } private Expression readInPredicate(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2) {
/*      */     ArrayList<Expression> arrayList;
/* 3289 */     read(105);
/* 3290 */     if (!paramBoolean2 && (this.database.getMode()).allowEmptyInPredicate && readIf(106)) {
/* 3291 */       return (Expression)ValueExpression.getBoolean(paramBoolean1);
/*      */     }
/*      */     
/* 3294 */     if (isQuery()) {
/* 3295 */       Query query = parseQuery();
/* 3296 */       if (!readIfMore()) {
/* 3297 */         return (Expression)new ConditionInQuery(paramExpression, paramBoolean1, paramBoolean2, query, false, 0);
/*      */       }
/* 3299 */       arrayList = Utils.newSmallArrayList();
/* 3300 */       arrayList.add(new Subquery(query));
/*      */     } else {
/* 3302 */       arrayList = Utils.newSmallArrayList();
/*      */     } 
/*      */     while (true) {
/* 3305 */       arrayList.add(readExpression());
/* 3306 */       if (!readIfMore())
/* 3307 */         return (Expression)new ConditionIn(paramExpression, paramBoolean1, paramBoolean2, arrayList); 
/*      */     } 
/*      */   }
/*      */   private IsJsonPredicate readJsonPredicate(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2) {
/*      */     JSONItemType jSONItemType;
/* 3312 */     if (readIf(84)) {
/* 3313 */       jSONItemType = JSONItemType.VALUE;
/* 3314 */     } else if (readIf(6)) {
/* 3315 */       jSONItemType = JSONItemType.ARRAY;
/* 3316 */     } else if (readIf("OBJECT")) {
/* 3317 */       jSONItemType = JSONItemType.OBJECT;
/* 3318 */     } else if (readIf("SCALAR")) {
/* 3319 */       jSONItemType = JSONItemType.SCALAR;
/*      */     } else {
/* 3321 */       jSONItemType = JSONItemType.VALUE;
/*      */     } 
/* 3323 */     boolean bool = false;
/* 3324 */     if (readIf(89)) {
/* 3325 */       read(80);
/* 3326 */       readIf("KEYS");
/* 3327 */       bool = true;
/* 3328 */     } else if (readIf("WITHOUT")) {
/* 3329 */       read(80);
/* 3330 */       readIf("KEYS");
/*      */     } 
/* 3332 */     return new IsJsonPredicate(paramExpression, paramBoolean1, paramBoolean2, bool, jSONItemType);
/*      */   }
/*      */   
/*      */   private Expression readLikePredicate(Expression paramExpression, CompareLike.LikeType paramLikeType, boolean paramBoolean1, boolean paramBoolean2) {
/* 3336 */     Expression expression1 = readConcat();
/* 3337 */     Expression expression2 = readIf("ESCAPE") ? readConcat() : null;
/* 3338 */     this.recompileAlways = true;
/* 3339 */     return (Expression)new CompareLike(this.database, paramExpression, paramBoolean1, paramBoolean2, expression1, expression2, paramLikeType);
/*      */   }
/*      */   private Expression readComparison(Expression paramExpression, int paramInt, boolean paramBoolean) {
/*      */     Comparison comparison;
/* 3343 */     int i = this.tokenIndex;
/* 3344 */     if (readIf(3))
/* 3345 */     { ConditionInQuery conditionInQuery; read(105);
/* 3346 */       if (isQuery()) {
/* 3347 */         Query query = parseQuery();
/* 3348 */         conditionInQuery = new ConditionInQuery(paramExpression, false, paramBoolean, query, true, paramInt);
/* 3349 */         read(106);
/*      */       } else {
/* 3351 */         setTokenIndex(i);
/* 3352 */         comparison = new Comparison(paramInt, (Expression)conditionInQuery, readConcat(), paramBoolean);
/*      */       }  }
/* 3354 */     else if (readIf(5) || readIf(72))
/* 3355 */     { ConditionInParameter conditionInParameter; read(105);
/* 3356 */       if (this.currentTokenType == 92 && paramInt == 0)
/* 3357 */       { Parameter parameter = readParameter();
/* 3358 */         conditionInParameter = new ConditionInParameter((Expression)comparison, false, paramBoolean, parameter);
/* 3359 */         read(106); }
/* 3360 */       else { ConditionInQuery conditionInQuery; if (isQuery()) {
/* 3361 */           Query query = parseQuery();
/* 3362 */           conditionInQuery = new ConditionInQuery((Expression)conditionInParameter, false, paramBoolean, query, false, paramInt);
/* 3363 */           read(106);
/*      */         } else {
/* 3365 */           setTokenIndex(i);
/* 3366 */           comparison = new Comparison(paramInt, (Expression)conditionInQuery, readConcat(), paramBoolean);
/*      */         }  }
/*      */        }
/* 3369 */     else { comparison = new Comparison(paramInt, (Expression)comparison, readConcat(), paramBoolean); }
/*      */     
/* 3371 */     return (Expression)comparison;
/*      */   }
/*      */   
/*      */   private Expression readConcat() {
/* 3375 */     Expression expression = readSum(); while (true) {
/*      */       ConcatenationOperation concatenationOperation; Expression expression1;
/* 3377 */       switch (this.currentTokenType) {
/*      */         case 104:
/* 3379 */           read();
/* 3380 */           expression1 = readSum();
/* 3381 */           if (readIf(104)) {
/* 3382 */             ConcatenationOperation concatenationOperation1 = new ConcatenationOperation();
/* 3383 */             concatenationOperation1.addParameter(expression);
/* 3384 */             concatenationOperation1.addParameter(expression1);
/*      */             while (true) {
/* 3386 */               concatenationOperation1.addParameter(readSum());
/* 3387 */               if (!readIf(104))
/* 3388 */               { concatenationOperation1.doneWithParameters();
/* 3389 */                 concatenationOperation = concatenationOperation1; } 
/*      */             } 
/* 3391 */           }  concatenationOperation = new ConcatenationOperation((Expression)concatenationOperation, expression1);
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 119:
/* 3396 */           expression = readTildeCondition((Expression)concatenationOperation, false);
/*      */           continue;
/*      */         case 122:
/* 3399 */           expression = readTildeCondition(expression, true); continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 3403 */     addExpected(104);
/* 3404 */     return expression;
/*      */   }
/*      */ 
/*      */   
/*      */   private Expression readSum() {
/*      */     BinaryOperation binaryOperation;
/* 3410 */     Expression expression = readFactor();
/*      */     while (true) {
/* 3412 */       while (readIf(103))
/* 3413 */         binaryOperation = new BinaryOperation(BinaryOperation.OpType.PLUS, expression, readFactor()); 
/* 3414 */       if (readIf(102)) {
/* 3415 */         binaryOperation = new BinaryOperation(BinaryOperation.OpType.MINUS, (Expression)binaryOperation, readFactor()); continue;
/*      */       }  break;
/* 3417 */     }  return (Expression)binaryOperation;
/*      */   }
/*      */ 
/*      */   
/*      */   private Expression readFactor() {
/*      */     MathFunction mathFunction;
/* 3423 */     Expression expression = readTerm(); while (true) {
/*      */       BinaryOperation binaryOperation;
/* 3425 */       while (readIf(108))
/* 3426 */         binaryOperation = new BinaryOperation(BinaryOperation.OpType.MULTIPLY, expression, readTerm()); 
/* 3427 */       if (readIf(113)) {
/* 3428 */         binaryOperation = new BinaryOperation(BinaryOperation.OpType.DIVIDE, (Expression)binaryOperation, readTerm()); continue;
/* 3429 */       }  if (readIf(114)) {
/* 3430 */         mathFunction = new MathFunction((Expression)binaryOperation, readTerm(), 1); continue;
/*      */       }  break;
/* 3432 */     }  return (Expression)mathFunction;
/*      */   }
/*      */ 
/*      */   
/*      */   private Expression readTildeCondition(Expression paramExpression, boolean paramBoolean) {
/*      */     CastSpecification castSpecification;
/* 3438 */     read();
/* 3439 */     if (readIf(108)) {
/* 3440 */       castSpecification = new CastSpecification(paramExpression, TypeInfo.TYPE_VARCHAR_IGNORECASE);
/*      */     }
/* 3442 */     return (Expression)new CompareLike(this.database, (Expression)castSpecification, paramBoolean, false, readSum(), null, CompareLike.LikeType.REGEXP); } private Expression readAggregate(AggregateType paramAggregateType, String paramString) { boolean bool2; ArrayList<Expression> arrayList; Expression expression1, expression2; ListaggArguments listaggArguments; Expression expression3;
/*      */     ArrayList arrayList1;
/*      */     Expression[] arrayOfExpression;
/*      */     int i;
/* 3446 */     if (this.currentSelect == null) {
/* 3447 */       this.expectedList = null;
/* 3448 */       throw getSyntaxError();
/*      */     } 
/*      */     
/* 3451 */     switch (paramAggregateType)
/*      */     { case Derby:
/* 3453 */         if (readIf(108)) {
/* 3454 */           aggregate = new Aggregate(AggregateType.COUNT_ALL, new Expression[0], this.currentSelect, false);
/*      */         } else {
/* 3456 */           boolean bool = readDistinctAgg();
/* 3457 */           Expression expression = readExpression();
/* 3458 */           if (expression instanceof Wildcard && !bool) {
/*      */             
/* 3460 */             aggregate = new Aggregate(AggregateType.COUNT_ALL, new Expression[0], this.currentSelect, false);
/*      */           } else {
/* 3462 */             aggregate = new Aggregate(AggregateType.COUNT, new Expression[] { expression }, this.currentSelect, bool);
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3611 */         read(106);
/* 3612 */         readFilterAndOver((AbstractAggregate)aggregate);
/* 3613 */         return (Expression)aggregate;case HSQLDB: case MySQL: case PostgreSQL: case null: case null: case null: case null: case null: case null: case null: case null: case null: aggregate = new Aggregate(paramAggregateType, new Expression[] { readExpression(), readNextArgument() }, this.currentSelect, false); read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate;case null: aggregate = new Aggregate(paramAggregateType, new Expression[] { readExpression() }, this.currentSelect, false); read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate;case null: bool2 = readDistinctAgg(); expression2 = readExpression(); listaggArguments = new ListaggArguments(); if ("STRING_AGG".equals(paramString)) { read(109); listaggArguments.setSeparator(readString()); arrayList1 = readIfOrderBy(); } else if ("GROUP_CONCAT".equals(paramString)) { arrayList1 = readIfOrderBy(); if (readIf("SEPARATOR")) listaggArguments.setSeparator(readString());  } else { if (readIf(109)) listaggArguments.setSeparator(readString());  if (readIf(60)) { read("OVERFLOW"); if (readIf("TRUNCATE")) { listaggArguments.setOnOverflowTruncate(true); if (this.currentTokenType == 94) listaggArguments.setFilter(readString());  if (!readIf(89)) { read("WITHOUT"); listaggArguments.setWithoutCount(true); }  read("COUNT"); } else { read("ERROR"); }  }  arrayList1 = null; }  arrayOfExpression = new Expression[] { expression2 }; i = this.tokenIndex; read(106); if (arrayList1 == null && isToken("WITHIN")) { aggregate = readWithinGroup(paramAggregateType, arrayOfExpression, bool2, listaggArguments, false, false); } else { setTokenIndex(i); aggregate = new Aggregate(AggregateType.LISTAGG, arrayOfExpression, this.currentSelect, bool2); aggregate.setExtraArguments(listaggArguments); if (arrayList1 != null) aggregate.setOrderByList(arrayList1);  }  read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate;case null: bool2 = readDistinctAgg(); aggregate = new Aggregate(AggregateType.ARRAY_AGG, new Expression[] { readExpression() }, this.currentSelect, bool2); aggregate.setOrderByList(readIfOrderBy()); read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate;case null: case null: case null: case null: if (isToken(106)) return (Expression)readWindowFunction(paramString);  arrayList = Utils.newSmallArrayList(); while (true) { arrayList.add(readExpression()); if (!readIfMore()) { aggregate = readWithinGroup(paramAggregateType, arrayList.<Expression>toArray(new Expression[0]), false, null, true, false); read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate; }  } case null: case null: expression1 = readExpression(); read(106); aggregate = readWithinGroup(paramAggregateType, new Expression[] { expression1 }, false, null, false, true); read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate;case null: if (readIf(106)) { aggregate = readWithinGroup(AggregateType.MODE, new Expression[0], false, null, false, true); } else { expression1 = readExpression(); aggregate = new Aggregate(AggregateType.MODE, new Expression[0], this.currentSelect, false); if (readIf(62)) { read("BY"); expression2 = readExpression(); String str1 = expression1.getSQL(0), str2 = expression2.getSQL(0); if (!str1.equals(str2)) throw DbException.getSyntaxError(42131, this.sqlCommand, this.token.start(), new String[] { str1, str2 });  readAggregateOrder(aggregate, expression1, true); } else { readAggregateOrder(aggregate, expression1, false); }  }  read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate;case null: bool1 = readIf(47); expression2 = readExpression(); if (bool1) { read(84); } else if (!readIf(84)) { read(116); }  expression3 = readExpression(); aggregate = new Aggregate(AggregateType.JSON_OBJECTAGG, new Expression[] { expression2, expression3 }, this.currentSelect, false); readJsonObjectFunctionFlags((ExpressionWithFlags)aggregate, false); read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate;case null: bool1 = readDistinctAgg(); aggregate = new Aggregate(AggregateType.JSON_ARRAYAGG, new Expression[] { readExpression() }, this.currentSelect, bool1); aggregate.setOrderByList(readIfOrderBy()); aggregate.setFlags(1); readJsonObjectFunctionFlags((ExpressionWithFlags)aggregate, true); read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate; }  boolean bool1 = readDistinctAgg(); Aggregate aggregate = new Aggregate(paramAggregateType, new Expression[] { readExpression() }, this.currentSelect, bool1); read(106); readFilterAndOver((AbstractAggregate)aggregate); return (Expression)aggregate; }
/*      */ 
/*      */ 
/*      */   
/*      */   private Aggregate readWithinGroup(AggregateType paramAggregateType, Expression[] paramArrayOfExpression, boolean paramBoolean1, Object paramObject, boolean paramBoolean2, boolean paramBoolean3) {
/* 3618 */     read("WITHIN");
/* 3619 */     read(37);
/* 3620 */     read(105);
/* 3621 */     read(62);
/* 3622 */     read("BY");
/* 3623 */     Aggregate aggregate = new Aggregate(paramAggregateType, paramArrayOfExpression, this.currentSelect, paramBoolean1);
/* 3624 */     aggregate.setExtraArguments(paramObject);
/* 3625 */     if (paramBoolean2) {
/* 3626 */       int i = paramArrayOfExpression.length;
/* 3627 */       ArrayList<QueryOrderBy> arrayList = new ArrayList(i);
/* 3628 */       for (byte b = 0; b < i; b++) {
/* 3629 */         if (b > 0) {
/* 3630 */           read(109);
/*      */         }
/* 3632 */         arrayList.add(parseSortSpecification());
/*      */       } 
/* 3634 */       aggregate.setOrderByList(arrayList);
/* 3635 */     } else if (paramBoolean3) {
/* 3636 */       readAggregateOrder(aggregate, readExpression(), true);
/*      */     } else {
/* 3638 */       aggregate.setOrderByList(parseSortSpecificationList());
/*      */     } 
/* 3640 */     return aggregate;
/*      */   }
/*      */   
/*      */   private void readAggregateOrder(Aggregate paramAggregate, Expression paramExpression, boolean paramBoolean) {
/* 3644 */     ArrayList<QueryOrderBy> arrayList = new ArrayList(1);
/* 3645 */     QueryOrderBy queryOrderBy = new QueryOrderBy();
/* 3646 */     queryOrderBy.expression = paramExpression;
/* 3647 */     if (paramBoolean) {
/* 3648 */       queryOrderBy.sortType = parseSortType();
/*      */     }
/* 3650 */     arrayList.add(queryOrderBy);
/* 3651 */     paramAggregate.setOrderByList(arrayList);
/*      */   }
/*      */   
/*      */   private ArrayList<QueryOrderBy> readIfOrderBy() {
/* 3655 */     if (readIf(62)) {
/* 3656 */       read("BY");
/* 3657 */       return parseSortSpecificationList();
/*      */     } 
/* 3659 */     return null;
/*      */   }
/*      */   
/*      */   private ArrayList<QueryOrderBy> parseSortSpecificationList() {
/* 3663 */     ArrayList<QueryOrderBy> arrayList = Utils.newSmallArrayList();
/*      */     while (true) {
/* 3665 */       arrayList.add(parseSortSpecification());
/* 3666 */       if (!readIf(109))
/* 3667 */         return arrayList; 
/*      */     } 
/*      */   }
/*      */   private QueryOrderBy parseSortSpecification() {
/* 3671 */     QueryOrderBy queryOrderBy = new QueryOrderBy();
/* 3672 */     queryOrderBy.expression = readExpression();
/* 3673 */     queryOrderBy.sortType = parseSortType();
/* 3674 */     return queryOrderBy;
/*      */   }
/*      */   
/*      */   private Expression readUserDefinedFunctionIf(Schema paramSchema, String paramString) {
/* 3678 */     UserDefinedFunction userDefinedFunction = findUserDefinedFunctionWithinPath(paramSchema, paramString);
/* 3679 */     if (userDefinedFunction == null)
/* 3680 */       return null; 
/* 3681 */     if (userDefinedFunction instanceof FunctionAlias) {
/* 3682 */       FunctionAlias functionAlias = (FunctionAlias)userDefinedFunction;
/* 3683 */       ArrayList<Expression> arrayList1 = Utils.newSmallArrayList();
/* 3684 */       if (!readIf(106)) {
/*      */         do {
/* 3686 */           arrayList1.add(readExpression());
/* 3687 */         } while (readIfMore());
/*      */       }
/* 3689 */       return (Expression)new JavaFunction(functionAlias, arrayList1.<Expression>toArray(new Expression[0]));
/*      */     } 
/* 3691 */     UserAggregate userAggregate = (UserAggregate)userDefinedFunction;
/* 3692 */     boolean bool = readDistinctAgg();
/* 3693 */     ArrayList<Expression> arrayList = Utils.newSmallArrayList();
/*      */     while (true) {
/* 3695 */       arrayList.add(readExpression());
/* 3696 */       if (!readIfMore()) {
/* 3697 */         Expression[] arrayOfExpression = arrayList.<Expression>toArray(new Expression[0]);
/* 3698 */         JavaAggregate javaAggregate = new JavaAggregate(userAggregate, arrayOfExpression, this.currentSelect, bool);
/* 3699 */         readFilterAndOver((AbstractAggregate)javaAggregate);
/* 3700 */         return (Expression)javaAggregate;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   private boolean readDistinctAgg() {
/* 3705 */     if (readIf(26)) {
/* 3706 */       return true;
/*      */     }
/* 3708 */     readIf(3);
/* 3709 */     return false;
/*      */   }
/*      */   
/*      */   private void readFilterAndOver(AbstractAggregate paramAbstractAggregate) {
/* 3713 */     if (readIf("FILTER")) {
/* 3714 */       read(105);
/* 3715 */       read(87);
/* 3716 */       Expression expression = readExpression();
/* 3717 */       read(106);
/* 3718 */       paramAbstractAggregate.setFilterCondition(expression);
/*      */     } 
/* 3720 */     readOver((DataAnalysisOperation)paramAbstractAggregate);
/*      */   }
/*      */   
/*      */   private void readOver(DataAnalysisOperation paramDataAnalysisOperation) {
/* 3724 */     if (readIf("OVER")) {
/* 3725 */       paramDataAnalysisOperation.setOverCondition(readWindowNameOrSpecification());
/* 3726 */       this.currentSelect.setWindowQuery();
/* 3727 */     } else if (paramDataAnalysisOperation.isAggregate()) {
/* 3728 */       this.currentSelect.setGroupQuery();
/*      */     } else {
/* 3730 */       throw getSyntaxError();
/*      */     } 
/*      */   }
/*      */   
/*      */   private Window readWindowNameOrSpecification() {
/* 3735 */     return isToken(105) ? readWindowSpecification() : new Window(readIdentifier(), null, null, null);
/*      */   }
/*      */   
/*      */   private Window readWindowSpecification() {
/* 3739 */     read(105);
/* 3740 */     String str = null;
/* 3741 */     if (this.currentTokenType == 2) {
/* 3742 */       String str1 = this.currentToken;
/* 3743 */       if (this.token.isQuoted() || (
/* 3744 */         !equalsToken(str1, "PARTITION") && 
/* 3745 */         !equalsToken(str1, "ROWS") && 
/* 3746 */         !equalsToken(str1, "RANGE") && 
/* 3747 */         !equalsToken(str1, "GROUPS"))) {
/* 3748 */         str = str1;
/* 3749 */         read();
/*      */       } 
/*      */     } 
/* 3752 */     ArrayList<Expression> arrayList = null;
/* 3753 */     if (readIf("PARTITION")) {
/* 3754 */       read("BY");
/* 3755 */       arrayList = Utils.newSmallArrayList();
/*      */       do {
/* 3757 */         Expression expression = readExpression();
/* 3758 */         arrayList.add(expression);
/* 3759 */       } while (readIf(109));
/*      */     } 
/* 3761 */     ArrayList<QueryOrderBy> arrayList1 = readIfOrderBy();
/* 3762 */     WindowFrame windowFrame = readWindowFrame();
/* 3763 */     read(106);
/* 3764 */     return new Window(str, arrayList, arrayList1, windowFrame);
/*      */   }
/*      */   private WindowFrame readWindowFrame() {
/*      */     WindowFrameUnits windowFrameUnits;
/*      */     WindowFrameBound windowFrameBound1, windowFrameBound2;
/* 3769 */     if (readIf("ROWS")) {
/* 3770 */       windowFrameUnits = WindowFrameUnits.ROWS;
/* 3771 */     } else if (readIf("RANGE")) {
/* 3772 */       windowFrameUnits = WindowFrameUnits.RANGE;
/* 3773 */     } else if (readIf("GROUPS")) {
/* 3774 */       windowFrameUnits = WindowFrameUnits.GROUPS;
/*      */     } else {
/* 3776 */       return null;
/*      */     } 
/*      */     
/* 3779 */     if (readIf(10)) {
/* 3780 */       windowFrameBound1 = readWindowFrameRange();
/* 3781 */       read(4);
/* 3782 */       windowFrameBound2 = readWindowFrameRange();
/*      */     } else {
/* 3784 */       windowFrameBound1 = readWindowFrameStarting();
/* 3785 */       windowFrameBound2 = null;
/*      */     } 
/* 3787 */     int i = this.token.start();
/* 3788 */     WindowFrameExclusion windowFrameExclusion = WindowFrameExclusion.EXCLUDE_NO_OTHERS;
/* 3789 */     if (readIf("EXCLUDE")) {
/* 3790 */       if (readIf("CURRENT")) {
/* 3791 */         read(66);
/* 3792 */         windowFrameExclusion = WindowFrameExclusion.EXCLUDE_CURRENT_ROW;
/* 3793 */       } else if (readIf(37)) {
/* 3794 */         windowFrameExclusion = WindowFrameExclusion.EXCLUDE_GROUP;
/* 3795 */       } else if (readIf("TIES")) {
/* 3796 */         windowFrameExclusion = WindowFrameExclusion.EXCLUDE_TIES;
/*      */       } else {
/* 3798 */         read("NO");
/* 3799 */         read("OTHERS");
/*      */       } 
/*      */     }
/* 3802 */     WindowFrame windowFrame = new WindowFrame(windowFrameUnits, windowFrameBound1, windowFrameBound2, windowFrameExclusion);
/* 3803 */     if (!windowFrame.isValid()) {
/* 3804 */       throw DbException.getSyntaxError(this.sqlCommand, i);
/*      */     }
/* 3806 */     return windowFrame;
/*      */   }
/*      */   
/*      */   private WindowFrameBound readWindowFrameStarting() {
/* 3810 */     if (readIf("UNBOUNDED")) {
/* 3811 */       read("PRECEDING");
/* 3812 */       return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_PRECEDING, null);
/*      */     } 
/* 3814 */     if (readIf("CURRENT")) {
/* 3815 */       read(66);
/* 3816 */       return new WindowFrameBound(WindowFrameBoundType.CURRENT_ROW, null);
/*      */     } 
/* 3818 */     Expression expression = readExpression();
/* 3819 */     read("PRECEDING");
/* 3820 */     return new WindowFrameBound(WindowFrameBoundType.PRECEDING, expression);
/*      */   }
/*      */   
/*      */   private WindowFrameBound readWindowFrameRange() {
/* 3824 */     if (readIf("UNBOUNDED")) {
/* 3825 */       if (readIf("PRECEDING")) {
/* 3826 */         return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_PRECEDING, null);
/*      */       }
/* 3828 */       read("FOLLOWING");
/* 3829 */       return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_FOLLOWING, null);
/*      */     } 
/* 3831 */     if (readIf("CURRENT")) {
/* 3832 */       read(66);
/* 3833 */       return new WindowFrameBound(WindowFrameBoundType.CURRENT_ROW, null);
/*      */     } 
/* 3835 */     Expression expression = readExpression();
/* 3836 */     if (readIf("PRECEDING")) {
/* 3837 */       return new WindowFrameBound(WindowFrameBoundType.PRECEDING, expression);
/*      */     }
/* 3839 */     read("FOLLOWING");
/* 3840 */     return new WindowFrameBound(WindowFrameBoundType.FOLLOWING, expression);
/*      */   }
/*      */   
/*      */   private Expression readFunction(Schema paramSchema, String paramString) {
/* 3844 */     String str = upperName(paramString);
/* 3845 */     if (paramSchema != null) {
/* 3846 */       return readFunctionWithSchema(paramSchema, paramString, str);
/*      */     }
/* 3848 */     boolean bool = this.database.isAllowBuiltinAliasOverride();
/* 3849 */     if (bool) {
/* 3850 */       Expression expression = readUserDefinedFunctionIf(null, paramString);
/* 3851 */       if (expression != null) {
/* 3852 */         return expression;
/*      */       }
/*      */     } 
/* 3855 */     AggregateType aggregateType = Aggregate.getAggregateType(str);
/* 3856 */     if (aggregateType != null) {
/* 3857 */       return readAggregate(aggregateType, str);
/*      */     }
/* 3859 */     Expression expression2 = readBuiltinFunctionIf(str);
/* 3860 */     if (expression2 != null) {
/* 3861 */       return expression2;
/*      */     }
/* 3863 */     WindowFunction windowFunction = readWindowFunction(str);
/* 3864 */     if (windowFunction != null) {
/* 3865 */       return (Expression)windowFunction;
/*      */     }
/* 3867 */     Expression expression1 = readCompatibilityFunction(str);
/* 3868 */     if (expression1 != null) {
/* 3869 */       return expression1;
/*      */     }
/* 3871 */     if (!bool) {
/* 3872 */       expression1 = readUserDefinedFunctionIf(null, paramString);
/* 3873 */       if (expression1 != null) {
/* 3874 */         return expression1;
/*      */       }
/*      */     } 
/* 3877 */     throw DbException.get(90022, paramString);
/*      */   }
/*      */   
/*      */   private Expression readFunctionWithSchema(Schema paramSchema, String paramString1, String paramString2) {
/* 3881 */     if (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && paramSchema
/* 3882 */       .getName().equals(this.database.sysIdentifier("PG_CATALOG"))) {
/* 3883 */       FunctionsPostgreSQL functionsPostgreSQL = FunctionsPostgreSQL.getFunction(paramString2);
/* 3884 */       if (functionsPostgreSQL != null) {
/* 3885 */         return (Expression)readParameters(functionsPostgreSQL);
/*      */       }
/*      */     } 
/* 3888 */     Expression expression = readUserDefinedFunctionIf(paramSchema, paramString1);
/* 3889 */     if (expression != null) {
/* 3890 */       return expression;
/*      */     }
/* 3892 */     throw DbException.get(90022, paramString1); } private Expression readCompatibilityFunction(String paramString) { Expression expression1; boolean bool; Column column; Expression expression2; Expression expression3;
/*      */     SimpleCase.SimpleWhen simpleWhen1;
/*      */     SimpleCase.SimpleWhen simpleWhen2;
/*      */     Expression expression4;
/* 3896 */     switch (paramString) {
/*      */       
/*      */       case "ARRAY_APPEND":
/*      */       case "ARRAY_CAT":
/* 3900 */         return (Expression)new ConcatenationOperation(readExpression(), readLastArgument());
/*      */       
/*      */       case "ARRAY_GET":
/* 3903 */         return (Expression)new ArrayElementReference(readExpression(), readLastArgument());
/*      */       
/*      */       case "ARRAY_LENGTH":
/* 3906 */         return (Expression)new CardinalityExpression(readSingleArgument(), false);
/*      */       
/*      */       case "DECODE":
/* 3909 */         expression1 = readExpression();
/* 3910 */         bool = (expression1.isConstant() && !expression1.getValue(this.session).containsNull()) ? true : false;
/* 3911 */         expression2 = readNextArgument(); expression3 = readNextArgument();
/* 3912 */         simpleWhen2 = simpleWhen1 = decodeToWhen(expression1, bool, expression2, expression3);
/* 3913 */         expression4 = null;
/* 3914 */         while (readIf(109)) {
/* 3915 */           expression2 = readExpression();
/* 3916 */           if (readIf(109)) {
/* 3917 */             expression3 = readExpression();
/* 3918 */             SimpleCase.SimpleWhen simpleWhen = decodeToWhen(expression1, bool, expression2, expression3);
/* 3919 */             simpleWhen2.setWhen(simpleWhen);
/* 3920 */             simpleWhen2 = simpleWhen; continue;
/*      */           } 
/* 3922 */           expression4 = expression2;
/*      */         } 
/*      */ 
/*      */         
/* 3926 */         read(106);
/* 3927 */         return (Expression)new SimpleCase(expression1, simpleWhen1, expression4);
/*      */ 
/*      */       
/*      */       case "CASEWHEN":
/* 3931 */         return readCompatibilityCase(readExpression());
/*      */       case "NVL2":
/* 3933 */         return readCompatibilityCase((Expression)new NullPredicate(readExpression(), true, false));
/*      */ 
/*      */ 
/*      */       
/*      */       case "CONVERT":
/* 3938 */         if ((this.database.getMode()).swapConvertFunctionParameters) {
/* 3939 */           column = parseColumnWithType(null);
/* 3940 */           expression1 = readNextArgument();
/*      */         } else {
/* 3942 */           expression1 = readExpression();
/* 3943 */           read(109);
/* 3944 */           column = parseColumnWithType(null);
/*      */         } 
/* 3946 */         read(106);
/* 3947 */         return (Expression)new CastSpecification(expression1, column);
/*      */ 
/*      */       
/*      */       case "IFNULL":
/* 3951 */         return (Expression)new CoalesceFunction(0, new Expression[] { readExpression(), readLastArgument() });
/*      */       case "NVL":
/* 3953 */         return readCoalesceFunction(0);
/*      */       
/*      */       case "DATABASE":
/* 3956 */         read(106);
/* 3957 */         return (Expression)new CurrentGeneralValueSpecification(0);
/*      */       
/*      */       case "CURDATE":
/*      */       case "SYSDATE":
/*      */       case "TODAY":
/* 3962 */         return readCurrentDateTimeValueFunction(0, true, paramString);
/*      */       
/*      */       case "SCHEMA":
/* 3965 */         read(106);
/* 3966 */         return (Expression)new CurrentGeneralValueSpecification(3);
/*      */       
/*      */       case "SYSTIMESTAMP":
/* 3969 */         return readCurrentDateTimeValueFunction(3, true, paramString);
/*      */       
/*      */       case "DAY":
/*      */       case "DAY_OF_MONTH":
/*      */       case "DAYOFMONTH":
/* 3974 */         return (Expression)new DateTimeFunction(0, 2, readSingleArgument(), null);
/*      */       case "DAY_OF_WEEK":
/*      */       case "DAYOFWEEK":
/* 3977 */         return (Expression)new DateTimeFunction(0, 20, readSingleArgument(), null);
/*      */       
/*      */       case "DAY_OF_YEAR":
/*      */       case "DAYOFYEAR":
/* 3981 */         return (Expression)new DateTimeFunction(0, 16, readSingleArgument(), null);
/*      */       
/*      */       case "HOUR":
/* 3984 */         return (Expression)new DateTimeFunction(0, 3, readSingleArgument(), null);
/*      */       case "ISO_DAY_OF_WEEK":
/* 3986 */         return (Expression)new DateTimeFunction(0, 17, 
/* 3987 */             readSingleArgument(), null);
/*      */       case "ISO_WEEK":
/* 3989 */         return (Expression)new DateTimeFunction(0, 18, readSingleArgument(), null);
/*      */       
/*      */       case "ISO_YEAR":
/* 3992 */         return (Expression)new DateTimeFunction(0, 19, readSingleArgument(), null);
/*      */       
/*      */       case "MINUTE":
/* 3995 */         return (Expression)new DateTimeFunction(0, 4, readSingleArgument(), null);
/*      */       case "MONTH":
/* 3997 */         return (Expression)new DateTimeFunction(0, 1, readSingleArgument(), null);
/*      */       case "QUARTER":
/* 3999 */         return (Expression)new DateTimeFunction(0, 12, readSingleArgument(), null);
/*      */       
/*      */       case "SECOND":
/* 4002 */         return (Expression)new DateTimeFunction(0, 5, readSingleArgument(), null);
/*      */       case "WEEK":
/* 4004 */         return (Expression)new DateTimeFunction(0, 21, readSingleArgument(), null);
/*      */       case "YEAR":
/* 4006 */         return (Expression)new DateTimeFunction(0, 0, readSingleArgument(), null);
/*      */       
/*      */       case "CURTIME":
/* 4009 */         return readCurrentDateTimeValueFunction(2, true, "CURTIME");
/*      */       case "SYSTIME":
/* 4011 */         read(106);
/* 4012 */         return readCurrentDateTimeValueFunction(2, false, "SYSTIME");
/*      */       
/*      */       case "NOW":
/* 4015 */         return readCurrentDateTimeValueFunction(4, true, "NOW");
/*      */       
/*      */       case "INSTR":
/* 4018 */         expression1 = readExpression();
/* 4019 */         return (Expression)new StringFunction(readNextArgument(), expression1, readIfArgument(), 0);
/*      */ 
/*      */       
/*      */       case "POSITION":
/* 4023 */         expression1 = readConcat();
/* 4024 */         if (!readIf(109)) {
/* 4025 */           read(41);
/*      */         }
/* 4027 */         return (Expression)new StringFunction(expression1, readSingleArgument(), null, 0);
/*      */ 
/*      */       
/*      */       case "LCASE":
/* 4031 */         return (Expression)new StringFunction1(readSingleArgument(), 1);
/*      */       
/*      */       case "SUBSTR":
/* 4034 */         return readSubstringFunction();
/*      */       
/*      */       case "LTRIM":
/* 4037 */         return (Expression)new TrimFunction(readSingleArgument(), null, 1);
/*      */       case "RTRIM":
/* 4039 */         return (Expression)new TrimFunction(readSingleArgument(), null, 2);
/*      */       
/*      */       case "UCASE":
/* 4042 */         return (Expression)new StringFunction1(readSingleArgument(), 0);
/*      */       
/*      */       case "CURRVAL":
/* 4045 */         return readCompatibilitySequenceValueFunction(true);
/*      */       case "NEXTVAL":
/* 4047 */         return readCompatibilitySequenceValueFunction(false);
/*      */     } 
/* 4049 */     return null; }
/*      */ 
/*      */ 
/*      */   
/*      */   private <T extends org.h2.expression.ExpressionWithVariableParameters> T readParameters(T paramT) {
/* 4054 */     if (!readIf(106)) {
/*      */       do {
/* 4056 */         paramT.addParameter(readExpression());
/* 4057 */       } while (readIfMore());
/*      */     }
/* 4059 */     paramT.doneWithParameters();
/* 4060 */     return paramT;
/*      */   }
/*      */   
/*      */   private SimpleCase.SimpleWhen decodeToWhen(Expression paramExpression1, boolean paramBoolean, Expression paramExpression2, Expression paramExpression3) {
/*      */     Comparison comparison;
/* 4065 */     if (!paramBoolean && (!paramExpression2.isConstant() || paramExpression2.getValue(this.session).containsNull())) {
/* 4066 */       comparison = new Comparison(6, paramExpression1, paramExpression2, true);
/*      */     }
/* 4068 */     return new SimpleCase.SimpleWhen((Expression)comparison, paramExpression3);
/*      */   }
/*      */   
/*      */   private Expression readCompatibilityCase(Expression paramExpression) {
/* 4072 */     return (Expression)new SearchedCase(new Expression[] { paramExpression, readNextArgument(), readLastArgument() });
/*      */   }
/*      */   
/*      */   private Expression readCompatibilitySequenceValueFunction(boolean paramBoolean) {
/* 4076 */     Expression expression1 = readExpression(), expression2 = readIf(109) ? readExpression() : null;
/* 4077 */     read(106);
/* 4078 */     return (Expression)new CompatibilitySequenceValueFunction(expression1, expression2, paramBoolean); } private Expression readBuiltinFunctionIf(String paramString) { Expression expression3; int i;
/*      */     Expression expression2;
/*      */     JsonConstructorFunction jsonConstructorFunction;
/*      */     Expression expression1, expression4, expression5;
/* 4082 */     switch (paramString) {
/*      */       case "ABS":
/* 4084 */         return (Expression)new MathFunction(readSingleArgument(), null, 0);
/*      */       case "MOD":
/* 4086 */         return (Expression)new MathFunction(readExpression(), readLastArgument(), 1);
/*      */       case "SIN":
/* 4088 */         return (Expression)new MathFunction1(readSingleArgument(), 0);
/*      */       case "COS":
/* 4090 */         return (Expression)new MathFunction1(readSingleArgument(), 1);
/*      */       case "TAN":
/* 4092 */         return (Expression)new MathFunction1(readSingleArgument(), 2);
/*      */       case "COT":
/* 4094 */         return (Expression)new MathFunction1(readSingleArgument(), 3);
/*      */       case "SINH":
/* 4096 */         return (Expression)new MathFunction1(readSingleArgument(), 4);
/*      */       case "COSH":
/* 4098 */         return (Expression)new MathFunction1(readSingleArgument(), 5);
/*      */       case "TANH":
/* 4100 */         return (Expression)new MathFunction1(readSingleArgument(), 6);
/*      */       case "ASIN":
/* 4102 */         return (Expression)new MathFunction1(readSingleArgument(), 7);
/*      */       case "ACOS":
/* 4104 */         return (Expression)new MathFunction1(readSingleArgument(), 8);
/*      */       case "ATAN":
/* 4106 */         return (Expression)new MathFunction1(readSingleArgument(), 9);
/*      */       case "ATAN2":
/* 4108 */         return (Expression)new MathFunction2(readExpression(), readLastArgument(), 0);
/*      */       case "LOG":
/* 4110 */         expression3 = readExpression();
/* 4111 */         if (readIf(109)) {
/* 4112 */           return (Expression)new MathFunction2(expression3, readSingleArgument(), 1);
/*      */         }
/* 4114 */         read(106);
/* 4115 */         return (Expression)new MathFunction1(expression3, 
/* 4116 */             (this.database.getMode()).logIsLogBase10 ? 10 : 11);
/*      */ 
/*      */       
/*      */       case "LOG10":
/* 4120 */         return (Expression)new MathFunction1(readSingleArgument(), 10);
/*      */       case "LN":
/* 4122 */         return (Expression)new MathFunction1(readSingleArgument(), 11);
/*      */       case "EXP":
/* 4124 */         return (Expression)new MathFunction1(readSingleArgument(), 12);
/*      */       case "POWER":
/* 4126 */         return (Expression)new MathFunction2(readExpression(), readLastArgument(), 2);
/*      */       case "SQRT":
/* 4128 */         return (Expression)new MathFunction1(readSingleArgument(), 13);
/*      */       case "FLOOR":
/* 4130 */         return (Expression)new MathFunction(readSingleArgument(), null, 2);
/*      */       case "CEIL":
/*      */       case "CEILING":
/* 4133 */         return (Expression)new MathFunction(readSingleArgument(), null, 3);
/*      */       case "ROUND":
/* 4135 */         return (Expression)new MathFunction(readExpression(), readIfArgument(), 4);
/*      */       case "ROUNDMAGIC":
/* 4137 */         return (Expression)new MathFunction(readSingleArgument(), null, 5);
/*      */       case "SIGN":
/* 4139 */         return (Expression)new MathFunction(readSingleArgument(), null, 6);
/*      */       case "TRUNC":
/*      */       case "TRUNCATE":
/* 4142 */         return (Expression)new MathFunction(readExpression(), readIfArgument(), 7);
/*      */       case "DEGREES":
/* 4144 */         return (Expression)new MathFunction1(readSingleArgument(), 14);
/*      */       case "RADIANS":
/* 4146 */         return (Expression)new MathFunction1(readSingleArgument(), 15);
/*      */       case "BITAND":
/* 4148 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 0);
/*      */       case "BITOR":
/* 4150 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 1);
/*      */       case "BITXOR":
/* 4152 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 2);
/*      */       case "BITNOT":
/* 4154 */         return (Expression)new BitFunction(readSingleArgument(), null, 3);
/*      */       case "BITNAND":
/* 4156 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 4);
/*      */       case "BITNOR":
/* 4158 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 5);
/*      */       case "BITXNOR":
/* 4160 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 6);
/*      */       case "BITGET":
/* 4162 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 7);
/*      */       case "BITCOUNT":
/* 4164 */         return (Expression)new BitFunction(readSingleArgument(), null, 8);
/*      */       case "LSHIFT":
/* 4166 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 9);
/*      */       case "RSHIFT":
/* 4168 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 10);
/*      */       case "ULSHIFT":
/* 4170 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 11);
/*      */       case "URSHIFT":
/* 4172 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 12);
/*      */       case "ROTATELEFT":
/* 4174 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 13);
/*      */       case "ROTATERIGHT":
/* 4176 */         return (Expression)new BitFunction(readExpression(), readLastArgument(), 14);
/*      */       case "EXTRACT":
/* 4178 */         i = readDateTimeField();
/* 4179 */         read(35);
/* 4180 */         return (Expression)new DateTimeFunction(0, i, readSingleArgument(), null);
/*      */       
/*      */       case "DATE_TRUNC":
/* 4183 */         return (Expression)new DateTimeFunction(1, readDateTimeField(), readLastArgument(), null);
/*      */       case "DATEADD":
/*      */       case "TIMESTAMPADD":
/* 4186 */         return (Expression)new DateTimeFunction(2, readDateTimeField(), readNextArgument(), 
/* 4187 */             readLastArgument());
/*      */       case "DATEDIFF":
/*      */       case "TIMESTAMPDIFF":
/* 4190 */         return (Expression)new DateTimeFunction(3, readDateTimeField(), readNextArgument(), 
/* 4191 */             readLastArgument());
/*      */       case "FORMATDATETIME":
/* 4193 */         return readDateTimeFormatFunction(0);
/*      */       case "PARSEDATETIME":
/* 4195 */         return readDateTimeFormatFunction(1);
/*      */       case "DAYNAME":
/* 4197 */         return (Expression)new DayMonthNameFunction(readSingleArgument(), 0);
/*      */       case "MONTHNAME":
/* 4199 */         return (Expression)new DayMonthNameFunction(readSingleArgument(), 1);
/*      */       case "CARDINALITY":
/* 4201 */         return (Expression)new CardinalityExpression(readSingleArgument(), false);
/*      */       case "ARRAY_MAX_CARDINALITY":
/* 4203 */         return (Expression)new CardinalityExpression(readSingleArgument(), true);
/*      */       case "LOCATE":
/* 4205 */         return (Expression)new StringFunction(readExpression(), readNextArgument(), readIfArgument(), 0);
/*      */       case "INSERT":
/* 4207 */         return (Expression)new StringFunction(readExpression(), readNextArgument(), readNextArgument(), readLastArgument(), 1);
/*      */       
/*      */       case "REPLACE":
/* 4210 */         return (Expression)new StringFunction(readExpression(), readNextArgument(), readIfArgument(), 2);
/*      */       case "LPAD":
/* 4212 */         return (Expression)new StringFunction(readExpression(), readNextArgument(), readIfArgument(), 3);
/*      */       case "RPAD":
/* 4214 */         return (Expression)new StringFunction(readExpression(), readNextArgument(), readIfArgument(), 4);
/*      */       case "TRANSLATE":
/* 4216 */         return (Expression)new StringFunction(readExpression(), readNextArgument(), readLastArgument(), 5);
/*      */       
/*      */       case "UPPER":
/* 4219 */         return (Expression)new StringFunction1(readSingleArgument(), 0);
/*      */       case "LOWER":
/* 4221 */         return (Expression)new StringFunction1(readSingleArgument(), 1);
/*      */       case "ASCII":
/* 4223 */         return (Expression)new StringFunction1(readSingleArgument(), 2);
/*      */       case "CHAR":
/*      */       case "CHR":
/* 4226 */         return (Expression)new StringFunction1(readSingleArgument(), 3);
/*      */       case "STRINGENCODE":
/* 4228 */         return (Expression)new StringFunction1(readSingleArgument(), 4);
/*      */       case "STRINGDECODE":
/* 4230 */         return (Expression)new StringFunction1(readSingleArgument(), 5);
/*      */       case "STRINGTOUTF8":
/* 4232 */         return (Expression)new StringFunction1(readSingleArgument(), 6);
/*      */       case "UTF8TOSTRING":
/* 4234 */         return (Expression)new StringFunction1(readSingleArgument(), 7);
/*      */       case "HEXTORAW":
/* 4236 */         return (Expression)new StringFunction1(readSingleArgument(), 8);
/*      */       case "RAWTOHEX":
/* 4238 */         return (Expression)new StringFunction1(readSingleArgument(), 9);
/*      */       case "SPACE":
/* 4240 */         return (Expression)new StringFunction1(readSingleArgument(), 10);
/*      */       case "QUOTE_IDENT":
/* 4242 */         return (Expression)new StringFunction1(readSingleArgument(), 11);
/*      */       case "SUBSTRING":
/* 4244 */         return readSubstringFunction();
/*      */       case "TO_CHAR":
/* 4246 */         expression2 = readExpression();
/* 4247 */         if (readIf(109)) {
/* 4248 */           expression4 = readExpression();
/* 4249 */           expression5 = readIf(109) ? readExpression() : null;
/*      */         } else {
/* 4251 */           expression5 = (Expression)(expression4 = null);
/*      */         } 
/* 4253 */         read(106);
/* 4254 */         return (Expression)new ToCharFunction(expression2, expression4, expression5);
/*      */       
/*      */       case "REPEAT":
/* 4257 */         return (Expression)new StringFunction2(readExpression(), readLastArgument(), 2);
/*      */       case "CHAR_LENGTH":
/*      */       case "CHARACTER_LENGTH":
/*      */       case "LENGTH":
/* 4261 */         return (Expression)new LengthFunction(readIfSingleArgument(), 0);
/*      */       case "OCTET_LENGTH":
/* 4263 */         return (Expression)new LengthFunction(readIfSingleArgument(), 1);
/*      */       case "BIT_LENGTH":
/* 4265 */         return (Expression)new LengthFunction(readIfSingleArgument(), 2);
/*      */       case "TRIM":
/* 4267 */         return readTrimFunction();
/*      */       case "REGEXP_LIKE":
/* 4269 */         return (Expression)readParameters(new RegexpFunction(0));
/*      */       case "REGEXP_REPLACE":
/* 4271 */         return (Expression)readParameters(new RegexpFunction(1));
/*      */       case "REGEXP_SUBSTR":
/* 4273 */         return (Expression)readParameters(new RegexpFunction(2));
/*      */       case "XMLATTR":
/* 4275 */         return (Expression)readParameters(new XMLFunction(0));
/*      */       case "XMLCDATA":
/* 4277 */         return (Expression)readParameters(new XMLFunction(1));
/*      */       case "XMLCOMMENT":
/* 4279 */         return (Expression)readParameters(new XMLFunction(2));
/*      */       case "XMLNODE":
/* 4281 */         return (Expression)readParameters(new XMLFunction(3));
/*      */       case "XMLSTARTDOC":
/* 4283 */         return (Expression)readParameters(new XMLFunction(4));
/*      */       case "XMLTEXT":
/* 4285 */         return (Expression)readParameters(new XMLFunction(5));
/*      */       case "TRIM_ARRAY":
/* 4287 */         return (Expression)new ArrayFunction(readExpression(), readLastArgument(), null, 0);
/*      */       case "ARRAY_CONTAINS":
/* 4289 */         return (Expression)new ArrayFunction(readExpression(), readLastArgument(), null, 1);
/*      */       case "ARRAY_SLICE":
/* 4291 */         return (Expression)new ArrayFunction(readExpression(), readNextArgument(), readLastArgument(), 2);
/*      */       
/*      */       case "COMPRESS":
/* 4294 */         return (Expression)new CompressFunction(readExpression(), readIfArgument(), 0);
/*      */       case "EXPAND":
/* 4296 */         return (Expression)new CompressFunction(readSingleArgument(), null, 1);
/*      */       case "SOUNDEX":
/* 4298 */         return (Expression)new SoundexFunction(readSingleArgument(), null, 0);
/*      */       case "DIFFERENCE":
/* 4300 */         return (Expression)new SoundexFunction(readExpression(), readLastArgument(), 1);
/*      */       case "JSON_OBJECT":
/* 4302 */         jsonConstructorFunction = new JsonConstructorFunction(false);
/* 4303 */         if (this.currentTokenType != 106 && !readJsonObjectFunctionFlags((ExpressionWithFlags)jsonConstructorFunction, false))
/*      */           while (true) {
/* 4305 */             boolean bool = readIf(47);
/* 4306 */             jsonConstructorFunction.addParameter(readExpression());
/* 4307 */             if (bool) {
/* 4308 */               read(84);
/* 4309 */             } else if (!readIf(84)) {
/* 4310 */               read(116);
/*      */             } 
/* 4312 */             jsonConstructorFunction.addParameter(readExpression());
/* 4313 */             if (!readIf(109)) {
/* 4314 */               readJsonObjectFunctionFlags((ExpressionWithFlags)jsonConstructorFunction, false); break;
/*      */             } 
/* 4316 */           }   read(106);
/* 4317 */         jsonConstructorFunction.doneWithParameters();
/* 4318 */         return (Expression)jsonConstructorFunction;
/*      */       
/*      */       case "JSON_ARRAY":
/* 4321 */         jsonConstructorFunction = new JsonConstructorFunction(true);
/* 4322 */         jsonConstructorFunction.setFlags(1);
/* 4323 */         if (this.currentTokenType != 106 && !readJsonObjectFunctionFlags((ExpressionWithFlags)jsonConstructorFunction, true))
/*      */           while (true) {
/* 4325 */             jsonConstructorFunction.addParameter(readExpression());
/* 4326 */             if (!readIf(109)) {
/* 4327 */               readJsonObjectFunctionFlags((ExpressionWithFlags)jsonConstructorFunction, true); break;
/*      */             } 
/* 4329 */           }   read(106);
/* 4330 */         jsonConstructorFunction.doneWithParameters();
/* 4331 */         return (Expression)jsonConstructorFunction;
/*      */       
/*      */       case "ENCRYPT":
/* 4334 */         return (Expression)new CryptFunction(readExpression(), readNextArgument(), readLastArgument(), 0);
/*      */       case "DECRYPT":
/* 4336 */         return (Expression)new CryptFunction(readExpression(), readNextArgument(), readLastArgument(), 1);
/*      */       case "COALESCE":
/* 4338 */         return readCoalesceFunction(0);
/*      */       case "GREATEST":
/* 4340 */         return readCoalesceFunction(1);
/*      */       case "LEAST":
/* 4342 */         return readCoalesceFunction(2);
/*      */       case "NULLIF":
/* 4344 */         return (Expression)new NullIfFunction(readExpression(), readLastArgument());
/*      */       case "CONCAT":
/* 4346 */         return readConcatFunction(0);
/*      */       case "CONCAT_WS":
/* 4348 */         return readConcatFunction(1);
/*      */       case "HASH":
/* 4350 */         return (Expression)new HashFunction(readExpression(), readNextArgument(), readIfArgument(), 0);
/*      */       case "ORA_HASH":
/* 4352 */         expression1 = readExpression();
/* 4353 */         if (readIfMore()) {
/* 4354 */           return (Expression)new HashFunction(expression1, readExpression(), readIfArgument(), 1);
/*      */         }
/* 4356 */         return (Expression)new HashFunction(expression1, 1);
/*      */       
/*      */       case "RAND":
/*      */       case "RANDOM":
/* 4360 */         return (Expression)new RandFunction(readIfSingleArgument(), 0);
/*      */       case "SECURE_RAND":
/* 4362 */         return (Expression)new RandFunction(readSingleArgument(), 1);
/*      */       case "RANDOM_UUID":
/*      */       case "UUID":
/* 4365 */         read(106);
/* 4366 */         return (Expression)new RandFunction(null, 2);
/*      */       case "ABORT_SESSION":
/* 4368 */         return (Expression)new SessionControlFunction(readIfSingleArgument(), 0);
/*      */       case "CANCEL_SESSION":
/* 4370 */         return (Expression)new SessionControlFunction(readIfSingleArgument(), 1);
/*      */       case "AUTOCOMMIT":
/* 4372 */         read(106);
/* 4373 */         return (Expression)new SysInfoFunction(0);
/*      */       case "DATABASE_PATH":
/* 4375 */         read(106);
/* 4376 */         return (Expression)new SysInfoFunction(1);
/*      */       case "H2VERSION":
/* 4378 */         read(106);
/* 4379 */         return (Expression)new SysInfoFunction(2);
/*      */       case "LOCK_MODE":
/* 4381 */         read(106);
/* 4382 */         return (Expression)new SysInfoFunction(3);
/*      */       case "LOCK_TIMEOUT":
/* 4384 */         read(106);
/* 4385 */         return (Expression)new SysInfoFunction(4);
/*      */       case "MEMORY_FREE":
/* 4387 */         read(106);
/* 4388 */         return (Expression)new SysInfoFunction(5);
/*      */       case "MEMORY_USED":
/* 4390 */         read(106);
/* 4391 */         return (Expression)new SysInfoFunction(6);
/*      */       case "READONLY":
/* 4393 */         read(106);
/* 4394 */         return (Expression)new SysInfoFunction(7);
/*      */       case "SESSION_ID":
/* 4396 */         read(106);
/* 4397 */         return (Expression)new SysInfoFunction(8);
/*      */       case "TRANSACTION_ID":
/* 4399 */         read(106);
/* 4400 */         return (Expression)new SysInfoFunction(9);
/*      */       case "DISK_SPACE_USED":
/* 4402 */         return (Expression)new TableInfoFunction(readIfSingleArgument(), null, 0);
/*      */       case "ESTIMATED_ENVELOPE":
/* 4404 */         return (Expression)new TableInfoFunction(readExpression(), readLastArgument(), 1);
/*      */       case "FILE_READ":
/* 4406 */         return (Expression)new FileFunction(readExpression(), readIfArgument(), 0);
/*      */       case "FILE_WRITE":
/* 4408 */         return (Expression)new FileFunction(readExpression(), readLastArgument(), 1);
/*      */       case "DATA_TYPE_SQL":
/* 4410 */         return (Expression)new DataTypeSQLFunction(readExpression(), readNextArgument(), readNextArgument(), 
/* 4411 */             readLastArgument());
/*      */       case "DB_OBJECT_ID":
/* 4413 */         return (Expression)new DBObjectFunction(readExpression(), readNextArgument(), readIfArgument(), 0);
/*      */       
/*      */       case "DB_OBJECT_SQL":
/* 4416 */         return (Expression)new DBObjectFunction(readExpression(), readNextArgument(), readIfArgument(), 1);
/*      */       
/*      */       case "CSVWRITE":
/* 4419 */         return (Expression)readParameters(new CSVWriteFunction());
/*      */       case "SIGNAL":
/* 4421 */         return (Expression)new SignalFunction(readExpression(), readLastArgument());
/*      */       case "TRUNCATE_VALUE":
/* 4423 */         return (Expression)new TruncateValueFunction(readExpression(), readNextArgument(), readLastArgument());
/*      */       case "ZERO":
/* 4425 */         read(106);
/* 4426 */         return (Expression)ValueExpression.get((Value)ValueInteger.get(0));
/*      */       case "PI":
/* 4428 */         read(106);
/* 4429 */         return (Expression)ValueExpression.get((Value)ValueDouble.get(Math.PI));
/*      */     } 
/* 4431 */     ModeFunction modeFunction = ModeFunction.getFunction(this.database, paramString);
/* 4432 */     return (modeFunction != null) ? (Expression)readParameters(modeFunction) : null; }
/*      */ 
/*      */   
/*      */   private Expression readDateTimeFormatFunction(int paramInt) {
/* 4436 */     DateTimeFormatFunction dateTimeFormatFunction = new DateTimeFormatFunction(paramInt);
/* 4437 */     dateTimeFormatFunction.addParameter(readExpression());
/* 4438 */     read(109);
/* 4439 */     dateTimeFormatFunction.addParameter(readExpression());
/* 4440 */     if (readIf(109)) {
/* 4441 */       dateTimeFormatFunction.addParameter(readExpression());
/* 4442 */       if (readIf(109)) {
/* 4443 */         dateTimeFormatFunction.addParameter(readExpression());
/*      */       }
/*      */     } 
/* 4446 */     read(106);
/* 4447 */     dateTimeFormatFunction.doneWithParameters();
/* 4448 */     return (Expression)dateTimeFormatFunction;
/*      */   }
/*      */   private Expression readTrimFunction() {
/*      */     byte b;
/*      */     Expression expression1;
/* 4453 */     boolean bool = false;
/* 4454 */     if (readIf("LEADING")) {
/* 4455 */       b = 1;
/* 4456 */       bool = true;
/* 4457 */     } else if (readIf("TRAILING")) {
/* 4458 */       b = 2;
/* 4459 */       bool = true;
/*      */     } else {
/* 4461 */       bool = readIf("BOTH");
/* 4462 */       b = 3;
/*      */     } 
/* 4464 */     Expression expression2 = null;
/* 4465 */     if (bool) {
/* 4466 */       if (!readIf(35)) {
/* 4467 */         expression2 = readExpression();
/* 4468 */         read(35);
/*      */       } 
/* 4470 */       expression1 = readExpression();
/*      */     }
/* 4472 */     else if (readIf(35)) {
/* 4473 */       expression1 = readExpression();
/*      */     } else {
/* 4475 */       expression1 = readExpression();
/* 4476 */       if (readIf(35)) {
/* 4477 */         expression2 = expression1;
/* 4478 */         expression1 = readExpression();
/* 4479 */       } else if (readIf(109)) {
/* 4480 */         expression2 = readExpression();
/*      */       } 
/*      */     } 
/*      */     
/* 4484 */     read(106);
/* 4485 */     return (Expression)new TrimFunction(expression1, expression2, b);
/*      */   }
/*      */   
/*      */   private ArrayTableFunction readUnnestFunction() {
/* 4489 */     ArrayTableFunction arrayTableFunction = new ArrayTableFunction(0);
/* 4490 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/* 4491 */     if (!readIf(106)) {
/* 4492 */       byte b = 0;
/*      */       do {
/* 4494 */         Expression expression = readExpression();
/* 4495 */         TypeInfo typeInfo = TypeInfo.TYPE_NULL;
/* 4496 */         if (expression.isConstant()) {
/* 4497 */           expression = expression.optimize(this.session);
/* 4498 */           TypeInfo typeInfo1 = expression.getType();
/* 4499 */           if (typeInfo1.getValueType() == 40) {
/* 4500 */             typeInfo = (TypeInfo)typeInfo1.getExtTypeInfo();
/*      */           }
/*      */         } 
/* 4503 */         arrayTableFunction.addParameter(expression);
/* 4504 */         arrayList.add(new Column("C" + ++b, typeInfo));
/* 4505 */       } while (readIfMore());
/*      */     } 
/* 4507 */     if (readIf(89)) {
/* 4508 */       read("ORDINALITY");
/* 4509 */       arrayList.add(new Column("NORD", TypeInfo.TYPE_INTEGER));
/*      */     } 
/* 4511 */     arrayTableFunction.setColumns(arrayList);
/* 4512 */     arrayTableFunction.doneWithParameters();
/* 4513 */     return arrayTableFunction;
/*      */   }
/*      */   
/*      */   private ArrayTableFunction readTableFunction(int paramInt) {
/* 4517 */     ArrayTableFunction arrayTableFunction = new ArrayTableFunction(paramInt);
/* 4518 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/*      */     while (true) {
/* 4520 */       arrayList.add(parseColumnWithType(readIdentifier()));
/* 4521 */       read(95);
/* 4522 */       arrayTableFunction.addParameter(readExpression());
/* 4523 */       if (!readIfMore()) {
/* 4524 */         arrayTableFunction.setColumns(arrayList);
/* 4525 */         arrayTableFunction.doneWithParameters();
/* 4526 */         return arrayTableFunction;
/*      */       } 
/*      */     } 
/*      */   } private Expression readSingleArgument() {
/* 4530 */     Expression expression = readExpression();
/* 4531 */     read(106);
/* 4532 */     return expression;
/*      */   }
/*      */   
/*      */   private Expression readNextArgument() {
/* 4536 */     read(109);
/* 4537 */     return readExpression();
/*      */   }
/*      */   
/*      */   private Expression readLastArgument() {
/* 4541 */     read(109);
/* 4542 */     Expression expression = readExpression();
/* 4543 */     read(106);
/* 4544 */     return expression;
/*      */   }
/*      */   
/*      */   private Expression readIfSingleArgument() {
/*      */     Expression expression;
/* 4549 */     if (readIf(106)) {
/* 4550 */       expression = null;
/*      */     } else {
/* 4552 */       expression = readExpression();
/* 4553 */       read(106);
/*      */     } 
/* 4555 */     return expression;
/*      */   }
/*      */   
/*      */   private Expression readIfArgument() {
/* 4559 */     Expression expression = readIf(109) ? readExpression() : null;
/* 4560 */     read(106);
/* 4561 */     return expression;
/*      */   }
/*      */   
/*      */   private Expression readCoalesceFunction(int paramInt) {
/* 4565 */     CoalesceFunction coalesceFunction = new CoalesceFunction(paramInt);
/* 4566 */     coalesceFunction.addParameter(readExpression());
/* 4567 */     while (readIfMore()) {
/* 4568 */       coalesceFunction.addParameter(readExpression());
/*      */     }
/* 4570 */     coalesceFunction.doneWithParameters();
/* 4571 */     return (Expression)coalesceFunction;
/*      */   }
/*      */   
/*      */   private Expression readConcatFunction(int paramInt) {
/* 4575 */     ConcatFunction concatFunction = new ConcatFunction(paramInt);
/* 4576 */     concatFunction.addParameter(readExpression());
/* 4577 */     concatFunction.addParameter(readNextArgument());
/* 4578 */     if (paramInt == 1) {
/* 4579 */       concatFunction.addParameter(readNextArgument());
/*      */     }
/* 4581 */     while (readIfMore()) {
/* 4582 */       concatFunction.addParameter(readExpression());
/*      */     }
/* 4584 */     concatFunction.doneWithParameters();
/* 4585 */     return (Expression)concatFunction;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Expression readSubstringFunction() {
/* 4596 */     SubstringFunction substringFunction = new SubstringFunction();
/* 4597 */     substringFunction.addParameter(readExpression());
/* 4598 */     if (readIf(35)) {
/* 4599 */       substringFunction.addParameter(readExpression());
/* 4600 */       if (readIf(33)) {
/* 4601 */         substringFunction.addParameter(readExpression());
/*      */       }
/* 4603 */     } else if (readIf(33)) {
/* 4604 */       substringFunction.addParameter((Expression)ValueExpression.get((Value)ValueInteger.get(1)));
/* 4605 */       substringFunction.addParameter(readExpression());
/*      */     } else {
/* 4607 */       read(109);
/* 4608 */       substringFunction.addParameter(readExpression());
/* 4609 */       if (readIf(109)) {
/* 4610 */         substringFunction.addParameter(readExpression());
/*      */       }
/*      */     } 
/* 4613 */     read(106);
/* 4614 */     substringFunction.doneWithParameters();
/* 4615 */     return (Expression)substringFunction;
/*      */   }
/*      */   
/*      */   private int readDateTimeField() {
/* 4619 */     int i = -1;
/* 4620 */     switch (this.currentTokenType) {
/*      */       case 2:
/* 4622 */         if (!this.token.isQuoted()) {
/* 4623 */           i = DateTimeFunction.getField(this.currentToken);
/*      */         }
/*      */         break;
/*      */       case 94:
/* 4627 */         if (this.token.value((CastDataProvider)this.session).getValueType() == 2) {
/* 4628 */           i = DateTimeFunction.getField(this.token.value((CastDataProvider)this.session).getString());
/*      */         }
/*      */         break;
/*      */       case 90:
/* 4632 */         i = 0;
/*      */         break;
/*      */       case 55:
/* 4635 */         i = 1;
/*      */         break;
/*      */       case 24:
/* 4638 */         i = 2;
/*      */         break;
/*      */       case 39:
/* 4641 */         i = 3;
/*      */         break;
/*      */       case 54:
/* 4644 */         i = 4;
/*      */         break;
/*      */       case 68:
/* 4647 */         i = 5; break;
/*      */     } 
/* 4649 */     if (i < 0) {
/* 4650 */       addExpected("date-time field");
/* 4651 */       throw getSyntaxError();
/*      */     } 
/* 4653 */     read();
/* 4654 */     return i;
/*      */   }
/*      */   
/*      */   private WindowFunction readWindowFunction(String paramString) {
/* 4658 */     WindowFunctionType windowFunctionType = WindowFunctionType.get(paramString);
/* 4659 */     if (windowFunctionType == null) {
/* 4660 */       return null;
/*      */     }
/* 4662 */     if (this.currentSelect == null) {
/* 4663 */       throw getSyntaxError();
/*      */     }
/* 4665 */     int i = WindowFunction.getMinArgumentCount(windowFunctionType);
/* 4666 */     Expression[] arrayOfExpression = null;
/* 4667 */     if (i > 0) {
/*      */       
/* 4669 */       int j = WindowFunction.getMaxArgumentCount(windowFunctionType);
/* 4670 */       arrayOfExpression = new Expression[j];
/* 4671 */       if (i == j) {
/* 4672 */         for (byte b = 0; b < i; b++) {
/* 4673 */           if (b > 0) {
/* 4674 */             read(109);
/*      */           }
/* 4676 */           arrayOfExpression[b] = readExpression();
/*      */         } 
/*      */       } else {
/* 4679 */         byte b = 0;
/* 4680 */         while (b < j && (
/* 4681 */           b <= 0 || readIf(109))) {
/*      */ 
/*      */           
/* 4684 */           arrayOfExpression[b] = readExpression();
/* 4685 */           b++;
/*      */         } 
/* 4687 */         if (b < i) {
/* 4688 */           throw getSyntaxError();
/*      */         }
/* 4690 */         if (b != j) {
/* 4691 */           arrayOfExpression = Arrays.<Expression>copyOf(arrayOfExpression, b);
/*      */         }
/*      */       } 
/*      */     } 
/* 4695 */     read(106);
/* 4696 */     WindowFunction windowFunction = new WindowFunction(windowFunctionType, this.currentSelect, arrayOfExpression);
/* 4697 */     switch (windowFunctionType) {
/*      */       case Derby:
/* 4699 */         readFromFirstOrLast(windowFunction);
/*      */       
/*      */       case HSQLDB:
/*      */       case MySQL:
/*      */       case PostgreSQL:
/*      */       case null:
/* 4705 */         readRespectOrIgnoreNulls(windowFunction);
/*      */         break;
/*      */     } 
/*      */ 
/*      */     
/* 4710 */     readOver((DataAnalysisOperation)windowFunction);
/* 4711 */     return windowFunction;
/*      */   }
/*      */   
/*      */   private void readFromFirstOrLast(WindowFunction paramWindowFunction) {
/* 4715 */     if (readIf(35) && !readIf("FIRST")) {
/* 4716 */       read("LAST");
/* 4717 */       paramWindowFunction.setFromLast(true);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void readRespectOrIgnoreNulls(WindowFunction paramWindowFunction) {
/* 4722 */     if (readIf("RESPECT")) {
/* 4723 */       read("NULLS");
/* 4724 */     } else if (readIf("IGNORE")) {
/* 4725 */       read("NULLS");
/* 4726 */       paramWindowFunction.setIgnoreNulls(true);
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean readJsonObjectFunctionFlags(ExpressionWithFlags paramExpressionWithFlags, boolean paramBoolean) {
/* 4731 */     int i = this.tokenIndex;
/* 4732 */     boolean bool = false;
/* 4733 */     int j = paramExpressionWithFlags.getFlags();
/* 4734 */     if (readIf(58)) {
/* 4735 */       if (readIf(60)) {
/* 4736 */         read(58);
/* 4737 */         j &= 0xFFFFFFFE;
/* 4738 */         bool = true;
/*      */       } else {
/* 4740 */         setTokenIndex(i);
/* 4741 */         return false;
/*      */       } 
/* 4743 */     } else if (readIf("ABSENT")) {
/* 4744 */       if (readIf(60)) {
/* 4745 */         read(58);
/* 4746 */         j |= 0x1;
/* 4747 */         bool = true;
/*      */       } else {
/* 4749 */         setTokenIndex(i);
/* 4750 */         return false;
/*      */       } 
/*      */     } 
/* 4753 */     if (!paramBoolean) {
/* 4754 */       if (readIf(89)) {
/* 4755 */         read(80);
/* 4756 */         read("KEYS");
/* 4757 */         j |= 0x2;
/* 4758 */         bool = true;
/* 4759 */       } else if (readIf("WITHOUT")) {
/* 4760 */         if (readIf(80))
/* 4761 */         { read("KEYS");
/* 4762 */           j &= 0xFFFFFFFD;
/* 4763 */           bool = true; }
/* 4764 */         else { if (bool) {
/* 4765 */             throw getSyntaxError();
/*      */           }
/* 4767 */           setTokenIndex(i);
/* 4768 */           return false; }
/*      */       
/*      */       } 
/*      */     }
/* 4772 */     if (bool) {
/* 4773 */       paramExpressionWithFlags.setFlags(j);
/*      */     }
/* 4775 */     return bool;
/*      */   }
/*      */   
/*      */   private Expression readKeywordCompatibilityFunctionOrColumn() {
/* 4779 */     boolean bool = (this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType)) ? true : false;
/* 4780 */     String str = this.currentToken;
/* 4781 */     read();
/* 4782 */     if (readIf(105))
/* 4783 */       return readCompatibilityFunction(upperName(str)); 
/* 4784 */     if (bool) {
/* 4785 */       return readIf(110) ? readTermObjectDot(str) : (Expression)new ExpressionColumn(this.database, null, null, str);
/*      */     }
/* 4787 */     throw getSyntaxError();
/*      */   }
/*      */   
/*      */   private Expression readCurrentDateTimeValueFunction(int paramInt, boolean paramBoolean, String paramString) {
/* 4791 */     int i = -1;
/* 4792 */     if (paramBoolean) {
/* 4793 */       if (paramInt != 0 && this.currentTokenType != 106) {
/* 4794 */         i = readInt();
/* 4795 */         if (i < 0 || i > 9) {
/* 4796 */           throw DbException.get(90151, new String[] { Integer.toString(i), "0", "9" });
/*      */         }
/*      */       } 
/*      */       
/* 4800 */       read(106);
/*      */     } 
/* 4802 */     if (this.database.isAllowBuiltinAliasOverride()) {
/*      */       
/* 4804 */       FunctionAlias functionAlias = this.database.getSchema(this.session.getCurrentSchemaName()).findFunction((paramString != null) ? paramString : CurrentDateTimeValueFunction.getName(paramInt));
/* 4805 */       if (functionAlias != null) {
/* 4806 */         (new Expression[1])[0] = 
/* 4807 */           (Expression)ValueExpression.get((Value)ValueInteger.get(i)); return (Expression)new JavaFunction(functionAlias, (i >= 0) ? new Expression[1] : new Expression[0]);
/*      */       } 
/*      */     } 
/*      */     
/* 4811 */     return (Expression)new CurrentDateTimeValueFunction(paramInt, i);
/*      */   }
/*      */   
/*      */   private Expression readIfWildcardRowidOrSequencePseudoColumn(String paramString1, String paramString2) {
/* 4815 */     if (readIf(108)) {
/* 4816 */       return (Expression)parseWildcard(paramString1, paramString2);
/*      */     }
/* 4818 */     if (readIf(91)) {
/* 4819 */       return (Expression)new ExpressionColumn(this.database, paramString1, paramString2);
/*      */     }
/* 4821 */     if ((this.database.getMode()).nextvalAndCurrvalPseudoColumns) {
/* 4822 */       return (Expression)readIfSequencePseudoColumn(paramString1, paramString2);
/*      */     }
/* 4824 */     return null;
/*      */   }
/*      */   
/*      */   private Wildcard parseWildcard(String paramString1, String paramString2) {
/* 4828 */     Wildcard wildcard = new Wildcard(paramString1, paramString2);
/* 4829 */     if (readIf(29)) {
/* 4830 */       read(105);
/* 4831 */       ArrayList<ExpressionColumn> arrayList = Utils.newSmallArrayList();
/*      */       while (true) {
/* 4833 */         String str1 = null, str2 = null;
/* 4834 */         String str3 = readIdentifier();
/* 4835 */         if (readIf(110)) {
/* 4836 */           str2 = str3;
/* 4837 */           str3 = readIdentifier();
/* 4838 */           if (readIf(110)) {
/* 4839 */             str1 = str2;
/* 4840 */             str2 = str3;
/* 4841 */             str3 = readIdentifier();
/* 4842 */             if (readIf(110)) {
/* 4843 */               checkDatabaseName(str1);
/* 4844 */               str1 = str2;
/* 4845 */               str2 = str3;
/* 4846 */               str3 = readIdentifier();
/*      */             } 
/*      */           } 
/*      */         } 
/* 4850 */         arrayList.add(new ExpressionColumn(this.database, str1, str2, str3));
/* 4851 */         if (!readIfMore())
/* 4852 */         { wildcard.setExceptColumns(arrayList); break; } 
/*      */       } 
/* 4854 */     }  return wildcard;
/*      */   }
/*      */   
/*      */   private SequenceValue readIfSequencePseudoColumn(String paramString1, String paramString2) {
/* 4858 */     if (paramString1 == null) {
/* 4859 */       paramString1 = this.session.getCurrentSchemaName();
/*      */     }
/* 4861 */     if (isToken("NEXTVAL")) {
/* 4862 */       Sequence sequence = findSequence(paramString1, paramString2);
/* 4863 */       if (sequence != null) {
/* 4864 */         read();
/* 4865 */         return new SequenceValue(sequence, getCurrentPrepared());
/*      */       } 
/* 4867 */     } else if (isToken("CURRVAL")) {
/* 4868 */       Sequence sequence = findSequence(paramString1, paramString2);
/* 4869 */       if (sequence != null) {
/* 4870 */         read();
/* 4871 */         return new SequenceValue(sequence);
/*      */       } 
/*      */     } 
/* 4874 */     return null;
/*      */   }
/*      */   
/*      */   private Expression readTermObjectDot(String paramString) {
/* 4878 */     Expression expression = readIfWildcardRowidOrSequencePseudoColumn(null, paramString);
/* 4879 */     if (expression != null) {
/* 4880 */       return expression;
/*      */     }
/* 4882 */     String str = readIdentifier();
/* 4883 */     if (readIf(105))
/* 4884 */       return readFunction(this.database.getSchema(paramString), str); 
/* 4885 */     if (readIf(110)) {
/* 4886 */       String str1 = paramString;
/* 4887 */       paramString = str;
/* 4888 */       expression = readIfWildcardRowidOrSequencePseudoColumn(str1, paramString);
/* 4889 */       if (expression != null) {
/* 4890 */         return expression;
/*      */       }
/* 4892 */       str = readIdentifier();
/* 4893 */       if (readIf(105)) {
/* 4894 */         checkDatabaseName(str1);
/* 4895 */         return readFunction(this.database.getSchema(paramString), str);
/* 4896 */       }  if (readIf(110)) {
/* 4897 */         checkDatabaseName(str1);
/* 4898 */         str1 = paramString;
/* 4899 */         paramString = str;
/* 4900 */         expression = readIfWildcardRowidOrSequencePseudoColumn(str1, paramString);
/* 4901 */         if (expression != null) {
/* 4902 */           return expression;
/*      */         }
/* 4904 */         str = readIdentifier();
/*      */       } 
/* 4906 */       return (Expression)new ExpressionColumn(this.database, str1, paramString, str);
/*      */     } 
/* 4908 */     return (Expression)new ExpressionColumn(this.database, null, paramString, str);
/*      */   }
/*      */   
/*      */   private void checkDatabaseName(String paramString) {
/* 4912 */     if (!this.database.getIgnoreCatalogs() && !equalsToken(this.database.getShortName(), paramString))
/* 4913 */       throw DbException.get(90013, paramString); 
/*      */   }
/*      */   
/*      */   private Parameter readParameter() {
/*      */     Parameter parameter;
/* 4918 */     int i = ((Token.ParameterToken)this.token).index();
/* 4919 */     read();
/*      */     
/* 4921 */     if (this.parameters == null) {
/* 4922 */       this.parameters = Utils.newSmallArrayList();
/*      */     }
/* 4924 */     if (i > 100000) {
/* 4925 */       throw DbException.getInvalidValueException("parameter index", Integer.valueOf(i));
/*      */     }
/* 4927 */     i--;
/* 4928 */     if (this.parameters.size() <= i) {
/* 4929 */       this.parameters.ensureCapacity(i + 1);
/* 4930 */       while (this.parameters.size() < i) {
/* 4931 */         this.parameters.add(null);
/*      */       }
/* 4933 */       parameter = new Parameter(i);
/* 4934 */       this.parameters.add(parameter);
/* 4935 */     } else if ((parameter = this.parameters.get(i)) == null) {
/* 4936 */       parameter = new Parameter(i);
/* 4937 */       this.parameters.set(i, parameter);
/*      */     } 
/* 4939 */     return parameter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Expression readTerm() {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield currentTokenType : I
/*      */     //   4: tableswitch default -> 1571, 2 -> 1583, 3 -> 1571, 4 -> 1571, 5 -> 1405, 6 -> 820, 7 -> 1571, 8 -> 1571, 9 -> 1571, 10 -> 1571, 11 -> 1244, 12 -> 1256, 13 -> 1571, 14 -> 1571, 15 -> 1571, 16 -> 1302, 17 -> 1308, 18 -> 1328, 19 -> 1334, 20 -> 1340, 21 -> 1346, 22 -> 1366, 23 -> 1386, 24 -> 1426, 25 -> 1571, 26 -> 1571, 27 -> 1571, 28 -> 1571, 29 -> 1571, 30 -> 1571, 31 -> 1027, 32 -> 1571, 33 -> 1571, 34 -> 1571, 35 -> 1571, 36 -> 1571, 37 -> 1571, 38 -> 1571, 39 -> 1426, 40 -> 1571, 41 -> 1571, 42 -> 1571, 43 -> 1571, 44 -> 932, 45 -> 1571, 46 -> 1571, 47 -> 1571, 48 -> 1434, 49 -> 1571, 50 -> 1571, 51 -> 1463, 52 -> 1483, 53 -> 1571, 54 -> 1426, 55 -> 1426, 56 -> 1571, 57 -> 1571, 58 -> 1102, 59 -> 1571, 60 -> 1571, 61 -> 1571, 62 -> 1571, 63 -> 1571, 64 -> 1571, 65 -> 1503, 66 -> 944, 67 -> 1049, 68 -> 1426, 69 -> 489, 70 -> 1392, 71 -> 1532, 72 -> 1405, 73 -> 1571, 74 -> 1398, 75 -> 489, 76 -> 1571, 77 -> 1016, 78 -> 1571, 79 -> 1571, 80 -> 1571, 81 -> 1038, 82 -> 1386, 83 -> 1571, 84 -> 1549, 85 -> 1156, 86 -> 1571, 87 -> 1571, 88 -> 1571, 89 -> 489, 90 -> 1426, 91 -> 1113, 92 -> 481, 93 -> 1571, 94 -> 1134, 95 -> 1571, 96 -> 1571, 97 -> 1571, 98 -> 1571, 99 -> 1571, 100 -> 1571, 101 -> 436, 102 -> 504, 103 -> 635, 104 -> 1571, 105 -> 647
/*      */     //   436: aload_0
/*      */     //   437: invokespecial read : ()V
/*      */     //   440: new org/h2/expression/Variable
/*      */     //   443: dup
/*      */     //   444: aload_0
/*      */     //   445: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   448: aload_0
/*      */     //   449: invokespecial readIdentifier : ()Ljava/lang/String;
/*      */     //   452: invokespecial <init> : (Lorg/h2/engine/SessionLocal;Ljava/lang/String;)V
/*      */     //   455: astore_1
/*      */     //   456: aload_0
/*      */     //   457: bipush #121
/*      */     //   459: invokespecial readIf : (I)Z
/*      */     //   462: ifeq -> 1666
/*      */     //   465: new org/h2/expression/function/SetFunction
/*      */     //   468: dup
/*      */     //   469: aload_1
/*      */     //   470: aload_0
/*      */     //   471: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   474: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;)V
/*      */     //   477: astore_1
/*      */     //   478: goto -> 1666
/*      */     //   481: aload_0
/*      */     //   482: invokespecial readParameter : ()Lorg/h2/expression/Parameter;
/*      */     //   485: astore_1
/*      */     //   486: goto -> 1666
/*      */     //   489: new org/h2/expression/Subquery
/*      */     //   492: dup
/*      */     //   493: aload_0
/*      */     //   494: invokespecial parseQuery : ()Lorg/h2/command/query/Query;
/*      */     //   497: invokespecial <init> : (Lorg/h2/command/query/Query;)V
/*      */     //   500: astore_1
/*      */     //   501: goto -> 1666
/*      */     //   504: aload_0
/*      */     //   505: invokespecial read : ()V
/*      */     //   508: aload_0
/*      */     //   509: getfield currentTokenType : I
/*      */     //   512: bipush #94
/*      */     //   514: if_icmpne -> 620
/*      */     //   517: aload_0
/*      */     //   518: getfield token : Lorg/h2/command/Token;
/*      */     //   521: aload_0
/*      */     //   522: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   525: invokevirtual value : (Lorg/h2/engine/CastDataProvider;)Lorg/h2/value/Value;
/*      */     //   528: invokevirtual negate : ()Lorg/h2/value/Value;
/*      */     //   531: invokestatic get : (Lorg/h2/value/Value;)Lorg/h2/expression/ValueExpression;
/*      */     //   534: astore_1
/*      */     //   535: aload_1
/*      */     //   536: invokevirtual getType : ()Lorg/h2/value/TypeInfo;
/*      */     //   539: invokevirtual getValueType : ()I
/*      */     //   542: istore_2
/*      */     //   543: iload_2
/*      */     //   544: bipush #12
/*      */     //   546: if_icmpne -> 580
/*      */     //   549: aload_1
/*      */     //   550: aload_0
/*      */     //   551: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   554: invokevirtual getValue : (Lorg/h2/engine/SessionLocal;)Lorg/h2/value/Value;
/*      */     //   557: invokevirtual getLong : ()J
/*      */     //   560: ldc2_w -2147483648
/*      */     //   563: lcmp
/*      */     //   564: ifne -> 580
/*      */     //   567: ldc_w -2147483648
/*      */     //   570: invokestatic get : (I)Lorg/h2/value/ValueInteger;
/*      */     //   573: invokestatic get : (Lorg/h2/value/Value;)Lorg/h2/expression/ValueExpression;
/*      */     //   576: astore_1
/*      */     //   577: goto -> 613
/*      */     //   580: iload_2
/*      */     //   581: bipush #13
/*      */     //   583: if_icmpne -> 613
/*      */     //   586: aload_1
/*      */     //   587: aload_0
/*      */     //   588: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   591: invokevirtual getValue : (Lorg/h2/engine/SessionLocal;)Lorg/h2/value/Value;
/*      */     //   594: invokevirtual getBigDecimal : ()Ljava/math/BigDecimal;
/*      */     //   597: getstatic org/h2/value/Value.MIN_LONG_DECIMAL : Ljava/math/BigDecimal;
/*      */     //   600: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
/*      */     //   603: ifne -> 613
/*      */     //   606: getstatic org/h2/value/ValueBigint.MIN : Lorg/h2/value/ValueBigint;
/*      */     //   609: invokestatic get : (Lorg/h2/value/Value;)Lorg/h2/expression/ValueExpression;
/*      */     //   612: astore_1
/*      */     //   613: aload_0
/*      */     //   614: invokespecial read : ()V
/*      */     //   617: goto -> 1666
/*      */     //   620: new org/h2/expression/UnaryOperation
/*      */     //   623: dup
/*      */     //   624: aload_0
/*      */     //   625: invokespecial readTerm : ()Lorg/h2/expression/Expression;
/*      */     //   628: invokespecial <init> : (Lorg/h2/expression/Expression;)V
/*      */     //   631: astore_1
/*      */     //   632: goto -> 1666
/*      */     //   635: aload_0
/*      */     //   636: invokespecial read : ()V
/*      */     //   639: aload_0
/*      */     //   640: invokespecial readTerm : ()Lorg/h2/expression/Expression;
/*      */     //   643: astore_1
/*      */     //   644: goto -> 1666
/*      */     //   647: aload_0
/*      */     //   648: invokespecial read : ()V
/*      */     //   651: aload_0
/*      */     //   652: bipush #106
/*      */     //   654: invokespecial readIf : (I)Z
/*      */     //   657: ifeq -> 670
/*      */     //   660: getstatic org/h2/value/ValueRow.EMPTY : Lorg/h2/value/ValueRow;
/*      */     //   663: invokestatic get : (Lorg/h2/value/Value;)Lorg/h2/expression/ValueExpression;
/*      */     //   666: astore_1
/*      */     //   667: goto -> 795
/*      */     //   670: aload_0
/*      */     //   671: invokespecial isQuery : ()Z
/*      */     //   674: ifeq -> 698
/*      */     //   677: new org/h2/expression/Subquery
/*      */     //   680: dup
/*      */     //   681: aload_0
/*      */     //   682: invokespecial parseQuery : ()Lorg/h2/command/query/Query;
/*      */     //   685: invokespecial <init> : (Lorg/h2/command/query/Query;)V
/*      */     //   688: astore_1
/*      */     //   689: aload_0
/*      */     //   690: bipush #106
/*      */     //   692: invokespecial read : (I)V
/*      */     //   695: goto -> 795
/*      */     //   698: aload_0
/*      */     //   699: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   702: astore_1
/*      */     //   703: aload_0
/*      */     //   704: invokespecial readIfMore : ()Z
/*      */     //   707: ifeq -> 759
/*      */     //   710: invokestatic newSmallArrayList : ()Ljava/util/ArrayList;
/*      */     //   713: astore_2
/*      */     //   714: aload_2
/*      */     //   715: aload_1
/*      */     //   716: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   719: pop
/*      */     //   720: aload_2
/*      */     //   721: aload_0
/*      */     //   722: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   725: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   728: pop
/*      */     //   729: aload_0
/*      */     //   730: invokespecial readIfMore : ()Z
/*      */     //   733: ifne -> 720
/*      */     //   736: new org/h2/expression/ExpressionList
/*      */     //   739: dup
/*      */     //   740: aload_2
/*      */     //   741: iconst_0
/*      */     //   742: anewarray org/h2/expression/Expression
/*      */     //   745: invokevirtual toArray : ([Ljava/lang/Object;)[Ljava/lang/Object;
/*      */     //   748: checkcast [Lorg/h2/expression/Expression;
/*      */     //   751: iconst_0
/*      */     //   752: invokespecial <init> : ([Lorg/h2/expression/Expression;Z)V
/*      */     //   755: astore_1
/*      */     //   756: goto -> 795
/*      */     //   759: aload_1
/*      */     //   760: instanceof org/h2/expression/BinaryOperation
/*      */     //   763: ifeq -> 795
/*      */     //   766: aload_1
/*      */     //   767: checkcast org/h2/expression/BinaryOperation
/*      */     //   770: astore_2
/*      */     //   771: aload_2
/*      */     //   772: invokevirtual getOperationType : ()Lorg/h2/expression/BinaryOperation$OpType;
/*      */     //   775: getstatic org/h2/expression/BinaryOperation$OpType.MINUS : Lorg/h2/expression/BinaryOperation$OpType;
/*      */     //   778: if_acmpne -> 795
/*      */     //   781: aload_0
/*      */     //   782: invokespecial readIntervalQualifier : ()Lorg/h2/value/TypeInfo;
/*      */     //   785: astore_3
/*      */     //   786: aload_3
/*      */     //   787: ifnull -> 795
/*      */     //   790: aload_2
/*      */     //   791: aload_3
/*      */     //   792: invokevirtual setForcedType : (Lorg/h2/value/TypeInfo;)V
/*      */     //   795: aload_0
/*      */     //   796: bipush #110
/*      */     //   798: invokespecial readIf : (I)Z
/*      */     //   801: ifeq -> 1666
/*      */     //   804: new org/h2/expression/FieldReference
/*      */     //   807: dup
/*      */     //   808: aload_1
/*      */     //   809: aload_0
/*      */     //   810: invokespecial readIdentifier : ()Ljava/lang/String;
/*      */     //   813: invokespecial <init> : (Lorg/h2/expression/Expression;Ljava/lang/String;)V
/*      */     //   816: astore_1
/*      */     //   817: goto -> 1666
/*      */     //   820: aload_0
/*      */     //   821: invokespecial read : ()V
/*      */     //   824: aload_0
/*      */     //   825: bipush #117
/*      */     //   827: invokespecial readIf : (I)Z
/*      */     //   830: ifeq -> 903
/*      */     //   833: aload_0
/*      */     //   834: bipush #118
/*      */     //   836: invokespecial readIf : (I)Z
/*      */     //   839: ifeq -> 852
/*      */     //   842: getstatic org/h2/value/ValueArray.EMPTY : Lorg/h2/value/ValueArray;
/*      */     //   845: invokestatic get : (Lorg/h2/value/Value;)Lorg/h2/expression/ValueExpression;
/*      */     //   848: astore_1
/*      */     //   849: goto -> 1666
/*      */     //   852: invokestatic newSmallArrayList : ()Ljava/util/ArrayList;
/*      */     //   855: astore_2
/*      */     //   856: aload_2
/*      */     //   857: aload_0
/*      */     //   858: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   861: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   864: pop
/*      */     //   865: aload_0
/*      */     //   866: bipush #109
/*      */     //   868: invokespecial readIf : (I)Z
/*      */     //   871: ifne -> 856
/*      */     //   874: aload_0
/*      */     //   875: bipush #118
/*      */     //   877: invokespecial read : (I)V
/*      */     //   880: new org/h2/expression/ExpressionList
/*      */     //   883: dup
/*      */     //   884: aload_2
/*      */     //   885: iconst_0
/*      */     //   886: anewarray org/h2/expression/Expression
/*      */     //   889: invokevirtual toArray : ([Ljava/lang/Object;)[Ljava/lang/Object;
/*      */     //   892: checkcast [Lorg/h2/expression/Expression;
/*      */     //   895: iconst_1
/*      */     //   896: invokespecial <init> : ([Lorg/h2/expression/Expression;Z)V
/*      */     //   899: astore_1
/*      */     //   900: goto -> 1666
/*      */     //   903: aload_0
/*      */     //   904: bipush #105
/*      */     //   906: invokespecial read : (I)V
/*      */     //   909: aload_0
/*      */     //   910: invokespecial parseQuery : ()Lorg/h2/command/query/Query;
/*      */     //   913: astore_2
/*      */     //   914: aload_0
/*      */     //   915: bipush #106
/*      */     //   917: invokespecial read : (I)V
/*      */     //   920: new org/h2/expression/ArrayConstructorByQuery
/*      */     //   923: dup
/*      */     //   924: aload_2
/*      */     //   925: invokespecial <init> : (Lorg/h2/command/query/Query;)V
/*      */     //   928: astore_1
/*      */     //   929: goto -> 1666
/*      */     //   932: aload_0
/*      */     //   933: invokespecial read : ()V
/*      */     //   936: aload_0
/*      */     //   937: invokespecial readInterval : ()Lorg/h2/expression/Expression;
/*      */     //   940: astore_1
/*      */     //   941: goto -> 1666
/*      */     //   944: aload_0
/*      */     //   945: invokespecial read : ()V
/*      */     //   948: aload_0
/*      */     //   949: bipush #105
/*      */     //   951: invokespecial read : (I)V
/*      */     //   954: aload_0
/*      */     //   955: bipush #106
/*      */     //   957: invokespecial readIf : (I)Z
/*      */     //   960: ifeq -> 973
/*      */     //   963: getstatic org/h2/value/ValueRow.EMPTY : Lorg/h2/value/ValueRow;
/*      */     //   966: invokestatic get : (Lorg/h2/value/Value;)Lorg/h2/expression/ValueExpression;
/*      */     //   969: astore_1
/*      */     //   970: goto -> 1666
/*      */     //   973: invokestatic newSmallArrayList : ()Ljava/util/ArrayList;
/*      */     //   976: astore_2
/*      */     //   977: aload_2
/*      */     //   978: aload_0
/*      */     //   979: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   982: invokevirtual add : (Ljava/lang/Object;)Z
/*      */     //   985: pop
/*      */     //   986: aload_0
/*      */     //   987: invokespecial readIfMore : ()Z
/*      */     //   990: ifne -> 977
/*      */     //   993: new org/h2/expression/ExpressionList
/*      */     //   996: dup
/*      */     //   997: aload_2
/*      */     //   998: iconst_0
/*      */     //   999: anewarray org/h2/expression/Expression
/*      */     //   1002: invokevirtual toArray : ([Ljava/lang/Object;)[Ljava/lang/Object;
/*      */     //   1005: checkcast [Lorg/h2/expression/Expression;
/*      */     //   1008: iconst_0
/*      */     //   1009: invokespecial <init> : ([Lorg/h2/expression/Expression;Z)V
/*      */     //   1012: astore_1
/*      */     //   1013: goto -> 1666
/*      */     //   1016: aload_0
/*      */     //   1017: invokespecial read : ()V
/*      */     //   1020: getstatic org/h2/expression/ValueExpression.TRUE : Lorg/h2/expression/ValueExpression;
/*      */     //   1023: astore_1
/*      */     //   1024: goto -> 1666
/*      */     //   1027: aload_0
/*      */     //   1028: invokespecial read : ()V
/*      */     //   1031: getstatic org/h2/expression/ValueExpression.FALSE : Lorg/h2/expression/ValueExpression;
/*      */     //   1034: astore_1
/*      */     //   1035: goto -> 1666
/*      */     //   1038: aload_0
/*      */     //   1039: invokespecial read : ()V
/*      */     //   1042: getstatic org/h2/expression/TypedValueExpression.UNKNOWN : Lorg/h2/expression/TypedValueExpression;
/*      */     //   1045: astore_1
/*      */     //   1046: goto -> 1666
/*      */     //   1049: aload_0
/*      */     //   1050: invokespecial read : ()V
/*      */     //   1053: aload_0
/*      */     //   1054: bipush #105
/*      */     //   1056: invokespecial readIf : (I)Z
/*      */     //   1059: ifeq -> 1068
/*      */     //   1062: aload_0
/*      */     //   1063: bipush #106
/*      */     //   1065: invokespecial read : (I)V
/*      */     //   1068: aload_0
/*      */     //   1069: getfield currentSelect : Lorg/h2/command/query/Select;
/*      */     //   1072: ifnonnull -> 1087
/*      */     //   1075: aload_0
/*      */     //   1076: getfield currentPrepared : Lorg/h2/command/Prepared;
/*      */     //   1079: ifnonnull -> 1087
/*      */     //   1082: aload_0
/*      */     //   1083: invokespecial getSyntaxError : ()Lorg/h2/message/DbException;
/*      */     //   1086: athrow
/*      */     //   1087: new org/h2/expression/Rownum
/*      */     //   1090: dup
/*      */     //   1091: aload_0
/*      */     //   1092: invokespecial getCurrentPrepared : ()Lorg/h2/command/Prepared;
/*      */     //   1095: invokespecial <init> : (Lorg/h2/command/Prepared;)V
/*      */     //   1098: astore_1
/*      */     //   1099: goto -> 1666
/*      */     //   1102: aload_0
/*      */     //   1103: invokespecial read : ()V
/*      */     //   1106: getstatic org/h2/expression/ValueExpression.NULL : Lorg/h2/expression/ValueExpression;
/*      */     //   1109: astore_1
/*      */     //   1110: goto -> 1666
/*      */     //   1113: aload_0
/*      */     //   1114: invokespecial read : ()V
/*      */     //   1117: new org/h2/expression/ExpressionColumn
/*      */     //   1120: dup
/*      */     //   1121: aload_0
/*      */     //   1122: getfield database : Lorg/h2/engine/Database;
/*      */     //   1125: aconst_null
/*      */     //   1126: aconst_null
/*      */     //   1127: invokespecial <init> : (Lorg/h2/engine/Database;Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   1130: astore_1
/*      */     //   1131: goto -> 1666
/*      */     //   1134: aload_0
/*      */     //   1135: getfield token : Lorg/h2/command/Token;
/*      */     //   1138: aload_0
/*      */     //   1139: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   1142: invokevirtual value : (Lorg/h2/engine/CastDataProvider;)Lorg/h2/value/Value;
/*      */     //   1145: invokestatic get : (Lorg/h2/value/Value;)Lorg/h2/expression/ValueExpression;
/*      */     //   1148: astore_1
/*      */     //   1149: aload_0
/*      */     //   1150: invokespecial read : ()V
/*      */     //   1153: goto -> 1666
/*      */     //   1156: aload_0
/*      */     //   1157: getfield database : Lorg/h2/engine/Database;
/*      */     //   1160: invokevirtual getMode : ()Lorg/h2/engine/Mode;
/*      */     //   1163: getfield onDuplicateKeyUpdate : Z
/*      */     //   1166: ifeq -> 1229
/*      */     //   1169: aload_0
/*      */     //   1170: getfield currentPrepared : Lorg/h2/command/Prepared;
/*      */     //   1173: instanceof org/h2/command/dml/Insert
/*      */     //   1176: ifeq -> 1198
/*      */     //   1179: aload_0
/*      */     //   1180: aload_0
/*      */     //   1181: getfield currentPrepared : Lorg/h2/command/Prepared;
/*      */     //   1184: checkcast org/h2/command/dml/Insert
/*      */     //   1187: invokevirtual getTable : ()Lorg/h2/table/Table;
/*      */     //   1190: aconst_null
/*      */     //   1191: invokespecial readOnDuplicateKeyValues : (Lorg/h2/table/Table;Lorg/h2/command/dml/Update;)Lorg/h2/expression/Expression;
/*      */     //   1194: astore_1
/*      */     //   1195: goto -> 1666
/*      */     //   1198: aload_0
/*      */     //   1199: getfield currentPrepared : Lorg/h2/command/Prepared;
/*      */     //   1202: instanceof org/h2/command/dml/Update
/*      */     //   1205: ifeq -> 1229
/*      */     //   1208: aload_0
/*      */     //   1209: getfield currentPrepared : Lorg/h2/command/Prepared;
/*      */     //   1212: checkcast org/h2/command/dml/Update
/*      */     //   1215: astore_2
/*      */     //   1216: aload_0
/*      */     //   1217: aload_2
/*      */     //   1218: invokevirtual getTable : ()Lorg/h2/table/Table;
/*      */     //   1221: aload_2
/*      */     //   1222: invokespecial readOnDuplicateKeyValues : (Lorg/h2/table/Table;Lorg/h2/command/dml/Update;)Lorg/h2/expression/Expression;
/*      */     //   1225: astore_1
/*      */     //   1226: goto -> 1666
/*      */     //   1229: new org/h2/expression/Subquery
/*      */     //   1232: dup
/*      */     //   1233: aload_0
/*      */     //   1234: invokespecial parseQuery : ()Lorg/h2/command/query/Query;
/*      */     //   1237: invokespecial <init> : (Lorg/h2/command/query/Query;)V
/*      */     //   1240: astore_1
/*      */     //   1241: goto -> 1666
/*      */     //   1244: aload_0
/*      */     //   1245: invokespecial read : ()V
/*      */     //   1248: aload_0
/*      */     //   1249: invokespecial readCase : ()Lorg/h2/expression/Expression;
/*      */     //   1252: astore_1
/*      */     //   1253: goto -> 1666
/*      */     //   1256: aload_0
/*      */     //   1257: invokespecial read : ()V
/*      */     //   1260: aload_0
/*      */     //   1261: bipush #105
/*      */     //   1263: invokespecial read : (I)V
/*      */     //   1266: aload_0
/*      */     //   1267: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   1270: astore_2
/*      */     //   1271: aload_0
/*      */     //   1272: bipush #7
/*      */     //   1274: invokespecial read : (I)V
/*      */     //   1277: aload_0
/*      */     //   1278: aconst_null
/*      */     //   1279: invokespecial parseColumnWithType : (Ljava/lang/String;)Lorg/h2/table/Column;
/*      */     //   1282: astore_3
/*      */     //   1283: aload_0
/*      */     //   1284: bipush #106
/*      */     //   1286: invokespecial read : (I)V
/*      */     //   1289: new org/h2/expression/function/CastSpecification
/*      */     //   1292: dup
/*      */     //   1293: aload_2
/*      */     //   1294: aload_3
/*      */     //   1295: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/table/Column;)V
/*      */     //   1298: astore_1
/*      */     //   1299: goto -> 1666
/*      */     //   1302: aload_0
/*      */     //   1303: iconst_0
/*      */     //   1304: invokespecial readCurrentGeneralValueSpecification : (I)Lorg/h2/expression/Expression;
/*      */     //   1307: areturn
/*      */     //   1308: aload_0
/*      */     //   1309: invokespecial read : ()V
/*      */     //   1312: aload_0
/*      */     //   1313: iconst_0
/*      */     //   1314: aload_0
/*      */     //   1315: bipush #105
/*      */     //   1317: invokespecial readIf : (I)Z
/*      */     //   1320: aconst_null
/*      */     //   1321: invokespecial readCurrentDateTimeValueFunction : (IZLjava/lang/String;)Lorg/h2/expression/Expression;
/*      */     //   1324: astore_1
/*      */     //   1325: goto -> 1666
/*      */     //   1328: aload_0
/*      */     //   1329: iconst_1
/*      */     //   1330: invokespecial readCurrentGeneralValueSpecification : (I)Lorg/h2/expression/Expression;
/*      */     //   1333: areturn
/*      */     //   1334: aload_0
/*      */     //   1335: iconst_2
/*      */     //   1336: invokespecial readCurrentGeneralValueSpecification : (I)Lorg/h2/expression/Expression;
/*      */     //   1339: areturn
/*      */     //   1340: aload_0
/*      */     //   1341: iconst_3
/*      */     //   1342: invokespecial readCurrentGeneralValueSpecification : (I)Lorg/h2/expression/Expression;
/*      */     //   1345: areturn
/*      */     //   1346: aload_0
/*      */     //   1347: invokespecial read : ()V
/*      */     //   1350: aload_0
/*      */     //   1351: iconst_1
/*      */     //   1352: aload_0
/*      */     //   1353: bipush #105
/*      */     //   1355: invokespecial readIf : (I)Z
/*      */     //   1358: aconst_null
/*      */     //   1359: invokespecial readCurrentDateTimeValueFunction : (IZLjava/lang/String;)Lorg/h2/expression/Expression;
/*      */     //   1362: astore_1
/*      */     //   1363: goto -> 1666
/*      */     //   1366: aload_0
/*      */     //   1367: invokespecial read : ()V
/*      */     //   1370: aload_0
/*      */     //   1371: iconst_3
/*      */     //   1372: aload_0
/*      */     //   1373: bipush #105
/*      */     //   1375: invokespecial readIf : (I)Z
/*      */     //   1378: aconst_null
/*      */     //   1379: invokespecial readCurrentDateTimeValueFunction : (IZLjava/lang/String;)Lorg/h2/expression/Expression;
/*      */     //   1382: astore_1
/*      */     //   1383: goto -> 1666
/*      */     //   1386: aload_0
/*      */     //   1387: iconst_4
/*      */     //   1388: invokespecial readCurrentGeneralValueSpecification : (I)Lorg/h2/expression/Expression;
/*      */     //   1391: areturn
/*      */     //   1392: aload_0
/*      */     //   1393: iconst_5
/*      */     //   1394: invokespecial readCurrentGeneralValueSpecification : (I)Lorg/h2/expression/Expression;
/*      */     //   1397: areturn
/*      */     //   1398: aload_0
/*      */     //   1399: bipush #6
/*      */     //   1401: invokespecial readCurrentGeneralValueSpecification : (I)Lorg/h2/expression/Expression;
/*      */     //   1404: areturn
/*      */     //   1405: aload_0
/*      */     //   1406: invokespecial read : ()V
/*      */     //   1409: aload_0
/*      */     //   1410: bipush #105
/*      */     //   1412: invokespecial read : (I)V
/*      */     //   1415: aload_0
/*      */     //   1416: getstatic org/h2/expression/aggregate/AggregateType.ANY : Lorg/h2/expression/aggregate/AggregateType;
/*      */     //   1419: ldc_w 'ANY'
/*      */     //   1422: invokespecial readAggregate : (Lorg/h2/expression/aggregate/AggregateType;Ljava/lang/String;)Lorg/h2/expression/Expression;
/*      */     //   1425: areturn
/*      */     //   1426: aload_0
/*      */     //   1427: invokespecial readKeywordCompatibilityFunctionOrColumn : ()Lorg/h2/expression/Expression;
/*      */     //   1430: astore_1
/*      */     //   1431: goto -> 1666
/*      */     //   1434: aload_0
/*      */     //   1435: invokespecial readColumnIfNotFunction : ()Lorg/h2/expression/Expression;
/*      */     //   1438: astore_1
/*      */     //   1439: aload_1
/*      */     //   1440: ifnonnull -> 1666
/*      */     //   1443: new org/h2/expression/function/StringFunction2
/*      */     //   1446: dup
/*      */     //   1447: aload_0
/*      */     //   1448: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   1451: aload_0
/*      */     //   1452: invokespecial readLastArgument : ()Lorg/h2/expression/Expression;
/*      */     //   1455: iconst_0
/*      */     //   1456: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;I)V
/*      */     //   1459: astore_1
/*      */     //   1460: goto -> 1666
/*      */     //   1463: aload_0
/*      */     //   1464: invokespecial read : ()V
/*      */     //   1467: aload_0
/*      */     //   1468: iconst_2
/*      */     //   1469: aload_0
/*      */     //   1470: bipush #105
/*      */     //   1472: invokespecial readIf : (I)Z
/*      */     //   1475: aconst_null
/*      */     //   1476: invokespecial readCurrentDateTimeValueFunction : (IZLjava/lang/String;)Lorg/h2/expression/Expression;
/*      */     //   1479: astore_1
/*      */     //   1480: goto -> 1666
/*      */     //   1483: aload_0
/*      */     //   1484: invokespecial read : ()V
/*      */     //   1487: aload_0
/*      */     //   1488: iconst_4
/*      */     //   1489: aload_0
/*      */     //   1490: bipush #105
/*      */     //   1492: invokespecial readIf : (I)Z
/*      */     //   1495: aconst_null
/*      */     //   1496: invokespecial readCurrentDateTimeValueFunction : (IZLjava/lang/String;)Lorg/h2/expression/Expression;
/*      */     //   1499: astore_1
/*      */     //   1500: goto -> 1666
/*      */     //   1503: aload_0
/*      */     //   1504: invokespecial readColumnIfNotFunction : ()Lorg/h2/expression/Expression;
/*      */     //   1507: astore_1
/*      */     //   1508: aload_1
/*      */     //   1509: ifnonnull -> 1666
/*      */     //   1512: new org/h2/expression/function/StringFunction2
/*      */     //   1515: dup
/*      */     //   1516: aload_0
/*      */     //   1517: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   1520: aload_0
/*      */     //   1521: invokespecial readLastArgument : ()Lorg/h2/expression/Expression;
/*      */     //   1524: iconst_1
/*      */     //   1525: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;I)V
/*      */     //   1528: astore_1
/*      */     //   1529: goto -> 1666
/*      */     //   1532: aload_0
/*      */     //   1533: invokespecial readColumnIfNotFunction : ()Lorg/h2/expression/Expression;
/*      */     //   1536: astore_1
/*      */     //   1537: aload_1
/*      */     //   1538: ifnonnull -> 1666
/*      */     //   1541: aload_0
/*      */     //   1542: invokespecial readSetFunction : ()Lorg/h2/expression/Expression;
/*      */     //   1545: astore_1
/*      */     //   1546: goto -> 1666
/*      */     //   1549: aload_0
/*      */     //   1550: getfield parseDomainConstraint : Z
/*      */     //   1553: ifeq -> 1571
/*      */     //   1556: aload_0
/*      */     //   1557: invokespecial read : ()V
/*      */     //   1560: new org/h2/expression/DomainValueExpression
/*      */     //   1563: dup
/*      */     //   1564: invokespecial <init> : ()V
/*      */     //   1567: astore_1
/*      */     //   1568: goto -> 1666
/*      */     //   1571: aload_0
/*      */     //   1572: invokespecial isIdentifier : ()Z
/*      */     //   1575: ifne -> 1583
/*      */     //   1578: aload_0
/*      */     //   1579: invokespecial getSyntaxError : ()Lorg/h2/message/DbException;
/*      */     //   1582: athrow
/*      */     //   1583: aload_0
/*      */     //   1584: getfield currentToken : Ljava/lang/String;
/*      */     //   1587: astore_2
/*      */     //   1588: aload_0
/*      */     //   1589: getfield token : Lorg/h2/command/Token;
/*      */     //   1592: invokevirtual isQuoted : ()Z
/*      */     //   1595: istore_3
/*      */     //   1596: aload_0
/*      */     //   1597: invokespecial read : ()V
/*      */     //   1600: aload_0
/*      */     //   1601: bipush #105
/*      */     //   1603: invokespecial readIf : (I)Z
/*      */     //   1606: ifeq -> 1619
/*      */     //   1609: aload_0
/*      */     //   1610: aconst_null
/*      */     //   1611: aload_2
/*      */     //   1612: invokespecial readFunction : (Lorg/h2/schema/Schema;Ljava/lang/String;)Lorg/h2/expression/Expression;
/*      */     //   1615: astore_1
/*      */     //   1616: goto -> 1666
/*      */     //   1619: aload_0
/*      */     //   1620: bipush #110
/*      */     //   1622: invokespecial readIf : (I)Z
/*      */     //   1625: ifeq -> 1637
/*      */     //   1628: aload_0
/*      */     //   1629: aload_2
/*      */     //   1630: invokespecial readTermObjectDot : (Ljava/lang/String;)Lorg/h2/expression/Expression;
/*      */     //   1633: astore_1
/*      */     //   1634: goto -> 1666
/*      */     //   1637: iload_3
/*      */     //   1638: ifeq -> 1659
/*      */     //   1641: new org/h2/expression/ExpressionColumn
/*      */     //   1644: dup
/*      */     //   1645: aload_0
/*      */     //   1646: getfield database : Lorg/h2/engine/Database;
/*      */     //   1649: aconst_null
/*      */     //   1650: aconst_null
/*      */     //   1651: aload_2
/*      */     //   1652: invokespecial <init> : (Lorg/h2/engine/Database;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   1655: astore_1
/*      */     //   1656: goto -> 1666
/*      */     //   1659: aload_0
/*      */     //   1660: aload_2
/*      */     //   1661: iload_3
/*      */     //   1662: invokespecial readTermWithIdentifier : (Ljava/lang/String;Z)Lorg/h2/expression/Expression;
/*      */     //   1665: astore_1
/*      */     //   1666: aload_0
/*      */     //   1667: bipush #117
/*      */     //   1669: invokespecial readIf : (I)Z
/*      */     //   1672: ifeq -> 1694
/*      */     //   1675: new org/h2/expression/ArrayElementReference
/*      */     //   1678: dup
/*      */     //   1679: aload_1
/*      */     //   1680: aload_0
/*      */     //   1681: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   1684: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;)V
/*      */     //   1687: astore_1
/*      */     //   1688: aload_0
/*      */     //   1689: bipush #118
/*      */     //   1691: invokespecial read : (I)V
/*      */     //   1694: aload_0
/*      */     //   1695: bipush #120
/*      */     //   1697: invokespecial readIf : (I)Z
/*      */     //   1700: ifeq -> 1778
/*      */     //   1703: aload_0
/*      */     //   1704: getfield database : Lorg/h2/engine/Database;
/*      */     //   1707: invokevirtual getMode : ()Lorg/h2/engine/Mode;
/*      */     //   1710: invokevirtual getEnum : ()Lorg/h2/engine/Mode$ModeEnum;
/*      */     //   1713: getstatic org/h2/engine/Mode$ModeEnum.PostgreSQL : Lorg/h2/engine/Mode$ModeEnum;
/*      */     //   1716: if_acmpne -> 1764
/*      */     //   1719: aload_0
/*      */     //   1720: ldc_w 'PG_CATALOG'
/*      */     //   1723: invokespecial isToken : (Ljava/lang/String;)Z
/*      */     //   1726: ifeq -> 1742
/*      */     //   1729: aload_0
/*      */     //   1730: ldc_w 'PG_CATALOG'
/*      */     //   1733: invokespecial read : (Ljava/lang/String;)V
/*      */     //   1736: aload_0
/*      */     //   1737: bipush #110
/*      */     //   1739: invokespecial read : (I)V
/*      */     //   1742: aload_0
/*      */     //   1743: ldc_w 'REGCLASS'
/*      */     //   1746: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   1749: ifeq -> 1764
/*      */     //   1752: new org/h2/mode/Regclass
/*      */     //   1755: dup
/*      */     //   1756: aload_1
/*      */     //   1757: invokespecial <init> : (Lorg/h2/expression/Expression;)V
/*      */     //   1760: astore_1
/*      */     //   1761: goto -> 1778
/*      */     //   1764: new org/h2/expression/function/CastSpecification
/*      */     //   1767: dup
/*      */     //   1768: aload_1
/*      */     //   1769: aload_0
/*      */     //   1770: aconst_null
/*      */     //   1771: invokespecial parseColumnWithType : (Ljava/lang/String;)Lorg/h2/table/Column;
/*      */     //   1774: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/table/Column;)V
/*      */     //   1777: astore_1
/*      */     //   1778: aload_0
/*      */     //   1779: invokespecial readIntervalQualifier : ()Lorg/h2/value/TypeInfo;
/*      */     //   1782: astore_2
/*      */     //   1783: aload_2
/*      */     //   1784: ifnull -> 1797
/*      */     //   1787: new org/h2/expression/function/CastSpecification
/*      */     //   1790: dup
/*      */     //   1791: aload_1
/*      */     //   1792: aload_2
/*      */     //   1793: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/value/TypeInfo;)V
/*      */     //   1796: astore_1
/*      */     //   1797: aload_0
/*      */     //   1798: getfield tokenIndex : I
/*      */     //   1801: istore_3
/*      */     //   1802: aload_0
/*      */     //   1803: ldc_w 'AT'
/*      */     //   1806: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   1809: ifeq -> 1876
/*      */     //   1812: aload_0
/*      */     //   1813: ldc_w 'TIME'
/*      */     //   1816: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   1819: ifeq -> 1845
/*      */     //   1822: aload_0
/*      */     //   1823: ldc_w 'ZONE'
/*      */     //   1826: invokespecial read : (Ljava/lang/String;)V
/*      */     //   1829: new org/h2/expression/TimeZoneOperation
/*      */     //   1832: dup
/*      */     //   1833: aload_1
/*      */     //   1834: aload_0
/*      */     //   1835: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   1838: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;)V
/*      */     //   1841: astore_1
/*      */     //   1842: goto -> 1778
/*      */     //   1845: aload_0
/*      */     //   1846: ldc_w 'LOCAL'
/*      */     //   1849: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   1852: ifeq -> 1868
/*      */     //   1855: new org/h2/expression/TimeZoneOperation
/*      */     //   1858: dup
/*      */     //   1859: aload_1
/*      */     //   1860: aconst_null
/*      */     //   1861: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/expression/Expression;)V
/*      */     //   1864: astore_1
/*      */     //   1865: goto -> 1778
/*      */     //   1868: aload_0
/*      */     //   1869: iload_3
/*      */     //   1870: invokevirtual setTokenIndex : (I)V
/*      */     //   1873: goto -> 1919
/*      */     //   1876: aload_0
/*      */     //   1877: ldc_w 'FORMAT'
/*      */     //   1880: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   1883: ifeq -> 1919
/*      */     //   1886: aload_0
/*      */     //   1887: ldc_w 'JSON'
/*      */     //   1890: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   1893: ifeq -> 1911
/*      */     //   1896: new org/h2/expression/Format
/*      */     //   1899: dup
/*      */     //   1900: aload_1
/*      */     //   1901: getstatic org/h2/expression/Format$FormatEnum.JSON : Lorg/h2/expression/Format$FormatEnum;
/*      */     //   1904: invokespecial <init> : (Lorg/h2/expression/Expression;Lorg/h2/expression/Format$FormatEnum;)V
/*      */     //   1907: astore_1
/*      */     //   1908: goto -> 1778
/*      */     //   1911: aload_0
/*      */     //   1912: iload_3
/*      */     //   1913: invokevirtual setTokenIndex : (I)V
/*      */     //   1916: goto -> 1919
/*      */     //   1919: aload_1
/*      */     //   1920: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #4944	-> 0
/*      */     //   #4946	-> 436
/*      */     //   #4947	-> 440
/*      */     //   #4948	-> 456
/*      */     //   #4949	-> 465
/*      */     //   #4953	-> 481
/*      */     //   #4954	-> 486
/*      */     //   #4958	-> 489
/*      */     //   #4959	-> 501
/*      */     //   #4961	-> 504
/*      */     //   #4962	-> 508
/*      */     //   #4963	-> 517
/*      */     //   #4964	-> 535
/*      */     //   #4965	-> 543
/*      */     //   #4966	-> 554
/*      */     //   #4969	-> 567
/*      */     //   #4970	-> 580
/*      */     //   #4971	-> 591
/*      */     //   #4974	-> 606
/*      */     //   #4976	-> 613
/*      */     //   #4977	-> 617
/*      */     //   #4978	-> 620
/*      */     //   #4980	-> 632
/*      */     //   #4982	-> 635
/*      */     //   #4983	-> 639
/*      */     //   #4984	-> 644
/*      */     //   #4986	-> 647
/*      */     //   #4987	-> 651
/*      */     //   #4988	-> 660
/*      */     //   #4989	-> 670
/*      */     //   #4990	-> 677
/*      */     //   #4991	-> 689
/*      */     //   #4993	-> 698
/*      */     //   #4994	-> 703
/*      */     //   #4995	-> 710
/*      */     //   #4996	-> 714
/*      */     //   #4998	-> 720
/*      */     //   #4999	-> 729
/*      */     //   #5000	-> 736
/*      */     //   #5001	-> 756
/*      */     //   #5002	-> 766
/*      */     //   #5003	-> 771
/*      */     //   #5004	-> 781
/*      */     //   #5005	-> 786
/*      */     //   #5006	-> 790
/*      */     //   #5011	-> 795
/*      */     //   #5012	-> 804
/*      */     //   #5016	-> 820
/*      */     //   #5017	-> 824
/*      */     //   #5018	-> 833
/*      */     //   #5019	-> 842
/*      */     //   #5021	-> 852
/*      */     //   #5023	-> 856
/*      */     //   #5024	-> 865
/*      */     //   #5025	-> 874
/*      */     //   #5026	-> 880
/*      */     //   #5027	-> 900
/*      */     //   #5029	-> 903
/*      */     //   #5030	-> 909
/*      */     //   #5031	-> 914
/*      */     //   #5032	-> 920
/*      */     //   #5034	-> 929
/*      */     //   #5036	-> 932
/*      */     //   #5037	-> 936
/*      */     //   #5038	-> 941
/*      */     //   #5040	-> 944
/*      */     //   #5041	-> 948
/*      */     //   #5042	-> 954
/*      */     //   #5043	-> 963
/*      */     //   #5045	-> 973
/*      */     //   #5047	-> 977
/*      */     //   #5048	-> 986
/*      */     //   #5049	-> 993
/*      */     //   #5051	-> 1013
/*      */     //   #5054	-> 1016
/*      */     //   #5055	-> 1020
/*      */     //   #5056	-> 1024
/*      */     //   #5058	-> 1027
/*      */     //   #5059	-> 1031
/*      */     //   #5060	-> 1035
/*      */     //   #5062	-> 1038
/*      */     //   #5063	-> 1042
/*      */     //   #5064	-> 1046
/*      */     //   #5066	-> 1049
/*      */     //   #5067	-> 1053
/*      */     //   #5068	-> 1062
/*      */     //   #5070	-> 1068
/*      */     //   #5071	-> 1082
/*      */     //   #5073	-> 1087
/*      */     //   #5074	-> 1099
/*      */     //   #5076	-> 1102
/*      */     //   #5077	-> 1106
/*      */     //   #5078	-> 1110
/*      */     //   #5080	-> 1113
/*      */     //   #5081	-> 1117
/*      */     //   #5082	-> 1131
/*      */     //   #5084	-> 1134
/*      */     //   #5085	-> 1149
/*      */     //   #5086	-> 1153
/*      */     //   #5088	-> 1156
/*      */     //   #5089	-> 1169
/*      */     //   #5090	-> 1179
/*      */     //   #5091	-> 1195
/*      */     //   #5092	-> 1198
/*      */     //   #5093	-> 1208
/*      */     //   #5094	-> 1216
/*      */     //   #5095	-> 1226
/*      */     //   #5098	-> 1229
/*      */     //   #5099	-> 1241
/*      */     //   #5101	-> 1244
/*      */     //   #5102	-> 1248
/*      */     //   #5103	-> 1253
/*      */     //   #5105	-> 1256
/*      */     //   #5106	-> 1260
/*      */     //   #5107	-> 1266
/*      */     //   #5108	-> 1271
/*      */     //   #5109	-> 1277
/*      */     //   #5110	-> 1283
/*      */     //   #5111	-> 1289
/*      */     //   #5112	-> 1299
/*      */     //   #5115	-> 1302
/*      */     //   #5117	-> 1308
/*      */     //   #5118	-> 1312
/*      */     //   #5119	-> 1325
/*      */     //   #5121	-> 1328
/*      */     //   #5123	-> 1334
/*      */     //   #5125	-> 1340
/*      */     //   #5127	-> 1346
/*      */     //   #5128	-> 1350
/*      */     //   #5129	-> 1363
/*      */     //   #5131	-> 1366
/*      */     //   #5132	-> 1370
/*      */     //   #5134	-> 1383
/*      */     //   #5137	-> 1386
/*      */     //   #5139	-> 1392
/*      */     //   #5141	-> 1398
/*      */     //   #5144	-> 1405
/*      */     //   #5145	-> 1409
/*      */     //   #5146	-> 1415
/*      */     //   #5153	-> 1426
/*      */     //   #5154	-> 1431
/*      */     //   #5156	-> 1434
/*      */     //   #5157	-> 1439
/*      */     //   #5158	-> 1443
/*      */     //   #5162	-> 1463
/*      */     //   #5163	-> 1467
/*      */     //   #5164	-> 1480
/*      */     //   #5166	-> 1483
/*      */     //   #5167	-> 1487
/*      */     //   #5169	-> 1500
/*      */     //   #5171	-> 1503
/*      */     //   #5172	-> 1508
/*      */     //   #5173	-> 1512
/*      */     //   #5177	-> 1532
/*      */     //   #5178	-> 1537
/*      */     //   #5179	-> 1541
/*      */     //   #5183	-> 1549
/*      */     //   #5184	-> 1556
/*      */     //   #5185	-> 1560
/*      */     //   #5186	-> 1568
/*      */     //   #5190	-> 1571
/*      */     //   #5191	-> 1578
/*      */     //   #5195	-> 1583
/*      */     //   #5196	-> 1588
/*      */     //   #5197	-> 1596
/*      */     //   #5198	-> 1600
/*      */     //   #5199	-> 1609
/*      */     //   #5200	-> 1619
/*      */     //   #5201	-> 1628
/*      */     //   #5202	-> 1637
/*      */     //   #5203	-> 1641
/*      */     //   #5205	-> 1659
/*      */     //   #5209	-> 1666
/*      */     //   #5210	-> 1675
/*      */     //   #5211	-> 1688
/*      */     //   #5213	-> 1694
/*      */     //   #5214	-> 1703
/*      */     //   #5216	-> 1719
/*      */     //   #5217	-> 1729
/*      */     //   #5218	-> 1736
/*      */     //   #5220	-> 1742
/*      */     //   #5221	-> 1752
/*      */     //   #5222	-> 1761
/*      */     //   #5225	-> 1764
/*      */     //   #5228	-> 1778
/*      */     //   #5229	-> 1783
/*      */     //   #5230	-> 1787
/*      */     //   #5232	-> 1797
/*      */     //   #5233	-> 1802
/*      */     //   #5234	-> 1812
/*      */     //   #5235	-> 1822
/*      */     //   #5236	-> 1829
/*      */     //   #5237	-> 1842
/*      */     //   #5238	-> 1845
/*      */     //   #5239	-> 1855
/*      */     //   #5240	-> 1865
/*      */     //   #5242	-> 1868
/*      */     //   #5244	-> 1876
/*      */     //   #5245	-> 1886
/*      */     //   #5246	-> 1896
/*      */     //   #5247	-> 1908
/*      */     //   #5249	-> 1911
/*      */     //   #5254	-> 1919
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Expression readCurrentGeneralValueSpecification(int paramInt) {
/* 5258 */     read();
/* 5259 */     if (readIf(105)) {
/* 5260 */       read(106);
/*      */     }
/* 5262 */     return (Expression)new CurrentGeneralValueSpecification(paramInt);
/*      */   }
/*      */   
/*      */   private Expression readColumnIfNotFunction() {
/* 5266 */     boolean bool = (this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType)) ? true : false;
/* 5267 */     String str = this.currentToken;
/* 5268 */     read();
/* 5269 */     if (readIf(105))
/* 5270 */       return null; 
/* 5271 */     if (bool) {
/* 5272 */       return readIf(110) ? readTermObjectDot(str) : (Expression)new ExpressionColumn(this.database, null, null, str);
/*      */     }
/* 5274 */     throw getSyntaxError();
/*      */   }
/*      */   
/*      */   private Expression readSetFunction() {
/* 5278 */     SetFunction setFunction = new SetFunction(readExpression(), readLastArgument());
/* 5279 */     if (this.database.isAllowBuiltinAliasOverride()) {
/* 5280 */       FunctionAlias functionAlias = this.database.getSchema(this.session.getCurrentSchemaName()).findFunction(setFunction
/* 5281 */           .getName());
/* 5282 */       if (functionAlias != null) {
/* 5283 */         return (Expression)new JavaFunction(functionAlias, new Expression[] { setFunction
/* 5284 */               .getSubexpression(0), setFunction.getSubexpression(1) });
/*      */       }
/*      */     } 
/* 5287 */     return (Expression)setFunction;
/*      */   }
/*      */   
/*      */   private Expression readOnDuplicateKeyValues(Table paramTable, Update paramUpdate) {
/* 5291 */     read();
/* 5292 */     read(105);
/* 5293 */     Column column = readTableColumn(new TableFilter(this.session, paramTable, null, this.rightsChecked, null, 0, null));
/* 5294 */     read(106);
/* 5295 */     return (Expression)new OnDuplicateKeyValues(column, paramUpdate);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Expression readTermWithIdentifier(String paramString, boolean paramBoolean) {
/* 5305 */     switch (paramString.charAt(0) & 0xFFDF) {
/*      */       case 67:
/* 5307 */         if (equalsToken("CURRENT", paramString)) {
/* 5308 */           int i = this.tokenIndex;
/* 5309 */           if (readIf(84) && readIf(33)) {
/* 5310 */             return (Expression)new SequenceValue(readSequence());
/*      */           }
/* 5312 */           setTokenIndex(i);
/* 5313 */           if (this.database.getMode().getEnum() == Mode.ModeEnum.DB2) {
/* 5314 */             return parseDB2SpecialRegisters(paramString);
/*      */           }
/*      */         } 
/*      */         break;
/*      */       case 68:
/* 5319 */         if (this.currentTokenType == 94 && this.token.value((CastDataProvider)this.session).getValueType() == 2 && (
/* 5320 */           equalsToken("DATE", paramString) || equalsToken("D", paramString))) {
/* 5321 */           String str = this.token.value((CastDataProvider)this.session).getString();
/* 5322 */           read();
/* 5323 */           return (Expression)ValueExpression.get((Value)ValueDate.parse(str));
/*      */         } 
/*      */         break;
/*      */       case 69:
/* 5327 */         if (this.currentTokenType == 94 && this.token.value((CastDataProvider)this.session).getValueType() == 2 && 
/* 5328 */           equalsToken("E", paramString)) {
/* 5329 */           String str = this.token.value((CastDataProvider)this.session).getString();
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 5334 */           str = StringUtils.replaceAll(str, "\\\\", "\\");
/* 5335 */           read();
/* 5336 */           return (Expression)ValueExpression.get(ValueVarchar.get(str));
/*      */         } 
/*      */         break;
/*      */       case 71:
/* 5340 */         if (this.currentTokenType == 94) {
/* 5341 */           int i = this.token.value((CastDataProvider)this.session).getValueType();
/* 5342 */           if (i == 2 && equalsToken("GEOMETRY", paramString)) {
/* 5343 */             ValueExpression valueExpression = ValueExpression.get((Value)ValueGeometry.get(this.token.value((CastDataProvider)this.session).getString()));
/* 5344 */             read();
/* 5345 */             return (Expression)valueExpression;
/* 5346 */           }  if (i == 6 && equalsToken("GEOMETRY", paramString)) {
/*      */             
/* 5348 */             ValueExpression valueExpression = ValueExpression.get((Value)ValueGeometry.getFromEWKB(this.token.value((CastDataProvider)this.session).getBytesNoCopy()));
/* 5349 */             read();
/* 5350 */             return (Expression)valueExpression;
/*      */           } 
/*      */         } 
/*      */         break;
/*      */       case 74:
/* 5355 */         if (this.currentTokenType == 94) {
/* 5356 */           int i = this.token.value((CastDataProvider)this.session).getValueType();
/* 5357 */           if (i == 2 && equalsToken("JSON", paramString)) {
/* 5358 */             ValueExpression valueExpression = ValueExpression.get((Value)ValueJson.fromJson(this.token.value((CastDataProvider)this.session).getString()));
/* 5359 */             read();
/* 5360 */             return (Expression)valueExpression;
/* 5361 */           }  if (i == 6 && equalsToken("JSON", paramString)) {
/* 5362 */             ValueExpression valueExpression = ValueExpression.get((Value)ValueJson.fromJson(this.token.value((CastDataProvider)this.session).getBytesNoCopy()));
/* 5363 */             read();
/* 5364 */             return (Expression)valueExpression;
/*      */           } 
/*      */         } 
/*      */         break;
/*      */       case 78:
/* 5369 */         if (equalsToken("NEXT", paramString)) {
/* 5370 */           int i = this.tokenIndex;
/* 5371 */           if (readIf(84) && readIf(33)) {
/* 5372 */             return (Expression)new SequenceValue(readSequence(), getCurrentPrepared());
/*      */           }
/* 5374 */           setTokenIndex(i);
/*      */         } 
/*      */         break;
/*      */       case 84:
/* 5378 */         if (equalsToken("TIME", paramString)) {
/* 5379 */           if (readIf(89)) {
/* 5380 */             read("TIME");
/* 5381 */             read("ZONE");
/* 5382 */             if (this.currentTokenType != 94 || this.token.value((CastDataProvider)this.session).getValueType() != 2) {
/* 5383 */               throw getSyntaxError();
/*      */             }
/* 5385 */             String str = this.token.value((CastDataProvider)this.session).getString();
/* 5386 */             read();
/* 5387 */             return (Expression)ValueExpression.get((Value)ValueTimeTimeZone.parse(str));
/*      */           } 
/* 5389 */           boolean bool = readIf("WITHOUT");
/* 5390 */           if (bool) {
/* 5391 */             read("TIME");
/* 5392 */             read("ZONE");
/*      */           } 
/* 5394 */           if (this.currentTokenType == 94 && this.token.value((CastDataProvider)this.session).getValueType() == 2) {
/* 5395 */             String str = this.token.value((CastDataProvider)this.session).getString();
/* 5396 */             read();
/* 5397 */             return (Expression)ValueExpression.get((Value)ValueTime.parse(str));
/* 5398 */           }  if (bool)
/* 5399 */             throw getSyntaxError(); 
/*      */           break;
/*      */         } 
/* 5402 */         if (equalsToken("TIMESTAMP", paramString)) {
/* 5403 */           if (readIf(89)) {
/* 5404 */             read("TIME");
/* 5405 */             read("ZONE");
/* 5406 */             if (this.currentTokenType != 94 || this.token.value((CastDataProvider)this.session).getValueType() != 2) {
/* 5407 */               throw getSyntaxError();
/*      */             }
/* 5409 */             String str = this.token.value((CastDataProvider)this.session).getString();
/* 5410 */             read();
/* 5411 */             return (Expression)ValueExpression.get((Value)ValueTimestampTimeZone.parse(str, (CastDataProvider)this.session));
/*      */           } 
/* 5413 */           boolean bool = readIf("WITHOUT");
/* 5414 */           if (bool) {
/* 5415 */             read("TIME");
/* 5416 */             read("ZONE");
/*      */           } 
/* 5418 */           if (this.currentTokenType == 94 && this.token.value((CastDataProvider)this.session).getValueType() == 2) {
/* 5419 */             String str = this.token.value((CastDataProvider)this.session).getString();
/* 5420 */             read();
/* 5421 */             return (Expression)ValueExpression.get((Value)ValueTimestamp.parse(str, (CastDataProvider)this.session));
/* 5422 */           }  if (bool)
/* 5423 */             throw getSyntaxError(); 
/*      */           break;
/*      */         } 
/* 5426 */         if (this.currentTokenType == 94 && this.token.value((CastDataProvider)this.session).getValueType() == 2) {
/* 5427 */           if (equalsToken("T", paramString)) {
/* 5428 */             String str = this.token.value((CastDataProvider)this.session).getString();
/* 5429 */             read();
/* 5430 */             return (Expression)ValueExpression.get((Value)ValueTime.parse(str));
/* 5431 */           }  if (equalsToken("TS", paramString)) {
/* 5432 */             String str = this.token.value((CastDataProvider)this.session).getString();
/* 5433 */             read();
/* 5434 */             return (Expression)ValueExpression.get((Value)ValueTimestamp.parse(str, (CastDataProvider)this.session));
/*      */           } 
/*      */         } 
/*      */         break;
/*      */       case 85:
/* 5439 */         if (this.currentTokenType == 94 && this.token.value((CastDataProvider)this.session).getValueType() == 2 && 
/* 5440 */           equalsToken("UUID", paramString)) {
/* 5441 */           String str = this.token.value((CastDataProvider)this.session).getString();
/* 5442 */           read();
/* 5443 */           return (Expression)ValueExpression.get((Value)ValueUuid.get(str));
/*      */         } 
/*      */         break;
/*      */     } 
/* 5447 */     return (Expression)new ExpressionColumn(this.database, null, null, paramString, paramBoolean);
/*      */   }
/*      */   
/*      */   private Prepared getCurrentPrepared() {
/* 5451 */     return this.currentPrepared;
/*      */   }
/*      */   private Expression readInterval() {
/*      */     IntervalQualifier intervalQualifier;
/* 5455 */     boolean bool = readIf(102);
/* 5456 */     if (!bool) {
/* 5457 */       readIf(103);
/*      */     }
/* 5459 */     if (this.currentTokenType != 94 || this.token.value((CastDataProvider)this.session).getValueType() != 2) {
/* 5460 */       addExpected("string");
/* 5461 */       throw getSyntaxError();
/*      */     } 
/* 5463 */     String str = this.token.value((CastDataProvider)this.session).getString();
/* 5464 */     read();
/*      */     
/* 5466 */     switch (this.currentTokenType) {
/*      */       case 90:
/* 5468 */         read();
/* 5469 */         if (readIf(76)) {
/* 5470 */           read(55);
/* 5471 */           IntervalQualifier intervalQualifier1 = IntervalQualifier.YEAR_TO_MONTH; break;
/*      */         } 
/* 5473 */         intervalQualifier = IntervalQualifier.YEAR;
/*      */         break;
/*      */       
/*      */       case 55:
/* 5477 */         read();
/* 5478 */         intervalQualifier = IntervalQualifier.MONTH;
/*      */         break;
/*      */       case 24:
/* 5481 */         read();
/* 5482 */         if (readIf(76)) {
/* 5483 */           switch (this.currentTokenType) {
/*      */             case 39:
/* 5485 */               intervalQualifier = IntervalQualifier.DAY_TO_HOUR;
/*      */               break;
/*      */             case 54:
/* 5488 */               intervalQualifier = IntervalQualifier.DAY_TO_MINUTE;
/*      */               break;
/*      */             case 68:
/* 5491 */               intervalQualifier = IntervalQualifier.DAY_TO_SECOND;
/*      */               break;
/*      */             default:
/* 5494 */               throw intervalDayError();
/*      */           } 
/* 5496 */           read(); break;
/*      */         } 
/* 5498 */         intervalQualifier = IntervalQualifier.DAY;
/*      */         break;
/*      */       
/*      */       case 39:
/* 5502 */         read();
/* 5503 */         if (readIf(76)) {
/* 5504 */           switch (this.currentTokenType) {
/*      */             case 54:
/* 5506 */               intervalQualifier = IntervalQualifier.HOUR_TO_MINUTE;
/*      */               break;
/*      */             case 68:
/* 5509 */               intervalQualifier = IntervalQualifier.HOUR_TO_SECOND;
/*      */               break;
/*      */             default:
/* 5512 */               throw intervalHourError();
/*      */           } 
/* 5514 */           read(); break;
/*      */         } 
/* 5516 */         intervalQualifier = IntervalQualifier.HOUR;
/*      */         break;
/*      */       
/*      */       case 54:
/* 5520 */         read();
/* 5521 */         if (readIf(76)) {
/* 5522 */           read(68);
/* 5523 */           intervalQualifier = IntervalQualifier.MINUTE_TO_SECOND; break;
/*      */         } 
/* 5525 */         intervalQualifier = IntervalQualifier.MINUTE;
/*      */         break;
/*      */       
/*      */       case 68:
/* 5529 */         read();
/* 5530 */         intervalQualifier = IntervalQualifier.SECOND;
/*      */         break;
/*      */       default:
/* 5533 */         throw intervalQualifierError();
/*      */     } 
/*      */     try {
/* 5536 */       return (Expression)ValueExpression.get((Value)IntervalUtils.parseInterval(intervalQualifier, bool, str));
/* 5537 */     } catch (Exception exception) {
/* 5538 */       throw DbException.get(22007, exception, new String[] { "INTERVAL", str });
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private Expression parseDB2SpecialRegisters(String paramString) {
/* 5544 */     if (readIf("TIMESTAMP")) {
/* 5545 */       if (readIf(89)) {
/* 5546 */         read("TIME");
/* 5547 */         read("ZONE");
/* 5548 */         return readCurrentDateTimeValueFunction(3, 
/* 5549 */             readIf(105), null);
/*      */       } 
/* 5551 */       return readCurrentDateTimeValueFunction(4, readIf(105), null);
/*      */     } 
/* 5553 */     if (readIf("TIME"))
/*      */     {
/* 5555 */       return readCurrentDateTimeValueFunction(2, false, null); } 
/* 5556 */     if (readIf("DATE")) {
/* 5557 */       return readCurrentDateTimeValueFunction(0, false, null);
/*      */     }
/*      */     
/* 5560 */     return (Expression)new ExpressionColumn(this.database, null, null, paramString);
/*      */   }
/*      */ 
/*      */   
/*      */   private Expression readCase() {
/* 5565 */     if (readIf(86))
/* 5566 */     { SearchedCase searchedCase = new SearchedCase();
/*      */       while (true)
/* 5568 */       { Expression expression1 = readExpression();
/* 5569 */         read("THEN");
/* 5570 */         searchedCase.addParameter(expression1);
/* 5571 */         searchedCase.addParameter(readExpression());
/* 5572 */         if (!readIf(86))
/* 5573 */         { if (readIf(27)) {
/* 5574 */             searchedCase.addParameter(readExpression());
/*      */           }
/* 5576 */           searchedCase.doneWithParameters();
/* 5577 */           SearchedCase searchedCase1 = searchedCase;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 5589 */           read(28);
/* 5590 */           return (Expression)searchedCase1; }  }  }  Expression expression = readExpression(); read(86); SimpleCase.SimpleWhen simpleWhen1 = readSimpleWhenClause(expression), simpleWhen2 = simpleWhen1; while (readIf(86)) { SimpleCase.SimpleWhen simpleWhen = readSimpleWhenClause(expression); simpleWhen2.setWhen(simpleWhen); simpleWhen2 = simpleWhen; }  SimpleCase simpleCase = new SimpleCase(expression, simpleWhen1, readIf(27) ? readExpression() : null); read(28); return (Expression)simpleCase;
/*      */   }
/*      */   
/*      */   private SimpleCase.SimpleWhen readSimpleWhenClause(Expression paramExpression) {
/* 5594 */     Expression expression = readWhenOperand(paramExpression);
/* 5595 */     if (readIf(109)) {
/* 5596 */       ArrayList<Expression> arrayList = Utils.newSmallArrayList();
/* 5597 */       arrayList.add(expression);
/*      */       while (true) {
/* 5599 */         arrayList.add(readWhenOperand(paramExpression));
/* 5600 */         if (!readIf(109))
/* 5601 */         { read("THEN");
/* 5602 */           return new SimpleCase.SimpleWhen(arrayList.<Expression>toArray(new Expression[0]), readExpression()); } 
/*      */       } 
/* 5604 */     }  read("THEN");
/* 5605 */     return new SimpleCase.SimpleWhen(expression, readExpression());
/*      */   }
/*      */   
/*      */   private Expression readWhenOperand(Expression paramExpression) {
/* 5609 */     int i = this.tokenIndex;
/* 5610 */     boolean bool = readIf(57);
/* 5611 */     Expression expression = readConditionRightHandSide(paramExpression, bool, true);
/* 5612 */     if (expression == null) {
/* 5613 */       if (bool) {
/* 5614 */         setTokenIndex(i);
/*      */       }
/* 5616 */       expression = readExpression();
/*      */     } 
/* 5618 */     return expression;
/*      */   }
/*      */   
/*      */   private int readNonNegativeInt() {
/* 5622 */     int i = readInt();
/* 5623 */     if (i < 0) {
/* 5624 */       throw DbException.getInvalidValueException("non-negative integer", Integer.valueOf(i));
/*      */     }
/* 5626 */     return i;
/*      */   }
/*      */   
/*      */   private int readInt() {
/* 5630 */     boolean bool = false;
/* 5631 */     if (this.currentTokenType == 102) {
/* 5632 */       bool = true;
/* 5633 */       read();
/* 5634 */     } else if (this.currentTokenType == 103) {
/* 5635 */       read();
/*      */     } 
/* 5637 */     if (this.currentTokenType != 94) {
/* 5638 */       throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "integer");
/*      */     }
/* 5640 */     Value value = this.token.value((CastDataProvider)this.session);
/* 5641 */     if (bool)
/*      */     {
/* 5643 */       value = value.negate();
/*      */     }
/* 5645 */     int i = value.getInt();
/* 5646 */     read();
/* 5647 */     return i;
/*      */   }
/*      */   
/*      */   private long readPositiveLong() {
/* 5651 */     long l = readLong();
/* 5652 */     if (l <= 0L) {
/* 5653 */       throw DbException.getInvalidValueException("positive long", Long.valueOf(l));
/*      */     }
/* 5655 */     return l;
/*      */   }
/*      */   
/*      */   private long readLong() {
/* 5659 */     boolean bool = false;
/* 5660 */     if (this.currentTokenType == 102) {
/* 5661 */       bool = true;
/* 5662 */       read();
/* 5663 */     } else if (this.currentTokenType == 103) {
/* 5664 */       read();
/*      */     } 
/* 5666 */     if (this.currentTokenType != 94) {
/* 5667 */       throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "long");
/*      */     }
/* 5669 */     Value value = this.token.value((CastDataProvider)this.session);
/* 5670 */     if (bool)
/*      */     {
/* 5672 */       value = value.negate();
/*      */     }
/* 5674 */     long l = value.getLong();
/* 5675 */     read();
/* 5676 */     return l;
/*      */   }
/*      */   private boolean readBooleanSetting() {
/*      */     boolean bool;
/* 5680 */     switch (this.currentTokenType) {
/*      */       case 60:
/*      */       case 77:
/* 5683 */         read();
/* 5684 */         return true;
/*      */       case 31:
/* 5686 */         read();
/* 5687 */         return false;
/*      */       case 94:
/* 5689 */         bool = this.token.value((CastDataProvider)this.session).getBoolean();
/* 5690 */         read();
/* 5691 */         return bool;
/*      */     } 
/* 5693 */     if (readIf("OFF")) {
/* 5694 */       return false;
/*      */     }
/* 5696 */     if (this.expectedList != null) {
/* 5697 */       addMultipleExpected(new int[] { 60, 77, 31 });
/*      */     }
/* 5699 */     throw getSyntaxError();
/*      */   }
/*      */ 
/*      */   
/*      */   private String readString() {
/* 5704 */     int i = this.token.start();
/* 5705 */     Expression expression = readExpression();
/*      */     try {
/* 5707 */       String str = expression.optimize(this.session).getValue(this.session).getString();
/* 5708 */       if (str == null || str.length() <= 1048576) {
/* 5709 */         return str;
/*      */       }
/* 5711 */     } catch (DbException dbException) {}
/*      */     
/* 5713 */     throw DbException.getSyntaxError(this.sqlCommand, i, "character string");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String readIdentifierWithSchema(String paramString) {
/* 5719 */     String str = readIdentifier();
/* 5720 */     this.schemaName = paramString;
/* 5721 */     if (readIf(110)) {
/* 5722 */       str = readIdentifierWithSchema2(str);
/*      */     }
/* 5724 */     return str;
/*      */   }
/*      */   
/*      */   private String readIdentifierWithSchema2(String paramString) {
/* 5728 */     this.schemaName = paramString;
/* 5729 */     if ((this.database.getMode()).allowEmptySchemaValuesAsDefaultSchema && readIf(110)) {
/* 5730 */       if (equalsToken(this.schemaName, this.database.getShortName()) || this.database.getIgnoreCatalogs()) {
/* 5731 */         this.schemaName = this.session.getCurrentSchemaName();
/* 5732 */         paramString = readIdentifier();
/*      */       } 
/*      */     } else {
/* 5735 */       paramString = readIdentifier();
/* 5736 */       if (this.currentTokenType == 110 && (
/* 5737 */         equalsToken(this.schemaName, this.database.getShortName()) || this.database.getIgnoreCatalogs())) {
/* 5738 */         read();
/* 5739 */         this.schemaName = paramString;
/* 5740 */         paramString = readIdentifier();
/*      */       } 
/*      */     } 
/*      */     
/* 5744 */     return paramString;
/*      */   }
/*      */   
/*      */   private String readIdentifierWithSchema() {
/* 5748 */     return readIdentifierWithSchema(this.session.getCurrentSchemaName());
/*      */   }
/*      */   
/*      */   private String readIdentifier() {
/* 5752 */     if (!isIdentifier())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 5758 */       if (!this.session.isQuirksMode() || !isKeyword(this.currentTokenType)) {
/* 5759 */         throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "identifier");
/*      */       }
/*      */     }
/* 5762 */     String str = this.currentToken;
/* 5763 */     read();
/* 5764 */     return str;
/*      */   }
/*      */   
/*      */   private void read(String paramString) {
/* 5768 */     if (this.token.isQuoted() || !equalsToken(paramString, this.currentToken)) {
/* 5769 */       addExpected(paramString);
/* 5770 */       throw getSyntaxError();
/*      */     } 
/* 5772 */     read();
/*      */   }
/*      */   
/*      */   private void read(int paramInt) {
/* 5776 */     if (paramInt != this.currentTokenType) {
/* 5777 */       addExpected(paramInt);
/* 5778 */       throw getSyntaxError();
/*      */     } 
/* 5780 */     read();
/*      */   }
/*      */   
/*      */   private boolean readIf(String paramString) {
/* 5784 */     if (!this.token.isQuoted() && equalsToken(paramString, this.currentToken)) {
/* 5785 */       read();
/* 5786 */       return true;
/*      */     } 
/* 5788 */     addExpected(paramString);
/* 5789 */     return false;
/*      */   }
/*      */   
/*      */   private boolean readIf(int paramInt) {
/* 5793 */     if (paramInt == this.currentTokenType) {
/* 5794 */       read();
/* 5795 */       return true;
/*      */     } 
/* 5797 */     addExpected(paramInt);
/* 5798 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isToken(String paramString) {
/* 5802 */     if (!this.token.isQuoted() && equalsToken(paramString, this.currentToken)) {
/* 5803 */       return true;
/*      */     }
/* 5805 */     addExpected(paramString);
/* 5806 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isToken(int paramInt) {
/* 5810 */     if (paramInt == this.currentTokenType) {
/* 5811 */       return true;
/*      */     }
/* 5813 */     addExpected(paramInt);
/* 5814 */     return false;
/*      */   }
/*      */   
/*      */   private boolean equalsToken(String paramString1, String paramString2) {
/* 5818 */     if (paramString1 == null) {
/* 5819 */       return (paramString2 == null);
/*      */     }
/* 5821 */     return (paramString1.equals(paramString2) || (!this.identifiersToUpper && paramString1.equalsIgnoreCase(paramString2)));
/*      */   }
/*      */   
/*      */   private boolean isIdentifier() {
/* 5825 */     return (this.currentTokenType == 2 || (this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType)));
/*      */   }
/*      */   
/*      */   private void addExpected(String paramString) {
/* 5829 */     if (this.expectedList != null) {
/* 5830 */       this.expectedList.add(paramString);
/*      */     }
/*      */   }
/*      */   
/*      */   private void addExpected(int paramInt) {
/* 5835 */     if (this.expectedList != null) {
/* 5836 */       this.expectedList.add(Token.TOKENS[paramInt]);
/*      */     }
/*      */   }
/*      */   
/*      */   private void addMultipleExpected(int... paramVarArgs) {
/* 5841 */     for (int i : paramVarArgs) {
/* 5842 */       this.expectedList.add(Token.TOKENS[i]);
/*      */     }
/*      */   }
/*      */   
/*      */   private void read() {
/* 5847 */     if (this.expectedList != null) {
/* 5848 */       this.expectedList.clear();
/*      */     }
/* 5850 */     int i = this.tokens.size();
/* 5851 */     if (this.tokenIndex + 1 < i) {
/* 5852 */       this.token = this.tokens.get(++this.tokenIndex);
/* 5853 */       this.currentTokenType = this.token.tokenType();
/* 5854 */       this.currentToken = this.token.asIdentifier();
/* 5855 */       if (this.currentToken != null && this.currentToken.length() > 256) {
/* 5856 */         throw DbException.get(42622, new String[] { this.currentToken.substring(0, 32), "256" });
/*      */       }
/* 5858 */       if (this.currentTokenType == 94) {
/* 5859 */         checkLiterals();
/*      */       }
/*      */     } else {
/* 5862 */       throw getSyntaxError();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void checkLiterals() {
/* 5867 */     if (!this.literalsChecked && this.session != null && !this.session.getAllowLiterals()) {
/* 5868 */       int i = this.database.getAllowLiterals();
/* 5869 */       if (i == 0 || ((this.token instanceof Token.CharacterStringToken || this.token instanceof Token.BinaryStringToken) && i != 2))
/*      */       {
/*      */         
/* 5872 */         throw DbException.get(90116);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initialize(String paramString, ArrayList<Token> paramArrayList, boolean paramBoolean) {
/* 5878 */     if (paramString == null) {
/* 5879 */       paramString = "";
/*      */     }
/* 5881 */     this.sqlCommand = paramString;
/* 5882 */     this
/* 5883 */       .tokens = (paramArrayList == null) ? (new Tokenizer((CastDataProvider)this.database, this.identifiersToUpper, this.identifiersToLower, this.nonKeywords)).tokenize(paramString, paramBoolean) : paramArrayList;
/* 5884 */     resetTokenIndex();
/*      */   }
/*      */   
/*      */   private void resetTokenIndex() {
/* 5888 */     this.tokenIndex = -1;
/* 5889 */     this.token = null;
/* 5890 */     this.currentTokenType = -1;
/* 5891 */     this.currentToken = null;
/*      */   }
/*      */   
/*      */   void setTokenIndex(int paramInt) {
/* 5895 */     if (paramInt != this.tokenIndex) {
/* 5896 */       if (this.expectedList != null) {
/* 5897 */         this.expectedList.clear();
/*      */       }
/* 5899 */       this.token = this.tokens.get(paramInt);
/* 5900 */       this.tokenIndex = paramInt;
/* 5901 */       this.currentTokenType = this.token.tokenType();
/* 5902 */       this.currentToken = this.token.asIdentifier();
/*      */     } 
/*      */   }
/*      */   
/*      */   private static boolean isKeyword(int paramInt) {
/* 5907 */     return (paramInt >= 3 && paramInt <= 91);
/*      */   }
/*      */   
/*      */   private boolean isKeyword(String paramString) {
/* 5911 */     return ParserUtil.isKeyword(paramString, !this.identifiersToUpper);
/*      */   }
/*      */   
/*      */   private String upperName(String paramString) {
/* 5915 */     return this.identifiersToUpper ? paramString : StringUtils.toUpperEnglish(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Column parseColumnForTable(String paramString, boolean paramBoolean) {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield database : Lorg/h2/engine/Database;
/*      */     //   4: invokevirtual getMode : ()Lorg/h2/engine/Mode;
/*      */     //   7: astore #4
/*      */     //   9: aload #4
/*      */     //   11: getfield identityDataType : Z
/*      */     //   14: ifeq -> 52
/*      */     //   17: aload_0
/*      */     //   18: ldc_w 'IDENTITY'
/*      */     //   21: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   24: ifeq -> 52
/*      */     //   27: new org/h2/table/Column
/*      */     //   30: dup
/*      */     //   31: aload_1
/*      */     //   32: getstatic org/h2/value/TypeInfo.TYPE_BIGINT : Lorg/h2/value/TypeInfo;
/*      */     //   35: invokespecial <init> : (Ljava/lang/String;Lorg/h2/value/TypeInfo;)V
/*      */     //   38: astore_3
/*      */     //   39: aload_0
/*      */     //   40: aload_3
/*      */     //   41: invokespecial parseCompatibilityIdentityOptions : (Lorg/h2/table/Column;)V
/*      */     //   44: aload_3
/*      */     //   45: iconst_1
/*      */     //   46: invokevirtual setPrimaryKey : (Z)V
/*      */     //   49: goto -> 148
/*      */     //   52: aload #4
/*      */     //   54: getfield serialDataTypes : Z
/*      */     //   57: ifeq -> 97
/*      */     //   60: aload_0
/*      */     //   61: ldc_w 'BIGSERIAL'
/*      */     //   64: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   67: ifeq -> 97
/*      */     //   70: new org/h2/table/Column
/*      */     //   73: dup
/*      */     //   74: aload_1
/*      */     //   75: getstatic org/h2/value/TypeInfo.TYPE_BIGINT : Lorg/h2/value/TypeInfo;
/*      */     //   78: invokespecial <init> : (Ljava/lang/String;Lorg/h2/value/TypeInfo;)V
/*      */     //   81: astore_3
/*      */     //   82: aload_3
/*      */     //   83: new org/h2/command/ddl/SequenceOptions
/*      */     //   86: dup
/*      */     //   87: invokespecial <init> : ()V
/*      */     //   90: iconst_0
/*      */     //   91: invokevirtual setIdentityOptions : (Lorg/h2/command/ddl/SequenceOptions;Z)V
/*      */     //   94: goto -> 148
/*      */     //   97: aload #4
/*      */     //   99: getfield serialDataTypes : Z
/*      */     //   102: ifeq -> 142
/*      */     //   105: aload_0
/*      */     //   106: ldc_w 'SERIAL'
/*      */     //   109: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   112: ifeq -> 142
/*      */     //   115: new org/h2/table/Column
/*      */     //   118: dup
/*      */     //   119: aload_1
/*      */     //   120: getstatic org/h2/value/TypeInfo.TYPE_INTEGER : Lorg/h2/value/TypeInfo;
/*      */     //   123: invokespecial <init> : (Ljava/lang/String;Lorg/h2/value/TypeInfo;)V
/*      */     //   126: astore_3
/*      */     //   127: aload_3
/*      */     //   128: new org/h2/command/ddl/SequenceOptions
/*      */     //   131: dup
/*      */     //   132: invokespecial <init> : ()V
/*      */     //   135: iconst_0
/*      */     //   136: invokevirtual setIdentityOptions : (Lorg/h2/command/ddl/SequenceOptions;Z)V
/*      */     //   139: goto -> 148
/*      */     //   142: aload_0
/*      */     //   143: aload_1
/*      */     //   144: invokespecial parseColumnWithType : (Ljava/lang/String;)Lorg/h2/table/Column;
/*      */     //   147: astore_3
/*      */     //   148: aload_0
/*      */     //   149: ldc_w 'INVISIBLE'
/*      */     //   152: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   155: ifeq -> 166
/*      */     //   158: aload_3
/*      */     //   159: iconst_0
/*      */     //   160: invokevirtual setVisible : (Z)V
/*      */     //   163: goto -> 181
/*      */     //   166: aload_0
/*      */     //   167: ldc_w 'VISIBLE'
/*      */     //   170: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   173: ifeq -> 181
/*      */     //   176: aload_3
/*      */     //   177: iconst_1
/*      */     //   178: invokevirtual setVisible : (Z)V
/*      */     //   181: iconst_0
/*      */     //   182: istore #5
/*      */     //   184: aload_0
/*      */     //   185: invokespecial parseNotNullConstraint : ()Lorg/h2/command/Parser$NullConstraintType;
/*      */     //   188: astore #6
/*      */     //   190: aload_3
/*      */     //   191: invokevirtual isIdentity : ()Z
/*      */     //   194: ifne -> 438
/*      */     //   197: aload_0
/*      */     //   198: bipush #7
/*      */     //   200: invokespecial readIf : (I)Z
/*      */     //   203: ifeq -> 217
/*      */     //   206: aload_3
/*      */     //   207: aload_0
/*      */     //   208: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   211: invokevirtual setGeneratedExpression : (Lorg/h2/expression/Expression;)V
/*      */     //   214: goto -> 378
/*      */     //   217: aload_0
/*      */     //   218: bipush #25
/*      */     //   220: invokespecial readIf : (I)Z
/*      */     //   223: ifeq -> 262
/*      */     //   226: aload_0
/*      */     //   227: bipush #60
/*      */     //   229: invokespecial readIf : (I)Z
/*      */     //   232: ifeq -> 247
/*      */     //   235: aload_0
/*      */     //   236: bipush #58
/*      */     //   238: invokespecial read : (I)V
/*      */     //   241: iconst_1
/*      */     //   242: istore #5
/*      */     //   244: goto -> 438
/*      */     //   247: aload_3
/*      */     //   248: aload_0
/*      */     //   249: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   252: aload_0
/*      */     //   253: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   256: invokevirtual setDefaultExpression : (Lorg/h2/engine/SessionLocal;Lorg/h2/expression/Expression;)V
/*      */     //   259: goto -> 378
/*      */     //   262: aload_0
/*      */     //   263: ldc_w 'GENERATED'
/*      */     //   266: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   269: ifeq -> 378
/*      */     //   272: aload_0
/*      */     //   273: ldc_w 'ALWAYS'
/*      */     //   276: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   279: istore #7
/*      */     //   281: iload #7
/*      */     //   283: ifne -> 299
/*      */     //   286: aload_0
/*      */     //   287: ldc_w 'BY'
/*      */     //   290: invokespecial read : (Ljava/lang/String;)V
/*      */     //   293: aload_0
/*      */     //   294: bipush #25
/*      */     //   296: invokespecial read : (I)V
/*      */     //   299: aload_0
/*      */     //   300: bipush #7
/*      */     //   302: invokespecial read : (I)V
/*      */     //   305: aload_0
/*      */     //   306: ldc_w 'IDENTITY'
/*      */     //   309: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   312: ifeq -> 360
/*      */     //   315: new org/h2/command/ddl/SequenceOptions
/*      */     //   318: dup
/*      */     //   319: invokespecial <init> : ()V
/*      */     //   322: astore #8
/*      */     //   324: aload_0
/*      */     //   325: bipush #105
/*      */     //   327: invokespecial readIf : (I)Z
/*      */     //   330: ifeq -> 349
/*      */     //   333: aload_0
/*      */     //   334: aload #8
/*      */     //   336: aconst_null
/*      */     //   337: iconst_0
/*      */     //   338: iconst_0
/*      */     //   339: invokespecial parseSequenceOptions : (Lorg/h2/command/ddl/SequenceOptions;Lorg/h2/command/ddl/CreateSequence;ZZ)Z
/*      */     //   342: pop
/*      */     //   343: aload_0
/*      */     //   344: bipush #106
/*      */     //   346: invokespecial read : (I)V
/*      */     //   349: aload_3
/*      */     //   350: aload #8
/*      */     //   352: iload #7
/*      */     //   354: invokevirtual setIdentityOptions : (Lorg/h2/command/ddl/SequenceOptions;Z)V
/*      */     //   357: goto -> 438
/*      */     //   360: iload #7
/*      */     //   362: ifne -> 370
/*      */     //   365: aload_0
/*      */     //   366: invokespecial getSyntaxError : ()Lorg/h2/message/DbException;
/*      */     //   369: athrow
/*      */     //   370: aload_3
/*      */     //   371: aload_0
/*      */     //   372: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   375: invokevirtual setGeneratedExpression : (Lorg/h2/expression/Expression;)V
/*      */     //   378: aload_3
/*      */     //   379: invokevirtual isGenerated : ()Z
/*      */     //   382: ifne -> 412
/*      */     //   385: aload_0
/*      */     //   386: bipush #60
/*      */     //   388: invokespecial readIf : (I)Z
/*      */     //   391: ifeq -> 412
/*      */     //   394: aload_0
/*      */     //   395: ldc 'UPDATE'
/*      */     //   397: invokespecial read : (Ljava/lang/String;)V
/*      */     //   400: aload_3
/*      */     //   401: aload_0
/*      */     //   402: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   405: aload_0
/*      */     //   406: invokespecial readExpression : ()Lorg/h2/expression/Expression;
/*      */     //   409: invokevirtual setOnUpdateExpression : (Lorg/h2/engine/SessionLocal;Lorg/h2/expression/Expression;)V
/*      */     //   412: aload_0
/*      */     //   413: aload #6
/*      */     //   415: invokespecial parseNotNullConstraint : (Lorg/h2/command/Parser$NullConstraintType;)Lorg/h2/command/Parser$NullConstraintType;
/*      */     //   418: astore #6
/*      */     //   420: aload_0
/*      */     //   421: aload_3
/*      */     //   422: aload #4
/*      */     //   424: invokespecial parseCompatibilityIdentity : (Lorg/h2/table/Column;Lorg/h2/engine/Mode;)Z
/*      */     //   427: ifeq -> 438
/*      */     //   430: aload_0
/*      */     //   431: aload #6
/*      */     //   433: invokespecial parseNotNullConstraint : (Lorg/h2/command/Parser$NullConstraintType;)Lorg/h2/command/Parser$NullConstraintType;
/*      */     //   436: astore #6
/*      */     //   438: getstatic org/h2/command/Parser$1.$SwitchMap$org$h2$command$Parser$NullConstraintType : [I
/*      */     //   441: aload #6
/*      */     //   443: invokevirtual ordinal : ()I
/*      */     //   446: iaload
/*      */     //   447: tableswitch default -> 521, 1 -> 472, 2 -> 498, 3 -> 506
/*      */     //   472: aload_3
/*      */     //   473: invokevirtual isIdentity : ()Z
/*      */     //   476: ifeq -> 490
/*      */     //   479: ldc_w 90023
/*      */     //   482: aload_3
/*      */     //   483: invokevirtual getName : ()Ljava/lang/String;
/*      */     //   486: invokestatic get : (ILjava/lang/String;)Lorg/h2/message/DbException;
/*      */     //   489: athrow
/*      */     //   490: aload_3
/*      */     //   491: iconst_1
/*      */     //   492: invokevirtual setNullable : (Z)V
/*      */     //   495: goto -> 552
/*      */     //   498: aload_3
/*      */     //   499: iconst_0
/*      */     //   500: invokevirtual setNullable : (Z)V
/*      */     //   503: goto -> 552
/*      */     //   506: aload_3
/*      */     //   507: invokevirtual isIdentity : ()Z
/*      */     //   510: ifne -> 552
/*      */     //   513: aload_3
/*      */     //   514: iload_2
/*      */     //   515: invokevirtual setNullable : (Z)V
/*      */     //   518: goto -> 552
/*      */     //   521: ldc_w 90088
/*      */     //   524: new java/lang/StringBuilder
/*      */     //   527: dup
/*      */     //   528: invokespecial <init> : ()V
/*      */     //   531: ldc_w 'Internal Error - unhandled case: '
/*      */     //   534: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   537: aload #6
/*      */     //   539: invokevirtual name : ()Ljava/lang/String;
/*      */     //   542: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   545: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   548: invokestatic get : (ILjava/lang/String;)Lorg/h2/message/DbException;
/*      */     //   551: athrow
/*      */     //   552: iload #5
/*      */     //   554: ifne -> 597
/*      */     //   557: aload_0
/*      */     //   558: bipush #25
/*      */     //   560: invokespecial readIf : (I)Z
/*      */     //   563: ifeq -> 584
/*      */     //   566: aload_0
/*      */     //   567: bipush #60
/*      */     //   569: invokespecial read : (I)V
/*      */     //   572: aload_0
/*      */     //   573: bipush #58
/*      */     //   575: invokespecial read : (I)V
/*      */     //   578: iconst_1
/*      */     //   579: istore #5
/*      */     //   581: goto -> 597
/*      */     //   584: aload_0
/*      */     //   585: ldc_w 'NULL_TO_DEFAULT'
/*      */     //   588: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   591: ifeq -> 597
/*      */     //   594: iconst_1
/*      */     //   595: istore #5
/*      */     //   597: iload #5
/*      */     //   599: ifeq -> 607
/*      */     //   602: aload_3
/*      */     //   603: iconst_1
/*      */     //   604: invokevirtual setDefaultOnNull : (Z)V
/*      */     //   607: aload_3
/*      */     //   608: invokevirtual isGenerated : ()Z
/*      */     //   611: ifne -> 636
/*      */     //   614: aload_0
/*      */     //   615: ldc_w 'SEQUENCE'
/*      */     //   618: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   621: ifeq -> 636
/*      */     //   624: aload_3
/*      */     //   625: aload_0
/*      */     //   626: invokespecial readSequence : ()Lorg/h2/schema/Sequence;
/*      */     //   629: aload_3
/*      */     //   630: invokevirtual isGeneratedAlways : ()Z
/*      */     //   633: invokevirtual setSequence : (Lorg/h2/schema/Sequence;Z)V
/*      */     //   636: aload_0
/*      */     //   637: ldc_w 'SELECTIVITY'
/*      */     //   640: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   643: ifeq -> 654
/*      */     //   646: aload_3
/*      */     //   647: aload_0
/*      */     //   648: invokespecial readNonNegativeInt : ()I
/*      */     //   651: invokevirtual setSelectivity : (I)V
/*      */     //   654: aload #4
/*      */     //   656: invokevirtual getEnum : ()Lorg/h2/engine/Mode$ModeEnum;
/*      */     //   659: getstatic org/h2/engine/Mode$ModeEnum.MySQL : Lorg/h2/engine/Mode$ModeEnum;
/*      */     //   662: if_acmpne -> 700
/*      */     //   665: aload_0
/*      */     //   666: ldc_w 'CHARACTER'
/*      */     //   669: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   672: ifeq -> 686
/*      */     //   675: aload_0
/*      */     //   676: bipush #71
/*      */     //   678: invokespecial readIf : (I)Z
/*      */     //   681: pop
/*      */     //   682: aload_0
/*      */     //   683: invokespecial readMySQLCharset : ()V
/*      */     //   686: aload_0
/*      */     //   687: ldc_w 'COLLATE'
/*      */     //   690: invokespecial readIf : (Ljava/lang/String;)Z
/*      */     //   693: ifeq -> 700
/*      */     //   696: aload_0
/*      */     //   697: invokespecial readMySQLCharset : ()V
/*      */     //   700: aload_0
/*      */     //   701: invokespecial readCommentIf : ()Ljava/lang/String;
/*      */     //   704: astore #7
/*      */     //   706: aload #7
/*      */     //   708: ifnull -> 717
/*      */     //   711: aload_3
/*      */     //   712: aload #7
/*      */     //   714: invokevirtual setComment : (Ljava/lang/String;)V
/*      */     //   717: aload_3
/*      */     //   718: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #5920	-> 0
/*      */     //   #5921	-> 9
/*      */     //   #5922	-> 27
/*      */     //   #5923	-> 39
/*      */     //   #5924	-> 44
/*      */     //   #5925	-> 52
/*      */     //   #5926	-> 70
/*      */     //   #5927	-> 82
/*      */     //   #5928	-> 97
/*      */     //   #5929	-> 115
/*      */     //   #5930	-> 127
/*      */     //   #5932	-> 142
/*      */     //   #5934	-> 148
/*      */     //   #5935	-> 158
/*      */     //   #5936	-> 166
/*      */     //   #5937	-> 176
/*      */     //   #5939	-> 181
/*      */     //   #5940	-> 184
/*      */     //   #5941	-> 190
/*      */     //   #5942	-> 197
/*      */     //   #5943	-> 206
/*      */     //   #5944	-> 217
/*      */     //   #5945	-> 226
/*      */     //   #5946	-> 235
/*      */     //   #5947	-> 241
/*      */     //   #5948	-> 244
/*      */     //   #5950	-> 247
/*      */     //   #5951	-> 262
/*      */     //   #5952	-> 272
/*      */     //   #5953	-> 281
/*      */     //   #5954	-> 286
/*      */     //   #5955	-> 293
/*      */     //   #5957	-> 299
/*      */     //   #5958	-> 305
/*      */     //   #5959	-> 315
/*      */     //   #5960	-> 324
/*      */     //   #5961	-> 333
/*      */     //   #5962	-> 343
/*      */     //   #5964	-> 349
/*      */     //   #5965	-> 357
/*      */     //   #5966	-> 360
/*      */     //   #5967	-> 365
/*      */     //   #5969	-> 370
/*      */     //   #5972	-> 378
/*      */     //   #5973	-> 394
/*      */     //   #5974	-> 400
/*      */     //   #5976	-> 412
/*      */     //   #5977	-> 420
/*      */     //   #5978	-> 430
/*      */     //   #5981	-> 438
/*      */     //   #5983	-> 472
/*      */     //   #5984	-> 479
/*      */     //   #5986	-> 490
/*      */     //   #5987	-> 495
/*      */     //   #5989	-> 498
/*      */     //   #5990	-> 503
/*      */     //   #5992	-> 506
/*      */     //   #5993	-> 513
/*      */     //   #5997	-> 521
/*      */     //   #5998	-> 539
/*      */     //   #5997	-> 548
/*      */     //   #6000	-> 552
/*      */     //   #6001	-> 557
/*      */     //   #6002	-> 566
/*      */     //   #6003	-> 572
/*      */     //   #6004	-> 578
/*      */     //   #6005	-> 584
/*      */     //   #6006	-> 594
/*      */     //   #6009	-> 597
/*      */     //   #6010	-> 602
/*      */     //   #6012	-> 607
/*      */     //   #6013	-> 614
/*      */     //   #6014	-> 624
/*      */     //   #6017	-> 636
/*      */     //   #6018	-> 646
/*      */     //   #6020	-> 654
/*      */     //   #6021	-> 665
/*      */     //   #6022	-> 675
/*      */     //   #6023	-> 682
/*      */     //   #6025	-> 686
/*      */     //   #6026	-> 696
/*      */     //   #6029	-> 700
/*      */     //   #6030	-> 706
/*      */     //   #6031	-> 711
/*      */     //   #6033	-> 717
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void parseCompatibilityIdentityOptions(Column paramColumn) {
/* 6037 */     SequenceOptions sequenceOptions = new SequenceOptions();
/* 6038 */     if (readIf(105)) {
/* 6039 */       sequenceOptions.setStartValue((Expression)ValueExpression.get((Value)ValueBigint.get(readLong())));
/* 6040 */       if (readIf(109)) {
/* 6041 */         sequenceOptions.setIncrement((Expression)ValueExpression.get((Value)ValueBigint.get(readLong())));
/*      */       }
/* 6043 */       read(106);
/*      */     } 
/* 6045 */     paramColumn.setIdentityOptions(sequenceOptions, false);
/*      */   }
/*      */   
/*      */   private String readCommentIf() {
/* 6049 */     if (readIf("COMMENT")) {
/* 6050 */       readIf(45);
/* 6051 */       return readString();
/*      */     } 
/* 6053 */     return null;
/*      */   }
/*      */   
/*      */   private Column parseColumnWithType(String paramString) {
/* 6057 */     TypeInfo typeInfo = readIfDataType();
/* 6058 */     if (typeInfo == null) {
/* 6059 */       String str = readIdentifierWithSchema();
/* 6060 */       return getColumnWithDomain(paramString, getSchema().getDomain(str));
/*      */     } 
/* 6062 */     return new Column(paramString, typeInfo);
/*      */   }
/*      */   
/*      */   private TypeInfo parseDataType() {
/* 6066 */     TypeInfo typeInfo = readIfDataType();
/* 6067 */     if (typeInfo == null) {
/* 6068 */       addExpected("data type");
/* 6069 */       throw getSyntaxError();
/*      */     } 
/* 6071 */     return typeInfo;
/*      */   }
/*      */   
/*      */   private TypeInfo readIfDataType() {
/* 6075 */     TypeInfo typeInfo = readIfDataType1();
/* 6076 */     if (typeInfo != null) {
/* 6077 */       while (readIf(6)) {
/* 6078 */         typeInfo = parseArrayType(typeInfo);
/*      */       }
/*      */     }
/* 6081 */     return typeInfo; } private TypeInfo readIfDataType1() {
/*      */     TypeInfo typeInfo;
/*      */     long l;
/*      */     int j;
/* 6085 */     switch (this.currentTokenType) {
/*      */       case 2:
/* 6087 */         if (this.token.isQuoted()) {
/* 6088 */           return null;
/*      */         }
/*      */         break;
/*      */       case 44:
/* 6092 */         read();
/* 6093 */         typeInfo = readIntervalQualifier();
/* 6094 */         if (typeInfo == null) {
/* 6095 */           throw intervalQualifierError();
/*      */         }
/* 6097 */         return typeInfo;
/*      */       
/*      */       case 58:
/* 6100 */         read();
/* 6101 */         return TypeInfo.TYPE_NULL;
/*      */       case 66:
/* 6103 */         read();
/* 6104 */         return parseRowType();
/*      */       
/*      */       case 6:
/* 6107 */         if (this.session.isQuirksMode()) {
/* 6108 */           read();
/* 6109 */           return parseArrayType(TypeInfo.TYPE_VARCHAR);
/*      */         } 
/* 6111 */         addExpected("data type");
/* 6112 */         throw getSyntaxError();
/*      */       default:
/* 6114 */         if (isKeyword(this.currentToken)) {
/*      */           break;
/*      */         }
/* 6117 */         addExpected("data type");
/* 6118 */         throw getSyntaxError();
/*      */     } 
/* 6120 */     int i = this.tokenIndex;
/* 6121 */     String str1 = this.currentToken;
/* 6122 */     read();
/* 6123 */     if (this.currentTokenType == 110) {
/* 6124 */       setTokenIndex(i);
/* 6125 */       return null;
/*      */     } 
/* 6127 */     String str2 = upperName(str1);
/* 6128 */     switch (str2) {
/*      */       case "BINARY":
/* 6130 */         if (readIf("VARYING")) {
/* 6131 */           str2 = "BINARY VARYING"; break;
/* 6132 */         }  if (readIf("LARGE")) {
/* 6133 */           read("OBJECT");
/* 6134 */           str2 = "BINARY LARGE OBJECT"; break;
/* 6135 */         }  if (this.variableBinary) {
/* 6136 */           str2 = "VARBINARY";
/*      */         }
/*      */         break;
/*      */       case "CHAR":
/* 6140 */         if (readIf("VARYING")) {
/* 6141 */           str2 = "CHAR VARYING"; break;
/* 6142 */         }  if (readIf("LARGE")) {
/* 6143 */           read("OBJECT");
/* 6144 */           str2 = "CHAR LARGE OBJECT";
/*      */         } 
/*      */         break;
/*      */       case "CHARACTER":
/* 6148 */         if (readIf("VARYING")) {
/* 6149 */           str2 = "CHARACTER VARYING"; break;
/* 6150 */         }  if (readIf("LARGE")) {
/* 6151 */           read("OBJECT");
/* 6152 */           str2 = "CHARACTER LARGE OBJECT";
/*      */         } 
/*      */         break;
/*      */       case "DATETIME":
/*      */       case "DATETIME2":
/* 6157 */         return parseDateTimeType(false);
/*      */       case "DEC":
/*      */       case "DECIMAL":
/* 6160 */         return parseNumericType(true);
/*      */       case "DECFLOAT":
/* 6162 */         return parseDecfloatType();
/*      */       case "DOUBLE":
/* 6164 */         if (readIf("PRECISION")) {
/* 6165 */           str2 = "DOUBLE PRECISION";
/*      */         }
/*      */         break;
/*      */       case "ENUM":
/* 6169 */         return parseEnumType();
/*      */       case "FLOAT":
/* 6171 */         return parseFloatType();
/*      */       case "GEOMETRY":
/* 6173 */         return parseGeometryType();
/*      */       case "LONG":
/* 6175 */         if (readIf("RAW")) {
/* 6176 */           str2 = "LONG RAW";
/*      */         }
/*      */         break;
/*      */       case "NATIONAL":
/* 6180 */         if (readIf("CHARACTER")) {
/* 6181 */           if (readIf("VARYING")) {
/* 6182 */             str2 = "NATIONAL CHARACTER VARYING"; break;
/* 6183 */           }  if (readIf("LARGE")) {
/* 6184 */             read("OBJECT");
/* 6185 */             str2 = "NATIONAL CHARACTER LARGE OBJECT"; break;
/*      */           } 
/* 6187 */           str2 = "NATIONAL CHARACTER";
/*      */           break;
/*      */         } 
/* 6190 */         read("CHAR");
/* 6191 */         if (readIf("VARYING")) {
/* 6192 */           str2 = "NATIONAL CHAR VARYING"; break;
/*      */         } 
/* 6194 */         str2 = "NATIONAL CHAR";
/*      */         break;
/*      */ 
/*      */       
/*      */       case "NCHAR":
/* 6199 */         if (readIf("VARYING")) {
/* 6200 */           str2 = "NCHAR VARYING"; break;
/* 6201 */         }  if (readIf("LARGE")) {
/* 6202 */           read("OBJECT");
/* 6203 */           str2 = "NCHAR LARGE OBJECT";
/*      */         } 
/*      */         break;
/*      */       case "NUMBER":
/* 6207 */         if ((this.database.getMode()).disallowedTypes.contains("NUMBER")) {
/* 6208 */           throw DbException.get(50004, "NUMBER");
/*      */         }
/* 6210 */         if (!isToken(105)) {
/* 6211 */           return TypeInfo.getTypeInfo(16, 40L, -1, null);
/*      */         }
/*      */       
/*      */       case "NUMERIC":
/* 6215 */         return parseNumericType(false);
/*      */       case "SMALLDATETIME":
/* 6217 */         return parseDateTimeType(true);
/*      */       case "TIME":
/* 6219 */         return parseTimeType();
/*      */       case "TIMESTAMP":
/* 6221 */         return parseTimestampType();
/*      */     } 
/*      */     
/* 6224 */     if (str1.length() == str2.length()) {
/* 6225 */       Domain domain = this.database.getSchema(this.session.getCurrentSchemaName()).findDomain(str1);
/* 6226 */       if (domain != null) {
/* 6227 */         setTokenIndex(i);
/* 6228 */         return null;
/*      */       } 
/*      */     } 
/* 6231 */     Mode mode = this.database.getMode();
/* 6232 */     DataType dataType = DataType.getTypeByName(str2, mode);
/* 6233 */     if (dataType == null || mode.disallowedTypes.contains(str2)) {
/* 6234 */       throw DbException.get(50004, str2);
/*      */     }
/*      */ 
/*      */     
/* 6238 */     if (dataType.specialPrecisionScale) {
/* 6239 */       l = dataType.defaultPrecision;
/* 6240 */       j = dataType.defaultScale;
/*      */     } else {
/* 6242 */       l = -1L;
/* 6243 */       j = -1;
/*      */     } 
/* 6245 */     int k = dataType.type;
/* 6246 */     if (this.database.getIgnoreCase() && k == 2 && !equalsToken("VARCHAR_CASESENSITIVE", str2)) {
/* 6247 */       dataType = DataType.getDataType(k = 4);
/*      */     }
/* 6249 */     if ((dataType.supportsPrecision || dataType.supportsScale) && readIf(105)) {
/* 6250 */       if (!readIf("MAX")) {
/* 6251 */         if (dataType.supportsPrecision) {
/* 6252 */           l = readPrecision(k);
/* 6253 */           if (l < dataType.minPrecision)
/* 6254 */             throw getInvalidPrecisionException(dataType, l); 
/* 6255 */           if (l > dataType.maxPrecision)
/*      */           {
/* 6257 */             if (this.session.isQuirksMode() || this.session.isTruncateLargeLength())
/* 6258 */             { switch (dataType.type)
/*      */               { case 1:
/*      */                 case 2:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 6:
/*      */                 case 35:
/*      */                 case 38:
/* 6266 */                   l = dataType.maxPrecision;
/*      */                   break;
/*      */                 
/*      */                 default:
/* 6270 */                   throw getInvalidPrecisionException(dataType, l); }  }
/*      */             else {  }
/* 6272 */              }  if (dataType.supportsScale && 
/* 6273 */             readIf(109)) {
/* 6274 */             j = readInt();
/* 6275 */             if (j < dataType.minScale || j > dataType.maxScale) {
/* 6276 */               throw DbException.get(90151, new String[] { Integer.toString(j), 
/* 6277 */                     Integer.toString(dataType.minScale), Integer.toString(dataType.maxScale) });
/*      */             }
/*      */           } 
/*      */         } else {
/*      */           
/* 6282 */           j = readInt();
/* 6283 */           if (j < dataType.minScale || j > dataType.maxScale) {
/* 6284 */             throw DbException.get(90151, new String[] { Integer.toString(j), 
/* 6285 */                   Integer.toString(dataType.minScale), Integer.toString(dataType.maxScale) });
/*      */           }
/*      */         } 
/*      */       }
/* 6289 */       read(106);
/*      */     } 
/* 6291 */     if (mode.allNumericTypesHavePrecision && DataType.isNumericType(dataType.type)) {
/* 6292 */       if (readIf(105)) {
/*      */ 
/*      */         
/* 6295 */         readNonNegativeInt();
/* 6296 */         read(106);
/*      */       } 
/* 6298 */       readIf("UNSIGNED");
/*      */     } 
/* 6300 */     if (mode.forBitData && DataType.isStringType(k) && 
/* 6301 */       readIf(33)) {
/* 6302 */       read("BIT");
/* 6303 */       read("DATA");
/* 6304 */       dataType = DataType.getDataType(k = 6);
/*      */     } 
/*      */     
/* 6307 */     return TypeInfo.getTypeInfo(k, l, j, null);
/*      */   }
/*      */   
/*      */   private static DbException getInvalidPrecisionException(DataType paramDataType, long paramLong) {
/* 6311 */     return DbException.get(90150, new String[] { Long.toString(paramLong), 
/* 6312 */           Long.toString(paramDataType.minPrecision), Long.toString(paramDataType.maxPrecision) });
/*      */   }
/*      */   
/*      */   private static Column getColumnWithDomain(String paramString, Domain paramDomain) {
/* 6316 */     Column column = new Column(paramString, paramDomain.getDataType());
/* 6317 */     column.setComment(paramDomain.getComment());
/* 6318 */     column.setDomain(paramDomain);
/* 6319 */     return column;
/*      */   }
/*      */   private TypeInfo parseFloatType() {
/*      */     boolean bool;
/* 6323 */     byte b = 15;
/*      */     
/* 6325 */     if (readIf(105)) {
/* 6326 */       bool = readNonNegativeInt();
/* 6327 */       read(106);
/* 6328 */       if (bool < true || bool > 53) {
/* 6329 */         throw DbException.get(90150, new String[] { Integer.toString(bool), "1", "53" });
/*      */       }
/* 6331 */       if (bool <= 24) {
/* 6332 */         b = 14;
/*      */       }
/*      */     } else {
/* 6335 */       bool = false;
/*      */     } 
/* 6337 */     return TypeInfo.getTypeInfo(b, bool, -1, null);
/*      */   }
/*      */   
/*      */   private TypeInfo parseNumericType(boolean paramBoolean) {
/* 6341 */     long l = -1L;
/* 6342 */     int i = -1;
/* 6343 */     if (readIf(105)) {
/* 6344 */       l = readPrecision(13);
/* 6345 */       if (l < 1L)
/* 6346 */         throw getInvalidNumericPrecisionException(l); 
/* 6347 */       if (l > 100000L) {
/* 6348 */         if (this.session.isQuirksMode() || this.session.isTruncateLargeLength()) {
/* 6349 */           l = 100000L;
/*      */         } else {
/* 6351 */           throw getInvalidNumericPrecisionException(l);
/*      */         } 
/*      */       }
/* 6354 */       if (readIf(109)) {
/* 6355 */         i = readInt();
/* 6356 */         if (i < 0 || i > 100000) {
/* 6357 */           throw DbException.get(90151, new String[] { Integer.toString(i), "0", "100000" });
/*      */         }
/*      */       } 
/*      */       
/* 6361 */       read(106);
/*      */     } 
/* 6363 */     return TypeInfo.getTypeInfo(13, l, i, paramBoolean ? (ExtTypeInfo)ExtTypeInfoNumeric.DECIMAL : null);
/*      */   }
/*      */   
/*      */   private TypeInfo parseDecfloatType() {
/* 6367 */     long l = -1L;
/* 6368 */     if (readIf(105)) {
/* 6369 */       l = readPrecision(16);
/* 6370 */       if (l < 1L || l > 100000L) {
/* 6371 */         throw getInvalidNumericPrecisionException(l);
/*      */       }
/* 6373 */       read(106);
/*      */     } 
/* 6375 */     return TypeInfo.getTypeInfo(16, l, -1, null);
/*      */   }
/*      */   
/*      */   private static DbException getInvalidNumericPrecisionException(long paramLong) {
/* 6379 */     return DbException.get(90150, new String[] { Long.toString(paramLong), "1", "100000" });
/*      */   }
/*      */ 
/*      */   
/*      */   private TypeInfo parseTimeType() {
/* 6384 */     int i = -1;
/* 6385 */     if (readIf(105)) {
/* 6386 */       i = readNonNegativeInt();
/* 6387 */       if (i > 9) {
/* 6388 */         throw DbException.get(90151, new String[] { Integer.toString(i), "0", "9" });
/*      */       }
/*      */       
/* 6391 */       read(106);
/*      */     } 
/* 6393 */     byte b = 18;
/* 6394 */     if (readIf(89)) {
/* 6395 */       read("TIME");
/* 6396 */       read("ZONE");
/* 6397 */       b = 19;
/* 6398 */     } else if (readIf("WITHOUT")) {
/* 6399 */       read("TIME");
/* 6400 */       read("ZONE");
/*      */     } 
/* 6402 */     return TypeInfo.getTypeInfo(b, -1L, i, null);
/*      */   }
/*      */   
/*      */   private TypeInfo parseTimestampType() {
/* 6406 */     int i = -1;
/* 6407 */     if (readIf(105)) {
/* 6408 */       i = readNonNegativeInt();
/*      */       
/* 6410 */       if (readIf(109)) {
/* 6411 */         i = readNonNegativeInt();
/*      */       }
/* 6413 */       if (i > 9) {
/* 6414 */         throw DbException.get(90151, new String[] { Integer.toString(i), "0", "9" });
/*      */       }
/*      */       
/* 6417 */       read(106);
/*      */     } 
/* 6419 */     byte b = 20;
/* 6420 */     if (readIf(89)) {
/* 6421 */       read("TIME");
/* 6422 */       read("ZONE");
/* 6423 */       b = 21;
/* 6424 */     } else if (readIf("WITHOUT")) {
/* 6425 */       read("TIME");
/* 6426 */       read("ZONE");
/*      */     } 
/* 6428 */     return TypeInfo.getTypeInfo(b, -1L, i, null);
/*      */   }
/*      */   
/*      */   private TypeInfo parseDateTimeType(boolean paramBoolean) {
/*      */     int i;
/* 6433 */     if (paramBoolean) {
/* 6434 */       i = 0;
/*      */     } else {
/* 6436 */       i = -1;
/* 6437 */       if (readIf(105)) {
/* 6438 */         i = readNonNegativeInt();
/* 6439 */         if (i > 9) {
/* 6440 */           throw DbException.get(90151, new String[] { Integer.toString(i), "0", "9" });
/*      */         }
/*      */         
/* 6443 */         read(106);
/*      */       } 
/*      */     } 
/* 6446 */     return TypeInfo.getTypeInfo(20, -1L, i, null);
/*      */   }
/*      */   
/*      */   private TypeInfo readIntervalQualifier() {
/*      */     IntervalQualifier intervalQualifier;
/* 6451 */     int i = -1, j = -1;
/* 6452 */     switch (this.currentTokenType) {
/*      */       case 90:
/* 6454 */         read();
/* 6455 */         if (readIf(105)) {
/* 6456 */           i = readNonNegativeInt();
/* 6457 */           read(106);
/*      */         } 
/* 6459 */         if (readIf(76)) {
/* 6460 */           read(55);
/* 6461 */           IntervalQualifier intervalQualifier1 = IntervalQualifier.YEAR_TO_MONTH; break;
/*      */         } 
/* 6463 */         intervalQualifier = IntervalQualifier.YEAR;
/*      */         break;
/*      */       
/*      */       case 55:
/* 6467 */         read();
/* 6468 */         if (readIf(105)) {
/* 6469 */           i = readNonNegativeInt();
/* 6470 */           read(106);
/*      */         } 
/* 6472 */         intervalQualifier = IntervalQualifier.MONTH;
/*      */         break;
/*      */       case 24:
/* 6475 */         read();
/* 6476 */         if (readIf(105)) {
/* 6477 */           i = readNonNegativeInt();
/* 6478 */           read(106);
/*      */         } 
/* 6480 */         if (readIf(76)) {
/* 6481 */           switch (this.currentTokenType) {
/*      */             case 39:
/* 6483 */               read();
/* 6484 */               intervalQualifier = IntervalQualifier.DAY_TO_HOUR;
/*      */               break;
/*      */             case 54:
/* 6487 */               read();
/* 6488 */               intervalQualifier = IntervalQualifier.DAY_TO_MINUTE;
/*      */               break;
/*      */             case 68:
/* 6491 */               read();
/* 6492 */               if (readIf(105)) {
/* 6493 */                 j = readNonNegativeInt();
/* 6494 */                 read(106);
/*      */               } 
/* 6496 */               intervalQualifier = IntervalQualifier.DAY_TO_SECOND;
/*      */               break;
/*      */           } 
/* 6499 */           throw intervalDayError();
/*      */         } 
/*      */         
/* 6502 */         intervalQualifier = IntervalQualifier.DAY;
/*      */         break;
/*      */       
/*      */       case 39:
/* 6506 */         read();
/* 6507 */         if (readIf(105)) {
/* 6508 */           i = readNonNegativeInt();
/* 6509 */           read(106);
/*      */         } 
/* 6511 */         if (readIf(76)) {
/* 6512 */           switch (this.currentTokenType) {
/*      */             case 54:
/* 6514 */               read();
/* 6515 */               intervalQualifier = IntervalQualifier.HOUR_TO_MINUTE;
/*      */               break;
/*      */             case 68:
/* 6518 */               read();
/* 6519 */               if (readIf(105)) {
/* 6520 */                 j = readNonNegativeInt();
/* 6521 */                 read(106);
/*      */               } 
/* 6523 */               intervalQualifier = IntervalQualifier.HOUR_TO_SECOND;
/*      */               break;
/*      */           } 
/* 6526 */           throw intervalHourError();
/*      */         } 
/*      */         
/* 6529 */         intervalQualifier = IntervalQualifier.HOUR;
/*      */         break;
/*      */       
/*      */       case 54:
/* 6533 */         read();
/* 6534 */         if (readIf(105)) {
/* 6535 */           i = readNonNegativeInt();
/* 6536 */           read(106);
/*      */         } 
/* 6538 */         if (readIf(76)) {
/* 6539 */           read(68);
/* 6540 */           if (readIf(105)) {
/* 6541 */             j = readNonNegativeInt();
/* 6542 */             read(106);
/*      */           } 
/* 6544 */           intervalQualifier = IntervalQualifier.MINUTE_TO_SECOND; break;
/*      */         } 
/* 6546 */         intervalQualifier = IntervalQualifier.MINUTE;
/*      */         break;
/*      */       
/*      */       case 68:
/* 6550 */         read();
/* 6551 */         if (readIf(105)) {
/* 6552 */           i = readNonNegativeInt();
/* 6553 */           if (readIf(109)) {
/* 6554 */             j = readNonNegativeInt();
/*      */           }
/* 6556 */           read(106);
/*      */         } 
/* 6558 */         intervalQualifier = IntervalQualifier.SECOND;
/*      */         break;
/*      */       default:
/* 6561 */         return null;
/*      */     } 
/* 6563 */     if (i >= 0 && (
/* 6564 */       i == 0 || i > 18)) {
/* 6565 */       throw DbException.get(90150, new String[] { Integer.toString(i), "1", "18" });
/*      */     }
/*      */ 
/*      */     
/* 6569 */     if (j >= 0 && 
/* 6570 */       j > 9) {
/* 6571 */       throw DbException.get(90151, new String[] { Integer.toString(j), "0", "9" });
/*      */     }
/*      */ 
/*      */     
/* 6575 */     return TypeInfo.getTypeInfo(intervalQualifier.ordinal() + 22, i, j, null);
/*      */   }
/*      */   
/*      */   private DbException intervalQualifierError() {
/* 6579 */     if (this.expectedList != null) {
/* 6580 */       addMultipleExpected(new int[] { 90, 55, 24, 39, 54, 68 });
/*      */     }
/* 6582 */     return getSyntaxError();
/*      */   }
/*      */   
/*      */   private DbException intervalDayError() {
/* 6586 */     if (this.expectedList != null) {
/* 6587 */       addMultipleExpected(new int[] { 39, 54, 68 });
/*      */     }
/* 6589 */     return getSyntaxError();
/*      */   }
/*      */   
/*      */   private DbException intervalHourError() {
/* 6593 */     if (this.expectedList != null) {
/* 6594 */       addMultipleExpected(new int[] { 54, 68 });
/*      */     }
/* 6596 */     return getSyntaxError();
/*      */   }
/*      */   
/*      */   private TypeInfo parseArrayType(TypeInfo paramTypeInfo) {
/* 6600 */     int i = -1;
/* 6601 */     if (readIf(117)) {
/*      */       
/* 6603 */       i = readNonNegativeInt();
/* 6604 */       if (i > 65536) {
/* 6605 */         throw DbException.get(90150, new String[] { Integer.toString(i), "0", "65536" });
/*      */       }
/*      */       
/* 6608 */       read(118);
/*      */     } 
/* 6610 */     return TypeInfo.getTypeInfo(40, i, -1, (ExtTypeInfo)paramTypeInfo);
/*      */   }
/*      */   
/*      */   private TypeInfo parseEnumType() {
/* 6614 */     read(105);
/* 6615 */     ArrayList<String> arrayList = new ArrayList();
/*      */     while (true) {
/* 6617 */       arrayList.add(readString());
/* 6618 */       if (!readIfMore())
/* 6619 */         return TypeInfo.getTypeInfo(36, -1L, -1, (ExtTypeInfo)new ExtTypeInfoEnum(arrayList.<String>toArray(new String[0]))); 
/*      */     } 
/*      */   }
/*      */   private TypeInfo parseGeometryType() {
/*      */     ExtTypeInfo extTypeInfo;
/* 6624 */     if (readIf(105)) {
/* 6625 */       int i = 0;
/* 6626 */       if (this.currentTokenType != 2 || this.token.isQuoted()) {
/* 6627 */         throw getSyntaxError();
/*      */       }
/* 6629 */       if (!readIf("GEOMETRY")) {
/*      */         try {
/* 6631 */           i = EWKTUtils.parseGeometryType(this.currentToken);
/* 6632 */           read();
/* 6633 */           if (i / 1000 == 0 && this.currentTokenType == 2 && !this.token.isQuoted()) {
/* 6634 */             i += EWKTUtils.parseDimensionSystem(this.currentToken) * 1000;
/* 6635 */             read();
/*      */           } 
/* 6637 */         } catch (IllegalArgumentException illegalArgumentException) {
/* 6638 */           throw getSyntaxError();
/*      */         } 
/*      */       }
/* 6641 */       Integer integer = null;
/* 6642 */       if (readIf(109)) {
/* 6643 */         integer = Integer.valueOf(readInt());
/*      */       }
/* 6645 */       read(106);
/* 6646 */       extTypeInfo = (ExtTypeInfo)new ExtTypeInfoGeometry(i, integer);
/*      */     } else {
/* 6648 */       extTypeInfo = null;
/*      */     } 
/* 6650 */     return TypeInfo.getTypeInfo(37, -1L, -1, extTypeInfo);
/*      */   }
/*      */   
/*      */   private TypeInfo parseRowType() {
/* 6654 */     read(105);
/* 6655 */     LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
/*      */     while (true) {
/* 6657 */       String str = readIdentifier();
/* 6658 */       if (linkedHashMap.putIfAbsent(str, parseDataType()) != null) {
/* 6659 */         throw DbException.get(42121, str);
/*      */       }
/* 6661 */       if (!readIfMore())
/* 6662 */         return TypeInfo.getTypeInfo(41, -1L, -1, (ExtTypeInfo)new ExtTypeInfoRow(linkedHashMap)); 
/*      */     } 
/*      */   }
/*      */   private long readPrecision(int paramInt) {
/* 6666 */     long l = readPositiveLong();
/* 6667 */     if (this.currentTokenType != 2 || this.token.isQuoted()) {
/* 6668 */       return l;
/*      */     }
/* 6670 */     if ((paramInt == 7 || paramInt == 3) && this.currentToken.length() == 1) {
/*      */       long l1;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 6676 */       switch (this.currentToken.charAt(0) & 0xFFDF) {
/*      */         case 75:
/* 6678 */           l1 = 1024L;
/*      */           break;
/*      */         case 77:
/* 6681 */           l1 = 1048576L;
/*      */           break;
/*      */         case 71:
/* 6684 */           l1 = 1073741824L;
/*      */           break;
/*      */         case 84:
/* 6687 */           l1 = 1099511627776L;
/*      */           break;
/*      */         case 80:
/* 6690 */           l1 = 1125899906842624L;
/*      */           break;
/*      */         default:
/* 6693 */           throw getSyntaxError();
/*      */       } 
/* 6695 */       if (l > Long.MAX_VALUE / l1) {
/* 6696 */         throw DbException.getInvalidValueException("precision", l + this.currentToken);
/*      */       }
/* 6698 */       l *= l1;
/* 6699 */       read();
/* 6700 */       if (this.currentTokenType != 2 || this.token.isQuoted()) {
/* 6701 */         return l;
/*      */       }
/*      */     } 
/* 6704 */     switch (paramInt) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/* 6709 */         if (!readIf("CHARACTERS") && !readIf("OCTETS") && 
/* 6710 */           (this.database.getMode()).charAndByteLengthUnits && !readIf("CHAR")) {
/* 6711 */           readIf("BYTE");
/*      */         }
/*      */         break;
/*      */     } 
/* 6715 */     return l;
/*      */   }
/*      */   private Prepared parseCreate() {
/*      */     IndexColumn[] arrayOfIndexColumn;
/* 6719 */     boolean bool1 = false;
/* 6720 */     if (readIf(61)) {
/* 6721 */       read("REPLACE");
/* 6722 */       bool1 = true;
/*      */     } 
/* 6724 */     boolean bool2 = readIf("FORCE");
/* 6725 */     if (readIf("VIEW"))
/* 6726 */       return (Prepared)parseCreateView(bool2, bool1); 
/* 6727 */     if (readIf("ALIAS"))
/* 6728 */       return (Prepared)parseCreateFunctionAlias(bool2); 
/* 6729 */     if (readIf("SEQUENCE"))
/* 6730 */       return (Prepared)parseCreateSequence(); 
/* 6731 */     if (readIf(82))
/* 6732 */       return (Prepared)parseCreateUser(); 
/* 6733 */     if (readIf("TRIGGER"))
/* 6734 */       return (Prepared)parseCreateTrigger(bool2); 
/* 6735 */     if (readIf("ROLE"))
/* 6736 */       return (Prepared)parseCreateRole(); 
/* 6737 */     if (readIf("SCHEMA"))
/* 6738 */       return (Prepared)parseCreateSchema(); 
/* 6739 */     if (readIf("CONSTANT"))
/* 6740 */       return (Prepared)parseCreateConstant(); 
/* 6741 */     if (readIf("DOMAIN") || readIf("TYPE") || readIf("DATATYPE"))
/* 6742 */       return (Prepared)parseCreateDomain(); 
/* 6743 */     if (readIf("AGGREGATE"))
/* 6744 */       return (Prepared)parseCreateAggregate(bool2); 
/* 6745 */     if (readIf("LINKED")) {
/* 6746 */       return (Prepared)parseCreateLinkedTable(false, false, bool2);
/*      */     }
/*      */     
/* 6749 */     boolean bool3 = false, bool4 = false;
/* 6750 */     if (readIf("MEMORY")) {
/* 6751 */       bool3 = true;
/* 6752 */     } else if (readIf("CACHED")) {
/* 6753 */       bool4 = true;
/*      */     } 
/* 6755 */     if (readIf("LOCAL")) {
/* 6756 */       read("TEMPORARY");
/* 6757 */       if (readIf("LINKED")) {
/* 6758 */         return (Prepared)parseCreateLinkedTable(true, false, bool2);
/*      */       }
/* 6760 */       read(75);
/* 6761 */       return (Prepared)parseCreateTable(true, false, bool4);
/* 6762 */     }  if (readIf("GLOBAL")) {
/* 6763 */       read("TEMPORARY");
/* 6764 */       if (readIf("LINKED")) {
/* 6765 */         return (Prepared)parseCreateLinkedTable(true, true, bool2);
/*      */       }
/* 6767 */       read(75);
/* 6768 */       return (Prepared)parseCreateTable(true, true, bool4);
/* 6769 */     }  if (readIf("TEMP") || readIf("TEMPORARY")) {
/* 6770 */       if (readIf("LINKED")) {
/* 6771 */         return (Prepared)parseCreateLinkedTable(true, true, bool2);
/*      */       }
/* 6773 */       read(75);
/* 6774 */       return (Prepared)parseCreateTable(true, true, bool4);
/* 6775 */     }  if (readIf(75)) {
/* 6776 */       if (!bool4 && !bool3) {
/* 6777 */         bool4 = (this.database.getDefaultTableType() == 0) ? true : false;
/*      */       }
/* 6779 */       return (Prepared)parseCreateTable(false, false, bool4);
/* 6780 */     }  if (readIf("SYNONYM")) {
/* 6781 */       return (Prepared)parseCreateSynonym(bool1);
/*      */     }
/* 6783 */     boolean bool5 = false, bool6 = false;
/* 6784 */     boolean bool7 = false, bool8 = false;
/* 6785 */     String str1 = null;
/* 6786 */     Schema schema = null;
/* 6787 */     boolean bool9 = false;
/* 6788 */     if (this.session.isQuirksMode() && readIf(63)) {
/* 6789 */       read(47);
/* 6790 */       if (readIf("HASH")) {
/* 6791 */         bool5 = true;
/*      */       }
/* 6793 */       bool6 = true;
/* 6794 */       if (!isToken(60)) {
/* 6795 */         bool9 = readIfNotExists();
/* 6796 */         str1 = readIdentifierWithSchema(null);
/* 6797 */         schema = getSchema();
/*      */       } 
/*      */     } else {
/* 6800 */       if (readIf(80)) {
/* 6801 */         bool7 = true;
/*      */       }
/* 6803 */       if (readIf("HASH")) {
/* 6804 */         bool5 = true;
/* 6805 */       } else if (!bool7 && readIf("SPATIAL")) {
/* 6806 */         bool8 = true;
/*      */       } 
/* 6808 */       read("INDEX");
/* 6809 */       if (!isToken(60)) {
/* 6810 */         bool9 = readIfNotExists();
/* 6811 */         str1 = readIdentifierWithSchema(null);
/* 6812 */         schema = getSchema();
/*      */       } 
/*      */     } 
/* 6815 */     read(60);
/* 6816 */     String str2 = readIdentifierWithSchema();
/* 6817 */     checkSchema(schema);
/* 6818 */     String str3 = readCommentIf();
/* 6819 */     if (!readIf(105)) {
/*      */       
/* 6821 */       if (bool5 || bool8) {
/* 6822 */         throw getSyntaxError();
/*      */       }
/* 6824 */       read(83);
/* 6825 */       if (!readIf("BTREE"))
/*      */       {
/* 6827 */         if (readIf("HASH")) {
/* 6828 */           bool5 = true;
/*      */         } else {
/* 6830 */           read("RTREE");
/* 6831 */           bool8 = true;
/*      */         }  } 
/* 6833 */       read(105);
/*      */     } 
/* 6835 */     CreateIndex createIndex = new CreateIndex(this.session, getSchema());
/* 6836 */     createIndex.setIfNotExists(bool9);
/* 6837 */     createIndex.setPrimaryKey(bool6);
/* 6838 */     createIndex.setTableName(str2);
/* 6839 */     createIndex.setHash(bool5);
/* 6840 */     createIndex.setSpatial(bool8);
/* 6841 */     createIndex.setIndexName(str1);
/* 6842 */     createIndex.setComment(str3);
/*      */     
/* 6844 */     int i = 0;
/* 6845 */     if (bool8) {
/* 6846 */       arrayOfIndexColumn = new IndexColumn[] { new IndexColumn(readIdentifier()) };
/* 6847 */       if (bool7) {
/* 6848 */         i = 1;
/*      */       }
/* 6850 */       read(106);
/*      */     } else {
/* 6852 */       arrayOfIndexColumn = parseIndexColumnList();
/* 6853 */       if (bool7) {
/* 6854 */         i = arrayOfIndexColumn.length;
/* 6855 */         if (readIf("INCLUDE")) {
/* 6856 */           read(105);
/* 6857 */           IndexColumn[] arrayOfIndexColumn1 = parseIndexColumnList();
/* 6858 */           int j = arrayOfIndexColumn1.length;
/* 6859 */           arrayOfIndexColumn = Arrays.<IndexColumn>copyOf(arrayOfIndexColumn, i + j);
/* 6860 */           System.arraycopy(arrayOfIndexColumn1, 0, arrayOfIndexColumn, i, j);
/*      */         } 
/* 6862 */       } else if (bool6) {
/* 6863 */         i = arrayOfIndexColumn.length;
/*      */       } 
/*      */     } 
/* 6866 */     createIndex.setIndexColumns(arrayOfIndexColumn);
/* 6867 */     createIndex.setUniqueColumnCount(i);
/* 6868 */     return (Prepared)createIndex;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean addRoleOrRight(GrantRevoke paramGrantRevoke) {
/* 6876 */     if (readIf(69)) {
/* 6877 */       paramGrantRevoke.addRight(1);
/* 6878 */       return true;
/* 6879 */     }  if (readIf("DELETE")) {
/* 6880 */       paramGrantRevoke.addRight(2);
/* 6881 */       return true;
/* 6882 */     }  if (readIf("INSERT")) {
/* 6883 */       paramGrantRevoke.addRight(4);
/* 6884 */       return true;
/* 6885 */     }  if (readIf("UPDATE")) {
/* 6886 */       paramGrantRevoke.addRight(8);
/* 6887 */       return true;
/* 6888 */     }  if (readIf("CONNECT"))
/*      */     {
/* 6890 */       return true; } 
/* 6891 */     if (readIf("RESOURCE"))
/*      */     {
/* 6893 */       return true;
/*      */     }
/* 6895 */     paramGrantRevoke.addRoleName(readIdentifier());
/* 6896 */     return false;
/*      */   }
/*      */   
/*      */   private GrantRevoke parseGrantRevoke(int paramInt) {
/*      */     boolean bool;
/* 6901 */     GrantRevoke grantRevoke = new GrantRevoke(this.session);
/* 6902 */     grantRevoke.setOperationType(paramInt);
/*      */     
/* 6904 */     if (readIf(3)) {
/* 6905 */       readIf("PRIVILEGES");
/* 6906 */       grantRevoke.addRight(15);
/* 6907 */       bool = true;
/* 6908 */     } else if (readIf("ALTER")) {
/* 6909 */       read(5);
/* 6910 */       read("SCHEMA");
/* 6911 */       grantRevoke.addRight(16);
/* 6912 */       grantRevoke.addTable(null);
/* 6913 */       bool = false;
/*      */     } else {
/* 6915 */       bool = addRoleOrRight(grantRevoke);
/* 6916 */       while (readIf(109)) {
/* 6917 */         if (addRoleOrRight(grantRevoke) != bool) {
/* 6918 */           throw DbException.get(90072);
/*      */         }
/*      */       } 
/*      */     } 
/* 6922 */     if (bool && 
/* 6923 */       readIf(60)) {
/* 6924 */       if (readIf("SCHEMA")) {
/* 6925 */         grantRevoke.setSchema(this.database.getSchema(readIdentifier()));
/*      */       } else {
/* 6927 */         readIf(75);
/*      */         do {
/* 6929 */           Table table = readTableOrView();
/* 6930 */           grantRevoke.addTable(table);
/* 6931 */         } while (readIf(109));
/*      */       } 
/*      */     }
/*      */     
/* 6935 */     read((paramInt == 49) ? 76 : 35);
/* 6936 */     grantRevoke.setGranteeName(readIdentifier());
/* 6937 */     return grantRevoke;
/*      */   }
/*      */   
/*      */   private TableValueConstructor parseValues() {
/* 6941 */     ArrayList<ArrayList<Expression>> arrayList = Utils.newSmallArrayList();
/* 6942 */     ArrayList<Expression> arrayList1 = parseValuesRow(Utils.newSmallArrayList());
/* 6943 */     arrayList.add(arrayList1);
/* 6944 */     int i = arrayList1.size();
/* 6945 */     while (readIf(109)) {
/* 6946 */       arrayList1 = parseValuesRow(new ArrayList<>(i));
/* 6947 */       if (arrayList1.size() != i) {
/* 6948 */         throw DbException.get(21002);
/*      */       }
/* 6950 */       arrayList.add(arrayList1);
/*      */     } 
/* 6952 */     return new TableValueConstructor(this.session, arrayList);
/*      */   }
/*      */   
/*      */   private ArrayList<Expression> parseValuesRow(ArrayList<Expression> paramArrayList) {
/* 6956 */     if (readIf(66)) {
/* 6957 */       read(105);
/* 6958 */     } else if (!readIf(105)) {
/* 6959 */       paramArrayList.add(readExpression());
/* 6960 */       return paramArrayList;
/*      */     } 
/*      */     while (true) {
/* 6963 */       paramArrayList.add(readExpression());
/* 6964 */       if (!readIfMore())
/* 6965 */         return paramArrayList; 
/*      */     } 
/*      */   } private Call parseCall() {
/*      */     boolean bool;
/* 6969 */     Call call = new Call(this.session);
/* 6970 */     this.currentPrepared = (Prepared)call;
/* 6971 */     int i = this.tokenIndex;
/*      */     
/* 6973 */     switch (this.currentTokenType) {
/*      */       case 2:
/* 6975 */         bool = true;
/*      */         break;
/*      */       case 75:
/* 6978 */         read();
/* 6979 */         read(105);
/* 6980 */         call.setTableFunction((TableFunction)readTableFunction(1));
/* 6981 */         return call;
/*      */       default:
/* 6983 */         bool = false; break;
/*      */     } 
/*      */     try {
/* 6986 */       call.setExpression(readExpression());
/* 6987 */     } catch (DbException dbException) {
/* 6988 */       if (bool && dbException.getErrorCode() == 90022) {
/* 6989 */         setTokenIndex(i);
/* 6990 */         String str1 = null, str2 = readIdentifier();
/* 6991 */         if (readIf(110)) {
/* 6992 */           str1 = str2;
/* 6993 */           str2 = readIdentifier();
/* 6994 */           if (readIf(110)) {
/* 6995 */             checkDatabaseName(str1);
/* 6996 */             str1 = str2;
/* 6997 */             str2 = readIdentifier();
/*      */           } 
/*      */         } 
/* 7000 */         read(105);
/* 7001 */         Schema schema = (str1 != null) ? this.database.getSchema(str1) : null;
/* 7002 */         call.setTableFunction(readTableFunction(str2, schema));
/* 7003 */         return call;
/*      */       } 
/* 7005 */       throw dbException;
/*      */     } 
/* 7007 */     return call;
/*      */   }
/*      */   
/*      */   private CreateRole parseCreateRole() {
/* 7011 */     CreateRole createRole = new CreateRole(this.session);
/* 7012 */     createRole.setIfNotExists(readIfNotExists());
/* 7013 */     createRole.setRoleName(readIdentifier());
/* 7014 */     return createRole;
/*      */   }
/*      */   private CreateSchema parseCreateSchema() {
/*      */     String str;
/* 7018 */     CreateSchema createSchema = new CreateSchema(this.session);
/* 7019 */     createSchema.setIfNotExists(readIfNotExists());
/*      */     
/* 7021 */     if (readIf(9)) {
/* 7022 */       str = readIdentifier();
/* 7023 */       createSchema.setSchemaName(str);
/* 7024 */       createSchema.setAuthorization(str);
/*      */     } else {
/* 7026 */       createSchema.setSchemaName(readIdentifier());
/* 7027 */       if (readIf(9)) {
/* 7028 */         str = readIdentifier();
/*      */       } else {
/* 7030 */         str = this.session.getUser().getName();
/*      */       } 
/*      */     } 
/* 7033 */     createSchema.setAuthorization(str);
/* 7034 */     if (readIf(89)) {
/* 7035 */       createSchema.setTableEngineParams(readTableEngineParams());
/*      */     }
/* 7037 */     return createSchema;
/*      */   }
/*      */   
/*      */   private ArrayList<String> readTableEngineParams() {
/* 7041 */     ArrayList<String> arrayList = Utils.newSmallArrayList();
/*      */     while (true) {
/* 7043 */       arrayList.add(readIdentifier());
/* 7044 */       if (!readIf(109))
/* 7045 */         return arrayList; 
/*      */     } 
/*      */   }
/*      */   private CreateSequence parseCreateSequence() {
/* 7049 */     boolean bool = readIfNotExists();
/* 7050 */     String str = readIdentifierWithSchema();
/* 7051 */     CreateSequence createSequence = new CreateSequence(this.session, getSchema());
/* 7052 */     createSequence.setIfNotExists(bool);
/* 7053 */     createSequence.setSequenceName(str);
/* 7054 */     SequenceOptions sequenceOptions = new SequenceOptions();
/* 7055 */     parseSequenceOptions(sequenceOptions, createSequence, true, false);
/* 7056 */     createSequence.setOptions(sequenceOptions);
/* 7057 */     return createSequence;
/*      */   }
/*      */   
/*      */   private boolean readIfNotExists() {
/* 7061 */     if (readIf(40)) {
/* 7062 */       read(57);
/* 7063 */       read(30);
/* 7064 */       return true;
/*      */     } 
/* 7066 */     return false;
/*      */   }
/*      */   
/*      */   private CreateConstant parseCreateConstant() {
/* 7070 */     boolean bool = readIfNotExists();
/* 7071 */     String str = readIdentifierWithSchema();
/* 7072 */     Schema schema = getSchema();
/* 7073 */     if (isKeyword(str)) {
/* 7074 */       throw DbException.get(90114, str);
/*      */     }
/*      */     
/* 7077 */     read(84);
/* 7078 */     Expression expression = readExpression();
/* 7079 */     CreateConstant createConstant = new CreateConstant(this.session, schema);
/* 7080 */     createConstant.setConstantName(str);
/* 7081 */     createConstant.setExpression(expression);
/* 7082 */     createConstant.setIfNotExists(bool);
/* 7083 */     return createConstant;
/*      */   }
/*      */   
/*      */   private CreateAggregate parseCreateAggregate(boolean paramBoolean) {
/* 7087 */     boolean bool = readIfNotExists();
/* 7088 */     String str1 = readIdentifierWithSchema(); String str2;
/* 7089 */     if (isKeyword(str1) || BuiltinFunctions.isBuiltinFunction(this.database, str2 = upperName(str1)) || 
/* 7090 */       Aggregate.getAggregateType(str2) != null) {
/* 7091 */       throw DbException.get(90076, str1);
/*      */     }
/* 7093 */     CreateAggregate createAggregate = new CreateAggregate(this.session, getSchema());
/* 7094 */     createAggregate.setForce(paramBoolean);
/* 7095 */     createAggregate.setName(str1);
/* 7096 */     createAggregate.setIfNotExists(bool);
/* 7097 */     read(33);
/* 7098 */     createAggregate.setJavaClassMethod(readStringOrIdentifier());
/* 7099 */     return createAggregate;
/*      */   }
/*      */   
/*      */   private CreateDomain parseCreateDomain() {
/* 7103 */     boolean bool = readIfNotExists();
/* 7104 */     String str1 = readIdentifierWithSchema();
/* 7105 */     Schema schema = getSchema();
/* 7106 */     CreateDomain createDomain = new CreateDomain(this.session, schema);
/* 7107 */     createDomain.setIfNotExists(bool);
/* 7108 */     createDomain.setTypeName(str1);
/* 7109 */     readIf(7);
/* 7110 */     TypeInfo typeInfo = readIfDataType();
/* 7111 */     if (typeInfo != null) {
/* 7112 */       createDomain.setDataType(typeInfo);
/*      */     } else {
/* 7114 */       String str = readIdentifierWithSchema();
/* 7115 */       createDomain.setParentDomain(getSchema().getDomain(str));
/*      */     } 
/* 7117 */     if (readIf(25)) {
/* 7118 */       createDomain.setDefaultExpression(readExpression());
/*      */     }
/* 7120 */     if (readIf(60)) {
/* 7121 */       read("UPDATE");
/* 7122 */       createDomain.setOnUpdateExpression(readExpression());
/*      */     } 
/*      */     
/* 7125 */     if (readIf("SELECTIVITY")) {
/* 7126 */       readNonNegativeInt();
/*      */     }
/* 7128 */     String str2 = readCommentIf();
/* 7129 */     if (str2 != null) {
/* 7130 */       createDomain.setComment(str2);
/*      */     }
/*      */     while (true) {
/*      */       String str;
/* 7134 */       if (readIf(14)) {
/* 7135 */         str = readIdentifier();
/* 7136 */         read(13);
/* 7137 */       } else if (readIf(13)) {
/* 7138 */         str = null;
/*      */       } else {
/*      */         break;
/*      */       } 
/* 7142 */       AlterDomainAddConstraint alterDomainAddConstraint = new AlterDomainAddConstraint(this.session, schema, bool);
/* 7143 */       alterDomainAddConstraint.setConstraintName(str);
/* 7144 */       alterDomainAddConstraint.setDomainName(str1);
/* 7145 */       this.parseDomainConstraint = true;
/*      */       try {
/* 7147 */         alterDomainAddConstraint.setCheckExpression(readExpression());
/*      */       } finally {
/* 7149 */         this.parseDomainConstraint = false;
/*      */       } 
/* 7151 */       createDomain.addConstraintCommand(alterDomainAddConstraint);
/*      */     } 
/* 7153 */     return createDomain;
/*      */   }
/*      */   private CreateTrigger parseCreateTrigger(boolean paramBoolean) {
/*      */     boolean bool1, bool2;
/* 7157 */     boolean bool = readIfNotExists();
/* 7158 */     String str1 = readIdentifierWithSchema(null);
/* 7159 */     Schema schema = getSchema();
/*      */     
/* 7161 */     if (readIf("INSTEAD")) {
/* 7162 */       read("OF");
/* 7163 */       bool2 = true;
/* 7164 */       bool1 = true;
/* 7165 */     } else if (readIf("BEFORE")) {
/* 7166 */       bool1 = false;
/* 7167 */       bool2 = true;
/*      */     } else {
/* 7169 */       read("AFTER");
/* 7170 */       bool1 = false;
/* 7171 */       bool2 = false;
/*      */     } 
/* 7173 */     int i = 0;
/* 7174 */     boolean bool3 = false;
/* 7175 */     boolean bool4 = (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL) ? true : false;
/*      */     do {
/* 7177 */       if (readIf("INSERT")) {
/* 7178 */         i |= 0x1;
/* 7179 */       } else if (readIf("UPDATE")) {
/* 7180 */         i |= 0x2;
/* 7181 */       } else if (readIf("DELETE")) {
/* 7182 */         i |= 0x4;
/* 7183 */       } else if (readIf(69)) {
/* 7184 */         i |= 0x8;
/* 7185 */       } else if (readIf("ROLLBACK")) {
/* 7186 */         bool3 = true;
/*      */       } else {
/* 7188 */         throw getSyntaxError();
/*      */       } 
/* 7190 */     } while (readIf(109) || (bool4 && readIf(61)));
/* 7191 */     read(60);
/* 7192 */     String str2 = readIdentifierWithSchema();
/* 7193 */     checkSchema(schema);
/* 7194 */     CreateTrigger createTrigger = new CreateTrigger(this.session, getSchema());
/* 7195 */     createTrigger.setForce(paramBoolean);
/* 7196 */     createTrigger.setTriggerName(str1);
/* 7197 */     createTrigger.setIfNotExists(bool);
/* 7198 */     createTrigger.setInsteadOf(bool1);
/* 7199 */     createTrigger.setBefore(bool2);
/* 7200 */     createTrigger.setOnRollback(bool3);
/* 7201 */     createTrigger.setTypeMask(i);
/* 7202 */     createTrigger.setTableName(str2);
/* 7203 */     if (readIf(33)) {
/* 7204 */       read("EACH");
/* 7205 */       if (readIf(66)) {
/* 7206 */         createTrigger.setRowBased(true);
/*      */       } else {
/* 7208 */         read("STATEMENT");
/*      */       } 
/*      */     } 
/* 7211 */     if (readIf("QUEUE")) {
/* 7212 */       createTrigger.setQueueSize(readNonNegativeInt());
/*      */     }
/* 7214 */     createTrigger.setNoWait(readIf("NOWAIT"));
/* 7215 */     if (readIf(7)) {
/* 7216 */       createTrigger.setTriggerSource(readString());
/*      */     } else {
/* 7218 */       read("CALL");
/* 7219 */       createTrigger.setTriggerClassName(readStringOrIdentifier());
/*      */     } 
/* 7221 */     return createTrigger;
/*      */   }
/*      */   
/*      */   private CreateUser parseCreateUser() {
/* 7225 */     CreateUser createUser = new CreateUser(this.session);
/* 7226 */     createUser.setIfNotExists(readIfNotExists());
/* 7227 */     createUser.setUserName(readIdentifier());
/* 7228 */     createUser.setComment(readCommentIf());
/* 7229 */     if (readIf("PASSWORD")) {
/* 7230 */       createUser.setPassword(readExpression());
/* 7231 */     } else if (readIf("SALT")) {
/* 7232 */       createUser.setSalt(readExpression());
/* 7233 */       read("HASH");
/* 7234 */       createUser.setHash(readExpression());
/* 7235 */     } else if (readIf("IDENTIFIED")) {
/* 7236 */       read("BY");
/*      */       
/* 7238 */       createUser.setPassword((Expression)ValueExpression.get(ValueVarchar.get(readIdentifier())));
/*      */     } else {
/* 7240 */       throw getSyntaxError();
/*      */     } 
/* 7242 */     if (readIf("ADMIN")) {
/* 7243 */       createUser.setAdmin(true);
/*      */     }
/* 7245 */     return createUser;
/*      */   }
/*      */   private CreateFunctionAlias parseCreateFunctionAlias(boolean paramBoolean) {
/*      */     String str1;
/* 7249 */     boolean bool = readIfNotExists();
/*      */     
/* 7251 */     if (this.currentTokenType != 2) {
/* 7252 */       str1 = this.currentToken;
/* 7253 */       read();
/* 7254 */       this.schemaName = this.session.getCurrentSchemaName();
/*      */     } else {
/* 7256 */       str1 = readIdentifierWithSchema();
/*      */     } 
/* 7258 */     String str2 = upperName(str1);
/* 7259 */     if (isReservedFunctionName(str2)) {
/* 7260 */       throw DbException.get(90076, str1);
/*      */     }
/* 7262 */     CreateFunctionAlias createFunctionAlias = new CreateFunctionAlias(this.session, getSchema());
/* 7263 */     createFunctionAlias.setForce(paramBoolean);
/* 7264 */     createFunctionAlias.setAliasName(str1);
/* 7265 */     createFunctionAlias.setIfNotExists(bool);
/* 7266 */     createFunctionAlias.setDeterministic(readIf("DETERMINISTIC"));
/*      */     
/* 7268 */     readIf("NOBUFFER");
/* 7269 */     if (readIf(7)) {
/* 7270 */       createFunctionAlias.setSource(readString());
/*      */     } else {
/* 7272 */       read(33);
/* 7273 */       createFunctionAlias.setJavaClassMethod(readStringOrIdentifier());
/*      */     } 
/* 7275 */     return createFunctionAlias;
/*      */   }
/*      */   
/*      */   private String readStringOrIdentifier() {
/* 7279 */     return (this.currentTokenType != 2) ? readString() : readIdentifier();
/*      */   }
/*      */   
/*      */   private boolean isReservedFunctionName(String paramString) {
/* 7283 */     int i = ParserUtil.getTokenType(paramString, false, false);
/* 7284 */     if (i != 2) {
/* 7285 */       if (this.database.isAllowBuiltinAliasOverride()) {
/* 7286 */         switch (i) {
/*      */           case 17:
/*      */           case 21:
/*      */           case 22:
/*      */           case 24:
/*      */           case 39:
/*      */           case 51:
/*      */           case 52:
/*      */           case 54:
/*      */           case 55:
/*      */           case 68:
/*      */           case 90:
/* 7298 */             return false;
/*      */         } 
/*      */       }
/* 7301 */       return true;
/*      */     } 
/* 7303 */     return (Aggregate.getAggregateType(paramString) != null || (
/* 7304 */       BuiltinFunctions.isBuiltinFunction(this.database, paramString) && !this.database.isAllowBuiltinAliasOverride()));
/*      */   }
/*      */   
/*      */   private Prepared parseWith() {
/* 7308 */     ArrayList<TableView> arrayList = new ArrayList();
/*      */     try {
/* 7310 */       return parseWith1(arrayList);
/* 7311 */     } catch (Throwable throwable) {
/* 7312 */       CommandContainer.clearCTE(this.session, arrayList);
/* 7313 */       throw throwable;
/*      */     } 
/*      */   }
/*      */   private Prepared parseWith1(List<TableView> paramList) {
/*      */     Prepared prepared;
/* 7318 */     readIf("RECURSIVE");
/*      */ 
/*      */ 
/*      */     
/* 7322 */     boolean bool = !this.session.isParsingCreateView() ? true : false;
/*      */     
/*      */     do {
/* 7325 */       paramList.add(parseSingleCommonTableExpression(bool));
/* 7326 */     } while (readIf(109));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7333 */     Collections.reverse(paramList);
/*      */     
/* 7335 */     int i = this.tokenIndex;
/* 7336 */     if (isQueryQuick()) {
/* 7337 */       prepared = parseWithQuery();
/* 7338 */     } else if (readIf("INSERT")) {
/* 7339 */       Insert insert = parseInsert(i);
/* 7340 */       insert.setPrepareAlways(true);
/* 7341 */     } else if (readIf("UPDATE")) {
/* 7342 */       Update update = parseUpdate(i);
/* 7343 */       update.setPrepareAlways(true);
/* 7344 */     } else if (readIf("MERGE")) {
/* 7345 */       prepared = parseMerge(i);
/* 7346 */       prepared.setPrepareAlways(true);
/* 7347 */     } else if (readIf("DELETE")) {
/* 7348 */       Delete delete = parseDelete(i);
/* 7349 */       delete.setPrepareAlways(true);
/* 7350 */     } else if (readIf("CREATE")) {
/* 7351 */       if (!isToken(75)) {
/* 7352 */         throw DbException.get(42000, "WITH statement supports only SELECT, TABLE, VALUES, CREATE TABLE, INSERT, UPDATE, MERGE or DELETE statements");
/*      */       }
/*      */       
/* 7355 */       prepared = parseCreate();
/* 7356 */       prepared.setPrepareAlways(true);
/*      */     } else {
/* 7358 */       throw DbException.get(42000, "WITH statement supports only SELECT, TABLE, VALUES, CREATE TABLE, INSERT, UPDATE, MERGE or DELETE statements");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7364 */     if (bool) {
/* 7365 */       if (this.cteCleanups == null) {
/* 7366 */         this.cteCleanups = new ArrayList<>(paramList.size());
/*      */       }
/* 7368 */       this.cteCleanups.addAll(paramList);
/*      */     } 
/* 7370 */     return prepared;
/*      */   }
/*      */   
/*      */   private Prepared parseWithQuery() {
/* 7374 */     Query query = parseQueryExpressionBodyAndEndOfQuery();
/* 7375 */     query.setPrepareAlways(true);
/* 7376 */     query.setNeverLazy(true);
/* 7377 */     return (Prepared)query;
/*      */   } private TableView parseSingleCommonTableExpression(boolean paramBoolean) {
/*      */     Table table1;
/*      */     List<Column> list;
/* 7381 */     String str = readIdentifierWithSchema();
/* 7382 */     Schema schema = getSchema();
/* 7383 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/* 7384 */     String[] arrayOfString1 = null;
/*      */ 
/*      */ 
/*      */     
/* 7388 */     if (readIf(105)) {
/* 7389 */       arrayOfString1 = parseColumnList();
/* 7390 */       for (String str1 : arrayOfString1)
/*      */       {
/*      */         
/* 7393 */         arrayList.add(new Column(str1, TypeInfo.TYPE_VARCHAR));
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 7398 */     if (!paramBoolean) {
/* 7399 */       table1 = getSchema().findTableOrView(this.session, str);
/*      */     } else {
/* 7401 */       table1 = this.session.findLocalTempTable(str);
/*      */     } 
/*      */     
/* 7404 */     if (table1 != null) {
/* 7405 */       if (!(table1 instanceof TableView)) {
/* 7406 */         throw DbException.get(42101, str);
/*      */       }
/*      */       
/* 7409 */       TableView tableView = (TableView)table1;
/* 7410 */       if (!tableView.isTableExpression()) {
/* 7411 */         throw DbException.get(42101, str);
/*      */       }
/*      */       
/* 7414 */       if (!paramBoolean) {
/* 7415 */         table1.lock(this.session, 2);
/* 7416 */         this.database.removeSchemaObject(this.session, (SchemaObject)table1);
/*      */       } else {
/*      */         
/* 7419 */         this.session.removeLocalTempTable(table1);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7428 */     Table table2 = TableView.createShadowTableForRecursiveTableExpression(paramBoolean, this.session, str, schema, arrayList, this.database);
/*      */ 
/*      */     
/* 7431 */     String[] arrayOfString2 = new String[1];
/*      */     try {
/* 7433 */       read(7);
/* 7434 */       read(105);
/* 7435 */       Query query = parseQuery();
/* 7436 */       if (!paramBoolean) {
/* 7437 */         query.session = this.session;
/*      */       }
/* 7439 */       read(106);
/* 7440 */       list = TableView.createQueryColumnTemplateList(arrayOfString1, query, arrayOfString2);
/*      */     } finally {
/*      */       
/* 7443 */       TableView.destroyShadowTableForRecursiveExpression(paramBoolean, this.session, table2);
/*      */     } 
/*      */     
/* 7446 */     return createCTEView(str, arrayOfString2[0], list, true, true, paramBoolean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TableView createCTEView(String paramString1, String paramString2, List<Column> paramList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
/*      */     TableView tableView;
/* 7456 */     Schema schema = getSchemaWithDefault();
/* 7457 */     int i = this.database.allocateObjectId();
/* 7458 */     Column[] arrayOfColumn = paramList.<Column>toArray(new Column[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 7464 */     synchronized (this.session) {
/* 7465 */       tableView = new TableView(schema, i, paramString1, paramString2, this.parameters, arrayOfColumn, this.session, paramBoolean1, false, true, paramBoolean3);
/*      */ 
/*      */ 
/*      */       
/* 7469 */       if (!tableView.isRecursiveQueryDetected() && paramBoolean1) {
/* 7470 */         if (!paramBoolean3) {
/* 7471 */           this.database.addSchemaObject(this.session, (SchemaObject)tableView);
/* 7472 */           tableView.lock(this.session, 2);
/* 7473 */           this.database.removeSchemaObject(this.session, (SchemaObject)tableView);
/*      */         } else {
/* 7475 */           this.session.removeLocalTempTable((Table)tableView);
/*      */         } 
/* 7477 */         tableView = new TableView(schema, i, paramString1, paramString2, this.parameters, arrayOfColumn, this.session, false, false, true, paramBoolean3);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 7483 */       this.database.unlockMeta(this.session);
/*      */     } 
/* 7485 */     tableView.setTableExpression(true);
/* 7486 */     tableView.setTemporary(paramBoolean3);
/* 7487 */     tableView.setHidden(true);
/* 7488 */     tableView.setOnCommitDrop(false);
/* 7489 */     if (paramBoolean2) {
/* 7490 */       if (!paramBoolean3) {
/* 7491 */         this.database.addSchemaObject(this.session, (SchemaObject)tableView);
/* 7492 */         tableView.unlock(this.session);
/* 7493 */         this.database.unlockMeta(this.session);
/*      */       } else {
/* 7495 */         this.session.addLocalTempTable((Table)tableView);
/*      */       } 
/*      */     }
/* 7498 */     return tableView;
/*      */   }
/*      */   
/*      */   private CreateView parseCreateView(boolean paramBoolean1, boolean paramBoolean2) {
/* 7502 */     boolean bool1 = readIfNotExists();
/* 7503 */     boolean bool2 = readIf("TABLE_EXPRESSION");
/* 7504 */     String str1 = readIdentifierWithSchema();
/* 7505 */     CreateView createView = new CreateView(this.session, getSchema());
/* 7506 */     this.createView = createView;
/* 7507 */     createView.setViewName(str1);
/* 7508 */     createView.setIfNotExists(bool1);
/* 7509 */     createView.setComment(readCommentIf());
/* 7510 */     createView.setOrReplace(paramBoolean2);
/* 7511 */     createView.setForce(paramBoolean1);
/* 7512 */     createView.setTableExpression(bool2);
/* 7513 */     if (readIf(105)) {
/* 7514 */       String[] arrayOfString = parseColumnList();
/* 7515 */       createView.setColumnNames(arrayOfString);
/*      */     } 
/* 7517 */     read(7);
/* 7518 */     String str2 = StringUtils.cache(this.sqlCommand.substring(this.token.start()));
/*      */     try {
/*      */       Query query;
/* 7521 */       this.session.setParsingCreateView(true);
/*      */       try {
/* 7523 */         query = parseQuery();
/* 7524 */         query.prepare();
/*      */       } finally {
/* 7526 */         this.session.setParsingCreateView(false);
/*      */       } 
/* 7528 */       createView.setSelect(query);
/* 7529 */     } catch (DbException dbException) {
/* 7530 */       if (paramBoolean1) {
/* 7531 */         createView.setSelectSQL(str2);
/* 7532 */         while (this.currentTokenType != 93) {
/* 7533 */           read();
/*      */         }
/*      */       } else {
/* 7536 */         throw dbException;
/*      */       } 
/*      */     } 
/* 7539 */     return createView;
/*      */   }
/*      */   
/*      */   private TransactionCommand parseCheckpoint() {
/*      */     TransactionCommand transactionCommand;
/* 7544 */     if (readIf("SYNC")) {
/* 7545 */       transactionCommand = new TransactionCommand(this.session, 76);
/*      */     } else {
/*      */       
/* 7548 */       transactionCommand = new TransactionCommand(this.session, 73);
/*      */     } 
/*      */     
/* 7551 */     return transactionCommand;
/*      */   }
/*      */   
/*      */   private Prepared parseAlter() {
/* 7555 */     if (readIf(75))
/* 7556 */       return parseAlterTable(); 
/* 7557 */     if (readIf(82))
/* 7558 */       return (Prepared)parseAlterUser(); 
/* 7559 */     if (readIf("INDEX"))
/* 7560 */       return (Prepared)parseAlterIndex(); 
/* 7561 */     if (readIf("SCHEMA"))
/* 7562 */       return parseAlterSchema(); 
/* 7563 */     if (readIf("SEQUENCE"))
/* 7564 */       return (Prepared)parseAlterSequence(); 
/* 7565 */     if (readIf("VIEW"))
/* 7566 */       return (Prepared)parseAlterView(); 
/* 7567 */     if (readIf("DOMAIN")) {
/* 7568 */       return (Prepared)parseAlterDomain();
/*      */     }
/* 7570 */     throw getSyntaxError();
/*      */   }
/*      */   
/*      */   private void checkSchema(Schema paramSchema) {
/* 7574 */     if (paramSchema != null && getSchema() != paramSchema) {
/* 7575 */       throw DbException.get(90080);
/*      */     }
/*      */   }
/*      */   
/*      */   private AlterIndexRename parseAlterIndex() {
/* 7580 */     boolean bool = readIfExists(false);
/* 7581 */     String str1 = readIdentifierWithSchema();
/* 7582 */     Schema schema = getSchema();
/* 7583 */     AlterIndexRename alterIndexRename = new AlterIndexRename(this.session);
/* 7584 */     alterIndexRename.setOldSchema(schema);
/* 7585 */     alterIndexRename.setOldName(str1);
/* 7586 */     alterIndexRename.setIfExists(bool);
/* 7587 */     read("RENAME");
/* 7588 */     read(76);
/* 7589 */     String str2 = readIdentifierWithSchema(schema.getName());
/* 7590 */     checkSchema(schema);
/* 7591 */     alterIndexRename.setNewName(str2);
/* 7592 */     return alterIndexRename;
/*      */   }
/*      */   
/*      */   private DefineCommand parseAlterDomain() {
/* 7596 */     boolean bool = readIfExists(false);
/* 7597 */     String str = readIdentifierWithSchema();
/* 7598 */     Schema schema = getSchema();
/* 7599 */     if (readIf("ADD")) {
/* 7600 */       boolean bool1 = false;
/* 7601 */       String str1 = null;
/* 7602 */       String str2 = null;
/* 7603 */       if (readIf(14)) {
/* 7604 */         bool1 = readIfNotExists();
/* 7605 */         str1 = readIdentifierWithSchema(schema.getName());
/* 7606 */         checkSchema(schema);
/* 7607 */         str2 = readCommentIf();
/*      */       } 
/* 7609 */       read(13);
/* 7610 */       AlterDomainAddConstraint alterDomainAddConstraint = new AlterDomainAddConstraint(this.session, schema, bool1);
/* 7611 */       alterDomainAddConstraint.setDomainName(str);
/* 7612 */       alterDomainAddConstraint.setConstraintName(str1);
/* 7613 */       this.parseDomainConstraint = true;
/*      */       try {
/* 7615 */         alterDomainAddConstraint.setCheckExpression(readExpression());
/*      */       } finally {
/* 7617 */         this.parseDomainConstraint = false;
/*      */       } 
/* 7619 */       alterDomainAddConstraint.setIfDomainExists(bool);
/* 7620 */       alterDomainAddConstraint.setComment(str2);
/* 7621 */       if (readIf("NOCHECK")) {
/* 7622 */         alterDomainAddConstraint.setCheckExisting(false);
/*      */       } else {
/* 7624 */         readIf(13);
/* 7625 */         alterDomainAddConstraint.setCheckExisting(true);
/*      */       } 
/* 7627 */       return (DefineCommand)alterDomainAddConstraint;
/* 7628 */     }  if (readIf("DROP"))
/* 7629 */     { if (readIf(14)) {
/* 7630 */         boolean bool1 = readIfExists(false);
/* 7631 */         String str1 = readIdentifierWithSchema(schema.getName());
/* 7632 */         checkSchema(schema);
/* 7633 */         AlterDomainDropConstraint alterDomainDropConstraint = new AlterDomainDropConstraint(this.session, getSchema(), bool1);
/*      */         
/* 7635 */         alterDomainDropConstraint.setConstraintName(str1);
/* 7636 */         alterDomainDropConstraint.setDomainName(str);
/* 7637 */         alterDomainDropConstraint.setIfDomainExists(bool);
/* 7638 */         return (DefineCommand)alterDomainDropConstraint;
/* 7639 */       }  if (readIf(25)) {
/* 7640 */         AlterDomainExpressions alterDomainExpressions = new AlterDomainExpressions(this.session, schema, 94);
/*      */         
/* 7642 */         alterDomainExpressions.setDomainName(str);
/* 7643 */         alterDomainExpressions.setIfDomainExists(bool);
/* 7644 */         alterDomainExpressions.setExpression(null);
/* 7645 */         return (DefineCommand)alterDomainExpressions;
/* 7646 */       }  if (readIf(60)) {
/* 7647 */         read("UPDATE");
/* 7648 */         AlterDomainExpressions alterDomainExpressions = new AlterDomainExpressions(this.session, schema, 95);
/*      */         
/* 7650 */         alterDomainExpressions.setDomainName(str);
/* 7651 */         alterDomainExpressions.setIfDomainExists(bool);
/* 7652 */         alterDomainExpressions.setExpression(null);
/* 7653 */         return (DefineCommand)alterDomainExpressions;
/*      */       }  }
/* 7655 */     else { if (readIf("RENAME")) {
/* 7656 */         if (readIf(14)) {
/* 7657 */           String str2 = readIdentifierWithSchema(schema.getName());
/* 7658 */           checkSchema(schema);
/* 7659 */           read(76);
/* 7660 */           AlterDomainRenameConstraint alterDomainRenameConstraint = new AlterDomainRenameConstraint(this.session, schema);
/* 7661 */           alterDomainRenameConstraint.setDomainName(str);
/* 7662 */           alterDomainRenameConstraint.setIfDomainExists(bool);
/* 7663 */           alterDomainRenameConstraint.setConstraintName(str2);
/* 7664 */           alterDomainRenameConstraint.setNewConstraintName(readIdentifier());
/* 7665 */           return (DefineCommand)alterDomainRenameConstraint;
/*      */         } 
/* 7667 */         read(76);
/* 7668 */         String str1 = readIdentifierWithSchema(schema.getName());
/* 7669 */         checkSchema(schema);
/* 7670 */         AlterDomainRename alterDomainRename = new AlterDomainRename(this.session, getSchema());
/* 7671 */         alterDomainRename.setDomainName(str);
/* 7672 */         alterDomainRename.setIfDomainExists(bool);
/* 7673 */         alterDomainRename.setNewDomainName(str1);
/* 7674 */         return (DefineCommand)alterDomainRename;
/*      */       } 
/* 7676 */       read(71);
/* 7677 */       if (readIf(25)) {
/* 7678 */         AlterDomainExpressions alterDomainExpressions = new AlterDomainExpressions(this.session, schema, 94);
/*      */         
/* 7680 */         alterDomainExpressions.setDomainName(str);
/* 7681 */         alterDomainExpressions.setIfDomainExists(bool);
/* 7682 */         alterDomainExpressions.setExpression(readExpression());
/* 7683 */         return (DefineCommand)alterDomainExpressions;
/* 7684 */       }  if (readIf(60)) {
/* 7685 */         read("UPDATE");
/* 7686 */         AlterDomainExpressions alterDomainExpressions = new AlterDomainExpressions(this.session, schema, 95);
/*      */         
/* 7688 */         alterDomainExpressions.setDomainName(str);
/* 7689 */         alterDomainExpressions.setIfDomainExists(bool);
/* 7690 */         alterDomainExpressions.setExpression(readExpression());
/* 7691 */         return (DefineCommand)alterDomainExpressions;
/*      */       }  }
/*      */     
/* 7694 */     throw getSyntaxError();
/*      */   }
/*      */   
/*      */   private DefineCommand parseAlterView() {
/* 7698 */     boolean bool = readIfExists(false);
/* 7699 */     String str = readIdentifierWithSchema();
/* 7700 */     Schema schema = getSchema();
/* 7701 */     Table table = schema.findTableOrView(this.session, str);
/* 7702 */     if (!(table instanceof TableView) && !bool) {
/* 7703 */       throw DbException.get(90037, str);
/*      */     }
/* 7705 */     if (readIf("RENAME")) {
/* 7706 */       read(76);
/* 7707 */       String str1 = readIdentifierWithSchema(schema.getName());
/* 7708 */       checkSchema(schema);
/* 7709 */       AlterTableRename alterTableRename = new AlterTableRename(this.session, getSchema());
/* 7710 */       alterTableRename.setTableName(str);
/* 7711 */       alterTableRename.setNewTableName(str1);
/* 7712 */       alterTableRename.setIfTableExists(bool);
/* 7713 */       return (DefineCommand)alterTableRename;
/*      */     } 
/* 7715 */     read("RECOMPILE");
/* 7716 */     TableView tableView = (TableView)table;
/* 7717 */     AlterView alterView = new AlterView(this.session);
/* 7718 */     alterView.setIfExists(bool);
/* 7719 */     alterView.setView(tableView);
/* 7720 */     return (DefineCommand)alterView;
/*      */   }
/*      */ 
/*      */   
/*      */   private Prepared parseAlterSchema() {
/* 7725 */     boolean bool = readIfExists(false);
/* 7726 */     String str1 = readIdentifierWithSchema();
/* 7727 */     Schema schema1 = getSchema();
/* 7728 */     read("RENAME");
/* 7729 */     read(76);
/* 7730 */     String str2 = readIdentifierWithSchema(schema1.getName());
/* 7731 */     Schema schema2 = findSchema(str1);
/* 7732 */     if (schema2 == null) {
/* 7733 */       if (bool) {
/* 7734 */         return (Prepared)new NoOperation(this.session);
/*      */       }
/* 7736 */       throw DbException.get(90079, str1);
/*      */     } 
/* 7738 */     AlterSchemaRename alterSchemaRename = new AlterSchemaRename(this.session);
/* 7739 */     alterSchemaRename.setOldSchema(schema2);
/* 7740 */     checkSchema(schema1);
/* 7741 */     alterSchemaRename.setNewName(str2);
/* 7742 */     return (Prepared)alterSchemaRename;
/*      */   }
/*      */   
/*      */   private AlterSequence parseAlterSequence() {
/* 7746 */     boolean bool = readIfExists(false);
/* 7747 */     String str = readIdentifierWithSchema();
/* 7748 */     AlterSequence alterSequence = new AlterSequence(this.session, getSchema());
/* 7749 */     alterSequence.setSequenceName(str);
/* 7750 */     alterSequence.setIfExists(bool);
/* 7751 */     SequenceOptions sequenceOptions = new SequenceOptions();
/* 7752 */     parseSequenceOptions(sequenceOptions, null, false, false);
/* 7753 */     alterSequence.setOptions(sequenceOptions);
/* 7754 */     return alterSequence;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean parseSequenceOptions(SequenceOptions paramSequenceOptions, CreateSequence paramCreateSequence, boolean paramBoolean1, boolean paramBoolean2) {
/* 7759 */     boolean bool = false;
/*      */     while (true) {
/* 7761 */       if (paramBoolean1 && readIf(7)) {
/* 7762 */         TypeInfo typeInfo = parseDataType();
/* 7763 */         if (!DataType.isNumericType(typeInfo.getValueType())) {
/* 7764 */           throw DbException.getUnsupportedException(typeInfo
/* 7765 */               .getSQL(new StringBuilder("CREATE SEQUENCE AS "), 3).toString());
/*      */         }
/* 7767 */         paramSequenceOptions.setDataType(typeInfo);
/* 7768 */       } else if (readIf("START")) {
/* 7769 */         read(89);
/* 7770 */         paramSequenceOptions.setStartValue(readExpression());
/* 7771 */       } else if (readIf("RESTART")) {
/* 7772 */         paramSequenceOptions.setRestartValue(readIf(89) ? readExpression() : (Expression)ValueExpression.DEFAULT);
/* 7773 */       } else if (paramCreateSequence == null || !parseCreateSequenceOption(paramCreateSequence)) {
/*      */         
/* 7775 */         if (paramBoolean2) {
/* 7776 */           int i = this.tokenIndex;
/* 7777 */           if (readIf(71)) {
/* 7778 */             if (!parseBasicSequenceOption(paramSequenceOptions)) {
/* 7779 */               setTokenIndex(i);
/*      */               break;
/*      */             } 
/*      */           } else {
/*      */             break;
/*      */           } 
/* 7785 */         } else if (!parseBasicSequenceOption(paramSequenceOptions)) {
/*      */           break;
/*      */         } 
/* 7788 */       }  bool = true;
/*      */     } 
/* 7790 */     return bool;
/*      */   }
/*      */   
/*      */   private boolean parseCreateSequenceOption(CreateSequence paramCreateSequence) {
/* 7794 */     if (readIf("BELONGS_TO_TABLE")) {
/* 7795 */       paramCreateSequence.setBelongsToTable(true);
/* 7796 */     } else if (!readIf(62)) {
/*      */ 
/*      */       
/* 7799 */       return false;
/*      */     } 
/* 7801 */     return true;
/*      */   }
/*      */   
/*      */   private boolean parseBasicSequenceOption(SequenceOptions paramSequenceOptions) {
/* 7805 */     if (readIf("INCREMENT")) {
/* 7806 */       readIf("BY");
/* 7807 */       paramSequenceOptions.setIncrement(readExpression());
/* 7808 */     } else if (readIf("MINVALUE")) {
/* 7809 */       paramSequenceOptions.setMinValue(readExpression());
/* 7810 */     } else if (readIf("MAXVALUE")) {
/* 7811 */       paramSequenceOptions.setMaxValue(readExpression());
/* 7812 */     } else if (readIf("CYCLE")) {
/* 7813 */       paramSequenceOptions.setCycle(Sequence.Cycle.CYCLE);
/* 7814 */     } else if (readIf("NO")) {
/* 7815 */       if (readIf("MINVALUE")) {
/* 7816 */         paramSequenceOptions.setMinValue((Expression)ValueExpression.NULL);
/* 7817 */       } else if (readIf("MAXVALUE")) {
/* 7818 */         paramSequenceOptions.setMaxValue((Expression)ValueExpression.NULL);
/* 7819 */       } else if (readIf("CYCLE")) {
/* 7820 */         paramSequenceOptions.setCycle(Sequence.Cycle.NO_CYCLE);
/* 7821 */       } else if (readIf("CACHE")) {
/* 7822 */         paramSequenceOptions.setCacheSize((Expression)ValueExpression.get((Value)ValueBigint.get(1L)));
/*      */       } else {
/* 7824 */         throw getSyntaxError();
/*      */       } 
/* 7826 */     } else if (readIf("EXHAUSTED")) {
/* 7827 */       paramSequenceOptions.setCycle(Sequence.Cycle.EXHAUSTED);
/* 7828 */     } else if (readIf("CACHE")) {
/* 7829 */       paramSequenceOptions.setCacheSize(readExpression());
/*      */     }
/* 7831 */     else if (readIf("NOMINVALUE")) {
/* 7832 */       paramSequenceOptions.setMinValue((Expression)ValueExpression.NULL);
/* 7833 */     } else if (readIf("NOMAXVALUE")) {
/* 7834 */       paramSequenceOptions.setMaxValue((Expression)ValueExpression.NULL);
/* 7835 */     } else if (readIf("NOCYCLE")) {
/* 7836 */       paramSequenceOptions.setCycle(Sequence.Cycle.NO_CYCLE);
/* 7837 */     } else if (readIf("NOCACHE")) {
/* 7838 */       paramSequenceOptions.setCacheSize((Expression)ValueExpression.get((Value)ValueBigint.get(1L)));
/*      */     } else {
/* 7840 */       return false;
/*      */     } 
/* 7842 */     return true;
/*      */   }
/*      */   
/*      */   private AlterUser parseAlterUser() {
/* 7846 */     String str = readIdentifier();
/* 7847 */     if (readIf(71)) {
/* 7848 */       AlterUser alterUser = new AlterUser(this.session);
/* 7849 */       alterUser.setType(19);
/* 7850 */       alterUser.setUser(this.database.getUser(str));
/* 7851 */       if (readIf("PASSWORD")) {
/* 7852 */         alterUser.setPassword(readExpression());
/* 7853 */       } else if (readIf("SALT")) {
/* 7854 */         alterUser.setSalt(readExpression());
/* 7855 */         read("HASH");
/* 7856 */         alterUser.setHash(readExpression());
/*      */       } else {
/* 7858 */         throw getSyntaxError();
/*      */       } 
/* 7860 */       return alterUser;
/* 7861 */     }  if (readIf("RENAME")) {
/* 7862 */       read(76);
/* 7863 */       AlterUser alterUser = new AlterUser(this.session);
/* 7864 */       alterUser.setType(18);
/* 7865 */       alterUser.setUser(this.database.getUser(str));
/* 7866 */       alterUser.setNewName(readIdentifier());
/* 7867 */       return alterUser;
/* 7868 */     }  if (readIf("ADMIN")) {
/* 7869 */       AlterUser alterUser = new AlterUser(this.session);
/* 7870 */       alterUser.setType(17);
/* 7871 */       User user = this.database.getUser(str);
/* 7872 */       alterUser.setUser(user);
/* 7873 */       if (readIf(77)) {
/* 7874 */         alterUser.setAdmin(true);
/* 7875 */       } else if (readIf(31)) {
/* 7876 */         alterUser.setAdmin(false);
/*      */       } else {
/* 7878 */         throw getSyntaxError();
/*      */       } 
/* 7880 */       return alterUser;
/*      */     } 
/* 7882 */     throw getSyntaxError();
/*      */   }
/*      */   
/*      */   private void readIfEqualOrTo() {
/* 7886 */     if (!readIf(95)) {
/* 7887 */       readIf(76);
/*      */     }
/*      */   }
/*      */   
/*      */   private Prepared parseSet() {
/* 7892 */     if (readIf(101)) {
/* 7893 */       Set set = new Set(this.session, 29);
/* 7894 */       set.setString(readIdentifier());
/* 7895 */       readIfEqualOrTo();
/* 7896 */       set.setExpression(readExpression());
/* 7897 */       return (Prepared)set;
/* 7898 */     }  if (readIf("AUTOCOMMIT")) {
/* 7899 */       readIfEqualOrTo();
/* 7900 */       return (Prepared)new TransactionCommand(this.session, readBooleanSetting() ? 69 : 70);
/*      */     } 
/* 7902 */     if (readIf("EXCLUSIVE")) {
/* 7903 */       readIfEqualOrTo();
/* 7904 */       Set set = new Set(this.session, 27);
/* 7905 */       set.setExpression(readExpression());
/* 7906 */       return (Prepared)set;
/* 7907 */     }  if (readIf("IGNORECASE")) {
/* 7908 */       readIfEqualOrTo();
/* 7909 */       Set set = new Set(this.session, 0);
/* 7910 */       set.setInt(readBooleanSetting() ? 1 : 0);
/* 7911 */       return (Prepared)set;
/* 7912 */     }  if (readIf("PASSWORD")) {
/* 7913 */       readIfEqualOrTo();
/* 7914 */       AlterUser alterUser = new AlterUser(this.session);
/* 7915 */       alterUser.setType(19);
/* 7916 */       alterUser.setUser(this.session.getUser());
/* 7917 */       alterUser.setPassword(readExpression());
/* 7918 */       return (Prepared)alterUser;
/* 7919 */     }  if (readIf("SALT")) {
/* 7920 */       readIfEqualOrTo();
/* 7921 */       AlterUser alterUser = new AlterUser(this.session);
/* 7922 */       alterUser.setType(19);
/* 7923 */       alterUser.setUser(this.session.getUser());
/* 7924 */       alterUser.setSalt(readExpression());
/* 7925 */       read("HASH");
/* 7926 */       alterUser.setHash(readExpression());
/* 7927 */       return (Prepared)alterUser;
/* 7928 */     }  if (readIf("MODE")) {
/* 7929 */       readIfEqualOrTo();
/* 7930 */       Set set = new Set(this.session, 2);
/* 7931 */       set.setString(readIdentifier());
/* 7932 */       return (Prepared)set;
/* 7933 */     }  if (readIf("DATABASE")) {
/* 7934 */       readIfEqualOrTo();
/* 7935 */       read("COLLATION");
/* 7936 */       return (Prepared)parseSetCollation();
/* 7937 */     }  if (readIf("COLLATION")) {
/* 7938 */       readIfEqualOrTo();
/* 7939 */       return (Prepared)parseSetCollation();
/* 7940 */     }  if (readIf("CLUSTER")) {
/* 7941 */       readIfEqualOrTo();
/* 7942 */       Set set = new Set(this.session, 12);
/* 7943 */       set.setString(readString());
/* 7944 */       return (Prepared)set;
/* 7945 */     }  if (readIf("DATABASE_EVENT_LISTENER")) {
/* 7946 */       readIfEqualOrTo();
/* 7947 */       Set set = new Set(this.session, 14);
/* 7948 */       set.setString(readString());
/* 7949 */       return (Prepared)set;
/* 7950 */     }  if (readIf("ALLOW_LITERALS")) {
/* 7951 */       int j; readIfEqualOrTo();
/* 7952 */       Set set = new Set(this.session, 21);
/*      */       
/* 7954 */       if (readIf(3)) {
/* 7955 */         j = 2;
/* 7956 */       } else if (readIf("NONE")) {
/* 7957 */         j = 0;
/* 7958 */       } else if (readIf("NUMBERS")) {
/* 7959 */         j = 1;
/*      */       } else {
/* 7961 */         j = readNonNegativeInt();
/*      */       } 
/* 7963 */       set.setInt(j);
/* 7964 */       return (Prepared)set;
/* 7965 */     }  if (readIf("DEFAULT_TABLE_TYPE")) {
/* 7966 */       int j; readIfEqualOrTo();
/* 7967 */       Set set = new Set(this.session, 6);
/*      */       
/* 7969 */       if (readIf("MEMORY")) {
/* 7970 */         j = 1;
/* 7971 */       } else if (readIf("CACHED")) {
/* 7972 */         j = 0;
/*      */       } else {
/* 7974 */         j = readNonNegativeInt();
/*      */       } 
/* 7976 */       set.setInt(j);
/* 7977 */       return (Prepared)set;
/* 7978 */     }  if (readIf("SCHEMA")) {
/* 7979 */       readIfEqualOrTo();
/* 7980 */       Set set = new Set(this.session, 22);
/* 7981 */       set.setExpression(readExpressionOrIdentifier());
/* 7982 */       return (Prepared)set;
/* 7983 */     }  if (readIf("CATALOG")) {
/* 7984 */       readIfEqualOrTo();
/* 7985 */       Set set = new Set(this.session, 40);
/* 7986 */       set.setExpression(readExpressionOrIdentifier());
/* 7987 */       return (Prepared)set;
/* 7988 */     }  if (readIf(SetTypes.getTypeName(24))) {
/* 7989 */       readIfEqualOrTo();
/* 7990 */       Set set = new Set(this.session, 24);
/* 7991 */       ArrayList<String> arrayList = Utils.newSmallArrayList();
/*      */       while (true)
/* 7993 */       { arrayList.add(readIdentifier());
/* 7994 */         if (!readIf(109))
/* 7995 */         { set.setStringArray(arrayList.<String>toArray(new String[0]));
/* 7996 */           return (Prepared)set; }  } 
/* 7997 */     }  if (readIf("JAVA_OBJECT_SERIALIZER")) {
/* 7998 */       readIfEqualOrTo();
/* 7999 */       Set set = new Set(this.session, 32);
/* 8000 */       set.setString(readString());
/* 8001 */       return (Prepared)set;
/* 8002 */     }  if (readIf("IGNORE_CATALOGS")) {
/* 8003 */       readIfEqualOrTo();
/* 8004 */       Set set = new Set(this.session, 39);
/* 8005 */       set.setInt(readBooleanSetting() ? 1 : 0);
/* 8006 */       return (Prepared)set;
/* 8007 */     }  if (readIf("SESSION")) {
/* 8008 */       read("CHARACTERISTICS");
/* 8009 */       read(7);
/* 8010 */       read("TRANSACTION");
/* 8011 */       return parseSetTransactionMode();
/* 8012 */     }  if (readIf("TRANSACTION"))
/*      */     {
/* 8014 */       return parseSetTransactionMode(); } 
/* 8015 */     if (readIf("TIME")) {
/* 8016 */       read("ZONE");
/* 8017 */       Set set = new Set(this.session, 42);
/* 8018 */       if (!readIf("LOCAL")) {
/* 8019 */         set.setExpression(readExpression());
/*      */       }
/* 8021 */       return (Prepared)set;
/* 8022 */     }  if (readIf("NON_KEYWORDS")) {
/* 8023 */       readIfEqualOrTo();
/* 8024 */       Set set = new Set(this.session, 41);
/* 8025 */       ArrayList<String> arrayList = Utils.newSmallArrayList();
/* 8026 */       if (this.currentTokenType != 93 && this.currentTokenType != 115) {
/*      */         do {
/* 8028 */           if (this.currentTokenType < 2 || this.currentTokenType > 91) {
/* 8029 */             throw getSyntaxError();
/*      */           }
/* 8031 */           arrayList.add(StringUtils.toUpperEnglish(this.currentToken));
/* 8032 */           read();
/* 8033 */         } while (readIf(109));
/*      */       }
/* 8035 */       set.setStringArray(arrayList.<String>toArray(new String[0]));
/* 8036 */       return (Prepared)set;
/* 8037 */     }  if (readIf("DEFAULT_NULL_ORDERING")) {
/* 8038 */       readIfEqualOrTo();
/* 8039 */       Set set = new Set(this.session, 44);
/* 8040 */       set.setString(readIdentifier());
/* 8041 */       return (Prepared)set;
/* 8042 */     }  if (readIf("LOG")) {
/* 8043 */       throw DbException.getUnsupportedException("LOG");
/*      */     }
/* 8045 */     String str = upperName(this.currentToken);
/* 8046 */     if (ConnectionInfo.isIgnoredByParser(str)) {
/* 8047 */       read();
/* 8048 */       readIfEqualOrTo();
/* 8049 */       read();
/* 8050 */       return (Prepared)new NoOperation(this.session);
/*      */     } 
/* 8052 */     int i = SetTypes.getType(str);
/* 8053 */     if (i >= 0) {
/* 8054 */       read();
/* 8055 */       readIfEqualOrTo();
/* 8056 */       Set set = new Set(this.session, i);
/* 8057 */       set.setExpression(readExpression());
/* 8058 */       return (Prepared)set;
/*      */     } 
/* 8060 */     Mode.ModeEnum modeEnum = this.database.getMode().getEnum();
/* 8061 */     if (modeEnum != Mode.ModeEnum.REGULAR) {
/* 8062 */       Prepared prepared = readSetCompatibility(modeEnum);
/* 8063 */       if (prepared != null) {
/* 8064 */         return prepared;
/*      */       }
/*      */     } 
/* 8067 */     if (this.session.isQuirksMode()) {
/* 8068 */       switch (str) {
/*      */         case "BINARY_COLLATION":
/*      */         case "UUID_COLLATION":
/* 8071 */           read();
/* 8072 */           readIfEqualOrTo();
/* 8073 */           readIdentifier();
/* 8074 */           return (Prepared)new NoOperation(this.session);
/*      */       } 
/*      */     }
/* 8077 */     throw getSyntaxError();
/*      */   }
/*      */ 
/*      */   
/*      */   private Prepared parseSetTransactionMode() {
/*      */     IsolationLevel isolationLevel;
/* 8083 */     read("ISOLATION");
/* 8084 */     read("LEVEL");
/* 8085 */     if (readIf("READ")) {
/* 8086 */       if (readIf("UNCOMMITTED")) {
/* 8087 */         isolationLevel = IsolationLevel.READ_UNCOMMITTED;
/*      */       } else {
/* 8089 */         read("COMMITTED");
/* 8090 */         isolationLevel = IsolationLevel.READ_COMMITTED;
/*      */       } 
/* 8092 */     } else if (readIf("REPEATABLE")) {
/* 8093 */       read("READ");
/* 8094 */       isolationLevel = IsolationLevel.REPEATABLE_READ;
/* 8095 */     } else if (readIf("SNAPSHOT")) {
/* 8096 */       isolationLevel = IsolationLevel.SNAPSHOT;
/*      */     } else {
/* 8098 */       read("SERIALIZABLE");
/* 8099 */       isolationLevel = IsolationLevel.SERIALIZABLE;
/*      */     } 
/* 8101 */     return (Prepared)new SetSessionCharacteristics(this.session, isolationLevel);
/*      */   }
/*      */   
/*      */   private Expression readExpressionOrIdentifier() {
/* 8105 */     if (isIdentifier()) {
/* 8106 */       return (Expression)ValueExpression.get(ValueVarchar.get(readIdentifier()));
/*      */     }
/* 8108 */     return readExpression();
/*      */   }
/*      */   
/*      */   private Prepared parseUse() {
/* 8112 */     readIfEqualOrTo();
/* 8113 */     Set set = new Set(this.session, 22);
/* 8114 */     set.setExpression((Expression)ValueExpression.get(ValueVarchar.get(readIdentifier())));
/* 8115 */     return (Prepared)set;
/*      */   }
/*      */   
/*      */   private Set parseSetCollation() {
/* 8119 */     Set set = new Set(this.session, 11);
/* 8120 */     String str = readIdentifier();
/* 8121 */     set.setString(str);
/* 8122 */     if (equalsToken(str, "OFF")) {
/* 8123 */       return set;
/*      */     }
/* 8125 */     Collator collator = CompareMode.getCollator(str);
/* 8126 */     if (collator == null) {
/* 8127 */       throw DbException.getInvalidValueException("collation", str);
/*      */     }
/* 8129 */     if (readIf("STRENGTH")) {
/* 8130 */       if (readIf(63)) {
/* 8131 */         set.setInt(0);
/* 8132 */       } else if (readIf("SECONDARY")) {
/* 8133 */         set.setInt(1);
/* 8134 */       } else if (readIf("TERTIARY")) {
/* 8135 */         set.setInt(2);
/* 8136 */       } else if (readIf("IDENTICAL")) {
/* 8137 */         set.setInt(3);
/*      */       } 
/*      */     } else {
/* 8140 */       set.setInt(collator.getStrength());
/*      */     } 
/* 8142 */     return set;
/*      */   }
/*      */   
/*      */   private Prepared readSetCompatibility(Mode.ModeEnum paramModeEnum) {
/* 8146 */     switch (paramModeEnum) {
/*      */       case Derby:
/* 8148 */         if (readIf("CREATE")) {
/* 8149 */           readIfEqualOrTo();
/*      */           
/* 8151 */           read();
/* 8152 */           return (Prepared)new NoOperation(this.session);
/*      */         } 
/*      */         break;
/*      */       case HSQLDB:
/* 8156 */         if (readIf("LOGSIZE")) {
/* 8157 */           readIfEqualOrTo();
/* 8158 */           Set set = new Set(this.session, 1);
/* 8159 */           set.setExpression(readExpression());
/* 8160 */           return (Prepared)set;
/*      */         } 
/*      */         break;
/*      */       case MySQL:
/* 8164 */         if (readIf("FOREIGN_KEY_CHECKS")) {
/* 8165 */           readIfEqualOrTo();
/* 8166 */           Set set = new Set(this.session, 25);
/* 8167 */           set.setExpression(readExpression());
/* 8168 */           return (Prepared)set;
/* 8169 */         }  if (readIf("NAMES")) {
/*      */           
/* 8171 */           readIfEqualOrTo();
/* 8172 */           read();
/* 8173 */           return (Prepared)new NoOperation(this.session);
/*      */         } 
/*      */         break;
/*      */       case PostgreSQL:
/* 8177 */         if (readIf("STATEMENT_TIMEOUT")) {
/* 8178 */           readIfEqualOrTo();
/* 8179 */           Set set = new Set(this.session, 30);
/* 8180 */           set.setInt(readNonNegativeInt());
/* 8181 */           return (Prepared)set;
/* 8182 */         }  if (readIf("CLIENT_ENCODING") || readIf("CLIENT_MIN_MESSAGES") || readIf("JOIN_COLLAPSE_LIMIT")) {
/* 8183 */           readIfEqualOrTo();
/* 8184 */           read();
/* 8185 */           return (Prepared)new NoOperation(this.session);
/* 8186 */         }  if (readIf("DATESTYLE")) {
/* 8187 */           readIfEqualOrTo();
/* 8188 */           if (!readIf("ISO")) {
/* 8189 */             String str = readString();
/* 8190 */             if (!equalsToken(str, "ISO")) {
/* 8191 */               throw getSyntaxError();
/*      */             }
/*      */           } 
/* 8194 */           return (Prepared)new NoOperation(this.session);
/* 8195 */         }  if (readIf("SEARCH_PATH")) {
/* 8196 */           readIfEqualOrTo();
/* 8197 */           Set set = new Set(this.session, 24);
/* 8198 */           ArrayList<String> arrayList = Utils.newSmallArrayList();
/* 8199 */           String str = this.database.sysIdentifier("PG_CATALOG");
/* 8200 */           boolean bool = false;
/*      */           
/*      */           do {
/* 8203 */             String str1 = (this.currentTokenType == 94) ? readString() : readIdentifier();
/* 8204 */             if ("$user".equals(str1)) {
/*      */               continue;
/*      */             }
/* 8207 */             if (str.equals(str1)) {
/* 8208 */               bool = true;
/*      */             }
/* 8210 */             arrayList.add(str1);
/* 8211 */           } while (readIf(109));
/*      */ 
/*      */ 
/*      */           
/* 8215 */           if (!bool && 
/* 8216 */             this.database.findSchema(str) != null) {
/* 8217 */             arrayList.add(0, str);
/*      */           }
/*      */           
/* 8220 */           set.setStringArray(arrayList.<String>toArray(new String[0]));
/* 8221 */           return (Prepared)set;
/*      */         } 
/*      */         break;
/*      */     } 
/*      */     
/* 8226 */     return null;
/*      */   }
/*      */   
/*      */   private RunScriptCommand parseRunScript() {
/* 8230 */     RunScriptCommand runScriptCommand = new RunScriptCommand(this.session);
/* 8231 */     read(35);
/* 8232 */     runScriptCommand.setFileNameExpr(readExpression());
/* 8233 */     if (readIf("COMPRESSION")) {
/* 8234 */       runScriptCommand.setCompressionAlgorithm(readIdentifier());
/*      */     }
/* 8236 */     if (readIf("CIPHER")) {
/* 8237 */       runScriptCommand.setCipher(readIdentifier());
/* 8238 */       if (readIf("PASSWORD")) {
/* 8239 */         runScriptCommand.setPassword(readExpression());
/*      */       }
/*      */     } 
/* 8242 */     if (readIf("CHARSET")) {
/* 8243 */       runScriptCommand.setCharset(Charset.forName(readString()));
/*      */     }
/* 8245 */     if (readIf("FROM_1X")) {
/* 8246 */       runScriptCommand.setFrom1X();
/*      */     } else {
/* 8248 */       if (readIf("QUIRKS_MODE")) {
/* 8249 */         runScriptCommand.setQuirksMode(true);
/*      */       }
/* 8251 */       if (readIf("VARIABLE_BINARY")) {
/* 8252 */         runScriptCommand.setVariableBinary(true);
/*      */       }
/*      */     } 
/* 8255 */     return runScriptCommand;
/*      */   }
/*      */   
/*      */   private ScriptCommand parseScript() {
/* 8259 */     ScriptCommand scriptCommand = new ScriptCommand(this.session);
/* 8260 */     boolean bool1 = true, bool2 = true, bool3 = true, bool4 = true;
/* 8261 */     boolean bool5 = false, bool6 = false, bool7 = false;
/* 8262 */     if (readIf("NODATA")) {
/* 8263 */       bool1 = false;
/*      */     } else {
/* 8265 */       if (readIf("SIMPLE")) {
/* 8266 */         bool6 = true;
/*      */       }
/* 8268 */       if (readIf("COLUMNS")) {
/* 8269 */         bool7 = true;
/*      */       }
/*      */     } 
/* 8272 */     if (readIf("NOPASSWORDS")) {
/* 8273 */       bool2 = false;
/*      */     }
/* 8275 */     if (readIf("NOSETTINGS")) {
/* 8276 */       bool3 = false;
/*      */     }
/* 8278 */     if (readIf("NOVERSION")) {
/* 8279 */       bool4 = false;
/*      */     }
/* 8281 */     if (readIf("DROP")) {
/* 8282 */       bool5 = true;
/*      */     }
/* 8284 */     if (readIf("BLOCKSIZE")) {
/* 8285 */       long l = readLong();
/* 8286 */       scriptCommand.setLobBlockSize(l);
/*      */     } 
/* 8288 */     scriptCommand.setData(bool1);
/* 8289 */     scriptCommand.setPasswords(bool2);
/* 8290 */     scriptCommand.setSettings(bool3);
/* 8291 */     scriptCommand.setVersion(bool4);
/* 8292 */     scriptCommand.setDrop(bool5);
/* 8293 */     scriptCommand.setSimple(bool6);
/* 8294 */     scriptCommand.setWithColumns(bool7);
/* 8295 */     if (readIf(76)) {
/* 8296 */       scriptCommand.setFileNameExpr(readExpression());
/* 8297 */       if (readIf("COMPRESSION")) {
/* 8298 */         scriptCommand.setCompressionAlgorithm(readIdentifier());
/*      */       }
/* 8300 */       if (readIf("CIPHER")) {
/* 8301 */         scriptCommand.setCipher(readIdentifier());
/* 8302 */         if (readIf("PASSWORD")) {
/* 8303 */           scriptCommand.setPassword(readExpression());
/*      */         }
/*      */       } 
/* 8306 */       if (readIf("CHARSET")) {
/* 8307 */         scriptCommand.setCharset(Charset.forName(readString()));
/*      */       }
/*      */     } 
/* 8310 */     if (readIf("SCHEMA"))
/* 8311 */     { HashSet<String> hashSet = new HashSet();
/*      */       while (true)
/* 8313 */       { hashSet.add(readIdentifier());
/* 8314 */         if (!readIf(109))
/* 8315 */         { scriptCommand.setSchemaNames(hashSet);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 8323 */           return scriptCommand; }  }  }  if (readIf(75)) { ArrayList<Table> arrayList = Utils.newSmallArrayList(); while (true) { arrayList.add(readTableOrView()); if (!readIf(109)) { scriptCommand.setTables(arrayList); break; }  }  }  return scriptCommand;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isDualTable(String paramString) {
/* 8334 */     return (((this.schemaName == null || equalsToken(this.schemaName, "SYS")) && equalsToken("DUAL", paramString)) || (
/* 8335 */       (this.database.getMode()).sysDummy1 && (this.schemaName == null || equalsToken(this.schemaName, "SYSIBM")) && 
/* 8336 */       equalsToken("SYSDUMMY1", paramString)));
/*      */   }
/*      */   
/*      */   private Table readTableOrView() {
/* 8340 */     return readTableOrView(readIdentifierWithSchema(null));
/*      */   }
/*      */   
/*      */   private Table readTableOrView(String paramString) {
/* 8344 */     if (this.schemaName != null) {
/* 8345 */       Table table = getSchema().resolveTableOrView(this.session, paramString);
/* 8346 */       if (table != null) {
/* 8347 */         return table;
/*      */       }
/*      */     } else {
/*      */       
/* 8351 */       Table table = this.database.getSchema(this.session.getCurrentSchemaName()).resolveTableOrView(this.session, paramString);
/* 8352 */       if (table != null) {
/* 8353 */         return table;
/*      */       }
/* 8355 */       String[] arrayOfString = this.session.getSchemaSearchPath();
/* 8356 */       if (arrayOfString != null) {
/* 8357 */         for (String str : arrayOfString) {
/* 8358 */           Schema schema = this.database.getSchema(str);
/* 8359 */           table = schema.resolveTableOrView(this.session, paramString);
/* 8360 */           if (table != null) {
/* 8361 */             return table;
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/* 8366 */     if (isDualTable(paramString)) {
/* 8367 */       return (Table)new DualTable(this.database);
/*      */     }
/*      */     
/* 8370 */     throw getTableOrViewNotFoundDbException(paramString);
/*      */   }
/*      */   
/*      */   private DbException getTableOrViewNotFoundDbException(String paramString) {
/* 8374 */     if (this.schemaName != null) {
/* 8375 */       return getTableOrViewNotFoundDbException(this.schemaName, paramString);
/*      */     }
/*      */     
/* 8378 */     String str = this.session.getCurrentSchemaName();
/* 8379 */     String[] arrayOfString = this.session.getSchemaSearchPath();
/* 8380 */     if (arrayOfString == null) {
/* 8381 */       return getTableOrViewNotFoundDbException(Collections.singleton(str), paramString);
/*      */     }
/*      */     
/* 8384 */     LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
/* 8385 */     linkedHashSet.add(str);
/* 8386 */     linkedHashSet.addAll(Arrays.asList(arrayOfString));
/* 8387 */     return getTableOrViewNotFoundDbException(linkedHashSet, paramString);
/*      */   }
/*      */   
/*      */   private DbException getTableOrViewNotFoundDbException(String paramString1, String paramString2) {
/* 8391 */     return getTableOrViewNotFoundDbException(Collections.singleton(paramString1), paramString2);
/*      */   }
/*      */ 
/*      */   
/*      */   private DbException getTableOrViewNotFoundDbException(Set<String> paramSet, String paramString) {
/* 8396 */     if (this.database == null || this.database.getFirstUserTable() == null) {
/* 8397 */       return DbException.get(42104, paramString);
/*      */     }
/*      */     
/* 8400 */     if ((this.database.getSettings()).caseInsensitiveIdentifiers) {
/* 8401 */       return DbException.get(42102, paramString);
/*      */     }
/*      */     
/* 8404 */     TreeSet<String> treeSet = new TreeSet();
/* 8405 */     for (String str : paramSet) {
/* 8406 */       findTableNameCandidates(str, paramString, treeSet);
/*      */     }
/*      */     
/* 8409 */     if (treeSet.isEmpty()) {
/* 8410 */       return DbException.get(42102, paramString);
/*      */     }
/*      */     
/* 8413 */     return DbException.get(42103, new String[] { paramString, 
/*      */           
/* 8415 */           String.join(", ", (Iterable)treeSet) });
/*      */   }
/*      */   
/*      */   private void findTableNameCandidates(String paramString1, String paramString2, Set<String> paramSet) {
/* 8419 */     Schema schema = this.database.getSchema(paramString1);
/* 8420 */     String str = StringUtils.toUpperEnglish(paramString2);
/* 8421 */     Collection collection = schema.getAllTablesAndViews(this.session);
/* 8422 */     for (Table table : collection) {
/* 8423 */       String str1 = table.getName();
/* 8424 */       if (str.equals(StringUtils.toUpperEnglish(str1))) {
/* 8425 */         paramSet.add(str1);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private UserDefinedFunction findUserDefinedFunctionWithinPath(Schema paramSchema, String paramString) {
/* 8431 */     if (paramSchema != null) {
/* 8432 */       return paramSchema.findFunctionOrAggregate(paramString);
/*      */     }
/* 8434 */     paramSchema = this.database.getSchema(this.session.getCurrentSchemaName());
/* 8435 */     UserDefinedFunction userDefinedFunction = paramSchema.findFunctionOrAggregate(paramString);
/* 8436 */     if (userDefinedFunction != null) {
/* 8437 */       return userDefinedFunction;
/*      */     }
/* 8439 */     String[] arrayOfString = this.session.getSchemaSearchPath();
/* 8440 */     if (arrayOfString != null) {
/* 8441 */       for (String str : arrayOfString) {
/* 8442 */         Schema schema = this.database.getSchema(str);
/* 8443 */         if (schema != paramSchema) {
/* 8444 */           userDefinedFunction = schema.findFunctionOrAggregate(paramString);
/* 8445 */           if (userDefinedFunction != null) {
/* 8446 */             return userDefinedFunction;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/* 8451 */     return null;
/*      */   }
/*      */   
/*      */   private Sequence findSequence(String paramString1, String paramString2) {
/* 8455 */     Sequence sequence = this.database.getSchema(paramString1).findSequence(paramString2);
/*      */     
/* 8457 */     if (sequence != null) {
/* 8458 */       return sequence;
/*      */     }
/* 8460 */     String[] arrayOfString = this.session.getSchemaSearchPath();
/* 8461 */     if (arrayOfString != null) {
/* 8462 */       for (String str : arrayOfString) {
/* 8463 */         sequence = this.database.getSchema(str).findSequence(paramString2);
/* 8464 */         if (sequence != null) {
/* 8465 */           return sequence;
/*      */         }
/*      */       } 
/*      */     }
/* 8469 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private Sequence readSequence() {
/* 8474 */     String str = readIdentifierWithSchema(null);
/* 8475 */     if (this.schemaName != null) {
/* 8476 */       return getSchema().getSequence(str);
/*      */     }
/* 8478 */     Sequence sequence = findSequence(this.session.getCurrentSchemaName(), str);
/*      */     
/* 8480 */     if (sequence != null) {
/* 8481 */       return sequence;
/*      */     }
/* 8483 */     throw DbException.get(90036, str);
/*      */   }
/*      */   
/*      */   private Prepared parseAlterTable() {
/* 8487 */     boolean bool = readIfExists(false);
/* 8488 */     String str = readIdentifierWithSchema();
/* 8489 */     Schema schema = getSchema();
/* 8490 */     if (readIf("ADD")) {
/* 8491 */       DefineCommand defineCommand = parseTableConstraintIf(str, schema, bool);
/* 8492 */       if (defineCommand != null) {
/* 8493 */         return (Prepared)defineCommand;
/*      */       }
/* 8495 */       return (Prepared)parseAlterTableAddColumn(str, schema, bool);
/* 8496 */     }  if (readIf(71))
/* 8497 */       return parseAlterTableSet(schema, str, bool); 
/* 8498 */     if (readIf("RENAME"))
/* 8499 */       return parseAlterTableRename(schema, str, bool); 
/* 8500 */     if (readIf("DROP"))
/* 8501 */       return parseAlterTableDrop(schema, str, bool); 
/* 8502 */     if (readIf("ALTER")) {
/* 8503 */       return parseAlterTableAlter(schema, str, bool);
/*      */     }
/* 8505 */     Mode mode = this.database.getMode();
/* 8506 */     if (mode.alterTableExtensionsMySQL || mode.alterTableModifyColumn) {
/* 8507 */       return parseAlterTableCompatibility(schema, str, bool, mode);
/*      */     }
/*      */     
/* 8510 */     throw getSyntaxError();
/*      */   }
/*      */   
/*      */   private Prepared parseAlterTableAlter(Schema paramSchema, String paramString, boolean paramBoolean) {
/* 8514 */     readIf("COLUMN");
/* 8515 */     boolean bool = readIfExists(false);
/* 8516 */     String str = readIdentifier();
/* 8517 */     Column column = columnIfTableExists(paramSchema, paramString, str, paramBoolean, bool);
/* 8518 */     if (readIf("RENAME")) {
/* 8519 */       read(76);
/* 8520 */       AlterTableRenameColumn alterTableRenameColumn = new AlterTableRenameColumn(this.session, paramSchema);
/*      */       
/* 8522 */       alterTableRenameColumn.setTableName(paramString);
/* 8523 */       alterTableRenameColumn.setIfTableExists(paramBoolean);
/* 8524 */       alterTableRenameColumn.setIfExists(bool);
/* 8525 */       alterTableRenameColumn.setOldColumnName(str);
/* 8526 */       String str1 = readIdentifier();
/* 8527 */       alterTableRenameColumn.setNewColumnName(str1);
/* 8528 */       return (Prepared)alterTableRenameColumn;
/* 8529 */     }  if (readIf("DROP")) {
/* 8530 */       if (readIf(25)) {
/* 8531 */         if (readIf(60)) {
/* 8532 */           read(58);
/* 8533 */           AlterTableAlterColumn alterTableAlterColumn1 = new AlterTableAlterColumn(this.session, paramSchema);
/* 8534 */           alterTableAlterColumn1.setTableName(paramString);
/* 8535 */           alterTableAlterColumn1.setIfTableExists(paramBoolean);
/* 8536 */           alterTableAlterColumn1.setOldColumn(column);
/* 8537 */           alterTableAlterColumn1.setType(100);
/* 8538 */           alterTableAlterColumn1.setBooleanFlag(false);
/* 8539 */           return (Prepared)alterTableAlterColumn1;
/*      */         } 
/* 8541 */         return getAlterTableAlterColumnDropDefaultExpression(paramSchema, paramString, paramBoolean, column, 10);
/*      */       } 
/* 8543 */       if (readIf("EXPRESSION")) {
/* 8544 */         return getAlterTableAlterColumnDropDefaultExpression(paramSchema, paramString, paramBoolean, column, 98);
/*      */       }
/* 8546 */       if (readIf("IDENTITY")) {
/* 8547 */         return getAlterTableAlterColumnDropDefaultExpression(paramSchema, paramString, paramBoolean, column, 99);
/*      */       }
/*      */       
/* 8550 */       if (readIf(60)) {
/* 8551 */         read("UPDATE");
/* 8552 */         AlterTableAlterColumn alterTableAlterColumn1 = new AlterTableAlterColumn(this.session, paramSchema);
/* 8553 */         alterTableAlterColumn1.setTableName(paramString);
/* 8554 */         alterTableAlterColumn1.setIfTableExists(paramBoolean);
/* 8555 */         alterTableAlterColumn1.setOldColumn(column);
/* 8556 */         alterTableAlterColumn1.setType(90);
/* 8557 */         alterTableAlterColumn1.setDefaultExpression(null);
/* 8558 */         return (Prepared)alterTableAlterColumn1;
/*      */       } 
/* 8560 */       read(57);
/* 8561 */       read(58);
/* 8562 */       AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/*      */       
/* 8564 */       alterTableAlterColumn.setTableName(paramString);
/* 8565 */       alterTableAlterColumn.setIfTableExists(paramBoolean);
/* 8566 */       alterTableAlterColumn.setOldColumn(column);
/* 8567 */       alterTableAlterColumn.setType(9);
/* 8568 */       return (Prepared)alterTableAlterColumn;
/* 8569 */     }  if (readIf("TYPE"))
/*      */     {
/* 8571 */       return (Prepared)parseAlterTableAlterColumnDataType(paramSchema, paramString, str, paramBoolean, bool); } 
/* 8572 */     if (readIf("SELECTIVITY")) {
/* 8573 */       AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/*      */       
/* 8575 */       alterTableAlterColumn.setTableName(paramString);
/* 8576 */       alterTableAlterColumn.setIfTableExists(paramBoolean);
/* 8577 */       alterTableAlterColumn.setType(13);
/* 8578 */       alterTableAlterColumn.setOldColumn(column);
/* 8579 */       alterTableAlterColumn.setSelectivity(readExpression());
/* 8580 */       return (Prepared)alterTableAlterColumn;
/*      */     } 
/* 8582 */     Prepared prepared = parseAlterTableAlterColumnIdentity(paramSchema, paramString, paramBoolean, column);
/* 8583 */     if (prepared != null) {
/* 8584 */       return prepared;
/*      */     }
/* 8586 */     if (readIf(71)) {
/* 8587 */       return parseAlterTableAlterColumnSet(paramSchema, paramString, paramBoolean, bool, str, column);
/*      */     }
/* 8589 */     return (Prepared)parseAlterTableAlterColumnType(paramSchema, paramString, str, paramBoolean, bool, true);
/*      */   }
/*      */ 
/*      */   
/*      */   private Prepared getAlterTableAlterColumnDropDefaultExpression(Schema paramSchema, String paramString, boolean paramBoolean, Column paramColumn, int paramInt) {
/* 8594 */     AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/* 8595 */     alterTableAlterColumn.setTableName(paramString);
/* 8596 */     alterTableAlterColumn.setIfTableExists(paramBoolean);
/* 8597 */     alterTableAlterColumn.setOldColumn(paramColumn);
/* 8598 */     alterTableAlterColumn.setType(paramInt);
/* 8599 */     alterTableAlterColumn.setDefaultExpression(null);
/* 8600 */     return (Prepared)alterTableAlterColumn;
/*      */   }
/*      */ 
/*      */   
/*      */   private Prepared parseAlterTableAlterColumnIdentity(Schema paramSchema, String paramString, boolean paramBoolean, Column paramColumn) {
/* 8605 */     int i = this.tokenIndex;
/* 8606 */     Boolean bool = null;
/* 8607 */     if (readIf(71) && readIf("GENERATED")) {
/* 8608 */       if (readIf("ALWAYS")) {
/* 8609 */         bool = Boolean.valueOf(true);
/*      */       } else {
/* 8611 */         read("BY");
/* 8612 */         read(25);
/* 8613 */         bool = Boolean.valueOf(false);
/*      */       } 
/*      */     } else {
/* 8616 */       setTokenIndex(i);
/*      */     } 
/* 8618 */     SequenceOptions sequenceOptions = new SequenceOptions();
/* 8619 */     if (!parseSequenceOptions(sequenceOptions, null, false, true) && bool == null) {
/* 8620 */       return null;
/*      */     }
/* 8622 */     if (paramColumn == null) {
/* 8623 */       return (Prepared)new NoOperation(this.session);
/*      */     }
/* 8625 */     if (!paramColumn.isIdentity()) {
/* 8626 */       AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/* 8627 */       parseAlterColumnUsingIf(alterTableAlterColumn);
/* 8628 */       alterTableAlterColumn.setTableName(paramString);
/* 8629 */       alterTableAlterColumn.setIfTableExists(paramBoolean);
/* 8630 */       alterTableAlterColumn.setType(11);
/* 8631 */       alterTableAlterColumn.setOldColumn(paramColumn);
/* 8632 */       Column column = paramColumn.getClone();
/* 8633 */       column.setIdentityOptions(sequenceOptions, (bool != null && bool.booleanValue()));
/* 8634 */       alterTableAlterColumn.setNewColumn(column);
/* 8635 */       return (Prepared)alterTableAlterColumn;
/*      */     } 
/* 8637 */     AlterSequence alterSequence = new AlterSequence(this.session, paramSchema);
/* 8638 */     alterSequence.setColumn(paramColumn, bool);
/* 8639 */     alterSequence.setOptions(sequenceOptions);
/* 8640 */     return commandIfTableExists(paramSchema, paramString, paramBoolean, (Prepared)alterSequence);
/*      */   }
/*      */ 
/*      */   
/*      */   private Prepared parseAlterTableAlterColumnSet(Schema paramSchema, String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2, Column paramColumn) {
/* 8645 */     if (readIf("DATA")) {
/* 8646 */       read("TYPE");
/* 8647 */       return (Prepared)parseAlterTableAlterColumnDataType(paramSchema, paramString1, paramString2, paramBoolean1, paramBoolean2);
/*      */     } 
/* 8649 */     AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/*      */     
/* 8651 */     alterTableAlterColumn.setTableName(paramString1);
/* 8652 */     alterTableAlterColumn.setIfTableExists(paramBoolean1);
/* 8653 */     alterTableAlterColumn.setOldColumn(paramColumn);
/* 8654 */     NullConstraintType nullConstraintType = parseNotNullConstraint();
/* 8655 */     switch (nullConstraintType) {
/*      */       case Derby:
/* 8657 */         alterTableAlterColumn.setType(9);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 8690 */         return (Prepared)alterTableAlterColumn;case HSQLDB: alterTableAlterColumn.setType(8); return (Prepared)alterTableAlterColumn;case MySQL: if (readIf(25)) { if (readIf(60)) { read(58); alterTableAlterColumn.setType(100); alterTableAlterColumn.setBooleanFlag(true); } else { Expression expression = readExpression(); alterTableAlterColumn.setType(10); alterTableAlterColumn.setDefaultExpression(expression); }  } else if (readIf(60)) { read("UPDATE"); Expression expression = readExpression(); alterTableAlterColumn.setType(90); alterTableAlterColumn.setDefaultExpression(expression); } else if (readIf("INVISIBLE")) { alterTableAlterColumn.setType(87); alterTableAlterColumn.setBooleanFlag(false); } else if (readIf("VISIBLE")) { alterTableAlterColumn.setType(87); alterTableAlterColumn.setBooleanFlag(true); }  return (Prepared)alterTableAlterColumn;
/*      */     } 
/*      */     throw DbException.get(90088, "Internal Error - unhandled case: " + nullConstraintType.name());
/*      */   } private Prepared parseAlterTableDrop(Schema paramSchema, String paramString, boolean paramBoolean) {
/* 8694 */     if (readIf(14)) {
/* 8695 */       boolean bool = readIfExists(false);
/* 8696 */       String str = readIdentifierWithSchema(paramSchema.getName());
/* 8697 */       bool = readIfExists(bool);
/* 8698 */       checkSchema(paramSchema);
/* 8699 */       AlterTableDropConstraint alterTableDropConstraint = new AlterTableDropConstraint(this.session, getSchema(), bool);
/* 8700 */       alterTableDropConstraint.setTableName(paramString);
/* 8701 */       alterTableDropConstraint.setIfTableExists(paramBoolean);
/* 8702 */       alterTableDropConstraint.setConstraintName(str);
/* 8703 */       ConstraintActionType constraintActionType = parseCascadeOrRestrict();
/* 8704 */       if (constraintActionType != null) {
/* 8705 */         alterTableDropConstraint.setDropAction(constraintActionType);
/*      */       }
/* 8707 */       return (Prepared)alterTableDropConstraint;
/* 8708 */     }  if (readIf(63)) {
/* 8709 */       read(47);
/* 8710 */       Table table1 = tableIfTableExists(paramSchema, paramString, paramBoolean);
/* 8711 */       if (table1 == null) {
/* 8712 */         return (Prepared)new NoOperation(this.session);
/*      */       }
/* 8714 */       Index index = table1.getPrimaryKey();
/* 8715 */       DropIndex dropIndex = new DropIndex(this.session, paramSchema);
/* 8716 */       dropIndex.setIndexName(index.getName());
/* 8717 */       return (Prepared)dropIndex;
/* 8718 */     }  if ((this.database.getMode()).alterTableExtensionsMySQL) {
/* 8719 */       Prepared prepared = parseAlterTableDropCompatibility(paramSchema, paramString, paramBoolean);
/* 8720 */       if (prepared != null) {
/* 8721 */         return prepared;
/*      */       }
/*      */     } 
/* 8724 */     readIf("COLUMN");
/* 8725 */     boolean bool1 = readIfExists(false);
/* 8726 */     ArrayList<Column> arrayList = new ArrayList();
/* 8727 */     Table table = tableIfTableExists(paramSchema, paramString, paramBoolean);
/*      */     
/* 8729 */     boolean bool2 = readIf(105);
/*      */     while (true) {
/* 8731 */       String str = readIdentifier();
/* 8732 */       if (table != null) {
/* 8733 */         Column column = table.getColumn(str, bool1);
/* 8734 */         if (column != null) {
/* 8735 */           arrayList.add(column);
/*      */         }
/*      */       } 
/* 8738 */       if (!readIf(109)) {
/* 8739 */         if (bool2)
/*      */         {
/* 8741 */           read(106);
/*      */         }
/* 8743 */         if (table == null || arrayList.isEmpty()) {
/* 8744 */           return (Prepared)new NoOperation(this.session);
/*      */         }
/* 8746 */         AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/* 8747 */         alterTableAlterColumn.setType(12);
/* 8748 */         alterTableAlterColumn.setTableName(paramString);
/* 8749 */         alterTableAlterColumn.setIfTableExists(paramBoolean);
/* 8750 */         alterTableAlterColumn.setColumnsToRemove(arrayList);
/* 8751 */         return (Prepared)alterTableAlterColumn;
/*      */       } 
/*      */     } 
/*      */   } private Prepared parseAlterTableDropCompatibility(Schema paramSchema, String paramString, boolean paramBoolean) {
/* 8755 */     if (readIf(34)) {
/* 8756 */       read(47);
/*      */       
/* 8758 */       boolean bool = readIfExists(false);
/* 8759 */       String str = readIdentifierWithSchema(paramSchema.getName());
/* 8760 */       checkSchema(paramSchema);
/* 8761 */       AlterTableDropConstraint alterTableDropConstraint = new AlterTableDropConstraint(this.session, getSchema(), bool);
/* 8762 */       alterTableDropConstraint.setTableName(paramString);
/* 8763 */       alterTableDropConstraint.setIfTableExists(paramBoolean);
/* 8764 */       alterTableDropConstraint.setConstraintName(str);
/* 8765 */       return (Prepared)alterTableDropConstraint;
/* 8766 */     }  if (readIf("INDEX")) {
/*      */       
/* 8768 */       boolean bool = readIfExists(false);
/* 8769 */       String str = readIdentifierWithSchema(paramSchema.getName());
/* 8770 */       if (paramSchema.findIndex(this.session, str) != null) {
/* 8771 */         DropIndex dropIndex = new DropIndex(this.session, getSchema());
/* 8772 */         dropIndex.setIndexName(str);
/* 8773 */         return commandIfTableExists(paramSchema, paramString, paramBoolean, (Prepared)dropIndex);
/*      */       } 
/* 8775 */       AlterTableDropConstraint alterTableDropConstraint = new AlterTableDropConstraint(this.session, getSchema(), bool);
/* 8776 */       alterTableDropConstraint.setTableName(paramString);
/* 8777 */       alterTableDropConstraint.setIfTableExists(paramBoolean);
/* 8778 */       alterTableDropConstraint.setConstraintName(str);
/* 8779 */       return (Prepared)alterTableDropConstraint;
/*      */     } 
/*      */     
/* 8782 */     return null;
/*      */   }
/*      */   
/*      */   private Prepared parseAlterTableRename(Schema paramSchema, String paramString, boolean paramBoolean) {
/* 8786 */     if (readIf("COLUMN")) {
/*      */       
/* 8788 */       String str1 = readIdentifier();
/* 8789 */       read(76);
/* 8790 */       AlterTableRenameColumn alterTableRenameColumn = new AlterTableRenameColumn(this.session, paramSchema);
/*      */       
/* 8792 */       alterTableRenameColumn.setTableName(paramString);
/* 8793 */       alterTableRenameColumn.setIfTableExists(paramBoolean);
/* 8794 */       alterTableRenameColumn.setOldColumnName(str1);
/* 8795 */       alterTableRenameColumn.setNewColumnName(readIdentifier());
/* 8796 */       return (Prepared)alterTableRenameColumn;
/* 8797 */     }  if (readIf(14)) {
/* 8798 */       String str1 = readIdentifierWithSchema(paramSchema.getName());
/* 8799 */       checkSchema(paramSchema);
/* 8800 */       read(76);
/* 8801 */       AlterTableRenameConstraint alterTableRenameConstraint = new AlterTableRenameConstraint(this.session, paramSchema);
/* 8802 */       alterTableRenameConstraint.setTableName(paramString);
/* 8803 */       alterTableRenameConstraint.setIfTableExists(paramBoolean);
/* 8804 */       alterTableRenameConstraint.setConstraintName(str1);
/* 8805 */       alterTableRenameConstraint.setNewConstraintName(readIdentifier());
/* 8806 */       return (Prepared)alterTableRenameConstraint;
/*      */     } 
/* 8808 */     read(76);
/* 8809 */     String str = readIdentifierWithSchema(paramSchema.getName());
/* 8810 */     checkSchema(paramSchema);
/*      */     
/* 8812 */     AlterTableRename alterTableRename = new AlterTableRename(this.session, getSchema());
/* 8813 */     alterTableRename.setTableName(paramString);
/* 8814 */     alterTableRename.setNewTableName(str);
/* 8815 */     alterTableRename.setIfTableExists(paramBoolean);
/* 8816 */     alterTableRename.setHidden(readIf("HIDDEN"));
/* 8817 */     return (Prepared)alterTableRename;
/*      */   }
/*      */ 
/*      */   
/*      */   private Prepared parseAlterTableSet(Schema paramSchema, String paramString, boolean paramBoolean) {
/* 8822 */     read("REFERENTIAL_INTEGRITY");
/* 8823 */     byte b = 55;
/* 8824 */     boolean bool = readBooleanSetting();
/* 8825 */     AlterTableSet alterTableSet = new AlterTableSet(this.session, paramSchema, b, bool);
/*      */     
/* 8827 */     alterTableSet.setTableName(paramString);
/* 8828 */     alterTableSet.setIfTableExists(paramBoolean);
/* 8829 */     if (readIf(13)) {
/* 8830 */       alterTableSet.setCheckExisting(true);
/* 8831 */     } else if (readIf("NOCHECK")) {
/* 8832 */       alterTableSet.setCheckExisting(false);
/*      */     } 
/* 8834 */     return (Prepared)alterTableSet;
/*      */   }
/*      */   
/*      */   private Prepared parseAlterTableCompatibility(Schema paramSchema, String paramString, boolean paramBoolean, Mode paramMode) {
/* 8838 */     if (paramMode.alterTableExtensionsMySQL) {
/* 8839 */       if (readIf("AUTO_INCREMENT")) {
/* 8840 */         readIf(95);
/* 8841 */         Expression expression = readExpression();
/* 8842 */         Table table = tableIfTableExists(paramSchema, paramString, paramBoolean);
/* 8843 */         if (table == null) {
/* 8844 */           return (Prepared)new NoOperation(this.session);
/*      */         }
/* 8846 */         Index index = table.findPrimaryKey();
/* 8847 */         if (index != null) {
/* 8848 */           for (IndexColumn indexColumn : index.getIndexColumns()) {
/* 8849 */             Column column = indexColumn.column;
/* 8850 */             if (column.isIdentity()) {
/* 8851 */               AlterSequence alterSequence = new AlterSequence(this.session, paramSchema);
/* 8852 */               alterSequence.setColumn(column, null);
/* 8853 */               SequenceOptions sequenceOptions = new SequenceOptions();
/* 8854 */               sequenceOptions.setRestartValue(expression);
/* 8855 */               alterSequence.setOptions(sequenceOptions);
/* 8856 */               return (Prepared)alterSequence;
/*      */             } 
/*      */           } 
/*      */         }
/* 8860 */         throw DbException.get(42122, "AUTO_INCREMENT PRIMARY KEY");
/* 8861 */       }  if (readIf("CHANGE")) {
/* 8862 */         readIf("COLUMN");
/* 8863 */         String str1 = readIdentifier();
/* 8864 */         String str2 = readIdentifier();
/* 8865 */         Column column = columnIfTableExists(paramSchema, paramString, str1, paramBoolean, false);
/* 8866 */         boolean bool = (column == null) ? true : column.isNullable();
/*      */ 
/*      */         
/* 8869 */         parseColumnForTable(str2, bool);
/* 8870 */         AlterTableRenameColumn alterTableRenameColumn = new AlterTableRenameColumn(this.session, paramSchema);
/* 8871 */         alterTableRenameColumn.setTableName(paramString);
/* 8872 */         alterTableRenameColumn.setIfTableExists(paramBoolean);
/* 8873 */         alterTableRenameColumn.setOldColumnName(str1);
/* 8874 */         alterTableRenameColumn.setNewColumnName(str2);
/* 8875 */         return (Prepared)alterTableRenameColumn;
/* 8876 */       }  if (readIf("CONVERT")) {
/* 8877 */         readIf(76);
/* 8878 */         readIf("CHARACTER");
/* 8879 */         readIf(71);
/* 8880 */         readMySQLCharset();
/*      */         
/* 8882 */         if (readIf("COLLATE")) {
/* 8883 */           readMySQLCharset();
/*      */         }
/*      */         
/* 8886 */         return (Prepared)new NoOperation(this.session);
/*      */       } 
/*      */     } 
/* 8889 */     if (paramMode.alterTableModifyColumn && readIf("MODIFY")) {
/*      */       AlterTableAlterColumn alterTableAlterColumn; Column column;
/* 8891 */       readIf("COLUMN");
/*      */       
/* 8893 */       boolean bool = readIf(105);
/* 8894 */       String str = readIdentifier();
/*      */       
/* 8896 */       NullConstraintType nullConstraintType = parseNotNullConstraint();
/* 8897 */       switch (nullConstraintType) {
/*      */         case Derby:
/*      */         case HSQLDB:
/* 8900 */           alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/* 8901 */           alterTableAlterColumn.setTableName(paramString);
/* 8902 */           alterTableAlterColumn.setIfTableExists(paramBoolean);
/* 8903 */           column = columnIfTableExists(paramSchema, paramString, str, paramBoolean, false);
/* 8904 */           alterTableAlterColumn.setOldColumn(column);
/* 8905 */           if (nullConstraintType == NullConstraintType.NULL_IS_ALLOWED) {
/* 8906 */             alterTableAlterColumn.setType(9); break;
/*      */           } 
/* 8908 */           alterTableAlterColumn.setType(8);
/*      */           break;
/*      */         
/*      */         case MySQL:
/* 8912 */           alterTableAlterColumn = parseAlterTableAlterColumnType(paramSchema, paramString, str, paramBoolean, false, 
/* 8913 */               (paramMode.getEnum() != Mode.ModeEnum.MySQL));
/*      */           break;
/*      */         default:
/* 8916 */           throw DbException.get(90088, "Internal Error - unhandled case: " + nullConstraintType
/* 8917 */               .name());
/*      */       } 
/* 8919 */       if (bool) {
/* 8920 */         read(106);
/*      */       }
/* 8922 */       return (Prepared)alterTableAlterColumn;
/*      */     } 
/* 8924 */     throw getSyntaxError();
/*      */   }
/*      */   
/*      */   private Table tableIfTableExists(Schema paramSchema, String paramString, boolean paramBoolean) {
/* 8928 */     Table table = paramSchema.resolveTableOrView(this.session, paramString);
/* 8929 */     if (table == null && !paramBoolean) {
/* 8930 */       throw getTableOrViewNotFoundDbException(paramSchema.getName(), paramString);
/*      */     }
/* 8932 */     return table;
/*      */   }
/*      */ 
/*      */   
/*      */   private Column columnIfTableExists(Schema paramSchema, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) {
/* 8937 */     Table table = tableIfTableExists(paramSchema, paramString1, paramBoolean1);
/* 8938 */     if (table == null) {
/* 8939 */       return null;
/*      */     }
/* 8941 */     return table.getColumn(paramString2, paramBoolean2);
/*      */   }
/*      */ 
/*      */   
/*      */   private Prepared commandIfTableExists(Schema paramSchema, String paramString, boolean paramBoolean, Prepared paramPrepared) {
/* 8946 */     return (tableIfTableExists(paramSchema, paramString, paramBoolean) == null) ? (Prepared)new NoOperation(this.session) : paramPrepared;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private AlterTableAlterColumn parseAlterTableAlterColumnType(Schema paramSchema, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
/* 8953 */     Column column1 = columnIfTableExists(paramSchema, paramString1, paramString2, paramBoolean1, paramBoolean2);
/* 8954 */     Column column2 = parseColumnForTable(paramString2, (!paramBoolean3 || column1 == null || column1
/* 8955 */         .isNullable()));
/* 8956 */     AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/* 8957 */     parseAlterColumnUsingIf(alterTableAlterColumn);
/* 8958 */     alterTableAlterColumn.setTableName(paramString1);
/* 8959 */     alterTableAlterColumn.setIfTableExists(paramBoolean1);
/* 8960 */     alterTableAlterColumn.setType(11);
/* 8961 */     alterTableAlterColumn.setOldColumn(column1);
/* 8962 */     alterTableAlterColumn.setNewColumn(column2);
/* 8963 */     return alterTableAlterColumn;
/*      */   }
/*      */ 
/*      */   
/*      */   private AlterTableAlterColumn parseAlterTableAlterColumnDataType(Schema paramSchema, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) {
/* 8968 */     Column column1 = columnIfTableExists(paramSchema, paramString1, paramString2, paramBoolean1, paramBoolean2);
/* 8969 */     Column column2 = parseColumnWithType(paramString2);
/* 8970 */     if (column1 != null) {
/* 8971 */       if (!column1.isNullable()) {
/* 8972 */         column2.setNullable(false);
/*      */       }
/* 8974 */       if (!column1.getVisible()) {
/* 8975 */         column2.setVisible(false);
/*      */       }
/* 8977 */       Expression expression = column1.getDefaultExpression();
/* 8978 */       if (expression != null) {
/* 8979 */         if (column1.isGenerated()) {
/* 8980 */           column2.setGeneratedExpression(expression);
/*      */         } else {
/* 8982 */           column2.setDefaultExpression(this.session, expression);
/*      */         } 
/*      */       }
/* 8985 */       expression = column1.getOnUpdateExpression();
/* 8986 */       if (expression != null) {
/* 8987 */         column2.setOnUpdateExpression(this.session, expression);
/*      */       }
/* 8989 */       Sequence sequence = column1.getSequence();
/* 8990 */       if (sequence != null) {
/* 8991 */         column2.setIdentityOptions(new SequenceOptions(sequence, column2.getType()), column1
/* 8992 */             .isGeneratedAlways());
/*      */       }
/* 8994 */       String str = column1.getComment();
/* 8995 */       if (str != null) {
/* 8996 */         column2.setComment(str);
/*      */       }
/*      */     } 
/* 8999 */     AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/* 9000 */     parseAlterColumnUsingIf(alterTableAlterColumn);
/* 9001 */     alterTableAlterColumn.setTableName(paramString1);
/* 9002 */     alterTableAlterColumn.setIfTableExists(paramBoolean1);
/* 9003 */     alterTableAlterColumn.setType(11);
/* 9004 */     alterTableAlterColumn.setOldColumn(column1);
/* 9005 */     alterTableAlterColumn.setNewColumn(column2);
/* 9006 */     return alterTableAlterColumn;
/*      */   }
/*      */ 
/*      */   
/*      */   private AlterTableAlterColumn parseAlterTableAddColumn(String paramString, Schema paramSchema, boolean paramBoolean) {
/* 9011 */     readIf("COLUMN");
/* 9012 */     AlterTableAlterColumn alterTableAlterColumn = new AlterTableAlterColumn(this.session, paramSchema);
/*      */     
/* 9014 */     alterTableAlterColumn.setType(7);
/* 9015 */     alterTableAlterColumn.setTableName(paramString);
/* 9016 */     alterTableAlterColumn.setIfTableExists(paramBoolean);
/* 9017 */     if (readIf(105)) {
/* 9018 */       alterTableAlterColumn.setIfNotExists(false);
/*      */       do {
/* 9020 */         parseTableColumnDefinition((CommandWithColumns)alterTableAlterColumn, paramSchema, paramString, false);
/* 9021 */       } while (readIfMore());
/*      */     } else {
/* 9023 */       boolean bool = readIfNotExists();
/* 9024 */       alterTableAlterColumn.setIfNotExists(bool);
/* 9025 */       parseTableColumnDefinition((CommandWithColumns)alterTableAlterColumn, paramSchema, paramString, false);
/* 9026 */       parseAlterColumnUsingIf(alterTableAlterColumn);
/*      */     } 
/* 9028 */     if (readIf("BEFORE")) {
/* 9029 */       alterTableAlterColumn.setAddBefore(readIdentifier());
/* 9030 */     } else if (readIf("AFTER")) {
/* 9031 */       alterTableAlterColumn.setAddAfter(readIdentifier());
/* 9032 */     } else if (readIf("FIRST")) {
/* 9033 */       alterTableAlterColumn.setAddFirst();
/*      */     } 
/* 9035 */     return alterTableAlterColumn;
/*      */   }
/*      */   
/*      */   private void parseAlterColumnUsingIf(AlterTableAlterColumn paramAlterTableAlterColumn) {
/* 9039 */     if (readIf(83)) {
/* 9040 */       paramAlterTableAlterColumn.setUsingExpression(readExpression());
/*      */     }
/*      */   }
/*      */   
/*      */   private ConstraintActionType parseAction() {
/* 9045 */     ConstraintActionType constraintActionType = parseCascadeOrRestrict();
/* 9046 */     if (constraintActionType != null) {
/* 9047 */       return constraintActionType;
/*      */     }
/* 9049 */     if (readIf("NO")) {
/* 9050 */       read("ACTION");
/* 9051 */       return ConstraintActionType.RESTRICT;
/*      */     } 
/* 9053 */     read(71);
/* 9054 */     if (readIf(58)) {
/* 9055 */       return ConstraintActionType.SET_NULL;
/*      */     }
/* 9057 */     read(25);
/* 9058 */     return ConstraintActionType.SET_DEFAULT;
/*      */   }
/*      */   
/*      */   private ConstraintActionType parseCascadeOrRestrict() {
/* 9062 */     if (readIf("CASCADE"))
/* 9063 */       return ConstraintActionType.CASCADE; 
/* 9064 */     if (readIf("RESTRICT")) {
/* 9065 */       return ConstraintActionType.RESTRICT;
/*      */     }
/* 9067 */     return null;
/*      */   }
/*      */   private DefineCommand parseTableConstraintIf(String paramString, Schema paramSchema, boolean paramBoolean) {
/*      */     AlterTableAddConstraint alterTableAddConstraint;
/*      */     boolean bool2;
/* 9072 */     String str1 = null, str2 = null;
/* 9073 */     boolean bool1 = false;
/* 9074 */     if (readIf(14)) {
/* 9075 */       bool1 = readIfNotExists();
/* 9076 */       str1 = readIdentifierWithSchema(paramSchema.getName());
/* 9077 */       checkSchema(paramSchema);
/* 9078 */       str2 = readCommentIf();
/*      */     } 
/*      */     
/* 9081 */     switch (this.currentTokenType) {
/*      */       case 63:
/* 9083 */         read();
/* 9084 */         read(47);
/* 9085 */         alterTableAddConstraint = new AlterTableAddConstraint(this.session, paramSchema, 6, bool1);
/*      */         
/* 9087 */         if (readIf("HASH")) {
/* 9088 */           alterTableAddConstraint.setPrimaryKeyHash(true);
/*      */         }
/* 9090 */         read(105);
/* 9091 */         alterTableAddConstraint.setIndexColumns(parseIndexColumnList());
/* 9092 */         if (readIf("INDEX")) {
/* 9093 */           String str = readIdentifierWithSchema();
/* 9094 */           alterTableAddConstraint.setIndex(getSchema().findIndex(this.session, str));
/*      */         } 
/*      */         break;
/*      */       case 80:
/* 9098 */         read();
/*      */         
/* 9100 */         bool2 = (this.database.getMode()).indexDefinitionInCreateTable;
/* 9101 */         if (bool2) {
/* 9102 */           if (!readIf(47)) {
/* 9103 */             readIf("INDEX");
/*      */           }
/* 9105 */           if (!isToken(105)) {
/* 9106 */             str1 = readIdentifier();
/*      */           }
/*      */         } 
/* 9109 */         read(105);
/* 9110 */         alterTableAddConstraint = new AlterTableAddConstraint(this.session, paramSchema, 4, bool1);
/*      */         
/* 9112 */         if (readIf(84)) {
/* 9113 */           read(106);
/* 9114 */           alterTableAddConstraint.setIndexColumns(null);
/*      */         } else {
/* 9116 */           alterTableAddConstraint.setIndexColumns(parseIndexColumnList());
/*      */         } 
/* 9118 */         if (readIf("INDEX")) {
/* 9119 */           String str = readIdentifierWithSchema();
/* 9120 */           alterTableAddConstraint.setIndex(getSchema().findIndex(this.session, str));
/*      */         } 
/* 9122 */         if (bool2 && readIf(83)) {
/* 9123 */           read("BTREE");
/*      */         }
/*      */         break;
/*      */       case 34:
/* 9127 */         read();
/* 9128 */         alterTableAddConstraint = new AlterTableAddConstraint(this.session, paramSchema, 5, bool1);
/*      */         
/* 9130 */         read(47);
/* 9131 */         read(105);
/* 9132 */         alterTableAddConstraint.setIndexColumns(parseIndexColumnList());
/* 9133 */         if (readIf("INDEX")) {
/* 9134 */           String str = readIdentifierWithSchema();
/* 9135 */           alterTableAddConstraint.setIndex(paramSchema.findIndex(this.session, str));
/*      */         } 
/* 9137 */         read("REFERENCES");
/* 9138 */         parseReferences(alterTableAddConstraint, paramSchema, paramString);
/*      */         break;
/*      */       case 13:
/* 9141 */         read();
/* 9142 */         alterTableAddConstraint = new AlterTableAddConstraint(this.session, paramSchema, 3, bool1);
/*      */         
/* 9144 */         alterTableAddConstraint.setCheckExpression(readExpression());
/*      */         break;
/*      */       default:
/* 9147 */         if (str1 == null) {
/* 9148 */           Mode mode = this.database.getMode();
/* 9149 */           if (mode.indexDefinitionInCreateTable) {
/* 9150 */             int i = this.tokenIndex;
/* 9151 */             if (readIf(47) || readIf("INDEX")) {
/*      */ 
/*      */               
/* 9154 */               if (DataType.getTypeByName(this.currentToken, mode) == null) {
/* 9155 */                 CreateIndex createIndex = new CreateIndex(this.session, paramSchema);
/* 9156 */                 createIndex.setComment(str2);
/* 9157 */                 createIndex.setTableName(paramString);
/* 9158 */                 createIndex.setIfTableExists(paramBoolean);
/* 9159 */                 if (!readIf(105)) {
/* 9160 */                   createIndex.setIndexName(readIdentifier());
/* 9161 */                   read(105);
/*      */                 } 
/* 9163 */                 createIndex.setIndexColumns(parseIndexColumnList());
/*      */                 
/* 9165 */                 if (readIf(83)) {
/* 9166 */                   read("BTREE");
/*      */                 }
/* 9168 */                 return (DefineCommand)createIndex;
/*      */               } 
/*      */               
/* 9171 */               setTokenIndex(i);
/*      */             } 
/*      */           } 
/*      */           
/* 9175 */           return null;
/*      */         } 
/* 9177 */         if (this.expectedList != null) {
/* 9178 */           addMultipleExpected(new int[] { 63, 80, 34, 13 });
/*      */         }
/* 9180 */         throw getSyntaxError();
/*      */     } 
/*      */     
/* 9183 */     if (alterTableAddConstraint.getType() != 6) {
/* 9184 */       if (readIf("NOCHECK")) {
/* 9185 */         alterTableAddConstraint.setCheckExisting(false);
/*      */       } else {
/* 9187 */         readIf(13);
/* 9188 */         alterTableAddConstraint.setCheckExisting(true);
/*      */       } 
/*      */     }
/* 9191 */     alterTableAddConstraint.setTableName(paramString);
/* 9192 */     alterTableAddConstraint.setIfTableExists(paramBoolean);
/* 9193 */     alterTableAddConstraint.setConstraintName(str1);
/* 9194 */     alterTableAddConstraint.setComment(str2);
/* 9195 */     return (DefineCommand)alterTableAddConstraint;
/*      */   }
/*      */ 
/*      */   
/*      */   private void parseReferences(AlterTableAddConstraint paramAlterTableAddConstraint, Schema paramSchema, String paramString) {
/* 9200 */     if (readIf(105)) {
/* 9201 */       paramAlterTableAddConstraint.setRefTableName(paramSchema, paramString);
/* 9202 */       paramAlterTableAddConstraint.setRefIndexColumns(parseIndexColumnList());
/*      */     } else {
/* 9204 */       String str = readIdentifierWithSchema(paramSchema.getName());
/* 9205 */       paramAlterTableAddConstraint.setRefTableName(getSchema(), str);
/* 9206 */       if (readIf(105)) {
/* 9207 */         paramAlterTableAddConstraint.setRefIndexColumns(parseIndexColumnList());
/*      */       }
/*      */     } 
/* 9210 */     if (readIf("INDEX")) {
/* 9211 */       String str = readIdentifierWithSchema();
/* 9212 */       paramAlterTableAddConstraint.setRefIndex(getSchema().findIndex(this.session, str));
/*      */     } 
/* 9214 */     while (readIf(60)) {
/* 9215 */       if (readIf("DELETE")) {
/* 9216 */         paramAlterTableAddConstraint.setDeleteAction(parseAction()); continue;
/*      */       } 
/* 9218 */       read("UPDATE");
/* 9219 */       paramAlterTableAddConstraint.setUpdateAction(parseAction());
/*      */     } 
/*      */     
/* 9222 */     if (readIf(57)) {
/* 9223 */       read("DEFERRABLE");
/*      */     } else {
/* 9225 */       readIf("DEFERRABLE");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private CreateLinkedTable parseCreateLinkedTable(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
/* 9231 */     read(75);
/* 9232 */     boolean bool = readIfNotExists();
/* 9233 */     String str1 = readIdentifierWithSchema();
/* 9234 */     CreateLinkedTable createLinkedTable = new CreateLinkedTable(this.session, getSchema());
/* 9235 */     createLinkedTable.setTemporary(paramBoolean1);
/* 9236 */     createLinkedTable.setGlobalTemporary(paramBoolean2);
/* 9237 */     createLinkedTable.setForce(paramBoolean3);
/* 9238 */     createLinkedTable.setIfNotExists(bool);
/* 9239 */     createLinkedTable.setTableName(str1);
/* 9240 */     createLinkedTable.setComment(readCommentIf());
/* 9241 */     read(105);
/* 9242 */     createLinkedTable.setDriver(readString());
/* 9243 */     read(109);
/* 9244 */     createLinkedTable.setUrl(readString());
/* 9245 */     read(109);
/* 9246 */     createLinkedTable.setUser(readString());
/* 9247 */     read(109);
/* 9248 */     createLinkedTable.setPassword(readString());
/* 9249 */     read(109);
/* 9250 */     String str2 = readString();
/* 9251 */     if (readIf(109)) {
/* 9252 */       createLinkedTable.setOriginalSchema(str2);
/* 9253 */       str2 = readString();
/*      */     } 
/* 9255 */     createLinkedTable.setOriginalTable(str2);
/* 9256 */     read(106);
/* 9257 */     if (readIf("EMIT")) {
/* 9258 */       read("UPDATES");
/* 9259 */       createLinkedTable.setEmitUpdates(true);
/* 9260 */     } else if (readIf("READONLY")) {
/* 9261 */       createLinkedTable.setReadOnly(true);
/*      */     } 
/* 9263 */     if (readIf("FETCH_SIZE")) {
/* 9264 */       createLinkedTable.setFetchSize(readNonNegativeInt());
/*      */     }
/* 9266 */     if (readIf("AUTOCOMMIT")) {
/* 9267 */       if (readIf("ON")) {
/* 9268 */         createLinkedTable.setAutoCommit(true);
/*      */       }
/* 9270 */       else if (readIf("OFF")) {
/* 9271 */         createLinkedTable.setAutoCommit(false);
/*      */       } 
/*      */     }
/* 9274 */     return createLinkedTable;
/*      */   }
/*      */ 
/*      */   
/*      */   private CreateTable parseCreateTable(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
/* 9279 */     boolean bool = readIfNotExists();
/* 9280 */     String str = readIdentifierWithSchema();
/* 9281 */     if (paramBoolean1 && paramBoolean2 && equalsToken("SESSION", this.schemaName)) {
/*      */ 
/*      */       
/* 9284 */       this.schemaName = this.session.getCurrentSchemaName();
/* 9285 */       paramBoolean2 = false;
/*      */     } 
/* 9287 */     Schema schema = getSchema();
/* 9288 */     CreateTable createTable = new CreateTable(this.session, schema);
/* 9289 */     createTable.setPersistIndexes(paramBoolean3);
/* 9290 */     createTable.setTemporary(paramBoolean1);
/* 9291 */     createTable.setGlobalTemporary(paramBoolean2);
/* 9292 */     createTable.setIfNotExists(bool);
/* 9293 */     createTable.setTableName(str);
/* 9294 */     createTable.setComment(readCommentIf());
/* 9295 */     if (readIf(105) && 
/* 9296 */       !readIf(106)) {
/*      */       do {
/* 9298 */         parseTableColumnDefinition((CommandWithColumns)createTable, schema, str, true);
/* 9299 */       } while (readIfMore());
/*      */     }
/*      */     
/* 9302 */     if (this.database.getMode().getEnum() == Mode.ModeEnum.MySQL) {
/* 9303 */       parseCreateTableMySQLTableOptions(createTable);
/*      */     }
/* 9305 */     if (readIf("ENGINE")) {
/* 9306 */       createTable.setTableEngine(readIdentifier());
/*      */     }
/* 9308 */     if (readIf(89)) {
/* 9309 */       createTable.setTableEngineParams(readTableEngineParams());
/*      */     }
/* 9311 */     if (paramBoolean1) {
/* 9312 */       if (readIf(60)) {
/* 9313 */         read("COMMIT");
/* 9314 */         if (readIf("DROP")) {
/* 9315 */           createTable.setOnCommitDrop();
/* 9316 */         } else if (readIf("DELETE")) {
/* 9317 */           read("ROWS");
/* 9318 */           createTable.setOnCommitTruncate();
/*      */         } 
/* 9320 */       } else if (readIf(57)) {
/* 9321 */         if (readIf("PERSISTENT")) {
/* 9322 */           createTable.setPersistData(false);
/*      */         } else {
/* 9324 */           read("LOGGED");
/*      */         } 
/*      */       } 
/* 9327 */       if (readIf("TRANSACTIONAL")) {
/* 9328 */         createTable.setTransactional(true);
/*      */       }
/* 9330 */     } else if (!paramBoolean3 && readIf(57)) {
/* 9331 */       read("PERSISTENT");
/* 9332 */       createTable.setPersistData(false);
/*      */     } 
/* 9334 */     if (readIf("HIDDEN")) {
/* 9335 */       createTable.setHidden(true);
/*      */     }
/* 9337 */     if (readIf(7)) {
/* 9338 */       readIf("SORTED");
/* 9339 */       createTable.setQuery(parseQuery());
/* 9340 */       if (readIf(89)) {
/* 9341 */         createTable.setWithNoData(readIf("NO"));
/* 9342 */         read("DATA");
/*      */       } 
/*      */     } 
/* 9345 */     return createTable;
/*      */   }
/*      */ 
/*      */   
/*      */   private void parseTableColumnDefinition(CommandWithColumns paramCommandWithColumns, Schema paramSchema, String paramString, boolean paramBoolean) {
/* 9350 */     DefineCommand defineCommand = parseTableConstraintIf(paramString, paramSchema, false);
/* 9351 */     if (defineCommand != null) {
/* 9352 */       paramCommandWithColumns.addConstraintCommand(defineCommand);
/*      */       return;
/*      */     } 
/* 9355 */     String str = readIdentifier();
/* 9356 */     if (paramBoolean && (this.currentTokenType == 109 || this.currentTokenType == 106)) {
/* 9357 */       paramCommandWithColumns.addColumn(new Column(str, TypeInfo.TYPE_UNKNOWN));
/*      */       return;
/*      */     } 
/* 9360 */     Column column = parseColumnForTable(str, true);
/* 9361 */     if (column.hasIdentityOptions() && column.isPrimaryKey()) {
/* 9362 */       paramCommandWithColumns.addConstraintCommand((DefineCommand)newPrimaryKeyConstraintCommand(this.session, paramSchema, paramString, column));
/*      */     }
/* 9364 */     paramCommandWithColumns.addColumn(column);
/* 9365 */     readColumnConstraints(paramCommandWithColumns, paramSchema, paramString, column);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AlterTableAddConstraint newPrimaryKeyConstraintCommand(SessionLocal paramSessionLocal, Schema paramSchema, String paramString, Column paramColumn) {
/* 9379 */     paramColumn.setPrimaryKey(false);
/* 9380 */     AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(paramSessionLocal, paramSchema, 6, false);
/*      */     
/* 9382 */     alterTableAddConstraint.setTableName(paramString);
/* 9383 */     alterTableAddConstraint.setIndexColumns(new IndexColumn[] { new IndexColumn(paramColumn.getName()) });
/* 9384 */     return alterTableAddConstraint;
/*      */   }
/*      */   
/*      */   private void readColumnConstraints(CommandWithColumns paramCommandWithColumns, Schema paramSchema, String paramString, Column paramColumn) {
/* 9388 */     String str = paramColumn.getComment();
/* 9389 */     boolean bool1 = false, bool2 = false;
/*      */     
/* 9391 */     Mode mode = this.database.getMode();
/*      */     while (true) {
/*      */       String str1;
/* 9394 */       if (readIf(14))
/* 9395 */       { str1 = readIdentifier(); }
/* 9396 */       else { if (str == null && (str = readCommentIf()) != null) {
/*      */           
/* 9398 */           paramColumn.setComment(str);
/*      */           continue;
/*      */         } 
/* 9401 */         str1 = null; }
/*      */       
/* 9403 */       if (!bool1 && readIf(63)) {
/* 9404 */         read(47);
/* 9405 */         bool1 = true;
/* 9406 */         boolean bool = readIf("HASH");
/* 9407 */         AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(this.session, paramSchema, 6, false);
/*      */         
/* 9409 */         alterTableAddConstraint.setConstraintName(str1);
/* 9410 */         alterTableAddConstraint.setPrimaryKeyHash(bool);
/* 9411 */         alterTableAddConstraint.setTableName(paramString);
/* 9412 */         alterTableAddConstraint.setIndexColumns(new IndexColumn[] { new IndexColumn(paramColumn.getName()) });
/* 9413 */         paramCommandWithColumns.addConstraintCommand((DefineCommand)alterTableAddConstraint); continue;
/* 9414 */       }  if (readIf(80)) {
/* 9415 */         AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(this.session, paramSchema, 4, false);
/*      */         
/* 9417 */         alterTableAddConstraint.setConstraintName(str1);
/* 9418 */         alterTableAddConstraint.setIndexColumns(new IndexColumn[] { new IndexColumn(paramColumn.getName()) });
/* 9419 */         alterTableAddConstraint.setTableName(paramString);
/* 9420 */         paramCommandWithColumns.addConstraintCommand((DefineCommand)alterTableAddConstraint); continue;
/* 9421 */       }  NullConstraintType nullConstraintType; if (!bool2 && (
/* 9422 */         nullConstraintType = parseNotNullConstraint()) != NullConstraintType.NO_NULL_CONSTRAINT_FOUND) {
/* 9423 */         bool2 = true;
/* 9424 */         if (nullConstraintType == NullConstraintType.NULL_IS_NOT_ALLOWED) {
/* 9425 */           paramColumn.setNullable(false); continue;
/* 9426 */         }  if (nullConstraintType == NullConstraintType.NULL_IS_ALLOWED) {
/* 9427 */           if (paramColumn.isIdentity()) {
/* 9428 */             throw DbException.get(90023, paramColumn.getName());
/*      */           }
/* 9430 */           paramColumn.setNullable(true);
/*      */         }  continue;
/* 9432 */       }  if (readIf(13)) {
/* 9433 */         AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(this.session, paramSchema, 3, false);
/*      */         
/* 9435 */         alterTableAddConstraint.setConstraintName(str1);
/* 9436 */         alterTableAddConstraint.setTableName(paramString);
/* 9437 */         alterTableAddConstraint.setCheckExpression(readExpression());
/* 9438 */         paramCommandWithColumns.addConstraintCommand((DefineCommand)alterTableAddConstraint); continue;
/* 9439 */       }  if (readIf("REFERENCES")) {
/* 9440 */         AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(this.session, paramSchema, 5, false);
/*      */         
/* 9442 */         alterTableAddConstraint.setConstraintName(str1);
/* 9443 */         alterTableAddConstraint.setIndexColumns(new IndexColumn[] { new IndexColumn(paramColumn.getName()) });
/* 9444 */         alterTableAddConstraint.setTableName(paramString);
/* 9445 */         parseReferences(alterTableAddConstraint, paramSchema, paramString);
/* 9446 */         paramCommandWithColumns.addConstraintCommand((DefineCommand)alterTableAddConstraint); continue;
/* 9447 */       }  if (str1 == null) {
/* 9448 */         if (paramColumn.getIdentityOptions() != null || !parseCompatibilityIdentity(paramColumn, mode))
/*      */           return;  continue;
/*      */       }  break;
/*      */     } 
/* 9452 */     throw getSyntaxError();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean parseCompatibilityIdentity(Column paramColumn, Mode paramMode) {
/* 9458 */     if (paramMode.autoIncrementClause && readIf("AUTO_INCREMENT")) {
/* 9459 */       parseCompatibilityIdentityOptions(paramColumn);
/* 9460 */       return true;
/*      */     } 
/* 9462 */     if (paramMode.identityClause && readIf("IDENTITY")) {
/* 9463 */       parseCompatibilityIdentityOptions(paramColumn);
/* 9464 */       return true;
/*      */     } 
/* 9466 */     return false;
/*      */   }
/*      */   
/*      */   private void parseCreateTableMySQLTableOptions(CreateTable paramCreateTable) {
/* 9470 */     boolean bool = false;
/*      */     label53: while (true) {
/* 9472 */       if (readIf("AUTO_INCREMENT")) {
/* 9473 */         readIf(95);
/* 9474 */         Expression expression = readExpression();
/*      */         
/* 9476 */         AlterTableAddConstraint alterTableAddConstraint = paramCreateTable.getPrimaryKey();
/* 9477 */         if (alterTableAddConstraint != null) {
/* 9478 */           for (IndexColumn indexColumn : alterTableAddConstraint.getIndexColumns()) {
/* 9479 */             String str = indexColumn.columnName;
/* 9480 */             for (Column column : paramCreateTable.getColumns()) {
/* 9481 */               if (this.database.equalsIdentifiers(column.getName(), str)) {
/* 9482 */                 SequenceOptions sequenceOptions = column.getIdentityOptions();
/* 9483 */                 if (sequenceOptions != null) {
/* 9484 */                   sequenceOptions.setStartValue(expression);
/*      */                   continue label53;
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         }
/* 9491 */         throw DbException.get(42122, "AUTO_INCREMENT PRIMARY KEY");
/*      */       } 
/* 9493 */       if (readIf(25))
/* 9494 */       { if (readIf("CHARACTER")) {
/* 9495 */           read(71);
/*      */         } else {
/* 9497 */           readIf("CHARSET");
/* 9498 */           readIf("COLLATE");
/*      */         } 
/* 9500 */         readMySQLCharset(); }
/* 9501 */       else if (readIf("CHARACTER"))
/* 9502 */       { read(71);
/* 9503 */         readMySQLCharset(); }
/* 9504 */       else if (readIf("COLLATE"))
/* 9505 */       { readMySQLCharset(); }
/* 9506 */       else if (readIf("CHARSET"))
/* 9507 */       { readMySQLCharset(); }
/* 9508 */       else if (readIf("COMMENT"))
/* 9509 */       { readIf(95);
/* 9510 */         paramCreateTable.setComment(readString()); }
/* 9511 */       else if (readIf("ENGINE"))
/* 9512 */       { readIf(95);
/* 9513 */         readIdentifier(); }
/* 9514 */       else if (readIf("ROW_FORMAT"))
/* 9515 */       { readIf(95);
/* 9516 */         readIdentifier(); }
/* 9517 */       else { if (bool) {
/* 9518 */           throw getSyntaxError();
/*      */         }
/*      */         break; }
/*      */       
/* 9522 */       bool = readIf(109);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void readMySQLCharset() {
/* 9527 */     readIf(95);
/* 9528 */     readIdentifier();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private enum NullConstraintType
/*      */   {
/* 9535 */     NULL_IS_ALLOWED, NULL_IS_NOT_ALLOWED, NO_NULL_CONSTRAINT_FOUND;
/*      */   }
/*      */   
/*      */   private NullConstraintType parseNotNullConstraint(NullConstraintType paramNullConstraintType) {
/* 9539 */     if (paramNullConstraintType == NullConstraintType.NO_NULL_CONSTRAINT_FOUND) {
/* 9540 */       paramNullConstraintType = parseNotNullConstraint();
/*      */     }
/* 9542 */     return paramNullConstraintType;
/*      */   }
/*      */   
/*      */   private NullConstraintType parseNotNullConstraint() {
/*      */     NullConstraintType nullConstraintType;
/* 9547 */     if (readIf(57)) {
/* 9548 */       read(58);
/* 9549 */       nullConstraintType = NullConstraintType.NULL_IS_NOT_ALLOWED;
/* 9550 */     } else if (readIf(58)) {
/* 9551 */       nullConstraintType = NullConstraintType.NULL_IS_ALLOWED;
/*      */     } else {
/* 9553 */       return NullConstraintType.NO_NULL_CONSTRAINT_FOUND;
/*      */     } 
/* 9555 */     if (this.database.getMode().getEnum() == Mode.ModeEnum.Oracle) {
/* 9556 */       nullConstraintType = parseNotNullCompatibility(nullConstraintType);
/*      */     }
/* 9558 */     return nullConstraintType;
/*      */   }
/*      */   
/*      */   private NullConstraintType parseNotNullCompatibility(NullConstraintType paramNullConstraintType) {
/* 9562 */     if (readIf("ENABLE")) {
/* 9563 */       if (!readIf("VALIDATE") && readIf("NOVALIDATE"))
/*      */       {
/* 9565 */         paramNullConstraintType = NullConstraintType.NULL_IS_ALLOWED;
/*      */       }
/* 9567 */     } else if (readIf("DISABLE")) {
/*      */       
/* 9569 */       paramNullConstraintType = NullConstraintType.NULL_IS_ALLOWED;
/* 9570 */       if (!readIf("VALIDATE")) {
/* 9571 */         readIf("NOVALIDATE");
/*      */       }
/*      */     } 
/* 9574 */     return paramNullConstraintType;
/*      */   }
/*      */   
/*      */   private CreateSynonym parseCreateSynonym(boolean paramBoolean) {
/* 9578 */     boolean bool = readIfNotExists();
/* 9579 */     String str1 = readIdentifierWithSchema();
/* 9580 */     Schema schema1 = getSchema();
/* 9581 */     read(33);
/* 9582 */     String str2 = readIdentifierWithSchema();
/*      */     
/* 9584 */     Schema schema2 = getSchema();
/* 9585 */     CreateSynonym createSynonym = new CreateSynonym(this.session, schema1);
/* 9586 */     createSynonym.setName(str1);
/* 9587 */     createSynonym.setSynonymFor(str2);
/* 9588 */     createSynonym.setSynonymForSchema(schema2);
/* 9589 */     createSynonym.setComment(readCommentIf());
/* 9590 */     createSynonym.setIfNotExists(bool);
/* 9591 */     createSynonym.setOrReplace(paramBoolean);
/* 9592 */     return createSynonym;
/*      */   }
/*      */   
/*      */   private static int getCompareType(int paramInt) {
/* 9596 */     switch (paramInt) {
/*      */       case 95:
/* 9598 */         return 0;
/*      */       case 96:
/* 9600 */         return 5;
/*      */       case 97:
/* 9602 */         return 3;
/*      */       case 98:
/* 9604 */         return 2;
/*      */       case 99:
/* 9606 */         return 4;
/*      */       case 100:
/* 9608 */         return 1;
/*      */       case 107:
/* 9610 */         return 8;
/*      */     } 
/* 9612 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quoteIdentifier(String paramString, int paramInt) {
/* 9624 */     if (paramString == null) {
/* 9625 */       return "\"\"";
/*      */     }
/* 9627 */     if ((paramInt & 0x1) != 0 && ParserUtil.isSimpleIdentifier(paramString, false, false)) {
/* 9628 */       return paramString;
/*      */     }
/* 9630 */     return StringUtils.quoteIdentifier(paramString);
/*      */   }
/*      */   
/*      */   public void setLiteralsChecked(boolean paramBoolean) {
/* 9634 */     this.literalsChecked = paramBoolean;
/*      */   }
/*      */   
/*      */   public void setRightsChecked(boolean paramBoolean) {
/* 9638 */     this.rightsChecked = paramBoolean;
/*      */   }
/*      */   
/*      */   public void setSuppliedParameters(ArrayList<Parameter> paramArrayList) {
/* 9642 */     this.suppliedParameters = paramArrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Expression parseExpression(String paramString) {
/* 9652 */     this.parameters = Utils.newSmallArrayList();
/* 9653 */     initialize(paramString, null, false);
/* 9654 */     read();
/* 9655 */     return readExpression();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Expression parseDomainConstraintExpression(String paramString) {
/* 9665 */     this.parameters = Utils.newSmallArrayList();
/* 9666 */     initialize(paramString, null, false);
/* 9667 */     read();
/*      */     try {
/* 9669 */       this.parseDomainConstraint = true;
/* 9670 */       return readExpression();
/*      */     } finally {
/* 9672 */       this.parseDomainConstraint = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Table parseTableName(String paramString) {
/* 9683 */     this.parameters = Utils.newSmallArrayList();
/* 9684 */     initialize(paramString, null, false);
/* 9685 */     read();
/* 9686 */     return readTableOrView();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object parseColumnList(String paramString, int paramInt) {
/* 9699 */     initialize(paramString, null, true); int i;
/* 9700 */     for (byte b = 0; b < i; b++) {
/* 9701 */       if (((Token)this.tokens.get(b)).start() >= paramInt) {
/* 9702 */         setTokenIndex(b);
/*      */         break;
/*      */       } 
/*      */     } 
/* 9706 */     read(105);
/* 9707 */     if (readIf(106)) {
/* 9708 */       return Utils.EMPTY_INT_ARRAY;
/*      */     }
/* 9710 */     if (isIdentifier()) {
/* 9711 */       ArrayList<String> arrayList = Utils.newSmallArrayList();
/*      */       while (true)
/* 9713 */       { if (!isIdentifier()) {
/* 9714 */           throw getSyntaxError();
/*      */         }
/* 9716 */         arrayList.add(this.currentToken);
/* 9717 */         read();
/* 9718 */         if (!readIfMore())
/* 9719 */           return arrayList.toArray(new String[0]);  } 
/* 9720 */     }  if (this.currentTokenType == 94) {
/* 9721 */       ArrayList<Integer> arrayList = Utils.newSmallArrayList();
/*      */       while (true) {
/* 9723 */         arrayList.add(Integer.valueOf(readInt()));
/* 9724 */         if (!readIfMore())
/* 9725 */         { i = arrayList.size();
/* 9726 */           int[] arrayOfInt = new int[i];
/* 9727 */           for (byte b1 = 0; b1 < i; b1++) {
/* 9728 */             arrayOfInt[b1] = ((Integer)arrayList.get(b1)).intValue();
/*      */           }
/* 9730 */           return arrayOfInt; } 
/*      */       } 
/* 9732 */     }  throw getSyntaxError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLastParseIndex() {
/* 9742 */     return this.token.start();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 9747 */     return StringUtils.addAsterisk(this.sqlCommand, this.token.start());
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */