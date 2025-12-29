package de.conti.tires.mandi.backend.core.validation;


import de.conti.tires.mandi.backend.core.base.Translatable;

/**
 * Interface for a translatable message to be translated during Jackson serialization.
 * TODO actually the type hierarchy and intention is kind of broken here: message should not be defined
 */
public interface TranslatableMessage extends Message, Translatable
{
}
