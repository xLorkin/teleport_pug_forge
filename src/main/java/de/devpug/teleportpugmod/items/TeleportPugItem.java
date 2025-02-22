package de.devpug.teleportpugmod.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.phys.Vec3;

public class TeleportPugItem extends Item {

	public static final int DURABILITY = 32;
	
	private static final Double MAX_PLANE_DISTINCE_TO_PLAYER = 256.0;
	private static final Double MAX_HEIGHT_DISTANCE_TO_PLAYER = 64.0;
	private static final long MIN_COOLDOWN_MILLISECONDS = 5000;
	
	private long lastTeleportTimePlayer = 0;
	private long lastTeleportTimeServer = 0;
	

	public TeleportPugItem(Properties pProperties) {
		super(pProperties);
	}
	
	
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
    	
    	System.out.println("isClientSide: " + level.isClientSide());
    	
    	long currentTime = System.currentTimeMillis();


    	
//		TODO: prevent player from spamming. Only allow use if teleport has finished


		if(level.isClientSide()) {
	    	long differenceTimePlayer = currentTime - lastTeleportTimePlayer;
	    	System.out.println("differenceTimePlayer: " + differenceTimePlayer);
			if(differenceTimePlayer < MIN_COOLDOWN_MILLISECONDS) {
				return InteractionResult.FAIL;
			}
			lastTeleportTimePlayer = currentTime;
			return InteractionResult.SUCCESS;
		}
		
    	long differenceTimeServer = currentTime - lastTeleportTimeServer;
    	System.out.println("differenceTimeServer: " + differenceTimeServer);
		if(differenceTimeServer < MIN_COOLDOWN_MILLISECONDS) {
			playSoundFail(level, player);
			return InteractionResult.FAIL;
		}

		
//		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TeleportPugMod.MOD_ID, "teleportpug"));
//		System.out.println("ResourceKey: " + key.toString());
//		System.out.println("Item ID: " + Item.getId(this));
//		System.out.println("Description ID: " + getDescriptionId());
		
		playSoundCharge(level, player);
		BlockPos blockPos = findRandomPlaceAndTeleport(level, player);
		playSoundTeleport(level, blockPos);
		hurtDurability(level, player, interactionHand);
		lastTeleportTimeServer = currentTime;
    	
        return InteractionResult.SUCCESS;
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


	private BlockPos findRandomPlaceAndTeleport(Level level, Player player) {
		RandomSource randomSource = RandomSource.create();

long startTime = System.currentTimeMillis();
		
        Vec3 playerPosition = player.getPosition(DEFAULT_MAX_STACK_SIZE);
        Vec3 targetPosition = playerPosition;
        BlockPos blockPos;
        
        boolean targetCoordinatesAreInAir = false;
        boolean targetCoordinateBelowIsGround = false;
//        boolean possibleRespawnPoint = false;

        long counterLoops = 0l;
        do
        {
//            counterLoops++;
//            System.out.println("Loops: " + counterLoops);
            
        	Double generatedX = Mth.nextDouble(randomSource, playerPosition.x - MAX_PLANE_DISTINCE_TO_PLAYER, playerPosition.x + MAX_PLANE_DISTINCE_TO_PLAYER);
            Double generatedY = Mth.nextDouble(randomSource, playerPosition.y - MAX_HEIGHT_DISTANCE_TO_PLAYER, playerPosition.y + MAX_HEIGHT_DISTANCE_TO_PLAYER);
            Double generatedZ = Mth.nextDouble(randomSource, playerPosition.z - MAX_PLANE_DISTINCE_TO_PLAYER, playerPosition.z + MAX_PLANE_DISTINCE_TO_PLAYER);
            
            blockPos = BlockPos.containing(generatedX , generatedY , generatedZ );
            BlockPos blockBelowPos = BlockPos.containing(generatedX , generatedY-1.0 , generatedZ );
            
            boolean blockPosIsWater = false;
            boolean blockBelowPosIsWater = false;
            
//            if(level.getBlockEntity(blockPos) != null && level.getBlockEntity(blockPos).getBlockState() != null && level.getBlockEntity(blockPos).getBlockState().getBlock() != null) {
//            	blockPosIsWater = Blocks.WATER.equals(level.getBlockEntity(blockPos).getBlockState().getBlock());
//            }
//            
//            if(level.getBlockEntity(blockBelowPos) != null && level.getBlockEntity(blockBelowPos).getBlockState() != null && level.getBlockEntity(blockBelowPos).getBlockState().getBlock() != null) {
//            	blockBelowPosIsWater = Blocks.WATER.equals(level.getBlockEntity(blockBelowPos).getBlockState().getBlock());
//            }
//            
            targetCoordinatesAreInAir = level.isEmptyBlock(blockPos) || blockPosIsWater;
            if(!targetCoordinatesAreInAir) {
            	continue;
            }
            
            targetCoordinateBelowIsGround = !level.isEmptyBlock(blockBelowPos) || blockBelowPosIsWater;
			if(!targetCoordinateBelowIsGround) {
				continue;
			}
            
			targetPosition = blockPos.getCenter();
//            boolean blockBelowIsLava = Blocks.LAVA.equals(world.getBlockEntity(blockBelowPos).getBlockState().getBlock());
//            if(blockBelowIsLava) {
//            	continue;
//            }
            
            
//        	BlockPos blockPosBelow = BlockPos.containing(generatedX , generatedY-1.0 , generatedZ );
//        	BlockState blockBelowState = world.getBlockState(blockPosBelow);
//        	Block blockBelow = blockBelowState.getBlock();
     
            

            
        }while (!targetCoordinatesAreInAir || !targetCoordinateBelowIsGround );
        
        player.teleportTo(targetPosition.x(), targetPosition.y(), targetPosition.z());
        
        long differenceTime = System.currentTimeMillis() - startTime;
        System.out.println("Player: " + player.getName().getString() + " took " + differenceTime + "ms to teleport" );
        
        return blockPos;
        
	}

}
