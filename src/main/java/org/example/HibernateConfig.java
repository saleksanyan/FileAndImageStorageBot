package org.example;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateConfig {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    // This method initializes and manages Hibernate configuration and services.
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create a central registry for Hibernate configuration and services.
                registry = new StandardServiceRegistryBuilder().configure().build();

                // Build Metadata from the configuration. Metadata represents Hibernate's
                // understanding of data models, entity mappings, and database connections.
                Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();

                // Create a SessionFactory using the Metadata. The SessionFactory is a heavyweight
                // object responsible for creating individual Hibernate Session instances.
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                // Handle exceptions gracefully, print stack trace for debugging purposes.
                e.printStackTrace();

                // If an exception occurs, release resources associated with the registry.
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        // Return the created or existing SessionFactory, allowing efficient sharing.
        return sessionFactory;
    }

    // This method is responsible for cleaning up and releasing Hibernate resources during application shutdown.
    public static void shutdown() {
        if (registry != null) {
            // Properly release resources and shut down Hibernate by destroying the registry.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
