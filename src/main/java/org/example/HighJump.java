package org.example;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.NumberSetting;
import org.rusherhack.core.setting.StringSetting;

public class HighJump extends ToggleableModule {
    
    private final StringSetting mode = new StringSetting("Mode", "Shulker");
    private final NumberSetting<Double> range = new NumberSetting<>("Range", 6.0, 1.0, 15.0).incremental(0.5);
    private final NumberSetting<Double> power = new NumberSetting<>("Power", 2.0, 0.1, 10.0).incremental(0.1);

    public HighJump() {
        super("HighJump", "Jumps higher under specific conditions.", ModuleCategory.MOVEMENT);
        this.registerSettings(this.mode, this.range, this.power);
    }

    @Subscribe
    private void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.level == null) return;

        // Trigger only when the player jumps (presses spacebar / jump input)
        if (mc.options.keyJump.isDown()) {
            boolean shouldBoost = false;
            String modeVal = this.mode.getValue().trim().toLowerCase();

            if (modeVal.equals("normal")) {
                shouldBoost = true;
            } else if (modeVal.equals("shulker")) {
                // Check if a Shulker entity is within range
                double r = this.range.getValue();
                for (Entity entity : mc.level.getEntities((Entity) null, mc.player.getBoundingBox().inflate(r), entity -> entity instanceof Shulker)) {
                    if (mc.player.distanceTo(entity) <= r) {
                        shouldBoost = true;
                        break;
                    }
                }

                // If not found, check if a Shulker Box block entity is nearby
                if (!shouldBoost) {
                    BlockPos playerPos = mc.player.blockPosition();
                    int intRange = (int) Math.ceil(r);
                    for (int x = -intRange; x <= intRange; x++) {
                        for (int y = -intRange; y <= intRange; y++) {
                            for (int z = -intRange; z <= intRange; z++) {
                                BlockPos pos = playerPos.offset(x, y, z);
                                if (mc.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= r * r) {
                                    BlockEntity be = mc.level.getBlockEntity(pos);
                                    if (be instanceof ShulkerBoxBlockEntity) {
                                        shouldBoost = true;
                                        break;
                                    }
                                }
                            }
                            if (shouldBoost) break;
                        }
                        if (shouldBoost) break;
                    }
                }
            }

            if (shouldBoost) {
                Vec3 velocity = mc.player.getDeltaMovement();
                // Apply vertical velocity boost
                mc.player.setDeltaMovement(velocity.x, this.power.getValue(), velocity.z);
            }
        }
    }
}
