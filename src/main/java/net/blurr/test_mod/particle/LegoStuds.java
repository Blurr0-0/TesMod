package net.blurr.test_mod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;public class LegoStuds extends TextureSheetParticle {

    private final SpriteSet sprites;
    protected LegoStuds(ClientLevel pLevel, double pX, double pY, double pZ, double dX, double dY, double dZ, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, dX, dY, dZ);

        this.friction = 0.8F;
        double groundOffset = 4;
        this.xo = this.x = pX;
        this.yo = this.y = pY + groundOffset;
        this.zo = this.z = pZ;
        this.xd = dX; //+ (Math.random() * 20.0D - 1.0D) * (double)0.05F;
        this.yd = dY;// + (Math.random() * 20.0D - 1.0D) * (double)0.05F;
        this.zd = dZ; //+ (Math.random() * 20.0D - 1.0D) * (double)0.05F;
        this.quadSize *= 0.85F;
        this.lifetime = 200;
        this.gravity = 0.1F;
        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
        //this.setBoundingBox(new AABB(pX - 0.5D, pY - 0.5D, pZ - 0.5D, pX + 0.5D, pY + 0.5D, pZ + 0.5D));
        this.setSize(3F, 3F);
        this.sprites = pSprites;
        this.setSpriteFromAge(pSprites);
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
        this.setSpriteFromAge(this.sprites);
        this.move(this.xd, this.yd, this.zd);
    }

    private void fadeOut() {
        this.alpha = (-(1/(float)lifetime) * age + 1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel pLevel, double pX, double pY, double pZ, double dx, double dy, double dz) {
            return new LegoStuds(pLevel, pX, pY, pZ, dx, dy, dz, this.sprites);
        }
    }
}


