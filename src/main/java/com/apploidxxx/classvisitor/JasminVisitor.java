package com.apploidxxx.classvisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.bcel.Const;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.generic.*;

/**
 *
 * Note: don't use *.bcel.generic.*
 *
 * Use *.bcel.classfile.* instead
 *
 * Example from https://commons.apache.org/bcel/
 *
 * @author Arthur Kupriyanov
 */
public class JasminVisitor extends EmptyVisitor {
    private final JavaClass clazz;
    private final PrintWriter out;
    private final String class_name;
    private final ConstantPoolGen cp;

    public JasminVisitor(final JavaClass clazz, final OutputStream out) {
        this.clazz = clazz;
        this.out = new PrintWriter(out);
        this.class_name = clazz.getClassName();
        this.cp = new ConstantPoolGen(clazz.getConstantPool());
    }

    /**
     * Start traversal using DefaultVisitor pattern.
     */
    public void disassemble() {
        new org.apache.bcel.classfile.DescendingVisitor(clazz, this).visit();
        out.close();
    }

    @Override
    public void visitJavaClass(final JavaClass clazz) {
        out.println(";; Produced by JasminVisitor (BCEL)");
        out.println(";; " + new Date() + "\n");

        out.println(".source " + clazz.getSourceFileName());
        out.println("." + Utility.classOrInterface(clazz.getAccessFlags()) + " " +
                Utility.accessToString(clazz.getAccessFlags(), true) +
                " " + clazz.getClassName().replace('.', '/'));
        out.println(".super " + clazz.getSuperclassName().replace('.', '/'));

        for (final String iface : clazz.getInterfaceNames()) {
            out.println(".implements " + iface.replace('.', '/'));
        }

        out.print("\n");
    }

    @Override
    public void visitField(final Field field) {
        out.print(".field " + Utility.accessToString(field.getAccessFlags()) +
                " \"" + field.getName() + "\"" + field.getSignature());
        if (field.getAttributes().length == 0) {
            out.print("\n");
        }
    }

    @Override
    public void visitConstantValue(final ConstantValue cv) {
        out.println(" = " + cv);
    }

    private Method _method;

    /**
     * Unfortunately Jasmin expects ".end method" after each method. Thus we've to check
     * for every of the method's attributes if it's the last one and print ".end method"
     * then.
     */
    private void printEndMethod(final Attribute attr) {
        final Attribute[] attributes = _method.getAttributes();

        if (attr == attributes[attributes.length - 1]) {
            out.println(".end method");
        }
    }

    @Override
    public void visitDeprecated(final Deprecated attribute) {
        printEndMethod(attribute);
    }

    @Override
    public void visitSynthetic(final Synthetic attribute) {
        if (_method != null) {
            printEndMethod(attribute);
        }
    }

    @Override
    public void visitMethod(final Method method) {
        this._method = method; // Remember for use in subsequent visitXXX calls

        out.println("\n.method " + Utility.accessToString(_method.getAccessFlags()) +
                " " + _method.getName() + _method.getSignature());

        final Attribute[] attributes = _method.getAttributes();
        if ((attributes == null) || (attributes.length == 0)) {
            out.println(".end method");
        }
    }

    @Override
    public void visitExceptionTable(final ExceptionTable e) {
        for (final String name : e.getExceptionNames()) {
            out.println(".throws " + name.replace('.', '/'));
        }

        printEndMethod(e);
    }

    private Hashtable<InstructionHandle, String> map;

    @Override
    public void visitCode(final Code code) {
        int label_counter = 0;

        out.println(".limit stack " + code.getMaxStack());
        out.println(".limit locals " + code.getMaxLocals());

        final MethodGen mg = new MethodGen(_method, class_name, cp);
        final InstructionList il = mg.getInstructionList();
        final InstructionHandle[] ihs = il.getInstructionHandles();

        /* Pass 1: Give all referenced instruction handles a symbolic name, i.e. a
         * label.
         */
        map = new Hashtable<>();

        for (final InstructionHandle ih1 : ihs) {
            if (ih1 instanceof BranchHandle) {
                final BranchInstruction bi = (BranchInstruction) ih1.getInstruction();

                if (bi instanceof Select) { // Special cases LOOKUPSWITCH and TABLESWITCH
                    for (final InstructionHandle target : ((Select) bi).getTargets()) {
                        put(target, "Label" + label_counter++ + ":");
                    }
                }

                final InstructionHandle ih = bi.getTarget();
                put(ih, "Label" + label_counter++ + ":");
            }
        }

        final LocalVariableGen[] lvs = mg.getLocalVariables();
        for (final LocalVariableGen lv : lvs) {
            InstructionHandle ih = lv.getStart();
            put(ih, "Label" + label_counter++ + ":");
            ih = lv.getEnd();
            put(ih, "Label" + label_counter++ + ":");
        }

        final CodeExceptionGen[] ehs = mg.getExceptionHandlers();
        for (final CodeExceptionGen c : ehs) {
            InstructionHandle ih = c.getStartPC();

            put(ih, "Label" + label_counter++ + ":");
            ih = c.getEndPC();
            put(ih, "Label" + label_counter++ + ":");
            ih = c.getHandlerPC();
            put(ih, "Label" + label_counter++ + ":");
        }

        final LineNumberGen[] lns = mg.getLineNumbers();
        for (final LineNumberGen ln : lns) {
            final InstructionHandle ih = ln.getInstruction();
            put(ih, ".line " + ln.getSourceLine());
        }

        // Pass 2: Output code.
        for (final LocalVariableGen l : lvs) {
            out.println(".var " + l.getIndex() + " is " + l.getName() + " " +
                    l.getType().getSignature() +
                    " from " + get(l.getStart()) +
                    " to " + get(l.getEnd()));
        }

        out.print("\n");

        for (InstructionHandle ih : ihs) {
            final Instruction inst = ih.getInstruction();
            String str = map.get(ih);

            if (str != null) {
                out.println(str);
            }

            if (inst instanceof BranchInstruction) {
                if (inst instanceof Select) { // Special cases LOOKUPSWITCH and TABLESWITCH
                    final Select s = (Select) inst;
                    final int[] matchs = s.getMatchs();
                    final InstructionHandle[] targets = s.getTargets();

                    if (s instanceof TABLESWITCH) {
                        out.println("\ttableswitch " + matchs[0] + " " + matchs[matchs.length - 1]);

                        for (final InstructionHandle target : targets) {
                            out.println("\t\t" + get(target));
                        }

                    } else { // LOOKUPSWITCH
                        out.println("\tlookupswitch ");

                        for (int j = 0; j < targets.length; j++) {
                            out.println("\t\t" + matchs[j] + " : " + get(targets[j]));
                        }
                    }

                    out.println("\t\tdefault: " + get(s.getTarget())); // Applies for both
                } else {
                    final BranchInstruction bi = (BranchInstruction) inst;
                    ih = bi.getTarget();
                    str = get(ih);
                    out.println("\t" + Const.getOpcodeName(bi.getOpcode()) + " " + str);
                }
            } else {
                out.println("\t" + inst.toString(cp.getConstantPool()));
            }
        }

        out.print("\n");

        for (final CodeExceptionGen c : ehs) {
            final ObjectType caught = c.getCatchType();
            final String class_name = (caught == null) ?  // catch any exception, used when compiling finally
                    "all" : caught.getClassName().replace('.', '/');

            out.println(".catch " + class_name + " from " +
                    get(c.getStartPC()) + " to " + get(c.getEndPC()) +
                    " using " + get(c.getHandlerPC()));
        }

        printEndMethod(code);
    }

    private String get(final InstructionHandle ih) {
        final String str = new StringTokenizer(map.get(ih), "\n").nextToken();
        return str.substring(0, str.length() - 1);
    }

    private void put(final InstructionHandle ih, final String line) {
        final String str = map.get(ih);

        if (str == null) {
            map.put(ih, line);
        } else {
            if (line.startsWith("Label") || str.endsWith(line)) {
                return;
            }

            map.put(ih, str + "\n" + line); // append
        }
    }

    public static void main(final String[] argv) throws Exception {
        JavaClass java_class;

        if (argv.length == 0) {
            System.err.println("disassemble: No input files specified");
            return;
        }

        for (final String arg : argv) {
            if ((java_class = Repository.lookupClass(arg)) == null) {
                java_class = new ClassParser(arg).parse();
            }

            String class_name = java_class.getClassName();
            final int index = class_name.lastIndexOf('.');
            final String path = class_name.substring(0, index + 1).replace('.', File.separatorChar);
            class_name = class_name.substring(index + 1);

            if (!path.equals("")) {
                final File f = new File(path);
                f.mkdirs();
            }

            final String name = path + class_name + ".j";
            final FileOutputStream out = new FileOutputStream(name);
            new JasminVisitor(java_class, out).disassemble();
            System.out.println("File dumped to: " + name);
        }
    }
}
