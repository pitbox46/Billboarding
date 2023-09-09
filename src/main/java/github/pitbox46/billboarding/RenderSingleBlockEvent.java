package github.pitbox46.billboarding;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

public record RenderSingleBlockEvent(BlockRenderDispatcher dispatcher,
                                     BlockState state,
                                     BlockPos pos,
                                     BlockAndTintGetter blockAndTintGetter,
                                     PoseStack poseStack,
                                     VertexConsumer vertexConsumer, boolean flag,
                                     Random rand, IModelData modelData,
                                     CallbackInfoReturnable<Boolean> cir) {
}
