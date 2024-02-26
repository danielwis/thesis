package org.example;

import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.*;

class CustomAttribute extends Attribute {
    String value = "hello world";

    // Create a named custom attribute
    CustomAttribute(String attrName /* , String value */) {
        super(attrName);
    }

    @Override
    protected Attribute read(ClassReader cr, int off, int len,
            char[] buf, int codeOff, Label[] labels) {
        return new CustomAttribute(cr.readUTF8(off, buf));
    }

    @Override
    protected ByteVector write(ClassWriter cw, byte[] code, int len,
            int maxStack, int maxLocals) {
        return new ByteVector().putShort(cw.newUTF8(value));
    }
}

/*
 * A ClassVisitor can be seen as an event filter (ASM manual, p. 14)
 * Methods must be called in the following order:
 * visit visitSource? visitOuterClass? ( visitAnnotation | visitAttribute )*
 * ( visitInnerClass | visitField | visitMethod )*
 * visitEnd
 */
class AttributeVisitor extends ClassVisitor {
    /*
     * We pass in a ClassWriter as the visitor here (it's a subclass of it that
     * "serialises"/writes whatever it comes across). Any calls not overridden
     * will just be delegated to `cv` instead.
     */
    public AttributeVisitor(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    /*
     * "Filter" the end of the class by adding a new attribute to the
     * superclass visitor (in our case, the writer) before exiting.
     */
    @Override
    public void visitEnd() {
        cv.visitAttribute(new CustomAttribute("Maven pkg name"));
        cv.visitAttribute(new CustomAttribute("Maven SHA1"));
        cv.visitEnd();
    }
}

public class MetadataAdder {
    ClassReader reader;
    ClassWriter writer;
    String filePath = "Experiment.class";

    public MetadataAdder(byte[] bytes) throws IOException {
        reader = new ClassReader(bytes);
        /*
         * Keep a reference to the reader to enable copying optimisations (see
         * ASM manual, pp. 20-21). Should not be done when removing/renaming
         * stuff as it can result in things not being removed properly, but
         * it's fine when just used for adding.
         */
        writer = new ClassWriter(reader, 0);
    }

    public void add() {
        System.out.println("Attempting to add attribute...");
        reader.accept(new AttributeVisitor(writer), 0);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // Write the byte array to the file
            fos.write(writer.toByteArray()); // this throws...?
            System.out.println("Byte array successfully written to the file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
