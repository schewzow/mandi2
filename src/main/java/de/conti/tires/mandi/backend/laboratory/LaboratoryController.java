package de.conti.tires.mandi.backend.laboratory;

import de.conti.tires.mandi.backend.core.base.AbstractBaseController;
import de.conti.tires.mandi.backend.core.validation.Validator;
import de.conti.tires.mandi.backend.laboratory.projection.LaboratoryDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for laboratory management.
 */
@RestController
@RequestMapping("${BASE_URL}/api/labs")
@RequiredArgsConstructor
public class    LaboratoryController
        extends AbstractBaseController<LaboratoryEntity, LaboratoryDetails, LaboratoryRepository>
{
    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryDetailConverter laboratoryDetailConverter;
    private final LaboratoryValidator laboratoryValidator;

    @Override
    protected Class<LaboratoryEntity> getEntityClass()
    {
        return LaboratoryEntity.class;
    }

    /**
     * @return repository to use
     */
    @Override
    protected LaboratoryRepository getRepository()
    {
        return laboratoryRepository;
    }

    /**
     * @return converter for converting the entity to the outcome projection
     */
    @Override
    protected Converter<LaboratoryEntity, LaboratoryDetails> getConverter()
    {
        return laboratoryDetailConverter;
    }

    @Override
    protected Validator<LaboratoryEntity> getValidator()
    {
        return laboratoryValidator;
    }

//    @GetMapping("/search")
//    PagedResponse<LaboratoryDetails> getLabs(
//            @RequestParam("filter") String filter,
//            Pageable pageable
//    ) {
//        Page<LaboratoryEntity> page = laboratoryRepository.findFiltered(filter.toUpperCase(), pageable);
//
//        PagedResponse<LaboratoryDetails> response = new PagedResponse<>();
//        PageInfo pageInfo = new PageInfo();
//        pageInfo.setPage(page.getPageable().getPageNumber());
//        pageInfo.setSize(page.getPageable().getPageSize());
//        pageInfo.setTotalElements(page.getTotalElements());
//        pageInfo.setTotalPages(page.getTotalPages());
//        pageInfo.setLastPage(page.getPageable().getPageNumber() == page.getTotalPages() - 1);
//
//        EmbeddedContent<LaboratoryDetails> embedded = new EmbeddedContent<>();
//        embedded.setContent(page.getContent().stream().map(laboratoryDetailConverter::convert).collect(Collectors.toList()));
//        response.setEmbedded(embedded);
//        response.setPage(pageInfo);
//
//        return response;
//    }
}
