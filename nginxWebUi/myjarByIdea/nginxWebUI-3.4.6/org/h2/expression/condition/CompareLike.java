package org.h2.expression.condition;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.SearchedCase;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;
import org.h2.value.ValueVarcharIgnoreCase;

public final class CompareLike extends Condition {
   private static final int MATCH = 0;
   private static final int ONE = 1;
   private static final int ANY = 2;
   private final CompareMode compareMode;
   private final String defaultEscape;
   private final LikeType likeType;
   private Expression left;
   private final boolean not;
   private final boolean whenOperand;
   private Expression right;
   private Expression escape;
   private boolean isInit;
   private char[] patternChars;
   private String patternString;
   private int[] patternTypes;
   private int patternLength;
   private Pattern patternRegexp;
   private boolean ignoreCase;
   private boolean fastCompare;
   private boolean invalidPattern;
   private boolean shortcutToStartsWith;
   private boolean shortcutToEndsWith;
   private boolean shortcutToContains;

   public CompareLike(Database var1, Expression var2, boolean var3, boolean var4, Expression var5, Expression var6, LikeType var7) {
      this(var1.getCompareMode(), var1.getSettings().defaultEscape, var2, var3, var4, var5, var6, var7);
   }

   public CompareLike(CompareMode var1, String var2, Expression var3, boolean var4, boolean var5, Expression var6, Expression var7, LikeType var8) {
      this.compareMode = var1;
      this.defaultEscape = var2;
      this.likeType = var8;
      this.left = var3;
      this.not = var4;
      this.whenOperand = var5;
      this.right = var6;
      this.escape = var7;
   }

   private static Character getEscapeChar(String var0) {
      return var0 != null && !var0.isEmpty() ? var0.charAt(0) : null;
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.getWhenSQL(this.left.getSQL(var1, var2, 0), var2);
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      if (this.not) {
         var1.append(" NOT");
      }

      switch (this.likeType) {
         case LIKE:
         case ILIKE:
            var1.append(this.likeType == CompareLike.LikeType.LIKE ? " LIKE " : " ILIKE ");
            this.right.getSQL(var1, var2, 0);
            if (this.escape != null) {
               this.escape.getSQL(var1.append(" ESCAPE "), var2, 0);
            }
            break;
         case REGEXP:
            var1.append(" REGEXP ");
            this.right.getSQL(var1, var2, 0);
            break;
         default:
            throw DbException.getUnsupportedException(this.likeType.name());
      }

      return var1;
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      if (this.likeType == CompareLike.LikeType.ILIKE || this.left.getType().getValueType() == 4) {
         this.ignoreCase = true;
      }

      if (this.escape != null) {
         this.escape = this.escape.optimize(var1);
      }

      if (this.whenOperand) {
         return this;
      } else {
         Value var2;
         if (this.left.isValueSet()) {
            var2 = this.left.getValue(var1);
            if (var2 == ValueNull.INSTANCE) {
               return TypedValueExpression.UNKNOWN;
            }
         }

         if (this.right.isValueSet() && (this.escape == null || this.escape.isValueSet())) {
            if (this.left.isValueSet()) {
               return ValueExpression.getBoolean(this.getValue(var1));
            }

            var2 = this.right.getValue(var1);
            if (var2 == ValueNull.INSTANCE) {
               return TypedValueExpression.UNKNOWN;
            }

            Value var3 = this.escape == null ? null : this.escape.getValue(var1);
            if (var3 == ValueNull.INSTANCE) {
               return TypedValueExpression.UNKNOWN;
            }

            String var4 = var2.getString();
            this.initPattern(var4, this.getEscapeChar(var3));
            if (this.invalidPattern) {
               return TypedValueExpression.UNKNOWN;
            }

            if (this.likeType != CompareLike.LikeType.REGEXP && "%".equals(var4)) {
               return (new SearchedCase(new Expression[]{new NullPredicate(this.left, true, false), ValueExpression.getBoolean(!this.not), TypedValueExpression.UNKNOWN})).optimize(var1);
            }

            if (this.isFullMatch()) {
               Object var5 = this.ignoreCase ? ValueVarcharIgnoreCase.get(this.patternString) : ValueVarchar.get(this.patternString);
               ValueExpression var6 = ValueExpression.get((Value)var5);
               return (new Comparison(this.not ? 1 : 0, this.left, var6, false)).optimize(var1);
            }

            this.isInit = true;
         }

         return this;
      }
   }

   private Character getEscapeChar(Value var1) {
      if (var1 == null) {
         return getEscapeChar(this.defaultEscape);
      } else {
         String var2 = var1.getString();
         Character var3;
         if (var2 == null) {
            var3 = getEscapeChar(this.defaultEscape);
         } else if (var2.length() == 0) {
            var3 = null;
         } else {
            if (var2.length() > 1) {
               throw DbException.get(22025, (String)var2);
            }

            var3 = var2.charAt(0);
         }

         return var3;
      }
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (!this.not && !this.whenOperand && this.likeType != CompareLike.LikeType.REGEXP && this.left instanceof ExpressionColumn) {
         ExpressionColumn var3 = (ExpressionColumn)this.left;
         if (var2 == var3.getTableFilter() && TypeInfo.haveSameOrdering(var3.getType(), this.ignoreCase ? TypeInfo.TYPE_VARCHAR_IGNORECASE : TypeInfo.TYPE_VARCHAR)) {
            if (this.right.isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
               if (this.escape == null || this.escape.isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
                  String var4 = this.right.getValue(var1).getString();
                  if (!this.isInit) {
                     Value var5 = this.escape == null ? null : this.escape.getValue(var1);
                     if (var5 == ValueNull.INSTANCE) {
                        throw DbException.getInternalError();
                     }

                     this.initPattern(var4, this.getEscapeChar(var5));
                  }

                  if (!this.invalidPattern) {
                     if (this.patternLength > 0 && this.patternTypes[0] == 0) {
                        if (DataType.isStringType(var3.getColumn().getType().getValueType())) {
                           int var11 = 0;
                           StringBuilder var6 = new StringBuilder();

                           while(var11 < this.patternLength && this.patternTypes[var11] == 0) {
                              var6.append(this.patternChars[var11++]);
                           }

                           String var7 = var6.toString();
                           if (var11 == this.patternLength) {
                              var2.addIndexCondition(IndexCondition.get(0, var3, ValueExpression.get(ValueVarchar.get(var7))));
                           } else if (var7.length() > 0) {
                              var2.addIndexCondition(IndexCondition.get(5, var3, ValueExpression.get(ValueVarchar.get(var7))));
                              char var9 = var7.charAt(var7.length() - 1);

                              for(int var10 = 1; var10 < 2000; ++var10) {
                                 String var8 = var7.substring(0, var7.length() - 1) + (char)(var9 + var10);
                                 if (this.compareMode.compareString(var7, var8, this.ignoreCase) < 0) {
                                    var2.addIndexCondition(IndexCondition.get(2, var3, ValueExpression.get(ValueVarchar.get(var8))));
                                    break;
                                 }
                              }
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }

   public Value getValue(SessionLocal var1) {
      return this.getValue(var1, this.left.getValue(var1));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      return !this.whenOperand ? super.getWhenValue(var1, var2) : this.getValue(var1, var2).isTrue();
   }

   private Value getValue(SessionLocal var1, Value var2) {
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         if (!this.isInit) {
            Value var3 = this.right.getValue(var1);
            if (var3 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }

            String var4 = var3.getString();
            Value var5 = this.escape == null ? null : this.escape.getValue(var1);
            if (var5 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }

            this.initPattern(var4, this.getEscapeChar(var5));
         }

         if (this.invalidPattern) {
            return ValueNull.INSTANCE;
         } else {
            String var6 = var2.getString();
            boolean var7;
            if (this.likeType == CompareLike.LikeType.REGEXP) {
               var7 = this.patternRegexp.matcher(var6).find();
            } else if (this.shortcutToStartsWith) {
               var7 = var6.regionMatches(this.ignoreCase, 0, this.patternString, 0, this.patternLength - 1);
            } else if (this.shortcutToEndsWith) {
               var7 = var6.regionMatches(this.ignoreCase, var6.length() - this.patternLength + 1, this.patternString, 1, this.patternLength - 1);
            } else if (this.shortcutToContains) {
               String var8 = this.patternString.substring(1, this.patternString.length() - 1);
               if (this.ignoreCase) {
                  var7 = containsIgnoreCase(var6, var8);
               } else {
                  var7 = var6.contains(var8);
               }
            } else {
               var7 = this.compareAt(var6, 0, 0, var6.length(), this.patternChars, this.patternTypes);
            }

            return ValueBoolean.get(this.not ^ var7);
         }
      }
   }

   private static boolean containsIgnoreCase(String var0, String var1) {
      int var2 = var1.length();
      if (var2 == 0) {
         return true;
      } else {
         char var3 = Character.toLowerCase(var1.charAt(0));
         char var4 = Character.toUpperCase(var1.charAt(0));

         for(int var5 = var0.length() - var2; var5 >= 0; --var5) {
            char var6 = var0.charAt(var5);
            if ((var6 == var3 || var6 == var4) && var0.regionMatches(true, var5, var1, 0, var2)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean compareAt(String var1, int var2, int var3, int var4, char[] var5, int[] var6) {
      for(; var2 < this.patternLength; ++var2) {
         switch (var6[var2]) {
            case 0:
               if (var3 < var4 && this.compare(var5, var1, var2, var3++)) {
                  break;
               }

               return false;
            case 1:
               if (var3++ >= var4) {
                  return false;
               }
               break;
            case 2:
               ++var2;
               if (var2 >= this.patternLength) {
                  return true;
               }

               while(var3 < var4) {
                  if (this.compare(var5, var1, var2, var3) && this.compareAt(var1, var2, var3, var4, var5, var6)) {
                     return true;
                  }

                  ++var3;
               }

               return false;
            default:
               throw DbException.getInternalError(Integer.toString(var6[var2]));
         }
      }

      return var3 == var4;
   }

   private boolean compare(char[] var1, String var2, int var3, int var4) {
      return var1[var3] == var2.charAt(var4) || !this.fastCompare && this.compareMode.equalsChars(this.patternString, var3, var2, var4, this.ignoreCase);
   }

   public boolean isWhenConditionOperand() {
      return this.whenOperand;
   }

   public boolean test(String var1, String var2, char var3) {
      this.initPattern(var1, var3);
      return this.test(var2);
   }

   public boolean test(String var1) {
      return this.invalidPattern ? false : this.compareAt(var1, 0, 0, var1.length(), this.patternChars, this.patternTypes);
   }

   public void initPattern(String var1, Character var2) {
      if (this.compareMode.getName().equals("OFF") && !this.ignoreCase) {
         this.fastCompare = true;
      }

      if (this.likeType == CompareLike.LikeType.REGEXP) {
         this.patternString = var1;

         try {
            if (this.ignoreCase) {
               this.patternRegexp = Pattern.compile(var1, 2);
            } else {
               this.patternRegexp = Pattern.compile(var1);
            }

         } catch (PatternSyntaxException var8) {
            throw DbException.get(22025, var8, var1);
         }
      } else {
         this.patternLength = 0;
         if (var1 == null) {
            this.patternTypes = null;
            this.patternChars = null;
         } else {
            int var3 = var1.length();
            this.patternChars = new char[var3];
            this.patternTypes = new int[var3];
            boolean var4 = false;

            int var5;
            for(var5 = 0; var5 < var3; ++var5) {
               char var6 = var1.charAt(var5);
               byte var7;
               if (var2 != null && var2 == var6) {
                  if (var5 >= var3 - 1) {
                     this.invalidPattern = true;
                     return;
                  }

                  ++var5;
                  var6 = var1.charAt(var5);
                  var7 = 0;
                  var4 = false;
               } else if (var6 == '%') {
                  if (var4) {
                     continue;
                  }

                  var7 = 2;
                  var4 = true;
               } else if (var6 == '_') {
                  var7 = 1;
               } else {
                  var7 = 0;
                  var4 = false;
               }

               this.patternTypes[this.patternLength] = var7;
               this.patternChars[this.patternLength++] = var6;
            }

            for(var5 = 0; var5 < this.patternLength - 1; ++var5) {
               if (this.patternTypes[var5] == 2 && this.patternTypes[var5 + 1] == 1) {
                  this.patternTypes[var5] = 1;
                  this.patternTypes[var5 + 1] = 2;
               }
            }

            this.patternString = new String(this.patternChars, 0, this.patternLength);
            this.shortcutToStartsWith = false;
            this.shortcutToEndsWith = false;
            this.shortcutToContains = false;
            if (this.compareMode.getName().equals("OFF") && this.patternLength > 1) {
               for(var5 = 0; var5 < this.patternLength && this.patternTypes[var5] == 0; ++var5) {
               }

               if (var5 == this.patternLength - 1 && this.patternTypes[this.patternLength - 1] == 2) {
                  this.shortcutToStartsWith = true;
                  return;
               }
            }

            if (this.compareMode.getName().equals("OFF") && this.patternLength > 1 && this.patternTypes[0] == 2) {
               for(var5 = 1; var5 < this.patternLength && this.patternTypes[var5] == 0; ++var5) {
               }

               if (var5 == this.patternLength) {
                  this.shortcutToEndsWith = true;
                  return;
               }
            }

            if (this.compareMode.getName().equals("OFF") && this.patternLength > 2 && this.patternTypes[0] == 2) {
               for(var5 = 1; var5 < this.patternLength && this.patternTypes[var5] == 0; ++var5) {
               }

               if (var5 == this.patternLength - 1 && this.patternTypes[this.patternLength - 1] == 2) {
                  this.shortcutToContains = true;
               }
            }

         }
      }
   }

   private boolean isFullMatch() {
      if (this.patternTypes == null) {
         return false;
      } else {
         int[] var1 = this.patternTypes;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var1[var3];
            if (var4 != 0) {
               return false;
            }
         }

         return true;
      }
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new CompareLike(this.compareMode, this.defaultEscape, this.left, !this.not, false, this.right, this.escape, this.likeType);
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
      this.right.mapColumns(var1, var2, var3);
      if (this.escape != null) {
         this.escape.mapColumns(var1, var2, var3);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      this.right.setEvaluatable(var1, var2);
      if (this.escape != null) {
         this.escape.setEvaluatable(var1, var2);
      }

   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      this.right.updateAggregate(var1, var2);
      if (this.escape != null) {
         this.escape.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && this.right.isEverything(var1) && (this.escape == null || this.escape.isEverything(var1));
   }

   public int getCost() {
      return this.left.getCost() + this.right.getCost() + 3;
   }

   public int getSubexpressionCount() {
      return this.escape == null ? 2 : 3;
   }

   public Expression getSubexpression(int var1) {
      switch (var1) {
         case 0:
            return this.left;
         case 1:
            return this.right;
         case 2:
            if (this.escape != null) {
               return this.escape;
            }
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   public static enum LikeType {
      LIKE,
      ILIKE,
      REGEXP;
   }
}
