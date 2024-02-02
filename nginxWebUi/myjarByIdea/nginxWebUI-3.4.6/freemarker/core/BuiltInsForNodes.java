package freemarker.core;

import freemarker.ext.dom._ExtDomApi;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template._TemplateAPI;
import java.util.List;

class BuiltInsForNodes {
   private BuiltInsForNodes() {
   }

   static class AncestorSequence extends SimpleSequence implements TemplateMethodModel {
      private Environment env;

      AncestorSequence(Environment env) {
         super((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
         this.env = env;
      }

      public Object exec(List names) throws TemplateModelException {
         if (names != null && !names.isEmpty()) {
            AncestorSequence result = new AncestorSequence(this.env);

            for(int i = 0; i < this.size(); ++i) {
               TemplateNodeModel tnm = (TemplateNodeModel)this.get(i);
               String nodeName = tnm.getNodeName();
               String nsURI = tnm.getNodeNamespace();
               if (nsURI == null) {
                  if (names.contains(nodeName)) {
                     result.add(tnm);
                  }
               } else {
                  for(int j = 0; j < names.size(); ++j) {
                     if (_ExtDomApi.matchesName((String)names.get(j), nodeName, nsURI, this.env)) {
                        result.add(tnm);
                        break;
                     }
                  }
               }
            }

            return result;
         } else {
            return this;
         }
      }
   }

   static class nextSiblingBI extends BuiltInForNodeEx {
      TemplateModel calculateResult(TemplateNodeModelEx nodeModel, Environment env) throws TemplateModelException {
         return nodeModel.getNextSibling();
      }
   }

   static class previousSiblingBI extends BuiltInForNodeEx {
      TemplateModel calculateResult(TemplateNodeModelEx nodeModel, Environment env) throws TemplateModelException {
         return nodeModel.getPreviousSibling();
      }
   }

   static class rootBI extends BuiltInForNode {
      TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
         TemplateNodeModel result = nodeModel;

         for(TemplateNodeModel parent = nodeModel.getParentNode(); parent != null; parent = parent.getParentNode()) {
            result = parent;
         }

         return result;
      }
   }

   static class parentBI extends BuiltInForNode {
      TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
         return nodeModel.getParentNode();
      }
   }

   static class node_typeBI extends BuiltInForNode {
      TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
         return new SimpleScalar(nodeModel.getNodeType());
      }
   }

   static class node_namespaceBI extends BuiltInForNode {
      TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
         String nsURI = nodeModel.getNodeNamespace();
         return nsURI == null ? null : new SimpleScalar(nsURI);
      }
   }

   static class node_nameBI extends BuiltInForNode {
      TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
         return new SimpleScalar(nodeModel.getNodeName());
      }
   }

   static class childrenBI extends BuiltInForNode {
      TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
         return nodeModel.getChildNodes();
      }
   }

   static class ancestorsBI extends BuiltInForNode {
      TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
         AncestorSequence result = new AncestorSequence(env);

         for(TemplateNodeModel parent = nodeModel.getParentNode(); parent != null; parent = parent.getParentNode()) {
            result.add(parent);
         }

         return result;
      }
   }
}
