package com.ledboot.main;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class RedefineClass {
	
	private byte [] byteArray = null;

	public RedefineClass(byte [] b){
		byteArray = b;
		ClassReader classReader = new ClassReader(byteArray);
		ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
		ClassVisitor changeVisitor = new ChangeVisitor(classWriter);
		classReader.accept(changeVisitor, ClassReader.EXPAND_FRAMES);
		try {
			OutputStream outputStream = new FileOutputStream("Person2.class");
			outputStream.write(classWriter.toByteArray());
			outputStream.close();
		} catch (Exception e) {
		}
		System.out.println("Redefine Done!");
	}
	
	class ChangeVisitor extends ClassVisitor{
		
		public ChangeVisitor(ClassVisitor cv){
			super(Opcodes.ASM5,cv);
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,String signature, String[] exceptions) {
			MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
			if(name.equals("showInfo")){
				return new RedefineAdvice(mv, access, name, desc);
			}
			return mv;
		}
	}

	class RedefineAdvice extends AdviceAdapter{

		protected RedefineAdvice( MethodVisitor mv, int access,String name, String desc) {
			super(Opcodes.ASM5, mv, access, name, desc);
		}
		
		@Override
		protected void onMethodEnter() {
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
			super.onMethodEnter();
		}
		
	}
}
