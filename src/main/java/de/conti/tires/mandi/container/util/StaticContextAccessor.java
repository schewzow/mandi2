package de.conti.tires.mandi.container.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


/**
 * Helper class to retrieve beans from spring context in a static way.
 */
@Component
public class StaticContextAccessor
{
   private static StaticContextAccessor instance;

   private ApplicationContext applicationContext;

   @Autowired
   StaticContextAccessor(ApplicationContext applicationContext)
   {
      this.applicationContext = applicationContext;
      instance = this;
   }

   /**
    * Retrieve bean from spring context if context is available, otherwise always <code>null</code> is returned.
    *
    * @param clazz bean class type to look for
    * @param <T>   type parameter
    * @return bean instance from spring context or <code>null</code> if no bean instance found or
    * <code>null</code> if spring context is not available
    */
   public static <T> T getBean(Class<T> clazz)
   {
      return instance != null ? instance.applicationContext.getBean(clazz) : null;
   }

}
