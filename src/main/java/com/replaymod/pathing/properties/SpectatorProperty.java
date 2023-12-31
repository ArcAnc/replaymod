package com.replaymod.pathing.properties;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.replaymod.replay.ReplayHandler;
import com.replaymod.replay.camera.CameraEntity;
import com.replaymod.replaystudio.pathing.property.AbstractProperty;
import com.replaymod.replaystudio.pathing.property.PropertyPart;
import com.replaymod.replaystudio.pathing.property.PropertyParts;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Property for the camera spectating an entity.
 */
public class SpectatorProperty extends AbstractProperty<Integer> {
    public static final SpectatorProperty PROPERTY = new SpectatorProperty();
    public final PropertyPart<Integer> ENTITY_ID = new PropertyParts.ForInteger(this, false);

    private SpectatorProperty() {
        super("spectate", "replaymod.gui.playeroverview.spectate", null, -1);
    }

    @Override
    public Collection<PropertyPart<Integer>> getParts() {
        return Collections.singletonList(ENTITY_ID);
    }

    @Override
    public void applyToGame(Integer value, Object replayHandler) {
        ReplayHandler handler = ((ReplayHandler) replayHandler);
        CameraEntity cameraEntity = handler.getCameraEntity();
        if (cameraEntity == null) {
            return;
        }
        Level world = cameraEntity.getLevel();
        // Lookup entity by id, returns null if an entity with the id does not exists
        Entity target = world.getEntity(value);
        // Spectate entity, when called with null, returns to camera
        handler.spectateEntity(target);
    }

    @Override
    public void toJson(JsonWriter writer, Integer value) throws IOException {
        writer.value(value);
    }

    @Override
    public Integer fromJson(JsonReader reader) throws IOException {
        return reader.nextInt();
    }
}
