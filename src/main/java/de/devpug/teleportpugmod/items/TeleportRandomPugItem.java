package de.devpug.teleportpugmod.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LavaCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TeleportRandomPugItem extends Item {

	private static final int DURABILITY = 32;
	private static final Double MAX_PLANE_DISTANCE_TO_PLAYER = 512.0;
	private static final Double MAX_HEIGHT_DISTANCE_TO_PLAYER = 100.0;
	private static final long COOLDOWN_MILLISECONDS = 5000;
	
	private long lastUseTimePlayer = 0;
	private long lastUseTimeServer = 0;
	

	public TeleportRandomPugItem(Properties pProperties) {
		super(pProperties.durability(DURABILITY));
	}
	
	
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
    	
    	
    	long useTime = System.currentTimeMillis();
//		prevent player from spamming. Only allow use if teleport has finished
		if(level.isClientSide()) {
	    	long differenceUseTimePlayer = useTime - lastUseTimePlayer;
			if(differenceUseTimePlayer < COOLDOWN_MILLISECONDS) {
				logMessage("Cooldown...", player);
				return InteractionResult.FAIL;
			}
			lastUseTimePlayer = useTime;
			return InteractionResult.SUCCESS;
		}
		
    	long differenceUseTimeServer = useTime - lastUseTimeServer;
		if(differenceUseTimeServer < COOLDOWN_MILLISECONDS) {
			playSoundFail(level, player);
			return InteractionResult.FAIL;
		}
		
		logMessage("Loading random position...", player);
		playSoundCharge(level, player);
		BlockPos blockPos = findRandomPlaceAndTeleport(level, player, useTime);
		if(blockPos == null) {
//			fail
    		playSoundFail(level, player);
    		lastUseTimeServer = 0;
    		lastUseTimePlayer = 0;
    		return InteractionResult.FAIL;
		}else {
//			success
			playSoundTeleport(level, blockPos);
			hurtDurability(level, player, interactionHand);
			lastUseTimeServer = useTime;
			lastUseTimePlayer = useTime;
	        return InteractionResult.SUCCESS;
		}

    }

	private void hurtDurability(Level level, Player player, InteractionHand interactionHand) {
		player.getItemInHand(interactionHand).hurtAndBreak(1, (ServerLevel)level, (ServerPlayer)player, item -> player.onEquippedItemBroken(item, EquipmentSlot.MAINHAND));
	}
	
	private void playSoundFail(Level level, Player player) {
		level.playSound(null, player.getOnPos(), SoundEvents.VAULT_INSERT_ITEM_FAIL, SoundSource.BLOCKS);
	}
	
	private void playSoundCharge(Level level, Player player) {
		level.playSound(null, player.getOnPos(), SoundEvents.BREEZE_CHARGE, SoundSource.BLOCKS);
	}
	
	private void playSoundTeleport(Level level, BlockPos blockPos) {
		level.playSound(null, blockPos, SoundEvents.PLAYER_TELEPORT, SoundSource.BLOCKS);
	}


	private BlockPos findRandomPlaceAndTeleport(Level level, Player player, long useTime) {
		

		
        Vec3 playerPosition = player.getPosition(16);
        Vec3 targetPosition = playerPosition;
        BlockPos blockPos;
        
        boolean targetBlockIsSafe = false;
        boolean targetGroundIsSafe = false;
        
        do
        {
        	long differenceTimeLoop = System.currentTimeMillis() - useTime;
//        	System.out.println("differenceTimeLoop: " + differenceTimeLoop);
    		if(differenceTimeLoop >= COOLDOWN_MILLISECONDS) {
        		logMessage("Teleport failed, took > " + COOLDOWN_MILLISECONDS + "ms", player );
        		return null;
        	}
            
//        	RandomSource randomSource = RandomSource.create();
        	Double generatedX = Mth.nextDouble(RandomSource.create(), playerPosition.x - MAX_PLANE_DISTANCE_TO_PLAYER, playerPosition.x + MAX_PLANE_DISTANCE_TO_PLAYER);
            Double generatedY = Mth.nextDouble(RandomSource.create(), playerPosition.y - MAX_HEIGHT_DISTANCE_TO_PLAYER, playerPosition.y + MAX_HEIGHT_DISTANCE_TO_PLAYER);
            Double generatedZ = Mth.nextDouble(RandomSource.create(), playerPosition.z - MAX_PLANE_DISTANCE_TO_PLAYER, playerPosition.z + MAX_PLANE_DISTANCE_TO_PLAYER);
            
            blockPos = BlockPos.containing(generatedX , generatedY , generatedZ );
     

            targetBlockIsSafe = isSafeTargetBlock(blockPos, level, true);
            if(!targetBlockIsSafe) {
            	continue;
            }
            
            BlockPos blockBelowPos = blockPos.below();
            targetGroundIsSafe = isSafeGroundBlock(blockBelowPos, level, 1);	
            
           
        }while (!targetBlockIsSafe || !targetGroundIsSafe );
        
        targetPosition = blockPos.getBottomCenter();
        
        player.teleportTo(targetPosition.x(), targetPosition.y(), targetPosition.z());
//        player.randomTeleport(targetPosition.x(), targetPosition.y(), targetPosition.z(), true);
        
        long differenceTime = System.currentTimeMillis() - useTime;
        logMessage("Teleported to: " + blockPos.toShortString() + " in " + differenceTime + "ms ", player);
        
        return blockPos;
	}
	
	private void logMessage(String msg, Player player) {
//		TODO: use logger?
		System.out.println(msg);
		player.displayClientMessage(Component.literal(msg), true);
	}


	private boolean isSafeTargetBlock(BlockPos blockPos, Level level, boolean countWaterAsSafeTarget) {
		if(blockPos == null) {
			return false;
		}
		boolean targetBlockIsSafe = level.getBlockState(blockPos).getBlock().equals(Blocks.AIR);
		if(targetBlockIsSafe) {
			return targetBlockIsSafe;
		}
		
		if(countWaterAsSafeTarget) {
			BlockState blockState = level.getBlockState(blockPos);
			if(blockState != null) {
				Block block = blockState.getBlock();
				if(block != null) {
					if(block.equals(Blocks.WATER)){
						targetBlockIsSafe = true;
			  		}
			  	}
			}
		}
		
		return targetBlockIsSafe;
	}
	
	private boolean isSafeGroundBlock(BlockPos blockPos, Level level, int countBelow) {

//		System.out.println("countBelow: " + countBelow);
		
//		this block is too high up, player could take fall damage
		if(countBelow >= 3) {
			return false;
		}
		BlockState blockState = level.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if(block.equals(Blocks.LAVA) || block.equals(Blocks.CACTUS)){
//			ground should not be lava or cactus
			return false;
		}
		if(block.equals(Blocks.AIR)) {
//			if this groundBlock is empty, check up to 2 more blocks below
			countBelow++;
			return isSafeGroundBlock(blockPos.below(), level, countBelow);
  		}

		return true;
	}

}
