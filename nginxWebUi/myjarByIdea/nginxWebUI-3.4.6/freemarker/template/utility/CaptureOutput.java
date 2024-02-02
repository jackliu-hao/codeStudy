package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/** @deprecated */
@Deprecated
public class CaptureOutput implements TemplateTransformModel {
   public Writer getWriter(final Writer out, Map args) throws TemplateModelException {
      String errmsg = "Must specify the name of the variable in which to capture the output with the 'var' or 'local' or 'global' parameter.";
      if (args == null) {
         throw new TemplateModelException(errmsg);
      } else {
         final boolean local = false;
         final boolean global = false;
         final TemplateModel nsModel = (TemplateModel)args.get("namespace");
         Object varNameModel = args.get("var");
         if (varNameModel == null) {
            varNameModel = args.get("local");
            if (varNameModel == null) {
               varNameModel = args.get("global");
               global = true;
            } else {
               local = true;
            }

            if (varNameModel == null) {
               throw new TemplateModelException(errmsg);
            }
         }

         if (args.size() == 2) {
            if (nsModel == null) {
               throw new TemplateModelException("Second parameter can only be namespace");
            }

            if (local) {
               throw new TemplateModelException("Cannot specify namespace for a local assignment");
            }

            if (global) {
               throw new TemplateModelException("Cannot specify namespace for a global assignment");
            }

            if (!(nsModel instanceof Environment.Namespace)) {
               throw new TemplateModelException("namespace parameter does not specify a namespace. It is a " + nsModel.getClass().getName());
            }
         } else if (args.size() != 1) {
            throw new TemplateModelException("Bad parameters. Use only one of 'var' or 'local' or 'global' parameters.");
         }

         if (!(varNameModel instanceof TemplateScalarModel)) {
            throw new TemplateModelException("'var' or 'local' or 'global' parameter doesn't evaluate to a string");
         } else {
            final String varName = ((TemplateScalarModel)varNameModel).getAsString();
            if (varName == null) {
               throw new TemplateModelException("'var' or 'local' or 'global' parameter evaluates to null string");
            } else {
               final StringBuilder buf = new StringBuilder();
               final Environment env = Environment.getCurrentEnvironment();
               return new Writer() {
                  public void write(char[] cbuf, int off, int len) {
                     buf.append(cbuf, off, len);
                  }

                  public void flush() throws IOException {
                     out.flush();
                  }

                  public void close() throws IOException {
                     SimpleScalar result = new SimpleScalar(buf.toString());

                     try {
                        if (local) {
                           env.setLocalVariable(varName, result);
                        } else if (global) {
                           env.setGlobalVariable(varName, result);
                        } else if (nsModel == null) {
                           env.setVariable(varName, result);
                        } else {
                           ((Environment.Namespace)nsModel).put(varName, result);
                        }

                     } catch (IllegalStateException var3) {
                        throw new IOException("Could not set variable " + varName + ": " + var3.getMessage());
                     }
                  }
               };
            }
         }
      }
   }
}
