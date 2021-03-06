package com.apploidxxx.classvisitor;

import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.Deprecated;

/**
 * @author Arthur Kupriyanov
 */
public class SimpleVisitor implements Visitor {
    @Override
    public void visitCode(Code obj) {

    }
    @Override
    public void visitSourceFile(SourceFile obj) {
        System.out.println("Visited SourceFile with name: " + obj.getName());
    }
    @Override
    public void visitMethod(Method obj) {
        System.out.println("Visited method with name: " + obj.getName());
        System.out.println("And with return type: " + obj.getReturnType());
    }
    @Override
    public void visitJavaClass(JavaClass obj) {
        System.out.println("Visited JavaClass with name: " + obj.getClassName());
    }

    @Override
    public void visitCodeException(CodeException obj) {

    }

    @Override
    public void visitConstantClass(ConstantClass obj) {

    }

    @Override
    public void visitConstantDouble(ConstantDouble obj) {

    }

    @Override
    public void visitConstantFieldref(ConstantFieldref obj) {

    }

    @Override
    public void visitConstantFloat(ConstantFloat obj) {

    }

    @Override
    public void visitConstantInteger(ConstantInteger obj) {

    }

    @Override
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {

    }

    @Override
    public void visitConstantInvokeDynamic(ConstantInvokeDynamic obj) {

    }

    @Override
    public void visitConstantLong(ConstantLong obj) {

    }

    @Override
    public void visitConstantMethodref(ConstantMethodref obj) {

    }

    @Override
    public void visitConstantNameAndType(ConstantNameAndType obj) {

    }

    @Override
    public void visitConstantPool(ConstantPool obj) {

    }

    @Override
    public void visitConstantString(ConstantString obj) {

    }

    @Override
    public void visitConstantUtf8(ConstantUtf8 obj) {

    }

    @Override
    public void visitConstantValue(ConstantValue obj) {

    }

    @Override
    public void visitDeprecated(Deprecated obj) {

    }

    @Override
    public void visitExceptionTable(ExceptionTable obj) {

    }

    @Override
    public void visitField(Field obj) {

    }

    @Override
    public void visitInnerClass(InnerClass obj) {

    }

    @Override
    public void visitInnerClasses(InnerClasses obj) {

    }



    @Override
    public void visitLineNumber(LineNumber obj) {

    }

    @Override
    public void visitLineNumberTable(LineNumberTable obj) {

    }

    @Override
    public void visitLocalVariable(LocalVariable obj) {

    }

    @Override
    public void visitLocalVariableTable(LocalVariableTable obj) {

    }



    @Override
    public void visitSignature(Signature obj) {

    }



    @Override
    public void visitSynthetic(Synthetic obj) {

    }

    @Override
    public void visitUnknown(Unknown obj) {

    }

    @Override
    public void visitStackMap(StackMap obj) {

    }

    @Override
    public void visitStackMapEntry(StackMapEntry obj) {

    }

    @Override
    public void visitAnnotation(Annotations obj) {

    }

    @Override
    public void visitParameterAnnotation(ParameterAnnotations obj) {

    }

    @Override
    public void visitAnnotationEntry(AnnotationEntry obj) {

    }

    @Override
    public void visitAnnotationDefault(AnnotationDefault obj) {

    }

    @Override
    public void visitLocalVariableTypeTable(LocalVariableTypeTable obj) {

    }

    @Override
    public void visitEnclosingMethod(EnclosingMethod obj) {

    }

    @Override
    public void visitBootstrapMethods(BootstrapMethods obj) {

    }

    @Override
    public void visitMethodParameters(MethodParameters obj) {

    }

    @Override
    public void visitConstantMethodType(ConstantMethodType obj) {

    }

    @Override
    public void visitConstantMethodHandle(ConstantMethodHandle obj) {

    }

    @Override
    public void visitParameterAnnotationEntry(ParameterAnnotationEntry obj) {

    }

    @Override
    public void visitConstantPackage(ConstantPackage constantPackage) {

    }

    @Override
    public void visitConstantModule(ConstantModule constantModule) {

    }
}
