package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Entity.class)
public abstract class MixinEntity_TranslateNameComponent {

    /**
     * New implementation based on {@link Entity#getCommandSenderName()}. This may cause issues with mods that override
     * {@link Entity#getCommandSenderName()} but not {@link Entity#func_145748_c_()}.
     *
     * @author nicksitnikov
     * @reason This method initially returned a component that was translated on the server. Replacing it with a
     *         {@link ChatComponentTranslation} allows the name of the entity to be correctly translated to the client's
     *         language.
     */
    @Overwrite
    public IChatComponent func_145748_c_() {
        String s = EntityList.getEntityString((Entity) (Object) this);

        if (s == null) {
            s = "generic";
        }

        return new ChatComponentTranslation("entity." + s + ".name");
    }
}
