package com.example.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;

public class AddonTemplate extends MeteorAddon {
    public static final Category CATEGORY = new Category("Custom");

    @Override
    public void onInitialize() {
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
        private final SettingGroup sgGeneral = settings.getDefaultGroup();

        private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
            .name("color")
            .description("Çizgi rengi.")
            .defaultValue(new SettingColor(255, 0, 0, 255))
            .build()
        );

        public ItemTracers() {
            super(CATEGORY, "item-tracers", "Yerdeki eşyaları takip eder.");
        }

        @EventHandler
        private void onRender(Render3DEvent event) {
            if (mc.world == null || mc.player == null) return;

            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ItemEntity item) {
                    // Sadece Antik Kalıntı (Hata riskini sıfırlamak için sabitledik)
                    if (item.getStack().getItem() != Items.ANCIENT_DEBRIS) continue;

                    event.renderer.line(
                        mc.player.getX(), mc.player.getEyeY(), mc.player.getZ(),
                        item.getX(), item.getY(), item.getZ(),
                        color.get()
                    );
                }
            }
        }
    }
}
