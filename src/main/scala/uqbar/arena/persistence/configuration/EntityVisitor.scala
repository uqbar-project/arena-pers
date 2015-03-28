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

  def methodAnnotation(clazz: Class[_], method: Method, annotation: PersistentField) {
    checkPersistentClass(method, clazz)
    val name = extractName(method, annotation.annotationType().getName())
    val fieldType = method.getGenericReturnType();
    this.entity.mappings += FieldMapping.create(name, fieldType)
  }

  def methodAnnotation(clazz: Class[_], method: Method, annotation: Relation) {
    checkPersistentClass(method, clazz)
    val name = extractName(method, annotation.annotationType().getName())
    val fieldType = method.getGenericReturnType();
    this.entity.mappings += RelationMapping.create(name, fieldType)
  }

  def checkPersistentClass(method: Method, clazz: Class[_]) {
    if (this.entity == null)
      throw new ConfigurationException("El metodo anotado se encuentra en una clase no anotada:" + method.getName() + " en:" + clazz.getCanonicalName())
  }

  def extractName(method: Method, annotationName: String): String = {
    if (!method.getName().startsWith("get")) {
      throw new ConfigurationException("La annotation " + annotationName + " solo es válida anotando un getter");
    }
    return StringUtils.uncapitalize(method.getName().substring(3));
  }
}