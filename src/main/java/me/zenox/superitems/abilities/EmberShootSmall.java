package me.zenox.superitems.abilities;

import me.zenox.superitems.SuperItems;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class EmberShootSmall extends ItemAbility {

    public EmberShootSmall() {
        super("fiery_ember_shoot", AbilityAction.RIGHT_CLICK_ALL, 5, 0);

        this.addLineToLore(ChatColor.GRAY + "Shoot a " + ChatColor.RED + "fireball" + ChatColor.GRAY + " that explodes");
        this.addLineToLore(ChatColor.GRAY + "on impact. Fiery Explosions!");
    }

    @Override
    public void runExecutable(Event event, Player p, ItemStack item) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);

        Location eyeLoc = p.getEyeLocation();

        Fireball f = (Fireball) eyeLoc.getWorld().spawnEntity(eyeLoc.add(eyeLoc.getDirection()), EntityType.FIREBALL);
        f.setVelocity(eyeLoc.getDirection().normalize());
        f.setMetadata("dmgEnv", new FixedMetadataValue(SuperItems.getPlugin(), false));
        f.setYield(1f);
        f.setShooter(p);
    }
}
