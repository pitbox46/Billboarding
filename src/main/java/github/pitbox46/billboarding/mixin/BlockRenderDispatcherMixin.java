package github.pitbox46.billboarding.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.pitbox46.billboarding.Billboarding;
import github.pitbox46.billboarding.RenderSingleBlockEvent;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherMixin {
    @Inject(at = @At(value = "HEAD"),
            method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Z",
            cancellable = true,
            remap = false)
    private void onRenderSingleBlock(BlockState state, BlockPos pos, BlockAndTintGetter blockAndTintGetter, PoseStack poseStack, VertexConsumer vertexConsumer, boolean b, Random random, IModelData modelData, CallbackInfoReturnable<Boolean> cir) {
        for (Pair<Predicate<BlockState>, Consumer<RenderSingleBlockEvent>> pair : Billboarding.BILLBOARDS_TO_RENDER) {
            if (pair.getLeft().test(state)) {
                pair.getRight().accept(new RenderSingleBlockEvent((BlockRenderDispatcher) (Object) this, state, pos, blockAndTintGetter, poseStack, vertexConsumer, b, random, modelData, cir));
                cir.setReturnValue(true);
                break;
            }
        }
    }
}
