package org.ivangeevo.inthegloom.component;


import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.inthegloom.InTheGloomMod;

public class ModComponents {
    public static final ComponentType<GloomComponent> GLOOM_COMPONENT = Registry.register(
        Registries.DATA_COMPONENT_TYPE, Identifier.of(InTheGloomMod.MOD_ID, "gloom"),
        ComponentType.<GloomComponent>builder().codec(GloomComponent.CODEC).build()
    );

    protected static void initialize() {
        // Any additional initialization if needed
    }
}
