package com.example.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
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
    public static final HudGroup HUD_GROUP = new HudGroup("Example");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Item Tracers Addon");
        Modules.get().add(new ItemTracers());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }

    public static class ItemTracers extends Module {
        public ItemTracers() {
            super(CATEGORY, "item-tracers-plus", "Eşyaları renkli çizgilerle takip eder.");
        }

        @EventHandler
        private void onRender(Render3DEvent event) {
            if (mc.world == null || mc.player == null) return;

            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ItemEntity item) {
                    Color color = new Color(255, 255, 255, 150);
                    if (item.getStack().getItem() == Items.ANCIENT_DEBRIS) color = new Color(255, 0, 0, 255);

                    // HATA BURADAYDI: mc.gameRenderer.getCamera().getPos() yerine Yarn standartı olan pos kullanıldı
                    Vec3d cameraPos = mc.gameRenderer.getCamera().pos;

                    event.renderer.line(
                        cameraPos.x, cameraPos.y, cameraPos.z,
                        item.getX(), item.getY(), item.getZ(),
                        color
                    );
                }
            }
        }
    }
}
