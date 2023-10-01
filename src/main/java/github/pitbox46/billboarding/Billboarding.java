package github.pitbox46.billboarding;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Blocks.*;

@Mod("billboarding")
public class Billboarding {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Set<Pair<Predicate<BlockState>, Consumer<RenderSingleBlockEvent>>> BILLBOARDS_TO_RENDER = new HashSet<>();
    public static final Consumer<RenderSingleBlockEvent> BASIC_CONSUMER = e -> {
        RendererEventHandler.TO_ADD.put(e.pos().immutable(), e.state());
    };

    public static void addBillboardingToBlock(Block... blocks) {
        for(Block block : blocks) {
            BILLBOARDS_TO_RENDER.add(Pair.of(
                    state -> state.getBlock().getRegistryName() != null && state.getBlock().getRegistryName().equals(block.getRegistryName()),
                    BASIC_CONSUMER
            ));
        }
    }

    public static void addBillboardingToBlock(String... strings) {
        for(String string : strings) {
            BILLBOARDS_TO_RENDER.add(Pair.of(
                    state -> state.getBlock().getRegistryName() != null && state.getBlock().getRegistryName().equals(new ResourceLocation(string)),
                    BASIC_CONSUMER
            ));
        }
    }

    public Billboarding() {
        addBillboardingToBlock(CAVE_VINES, CAVE_VINES_PLANT, WEEPING_VINES, WEEPING_VINES_PLANT, TWISTING_VINES, TWISTING_VINES_PLANT, NETHER_SPROUTS, KELP, ATTACHED_MELON_STEM, ATTACHED_PUMPKIN_STEM, HANGING_ROOTS, CRIMSON_ROOTS, WARPED_ROOTS, POINTED_DRIPSTONE, COBWEB, SWEET_BERRY_BUSH, CRIMSON_FUNGUS, WARPED_FUNGUS, KELP_PLANT, BAMBOO_SAPLING, OAK_SAPLING, SPRUCE_SAPLING, BIRCH_SAPLING, JUNGLE_SAPLING, ACACIA_SAPLING, DARK_OAK_SAPLING, GRASS, FERN, DEAD_BUSH, SEAGRASS, TALL_SEAGRASS, DANDELION, POPPY, BLUE_ORCHID, ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY, CORNFLOWER, WITHER_ROSE, LILY_OF_THE_VALLEY, BROWN_MUSHROOM, RED_MUSHROOM, MELON_STEM, PUMPKIN_STEM, WHEAT, NETHER_WART, CARROTS, POTATOES, BEETROOTS, SUGAR_CANE, TORCH, WALL_TORCH, SOUL_TORCH, SOUL_WALL_TORCH, REDSTONE_TORCH, REDSTONE_WALL_TORCH);
        BILLBOARDS_TO_RENDER.add(Pair.of(state -> state.getBlock() instanceof CoralPlantBlock, BASIC_CONSUMER));
        BILLBOARDS_TO_RENDER.add(Pair.of(state -> state.getBlock() instanceof BaseCoralPlantBlock, BASIC_CONSUMER));
        BILLBOARDS_TO_RENDER.add(Pair.of(state -> state.getBlock() instanceof FlowerBlock, BASIC_CONSUMER));
        BILLBOARDS_TO_RENDER.add(Pair.of(state -> state.getBlock() instanceof TallGrassBlock, BASIC_CONSUMER));
        BILLBOARDS_TO_RENDER.add(Pair.of(state -> state.getBlock() instanceof DoublePlantBlock && state.getBlock() != SMALL_DRIPLEAF, BASIC_CONSUMER));
        BILLBOARDS_TO_RENDER.add(Pair.of(state -> state.getBlock() instanceof TallFlowerBlock, BASIC_CONSUMER));
        BILLBOARDS_TO_RENDER.add(Pair.of(state -> state.getBlock() instanceof AmethystClusterBlock && (state.getValue(AmethystClusterBlock.FACING) == Direction.UP || state.getValue(AmethystClusterBlock.FACING) == Direction.DOWN), BASIC_CONSUMER));
//        BILLBOARDS_TO_RENDER.add(Pair.of(state -> state.getBlock() instanceof FlowerPotBlock && state.getBlock() != FLOWER_POT, a -> {
//            a.dispatcher().renderBatched(Blocks.FLOWER_POT.defaultBlockState(), a.pos(), a.blockAndTintGetter(), a.poseStack(), a.vertexConsumer(), a.flag(), a.rand(), a.modelData());
//            RendererEventHandler.TO_ADD.put(a.pos().immutable(), ((FlowerPotBlock) a.state().getBlock()).getContent().defaultBlockState());
//        }));
        addBillboardingToBlock("wildbackport:mangrove_propagule");
        addBillboardingToBlock("phantasm:oblivine", "phantasm:hanging_pream_berry");
        addBillboardingToBlock("minecraft:big_dripleaf_stem");
    }
}
