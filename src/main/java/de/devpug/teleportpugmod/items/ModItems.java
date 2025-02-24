package de.devpug.teleportpugmod.items;

import de.devpug.teleportpugmod.TeleportPugMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TeleportPugMod.MOD_ID);
	
    
    public static final RegistryObject<Item> TELEPORT_RANDOM_PUG = ITEMS.register("teleportrandompug",
            () -> new TeleportRandomPugItem(new Item.Properties().setId(ITEMS.key("teleportrandompug")))
        );
    
    public static final RegistryObject<Item> TELEPORT_RESPAWN_PUG = ITEMS.register("teleportrespawnpug",
            () -> new TeleportRespawnPugItem(new Item.Properties().setId(ITEMS.key("teleportrespawnpug")))
        );
    
//    public static final RegistryObject<Item> RANDOM_TELEPORT_PUG = ITEMS.register("teleportpug",
//            () -> new RandomTeleportPugItem(new Item.Properties().setId(ResourceKey.create(ForgeRegistries.ITEMS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath(TeleportPugMod.MOD_ID, "teleportpug"))))
//        );
    //ResourceKey[minecraft:item / teleportpugmod:teleportpug]

    

    public static void register(IEventBus eventBus) {
    	ITEMS.register(eventBus);
    }
}
