package github.pitbox46.billboarding.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import github.pitbox46.billboarding.Billboarding;
import github.pitbox46.billboarding.RenderSingleBlockEvent;
import github.pitbox46.billboarding.RendererEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    private static final int RENDER_LEVEL_ORDINAL = 12;

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Nullable private ClientLevel level;

    @Inject(at = @At(value = "HEAD"), method = "blockChanged")
    private void onBlockChanged(BlockGetter p_109545_, BlockPos pos, BlockState state1, BlockState state2, int p_109549_, CallbackInfo ci) {
        for (Pair<Predicate<BlockState>, Consumer<RenderSingleBlockEvent>> pair : Billboarding.BILLBOARDS_TO_RENDER) {
            if(pair.getLeft().test(state2)) {
                RendererEventHandler.TO_ADD.put(pos, state2);
                return;
            }
        }

        RendererEventHandler.TO_REMOVE.add(pos);
    }

    //Occurs before `profilerfiller.popPush("entities");`
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = RENDER_LEVEL_ORDINAL), method = "renderLevel", locals = LocalCapture.CAPTURE_FAILHARD)
    private void onRenderLevel(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_109607_, CallbackInfo ci, ProfilerFiller profilerfiller, boolean flag, Vec3 vec3, double d0, double d1, double d2, Matrix4f matrix4f, boolean flag1, Frustum frustum) {
        RendererEventHandler.renderBillboards(this.minecraft, p_109604_, p_109600_, frustum);
    }
}
