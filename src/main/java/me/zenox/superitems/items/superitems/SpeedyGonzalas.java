package me.zenox.superitems.items.superitems;

import com.archyx.aureliumskills.stats.Stats;
import me.zenox.superitems.items.BasicItem;
import me.zenox.superitems.items.SuperItem;
import me.zenox.superitems.items.abilities.Speed100;
import me.zenox.superitems.items.abilities.Tarhelm;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpeedyGonzalas extends SuperItem {
    public SpeedyGonzalas() {
        super("Speedy Gonzalas", "speed_sword", Rarity.COMMON, Type.SWORD, Material.GOLDEN_SWORD, false, 1, Map.of(Stats.WISDOM, 5d), List.of(new Speed100(1, 30)));

        this.getMeta().addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Wow, thats really fast!.");
        this.getMeta().setLore(lore);
    }

    @Override
    public List<Recipe> getRecipes(List<BasicItem> registeredItems) {
        ShapedRecipe recipe = new ShapedRecipe(this.getKey(), this.getItemStack(1));
        return List.of();
    }
}