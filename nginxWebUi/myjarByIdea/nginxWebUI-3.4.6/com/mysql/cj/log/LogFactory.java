package com.mysql.cj.log;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.Util;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LogFactory {
   public static Log getLogger(String className, String instanceName) {
      if (className == null) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Logger class can not be NULL");
      } else if (instanceName == null) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Logger instance name can not be NULL");
      } else {
         try {
            Class<?> loggerClass = null;

            try {
               loggerClass = Class.forName(className);
            } catch (ClassNotFoundException var4) {
               loggerClass = Class.forName(Util.getPackageName(LogFactory.class) + "." + className);
            }

            Constructor<?> constructor = loggerClass.getConstructor(String.class);
            return (Log)constructor.newInstance(instanceName);
         } catch (ClassNotFoundException var5) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)("Unable to load class for logger '" + className + "'"), (Throwable)var5);
         } catch (NoSuchMethodException var6) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)"Logger class does not have a single-arg constructor that takes an instance name", (Throwable)var6);
         } catch (InstantiationException var7) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)("Unable to instantiate logger class '" + className + "', exception in constructor?"), (Throwable)var7);
         } catch (InvocationTargetException var8) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)("Unable to instantiate logger class '" + className + "', exception in constructor?"), (Throwable)var8);
         } catch (IllegalAccessException var9) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)("Unable to instantiate logger class '" + className + "', constructor not public"), (Throwable)var9);
         } catch (ClassCastException var10) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)("Logger class '" + className + "' does not implement the '" + Log.class.getName() + "' interface"), (Throwable)var10);
         }
      }
   }
}
