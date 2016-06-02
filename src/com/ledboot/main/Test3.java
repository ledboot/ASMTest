package com.ledboot.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@SuppressWarnings({"unchecked","rawtypes", "unused"})
public class Test3 {
	public static void main(String[] args) {
		
		ClassWriter cw = new ClassWriter(0);
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC,"com/ledboot/ASMTest/Person", null, "java/lang/Object", null);
		cw.visitField(Opcodes.ACC_PRIVATE, "name", "Ljava/lang/String;", null,null).visitEnd();
		cw.visitField(Opcodes.ACC_PRIVATE, "age", "I", null, 0).visitEnd();
		
		MethodVisitor mvConstruct = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mvConstruct.visitVarInsn(Opcodes.ALOAD, 0);
		mvConstruct.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mvConstruct.visitInsn(Opcodes.RETURN);
		mvConstruct.visitMaxs(1, 1);
		mvConstruct.visitEnd();

		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE, "showInfo","()V", null, null);
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out","Ljava/io/PrintStream;");
		mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder","<init>", "()V", false);
		mv.visitLdcInsn("name=");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder","append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;",false);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, "com/ledboot/ASMTest/Person","name", "Ljava/lang/String;");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder","append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;",false);
		mv.visitLdcInsn(",age=");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder","append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;",false);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, "com/ledboot/ASMTest/Person","age", "I");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder","append", "(I)Ljava/lang/StringBuilder;", false);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder","toString", "()Ljava/lang/String;", false);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream","println", "(Ljava/lang/String;)V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(4, 4);
		mv.visitEnd();

		MethodVisitor mvGetName = cw.visitMethod(Opcodes.ACC_PUBLIC, "getName","()Ljava/lang/String;", null, null);
		mvGetName.visitVarInsn(Opcodes.ALOAD, 0);
		mvGetName.visitFieldInsn(Opcodes.GETFIELD,"com/ledboot/ASMTest/Person", "name", "Ljava/lang/String;");
		mvGetName.visitInsn(Opcodes.ARETURN);
		mvGetName.visitMaxs(2, 2);
		mvGetName.visitEnd();

		MethodVisitor mvSetName = cw.visitMethod(Opcodes.ACC_PUBLIC, "setName","(Ljava/lang/String;)V", null, null);
		mvSetName.visitVarInsn(Opcodes.ALOAD, 0);
		mvSetName.visitVarInsn(Opcodes.ALOAD, 1);
		mvSetName.visitFieldInsn(Opcodes.PUTFIELD,"com/ledboot/ASMTest/Person", "name", "Ljava/lang/String;");
		mvSetName.visitInsn(Opcodes.RETURN);
		mvSetName.visitMaxs(2, 2);
		mvSetName.visitEnd();

		MethodVisitor mvGetAge = cw.visitMethod(Opcodes.ACC_PUBLIC, "getAge","()I", null, null);
		mvGetAge.visitVarInsn(Opcodes.ALOAD, 0);
		mvGetAge.visitFieldInsn(Opcodes.GETFIELD, "com/ledboot/ASMTest/Person","age", "I");
		mvGetAge.visitInsn(Opcodes.IRETURN);
		mvGetAge.visitMaxs(2, 2);
		mvGetAge.visitEnd();

		MethodVisitor mvSetAge = cw.visitMethod(Opcodes.ACC_PUBLIC, "setAge","(I)V", null, null);
		mvSetAge.visitVarInsn(Opcodes.ALOAD, 0);
		mvSetAge.visitVarInsn(Opcodes.ILOAD, 1);
		mvSetAge.visitFieldInsn(Opcodes.PUTFIELD, "com/ledboot/ASMTest/Person","age", "I");
		mvSetAge.visitInsn(Opcodes.RETURN);
		mvSetAge.visitMaxs(2, 2);
		mvSetAge.visitEnd();

		cw.visitEnd();

		try {
			FileOutputStream out = new FileOutputStream("Person.class");
			out.write(cw.toByteArray());
			out.close();
		} catch (Exception e) {
		}
		System.out.println("done!");
		
		Class clazz = new MyClassLoader().defineClass("com.ledboot.ASMTest.Person", cw.toByteArray());
		try {
			Object personObj = clazz.newInstance();
			Field nameField = clazz.getDeclaredField("name");
			Field ageField = clazz.getDeclaredField("age");
			Method setNameMethod = clazz.getDeclaredMethod("setName", new Class[]{String.class});
			Object[] nameArgs = new Object[]{new String("Gwynn")};
			setNameMethod.setAccessible(true);
			setNameMethod.invoke(personObj, nameArgs);
			
			Method setAgeMethod = clazz.getDeclaredMethod("setAge", new Class[]{int.class});
			Object[] ageArgs = new Object[]{new Integer(18)};
			setAgeMethod.setAccessible(true);
			setAgeMethod.invoke(personObj, ageArgs);
			
			Method showInfoMethod = clazz.getDeclaredMethod("showInfo", new Class[]{});
			showInfoMethod.setAccessible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		byte [] byteArray = new byte[1024];
		try {
			InputStream in = new FileInputStream("Person.class");
			in.read(byteArray);
			in.close();
		} catch (Exception e) {
		}
		
		RedefineClass redefineClass = new RedefineClass(byteArray);

	}
}
