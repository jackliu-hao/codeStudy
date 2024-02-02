/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.ResultBuilder;
/*     */ import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
/*     */ import com.mysql.cj.protocol.x.XProtocol;
/*     */ import com.mysql.cj.protocol.x.XProtocolError;
/*     */ import com.mysql.cj.protocol.x.XProtocolRowInputStream;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.xdevapi.PreparableStatement;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Spliterators;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.stream.Collector;
/*     */ import java.util.stream.Stream;
/*     */ import java.util.stream.StreamSupport;
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
/*     */ public class MysqlxSession
/*     */   extends CoreSession
/*     */ {
/*     */   public MysqlxSession(HostInfo hostInfo, PropertySet propSet) {
/*  58 */     super(hostInfo, propSet);
/*     */ 
/*     */     
/*  61 */     this.protocol = (Protocol<? extends Message>)new XProtocol(hostInfo, propSet);
/*     */     
/*  63 */     this.messageBuilder = this.protocol.getMessageBuilder();
/*     */     
/*  65 */     this.protocol.connect(hostInfo.getUser(), hostInfo.getPassword(), hostInfo.getDatabase());
/*     */   }
/*     */   
/*     */   public MysqlxSession(XProtocol prot) {
/*  69 */     super(null, prot.getPropertySet());
/*  70 */     this.protocol = (Protocol<? extends Message>)prot;
/*  71 */     this.messageBuilder = this.protocol.getMessageBuilder();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProcessHost() {
/*  76 */     return this.protocol.getSocketConnection().getHost();
/*     */   }
/*     */   
/*     */   public int getPort() {
/*  80 */     return this.protocol.getSocketConnection().getPort();
/*     */   }
/*     */   
/*     */   public XProtocol getProtocol() {
/*  84 */     return (XProtocol)this.protocol;
/*     */   }
/*     */ 
/*     */   
/*     */   public void quit() {
/*     */     try {
/*  90 */       this.protocol.close();
/*  91 */     } catch (IOException ex) {
/*  92 */       throw new CJCommunicationsException(ex);
/*     */     } 
/*  94 */     super.quit();
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/*  98 */     return !((XProtocol)this.protocol).isOpen();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsPreparedStatements() {
/* 108 */     return ((XProtocol)this.protocol).supportsPreparedStatements();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean readyForPreparingStatements() {
/* 118 */     return ((XProtocol)this.protocol).readyForPreparingStatements();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNewPreparedStatementId(PreparableStatement<?> preparableStatement) {
/* 129 */     return ((XProtocol)this.protocol).getNewPreparedStatementId(preparableStatement);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void freePreparedStatementId(int preparedStatementId) {
/* 139 */     ((XProtocol)this.protocol).freePreparedStatementId(preparedStatementId);
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
/*     */   public boolean failedPreparingStatement(int preparedStatementId, XProtocolError e) {
/* 153 */     return ((XProtocol)this.protocol).failedPreparingStatement(preparedStatementId, e);
/*     */   }
/*     */   
/*     */   public <M extends Message, R, RES> RES query(M message, Predicate<Row> rowFilter, Function<Row, R> rowMapper, Collector<R, ?, RES> collector) {
/* 157 */     this.protocol.send((Message)message, 0);
/* 158 */     ColumnDefinition metadata = this.protocol.readMetadata();
/* 159 */     XProtocolRowInputStream xProtocolRowInputStream = new XProtocolRowInputStream(metadata, (XProtocol)this.protocol, null);
/* 160 */     Stream<Row> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator<? extends Row>)xProtocolRowInputStream, 0), false);
/* 161 */     if (rowFilter != null) {
/* 162 */       stream = stream.filter(rowFilter);
/*     */     }
/* 164 */     RES result = stream.<R>map(rowMapper).collect(collector);
/* 165 */     this.protocol.readQueryResult((ResultBuilder)new StatementExecuteOkBuilder());
/* 166 */     return result;
/*     */   }
/*     */   
/*     */   public <M extends Message, R extends QueryResult> R query(M message, ResultBuilder<R> resultBuilder) {
/* 170 */     return (R)((XProtocol)this.protocol).query((Message)message, resultBuilder);
/*     */   }
/*     */   
/*     */   public <M extends Message, R extends QueryResult> CompletableFuture<R> queryAsync(M message, ResultBuilder<R> resultBuilder) {
/* 174 */     return ((XProtocol)this.protocol).queryAsync((Message)message, resultBuilder);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\MysqlxSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */