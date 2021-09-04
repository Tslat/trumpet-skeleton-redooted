package net.tslat.trumpetskeleton.client;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.tslat.trumpetskeleton.register.Entities;

public class ClientOps {
	@SubscribeEvent
	public static void setupClient(final FMLClientSetupEvent event) {
		EntityRenderers.register(Entities.TRUMPET_SKELETON.get(), SkeletonRenderer::new);
	}
}
