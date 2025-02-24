package de.devpug.teleportpugmod.blocks;

import java.util.function.Supplier;

import de.devpug.teleportpugmod.TeleportPugMod;
import de.devpug.teleportpugmod.items.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TeleportPugMod.MOD_ID);
	
	public static final RegistryObject<Block> TEST_BLOCK = registerBlock("test_block", () -> new Block(BlockBehaviour.Properties.of()));
	
	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}
	
	private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block){
		ModItems.ITEMS.register(name,  () -> new BlockItem(block.get(), new Item.Properties()));
	}
	
	public static void register(IEventBus eventbBus) {
		BLOCKS.register(eventbBus);
	}
	
}
