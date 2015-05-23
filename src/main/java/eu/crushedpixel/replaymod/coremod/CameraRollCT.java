package eu.crushedpixel.replaymod.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

public class CameraRollCT implements IClassTransformer {

    private static final String REPLAY_HANDLER = "eu/crushedpixel/replaymod/replay/ReplayHandler";
    private static final String CLASS_NAME = "net.minecraft.client.renderer.EntityRenderer";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (CLASS_NAME.equals(transformedName)) {
            return transform(bytes, name.equals(transformedName) ? "orientCamera" : "g");
        }
        return bytes;
    }

    private byte[] transform(byte[] bytes, String name_orientCamera) {
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        boolean success = false;
        for (MethodNode m : classNode.methods) {
            if ("(F)V".equals(m.desc) && name_orientCamera.equals(m.name)) {
                ListIterator<AbstractInsnNode> iter = m.instructions.iterator();
                int f = 0;
                while (iter.hasNext()) {
                    AbstractInsnNode node = iter.next();
                    if ((f == 0 || f == 1) && node.getOpcode() == FCONST_0) {
                        f++;
                    } else if ((f == 2) && node instanceof LdcInsnNode && ((LdcInsnNode) node).cst.equals(-0.1f)) {
                        f++;
                    } else if (f == 3) {
                        inject(iter);
                        success = true;
                        break;
                    } else {
                        f = 0;
                    }
                }
            }
        }
        if (!success) {
            throw new NoSuchMethodError();
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    /**
     * public void orientCamera(float f) {
     *     ...
     *     if (ReplayHandler.isInReplay()) {
     *         GL11.glRotated(ReplayHandler.getCameraTilt(), 0, 0, 1);
     *     }
     *     ...
     * }
     */
    private void inject(ListIterator<AbstractInsnNode> iter) {
        LabelNode l = new LabelNode();
        iter.add(new MethodInsnNode(INVOKESTATIC, REPLAY_HANDLER, "isInReplay", "()Z", false));
        iter.add(new JumpInsnNode(IFEQ, l));
        iter.add(new MethodInsnNode(INVOKESTATIC, REPLAY_HANDLER, "getCameraTilt", "()F", false));
        iter.add(new InsnNode(F2D));
        iter.add(new LdcInsnNode(0D));
        iter.add(new LdcInsnNode(0D));
        iter.add(new LdcInsnNode(1D));
        iter.add(new MethodInsnNode(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glRotated", "(DDDD)V", false));
        iter.add(l);
        System.out.println("REPLAY MOD CORE PATCHER: Patched EntityRenderer.orientCamera(F) method");
    }
}
