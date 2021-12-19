package com.mitchej123.hodgepodge.mixins.minecraft;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.core.util.ItemStackMap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;
import java.util.Map;

@Mixin(FurnaceRecipes.class)
public abstract class MixinFurnaceRecipes {
    /*
     * Speed up FurnaceRecipes.getSmeltingResult by:
     *  1) Hijacking the constructor here to recreate the lists with a replacement hash map and an ItemStack hashing strategy
     *  2) No longer looping over every. single. recipe. in the list and using the .get()  
     */
    @Shadow private Map smeltingList;
    @Shadow private Map experienceList;
    @Shadow abstract boolean func_151397_a(ItemStack p_151397_1_, ItemStack p_151397_2_);

    @Redirect(
        at=@At(
            value="INVOKE", 
            target="Lnet/minecraft/item/crafting/FurnaceRecipes;func_151393_a(Lnet/minecraft/block/Block;Lnet/minecraft/item/ItemStack;F)V",
            ordinal = 0
        ), 
        method="Lnet/minecraft/item/crafting/FurnaceRecipes;<init>()V"
    )
    private void doStuff(FurnaceRecipes instance, Block p_151393_1_, ItemStack p_151393_2_, float p_151393_3_) throws NoSuchFieldException, IllegalAccessException {
        Hodgepodge.log.info("Swapping out smeltingList and experienceList in FurnaceRecipes");
        
        // Hack into the first call in the constructor and replace the lists with a new hashmap that has an ItemStackMi hashing strategy
        boolean devEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        try {
            Class<?> clazz = Class.forName("net.minecraft.item.crafting.FurnaceRecipes");
    
            Field smeltingList = clazz.getDeclaredField(devEnv ? "smeltingList" : "field_77604_b");
            smeltingList.setAccessible(true);
            smeltingList.set(instance, new ItemStackMap<ItemStack>());
    
            Field experienceList = clazz.getDeclaredField(devEnv ? "experienceList" : "field_77605_c");
            experienceList.setAccessible(true);
            experienceList.set(instance, new ItemStackMap<Float>());
            
            Hodgepodge.log.info("Successfully swapped the lists in FurnaceRecipes");

        } catch (ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        instance.func_151393_a(p_151393_1_, p_151393_2_, p_151393_3_);
    }
    /**
     * @author mitchej123
     * @reason Significantly Faster
     *  Inspired by later versions of forge
     */
    @SuppressWarnings("unchecked")
    @Overwrite(remap = false)
    public void func_151394_a /* addSmeltingRecipe */ (ItemStack input, ItemStack stack, float experience) {
        if (getSmeltingResult(input) != null) {
            Hodgepodge.log.info("Overwriting smelting recipe for input: {} and output {} with {}", input, getSmeltingResult(input), stack); 
        }
        this.smeltingList.put(input, stack);
        this.experienceList.put(stack, experience);
    }

    /**
     * @author mitchej123
     * @reason Significantly Faster
     */
    @Overwrite
    public ItemStack getSmeltingResult(ItemStack stack) {
        return (ItemStack) this.smeltingList.get(stack);
    }

    /**
     * @author mitchej123
     * @reason Significantly Faster
     */
    @Overwrite(remap = false)
    public float func_151398_b /* getSmeltingExperience */ (ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0f;
        
        float exp = stack.getItem().getSmeltingExperience(stack);
        if (exp == -1) {
            exp = (Float) (this.experienceList.getOrDefault(stack, 0f));
        }
        return exp;
    }

}
