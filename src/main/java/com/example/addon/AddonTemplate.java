package com.example.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LoggerFactory.getLogger("ItemTracersAddon");
    public static final Category CATEGORY = new Category("Custom");

    @Override
    public void onInitialize() {
        LOG.info("Addon Initialized!");
        Modules.get().add(new ItemTracersModul());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }

    public static class ItemTracersModul extends Module {
        public ItemTracersModul() {
            super(CATEGORY, "item-tracers-plus", "Follow items with colored lines.");
        }

        @EventHandler
        private void onRender(Render3DEvent event) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ItemEntity item) {
                    Color color = new Color(255, 255, 255, 150); 
                    var type = item.getStack().getItem();

                    if (type == Items.ANCIENT_DEBRIS) color = new Color(255, 0, 0, 255); 
                    else if (type == Items.OAK_LOG || type == Items.STRIPPED_OAK_LOG) color = new Color(0, 0, 255, 255);

                    Vec3d start = new Vec3d(0, 0, 75)
                        .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                        .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                        .add(mc.gameRenderer.getCamera().getPos());

                    event.renderer.line(start.x, start.y, start.z, item.getX(), item.getY(), item.getZ(), color);
                }
            }
        }
    }
}
