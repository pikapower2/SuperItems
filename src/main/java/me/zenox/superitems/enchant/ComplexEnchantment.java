package me.zenox.superitems.enchant;

import com.archyx.aureliumskills.modifier.StatModifier;
import me.zenox.superitems.Slot;
import me.zenox.superitems.data.TranslatableText;
import me.zenox.superitems.item.ComplexItem;
import me.zenox.superitems.item.ComplexItemStack;
import me.zenox.superitems.util.QuadConsumer;
import me.zenox.superitems.util.Util;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public abstract class ComplexEnchantment {
    private final String id;
    private final TranslatableText name;
    private final int maxLevel;
    // WEIGHT, not rarity (smaller is rarer)
    private final int rarity;
    private final List<ComplexItem.Type> types;
    private final List<Slot> slots;
    private final List<StatModifier> stats;
    private final QuadConsumer<Event, Integer, ItemStack, Player> executable;
    private final Enchantment vanillaEnchant;
    private final List<ComplexEnchantment> exclusive;

    private final Class<? extends Event> eventType;

    private static final List<ComplexEnchantment> registeredEnchants = new ArrayList<>();

    /**
     * New instance of ComplexEnchantment
     *
     * @param id
     * @param maxLevel
     * @param rarity
     * @param types
     * @param slot
     * @param stats
     * @param executable
     * @param vanillaEnchant
     * @param exclusive
     * @param eventType
     */
    public ComplexEnchantment(String id, int maxLevel, int rarity, List<ComplexItem.Type> types, List<Slot> slot, List<StatModifier> stats, QuadConsumer<Event, Integer, ItemStack, Player> executable, Enchantment vanillaEnchant, List<ComplexEnchantment> exclusive, Class<? extends Event> eventType){
        this.id = id;
        this.name = new TranslatableText(TranslatableText.Type.ENCHANT_NAME + "-" + id);
        this.maxLevel = maxLevel;
        this.rarity = rarity;
        this.types = types;
        this.slots = slot;
        this.stats = stats;
        this.executable = executable;
        this.vanillaEnchant = vanillaEnchant;
        this.exclusive = exclusive;
        this.eventType = eventType;

        for (ComplexEnchantment enchantment :
                registeredEnchants) {
            if (enchantment.getId().equalsIgnoreCase(id)) {
                Util.logToConsole("Duplicate Enchantment ID: " + id + " | Exact Match: " + enchantment.equals(this));
                throw new IllegalArgumentException("Enchantment ID cannot be duplicate");
            }
        }

        registeredEnchants.add(this);
    }

    public ComplexEnchantment(EnchantmentSettings settings, Class<? extends Event> eventType) {
        this(settings.getId(), settings.getMaxLevel(), settings.getRarity(), settings.getTypes(), settings.getSlots(), settings.getStats(), settings.getExecutable(), settings.getVanillaEnchant(), settings.getExclusive(), eventType);
    }

    public ComplexEnchantment(String id, int maxLevel, int rarity, List<ComplexItem.Type> types, Slot slot, List<StatModifier> stats, QuadConsumer<Event, Integer, ItemStack, Player> executable, Enchantment vanillaEnchant, List<ComplexEnchantment> exclusive, Class<? extends Event> eventType){
        this(id, maxLevel, rarity, types, List.of(slot), stats, executable, vanillaEnchant, exclusive, eventType);
    }

    @Nullable
    public static ComplexEnchantment byId(String id){
        try {
            return registeredEnchants.stream()
                    .filter(enchantment -> enchantment.getId().equalsIgnoreCase(id))
                    .toList().get(0);
        } catch(ArrayIndexOutOfBoundsException ignored){
            return null;
        }
    }

    public static List<ComplexEnchantment> getRegisteredEnchants() {
        return registeredEnchants;
    }

    public void useEnchant(Event e){
        if (!this.eventType.isInstance(e)) return;
        if (!checkEvent(e)) return;
        Player p = getPlayerOfEvent(e);
        List<ItemStack> items = new ArrayList<>();
        for (Slot slot : this.slots){
            items.addAll(slot.item(p));
        }
        for (ItemStack item : items){
            if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null) continue;
            ComplexItemStack cItem = ComplexItemStack.of(item);
            if(cItem != null && cItem.getComplexMeta().getComplexEnchants().containsKey(this))
            executable.accept(e, cItem.getComplexMeta().getComplexEnchants().get(this), item, p);
        }


    }

    abstract boolean checkEvent(Event e);

    abstract Player getPlayerOfEvent(Event e);


    public String getId() {
        return id;
    }

    public TranslatableText getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getRarity() {
        return rarity;
    }

    public List<ComplexItem.Type> getTypes() {
        return types;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public List<StatModifier> getStats() {
        return stats;
    }

    public QuadConsumer<Event, Integer, ItemStack, Player> getExecutable() {
        return executable;
    }

    public Enchantment getVanillaEnchant() {
        return vanillaEnchant;
    }

    public List<ComplexEnchantment> getExclusive() {
        return exclusive;
    }
}
