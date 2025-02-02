package me.zenox.superitems.item;

import com.archyx.aureliumskills.stats.Stats;
import me.zenox.superitems.Slot;
import me.zenox.superitems.abilities.*;
import me.zenox.superitems.abilities.Transcendence;
import me.zenox.superitems.attribute.AttributeRegistry;
import me.zenox.superitems.gui.EnchantingGUI;
import me.zenox.superitems.item.armoritems.*;
import me.zenox.superitems.item.basicitems.CorruptPearl;
import me.zenox.superitems.item.basicitems.GardenerSapling;
import me.zenox.superitems.item.basicitems.RavagerSkin;
import me.zenox.superitems.item.basicitems.TormentedSoul;
import me.zenox.superitems.item.superitems.ObsidilithScythe;
import me.zenox.superitems.item.superitems.VertexicalBlade;
import me.zenox.superitems.item.superitems.VoidScepter;
import me.zenox.superitems.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemRegistry {


    private final static List<ComplexItem> registeredItems = new ArrayList<>();
    public static final ComplexItem GARDENER_SAPLING = registerItem(new GardenerSapling());
    public static final ComplexItem ENCHANTED_MAGMA_BLOCK = registerItem(new ComplexItem(new ItemSettings()
            .id("enchanted_magma_block")
            .material(Material.MAGMA_BLOCK)));
    public static final ComplexItem PURIFIED_MAGMA_DISTILLATE = registerItem(new ComplexItem(new ItemSettings()
            .id("purified_magma_distillate")
            .material(Material.MAGMA_CREAM)
            .glow()));
    public static final ComplexItem ENCHANTED_BLAZE_ROD = registerItem(new ComplexItem(new ItemSettings()
            .id("enchanted_blaze_rod")
            .material(Material.BLAZE_ROD)
            .glow()));
    public static final ComplexItem BURNING_ASHES = registerItem(new ComplexItem(new ItemSettings()
            .id("burning_ashes")
            .material(Material.GUNPOWDER)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .glow()));
    public static final ComplexItem MOLTEN_POWDER = registerItem(new ComplexItem(new ItemSettings()
            .id("molten_powder")
            .material(Material.BLAZE_POWDER)
            .glow()));
    public static final ComplexItem ENCHANTED_ENDER_PEARL = registerItem(new ComplexItem(new ItemSettings()
            .id("enchanted_ender_pearl")
            .material(Material.ENDER_PEARL)));
    public static final ComplexItem ABSOLUTE_ENDER_PEARL = registerItem(new ComplexItem(new ItemSettings()
            .id("absolute_ender_pearl")
            .material(Material.ENDER_PEARL)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .glow()));
    public static final ComplexItem DARK_SKULL = registerItem(new ComplexItem(new ItemSettings()
            .id("dark_skull")
            .material(Material.WITHER_SKELETON_SKULL)
            .rarity(ComplexItem.Rarity.UNCOMMON)));
    // Cannot be migrated due to custom drop stuff!
    public static final ComplexItem TORMENTED_SOUL = registerItem(new TormentedSoul());

    public static final ComplexItem TITANIUM_CUBE = registerItem(new ComplexItem(new ItemSettings()
            .id("titanium_cube")
            .material(Material.IRON_BLOCK)
            .rarity(ComplexItem.Rarity.COMMON)));

    public static final ComplexItem MAGIC_TOY_STICK = registerItem(new ComplexItem(new ItemSettings()
            .id("magic_toy_stick")
            .material(Material.STICK)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .type(ComplexItem.Type.WAND)
            .ability(AbilityRegistry.MAGIC_MISSILE_COMBUST_6)));

    public static final ComplexItem SOUL_CRYSTAL = registerItem(new ComplexItem(new ItemSettings()
            .id("soul_crystal")
            .material(Material.END_CRYSTAL)
            .rarity(ComplexItem.Rarity.RARE)
            .type(ComplexItem.Type.DEPLOYABLE)
            .ability(AbilityRegistry.SOUL_RIFT)));

    public static final ComplexItem FIERY_EMBER_STAFF = registerItem(new ComplexItem(new ItemSettings()
            .id("fiery_ember_staff")
            .material(Material.BLAZE_ROD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .type(ComplexItem.Type.STAFF)
            .ability(new EmberShootSmall())));

    public static final ComplexItem DARK_EMBER_STAFF = registerItem(new ComplexItem(new ItemSettings()
            .id("dark_ember_staff")
            .material(Material.BLAZE_ROD)
            .rarity(ComplexItem.Rarity.RARE)
            .type(ComplexItem.Type.STAFF)
            .ability(new EmberAttune())
            .ability(new EmberShoot())));

    public static final ComplexItem TORMENTED_BLADE = registerItem(new ComplexItem(new ItemSettings()
            .id("tormented_blade")
            .material(Material.IRON_AXE)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .type(ComplexItem.Type.AXE)
            .ability(AbilityRegistry.TARHELM)));

    public static final ComplexItem SWORD_OF_JUSTICE = registerItem(new ComplexItem(new ItemSettings()
            .id("sword_of_justice")
            .material(Material.IRON_SWORD)
            .rarity(ComplexItem.Rarity.RARE)
            .type(ComplexItem.Type.SWORD)
            .enchant(Enchantment.DAMAGE_ALL, 10)
            .unbreakable()
            .attribute(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "superitems:attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND))
            .attribute(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "superitems:attack_speed", 10, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND))
            .ability(AbilityRegistry.JUSTICE)
    ));

    public static final ComplexItem CRUCIFIED_AMULET = registerItem(new ComplexItem(new ItemSettings()
            .id("crucified_amulet")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .ability(new Crucify())));

    // Need attribute system in order to migrate this because im lazy
    public static final ComplexItem DESECRATOR_HELMET = registerItem(new DesecratorHelmet());
    public static final ComplexItem DESECRATOR_CHESTPLATE = registerItem(new DesecratorChestplate());
    public static final ComplexItem DESECRATOR_LEGGINGS = registerItem(new DesecratorLeggings());
    public static final ComplexItem DESECRATOR_BOOTS = registerItem(new DesecratorBoots());

    public static final ComplexItem DESECRATOR_SCALE = registerItem(new ComplexItem(new ItemSettings()
            .id("desecrator_scale")
            .material(Material.PHANTOM_MEMBRANE)));
    public static final ComplexItem DESECRATOR_CLAW = registerItem(new ComplexItem(new ItemSettings()
            .id("desecrator_claw")
            .material(Material.DAMAGED_ANVIL)));

    public static final ComplexItem DESECRATOR_TOE = registerItem(new ComplexItem(new ItemSettings()
            .id("desecrator_toe")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjY1OGU3OTZmYTcxOTRjZjk1MTJkNDEwNjYxNWNmNDQ1MmUwYWNjNTM4OGVmMDA0ZTAxMDEzMjM0Y2Y5ZDg4In19fQ==")));

    public static final ComplexItem CRULEN_SHARD = registerItem(new ComplexItem(new ItemSettings()
            .id("crulen_shard")
            .material(Material.QUARTZ)
            .glow()));

    public static final ComplexItem PYTHEMION_GEM = registerItem(new ComplexItem(new ItemSettings()
            .id("pythemion_gem")
            .material(Material.EMERALD)
            .glow()));

    public static final ComplexItem PSYCHEDELIC_ORB = registerItem(new ComplexItem(new ItemSettings()
            .id("psychedelic_orb")
            .material(Material.ENDER_PEARL)
            .glow()));

    public static final ComplexItem HYPER_CRUX = registerItem(new ComplexItem(new ItemSettings()
            .id("hyper_crux")
            .material(Material.PLAYER_HEAD)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg4MWM4NzY0ZmJmOTNiMjMxNWNhMTEzNTczZmE3OTlmNGNmZGMwY2E4NjhjODM0MDUyNTVhN2Q1NTZhNzM5ZiJ9fX0=")
            .glow()));

    // Event stuff so no migration
    public static final ComplexItem RAVAGER_SKIN = registerItem(new RavagerSkin());

    public static final ComplexItem TOUGH_FABRIC = registerItem(new ComplexItem(new ItemSettings()
            .id("tough_fabric")
            .material(Material.LEATHER)
            .glow()));

    public static final ComplexItem KEVLAR = registerItem(new ComplexItem(new ItemSettings()
            .id("kevlar")
            .material(Material.LEATHER)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .glow()));

    public static final ComplexItem TOTEM_POLE = registerItem(new ComplexItem(new ItemSettings()
            .id("totem_pole")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU4MDkxMjhhMGU1YTQ0YzJlMzk1MzJlNmJiYzY4MjUyY2I4YzlkNWVjZDI0NmU1OTY1MDc3YzE0N2M3OTVlNyJ9fX0=")
            .ability(AbilityRegistry.CENTRALIZE)));

    public static final ComplexItem CORRUPT_TOTEM_POLE = registerItem(new ComplexItem(new ItemSettings()
            .id("corrupt_totem_pole")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.RARE)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk3YzI4NmI2ZDE2MjM5YTcxZmYxNjc0OTQ0MTZhZDk0MDcxNzIwNTEwY2Y4YTgyYWIxZjQ1MWZmNGE5MDkxNiJ9fX0=")
            .ability(AbilityRegistry.CENTRALIZE_CORRUPT)));

    public static final ComplexItem WARPED_CUBE = registerItem(new ComplexItem(new ItemSettings()
            .id("warped_cube")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVjYjVkYTFjMmVlZDQzYmY2ODUxODllMDgwMjlmYzJhZWVlZGZhZTFjNmEyMTRlNzBmNzRiOGEzMjExYjBhIn19fQ==")));

    public static final ComplexItem ROUGH_VOID_STONE = registerItem(new ComplexItem(new ItemSettings()
            .id("rough_void_stone")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ0ODc1MGJiNWFkYTI5ZGZmNjgyMGRiNjkzM2RjNjJhMGJmNmJkZTcyNzM0MWViN2RkMTg0NTNhMTBkNjQ5MyJ9fX0=")));

    public static final ComplexItem VOID_STONE = registerItem(new ComplexItem(new ItemSettings()
            .id("void_stone")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ0ODc1MGJiNWFkYTI5ZGZmNjgyMGRiNjkzM2RjNjJhMGJmNmJkZTcyNzM0MWViN2RkMTg0NTNhMTBkNjQ5MyJ9fX0=")));

    public static final ComplexItem OBSIDIAN_TABLET = registerItem(new ComplexItem(new ItemSettings()
            .id("obsidian_tablet")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWExNTlkOWFjMmZkYjE2NDg1OGI2MzUwZTAzYzE5MjRmMTdlNWFhODYxMWEzNDdkNTViNmI4OTgyMGZhZjA5NCJ9fX0=")));


    public static final ComplexItem VOID_SCEPTER = registerItem(new VoidScepter());
    public static final ComplexItem OBSIDIAN_SCYTHE = registerItem(new ObsidilithScythe());

    public static final ComplexItem VOID_HELMET = registerItem(new VoidMask());
    public static final ComplexItem VOID_CHESTPLATE = registerItem(new VoidChestplate());
    public static final ComplexItem VOID_LEGGINGS = registerItem(new VoidLeggings());
    public static final ComplexItem VOID_BOOTS = registerItem(new VoidBoots());

    public static final ComplexItem CORRUPT_PEARL = registerItem(new CorruptPearl());

    // Obsidian
    public static final ComplexItem ENCHANTED_OBSIDIAN = registerItem(new ComplexItem(new ItemSettings()
            .id("enchanted_obsidian")
            .material(Material.OBSIDIAN)
            .glow()));

    public static final ComplexItem COMPACT_OBSIDIAN = registerItem(new ComplexItem(new ItemSettings()
            .id("compact_obsidian")
            .material(Material.PLAYER_HEAD)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE5MjZiZmE5Y2FmOGJjYTkwNjkyNzgwOTc4YjVjNzRkNzEzZTg2NWY1YmRkMzc5MjA5N2IxODc5OTk3ZTU1NyJ9fX0=")));

    public static final ComplexItem CORRUPT_OBSIDIAN = registerItem(new ComplexItem(new ItemSettings()
            .id("corrupt_obsidian")
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .material(Material.PLAYER_HEAD)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFjODc0NTRmMWVlNTg1YjkwZmRiM2EzZTQwOTUyYTVjMmY4MGMwYTQ5ZGZlYzYyODcwZmRmZjE4Mzk2N2E4NCJ9fX0=")));

    public static final ComplexItem CRESTFALLEN_MONOLITH = registerItem(new ComplexItem(new ItemSettings()
            .id("crestfallen_monolith")
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .material(Material.PLAYER_HEAD)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIzOWMwZTUzZTc5ZTdlYmQ0ZGI2YzZkMDk2YzlkOWExNjBjZmYyNzgyMmMwNzdmYjhmNWQ0NTk2OWNjNDk3MiJ9fX0=")));

    public static final ComplexItem PAGES_OF_AGONY = registerItem(new ComplexItem(new ItemSettings()
            .id("pages_of_agony")
            .material(Material.PAPER)));

    public static final ComplexItem DIMENSIONAL_JOURNAL = registerItem(new ComplexItem(new ItemSettings()
            .id("dimensional_journal")
            .material(Material.BOOK)
            .rarity(ComplexItem.Rarity.RARE)
            .stat(Stats.WISDOM, 100d)
            .abilities(new Transcendence())));

    public static final ComplexItem VERTEXICAL_CORE = registerItem(new ComplexItem(new ItemSettings()
            .id("vertexical_core")
            .material(Material.PLAYER_HEAD)
            .rarity(ComplexItem.Rarity.RARE)
            .skullURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWYwMjMwNTExYjg0NGNmM2FmZjBjZWRiNDRjMTMyNDI3OTlkMzMxNTIyMzVmMTdjZWU1NzQ2NTE4NzhlZDVkMCJ9fX0=")));

    public static final ComplexItem VERTEXICAL_BLADE = registerItem(new VertexicalBlade());

    public static final ComplexItem DEV_STICK = registerItem(new ComplexItem(new ItemSettings()
            .id("dev_stick")
            .material(Material.STICK)
            .rarity(ComplexItem.Rarity.VERY_SPECIAL)
            .glow()
            .ability(AbilityRegistry.MAGIC_MISSILE_DEV)));

    public static final ComplexItem FLAMING_HELMET = registerItem(new FlamingHelmet());
    public static final ComplexItem FLAMING_CHESTPLATE = registerItem(new FlamingChestplate());
    public static final ComplexItem FLAMING_LEGGINGS = registerItem(new FlamingLeggings());
    public static final ComplexItem FLAMING_BOOTS = registerItem(new FlamingBoots());

    public static final ComplexItem VOLKEN_AXE = registerItem(new ComplexItem(new ItemSettings()
            .id("volken_axe")
            .material(Material.GOLDEN_AXE)
            .rarity(ComplexItem.Rarity.RARE)
            .type(ComplexItem.Type.AXE)));

    public static final ComplexItem VOLCANAXE = registerItem(new ComplexItem(new ItemSettings()
            .id("volkanaxe")
            .material(Material.GOLDEN_AXE)
            .enchant(Enchantment.DIG_SPEED, 6)
            .rarity(ComplexItem.Rarity.EPIC)
            .type(ComplexItem.Type.AXE)));


    public static final ComplexItem CHROMOTONIN = registerItem(new ComplexItem(new ItemSettings()
            .id ("chromotonin")
            .material(Material.LEAD)
            .rarity(ComplexItem.Rarity.OMEGA)
            .type(ComplexItem.Type.MISC)

    ));

    public static final ComplexItem VOLKEN_STICK = registerItem(new ComplexItem(new ItemSettings()
            .id("volken_stick")
            .material(Material.STICK)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .type(ComplexItem.Type.MISC)));

    public static final ComplexItem SUPER_SPACE_HELMET = registerItem(new ComplexItem(new ItemSettings()
            .id("super_space_helmet")
            .material(Material.RED_STAINED_GLASS_PANE)
            .stat(Stats.STRENGTH, 1000d)
            .stat(Stats.HEALTH, 1000d)
            .stat(Stats.TOUGHNESS, 1000d)
            .stat(Stats.REGENERATION, 1000d)
            .stat(Stats.WISDOM, 1000d)
            .stat(Stats.LUCK, 1000d)
            .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 100)
            .enchant(Enchantment.OXYGEN, 100)
            .enchant(Enchantment.DEPTH_STRIDER, 100)
            .enchant(Enchantment.WATER_WORKER, 100)
            .attribute(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "superitems:armor", 300, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD))
            .attribute(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "superitems:armor_toughness", 500, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD))
            .attribute(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "superitems:attack_speed", 5, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HEAD))
            .attribute(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "superitems:movement_speed", 5, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HEAD))
            .rarity(ComplexItem.Rarity.MYTHIC)
            .type(ComplexItem.Type.HELMET)));

    public static final ComplexItem CREATIVE_MIND = registerItem(new ComplexItem((new ItemSettings()
            .id("creative_mind")
            .material(Material.PAINTING)
            .stat(Stats.WISDOM, 1d)
            .enchant(Enchantment.OXYGEN, 1)
            .rarity(ComplexItem.Rarity.VERY_SPECIAL)
            .type(ComplexItem.Type.MISC)
            .type(ComplexItem.Type.HELMET))));

    public static final ComplexItem LAPIS_ORBITEX = registerItem(new ComplexItem(new ItemSettings()
            .id("lapis_orbitex")
            .material(Material.BLUE_DYE)
            .rarity(ComplexItem.Rarity.COMMON)
            .type(ComplexItem.Type.ENCHANTING_FUEL)
            .glow()
            .variable(EnchantingGUI.ENCHANT_FUEL_VAR, 5)));

    public static final ComplexItem SHADOW_BLADE = registerItem(new ComplexItem(new ItemSettings()
            .id("shadow_blade")
            .material(Material.IRON_SWORD)
            .rarity(ComplexItem.Rarity.UNCOMMON)
            .type(ComplexItem.Type.SWORD)
            .ability(AbilityRegistry.DARK_FURY)));

    public static final ComplexItem DARK_ORB = registerItem(new ComplexItem(new ItemSettings()
            .id("dark_orb")
            .material(Material.ENDER_PEARL)));

    public static final ComplexItem HEAT_BLAZED_CORE = registerItem(new ComplexItem(new ItemSettings()
            .id("heat_blazed_core")
            .material(Material.FIRE_CHARGE)));

    public static final ComplexItem GREATSWORD_VOLKUMOS = registerItem(new ComplexItem(new ItemSettings()
            .id("greatsword_volkumos")
            .material(Material.STONE_SWORD)
            .rarity(ComplexItem.Rarity.RARE)
            .type(ComplexItem.Type.SWORD)
            .modifier(new me.zenox.superitems.attribute.AttributeModifier("superitems:attack_speed", AttributeRegistry.ATTACK_DAMAGE, 20d, AttributeModifier.Operation.ADD_NUMBER, Slot.MAIN_HAND))
            .attribute(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "superitems:attack_speed", -3.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND))
            .stat(Stats.STRENGTH, 50d)
            .ability(AbilityRegistry.TERRA_STRIKE)));

    public static final ComplexItem HYPE = registerItem(new ComplexItem(new ItemSettings()
            .id("hype")
            .material(Material.IRON_SWORD)
            .stat(Stats.WISDOM, 100d)
            .stat(Stats.STRENGTH, 100d)
            //.ability(AbilityRegistry.WITHPLOSION) cringe
            .rarity(ComplexItem.Rarity.EPIC)
            .type(ComplexItem.Type.SWORD)));

    public static final ComplexItem DARKCALLER = registerItem(new ComplexItem(new ItemSettings()
            .id("darkcaller")
            .material(Material.BEACON)
            .stat(Stats.WISDOM, 50d)
            .enchant(Enchantment.OXYGEN, 1)
            .ability(AbilityRegistry.DARKCALL)
            .rarity(ComplexItem.Rarity.EPIC)
            .type(ComplexItem.Type.STAFF)));

    public static final ComplexItem BURNING_SMOKE = registerItem(new ComplexItem(new ItemSettings()
            .id("burning_smoke")
            .material(Material.GLASS_BOTTLE)));

    private final static List<Recipe> registeredRecipes = new ArrayList<>();

    private static ComplexItem registerItem(ComplexItem item) {
        registeredItems.add(item);
        return item;
    }

    @Deprecated
    public static List<Recipe> registerRecipes() {
        for (ComplexItem item : registeredItems) {
            registeredRecipes.addAll(item.getRecipes());
        }

        for (Recipe recipe : registeredRecipes) {
            try {
                Bukkit.addRecipe(recipe);
            } catch (IllegalStateException e) {
                if (recipe instanceof Keyed) {
                    //Util.logToConsole("Found duplicate recipe, re-adding.");
                    Bukkit.removeRecipe(((Keyed) recipe).getKey());
                    Bukkit.addRecipe(recipe);
                }

            }
        }
        return registeredRecipes;
    }

    public static void registerItems() {
        Util.logToConsole(ChatColor.WHITE + "Registering " + ChatColor.GOLD + registeredItems.size() + ChatColor.WHITE + " items.");
    }

    @Nullable
    public static ComplexItem byItem(ItemStack item) {
        try {
            PersistentDataContainer container = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
            String id = container.get(ComplexItem.GLOBAL_ID, PersistentDataType.STRING);
            return byId(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Nullable
    public static ComplexItem byId(String id) {
        for (ComplexItem item : registeredItems) {
            if (id == null) return null;
            if (id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    public static List<ComplexItem> getRegisteredItems() {
        return registeredItems;
    }

}
