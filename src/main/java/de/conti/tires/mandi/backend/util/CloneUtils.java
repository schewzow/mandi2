package de.conti.tires.mandi.backend.util;

import com.google.common.collect.Sets;
import de.conti.tires.mandi.backend.core.base.AuditBaseEntity;
import de.conti.tires.mandi.backend.core.base.BaseEntity;
import de.conti.tires.mandi.backend.core.exception.ValidationException;
import de.conti.tires.mandi.backend.core.validation.ValidationErrors;
import de.conti.tires.mandi.backend.core.validation.Validator;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;


/**
 * Cloning related functionality.
 */
public class CloneUtils {
    /**
     * Makes a shallow copy of an object using it property getters/setters.
     * No deep copy - references are copied instead of cloned.
     *
     * @param source source bean
     * @param <E>    bean type (Hibernate proxies will be unwrapped)
     * @return shallow copy
     * @throws IllegalArgumentException object could not be cloned
     */
    public static <E> E shallowBeanCopy(E source) throws IllegalArgumentException {
        try {
            E instance = HibernateUtils.unproxyClass(source).newInstance();
            BeanUtils.copyProperties(instance, source);
            return instance;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException("cloning failed", e);
        }
    }

    /**
     * Makes a shallow copy of an {@link AuditBaseEntity} using it property getters/setters.
     * No deep copy - references are copied instead of cloned. But keep in mind that Hibernate proxies will be called.
     * <p>
     * UUID, version, created and modified information will be cleared.
     *
     * @param source source bean
     * @param <E>    bean type (Hibernate proxies will be unwrapped)
     * @return shallow copy
     * @throws IllegalArgumentException object could not be cloned
     * @deprecated use {@link #cloneBaseEntity(BaseEntity, String...)} instead
     */
    @Deprecated
    public static <E extends AuditBaseEntity> E shallowAuditBaseCopy(E source) throws IllegalArgumentException {
        E copy = shallowBeanCopy(source);
        copy.setUuid(null);
        copy.setCreatedBy(null);
        copy.setCreatedDate(null);
        copy.setLastModifiedBy(null);
        copy.setLastModifiedDate(null);
        copy.setVersion(null);
        return copy;
    }

    /**
     * Makes a shallow copy of a {@link BaseEntity} using it property getters/setters.
     * {@link Set} properties are initialized as empty {@link HashSet} instances.
     * Other {@link Collection} instances are ignored.
     * {@link Map} properties are initialized as empty {@link HashMap} instances.
     * The following properties will be ignored: uuid, createdBy, createdDate, lastModifiedBy, lastModifiedDate,
     * version. Additional ones can be defined.
     * <p>
     * No deep copy - references are copied instead of cloned except the case described above.
     *
     * @param source             source bean
     * @param propertiesToIgnore names of properties to ignore in addition to standard excludes
     * @param <E>                bean type (Hibernate proxies will be unwrapped)
     * @return shallow copy
     * @throws IllegalArgumentException object could not be cloned
     */
    @SneakyThrows
    public static <E extends BaseEntity> E cloneBaseEntity(@NonNull E source, @NonNull String... propertiesToIgnore)
            throws IllegalArgumentException {
        Set<String> excludes = Sets
                .newHashSet("uuid", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate", "version");
        excludes.addAll(Arrays.asList(propertiesToIgnore));
        E target = HibernateUtils.unproxyClass(source).getConstructor().newInstance();
        Stream.of(PropertyUtils.getPropertyDescriptors(source))
                .filter(prop -> prop.getReadMethod() != null && prop.getWriteMethod() != null)
                .filter(prop -> !(excludes.contains(prop.getName())))
                .forEach(prop -> copyValue(prop, source, target));

        return target;
    }

    @SneakyThrows
    private static void copyValue(@NonNull PropertyDescriptor descriptor, @NonNull Object source, @NonNull Object target) {
        Class<?> propertyType = descriptor.getPropertyType();
        Object value;
        if (Set.class.equals(propertyType)) {
            value = new HashSet<>();
        } else if (Map.class.equals(propertyType)) {
            value = new HashMap<>();
        } else if (Collection.class.isAssignableFrom(propertyType)) {
            return; // ignore
        } else {
            value = descriptor.getReadMethod().invoke(source);
        }
        descriptor.getWriteMethod().invoke(target, value);
    }

    /**
     * Validates an upcoming duplication using a controller's {@link Validator}.
     *
     * @param <E>         entity type
     * @param source      source object
     * @param validator   validator to use
     * @param initializer duplicated (temporary) object and (mocked) payload data map initializer
     * @throws ValidationException validator finds an error
     */
    public static <E extends BaseEntity> void validateDuplication(@NonNull E source,
                                                                  @NonNull Validator<E> validator, @NonNull BiConsumer<E, Map<String, Object>> initializer)
            throws ValidationException {
        ValidationErrors errors = new ValidationErrors();
        E tempEntity = cloneBaseEntity(source);
        Map<String, Object> data = new HashMap<>();
        initializer.accept(tempEntity, data);
        validator.validate(tempEntity, null, data, errors);

        if (!(errors.isEmpty())) {
            throw new ValidationException(errors);
        }
    }
}
