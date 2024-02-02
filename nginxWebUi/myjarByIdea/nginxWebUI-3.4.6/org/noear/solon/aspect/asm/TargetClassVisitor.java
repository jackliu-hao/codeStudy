package org.noear.solon.aspect.asm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.core.event.EventBus;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class TargetClassVisitor extends ClassVisitor {
   private boolean isFinal;
   private List<MethodBean> methods = new ArrayList();
   private List<MethodBean> declaredMethods = new ArrayList();
   private List<MethodBean> constructors = new ArrayList();

   public TargetClassVisitor() {
      super(524288);
   }

   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      super.visit(version, access, name, signature, superName, interfaces);
      if ((access & 16) == 16) {
         this.isFinal = true;
      }

      if (superName != null) {
         List<MethodBean> beans = this.initMethodBeanByParent(superName);
         if (beans != null && !beans.isEmpty()) {
            Iterator var8 = beans.iterator();

            while(var8.hasNext()) {
               MethodBean bean = (MethodBean)var8.next();
               if (!this.methods.contains(bean)) {
                  this.methods.add(bean);
               }
            }
         }
      }

   }

   public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
      MethodBean methodBean;
      if ("<init>".equals(name)) {
         methodBean = new MethodBean(access, name, descriptor);
         this.constructors.add(methodBean);
      } else if (!"<clinit>".equals(name)) {
         if ((access & 16) == 16 || (access & 8) == 8) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
         }

         methodBean = new MethodBean(access, name, descriptor);
         this.declaredMethods.add(methodBean);
         if ((access & 1) == 1 && (access & 1024) != 1024 && !this.methods.contains(methodBean)) {
            this.methods.add(methodBean);
         }
      }

      return super.visitMethod(access, name, descriptor, signature, exceptions);
   }

   public boolean isFinal() {
      return this.isFinal;
   }

   public List<MethodBean> getMethods() {
      return this.methods;
   }

   public List<MethodBean> getDeclaredMethods() {
      return this.declaredMethods;
   }

   public List<MethodBean> getConstructors() {
      return this.constructors;
   }

   private List<MethodBean> initMethodBeanByParent(String superName) {
      try {
         if (superName != null && !superName.isEmpty()) {
            ClassReader reader = new ClassReader(superName);
            TargetClassVisitor visitor = new TargetClassVisitor();
            reader.accept(visitor, 2);
            List<MethodBean> beans = new ArrayList();
            Iterator var5 = visitor.methods.iterator();

            while(var5.hasNext()) {
               MethodBean methodBean = (MethodBean)var5.next();
               if ((methodBean.access & 16) != 16 && (methodBean.access & 8) != 8 && (methodBean.access & 1) == 1) {
                  beans.add(methodBean);
               }
            }

            return beans;
         }
      } catch (Exception var7) {
         EventBus.push(var7);
      }

      return null;
   }
}
