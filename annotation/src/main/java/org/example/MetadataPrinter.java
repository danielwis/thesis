package org.example;

import org.objectweb.asm.*;

public class MetadataPrinter extends ClassVisitor {
        public MetadataPrinter() {
                super(Opcodes.ASM9);
        }

        public void visit(int version, int access, String name,
                        String signature, String superName, String[] interfaces) {
                System.out.println(name + " extends " + superName + " {");
        }

        public void visitSource(String source, String debug) {
        }

        public void visitOuterClass(String owner, String name, String desc) {
        }

        public AnnotationVisitor visitAnnotation(String desc,
                        boolean visible) {
                return null;
        }

        public void visitAttribute(Attribute attr) {
                // Relying on Java to optimise this to a StringBuilder (though
                // raw strings aren't too bad in this case).
                String out = "Found attribute '" + attr.type + "'";
                if (attr instanceof MavenPackageAttribute) {
                        out += ": " + ((MavenPackageAttribute) attr).getValue();
                } else if (attr instanceof MavenSHAAttribute) {
                        out += ": " + ((MavenSHAAttribute) attr).getValue();
                }

                System.out.println(out);
        }

        public void visitInnerClass(String name, String outerName,
                        String innerName, int access) {
        }

        public FieldVisitor visitField(int access, String name, String desc,
                        String signature, Object value) {
                System.out.println("Found field: " + desc + " " + name);
                return null;
        }

        public MethodVisitor visitMethod(int access, String name,
                        String desc, String signature, String[] exceptions) {
                System.out.println("Found method: " + name + desc);
                return null;
        }

        public void visitEnd() {
                System.out.println("}");
        }
}
