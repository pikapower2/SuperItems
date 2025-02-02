package me.zenox.superitems.abilities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import me.zenox.superitems.Slot;
import me.zenox.superitems.SuperItems;
import me.zenox.superitems.item.ComplexItemStack;
import me.zenox.superitems.item.ItemRegistry;
import me.zenox.superitems.persistence.NBTEditor;
import me.zenox.superitems.util.Geo;
import me.zenox.superitems.util.TriConsumer;
import me.zenox.superitems.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static me.zenox.superitems.item.ItemRegistry.TOTEM_POLE;
import static me.zenox.superitems.util.Util.getNearbyBlocks;

public class ItemAbility extends Ability {
    private static final int SHARD_SPEED = 3;
    private static final int SHARD_RADIUS = 3;
    private final AbilityAction action;

    public ItemAbility(String id, AbilityAction action, int manaCost, double cooldown) {
        super(id, manaCost, cooldown, PlayerInteractEvent.class, Slot.EITHER_HAND);
        this.action = action;
    }

    public ItemAbility(String id, AbilityAction action, int manaCost, double cooldown, Slot slot) {
        super(id, manaCost, cooldown, PlayerInteractEvent.class, slot);
        this.action = action;
    }

    public ItemAbility(String id, AbilityAction action, int manaCost, double cooldown, TriConsumer<Event, Player, ItemStack> exectuable) {
        super(id, manaCost, cooldown, PlayerInteractEvent.class, Slot.EITHER_HAND, exectuable);
        this.action = action;
    }

    @Override
    Player getPlayerOfEvent(Event e) {
        return ((PlayerInteractEvent) e).getPlayer();
    }

    @Override
    List<ItemStack> getItem(Player p, Event e) {
        return Arrays.stream(new ItemStack[]{((PlayerInteractEvent) e).getItem()}).filter(Objects::nonNull).toList();
    }

    public AbilityAction getAction() {
        return action;
    }

    @Override
    public boolean checkEvent(Event event) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);
        return action.isAction(e.getAction(), e.getPlayer().isSneaking());
    }

    /**
     * Represents some static executables
     */
    public static void soulRiftAbility(Event event, Player p, ItemStack item) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);
        Action action = e.getAction();
        World w = p.getWorld();
        Location loc;
        boolean allowed = true;
        if (SuperItems.getPlugin().isUsingWorldGuard) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
            com.sk89q.worldedit.util.Location guardLoc = BukkitAdapter.adapt(p.getLocation());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            allowed = query.testState(guardLoc, localPlayer, Flags.BUILD);
        }
        if (!allowed) {
            Util.sendMessage(p, "You cannot use this item in a worldguard region!");
            return;
        }

        e.setCancelled(true);

        if (action.equals(Action.RIGHT_CLICK_BLOCK)) loc = Objects.requireNonNull(e.getClickedBlock()).getLocation();
        else if (action.equals(Action.RIGHT_CLICK_AIR)) loc = p.getLocation();
        else return;

        EnderCrystal crystal = (EnderCrystal) w.spawnEntity(loc.add(0, 2, 0), EntityType.ENDER_CRYSTAL);
        crystal.setInvulnerable(true);
        crystal.setMetadata("dmgEnv", new FixedMetadataValue(SuperItems.getPlugin(), false));

        new BukkitRunnable() {
            final List<Block> blocks = getNearbyBlocks(crystal.getLocation(), 7, 2);
            final List<FallingBlock> fBlocks = new ArrayList<>();
            final List<LivingEntity> entities = new ArrayList<>();
            int count = 0;

            @Override
            public void run() {
                // Particle Magic
                List<Vector> dodecahedron = Geo.makeDodecahedron(loc.toVector(), 2);
                for (Vector v : dodecahedron) {
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 187, 215), 0.5F);
                    w.spawnParticle(Particle.REDSTONE, v.toLocation(w).add(0, 0.5 + Math.sin(count) / 4, 0), 1, dustOptions);
                }


                // At 5 seconds

                if (count == 100) {
                    cancel();
                    crystal.remove();
                    w.createExplosion(crystal.getLocation(), 3, false, false);
                    for (FallingBlock fallingblock : fBlocks) {
                        fallingblock.setGravity(true);
                    }
                    for (LivingEntity entity : entities) {
                        entity.setGravity(true);
                    }

                }

                Random r = new Random();
                Block block = blocks.get(r.nextInt(blocks.size()));

                if (!(block.getType().getBlastResistance() > 1200 || block.getType().equals(Material.PLAYER_HEAD) || block.getType().equals(Material.PLAYER_WALL_HEAD))) {
                    FallingBlock fBlock = w.spawnFallingBlock(block.getLocation(), block.getBlockData());
                    fBlock.setVelocity((fBlock.getLocation().toVector().subtract(crystal.getLocation().toVector()).multiply(-10).normalize()));
                    fBlock.setGravity(false);
                    fBlock.setDropItem(false);
                    fBlock.setHurtEntities(true);
                    fBlock.setMetadata("temporary", new FixedMetadataValue(SuperItems.getPlugin(), true));

                    fBlocks.add(fBlock);
                }

                for (FallingBlock fallingBlock : fBlocks) {
                    if (r.nextInt(count / 25 + 1) == 0) {
                        fallingBlock.setVelocity((fallingBlock.getLocation().toVector().subtract(crystal.getLocation().add(r.nextDouble() - 0.5, r.nextDouble() - 0.5 + 2, r.nextDouble() - 0.5).toVector()).multiply(-0.5).normalize()));
                    }
                }

                for (LivingEntity entity : entities) {
                    if (r.nextInt(count / 25 + 1) == 0 && !entity.equals(p)) {
                        entity.setVelocity((entity.getLocation().toVector().subtract(crystal.getLocation().add(r.nextDouble() - 0.5, r.nextDouble() - 0.5 + 2, r.nextDouble() - 0.5).toVector()).multiply(-0.5).normalize()));
                        entity.damage((entity.getHealth() * (2f / 3f)) / 10f + 1f);
                    }
                }

                if (count == 0) {
                    for (Entity entity : crystal.getNearbyEntities(7.5, 5, 7.5)) {
                        if (entity instanceof LivingEntity && !entity.equals(p)) {
                            entity.setVelocity((entity.getLocation().toVector().subtract(crystal.getLocation().add(0, 2, 0).toVector()).multiply(-10).normalize()));
                            entity.setGravity(false);
                            entities.add((LivingEntity) entity);
                        }
                    }
                }
                count++;
            }
        }.runTaskTimer(SuperItems.getPlugin(), 0, 0);
    }

    public static void magicMissileAbility(Event event, Player p, ItemStack item, Boolean combustion, Integer explosionPower) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);
        Random r = new Random();
        World w = p.getWorld();

        LocalPlayer localPlayer;
        com.sk89q.worldedit.util.Location guardLoc;
        RegionContainer container;
        RegionQuery query;

        if (SuperItems.getPlugin().isUsingWorldGuard) {
            localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
            guardLoc = BukkitAdapter.adapt(p.getLocation());
            container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            query = container.createQuery();

            if (!query.testState(guardLoc, localPlayer, Flags.BUILD)) {
                Util.sendMessage(p, "You cannot use this item in a worldguard region! Flags: [BUILD]");
                return;
            }
        }

        // 20% chance to remove an item from their hand
        if (r.nextInt(5) == 0 && combustion) {
            item.setAmount(e.getItem().getAmount() - 1);
            Util.sendMessage(p, ChatColor.GOLD + "Woah! Your " + ChatColor.ITALIC + "Magic Toy Stick " + ChatColor.GOLD + "combusted in your hand!", false);
            p.damage(5, p);
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 0.5F);
        }


        Trident trident = (Trident) w.spawnEntity(p.getLocation().add(0, 1.8, 0), EntityType.TRIDENT);
        Vector v = p.getLocation().getDirection().normalize().clone();
        Vector v2 = v.multiply(3);
        trident.setVelocity(v2);
        trident.setDamage(0);
        trident.setGravity(false);
        trident.setPierceLevel(127);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                trident.setVelocity(v2);
                Location loc = trident.getLocation();
                for (Entity entity :
                        trident.getNearbyEntities(2, 2, 2)) {
                    if (entity instanceof Damageable && !entity.equals(p)) {
                        ((Damageable) entity).damage(explosionPower, p);
                    }
                }
                for (int i = 0; i < 5; i++) {
                    w.spawnParticle(Particle.ELECTRIC_SPARK, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                    w.spawnParticle(Particle.REVERSE_PORTAL, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                    w.spawnParticle(Particle.ELECTRIC_SPARK, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                    w.spawnParticle(Particle.REVERSE_PORTAL, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                    w.spawnParticle(Particle.END_ROD, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                    w.spawnParticle(Particle.SMOKE_NORMAL, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                }

                if (SuperItems.getPlugin().isUsingWorldGuard) {
                    LocalPlayer localPlayer2 = WorldGuardPlugin.inst().wrapPlayer(p);
                    com.sk89q.worldedit.util.Location guardLoc2 = BukkitAdapter.adapt(trident.getLocation());
                    RegionContainer container2 = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionQuery query2 = container2.createQuery();

                    if (!query2.testState(guardLoc2, localPlayer2, Flags.BUILD)) {
                        trident.remove();
                        Util.sendMessage(p, "You cannot shoot this item into a worldguard region! Flags: [PVP]");
                        cancel();
                    }
                }

                if (trident.isInBlock()) {
                    trident.remove();
                    List<Block> blocks = getNearbyBlocks(loc, explosionPower / 2, explosionPower / 4);
                    for (Block block : blocks) {
                        if (block.getType().getBlastResistance() > 1200 || block.getType().equals(Material.PLAYER_HEAD) || block.getType().equals(Material.PLAYER_WALL_HEAD))
                            continue;
                        if (r.nextDouble() > 0.7) continue;
                        if (SuperItems.getPlugin().isUsingWorldGuard) {
                            LocalPlayer localPlayer2 = WorldGuardPlugin.inst().wrapPlayer(p);
                            com.sk89q.worldedit.util.Location guardLoc2 = BukkitAdapter.adapt(block.getLocation());
                            RegionContainer container2 = WorldGuard.getInstance().getPlatform().getRegionContainer();
                            RegionQuery query2 = container2.createQuery();


                            if (!query2.testState(guardLoc2, localPlayer2, Flags.BUILD)) {
                                continue;
                            }
                        }
                        FallingBlock fallingBlock = w.spawnFallingBlock(block.getLocation(), block.getBlockData());
                        fallingBlock.setVelocity(fallingBlock.getLocation().toVector().subtract(loc.clone().add(r.nextDouble() * 4 - 2, -2, r.nextDouble() * 4 - 2).toVector()).multiply(explosionPower * 2).normalize());
                        fallingBlock.setDropItem(false);
                        fallingBlock.setHurtEntities(true);
                        fallingBlock.setMetadata("temporary", new FixedMetadataValue(SuperItems.getPlugin(), true));
                    }
                    try {
                        trident.remove();
                    } catch (Exception ignored) {

                    }
                    w.createExplosion(loc, explosionPower, false, false, p);
                    cancel();
                }
                count++;
                if (count >= 600) {
                    trident.remove();
                    cancel();
                }

            }
        }.runTaskTimer(SuperItems.getPlugin(), 0, 2);
    }

    public static void centralizeAbility(Event event, Player p, ItemStack item, Boolean corrupted, Integer duration) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);
        World w = p.getWorld();
        Location loc = p.getLocation();
        if (e.getClickedBlock() != null) loc = e.getClickedBlock().getLocation().add(0.5, 1, 0.5);

        final Random r = new Random();

        ItemMeta meta = item.getItemMeta();

        ArmorStand totem = ((ArmorStand) w.spawnEntity(loc, EntityType.ARMOR_STAND));

        totem.setInvulnerable(false);
        totem.setGravity(false);
        totem.setVisible(false);
        totem.setPersistent(false);

        // NMS-Setting NBT data
        NBTEditor.set(totem, 2039583, "DisabledSlots");

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        List<Particle> particleList = new ArrayList<>();

        if (corrupted) {
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(SuperItems.getPlugin(), "centralize_corrupt_uses");

            int uses = 5;

            if (dataContainer.has(key, PersistentDataType.INTEGER)) {
                uses = dataContainer.get(key, PersistentDataType.INTEGER);
            }

            uses--;

            dataContainer.set(key, PersistentDataType.INTEGER, uses);

            if (uses == 0) {
                Location highestblockpos = w.getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getLocation().add(0, 1, 0);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn -s Demiser 1 " + w.getName() + "," + highestblockpos.getX() + "," + highestblockpos.getY() + "," + highestblockpos.getZ());
                p.getInventory().setItem(e.getHand(), TOTEM_POLE.getItemStack(1));
            } else {
                List<String> lore = meta.getLore();
                for (String loreitem : lore) {
                    if (loreitem.startsWith(ChatColor.LIGHT_PURPLE + "Corrupted Uses Left: ")) {
                        lore.set(lore.indexOf(loreitem), ChatColor.LIGHT_PURPLE + "Corrupted Uses Left: " + ChatColor.DARK_PURPLE + uses);
                        meta.setLore(lore);
                    }
                }
            }

            item.setItemMeta(meta);


            w.playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1, 1.2f);
            LeatherArmorMeta chestMeta = ((LeatherArmorMeta) chestplate.getItemMeta());
            chestMeta.setColor(Color.PURPLE);
            LeatherArmorMeta leggingsMeta = ((LeatherArmorMeta) leggings.getItemMeta());
            leggingsMeta.setColor(Color.PURPLE);
            LeatherArmorMeta bootsMeta = ((LeatherArmorMeta) boots.getItemMeta());
            bootsMeta.setColor(Color.PURPLE);

            chestplate.setItemMeta(chestMeta);
            leggings.setItemMeta(leggingsMeta);
            boots.setItemMeta(bootsMeta);

            totem.getEquipment().setHelmet(Util.makeSkull(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk3YzI4NmI2ZDE2MjM5YTcxZmYxNjc0OTQ0MTZhZDk0MDcxNzIwNTEwY2Y4YTgyYWIxZjQ1MWZmNGE5MDkxNiJ9fX0="));
            totem.getEquipment().setChestplate(chestplate);
            totem.getEquipment().setLeggings(leggings);
            totem.getEquipment().setBoots(boots);

            particleList.add(Particle.REVERSE_PORTAL);
            particleList.add(Particle.CRIT_MAGIC);
            particleList.add(Particle.END_ROD);

        } else {
            w.playSound(loc, Sound.ITEM_TOTEM_USE, 1, 0);

            LeatherArmorMeta chestMeta = ((LeatherArmorMeta) chestplate.getItemMeta());
            chestMeta.setColor(Color.YELLOW);
            LeatherArmorMeta leggingsMeta = ((LeatherArmorMeta) leggings.getItemMeta());
            leggingsMeta.setColor(Color.YELLOW);
            LeatherArmorMeta bootsMeta = ((LeatherArmorMeta) boots.getItemMeta());
            bootsMeta.setColor(Color.YELLOW);

            chestplate.setItemMeta(chestMeta);
            leggings.setItemMeta(leggingsMeta);
            boots.setItemMeta(bootsMeta);

            totem.getEquipment().setHelmet(Util.makeSkull(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU4MDkxMjhhMGU1YTQ0YzJlMzk1MzJlNmJiYzY4MjUyY2I4YzlkNWVjZDI0NmU1OTY1MDc3YzE0N2M3OTVlNyJ9fX0="));
            totem.getEquipment().setChestplate(chestplate);
            totem.getEquipment().setLeggings(leggings);
            totem.getEquipment().setBoots(boots);

            particleList.add(Particle.TOTEM);
            particleList.add(Particle.CRIT_MAGIC);

        }

        new BukkitRunnable() {
            final int wings = 6;
            final Location loc = totem.getLocation();
            final double startRadius = 4;
            double a = 0;
            int count = 0;
            double x = 0;
            double y = 0;
            double z = 0;
            double radius = startRadius;

            @Override
            public void run() {
                // Spawn Particles
                for (int i = 0; i < wings; i++) {
                    x = Math.cos(a + i * (2 * Math.PI / wings)) * radius;
                    y = Math.sin(a) / 2;
                    z = Math.sin(a + i * (2 * Math.PI / wings)) * radius;

                    Location loc2 = new Location(p.getWorld(), (float) (loc.getX() + x), (float) (loc.getY() + y + 1), (float) (loc.getZ() + z));
                    p.getWorld().spawnParticle(particleList.get(r.nextInt(particleList.size())), loc2, 0);
                    p.getWorld().spawnParticle(particleList.get(r.nextInt(particleList.size())), loc2, 0);

                    a += Math.PI / 150;
                    radius -= 0.025;

                    if (radius <= 0.8) radius = startRadius;

                }

                // Apply effects
                if (count % 60 == 0) {
                    if (corrupted) {
                        if (p.getLocation().distanceSquared(loc) < Math.pow(45, 2)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 70, 1));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 70, 0));
                        }
                    } else {
                        for (Entity ent : w.getNearbyEntities(loc, 40, 40, 40)) {
                            if (ent.getLocation().distanceSquared(loc) < Math.pow(30, 2)) {
                                if (ent instanceof Player) {
                                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 70, 0));
                                    ((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 70, 0));
                                }
                            }
                        }
                    }
                }

                if (count / 20 >= duration) {
                    w.playSound(loc, corrupted ? Sound.ENTITY_ELDER_GUARDIAN_DEATH : Sound.ITEM_TOTEM_USE, 1, 1.2f);
                    totem.remove();
                    cancel();
                }

                count++;
            }
        }.runTaskTimer(SuperItems.getPlugin(), 3, 1);
    }

    public static void obsidianShardAbility(Event event, Player p, ItemStack item) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);
        World w = p.getWorld();

        for (float i = 0; i < 3; i++) {
            i += .5;
            shootShard(p, new Vector(Math.cos((i) * (Math.PI / 4)) * SHARD_RADIUS, Math.sin((i) * (Math.PI / 3)) * SHARD_RADIUS, 0d).rotateAroundY(p.getLocation().getYaw() % 180).toLocation(p.getWorld()), (i + 1) * 0.2);
            i -= .5;
        }

        shootShard(p, new Location(p.getWorld(), 0, 1.8, 0), 0);
    }

    // Obsidian Shard Helper Method
    private static void shootShard(Player p, Location deltaLoc, double delay) {
        Random r = new Random();
        World w = p.getWorld();

        Arrow arrow = (Arrow) w.spawnEntity(p.getLocation().add(deltaLoc), EntityType.ARROW);
        Vector v = p.getLocation().getDirection().normalize().clone();
        Vector v2 = v.multiply(SHARD_SPEED);
        arrow.setShooter(p);
        arrow.setGravity(false);
        arrow.setPierceLevel(123);
        arrow.getLocation().setDirection(v);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setInvulnerable(true);
        arrow.setKnockbackStrength(1);
        arrow.setCritical(false);
        arrow.setDamage(0d);

        ModeledEntity shard = ModelEngineAPI.createModeledEntity(arrow);
        shard.addModel(ModelEngineAPI.createActiveModel("obsidianshard"), true);
        shard.getLookController().setBodyYaw(p.getLocation().getYaw());
//        Util.sendMessage(p, "Yaw: " + p.getLocation().getYaw());
//        Util.sendMessage(p, "Pitch: " + p.getLocation().getPitch());
        //shard.getLookController().setPitch(p.getLocation().getPitch());
        shard.getModel("obsidianshard").setLockPitch(true);
        shard.getModel("obsidianshard").setLockYaw(true);

        w.playSound(p.getLocation(), Sound.ENTITY_BEE_HURT, 1, 0.2f);

        new BukkitRunnable() {
            double count = 0;

            @Override
            public void run() {

                Location loc = arrow.getLocation();
                for (Entity entity :
                        arrow.getNearbyEntities(0.5, 2, 0.5)) {
                    if (SuperItems.getPlugin().isUsingWorldGuard) {
                        LocalPlayer localPlayer2 = WorldGuardPlugin.inst().wrapPlayer(p);
                        com.sk89q.worldedit.util.Location guardLoc2 = BukkitAdapter.adapt(arrow.getLocation());
                        RegionContainer container2 = WorldGuard.getInstance().getPlatform().getRegionContainer();
                        RegionQuery query2 = container2.createQuery();
                        if (!query2.testState(guardLoc2, localPlayer2, Flags.BUILD)) {
                            break;
                        }

                    }
                    if (entity instanceof Damageable && !entity.equals(p)) {
                        ((Damageable) entity).damage(20, p);
                        if (entity instanceof LivingEntity)
                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 1));
                    }
                }

                if (count / 20 == delay) {
                    w.playSound(loc, Sound.ITEM_AXE_WAX_OFF, 1, 1.2f);
                } else if (count / 20 > delay) {
                    arrow.setVelocity(v2);
                    if (count % 20 == 0) {
                        w.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 0.7f, 0.4f);
                    }
                }

                if (count / 20 > 30) {
                    cancel();
                    arrow.remove();
                }

                if (arrow.isInBlock()) {
                    arrow.remove();
                    cancel();
                } else {
                    for (int i = 0; i < 2; i++) {
                        w.spawnParticle(Particle.SQUID_INK, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                        w.spawnParticle(Particle.REVERSE_PORTAL, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                        w.spawnParticle(Particle.BLOCK_CRACK, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2, Material.OBSIDIAN.createBlockData());
                        w.spawnParticle(Particle.SMOKE_NORMAL, loc, 0, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, 0.2);
                    }
                }

                count = count + 1;

            }
        }.runTaskTimer(SuperItems.getPlugin(), 0, 1);
    }

    public static void tarhelmAbility(Event event, Player p, ItemStack item) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);
        p.playSound(p.getLocation(), Sound.ENTITY_RAVAGER_ATTACK, 1, 0);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 3));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 400, 0));
    }


    public static void darkcallerAbility(Event event, Player p, ItemStack item) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);
        World w = p.getWorld();

        LocalPlayer localPlayer;
        com.sk89q.worldedit.util.Location guardLoc;
        RegionContainer container;
        RegionQuery query;

        ArmorStand totem = ((ArmorStand) w.spawnEntity(p.getLocation(), EntityType.ARMOR_STAND));

        totem.setInvulnerable(false);
        totem.setGravity(false);
        totem.setVisible(false);
        totem.setPersistent(false);

        // NMS-Setting NBT data
        NBTEditor.set(totem, 2039583, "DisabledSlots");

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        List<Particle> particleList = new ArrayList<>();


        LeatherArmorMeta chestMeta = ((LeatherArmorMeta) chestplate.getItemMeta());
        chestMeta.setColor(Color.WHITE);
        LeatherArmorMeta leggingsMeta = ((LeatherArmorMeta) leggings.getItemMeta());
        leggingsMeta.setColor(Color.BLACK);
        LeatherArmorMeta bootsMeta = ((LeatherArmorMeta) boots.getItemMeta());
        bootsMeta.setColor(Color.BLACK);

        chestplate.setItemMeta(chestMeta);
        leggings.setItemMeta(leggingsMeta);
        boots.setItemMeta(bootsMeta);

        totem.getEquipment().setHelmet(Util.makeSkull(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RlMzNjOTVmZWMxYjhkOTg4MjUwZjVmNWIzYTI0ODU3NDI0MzlmYWVhYTc1ZWQ1MDZlYTAxZDc1ZTE3ZjIxIn19fQ==="));
        totem.getEquipment().setChestplate(chestplate);
        totem.getEquipment().setLeggings(leggings);
        totem.getEquipment().setBoots(boots);

    }

    public static void terraStrikeAbility(Event event, Player p, ItemStack item) {
        PlayerInteractEvent e = ((PlayerInteractEvent) event);
        Location loc = p.getLocation();
        Random r = new Random();
        Arrow arr = p.getWorld().spawnArrow(p.getEyeLocation(), loc.getDirection(), 2f, 0);
        arr.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arr.setPierceLevel(100);

        Giant giant = (Giant) p.getWorld().spawnEntity(loc, EntityType.GIANT);
        giant.setInvisible(true);
        giant.setInvulnerable(true);
        giant.setCustomName("Dinnerbone");
        giant.getEquipment().setItemInMainHand(new ComplexItemStack(ItemRegistry.GREATSWORD_VOLKUMOS).getItem());

        PacketContainer destroyArrow = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyArrow.getModifier()
                .write(0, new IntArrayList(new int[]{arr.getEntityId()}));

        SuperItems.getPlugin().getProtocolManager().broadcastServerPacket(destroyArrow);

        new BukkitRunnable(){
            boolean contacted = false;
            @Override
            public void run() {
                giant.setCustomName("Dinnerbone");

                Location giantLocation = arr.getLocation();
                giantLocation.setY(arr.getLocation().getY()-1);
                giant.teleport(giantLocation);

                if(arr.isInBlock() && !contacted){
                    contacted = true;
                    arr.setTicksLived(1);

                    p.getWorld().createExplosion(arr.getLocation(), 4, false, false, p);
                    List<Block> blocks = getNearbyBlocks(arr.getLocation(), 4, 2);

                    for (Block block : blocks) {
                        if (block.getType().getBlastResistance() > 1200 || block.getType().equals(Material.PLAYER_HEAD) || block.getType().equals(Material.PLAYER_WALL_HEAD))
                            continue;
                        if (r.nextDouble() > 0.7) continue;
                        FallingBlock fallingBlock = p.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                        fallingBlock.setVelocity(fallingBlock.getLocation().toVector().subtract(arr.getLocation().clone().toVector()).multiply(2).normalize());
                        fallingBlock.setDropItem(false);
                        fallingBlock.setHurtEntities(true);
                        fallingBlock.setMetadata("temporary", new FixedMetadataValue(SuperItems.getPlugin(), true));
                    }

                    for(Entity entity : arr.getNearbyEntities(5, 5, 5)){
                        if(entity instanceof LivingEntity && !Util.isInvulnerable(entity)){
                            LivingEntity lentity = ((LivingEntity) entity);
                            lentity.damage(75, p);
                            lentity.setVelocity(lentity.getVelocity().add(lentity.getLocation().toVector().subtract(arr.getLocation().clone().toVector())));
                        }
                    }
                }
                if((contacted && arr.getTicksLived() > 80) || arr.getTicksLived() > 200){
                    arr.remove();
                    giant.remove();
                }
            }
        }.runTaskTimer(SuperItems.getPlugin(), 0, 1);
    }

    public enum AbilityAction {

        LEFT_CLICK_BLOCK("LEFT CLICK", new Action[]{Action.LEFT_CLICK_BLOCK}, false), LEFT_CLICK_AIR("LEFT CLICK", new Action[]{Action.LEFT_CLICK_AIR}, false), LEFT_CLICK_ALL("LEFT CLICK", new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}, false), SHIFT_LEFT_CLICK("SHIFT LEFT CLICK", new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}, true),
        RIGHT_CLICK_BLOCK("RIGHT CLICK", new Action[]{Action.RIGHT_CLICK_AIR}, false), RIGHT_CLICK_AIR("RIGHT CLICK", new Action[]{Action.RIGHT_CLICK_BLOCK}, false), RIGHT_CLICK_ALL("RIGHT CLICK", new Action[]{Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR}, false), SHIFT_RIGHT_CLICK("SHIFT RIGHT CLICK", new Action[]{Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}, true);

        private final String name;
        private final Action[] actionList;
        private final boolean requiresShift;

        AbilityAction(String name, Action[] actionList, boolean requiresShift) {
            this.name = name;
            this.actionList = actionList;
            this.requiresShift = requiresShift;
        }

        public String getName() {
            return this.name;
        }

        public boolean isAction(Action action, boolean isCrouching) {
            if (this.requiresShift && !isCrouching) return false;

            return Arrays.asList(actionList).contains(action);

        }
    }
}
