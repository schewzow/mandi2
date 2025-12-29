package de.conti.tires.mandi.backend.core.base;

import de.conti.tires.mandi.backend.core.validation.Message;
import de.conti.tires.mandi.container.util.StaticContextAccessor;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.introspect.BeanPropertyDefinition;

import java.lang.reflect.Method;

@NoArgsConstructor
public class TranslatableSerializer extends ValueSerializer<Translatable> {

    private String buildMessage(Translatable message) {
        MessageSource ms = StaticContextAccessor.getBean(MessageSource.class);
        String key = message.getKey();
        return ms == null ? key :
                ms.getMessage(key, message.getParameters(), key, LocaleContextHolder.getLocale());
    }

    @Override
    public void serialize(Translatable value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        // 1. Manually introspect the class to avoid calling Jackson's loop-prone serializer lookups
        JavaType javaType = ctxt.constructType(value.getClass());
        BeanDescription beanDesc = ctxt.introspectBeanDescription(javaType);

        gen.writeStartObject();

        // 2. Handle 'Message' specific logic
        if (value instanceof Message message) {
            message.setMessage(buildMessage(value));
        }

        // 3. Manually iterate and write all discovered properties (getters/fields)
        // This bypasses the need for BeanPropertyWriter and breaks the infinite loop.
        for (BeanPropertyDefinition prop : beanDesc.findProperties()) {
            try {
                if (prop.hasGetter()) {
                    Method getter = prop.getGetter().getAnnotated();
                    Object val = getter.invoke(value);
                    if (val != null) {
                        gen.writeName(prop.getName());
                        // Delegate to Jackson only for the field's value, not the whole object
                        ctxt.findValueSerializer(val.getClass()).serialize(val, gen, ctxt);
                    }
                }
            } catch (Exception e) {
                // Skip properties that can't be accessed to keep JSON valid
            }
        }

        // 4. Add the virtual "message" field for non-Message objects
        if (!(value instanceof Message)) {
            gen.writeName("message");
            gen.writeString(buildMessage(value));
        }

        gen.writeEndObject();
    }
}