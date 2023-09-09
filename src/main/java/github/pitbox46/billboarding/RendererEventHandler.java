package github.pitbox46.billboarding;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RendererEventHandler {
    public static final Map<BlockPos, BlockState> TO_ADD = Collections.synchronizedMap(new HashMap<>());
    public static final Set<BlockPos> TO_REMOVE = Collections.synchronizedSet(new HashSet<>());
    public static final Map<BlockPos, BlockState> BILLBOARDS_TO_RENDER = new ConcurrentHashMap<>();

    public static void renderBillboards(Minecraft mc, Camera cam, PoseStack poseStack, Frustum frustum) {
        ClientLevel level = mc.level;
        MultiBufferSource.BufferSource multibuffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        double viewDistance = cam.getEntity().isInWater() ? 2 * 16 : 4 * 16;

        for (Map.Entry<BlockPos, BlockState> entry : BILLBOARDS_TO_RENDER.entrySet()) {
            BlockPos pos = entry.getKey();

            if(Math.pow(viewDistance, 2) < pos.distSqr(cam.getBlockPosition()) || !frustum.isVisible(new AABB(pos)))
                continue;

            BlockState state = entry.getValue();
            Vec3 vec3 = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5f, 0, 0.5f).subtract(cam.getPosition());
            if (level.getBlockState(pos).getBlock() instanceof FlowerPotBlock) {
                vec3 = vec3.add(0, 0.250f, 0);
            } else if (state.getBlock() instanceof WallTorchBlock || state.getBlock() instanceof RedstoneWallTorchBlock) {
                Vec3i vec3i = state.getValue(WallTorchBlock.FACING).getOpposite().getNormal();
                Vec3 adjusterTorch = new Vec3(vec3i.getX(), vec3i.getY(), vec3i.getZ()).multiply(0.3f, 1, 0.3f).add(0, 0.2f, 0);
                vec3 = vec3.add(adjusterTorch);
            } else {
                vec3 = vec3.add(state.getOffset(level, pos));
            }
            //Render Billboarding
            TextureAtlasSprite textureatlassprite = mc.getBlockRenderer().getBlockModelShaper().getTexture(state, level, pos);
            poseStack.pushPose();
            poseStack.translate(vec3.x, vec3.y, vec3.z);
            float f = 1F;
            poseStack.scale(f, f, f);
            float f1 = 0.5F;



            //Rotation
//            double dist = vec3.length();
//                double degUp = -Math.asin(vec3.y / dist) * 180 / Math.PI;
//                poseStack.mulPose(Vector3f.XP.rotationDegrees((float) degUp));
//            double degRight = Math.atan2(vec3.x, vec3.z) * 180 / Math.PI;
//            poseStack.mulPose(Vector3f.YP.rotationDegrees((float) degRight));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-cam.getYRot()));
            VertexConsumer vertexconsumer = multibuffersource.getBuffer(Sheets.cutoutBlockSheet());

            float f6 = textureatlassprite.getU0();
            float f7 = textureatlassprite.getV0();
            float f8 = textureatlassprite.getU1();
            float f9 = textureatlassprite.getV1();

            int light = LightTexture.pack(
                    (int) Math.min(level.getBrightness(LightLayer.BLOCK, pos), 15),
                    Math.min(level.getBrightness(LightLayer.SKY, pos), 15));

            int color = mc.getBlockColors().getColor(state, level, pos, 0);
            int r = 255, g = 255, b = 255;
            if (color != -1) {
                r = Math.min((color >> 16 & 255), 255);
                g = Math.min((color >> 8 & 255), 255);
                b = Math.min((color & 255), 255);
            }


            if (state.getBlock() instanceof AmethystClusterBlock && state.getValue(AmethystClusterBlock.FACING) == Direction.DOWN) {
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                poseStack.translate(0, -1F, 0);
            }

            PoseStack.Pose pose = poseStack.last();
            fireVertex(pose, vertexconsumer, f1, 0.0F, 0.0F, f8, f9, r, g, b, light);
            fireVertex(pose, vertexconsumer, -f1, 0.0F, 0.0F, f6, f9, r, g, b, light);
            fireVertex(pose, vertexconsumer, -f1, 1F, 0.0F, f6, f7, r, g, b, light);
            fireVertex(pose, vertexconsumer, f1, 1F, 0.0F, f8, f7, r, g, b, light);

            poseStack.popPose();
        }
    }

    private static void fireVertex(PoseStack.Pose p_114415_, VertexConsumer p_114416_, float p_114417_, float p_114418_, float p_114419_, float p_114420_, float p_114421_, int r, int g, int b, int light) {
        p_114416_
                .vertex(p_114415_.pose(), p_114417_, p_114418_, p_114419_)
                .color(r, g, b, 255)
                .uv(p_114420_, p_114421_)
                .overlayCoords(0, 10)
                .uv2(light)
                .normal(p_114415_.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            if(Minecraft.getInstance().level == null) {
                BILLBOARDS_TO_RENDER.clear();
                TO_REMOVE.clear();
                TO_ADD.clear();
                return;
            }

            TO_ADD.forEach(BILLBOARDS_TO_RENDER::put);
            TO_REMOVE.forEach(pos -> {
                if (BILLBOARDS_TO_RENDER.containsKey(pos) && BILLBOARDS_TO_RENDER.get(pos).getBlock() != Minecraft.getInstance().level.getBlockState(pos).getBlock()) {
                    BILLBOARDS_TO_RENDER.remove(pos);
                }
            });
            TO_REMOVE.clear();
            TO_ADD.clear();
        }
    }

    @SubscribeEvent
    public static void onChangeWorld(WorldEvent.Unload event) {
        if(event.getWorld().isClientSide()) {
            TO_REMOVE.addAll(BILLBOARDS_TO_RENDER.keySet());
        }
    }
}
