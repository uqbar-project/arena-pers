package uqbar.arena.persistence.configuration

import java.lang.reflect.Method
import org.apache.commons.lang.StringUtils
import org.uqbar.commons.model.Entity
import uqbar.arena.persistence.Configuration
import uqbar.arena.persistence.ConfigurationException
import uqbar.arena.persistence.annotations.PersistentClass
import uqbar.arena.persistence.annotations.PersistentField
import uqbar.arena.persistence.annotations.Relation
import uqbar.arena.persistence.mapping.EntityMapping
import uqbar.arena.persistence.mapping.FieldMapping
import uqbar.arena.persistence.mapping.RelationMapping
import org.apache.log4j.Logger
import java.lang.reflect.Field

class EntityVisitor[T <: Entity](entityClazz: Class[T]) {
  var entity: EntityMapping[_] = null
  val log = Logger.getLogger(this.getClass())

  if (isEntity(entityClazz)) {
    entity = new EntityMapping(entityClazz)
    log.info(s"Loaded persistent class: ${entityClazz.getCanonicalName()}")
    Configuration.entities.put(entityClazz.getCanonicalName(), entity)
  }

  def isEntity(clazz: Class[_]): Boolean = {
    if (clazz == classOf[Object] || clazz == null)
      return false
    else if (clazz.isAnnotationPresent(classOf[PersistentClass]))
      true
    else
      isEntity(clazz.getSuperclass())
  }

  def fieldAnnotation(clazz: Class[_], field: Field, annotation: PersistentField) {
    checkPersistentClass(field, clazz)
    this.entity.mappings += FieldMapping.create(field.getName, field.getType)
  }

  def fieldAnnotation(clazz: Class[_], field: Field, annotation: Relation) {
    checkPersistentClass(field, clazz)
    this.entity.mappings += RelationMapping.create(field.getName, field.getGenericType)
  }

  def checkPersistentClass(field: Field, clazz: Class[_]) {
    if (this.entity == null)
      throw new ConfigurationException("El atributo anotado se encuentra en una clase no anotada:" + field.getName() + " en:" + clazz.getCanonicalName())
  }

}