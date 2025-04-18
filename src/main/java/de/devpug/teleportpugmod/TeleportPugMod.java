package de.devpug.teleportpugmod;
 
import com.mojang.logging.LogUtils;

import de.devpug.teleportpugmod.blocks.ModBlocks;
import de.devpug.teleportpugmod.items.ModItems;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TeleportPugMod.MOD_ID)
public class TeleportPugMod {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "teleportpugmod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    
    
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "teleportpugmod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);


    public TeleportPugMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
//        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
//        ITEMS.register(modEventBus);
        ModItems.register(modEventBus);
//        ModBlocks.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        
//        TODO PAU: DataGenerator!
//        modEventBus.addListener(this::addDataGenerator);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }    
    
    
    private void addDataGenerator(GatherDataEvent event) {
    	System.out.println("ADD DATA GENERATOR");
    	ModelProvider modelProvider = new ModelProvider(new PackOutput(Paths.get(MOD_ID + ":items")));
    	
    	event.getGenerator().addProvider(true, modelProvider);
    	
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {


//        if (Config.logDirtBlock)
//            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
//
//        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

//        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.TELEPORT_RANDOM_PUG);
            event.accept(ModItems.TELEPORT_RESPAWN_PUG);
            event.accept(ModItems.BROADSWORD1);
            event.accept(ModItems.BUSTERSWORD1);
        }

    }

    
}
