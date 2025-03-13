package org.teafear.milkomatic;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.block.data.Directional;

public class MilkBucketPlugin extends JavaPlugin implements Listener {

    private final NamespacedKey lightningKey = new NamespacedKey(this, "struck-by-lightning");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(ChatColor.GOLD + "[Milkomatic] Loaded for MC 1.17-1.21!");
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (event.getItem().getType() != Material.BUCKET) return;
        
        event.setCancelled(true);
        Block dispenser = event.getBlock();
        
        if (!(dispenser.getState() instanceof Dispenser)) return;
        
        processMilking((Dispenser) dispenser.getState());
    }

    private void processMilking(Dispenser dispenser) {
        Inventory inv = dispenser.getInventory();
        removeBucket(inv);

        BlockFace facing = ((Directional) dispenser.getBlockData()).getFacing();
        Location targetLoc = dispenser.getLocation().add(
            facing.getModX() + 0.5,
            facing.getModY() + 0.5,
            facing.getModZ() + 0.5
        );

        targetLoc.getWorld().getNearbyEntities(targetLoc, 1.5, 1.5, 1.5).stream()
            .filter(this::isValidMilkSource)
            .findFirst()
            .ifPresent(entity -> {
                giveMilk(dispenser, targetLoc, entity);
                playEffects(targetLoc);
            });
    }

    private boolean isValidMilkSource(Entity entity) {
        if (entity instanceof Cow || entity instanceof MushroomCow) {
            return true;
        }
        
        if (entity instanceof Goat) {
            Goat goat = (Goat) entity;
            return goat.isAdult() && !goat.isScreaming();
        }
        
        if (entity instanceof Animals) {
            return entity.getPersistentDataContainer().has(lightningKey);
        }
        
        return false;
    }

    private void giveMilk(Dispenser dispenser, Location loc, Entity source) {
        ItemStack milk = new ItemStack(getMilkType(source));
        
        loc.getWorld().getNearbyPlayers(loc, 3).stream()
            .findFirst()
            .ifPresentOrElse(
                player -> giveToPlayer(player, milk, loc),
                () -> dispenser.getWorld().dropItemNaturally(loc, milk)
            );
    }

    private Material getMilkType(Entity source) {
        if (source instanceof MushroomCow) {
            return Material.SUSPICIOUS_STEW; // Для грибных коров
        }
        if (source instanceof Goat) {
            return Material.GOAT_HORN; // Для коз
        }
        if (source.getPersistentDataContainer().has(lightningKey)) {
            return Material.HONEY_BOTTLE; // Для пораженных молнией
        }
        return Material.MILK_BUCKET;
    }

    private void playEffects(Location loc) {
        loc.getWorld().playSound(loc, Sound.ENTITY_COW_MILK, 1.0f, 1.0f);
        loc.getWorld().spawnParticle(Particle.HEART, loc, 3);
    }

    private void removeBucket(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == Material.BUCKET) {
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() <= 0) inv.remove(item);
                break;
            }
        }
    }

    private void giveToPlayer(Player player, ItemStack milk, Location dropLoc) {
        if (player.getInventory().addItem(milk).isEmpty()) return;
        player.getWorld().dropItemNaturally(dropLoc, milk);
    }
}