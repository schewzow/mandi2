package de.conti.tires.mandi.backend.core.base;

/**
 * Interface for a translatable message to be translated during Jackson serialization.
 * <p>
 * A {@code message} property containing the translated text will be created on-the-fly.
 * Thus DO NOT DEFINE A MESSAGE PROPERTY!
 */
//@JsonSerialize(using = TranslatableSerializer.class)
public interface Translatable {
    /**
     * @return message key - never {@code null}
     */
    String getKey();

    /**
     * @return message parameters (never {@code null} but might be empty)
     */
    Object[] getParameters();
}
