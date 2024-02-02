package org.h2.command;

import java.nio.charset.Charset;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import org.h2.api.IntervalQualifier;
import org.h2.command.ddl.AlterDomainAddConstraint;
import org.h2.command.ddl.AlterDomainDropConstraint;
import org.h2.command.ddl.AlterDomainExpressions;
import org.h2.command.ddl.AlterDomainRename;
import org.h2.command.ddl.AlterDomainRenameConstraint;
import org.h2.command.ddl.AlterIndexRename;
import org.h2.command.ddl.AlterSchemaRename;
import org.h2.command.ddl.AlterSequence;
import org.h2.command.ddl.AlterTableAddConstraint;
import org.h2.command.ddl.AlterTableAlterColumn;
import org.h2.command.ddl.AlterTableDropConstraint;
import org.h2.command.ddl.AlterTableRename;
import org.h2.command.ddl.AlterTableRenameColumn;
import org.h2.command.ddl.AlterTableRenameConstraint;
import org.h2.command.ddl.AlterUser;
import org.h2.command.ddl.AlterView;
import org.h2.command.ddl.Analyze;
import org.h2.command.ddl.CommandWithColumns;
import org.h2.command.ddl.CreateAggregate;
import org.h2.command.ddl.CreateConstant;
import org.h2.command.ddl.CreateDomain;
import org.h2.command.ddl.CreateFunctionAlias;
import org.h2.command.ddl.CreateIndex;
import org.h2.command.ddl.CreateLinkedTable;
import org.h2.command.ddl.CreateRole;
import org.h2.command.ddl.CreateSchema;
import org.h2.command.ddl.CreateSequence;
import org.h2.command.ddl.CreateSynonym;
import org.h2.command.ddl.CreateTable;
import org.h2.command.ddl.CreateTrigger;
import org.h2.command.ddl.CreateUser;
import org.h2.command.ddl.CreateView;
import org.h2.command.ddl.DeallocateProcedure;
import org.h2.command.ddl.DefineCommand;
import org.h2.command.ddl.DropAggregate;
import org.h2.command.ddl.DropConstant;
import org.h2.command.ddl.DropDatabase;
import org.h2.command.ddl.DropDomain;
import org.h2.command.ddl.DropFunctionAlias;
import org.h2.command.ddl.DropIndex;
import org.h2.command.ddl.DropRole;
import org.h2.command.ddl.DropSchema;
import org.h2.command.ddl.DropSequence;
import org.h2.command.ddl.DropSynonym;
import org.h2.command.ddl.DropTable;
import org.h2.command.ddl.DropTrigger;
import org.h2.command.ddl.DropUser;
import org.h2.command.ddl.DropView;
import org.h2.command.ddl.GrantRevoke;
import org.h2.command.ddl.PrepareProcedure;
import org.h2.command.ddl.SequenceOptions;
import org.h2.command.ddl.SetComment;
import org.h2.command.ddl.TruncateTable;
import org.h2.command.dml.AlterTableSet;
import org.h2.command.dml.BackupCommand;
import org.h2.command.dml.Call;
import org.h2.command.dml.CommandWithValues;
import org.h2.command.dml.DataChangeStatement;
import org.h2.command.dml.Delete;
import org.h2.command.dml.ExecuteImmediate;
import org.h2.command.dml.ExecuteProcedure;
import org.h2.command.dml.Explain;
import org.h2.command.dml.Help;
import org.h2.command.dml.Insert;
import org.h2.command.dml.Merge;
import org.h2.command.dml.MergeUsing;
import org.h2.command.dml.NoOperation;
import org.h2.command.dml.RunScriptCommand;
import org.h2.command.dml.ScriptCommand;
import org.h2.command.dml.Set;
import org.h2.command.dml.SetClauseList;
import org.h2.command.dml.SetSessionCharacteristics;
import org.h2.command.dml.SetTypes;
import org.h2.command.dml.TransactionCommand;
import org.h2.command.dml.Update;
import org.h2.command.query.Query;
import org.h2.command.query.QueryOrderBy;
import org.h2.command.query.Select;
import org.h2.command.query.SelectUnion;
import org.h2.command.query.TableValueConstructor;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Database;
import org.h2.engine.DbSettings;
import org.h2.engine.IsolationLevel;
import org.h2.engine.Mode;
import org.h2.engine.Procedure;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Alias;
import org.h2.expression.ArrayConstructorByQuery;
import org.h2.expression.ArrayElementReference;
import org.h2.expression.BinaryOperation;
import org.h2.expression.ConcatenationOperation;
import org.h2.expression.DomainValueExpression;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionWithFlags;
import org.h2.expression.ExpressionWithVariableParameters;
import org.h2.expression.FieldReference;
import org.h2.expression.Format;
import org.h2.expression.Parameter;
import org.h2.expression.Rownum;
import org.h2.expression.SearchedCase;
import org.h2.expression.SequenceValue;
import org.h2.expression.SimpleCase;
import org.h2.expression.Subquery;
import org.h2.expression.TimeZoneOperation;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.UnaryOperation;
import org.h2.expression.ValueExpression;
import org.h2.expression.Variable;
import org.h2.expression.Wildcard;
import org.h2.expression.aggregate.AbstractAggregate;
import org.h2.expression.aggregate.Aggregate;
import org.h2.expression.aggregate.AggregateType;
import org.h2.expression.aggregate.JavaAggregate;
import org.h2.expression.aggregate.ListaggArguments;
import org.h2.expression.analysis.DataAnalysisOperation;
import org.h2.expression.analysis.Window;
import org.h2.expression.analysis.WindowFrame;
import org.h2.expression.analysis.WindowFrameBound;
import org.h2.expression.analysis.WindowFrameBoundType;
import org.h2.expression.analysis.WindowFrameExclusion;
import org.h2.expression.analysis.WindowFrameUnits;
import org.h2.expression.analysis.WindowFunction;
import org.h2.expression.analysis.WindowFunctionType;
import org.h2.expression.condition.BetweenPredicate;
import org.h2.expression.condition.BooleanTest;
import org.h2.expression.condition.CompareLike;
import org.h2.expression.condition.Comparison;
import org.h2.expression.condition.ConditionAndOr;
import org.h2.expression.condition.ConditionAndOrN;
import org.h2.expression.condition.ConditionIn;
import org.h2.expression.condition.ConditionInParameter;
import org.h2.expression.condition.ConditionInQuery;
import org.h2.expression.condition.ConditionLocalAndGlobal;
import org.h2.expression.condition.ConditionNot;
import org.h2.expression.condition.ExistsPredicate;
import org.h2.expression.condition.IsJsonPredicate;
import org.h2.expression.condition.NullPredicate;
import org.h2.expression.condition.TypePredicate;
import org.h2.expression.condition.UniquePredicate;
import org.h2.expression.function.ArrayFunction;
import org.h2.expression.function.BitFunction;
import org.h2.expression.function.BuiltinFunctions;
import org.h2.expression.function.CSVWriteFunction;
import org.h2.expression.function.CardinalityExpression;
import org.h2.expression.function.CastSpecification;
import org.h2.expression.function.CoalesceFunction;
import org.h2.expression.function.CompatibilitySequenceValueFunction;
import org.h2.expression.function.CompressFunction;
import org.h2.expression.function.ConcatFunction;
import org.h2.expression.function.CryptFunction;
import org.h2.expression.function.CurrentDateTimeValueFunction;
import org.h2.expression.function.CurrentGeneralValueSpecification;
import org.h2.expression.function.DBObjectFunction;
import org.h2.expression.function.DataTypeSQLFunction;
import org.h2.expression.function.DateTimeFormatFunction;
import org.h2.expression.function.DateTimeFunction;
import org.h2.expression.function.DayMonthNameFunction;
import org.h2.expression.function.FileFunction;
import org.h2.expression.function.HashFunction;
import org.h2.expression.function.JavaFunction;
import org.h2.expression.function.JsonConstructorFunction;
import org.h2.expression.function.LengthFunction;
import org.h2.expression.function.MathFunction;
import org.h2.expression.function.MathFunction1;
import org.h2.expression.function.MathFunction2;
import org.h2.expression.function.NullIfFunction;
import org.h2.expression.function.RandFunction;
import org.h2.expression.function.RegexpFunction;
import org.h2.expression.function.SessionControlFunction;
import org.h2.expression.function.SetFunction;
import org.h2.expression.function.SignalFunction;
import org.h2.expression.function.SoundexFunction;
import org.h2.expression.function.StringFunction;
import org.h2.expression.function.StringFunction1;
import org.h2.expression.function.StringFunction2;
import org.h2.expression.function.SubstringFunction;
import org.h2.expression.function.SysInfoFunction;
import org.h2.expression.function.TableInfoFunction;
import org.h2.expression.function.ToCharFunction;
import org.h2.expression.function.TrimFunction;
import org.h2.expression.function.TruncateValueFunction;
import org.h2.expression.function.XMLFunction;
import org.h2.expression.function.table.ArrayTableFunction;
import org.h2.expression.function.table.CSVReadFunction;
import org.h2.expression.function.table.JavaTableFunction;
import org.h2.expression.function.table.LinkSchemaFunction;
import org.h2.expression.function.table.TableFunction;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mode.FunctionsPostgreSQL;
import org.h2.mode.ModeFunction;
import org.h2.mode.OnDuplicateKeyValues;
import org.h2.mode.Regclass;
import org.h2.schema.Domain;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.schema.UserAggregate;
import org.h2.schema.UserDefinedFunction;
import org.h2.table.Column;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.DualTable;
import org.h2.table.FunctionTable;
import org.h2.table.IndexColumn;
import org.h2.table.IndexHints;
import org.h2.table.RangeTable;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.table.TableView;
import org.h2.util.IntervalUtils;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.util.geometry.EWKTUtils;
import org.h2.util.json.JSONItemType;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.ExtTypeInfoEnum;
import org.h2.value.ExtTypeInfoGeometry;
import org.h2.value.ExtTypeInfoNumeric;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDate;
import org.h2.value.ValueDouble;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueInteger;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueUuid;
import org.h2.value.ValueVarchar;

public class Parser {
   private static final String WITH_STATEMENT_SUPPORTS_LIMITED_SUB_STATEMENTS = "WITH statement supports only SELECT, TABLE, VALUES, CREATE TABLE, INSERT, UPDATE, MERGE or DELETE statements";
   private final Database database;
   private final SessionLocal session;
   private final boolean identifiersToLower;
   private final boolean identifiersToUpper;
   private final boolean variableBinary;
   private final BitSet nonKeywords;
   ArrayList<Token> tokens;
   int tokenIndex;
   Token token;
   private int currentTokenType;
   private String currentToken;
   private String sqlCommand;
   private CreateView createView;
   private Prepared currentPrepared;
   private Select currentSelect;
   private List<TableView> cteCleanups;
   private ArrayList<Parameter> parameters;
   private ArrayList<Parameter> suppliedParameters;
   private String schemaName;
   private ArrayList<String> expectedList;
   private boolean rightsChecked;
   private boolean recompileAlways;
   private boolean literalsChecked;
   private int orderInFrom;
   private boolean parseDomainConstraint;

   public static BitSet parseNonKeywords(String[] var0) {
      if (var0.length == 0) {
         return null;
      } else {
         BitSet var1 = new BitSet();
         String[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            int var6 = Arrays.binarySearch(Token.TOKENS, 3, 92, var5);
            if (var6 >= 0) {
               var1.set(var6);
            }
         }

         return var1.isEmpty() ? null : var1;
      }
   }

   public static String formatNonKeywords(BitSet var0) {
      if (var0 != null && !var0.isEmpty()) {
         StringBuilder var1 = new StringBuilder();
         int var2 = -1;

         while((var2 = var0.nextSetBit(var2 + 1)) >= 0) {
            if (var2 >= 3 && var2 <= 91) {
               if (var1.length() > 0) {
                  var1.append(',');
               }

               var1.append(Token.TOKENS[var2]);
            }
         }

         return var1.toString();
      } else {
         return "";
      }
   }

   public Parser(SessionLocal var1) {
      this.database = var1.getDatabase();
      DbSettings var2 = this.database.getSettings();
      this.identifiersToLower = var2.databaseToLower;
      this.identifiersToUpper = var2.databaseToUpper;
      this.variableBinary = var1.isVariableBinary();
      this.nonKeywords = var1.getNonKeywords();
      this.session = var1;
   }

   public Parser() {
      this.database = null;
      this.identifiersToLower = false;
      this.identifiersToUpper = false;
      this.variableBinary = false;
      this.nonKeywords = null;
      this.session = null;
   }

   public Prepared prepare(String var1) {
      Prepared var2 = this.parse(var1, (ArrayList)null);
      var2.prepare();
      if (this.currentTokenType != 93) {
         throw this.getSyntaxError();
      } else {
         return var2;
      }
   }

   public Command prepareCommand(String var1) {
      try {
         Prepared var2 = this.parse(var1, (ArrayList)null);
         if (this.currentTokenType != 115 && this.currentTokenType != 93) {
            this.addExpected(115);
            throw this.getSyntaxError();
         } else {
            try {
               var2.prepare();
            } catch (Throwable var6) {
               CommandContainer.clearCTE(this.session, var2);
               throw var6;
            }

            int var3 = this.token.start();
            if (var3 < var1.length()) {
               var1 = var1.substring(0, var3);
            }

            CommandContainer var4 = new CommandContainer(this.session, var1, var2);

            while(this.currentTokenType == 115) {
               this.read();
            }

            if (this.currentTokenType != 93) {
               int var5 = this.token.start();
               return this.prepareCommandList(var4, var2, var1, this.sqlCommand.substring(var5), this.getRemainingTokens(var5));
            } else {
               return var4;
            }
         }
      } catch (DbException var7) {
         throw var7.addSQL(this.sqlCommand);
      }
   }

   private CommandList prepareCommandList(CommandContainer var1, Prepared var2, String var3, String var4, ArrayList<Token> var5) {
      try {
         ArrayList var6;
         int var7;
         for(var6 = Utils.newSmallArrayList(); !(var2 instanceof DefineCommand); var5 = this.getRemainingTokens(var7)) {
            this.suppliedParameters = this.parameters;

            try {
               var2 = this.parse(var4, var5);
            } catch (DbException var8) {
               if (var8.getErrorCode() == 90123) {
                  throw var8;
               }

               return new CommandList(this.session, var3, var1, var6, this.parameters, var4);
            }

            var6.add(var2);
            if (this.currentTokenType != 115 && this.currentTokenType != 93) {
               this.addExpected(115);
               throw this.getSyntaxError();
            }

            while(this.currentTokenType == 115) {
               this.read();
            }

            if (this.currentTokenType == 93) {
               return new CommandList(this.session, var3, var1, var6, this.parameters, (String)null);
            }

            var7 = this.token.start();
            var4 = this.sqlCommand.substring(var7);
         }

         return new CommandList(this.session, var3, var1, var6, this.parameters, var4);
      } catch (Throwable var9) {
         var1.clearCTE();
         throw var9;
      }
   }

   private ArrayList<Token> getRemainingTokens(int var1) {
      List var2 = this.tokens.subList(this.tokenIndex, this.tokens.size());
      ArrayList var3 = new ArrayList(var2);
      var2.clear();
      this.tokens.add(new Token.EndOfInputToken(var1));
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         Token var5 = (Token)var4.next();
         var5.subtractFromStart(var1);
      }

      return var3;
   }

   Prepared parse(String var1, ArrayList<Token> var2) {
      this.initialize(var1, var2, false);

      Prepared var3;
      try {
         var3 = this.parse(var1, false);
      } catch (DbException var5) {
         if (var5.getErrorCode() != 42000) {
            throw var5.addSQL(var1);
         }

         this.resetTokenIndex();
         var3 = this.parse(var1, true);
      }

      var3.setPrepareAlways(this.recompileAlways);
      var3.setParameterList(this.parameters);
      return var3;
   }

   private Prepared parse(String var1, boolean var2) {
      if (var2) {
         this.expectedList = new ArrayList();
      } else {
         this.expectedList = null;
      }

      this.parameters = this.suppliedParameters != null ? this.suppliedParameters : Utils.newSmallArrayList();
      this.currentSelect = null;
      this.currentPrepared = null;
      this.createView = null;
      this.cteCleanups = null;
      this.recompileAlways = false;
      this.read();

      try {
         Prepared var3 = this.parsePrepared();
         var3.setCteCleanups(this.cteCleanups);
         return var3;
      } catch (Throwable var5) {
         if (this.cteCleanups != null) {
            CommandContainer.clearCTE(this.session, this.cteCleanups);
         }

         throw var5;
      }
   }

   private Prepared parsePrepared() {
      int var1 = this.tokenIndex;
      Object var2 = null;
      switch (this.currentTokenType) {
         case 2:
            if (!this.token.isQuoted()) {
               switch (this.currentToken.charAt(0) & 65503) {
                  case 65:
                     if (this.readIf("ALTER")) {
                        var2 = this.parseAlter();
                     } else if (this.readIf("ANALYZE")) {
                        var2 = this.parseAnalyze();
                     }
                     break;
                  case 66:
                     if (this.readIf("BACKUP")) {
                        var2 = this.parseBackup();
                     } else if (this.readIf("BEGIN")) {
                        var2 = this.parseBegin();
                     }
                     break;
                  case 67:
                     if (this.readIf("COMMIT")) {
                        var2 = this.parseCommit();
                     } else if (this.readIf("CREATE")) {
                        var2 = this.parseCreate();
                     } else if (this.readIf("CALL")) {
                        var2 = this.parseCall();
                     } else if (this.readIf("CHECKPOINT")) {
                        var2 = this.parseCheckpoint();
                     } else if (this.readIf("COMMENT")) {
                        var2 = this.parseComment();
                     }
                     break;
                  case 68:
                     if (this.readIf("DELETE")) {
                        var2 = this.parseDelete(var1);
                     } else if (this.readIf("DROP")) {
                        var2 = this.parseDrop();
                     } else if (this.readIf("DECLARE")) {
                        var2 = this.parseCreate();
                     } else if (this.database.getMode().getEnum() != Mode.ModeEnum.MSSQLServer && this.readIf("DEALLOCATE")) {
                        var2 = this.parseDeallocate();
                     }
                     break;
                  case 69:
                     if (this.readIf("EXPLAIN")) {
                        var2 = this.parseExplain();
                     } else if (this.database.getMode().getEnum() != Mode.ModeEnum.MSSQLServer) {
                        if (this.readIf("EXECUTE")) {
                           var2 = this.parseExecutePostgre();
                        }
                     } else if (this.readIf("EXEC") || this.readIf("EXECUTE")) {
                        var2 = this.parseExecuteSQLServer();
                     }
                  case 70:
                  case 74:
                  case 75:
                  case 76:
                  case 78:
                  case 79:
                  case 81:
                  default:
                     break;
                  case 71:
                     if (this.readIf("GRANT")) {
                        var2 = this.parseGrantRevoke(49);
                     }
                     break;
                  case 72:
                     if (this.readIf("HELP")) {
                        var2 = this.parseHelp();
                     }
                     break;
                  case 73:
                     if (this.readIf("INSERT")) {
                        var2 = this.parseInsert(var1);
                     }
                     break;
                  case 77:
                     if (this.readIf("MERGE")) {
                        var2 = this.parseMerge(var1);
                     }
                     break;
                  case 80:
                     if (this.readIf("PREPARE")) {
                        var2 = this.parsePrepare();
                     }
                     break;
                  case 82:
                     if (this.readIf("ROLLBACK")) {
                        var2 = this.parseRollback();
                     } else if (this.readIf("REVOKE")) {
                        var2 = this.parseGrantRevoke(50);
                     } else if (this.readIf("RUNSCRIPT")) {
                        var2 = this.parseRunScript();
                     } else if (this.readIf("RELEASE")) {
                        var2 = this.parseReleaseSavepoint();
                     } else if (this.database.getMode().replaceInto && this.readIf("REPLACE")) {
                        var2 = this.parseReplace(var1);
                     }
                     break;
                  case 83:
                     if (this.readIf("SAVEPOINT")) {
                        var2 = this.parseSavepoint();
                     } else if (this.readIf("SCRIPT")) {
                        var2 = this.parseScript();
                     } else if (this.readIf("SHUTDOWN")) {
                        var2 = this.parseShutdown();
                     } else if (this.readIf("SHOW")) {
                        var2 = this.parseShow();
                     }
                     break;
                  case 84:
                     if (this.readIf("TRUNCATE")) {
                        var2 = this.parseTruncate();
                     }
                     break;
                  case 85:
                     if (this.readIf("UPDATE")) {
                        var2 = this.parseUpdate(var1);
                     } else if (this.readIf("USE")) {
                        var2 = this.parseUse();
                     }
               }
            }
            break;
         case 69:
         case 75:
         case 85:
         case 105:
            var2 = this.parseQuery();
            break;
         case 71:
            this.read();
            var2 = this.parseSet();
            break;
         case 89:
            this.read();
            var2 = this.parseWithStatementOrQuery(var1);
            break;
         case 92:
            this.readParameter().setValue(ValueNull.INSTANCE);
            this.read(95);
            var1 = this.tokenIndex;
            this.read("CALL");
            var2 = this.parseCall();
            break;
         case 93:
         case 115:
            NoOperation var7 = new NoOperation(this.session);
            this.setSQL(var7, var1);
            return var7;
      }

      if (var2 == null) {
         throw this.getSyntaxError();
      } else {
         int var4;
         if (this.parameters != null) {
            int var3 = 0;

            for(var4 = this.parameters.size(); var3 < var4; ++var3) {
               if (this.parameters.get(var3) == null) {
                  this.parameters.set(var3, new Parameter(var3));
               }
            }
         }

         boolean var8 = this.readIf(111);
         if (var8) {
            while(true) {
               var4 = (int)this.readLong() - 1;
               if (var4 < 0 || var4 >= this.parameters.size()) {
                  throw this.getSyntaxError();
               }

               Parameter var5 = (Parameter)this.parameters.get(var4);
               if (var5 == null) {
                  throw this.getSyntaxError();
               }

               this.read(116);
               Expression var6 = this.readExpression();
               var6 = var6.optimize(this.session);
               var5.setValue(var6.getValue(this.session));
               if (!this.readIf(109)) {
                  this.read(112);
                  Iterator var9 = this.parameters.iterator();

                  while(var9.hasNext()) {
                     var5 = (Parameter)var9.next();
                     var5.checkSet();
                  }

                  this.parameters.clear();
                  break;
               }
            }
         }

         if (var8 || ((Prepared)var2).getSQL() == null) {
            this.setSQL((Prepared)var2, var1);
         }

         return (Prepared)var2;
      }
   }

   private DbException getSyntaxError() {
      return this.expectedList != null && !this.expectedList.isEmpty() ? DbException.getSyntaxError(this.sqlCommand, this.token.start(), String.join(", ", this.expectedList)) : DbException.getSyntaxError(this.sqlCommand, this.token.start());
   }

   private Prepared parseBackup() {
      BackupCommand var1 = new BackupCommand(this.session);
      this.read(76);
      var1.setFileName(this.readExpression());
      return var1;
   }

   private Prepared parseAnalyze() {
      Analyze var1 = new Analyze(this.session);
      if (this.readIf(75)) {
         Table var2 = this.readTableOrView();
         var1.setTable(var2);
      }

      if (this.readIf("SAMPLE_SIZE")) {
         var1.setTop(this.readNonNegativeInt());
      }

      return var1;
   }

   private TransactionCommand parseBegin() {
      if (!this.readIf("WORK")) {
         this.readIf("TRANSACTION");
      }

      TransactionCommand var1 = new TransactionCommand(this.session, 83);
      return var1;
   }

   private TransactionCommand parseCommit() {
      TransactionCommand var1;
      if (this.readIf("TRANSACTION")) {
         var1 = new TransactionCommand(this.session, 78);
         var1.setTransactionName(this.readIdentifier());
         return var1;
      } else {
         var1 = new TransactionCommand(this.session, 71);
         this.readIf("WORK");
         return var1;
      }
   }

   private TransactionCommand parseShutdown() {
      byte var1 = 80;
      if (this.readIf("IMMEDIATELY")) {
         var1 = 81;
      } else if (this.readIf("COMPACT")) {
         var1 = 82;
      } else if (this.readIf("DEFRAG")) {
         var1 = 84;
      } else {
         this.readIf("SCRIPT");
      }

      return new TransactionCommand(this.session, var1);
   }

   private TransactionCommand parseRollback() {
      TransactionCommand var1;
      if (this.readIf("TRANSACTION")) {
         var1 = new TransactionCommand(this.session, 79);
         var1.setTransactionName(this.readIdentifier());
         return var1;
      } else {
         this.readIf("WORK");
         if (this.readIf(76)) {
            this.read("SAVEPOINT");
            var1 = new TransactionCommand(this.session, 75);
            var1.setSavepointName(this.readIdentifier());
         } else {
            var1 = new TransactionCommand(this.session, 72);
         }

         return var1;
      }
   }

   private Prepared parsePrepare() {
      if (this.readIf("COMMIT")) {
         TransactionCommand var1 = new TransactionCommand(this.session, 77);
         var1.setTransactionName(this.readIdentifier());
         return var1;
      } else {
         return this.parsePrepareProcedure();
      }
   }

   private Prepared parsePrepareProcedure() {
      if (this.database.getMode().getEnum() == Mode.ModeEnum.MSSQLServer) {
         throw this.getSyntaxError();
      } else {
         String var1 = this.readIdentifier();
         if (this.readIf(105)) {
            ArrayList var2 = Utils.newSmallArrayList();
            int var3 = 0;

            while(true) {
               Column var4 = this.parseColumnForTable("C" + var3, true);
               var2.add(var4);
               if (!this.readIfMore()) {
                  break;
               }

               ++var3;
            }
         }

         this.read(7);
         Prepared var5 = this.parsePrepared();
         PrepareProcedure var6 = new PrepareProcedure(this.session);
         var6.setProcedureName(var1);
         var6.setPrepared(var5);
         return var6;
      }
   }

   private TransactionCommand parseSavepoint() {
      TransactionCommand var1 = new TransactionCommand(this.session, 74);
      var1.setSavepointName(this.readIdentifier());
      return var1;
   }

   private Prepared parseReleaseSavepoint() {
      NoOperation var1 = new NoOperation(this.session);
      this.readIf("SAVEPOINT");
      this.readIdentifier();
      return var1;
   }

   private Schema findSchema(String var1) {
      if (var1 == null) {
         return null;
      } else {
         Schema var2 = this.database.findSchema(var1);
         if (var2 == null && this.equalsToken("SESSION", var1)) {
            var2 = this.database.getSchema(this.session.getCurrentSchemaName());
         }

         return var2;
      }
   }

   private Schema getSchema(String var1) {
      if (var1 == null) {
         return null;
      } else {
         Schema var2 = this.findSchema(var1);
         if (var2 == null) {
            throw DbException.get(90079, var1);
         } else {
            return var2;
         }
      }
   }

   private Schema getSchema() {
      return this.getSchema(this.schemaName);
   }

   private Schema getSchemaWithDefault() {
      if (this.schemaName == null) {
         this.schemaName = this.session.getCurrentSchemaName();
      }

      return this.getSchema(this.schemaName);
   }

   private Column readTableColumn(TableFilter var1) {
      String var2 = this.readIdentifier();
      if (this.readIf(110)) {
         var2 = this.readTableColumn(var1, var2);
      }

      return var1.getTable().getColumn(var2);
   }

   private String readTableColumn(TableFilter var1, String var2) {
      String var3 = this.readIdentifier();
      if (this.readIf(110)) {
         String var4 = var2;
         var2 = var3;
         var3 = this.readIdentifier();
         if (this.readIf(110)) {
            this.checkDatabaseName(var4);
            var4 = var2;
            var2 = var3;
            var3 = this.readIdentifier();
         }

         if (!this.equalsToken(var4, var1.getTable().getSchema().getName())) {
            throw DbException.get(90079, var4);
         }
      }

      if (!this.equalsToken(var2, var1.getTableAlias())) {
         throw DbException.get(42102, (String)var2);
      } else {
         return var3;
      }
   }

   private Update parseUpdate(int var1) {
      Update var2 = new Update(this.session);
      this.currentPrepared = var2;
      Expression var3 = null;
      if (this.database.getMode().topInDML && this.readIf("TOP")) {
         this.read(105);
         var3 = this.readTerm().optimize(this.session);
         this.read(106);
      }

      TableFilter var4 = this.readSimpleTableFilter();
      var2.setTableFilter(var4);
      var2.setSetClauseList(this.readUpdateSetClause(var4));
      if (this.database.getMode().allowUsingFromClauseInUpdateStatement && this.readIf(35)) {
         TableFilter var5 = this.readTablePrimary();
         var2.setFromTableFilter(var5);
      }

      if (this.readIf(87)) {
         var2.setCondition(this.readExpression());
      }

      if (var3 == null) {
         this.readIfOrderBy();
         var3 = this.readFetchOrLimit();
      }

      var2.setFetch(var3);
      this.setSQL(var2, var1);
      return var2;
   }

   private SetClauseList readUpdateSetClause(TableFilter var1) {
      this.read(71);
      SetClauseList var2 = new SetClauseList(var1.getTable());

      do {
         if (!this.readIf(105)) {
            Column var4 = this.readTableColumn(var1);
            this.read(95);
            var2.addSingle(var4, this.readExpressionOrDefault());
         } else {
            ArrayList var3 = Utils.newSmallArrayList();

            do {
               var3.add(this.readTableColumn(var1));
            } while(this.readIfMore());

            this.read(95);
            var2.addMultiple(var3, this.readExpression());
         }
      } while(this.readIf(109));

      return var2;
   }

   private TableFilter readSimpleTableFilter() {
      return new TableFilter(this.session, this.readTableOrView(), this.readFromAlias((String)null), this.rightsChecked, this.currentSelect, 0, (IndexHints)null);
   }

   private Delete parseDelete(int var1) {
      Delete var2 = new Delete(this.session);
      Expression var3 = null;
      if (this.database.getMode().topInDML && this.readIf("TOP")) {
         var3 = this.readTerm().optimize(this.session);
      }

      this.currentPrepared = var2;
      if (!this.readIf(35) && this.database.getMode().getEnum() == Mode.ModeEnum.MySQL) {
         this.readIdentifierWithSchema();
         this.read(35);
      }

      var2.setTableFilter(this.readSimpleTableFilter());
      if (this.readIf(87)) {
         var2.setCondition(this.readExpression());
      }

      if (var3 == null) {
         var3 = this.readFetchOrLimit();
      }

      var2.setFetch(var3);
      this.setSQL(var2, var1);
      return var2;
   }

   private Expression readFetchOrLimit() {
      Object var1 = null;
      if (this.readIf(32)) {
         if (!this.readIf("FIRST")) {
            this.read("NEXT");
         }

         if (!this.readIf(66) && !this.readIf("ROWS")) {
            var1 = this.readExpression().optimize(this.session);
            if (!this.readIf(66)) {
               this.read("ROWS");
            }
         } else {
            var1 = ValueExpression.get(ValueInteger.get(1));
         }

         this.read("ONLY");
      } else if (this.database.getMode().limit && this.readIf(50)) {
         var1 = this.readTerm().optimize(this.session);
      }

      return (Expression)var1;
   }

   private IndexColumn[] parseIndexColumnList() {
      ArrayList var1 = Utils.newSmallArrayList();

      do {
         var1.add(new IndexColumn(this.readIdentifier(), this.parseSortType()));
      } while(this.readIfMore());

      return (IndexColumn[])var1.toArray(new IndexColumn[0]);
   }

   private int parseSortType() {
      int var1 = !this.readIf("ASC") && this.readIf("DESC") ? 1 : 0;
      if (this.readIf("NULLS")) {
         if (this.readIf("FIRST")) {
            var1 |= 2;
         } else {
            this.read("LAST");
            var1 |= 4;
         }
      }

      return var1;
   }

   private String[] parseColumnList() {
      ArrayList var1 = Utils.newSmallArrayList();

      do {
         var1.add(this.readIdentifier());
      } while(this.readIfMore());

      return (String[])var1.toArray(new String[0]);
   }

   private Column[] parseColumnList(Table var1) {
      ArrayList var2 = Utils.newSmallArrayList();
      HashSet var3 = new HashSet();
      if (!this.readIf(106)) {
         do {
            Column var4 = this.parseColumn(var1);
            if (!var3.add(var4)) {
               throw DbException.get(42121, (String)var4.getTraceSQL());
            }

            var2.add(var4);
         } while(this.readIfMore());
      }

      return (Column[])var2.toArray(new Column[0]);
   }

   private Column parseColumn(Table var1) {
      if (this.currentTokenType == 91) {
         this.read();
         return var1.getRowIdColumn();
      } else {
         return var1.getColumn(this.readIdentifier());
      }
   }

   private boolean readIfMore() {
      if (this.readIf(109)) {
         return true;
      } else {
         this.read(106);
         return false;
      }
   }

   private Prepared parseHelp() {
      HashSet var1 = new HashSet();

      while(this.currentTokenType != 93) {
         var1.add(StringUtils.toUpperEnglish(this.currentToken));
         this.read();
      }

      return new Help(this.session, (String[])var1.toArray(new String[0]));
   }

   private Prepared parseShow() {
      ArrayList var1 = Utils.newSmallArrayList();
      StringBuilder var2 = new StringBuilder("SELECT ");
      if (this.readIf("CLIENT_ENCODING")) {
         var2.append("'UNICODE' CLIENT_ENCODING");
      } else if (this.readIf("DEFAULT_TRANSACTION_ISOLATION")) {
         var2.append("'read committed' DEFAULT_TRANSACTION_ISOLATION");
      } else if (this.readIf("TRANSACTION")) {
         this.read("ISOLATION");
         this.read("LEVEL");
         var2.append("LOWER(ISOLATION_LEVEL) TRANSACTION_ISOLATION FROM INFORMATION_SCHEMA.SESSIONS WHERE SESSION_ID = SESSION_ID()");
      } else if (this.readIf("DATESTYLE")) {
         var2.append("'ISO' DATESTYLE");
      } else if (this.readIf("SEARCH_PATH")) {
         String[] var3 = this.session.getSchemaSearchPath();
         StringBuilder var4 = new StringBuilder();
         if (var3 != null) {
            for(int var5 = 0; var5 < var3.length; ++var5) {
               if (var5 > 0) {
                  var4.append(", ");
               }

               ParserUtil.quoteIdentifier(var4, var3[var5], 1);
            }
         }

         StringUtils.quoteStringSQL(var2, var4.toString());
         var2.append(" SEARCH_PATH");
      } else if (this.readIf("SERVER_VERSION")) {
         var2.append("'8.2.23' SERVER_VERSION");
      } else if (this.readIf("SERVER_ENCODING")) {
         var2.append("'UTF8' SERVER_ENCODING");
      } else if (this.readIf("SSL")) {
         var2.append("'off' SSL");
      } else {
         String var9;
         if (this.readIf("TABLES")) {
            var9 = this.database.getMainSchema().getName();
            if (this.readIf(35)) {
               var9 = this.readIdentifier();
            }

            var2.append("TABLE_NAME, TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=? ORDER BY TABLE_NAME");
            var1.add(ValueVarchar.get(var9));
         } else if (this.readIf("COLUMNS")) {
            this.read(35);
            var9 = this.readIdentifierWithSchema();
            String var10 = this.getSchema().getName();
            var1.add(ValueVarchar.get(var9));
            if (this.readIf(35)) {
               var10 = this.readIdentifier();
            }

            var2.append("C.COLUMN_NAME FIELD, ");
            boolean var13 = this.session.isOldInformationSchema();
            var2.append(var13 ? "C.COLUMN_TYPE" : "DATA_TYPE_SQL(?2, ?1, 'TABLE', C.DTD_IDENTIFIER)");
            var2.append(" TYPE, C.IS_NULLABLE \"NULL\", CASE (SELECT MAX(I.INDEX_TYPE_NAME) FROM INFORMATION_SCHEMA.INDEXES I ");
            if (!var13) {
               var2.append("JOIN INFORMATION_SCHEMA.INDEX_COLUMNS IC ");
            }

            var2.append("WHERE I.TABLE_SCHEMA=C.TABLE_SCHEMA AND I.TABLE_NAME=C.TABLE_NAME ");
            if (var13) {
               var2.append("AND I.COLUMN_NAME=C.COLUMN_NAME");
            } else {
               var2.append("AND IC.TABLE_SCHEMA=C.TABLE_SCHEMA AND IC.TABLE_NAME=C.TABLE_NAME AND IC.INDEX_SCHEMA=I.INDEX_SCHEMA AND IC.INDEX_NAME=I.INDEX_NAME AND IC.COLUMN_NAME=C.COLUMN_NAME");
            }

            var2.append(")WHEN 'PRIMARY KEY' THEN 'PRI' WHEN 'UNIQUE INDEX' THEN 'UNI' ELSE '' END `KEY`, COALESCE(COLUMN_DEFAULT, 'NULL') `DEFAULT` FROM INFORMATION_SCHEMA.COLUMNS C WHERE C.TABLE_NAME=?1 AND C.TABLE_SCHEMA=?2 ORDER BY C.ORDINAL_POSITION");
            var1.add(ValueVarchar.get(var10));
         } else if (!this.readIf("DATABASES") && !this.readIf("SCHEMAS")) {
            if (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && this.readIf("ALL")) {
               var2.append("NAME, SETTING FROM PG_CATALOG.PG_SETTINGS");
            }
         } else {
            var2.append("SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA");
         }
      }

      boolean var11 = this.session.getAllowLiterals();

      Prepared var12;
      try {
         this.session.setAllowLiterals(true);
         var12 = prepare(this.session, var2.toString(), var1);
      } finally {
         this.session.setAllowLiterals(var11);
      }

      return var12;
   }

   private static Prepared prepare(SessionLocal var0, String var1, ArrayList<Value> var2) {
      Prepared var3 = var0.prepare(var1);
      ArrayList var4 = var3.getParameters();
      if (var4 != null) {
         int var5 = 0;

         for(int var6 = var4.size(); var5 < var6; ++var5) {
            Parameter var7 = (Parameter)var4.get(var5);
            var7.setValue((Value)var2.get(var5));
         }
      }

      return var3;
   }

   private boolean isDerivedTable() {
      int var1 = this.tokenIndex;

      int var2;
      for(var2 = 0; ((Token)this.tokens.get(var1)).tokenType() == 105; ++var1) {
         ++var2;
      }

      boolean var3 = this.isDirectQuery(var1);
      if (var3 && var2 > 0) {
         var1 = this.scanToCloseParen(var1 + 1);
         if (var1 < 0) {
            var3 = false;
         } else {
            while(true) {
               switch (((Token)this.tokens.get(var1)).tokenType()) {
                  case 46:
                     var3 = false;
                     return var3;
                  case 93:
                  case 115:
                     var3 = false;
                     return var3;
                  case 105:
                     var1 = this.scanToCloseParen(var1 + 1);
                     if (var1 < 0) {
                        var3 = false;
                        return var3;
                     }
                     break;
                  case 106:
                     --var2;
                     if (var2 == 0) {
                        return var3;
                     }

                     ++var1;
                     break;
                  default:
                     ++var1;
               }
            }
         }
      }

      return var3;
   }

   private boolean isQuery() {
      int var1 = this.tokenIndex;

      int var2;
      for(var2 = 0; ((Token)this.tokens.get(var1)).tokenType() == 105; ++var1) {
         ++var2;
      }

      boolean var3 = this.isDirectQuery(var1);
      if (var3 && var2 > 0) {
         ++var1;

         while(true) {
            var1 = this.scanToCloseParen(var1);
            if (var1 < 0) {
               var3 = false;
               break;
            }

            switch (((Token)this.tokens.get(var1)).tokenType()) {
               case 29:
               case 32:
               case 43:
               case 50:
               case 53:
               case 59:
               case 62:
               case 79:
               case 93:
               case 106:
               case 115:
                  --var2;
                  if (var2 <= 0) {
                     return var3;
                  }
                  break;
               default:
                  var3 = false;
                  return var3;
            }
         }
      }

      return var3;
   }

   private int scanToCloseParen(int var1) {
      int var2 = 0;

      while(true) {
         switch (((Token)this.tokens.get(var1)).tokenType()) {
            case 93:
            case 115:
               return -1;
            case 105:
               ++var2;
               break;
            case 106:
               --var2;
               if (var2 < 0) {
                  return var1 + 1;
               }
         }

         ++var1;
      }
   }

   private boolean isQueryQuick() {
      int var1;
      for(var1 = this.tokenIndex; ((Token)this.tokens.get(var1)).tokenType() == 105; ++var1) {
      }

      return this.isDirectQuery(var1);
   }

   private boolean isDirectQuery(int var1) {
      boolean var2;
      switch (((Token)this.tokens.get(var1)).tokenType()) {
         case 69:
         case 85:
         case 89:
            var2 = true;
            break;
         case 75:
            var2 = ((Token)this.tokens.get(var1 + 1)).tokenType() != 105;
            break;
         default:
            var2 = false;
      }

      return var2;
   }

   private Prepared parseMerge(int var1) {
      this.read("INTO");
      TableFilter var2 = this.readSimpleTableFilter();
      return (Prepared)(this.readIf(83) ? this.parseMergeUsing(var2, var1) : this.parseMergeInto(var2, var1));
   }

   private Prepared parseMergeInto(TableFilter var1, int var2) {
      Merge var3 = new Merge(this.session, false);
      this.currentPrepared = var3;
      var3.setTable(var1.getTable());
      Table var4 = var3.getTable();
      if (this.readIf(105)) {
         if (this.isQueryQuick()) {
            var3.setQuery(this.parseQuery());
            this.read(106);
            return var3;
         }

         var3.setColumns(this.parseColumnList(var4));
      }

      if (this.readIf(47)) {
         this.read(105);
         var3.setKeys(this.parseColumnList(var4));
      }

      if (this.readIf(85)) {
         this.parseValuesForCommand(var3);
      } else {
         var3.setQuery(this.parseQuery());
      }

      this.setSQL(var3, var2);
      return var3;
   }

   private MergeUsing parseMergeUsing(TableFilter var1, int var2) {
      MergeUsing var3 = new MergeUsing(this.session, var1);
      this.currentPrepared = var3;
      var3.setSourceTableFilter(this.readTableReference());
      this.read(60);
      Expression var4 = this.readExpression();
      var3.setOnCondition(var4);
      this.read(86);

      do {
         boolean var5 = this.readIf("MATCHED");
         if (var5) {
            this.parseWhenMatched(var3);
         } else {
            this.parseWhenNotMatched(var3);
         }
      } while(this.readIf(86));

      this.setSQL(var3, var2);
      return var3;
   }

   private void parseWhenMatched(MergeUsing var1) {
      Expression var2 = this.readIf(4) ? this.readExpression() : null;
      this.read("THEN");
      Object var3;
      if (this.readIf("UPDATE")) {
         MergeUsing.WhenMatchedThenUpdate var4 = var1.new WhenMatchedThenUpdate();
         var4.setSetClauseList(this.readUpdateSetClause(var1.getTargetTableFilter()));
         var3 = var4;
      } else {
         this.read("DELETE");
         var3 = var1.new WhenMatchedThenDelete();
      }

      if (var2 == null && this.database.getMode().mergeWhere && this.readIf(87)) {
         var2 = this.readExpression();
      }

      ((MergeUsing.When)var3).setAndCondition(var2);
      var1.addWhen((MergeUsing.When)var3);
   }

   private void parseWhenNotMatched(MergeUsing var1) {
      this.read(57);
      this.read("MATCHED");
      Expression var2 = this.readIf(4) ? this.readExpression() : null;
      this.read("THEN");
      this.read("INSERT");
      Column[] var3 = this.readIf(105) ? this.parseColumnList(var1.getTargetTableFilter().getTable()) : null;
      Boolean var4 = this.readIfOverriding();
      this.read(85);
      this.read(105);
      ArrayList var5 = Utils.newSmallArrayList();
      if (!this.readIf(106)) {
         do {
            var5.add(this.readExpressionOrDefault());
         } while(this.readIfMore());
      }

      MergeUsing.WhenNotMatched var6 = var1.new WhenNotMatched(var3, var4, (Expression[])var5.toArray(new Expression[0]));
      var6.setAndCondition(var2);
      var1.addWhen(var6);
   }

   private Insert parseInsert(int var1) {
      Insert var2 = new Insert(this.session);
      this.currentPrepared = var2;
      Mode var3 = this.database.getMode();
      if (var3.onDuplicateKeyUpdate && this.readIf("IGNORE")) {
         var2.setIgnore(true);
      }

      this.read("INTO");
      Table var4 = this.readTableOrView();
      var2.setTable(var4);
      Column[] var5 = null;
      if (this.readIf(105)) {
         if (this.isQueryQuick()) {
            var2.setQuery(this.parseQuery());
            this.read(106);
            return var2;
         }

         var5 = this.parseColumnList(var4);
         var2.setColumns(var5);
      }

      Boolean var6 = this.readIfOverriding();
      var2.setOverridingSystem(var6);
      boolean var7 = false;
      if (this.readIf("DIRECT")) {
         var7 = true;
         var2.setInsertFromSelect(true);
      }

      if (this.readIf("SORTED")) {
         var7 = true;
      }

      label48: {
         if (!var7) {
            if (var6 == null && this.readIf(25)) {
               this.read(85);
               var2.addRow(new Expression[0]);
               break label48;
            }

            if (this.readIf(85)) {
               this.parseValuesForCommand(var2);
               break label48;
            }

            if (this.readIf(71)) {
               this.parseInsertSet(var2, var4, var5);
               break label48;
            }
         }

         var2.setQuery(this.parseQuery());
      }

      if (var3.onDuplicateKeyUpdate || var3.insertOnConflict || var3.isolationLevelInSelectOrInsertStatement) {
         this.parseInsertCompatibility(var2, var4, var3);
      }

      this.setSQL(var2, var1);
      return var2;
   }

   private Boolean readIfOverriding() {
      Boolean var1 = null;
      if (this.readIf("OVERRIDING")) {
         if (this.readIf(82)) {
            var1 = Boolean.FALSE;
         } else {
            this.read("SYSTEM");
            var1 = Boolean.TRUE;
         }

         this.read(84);
      }

      return var1;
   }

   private void parseInsertSet(Insert var1, Table var2, Column[] var3) {
      if (var3 != null) {
         throw this.getSyntaxError();
      } else {
         ArrayList var4 = Utils.newSmallArrayList();
         ArrayList var5 = Utils.newSmallArrayList();

         do {
            var4.add(this.parseColumn(var2));
            this.read(95);
            var5.add(this.readExpressionOrDefault());
         } while(this.readIf(109));

         var1.setColumns((Column[])var4.toArray(new Column[0]));
         var1.addRow((Expression[])var5.toArray(new Expression[0]));
      }
   }

   private void parseInsertCompatibility(Insert var1, Table var2, Mode var3) {
      if (var3.onDuplicateKeyUpdate && this.readIf(60)) {
         this.read("DUPLICATE");
         this.read(47);
         this.read("UPDATE");

         do {
            String var4 = this.readIdentifier();
            if (this.readIf(110)) {
               String var5 = var4;
               String var6 = this.readIdentifier();
               if (this.readIf(110)) {
                  if (!var2.getSchema().getName().equals(var4)) {
                     throw DbException.get(90080);
                  }

                  var4 = this.readIdentifier();
               } else {
                  var4 = var6;
                  var6 = var5;
               }

               if (!var2.getName().equals(var6)) {
                  throw DbException.get(42102, (String)var6);
               }
            }

            Column var7 = var2.getColumn(var4);
            this.read(95);
            var1.addAssignmentForDuplicate(var7, this.readExpressionOrDefault());
         } while(this.readIf(109));
      }

      if (var3.insertOnConflict && this.readIf(60)) {
         this.read("CONFLICT");
         this.read("DO");
         this.read("NOTHING");
         var1.setIgnore(true);
      }

      if (var3.isolationLevelInSelectOrInsertStatement) {
         this.parseIsolationClause();
      }

   }

   private Merge parseReplace(int var1) {
      Merge var2 = new Merge(this.session, true);
      this.currentPrepared = var2;
      this.read("INTO");
      Table var3 = this.readTableOrView();
      var2.setTable(var3);
      if (this.readIf(105)) {
         if (this.isQueryQuick()) {
            var2.setQuery(this.parseQuery());
            this.read(106);
            return var2;
         }

         var2.setColumns(this.parseColumnList(var3));
      }

      if (this.readIf(85)) {
         this.parseValuesForCommand(var2);
      } else {
         var2.setQuery(this.parseQuery());
      }

      this.setSQL(var2, var1);
      return var2;
   }

   private void parseValuesForCommand(CommandWithValues var1) {
      ArrayList var2 = Utils.newSmallArrayList();

      do {
         var2.clear();
         boolean var3;
         if (this.readIf(66)) {
            this.read(105);
            var3 = true;
         } else {
            var3 = this.readIf(105);
         }

         if (var3) {
            if (!this.readIf(106)) {
               do {
                  var2.add(this.readExpressionOrDefault());
               } while(this.readIfMore());
            }
         } else {
            var2.add(this.readExpressionOrDefault());
         }

         var1.addRow((Expression[])var2.toArray(new Expression[0]));
      } while(this.readIf(109));

   }

   private TableFilter readTablePrimary() {
      String var2 = null;
      if (this.readIf(105)) {
         if (this.isDerivedTable()) {
            return this.readDerivedTableWithCorrelation();
         } else {
            TableFilter var15 = this.readTableReference();
            this.read(106);
            return this.readCorrelation(var15);
         }
      } else {
         Object var1;
         if (this.readIf(85)) {
            TableValueConstructor var3 = this.parseValues();
            var2 = this.session.getNextSystemIdentifier(this.sqlCommand);
            var1 = var3.toTable(var2, (Column[])null, this.parameters, this.createView != null, this.currentSelect);
         } else if (this.readIf(75)) {
            this.read(105);
            ArrayTableFunction var12 = this.readTableFunction(1);
            var1 = new FunctionTable(this.database.getMainSchema(), this.session, var12);
         } else {
            label79: {
               boolean var13 = this.token.isQuoted();
               String var4 = this.readIdentifier();
               int var5 = this.tokenIndex;
               this.schemaName = null;
               if (this.readIf(110)) {
                  var4 = this.readIdentifierWithSchema2(var4);
               } else if (!var13 && this.readIf(75)) {
                  var1 = this.readDataChangeDeltaTable(this.upperName(var4), var5);
                  break label79;
               }

               Schema var6;
               if (this.schemaName == null) {
                  var6 = null;
               } else {
                  var6 = this.findSchema(this.schemaName);
                  if (var6 == null) {
                     if (!this.isDualTable(var4)) {
                        throw DbException.get(90079, this.schemaName);
                     }

                     var1 = new DualTable(this.database);
                     break label79;
                  }
               }

               boolean var7 = this.readIf(105);
               if (var7 && this.readIf("INDEX")) {
                  this.readIdentifierWithSchema((String)null);
                  this.read(106);
                  var7 = false;
               }

               if (var7) {
                  Schema var8 = this.database.getMainSchema();
                  if (!this.equalsToken(var4, "SYSTEM_RANGE") && !this.equalsToken(var4, "GENERATE_SERIES")) {
                     var1 = new FunctionTable(var8, this.session, this.readTableFunction(var4, var6));
                  } else {
                     Expression var9 = this.readExpression();
                     this.read(109);
                     Expression var10 = this.readExpression();
                     if (this.readIf(109)) {
                        Expression var11 = this.readExpression();
                        this.read(106);
                        var1 = new RangeTable(var8, var9, var10, var11);
                     } else {
                        this.read(106);
                        var1 = new RangeTable(var8, var9, var10);
                     }
                  }
               } else {
                  var1 = this.readTableOrView(var4);
               }
            }
         }

         ArrayList var14 = null;
         IndexHints var16 = null;
         if (this.readIfUseIndex()) {
            var16 = this.parseIndexHints((Table)var1);
         } else {
            var2 = this.readFromAlias(var2);
            if (var2 != null) {
               var14 = this.readDerivedColumnNames();
               if (this.readIfUseIndex()) {
                  var16 = this.parseIndexHints((Table)var1);
               }
            }
         }

         return this.buildTableFilter((Table)var1, var2, var14, var16);
      }
   }

   private TableFilter readCorrelation(TableFilter var1) {
      String var2 = this.readFromAlias((String)null);
      if (var2 != null) {
         var1.setAlias(var2);
         ArrayList var3 = this.readDerivedColumnNames();
         if (var3 != null) {
            var1.setDerivedColumns(var3);
         }
      }

      return var1;
   }

   private TableFilter readDerivedTableWithCorrelation() {
      Query var1 = this.parseQueryExpression();
      this.read(106);
      ArrayList var4 = null;
      IndexHints var5 = null;
      Table var2;
      String var3;
      if (this.readIfUseIndex()) {
         var3 = this.session.getNextSystemIdentifier(this.sqlCommand);
         var2 = var1.toTable(var3, (Column[])null, this.parameters, this.createView != null, this.currentSelect);
         var5 = this.parseIndexHints(var2);
      } else {
         var3 = this.readFromAlias((String)null);
         if (var3 != null) {
            var4 = this.readDerivedColumnNames();
            Column[] var6 = null;
            if (var4 != null) {
               var1.init();
               var6 = (Column[])TableView.createQueryColumnTemplateList((String[])var4.toArray(new String[0]), var1, new String[1]).toArray(new Column[0]);
            }

            var2 = var1.toTable(var3, var6, this.parameters, this.createView != null, this.currentSelect);
            if (this.readIfUseIndex()) {
               var5 = this.parseIndexHints(var2);
            }
         } else {
            var3 = this.session.getNextSystemIdentifier(this.sqlCommand);
            var2 = var1.toTable(var3, (Column[])null, this.parameters, this.createView != null, this.currentSelect);
         }
      }

      return this.buildTableFilter(var2, var3, var4, var5);
   }

   private TableFilter buildTableFilter(Table var1, String var2, ArrayList<String> var3, IndexHints var4) {
      if (this.database.getMode().discardWithTableHints) {
         this.discardWithTableHints();
      }

      if (var2 == null && var1.isView() && var1.isTableExpression()) {
         var2 = var1.getName();
      }

      TableFilter var5 = new TableFilter(this.session, var1, var2, this.rightsChecked, this.currentSelect, this.orderInFrom++, var4);
      if (var3 != null) {
         var5.setDerivedColumns(var3);
      }

      return var5;
   }

   private Table readDataChangeDeltaTable(String var1, int var2) {
      this.read(105);
      int var3 = this.tokenIndex;
      DataChangeDeltaTable.ResultOption var5 = DataChangeDeltaTable.ResultOption.FINAL;
      Object var4;
      switch (var1) {
         case "OLD":
            var5 = DataChangeDeltaTable.ResultOption.OLD;
            if (this.readIf("UPDATE")) {
               var4 = this.parseUpdate(var3);
            } else if (this.readIf("DELETE")) {
               var4 = this.parseDelete(var3);
            } else if (this.readIf("MERGE")) {
               var4 = (DataChangeStatement)this.parseMerge(var3);
            } else {
               if (!this.database.getMode().replaceInto || !this.readIf("REPLACE")) {
                  throw this.getSyntaxError();
               }

               var4 = this.parseReplace(var3);
            }
            break;
         case "NEW":
            var5 = DataChangeDeltaTable.ResultOption.NEW;
         case "FINAL":
            if (this.readIf("INSERT")) {
               var4 = this.parseInsert(var3);
            } else if (this.readIf("UPDATE")) {
               var4 = this.parseUpdate(var3);
            } else if (this.readIf("MERGE")) {
               var4 = (DataChangeStatement)this.parseMerge(var3);
            } else {
               if (this.database.getMode().replaceInto && this.readIf("REPLACE")) {
                  var4 = this.parseReplace(var3);
                  break;
               }

               throw this.getSyntaxError();
            }
            break;
         default:
            this.setTokenIndex(var2);
            this.addExpected("OLD TABLE");
            this.addExpected("NEW TABLE");
            this.addExpected("FINAL TABLE");
            throw this.getSyntaxError();
      }

      this.read(106);
      if (this.currentSelect != null) {
         this.currentSelect.setNeverLazy(true);
      }

      return new DataChangeDeltaTable(this.getSchemaWithDefault(), this.session, (DataChangeStatement)var4, var5);
   }

   private TableFunction readTableFunction(String var1, Schema var2) {
      if (var2 == null) {
         switch (this.upperName(var1)) {
            case "UNNEST":
               return this.readUnnestFunction();
            case "TABLE_DISTINCT":
               return this.readTableFunction(2);
            case "CSVREAD":
               this.recompileAlways = true;
               return (TableFunction)this.readParameters(new CSVReadFunction());
            case "LINK_SCHEMA":
               this.recompileAlways = true;
               return (TableFunction)this.readParameters(new LinkSchemaFunction());
         }
      }

      FunctionAlias var5 = this.getFunctionAliasWithinPath(var1, var2);
      if (!var5.isDeterministic()) {
         this.recompileAlways = true;
      }

      ArrayList var6 = Utils.newSmallArrayList();
      if (!this.readIf(106)) {
         do {
            var6.add(this.readExpression());
         } while(this.readIfMore());
      }

      return new JavaTableFunction(var5, (Expression[])var6.toArray(new Expression[0]));
   }

   private boolean readIfUseIndex() {
      int var1 = this.tokenIndex;
      if (!this.readIf("USE")) {
         return false;
      } else if (!this.readIf("INDEX")) {
         this.setTokenIndex(var1);
         return false;
      } else {
         return true;
      }
   }

   private IndexHints parseIndexHints(Table var1) {
      this.read(105);
      LinkedHashSet var2 = new LinkedHashSet();
      if (!this.readIf(106)) {
         do {
            String var3 = this.readIdentifierWithSchema();
            Index var4 = var1.getIndex(var3);
            var2.add(var4.getName());
         } while(this.readIfMore());
      }

      return IndexHints.createUseIndexHints(var2);
   }

   private String readFromAlias(String var1) {
      if (this.readIf(7) || this.isIdentifier()) {
         var1 = this.readIdentifier();
      }

      return var1;
   }

   private ArrayList<String> readDerivedColumnNames() {
      if (!this.readIf(105)) {
         return null;
      } else {
         ArrayList var1 = new ArrayList();

         do {
            var1.add(this.readIdentifier());
         } while(this.readIfMore());

         return var1;
      }
   }

   private void discardWithTableHints() {
      if (this.readIf(89)) {
         this.read(105);

         do {
            this.discardTableHint();
         } while(this.readIfMore());
      }

   }

   private void discardTableHint() {
      if (this.readIf("INDEX")) {
         if (this.readIf(105)) {
            do {
               this.readExpression();
            } while(this.readIfMore());
         } else {
            this.read(95);
            this.readExpression();
         }
      } else {
         this.readExpression();
      }

   }

   private Prepared parseTruncate() {
      this.read(75);
      Table var1 = this.readTableOrView();
      boolean var2 = this.database.getMode().truncateTableRestartIdentity;
      if (this.readIf("CONTINUE")) {
         this.read("IDENTITY");
         var2 = false;
      } else if (this.readIf("RESTART")) {
         this.read("IDENTITY");
         var2 = true;
      }

      TruncateTable var3 = new TruncateTable(this.session);
      var3.setTable(var1);
      var3.setRestart(var2);
      return var3;
   }

   private boolean readIfExists(boolean var1) {
      if (this.readIf(40)) {
         this.read(30);
         var1 = true;
      }

      return var1;
   }

   private Prepared parseComment() {
      boolean var1 = false;
      this.read(60);
      boolean var2 = false;
      byte var8;
      if (!this.readIf(75) && !this.readIf("VIEW")) {
         if (this.readIf("COLUMN")) {
            var2 = true;
            var8 = 0;
         } else if (this.readIf("CONSTANT")) {
            var8 = 11;
         } else if (this.readIf(14)) {
            var8 = 5;
         } else if (this.readIf("ALIAS")) {
            var8 = 9;
         } else if (this.readIf("INDEX")) {
            var8 = 1;
         } else if (this.readIf("ROLE")) {
            var8 = 7;
         } else if (this.readIf("SCHEMA")) {
            var8 = 10;
         } else if (this.readIf("SEQUENCE")) {
            var8 = 3;
         } else if (this.readIf("TRIGGER")) {
            var8 = 4;
         } else if (this.readIf(82)) {
            var8 = 2;
         } else {
            if (!this.readIf("DOMAIN")) {
               throw this.getSyntaxError();
            }

            var8 = 12;
         }
      } else {
         var8 = 0;
      }

      SetComment var3 = new SetComment(this.session);
      String var4;
      if (var2) {
         var4 = this.readIdentifier();
         String var5 = null;
         this.read(110);
         boolean var6 = this.database.getMode().allowEmptySchemaValuesAsDefaultSchema;
         String var7 = var6 && this.currentTokenType == 110 ? null : this.readIdentifier();
         if (this.readIf(110)) {
            var5 = var4;
            var4 = var7;
            var7 = var6 && this.currentTokenType == 110 ? null : this.readIdentifier();
            if (this.readIf(110)) {
               this.checkDatabaseName(var5);
               var5 = var4;
               var4 = var7;
               var7 = this.readIdentifier();
            }
         }

         if (var7 == null || var4 == null) {
            throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "table.column");
         }

         this.schemaName = var5 != null ? var5 : this.session.getCurrentSchemaName();
         var3.setColumn(true);
         var3.setColumnName(var7);
      } else {
         var4 = this.readIdentifierWithSchema();
      }

      var3.setSchemaName(this.schemaName);
      var3.setObjectName(var4);
      var3.setObjectType(var8);
      this.read(45);
      var3.setCommentExpression(this.readExpression());
      return var3;
   }

   private Prepared parseDrop() {
      boolean var1;
      if (!this.readIf(75)) {
         String var6;
         if (this.readIf("INDEX")) {
            var1 = this.readIfExists(false);
            var6 = this.readIdentifierWithSchema();
            DropIndex var17 = new DropIndex(this.session, this.getSchema());
            var17.setIndexName(var6);
            var1 = this.readIfExists(var1);
            var17.setIfExists(var1);
            if (this.readIf(60)) {
               this.readIdentifierWithSchema();
            }

            return var17;
         } else if (this.readIf(82)) {
            var1 = this.readIfExists(false);
            DropUser var14 = new DropUser(this.session);
            var14.setUserName(this.readIdentifier());
            var1 = this.readIfExists(var1);
            this.readIf("CASCADE");
            var14.setIfExists(var1);
            return var14;
         } else if (this.readIf("SEQUENCE")) {
            var1 = this.readIfExists(false);
            var6 = this.readIdentifierWithSchema();
            DropSequence var16 = new DropSequence(this.session, this.getSchema());
            var16.setSequenceName(var6);
            var1 = this.readIfExists(var1);
            var16.setIfExists(var1);
            return var16;
         } else if (this.readIf("CONSTANT")) {
            var1 = this.readIfExists(false);
            var6 = this.readIdentifierWithSchema();
            DropConstant var15 = new DropConstant(this.session, this.getSchema());
            var15.setConstantName(var6);
            var1 = this.readIfExists(var1);
            var15.setIfExists(var1);
            return var15;
         } else if (this.readIf("TRIGGER")) {
            var1 = this.readIfExists(false);
            var6 = this.readIdentifierWithSchema();
            DropTrigger var13 = new DropTrigger(this.session, this.getSchema());
            var13.setTriggerName(var6);
            var1 = this.readIfExists(var1);
            var13.setIfExists(var1);
            return var13;
         } else if (this.readIf("VIEW")) {
            var1 = this.readIfExists(false);
            var6 = this.readIdentifierWithSchema();
            DropView var12 = new DropView(this.session, this.getSchema());
            var12.setViewName(var6);
            var1 = this.readIfExists(var1);
            var12.setIfExists(var1);
            ConstraintActionType var4 = this.parseCascadeOrRestrict();
            if (var4 != null) {
               var12.setDropAction(var4);
            }

            return var12;
         } else if (this.readIf("ROLE")) {
            var1 = this.readIfExists(false);
            DropRole var8 = new DropRole(this.session);
            var8.setRoleName(this.readIdentifier());
            var1 = this.readIfExists(var1);
            var8.setIfExists(var1);
            return var8;
         } else if (this.readIf("ALIAS")) {
            var1 = this.readIfExists(false);
            var6 = this.readIdentifierWithSchema();
            DropFunctionAlias var11 = new DropFunctionAlias(this.session, this.getSchema());
            var11.setAliasName(var6);
            var1 = this.readIfExists(var1);
            var11.setIfExists(var1);
            return var11;
         } else if (this.readIf("SCHEMA")) {
            var1 = this.readIfExists(false);
            DropSchema var7 = new DropSchema(this.session);
            var7.setSchemaName(this.readIdentifier());
            var1 = this.readIfExists(var1);
            var7.setIfExists(var1);
            ConstraintActionType var10 = this.parseCascadeOrRestrict();
            if (var10 != null) {
               var7.setDropAction(var10);
            }

            return var7;
         } else if (this.readIf(3)) {
            this.read("OBJECTS");
            DropDatabase var5 = new DropDatabase(this.session);
            var5.setDropAllObjects(true);
            if (this.readIf("DELETE")) {
               this.read("FILES");
               var5.setDeleteFiles(true);
            }

            return var5;
         } else if (!this.readIf("DOMAIN") && !this.readIf("TYPE") && !this.readIf("DATATYPE")) {
            if (this.readIf("AGGREGATE")) {
               return this.parseDropAggregate();
            } else if (this.readIf("SYNONYM")) {
               var1 = this.readIfExists(false);
               var6 = this.readIdentifierWithSchema();
               DropSynonym var9 = new DropSynonym(this.session, this.getSchema());
               var9.setSynonymName(var6);
               var1 = this.readIfExists(var1);
               var9.setIfExists(var1);
               return var9;
            } else {
               throw this.getSyntaxError();
            }
         } else {
            return this.parseDropDomain();
         }
      } else {
         var1 = this.readIfExists(false);
         DropTable var2 = new DropTable(this.session);

         do {
            String var3 = this.readIdentifierWithSchema();
            var2.addTable(this.getSchema(), var3);
         } while(this.readIf(109));

         var1 = this.readIfExists(var1);
         var2.setIfExists(var1);
         if (this.readIf("CASCADE")) {
            var2.setDropAction(ConstraintActionType.CASCADE);
            this.readIf("CONSTRAINTS");
         } else if (this.readIf("RESTRICT")) {
            var2.setDropAction(ConstraintActionType.RESTRICT);
         } else if (this.readIf("IGNORE")) {
            var2.setDropAction(ConstraintActionType.SET_DEFAULT);
         }

         return var2;
      }
   }

   private DropDomain parseDropDomain() {
      boolean var1 = this.readIfExists(false);
      String var2 = this.readIdentifierWithSchema();
      DropDomain var3 = new DropDomain(this.session, this.getSchema());
      var3.setDomainName(var2);
      var1 = this.readIfExists(var1);
      var3.setIfDomainExists(var1);
      ConstraintActionType var4 = this.parseCascadeOrRestrict();
      if (var4 != null) {
         var3.setDropAction(var4);
      }

      return var3;
   }

   private DropAggregate parseDropAggregate() {
      boolean var1 = this.readIfExists(false);
      String var2 = this.readIdentifierWithSchema();
      DropAggregate var3 = new DropAggregate(this.session, this.getSchema());
      var3.setName(var2);
      var1 = this.readIfExists(var1);
      var3.setIfExists(var1);
      return var3;
   }

   private TableFilter readTableReference() {
      TableFilter var1;
      TableFilter var2 = var1 = this.readTablePrimary();

      while(true) {
         TableFilter var3;
         Expression var4;
         switch (this.currentTokenType) {
            case 15:
               this.read();
               this.read(46);
               var3 = this.readTablePrimary();
               this.addJoin(var1, var3, false, (Expression)null);
               break;
            case 36:
               this.read();
               throw this.getSyntaxError();
            case 42:
               this.read();
               this.read(46);
               var3 = this.readTableReference();
               var4 = this.readJoinSpecification(var1, var3, false);
               this.addJoin(var1, var3, false, var4);
               break;
            case 46:
               this.read();
               var3 = this.readTableReference();
               var4 = this.readJoinSpecification(var1, var3, false);
               this.addJoin(var1, var3, false, var4);
               break;
            case 48:
               this.read();
               this.readIf("OUTER");
               this.read(46);
               var3 = this.readTableReference();
               var4 = this.readJoinSpecification(var1, var3, false);
               this.addJoin(var1, var3, true, var4);
               break;
            case 56:
               this.read();
               this.read(46);
               var3 = this.readTablePrimary();
               var4 = null;
               Column[] var5 = var2.getTable().getColumns();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  Column var8 = var5[var7];
                  Column var9 = var3.getColumn(var2.getColumnName(var8), true);
                  if (var9 != null) {
                     var4 = this.addJoinColumn(var4, var2, var3, var8, var9, false);
                  }
               }

               this.addJoin(var1, var3, false, var4);
               break;
            case 65:
               this.read();
               this.readIf("OUTER");
               this.read(46);
               var3 = this.readTableReference();
               var4 = this.readJoinSpecification(var1, var3, true);
               this.addJoin(var3, var1, true, var4);
               var1 = var3;
               break;
            default:
               if (this.expectedList != null) {
                  this.addMultipleExpected(65, 48, 42, 46, 15, 56);
               }

               return var1;
         }

         var2 = var3;
      }
   }

   private Expression readJoinSpecification(TableFilter var1, TableFilter var2, boolean var3) {
      Expression var4 = null;
      if (this.readIf(60)) {
         var4 = this.readExpression();
      } else if (this.readIf(83)) {
         this.read(105);

         do {
            String var5 = this.readIdentifier();
            var4 = this.addJoinColumn(var4, var1, var2, var1.getColumn(var5, false), var2.getColumn(var5, false), var3);
         } while(this.readIfMore());
      }

      return var4;
   }

   private Expression addJoinColumn(Expression var1, TableFilter var2, TableFilter var3, Column var4, Column var5, boolean var6) {
      if (var6) {
         var2.addCommonJoinColumns(var4, var5, var3);
         var3.addCommonJoinColumnToExclude(var5);
      } else {
         var2.addCommonJoinColumns(var4, var4, var2);
         var3.addCommonJoinColumnToExclude(var5);
      }

      ExpressionColumn var7 = new ExpressionColumn(this.database, var2.getSchemaName(), var2.getTableAlias(), var2.getColumnName(var4));
      ExpressionColumn var8 = new ExpressionColumn(this.database, var3.getSchemaName(), var3.getTableAlias(), var3.getColumnName(var5));
      Comparison var9 = new Comparison(0, var7, var8, false);
      Object var10;
      if (var1 == null) {
         var10 = var9;
      } else {
         var10 = new ConditionAndOr(0, var1, var9);
      }

      return (Expression)var10;
   }

   private void addJoin(TableFilter var1, TableFilter var2, boolean var3, Expression var4) {
      if (var2.getJoin() != null) {
         String var5 = "SYSTEM_JOIN_" + this.token.start();
         TableFilter var6 = new TableFilter(this.session, new DualTable(this.database), var5, this.rightsChecked, this.currentSelect, var2.getOrderInFrom(), (IndexHints)null);
         var6.setNestedJoin(var2);
         var2 = var6;
      }

      var1.addJoin(var2, var3, var4);
   }

   private Prepared parseExecutePostgre() {
      if (this.readIf("IMMEDIATE")) {
         return new ExecuteImmediate(this.session, this.readExpression());
      } else {
         ExecuteProcedure var1 = new ExecuteProcedure(this.session);
         String var2 = this.readIdentifier();
         Procedure var3 = this.session.getProcedure(var2);
         if (var3 == null) {
            throw DbException.get(90077, var2);
         } else {
            var1.setProcedure(var3);
            if (this.readIf(105)) {
               int var4 = 0;

               while(true) {
                  var1.setExpression(var4, this.readExpression());
                  if (!this.readIfMore()) {
                     break;
                  }

                  ++var4;
               }
            }

            return var1;
         }
      }
   }

   private Prepared parseExecuteSQLServer() {
      Call var1 = new Call(this.session);
      this.currentPrepared = var1;
      String var2 = null;
      String var3 = this.readIdentifier();
      if (this.readIf(110)) {
         var2 = var3;
         var3 = this.readIdentifier();
         if (this.readIf(110)) {
            this.checkDatabaseName(var2);
            var2 = var3;
            var3 = this.readIdentifier();
         }
      }

      FunctionAlias var4 = this.getFunctionAliasWithinPath(var3, var2 != null ? this.database.getSchema(var2) : null);
      ArrayList var6 = Utils.newSmallArrayList();
      if (this.currentTokenType != 115 && this.currentTokenType != 93) {
         do {
            var6.add(this.readExpression());
         } while(this.readIf(109));
      }

      Expression[] var5 = (Expression[])var6.toArray(new Expression[0]);
      var1.setExpression(new JavaFunction(var4, var5));
      return var1;
   }

   private FunctionAlias getFunctionAliasWithinPath(String var1, Schema var2) {
      UserDefinedFunction var3 = this.findUserDefinedFunctionWithinPath(var2, var1);
      if (var3 instanceof FunctionAlias) {
         return (FunctionAlias)var3;
      } else {
         throw DbException.get(90022, var1);
      }
   }

   private DeallocateProcedure parseDeallocate() {
      this.readIf("PLAN");
      DeallocateProcedure var1 = new DeallocateProcedure(this.session);
      var1.setProcedureName(this.readIdentifier());
      return var1;
   }

   private Explain parseExplain() {
      Explain var1 = new Explain(this.session);
      if (this.readIf("ANALYZE")) {
         var1.setExecuteCommand(true);
      } else if (this.readIf("PLAN")) {
         this.readIf(33);
      }

      switch (this.currentTokenType) {
         case 69:
         case 75:
         case 85:
         case 89:
         case 105:
            Query var2 = this.parseQuery();
            var2.setNeverLazy(true);
            var1.setCommand(var2);
            break;
         default:
            int var3 = this.tokenIndex;
            if (this.readIf("DELETE")) {
               var1.setCommand(this.parseDelete(var3));
            } else if (this.readIf("UPDATE")) {
               var1.setCommand(this.parseUpdate(var3));
            } else if (this.readIf("INSERT")) {
               var1.setCommand(this.parseInsert(var3));
            } else {
               if (!this.readIf("MERGE")) {
                  throw this.getSyntaxError();
               }

               var1.setCommand(this.parseMerge(var3));
            }
      }

      return var1;
   }

   private Query parseQuery() {
      int var1 = this.parameters.size();
      Query var2 = this.parseQueryExpression();
      int var3 = this.parameters.size();
      ArrayList var4 = new ArrayList(var3);

      for(int var5 = var1; var5 < var3; ++var5) {
         var4.add(this.parameters.get(var5));
      }

      var2.setParameterList(var4);
      var2.init();
      return var2;
   }

   private Prepared parseWithStatementOrQuery(int var1) {
      int var2 = this.parameters.size();
      Prepared var3 = this.parseWith();
      int var4 = this.parameters.size();
      ArrayList var5 = new ArrayList(var4);

      for(int var6 = var2; var6 < var4; ++var6) {
         var5.add(this.parameters.get(var6));
      }

      var3.setParameterList(var5);
      if (var3 instanceof Query) {
         Query var7 = (Query)var3;
         var7.init();
      }

      this.setSQL(var3, var1);
      return var3;
   }

   private Query parseQueryExpression() {
      Query var1;
      if (this.readIf(89)) {
         try {
            var1 = (Query)this.parseWith();
         } catch (ClassCastException var3) {
            throw DbException.get(42000, (String)"WITH statement supports only query in this context");
         }

         var1.setNeverLazy(true);
      } else {
         var1 = this.parseQueryExpressionBodyAndEndOfQuery();
      }

      return var1;
   }

   private Query parseQueryExpressionBodyAndEndOfQuery() {
      int var1 = this.tokenIndex;
      Query var2 = this.parseQueryExpressionBody();
      this.parseEndOfQuery(var2);
      this.setSQL(var2, var1);
      return var2;
   }

   private Query parseQueryExpressionBody() {
      Object var1 = this.parseQueryTerm();

      while(true) {
         SelectUnion.UnionType var2;
         if (this.readIf(79)) {
            if (this.readIf(3)) {
               var2 = SelectUnion.UnionType.UNION_ALL;
            } else {
               this.readIf(26);
               var2 = SelectUnion.UnionType.UNION;
            }
         } else {
            if (!this.readIf(29) && !this.readIf(53)) {
               return (Query)var1;
            }

            var2 = SelectUnion.UnionType.EXCEPT;
         }

         var1 = new SelectUnion(this.session, var2, (Query)var1, this.parseQueryTerm());
      }
   }

   private Query parseQueryTerm() {
      Object var1;
      for(var1 = this.parseQueryPrimary(); this.readIf(43); var1 = new SelectUnion(this.session, SelectUnion.UnionType.INTERSECT, (Query)var1, this.parseQueryPrimary())) {
      }

      return (Query)var1;
   }

   private void parseEndOfQuery(Query var1) {
      Select var2;
      if (this.readIf(62)) {
         this.read("BY");
         var2 = this.currentSelect;
         if (var1 instanceof Select) {
            this.currentSelect = (Select)var1;
         }

         ArrayList var3 = Utils.newSmallArrayList();

         do {
            boolean var4 = this.currentTokenType == 94;
            QueryOrderBy var5 = new QueryOrderBy();
            Expression var6 = this.readExpression();
            if (var4 && var6 instanceof ValueExpression && var6.getType().getValueType() == 11) {
               var5.columnIndexExpr = var6;
            } else if (var6 instanceof Parameter) {
               this.recompileAlways = true;
               var5.columnIndexExpr = var6;
            } else {
               var5.expression = var6;
            }

            var5.sortType = this.parseSortType();
            var3.add(var5);
         } while(this.readIf(109));

         var1.setOrder(var3);
         this.currentSelect = var2;
      }

      if (var1.getFetch() == null) {
         var2 = this.currentSelect;
         this.currentSelect = null;
         boolean var7 = false;
         if (this.readIf(59)) {
            var7 = true;
            var1.setOffset(this.readExpression().optimize(this.session));
            if (!this.readIf(66)) {
               this.readIf("ROWS");
            }
         }

         if (this.readIf(32)) {
            var7 = true;
            if (!this.readIf("FIRST")) {
               this.read("NEXT");
            }

            if (!this.readIf(66) && !this.readIf("ROWS")) {
               var1.setFetch(this.readExpression().optimize(this.session));
               if (this.readIf("PERCENT")) {
                  var1.setFetchPercent(true);
               }

               if (!this.readIf(66)) {
                  this.read("ROWS");
               }
            } else {
               var1.setFetch(ValueExpression.get(ValueInteger.get(1)));
            }

            if (this.readIf(89)) {
               this.read("TIES");
               var1.setWithTies(true);
            } else {
               this.read("ONLY");
            }
         }

         if (!var7 && this.database.getMode().limit && this.readIf(50)) {
            Expression var8 = this.readExpression().optimize(this.session);
            if (this.readIf(59)) {
               var1.setOffset(this.readExpression().optimize(this.session));
            } else if (this.readIf(109)) {
               Expression var9 = var8;
               var8 = this.readExpression().optimize(this.session);
               var1.setOffset(var9);
            }

            var1.setFetch(var8);
         }

         this.currentSelect = var2;
      }

      if (this.readIf(33)) {
         if (this.readIf("UPDATE")) {
            if (this.readIf("OF")) {
               do {
                  this.readIdentifierWithSchema();
               } while(this.readIf(109));
            } else if (this.readIf("NOWAIT")) {
            }

            var1.setForUpdate(true);
         } else if (this.readIf("READ") || this.readIf(32)) {
            this.read("ONLY");
         }
      }

      if (this.database.getMode().isolationLevelInSelectOrInsertStatement) {
         this.parseIsolationClause();
      }

   }

   private void parseIsolationClause() {
      if (this.readIf(89)) {
         if (!this.readIf("RR") && !this.readIf("RS")) {
            if (!this.readIf("CS") && this.readIf("UR")) {
            }
         } else if (this.readIf("USE")) {
            this.read(4);
            this.read("KEEP");
            if (!this.readIf("SHARE") && !this.readIf("UPDATE") && this.readIf("EXCLUSIVE")) {
            }

            this.read("LOCKS");
         }
      }

   }

   private Query parseQueryPrimary() {
      if (this.readIf(105)) {
         Query var2 = this.parseQueryExpressionBodyAndEndOfQuery();
         this.read(106);
         return var2;
      } else {
         int var1 = this.tokenIndex;
         if (this.readIf(69)) {
            return this.parseSelect(var1);
         } else if (this.readIf(75)) {
            return this.parseExplicitTable(var1);
         } else {
            this.read(85);
            return this.parseValues();
         }
      }
   }

   private void parseSelectFromPart(Select var1) {
      while(true) {
         TableFilter var2 = this.readTableReference();
         var1.addTableFilter(var2, true);
         boolean var3 = false;

         while(true) {
            TableFilter var4 = var2.getNestedJoin();
            if (var4 != null) {
               var4.visit((var1x) -> {
                  var1.addTableFilter(var1x, false);
               });
            }

            TableFilter var5 = var2.getJoin();
            if (var5 == null) {
               if (this.readIf(109)) {
                  break;
               }

               return;
            }

            var3 |= var5.isJoinOuter();
            if (var3) {
               var1.addTableFilter(var5, false);
            } else {
               Expression var6 = var5.getJoinCondition();
               if (var6 != null) {
                  var1.addCondition(var6);
               }

               var5.removeJoinCondition();
               var2.removeJoin();
               var1.addTableFilter(var5, true);
            }

            var2 = var5;
         }
      }
   }

   private void parseSelectExpressions(Select var1) {
      if (this.database.getMode().topInSelect && this.readIf("TOP")) {
         Select var2 = this.currentSelect;
         this.currentSelect = null;
         var1.setFetch(this.readTerm().optimize(this.session));
         if (this.readIf("PERCENT")) {
            var1.setFetchPercent(true);
         }

         if (this.readIf(89)) {
            this.read("TIES");
            var1.setWithTies(true);
         }

         this.currentSelect = var2;
      }

      ArrayList var4;
      if (this.readIf(26)) {
         if (this.readIf(60)) {
            this.read(105);
            var4 = Utils.newSmallArrayList();

            do {
               var4.add(this.readExpression());
            } while(this.readIfMore());

            var1.setDistinct((Expression[])var4.toArray(new Expression[0]));
         } else {
            var1.setDistinct();
         }
      } else {
         this.readIf(3);
      }

      var4 = Utils.newSmallArrayList();

      do {
         if (this.readIf(108)) {
            var4.add(this.parseWildcard((String)null, (String)null));
         } else {
            switch (this.currentTokenType) {
               case 32:
               case 35:
               case 37:
               case 38:
               case 59:
               case 62:
               case 64:
               case 87:
               case 88:
               case 93:
               case 106:
               case 115:
                  continue;
            }

            Object var3 = this.readExpression();
            if (this.readIf(7) || this.isIdentifier()) {
               var3 = new Alias((Expression)var3, this.readIdentifier(), this.database.getMode().aliasColumnName);
            }

            var4.add(var3);
         }
      } while(this.readIf(109));

      var1.setExpressions(var4);
   }

   private Select parseSelect(int var1) {
      Select var2 = new Select(this.session, this.currentSelect);
      Select var3 = this.currentSelect;
      Prepared var4 = this.currentPrepared;
      this.currentSelect = var2;
      this.currentPrepared = var2;
      this.parseSelectExpressions(var2);
      if (!this.readIf(35)) {
         TableFilter var5 = new TableFilter(this.session, new DualTable(this.database), (String)null, this.rightsChecked, this.currentSelect, 0, (IndexHints)null);
         var2.addTableFilter(var5, true);
      } else {
         this.parseSelectFromPart(var2);
      }

      if (this.readIf(87)) {
         var2.addCondition(this.readExpressionWithGlobalConditions());
      }

      this.currentSelect = var3;
      if (this.readIf(37)) {
         this.read("BY");
         var2.setGroupQuery();
         ArrayList var10 = Utils.newSmallArrayList();

         while(true) {
            if (this.isToken(105) && this.isOrdinaryGroupingSet()) {
               if (!this.readIf(106)) {
                  do {
                     var10.add(this.readExpression());
                  } while(this.readIfMore());
               }
            } else {
               Expression var6 = this.readExpression();
               if (this.database.getMode().groupByColumnIndex && var6 instanceof ValueExpression && var6.getType().getValueType() == 11) {
                  ArrayList var7 = var2.getExpressions();
                  Iterator var8 = var7.iterator();

                  while(var8.hasNext()) {
                     Expression var9 = (Expression)var8.next();
                     if (var9 instanceof Wildcard) {
                        throw this.getSyntaxError();
                     }
                  }

                  int var14 = var6.getValue(this.session).getInt();
                  if (var14 < 1 || var14 > var7.size()) {
                     throw DbException.get(90157, Integer.toString(var14), Integer.toString(var7.size()));
                  }

                  var10.add(var7.get(var14 - 1));
               } else {
                  var10.add(var6);
               }
            }

            if (!this.readIf(109)) {
               if (!var10.isEmpty()) {
                  var2.setGroupBy(var10);
               }
               break;
            }
         }
      }

      this.currentSelect = var2;
      if (this.readIf(38)) {
         var2.setGroupQuery();
         var2.setHaving(this.readExpressionWithGlobalConditions());
      }

      if (this.readIf(88)) {
         do {
            int var11 = this.token.start();
            String var12 = this.readIdentifier();
            this.read(7);
            Window var13 = this.readWindowSpecification();
            if (!this.currentSelect.addWindow(var12, var13)) {
               throw DbException.getSyntaxError(this.sqlCommand, var11, "unique identifier");
            }
         } while(this.readIf(109));
      }

      if (this.readIf(64)) {
         var2.setWindowQuery();
         var2.setQualify(this.readExpressionWithGlobalConditions());
      }

      var2.setParameterList(this.parameters);
      this.currentSelect = var3;
      this.currentPrepared = var4;
      this.setSQL(var2, var1);
      return var2;
   }

   private boolean isOrdinaryGroupingSet() {
      int var1 = this.scanToCloseParen(this.tokenIndex + 1);
      if (var1 < 0) {
         return false;
      } else {
         switch (((Token)this.tokens.get(var1)).tokenType()) {
            case 29:
            case 32:
            case 33:
            case 38:
            case 43:
            case 50:
            case 53:
            case 59:
            case 62:
            case 64:
            case 79:
            case 88:
            case 93:
            case 106:
            case 109:
            case 115:
               this.setTokenIndex(this.tokenIndex + 1);
               return true;
            default:
               return false;
         }
      }
   }

   private Query parseExplicitTable(int var1) {
      Table var2 = this.readTableOrView();
      Select var3 = new Select(this.session, this.currentSelect);
      TableFilter var4 = new TableFilter(this.session, var2, (String)null, this.rightsChecked, var3, this.orderInFrom++, (IndexHints)null);
      var3.addTableFilter(var4, true);
      var3.setExplicitTable();
      this.setSQL(var3, var1);
      return var3;
   }

   private void setSQL(Prepared var1, int var2) {
      String var3 = this.sqlCommand;
      int var4 = ((Token)this.tokens.get(var2)).start();

      int var5;
      for(var5 = this.token.start(); var4 < var5 && var3.charAt(var4) <= ' '; ++var4) {
      }

      while(var4 < var5 && var3.charAt(var5 - 1) <= ' ') {
         --var5;
      }

      var3 = var3.substring(var4, var5);
      ArrayList var6;
      int var8;
      if (var2 == 0 && this.currentTokenType == 93) {
         var6 = this.tokens;
         if (var4 != 0) {
            int var10 = 0;

            for(var8 = var6.size() - 1; var10 < var8; ++var10) {
               ((Token)var6.get(var10)).subtractFromStart(var4);
            }
         }

         this.token.setStart(var3.length());
         this.sqlCommand = var3;
      } else {
         List var7 = this.tokens.subList(var2, this.tokenIndex);
         var6 = new ArrayList(var7.size() + 1);

         for(var8 = var2; var8 < this.tokenIndex; ++var8) {
            Token var9 = ((Token)this.tokens.get(var8)).clone();
            var9.subtractFromStart(var4);
            var6.add(var9);
         }

         var6.add(new Token.EndOfInputToken(var3.length()));
      }

      var1.setSQL(var3, var6);
   }

   private Expression readExpressionOrDefault() {
      return (Expression)(this.readIf(25) ? ValueExpression.DEFAULT : this.readExpression());
   }

   private Expression readExpressionWithGlobalConditions() {
      Expression var1 = this.readCondition();
      if (this.readIf(4)) {
         var1 = this.readAnd(new ConditionAndOr(0, var1, this.readCondition()));
      } else if (this.readIf("_LOCAL_AND_GLOBAL_")) {
         var1 = this.readAnd(new ConditionLocalAndGlobal(var1, this.readCondition()));
      }

      return this.readExpressionPart2(var1);
   }

   private Expression readExpression() {
      return this.readExpressionPart2(this.readAnd(this.readCondition()));
   }

   private Expression readExpressionPart2(Expression var1) {
      if (!this.readIf(61)) {
         return var1;
      } else {
         Expression var2 = this.readAnd(this.readCondition());
         if (!this.readIf(61)) {
            return new ConditionAndOr(1, var1, var2);
         } else {
            ArrayList var3 = new ArrayList();
            var3.add(var1);
            var3.add(var2);

            do {
               var3.add(this.readAnd(this.readCondition()));
            } while(this.readIf(61));

            return new ConditionAndOrN(1, var3);
         }
      }
   }

   private Expression readAnd(Expression var1) {
      if (!this.readIf(4)) {
         return var1;
      } else {
         Expression var2 = this.readCondition();
         if (!this.readIf(4)) {
            return new ConditionAndOr(0, var1, var2);
         } else {
            ArrayList var3 = new ArrayList();
            var3.add(var1);
            var3.add(var2);

            do {
               var3.add(this.readCondition());
            } while(this.readIf(4));

            return new ConditionAndOrN(0, var3);
         }
      }
   }

   private Expression readCondition() {
      Query var1;
      switch (this.currentTokenType) {
         case 30:
            this.read();
            this.read(105);
            var1 = this.parseQuery();
            this.read(106);
            return new ExistsPredicate(var1);
         case 57:
            this.read();
            return new ConditionNot(this.readCondition());
         case 80:
            this.read();
            this.read(105);
            var1 = this.parseQuery();
            this.read(106);
            return new UniquePredicate(var1);
         default:
            int var5 = this.tokenIndex;
            Expression var2;
            if (this.readIf("INTERSECTS")) {
               if (this.readIf(105)) {
                  var2 = this.readConcat();
                  this.read(109);
                  Expression var6 = this.readConcat();
                  this.read(106);
                  return new Comparison(8, var2, var6, false);
               }

               this.setTokenIndex(var5);
            }

            if (this.expectedList != null) {
               this.addMultipleExpected(57, 30, 80);
               this.addExpected("INTERSECTS");
            }

            var2 = this.readConcat();

            do {
               int var3 = this.tokenIndex;
               boolean var4 = this.readIf(57);
               if (var4 && this.isToken(58)) {
                  this.setTokenIndex(var3);
                  break;
               }

               var2 = this.readConditionRightHandSide(var2, var4, false);
            } while(var2 != null);

            return var2;
      }
   }

   private Expression readConditionRightHandSide(Expression var1, boolean var2, boolean var3) {
      Object var6;
      if (!var2 && this.readIf(45)) {
         var6 = this.readConditionIs(var1, var3);
      } else {
         switch (this.currentTokenType) {
            case 10:
               this.read();
               boolean var4 = this.readIf(73);
               if (!var4) {
                  this.readIf(8);
               }

               Expression var5 = this.readConcat();
               this.read(4);
               var6 = new BetweenPredicate(var1, var2, var3, var4, var5, this.readConcat());
               break;
            case 41:
               this.read();
               var6 = this.readInPredicate(var1, var2, var3);
               break;
            case 49:
               this.read();
               var6 = this.readLikePredicate(var1, CompareLike.LikeType.LIKE, var2, var3);
               break;
            default:
               if (this.readIf("ILIKE")) {
                  var6 = this.readLikePredicate(var1, CompareLike.LikeType.ILIKE, var2, var3);
               } else if (this.readIf("REGEXP")) {
                  Expression var7 = this.readConcat();
                  this.recompileAlways = true;
                  var6 = new CompareLike(this.database, var1, var2, var3, var7, (Expression)null, CompareLike.LikeType.REGEXP);
               } else {
                  if (var2) {
                     if (var3) {
                        return null;
                     }

                     if (this.expectedList != null) {
                        this.addMultipleExpected(10, 41, 49);
                     }

                     throw this.getSyntaxError();
                  }

                  int var8 = getCompareType(this.currentTokenType);
                  if (var8 < 0) {
                     return null;
                  }

                  this.read();
                  var6 = this.readComparison(var1, var8, var3);
               }
         }
      }

      return (Expression)var6;
   }

   private Expression readConditionIs(Expression var1, boolean var2) {
      boolean var3 = this.readIf(57);
      Object var4;
      switch (this.currentTokenType) {
         case 26:
            this.read();
            this.read(35);
            var4 = this.readComparison(var1, var3 ? 6 : 7, var2);
            break;
         case 31:
            this.read();
            var4 = new BooleanTest(var1, var3, var2, false);
            break;
         case 58:
            this.read();
            var4 = new NullPredicate(var1, var3, var2);
            break;
         case 77:
            this.read();
            var4 = new BooleanTest(var1, var3, var2, true);
            break;
         case 81:
            this.read();
            var4 = new BooleanTest(var1, var3, var2, (Boolean)null);
            break;
         default:
            if (this.readIf("OF")) {
               var4 = this.readTypePredicate(var1, var3, var2);
            } else if (this.readIf("JSON")) {
               var4 = this.readJsonPredicate(var1, var3, var2);
            } else {
               if (this.expectedList != null) {
                  this.addMultipleExpected(58, 26, 77, 31, 81);
               }

               if (var2 || !this.session.isQuirksMode()) {
                  throw this.getSyntaxError();
               }

               var4 = new Comparison(var3 ? 7 : 6, var1, this.readConcat(), false);
            }
      }

      return (Expression)var4;
   }

   private TypePredicate readTypePredicate(Expression var1, boolean var2, boolean var3) {
      this.read(105);
      ArrayList var4 = Utils.newSmallArrayList();

      do {
         var4.add(this.parseDataType());
      } while(this.readIfMore());

      return new TypePredicate(var1, var2, var3, (TypeInfo[])var4.toArray(new TypeInfo[0]));
   }

   private Expression readInPredicate(Expression var1, boolean var2, boolean var3) {
      this.read(105);
      if (!var3 && this.database.getMode().allowEmptyInPredicate && this.readIf(106)) {
         return ValueExpression.getBoolean(var2);
      } else {
         ArrayList var4;
         if (this.isQuery()) {
            Query var5 = this.parseQuery();
            if (!this.readIfMore()) {
               return new ConditionInQuery(var1, var2, var3, var5, false, 0);
            }

            var4 = Utils.newSmallArrayList();
            var4.add(new Subquery(var5));
         } else {
            var4 = Utils.newSmallArrayList();
         }

         do {
            var4.add(this.readExpression());
         } while(this.readIfMore());

         return new ConditionIn(var1, var2, var3, var4);
      }
   }

   private IsJsonPredicate readJsonPredicate(Expression var1, boolean var2, boolean var3) {
      JSONItemType var4;
      if (this.readIf(84)) {
         var4 = JSONItemType.VALUE;
      } else if (this.readIf(6)) {
         var4 = JSONItemType.ARRAY;
      } else if (this.readIf("OBJECT")) {
         var4 = JSONItemType.OBJECT;
      } else if (this.readIf("SCALAR")) {
         var4 = JSONItemType.SCALAR;
      } else {
         var4 = JSONItemType.VALUE;
      }

      boolean var5 = false;
      if (this.readIf(89)) {
         this.read(80);
         this.readIf("KEYS");
         var5 = true;
      } else if (this.readIf("WITHOUT")) {
         this.read(80);
         this.readIf("KEYS");
      }

      return new IsJsonPredicate(var1, var2, var3, var5, var4);
   }

   private Expression readLikePredicate(Expression var1, CompareLike.LikeType var2, boolean var3, boolean var4) {
      Expression var5 = this.readConcat();
      Expression var6 = this.readIf("ESCAPE") ? this.readConcat() : null;
      this.recompileAlways = true;
      return new CompareLike(this.database, var1, var3, var4, var5, var6, var2);
   }

   private Expression readComparison(Expression var1, int var2, boolean var3) {
      int var4 = this.tokenIndex;
      Query var5;
      Object var6;
      if (this.readIf(3)) {
         this.read(105);
         if (this.isQuery()) {
            var5 = this.parseQuery();
            var6 = new ConditionInQuery(var1, false, var3, var5, true, var2);
            this.read(106);
         } else {
            this.setTokenIndex(var4);
            var6 = new Comparison(var2, var1, this.readConcat(), var3);
         }
      } else if (!this.readIf(5) && !this.readIf(72)) {
         var6 = new Comparison(var2, var1, this.readConcat(), var3);
      } else {
         this.read(105);
         if (this.currentTokenType == 92 && var2 == 0) {
            Parameter var7 = this.readParameter();
            var6 = new ConditionInParameter(var1, false, var3, var7);
            this.read(106);
         } else if (this.isQuery()) {
            var5 = this.parseQuery();
            var6 = new ConditionInQuery(var1, false, var3, var5, false, var2);
            this.read(106);
         } else {
            this.setTokenIndex(var4);
            var6 = new Comparison(var2, var1, this.readConcat(), var3);
         }
      }

      return (Expression)var6;
   }

   private Expression readConcat() {
      Object var1 = this.readSum();

      while(true) {
         switch (this.currentTokenType) {
            case 104:
               this.read();
               Expression var2 = this.readSum();
               if (!this.readIf(104)) {
                  var1 = new ConcatenationOperation((Expression)var1, var2);
                  break;
               }

               ConcatenationOperation var3 = new ConcatenationOperation();
               var3.addParameter((Expression)var1);
               var3.addParameter(var2);

               do {
                  var3.addParameter(this.readSum());
               } while(this.readIf(104));

               var3.doneWithParameters();
               var1 = var3;
               break;
            case 119:
               var1 = this.readTildeCondition((Expression)var1, false);
               break;
            case 122:
               var1 = this.readTildeCondition((Expression)var1, true);
               break;
            default:
               this.addExpected(104);
               return (Expression)var1;
         }
      }
   }

   private Expression readSum() {
      Object var1 = this.readFactor();

      while(true) {
         while(!this.readIf(103)) {
            if (!this.readIf(102)) {
               return (Expression)var1;
            }

            var1 = new BinaryOperation(BinaryOperation.OpType.MINUS, (Expression)var1, this.readFactor());
         }

         var1 = new BinaryOperation(BinaryOperation.OpType.PLUS, (Expression)var1, this.readFactor());
      }
   }

   private Expression readFactor() {
      Object var1 = this.readTerm();

      while(true) {
         while(!this.readIf(108)) {
            if (this.readIf(113)) {
               var1 = new BinaryOperation(BinaryOperation.OpType.DIVIDE, (Expression)var1, this.readTerm());
            } else {
               if (!this.readIf(114)) {
                  return (Expression)var1;
               }

               var1 = new MathFunction((Expression)var1, this.readTerm(), 1);
            }
         }

         var1 = new BinaryOperation(BinaryOperation.OpType.MULTIPLY, (Expression)var1, this.readTerm());
      }
   }

   private Expression readTildeCondition(Expression var1, boolean var2) {
      this.read();
      if (this.readIf(108)) {
         var1 = new CastSpecification((Expression)var1, TypeInfo.TYPE_VARCHAR_IGNORECASE);
      }

      return new CompareLike(this.database, (Expression)var1, var2, false, this.readSum(), (Expression)null, CompareLike.LikeType.REGEXP);
   }

   private Expression readAggregate(AggregateType var1, String var2) {
      if (this.currentSelect == null) {
         this.expectedList = null;
         throw this.getSyntaxError();
      } else {
         Aggregate var3;
         boolean var4;
         Expression var5;
         Expression var10;
         switch (var1) {
            case COUNT:
               if (this.readIf(108)) {
                  var3 = new Aggregate(AggregateType.COUNT_ALL, new Expression[0], this.currentSelect, false);
               } else {
                  var4 = this.readDistinctAgg();
                  var5 = this.readExpression();
                  if (var5 instanceof Wildcard && !var4) {
                     var3 = new Aggregate(AggregateType.COUNT_ALL, new Expression[0], this.currentSelect, false);
                  } else {
                     var3 = new Aggregate(AggregateType.COUNT, new Expression[]{var5}, this.currentSelect, var4);
                  }
               }
               break;
            case COVAR_POP:
            case COVAR_SAMP:
            case CORR:
            case REGR_SLOPE:
            case REGR_INTERCEPT:
            case REGR_COUNT:
            case REGR_R2:
            case REGR_AVGX:
            case REGR_AVGY:
            case REGR_SXX:
            case REGR_SYY:
            case REGR_SXY:
               var3 = new Aggregate(var1, new Expression[]{this.readExpression(), this.readNextArgument()}, this.currentSelect, false);
               break;
            case HISTOGRAM:
               var3 = new Aggregate(var1, new Expression[]{this.readExpression()}, this.currentSelect, false);
               break;
            case LISTAGG:
               var4 = this.readDistinctAgg();
               var5 = this.readExpression();
               ListaggArguments var13 = new ListaggArguments();
               ArrayList var14;
               if ("STRING_AGG".equals(var2)) {
                  this.read(109);
                  var13.setSeparator(this.readString());
                  var14 = this.readIfOrderBy();
               } else if ("GROUP_CONCAT".equals(var2)) {
                  var14 = this.readIfOrderBy();
                  if (this.readIf("SEPARATOR")) {
                     var13.setSeparator(this.readString());
                  }
               } else {
                  if (this.readIf(109)) {
                     var13.setSeparator(this.readString());
                  }

                  if (this.readIf(60)) {
                     this.read("OVERFLOW");
                     if (this.readIf("TRUNCATE")) {
                        var13.setOnOverflowTruncate(true);
                        if (this.currentTokenType == 94) {
                           var13.setFilter(this.readString());
                        }

                        if (!this.readIf(89)) {
                           this.read("WITHOUT");
                           var13.setWithoutCount(true);
                        }

                        this.read("COUNT");
                     } else {
                        this.read("ERROR");
                     }
                  }

                  var14 = null;
               }

               Expression[] var8 = new Expression[]{var5};
               int var9 = this.tokenIndex;
               this.read(106);
               if (var14 == null && this.isToken("WITHIN")) {
                  var3 = this.readWithinGroup(var1, var8, var4, var13, false, false);
               } else {
                  this.setTokenIndex(var9);
                  var3 = new Aggregate(AggregateType.LISTAGG, var8, this.currentSelect, var4);
                  var3.setExtraArguments(var13);
                  if (var14 != null) {
                     var3.setOrderByList(var14);
                  }
               }
               break;
            case ARRAY_AGG:
               var4 = this.readDistinctAgg();
               var3 = new Aggregate(AggregateType.ARRAY_AGG, new Expression[]{this.readExpression()}, this.currentSelect, var4);
               var3.setOrderByList(this.readIfOrderBy());
               break;
            case RANK:
            case DENSE_RANK:
            case PERCENT_RANK:
            case CUME_DIST:
               if (this.isToken(106)) {
                  return this.readWindowFunction(var2);
               }

               ArrayList var11 = Utils.newSmallArrayList();

               do {
                  var11.add(this.readExpression());
               } while(this.readIfMore());

               var3 = this.readWithinGroup(var1, (Expression[])var11.toArray(new Expression[0]), false, (Object)null, true, false);
               break;
            case PERCENTILE_CONT:
            case PERCENTILE_DISC:
               var10 = this.readExpression();
               this.read(106);
               var3 = this.readWithinGroup(var1, new Expression[]{var10}, false, (Object)null, false, true);
               break;
            case MODE:
               if (this.readIf(106)) {
                  var3 = this.readWithinGroup(AggregateType.MODE, new Expression[0], false, (Object)null, false, true);
               } else {
                  var10 = this.readExpression();
                  var3 = new Aggregate(AggregateType.MODE, new Expression[0], this.currentSelect, false);
                  if (this.readIf(62)) {
                     this.read("BY");
                     var5 = this.readExpression();
                     String var12 = var10.getSQL(0);
                     String var7 = var5.getSQL(0);
                     if (!var12.equals(var7)) {
                        throw DbException.getSyntaxError(42131, this.sqlCommand, this.token.start(), var12, var7);
                     }

                     this.readAggregateOrder(var3, var10, true);
                  } else {
                     this.readAggregateOrder(var3, var10, false);
                  }
               }
               break;
            case JSON_OBJECTAGG:
               var4 = this.readIf(47);
               var5 = this.readExpression();
               if (var4) {
                  this.read(84);
               } else if (!this.readIf(84)) {
                  this.read(116);
               }

               Expression var6 = this.readExpression();
               var3 = new Aggregate(AggregateType.JSON_OBJECTAGG, new Expression[]{var5, var6}, this.currentSelect, false);
               this.readJsonObjectFunctionFlags(var3, false);
               break;
            case JSON_ARRAYAGG:
               var4 = this.readDistinctAgg();
               var3 = new Aggregate(AggregateType.JSON_ARRAYAGG, new Expression[]{this.readExpression()}, this.currentSelect, var4);
               var3.setOrderByList(this.readIfOrderBy());
               var3.setFlags(1);
               this.readJsonObjectFunctionFlags(var3, true);
               break;
            default:
               var4 = this.readDistinctAgg();
               var3 = new Aggregate(var1, new Expression[]{this.readExpression()}, this.currentSelect, var4);
         }

         this.read(106);
         this.readFilterAndOver(var3);
         return var3;
      }
   }

   private Aggregate readWithinGroup(AggregateType var1, Expression[] var2, boolean var3, Object var4, boolean var5, boolean var6) {
      this.read("WITHIN");
      this.read(37);
      this.read(105);
      this.read(62);
      this.read("BY");
      Aggregate var7 = new Aggregate(var1, var2, this.currentSelect, var3);
      var7.setExtraArguments(var4);
      if (var5) {
         int var8 = var2.length;
         ArrayList var9 = new ArrayList(var8);

         for(int var10 = 0; var10 < var8; ++var10) {
            if (var10 > 0) {
               this.read(109);
            }

            var9.add(this.parseSortSpecification());
         }

         var7.setOrderByList(var9);
      } else if (var6) {
         this.readAggregateOrder(var7, this.readExpression(), true);
      } else {
         var7.setOrderByList(this.parseSortSpecificationList());
      }

      return var7;
   }

   private void readAggregateOrder(Aggregate var1, Expression var2, boolean var3) {
      ArrayList var4 = new ArrayList(1);
      QueryOrderBy var5 = new QueryOrderBy();
      var5.expression = var2;
      if (var3) {
         var5.sortType = this.parseSortType();
      }

      var4.add(var5);
      var1.setOrderByList(var4);
   }

   private ArrayList<QueryOrderBy> readIfOrderBy() {
      if (this.readIf(62)) {
         this.read("BY");
         return this.parseSortSpecificationList();
      } else {
         return null;
      }
   }

   private ArrayList<QueryOrderBy> parseSortSpecificationList() {
      ArrayList var1 = Utils.newSmallArrayList();

      do {
         var1.add(this.parseSortSpecification());
      } while(this.readIf(109));

      return var1;
   }

   private QueryOrderBy parseSortSpecification() {
      QueryOrderBy var1 = new QueryOrderBy();
      var1.expression = this.readExpression();
      var1.sortType = this.parseSortType();
      return var1;
   }

   private Expression readUserDefinedFunctionIf(Schema var1, String var2) {
      UserDefinedFunction var3 = this.findUserDefinedFunctionWithinPath(var1, var2);
      if (var3 == null) {
         return null;
      } else if (var3 instanceof FunctionAlias) {
         FunctionAlias var9 = (FunctionAlias)var3;
         ArrayList var10 = Utils.newSmallArrayList();
         if (!this.readIf(106)) {
            do {
               var10.add(this.readExpression());
            } while(this.readIfMore());
         }

         return new JavaFunction(var9, (Expression[])var10.toArray(new Expression[0]));
      } else {
         UserAggregate var4 = (UserAggregate)var3;
         boolean var5 = this.readDistinctAgg();
         ArrayList var6 = Utils.newSmallArrayList();

         do {
            var6.add(this.readExpression());
         } while(this.readIfMore());

         Expression[] var7 = (Expression[])var6.toArray(new Expression[0]);
         JavaAggregate var8 = new JavaAggregate(var4, var7, this.currentSelect, var5);
         this.readFilterAndOver(var8);
         return var8;
      }
   }

   private boolean readDistinctAgg() {
      if (this.readIf(26)) {
         return true;
      } else {
         this.readIf(3);
         return false;
      }
   }

   private void readFilterAndOver(AbstractAggregate var1) {
      if (this.readIf("FILTER")) {
         this.read(105);
         this.read(87);
         Expression var2 = this.readExpression();
         this.read(106);
         var1.setFilterCondition(var2);
      }

      this.readOver(var1);
   }

   private void readOver(DataAnalysisOperation var1) {
      if (this.readIf("OVER")) {
         var1.setOverCondition(this.readWindowNameOrSpecification());
         this.currentSelect.setWindowQuery();
      } else {
         if (!var1.isAggregate()) {
            throw this.getSyntaxError();
         }

         this.currentSelect.setGroupQuery();
      }

   }

   private Window readWindowNameOrSpecification() {
      return this.isToken(105) ? this.readWindowSpecification() : new Window(this.readIdentifier(), (ArrayList)null, (ArrayList)null, (WindowFrame)null);
   }

   private Window readWindowSpecification() {
      this.read(105);
      String var1 = null;
      if (this.currentTokenType == 2) {
         String var2 = this.currentToken;
         if (this.token.isQuoted() || !this.equalsToken(var2, "PARTITION") && !this.equalsToken(var2, "ROWS") && !this.equalsToken(var2, "RANGE") && !this.equalsToken(var2, "GROUPS")) {
            var1 = var2;
            this.read();
         }
      }

      ArrayList var5 = null;
      if (this.readIf("PARTITION")) {
         this.read("BY");
         var5 = Utils.newSmallArrayList();

         do {
            Expression var3 = this.readExpression();
            var5.add(var3);
         } while(this.readIf(109));
      }

      ArrayList var6 = this.readIfOrderBy();
      WindowFrame var4 = this.readWindowFrame();
      this.read(106);
      return new Window(var1, var5, var6, var4);
   }

   private WindowFrame readWindowFrame() {
      WindowFrameUnits var1;
      if (this.readIf("ROWS")) {
         var1 = WindowFrameUnits.ROWS;
      } else if (this.readIf("RANGE")) {
         var1 = WindowFrameUnits.RANGE;
      } else {
         if (!this.readIf("GROUPS")) {
            return null;
         }

         var1 = WindowFrameUnits.GROUPS;
      }

      WindowFrameBound var2;
      WindowFrameBound var3;
      if (this.readIf(10)) {
         var2 = this.readWindowFrameRange();
         this.read(4);
         var3 = this.readWindowFrameRange();
      } else {
         var2 = this.readWindowFrameStarting();
         var3 = null;
      }

      int var4 = this.token.start();
      WindowFrameExclusion var5 = WindowFrameExclusion.EXCLUDE_NO_OTHERS;
      if (this.readIf("EXCLUDE")) {
         if (this.readIf("CURRENT")) {
            this.read(66);
            var5 = WindowFrameExclusion.EXCLUDE_CURRENT_ROW;
         } else if (this.readIf(37)) {
            var5 = WindowFrameExclusion.EXCLUDE_GROUP;
         } else if (this.readIf("TIES")) {
            var5 = WindowFrameExclusion.EXCLUDE_TIES;
         } else {
            this.read("NO");
            this.read("OTHERS");
         }
      }

      WindowFrame var6 = new WindowFrame(var1, var2, var3, var5);
      if (!var6.isValid()) {
         throw DbException.getSyntaxError(this.sqlCommand, var4);
      } else {
         return var6;
      }
   }

   private WindowFrameBound readWindowFrameStarting() {
      if (this.readIf("UNBOUNDED")) {
         this.read("PRECEDING");
         return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_PRECEDING, (Expression)null);
      } else if (this.readIf("CURRENT")) {
         this.read(66);
         return new WindowFrameBound(WindowFrameBoundType.CURRENT_ROW, (Expression)null);
      } else {
         Expression var1 = this.readExpression();
         this.read("PRECEDING");
         return new WindowFrameBound(WindowFrameBoundType.PRECEDING, var1);
      }
   }

   private WindowFrameBound readWindowFrameRange() {
      if (this.readIf("UNBOUNDED")) {
         if (this.readIf("PRECEDING")) {
            return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_PRECEDING, (Expression)null);
         } else {
            this.read("FOLLOWING");
            return new WindowFrameBound(WindowFrameBoundType.UNBOUNDED_FOLLOWING, (Expression)null);
         }
      } else if (this.readIf("CURRENT")) {
         this.read(66);
         return new WindowFrameBound(WindowFrameBoundType.CURRENT_ROW, (Expression)null);
      } else {
         Expression var1 = this.readExpression();
         if (this.readIf("PRECEDING")) {
            return new WindowFrameBound(WindowFrameBoundType.PRECEDING, var1);
         } else {
            this.read("FOLLOWING");
            return new WindowFrameBound(WindowFrameBoundType.FOLLOWING, var1);
         }
      }
   }

   private Expression readFunction(Schema var1, String var2) {
      String var3 = this.upperName(var2);
      if (var1 != null) {
         return this.readFunctionWithSchema(var1, var2, var3);
      } else {
         boolean var4 = this.database.isAllowBuiltinAliasOverride();
         if (var4) {
            Expression var5 = this.readUserDefinedFunctionIf((Schema)null, var2);
            if (var5 != null) {
               return var5;
            }
         }

         AggregateType var7 = Aggregate.getAggregateType(var3);
         if (var7 != null) {
            return this.readAggregate(var7, var3);
         } else {
            Expression var6 = this.readBuiltinFunctionIf(var3);
            if (var6 != null) {
               return var6;
            } else {
               WindowFunction var8 = this.readWindowFunction(var3);
               if (var8 != null) {
                  return var8;
               } else {
                  var6 = this.readCompatibilityFunction(var3);
                  if (var6 != null) {
                     return var6;
                  } else {
                     if (!var4) {
                        var6 = this.readUserDefinedFunctionIf((Schema)null, var2);
                        if (var6 != null) {
                           return var6;
                        }
                     }

                     throw DbException.get(90022, var2);
                  }
               }
            }
         }
      }
   }

   private Expression readFunctionWithSchema(Schema var1, String var2, String var3) {
      if (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && var1.getName().equals(this.database.sysIdentifier("PG_CATALOG"))) {
         FunctionsPostgreSQL var4 = FunctionsPostgreSQL.getFunction(var3);
         if (var4 != null) {
            return (Expression)this.readParameters(var4);
         }
      }

      Expression var5 = this.readUserDefinedFunctionIf(var1, var2);
      if (var5 != null) {
         return var5;
      } else {
         throw DbException.get(90022, var2);
      }
   }

   private Expression readCompatibilityFunction(String var1) {
      Expression var4;
      switch (var1) {
         case "ARRAY_APPEND":
         case "ARRAY_CAT":
            return new ConcatenationOperation(this.readExpression(), this.readLastArgument());
         case "ARRAY_GET":
            return new ArrayElementReference(this.readExpression(), this.readLastArgument());
         case "ARRAY_LENGTH":
            return new CardinalityExpression(this.readSingleArgument(), false);
         case "DECODE":
            var4 = this.readExpression();
            boolean var12 = var4.isConstant() && !var4.getValue(this.session).containsNull();
            Expression var6 = this.readNextArgument();
            Expression var7 = this.readNextArgument();
            SimpleCase.SimpleWhen var8 = this.decodeToWhen(var4, var12, var6, var7);
            SimpleCase.SimpleWhen var9 = var8;

            Expression var10;
            SimpleCase.SimpleWhen var11;
            for(var10 = null; this.readIf(109); var9 = var11) {
               var6 = this.readExpression();
               if (!this.readIf(109)) {
                  var10 = var6;
                  break;
               }

               var7 = this.readExpression();
               var11 = this.decodeToWhen(var4, var12, var6, var7);
               var9.setWhen(var11);
            }

            this.read(106);
            return new SimpleCase(var4, var8, var10);
         case "CASEWHEN":
            return this.readCompatibilityCase(this.readExpression());
         case "NVL2":
            return this.readCompatibilityCase(new NullPredicate(this.readExpression(), true, false));
         case "CONVERT":
            Column var5;
            if (this.database.getMode().swapConvertFunctionParameters) {
               var5 = this.parseColumnWithType((String)null);
               var4 = this.readNextArgument();
            } else {
               var4 = this.readExpression();
               this.read(109);
               var5 = this.parseColumnWithType((String)null);
            }

            this.read(106);
            return new CastSpecification(var4, var5);
         case "IFNULL":
            return new CoalesceFunction(0, new Expression[]{this.readExpression(), this.readLastArgument()});
         case "NVL":
            return this.readCoalesceFunction(0);
         case "DATABASE":
            this.read(106);
            return new CurrentGeneralValueSpecification(0);
         case "CURDATE":
         case "SYSDATE":
         case "TODAY":
            return this.readCurrentDateTimeValueFunction(0, true, var1);
         case "SCHEMA":
            this.read(106);
            return new CurrentGeneralValueSpecification(3);
         case "SYSTIMESTAMP":
            return this.readCurrentDateTimeValueFunction(3, true, var1);
         case "DAY":
         case "DAY_OF_MONTH":
         case "DAYOFMONTH":
            return new DateTimeFunction(0, 2, this.readSingleArgument(), (Expression)null);
         case "DAY_OF_WEEK":
         case "DAYOFWEEK":
            return new DateTimeFunction(0, 20, this.readSingleArgument(), (Expression)null);
         case "DAY_OF_YEAR":
         case "DAYOFYEAR":
            return new DateTimeFunction(0, 16, this.readSingleArgument(), (Expression)null);
         case "HOUR":
            return new DateTimeFunction(0, 3, this.readSingleArgument(), (Expression)null);
         case "ISO_DAY_OF_WEEK":
            return new DateTimeFunction(0, 17, this.readSingleArgument(), (Expression)null);
         case "ISO_WEEK":
            return new DateTimeFunction(0, 18, this.readSingleArgument(), (Expression)null);
         case "ISO_YEAR":
            return new DateTimeFunction(0, 19, this.readSingleArgument(), (Expression)null);
         case "MINUTE":
            return new DateTimeFunction(0, 4, this.readSingleArgument(), (Expression)null);
         case "MONTH":
            return new DateTimeFunction(0, 1, this.readSingleArgument(), (Expression)null);
         case "QUARTER":
            return new DateTimeFunction(0, 12, this.readSingleArgument(), (Expression)null);
         case "SECOND":
            return new DateTimeFunction(0, 5, this.readSingleArgument(), (Expression)null);
         case "WEEK":
            return new DateTimeFunction(0, 21, this.readSingleArgument(), (Expression)null);
         case "YEAR":
            return new DateTimeFunction(0, 0, this.readSingleArgument(), (Expression)null);
         case "CURTIME":
            return this.readCurrentDateTimeValueFunction(2, true, "CURTIME");
         case "SYSTIME":
            this.read(106);
            return this.readCurrentDateTimeValueFunction(2, false, "SYSTIME");
         case "NOW":
            return this.readCurrentDateTimeValueFunction(4, true, "NOW");
         case "INSTR":
            var4 = this.readExpression();
            return new StringFunction(this.readNextArgument(), var4, this.readIfArgument(), 0);
         case "POSITION":
            var4 = this.readConcat();
            if (!this.readIf(109)) {
               this.read(41);
            }

            return new StringFunction(var4, this.readSingleArgument(), (Expression)null, 0);
         case "LCASE":
            return new StringFunction1(this.readSingleArgument(), 1);
         case "SUBSTR":
            return this.readSubstringFunction();
         case "LTRIM":
            return new TrimFunction(this.readSingleArgument(), (Expression)null, 1);
         case "RTRIM":
            return new TrimFunction(this.readSingleArgument(), (Expression)null, 2);
         case "UCASE":
            return new StringFunction1(this.readSingleArgument(), 0);
         case "CURRVAL":
            return this.readCompatibilitySequenceValueFunction(true);
         case "NEXTVAL":
            return this.readCompatibilitySequenceValueFunction(false);
         default:
            return null;
      }
   }

   private <T extends ExpressionWithVariableParameters> T readParameters(T var1) {
      if (!this.readIf(106)) {
         do {
            var1.addParameter(this.readExpression());
         } while(this.readIfMore());
      }

      var1.doneWithParameters();
      return var1;
   }

   private SimpleCase.SimpleWhen decodeToWhen(Expression var1, boolean var2, Expression var3, Expression var4) {
      if (!var2 && (!((Expression)var3).isConstant() || ((Expression)var3).getValue(this.session).containsNull())) {
         var3 = new Comparison(6, var1, (Expression)var3, true);
      }

      return new SimpleCase.SimpleWhen((Expression)var3, var4);
   }

   private Expression readCompatibilityCase(Expression var1) {
      return new SearchedCase(new Expression[]{var1, this.readNextArgument(), this.readLastArgument()});
   }

   private Expression readCompatibilitySequenceValueFunction(boolean var1) {
      Expression var2 = this.readExpression();
      Expression var3 = this.readIf(109) ? this.readExpression() : null;
      this.read(106);
      return new CompatibilitySequenceValueFunction(var2, var3, var1);
   }

   private Expression readBuiltinFunctionIf(String var1) {
      Expression var4;
      JsonConstructorFunction var7;
      switch (var1) {
         case "ABS":
            return new MathFunction(this.readSingleArgument(), (Expression)null, 0);
         case "MOD":
            return new MathFunction(this.readExpression(), this.readLastArgument(), 1);
         case "SIN":
            return new MathFunction1(this.readSingleArgument(), 0);
         case "COS":
            return new MathFunction1(this.readSingleArgument(), 1);
         case "TAN":
            return new MathFunction1(this.readSingleArgument(), 2);
         case "COT":
            return new MathFunction1(this.readSingleArgument(), 3);
         case "SINH":
            return new MathFunction1(this.readSingleArgument(), 4);
         case "COSH":
            return new MathFunction1(this.readSingleArgument(), 5);
         case "TANH":
            return new MathFunction1(this.readSingleArgument(), 6);
         case "ASIN":
            return new MathFunction1(this.readSingleArgument(), 7);
         case "ACOS":
            return new MathFunction1(this.readSingleArgument(), 8);
         case "ATAN":
            return new MathFunction1(this.readSingleArgument(), 9);
         case "ATAN2":
            return new MathFunction2(this.readExpression(), this.readLastArgument(), 0);
         case "LOG":
            var4 = this.readExpression();
            if (this.readIf(109)) {
               return new MathFunction2(var4, this.readSingleArgument(), 1);
            }

            this.read(106);
            return new MathFunction1(var4, this.database.getMode().logIsLogBase10 ? 10 : 11);
         case "LOG10":
            return new MathFunction1(this.readSingleArgument(), 10);
         case "LN":
            return new MathFunction1(this.readSingleArgument(), 11);
         case "EXP":
            return new MathFunction1(this.readSingleArgument(), 12);
         case "POWER":
            return new MathFunction2(this.readExpression(), this.readLastArgument(), 2);
         case "SQRT":
            return new MathFunction1(this.readSingleArgument(), 13);
         case "FLOOR":
            return new MathFunction(this.readSingleArgument(), (Expression)null, 2);
         case "CEIL":
         case "CEILING":
            return new MathFunction(this.readSingleArgument(), (Expression)null, 3);
         case "ROUND":
            return new MathFunction(this.readExpression(), this.readIfArgument(), 4);
         case "ROUNDMAGIC":
            return new MathFunction(this.readSingleArgument(), (Expression)null, 5);
         case "SIGN":
            return new MathFunction(this.readSingleArgument(), (Expression)null, 6);
         case "TRUNC":
         case "TRUNCATE":
            return new MathFunction(this.readExpression(), this.readIfArgument(), 7);
         case "DEGREES":
            return new MathFunction1(this.readSingleArgument(), 14);
         case "RADIANS":
            return new MathFunction1(this.readSingleArgument(), 15);
         case "BITAND":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 0);
         case "BITOR":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 1);
         case "BITXOR":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 2);
         case "BITNOT":
            return new BitFunction(this.readSingleArgument(), (Expression)null, 3);
         case "BITNAND":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 4);
         case "BITNOR":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 5);
         case "BITXNOR":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 6);
         case "BITGET":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 7);
         case "BITCOUNT":
            return new BitFunction(this.readSingleArgument(), (Expression)null, 8);
         case "LSHIFT":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 9);
         case "RSHIFT":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 10);
         case "ULSHIFT":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 11);
         case "URSHIFT":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 12);
         case "ROTATELEFT":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 13);
         case "ROTATERIGHT":
            return new BitFunction(this.readExpression(), this.readLastArgument(), 14);
         case "EXTRACT":
            int var8 = this.readDateTimeField();
            this.read(35);
            return new DateTimeFunction(0, var8, this.readSingleArgument(), (Expression)null);
         case "DATE_TRUNC":
            return new DateTimeFunction(1, this.readDateTimeField(), this.readLastArgument(), (Expression)null);
         case "DATEADD":
         case "TIMESTAMPADD":
            return new DateTimeFunction(2, this.readDateTimeField(), this.readNextArgument(), this.readLastArgument());
         case "DATEDIFF":
         case "TIMESTAMPDIFF":
            return new DateTimeFunction(3, this.readDateTimeField(), this.readNextArgument(), this.readLastArgument());
         case "FORMATDATETIME":
            return this.readDateTimeFormatFunction(0);
         case "PARSEDATETIME":
            return this.readDateTimeFormatFunction(1);
         case "DAYNAME":
            return new DayMonthNameFunction(this.readSingleArgument(), 0);
         case "MONTHNAME":
            return new DayMonthNameFunction(this.readSingleArgument(), 1);
         case "CARDINALITY":
            return new CardinalityExpression(this.readSingleArgument(), false);
         case "ARRAY_MAX_CARDINALITY":
            return new CardinalityExpression(this.readSingleArgument(), true);
         case "LOCATE":
            return new StringFunction(this.readExpression(), this.readNextArgument(), this.readIfArgument(), 0);
         case "INSERT":
            return new StringFunction(this.readExpression(), this.readNextArgument(), this.readNextArgument(), this.readLastArgument(), 1);
         case "REPLACE":
            return new StringFunction(this.readExpression(), this.readNextArgument(), this.readIfArgument(), 2);
         case "LPAD":
            return new StringFunction(this.readExpression(), this.readNextArgument(), this.readIfArgument(), 3);
         case "RPAD":
            return new StringFunction(this.readExpression(), this.readNextArgument(), this.readIfArgument(), 4);
         case "TRANSLATE":
            return new StringFunction(this.readExpression(), this.readNextArgument(), this.readLastArgument(), 5);
         case "UPPER":
            return new StringFunction1(this.readSingleArgument(), 0);
         case "LOWER":
            return new StringFunction1(this.readSingleArgument(), 1);
         case "ASCII":
            return new StringFunction1(this.readSingleArgument(), 2);
         case "CHAR":
         case "CHR":
            return new StringFunction1(this.readSingleArgument(), 3);
         case "STRINGENCODE":
            return new StringFunction1(this.readSingleArgument(), 4);
         case "STRINGDECODE":
            return new StringFunction1(this.readSingleArgument(), 5);
         case "STRINGTOUTF8":
            return new StringFunction1(this.readSingleArgument(), 6);
         case "UTF8TOSTRING":
            return new StringFunction1(this.readSingleArgument(), 7);
         case "HEXTORAW":
            return new StringFunction1(this.readSingleArgument(), 8);
         case "RAWTOHEX":
            return new StringFunction1(this.readSingleArgument(), 9);
         case "SPACE":
            return new StringFunction1(this.readSingleArgument(), 10);
         case "QUOTE_IDENT":
            return new StringFunction1(this.readSingleArgument(), 11);
         case "SUBSTRING":
            return this.readSubstringFunction();
         case "TO_CHAR":
            var4 = this.readExpression();
            Expression var6;
            Expression var9;
            if (this.readIf(109)) {
               var9 = this.readExpression();
               var6 = this.readIf(109) ? this.readExpression() : null;
            } else {
               var9 = null;
               var6 = null;
            }

            this.read(106);
            return new ToCharFunction(var4, var9, var6);
         case "REPEAT":
            return new StringFunction2(this.readExpression(), this.readLastArgument(), 2);
         case "CHAR_LENGTH":
         case "CHARACTER_LENGTH":
         case "LENGTH":
            return new LengthFunction(this.readIfSingleArgument(), 0);
         case "OCTET_LENGTH":
            return new LengthFunction(this.readIfSingleArgument(), 1);
         case "BIT_LENGTH":
            return new LengthFunction(this.readIfSingleArgument(), 2);
         case "TRIM":
            return this.readTrimFunction();
         case "REGEXP_LIKE":
            return (Expression)this.readParameters(new RegexpFunction(0));
         case "REGEXP_REPLACE":
            return (Expression)this.readParameters(new RegexpFunction(1));
         case "REGEXP_SUBSTR":
            return (Expression)this.readParameters(new RegexpFunction(2));
         case "XMLATTR":
            return (Expression)this.readParameters(new XMLFunction(0));
         case "XMLCDATA":
            return (Expression)this.readParameters(new XMLFunction(1));
         case "XMLCOMMENT":
            return (Expression)this.readParameters(new XMLFunction(2));
         case "XMLNODE":
            return (Expression)this.readParameters(new XMLFunction(3));
         case "XMLSTARTDOC":
            return (Expression)this.readParameters(new XMLFunction(4));
         case "XMLTEXT":
            return (Expression)this.readParameters(new XMLFunction(5));
         case "TRIM_ARRAY":
            return new ArrayFunction(this.readExpression(), this.readLastArgument(), (Expression)null, 0);
         case "ARRAY_CONTAINS":
            return new ArrayFunction(this.readExpression(), this.readLastArgument(), (Expression)null, 1);
         case "ARRAY_SLICE":
            return new ArrayFunction(this.readExpression(), this.readNextArgument(), this.readLastArgument(), 2);
         case "COMPRESS":
            return new CompressFunction(this.readExpression(), this.readIfArgument(), 0);
         case "EXPAND":
            return new CompressFunction(this.readSingleArgument(), (Expression)null, 1);
         case "SOUNDEX":
            return new SoundexFunction(this.readSingleArgument(), (Expression)null, 0);
         case "DIFFERENCE":
            return new SoundexFunction(this.readExpression(), this.readLastArgument(), 1);
         case "JSON_OBJECT":
            var7 = new JsonConstructorFunction(false);
            if (this.currentTokenType != 106 && !this.readJsonObjectFunctionFlags(var7, false)) {
               do {
                  boolean var5 = this.readIf(47);
                  var7.addParameter(this.readExpression());
                  if (var5) {
                     this.read(84);
                  } else if (!this.readIf(84)) {
                     this.read(116);
                  }

                  var7.addParameter(this.readExpression());
               } while(this.readIf(109));

               this.readJsonObjectFunctionFlags(var7, false);
            }

            this.read(106);
            var7.doneWithParameters();
            return var7;
         case "JSON_ARRAY":
            var7 = new JsonConstructorFunction(true);
            var7.setFlags(1);
            if (this.currentTokenType != 106 && !this.readJsonObjectFunctionFlags(var7, true)) {
               do {
                  var7.addParameter(this.readExpression());
               } while(this.readIf(109));

               this.readJsonObjectFunctionFlags(var7, true);
            }

            this.read(106);
            var7.doneWithParameters();
            return var7;
         case "ENCRYPT":
            return new CryptFunction(this.readExpression(), this.readNextArgument(), this.readLastArgument(), 0);
         case "DECRYPT":
            return new CryptFunction(this.readExpression(), this.readNextArgument(), this.readLastArgument(), 1);
         case "COALESCE":
            return this.readCoalesceFunction(0);
         case "GREATEST":
            return this.readCoalesceFunction(1);
         case "LEAST":
            return this.readCoalesceFunction(2);
         case "NULLIF":
            return new NullIfFunction(this.readExpression(), this.readLastArgument());
         case "CONCAT":
            return this.readConcatFunction(0);
         case "CONCAT_WS":
            return this.readConcatFunction(1);
         case "HASH":
            return new HashFunction(this.readExpression(), this.readNextArgument(), this.readIfArgument(), 0);
         case "ORA_HASH":
            var4 = this.readExpression();
            if (this.readIfMore()) {
               return new HashFunction(var4, this.readExpression(), this.readIfArgument(), 1);
            }

            return new HashFunction(var4, 1);
         case "RAND":
         case "RANDOM":
            return new RandFunction(this.readIfSingleArgument(), 0);
         case "SECURE_RAND":
            return new RandFunction(this.readSingleArgument(), 1);
         case "RANDOM_UUID":
         case "UUID":
            this.read(106);
            return new RandFunction((Expression)null, 2);
         case "ABORT_SESSION":
            return new SessionControlFunction(this.readIfSingleArgument(), 0);
         case "CANCEL_SESSION":
            return new SessionControlFunction(this.readIfSingleArgument(), 1);
         case "AUTOCOMMIT":
            this.read(106);
            return new SysInfoFunction(0);
         case "DATABASE_PATH":
            this.read(106);
            return new SysInfoFunction(1);
         case "H2VERSION":
            this.read(106);
            return new SysInfoFunction(2);
         case "LOCK_MODE":
            this.read(106);
            return new SysInfoFunction(3);
         case "LOCK_TIMEOUT":
            this.read(106);
            return new SysInfoFunction(4);
         case "MEMORY_FREE":
            this.read(106);
            return new SysInfoFunction(5);
         case "MEMORY_USED":
            this.read(106);
            return new SysInfoFunction(6);
         case "READONLY":
            this.read(106);
            return new SysInfoFunction(7);
         case "SESSION_ID":
            this.read(106);
            return new SysInfoFunction(8);
         case "TRANSACTION_ID":
            this.read(106);
            return new SysInfoFunction(9);
         case "DISK_SPACE_USED":
            return new TableInfoFunction(this.readIfSingleArgument(), (Expression)null, 0);
         case "ESTIMATED_ENVELOPE":
            return new TableInfoFunction(this.readExpression(), this.readLastArgument(), 1);
         case "FILE_READ":
            return new FileFunction(this.readExpression(), this.readIfArgument(), 0);
         case "FILE_WRITE":
            return new FileFunction(this.readExpression(), this.readLastArgument(), 1);
         case "DATA_TYPE_SQL":
            return new DataTypeSQLFunction(this.readExpression(), this.readNextArgument(), this.readNextArgument(), this.readLastArgument());
         case "DB_OBJECT_ID":
            return new DBObjectFunction(this.readExpression(), this.readNextArgument(), this.readIfArgument(), 0);
         case "DB_OBJECT_SQL":
            return new DBObjectFunction(this.readExpression(), this.readNextArgument(), this.readIfArgument(), 1);
         case "CSVWRITE":
            return (Expression)this.readParameters(new CSVWriteFunction());
         case "SIGNAL":
            return new SignalFunction(this.readExpression(), this.readLastArgument());
         case "TRUNCATE_VALUE":
            return new TruncateValueFunction(this.readExpression(), this.readNextArgument(), this.readLastArgument());
         case "ZERO":
            this.read(106);
            return ValueExpression.get(ValueInteger.get(0));
         case "PI":
            this.read(106);
            return ValueExpression.get(ValueDouble.get(Math.PI));
         default:
            ModeFunction var2 = ModeFunction.getFunction(this.database, var1);
            return var2 != null ? (Expression)this.readParameters(var2) : null;
      }
   }

   private Expression readDateTimeFormatFunction(int var1) {
      DateTimeFormatFunction var2 = new DateTimeFormatFunction(var1);
      var2.addParameter(this.readExpression());
      this.read(109);
      var2.addParameter(this.readExpression());
      if (this.readIf(109)) {
         var2.addParameter(this.readExpression());
         if (this.readIf(109)) {
            var2.addParameter(this.readExpression());
         }
      }

      this.read(106);
      var2.doneWithParameters();
      return var2;
   }

   private Expression readTrimFunction() {
      boolean var2 = false;
      byte var1;
      if (this.readIf("LEADING")) {
         var1 = 1;
         var2 = true;
      } else if (this.readIf("TRAILING")) {
         var1 = 2;
         var2 = true;
      } else {
         var2 = this.readIf("BOTH");
         var1 = 3;
      }

      Expression var4 = null;
      Expression var3;
      if (var2) {
         if (!this.readIf(35)) {
            var4 = this.readExpression();
            this.read(35);
         }

         var3 = this.readExpression();
      } else if (this.readIf(35)) {
         var3 = this.readExpression();
      } else {
         var3 = this.readExpression();
         if (this.readIf(35)) {
            var4 = var3;
            var3 = this.readExpression();
         } else if (this.readIf(109)) {
            var4 = this.readExpression();
         }
      }

      this.read(106);
      return new TrimFunction(var3, var4, var1);
   }

   private ArrayTableFunction readUnnestFunction() {
      ArrayTableFunction var1 = new ArrayTableFunction(0);
      ArrayList var2 = Utils.newSmallArrayList();
      if (!this.readIf(106)) {
         int var3 = 0;

         do {
            Expression var4 = this.readExpression();
            TypeInfo var5 = TypeInfo.TYPE_NULL;
            if (var4.isConstant()) {
               var4 = var4.optimize(this.session);
               TypeInfo var6 = var4.getType();
               if (var6.getValueType() == 40) {
                  var5 = (TypeInfo)var6.getExtTypeInfo();
               }
            }

            var1.addParameter(var4);
            StringBuilder var10003 = (new StringBuilder()).append("C");
            ++var3;
            var2.add(new Column(var10003.append(var3).toString(), var5));
         } while(this.readIfMore());
      }

      if (this.readIf(89)) {
         this.read("ORDINALITY");
         var2.add(new Column("NORD", TypeInfo.TYPE_INTEGER));
      }

      var1.setColumns(var2);
      var1.doneWithParameters();
      return var1;
   }

   private ArrayTableFunction readTableFunction(int var1) {
      ArrayTableFunction var2 = new ArrayTableFunction(var1);
      ArrayList var3 = Utils.newSmallArrayList();

      do {
         var3.add(this.parseColumnWithType(this.readIdentifier()));
         this.read(95);
         var2.addParameter(this.readExpression());
      } while(this.readIfMore());

      var2.setColumns(var3);
      var2.doneWithParameters();
      return var2;
   }

   private Expression readSingleArgument() {
      Expression var1 = this.readExpression();
      this.read(106);
      return var1;
   }

   private Expression readNextArgument() {
      this.read(109);
      return this.readExpression();
   }

   private Expression readLastArgument() {
      this.read(109);
      Expression var1 = this.readExpression();
      this.read(106);
      return var1;
   }

   private Expression readIfSingleArgument() {
      Expression var1;
      if (this.readIf(106)) {
         var1 = null;
      } else {
         var1 = this.readExpression();
         this.read(106);
      }

      return var1;
   }

   private Expression readIfArgument() {
      Expression var1 = this.readIf(109) ? this.readExpression() : null;
      this.read(106);
      return var1;
   }

   private Expression readCoalesceFunction(int var1) {
      CoalesceFunction var2 = new CoalesceFunction(var1);
      var2.addParameter(this.readExpression());

      while(this.readIfMore()) {
         var2.addParameter(this.readExpression());
      }

      var2.doneWithParameters();
      return var2;
   }

   private Expression readConcatFunction(int var1) {
      ConcatFunction var2 = new ConcatFunction(var1);
      var2.addParameter(this.readExpression());
      var2.addParameter(this.readNextArgument());
      if (var1 == 1) {
         var2.addParameter(this.readNextArgument());
      }

      while(this.readIfMore()) {
         var2.addParameter(this.readExpression());
      }

      var2.doneWithParameters();
      return var2;
   }

   private Expression readSubstringFunction() {
      SubstringFunction var1 = new SubstringFunction();
      var1.addParameter(this.readExpression());
      if (this.readIf(35)) {
         var1.addParameter(this.readExpression());
         if (this.readIf(33)) {
            var1.addParameter(this.readExpression());
         }
      } else if (this.readIf(33)) {
         var1.addParameter(ValueExpression.get(ValueInteger.get(1)));
         var1.addParameter(this.readExpression());
      } else {
         this.read(109);
         var1.addParameter(this.readExpression());
         if (this.readIf(109)) {
            var1.addParameter(this.readExpression());
         }
      }

      this.read(106);
      var1.doneWithParameters();
      return var1;
   }

   private int readDateTimeField() {
      int var1 = -1;
      switch (this.currentTokenType) {
         case 2:
            if (!this.token.isQuoted()) {
               var1 = DateTimeFunction.getField(this.currentToken);
            }
            break;
         case 24:
            var1 = 2;
            break;
         case 39:
            var1 = 3;
            break;
         case 54:
            var1 = 4;
            break;
         case 55:
            var1 = 1;
            break;
         case 68:
            var1 = 5;
            break;
         case 90:
            var1 = 0;
            break;
         case 94:
            if (this.token.value(this.session).getValueType() == 2) {
               var1 = DateTimeFunction.getField(this.token.value(this.session).getString());
            }
      }

      if (var1 < 0) {
         this.addExpected("date-time field");
         throw this.getSyntaxError();
      } else {
         this.read();
         return var1;
      }
   }

   private WindowFunction readWindowFunction(String var1) {
      WindowFunctionType var2 = WindowFunctionType.get(var1);
      if (var2 == null) {
         return null;
      } else if (this.currentSelect == null) {
         throw this.getSyntaxError();
      } else {
         int var3 = WindowFunction.getMinArgumentCount(var2);
         Expression[] var4 = null;
         if (var3 > 0) {
            int var5 = WindowFunction.getMaxArgumentCount(var2);
            var4 = new Expression[var5];
            int var6;
            if (var3 == var5) {
               for(var6 = 0; var6 < var3; ++var6) {
                  if (var6 > 0) {
                     this.read(109);
                  }

                  var4[var6] = this.readExpression();
               }
            } else {
               for(var6 = 0; var6 < var5 && (var6 <= 0 || this.readIf(109)); ++var6) {
                  var4[var6] = this.readExpression();
               }

               if (var6 < var3) {
                  throw this.getSyntaxError();
               }

               if (var6 != var5) {
                  var4 = (Expression[])Arrays.copyOf(var4, var6);
               }
            }
         }

         this.read(106);
         WindowFunction var7 = new WindowFunction(var2, this.currentSelect, var4);
         switch (var2) {
            case NTH_VALUE:
               this.readFromFirstOrLast(var7);
            case LEAD:
            case LAG:
            case FIRST_VALUE:
            case LAST_VALUE:
               this.readRespectOrIgnoreNulls(var7);
            default:
               this.readOver(var7);
               return var7;
         }
      }
   }

   private void readFromFirstOrLast(WindowFunction var1) {
      if (this.readIf(35) && !this.readIf("FIRST")) {
         this.read("LAST");
         var1.setFromLast(true);
      }

   }

   private void readRespectOrIgnoreNulls(WindowFunction var1) {
      if (this.readIf("RESPECT")) {
         this.read("NULLS");
      } else if (this.readIf("IGNORE")) {
         this.read("NULLS");
         var1.setIgnoreNulls(true);
      }

   }

   private boolean readJsonObjectFunctionFlags(ExpressionWithFlags var1, boolean var2) {
      int var3 = this.tokenIndex;
      boolean var4 = false;
      int var5 = var1.getFlags();
      if (this.readIf(58)) {
         if (!this.readIf(60)) {
            this.setTokenIndex(var3);
            return false;
         }

         this.read(58);
         var5 &= -2;
         var4 = true;
      } else if (this.readIf("ABSENT")) {
         if (!this.readIf(60)) {
            this.setTokenIndex(var3);
            return false;
         }

         this.read(58);
         var5 |= 1;
         var4 = true;
      }

      if (!var2) {
         if (this.readIf(89)) {
            this.read(80);
            this.read("KEYS");
            var5 |= 2;
            var4 = true;
         } else if (this.readIf("WITHOUT")) {
            if (!this.readIf(80)) {
               if (var4) {
                  throw this.getSyntaxError();
               }

               this.setTokenIndex(var3);
               return false;
            }

            this.read("KEYS");
            var5 &= -3;
            var4 = true;
         }
      }

      if (var4) {
         var1.setFlags(var5);
      }

      return var4;
   }

   private Expression readKeywordCompatibilityFunctionOrColumn() {
      boolean var1 = this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType);
      String var2 = this.currentToken;
      this.read();
      if (this.readIf(105)) {
         return this.readCompatibilityFunction(this.upperName(var2));
      } else if (var1) {
         return (Expression)(this.readIf(110) ? this.readTermObjectDot(var2) : new ExpressionColumn(this.database, (String)null, (String)null, var2));
      } else {
         throw this.getSyntaxError();
      }
   }

   private Expression readCurrentDateTimeValueFunction(int var1, boolean var2, String var3) {
      int var4 = -1;
      if (var2) {
         if (var1 != 0 && this.currentTokenType != 106) {
            var4 = this.readInt();
            if (var4 < 0 || var4 > 9) {
               throw DbException.get(90151, Integer.toString(var4), "0", "9");
            }
         }

         this.read(106);
      }

      if (this.database.isAllowBuiltinAliasOverride()) {
         FunctionAlias var5 = this.database.getSchema(this.session.getCurrentSchemaName()).findFunction(var3 != null ? var3 : CurrentDateTimeValueFunction.getName(var1));
         if (var5 != null) {
            return new JavaFunction(var5, var4 >= 0 ? new Expression[]{ValueExpression.get(ValueInteger.get(var4))} : new Expression[0]);
         }
      }

      return new CurrentDateTimeValueFunction(var1, var4);
   }

   private Expression readIfWildcardRowidOrSequencePseudoColumn(String var1, String var2) {
      if (this.readIf(108)) {
         return this.parseWildcard(var1, var2);
      } else if (this.readIf(91)) {
         return new ExpressionColumn(this.database, var1, var2);
      } else {
         return this.database.getMode().nextvalAndCurrvalPseudoColumns ? this.readIfSequencePseudoColumn(var1, var2) : null;
      }
   }

   private Wildcard parseWildcard(String var1, String var2) {
      Wildcard var3 = new Wildcard(var1, var2);
      if (this.readIf(29)) {
         this.read(105);
         ArrayList var4 = Utils.newSmallArrayList();

         do {
            String var5 = null;
            String var6 = null;
            String var7 = this.readIdentifier();
            if (this.readIf(110)) {
               var6 = var7;
               var7 = this.readIdentifier();
               if (this.readIf(110)) {
                  var5 = var6;
                  var6 = var7;
                  var7 = this.readIdentifier();
                  if (this.readIf(110)) {
                     this.checkDatabaseName(var5);
                     var5 = var6;
                     var6 = var7;
                     var7 = this.readIdentifier();
                  }
               }
            }

            var4.add(new ExpressionColumn(this.database, var5, var6, var7));
         } while(this.readIfMore());

         var3.setExceptColumns(var4);
      }

      return var3;
   }

   private SequenceValue readIfSequencePseudoColumn(String var1, String var2) {
      if (var1 == null) {
         var1 = this.session.getCurrentSchemaName();
      }

      Sequence var3;
      if (this.isToken("NEXTVAL")) {
         var3 = this.findSequence(var1, var2);
         if (var3 != null) {
            this.read();
            return new SequenceValue(var3, this.getCurrentPrepared());
         }
      } else if (this.isToken("CURRVAL")) {
         var3 = this.findSequence(var1, var2);
         if (var3 != null) {
            this.read();
            return new SequenceValue(var3);
         }
      }

      return null;
   }

   private Expression readTermObjectDot(String var1) {
      Expression var2 = this.readIfWildcardRowidOrSequencePseudoColumn((String)null, var1);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = this.readIdentifier();
         if (this.readIf(105)) {
            return this.readFunction(this.database.getSchema(var1), var3);
         } else if (this.readIf(110)) {
            String var4 = var1;
            var1 = var3;
            var2 = this.readIfWildcardRowidOrSequencePseudoColumn(var4, var3);
            if (var2 != null) {
               return var2;
            } else {
               var3 = this.readIdentifier();
               if (this.readIf(105)) {
                  this.checkDatabaseName(var4);
                  return this.readFunction(this.database.getSchema(var1), var3);
               } else {
                  if (this.readIf(110)) {
                     this.checkDatabaseName(var4);
                     var4 = var1;
                     var1 = var3;
                     var2 = this.readIfWildcardRowidOrSequencePseudoColumn(var4, var3);
                     if (var2 != null) {
                        return var2;
                     }

                     var3 = this.readIdentifier();
                  }

                  return new ExpressionColumn(this.database, var4, var1, var3);
               }
            }
         } else {
            return new ExpressionColumn(this.database, (String)null, var1, var3);
         }
      }
   }

   private void checkDatabaseName(String var1) {
      if (!this.database.getIgnoreCatalogs() && !this.equalsToken(this.database.getShortName(), var1)) {
         throw DbException.get(90013, var1);
      }
   }

   private Parameter readParameter() {
      int var1 = ((Token.ParameterToken)this.token).index();
      this.read();
      if (this.parameters == null) {
         this.parameters = Utils.newSmallArrayList();
      }

      if (var1 > 100000) {
         throw DbException.getInvalidValueException("parameter index", var1);
      } else {
         --var1;
         Parameter var2;
         if (this.parameters.size() <= var1) {
            this.parameters.ensureCapacity(var1 + 1);

            while(this.parameters.size() < var1) {
               this.parameters.add((Object)null);
            }

            var2 = new Parameter(var1);
            this.parameters.add(var2);
         } else if ((var2 = (Parameter)this.parameters.get(var1)) == null) {
            var2 = new Parameter(var1);
            this.parameters.set(var1, var2);
         }

         return var2;
      }
   }

   private Expression readTerm() {
      Object var1;
      ArrayList var2;
      switch (this.currentTokenType) {
         case 5:
         case 72:
            this.read();
            this.read(105);
            return this.readAggregate(AggregateType.ANY, "ANY");
         case 6:
            this.read();
            if (this.readIf(117)) {
               if (this.readIf(118)) {
                  var1 = ValueExpression.get(ValueArray.EMPTY);
               } else {
                  var2 = Utils.newSmallArrayList();

                  do {
                     var2.add(this.readExpression());
                  } while(this.readIf(109));

                  this.read(118);
                  var1 = new ExpressionList((Expression[])var2.toArray(new Expression[0]), true);
               }
            } else {
               this.read(105);
               Query var11 = this.parseQuery();
               this.read(106);
               var1 = new ArrayConstructorByQuery(var11);
            }
            break;
         case 11:
            this.read();
            var1 = this.readCase();
            break;
         case 12:
            this.read();
            this.read(105);
            Expression var9 = this.readExpression();
            this.read(7);
            Column var7 = this.parseColumnWithType((String)null);
            this.read(106);
            var1 = new CastSpecification(var9, var7);
            break;
         case 16:
            return this.readCurrentGeneralValueSpecification(0);
         case 17:
            this.read();
            var1 = this.readCurrentDateTimeValueFunction(0, this.readIf(105), (String)null);
            break;
         case 18:
            return this.readCurrentGeneralValueSpecification(1);
         case 19:
            return this.readCurrentGeneralValueSpecification(2);
         case 20:
            return this.readCurrentGeneralValueSpecification(3);
         case 21:
            this.read();
            var1 = this.readCurrentDateTimeValueFunction(1, this.readIf(105), (String)null);
            break;
         case 22:
            this.read();
            var1 = this.readCurrentDateTimeValueFunction(3, this.readIf(105), (String)null);
            break;
         case 23:
         case 82:
            return this.readCurrentGeneralValueSpecification(4);
         case 24:
         case 39:
         case 54:
         case 55:
         case 68:
         case 90:
            var1 = this.readKeywordCompatibilityFunctionOrColumn();
            break;
         case 31:
            this.read();
            var1 = ValueExpression.FALSE;
            break;
         case 44:
            this.read();
            var1 = this.readInterval();
            break;
         case 48:
            var1 = this.readColumnIfNotFunction();
            if (var1 == null) {
               var1 = new StringFunction2(this.readExpression(), this.readLastArgument(), 0);
            }
            break;
         case 51:
            this.read();
            var1 = this.readCurrentDateTimeValueFunction(2, this.readIf(105), (String)null);
            break;
         case 52:
            this.read();
            var1 = this.readCurrentDateTimeValueFunction(4, this.readIf(105), (String)null);
            break;
         case 58:
            this.read();
            var1 = ValueExpression.NULL;
            break;
         case 65:
            var1 = this.readColumnIfNotFunction();
            if (var1 == null) {
               var1 = new StringFunction2(this.readExpression(), this.readLastArgument(), 1);
            }
            break;
         case 66:
            this.read();
            this.read(105);
            if (this.readIf(106)) {
               var1 = ValueExpression.get(ValueRow.EMPTY);
            } else {
               var2 = Utils.newSmallArrayList();

               do {
                  var2.add(this.readExpression());
               } while(this.readIfMore());

               var1 = new ExpressionList((Expression[])var2.toArray(new Expression[0]), false);
            }
            break;
         case 67:
            this.read();
            if (this.readIf(105)) {
               this.read(106);
            }

            if (this.currentSelect == null && this.currentPrepared == null) {
               throw this.getSyntaxError();
            }

            var1 = new Rownum(this.getCurrentPrepared());
            break;
         case 69:
         case 75:
         case 89:
            var1 = new Subquery(this.parseQuery());
            break;
         case 70:
            return this.readCurrentGeneralValueSpecification(5);
         case 71:
            var1 = this.readColumnIfNotFunction();
            if (var1 == null) {
               var1 = this.readSetFunction();
            }
            break;
         case 74:
            return this.readCurrentGeneralValueSpecification(6);
         case 77:
            this.read();
            var1 = ValueExpression.TRUE;
            break;
         case 81:
            this.read();
            var1 = TypedValueExpression.UNKNOWN;
            break;
         case 84:
            if (this.parseDomainConstraint) {
               this.read();
               var1 = new DomainValueExpression();
               break;
            }
         case 3:
         case 4:
         case 7:
         case 8:
         case 9:
         case 10:
         case 13:
         case 14:
         case 15:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 40:
         case 41:
         case 42:
         case 43:
         case 45:
         case 46:
         case 47:
         case 49:
         case 50:
         case 53:
         case 56:
         case 57:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 73:
         case 76:
         case 78:
         case 79:
         case 80:
         case 83:
         case 86:
         case 87:
         case 88:
         case 93:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 104:
         default:
            if (!this.isIdentifier()) {
               throw this.getSyntaxError();
            }
         case 2:
            String var12 = this.currentToken;
            boolean var8 = this.token.isQuoted();
            this.read();
            if (this.readIf(105)) {
               var1 = this.readFunction((Schema)null, var12);
            } else if (this.readIf(110)) {
               var1 = this.readTermObjectDot(var12);
            } else if (var8) {
               var1 = new ExpressionColumn(this.database, (String)null, (String)null, var12);
            } else {
               var1 = this.readTermWithIdentifier(var12, var8);
            }
            break;
         case 85:
            if (this.database.getMode().onDuplicateKeyUpdate) {
               if (this.currentPrepared instanceof Insert) {
                  var1 = this.readOnDuplicateKeyValues(((Insert)this.currentPrepared).getTable(), (Update)null);
                  break;
               }

               if (this.currentPrepared instanceof Update) {
                  Update var6 = (Update)this.currentPrepared;
                  var1 = this.readOnDuplicateKeyValues(var6.getTable(), var6);
                  break;
               }
            }

            var1 = new Subquery(this.parseQuery());
            break;
         case 91:
            this.read();
            var1 = new ExpressionColumn(this.database, (String)null, (String)null);
            break;
         case 92:
            var1 = this.readParameter();
            break;
         case 94:
            var1 = ValueExpression.get(this.token.value(this.session));
            this.read();
            break;
         case 101:
            this.read();
            var1 = new Variable(this.session, this.readIdentifier());
            if (this.readIf(121)) {
               var1 = new SetFunction((Expression)var1, this.readExpression());
            }
            break;
         case 102:
            this.read();
            if (this.currentTokenType == 94) {
               var1 = ValueExpression.get(this.token.value(this.session).negate());
               int var5 = ((Expression)var1).getType().getValueType();
               if (var5 == 12 && ((Expression)var1).getValue(this.session).getLong() == -2147483648L) {
                  var1 = ValueExpression.get(ValueInteger.get(Integer.MIN_VALUE));
               } else if (var5 == 13 && ((Expression)var1).getValue(this.session).getBigDecimal().compareTo(Value.MIN_LONG_DECIMAL) == 0) {
                  var1 = ValueExpression.get(ValueBigint.MIN);
               }

               this.read();
            } else {
               var1 = new UnaryOperation(this.readTerm());
            }
            break;
         case 103:
            this.read();
            var1 = this.readTerm();
            break;
         case 105:
            this.read();
            if (this.readIf(106)) {
               var1 = ValueExpression.get(ValueRow.EMPTY);
            } else if (this.isQuery()) {
               var1 = new Subquery(this.parseQuery());
               this.read(106);
            } else {
               var1 = this.readExpression();
               if (!this.readIfMore()) {
                  if (var1 instanceof BinaryOperation) {
                     BinaryOperation var4 = (BinaryOperation)var1;
                     if (var4.getOperationType() == BinaryOperation.OpType.MINUS) {
                        TypeInfo var3 = this.readIntervalQualifier();
                        if (var3 != null) {
                           var4.setForcedType(var3);
                        }
                     }
                  }
               } else {
                  var2 = Utils.newSmallArrayList();
                  var2.add(var1);

                  do {
                     var2.add(this.readExpression());
                  } while(this.readIfMore());

                  var1 = new ExpressionList((Expression[])var2.toArray(new Expression[0]), false);
               }
            }

            if (this.readIf(110)) {
               var1 = new FieldReference((Expression)var1, this.readIdentifier());
            }
      }

      if (this.readIf(117)) {
         var1 = new ArrayElementReference((Expression)var1, this.readExpression());
         this.read(118);
      }

      if (this.readIf(120)) {
         label159: {
            if (this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL) {
               if (this.isToken("PG_CATALOG")) {
                  this.read("PG_CATALOG");
                  this.read(110);
               }

               if (this.readIf("REGCLASS")) {
                  var1 = new Regclass((Expression)var1);
                  break label159;
               }
            }

            var1 = new CastSpecification((Expression)var1, this.parseColumnWithType((String)null));
         }
      }

      while(true) {
         TypeInfo var13 = this.readIntervalQualifier();
         if (var13 != null) {
            var1 = new CastSpecification((Expression)var1, var13);
         }

         int var10 = this.tokenIndex;
         if (this.readIf("AT")) {
            if (this.readIf("TIME")) {
               this.read("ZONE");
               var1 = new TimeZoneOperation((Expression)var1, this.readExpression());
            } else {
               if (!this.readIf("LOCAL")) {
                  this.setTokenIndex(var10);
                  break;
               }

               var1 = new TimeZoneOperation((Expression)var1, (Expression)null);
            }
         } else {
            if (!this.readIf("FORMAT")) {
               break;
            }

            if (!this.readIf("JSON")) {
               this.setTokenIndex(var10);
               break;
            }

            var1 = new Format((Expression)var1, Format.FormatEnum.JSON);
         }
      }

      return (Expression)var1;
   }

   private Expression readCurrentGeneralValueSpecification(int var1) {
      this.read();
      if (this.readIf(105)) {
         this.read(106);
      }

      return new CurrentGeneralValueSpecification(var1);
   }

   private Expression readColumnIfNotFunction() {
      boolean var1 = this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType);
      String var2 = this.currentToken;
      this.read();
      if (this.readIf(105)) {
         return null;
      } else if (var1) {
         return (Expression)(this.readIf(110) ? this.readTermObjectDot(var2) : new ExpressionColumn(this.database, (String)null, (String)null, var2));
      } else {
         throw this.getSyntaxError();
      }
   }

   private Expression readSetFunction() {
      SetFunction var1 = new SetFunction(this.readExpression(), this.readLastArgument());
      if (this.database.isAllowBuiltinAliasOverride()) {
         FunctionAlias var2 = this.database.getSchema(this.session.getCurrentSchemaName()).findFunction(var1.getName());
         if (var2 != null) {
            return new JavaFunction(var2, new Expression[]{var1.getSubexpression(0), var1.getSubexpression(1)});
         }
      }

      return var1;
   }

   private Expression readOnDuplicateKeyValues(Table var1, Update var2) {
      this.read();
      this.read(105);
      Column var3 = this.readTableColumn(new TableFilter(this.session, var1, (String)null, this.rightsChecked, (Select)null, 0, (IndexHints)null));
      this.read(106);
      return new OnDuplicateKeyValues(var3, var2);
   }

   private Expression readTermWithIdentifier(String var1, boolean var2) {
      String var3;
      ValueExpression var6;
      int var7;
      switch (var1.charAt(0) & 65503) {
         case 67:
            if (this.equalsToken("CURRENT", var1)) {
               var7 = this.tokenIndex;
               if (this.readIf(84) && this.readIf(33)) {
                  return new SequenceValue(this.readSequence());
               }

               this.setTokenIndex(var7);
               if (this.database.getMode().getEnum() == Mode.ModeEnum.DB2) {
                  return this.parseDB2SpecialRegisters(var1);
               }
            }
            break;
         case 68:
            if (this.currentTokenType != 94 || this.token.value(this.session).getValueType() != 2 || !this.equalsToken("DATE", var1) && !this.equalsToken("D", var1)) {
               break;
            }

            var3 = this.token.value(this.session).getString();
            this.read();
            return ValueExpression.get(ValueDate.parse(var3));
         case 69:
            if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2 && this.equalsToken("E", var1)) {
               var3 = this.token.value(this.session).getString();
               var3 = StringUtils.replaceAll(var3, "\\\\", "\\");
               this.read();
               return ValueExpression.get(ValueVarchar.get(var3));
            }
         case 70:
         case 72:
         case 73:
         case 75:
         case 76:
         case 77:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         default:
            break;
         case 71:
            if (this.currentTokenType == 94) {
               var7 = this.token.value(this.session).getValueType();
               if (var7 == 2 && this.equalsToken("GEOMETRY", var1)) {
                  var6 = ValueExpression.get(ValueGeometry.get(this.token.value(this.session).getString()));
                  this.read();
                  return var6;
               }

               if (var7 == 6 && this.equalsToken("GEOMETRY", var1)) {
                  var6 = ValueExpression.get(ValueGeometry.getFromEWKB(this.token.value(this.session).getBytesNoCopy()));
                  this.read();
                  return var6;
               }
            }
            break;
         case 74:
            if (this.currentTokenType == 94) {
               var7 = this.token.value(this.session).getValueType();
               if (var7 == 2 && this.equalsToken("JSON", var1)) {
                  var6 = ValueExpression.get(ValueJson.fromJson(this.token.value(this.session).getString()));
                  this.read();
                  return var6;
               }

               if (var7 == 6 && this.equalsToken("JSON", var1)) {
                  var6 = ValueExpression.get(ValueJson.fromJson(this.token.value(this.session).getBytesNoCopy()));
                  this.read();
                  return var6;
               }
            }
            break;
         case 78:
            if (this.equalsToken("NEXT", var1)) {
               var7 = this.tokenIndex;
               if (this.readIf(84) && this.readIf(33)) {
                  return new SequenceValue(this.readSequence(), this.getCurrentPrepared());
               }

               this.setTokenIndex(var7);
            }
            break;
         case 84:
            String var4;
            boolean var5;
            if (this.equalsToken("TIME", var1)) {
               if (this.readIf(89)) {
                  this.read("TIME");
                  this.read("ZONE");
                  if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
                     var3 = this.token.value(this.session).getString();
                     this.read();
                     return ValueExpression.get(ValueTimeTimeZone.parse(var3));
                  }

                  throw this.getSyntaxError();
               }

               var5 = this.readIf("WITHOUT");
               if (var5) {
                  this.read("TIME");
                  this.read("ZONE");
               }

               if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
                  var4 = this.token.value(this.session).getString();
                  this.read();
                  return ValueExpression.get(ValueTime.parse(var4));
               }

               if (var5) {
                  throw this.getSyntaxError();
               }
            } else if (this.equalsToken("TIMESTAMP", var1)) {
               if (this.readIf(89)) {
                  this.read("TIME");
                  this.read("ZONE");
                  if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
                     var3 = this.token.value(this.session).getString();
                     this.read();
                     return ValueExpression.get(ValueTimestampTimeZone.parse(var3, this.session));
                  }

                  throw this.getSyntaxError();
               }

               var5 = this.readIf("WITHOUT");
               if (var5) {
                  this.read("TIME");
                  this.read("ZONE");
               }

               if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
                  var4 = this.token.value(this.session).getString();
                  this.read();
                  return ValueExpression.get(ValueTimestamp.parse(var4, this.session));
               }

               if (var5) {
                  throw this.getSyntaxError();
               }
            } else if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
               if (this.equalsToken("T", var1)) {
                  var3 = this.token.value(this.session).getString();
                  this.read();
                  return ValueExpression.get(ValueTime.parse(var3));
               }

               if (this.equalsToken("TS", var1)) {
                  var3 = this.token.value(this.session).getString();
                  this.read();
                  return ValueExpression.get(ValueTimestamp.parse(var3, this.session));
               }
            }
            break;
         case 85:
            if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2 && this.equalsToken("UUID", var1)) {
               var3 = this.token.value(this.session).getString();
               this.read();
               return ValueExpression.get(ValueUuid.get(var3));
            }
      }

      return new ExpressionColumn(this.database, (String)null, (String)null, var1, var2);
   }

   private Prepared getCurrentPrepared() {
      return this.currentPrepared;
   }

   private Expression readInterval() {
      boolean var1 = this.readIf(102);
      if (!var1) {
         this.readIf(103);
      }

      if (this.currentTokenType == 94 && this.token.value(this.session).getValueType() == 2) {
         String var2 = this.token.value(this.session).getString();
         this.read();
         IntervalQualifier var3;
         switch (this.currentTokenType) {
            case 24:
               this.read();
               if (this.readIf(76)) {
                  switch (this.currentTokenType) {
                     case 39:
                        var3 = IntervalQualifier.DAY_TO_HOUR;
                        break;
                     case 54:
                        var3 = IntervalQualifier.DAY_TO_MINUTE;
                        break;
                     case 68:
                        var3 = IntervalQualifier.DAY_TO_SECOND;
                        break;
                     default:
                        throw this.intervalDayError();
                  }

                  this.read();
               } else {
                  var3 = IntervalQualifier.DAY;
               }
               break;
            case 39:
               this.read();
               if (this.readIf(76)) {
                  switch (this.currentTokenType) {
                     case 54:
                        var3 = IntervalQualifier.HOUR_TO_MINUTE;
                        break;
                     case 68:
                        var3 = IntervalQualifier.HOUR_TO_SECOND;
                        break;
                     default:
                        throw this.intervalHourError();
                  }

                  this.read();
               } else {
                  var3 = IntervalQualifier.HOUR;
               }
               break;
            case 54:
               this.read();
               if (this.readIf(76)) {
                  this.read(68);
                  var3 = IntervalQualifier.MINUTE_TO_SECOND;
               } else {
                  var3 = IntervalQualifier.MINUTE;
               }
               break;
            case 55:
               this.read();
               var3 = IntervalQualifier.MONTH;
               break;
            case 68:
               this.read();
               var3 = IntervalQualifier.SECOND;
               break;
            case 90:
               this.read();
               if (this.readIf(76)) {
                  this.read(55);
                  var3 = IntervalQualifier.YEAR_TO_MONTH;
               } else {
                  var3 = IntervalQualifier.YEAR;
               }
               break;
            default:
               throw this.intervalQualifierError();
         }

         try {
            return ValueExpression.get(IntervalUtils.parseInterval(var3, var1, var2));
         } catch (Exception var5) {
            throw DbException.get(22007, var5, "INTERVAL", var2);
         }
      } else {
         this.addExpected("string");
         throw this.getSyntaxError();
      }
   }

   private Expression parseDB2SpecialRegisters(String var1) {
      if (this.readIf("TIMESTAMP")) {
         if (this.readIf(89)) {
            this.read("TIME");
            this.read("ZONE");
            return this.readCurrentDateTimeValueFunction(3, this.readIf(105), (String)null);
         } else {
            return this.readCurrentDateTimeValueFunction(4, this.readIf(105), (String)null);
         }
      } else if (this.readIf("TIME")) {
         return this.readCurrentDateTimeValueFunction(2, false, (String)null);
      } else {
         return (Expression)(this.readIf("DATE") ? this.readCurrentDateTimeValueFunction(0, false, (String)null) : new ExpressionColumn(this.database, (String)null, (String)null, var1));
      }
   }

   private Expression readCase() {
      Object var1;
      if (this.readIf(86)) {
         SearchedCase var2 = new SearchedCase();

         do {
            Expression var3 = this.readExpression();
            this.read("THEN");
            var2.addParameter(var3);
            var2.addParameter(this.readExpression());
         } while(this.readIf(86));

         if (this.readIf(27)) {
            var2.addParameter(this.readExpression());
         }

         var2.doneWithParameters();
         var1 = var2;
      } else {
         Expression var6 = this.readExpression();
         this.read(86);
         SimpleCase.SimpleWhen var7 = this.readSimpleWhenClause(var6);

         SimpleCase.SimpleWhen var5;
         for(SimpleCase.SimpleWhen var4 = var7; this.readIf(86); var4 = var5) {
            var5 = this.readSimpleWhenClause(var6);
            var4.setWhen(var5);
         }

         var1 = new SimpleCase(var6, var7, this.readIf(27) ? this.readExpression() : null);
      }

      this.read(28);
      return (Expression)var1;
   }

   private SimpleCase.SimpleWhen readSimpleWhenClause(Expression var1) {
      Expression var2 = this.readWhenOperand(var1);
      if (!this.readIf(109)) {
         this.read("THEN");
         return new SimpleCase.SimpleWhen(var2, this.readExpression());
      } else {
         ArrayList var3 = Utils.newSmallArrayList();
         var3.add(var2);

         do {
            var3.add(this.readWhenOperand(var1));
         } while(this.readIf(109));

         this.read("THEN");
         return new SimpleCase.SimpleWhen((Expression[])var3.toArray(new Expression[0]), this.readExpression());
      }
   }

   private Expression readWhenOperand(Expression var1) {
      int var2 = this.tokenIndex;
      boolean var3 = this.readIf(57);
      Expression var4 = this.readConditionRightHandSide(var1, var3, true);
      if (var4 == null) {
         if (var3) {
            this.setTokenIndex(var2);
         }

         var4 = this.readExpression();
      }

      return var4;
   }

   private int readNonNegativeInt() {
      int var1 = this.readInt();
      if (var1 < 0) {
         throw DbException.getInvalidValueException("non-negative integer", var1);
      } else {
         return var1;
      }
   }

   private int readInt() {
      boolean var1 = false;
      if (this.currentTokenType == 102) {
         var1 = true;
         this.read();
      } else if (this.currentTokenType == 103) {
         this.read();
      }

      if (this.currentTokenType != 94) {
         throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "integer");
      } else {
         Value var2 = this.token.value(this.session);
         if (var1) {
            var2 = var2.negate();
         }

         int var3 = var2.getInt();
         this.read();
         return var3;
      }
   }

   private long readPositiveLong() {
      long var1 = this.readLong();
      if (var1 <= 0L) {
         throw DbException.getInvalidValueException("positive long", var1);
      } else {
         return var1;
      }
   }

   private long readLong() {
      boolean var1 = false;
      if (this.currentTokenType == 102) {
         var1 = true;
         this.read();
      } else if (this.currentTokenType == 103) {
         this.read();
      }

      if (this.currentTokenType != 94) {
         throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "long");
      } else {
         Value var2 = this.token.value(this.session);
         if (var1) {
            var2 = var2.negate();
         }

         long var3 = var2.getLong();
         this.read();
         return var3;
      }
   }

   private boolean readBooleanSetting() {
      switch (this.currentTokenType) {
         case 31:
            this.read();
            return false;
         case 60:
         case 77:
            this.read();
            return true;
         case 94:
            boolean var1 = this.token.value(this.session).getBoolean();
            this.read();
            return var1;
         default:
            if (this.readIf("OFF")) {
               return false;
            } else {
               if (this.expectedList != null) {
                  this.addMultipleExpected(60, 77, 31);
               }

               throw this.getSyntaxError();
            }
      }
   }

   private String readString() {
      int var1 = this.token.start();
      Expression var2 = this.readExpression();

      try {
         String var3 = var2.optimize(this.session).getValue(this.session).getString();
         if (var3 == null || var3.length() <= 1048576) {
            return var3;
         }
      } catch (DbException var4) {
      }

      throw DbException.getSyntaxError(this.sqlCommand, var1, "character string");
   }

   private String readIdentifierWithSchema(String var1) {
      String var2 = this.readIdentifier();
      this.schemaName = var1;
      if (this.readIf(110)) {
         var2 = this.readIdentifierWithSchema2(var2);
      }

      return var2;
   }

   private String readIdentifierWithSchema2(String var1) {
      this.schemaName = var1;
      if (this.database.getMode().allowEmptySchemaValuesAsDefaultSchema && this.readIf(110)) {
         if (this.equalsToken(this.schemaName, this.database.getShortName()) || this.database.getIgnoreCatalogs()) {
            this.schemaName = this.session.getCurrentSchemaName();
            var1 = this.readIdentifier();
         }
      } else {
         var1 = this.readIdentifier();
         if (this.currentTokenType == 110 && (this.equalsToken(this.schemaName, this.database.getShortName()) || this.database.getIgnoreCatalogs())) {
            this.read();
            this.schemaName = var1;
            var1 = this.readIdentifier();
         }
      }

      return var1;
   }

   private String readIdentifierWithSchema() {
      return this.readIdentifierWithSchema(this.session.getCurrentSchemaName());
   }

   private String readIdentifier() {
      if (this.isIdentifier() || this.session.isQuirksMode() && isKeyword(this.currentTokenType)) {
         String var1 = this.currentToken;
         this.read();
         return var1;
      } else {
         throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "identifier");
      }
   }

   private void read(String var1) {
      if (!this.token.isQuoted() && this.equalsToken(var1, this.currentToken)) {
         this.read();
      } else {
         this.addExpected(var1);
         throw this.getSyntaxError();
      }
   }

   private void read(int var1) {
      if (var1 != this.currentTokenType) {
         this.addExpected(var1);
         throw this.getSyntaxError();
      } else {
         this.read();
      }
   }

   private boolean readIf(String var1) {
      if (!this.token.isQuoted() && this.equalsToken(var1, this.currentToken)) {
         this.read();
         return true;
      } else {
         this.addExpected(var1);
         return false;
      }
   }

   private boolean readIf(int var1) {
      if (var1 == this.currentTokenType) {
         this.read();
         return true;
      } else {
         this.addExpected(var1);
         return false;
      }
   }

   private boolean isToken(String var1) {
      if (!this.token.isQuoted() && this.equalsToken(var1, this.currentToken)) {
         return true;
      } else {
         this.addExpected(var1);
         return false;
      }
   }

   private boolean isToken(int var1) {
      if (var1 == this.currentTokenType) {
         return true;
      } else {
         this.addExpected(var1);
         return false;
      }
   }

   private boolean equalsToken(String var1, String var2) {
      if (var1 == null) {
         return var2 == null;
      } else {
         return var1.equals(var2) || !this.identifiersToUpper && var1.equalsIgnoreCase(var2);
      }
   }

   private boolean isIdentifier() {
      return this.currentTokenType == 2 || this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType);
   }

   private void addExpected(String var1) {
      if (this.expectedList != null) {
         this.expectedList.add(var1);
      }

   }

   private void addExpected(int var1) {
      if (this.expectedList != null) {
         this.expectedList.add(Token.TOKENS[var1]);
      }

   }

   private void addMultipleExpected(int... var1) {
      int[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         this.expectedList.add(Token.TOKENS[var5]);
      }

   }

   private void read() {
      if (this.expectedList != null) {
         this.expectedList.clear();
      }

      int var1 = this.tokens.size();
      if (this.tokenIndex + 1 < var1) {
         this.token = (Token)this.tokens.get(++this.tokenIndex);
         this.currentTokenType = this.token.tokenType();
         this.currentToken = this.token.asIdentifier();
         if (this.currentToken != null && this.currentToken.length() > 256) {
            throw DbException.get(42622, (String[])(this.currentToken.substring(0, 32), "256"));
         } else {
            if (this.currentTokenType == 94) {
               this.checkLiterals();
            }

         }
      } else {
         throw this.getSyntaxError();
      }
   }

   private void checkLiterals() {
      if (!this.literalsChecked && this.session != null && !this.session.getAllowLiterals()) {
         int var1 = this.database.getAllowLiterals();
         if (var1 == 0 || (this.token instanceof Token.CharacterStringToken || this.token instanceof Token.BinaryStringToken) && var1 != 2) {
            throw DbException.get(90116);
         }
      }

   }

   private void initialize(String var1, ArrayList<Token> var2, boolean var3) {
      if (var1 == null) {
         var1 = "";
      }

      this.sqlCommand = var1;
      this.tokens = var2 == null ? (new Tokenizer(this.database, this.identifiersToUpper, this.identifiersToLower, this.nonKeywords)).tokenize(var1, var3) : var2;
      this.resetTokenIndex();
   }

   private void resetTokenIndex() {
      this.tokenIndex = -1;
      this.token = null;
      this.currentTokenType = -1;
      this.currentToken = null;
   }

   void setTokenIndex(int var1) {
      if (var1 != this.tokenIndex) {
         if (this.expectedList != null) {
            this.expectedList.clear();
         }

         this.token = (Token)this.tokens.get(var1);
         this.tokenIndex = var1;
         this.currentTokenType = this.token.tokenType();
         this.currentToken = this.token.asIdentifier();
      }

   }

   private static boolean isKeyword(int var0) {
      return var0 >= 3 && var0 <= 91;
   }

   private boolean isKeyword(String var1) {
      return ParserUtil.isKeyword(var1, !this.identifiersToUpper);
   }

   private String upperName(String var1) {
      return this.identifiersToUpper ? var1 : StringUtils.toUpperEnglish(var1);
   }

   private Column parseColumnForTable(String var1, boolean var2) {
      Mode var4 = this.database.getMode();
      Column var3;
      if (var4.identityDataType && this.readIf("IDENTITY")) {
         var3 = new Column(var1, TypeInfo.TYPE_BIGINT);
         this.parseCompatibilityIdentityOptions(var3);
         var3.setPrimaryKey(true);
      } else if (var4.serialDataTypes && this.readIf("BIGSERIAL")) {
         var3 = new Column(var1, TypeInfo.TYPE_BIGINT);
         var3.setIdentityOptions(new SequenceOptions(), false);
      } else if (var4.serialDataTypes && this.readIf("SERIAL")) {
         var3 = new Column(var1, TypeInfo.TYPE_INTEGER);
         var3.setIdentityOptions(new SequenceOptions(), false);
      } else {
         var3 = this.parseColumnWithType(var1);
      }

      if (this.readIf("INVISIBLE")) {
         var3.setVisible(false);
      } else if (this.readIf("VISIBLE")) {
         var3.setVisible(true);
      }

      boolean var5 = false;
      NullConstraintType var6 = this.parseNotNullConstraint();
      if (!var3.isIdentity()) {
         label130: {
            if (this.readIf(7)) {
               var3.setGeneratedExpression(this.readExpression());
            } else if (this.readIf(25)) {
               if (this.readIf(60)) {
                  this.read(58);
                  var5 = true;
                  break label130;
               }

               var3.setDefaultExpression(this.session, this.readExpression());
            } else if (this.readIf("GENERATED")) {
               boolean var7 = this.readIf("ALWAYS");
               if (!var7) {
                  this.read("BY");
                  this.read(25);
               }

               this.read(7);
               if (this.readIf("IDENTITY")) {
                  SequenceOptions var8 = new SequenceOptions();
                  if (this.readIf(105)) {
                     this.parseSequenceOptions(var8, (CreateSequence)null, false, false);
                     this.read(106);
                  }

                  var3.setIdentityOptions(var8, var7);
                  break label130;
               }

               if (!var7) {
                  throw this.getSyntaxError();
               }

               var3.setGeneratedExpression(this.readExpression());
            }

            if (!var3.isGenerated() && this.readIf(60)) {
               this.read("UPDATE");
               var3.setOnUpdateExpression(this.session, this.readExpression());
            }

            var6 = this.parseNotNullConstraint(var6);
            if (this.parseCompatibilityIdentity(var3, var4)) {
               var6 = this.parseNotNullConstraint(var6);
            }
         }
      }

      switch (var6) {
         case NULL_IS_ALLOWED:
            if (var3.isIdentity()) {
               throw DbException.get(90023, var3.getName());
            }

            var3.setNullable(true);
            break;
         case NULL_IS_NOT_ALLOWED:
            var3.setNullable(false);
            break;
         case NO_NULL_CONSTRAINT_FOUND:
            if (!var3.isIdentity()) {
               var3.setNullable(var2);
            }
            break;
         default:
            throw DbException.get(90088, "Internal Error - unhandled case: " + var6.name());
      }

      if (!var5) {
         if (this.readIf(25)) {
            this.read(60);
            this.read(58);
            var5 = true;
         } else if (this.readIf("NULL_TO_DEFAULT")) {
            var5 = true;
         }
      }

      if (var5) {
         var3.setDefaultOnNull(true);
      }

      if (!var3.isGenerated() && this.readIf("SEQUENCE")) {
         var3.setSequence(this.readSequence(), var3.isGeneratedAlways());
      }

      if (this.readIf("SELECTIVITY")) {
         var3.setSelectivity(this.readNonNegativeInt());
      }

      if (var4.getEnum() == Mode.ModeEnum.MySQL) {
         if (this.readIf("CHARACTER")) {
            this.readIf(71);
            this.readMySQLCharset();
         }

         if (this.readIf("COLLATE")) {
            this.readMySQLCharset();
         }
      }

      String var9 = this.readCommentIf();
      if (var9 != null) {
         var3.setComment(var9);
      }

      return var3;
   }

   private void parseCompatibilityIdentityOptions(Column var1) {
      SequenceOptions var2 = new SequenceOptions();
      if (this.readIf(105)) {
         var2.setStartValue(ValueExpression.get(ValueBigint.get(this.readLong())));
         if (this.readIf(109)) {
            var2.setIncrement(ValueExpression.get(ValueBigint.get(this.readLong())));
         }

         this.read(106);
      }

      var1.setIdentityOptions(var2, false);
   }

   private String readCommentIf() {
      if (this.readIf("COMMENT")) {
         this.readIf(45);
         return this.readString();
      } else {
         return null;
      }
   }

   private Column parseColumnWithType(String var1) {
      TypeInfo var2 = this.readIfDataType();
      if (var2 == null) {
         String var3 = this.readIdentifierWithSchema();
         return getColumnWithDomain(var1, this.getSchema().getDomain(var3));
      } else {
         return new Column(var1, var2);
      }
   }

   private TypeInfo parseDataType() {
      TypeInfo var1 = this.readIfDataType();
      if (var1 == null) {
         this.addExpected("data type");
         throw this.getSyntaxError();
      } else {
         return var1;
      }
   }

   private TypeInfo readIfDataType() {
      TypeInfo var1 = this.readIfDataType1();
      if (var1 != null) {
         while(this.readIf(6)) {
            var1 = this.parseArrayType(var1);
         }
      }

      return var1;
   }

   private TypeInfo readIfDataType1() {
      switch (this.currentTokenType) {
         case 2:
            if (this.token.isQuoted()) {
               return null;
            }
            break;
         case 6:
            if (this.session.isQuirksMode()) {
               this.read();
               return this.parseArrayType(TypeInfo.TYPE_VARCHAR);
            }

            this.addExpected("data type");
            throw this.getSyntaxError();
         case 44:
            this.read();
            TypeInfo var1 = this.readIntervalQualifier();
            if (var1 == null) {
               throw this.intervalQualifierError();
            }

            return var1;
         case 58:
            this.read();
            return TypeInfo.TYPE_NULL;
         case 66:
            this.read();
            return this.parseRowType();
         default:
            if (!this.isKeyword(this.currentToken)) {
               this.addExpected("data type");
               throw this.getSyntaxError();
            }
      }

      int var10 = this.tokenIndex;
      String var2 = this.currentToken;
      this.read();
      if (this.currentTokenType == 110) {
         this.setTokenIndex(var10);
         return null;
      } else {
         switch (this.upperName(var2)) {
            case "BINARY":
               if (this.readIf("VARYING")) {
                  var3 = "BINARY VARYING";
               } else if (this.readIf("LARGE")) {
                  this.read("OBJECT");
                  var3 = "BINARY LARGE OBJECT";
               } else if (this.variableBinary) {
                  var3 = "VARBINARY";
               }
               break;
            case "CHAR":
               if (this.readIf("VARYING")) {
                  var3 = "CHAR VARYING";
               } else if (this.readIf("LARGE")) {
                  this.read("OBJECT");
                  var3 = "CHAR LARGE OBJECT";
               }
               break;
            case "CHARACTER":
               if (this.readIf("VARYING")) {
                  var3 = "CHARACTER VARYING";
               } else if (this.readIf("LARGE")) {
                  this.read("OBJECT");
                  var3 = "CHARACTER LARGE OBJECT";
               }
               break;
            case "DATETIME":
            case "DATETIME2":
               return this.parseDateTimeType(false);
            case "DEC":
            case "DECIMAL":
               return this.parseNumericType(true);
            case "DECFLOAT":
               return this.parseDecfloatType();
            case "DOUBLE":
               if (this.readIf("PRECISION")) {
                  var3 = "DOUBLE PRECISION";
               }
               break;
            case "ENUM":
               return this.parseEnumType();
            case "FLOAT":
               return this.parseFloatType();
            case "GEOMETRY":
               return this.parseGeometryType();
            case "LONG":
               if (this.readIf("RAW")) {
                  var3 = "LONG RAW";
               }
               break;
            case "NATIONAL":
               if (this.readIf("CHARACTER")) {
                  if (this.readIf("VARYING")) {
                     var3 = "NATIONAL CHARACTER VARYING";
                  } else if (this.readIf("LARGE")) {
                     this.read("OBJECT");
                     var3 = "NATIONAL CHARACTER LARGE OBJECT";
                  } else {
                     var3 = "NATIONAL CHARACTER";
                  }
               } else {
                  this.read("CHAR");
                  if (this.readIf("VARYING")) {
                     var3 = "NATIONAL CHAR VARYING";
                  } else {
                     var3 = "NATIONAL CHAR";
                  }
               }
               break;
            case "NCHAR":
               if (this.readIf("VARYING")) {
                  var3 = "NCHAR VARYING";
               } else if (this.readIf("LARGE")) {
                  this.read("OBJECT");
                  var3 = "NCHAR LARGE OBJECT";
               }
               break;
            case "NUMBER":
               if (this.database.getMode().disallowedTypes.contains("NUMBER")) {
                  throw DbException.get(50004, (String)"NUMBER");
               }

               if (!this.isToken(105)) {
                  return TypeInfo.getTypeInfo(16, 40L, -1, (ExtTypeInfo)null);
               }
            case "NUMERIC":
               return this.parseNumericType(false);
            case "SMALLDATETIME":
               return this.parseDateTimeType(true);
            case "TIME":
               return this.parseTimeType();
            case "TIMESTAMP":
               return this.parseTimestampType();
         }

         if (var2.length() == var3.length()) {
            Domain var4 = this.database.getSchema(this.session.getCurrentSchemaName()).findDomain(var2);
            if (var4 != null) {
               this.setTokenIndex(var10);
               return null;
            }
         }

         Mode var11 = this.database.getMode();
         DataType var12 = DataType.getTypeByName(var3, var11);
         if (var12 != null && !var11.disallowedTypes.contains(var3)) {
            long var6;
            int var8;
            if (var12.specialPrecisionScale) {
               var6 = var12.defaultPrecision;
               var8 = var12.defaultScale;
            } else {
               var6 = -1L;
               var8 = -1;
            }

            int var9 = var12.type;
            if (this.database.getIgnoreCase() && var9 == 2 && !this.equalsToken("VARCHAR_CASESENSITIVE", var3)) {
               var9 = 4;
               var12 = DataType.getDataType(4);
            }

            if ((var12.supportsPrecision || var12.supportsScale) && this.readIf(105)) {
               if (!this.readIf("MAX")) {
                  if (var12.supportsPrecision) {
                     var6 = this.readPrecision(var9);
                     if (var6 < var12.minPrecision) {
                        throw getInvalidPrecisionException(var12, var6);
                     }

                     if (var6 > var12.maxPrecision) {
                        if (!this.session.isQuirksMode() && !this.session.isTruncateLargeLength()) {
                           throw getInvalidPrecisionException(var12, var6);
                        }

                        switch (var12.type) {
                           case 1:
                           case 2:
                           case 4:
                           case 5:
                           case 6:
                           case 35:
                           case 38:
                              var6 = var12.maxPrecision;
                              break;
                           default:
                              throw getInvalidPrecisionException(var12, var6);
                        }
                     }

                     if (var12.supportsScale && this.readIf(109)) {
                        var8 = this.readInt();
                        if (var8 < var12.minScale || var8 > var12.maxScale) {
                           throw DbException.get(90151, Integer.toString(var8), Integer.toString(var12.minScale), Integer.toString(var12.maxScale));
                        }
                     }
                  } else {
                     var8 = this.readInt();
                     if (var8 < var12.minScale || var8 > var12.maxScale) {
                        throw DbException.get(90151, Integer.toString(var8), Integer.toString(var12.minScale), Integer.toString(var12.maxScale));
                     }
                  }
               }

               this.read(106);
            }

            if (var11.allNumericTypesHavePrecision && DataType.isNumericType(var12.type)) {
               if (this.readIf(105)) {
                  this.readNonNegativeInt();
                  this.read(106);
               }

               this.readIf("UNSIGNED");
            }

            if (var11.forBitData && DataType.isStringType(var9) && this.readIf(33)) {
               this.read("BIT");
               this.read("DATA");
               var9 = 6;
               var12 = DataType.getDataType(6);
            }

            return TypeInfo.getTypeInfo(var9, var6, var8, (ExtTypeInfo)null);
         } else {
            throw DbException.get(50004, (String)var3);
         }
      }
   }

   private static DbException getInvalidPrecisionException(DataType var0, long var1) {
      return DbException.get(90150, Long.toString(var1), Long.toString(var0.minPrecision), Long.toString(var0.maxPrecision));
   }

   private static Column getColumnWithDomain(String var0, Domain var1) {
      Column var2 = new Column(var0, var1.getDataType());
      var2.setComment(var1.getComment());
      var2.setDomain(var1);
      return var2;
   }

   private TypeInfo parseFloatType() {
      byte var1 = 15;
      int var2;
      if (this.readIf(105)) {
         var2 = this.readNonNegativeInt();
         this.read(106);
         if (var2 < 1 || var2 > 53) {
            throw DbException.get(90150, Integer.toString(var2), "1", "53");
         }

         if (var2 <= 24) {
            var1 = 14;
         }
      } else {
         var2 = 0;
      }

      return TypeInfo.getTypeInfo(var1, (long)var2, -1, (ExtTypeInfo)null);
   }

   private TypeInfo parseNumericType(boolean var1) {
      long var2 = -1L;
      int var4 = -1;
      if (this.readIf(105)) {
         var2 = this.readPrecision(13);
         if (var2 < 1L) {
            throw getInvalidNumericPrecisionException(var2);
         }

         if (var2 > 100000L) {
            if (!this.session.isQuirksMode() && !this.session.isTruncateLargeLength()) {
               throw getInvalidNumericPrecisionException(var2);
            }

            var2 = 100000L;
         }

         if (this.readIf(109)) {
            var4 = this.readInt();
            if (var4 < 0 || var4 > 100000) {
               throw DbException.get(90151, Integer.toString(var4), "0", "100000");
            }
         }

         this.read(106);
      }

      return TypeInfo.getTypeInfo(13, var2, var4, var1 ? ExtTypeInfoNumeric.DECIMAL : null);
   }

   private TypeInfo parseDecfloatType() {
      long var1 = -1L;
      if (this.readIf(105)) {
         var1 = this.readPrecision(16);
         if (var1 < 1L || var1 > 100000L) {
            throw getInvalidNumericPrecisionException(var1);
         }

         this.read(106);
      }

      return TypeInfo.getTypeInfo(16, var1, -1, (ExtTypeInfo)null);
   }

   private static DbException getInvalidNumericPrecisionException(long var0) {
      return DbException.get(90150, Long.toString(var0), "1", "100000");
   }

   private TypeInfo parseTimeType() {
      int var1 = -1;
      if (this.readIf(105)) {
         var1 = this.readNonNegativeInt();
         if (var1 > 9) {
            throw DbException.get(90151, Integer.toString(var1), "0", "9");
         }

         this.read(106);
      }

      byte var2 = 18;
      if (this.readIf(89)) {
         this.read("TIME");
         this.read("ZONE");
         var2 = 19;
      } else if (this.readIf("WITHOUT")) {
         this.read("TIME");
         this.read("ZONE");
      }

      return TypeInfo.getTypeInfo(var2, -1L, var1, (ExtTypeInfo)null);
   }

   private TypeInfo parseTimestampType() {
      int var1 = -1;
      if (this.readIf(105)) {
         var1 = this.readNonNegativeInt();
         if (this.readIf(109)) {
            var1 = this.readNonNegativeInt();
         }

         if (var1 > 9) {
            throw DbException.get(90151, Integer.toString(var1), "0", "9");
         }

         this.read(106);
      }

      byte var2 = 20;
      if (this.readIf(89)) {
         this.read("TIME");
         this.read("ZONE");
         var2 = 21;
      } else if (this.readIf("WITHOUT")) {
         this.read("TIME");
         this.read("ZONE");
      }

      return TypeInfo.getTypeInfo(var2, -1L, var1, (ExtTypeInfo)null);
   }

   private TypeInfo parseDateTimeType(boolean var1) {
      int var2;
      if (var1) {
         var2 = 0;
      } else {
         var2 = -1;
         if (this.readIf(105)) {
            var2 = this.readNonNegativeInt();
            if (var2 > 9) {
               throw DbException.get(90151, Integer.toString(var2), "0", "9");
            }

            this.read(106);
         }
      }

      return TypeInfo.getTypeInfo(20, -1L, var2, (ExtTypeInfo)null);
   }

   private TypeInfo readIntervalQualifier() {
      IntervalQualifier var1;
      int var2;
      int var3;
      var2 = -1;
      var3 = -1;
      label84:
      switch (this.currentTokenType) {
         case 24:
            this.read();
            if (this.readIf(105)) {
               var2 = this.readNonNegativeInt();
               this.read(106);
            }

            if (this.readIf(76)) {
               switch (this.currentTokenType) {
                  case 39:
                     this.read();
                     var1 = IntervalQualifier.DAY_TO_HOUR;
                     break label84;
                  case 54:
                     this.read();
                     var1 = IntervalQualifier.DAY_TO_MINUTE;
                     break label84;
                  case 68:
                     this.read();
                     if (this.readIf(105)) {
                        var3 = this.readNonNegativeInt();
                        this.read(106);
                     }

                     var1 = IntervalQualifier.DAY_TO_SECOND;
                     break label84;
                  default:
                     throw this.intervalDayError();
               }
            } else {
               var1 = IntervalQualifier.DAY;
               break;
            }
         case 39:
            this.read();
            if (this.readIf(105)) {
               var2 = this.readNonNegativeInt();
               this.read(106);
            }

            if (this.readIf(76)) {
               switch (this.currentTokenType) {
                  case 54:
                     this.read();
                     var1 = IntervalQualifier.HOUR_TO_MINUTE;
                     break label84;
                  case 68:
                     this.read();
                     if (this.readIf(105)) {
                        var3 = this.readNonNegativeInt();
                        this.read(106);
                     }

                     var1 = IntervalQualifier.HOUR_TO_SECOND;
                     break label84;
                  default:
                     throw this.intervalHourError();
               }
            } else {
               var1 = IntervalQualifier.HOUR;
               break;
            }
         case 54:
            this.read();
            if (this.readIf(105)) {
               var2 = this.readNonNegativeInt();
               this.read(106);
            }

            if (this.readIf(76)) {
               this.read(68);
               if (this.readIf(105)) {
                  var3 = this.readNonNegativeInt();
                  this.read(106);
               }

               var1 = IntervalQualifier.MINUTE_TO_SECOND;
            } else {
               var1 = IntervalQualifier.MINUTE;
            }
            break;
         case 55:
            this.read();
            if (this.readIf(105)) {
               var2 = this.readNonNegativeInt();
               this.read(106);
            }

            var1 = IntervalQualifier.MONTH;
            break;
         case 68:
            this.read();
            if (this.readIf(105)) {
               var2 = this.readNonNegativeInt();
               if (this.readIf(109)) {
                  var3 = this.readNonNegativeInt();
               }

               this.read(106);
            }

            var1 = IntervalQualifier.SECOND;
            break;
         case 90:
            this.read();
            if (this.readIf(105)) {
               var2 = this.readNonNegativeInt();
               this.read(106);
            }

            if (this.readIf(76)) {
               this.read(55);
               var1 = IntervalQualifier.YEAR_TO_MONTH;
            } else {
               var1 = IntervalQualifier.YEAR;
            }
            break;
         default:
            return null;
      }

      if (var2 >= 0 && (var2 == 0 || var2 > 18)) {
         throw DbException.get(90150, Integer.toString(var2), "1", "18");
      } else if (var3 >= 0 && var3 > 9) {
         throw DbException.get(90151, Integer.toString(var3), "0", "9");
      } else {
         return TypeInfo.getTypeInfo(var1.ordinal() + 22, (long)var2, var3, (ExtTypeInfo)null);
      }
   }

   private DbException intervalQualifierError() {
      if (this.expectedList != null) {
         this.addMultipleExpected(90, 55, 24, 39, 54, 68);
      }

      return this.getSyntaxError();
   }

   private DbException intervalDayError() {
      if (this.expectedList != null) {
         this.addMultipleExpected(39, 54, 68);
      }

      return this.getSyntaxError();
   }

   private DbException intervalHourError() {
      if (this.expectedList != null) {
         this.addMultipleExpected(54, 68);
      }

      return this.getSyntaxError();
   }

   private TypeInfo parseArrayType(TypeInfo var1) {
      int var2 = -1;
      if (this.readIf(117)) {
         var2 = this.readNonNegativeInt();
         if (var2 > 65536) {
            throw DbException.get(90150, Integer.toString(var2), "0", "65536");
         }

         this.read(118);
      }

      return TypeInfo.getTypeInfo(40, (long)var2, -1, var1);
   }

   private TypeInfo parseEnumType() {
      this.read(105);
      ArrayList var1 = new ArrayList();

      do {
         var1.add(this.readString());
      } while(this.readIfMore());

      return TypeInfo.getTypeInfo(36, -1L, -1, new ExtTypeInfoEnum((String[])var1.toArray(new String[0])));
   }

   private TypeInfo parseGeometryType() {
      ExtTypeInfoGeometry var1;
      if (this.readIf(105)) {
         int var2 = 0;
         if (this.currentTokenType != 2 || this.token.isQuoted()) {
            throw this.getSyntaxError();
         }

         if (!this.readIf("GEOMETRY")) {
            try {
               var2 = EWKTUtils.parseGeometryType(this.currentToken);
               this.read();
               if (var2 / 1000 == 0 && this.currentTokenType == 2 && !this.token.isQuoted()) {
                  var2 += EWKTUtils.parseDimensionSystem(this.currentToken) * 1000;
                  this.read();
               }
            } catch (IllegalArgumentException var4) {
               throw this.getSyntaxError();
            }
         }

         Integer var3 = null;
         if (this.readIf(109)) {
            var3 = this.readInt();
         }

         this.read(106);
         var1 = new ExtTypeInfoGeometry(var2, var3);
      } else {
         var1 = null;
      }

      return TypeInfo.getTypeInfo(37, -1L, -1, var1);
   }

   private TypeInfo parseRowType() {
      this.read(105);
      LinkedHashMap var1 = new LinkedHashMap();

      do {
         String var2 = this.readIdentifier();
         if (var1.putIfAbsent(var2, this.parseDataType()) != null) {
            throw DbException.get(42121, (String)var2);
         }
      } while(this.readIfMore());

      return TypeInfo.getTypeInfo(41, -1L, -1, new ExtTypeInfoRow(var1));
   }

   private long readPrecision(int var1) {
      long var2 = this.readPositiveLong();
      if (this.currentTokenType == 2 && !this.token.isQuoted()) {
         if ((var1 == 7 || var1 == 3) && this.currentToken.length() == 1) {
            long var4;
            switch (this.currentToken.charAt(0) & 65503) {
               case 71:
                  var4 = 1073741824L;
                  break;
               case 72:
               case 73:
               case 74:
               case 76:
               case 78:
               case 79:
               case 81:
               case 82:
               case 83:
               default:
                  throw this.getSyntaxError();
               case 75:
                  var4 = 1024L;
                  break;
               case 77:
                  var4 = 1048576L;
                  break;
               case 80:
                  var4 = 1125899906842624L;
                  break;
               case 84:
                  var4 = 1099511627776L;
            }

            if (var2 > Long.MAX_VALUE / var4) {
               throw DbException.getInvalidValueException("precision", var2 + this.currentToken);
            }

            var2 *= var4;
            this.read();
            if (this.currentTokenType != 2 || this.token.isQuoted()) {
               return var2;
            }
         }

         switch (var1) {
            case 1:
            case 2:
            case 3:
            case 4:
               if (!this.readIf("CHARACTERS") && !this.readIf("OCTETS") && this.database.getMode().charAndByteLengthUnits && !this.readIf("CHAR")) {
                  this.readIf("BYTE");
               }
            default:
               return var2;
         }
      } else {
         return var2;
      }
   }

   private Prepared parseCreate() {
      boolean var1 = false;
      if (this.readIf(61)) {
         this.read("REPLACE");
         var1 = true;
      }

      boolean var2 = this.readIf("FORCE");
      if (this.readIf("VIEW")) {
         return this.parseCreateView(var2, var1);
      } else if (this.readIf("ALIAS")) {
         return this.parseCreateFunctionAlias(var2);
      } else if (this.readIf("SEQUENCE")) {
         return this.parseCreateSequence();
      } else if (this.readIf(82)) {
         return this.parseCreateUser();
      } else if (this.readIf("TRIGGER")) {
         return this.parseCreateTrigger(var2);
      } else if (this.readIf("ROLE")) {
         return this.parseCreateRole();
      } else if (this.readIf("SCHEMA")) {
         return this.parseCreateSchema();
      } else if (this.readIf("CONSTANT")) {
         return this.parseCreateConstant();
      } else if (!this.readIf("DOMAIN") && !this.readIf("TYPE") && !this.readIf("DATATYPE")) {
         if (this.readIf("AGGREGATE")) {
            return this.parseCreateAggregate(var2);
         } else if (this.readIf("LINKED")) {
            return this.parseCreateLinkedTable(false, false, var2);
         } else {
            boolean var3 = false;
            boolean var4 = false;
            if (this.readIf("MEMORY")) {
               var3 = true;
            } else if (this.readIf("CACHED")) {
               var4 = true;
            }

            if (this.readIf("LOCAL")) {
               this.read("TEMPORARY");
               if (this.readIf("LINKED")) {
                  return this.parseCreateLinkedTable(true, false, var2);
               } else {
                  this.read(75);
                  return this.parseCreateTable(true, false, var4);
               }
            } else if (this.readIf("GLOBAL")) {
               this.read("TEMPORARY");
               if (this.readIf("LINKED")) {
                  return this.parseCreateLinkedTable(true, true, var2);
               } else {
                  this.read(75);
                  return this.parseCreateTable(true, true, var4);
               }
            } else if (!this.readIf("TEMP") && !this.readIf("TEMPORARY")) {
               if (this.readIf(75)) {
                  if (!var4 && !var3) {
                     var4 = this.database.getDefaultTableType() == 0;
                  }

                  return this.parseCreateTable(false, false, var4);
               } else if (this.readIf("SYNONYM")) {
                  return this.parseCreateSynonym(var1);
               } else {
                  boolean var5 = false;
                  boolean var6 = false;
                  boolean var7 = false;
                  boolean var8 = false;
                  String var9 = null;
                  Schema var10 = null;
                  boolean var11 = false;
                  if (this.session.isQuirksMode() && this.readIf(63)) {
                     this.read(47);
                     if (this.readIf("HASH")) {
                        var5 = true;
                     }

                     var6 = true;
                     if (!this.isToken(60)) {
                        var11 = this.readIfNotExists();
                        var9 = this.readIdentifierWithSchema((String)null);
                        var10 = this.getSchema();
                     }
                  } else {
                     if (this.readIf(80)) {
                        var7 = true;
                     }

                     if (this.readIf("HASH")) {
                        var5 = true;
                     } else if (!var7 && this.readIf("SPATIAL")) {
                        var8 = true;
                     }

                     this.read("INDEX");
                     if (!this.isToken(60)) {
                        var11 = this.readIfNotExists();
                        var9 = this.readIdentifierWithSchema((String)null);
                        var10 = this.getSchema();
                     }
                  }

                  this.read(60);
                  String var12 = this.readIdentifierWithSchema();
                  this.checkSchema(var10);
                  String var13 = this.readCommentIf();
                  if (!this.readIf(105)) {
                     if (var5 || var8) {
                        throw this.getSyntaxError();
                     }

                     this.read(83);
                     if (!this.readIf("BTREE")) {
                        if (this.readIf("HASH")) {
                           var5 = true;
                        } else {
                           this.read("RTREE");
                           var8 = true;
                        }
                     }

                     this.read(105);
                  }

                  CreateIndex var14 = new CreateIndex(this.session, this.getSchema());
                  var14.setIfNotExists(var11);
                  var14.setPrimaryKey(var6);
                  var14.setTableName(var12);
                  var14.setHash(var5);
                  var14.setSpatial(var8);
                  var14.setIndexName(var9);
                  var14.setComment(var13);
                  int var16 = 0;
                  IndexColumn[] var15;
                  if (var8) {
                     var15 = new IndexColumn[]{new IndexColumn(this.readIdentifier())};
                     if (var7) {
                        var16 = 1;
                     }

                     this.read(106);
                  } else {
                     var15 = this.parseIndexColumnList();
                     if (var7) {
                        var16 = var15.length;
                        if (this.readIf("INCLUDE")) {
                           this.read(105);
                           IndexColumn[] var17 = this.parseIndexColumnList();
                           int var18 = var17.length;
                           var15 = (IndexColumn[])Arrays.copyOf(var15, var16 + var18);
                           System.arraycopy(var17, 0, var15, var16, var18);
                        }
                     } else if (var6) {
                        var16 = var15.length;
                     }
                  }

                  var14.setIndexColumns(var15);
                  var14.setUniqueColumnCount(var16);
                  return var14;
               }
            } else if (this.readIf("LINKED")) {
               return this.parseCreateLinkedTable(true, true, var2);
            } else {
               this.read(75);
               return this.parseCreateTable(true, true, var4);
            }
         }
      } else {
         return this.parseCreateDomain();
      }
   }

   private boolean addRoleOrRight(GrantRevoke var1) {
      if (this.readIf(69)) {
         var1.addRight(1);
         return true;
      } else if (this.readIf("DELETE")) {
         var1.addRight(2);
         return true;
      } else if (this.readIf("INSERT")) {
         var1.addRight(4);
         return true;
      } else if (this.readIf("UPDATE")) {
         var1.addRight(8);
         return true;
      } else if (this.readIf("CONNECT")) {
         return true;
      } else if (this.readIf("RESOURCE")) {
         return true;
      } else {
         var1.addRoleName(this.readIdentifier());
         return false;
      }
   }

   private GrantRevoke parseGrantRevoke(int var1) {
      GrantRevoke var2 = new GrantRevoke(this.session);
      var2.setOperationType(var1);
      boolean var3;
      if (this.readIf(3)) {
         this.readIf("PRIVILEGES");
         var2.addRight(15);
         var3 = true;
      } else if (this.readIf("ALTER")) {
         this.read(5);
         this.read("SCHEMA");
         var2.addRight(16);
         var2.addTable((Table)null);
         var3 = false;
      } else {
         var3 = this.addRoleOrRight(var2);

         while(this.readIf(109)) {
            if (this.addRoleOrRight(var2) != var3) {
               throw DbException.get(90072);
            }
         }
      }

      if (var3 && this.readIf(60)) {
         if (this.readIf("SCHEMA")) {
            var2.setSchema(this.database.getSchema(this.readIdentifier()));
         } else {
            this.readIf(75);

            do {
               Table var4 = this.readTableOrView();
               var2.addTable(var4);
            } while(this.readIf(109));
         }
      }

      this.read(var1 == 49 ? 76 : 35);
      var2.setGranteeName(this.readIdentifier());
      return var2;
   }

   private TableValueConstructor parseValues() {
      ArrayList var1 = Utils.newSmallArrayList();
      ArrayList var2 = this.parseValuesRow(Utils.newSmallArrayList());
      var1.add(var2);
      int var3 = var2.size();

      while(this.readIf(109)) {
         var2 = this.parseValuesRow(new ArrayList(var3));
         if (var2.size() != var3) {
            throw DbException.get(21002);
         }

         var1.add(var2);
      }

      return new TableValueConstructor(this.session, var1);
   }

   private ArrayList<Expression> parseValuesRow(ArrayList<Expression> var1) {
      if (this.readIf(66)) {
         this.read(105);
      } else if (!this.readIf(105)) {
         var1.add(this.readExpression());
         return var1;
      }

      do {
         var1.add(this.readExpression());
      } while(this.readIfMore());

      return var1;
   }

   private Call parseCall() {
      Call var1 = new Call(this.session);
      this.currentPrepared = var1;
      int var2 = this.tokenIndex;
      boolean var3;
      switch (this.currentTokenType) {
         case 2:
            var3 = true;
            break;
         case 75:
            this.read();
            this.read(105);
            var1.setTableFunction(this.readTableFunction(1));
            return var1;
         default:
            var3 = false;
      }

      try {
         var1.setExpression(this.readExpression());
         return var1;
      } catch (DbException var8) {
         if (var3 && var8.getErrorCode() == 90022) {
            this.setTokenIndex(var2);
            String var5 = null;
            String var6 = this.readIdentifier();
            if (this.readIf(110)) {
               var5 = var6;
               var6 = this.readIdentifier();
               if (this.readIf(110)) {
                  this.checkDatabaseName(var5);
                  var5 = var6;
                  var6 = this.readIdentifier();
               }
            }

            this.read(105);
            Schema var7 = var5 != null ? this.database.getSchema(var5) : null;
            var1.setTableFunction(this.readTableFunction(var6, var7));
            return var1;
         } else {
            throw var8;
         }
      }
   }

   private CreateRole parseCreateRole() {
      CreateRole var1 = new CreateRole(this.session);
      var1.setIfNotExists(this.readIfNotExists());
      var1.setRoleName(this.readIdentifier());
      return var1;
   }

   private CreateSchema parseCreateSchema() {
      CreateSchema var1 = new CreateSchema(this.session);
      var1.setIfNotExists(this.readIfNotExists());
      String var2;
      if (this.readIf(9)) {
         var2 = this.readIdentifier();
         var1.setSchemaName(var2);
         var1.setAuthorization(var2);
      } else {
         var1.setSchemaName(this.readIdentifier());
         if (this.readIf(9)) {
            var2 = this.readIdentifier();
         } else {
            var2 = this.session.getUser().getName();
         }
      }

      var1.setAuthorization(var2);
      if (this.readIf(89)) {
         var1.setTableEngineParams(this.readTableEngineParams());
      }

      return var1;
   }

   private ArrayList<String> readTableEngineParams() {
      ArrayList var1 = Utils.newSmallArrayList();

      do {
         var1.add(this.readIdentifier());
      } while(this.readIf(109));

      return var1;
   }

   private CreateSequence parseCreateSequence() {
      boolean var1 = this.readIfNotExists();
      String var2 = this.readIdentifierWithSchema();
      CreateSequence var3 = new CreateSequence(this.session, this.getSchema());
      var3.setIfNotExists(var1);
      var3.setSequenceName(var2);
      SequenceOptions var4 = new SequenceOptions();
      this.parseSequenceOptions(var4, var3, true, false);
      var3.setOptions(var4);
      return var3;
   }

   private boolean readIfNotExists() {
      if (this.readIf(40)) {
         this.read(57);
         this.read(30);
         return true;
      } else {
         return false;
      }
   }

   private CreateConstant parseCreateConstant() {
      boolean var1 = this.readIfNotExists();
      String var2 = this.readIdentifierWithSchema();
      Schema var3 = this.getSchema();
      if (this.isKeyword(var2)) {
         throw DbException.get(90114, var2);
      } else {
         this.read(84);
         Expression var4 = this.readExpression();
         CreateConstant var5 = new CreateConstant(this.session, var3);
         var5.setConstantName(var2);
         var5.setExpression(var4);
         var5.setIfNotExists(var1);
         return var5;
      }
   }

   private CreateAggregate parseCreateAggregate(boolean var1) {
      boolean var2 = this.readIfNotExists();
      String var3 = this.readIdentifierWithSchema();
      String var4;
      if (!this.isKeyword(var3) && !BuiltinFunctions.isBuiltinFunction(this.database, var4 = this.upperName(var3)) && Aggregate.getAggregateType(var4) == null) {
         CreateAggregate var5 = new CreateAggregate(this.session, this.getSchema());
         var5.setForce(var1);
         var5.setName(var3);
         var5.setIfNotExists(var2);
         this.read(33);
         var5.setJavaClassMethod(this.readStringOrIdentifier());
         return var5;
      } else {
         throw DbException.get(90076, var3);
      }
   }

   private CreateDomain parseCreateDomain() {
      boolean var1 = this.readIfNotExists();
      String var2 = this.readIdentifierWithSchema();
      Schema var3 = this.getSchema();
      CreateDomain var4 = new CreateDomain(this.session, var3);
      var4.setIfNotExists(var1);
      var4.setTypeName(var2);
      this.readIf(7);
      TypeInfo var5 = this.readIfDataType();
      String var6;
      if (var5 != null) {
         var4.setDataType(var5);
      } else {
         var6 = this.readIdentifierWithSchema();
         var4.setParentDomain(this.getSchema().getDomain(var6));
      }

      if (this.readIf(25)) {
         var4.setDefaultExpression(this.readExpression());
      }

      if (this.readIf(60)) {
         this.read("UPDATE");
         var4.setOnUpdateExpression(this.readExpression());
      }

      if (this.readIf("SELECTIVITY")) {
         this.readNonNegativeInt();
      }

      var6 = this.readCommentIf();
      if (var6 != null) {
         var4.setComment(var6);
      }

      while(true) {
         String var7;
         if (this.readIf(14)) {
            var7 = this.readIdentifier();
            this.read(13);
         } else {
            if (!this.readIf(13)) {
               return var4;
            }

            var7 = null;
         }

         AlterDomainAddConstraint var8 = new AlterDomainAddConstraint(this.session, var3, var1);
         var8.setConstraintName(var7);
         var8.setDomainName(var2);
         this.parseDomainConstraint = true;

         try {
            var8.setCheckExpression(this.readExpression());
         } finally {
            this.parseDomainConstraint = false;
         }

         var4.addConstraintCommand(var8);
      }
   }

   private CreateTrigger parseCreateTrigger(boolean var1) {
      boolean var2 = this.readIfNotExists();
      String var3 = this.readIdentifierWithSchema((String)null);
      Schema var4 = this.getSchema();
      boolean var5;
      boolean var6;
      if (this.readIf("INSTEAD")) {
         this.read("OF");
         var6 = true;
         var5 = true;
      } else if (this.readIf("BEFORE")) {
         var5 = false;
         var6 = true;
      } else {
         this.read("AFTER");
         var5 = false;
         var6 = false;
      }

      int var7 = 0;
      boolean var8 = false;
      boolean var9 = this.database.getMode().getEnum() == Mode.ModeEnum.PostgreSQL;

      do {
         do {
            if (this.readIf("INSERT")) {
               var7 |= 1;
            } else if (this.readIf("UPDATE")) {
               var7 |= 2;
            } else if (this.readIf("DELETE")) {
               var7 |= 4;
            } else if (this.readIf(69)) {
               var7 |= 8;
            } else {
               if (!this.readIf("ROLLBACK")) {
                  throw this.getSyntaxError();
               }

               var8 = true;
            }
         } while(this.readIf(109));
      } while(var9 && this.readIf(61));

      this.read(60);
      String var10 = this.readIdentifierWithSchema();
      this.checkSchema(var4);
      CreateTrigger var11 = new CreateTrigger(this.session, this.getSchema());
      var11.setForce(var1);
      var11.setTriggerName(var3);
      var11.setIfNotExists(var2);
      var11.setInsteadOf(var5);
      var11.setBefore(var6);
      var11.setOnRollback(var8);
      var11.setTypeMask(var7);
      var11.setTableName(var10);
      if (this.readIf(33)) {
         this.read("EACH");
         if (this.readIf(66)) {
            var11.setRowBased(true);
         } else {
            this.read("STATEMENT");
         }
      }

      if (this.readIf("QUEUE")) {
         var11.setQueueSize(this.readNonNegativeInt());
      }

      var11.setNoWait(this.readIf("NOWAIT"));
      if (this.readIf(7)) {
         var11.setTriggerSource(this.readString());
      } else {
         this.read("CALL");
         var11.setTriggerClassName(this.readStringOrIdentifier());
      }

      return var11;
   }

   private CreateUser parseCreateUser() {
      CreateUser var1 = new CreateUser(this.session);
      var1.setIfNotExists(this.readIfNotExists());
      var1.setUserName(this.readIdentifier());
      var1.setComment(this.readCommentIf());
      if (this.readIf("PASSWORD")) {
         var1.setPassword(this.readExpression());
      } else if (this.readIf("SALT")) {
         var1.setSalt(this.readExpression());
         this.read("HASH");
         var1.setHash(this.readExpression());
      } else {
         if (!this.readIf("IDENTIFIED")) {
            throw this.getSyntaxError();
         }

         this.read("BY");
         var1.setPassword(ValueExpression.get(ValueVarchar.get(this.readIdentifier())));
      }

      if (this.readIf("ADMIN")) {
         var1.setAdmin(true);
      }

      return var1;
   }

   private CreateFunctionAlias parseCreateFunctionAlias(boolean var1) {
      boolean var2 = this.readIfNotExists();
      String var3;
      if (this.currentTokenType != 2) {
         var3 = this.currentToken;
         this.read();
         this.schemaName = this.session.getCurrentSchemaName();
      } else {
         var3 = this.readIdentifierWithSchema();
      }

      String var4 = this.upperName(var3);
      if (this.isReservedFunctionName(var4)) {
         throw DbException.get(90076, var3);
      } else {
         CreateFunctionAlias var5 = new CreateFunctionAlias(this.session, this.getSchema());
         var5.setForce(var1);
         var5.setAliasName(var3);
         var5.setIfNotExists(var2);
         var5.setDeterministic(this.readIf("DETERMINISTIC"));
         this.readIf("NOBUFFER");
         if (this.readIf(7)) {
            var5.setSource(this.readString());
         } else {
            this.read(33);
            var5.setJavaClassMethod(this.readStringOrIdentifier());
         }

         return var5;
      }
   }

   private String readStringOrIdentifier() {
      return this.currentTokenType != 2 ? this.readString() : this.readIdentifier();
   }

   private boolean isReservedFunctionName(String var1) {
      int var2 = ParserUtil.getTokenType(var1, false, false);
      if (var2 != 2) {
         if (this.database.isAllowBuiltinAliasOverride()) {
            switch (var2) {
               case 17:
               case 21:
               case 22:
               case 24:
               case 39:
               case 51:
               case 52:
               case 54:
               case 55:
               case 68:
               case 90:
                  return false;
            }
         }

         return true;
      } else {
         return Aggregate.getAggregateType(var1) != null || BuiltinFunctions.isBuiltinFunction(this.database, var1) && !this.database.isAllowBuiltinAliasOverride();
      }
   }

   private Prepared parseWith() {
      ArrayList var1 = new ArrayList();

      try {
         return this.parseWith1(var1);
      } catch (Throwable var3) {
         CommandContainer.clearCTE(this.session, (List)var1);
         throw var3;
      }
   }

   private Prepared parseWith1(List<TableView> var1) {
      this.readIf("RECURSIVE");
      boolean var2 = !this.session.isParsingCreateView();

      do {
         var1.add(this.parseSingleCommonTableExpression(var2));
      } while(this.readIf(109));

      Collections.reverse(var1);
      int var4 = this.tokenIndex;
      Object var3;
      if (this.isQueryQuick()) {
         var3 = this.parseWithQuery();
      } else if (this.readIf("INSERT")) {
         var3 = this.parseInsert(var4);
         ((Prepared)var3).setPrepareAlways(true);
      } else if (this.readIf("UPDATE")) {
         var3 = this.parseUpdate(var4);
         ((Prepared)var3).setPrepareAlways(true);
      } else if (this.readIf("MERGE")) {
         var3 = this.parseMerge(var4);
         ((Prepared)var3).setPrepareAlways(true);
      } else if (this.readIf("DELETE")) {
         var3 = this.parseDelete(var4);
         ((Prepared)var3).setPrepareAlways(true);
      } else {
         if (!this.readIf("CREATE")) {
            throw DbException.get(42000, (String)"WITH statement supports only SELECT, TABLE, VALUES, CREATE TABLE, INSERT, UPDATE, MERGE or DELETE statements");
         }

         if (!this.isToken(75)) {
            throw DbException.get(42000, (String)"WITH statement supports only SELECT, TABLE, VALUES, CREATE TABLE, INSERT, UPDATE, MERGE or DELETE statements");
         }

         var3 = this.parseCreate();
         ((Prepared)var3).setPrepareAlways(true);
      }

      if (var2) {
         if (this.cteCleanups == null) {
            this.cteCleanups = new ArrayList(var1.size());
         }

         this.cteCleanups.addAll(var1);
      }

      return (Prepared)var3;
   }

   private Prepared parseWithQuery() {
      Query var1 = this.parseQueryExpressionBodyAndEndOfQuery();
      var1.setPrepareAlways(true);
      var1.setNeverLazy(true);
      return var1;
   }

   private TableView parseSingleCommonTableExpression(boolean var1) {
      String var2 = this.readIdentifierWithSchema();
      Schema var3 = this.getSchema();
      ArrayList var4 = Utils.newSmallArrayList();
      String[] var5 = null;
      if (this.readIf(105)) {
         var5 = this.parseColumnList();
         String[] var6 = var5;
         int var7 = var5.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            var4.add(new Column(var9, TypeInfo.TYPE_VARCHAR));
         }
      }

      Table var14;
      if (!var1) {
         var14 = this.getSchema().findTableOrView(this.session, var2);
      } else {
         var14 = this.session.findLocalTempTable(var2);
      }

      if (var14 != null) {
         if (!(var14 instanceof TableView)) {
            throw DbException.get(42101, (String)var2);
         }

         TableView var15 = (TableView)var14;
         if (!var15.isTableExpression()) {
            throw DbException.get(42101, (String)var2);
         }

         if (!var1) {
            var14.lock(this.session, 2);
            this.database.removeSchemaObject(this.session, var14);
         } else {
            this.session.removeLocalTempTable(var14);
         }
      }

      Table var16 = TableView.createShadowTableForRecursiveTableExpression(var1, this.session, var2, var3, var4, this.database);
      String[] var18 = new String[1];

      List var17;
      try {
         this.read(7);
         this.read(105);
         Query var10 = this.parseQuery();
         if (!var1) {
            var10.session = this.session;
         }

         this.read(106);
         var17 = TableView.createQueryColumnTemplateList(var5, var10, var18);
      } finally {
         TableView.destroyShadowTableForRecursiveExpression(var1, this.session, var16);
      }

      return this.createCTEView(var2, var18[0], var17, true, true, var1);
   }

   private TableView createCTEView(String var1, String var2, List<Column> var3, boolean var4, boolean var5, boolean var6) {
      Schema var7 = this.getSchemaWithDefault();
      int var8 = this.database.allocateObjectId();
      Column[] var9 = (Column[])var3.toArray(new Column[0]);
      TableView var10;
      synchronized(this.session) {
         var10 = new TableView(var7, var8, var1, var2, this.parameters, var9, this.session, var4, false, true, var6);
         if (!var10.isRecursiveQueryDetected() && var4) {
            if (!var6) {
               this.database.addSchemaObject(this.session, var10);
               var10.lock(this.session, 2);
               this.database.removeSchemaObject(this.session, var10);
            } else {
               this.session.removeLocalTempTable(var10);
            }

            var10 = new TableView(var7, var8, var1, var2, this.parameters, var9, this.session, false, false, true, var6);
         }

         this.database.unlockMeta(this.session);
      }

      var10.setTableExpression(true);
      var10.setTemporary(var6);
      var10.setHidden(true);
      var10.setOnCommitDrop(false);
      if (var5) {
         if (!var6) {
            this.database.addSchemaObject(this.session, var10);
            var10.unlock(this.session);
            this.database.unlockMeta(this.session);
         } else {
            this.session.addLocalTempTable(var10);
         }
      }

      return var10;
   }

   private CreateView parseCreateView(boolean var1, boolean var2) {
      boolean var3 = this.readIfNotExists();
      boolean var4 = this.readIf("TABLE_EXPRESSION");
      String var5 = this.readIdentifierWithSchema();
      CreateView var6 = new CreateView(this.session, this.getSchema());
      this.createView = var6;
      var6.setViewName(var5);
      var6.setIfNotExists(var3);
      var6.setComment(this.readCommentIf());
      var6.setOrReplace(var2);
      var6.setForce(var1);
      var6.setTableExpression(var4);
      if (this.readIf(105)) {
         String[] var7 = this.parseColumnList();
         var6.setColumnNames(var7);
      }

      this.read(7);
      String var14 = StringUtils.cache(this.sqlCommand.substring(this.token.start()));

      try {
         this.session.setParsingCreateView(true);

         Query var8;
         try {
            var8 = this.parseQuery();
            var8.prepare();
         } finally {
            this.session.setParsingCreateView(false);
         }

         var6.setSelect(var8);
      } catch (DbException var13) {
         if (!var1) {
            throw var13;
         }

         var6.setSelectSQL(var14);

         while(this.currentTokenType != 93) {
            this.read();
         }
      }

      return var6;
   }

   private TransactionCommand parseCheckpoint() {
      TransactionCommand var1;
      if (this.readIf("SYNC")) {
         var1 = new TransactionCommand(this.session, 76);
      } else {
         var1 = new TransactionCommand(this.session, 73);
      }

      return var1;
   }

   private Prepared parseAlter() {
      if (this.readIf(75)) {
         return this.parseAlterTable();
      } else if (this.readIf(82)) {
         return this.parseAlterUser();
      } else if (this.readIf("INDEX")) {
         return this.parseAlterIndex();
      } else if (this.readIf("SCHEMA")) {
         return this.parseAlterSchema();
      } else if (this.readIf("SEQUENCE")) {
         return this.parseAlterSequence();
      } else if (this.readIf("VIEW")) {
         return this.parseAlterView();
      } else if (this.readIf("DOMAIN")) {
         return this.parseAlterDomain();
      } else {
         throw this.getSyntaxError();
      }
   }

   private void checkSchema(Schema var1) {
      if (var1 != null && this.getSchema() != var1) {
         throw DbException.get(90080);
      }
   }

   private AlterIndexRename parseAlterIndex() {
      boolean var1 = this.readIfExists(false);
      String var2 = this.readIdentifierWithSchema();
      Schema var3 = this.getSchema();
      AlterIndexRename var4 = new AlterIndexRename(this.session);
      var4.setOldSchema(var3);
      var4.setOldName(var2);
      var4.setIfExists(var1);
      this.read("RENAME");
      this.read(76);
      String var5 = this.readIdentifierWithSchema(var3.getName());
      this.checkSchema(var3);
      var4.setNewName(var5);
      return var4;
   }

   private DefineCommand parseAlterDomain() {
      boolean var1 = this.readIfExists(false);
      String var2 = this.readIdentifierWithSchema();
      Schema var3 = this.getSchema();
      String var5;
      boolean var11;
      if (this.readIf("ADD")) {
         var11 = false;
         var5 = null;
         String var15 = null;
         if (this.readIf(14)) {
            var11 = this.readIfNotExists();
            var5 = this.readIdentifierWithSchema(var3.getName());
            this.checkSchema(var3);
            var15 = this.readCommentIf();
         }

         this.read(13);
         AlterDomainAddConstraint var7 = new AlterDomainAddConstraint(this.session, var3, var11);
         var7.setDomainName(var2);
         var7.setConstraintName(var5);
         this.parseDomainConstraint = true;

         try {
            var7.setCheckExpression(this.readExpression());
         } finally {
            this.parseDomainConstraint = false;
         }

         var7.setIfDomainExists(var1);
         var7.setComment(var15);
         if (this.readIf("NOCHECK")) {
            var7.setCheckExisting(false);
         } else {
            this.readIf(13);
            var7.setCheckExisting(true);
         }

         return var7;
      } else {
         AlterDomainExpressions var4;
         if (this.readIf("DROP")) {
            if (this.readIf(14)) {
               var11 = this.readIfExists(false);
               var5 = this.readIdentifierWithSchema(var3.getName());
               this.checkSchema(var3);
               AlterDomainDropConstraint var6 = new AlterDomainDropConstraint(this.session, this.getSchema(), var11);
               var6.setConstraintName(var5);
               var6.setDomainName(var2);
               var6.setIfDomainExists(var1);
               return var6;
            }

            if (this.readIf(25)) {
               var4 = new AlterDomainExpressions(this.session, var3, 94);
               var4.setDomainName(var2);
               var4.setIfDomainExists(var1);
               var4.setExpression((Expression)null);
               return var4;
            }

            if (this.readIf(60)) {
               this.read("UPDATE");
               var4 = new AlterDomainExpressions(this.session, var3, 95);
               var4.setDomainName(var2);
               var4.setIfDomainExists(var1);
               var4.setExpression((Expression)null);
               return var4;
            }
         } else {
            if (this.readIf("RENAME")) {
               String var14;
               if (this.readIf(14)) {
                  var14 = this.readIdentifierWithSchema(var3.getName());
                  this.checkSchema(var3);
                  this.read(76);
                  AlterDomainRenameConstraint var13 = new AlterDomainRenameConstraint(this.session, var3);
                  var13.setDomainName(var2);
                  var13.setIfDomainExists(var1);
                  var13.setConstraintName(var14);
                  var13.setNewConstraintName(this.readIdentifier());
                  return var13;
               }

               this.read(76);
               var14 = this.readIdentifierWithSchema(var3.getName());
               this.checkSchema(var3);
               AlterDomainRename var12 = new AlterDomainRename(this.session, this.getSchema());
               var12.setDomainName(var2);
               var12.setIfDomainExists(var1);
               var12.setNewDomainName(var14);
               return var12;
            }

            this.read(71);
            if (this.readIf(25)) {
               var4 = new AlterDomainExpressions(this.session, var3, 94);
               var4.setDomainName(var2);
               var4.setIfDomainExists(var1);
               var4.setExpression(this.readExpression());
               return var4;
            }

            if (this.readIf(60)) {
               this.read("UPDATE");
               var4 = new AlterDomainExpressions(this.session, var3, 95);
               var4.setDomainName(var2);
               var4.setIfDomainExists(var1);
               var4.setExpression(this.readExpression());
               return var4;
            }
         }

         throw this.getSyntaxError();
      }
   }

   private DefineCommand parseAlterView() {
      boolean var1 = this.readIfExists(false);
      String var2 = this.readIdentifierWithSchema();
      Schema var3 = this.getSchema();
      Table var4 = var3.findTableOrView(this.session, var2);
      if (!(var4 instanceof TableView) && !var1) {
         throw DbException.get(90037, var2);
      } else if (this.readIf("RENAME")) {
         this.read(76);
         String var7 = this.readIdentifierWithSchema(var3.getName());
         this.checkSchema(var3);
         AlterTableRename var8 = new AlterTableRename(this.session, this.getSchema());
         var8.setTableName(var2);
         var8.setNewTableName(var7);
         var8.setIfTableExists(var1);
         return var8;
      } else {
         this.read("RECOMPILE");
         TableView var5 = (TableView)var4;
         AlterView var6 = new AlterView(this.session);
         var6.setIfExists(var1);
         var6.setView(var5);
         return var6;
      }
   }

   private Prepared parseAlterSchema() {
      boolean var1 = this.readIfExists(false);
      String var2 = this.readIdentifierWithSchema();
      Schema var3 = this.getSchema();
      this.read("RENAME");
      this.read(76);
      String var4 = this.readIdentifierWithSchema(var3.getName());
      Schema var5 = this.findSchema(var2);
      if (var5 == null) {
         if (var1) {
            return new NoOperation(this.session);
         } else {
            throw DbException.get(90079, var2);
         }
      } else {
         AlterSchemaRename var6 = new AlterSchemaRename(this.session);
         var6.setOldSchema(var5);
         this.checkSchema(var3);
         var6.setNewName(var4);
         return var6;
      }
   }

   private AlterSequence parseAlterSequence() {
      boolean var1 = this.readIfExists(false);
      String var2 = this.readIdentifierWithSchema();
      AlterSequence var3 = new AlterSequence(this.session, this.getSchema());
      var3.setSequenceName(var2);
      var3.setIfExists(var1);
      SequenceOptions var4 = new SequenceOptions();
      this.parseSequenceOptions(var4, (CreateSequence)null, false, false);
      var3.setOptions(var4);
      return var3;
   }

   private boolean parseSequenceOptions(SequenceOptions var1, CreateSequence var2, boolean var3, boolean var4) {
      boolean var5 = false;

      while(true) {
         if (var3 && this.readIf(7)) {
            TypeInfo var7 = this.parseDataType();
            if (!DataType.isNumericType(var7.getValueType())) {
               throw DbException.getUnsupportedException(var7.getSQL(new StringBuilder("CREATE SEQUENCE AS "), 3).toString());
            }

            var1.setDataType(var7);
         } else if (this.readIf("START")) {
            this.read(89);
            var1.setStartValue(this.readExpression());
         } else if (this.readIf("RESTART")) {
            var1.setRestartValue((Expression)(this.readIf(89) ? this.readExpression() : ValueExpression.DEFAULT));
         } else if (var2 == null || !this.parseCreateSequenceOption(var2)) {
            if (var4) {
               int var6 = this.tokenIndex;
               if (!this.readIf(71)) {
                  break;
               }

               if (!this.parseBasicSequenceOption(var1)) {
                  this.setTokenIndex(var6);
                  break;
               }
            } else if (!this.parseBasicSequenceOption(var1)) {
               break;
            }
         }

         var5 = true;
      }

      return var5;
   }

   private boolean parseCreateSequenceOption(CreateSequence var1) {
      if (this.readIf("BELONGS_TO_TABLE")) {
         var1.setBelongsToTable(true);
      } else if (!this.readIf(62)) {
         return false;
      }

      return true;
   }

   private boolean parseBasicSequenceOption(SequenceOptions var1) {
      if (this.readIf("INCREMENT")) {
         this.readIf("BY");
         var1.setIncrement(this.readExpression());
      } else if (this.readIf("MINVALUE")) {
         var1.setMinValue(this.readExpression());
      } else if (this.readIf("MAXVALUE")) {
         var1.setMaxValue(this.readExpression());
      } else if (this.readIf("CYCLE")) {
         var1.setCycle(Sequence.Cycle.CYCLE);
      } else if (this.readIf("NO")) {
         if (this.readIf("MINVALUE")) {
            var1.setMinValue(ValueExpression.NULL);
         } else if (this.readIf("MAXVALUE")) {
            var1.setMaxValue(ValueExpression.NULL);
         } else if (this.readIf("CYCLE")) {
            var1.setCycle(Sequence.Cycle.NO_CYCLE);
         } else {
            if (!this.readIf("CACHE")) {
               throw this.getSyntaxError();
            }

            var1.setCacheSize(ValueExpression.get(ValueBigint.get(1L)));
         }
      } else if (this.readIf("EXHAUSTED")) {
         var1.setCycle(Sequence.Cycle.EXHAUSTED);
      } else if (this.readIf("CACHE")) {
         var1.setCacheSize(this.readExpression());
      } else if (this.readIf("NOMINVALUE")) {
         var1.setMinValue(ValueExpression.NULL);
      } else if (this.readIf("NOMAXVALUE")) {
         var1.setMaxValue(ValueExpression.NULL);
      } else if (this.readIf("NOCYCLE")) {
         var1.setCycle(Sequence.Cycle.NO_CYCLE);
      } else {
         if (!this.readIf("NOCACHE")) {
            return false;
         }

         var1.setCacheSize(ValueExpression.get(ValueBigint.get(1L)));
      }

      return true;
   }

   private AlterUser parseAlterUser() {
      String var1 = this.readIdentifier();
      AlterUser var2;
      if (this.readIf(71)) {
         var2 = new AlterUser(this.session);
         var2.setType(19);
         var2.setUser(this.database.getUser(var1));
         if (this.readIf("PASSWORD")) {
            var2.setPassword(this.readExpression());
         } else {
            if (!this.readIf("SALT")) {
               throw this.getSyntaxError();
            }

            var2.setSalt(this.readExpression());
            this.read("HASH");
            var2.setHash(this.readExpression());
         }

         return var2;
      } else if (this.readIf("RENAME")) {
         this.read(76);
         var2 = new AlterUser(this.session);
         var2.setType(18);
         var2.setUser(this.database.getUser(var1));
         var2.setNewName(this.readIdentifier());
         return var2;
      } else if (this.readIf("ADMIN")) {
         var2 = new AlterUser(this.session);
         var2.setType(17);
         User var3 = this.database.getUser(var1);
         var2.setUser(var3);
         if (this.readIf(77)) {
            var2.setAdmin(true);
         } else {
            if (!this.readIf(31)) {
               throw this.getSyntaxError();
            }

            var2.setAdmin(false);
         }

         return var2;
      } else {
         throw this.getSyntaxError();
      }
   }

   private void readIfEqualOrTo() {
      if (!this.readIf(95)) {
         this.readIf(76);
      }

   }

   private Prepared parseSet() {
      Set var1;
      if (this.readIf(101)) {
         var1 = new Set(this.session, 29);
         var1.setString(this.readIdentifier());
         this.readIfEqualOrTo();
         var1.setExpression(this.readExpression());
         return var1;
      } else if (this.readIf("AUTOCOMMIT")) {
         this.readIfEqualOrTo();
         return new TransactionCommand(this.session, this.readBooleanSetting() ? 69 : 70);
      } else if (this.readIf("EXCLUSIVE")) {
         this.readIfEqualOrTo();
         var1 = new Set(this.session, 27);
         var1.setExpression(this.readExpression());
         return var1;
      } else if (this.readIf("IGNORECASE")) {
         this.readIfEqualOrTo();
         var1 = new Set(this.session, 0);
         var1.setInt(this.readBooleanSetting() ? 1 : 0);
         return var1;
      } else {
         AlterUser var9;
         if (this.readIf("PASSWORD")) {
            this.readIfEqualOrTo();
            var9 = new AlterUser(this.session);
            var9.setType(19);
            var9.setUser(this.session.getUser());
            var9.setPassword(this.readExpression());
            return var9;
         } else if (this.readIf("SALT")) {
            this.readIfEqualOrTo();
            var9 = new AlterUser(this.session);
            var9.setType(19);
            var9.setUser(this.session.getUser());
            var9.setSalt(this.readExpression());
            this.read("HASH");
            var9.setHash(this.readExpression());
            return var9;
         } else if (this.readIf("MODE")) {
            this.readIfEqualOrTo();
            var1 = new Set(this.session, 2);
            var1.setString(this.readIdentifier());
            return var1;
         } else if (this.readIf("DATABASE")) {
            this.readIfEqualOrTo();
            this.read("COLLATION");
            return this.parseSetCollation();
         } else if (this.readIf("COLLATION")) {
            this.readIfEqualOrTo();
            return this.parseSetCollation();
         } else if (this.readIf("CLUSTER")) {
            this.readIfEqualOrTo();
            var1 = new Set(this.session, 12);
            var1.setString(this.readString());
            return var1;
         } else if (this.readIf("DATABASE_EVENT_LISTENER")) {
            this.readIfEqualOrTo();
            var1 = new Set(this.session, 14);
            var1.setString(this.readString());
            return var1;
         } else {
            int var7;
            if (this.readIf("ALLOW_LITERALS")) {
               this.readIfEqualOrTo();
               var1 = new Set(this.session, 21);
               if (this.readIf(3)) {
                  var7 = 2;
               } else if (this.readIf("NONE")) {
                  var7 = 0;
               } else if (this.readIf("NUMBERS")) {
                  var7 = 1;
               } else {
                  var7 = this.readNonNegativeInt();
               }

               var1.setInt(var7);
               return var1;
            } else if (this.readIf("DEFAULT_TABLE_TYPE")) {
               this.readIfEqualOrTo();
               var1 = new Set(this.session, 6);
               if (this.readIf("MEMORY")) {
                  var7 = 1;
               } else if (this.readIf("CACHED")) {
                  var7 = 0;
               } else {
                  var7 = this.readNonNegativeInt();
               }

               var1.setInt(var7);
               return var1;
            } else if (this.readIf("SCHEMA")) {
               this.readIfEqualOrTo();
               var1 = new Set(this.session, 22);
               var1.setExpression(this.readExpressionOrIdentifier());
               return var1;
            } else if (this.readIf("CATALOG")) {
               this.readIfEqualOrTo();
               var1 = new Set(this.session, 40);
               var1.setExpression(this.readExpressionOrIdentifier());
               return var1;
            } else {
               ArrayList var2;
               if (this.readIf(SetTypes.getTypeName(24))) {
                  this.readIfEqualOrTo();
                  var1 = new Set(this.session, 24);
                  var2 = Utils.newSmallArrayList();

                  do {
                     var2.add(this.readIdentifier());
                  } while(this.readIf(109));

                  var1.setStringArray((String[])var2.toArray(new String[0]));
                  return var1;
               } else if (this.readIf("JAVA_OBJECT_SERIALIZER")) {
                  this.readIfEqualOrTo();
                  var1 = new Set(this.session, 32);
                  var1.setString(this.readString());
                  return var1;
               } else if (this.readIf("IGNORE_CATALOGS")) {
                  this.readIfEqualOrTo();
                  var1 = new Set(this.session, 39);
                  var1.setInt(this.readBooleanSetting() ? 1 : 0);
                  return var1;
               } else if (this.readIf("SESSION")) {
                  this.read("CHARACTERISTICS");
                  this.read(7);
                  this.read("TRANSACTION");
                  return this.parseSetTransactionMode();
               } else if (this.readIf("TRANSACTION")) {
                  return this.parseSetTransactionMode();
               } else if (this.readIf("TIME")) {
                  this.read("ZONE");
                  var1 = new Set(this.session, 42);
                  if (!this.readIf("LOCAL")) {
                     var1.setExpression(this.readExpression());
                  }

                  return var1;
               } else if (!this.readIf("NON_KEYWORDS")) {
                  if (this.readIf("DEFAULT_NULL_ORDERING")) {
                     this.readIfEqualOrTo();
                     var1 = new Set(this.session, 44);
                     var1.setString(this.readIdentifier());
                     return var1;
                  } else if (this.readIf("LOG")) {
                     throw DbException.getUnsupportedException("LOG");
                  } else {
                     String var6 = this.upperName(this.currentToken);
                     if (ConnectionInfo.isIgnoredByParser(var6)) {
                        this.read();
                        this.readIfEqualOrTo();
                        this.read();
                        return new NoOperation(this.session);
                     } else {
                        var7 = SetTypes.getType(var6);
                        if (var7 >= 0) {
                           this.read();
                           this.readIfEqualOrTo();
                           Set var8 = new Set(this.session, var7);
                           var8.setExpression(this.readExpression());
                           return var8;
                        } else {
                           Mode.ModeEnum var3 = this.database.getMode().getEnum();
                           if (var3 != Mode.ModeEnum.REGULAR) {
                              Prepared var4 = this.readSetCompatibility(var3);
                              if (var4 != null) {
                                 return var4;
                              }
                           }

                           if (this.session.isQuirksMode()) {
                              switch (var6) {
                                 case "BINARY_COLLATION":
                                 case "UUID_COLLATION":
                                    this.read();
                                    this.readIfEqualOrTo();
                                    this.readIdentifier();
                                    return new NoOperation(this.session);
                              }
                           }

                           throw this.getSyntaxError();
                        }
                     }
                  }
               } else {
                  this.readIfEqualOrTo();
                  var1 = new Set(this.session, 41);
                  var2 = Utils.newSmallArrayList();
                  if (this.currentTokenType != 93 && this.currentTokenType != 115) {
                     do {
                        if (this.currentTokenType < 2 || this.currentTokenType > 91) {
                           throw this.getSyntaxError();
                        }

                        var2.add(StringUtils.toUpperEnglish(this.currentToken));
                        this.read();
                     } while(this.readIf(109));
                  }

                  var1.setStringArray((String[])var2.toArray(new String[0]));
                  return var1;
               }
            }
         }
      }
   }

   private Prepared parseSetTransactionMode() {
      this.read("ISOLATION");
      this.read("LEVEL");
      IsolationLevel var1;
      if (this.readIf("READ")) {
         if (this.readIf("UNCOMMITTED")) {
            var1 = IsolationLevel.READ_UNCOMMITTED;
         } else {
            this.read("COMMITTED");
            var1 = IsolationLevel.READ_COMMITTED;
         }
      } else if (this.readIf("REPEATABLE")) {
         this.read("READ");
         var1 = IsolationLevel.REPEATABLE_READ;
      } else if (this.readIf("SNAPSHOT")) {
         var1 = IsolationLevel.SNAPSHOT;
      } else {
         this.read("SERIALIZABLE");
         var1 = IsolationLevel.SERIALIZABLE;
      }

      return new SetSessionCharacteristics(this.session, var1);
   }

   private Expression readExpressionOrIdentifier() {
      return (Expression)(this.isIdentifier() ? ValueExpression.get(ValueVarchar.get(this.readIdentifier())) : this.readExpression());
   }

   private Prepared parseUse() {
      this.readIfEqualOrTo();
      Set var1 = new Set(this.session, 22);
      var1.setExpression(ValueExpression.get(ValueVarchar.get(this.readIdentifier())));
      return var1;
   }

   private Set parseSetCollation() {
      Set var1 = new Set(this.session, 11);
      String var2 = this.readIdentifier();
      var1.setString(var2);
      if (this.equalsToken(var2, "OFF")) {
         return var1;
      } else {
         Collator var3 = CompareMode.getCollator(var2);
         if (var3 == null) {
            throw DbException.getInvalidValueException("collation", var2);
         } else {
            if (this.readIf("STRENGTH")) {
               if (this.readIf(63)) {
                  var1.setInt(0);
               } else if (this.readIf("SECONDARY")) {
                  var1.setInt(1);
               } else if (this.readIf("TERTIARY")) {
                  var1.setInt(2);
               } else if (this.readIf("IDENTICAL")) {
                  var1.setInt(3);
               }
            } else {
               var1.setInt(var3.getStrength());
            }

            return var1;
         }
      }
   }

   private Prepared readSetCompatibility(Mode.ModeEnum var1) {
      Set var2;
      switch (var1) {
         case Derby:
            if (this.readIf("CREATE")) {
               this.readIfEqualOrTo();
               this.read();
               return new NoOperation(this.session);
            }
            break;
         case HSQLDB:
            if (this.readIf("LOGSIZE")) {
               this.readIfEqualOrTo();
               var2 = new Set(this.session, 1);
               var2.setExpression(this.readExpression());
               return var2;
            }
            break;
         case MySQL:
            if (this.readIf("FOREIGN_KEY_CHECKS")) {
               this.readIfEqualOrTo();
               var2 = new Set(this.session, 25);
               var2.setExpression(this.readExpression());
               return var2;
            }

            if (this.readIf("NAMES")) {
               this.readIfEqualOrTo();
               this.read();
               return new NoOperation(this.session);
            }
            break;
         case PostgreSQL:
            if (this.readIf("STATEMENT_TIMEOUT")) {
               this.readIfEqualOrTo();
               var2 = new Set(this.session, 30);
               var2.setInt(this.readNonNegativeInt());
               return var2;
            }

            if (this.readIf("CLIENT_ENCODING") || this.readIf("CLIENT_MIN_MESSAGES") || this.readIf("JOIN_COLLAPSE_LIMIT")) {
               this.readIfEqualOrTo();
               this.read();
               return new NoOperation(this.session);
            }

            if (this.readIf("DATESTYLE")) {
               this.readIfEqualOrTo();
               if (!this.readIf("ISO")) {
                  String var7 = this.readString();
                  if (!this.equalsToken(var7, "ISO")) {
                     throw this.getSyntaxError();
                  }
               }

               return new NoOperation(this.session);
            }

            if (this.readIf("SEARCH_PATH")) {
               this.readIfEqualOrTo();
               var2 = new Set(this.session, 24);
               ArrayList var3 = Utils.newSmallArrayList();
               String var4 = this.database.sysIdentifier("PG_CATALOG");
               boolean var5 = false;

               do {
                  String var6 = this.currentTokenType == 94 ? this.readString() : this.readIdentifier();
                  if (!"$user".equals(var6)) {
                     if (var4.equals(var6)) {
                        var5 = true;
                     }

                     var3.add(var6);
                  }
               } while(this.readIf(109));

               if (!var5 && this.database.findSchema(var4) != null) {
                  var3.add(0, var4);
               }

               var2.setStringArray((String[])var3.toArray(new String[0]));
               return var2;
            }
      }

      return null;
   }

   private RunScriptCommand parseRunScript() {
      RunScriptCommand var1 = new RunScriptCommand(this.session);
      this.read(35);
      var1.setFileNameExpr(this.readExpression());
      if (this.readIf("COMPRESSION")) {
         var1.setCompressionAlgorithm(this.readIdentifier());
      }

      if (this.readIf("CIPHER")) {
         var1.setCipher(this.readIdentifier());
         if (this.readIf("PASSWORD")) {
            var1.setPassword(this.readExpression());
         }
      }

      if (this.readIf("CHARSET")) {
         var1.setCharset(Charset.forName(this.readString()));
      }

      if (this.readIf("FROM_1X")) {
         var1.setFrom1X();
      } else {
         if (this.readIf("QUIRKS_MODE")) {
            var1.setQuirksMode(true);
         }

         if (this.readIf("VARIABLE_BINARY")) {
            var1.setVariableBinary(true);
         }
      }

      return var1;
   }

   private ScriptCommand parseScript() {
      ScriptCommand var1 = new ScriptCommand(this.session);
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      if (this.readIf("NODATA")) {
         var2 = false;
      } else {
         if (this.readIf("SIMPLE")) {
            var7 = true;
         }

         if (this.readIf("COLUMNS")) {
            var8 = true;
         }
      }

      if (this.readIf("NOPASSWORDS")) {
         var3 = false;
      }

      if (this.readIf("NOSETTINGS")) {
         var4 = false;
      }

      if (this.readIf("NOVERSION")) {
         var5 = false;
      }

      if (this.readIf("DROP")) {
         var6 = true;
      }

      if (this.readIf("BLOCKSIZE")) {
         long var9 = this.readLong();
         var1.setLobBlockSize(var9);
      }

      var1.setData(var2);
      var1.setPasswords(var3);
      var1.setSettings(var4);
      var1.setVersion(var5);
      var1.setDrop(var6);
      var1.setSimple(var7);
      var1.setWithColumns(var8);
      if (this.readIf(76)) {
         var1.setFileNameExpr(this.readExpression());
         if (this.readIf("COMPRESSION")) {
            var1.setCompressionAlgorithm(this.readIdentifier());
         }

         if (this.readIf("CIPHER")) {
            var1.setCipher(this.readIdentifier());
            if (this.readIf("PASSWORD")) {
               var1.setPassword(this.readExpression());
            }
         }

         if (this.readIf("CHARSET")) {
            var1.setCharset(Charset.forName(this.readString()));
         }
      }

      if (this.readIf("SCHEMA")) {
         HashSet var11 = new HashSet();

         do {
            var11.add(this.readIdentifier());
         } while(this.readIf(109));

         var1.setSchemaNames(var11);
      } else if (this.readIf(75)) {
         ArrayList var12 = Utils.newSmallArrayList();

         do {
            var12.add(this.readTableOrView());
         } while(this.readIf(109));

         var1.setTables(var12);
      }

      return var1;
   }

   private boolean isDualTable(String var1) {
      return (this.schemaName == null || this.equalsToken(this.schemaName, "SYS")) && this.equalsToken("DUAL", var1) || this.database.getMode().sysDummy1 && (this.schemaName == null || this.equalsToken(this.schemaName, "SYSIBM")) && this.equalsToken("SYSDUMMY1", var1);
   }

   private Table readTableOrView() {
      return this.readTableOrView(this.readIdentifierWithSchema((String)null));
   }

   private Table readTableOrView(String var1) {
      Table var2;
      if (this.schemaName != null) {
         var2 = this.getSchema().resolveTableOrView(this.session, var1);
         if (var2 != null) {
            return var2;
         }
      } else {
         var2 = this.database.getSchema(this.session.getCurrentSchemaName()).resolveTableOrView(this.session, var1);
         if (var2 != null) {
            return var2;
         }

         String[] var3 = this.session.getSchemaSearchPath();
         if (var3 != null) {
            String[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String var7 = var4[var6];
               Schema var8 = this.database.getSchema(var7);
               var2 = var8.resolveTableOrView(this.session, var1);
               if (var2 != null) {
                  return var2;
               }
            }
         }
      }

      if (this.isDualTable(var1)) {
         return new DualTable(this.database);
      } else {
         throw this.getTableOrViewNotFoundDbException(var1);
      }
   }

   private DbException getTableOrViewNotFoundDbException(String var1) {
      if (this.schemaName != null) {
         return this.getTableOrViewNotFoundDbException(this.schemaName, var1);
      } else {
         String var2 = this.session.getCurrentSchemaName();
         String[] var3 = this.session.getSchemaSearchPath();
         if (var3 == null) {
            return this.getTableOrViewNotFoundDbException(Collections.singleton(var2), var1);
         } else {
            LinkedHashSet var4 = new LinkedHashSet();
            var4.add(var2);
            var4.addAll(Arrays.asList(var3));
            return this.getTableOrViewNotFoundDbException((java.util.Set)var4, var1);
         }
      }
   }

   private DbException getTableOrViewNotFoundDbException(String var1, String var2) {
      return this.getTableOrViewNotFoundDbException(Collections.singleton(var1), var2);
   }

   private DbException getTableOrViewNotFoundDbException(java.util.Set<String> var1, String var2) {
      if (this.database != null && this.database.getFirstUserTable() != null) {
         if (this.database.getSettings().caseInsensitiveIdentifiers) {
            return DbException.get(42102, (String)var2);
         } else {
            TreeSet var3 = new TreeSet();
            Iterator var4 = var1.iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               this.findTableNameCandidates(var5, var2, var3);
            }

            return var3.isEmpty() ? DbException.get(42102, (String)var2) : DbException.get(42103, (String[])(var2, String.join(", ", var3)));
         }
      } else {
         return DbException.get(42104, (String)var2);
      }
   }

   private void findTableNameCandidates(String var1, String var2, java.util.Set<String> var3) {
      Schema var4 = this.database.getSchema(var1);
      String var5 = StringUtils.toUpperEnglish(var2);
      Collection var6 = var4.getAllTablesAndViews(this.session);
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         Table var8 = (Table)var7.next();
         String var9 = var8.getName();
         if (var5.equals(StringUtils.toUpperEnglish(var9))) {
            var3.add(var9);
         }
      }

   }

   private UserDefinedFunction findUserDefinedFunctionWithinPath(Schema var1, String var2) {
      if (var1 != null) {
         return var1.findFunctionOrAggregate(var2);
      } else {
         var1 = this.database.getSchema(this.session.getCurrentSchemaName());
         UserDefinedFunction var3 = var1.findFunctionOrAggregate(var2);
         if (var3 != null) {
            return var3;
         } else {
            String[] var4 = this.session.getSchemaSearchPath();
            if (var4 != null) {
               String[] var5 = var4;
               int var6 = var4.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  String var8 = var5[var7];
                  Schema var9 = this.database.getSchema(var8);
                  if (var9 != var1) {
                     var3 = var9.findFunctionOrAggregate(var2);
                     if (var3 != null) {
                        return var3;
                     }
                  }
               }
            }

            return null;
         }
      }
   }

   private Sequence findSequence(String var1, String var2) {
      Sequence var3 = this.database.getSchema(var1).findSequence(var2);
      if (var3 != null) {
         return var3;
      } else {
         String[] var4 = this.session.getSchemaSearchPath();
         if (var4 != null) {
            String[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var5[var7];
               var3 = this.database.getSchema(var8).findSequence(var2);
               if (var3 != null) {
                  return var3;
               }
            }
         }

         return null;
      }
   }

   private Sequence readSequence() {
      String var1 = this.readIdentifierWithSchema((String)null);
      if (this.schemaName != null) {
         return this.getSchema().getSequence(var1);
      } else {
         Sequence var2 = this.findSequence(this.session.getCurrentSchemaName(), var1);
         if (var2 != null) {
            return var2;
         } else {
            throw DbException.get(90036, var1);
         }
      }
   }

   private Prepared parseAlterTable() {
      boolean var1 = this.readIfExists(false);
      String var2 = this.readIdentifierWithSchema();
      Schema var3 = this.getSchema();
      if (this.readIf("ADD")) {
         DefineCommand var5 = this.parseTableConstraintIf(var2, var3, var1);
         return (Prepared)(var5 != null ? var5 : this.parseAlterTableAddColumn(var2, var3, var1));
      } else if (this.readIf(71)) {
         return this.parseAlterTableSet(var3, var2, var1);
      } else if (this.readIf("RENAME")) {
         return this.parseAlterTableRename(var3, var2, var1);
      } else if (this.readIf("DROP")) {
         return this.parseAlterTableDrop(var3, var2, var1);
      } else if (this.readIf("ALTER")) {
         return this.parseAlterTableAlter(var3, var2, var1);
      } else {
         Mode var4 = this.database.getMode();
         if (!var4.alterTableExtensionsMySQL && !var4.alterTableModifyColumn) {
            throw this.getSyntaxError();
         } else {
            return this.parseAlterTableCompatibility(var3, var2, var1, var4);
         }
      }
   }

   private Prepared parseAlterTableAlter(Schema var1, String var2, boolean var3) {
      this.readIf("COLUMN");
      boolean var4 = this.readIfExists(false);
      String var5 = this.readIdentifier();
      Column var6 = this.columnIfTableExists(var1, var2, var5, var3, var4);
      if (this.readIf("RENAME")) {
         this.read(76);
         AlterTableRenameColumn var10 = new AlterTableRenameColumn(this.session, var1);
         var10.setTableName(var2);
         var10.setIfTableExists(var3);
         var10.setIfExists(var4);
         var10.setOldColumnName(var5);
         String var8 = this.readIdentifier();
         var10.setNewColumnName(var8);
         return var10;
      } else {
         AlterTableAlterColumn var9;
         if (this.readIf("DROP")) {
            if (this.readIf(25)) {
               if (this.readIf(60)) {
                  this.read(58);
                  var9 = new AlterTableAlterColumn(this.session, var1);
                  var9.setTableName(var2);
                  var9.setIfTableExists(var3);
                  var9.setOldColumn(var6);
                  var9.setType(100);
                  var9.setBooleanFlag(false);
                  return var9;
               } else {
                  return this.getAlterTableAlterColumnDropDefaultExpression(var1, var2, var3, var6, 10);
               }
            } else if (this.readIf("EXPRESSION")) {
               return this.getAlterTableAlterColumnDropDefaultExpression(var1, var2, var3, var6, 98);
            } else if (this.readIf("IDENTITY")) {
               return this.getAlterTableAlterColumnDropDefaultExpression(var1, var2, var3, var6, 99);
            } else if (this.readIf(60)) {
               this.read("UPDATE");
               var9 = new AlterTableAlterColumn(this.session, var1);
               var9.setTableName(var2);
               var9.setIfTableExists(var3);
               var9.setOldColumn(var6);
               var9.setType(90);
               var9.setDefaultExpression((Expression)null);
               return var9;
            } else {
               this.read(57);
               this.read(58);
               var9 = new AlterTableAlterColumn(this.session, var1);
               var9.setTableName(var2);
               var9.setIfTableExists(var3);
               var9.setOldColumn(var6);
               var9.setType(9);
               return var9;
            }
         } else if (this.readIf("TYPE")) {
            return this.parseAlterTableAlterColumnDataType(var1, var2, var5, var3, var4);
         } else if (this.readIf("SELECTIVITY")) {
            var9 = new AlterTableAlterColumn(this.session, var1);
            var9.setTableName(var2);
            var9.setIfTableExists(var3);
            var9.setType(13);
            var9.setOldColumn(var6);
            var9.setSelectivity(this.readExpression());
            return var9;
         } else {
            Prepared var7 = this.parseAlterTableAlterColumnIdentity(var1, var2, var3, var6);
            if (var7 != null) {
               return var7;
            } else {
               return (Prepared)(this.readIf(71) ? this.parseAlterTableAlterColumnSet(var1, var2, var3, var4, var5, var6) : this.parseAlterTableAlterColumnType(var1, var2, var5, var3, var4, true));
            }
         }
      }
   }

   private Prepared getAlterTableAlterColumnDropDefaultExpression(Schema var1, String var2, boolean var3, Column var4, int var5) {
      AlterTableAlterColumn var6 = new AlterTableAlterColumn(this.session, var1);
      var6.setTableName(var2);
      var6.setIfTableExists(var3);
      var6.setOldColumn(var4);
      var6.setType(var5);
      var6.setDefaultExpression((Expression)null);
      return var6;
   }

   private Prepared parseAlterTableAlterColumnIdentity(Schema var1, String var2, boolean var3, Column var4) {
      int var5 = this.tokenIndex;
      Boolean var6 = null;
      if (this.readIf(71) && this.readIf("GENERATED")) {
         if (this.readIf("ALWAYS")) {
            var6 = true;
         } else {
            this.read("BY");
            this.read(25);
            var6 = false;
         }
      } else {
         this.setTokenIndex(var5);
      }

      SequenceOptions var7 = new SequenceOptions();
      if (!this.parseSequenceOptions(var7, (CreateSequence)null, false, true) && var6 == null) {
         return null;
      } else if (var4 == null) {
         return new NoOperation(this.session);
      } else if (var4.isIdentity()) {
         AlterSequence var10 = new AlterSequence(this.session, var1);
         var10.setColumn(var4, var6);
         var10.setOptions(var7);
         return this.commandIfTableExists(var1, var2, var3, var10);
      } else {
         AlterTableAlterColumn var8 = new AlterTableAlterColumn(this.session, var1);
         this.parseAlterColumnUsingIf(var8);
         var8.setTableName(var2);
         var8.setIfTableExists(var3);
         var8.setType(11);
         var8.setOldColumn(var4);
         Column var9 = var4.getClone();
         var9.setIdentityOptions(var7, var6 != null && var6);
         var8.setNewColumn(var9);
         return var8;
      }
   }

   private Prepared parseAlterTableAlterColumnSet(Schema var1, String var2, boolean var3, boolean var4, String var5, Column var6) {
      if (this.readIf("DATA")) {
         this.read("TYPE");
         return this.parseAlterTableAlterColumnDataType(var1, var2, var5, var3, var4);
      } else {
         AlterTableAlterColumn var7 = new AlterTableAlterColumn(this.session, var1);
         var7.setTableName(var2);
         var7.setIfTableExists(var3);
         var7.setOldColumn(var6);
         NullConstraintType var8 = this.parseNotNullConstraint();
         switch (var8) {
            case NULL_IS_ALLOWED:
               var7.setType(9);
               break;
            case NULL_IS_NOT_ALLOWED:
               var7.setType(8);
               break;
            case NO_NULL_CONSTRAINT_FOUND:
               Expression var9;
               if (this.readIf(25)) {
                  if (this.readIf(60)) {
                     this.read(58);
                     var7.setType(100);
                     var7.setBooleanFlag(true);
                  } else {
                     var9 = this.readExpression();
                     var7.setType(10);
                     var7.setDefaultExpression(var9);
                  }
               } else if (this.readIf(60)) {
                  this.read("UPDATE");
                  var9 = this.readExpression();
                  var7.setType(90);
                  var7.setDefaultExpression(var9);
               } else if (this.readIf("INVISIBLE")) {
                  var7.setType(87);
                  var7.setBooleanFlag(false);
               } else if (this.readIf("VISIBLE")) {
                  var7.setType(87);
                  var7.setBooleanFlag(true);
               }
               break;
            default:
               throw DbException.get(90088, "Internal Error - unhandled case: " + var8.name());
         }

         return var7;
      }
   }

   private Prepared parseAlterTableDrop(Schema var1, String var2, boolean var3) {
      boolean var10;
      if (this.readIf(14)) {
         var10 = this.readIfExists(false);
         String var13 = this.readIdentifierWithSchema(var1.getName());
         var10 = this.readIfExists(var10);
         this.checkSchema(var1);
         AlterTableDropConstraint var15 = new AlterTableDropConstraint(this.session, this.getSchema(), var10);
         var15.setTableName(var2);
         var15.setIfTableExists(var3);
         var15.setConstraintName(var13);
         ConstraintActionType var16 = this.parseCascadeOrRestrict();
         if (var16 != null) {
            var15.setDropAction(var16);
         }

         return var15;
      } else if (this.readIf(63)) {
         this.read(47);
         Table var11 = this.tableIfTableExists(var1, var2, var3);
         if (var11 == null) {
            return new NoOperation(this.session);
         } else {
            Index var12 = var11.getPrimaryKey();
            DropIndex var14 = new DropIndex(this.session, var1);
            var14.setIndexName(var12.getName());
            return var14;
         }
      } else {
         if (this.database.getMode().alterTableExtensionsMySQL) {
            Prepared var4 = this.parseAlterTableDropCompatibility(var1, var2, var3);
            if (var4 != null) {
               return var4;
            }
         }

         this.readIf("COLUMN");
         var10 = this.readIfExists(false);
         ArrayList var5 = new ArrayList();
         Table var6 = this.tableIfTableExists(var1, var2, var3);
         boolean var7 = this.readIf(105);

         do {
            String var8 = this.readIdentifier();
            if (var6 != null) {
               Column var9 = var6.getColumn(var8, var10);
               if (var9 != null) {
                  var5.add(var9);
               }
            }
         } while(this.readIf(109));

         if (var7) {
            this.read(106);
         }

         if (var6 != null && !var5.isEmpty()) {
            AlterTableAlterColumn var17 = new AlterTableAlterColumn(this.session, var1);
            var17.setType(12);
            var17.setTableName(var2);
            var17.setIfTableExists(var3);
            var17.setColumnsToRemove(var5);
            return var17;
         } else {
            return new NoOperation(this.session);
         }
      }
   }

   private Prepared parseAlterTableDropCompatibility(Schema var1, String var2, boolean var3) {
      boolean var4;
      String var5;
      AlterTableDropConstraint var6;
      if (this.readIf(34)) {
         this.read(47);
         var4 = this.readIfExists(false);
         var5 = this.readIdentifierWithSchema(var1.getName());
         this.checkSchema(var1);
         var6 = new AlterTableDropConstraint(this.session, this.getSchema(), var4);
         var6.setTableName(var2);
         var6.setIfTableExists(var3);
         var6.setConstraintName(var5);
         return var6;
      } else if (this.readIf("INDEX")) {
         var4 = this.readIfExists(false);
         var5 = this.readIdentifierWithSchema(var1.getName());
         if (var1.findIndex(this.session, var5) != null) {
            DropIndex var7 = new DropIndex(this.session, this.getSchema());
            var7.setIndexName(var5);
            return this.commandIfTableExists(var1, var2, var3, var7);
         } else {
            var6 = new AlterTableDropConstraint(this.session, this.getSchema(), var4);
            var6.setTableName(var2);
            var6.setIfTableExists(var3);
            var6.setConstraintName(var5);
            return var6;
         }
      } else {
         return null;
      }
   }

   private Prepared parseAlterTableRename(Schema var1, String var2, boolean var3) {
      String var4;
      if (this.readIf("COLUMN")) {
         var4 = this.readIdentifier();
         this.read(76);
         AlterTableRenameColumn var7 = new AlterTableRenameColumn(this.session, var1);
         var7.setTableName(var2);
         var7.setIfTableExists(var3);
         var7.setOldColumnName(var4);
         var7.setNewColumnName(this.readIdentifier());
         return var7;
      } else if (this.readIf(14)) {
         var4 = this.readIdentifierWithSchema(var1.getName());
         this.checkSchema(var1);
         this.read(76);
         AlterTableRenameConstraint var6 = new AlterTableRenameConstraint(this.session, var1);
         var6.setTableName(var2);
         var6.setIfTableExists(var3);
         var6.setConstraintName(var4);
         var6.setNewConstraintName(this.readIdentifier());
         return var6;
      } else {
         this.read(76);
         var4 = this.readIdentifierWithSchema(var1.getName());
         this.checkSchema(var1);
         AlterTableRename var5 = new AlterTableRename(this.session, this.getSchema());
         var5.setTableName(var2);
         var5.setNewTableName(var4);
         var5.setIfTableExists(var3);
         var5.setHidden(this.readIf("HIDDEN"));
         return var5;
      }
   }

   private Prepared parseAlterTableSet(Schema var1, String var2, boolean var3) {
      this.read("REFERENTIAL_INTEGRITY");
      byte var4 = 55;
      boolean var5 = this.readBooleanSetting();
      AlterTableSet var6 = new AlterTableSet(this.session, var1, var4, var5);
      var6.setTableName(var2);
      var6.setIfTableExists(var3);
      if (this.readIf(13)) {
         var6.setCheckExisting(true);
      } else if (this.readIf("NOCHECK")) {
         var6.setCheckExisting(false);
      }

      return var6;
   }

   private Prepared parseAlterTableCompatibility(Schema var1, String var2, boolean var3, Mode var4) {
      String var6;
      if (var4.alterTableExtensionsMySQL) {
         if (this.readIf("AUTO_INCREMENT")) {
            this.readIf(95);
            Expression var16 = this.readExpression();
            Table var17 = this.tableIfTableExists(var1, var2, var3);
            if (var17 == null) {
               return new NoOperation(this.session);
            }

            Index var19 = var17.findPrimaryKey();
            if (var19 != null) {
               IndexColumn[] var21 = var19.getIndexColumns();
               int var23 = var21.length;

               for(int var10 = 0; var10 < var23; ++var10) {
                  IndexColumn var11 = var21[var10];
                  Column var12 = var11.column;
                  if (var12.isIdentity()) {
                     AlterSequence var13 = new AlterSequence(this.session, var1);
                     var13.setColumn(var12, (Boolean)null);
                     SequenceOptions var14 = new SequenceOptions();
                     var14.setRestartValue(var16);
                     var13.setOptions(var14);
                     return var13;
                  }
               }
            }

            throw DbException.get(42122, (String)"AUTO_INCREMENT PRIMARY KEY");
         }

         if (this.readIf("CHANGE")) {
            this.readIf("COLUMN");
            String var15 = this.readIdentifier();
            var6 = this.readIdentifier();
            Column var18 = this.columnIfTableExists(var1, var2, var15, var3, false);
            boolean var20 = var18 == null ? true : var18.isNullable();
            this.parseColumnForTable(var6, var20);
            AlterTableRenameColumn var22 = new AlterTableRenameColumn(this.session, var1);
            var22.setTableName(var2);
            var22.setIfTableExists(var3);
            var22.setOldColumnName(var15);
            var22.setNewColumnName(var6);
            return var22;
         }

         if (this.readIf("CONVERT")) {
            this.readIf(76);
            this.readIf("CHARACTER");
            this.readIf(71);
            this.readMySQLCharset();
            if (this.readIf("COLLATE")) {
               this.readMySQLCharset();
            }

            return new NoOperation(this.session);
         }
      }

      if (var4.alterTableModifyColumn && this.readIf("MODIFY")) {
         this.readIf("COLUMN");
         boolean var5 = this.readIf(105);
         var6 = this.readIdentifier();
         NullConstraintType var8 = this.parseNotNullConstraint();
         AlterTableAlterColumn var7;
         switch (var8) {
            case NULL_IS_ALLOWED:
            case NULL_IS_NOT_ALLOWED:
               var7 = new AlterTableAlterColumn(this.session, var1);
               var7.setTableName(var2);
               var7.setIfTableExists(var3);
               Column var9 = this.columnIfTableExists(var1, var2, var6, var3, false);
               var7.setOldColumn(var9);
               if (var8 == Parser.NullConstraintType.NULL_IS_ALLOWED) {
                  var7.setType(9);
               } else {
                  var7.setType(8);
               }
               break;
            case NO_NULL_CONSTRAINT_FOUND:
               var7 = this.parseAlterTableAlterColumnType(var1, var2, var6, var3, false, var4.getEnum() != Mode.ModeEnum.MySQL);
               break;
            default:
               throw DbException.get(90088, "Internal Error - unhandled case: " + var8.name());
         }

         if (var5) {
            this.read(106);
         }

         return var7;
      } else {
         throw this.getSyntaxError();
      }
   }

   private Table tableIfTableExists(Schema var1, String var2, boolean var3) {
      Table var4 = var1.resolveTableOrView(this.session, var2);
      if (var4 == null && !var3) {
         throw this.getTableOrViewNotFoundDbException(var1.getName(), var2);
      } else {
         return var4;
      }
   }

   private Column columnIfTableExists(Schema var1, String var2, String var3, boolean var4, boolean var5) {
      Table var6 = this.tableIfTableExists(var1, var2, var4);
      return var6 == null ? null : var6.getColumn(var3, var5);
   }

   private Prepared commandIfTableExists(Schema var1, String var2, boolean var3, Prepared var4) {
      return (Prepared)(this.tableIfTableExists(var1, var2, var3) == null ? new NoOperation(this.session) : var4);
   }

   private AlterTableAlterColumn parseAlterTableAlterColumnType(Schema var1, String var2, String var3, boolean var4, boolean var5, boolean var6) {
      Column var7 = this.columnIfTableExists(var1, var2, var3, var4, var5);
      Column var8 = this.parseColumnForTable(var3, !var6 || var7 == null || var7.isNullable());
      AlterTableAlterColumn var9 = new AlterTableAlterColumn(this.session, var1);
      this.parseAlterColumnUsingIf(var9);
      var9.setTableName(var2);
      var9.setIfTableExists(var4);
      var9.setType(11);
      var9.setOldColumn(var7);
      var9.setNewColumn(var8);
      return var9;
   }

   private AlterTableAlterColumn parseAlterTableAlterColumnDataType(Schema var1, String var2, String var3, boolean var4, boolean var5) {
      Column var6 = this.columnIfTableExists(var1, var2, var3, var4, var5);
      Column var7 = this.parseColumnWithType(var3);
      if (var6 != null) {
         if (!var6.isNullable()) {
            var7.setNullable(false);
         }

         if (!var6.getVisible()) {
            var7.setVisible(false);
         }

         Expression var8 = var6.getDefaultExpression();
         if (var8 != null) {
            if (var6.isGenerated()) {
               var7.setGeneratedExpression(var8);
            } else {
               var7.setDefaultExpression(this.session, var8);
            }
         }

         var8 = var6.getOnUpdateExpression();
         if (var8 != null) {
            var7.setOnUpdateExpression(this.session, var8);
         }

         Sequence var9 = var6.getSequence();
         if (var9 != null) {
            var7.setIdentityOptions(new SequenceOptions(var9, var7.getType()), var6.isGeneratedAlways());
         }

         String var10 = var6.getComment();
         if (var10 != null) {
            var7.setComment(var10);
         }
      }

      AlterTableAlterColumn var11 = new AlterTableAlterColumn(this.session, var1);
      this.parseAlterColumnUsingIf(var11);
      var11.setTableName(var2);
      var11.setIfTableExists(var4);
      var11.setType(11);
      var11.setOldColumn(var6);
      var11.setNewColumn(var7);
      return var11;
   }

   private AlterTableAlterColumn parseAlterTableAddColumn(String var1, Schema var2, boolean var3) {
      this.readIf("COLUMN");
      AlterTableAlterColumn var4 = new AlterTableAlterColumn(this.session, var2);
      var4.setType(7);
      var4.setTableName(var1);
      var4.setIfTableExists(var3);
      if (this.readIf(105)) {
         var4.setIfNotExists(false);

         do {
            this.parseTableColumnDefinition(var4, var2, var1, false);
         } while(this.readIfMore());
      } else {
         boolean var5 = this.readIfNotExists();
         var4.setIfNotExists(var5);
         this.parseTableColumnDefinition(var4, var2, var1, false);
         this.parseAlterColumnUsingIf(var4);
      }

      if (this.readIf("BEFORE")) {
         var4.setAddBefore(this.readIdentifier());
      } else if (this.readIf("AFTER")) {
         var4.setAddAfter(this.readIdentifier());
      } else if (this.readIf("FIRST")) {
         var4.setAddFirst();
      }

      return var4;
   }

   private void parseAlterColumnUsingIf(AlterTableAlterColumn var1) {
      if (this.readIf(83)) {
         var1.setUsingExpression(this.readExpression());
      }

   }

   private ConstraintActionType parseAction() {
      ConstraintActionType var1 = this.parseCascadeOrRestrict();
      if (var1 != null) {
         return var1;
      } else if (this.readIf("NO")) {
         this.read("ACTION");
         return ConstraintActionType.RESTRICT;
      } else {
         this.read(71);
         if (this.readIf(58)) {
            return ConstraintActionType.SET_NULL;
         } else {
            this.read(25);
            return ConstraintActionType.SET_DEFAULT;
         }
      }
   }

   private ConstraintActionType parseCascadeOrRestrict() {
      if (this.readIf("CASCADE")) {
         return ConstraintActionType.CASCADE;
      } else {
         return this.readIf("RESTRICT") ? ConstraintActionType.RESTRICT : null;
      }
   }

   private DefineCommand parseTableConstraintIf(String var1, Schema var2, boolean var3) {
      String var4 = null;
      String var5 = null;
      boolean var6 = false;
      if (this.readIf(14)) {
         var6 = this.readIfNotExists();
         var4 = this.readIdentifierWithSchema(var2.getName());
         this.checkSchema(var2);
         var5 = this.readCommentIf();
      }

      AlterTableAddConstraint var7;
      String var9;
      switch (this.currentTokenType) {
         case 13:
            this.read();
            var7 = new AlterTableAddConstraint(this.session, var2, 3, var6);
            var7.setCheckExpression(this.readExpression());
            break;
         case 34:
            this.read();
            var7 = new AlterTableAddConstraint(this.session, var2, 5, var6);
            this.read(47);
            this.read(105);
            var7.setIndexColumns(this.parseIndexColumnList());
            if (this.readIf("INDEX")) {
               var9 = this.readIdentifierWithSchema();
               var7.setIndex(var2.findIndex(this.session, var9));
            }

            this.read("REFERENCES");
            this.parseReferences(var7, var2, var1);
            break;
         case 63:
            this.read();
            this.read(47);
            var7 = new AlterTableAddConstraint(this.session, var2, 6, var6);
            if (this.readIf("HASH")) {
               var7.setPrimaryKeyHash(true);
            }

            this.read(105);
            var7.setIndexColumns(this.parseIndexColumnList());
            if (this.readIf("INDEX")) {
               String var12 = this.readIdentifierWithSchema();
               var7.setIndex(this.getSchema().findIndex(this.session, var12));
            }
            break;
         case 80:
            this.read();
            boolean var8 = this.database.getMode().indexDefinitionInCreateTable;
            if (var8) {
               if (!this.readIf(47)) {
                  this.readIf("INDEX");
               }

               if (!this.isToken(105)) {
                  var4 = this.readIdentifier();
               }
            }

            this.read(105);
            var7 = new AlterTableAddConstraint(this.session, var2, 4, var6);
            if (this.readIf(84)) {
               this.read(106);
               var7.setIndexColumns((IndexColumn[])null);
            } else {
               var7.setIndexColumns(this.parseIndexColumnList());
            }

            if (this.readIf("INDEX")) {
               var9 = this.readIdentifierWithSchema();
               var7.setIndex(this.getSchema().findIndex(this.session, var9));
            }

            if (var8 && this.readIf(83)) {
               this.read("BTREE");
            }
            break;
         default:
            if (var4 != null) {
               if (this.expectedList != null) {
                  this.addMultipleExpected(63, 80, 34, 13);
               }

               throw this.getSyntaxError();
            }

            Mode var13 = this.database.getMode();
            if (var13.indexDefinitionInCreateTable) {
               int var10 = this.tokenIndex;
               if (this.readIf(47) || this.readIf("INDEX")) {
                  if (DataType.getTypeByName(this.currentToken, var13) == null) {
                     CreateIndex var11 = new CreateIndex(this.session, var2);
                     var11.setComment(var5);
                     var11.setTableName(var1);
                     var11.setIfTableExists(var3);
                     if (!this.readIf(105)) {
                        var11.setIndexName(this.readIdentifier());
                        this.read(105);
                     }

                     var11.setIndexColumns(this.parseIndexColumnList());
                     if (this.readIf(83)) {
                        this.read("BTREE");
                     }

                     return var11;
                  }

                  this.setTokenIndex(var10);
               }
            }

            return null;
      }

      if (var7.getType() != 6) {
         if (this.readIf("NOCHECK")) {
            var7.setCheckExisting(false);
         } else {
            this.readIf(13);
            var7.setCheckExisting(true);
         }
      }

      var7.setTableName(var1);
      var7.setIfTableExists(var3);
      var7.setConstraintName(var4);
      var7.setComment(var5);
      return var7;
   }

   private void parseReferences(AlterTableAddConstraint var1, Schema var2, String var3) {
      String var4;
      if (this.readIf(105)) {
         var1.setRefTableName(var2, var3);
         var1.setRefIndexColumns(this.parseIndexColumnList());
      } else {
         var4 = this.readIdentifierWithSchema(var2.getName());
         var1.setRefTableName(this.getSchema(), var4);
         if (this.readIf(105)) {
            var1.setRefIndexColumns(this.parseIndexColumnList());
         }
      }

      if (this.readIf("INDEX")) {
         var4 = this.readIdentifierWithSchema();
         var1.setRefIndex(this.getSchema().findIndex(this.session, var4));
      }

      while(this.readIf(60)) {
         if (this.readIf("DELETE")) {
            var1.setDeleteAction(this.parseAction());
         } else {
            this.read("UPDATE");
            var1.setUpdateAction(this.parseAction());
         }
      }

      if (this.readIf(57)) {
         this.read("DEFERRABLE");
      } else {
         this.readIf("DEFERRABLE");
      }

   }

   private CreateLinkedTable parseCreateLinkedTable(boolean var1, boolean var2, boolean var3) {
      this.read(75);
      boolean var4 = this.readIfNotExists();
      String var5 = this.readIdentifierWithSchema();
      CreateLinkedTable var6 = new CreateLinkedTable(this.session, this.getSchema());
      var6.setTemporary(var1);
      var6.setGlobalTemporary(var2);
      var6.setForce(var3);
      var6.setIfNotExists(var4);
      var6.setTableName(var5);
      var6.setComment(this.readCommentIf());
      this.read(105);
      var6.setDriver(this.readString());
      this.read(109);
      var6.setUrl(this.readString());
      this.read(109);
      var6.setUser(this.readString());
      this.read(109);
      var6.setPassword(this.readString());
      this.read(109);
      String var7 = this.readString();
      if (this.readIf(109)) {
         var6.setOriginalSchema(var7);
         var7 = this.readString();
      }

      var6.setOriginalTable(var7);
      this.read(106);
      if (this.readIf("EMIT")) {
         this.read("UPDATES");
         var6.setEmitUpdates(true);
      } else if (this.readIf("READONLY")) {
         var6.setReadOnly(true);
      }

      if (this.readIf("FETCH_SIZE")) {
         var6.setFetchSize(this.readNonNegativeInt());
      }

      if (this.readIf("AUTOCOMMIT")) {
         if (this.readIf("ON")) {
            var6.setAutoCommit(true);
         } else if (this.readIf("OFF")) {
            var6.setAutoCommit(false);
         }
      }

      return var6;
   }

   private CreateTable parseCreateTable(boolean var1, boolean var2, boolean var3) {
      boolean var4 = this.readIfNotExists();
      String var5 = this.readIdentifierWithSchema();
      if (var1 && var2 && this.equalsToken("SESSION", this.schemaName)) {
         this.schemaName = this.session.getCurrentSchemaName();
         var2 = false;
      }

      Schema var6 = this.getSchema();
      CreateTable var7 = new CreateTable(this.session, var6);
      var7.setPersistIndexes(var3);
      var7.setTemporary(var1);
      var7.setGlobalTemporary(var2);
      var7.setIfNotExists(var4);
      var7.setTableName(var5);
      var7.setComment(this.readCommentIf());
      if (this.readIf(105) && !this.readIf(106)) {
         do {
            this.parseTableColumnDefinition(var7, var6, var5, true);
         } while(this.readIfMore());
      }

      if (this.database.getMode().getEnum() == Mode.ModeEnum.MySQL) {
         this.parseCreateTableMySQLTableOptions(var7);
      }

      if (this.readIf("ENGINE")) {
         var7.setTableEngine(this.readIdentifier());
      }

      if (this.readIf(89)) {
         var7.setTableEngineParams(this.readTableEngineParams());
      }

      if (var1) {
         if (this.readIf(60)) {
            this.read("COMMIT");
            if (this.readIf("DROP")) {
               var7.setOnCommitDrop();
            } else if (this.readIf("DELETE")) {
               this.read("ROWS");
               var7.setOnCommitTruncate();
            }
         } else if (this.readIf(57)) {
            if (this.readIf("PERSISTENT")) {
               var7.setPersistData(false);
            } else {
               this.read("LOGGED");
            }
         }

         if (this.readIf("TRANSACTIONAL")) {
            var7.setTransactional(true);
         }
      } else if (!var3 && this.readIf(57)) {
         this.read("PERSISTENT");
         var7.setPersistData(false);
      }

      if (this.readIf("HIDDEN")) {
         var7.setHidden(true);
      }

      if (this.readIf(7)) {
         this.readIf("SORTED");
         var7.setQuery(this.parseQuery());
         if (this.readIf(89)) {
            var7.setWithNoData(this.readIf("NO"));
            this.read("DATA");
         }
      }

      return var7;
   }

   private void parseTableColumnDefinition(CommandWithColumns var1, Schema var2, String var3, boolean var4) {
      DefineCommand var5 = this.parseTableConstraintIf(var3, var2, false);
      if (var5 != null) {
         var1.addConstraintCommand(var5);
      } else {
         String var6 = this.readIdentifier();
         if (var4 && (this.currentTokenType == 109 || this.currentTokenType == 106)) {
            var1.addColumn(new Column(var6, TypeInfo.TYPE_UNKNOWN));
         } else {
            Column var7 = this.parseColumnForTable(var6, true);
            if (var7.hasIdentityOptions() && var7.isPrimaryKey()) {
               var1.addConstraintCommand(newPrimaryKeyConstraintCommand(this.session, var2, var3, var7));
            }

            var1.addColumn(var7);
            this.readColumnConstraints(var1, var2, var3, var7);
         }
      }
   }

   public static AlterTableAddConstraint newPrimaryKeyConstraintCommand(SessionLocal var0, Schema var1, String var2, Column var3) {
      var3.setPrimaryKey(false);
      AlterTableAddConstraint var4 = new AlterTableAddConstraint(var0, var1, 6, false);
      var4.setTableName(var2);
      var4.setIndexColumns(new IndexColumn[]{new IndexColumn(var3.getName())});
      return var4;
   }

   private void readColumnConstraints(CommandWithColumns var1, Schema var2, String var3, Column var4) {
      String var5 = var4.getComment();
      boolean var6 = false;
      boolean var7 = false;
      Mode var9 = this.database.getMode();

      label80:
      do {
         while(true) {
            while(true) {
               while(true) {
                  while(true) {
                     while(true) {
                        String var10;
                        while(true) {
                           if (this.readIf(14)) {
                              var10 = this.readIdentifier();
                              break;
                           }

                           if (var5 != null || (var5 = this.readCommentIf()) == null) {
                              var10 = null;
                              break;
                           }

                           var4.setComment(var5);
                        }

                        if (var6 || !this.readIf(63)) {
                           AlterTableAddConstraint var13;
                           if (!this.readIf(80)) {
                              NullConstraintType var8;
                              if (var7 || (var8 = this.parseNotNullConstraint()) == Parser.NullConstraintType.NO_NULL_CONSTRAINT_FOUND) {
                                 if (!this.readIf(13)) {
                                    if (!this.readIf("REFERENCES")) {
                                       if (var10 != null) {
                                          throw this.getSyntaxError();
                                       }
                                       continue label80;
                                    }

                                    var13 = new AlterTableAddConstraint(this.session, var2, 5, false);
                                    var13.setConstraintName(var10);
                                    var13.setIndexColumns(new IndexColumn[]{new IndexColumn(var4.getName())});
                                    var13.setTableName(var3);
                                    this.parseReferences(var13, var2, var3);
                                    var1.addConstraintCommand(var13);
                                 } else {
                                    var13 = new AlterTableAddConstraint(this.session, var2, 3, false);
                                    var13.setConstraintName(var10);
                                    var13.setTableName(var3);
                                    var13.setCheckExpression(this.readExpression());
                                    var1.addConstraintCommand(var13);
                                 }
                              } else {
                                 var7 = true;
                                 if (var8 == Parser.NullConstraintType.NULL_IS_NOT_ALLOWED) {
                                    var4.setNullable(false);
                                 } else if (var8 == Parser.NullConstraintType.NULL_IS_ALLOWED) {
                                    if (var4.isIdentity()) {
                                       throw DbException.get(90023, var4.getName());
                                    }

                                    var4.setNullable(true);
                                 }
                              }
                           } else {
                              var13 = new AlterTableAddConstraint(this.session, var2, 4, false);
                              var13.setConstraintName(var10);
                              var13.setIndexColumns(new IndexColumn[]{new IndexColumn(var4.getName())});
                              var13.setTableName(var3);
                              var1.addConstraintCommand(var13);
                           }
                        } else {
                           this.read(47);
                           var6 = true;
                           boolean var11 = this.readIf("HASH");
                           AlterTableAddConstraint var12 = new AlterTableAddConstraint(this.session, var2, 6, false);
                           var12.setConstraintName(var10);
                           var12.setPrimaryKeyHash(var11);
                           var12.setTableName(var3);
                           var12.setIndexColumns(new IndexColumn[]{new IndexColumn(var4.getName())});
                           var1.addConstraintCommand(var12);
                        }
                     }
                  }
               }
            }
         }
      } while(var4.getIdentityOptions() == null && this.parseCompatibilityIdentity(var4, var9));

   }

   private boolean parseCompatibilityIdentity(Column var1, Mode var2) {
      if (var2.autoIncrementClause && this.readIf("AUTO_INCREMENT")) {
         this.parseCompatibilityIdentityOptions(var1);
         return true;
      } else if (var2.identityClause && this.readIf("IDENTITY")) {
         this.parseCompatibilityIdentityOptions(var1);
         return true;
      } else {
         return false;
      }
   }

   private void parseCreateTableMySQLTableOptions(CreateTable var1) {
      boolean var2 = false;

      while(true) {
         if (this.readIf("AUTO_INCREMENT")) {
            this.readIf(95);
            Expression var3 = this.readExpression();
            AlterTableAddConstraint var4 = var1.getPrimaryKey();
            if (var4 == null) {
               throw DbException.get(42122, (String)"AUTO_INCREMENT PRIMARY KEY");
            }

            IndexColumn[] var5 = var4.getIndexColumns();
            int var6 = var5.length;
            int var7 = 0;

            label65:
            while(true) {
               if (var7 >= var6) {
                  throw DbException.get(42122, (String)"AUTO_INCREMENT PRIMARY KEY");
               }

               IndexColumn var8 = var5[var7];
               String var9 = var8.columnName;
               Iterator var10 = var1.getColumns().iterator();

               while(var10.hasNext()) {
                  Column var11 = (Column)var10.next();
                  if (this.database.equalsIdentifiers(var11.getName(), var9)) {
                     SequenceOptions var12 = var11.getIdentityOptions();
                     if (var12 != null) {
                        var12.setStartValue(var3);
                        break label65;
                     }
                  }
               }

               ++var7;
            }
         } else if (this.readIf(25)) {
            if (this.readIf("CHARACTER")) {
               this.read(71);
            } else {
               this.readIf("CHARSET");
               this.readIf("COLLATE");
            }

            this.readMySQLCharset();
         } else if (this.readIf("CHARACTER")) {
            this.read(71);
            this.readMySQLCharset();
         } else if (this.readIf("COLLATE")) {
            this.readMySQLCharset();
         } else if (this.readIf("CHARSET")) {
            this.readMySQLCharset();
         } else if (this.readIf("COMMENT")) {
            this.readIf(95);
            var1.setComment(this.readString());
         } else if (this.readIf("ENGINE")) {
            this.readIf(95);
            this.readIdentifier();
         } else {
            if (!this.readIf("ROW_FORMAT")) {
               if (var2) {
                  throw this.getSyntaxError();
               }

               return;
            }

            this.readIf(95);
            this.readIdentifier();
         }

         var2 = this.readIf(109);
      }
   }

   private void readMySQLCharset() {
      this.readIf(95);
      this.readIdentifier();
   }

   private NullConstraintType parseNotNullConstraint(NullConstraintType var1) {
      if (var1 == Parser.NullConstraintType.NO_NULL_CONSTRAINT_FOUND) {
         var1 = this.parseNotNullConstraint();
      }

      return var1;
   }

   private NullConstraintType parseNotNullConstraint() {
      NullConstraintType var1;
      if (this.readIf(57)) {
         this.read(58);
         var1 = Parser.NullConstraintType.NULL_IS_NOT_ALLOWED;
      } else {
         if (!this.readIf(58)) {
            return Parser.NullConstraintType.NO_NULL_CONSTRAINT_FOUND;
         }

         var1 = Parser.NullConstraintType.NULL_IS_ALLOWED;
      }

      if (this.database.getMode().getEnum() == Mode.ModeEnum.Oracle) {
         var1 = this.parseNotNullCompatibility(var1);
      }

      return var1;
   }

   private NullConstraintType parseNotNullCompatibility(NullConstraintType var1) {
      if (this.readIf("ENABLE")) {
         if (!this.readIf("VALIDATE") && this.readIf("NOVALIDATE")) {
            var1 = Parser.NullConstraintType.NULL_IS_ALLOWED;
         }
      } else if (this.readIf("DISABLE")) {
         var1 = Parser.NullConstraintType.NULL_IS_ALLOWED;
         if (!this.readIf("VALIDATE")) {
            this.readIf("NOVALIDATE");
         }
      }

      return var1;
   }

   private CreateSynonym parseCreateSynonym(boolean var1) {
      boolean var2 = this.readIfNotExists();
      String var3 = this.readIdentifierWithSchema();
      Schema var4 = this.getSchema();
      this.read(33);
      String var5 = this.readIdentifierWithSchema();
      Schema var6 = this.getSchema();
      CreateSynonym var7 = new CreateSynonym(this.session, var4);
      var7.setName(var3);
      var7.setSynonymFor(var5);
      var7.setSynonymForSchema(var6);
      var7.setComment(this.readCommentIf());
      var7.setIfNotExists(var2);
      var7.setOrReplace(var1);
      return var7;
   }

   private static int getCompareType(int var0) {
      switch (var0) {
         case 95:
            return 0;
         case 96:
            return 5;
         case 97:
            return 3;
         case 98:
            return 2;
         case 99:
            return 4;
         case 100:
            return 1;
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         default:
            return -1;
         case 107:
            return 8;
      }
   }

   public static String quoteIdentifier(String var0, int var1) {
      if (var0 == null) {
         return "\"\"";
      } else {
         return (var1 & 1) != 0 && ParserUtil.isSimpleIdentifier(var0, false, false) ? var0 : StringUtils.quoteIdentifier(var0);
      }
   }

   public void setLiteralsChecked(boolean var1) {
      this.literalsChecked = var1;
   }

   public void setRightsChecked(boolean var1) {
      this.rightsChecked = var1;
   }

   public void setSuppliedParameters(ArrayList<Parameter> var1) {
      this.suppliedParameters = var1;
   }

   public Expression parseExpression(String var1) {
      this.parameters = Utils.newSmallArrayList();
      this.initialize(var1, (ArrayList)null, false);
      this.read();
      return this.readExpression();
   }

   public Expression parseDomainConstraintExpression(String var1) {
      this.parameters = Utils.newSmallArrayList();
      this.initialize(var1, (ArrayList)null, false);
      this.read();

      Expression var2;
      try {
         this.parseDomainConstraint = true;
         var2 = this.readExpression();
      } finally {
         this.parseDomainConstraint = false;
      }

      return var2;
   }

   public Table parseTableName(String var1) {
      this.parameters = Utils.newSmallArrayList();
      this.initialize(var1, (ArrayList)null, false);
      this.read();
      return this.readTableOrView();
   }

   public Object parseColumnList(String var1, int var2) {
      this.initialize(var1, (ArrayList)null, true);
      int var3 = 0;

      int var4;
      for(var4 = this.tokens.size(); var3 < var4; ++var3) {
         if (((Token)this.tokens.get(var3)).start() >= var2) {
            this.setTokenIndex(var3);
            break;
         }
      }

      this.read(105);
      if (this.readIf(106)) {
         return Utils.EMPTY_INT_ARRAY;
      } else {
         ArrayList var7;
         if (this.isIdentifier()) {
            var7 = Utils.newSmallArrayList();

            while(this.isIdentifier()) {
               var7.add(this.currentToken);
               this.read();
               if (!this.readIfMore()) {
                  return var7.toArray(new String[0]);
               }
            }

            throw this.getSyntaxError();
         } else if (this.currentTokenType != 94) {
            throw this.getSyntaxError();
         } else {
            var7 = Utils.newSmallArrayList();

            do {
               var7.add(this.readInt());
            } while(this.readIfMore());

            var4 = var7.size();
            int[] var5 = new int[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = (Integer)var7.get(var6);
            }

            return var5;
         }
      }
   }

   public int getLastParseIndex() {
      return this.token.start();
   }

   public String toString() {
      return StringUtils.addAsterisk(this.sqlCommand, this.token.start());
   }

   private static enum NullConstraintType {
      NULL_IS_ALLOWED,
      NULL_IS_NOT_ALLOWED,
      NO_NULL_CONSTRAINT_FOUND;
   }
}
