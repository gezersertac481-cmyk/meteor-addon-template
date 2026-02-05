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

        // Karmaşık liste yerine basit aç/kapat ayarı (Hata riskini bitirir)
        private final Setting<Boolean> onlyDebris = sgGeneral.add(new BoolSetting.Builder()
            .name("only-ancient-debris")
            .description("Sadece Antik Kalıntıları gösterir.")
            .defaultValue(true)
            .build()
        );

        private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
            .name("line-color")
            .description("Çizgi rengi.")
            .defaultValue(new SettingColor(255, 0, 0, 255))
            .build()
        );

        public ItemTracers() {
            super(CATEGORY, "item-tracers-plus", "Yerdeki eşyaları takip eder.");
        }

        @EventHandler
        private void onRender(Render3DEvent event) {
            if (mc.world == null || mc.player == null) return;

            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ItemEntity item) {
                    // Filtreleme
                    if (onlyDebris.get() && item.getStack().getItem() != Items.ANCIENT_DEBRIS) continue;

                    // 1.21.4'te Render3DEvent artık renderer'ı metodla çağırıyor olabilir
                    // En güvenli çizim:
                    double x = item.getX();
                    double y = item.getY() + 0.1;
                    double z = item.getZ();

                    event.renderer.line(
                        mc.player.getX(), mc.player.getEyeY(), mc.player.getZ(),
                        x, y, z,
                        lineColor.get()
                    );
                }
            }
        }
    }
}
