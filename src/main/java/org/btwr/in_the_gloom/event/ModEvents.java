package org.btwr.in_the_gloom.event;

import net.minecraft.entity.LivingEntity;
import org.btwr.shared_library.event.SimpleEvent;

import java.util.function.Consumer;

public class ModEvents {

    public static final SimpleEvent<Consumer<LivingEntity>> LIVING_TICK = new SimpleEvent<>(
            handlers -> living -> handlers.forEach(c -> c.accept(living))
    );

    public static void register() {

    }

}
