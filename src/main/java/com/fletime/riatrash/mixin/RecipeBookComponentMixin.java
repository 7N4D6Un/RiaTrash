package com.fletime.riatrash.mixin;

import com.fletime.riatrash.RiaTrash;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
	@Inject(method = "updateScreenPosition", at = @At("RETURN"))
	private void riatrash$repositionTrashButton(int width, int imageWidth, CallbackInfoReturnable<Integer> cir) {
		RiaTrash.repositionTrashButton(cir.getReturnValue());
	}
}
