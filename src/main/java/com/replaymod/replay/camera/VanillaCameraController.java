package com.replaymod.replay.camera;

import de.johni0702.minecraft.gui.utils.lwjgl.vector.Vector3f;
import net.minecraft.client.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

/**
 * Camera controller performing vanilla creative-like camera movements.
 */
public class VanillaCameraController implements CameraController {
    private static final int MAX_SPEED = 1000;
    private static final int MIN_SPEED = -1000;

    private static final Vector3f[] DIRECTIONS = new Vector3f[]{
            new Vector3f(0, 0, 1), new Vector3f(0, 0, -1), new Vector3f(1, 0, 0), new Vector3f(-1, 0, 0),
            new Vector3f(0, 1, 0), new Vector3f(0, -1, 0),
    };

    private final KeyMapping[] bindings = new KeyMapping[6];

    private final CameraEntity camera;

    private int speed;

    public VanillaCameraController(Minecraft mc, CameraEntity camera) {
        this.camera = camera;
        Options options = mc.options;
        this.bindings[0] = options.keyUp;
        this.bindings[1] = options.keyDown;
        this.bindings[2] = options.keyLeft;
        this.bindings[3] = options.keyRight;
        this.bindings[4] = options.keyJump;
        this.bindings[5] = options.keyShift;
    }

    @Override
    public void update(float partialTicksPassed) {
        if (partialTicksPassed == 0) {
            return;
        }
        Vector3f direction = new Vector3f(0, 0, 0);
        for (int i = 0; i < 6; i++) { // First, get movement direction depending on keys pressed
            if (bindings[i].isDown()) {
                Vector3f.add(direction, DIRECTIONS[i], direction);
            }
        }
        if (direction.length() == 0) {
            return;
        }
        direction.normalise(direction); // Normalize, so we don't move quicker if we hold down multiple keys
        double yawRadians = Math.toRadians(camera.xRot);
        float yawSin = (float) Math.sin(yawRadians), yawCos = (float) Math.cos(yawRadians);
        // Rotate by yaw
        direction.set(
                direction.x * yawCos - direction.z * yawSin,
                direction.y,
                direction.x * yawSin + direction.z * yawCos
        );
        // Adjust for current speed
        // We transform speed to blocks per second: x->2^(x/300+1)
        direction.scale((float) Math.pow(2, speed / 300d + 1));
        // Adjust for time passed
        direction.scale(partialTicksPassed / 20);
        // Actually move
        camera.moveCamera(direction.x, direction.y, direction.z);
    }

    @Override
    public void increaseSpeed() {
        speed = Math.min(MAX_SPEED, speed + 1);
    }

    @Override
    public void decreaseSpeed() {
        speed = Math.max(MIN_SPEED, speed - 1);
    }
}
