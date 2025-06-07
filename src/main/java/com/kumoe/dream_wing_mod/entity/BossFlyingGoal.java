package com.kumoe.dream_wing_mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BossFlyingGoal extends Goal {
	private final PathfinderMob mob;
	private final RandomSource random = RandomSource.create();
	private Vec3 targetPos;
	private int recalculateTimer = 0;

	private static final double MIN_FLY_HEIGHT = 3.0;
	private static final double MAX_FLY_HEIGHT = 5.0;
	private static final int RECALCULATE_INTERVAL = 40;
	private static final double MOVE_SPEED = 0.15;
	private static final double HOVER_DISTANCE = 3.0; // 距离小于这个就悬停

	public BossFlyingGoal(PathfinderMob mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return true;
	}

	@Override
	public void start() {
		recalculateTimer = 0;
	}

	@Override
	public void tick() {
		if (--recalculateTimer <= 0 || targetPos == null || mob.distanceToSqr(targetPos) < 1.0) {
			recalculateTimer = RECALCULATE_INTERVAL;
			if (mob.getTarget() != null && mob.getTarget().isAlive()) {
				targetPos = getAttackTargetPosition();
			} else {
				targetPos = getIdleFlyPosition();
			}
		}

		Vec3 currentPos = mob.position();
		if (targetPos != null) {
			double distSqr = currentPos.distanceToSqr(targetPos);

			if (mob.getTarget() != null && mob.getTarget().isAlive() && distSqr < HOVER_DISTANCE * HOVER_DISTANCE) {
				// 🚁 悬停模式（不推进，只转身朝目标）
				Vec3 targetVec = mob.getTarget().position().subtract(currentPos);
				faceTowards(targetVec);
				mob.setDeltaMovement(Vec3.ZERO);
			} else {
				// 🛫 正常飞行推进
				Vec3 direction = targetPos.subtract(currentPos).normalize().scale(MOVE_SPEED);
				if (!mob.getDeltaMovement().equals(direction)) {
					mob.setDeltaMovement(direction);
				}
				faceTowards(direction);
			}
		}
	}

	private void faceTowards(Vec3 direction) {
		if (direction.lengthSqr() == 0) return;
		float yRot = (float) (Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) - 90.0F;
		float currentYaw = mob.getYRot();
		float maxTurn = 10f; // 每 tick 最多转 10 度
		float newYaw = Mth.approachDegrees(currentYaw, yRot, maxTurn);
		mob.setYRot(newYaw);
		mob.yBodyRot = newYaw;
		mob.yHeadRot = newYaw;
	}

	private Vec3 getIdleFlyPosition() {
		Vec3 pos = mob.position();
		double angle = random.nextDouble() * 2 * Math.PI;
		double radius = 2.0 + random.nextDouble() * 4.0;
		double dx = Math.cos(angle) * radius;
		double dz = Math.sin(angle) * radius;

		double groundY = getGroundHeight(BlockPos.containing(pos.x + dx, pos.y, pos.z + dz));
		double flyHeight = groundY + MIN_FLY_HEIGHT + random.nextDouble() * (MAX_FLY_HEIGHT - MIN_FLY_HEIGHT);

		return new Vec3(pos.x + dx, flyHeight, pos.z + dz);
	}

	private Vec3 getAttackTargetPosition() {
		Vec3 playerPos = mob.getTarget().position();
		Vec3 lookDir = mob.getTarget().getLookAngle().normalize();
		double forwardDistance = 4.0 + random.nextDouble() * 2.0;
		Vec3 offset = lookDir.scale(forwardDistance);

		double groundY = getGroundHeight(BlockPos.containing(playerPos));
		double flyHeight = groundY + (MIN_FLY_HEIGHT + MAX_FLY_HEIGHT) / 2;

		return playerPos.add(offset).add(0, flyHeight - playerPos.y, 0);
	}

	private double getGroundHeight(BlockPos pos) {
		int y = mob.level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY();
		return y <= mob.level().getMinBuildHeight() ? mob.level().getMinBuildHeight() + 1.0 : y;
	}
}
