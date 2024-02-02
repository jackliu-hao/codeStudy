/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.ByteString;
/*     */ import com.google.protobuf.Message;
/*     */ import com.mysql.cj.MessageBuilder;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Security;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.mysql.cj.x.protobuf.MysqlxConnection;
/*     */ import com.mysql.cj.x.protobuf.MysqlxCrud;
/*     */ import com.mysql.cj.x.protobuf.MysqlxDatatypes;
/*     */ import com.mysql.cj.x.protobuf.MysqlxExpect;
/*     */ import com.mysql.cj.x.protobuf.MysqlxExpr;
/*     */ import com.mysql.cj.x.protobuf.MysqlxPrepare;
/*     */ import com.mysql.cj.x.protobuf.MysqlxSession;
/*     */ import com.mysql.cj.x.protobuf.MysqlxSql;
/*     */ import com.mysql.cj.xdevapi.CreateIndexParams;
/*     */ import com.mysql.cj.xdevapi.ExprUtil;
/*     */ import com.mysql.cj.xdevapi.FilterParams;
/*     */ import com.mysql.cj.xdevapi.InsertParams;
/*     */ import com.mysql.cj.xdevapi.Schema;
/*     */ import com.mysql.cj.xdevapi.UpdateParams;
/*     */ import com.mysql.cj.xdevapi.UpdateSpec;
/*     */ import java.math.BigInteger;
/*     */ import java.security.DigestException;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.stream.Collectors;
/*     */ import javax.security.auth.callback.Callback;
/*     */ import javax.security.auth.callback.CallbackHandler;
/*     */ import javax.security.auth.callback.NameCallback;
/*     */ import javax.security.auth.callback.PasswordCallback;
/*     */ import javax.security.auth.callback.UnsupportedCallbackException;
/*     */ import javax.security.sasl.Sasl;
/*     */ import javax.security.sasl.SaslClient;
/*     */ import javax.security.sasl.SaslException;
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
/*     */ public class XMessageBuilder
/*     */   implements MessageBuilder<XMessage>
/*     */ {
/*     */   private static final String XPLUGIN_NAMESPACE = "mysqlx";
/*     */   
/*     */   public XMessage buildCapabilitiesGet() {
/* 104 */     return new XMessage((Message)MysqlxConnection.CapabilitiesGet.getDefaultInstance());
/*     */   }
/*     */ 
/*     */   
/*     */   public XMessage buildCapabilitiesSet(Map<String, Object> keyValuePair) {
/* 109 */     MysqlxConnection.Capabilities.Builder capsB = MysqlxConnection.Capabilities.newBuilder();
/* 110 */     keyValuePair.forEach((k, v) -> {
/*     */           MysqlxDatatypes.Any val;
/*     */           
/*     */           if (XServerCapabilities.KEY_SESSION_CONNECT_ATTRS.equals(k) || XServerCapabilities.KEY_COMPRESSION.equals(k)) {
/*     */             MysqlxDatatypes.Object.Builder attrB = MysqlxDatatypes.Object.newBuilder();
/*     */             
/*     */             ((Map)v).forEach(());
/*     */             val = MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(attrB).build();
/*     */           } else {
/*     */             val = ExprUtil.argObjectToScalarAny(v);
/*     */           } 
/*     */           MysqlxConnection.Capability cap = MysqlxConnection.Capability.newBuilder().setName(k).setValue(val).build();
/*     */           capsB.addCapabilities(cap);
/*     */         });
/* 124 */     return new XMessage((Message)MysqlxConnection.CapabilitiesSet.newBuilder().setCapabilities(capsB).build());
/*     */   }
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
/*     */   public XMessage buildDocInsert(String schemaName, String collectionName, List<String> json, boolean upsert) {
/* 142 */     MysqlxCrud.Insert.Builder builder = MysqlxCrud.Insert.newBuilder().setCollection(ExprUtil.buildCollection(schemaName, collectionName));
/* 143 */     if (upsert != builder.getUpsert()) {
/* 144 */       builder.setUpsert(upsert);
/*     */     }
/* 146 */     json.stream().map(str -> MysqlxCrud.Insert.TypedRow.newBuilder().addField(ExprUtil.argObjectToExpr(str, false)).build()).forEach(builder::addRow);
/* 147 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   private MysqlxCrud.Insert.Builder commonRowInsertBuilder(String schemaName, String tableName, InsertParams insertParams) {
/* 164 */     MysqlxCrud.Insert.Builder builder = MysqlxCrud.Insert.newBuilder().setDataModel(MysqlxCrud.DataModel.TABLE).setCollection(ExprUtil.buildCollection(schemaName, tableName));
/* 165 */     if (insertParams.getProjection() != null) {
/* 166 */       builder.addAllProjection((List)insertParams.getProjection());
/*     */     }
/* 168 */     return builder;
/*     */   }
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
/*     */   public XMessage buildRowInsert(String schemaName, String tableName, InsertParams insertParams) {
/* 185 */     MysqlxCrud.Insert.Builder builder = commonRowInsertBuilder(schemaName, tableName, insertParams);
/* 186 */     builder.addAllRow((List)insertParams.getRows());
/* 187 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   private MysqlxCrud.Update.Builder commonDocUpdateBuilder(FilterParams filterParams, List<UpdateSpec> updates) {
/* 201 */     MysqlxCrud.Update.Builder builder = MysqlxCrud.Update.newBuilder().setCollection((MysqlxCrud.Collection)filterParams.getCollection());
/* 202 */     updates.forEach(u -> {
/*     */           MysqlxCrud.UpdateOperation.Builder opBuilder = MysqlxCrud.UpdateOperation.newBuilder();
/*     */           opBuilder.setOperation((MysqlxCrud.UpdateOperation.UpdateType)u.getUpdateType());
/*     */           opBuilder.setSource((MysqlxExpr.ColumnIdentifier)u.getSource());
/*     */           if (u.getValue() != null) {
/*     */             opBuilder.setValue((MysqlxExpr.Expr)u.getValue());
/*     */           }
/*     */           builder.addOperation(opBuilder.build());
/*     */         });
/* 211 */     return builder;
/*     */   }
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
/*     */   public XMessage buildDocUpdate(FilterParams filterParams, List<UpdateSpec> updates) {
/* 225 */     MysqlxCrud.Update.Builder builder = commonDocUpdateBuilder(filterParams, updates);
/* 226 */     applyFilterParams(filterParams, builder::addAllOrder, builder::setLimit, builder::setCriteria, builder::addAllArgs);
/* 227 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   public XMessage buildPrepareDocUpdate(int preparedStatementId, FilterParams filterParams, List<UpdateSpec> updates) {
/* 243 */     MysqlxCrud.Update.Builder updateBuilder = commonDocUpdateBuilder(filterParams, updates);
/* 244 */     applyFilterParams(filterParams, updateBuilder::addAllOrder, updateBuilder::setLimitExpr, updateBuilder::setCriteria);
/*     */     
/* 246 */     MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
/* 247 */     builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.UPDATE).setUpdate(updateBuilder.build()).build());
/* 248 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   private MysqlxCrud.Update.Builder commonRowUpdateBuilder(FilterParams filterParams, UpdateParams updateParams) {
/* 263 */     MysqlxCrud.Update.Builder builder = MysqlxCrud.Update.newBuilder().setDataModel(MysqlxCrud.DataModel.TABLE).setCollection((MysqlxCrud.Collection)filterParams.getCollection());
/* 264 */     ((Map)updateParams.getUpdates()).entrySet().stream()
/* 265 */       .map(e -> MysqlxCrud.UpdateOperation.newBuilder().setOperation(MysqlxCrud.UpdateOperation.UpdateType.SET).setSource((MysqlxExpr.ColumnIdentifier)e.getKey()).setValue((MysqlxExpr.Expr)e.getValue()).build())
/* 266 */       .forEach(builder::addOperation);
/* 267 */     return builder;
/*     */   }
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
/*     */   public XMessage buildRowUpdate(FilterParams filterParams, UpdateParams updateParams) {
/* 281 */     MysqlxCrud.Update.Builder builder = commonRowUpdateBuilder(filterParams, updateParams);
/* 282 */     applyFilterParams(filterParams, builder::addAllOrder, builder::setLimit, builder::setCriteria, builder::addAllArgs);
/* 283 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   public XMessage buildPrepareRowUpdate(int preparedStatementId, FilterParams filterParams, UpdateParams updateParams) {
/* 299 */     MysqlxCrud.Update.Builder updateBuilder = commonRowUpdateBuilder(filterParams, updateParams);
/* 300 */     applyFilterParams(filterParams, updateBuilder::addAllOrder, updateBuilder::setLimitExpr, updateBuilder::setCriteria);
/*     */     
/* 302 */     MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
/* 303 */     builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.UPDATE).setUpdate(updateBuilder.build()).build());
/* 304 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   private MysqlxCrud.Find.Builder commonFindBuilder(FilterParams filterParams) {
/* 317 */     MysqlxCrud.Find.Builder builder = MysqlxCrud.Find.newBuilder().setCollection((MysqlxCrud.Collection)filterParams.getCollection());
/* 318 */     builder.setDataModel(filterParams.isRelational() ? MysqlxCrud.DataModel.TABLE : MysqlxCrud.DataModel.DOCUMENT);
/* 319 */     if (filterParams.getFields() != null) {
/* 320 */       builder.addAllProjection((List)filterParams.getFields());
/*     */     }
/* 322 */     if (filterParams.getGrouping() != null) {
/* 323 */       builder.addAllGrouping((List)filterParams.getGrouping());
/*     */     }
/* 325 */     if (filterParams.getGroupingCriteria() != null) {
/* 326 */       builder.setGroupingCriteria((MysqlxExpr.Expr)filterParams.getGroupingCriteria());
/*     */     }
/* 328 */     if (filterParams.getLock() != null) {
/* 329 */       builder.setLocking(MysqlxCrud.Find.RowLock.forNumber(filterParams.getLock().asNumber()));
/*     */     }
/* 331 */     if (filterParams.getLockOption() != null) {
/* 332 */       builder.setLockingOptions(MysqlxCrud.Find.RowLockOptions.forNumber(filterParams.getLockOption().asNumber()));
/*     */     }
/* 334 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XMessage buildFind(FilterParams filterParams) {
/* 346 */     MysqlxCrud.Find.Builder builder = commonFindBuilder(filterParams);
/* 347 */     applyFilterParams(filterParams, builder::addAllOrder, builder::setLimit, builder::setCriteria, builder::addAllArgs);
/* 348 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   public XMessage buildPrepareFind(int preparedStatementId, FilterParams filterParams) {
/* 362 */     MysqlxCrud.Find.Builder findBuilder = commonFindBuilder(filterParams);
/* 363 */     applyFilterParams(filterParams, findBuilder::addAllOrder, findBuilder::setLimitExpr, findBuilder::setCriteria);
/*     */     
/* 365 */     MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
/* 366 */     builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.FIND).setFind(findBuilder.build()).build());
/* 367 */     return new XMessage((Message)builder.build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MysqlxCrud.Delete.Builder commonDeleteBuilder(FilterParams filterParams) {
/* 379 */     MysqlxCrud.Delete.Builder builder = MysqlxCrud.Delete.newBuilder().setCollection((MysqlxCrud.Collection)filterParams.getCollection());
/* 380 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XMessage buildDelete(FilterParams filterParams) {
/* 392 */     MysqlxCrud.Delete.Builder builder = commonDeleteBuilder(filterParams);
/* 393 */     applyFilterParams(filterParams, builder::addAllOrder, builder::setLimit, builder::setCriteria, builder::addAllArgs);
/* 394 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   public XMessage buildPrepareDelete(int preparedStatementId, FilterParams filterParams) {
/* 408 */     MysqlxCrud.Delete.Builder deleteBuilder = commonDeleteBuilder(filterParams);
/* 409 */     applyFilterParams(filterParams, deleteBuilder::addAllOrder, deleteBuilder::setLimitExpr, deleteBuilder::setCriteria);
/*     */     
/* 411 */     MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
/* 412 */     builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.DELETE).setDelete(deleteBuilder.build()).build());
/* 413 */     return new XMessage((Message)builder.build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MysqlxSql.StmtExecute.Builder commonSqlStatementBuilder(String statement) {
/* 425 */     MysqlxSql.StmtExecute.Builder builder = MysqlxSql.StmtExecute.newBuilder();
/*     */     
/* 427 */     builder.setStmt(ByteString.copyFromUtf8(statement));
/* 428 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XMessage buildSqlStatement(String statement) {
/* 439 */     return buildSqlStatement(statement, (List<Object>)null);
/*     */   }
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
/*     */   public XMessage buildSqlStatement(String statement, List<Object> args) {
/* 452 */     MysqlxSql.StmtExecute.Builder builder = commonSqlStatementBuilder(statement);
/* 453 */     if (args != null) {
/* 454 */       builder.addAllArgs((Iterable)args.stream().map(ExprUtil::argObjectToScalarAny).collect(Collectors.toList()));
/*     */     }
/* 456 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   public XMessage buildPrepareSqlStatement(int preparedStatementId, String statement) {
/* 469 */     MysqlxSql.StmtExecute.Builder stmtExecBuilder = commonSqlStatementBuilder(statement);
/*     */     
/* 471 */     MysqlxPrepare.Prepare.Builder builder = MysqlxPrepare.Prepare.newBuilder().setStmtId(preparedStatementId);
/* 472 */     builder.setStmt(MysqlxPrepare.Prepare.OneOfMessage.newBuilder().setType(MysqlxPrepare.Prepare.OneOfMessage.Type.STMT).setStmtExecute(stmtExecBuilder.build()).build());
/* 473 */     return new XMessage((Message)builder.build());
/*     */   }
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
/*     */   private static void applyFilterParams(FilterParams filterParams, Consumer<List<MysqlxCrud.Order>> setOrder, Consumer<MysqlxCrud.Limit> setLimit, Consumer<MysqlxExpr.Expr> setCriteria, Consumer<List<MysqlxDatatypes.Scalar>> setArgs) {
/* 495 */     filterParams.verifyAllArgsBound();
/* 496 */     if (filterParams.getOrder() != null) {
/* 497 */       setOrder.accept((List<MysqlxCrud.Order>)filterParams.getOrder());
/*     */     }
/* 499 */     if (filterParams.getLimit() != null) {
/* 500 */       MysqlxCrud.Limit.Builder lb = MysqlxCrud.Limit.newBuilder().setRowCount(filterParams.getLimit().longValue());
/* 501 */       if (filterParams.getOffset() != null) {
/* 502 */         lb.setOffset(filterParams.getOffset().longValue());
/*     */       }
/* 504 */       setLimit.accept(lb.build());
/*     */     } 
/* 506 */     if (filterParams.getCriteria() != null) {
/* 507 */       setCriteria.accept((MysqlxExpr.Expr)filterParams.getCriteria());
/*     */     }
/* 509 */     if (filterParams.getArgs() != null) {
/* 510 */       setArgs.accept((List<MysqlxDatatypes.Scalar>)filterParams.getArgs());
/*     */     }
/*     */   }
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
/*     */   private static void applyFilterParams(FilterParams filterParams, Consumer<List<MysqlxCrud.Order>> setOrder, Consumer<MysqlxCrud.LimitExpr> setLimit, Consumer<MysqlxExpr.Expr> setCriteria) {
/* 531 */     if (filterParams.getOrder() != null) {
/* 532 */       setOrder.accept((List<MysqlxCrud.Order>)filterParams.getOrder());
/*     */     }
/* 534 */     Object argsList = filterParams.getArgs();
/* 535 */     int numberOfArgs = (argsList == null) ? 0 : ((List)argsList).size();
/* 536 */     if (filterParams.getLimit() != null) {
/* 537 */       MysqlxCrud.LimitExpr.Builder lb = MysqlxCrud.LimitExpr.newBuilder().setRowCount(ExprUtil.buildPlaceholderExpr(numberOfArgs));
/* 538 */       if (filterParams.supportsOffset()) {
/* 539 */         lb.setOffset(ExprUtil.buildPlaceholderExpr(numberOfArgs + 1));
/*     */       }
/* 541 */       setLimit.accept(lb.build());
/*     */     } 
/* 543 */     if (filterParams.getCriteria() != null) {
/* 544 */       setCriteria.accept((MysqlxExpr.Expr)filterParams.getCriteria());
/*     */     }
/*     */   }
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
/*     */   public XMessage buildPrepareExecute(int preparedStatementId, FilterParams filterParams) {
/* 560 */     MysqlxPrepare.Execute.Builder builder = MysqlxPrepare.Execute.newBuilder().setStmtId(preparedStatementId);
/* 561 */     if (filterParams.getArgs() != null) {
/* 562 */       builder.addAllArgs((Iterable)((List)filterParams.getArgs()).stream().map(s -> MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR).setScalar(s).build())
/* 563 */           .collect(Collectors.toList()));
/*     */     }
/* 565 */     if (filterParams.getLimit() != null) {
/* 566 */       builder.addArgs(ExprUtil.anyOf(ExprUtil.scalarOf(filterParams.getLimit().longValue())));
/* 567 */       if (filterParams.supportsOffset()) {
/* 568 */         builder.addArgs(ExprUtil.anyOf(ExprUtil.scalarOf((filterParams.getOffset() != null) ? filterParams.getOffset().longValue() : 0L)));
/*     */       }
/*     */     } 
/* 571 */     return new XMessage((Message)builder.build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XMessage buildPrepareDeallocate(int preparedStatementId) {
/* 583 */     MysqlxPrepare.Deallocate.Builder builder = MysqlxPrepare.Deallocate.newBuilder().setStmtId(preparedStatementId);
/* 584 */     return new XMessage((Message)builder.build());
/*     */   }
/*     */   
/*     */   public XMessage buildCreateCollection(String schemaName, String collectionName, Schema.CreateCollectionOptions options) {
/* 588 */     if (schemaName == null) {
/* 589 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" }));
/*     */     }
/* 591 */     if (collectionName == null) {
/* 592 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "collectionName" }));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 597 */     MysqlxDatatypes.Object.Builder argsBuilder = MysqlxDatatypes.Object.newBuilder().addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(collectionName))).addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName)));
/*     */     
/* 599 */     MysqlxDatatypes.Object.Builder optBuilder = MysqlxDatatypes.Object.newBuilder();
/*     */     
/* 601 */     boolean hasOptions = false;
/* 602 */     if (options.getReuseExisting() != null) {
/* 603 */       hasOptions = true;
/* 604 */       optBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("reuse_existing").setValue(ExprUtil.buildAny(options.getReuseExisting().booleanValue())));
/*     */     } 
/*     */     
/* 607 */     if (options.getValidation() != null) {
/* 608 */       hasOptions = true;
/* 609 */       MysqlxDatatypes.Object.Builder validationBuilder = MysqlxDatatypes.Object.newBuilder();
/* 610 */       if (options.getValidation().getSchema() != null) {
/* 611 */         validationBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(options.getValidation().getSchema())));
/*     */       }
/* 613 */       if (options.getValidation().getLevel() != null) {
/* 614 */         validationBuilder
/* 615 */           .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("level").setValue(ExprUtil.buildAny(options.getValidation().getLevel().name().toLowerCase())));
/*     */       }
/* 617 */       optBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("validation").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(validationBuilder)));
/*     */     } 
/* 619 */     if (hasOptions) {
/* 620 */       argsBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("options").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(optBuilder)));
/*     */     }
/*     */     
/* 623 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_CREATE_COLLECTION, new MysqlxDatatypes.Any[] {
/* 624 */             MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(argsBuilder).build() }));
/*     */   }
/*     */   
/*     */   public XMessage buildModifyCollectionOptions(String schemaName, String collectionName, Schema.ModifyCollectionOptions options) {
/* 628 */     if (schemaName == null) {
/* 629 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" }));
/*     */     }
/* 631 */     if (collectionName == null) {
/* 632 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "collectionName" }));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 637 */     MysqlxDatatypes.Object.Builder argsBuilder = MysqlxDatatypes.Object.newBuilder().addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(collectionName))).addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName)));
/*     */     
/* 639 */     MysqlxDatatypes.Object.Builder optBuilder = MysqlxDatatypes.Object.newBuilder();
/*     */     
/* 641 */     if (options != null && options.getValidation() != null) {
/* 642 */       MysqlxDatatypes.Object.Builder validationBuilder = MysqlxDatatypes.Object.newBuilder();
/* 643 */       if (options.getValidation().getSchema() != null) {
/* 644 */         validationBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(options.getValidation().getSchema())));
/*     */       }
/* 646 */       if (options.getValidation().getLevel() != null) {
/* 647 */         validationBuilder
/* 648 */           .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("level").setValue(ExprUtil.buildAny(options.getValidation().getLevel().name().toLowerCase())));
/*     */       }
/* 650 */       optBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("validation").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(validationBuilder)));
/*     */     } 
/*     */     
/* 653 */     argsBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("options").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(optBuilder)));
/*     */     
/* 655 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_MODIFY_COLLECTION_OPTIONS, new MysqlxDatatypes.Any[] {
/* 656 */             MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(argsBuilder).build() }));
/*     */   }
/*     */   
/*     */   public XMessage buildCreateCollection(String schemaName, String collectionName) {
/* 660 */     if (schemaName == null) {
/* 661 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" }));
/*     */     }
/* 663 */     if (collectionName == null) {
/* 664 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "collectionName" }));
/*     */     }
/* 666 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_CREATE_COLLECTION, new MysqlxDatatypes.Any[] {
/* 667 */             MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
/* 668 */             .setObj(MysqlxDatatypes.Object.newBuilder()
/* 669 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(collectionName)))
/* 670 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName))))
/* 671 */             .build()
/*     */           }));
/*     */   }
/*     */   
/*     */   public XMessage buildDropCollection(String schemaName, String collectionName) {
/* 676 */     if (schemaName == null) {
/* 677 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" }));
/*     */     }
/* 679 */     if (collectionName == null) {
/* 680 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "collectionName" }));
/*     */     }
/* 682 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_DROP_COLLECTION, new MysqlxDatatypes.Any[] {
/* 683 */             MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
/* 684 */             .setObj(MysqlxDatatypes.Object.newBuilder()
/* 685 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(collectionName)))
/* 686 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName))))
/* 687 */             .build() }));
/*     */   }
/*     */   
/*     */   public XMessage buildClose() {
/* 691 */     return new XMessage((Message)MysqlxSession.Close.getDefaultInstance());
/*     */   }
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
/*     */   public XMessage buildListObjects(String schemaName, String pattern) {
/* 714 */     if (schemaName == null) {
/* 715 */       throw new XProtocolError(Messages.getString("CreateTableStatement.0", new String[] { "schemaName" }));
/*     */     }
/*     */ 
/*     */     
/* 719 */     MysqlxDatatypes.Object.Builder obj = MysqlxDatatypes.Object.newBuilder().addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName)));
/*     */     
/* 721 */     if (pattern != null) {
/* 722 */       obj.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("pattern").setValue(ExprUtil.buildAny(pattern)));
/*     */     }
/*     */     
/* 725 */     return new XMessage((Message)
/* 726 */         buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_LIST_OBJECTS, new MysqlxDatatypes.Any[] { MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(obj).build() }));
/*     */   }
/*     */   
/*     */   public XMessage buildEnableNotices(String... notices) {
/* 730 */     MysqlxDatatypes.Array.Builder abuilder = MysqlxDatatypes.Array.newBuilder();
/* 731 */     for (String notice : notices) {
/* 732 */       abuilder.addValue(ExprUtil.buildAny(notice));
/*     */     }
/* 734 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_ENABLE_NOTICES, new MysqlxDatatypes.Any[] {
/* 735 */             MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
/* 736 */             .setObj(MysqlxDatatypes.Object.newBuilder()
/* 737 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("notice").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.ARRAY).setArray(abuilder))))
/* 738 */             .build() }));
/*     */   }
/*     */   
/*     */   public XMessage buildDisableNotices(String... notices) {
/* 742 */     MysqlxDatatypes.Array.Builder abuilder = MysqlxDatatypes.Array.newBuilder();
/* 743 */     for (String notice : notices) {
/* 744 */       abuilder.addValue(ExprUtil.buildAny(notice));
/*     */     }
/* 746 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_DISABLE_NOTICES, new MysqlxDatatypes.Any[] {
/* 747 */             MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
/* 748 */             .setObj(MysqlxDatatypes.Object.newBuilder()
/* 749 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("notice").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.ARRAY).setArray(abuilder))))
/* 750 */             .build()
/*     */           }));
/*     */   }
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
/*     */   public XMessage buildListNotices() {
/* 765 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_LIST_NOTICES, new MysqlxDatatypes.Any[0]));
/*     */   }
/*     */   
/*     */   public XMessage buildCreateCollectionIndex(String schemaName, String collectionName, CreateIndexParams params) {
/* 769 */     MysqlxDatatypes.Object.Builder builder = MysqlxDatatypes.Object.newBuilder();
/* 770 */     builder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(params.getIndexName())))
/* 771 */       .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("collection").setValue(ExprUtil.buildAny(collectionName)))
/* 772 */       .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName)))
/* 773 */       .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("unique").setValue(ExprUtil.buildAny(false)));
/* 774 */     if (params.getIndexType() != null) {
/* 775 */       builder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("type").setValue(ExprUtil.buildAny(params.getIndexType())));
/*     */     }
/*     */     
/* 778 */     MysqlxDatatypes.Array.Builder aBuilder = MysqlxDatatypes.Array.newBuilder();
/* 779 */     for (CreateIndexParams.IndexField indexField : params.getFields()) {
/*     */ 
/*     */       
/* 782 */       MysqlxDatatypes.Object.Builder fBuilder = MysqlxDatatypes.Object.newBuilder().addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("member").setValue(ExprUtil.buildAny(indexField.getField()))).addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("type").setValue(ExprUtil.buildAny(indexField.getType())));
/* 783 */       if (indexField.isRequired() != null) {
/* 784 */         fBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("required").setValue(ExprUtil.buildAny(indexField.isRequired().booleanValue())));
/*     */       }
/* 786 */       if (indexField.getOptions() != null) {
/* 787 */         fBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("options").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR)
/* 788 */               .setScalar(MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_UINT).setVUnsignedInt(indexField.getOptions().intValue())).build()));
/*     */       }
/* 790 */       if (indexField.getSrid() != null) {
/* 791 */         fBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("srid").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.SCALAR)
/* 792 */               .setScalar(MysqlxDatatypes.Scalar.newBuilder().setType(MysqlxDatatypes.Scalar.Type.V_UINT).setVUnsignedInt(indexField.getSrid().intValue())).build()));
/*     */       }
/* 794 */       if (indexField.isArray() != null) {
/* 795 */         fBuilder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("array").setValue(ExprUtil.buildAny(indexField.isArray().booleanValue())));
/*     */       }
/* 797 */       aBuilder.addValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(fBuilder));
/*     */     } 
/*     */     
/* 800 */     builder.addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("constraint").setValue(MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.ARRAY).setArray(aBuilder)));
/* 801 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_CREATE_COLLECTION_INDEX, new MysqlxDatatypes.Any[] {
/* 802 */             MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT).setObj(builder).build() }));
/*     */   }
/*     */   
/*     */   public XMessage buildDropCollectionIndex(String schemaName, String collectionName, String indexName) {
/* 806 */     return new XMessage((Message)buildXpluginCommand(XpluginStatementCommand.XPLUGIN_STMT_DROP_COLLECTION_INDEX, new MysqlxDatatypes.Any[] {
/* 807 */             MysqlxDatatypes.Any.newBuilder().setType(MysqlxDatatypes.Any.Type.OBJECT)
/* 808 */             .setObj(MysqlxDatatypes.Object.newBuilder()
/* 809 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("name").setValue(ExprUtil.buildAny(indexName)))
/* 810 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("collection").setValue(ExprUtil.buildAny(collectionName)))
/* 811 */               .addFld(MysqlxDatatypes.Object.ObjectField.newBuilder().setKey("schema").setValue(ExprUtil.buildAny(schemaName))))
/*     */             
/* 813 */             .build()
/*     */           }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MysqlxSql.StmtExecute buildXpluginCommand(XpluginStatementCommand command, MysqlxDatatypes.Any... args) {
/* 826 */     MysqlxSql.StmtExecute.Builder builder = MysqlxSql.StmtExecute.newBuilder();
/*     */     
/* 828 */     builder.setNamespace("mysqlx");
/*     */     
/* 830 */     builder.setStmt(ByteString.copyFromUtf8(command.commandName));
/* 831 */     Arrays.<MysqlxDatatypes.Any>stream(args).forEach(a -> builder.addArgs(a));
/*     */     
/* 833 */     return builder.build();
/*     */   }
/*     */   
/*     */   public XMessage buildSha256MemoryAuthStart() {
/* 837 */     return new XMessage((Message)MysqlxSession.AuthenticateStart.newBuilder().setMechName("SHA256_MEMORY").build());
/*     */   }
/*     */ 
/*     */   
/*     */   public XMessage buildSha256MemoryAuthContinue(String user, String password, byte[] nonce, String database) {
/* 842 */     String encoding = "UTF8";
/* 843 */     byte[] databaseBytes = (database == null) ? new byte[0] : StringUtils.getBytes(database, encoding);
/* 844 */     byte[] userBytes = (user == null) ? new byte[0] : StringUtils.getBytes(user, encoding);
/* 845 */     byte[] passwordBytes = (password == null || password.length() == 0) ? new byte[0] : StringUtils.getBytes(password, encoding);
/*     */     
/* 847 */     byte[] hashedPassword = passwordBytes;
/*     */     try {
/* 849 */       hashedPassword = Security.scrambleCachingSha2(passwordBytes, nonce);
/* 850 */     } catch (DigestException e) {
/* 851 */       throw new RuntimeException(e);
/*     */     } 
/*     */     
/* 854 */     hashedPassword = StringUtils.toHexString(hashedPassword, hashedPassword.length).getBytes();
/*     */     
/* 856 */     byte[] reply = new byte[databaseBytes.length + userBytes.length + hashedPassword.length + 2];
/* 857 */     System.arraycopy(databaseBytes, 0, reply, 0, databaseBytes.length);
/* 858 */     int pos = databaseBytes.length;
/* 859 */     reply[pos++] = 0;
/* 860 */     System.arraycopy(userBytes, 0, reply, pos, userBytes.length);
/* 861 */     pos += userBytes.length;
/* 862 */     reply[pos++] = 0;
/* 863 */     System.arraycopy(hashedPassword, 0, reply, pos, hashedPassword.length);
/*     */     
/* 865 */     MysqlxSession.AuthenticateContinue.Builder builder = MysqlxSession.AuthenticateContinue.newBuilder();
/* 866 */     builder.setAuthData(ByteString.copyFrom(reply));
/* 867 */     return new XMessage((Message)builder.build());
/*     */   }
/*     */   
/*     */   public XMessage buildMysql41AuthStart() {
/* 871 */     return new XMessage((Message)MysqlxSession.AuthenticateStart.newBuilder().setMechName("MYSQL41").build());
/*     */   }
/*     */ 
/*     */   
/*     */   public XMessage buildMysql41AuthContinue(String user, String password, byte[] salt, String database) {
/* 876 */     String encoding = "UTF8";
/* 877 */     byte[] userBytes = (user == null) ? new byte[0] : StringUtils.getBytes(user, encoding);
/* 878 */     byte[] passwordBytes = (password == null || password.length() == 0) ? new byte[0] : StringUtils.getBytes(password, encoding);
/* 879 */     byte[] databaseBytes = (database == null) ? new byte[0] : StringUtils.getBytes(database, encoding);
/*     */     
/* 881 */     byte[] hashedPassword = passwordBytes;
/* 882 */     if (password != null && password.length() > 0) {
/* 883 */       hashedPassword = Security.scramble411(passwordBytes, salt);
/*     */       
/* 885 */       hashedPassword = String.format("*%040x", new Object[] { new BigInteger(1, hashedPassword) }).getBytes();
/*     */     } 
/*     */ 
/*     */     
/* 889 */     byte[] reply = new byte[databaseBytes.length + userBytes.length + hashedPassword.length + 2];
/*     */ 
/*     */     
/* 892 */     System.arraycopy(databaseBytes, 0, reply, 0, databaseBytes.length);
/* 893 */     int pos = databaseBytes.length;
/* 894 */     reply[pos++] = 0;
/* 895 */     System.arraycopy(userBytes, 0, reply, pos, userBytes.length);
/* 896 */     pos += userBytes.length;
/* 897 */     reply[pos++] = 0;
/* 898 */     System.arraycopy(hashedPassword, 0, reply, pos, hashedPassword.length);
/*     */     
/* 900 */     MysqlxSession.AuthenticateContinue.Builder builder = MysqlxSession.AuthenticateContinue.newBuilder();
/* 901 */     builder.setAuthData(ByteString.copyFrom(reply));
/* 902 */     return new XMessage((Message)builder.build());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public XMessage buildPlainAuthStart(final String user, final String password, String database) {
/* 908 */     CallbackHandler callbackHandler = new CallbackHandler() {
/*     */         public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
/* 910 */           for (Callback c : callbacks) {
/* 911 */             if (NameCallback.class.isAssignableFrom(c.getClass())) {
/*     */               
/* 913 */               ((NameCallback)c).setName(user);
/* 914 */             } else if (PasswordCallback.class.isAssignableFrom(c.getClass())) {
/*     */               
/* 916 */               ((PasswordCallback)c).setPassword((password == null) ? new char[0] : password.toCharArray());
/*     */             } else {
/*     */               
/* 919 */               throw new UnsupportedCallbackException(c);
/*     */             } 
/*     */           } 
/*     */         }
/*     */       };
/*     */     
/*     */     try {
/* 926 */       String[] mechanisms = { "PLAIN" };
/* 927 */       String authorizationId = (database == null || database.trim().length() == 0) ? null : database;
/* 928 */       String protocol = "X Protocol";
/* 929 */       Map<String, ?> props = null;
/*     */       
/* 931 */       String serverName = "<unknown>";
/* 932 */       SaslClient saslClient = Sasl.createSaslClient(mechanisms, authorizationId, protocol, serverName, props, callbackHandler);
/*     */ 
/*     */       
/* 935 */       MysqlxSession.AuthenticateStart.Builder authStartBuilder = MysqlxSession.AuthenticateStart.newBuilder();
/* 936 */       authStartBuilder.setMechName("PLAIN");
/*     */       
/* 938 */       authStartBuilder.setAuthData(ByteString.copyFrom(saslClient.evaluateChallenge(null)));
/*     */       
/* 940 */       return new XMessage((Message)authStartBuilder.build());
/* 941 */     } catch (SaslException ex) {
/*     */       
/* 943 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public XMessage buildExternalAuthStart(String database) {
/* 948 */     CallbackHandler callbackHandler = new CallbackHandler() {
/*     */         public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
/* 950 */           Callback[] arrayOfCallback = callbacks; int i = arrayOfCallback.length; byte b = 0; if (b < i) { Callback c = arrayOfCallback[b];
/* 951 */             if (NameCallback.class.isAssignableFrom(c.getClass()))
/*     */             {
/* 953 */               throw new UnsupportedCallbackException(c); } 
/* 954 */             if (PasswordCallback.class.isAssignableFrom(c.getClass()))
/*     */             {
/* 956 */               throw new UnsupportedCallbackException(c);
/*     */             }
/* 958 */             throw new UnsupportedCallbackException(c); }
/*     */         
/*     */         }
/*     */       };
/*     */ 
/*     */     
/*     */     try {
/* 965 */       String[] mechanisms = { "EXTERNAL" };
/* 966 */       String authorizationId = (database == null || database.trim().length() == 0) ? null : database;
/* 967 */       String protocol = "X Protocol";
/* 968 */       Map<String, ?> props = null;
/*     */       
/* 970 */       String serverName = "<unknown>";
/* 971 */       SaslClient saslClient = Sasl.createSaslClient(mechanisms, authorizationId, protocol, serverName, props, callbackHandler);
/*     */ 
/*     */       
/* 974 */       MysqlxSession.AuthenticateStart.Builder authStartBuilder = MysqlxSession.AuthenticateStart.newBuilder();
/* 975 */       authStartBuilder.setMechName("EXTERNAL");
/*     */       
/* 977 */       authStartBuilder.setAuthData(ByteString.copyFrom(saslClient.evaluateChallenge(null)));
/*     */       
/* 979 */       return new XMessage((Message)authStartBuilder.build());
/* 980 */     } catch (SaslException ex) {
/*     */       
/* 982 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public XMessage buildSessionResetAndClose() {
/* 987 */     return new XMessage((Message)MysqlxSession.Reset.newBuilder().build());
/*     */   }
/*     */   
/*     */   public XMessage buildSessionResetKeepOpen() {
/* 991 */     return new XMessage((Message)MysqlxSession.Reset.newBuilder().setKeepOpen(true).build());
/*     */   }
/*     */   
/*     */   public XMessage buildExpectOpen() {
/* 995 */     return new XMessage((Message)MysqlxExpect.Open.newBuilder().addCond(MysqlxExpect.Open.Condition.newBuilder()
/* 996 */           .setConditionKey(2).setConditionValue(ByteString.copyFromUtf8("6.1"))).build());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XMessageBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */