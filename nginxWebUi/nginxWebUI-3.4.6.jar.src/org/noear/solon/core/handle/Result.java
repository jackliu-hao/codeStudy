/*     */ package org.noear.solon.core.handle;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.noear.solon.annotation.Note;
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
/*     */ public class Result<T>
/*     */   implements Serializable
/*     */ {
/*  30 */   public static int SUCCEED_CODE = 200;
/*  31 */   public static int FAILURE_CODE = 400;
/*     */ 
/*     */   
/*     */   private int code;
/*     */ 
/*     */   
/*     */   private String description;
/*     */ 
/*     */   
/*     */   private T data;
/*     */ 
/*     */   
/*     */   public int getCode() {
/*  44 */     return this.code;
/*     */   }
/*     */   
/*     */   public void setCode(int code) {
/*  48 */     this.code = code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDescription() {
/*  57 */     return this.description;
/*     */   }
/*     */   
/*     */   public void setDescription(String description) {
/*  61 */     if (description == null) {
/*  62 */       this.description = "";
/*     */     } else {
/*  64 */       this.description = description;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setData(T data) {
/*  74 */     this.data = data;
/*     */   }
/*     */   
/*     */   public T getData() {
/*  78 */     return this.data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result() {
/*  85 */     this.code = SUCCEED_CODE;
/*  86 */     this.description = "";
/*     */   }
/*     */   
/*     */   public Result(T data) {
/*  90 */     this.code = SUCCEED_CODE;
/*  91 */     this.description = "";
/*  92 */     this.data = data;
/*     */   }
/*     */   
/*     */   public Result(int code, String description) {
/*  96 */     this.code = code;
/*  97 */     this.description = description;
/*     */   }
/*     */   
/*     */   public Result(int code, String description, T data) {
/* 101 */     this.code = code;
/* 102 */     this.description = description;
/* 103 */     this.data = data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("成功的空结果")
/*     */   public static <T> Result<T> succeed() {
/* 111 */     return new Result<>(SUCCEED_CODE, "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("成功的结果")
/*     */   public static <T> Result<T> succeed(T data) {
/* 119 */     return new Result<>(data);
/*     */   }
/*     */   
/*     */   @Note("成功的结果")
/*     */   public static <T> Result<T> succeed(T data, String description) {
/* 124 */     return new Result<>(SUCCEED_CODE, description, data);
/*     */   }
/*     */   
/*     */   @Note("成功的结果")
/*     */   public static <T> Result<T> succeed(T data, int code) {
/* 129 */     return new Result<>(code, "", data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("失败的空结果")
/*     */   public static <T> Result<T> failure() {
/* 137 */     return new Result<>(FAILURE_CODE, "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("失败的结果")
/*     */   public static <T> Result<T> failure(int code) {
/* 145 */     return failure(code, "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("失败的结果")
/*     */   public static <T> Result<T> failure(int code, String description) {
/* 153 */     return new Result<>(code, description);
/*     */   }
/*     */   
/*     */   @Note("失败的结果")
/*     */   public static <T> Result<T> failure(String description) {
/* 158 */     return new Result<>(FAILURE_CODE, description);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\Result.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */