package hodgepodge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
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
import com.mitchej123.hodgepodge.util.InfinitePotionDuration;

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

        public void testRemoveHiddenEffectsPreservesActiveAndUnmatchedEffects() {
            PotionEffect active = effect(2, 4);
            PotionEffect first = new PotionEffect(Potion.moveSpeed.id, -1, 3, true);
            PotionEffect retained = effect(-1, 2);
            PotionEffect last = new PotionEffect(Potion.moveSpeed.id, -1, 1, true);
            hidden(active).hodgepodge$setHidden(first);
            hidden(first).hodgepodge$setHidden(retained);
            hidden(retained).hodgepodge$setHidden(last);

            assertTrue(InfinitePotionDuration.removeHiddenEffects(active, PotionEffect::getIsAmbient));
            assertEquals(4, active.getAmplifier());
            assertEquals(2, active.getDuration());
            assertSame(retained, hidden(active).hodgepodge$getHidden());
            assertFalse(InfinitePotionDuration.removeHiddenEffects(active, PotionEffect::getIsAmbient));
            assertTrue(active.onUpdate(entity));
            assertTrue(active.onUpdate(entity));
            assertEquals(2, active.getAmplifier());
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
