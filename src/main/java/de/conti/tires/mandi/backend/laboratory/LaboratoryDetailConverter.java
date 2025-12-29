package de.conti.tires.mandi.backend.laboratory;

import de.conti.tires.mandi.backend.laboratory.projection.LaboratoryDetails;
import de.conti.tires.mandi.backend.user.UserSummaryConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * Converter for laboratory detail projection.
 */
@Component
@RequiredArgsConstructor
public class LaboratoryDetailConverter implements Converter<LaboratoryEntity, LaboratoryDetails> {
    private UserSummaryConverter userConverter = new UserSummaryConverter();

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public LaboratoryDetails convert(@NonNull LaboratoryEntity source) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true); // prevent error - we'll handle references in a few lines

        LaboratoryDetails details = new LaboratoryDetails();
        modelMapper.map(source, details);

        Optional.ofNullable(source.getCreatedBy())
                .ifPresent(value -> details.setCreatedBy(userConverter.convert(value)));

        Optional.ofNullable(source.getLastModifiedBy())
                .ifPresent(value -> details.setLastModifiedBy(userConverter.convert(value)));

        return details;
    }
}
