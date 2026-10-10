package cn.elytra.gtnh.cutcorners.mixins.late.thaumcraft;

import cn.elytra.gtnh.cutcorners.CutCorners;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.tiles.TileNode;

@Mixin(value = TileNode.class, remap = false)
public abstract class TileNodeMixin {

    @Shadow
    int regeneration;

    @Unique
    private boolean gtnhcc$recalculated;

    @Inject(method = "handleRecharge", at = @At("HEAD"))
    private void gtnhcc$captureRecalculation(CallbackInfoReturnable<Boolean> cir) {
        this.gtnhcc$recalculated = this.regeneration < 0;
    }

    @Inject(method = "handleRecharge", at = @At("RETURN"))
    private void gtnhcc$scaleIntervalOnce(CallbackInfoReturnable<Boolean> cir) {
        if (!this.gtnhcc$recalculated) {
            return;
        }

        // A Fading node has an interval of 0 and must never recharge. Scaling it would be clamped back up to
        // the 1 tick lower bound by the strategy, silently turning it into a node that recharges every tick.
        if (this.regeneration <= 0) {
            return;
        }

        this.regeneration = CutCorners.getStrategy().getThaumcraftNodeRegenerationTime(this.regeneration);
    }
}
