package com.teafear.milkomatic;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.*;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;

public class MilkBucketPlugin extends JavaPlugin implements Listener {

    private double throwSpeed;
    private boolean enableSounds;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Milkomatic успешно включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Milkomatic успешно выключен!");
    }

    private void loadConfig() {
        reloadConfig();
        throwSpeed = getConfig().getDouble("settings.throw-speed", 0.5);
        enableSounds = getConfig().getBoolean("settings.enable-sounds", true);
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (event.getItem().getType() != Material.BUCKET) return;
        event.setCancelled(true);

        Block dispenserBlock = event.getBlock();
        if (!(dispenserBlock.getState() instanceof Dispenser)) return;
        
        Dispenser dispenser = (Dispenser) dispenserBlock.getState();
        Inventory inv = dispenser.getInventory();

        // Удаление ведра
        if (!removeBucket(inv)) {
            return;
        }
        dispenser.update(true); // Обновляем инвентарь диспенсера

        BlockFace facing = ((Directional) dispenser.getBlockData()).getFacing();
        Block targetBlock = dispenserBlock.getRelative(facing);
        
        // Проверка на твёрдый блок и воздух
        if (targetBlock.getType().isSolid() || targetBlock.getType() != Material.AIR) {
            return;
        }

        Location targetLoc = targetBlock.getLocation().add(0.5, 0.5, 0.5);
        ItemStack milkBucket = new ItemStack(Material.MILK_BUCKET);

        // Выбрасываем ведро и настраиваем его
        Item droppedItem = dispenser.getWorld().dropItem(targetLoc, milkBucket);
        Vector velocity = new Vector(
            facing.getModX() * throwSpeed,
            facing.getModY() * throwSpeed + 0.2,
            facing.getModZ() * throwSpeed
        );
        droppedItem.setVelocity(velocity);
        droppedItem.setPickupDelay(20); // Задержка подбора
        droppedItem.setCanMobPickup(false);

        if (enableSounds) {
            targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_COW_MILK, 1.0f, 1.0f);
        }
    }

    private boolean removeBucket(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() == Material.BUCKET) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    inv.setItem(i, null);
                }
                return true;
            }
        }
        return false;
    }
}