package uqbar.arena.persistence

import scala.collection.JavaConversions.asScalaSet
import scala.collection.mutable.HashMap

import org.uqbar.commons.model.Entity

import com.uqbar.commons.descriptor.ClassDescriptor

import uqbar.arena.persistence.configuration.EntityVisitor
import uqbar.arena.persistence.mapping.EntityMapping
import uqbar.arena.persistence.reflection.ClasspathCrawler

object Configuration {

  var entities = new HashMap[String, EntityMapping[_]];
  var rootPackageName = ""
  protected var initialized = false;

  def clear() {
    entities = new HashMap[String, EntityMapping[_]];
  }
  
  def setRootPackageName(value:String) = {
    this.rootPackageName = value
  }

  def checkStarted() {
    if (!initialized)
      throw new ConfigurationException("Se debe inicializar la configuración de la persistencia llamando a Configure.configure() antes de realizar cualquier cosa.")
  }

  def configure() {
    try {
      val classDescriptor = new ClassDescriptor();

      val clazzes = new ClasspathCrawler(this.getClass().getClassLoader()).getClasses(rootPackageName);

      clazzes.foreach { clazz =>
        val visitor = new EntityVisitor(clazz.asInstanceOf[Class[Entity]]);
        classDescriptor.describe(clazz, visitor);
      }

      SessionManager.startDB();
      initialized = true;
    } catch {
      case e: RuntimeException if e.getCause() != null && e.getCause().getClass() == classOf[ConfigurationException] => throw e.getCause()
      case e: Throwable => throw e
    }
  }

  def mappingFor[T <: Entity](clazz: Class[T]): EntityMapping[T] = {
    mappingByName(clazz.getName()).asInstanceOf[EntityMapping[T]]
  }

  def mappingFor[T <: Entity](target: T): EntityMapping[T] = {
    mappingFor(target.getClass()).asInstanceOf[EntityMapping[T]]
  }

  def mappingByName(className: String): EntityMapping[_] = {
    checkStarted();
    this.entities.get(className).orNull
  }
}

class ConfigurationException(message: String, cause: Throwable) extends Exception(message, cause) {
  def this(message: String) = this(message, null)
}