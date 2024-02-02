package freemarker.debug;

import freemarker.template.TemplateModelException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface DebugModel extends Remote {
   int TYPE_SCALAR = 1;
   int TYPE_NUMBER = 2;
   int TYPE_DATE = 4;
   int TYPE_BOOLEAN = 8;
   int TYPE_SEQUENCE = 16;
   int TYPE_COLLECTION = 32;
   int TYPE_HASH = 64;
   int TYPE_HASH_EX = 128;
   int TYPE_METHOD = 256;
   int TYPE_METHOD_EX = 512;
   int TYPE_TRANSFORM = 1024;
   int TYPE_ENVIRONMENT = 2048;
   int TYPE_TEMPLATE = 4096;
   int TYPE_CONFIGURATION = 8192;

   String getAsString() throws TemplateModelException, RemoteException;

   Number getAsNumber() throws TemplateModelException, RemoteException;

   boolean getAsBoolean() throws TemplateModelException, RemoteException;

   Date getAsDate() throws TemplateModelException, RemoteException;

   int getDateType() throws TemplateModelException, RemoteException;

   int size() throws TemplateModelException, RemoteException;

   DebugModel get(int var1) throws TemplateModelException, RemoteException;

   DebugModel[] get(int var1, int var2) throws TemplateModelException, RemoteException;

   DebugModel get(String var1) throws TemplateModelException, RemoteException;

   DebugModel[] get(String[] var1) throws TemplateModelException, RemoteException;

   DebugModel[] getCollection() throws TemplateModelException, RemoteException;

   String[] keys() throws TemplateModelException, RemoteException;

   int getModelTypes() throws RemoteException;
}
