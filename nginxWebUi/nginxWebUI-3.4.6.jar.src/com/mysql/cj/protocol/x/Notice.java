/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.ByteString;
/*     */ import com.google.protobuf.InvalidProtocolBufferException;
/*     */ import com.google.protobuf.Parser;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.protocol.Warning;
/*     */ import com.mysql.cj.x.protobuf.MysqlxDatatypes;
/*     */ import com.mysql.cj.x.protobuf.MysqlxNotice;
/*     */ import java.util.List;
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
/*     */ public class Notice
/*     */   implements ProtocolEntity
/*     */ {
/*     */   public static final int NoticeScope_Global = 1;
/*     */   public static final int NoticeScope_Local = 2;
/*     */   public static final int NoticeType_WARNING = 1;
/*     */   public static final int NoticeType_SESSION_VARIABLE_CHANGED = 2;
/*     */   public static final int NoticeType_SESSION_STATE_CHANGED = 3;
/*     */   public static final int NoticeType_GROUP_REPLICATION_STATE_CHANGED = 4;
/*     */   public static final int SessionStateChanged_CURRENT_SCHEMA = 1;
/*     */   public static final int SessionStateChanged_ACCOUNT_EXPIRED = 2;
/*     */   public static final int SessionStateChanged_GENERATED_INSERT_ID = 3;
/*     */   public static final int SessionStateChanged_ROWS_AFFECTED = 4;
/*     */   public static final int SessionStateChanged_ROWS_FOUND = 5;
/*     */   public static final int SessionStateChanged_ROWS_MATCHED = 6;
/*     */   public static final int SessionStateChanged_TRX_COMMITTED = 7;
/*     */   public static final int SessionStateChanged_TRX_ROLLEDBACK = 9;
/*     */   public static final int SessionStateChanged_PRODUCED_MESSAGE = 10;
/*     */   public static final int SessionStateChanged_CLIENT_ID_ASSIGNED = 11;
/*     */   public static final int SessionStateChanged_GENERATED_DOCUMENT_IDS = 12;
/*     */   
/*     */   public static Notice getInstance(XMessage message) {
/*  52 */     MysqlxNotice.Frame notice = (MysqlxNotice.Frame)message.getMessage();
/*  53 */     switch (notice.getType()) {
/*     */       case 1:
/*  55 */         return new XWarning(notice);
/*     */       
/*     */       case 2:
/*  58 */         return new XSessionVariableChanged(notice);
/*     */       
/*     */       case 3:
/*  61 */         return new XSessionStateChanged(notice);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     return new Notice(notice);
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
/*     */   
/*  92 */   protected int scope = 0;
/*  93 */   protected int type = 0;
/*     */   
/*     */   public Notice(MysqlxNotice.Frame frm) {
/*  96 */     this.scope = frm.getScope().getNumber();
/*  97 */     this.type = frm.getType();
/*     */   }
/*     */   
/*     */   public int getType() {
/* 101 */     return this.type;
/*     */   }
/*     */   
/*     */   public int getScope() {
/* 105 */     return this.scope;
/*     */   }
/*     */ 
/*     */   
/*     */   static <T extends com.google.protobuf.GeneratedMessageV3> T parseNotice(ByteString payload, Class<T> noticeClass) {
/*     */     try {
/* 111 */       Parser<T> parser = (Parser<T>)MessageConstants.MESSAGE_CLASS_TO_PARSER.get(noticeClass);
/* 112 */       return (T)parser.parseFrom(payload);
/* 113 */     } catch (InvalidProtocolBufferException ex) {
/* 114 */       throw new CJCommunicationsException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class XWarning
/*     */     extends Notice implements Warning {
/*     */     private int level;
/*     */     private long code;
/*     */     private String message;
/*     */     
/*     */     public XWarning(MysqlxNotice.Frame frm) {
/* 125 */       super(frm);
/* 126 */       MysqlxNotice.Warning warn = parseNotice(frm.getPayload(), MysqlxNotice.Warning.class);
/* 127 */       this.level = warn.getLevel().getNumber();
/* 128 */       this.code = Integer.toUnsignedLong(warn.getCode());
/* 129 */       this.message = warn.getMsg();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getLevel() {
/* 134 */       return this.level;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getCode() {
/* 139 */       return this.code;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getMessage() {
/* 144 */       return this.message;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class XSessionVariableChanged
/*     */     extends Notice {
/* 150 */     private String paramName = null;
/* 151 */     private MysqlxDatatypes.Scalar value = null;
/*     */     
/*     */     public XSessionVariableChanged(MysqlxNotice.Frame frm) {
/* 154 */       super(frm);
/* 155 */       MysqlxNotice.SessionVariableChanged svmsg = parseNotice(frm.getPayload(), MysqlxNotice.SessionVariableChanged.class);
/* 156 */       this.paramName = svmsg.getParam();
/* 157 */       this.value = svmsg.getValue();
/*     */     }
/*     */     
/*     */     public String getParamName() {
/* 161 */       return this.paramName;
/*     */     }
/*     */     
/*     */     public MysqlxDatatypes.Scalar getValue() {
/* 165 */       return this.value;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class XSessionStateChanged
/*     */     extends Notice {
/* 171 */     private Integer paramType = null;
/* 172 */     private List<MysqlxDatatypes.Scalar> valueList = null;
/*     */     
/*     */     public XSessionStateChanged(MysqlxNotice.Frame frm) {
/* 175 */       super(frm);
/* 176 */       MysqlxNotice.SessionStateChanged ssmsg = parseNotice(frm.getPayload(), MysqlxNotice.SessionStateChanged.class);
/* 177 */       this.paramType = Integer.valueOf(ssmsg.getParam().getNumber());
/* 178 */       this.valueList = ssmsg.getValueList();
/*     */     }
/*     */     
/*     */     public Integer getParamType() {
/* 182 */       return this.paramType;
/*     */     }
/*     */     
/*     */     public List<MysqlxDatatypes.Scalar> getValueList() {
/* 186 */       return this.valueList;
/*     */     }
/*     */     
/*     */     public MysqlxDatatypes.Scalar getValue() {
/* 190 */       if (this.valueList != null && !this.valueList.isEmpty()) {
/* 191 */         return this.valueList.get(0);
/*     */       }
/* 193 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\Notice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */