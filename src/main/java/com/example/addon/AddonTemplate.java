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
import java.util.ArrayList;

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

        // İSTEDİĞİN ÖZELLİK: Takip edilecek eşya listesini seçme ayarı
        private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
            .name("items")
            .description("Takip edilecek eşyaları seçin.")
            .defaultValue(List.of(Items.ANCIENT_DEBRIS, Items.DIAMOND, Items.NETHERITE_INGOT))
            .build()
        );

        private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
            .name("line-color")
            .description("Takip çizgisinin rengi.")
            .defaultValue(new SettingColor(255, 255, 255, 255))
            .build()
        );

        public ItemTracers() {
            super(CATEGORY, "item-tracers-plus", "Yerdeki seçili eşyaları çizgilerle takip eder.");
        }

        @EventHandler
        private void onRender(Render3DEvent event) {
            if (mc.world == null || mc.player == null) return;

            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ItemEntity item) {
                    // Sadece listede seçtiğin eşyalar için çizgi çiz
                    if (!items.get().contains(item.getStack().getItem())) continue;

                    // Çizgi başlangıcı: Oyuncunun göz hizası
                    Vec3d start = mc.player.getEyePos();
                    
                    // Çizgi bitişi: Eşyanın tam merkezi (Yere gömülmemesi için +0.2 ekledik)
                    double x = item.prevX + (item.getX() - item.prevX) * event.tickDelta;
                    double y = item.prevY + (item.getY() - item.prevY) * event.tickDelta + 0.2;
                    double z = item.prevZ + (item.getZ() - item.prevZ) * event.tickDelta;

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
