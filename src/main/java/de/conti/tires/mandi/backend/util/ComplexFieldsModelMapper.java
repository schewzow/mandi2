package de.conti.tires.mandi.backend.util;

import de.conti.tires.mandi.backend.core.base.BaseEntity;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isEmpty;


/**
 * A ModelMapper with various converter to handle complex string fields such as datetime and url.
 * <p>
 * On default this implementation uses {@link MatchingStrategies#STRICT}.
 */
public class ComplexFieldsModelMapper extends ModelMapper
{
   private BaseEntity target;

   /**
    * Creates a new instance and sets the entity for which the fields should be mapped.
    *
    * @param target Entity for which the fields should be mapped.
    */
   public <E extends BaseEntity> ComplexFieldsModelMapper(E target)
   {
      this();
      this.target = target;
   }

   /**
    * Creates a new instance.
    */
   public ComplexFieldsModelMapper()
   {
      Provider<LocalDateTime> dateProvider = new AbstractProvider<LocalDateTime>()
      {
         @Override
         public LocalDateTime get()
         {
            return LocalDateTime.now();
         }
      };

      Converter<String, LocalDateTime> stringConverter = new AbstractConverter<String, LocalDateTime>()
      {
         @Override
         protected LocalDateTime convert(String source)
         {
            DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return LocalDateTime.parse(source, format);
         }
      };

      Converter<String, URL> urlConverter = new AbstractConverter<String, URL>()
      {
         @Override
         protected URL convert(String source)
         {
            if (isEmpty(source))
            {
               return null;
            }

            try
            {
               return new URL(source);
            }
            catch (MalformedURLException e)
            {
               throw new IllegalArgumentException("payload cannot be converted to a valid url");
            }
         }
      };

      Converter<String, LocalDate> localDateConverter = new AbstractConverter<String, LocalDate>()
      {
         @Override protected LocalDate convert(String source)
         {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            return LocalDate.parse(source, dateTimeFormatter);
         }
      };

      Converter<String, UUID> uuidConverter = new AbstractConverter<String, UUID>()
      {
         @Override protected UUID convert(String source)
         {
            return UUID.fromString(source);
         }
      };

      Converter<String, List<String>> stringListConverter = new AbstractConverter<String, List<String>>()
      {
         @Override
         public List<String> convert(String value)
         {
            return parseSemicolonSeparatedCollection(value, Collectors.toList());
         }
      };

      this.createTypeMap(String.class, LocalDateTime.class);
      this.addConverter(stringConverter);
      this.addConverter(urlConverter);
      this.addConverter(uuidConverter);
      this.addConverter(localDateConverter);
      this.addConverter(stringListConverter);
      this.getTypeMap(String.class, LocalDateTime.class).setProvider(dateProvider);
   }

   /**
    * Parses a semicolon separated string and collects the segments (ignoring empty ones) via the given collector.
    *
    * @param value     value to parse ({@code null} safe)
    * @param collector collection collector to use
    * @param <E>       collection type
    * @return resulting collection
    */
   public static <E extends Collection<String>> E parseSemicolonSeparatedCollection(String value,
         @NonNull Collector<? super String, ?, E> collector)
   {
      return parseSemicolonSeparatedCollection(value).collect(collector);
   }

   /**
    * Parses a semicolon separated string and returns the found segments (ignoring empty ones).
    *
    * @param value value to parse ({@code null} safe)
    * @return found segments
    */
   public static Stream<String> parseSemicolonSeparatedCollection(String value)
   {
      if (value == null)
      {
         return Stream.empty();
      }
      return Stream.of(value.split(";"))
            .filter(StringUtils::isNotEmpty); // we do not want to save empty/null elements
   }

   private static class ComplexFieldsModelMapperException extends RuntimeException
   {
      public ComplexFieldsModelMapperException()
      {
         super("Invalid usage of ComplexFieldsModelMapper!");
      }
   }
}
