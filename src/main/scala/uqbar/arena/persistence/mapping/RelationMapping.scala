package uqbar.arena.persistence.mapping

import java.lang.reflect.Type
import org.neo4j.graphdb.Node
import uqbar.arena.persistence.ConfigurationException
import uqbar.arena.persistence.Session
import uqbar.arena.persistence.reflection.TypeWrapper
import org.uqbar.commons.utils.ReflectionUtils
import org.neo4j.graphdb.DynamicRelationshipType
import org.neo4j.graphdb.Direction
import org.uqbar.commons.model.Entity
import scala.collection.JavaConversions._
import java.util.Collection

object RelationMapping {
  def create(name: String, fieldType: Type): RelationMapping = {
    val wrappedType = new TypeWrapper(fieldType)
    if (wrappedType.isCollectionOfPersistent)
      new CollectionRelationMapping(name, wrappedType)
    else
      new SimpleRelationMapping(name, wrappedType)
  }
}

abstract class RelationMapping(name: String, wrappedType: TypeWrapper) extends Mapping {
  checkRelation();

  def query(queryBuilder: QueryBuilder, target: Object) {
    val value = ReflectionUtils.invokeGetter(target, this.name).asInstanceOf[Collection[Entity]]
    if (value != null) {
      throw new Exception("No se puede hacer query by example con relaciones entre objetos.")
    }
  }

  def checkRelation() {
    if (wrappedType.isNative && wrappedType.isEnum && wrappedType.isBuiltinType) {
      throw new ConfigurationException("La annotation Relation no es aplicable a tipos nativos, Enum, String o java.util.Date:" + wrappedType.name);
    }
    if (!wrappedType.isCollectionOfPersistent && !wrappedType.isPersistent) {
      throw new ConfigurationException("La annotation Relation es solo aplicable a PersistentClass o a colecciones de PersistentClass:" + wrappedType.name);
    }
  }
}

class SimpleRelationMapping(name: String, wrappedType: TypeWrapper) extends RelationMapping(name, wrappedType) {

  def persist(session: Session, node: Node, target: Object): Unit = {
    val value = ReflectionUtils.invokeGetter(target, this.name)
    val relType = DynamicRelationshipType.withName(this.name)
    val graphDB = session.graphDB

    val r = node.getSingleRelationship(relType, Direction.OUTGOING)
    if (r != null) {
      r.delete()
    }

    if (value == null) {
      return ;
    }

    val entity = value.asInstanceOf[Entity]
    if (entity.isNew())
      session.save(entity)

    val otherNode = graphDB.getNodeById(entity.getId().longValue())
    node.createRelationshipTo(otherNode, relType)
  }

  def hidrate(session: Session, node: Node, target: Object): Unit = {
    val relType = DynamicRelationshipType.withName(this.name)
    val graphDB = session.graphDB
    val r = node.getSingleRelationship(relType, Direction.OUTGOING)

    if (r == null) {
      ReflectionUtils.invokeSetter(target, this.name, null)
      return
    }

    val otherNode = r.getEndNode()

    val entity: Any = session.get(otherNode.getProperty("clazzName").toString(), otherNode.getId().intValue())
    ReflectionUtils.invokeSetter(target, this.name, entity)
  }

}

class CollectionRelationMapping(name: String, wrappedType: TypeWrapper) extends RelationMapping(name, wrappedType) {
  def persist(session: Session, node: Node, target: Object): Unit = {
    val relType = DynamicRelationshipType.withName(this.name)
    val graphDB = session.graphDB
    val rels = node.getRelationships(relType, Direction.OUTGOING)
    for (r <- rels) {
      r.delete();
    }
    val value = ReflectionUtils.invokeGetter(target, this.name).asInstanceOf[Collection[Entity]]
    if (value == null || value.isEmpty())
      return

    for (e <- value) {
      if (e.isNew())
        session.save(e)

      val otherNode = graphDB.getNodeById(e.getId().longValue())
      node.createRelationshipTo(otherNode, relType)
    }
  }

  def hidrate(session: Session, node: Node, target: Object): Unit = {
    val relType = DynamicRelationshipType.withName(this.name)
    val graphDB = session.graphDB
    val rels = node.getRelationships(relType, Direction.OUTGOING)

    var value = ReflectionUtils.invokeGetter(target, this.name).asInstanceOf[Collection[Entity]]

    if (value == null || !value.isEmpty()) {
      value = this.wrappedType.newInstance();
      ReflectionUtils.invokeSetter(target, this.name, value)
    }

    for (r <- rels) {
      val otherNode = r.getEndNode()
      val entity: Entity = session.get(otherNode.getProperty("clazzName").toString(), otherNode.getId().intValue()).asInstanceOf[Entity]
      value.add(entity)
    }
  }

}