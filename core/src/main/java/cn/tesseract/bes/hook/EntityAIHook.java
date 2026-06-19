package cn.tesseract.bes.hook;

import cn.tesseract.asm.Hook;
import cn.tesseract.asm.ReturnCondition;
import net.minecraft.bes.EntityAIArrowAttack;
import net.minecraft.bes.EntityLiving;
import net.minecraft.bes.EntityLivingBase;
import net.minecraft.bes.EntitySkeleton;
import net.minecraft.bes.IRangedAttackMob;
import net.minecraft.bes.ItemBow;
import net.minecraft.bes.ItemStack;
import net.minecraft.bes.MathHelper;
import net.minecraft.bes.vg;

public class EntityAIHook {
    private static final double SKELETON_FALLBACK_RANGE_SQ = 225.0D;
    private static final int BOW_PATH_RECALC_INTERVAL = 10;
    private static final float DEFAULT_STEP_HEIGHT = 0.5F;
    private static final float MELEE_STEP_HEIGHT = 1.0F;

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void setCombatTask(EntitySkeleton skeleton) {
        skeleton.tasks.obf1_a(skeleton.aiAttackOnCollide);
        skeleton.tasks.obf1_a(skeleton.obf1_a);
        skeleton.tasks.obf1_b(vg.class);

        ItemStack held = skeleton.obf1_q_();
        if (held != null && held.getItem() instanceof ItemBow) {
            skeleton.stepHeight = DEFAULT_STEP_HEIGHT;
            skeleton.tasks.obf1_a(4, skeleton.obf1_a);
        } else {
            skeleton.stepHeight = MELEE_STEP_HEIGHT;
            skeleton.tasks.obf1_a(4, skeleton.aiAttackOnCollide);
        }
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void updateTask(EntityAIArrowAttack ai) {
        EntityLiving attacker = ai.obf1_a;
        EntityLivingBase target = ai.attackTarget;
        if (attacker == null || target == null || target.isDead || target.obf1_dp() <= 0.0F) {
            return;
        }

        double distanceSq = attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
        if (distanceSq <= (double) ai.field_82642_h) {
            ++ai.field_75318_f;
            attacker.obf1_z.obf1_b = null;
        } else if ((attacker.ticksExisted + attacker.entityId) % BOW_PATH_RECALC_INTERVAL == 0) {
            ai.field_75318_f = 0;
            attacker.obf1_z.tryMoveToEntityLiving(target, ai.entityMoveSpeed);
        }

        attacker.obf1_x.setLookPositionWithEntity(target, 30.0F, 30.0F);

        if (--ai.rangedAttackTime == 0) {
            if (distanceSq > (double) ai.field_82642_h) {
                return;
            }

            float distanceRatio = MathHelper.sqrt_double(distanceSq) / ai.field_96562_i;
            if (distanceRatio < 0.1F) {
                distanceRatio = 0.1F;
            }
            if (distanceRatio > 1.0F) {
                distanceRatio = 1.0F;
            }

            ((IRangedAttackMob) attacker).attackEntityWithRangedAttack(target, distanceRatio);
            ai.rangedAttackTime = ai.maxRangedAttackTime;
            if (attacker.obf1_O_()) {
                ai.rangedAttackTime = (int) ((float) ai.rangedAttackTime * 0.67F);
            }
        } else if (ai.rangedAttackTime < 0) {
            ai.rangedAttackTime = ai.maxRangedAttackTime;
            if (attacker.obf1_O_()) {
                ai.rangedAttackTime = (int) ((float) ai.rangedAttackTime * 0.67F);
            }
        }
    }

    @Hook(injector = "exit")
    public static void onLivingUpdate(EntitySkeleton skeleton) {
        if (skeleton.worldObj == null || skeleton.worldObj.isRemote || skeleton.isDead) {
            return;
        }
        ItemStack held = skeleton.obf1_q_();
        skeleton.stepHeight = held != null && held.getItem() instanceof ItemBow ? DEFAULT_STEP_HEIGHT : MELEE_STEP_HEIGHT;
        if (held == null || !(held.getItem() instanceof ItemBow)) {
            return;
        }
        EntityLivingBase target = skeleton.getAttackTarget();
        if (target == null || target.isDead || target.obf1_dp() <= 0.0F) {
            return;
        }
        double distanceSq = skeleton.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
        if (distanceSq > SKELETON_FALLBACK_RANGE_SQ) {
            return;
        }
        if (skeleton.obf1_a.rangedAttackTime >= 0 || (skeleton.ticksExisted + skeleton.entityId) % 80 != 0) {
            return;
        }

        float distanceRatio = MathHelper.sqrt_double(distanceSq) / 15.0F;
        if (distanceRatio < 0.1F) {
            distanceRatio = 0.1F;
        }
        if (distanceRatio > 1.0F) {
            distanceRatio = 1.0F;
        }

        skeleton.obf1_x.setLookPositionWithEntity(target, 30.0F, 30.0F);
        skeleton.attackEntityWithRangedAttack(target, distanceRatio);
    }
}
