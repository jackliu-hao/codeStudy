/*    */ package com.mysql.cj.log;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
/*    */ import com.mysql.cj.util.Util;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LogFactory
/*    */ {
/*    */   public static Log getLogger(String className, String instanceName) {
/* 56 */     if (className == null) {
/* 57 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Logger class can not be NULL");
/*    */     }
/*    */     
/* 60 */     if (instanceName == null) {
/* 61 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Logger instance name can not be NULL");
/*    */     }
/*    */     
/*    */     try {
/* 65 */       Class<?> loggerClass = null;
/*    */       
/*    */       try {
/* 68 */         loggerClass = Class.forName(className);
/* 69 */       } catch (ClassNotFoundException nfe) {
/* 70 */         loggerClass = Class.forName(Util.getPackageName(LogFactory.class) + "." + className);
/*    */       } 
/*    */       
/* 73 */       Constructor<?> constructor = loggerClass.getConstructor(new Class[] { String.class });
/*    */       
/* 75 */       return (Log)constructor.newInstance(new Object[] { instanceName });
/* 76 */     } catch (ClassNotFoundException cnfe) {
/* 77 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unable to load class for logger '" + className + "'", cnfe);
/* 78 */     } catch (NoSuchMethodException nsme) {
/* 79 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Logger class does not have a single-arg constructor that takes an instance name", nsme);
/*    */     }
/* 81 */     catch (InstantiationException inse) {
/* 82 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unable to instantiate logger class '" + className + "', exception in constructor?", inse);
/*    */     }
/* 84 */     catch (InvocationTargetException ite) {
/* 85 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unable to instantiate logger class '" + className + "', exception in constructor?", ite);
/*    */     }
/* 87 */     catch (IllegalAccessException iae) {
/* 88 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unable to instantiate logger class '" + className + "', constructor not public", iae);
/*    */     }
/* 90 */     catch (ClassCastException cce) {
/* 91 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Logger class '" + className + "' does not implement the '" + Log.class
/* 92 */           .getName() + "' interface", cce);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\LogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */