package org.ivangeevo.inthegloom.component;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GloomComponent(int level) {
    public static final Codec<GloomComponent> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
            Codec.INT.fieldOf("level").forGetter(GloomComponent::level)
        ).apply(builder, GloomComponent::new)
    );
}
