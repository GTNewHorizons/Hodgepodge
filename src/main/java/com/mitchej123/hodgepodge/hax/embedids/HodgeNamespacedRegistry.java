package com.mitchej123.hodgepodge.hax.embedids;

import com.mitchej123.hodgepodge.mixins.interfaces.HasID;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import org.jetbrains.annotations.Nullable;

/// Subclass of [FMLControlledNamespacedRegistry] that uses a direct embedded map. This means much faster reads, at the
/// cost of being singleton - only one instance may exist at any given time!
public class HodgeNamespacedRegistry<T extends HasID> extends FMLControlledNamespacedRegistry<T> {
    private final Class<T> type;

    protected HodgeNamespacedRegistry(@Nullable String defaultName, int maxIdValue, int minIdValue, Class<T> type, char discriminator) {
        super(defaultName, maxIdValue, minIdValue, type, discriminator);

        this.type = type;
        setUnderlyingMap();
    }

    private void setUnderlyingMap() {
        var map = new EmbeddedReference2IntMap();
        map.setType(type);
        underlyingIntegerMap = map;
    }

    /// Oddly enough, a Mixin would be *cleaner* here.
    @Override
    protected void set(FMLControlledNamespacedRegistry<T> registry) {
        if (superType != registry.superType) throw new IllegalArgumentException("incompatible registry");

        this.discriminator = registry.discriminator;
        this.optionalDefaultName = registry.optionalDefaultName;
        this.maxId = registry.maxId;
        this.minId = registry.minId;
        this.aliases.clear();
        this.aliases.putAll(registry.aliases);
        this.activeSubstitutions.clear();

        // This is the altered bit!
        setUnderlyingMap();
        registryObjects.clear();

        for (T thing : registry.typeSafeIterable())
        {
            int id = registry.getId(thing);
            addObjectRaw(id, registry.getNameForObject(thing), thing);
        }
        this.activeSubstitutions.putAll(registry.activeSubstitutions);
    }

}
