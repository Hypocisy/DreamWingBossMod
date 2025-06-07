package com.kumoe.dream_wing_mod.entity;

import com.kumoe.dream_wing_mod.Config;
import com.kumoe.dream_wing_mod.DreamWingBossMod;
import com.kumoe.dream_wing_mod.projectiles.DreamWingFireballEntity;
import com.kumoe.dream_wing_mod.register.ModMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class DreamWingBossEntity extends Monster implements GeoAnimatable {
	private static final AttributeModifier max_health = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DreamWingBossMod.MODID, "phase_2_max_health"), 300d, AttributeModifier.Operation.ADD_VALUE);
	private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR;

	static {
		LIVING_ENTITY_SELECTOR = (livingEntity) -> !livingEntity.getType().is(EntityTypeTags.WITHER_FRIENDS) && livingEntity.attackable();
	}

	private final Set<UUID> summonedMobs = new HashSet<>();
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private int phase = 1;
	private long lastSummonTime = 0;
	private long lastSlashTime = 0;
	private long lastProjectileTime = 0;

	public DreamWingBossEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.moveControl = new FlyingMoveControl(this, 2, false);
		this.setNoGravity(true); // 浮空
	}

	public static AttributeSupplier createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.8)
				.add(Attributes.MAX_HEALTH, 300)
				.add(Attributes.ATTACK_DAMAGE, 3.0f)
				.add(Attributes.ATTACK_SPEED, 1.0f)
				.add(Attributes.FLYING_SPEED, 1.5)
				.add(Attributes.FOLLOW_RANGE, 32.0).build();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new DoNothingGoal());
		this.goalSelector.addGoal(5, new BossFlyingGoal(this));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
		nav.setCanOpenDoors(false);
		nav.setCanFloat(true);
		nav.setCanPassDoors(true);
		return nav;
	}

	@Override
	public void tick() {
		super.tick();

		LivingEntity target = this.getTarget();
		long now = this.level().getGameTime();

		// Boss切换阶段
		if (this.getHealth() <= 300 && phase == 1) {
			phase = 2;
			this.getAttribute(Attributes.MAX_HEALTH)
					.addOrReplacePermanentModifier(max_health);
			this.heal(getMaxHealth());
		}

		// 技能释放逻辑 - 只处理技能，飞行由AI Goal处理
		if (target != null) {
			if (now - lastSummonTime >= Config.summon_frequency && countLivingSummons() < Config.max_summon_limit) {
				performSummon();
				lastSummonTime = now;
			}

			if (phase == 2 && now - lastProjectileTime >= 80) {
				performProjectile();
				lastProjectileTime = now;
			}
		}

		// 清理死亡召唤物
		summonedMobs.removeIf(uuid -> {
			Entity e = ((ServerLevelAccessor) level()).getLevel().getEntity(uuid);
			return !(e instanceof LivingEntity) || !e.isAlive();
		});
	}

	private void performSummon() {
		if (!level().isClientSide) {
			var rand = level().random;
			int count = Config.basicSummonCount + rand.nextInt(Config.randomSummonCount); // 3~5
			for (int i = 0; i < count; i++) {
				LivingEntity summon = (LivingEntity) Config.ENTITYS.get(rand.nextInt(Config.ENTITYS.size())).create(level()); // TODO: 用 configurable 类型
				if (summon != null) {
					summon.moveTo(this.getTarget().getX() + rand.nextDouble() * 4 - 2,
							this.getTarget().getY(),
							this.getTarget().getZ() + rand.nextDouble() * 4 - 2);
					level().addFreshEntity(summon);
					summonedMobs.add(summon.getUUID()); // 记录召唤物
				}
			}
		}
	}

	private void performSlash() {
		LivingEntity target = this.getTarget();
		if (target == null) return;
		for (int i = 0; i < 3 + level().random.nextInt(2); i++) {
			this.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
			this.doHurtTarget(target);
		}
	}

	private void performProjectile() {
		if (!level().isClientSide) {
			this.addEffect(new MobEffectInstance(ModMobEffects.DUMMY_EFFECT.getDelegate(), 100));
			// 创建射弹实体，设置高速度
			DreamWingFireballEntity proj = new DreamWingFireballEntity(level());
			LivingEntity target = this.getTarget();
			if (target != null) {
				Vec3 start = this.position().add(0, this.getEyeHeight(), 0);
				Vec3 targetPos = target.position().add(0, target.getEyeHeight() / 2.0, 0);
				Vec3 direction = targetPos.subtract(start).normalize().scale(1.5);
				proj.setDeltaMovement(direction);
				proj.moveTo(start.x, start.y, start.z);
			}
			level().addFreshEntity(proj);
		}
	}

	private int countLivingSummons() {
		return (int) summonedMobs.stream()
				.map(uuid -> ((ServerLevelAccessor) level()).getLevel().getEntity(uuid))
				.filter(entity -> entity instanceof LivingEntity && entity.isAlive())
				.count();
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		// 如果召唤生物未被清除，则无敌
		return countLivingSummons() > 0 || super.isInvulnerableTo(source);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//		controllers.add(new AnimationController<>(this, "idleController", 0, this::idlePredicate));
		controllers.add(new AnimationController<>(this, "flyController", 0, this::flyPredicate));
		controllers.add(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
		controllers.add(new AnimationController<>(this, "hurtController", 0, this::hurtPredicate));
	}

	@Override
	public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
		return false;
	}

	private <E extends GeoAnimatable> PlayState idlePredicate(AnimationState<E> event) {
		if (this.getHealth() <= 0.0F) return PlayState.STOP;

		if (!event.isMoving() && event.getController().hasAnimationFinished()) {
			event.getController().setAnimation(RawAnimation.begin().then("fly_idle", Animation.LoopType.LOOP));
		}
		return PlayState.CONTINUE;
	}

	private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> event) {
		if (this.swinging) {
			event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	private <E extends GeoAnimatable> PlayState flyPredicate(AnimationState<E> event) {
		if (this.getHealth() <= 0.0F) return PlayState.STOP;

		// 保证动画始终播放，如果动画为空或播放完才设置
		if (event.isMoving() && event.getController().hasAnimationFinished()) {
			event.getController().setAnimation(
					RawAnimation.begin().then("fly", Animation.LoopType.PLAY_ONCE)
			);
		}

		return PlayState.CONTINUE;
	}

	private <E extends GeoAnimatable> PlayState hurtPredicate(AnimationState<E> event) {
		if (event.isMoving()) return PlayState.STOP;

		if (this.hurtTime + 5 >= this.tickCount) { // 避免重复触发
			event.getController().setAnimation(RawAnimation.begin().then("hurt", Animation.LoopType.PLAY_ONCE));
			return PlayState.CONTINUE;
		}

		return PlayState.STOP;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public double getTick(Object object) {
		return this.tickCount;
	}

	class DoNothingGoal extends Goal {
		public DoNothingGoal() {
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
		}

		public boolean canUse() {
			return DreamWingBossEntity.this.invulnerableTime > 0;
		}
	}
}