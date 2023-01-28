package com.replaymod.render.blend.exporters;

import com.replaymod.render.blend.BlendMeshBuilder;
import com.replaymod.render.blend.Exporter;
import com.replaymod.render.blend.data.DMesh;
import com.replaymod.render.blend.data.DObject;
import net.minecraft.client.model.geom.ModelPart;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

import static com.replaymod.render.blend.Util.isGlTextureMatrixIdentity;

public class ModelRendererExporter implements Exporter {
    private final RenderStateShard renderState;

    public ModelRendererExporter(RenderStateShard renderState) {
        this.renderState = renderState;
    }

    @Override
    public void setup() throws IOException {
    }

    public void preRenderModel(ModelPart model, float scale) {
        DObject object = getObjectForModel(model, scale);
        renderState.pushObject(object);
        renderState.pushModelView();
    }

    public void onRenderModel() {
        if (!GL11.glIsEnabled(GL11.GL_TEXTURE_2D) || !isGlTextureMatrixIdentity()) {
            return;
        }
        renderState.popModelView();
        renderState.pushModelView();
        renderState.applyLastModelViewTransformToObject();
        DObject object = renderState.peekObject();
        object.setVisible(renderState.getFrame());
        object.keyframeLocRotScale(renderState.getFrame());
    }

    public void postRenderModel() {
        renderState.pop();
    }

    private DObject getObjectForModel(ModelPart model, float scale) {
        int frame = renderState.getFrame();
        DObject parent = renderState.peekObject();
        DObject object = null;
        for (DObject child : parent.getChildren()) {
            if (child.lastFrame < frame
                    && child instanceof ModelBasedDObject
                    && ((ModelBasedDObject) child).isBasedOn(model, scale)) {
                object = child;
                break;
            }
        }
        if (object == null) {
            object = new ModelBasedDObject(model, scale);
            object.id.name = "???"; // FIXME 1.15 can we somehow nicely derive this?
            object.setParent(parent);
        }
        object.lastFrame = frame;
        return object;
    }

    private static DMesh generateMesh(ModelPart model, float scale) {
        DMesh mesh = new DMesh();
        BlendMeshBuilder builder = new BlendMeshBuilder(mesh);
        // FIXME 1.15
        builder.maybeFinishDrawing();
        return mesh;
    }

    private static class ModelBasedDObject extends DObject {
        private final ModelPart model;
        private final float scale;
        private boolean valid;

        public ModelBasedDObject(ModelPart model, float scale) {
            super(generateMesh(model, scale));
            this.model = model;
            this.scale = scale;
        }

        public boolean isBasedOn(ModelPart model, float scale) {
            return this.model == model && Math.abs(this.scale - scale) < 1e-4;
        }

        @Override
        public void setVisible(int frame) {
            valid = true;
            super.setVisible(frame);
        }

        @Override
        public boolean isValid() {
            return valid;
        }
    }
}
