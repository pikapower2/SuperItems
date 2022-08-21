package me.zenox.superitems.items.superitems;

import me.zenox.superitems.items.ComplexItem;
import me.zenox.superitems.items.abilities.MagicMissile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.Map;

public class DevStick extends ComplexItem {
    public DevStick() {
        super("Dev Stick", "dev_stick", Rarity.VERY_SPECIAL, Type.SUPERITEM, Material.STICK, Map.of(), List.of(new MagicMissile(15, false)));

        List<String> lore = List.of(ChatColor.RED + "" + ChatColor.ITALIC + "Built Diffrent");

        this.getMeta().setLore(lore);
        this.getMeta().addEnchant(Enchantment.DAMAGE_ALL, 20, true);
        this.getMeta().addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        this.getMeta().addEnchant(Enchantment.KNOCKBACK, 42, true);
        this.getMeta().addEnchant(Enchantment.SILK_TOUCH, 1, true);
        this.getMeta().addEnchant(Enchantment.DIG_SPEED, 50, true);
        this.getMeta()
        this.getMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    @Override
    public List<Recipe> getRecipes() {
        return super.getRecipes();
    }
}
