package org.teafear.milkomatic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MilkBucketPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Плагин успешно включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин успешно выключен!");
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        // Проверяем, что предмет - это пустое ведро
        if (event.getItem().getType() == Material.BUCKET) {
            Block block = event.getBlock();

            // Проверяем, что блок - это раздатчик
            if (block.getState() instanceof Dispenser) {
                Dispenser dispenser = (Dispenser) block.getState();
                Location dispenseLocation = dispenser.getLocation();
                Location targetLocation = dispenseLocation.clone().add(event.getVelocity());

                // Проверяем, есть ли корова в направлении выброса
                targetLocation.getWorld().getNearbyEntities(targetLocation, 1, 1, 1).stream()
                        .filter(entity -> entity instanceof Cow)
                        .findFirst()
                        .ifPresent(cow -> {
                            event.setCancelled(true); // Отменяем стандартное выбрасывание

                            // Создаем ведро с молоком
                            ItemStack milkBucket = new ItemStack(Material.MILK_BUCKET);

                            // Выбрасываем ведро с молоком
                            Item droppedItem = targetLocation.getWorld().dropItemNaturally(targetLocation, milkBucket);

                            // Устанавливаем траекторию падения
                            droppedItem.setVelocity(new Vector(0, 0, 0));

                            // Издаем звук доения коровы
                            cow.getWorld().playSound(cow.getLocation(), Sound.ENTITY_COW_MILK, 1.0f, 1.0f);
                        });
            }
        }
    }
}
