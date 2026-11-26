package hodgepodge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.stream.Stream;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Bootstrap;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.MockedStatic;

import com.mitchej123.hodgepodge.mixins.interfaces.PotionEffectHiddenExt;

import cpw.mods.fml.common.Loader;

class HiddenPotionDurationTest {

    @TestFactory
    Stream<DynamicTest> hiddenEffects() throws Exception {
        return MixinScenarios.run(getClass());
    }

    public static class Scenarios {

        private EntityLivingBase entity;

        public void setUp() {
            try (MockedStatic<Loader> loader = mockStatic(Loader.class)) {
                loader.when(Loader::instance).thenReturn(mock(Loader.class));
                Bootstrap.func_151354_b();
            }
            entity = mock(EntityLivingBase.class);
            entity.worldObj = mock(World.class);
        }

        public void tearDown() {}

        public void testHiddenEffectReturnsWithRemainingDuration() {
            PotionEffect active = effect(2, 1);
            hidden(active).hodgepodge$setHidden(effect(5, 0));

            assertTrue(active.onUpdate(entity));
            assertTrue(active.onUpdate(entity));
            assertEquals(0, active.getAmplifier());
            assertEquals(3, active.getDuration());
        }

        public void testExpiredHiddenEffectDoesNotReturn() {
            PotionEffect active = effect(2, 1);
            hidden(active).hodgepodge$setHidden(effect(2, 0));

            assertTrue(active.onUpdate(entity));
            assertFalse(active.onUpdate(entity));
        }

        public void testExpiredEffectDoesNotBlockInfiniteEffect() {
            PotionEffect active = effect(2, 2);
            PotionEffect finite = effect(1, 1);
            hidden(active).hodgepodge$setHidden(finite);
            hidden(finite).hodgepodge$setHidden(effect(-1, 0));

            assertTrue(active.onUpdate(entity));
            assertTrue(active.onUpdate(entity));
            assertEquals(0, active.getAmplifier());
            assertEquals(-1, active.getDuration());
        }

        private static PotionEffect effect(int duration, int amplifier) {
            return new PotionEffect(Potion.moveSpeed.id, duration, amplifier);
        }

        private static PotionEffectHiddenExt hidden(PotionEffect effect) {
            return (PotionEffectHiddenExt) effect;
        }
    }
}
