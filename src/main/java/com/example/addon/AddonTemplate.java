package com.example.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LoggerFactory.getLogger("ItemTracersAddon");
    public static final Category CATEGORY = new Category("Custom");
    public static final HudGroup HUD_GROUP = new HudGroup("Example");

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

        // ÖZELLİK: İstediğin eşyaları buradan seçeceksin
        private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
            .name("items")
            .description("Takip edilecek eşyaları seçin (Örn: Antik Kalıntı).")
            .defaultValue(List.of(Items.ANCIENT_DEBRIS))
            .build()
        );

        private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
            .name("line-color")
            .description("Çizgi rengi.")
            .defaultValue(new SettingColor(255, 0, 0, 255))
            .build()
        );

        public ItemTracers() {
            super(CATEGORY, "item-tracers-plus", "Sadece seçili eşyaları takip eder.");
        }

        @EventHandler
        private void onRender(Render3DEvent event) {
            if (mc.world == null || mc.player == null) return;

            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ItemEntity item) {
                    // Sadece listede seçtiğin eşyalar için çizgi çiz (Kırıktaş vb. elenir)
                    if (!items.get().contains(item.getStack().getItem())) continue;

                    // HATA DÜZELTME: Modern Minecraft sürümleri için lerp fonksiyonu kullanıyoruz
                    double x = item.lastRenderX + (item.getX() - item.lastRenderX) * event.tickDelta;
                    double y = item.lastRenderY + (item.getY() - item.lastRenderY) * event.tickDelta + 0.1;
                    double z = item.lastRenderZ + (item.getZ() - item.lastRenderZ) * event.tickDelta;

                    Vec3d start = mc.player.getEyePos();

                    event.renderer.line(
                        start.x, start.y, start.z,
                        x, y, z,
                        lineColor.get()
                    );
                }
            }
        }
    }
}
