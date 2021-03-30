package com.github.arcanjoaq.kefla.hibernate;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;

class ContainerEntityManagerFactory {

  private DataSource dataSource;
  private PersistenceUnitProperties persistenceUnitProperties;
  private List<Class<?>> managedClasses;

  public ContainerEntityManagerFactory(final DataSource dataSource, 
      final PersistenceUnitProperties persistenceUnitProperties, 
      final List<Class<?>> managedClasses) {
    checkArgument(managedClasses != null);
    checkArgument(dataSource != null);
    checkArgument(persistenceUnitProperties != null);
    this.dataSource = dataSource;
    this.persistenceUnitProperties = persistenceUnitProperties;
    this.managedClasses = managedClasses;
  }
  
  public EntityManagerFactory createEntityManager() {
    final HibernateContainerPersistenceUnitInfo persistenceUnitInfo = 
        new HibernateContainerPersistenceUnitInfo(
        persistenceUnitProperties.getName(), 
        managedClasses, persistenceUnitProperties)
        .setNonJtaDataSource(dataSource);

    return new HibernatePersistenceProvider()
        .createContainerEntityManagerFactory(persistenceUnitInfo, null);
  }
}