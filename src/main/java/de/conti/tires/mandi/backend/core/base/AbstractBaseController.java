package de.conti.tires.mandi.backend.core.base;

import de.conti.tires.mandi.backend.core.exception.ApiException;
import de.conti.tires.mandi.backend.core.exception.InvalidReferenceException;
import de.conti.tires.mandi.backend.core.exception.ValidationException;
import de.conti.tires.mandi.backend.core.validation.Validator;
import de.conti.tires.mandi.backend.payload.EmbeddedContent;
import de.conti.tires.mandi.backend.payload.PageInfo;
import de.conti.tires.mandi.backend.payload.PagedResponse;
import de.conti.tires.mandi.backend.util.CloneUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractBaseController<E extends BaseEntity, P, R extends BaseEntityRepository<E>> {

    @Autowired
    protected EntityManagerFactory entityManagerFactory;
    @Autowired
    protected EntityManager entityManager;

    /**
     * Creates a new single entity. Usually not all fields will be filled.
     * UUID will be ignored.
     *
     * @param payload input data (property to value)
     * @return resulting entity as dto
     * @throws InvalidReferenceException single reference is no UUID or referenced entity not found
     * @throws ValidationException       validation error found
     * @throws AccessDeniedException     user is not allowed to perform requested operation
     */
    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public P postItem(@RequestBody Map<String, Object> payload) throws InvalidReferenceException, ValidationException,
            AccessDeniedException
    {
        //System.out.println("postItem payload: " + payload);
        E entity = createEntity(payload);
        return performUpdateFromMap(payload, entity, true);
    }

    /**
     * Perform a (partial) update of an entity.
     *
     * @param uuid    entity primary key
     * @param payload update information (property to value)
     * @return dto of updated entity
     * @throws ResourceNotFoundException entity primary key not found
     * @throws InvalidReferenceException single reference is no UUID or referenced entity not found
     * @throws ValidationException       validation error found
     * @throws AccessDeniedException     user is not allowed to perform requested operation
     */
    @PatchMapping("{uuid}")
    @Transactional
    public P patchItem(@PathVariable("uuid") UUID uuid, @RequestBody Map<String, Object> payload)
            throws ResourceNotFoundException, InvalidReferenceException, ValidationException, AccessDeniedException
    {
        return performUpdateFromMap(payload, loadEntity(uuid).orElseThrow(ResourceNotFoundException::new), false);
    }

    /**
     * Requests a single entity via primary key
     *
     * @param uuid object primary key
     * @return requested entity as dto
     * @throws ResourceNotFoundException object with requested ID does not exist
     * @throws AccessDeniedException     user is not allowed to perform requested operation
     */
    @GetMapping("{uuid}")
    @Transactional
    public P getItem(@PathVariable("uuid") UUID uuid) throws ResourceNotFoundException, AccessDeniedException
    {
        E entity = loadEntity(uuid).orElseThrow(ResourceNotFoundException::new);

        //authorization.getReadRestriction().testAuth(entity, authenticationService.currentUser());

        return getConverter().convert(entity);
    }

    @GetMapping("/search")
    @Transactional
    public PagedResponse<P> getPage(@RequestParam("filter") String filter, Pageable pageable)
    {
        Page<E> page = (Page<E>) getRepository().findFiltered(filter.toUpperCase(), pageable);
        PagedResponse<P> response = new PagedResponse<>();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page.getPageable().getPageNumber());
        pageInfo.setSize(page.getPageable().getPageSize());
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setLastPage(page.getPageable().getPageNumber() == page.getTotalPages() - 1);

        EmbeddedContent<P> embedded = new EmbeddedContent<>();
        embedded.setContent(page.getContent().stream().map(getConverter()::convert).collect(Collectors.toList()));
        response.setEmbedded(embedded);
        response.setPage(pageInfo);

        return response;
    }

    /**
     * Loads the entity with the given ID.
     *
     * @param uuid primary key / ID
     * @return corresponding existing entity instance - if existing
     */
    protected Optional<E> loadEntity(UUID uuid)
    {
        return getRepository().findById(uuid);
    }

    /**
     * @return Repository to use.
     */
    protected abstract R getRepository();

    /**
     * @return Converter for converting the entity to the outcome projection.
     */
    protected abstract Converter<E, P> getConverter();

    /**
     * Creates a new entity instance. Instance must be detached.
     * <p>
     * The default implementation just calls the default constructor.
     *
     * @param payload request payload
     * @return New entity.
     */
    protected E createEntity(@NotNull Map<String, Object> payload)
    {
        try
        {
            return getEntityClass().getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new ApiException(e);
        }
    }

    /**
     * @return Entity class.
     */
    protected abstract Class<E> getEntityClass();

    /**
     * Updates a (partial) update of the given entity based on the given data.
     *
     * @param payload update data source
     * @param entity  target entity to modify
     * @param create  entity is a new one that is created right now (in difference to patching an existing one).
     * @return resulting projection
     * @throws InvalidReferenceException single reference is no UUID or referenced entity not found
     * @throws AccessDeniedException     user is not allowed to perform requested operation
     * @throws ValidationException       a value was sent can not be transformed to the target structure
     */
    protected P performUpdateFromMap(Map<String, Object> payload, E entity, boolean create)
            throws InvalidReferenceException, AccessDeniedException, ValidationException
    {
        E oldState = create ? null : createCopy(entity);
        performPatch(payload, entity, create);
        EntityDiff<E> diff = new EntityDiff(entity.getClass(), oldState, entity, payload);
        if (create)
        {
            //authorization.getCreateRestriction().testAuth(diff, authenticationService.currentUser());
        }
        else
        {
            //authorization.getModifyRestriction().testAuth(diff, authenticationService.currentUser());
        }
        ServiceUtils.performValidation(payload,entity,create,oldState,getValidator());
        saveEntity(entity, create, payload);
        //ServiceUtils.notifyHandler(create, entity, payload, oldState, domainObjectEventHandler);
        return getConverter().convert(entity);
    }

    /**
     * Creates a detached copy.
     *
     * @param source source object. Never {@code null}.
     * @return Detached copy.
     */
    protected E createCopy(@NotNull E source)
    {
        // If we face performance issues one day we can replace this by a custom flat mapping
        return CloneUtils.shallowBeanCopy(source);
    }

    /**
     * Updates the target entity from the patch.
     * <p>
     * Single {@link BaseEntity} references have to be delivered as UUIDs. Collections are ignored.
     * <p>
     * The default implementation does not check for invalid fields and ignores such instead.
     *
     * @param payload payload to process (not empty, not {@code null})
     * @param target  target to update from payload (never {@code null})
     * @param create  this method is called during creation of a new entity (otherwise an existing one is modified)
     * @throws InvalidReferenceException single reference is no UUID or referenced entity not found
     * @throws ValidationException       a value was sent can not be transformed to the target structure
     */
    protected void performPatch(@NotEmpty Map<String, Object> payload, @NotNull E target, boolean create)
            throws InvalidReferenceException, ValidationException
    {
        ServiceUtils.performPatch(payload, target, entityManagerFactory, entityManager);
    }

    /**
     * Stores the given entity to the database.
     * Will be called before informing change listeners and converting to projection.
     * <p>
     * This implementation calls {@link BaseEntityRepository#flush()} and therefore audit information is updated.
     *
     * @param entity  entity to save - never {@code null}
     * @param create  {@code true} if this is a new entity that was not persisted yet
     * @param payload input data
     */
    protected void saveEntity(E entity, boolean create, Map<String, Object> payload)
    {
        getRepository().saveAndFlush(entity);
    }

    /**
     * Specifies an optional validator to validate the entity before saving.
     * <p>
     * The default implementation returns {@code null} - hence defining no validation rules.
     *
     * @return the Validator instance or {@code null} if no validator is available
     */
    protected Validator<E> getValidator()
    {
        return null;
    }
}
