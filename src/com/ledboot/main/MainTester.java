package com.ledboot.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@SuppressWarnings({"unchecked","rawtypes", "unused"})
public class MainTester {

	public static void main(String[] args) {
		try {
			ClassPrinter printer = new ClassPrinter();
			ClassReader cr = new ClassReader("java.lang.Runnable");
			cr.accept(printer, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static class  ClassPrinter extends ClassVisitor{
		
		public ClassPrinter(){
			super(Opcodes.ASM5);
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName,String[] interfaces) {
			super.visit(version, access, name, signature, superName, interfaces);
			System.out.println("superName="+superName+",name="+name);
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			System.out.println(name+desc);
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
		
		@Override
		public void visitAttribute(Attribute attr) {
			super.visitAttribute(attr);
		}
		
		@Override
		public void visitEnd() {
			System.out.println("end");
			super.visitEnd();
		}
	}
	
	
	
	private static void invokeClass(){
		try {
			InputStream in = new FileInputStream("Person2.class");
			ClassReader reader = new ClassReader(in);
			byte [] byteArr = reader.b;
			Class clazz = new MyClassLoader().defineClass(
					"com.ledboot.ASMTest.Person", byteArr);
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
				showInfoMethod.invoke(personObj, null);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			
		}
	}

}
