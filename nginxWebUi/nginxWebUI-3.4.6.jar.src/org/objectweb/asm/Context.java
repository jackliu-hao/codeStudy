package org.objectweb.asm;

final class Context {
  Attribute[] attributePrototypes;
  
  int parsingOptions;
  
  char[] charBuffer;
  
  int currentMethodAccessFlags;
  
  String currentMethodName;
  
  String currentMethodDescriptor;
  
  Label[] currentMethodLabels;
  
  int currentTypeAnnotationTarget;
  
  TypePath currentTypeAnnotationTargetPath;
  
  Label[] currentLocalVariableAnnotationRangeStarts;
  
  Label[] currentLocalVariableAnnotationRangeEnds;
  
  int[] currentLocalVariableAnnotationRangeIndices;
  
  int currentFrameOffset;
  
  int currentFrameType;
  
  int currentFrameLocalCount;
  
  int currentFrameLocalCountDelta;
  
  Object[] currentFrameLocalTypes;
  
  int currentFrameStackCount;
  
  Object[] currentFrameStackTypes;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\Context.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */