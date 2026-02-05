package com.example.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import java.util.List;

public class AddonTemplate extends MeteorAddon {
    // Kategori burada tanımlı, dışarıdan (HudExample gibi) bir şey beklemiyor
    public static final Category CATEGORY = new Category("Custom");

    @Override
    public void onInitialize() {
        // Sadece kendi modülümüzü yüklüyoruz, diğer hatalı örnekleri pas geçiyoruz
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

        // Seçilebilir eşya listesi ayarı
        private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
            .name("items")
            .description("Takip edilecek eşyaları seçin.")
            .defaultValue(List.of(Items.ANCIENT_DEBRIS))
            .build()
        );

        // Çizgi rengi ayarı
        private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
            .name("line-color")
            .description("Çizgi rengi.")
            .defaultValue(new SettingColor(255, 0, 0, 255))
            .build()
        );

        public ItemTracers() {
            super(CATEGORY, "item-tracers-plus", "Eşyaları artı işaretinden takip eder.");
        }

        @EventHandler
        private void onRender(Render3DEvent event) {
            if (mc.world == null) return;

            // Dünyadaki tüm varlıkları tara
            mc.world.getEntities().forEach(entity -> {
                if (entity instanceof ItemEntity item) {
                    // Sadece seçili eşyaları filtrele
                    if (!items.get().contains(item.getStack().getItem())) return;

                    // 1.21.4 uyumlu akıcı koordinat (lastRenderX/Y/Z)
                    double x = item.lastRenderX + (item.getX() - item.lastRenderX) * event.tickDelta;
                    double y = item.lastRenderY + (item.getY() - item.lastRenderY) * event.tickDelta + 0.1;
                    double z = item.lastRenderZ + (item.getZ() - item.lastRenderZ) * event.tickDelta;

                    // Çizgiyi RenderUtils.center ile tam artı işaretinden başlat
                    event.renderer.line(
                        RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z,
                        x, y, z,
                        lineColor.get()
                    );
                }
            });
        }
    }
}
