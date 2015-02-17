package com.gmail.marzipankaiser.argumentreader;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.CoalType;
import org.bukkit.CropState;
import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.GrassSpecies;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.OfflinePlayer;
import org.bukkit.PortalType;
import org.bukkit.Rotation;
import org.bukkit.SandstoneType;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.Warning;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.conversations.Conversation;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;
import org.bukkit.material.CocoaPlant;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.messaging.PluginChannelDirection;
import org.bukkit.potion.PotionType;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.marzipankaiser.argumentreader.ArgumentType.TEnum;

public class BukkitArgumentType {

	
	////----------------------------------------------------------------------
	/// Player
	public static class TPlayer implements ArgumentType{
		static final TOr Name = 
				new TOr(IDENTIFIER, STRING, STRING_IN_ANGLE_BRACKETS);
		@Override
		public Player readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			String name = (String) Name.readAndValidateFrom(ar, context);
			Player p = Bukkit.getPlayer(name);
			if(p==null){
				ar.syntaxError("No player with name "+name);
			}
			return p;
		}

		@Override
		public String name() {
			return "online player";
		}
	};
	public static final TPlayer PLAYER = new TPlayer();
	////----------------------------------------------------------------------
	/// Offline Player
	public static class TOfflinePlayer implements ArgumentType{
		static final TOr Name = 
				new TOr(IDENTIFIER, STRING, STRING_IN_ANGLE_BRACKETS);
		@Override
		public OfflinePlayer readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			String name = (String) Name.readAndValidateFrom(ar, context);
			OfflinePlayer p = Bukkit.getOfflinePlayer(name); 
			if(p==null){
				ar.syntaxError("No player with name "+name);
			}
			return p;
		}

		@Override
		public String name() {
			return "offine player";
		}
	};
	public static final TOfflinePlayer OFFLINE_PLAYER = new TOfflinePlayer();
	////----------------------------------------------------------------------
	/// World
	public static class TWorld implements ArgumentType{
		@Override
		public World readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			World res=null;
			if(ar.tryExpect('#')){ // by UUID
				UUID uuid = JAVA_UUID.readAndValidateFrom(ar, context);
				res = Bukkit.getServer().getWorld(uuid);
				if(res==null)
					ar.syntaxError("Couldn't find world with UUID "+uuid);
			}else{
				String name = IDENTIFIER.readAndValidateFrom(ar, context);
				res = Bukkit.getServer().getWorld(name);
				if(res==null)
					ar.syntaxError("Couldn't find world with name "+name);
			}
			
			return res;
		}
		@Override
		public String name() {
			return "world";
		}
	};
	public static final TWorld WORLD = new TWorld();
	////----------------------------------------------------------------------
	/// Location
	public static class TLocation implements ArgumentType{
		@Override
		public Object readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			if(ar.tryExpect("here")){
				if(context!=null){
					Object me = context.get("me");
					if(me instanceof Player){
						return ((Player) me).getLocation();
					}else if(me instanceof CommandBlock){
						return ((CommandBlock) me).getLocation();
					}
				}
				ar.syntaxError("'here' can only be used by Players or CommandBlocks.");
			}else if(ar.peekChar()=='('){
				// Syntax in () like command
				String args = STRING_PARENTHESIZED.readAndValidateFrom(ar, context);
				ArgumentReader subargs 
					= new ArgumentReader(args, ar.getSubcommandLibrary());
				Map<String, Object> parsed =subargs.readArguments(
						Arrays.asList(
								new Argument("world", WORLD, true),
								new Argument("x", FLOAT, true),
								new Argument("y", FLOAT, true),
								new Argument("z", FLOAT, true),
								new ArgumentWithDefault("yaw", FLOAT, 0.0f),
								new ArgumentWithDefault("pitch", FLOAT, 0.0f)
						), context);
				return new Location(
						(World) parsed.get("world"),
						(Double) parsed.get("x"), 
						(Double) parsed.get("y"),
						(Double) parsed.get("z"),
						(float) (double) (Double) parsed.get("yaw"),
						(float) (double) (Double) parsed.get("pitch"));
			}
			ar.syntaxError("Invalid syntax for Location"); 
			return null;
		}
		@Override
		public String name() {
			return "Location";
		}
	};
	public static final TLocation LOCATION = new TLocation();
	
	////----------------------------------------------------------------------
	////----------------------------------------------------------------------
	/// Enums
	public static final TEnum ACHIEVEMENT = new TEnum(Achievement.class);
	public static final TEnum ART = new TEnum(Art.class);
	public static final TEnum CHAT_COLOR = new TEnum(ChatColor.class);
	public static final TEnum CROP_STATE = new TEnum(CropState.class);
	public static final TEnum COAL_TYPE = new TEnum(CoalType.class);
	public static final TEnum DIFFICULTY = new TEnum(Difficulty.class);
	public static final TEnum DYE_COLOR = new TEnum(DyeColor.class);
	public static final TEnum EFFECT = new TEnum(Effect.class);
	public static final TEnum EFFECT_TYPE = new TEnum(Effect.Type.class);
	public static final TEnum ENTITY_EFFECT = new TEnum(EntityEffect.class);
	public static final TEnum ENTITY_TYPE = new TEnum(EntityType.class);
	public static final TEnum FIREWORK_EFFECT_TYPE 
		= new TEnum(FireworkEffect.Type.class);
	public static final TEnum GAME_MODE = new TEnum(GameMode.class);
	public static final TEnum GRASS_SPECIES = new TEnum(GrassSpecies.class);
	public static final TEnum INSTRUMENT = new TEnum(Instrument.class);
	public static final TEnum MATERIAL = new TEnum(Material.class);
	public static final TEnum NOTE_TONE = new TEnum(Note.Tone.class);
	public static final TEnum PORTAL_TYPE = new TEnum(PortalType.class);
	public static final TEnum ROTATION = new TEnum(Rotation.class);
	public static final TEnum SANDSTONE_TYPE = new TEnum(SandstoneType.class);
	public static final TEnum STATISTIC = new TEnum(Statistic.class);
	public static final TEnum TREE_SPECIES = new TEnum(TreeSpecies.class);
	public static final TEnum TREE_TYPE = new TEnum(TreeType.class);
	public static final TEnum WARNING_WARNING_STATE 
		= new TEnum(Warning.WarningState.class);
	// additionally auto (Emacs) generated: (from apidocs tree, enums)
	public static final TEnum SKULL_TYPE = new TEnum(SkullType.class);
	public static final TEnum WORLD_TYPE = new TEnum(WorldType.class);
	public static final TEnum SOUND = new TEnum(Sound.class);
	public static final TEnum WORLD_ENVIRONMENT = new TEnum(World.Environment.class);
	public static final TEnum BLOCK_FACE = new TEnum(BlockFace.class);
	public static final TEnum BIOME = new TEnum(Biome.class);
	public static final TEnum PISTON_MOVE_REACTION = new TEnum(PistonMoveReaction.class);
	public static final TEnum COCOA_PLANT_COCOA_PLANT_SIZE = new TEnum(CocoaPlant.CocoaPlantSize.class);
	public static final TEnum PERMISSION_DEFAULT = new TEnum(PermissionDefault.class);
	public static final TEnum ENCHANTMENT_TARGET = new TEnum(EnchantmentTarget.class);
	public static final TEnum SKELETON_SKELETON_TYPE = new TEnum(Skeleton.SkeletonType.class);
	public static final TEnum VILLAGER_PROFESSION = new TEnum(Villager.Profession.class);
	public static final TEnum OCELOT_TYPE = new TEnum(Ocelot.Type.class);
	public static final TEnum POTION_TYPE = new TEnum(PotionType.class);
	public static final TEnum INVENTORY_TYPE = new TEnum(InventoryType.class);
	public static final TEnum INVENTORY_TYPE_SLOT_TYPE = new TEnum(InventoryType.SlotType.class);
	public static final TEnum PORTAL_CREATE_EVENT_CREATE_REASON = new TEnum(PortalCreateEvent.CreateReason.class);
	public static final TEnum EVENT_RESULT = new TEnum(Event.Result.class);
	public static final TEnum EVENT_PRIORITY = new TEnum(EventPriority.class);
	public static final TEnum ENTITY_REGAIN_HEALTH_EVENT_REGAIN_REASON = new TEnum(EntityRegainHealthEvent.RegainReason.class);
	public static final TEnum CREATURE_SPAWN_EVENT_SPAWN_REASON = new TEnum(CreatureSpawnEvent.SpawnReason.class);
	public static final TEnum ENTITY_DAMAGE_EVENT_DAMAGE_CAUSE = new TEnum(EntityDamageEvent.DamageCause.class);
	public static final TEnum ENTITY_TARGET_EVENT_TARGET_REASON = new TEnum(EntityTargetEvent.TargetReason.class);
	public static final TEnum CREEPER_POWER_EVENT_POWER_CAUSE = new TEnum(CreeperPowerEvent.PowerCause.class);
	public static final TEnum ACTION = new TEnum(Action.class);
	public static final TEnum BLOCK_IGNITE_EVENT_IGNITE_CAUSE = new TEnum(BlockIgniteEvent.IgniteCause.class);
	public static final TEnum PLAYER_ANIMATION_TYPE = new TEnum(PlayerAnimationType.class);
	public static final TEnum PLAYER_TELEPORT_EVENT_TELEPORT_CAUSE = new TEnum(PlayerTeleportEvent.TeleportCause.class);
	public static final TEnum ASYNC_PLAYER_PRE_LOGIN_EVENT_RESULT = new TEnum(AsyncPlayerPreLoginEvent.Result.class);
	public static final TEnum PLAYER_LOGIN_EVENT_RESULT = new TEnum(PlayerLoginEvent.Result.class);
	public static final TEnum PLAYER_FISH_EVENT_STATE = new TEnum(PlayerFishEvent.State.class);
	public static final TEnum HANGING_BREAK_EVENT_REMOVE_CAUSE = new TEnum(HangingBreakEvent.RemoveCause.class);
	public static final TEnum LAZY_METADATA_VALUE_CACHE_STRATEGY = new TEnum(LazyMetadataValue.CacheStrategy.class);
	public static final TEnum CONVERSATION_CONVERSATION_STATE = new TEnum(Conversation.ConversationState.class);
	public static final TEnum MAP_VIEW_SCALE = new TEnum(MapView.Scale.class);
	public static final TEnum MAP_CURSOR_TYPE = new TEnum(MapCursor.Type.class);
	public static final TEnum INVENTORY_VIEW_PROPERTY = new TEnum(InventoryView.Property.class);
	public static final TEnum SERVICE_PRIORITY = new TEnum(ServicePriority.class);
	public static final TEnum PLUGIN_LOAD_ORDER = new TEnum(PluginLoadOrder.class);
	public static final TEnum PLUGIN_CHANNEL_DIRECTION = new TEnum(PluginChannelDirection.class);
};