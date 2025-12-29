package de.conti.tires.mandi.backend.user;

import de.conti.tires.mandi.backend.core.base.AbstractBaseController;
import de.conti.tires.mandi.backend.core.validation.Validator;
import de.conti.tires.mandi.backend.user.projection.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for laboratory management.
 */
@RestController
@RequestMapping("${BASE_URL}/api/users")
@RequiredArgsConstructor
public class UserController
        extends AbstractBaseController<UserEntity, UserDetails, UserRepository>
{
    private final UserRepository repository;
    private final UserDetailsConverter detailsConverter;
    private final UserValidator validator;

    @Override
    protected Class<UserEntity> getEntityClass()
    {
        return UserEntity.class;
    }

    /**
     * @return repository to use
     */
    @Override
    protected UserRepository getRepository()
    {
        return repository;
    }

    /**
     * @return converter for converting the entity to the outcome projection
     */
    @Override
    protected Converter<UserEntity, UserDetails> getConverter()
    {
        return detailsConverter;
    }

    @Override
    protected Validator<UserEntity> getValidator()
    {
        return validator;
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
