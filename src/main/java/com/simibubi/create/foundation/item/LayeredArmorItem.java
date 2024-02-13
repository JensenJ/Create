package com.simibubi.create.foundation.item;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.mixin.accessor.HumanoidArmorLayerAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public interface LayeredArmorItem extends CustomRenderedArmorItem {
	@Environment(EnvType.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	default void renderArmorPiece(HumanoidArmorLayer<?, ?, ?> layer, PoseStack poseStack,
			MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light,
			HumanoidModel<?> originalModel, ItemStack stack) {
		if (!(stack.getItem() instanceof ArmorItem item)) {
			return;
		}
		if (LivingEntity.getEquipmentSlotForItem(stack) != slot) {
			return;
		}

		HumanoidArmorLayerAccessor accessor = (HumanoidArmorLayerAccessor) layer;
		Map<String, ResourceLocation> locationCache = HumanoidArmorLayerAccessor.create$getArmorLocationCache();
		boolean glint = stack.hasFoil();

		HumanoidModel<?> innerModel = accessor.create$getInnerModel();
		layer.getParentModel().copyPropertiesTo((HumanoidModel) innerModel);
		accessor.create$callSetPartVisibility(innerModel, slot);
		String locationStr2 = getArmorTextureLocation(entity, slot, stack, 2);
		ResourceLocation location2 = locationCache.computeIfAbsent(locationStr2, ResourceLocation::new);
		renderModel(poseStack, bufferSource, light, item, innerModel, glint, 1.0F, 1.0F, 1.0F, location2);

		HumanoidModel<?> outerModel = accessor.create$getOuterModel();
		layer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
		accessor.create$callSetPartVisibility(outerModel, slot);
		String locationStr1 = getArmorTextureLocation(entity, slot, stack, 1);
		ResourceLocation location1 = locationCache.computeIfAbsent(locationStr1, ResourceLocation::new);
		renderModel(poseStack, bufferSource, light, item, outerModel, glint, 1.0F, 1.0F, 1.0F, location1);
	}

	// fabric: forge patches! yay!
	@Environment(EnvType.CLIENT)
	private static void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorItem pArmorItem,
									Model pModel, boolean pWithGlint, float pRed, float pGreen, float pBlue, ResourceLocation armorResource) {
		VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.armorCutoutNoCull(armorResource));
		pModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, pRed, pGreen, pBlue, 1.0F);
	}

	String getArmorTextureLocation(LivingEntity entity, EquipmentSlot slot, ItemStack stack, int layer);
}
