package uqbar.arena.persistence.mapping

import scala.collection.JavaConversions._
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set

import org.neo4j.graphdb.DynamicRelationshipType
import org.neo4j.graphdb.Node
import org.uqbar.commons.model.Entity

import uqbar.arena.persistence.Session

class EntityMapping[T <: Entity](clazz: Class[T]) {
  var mappings = new HashSet[Mapping]();

  def persist(session: Session, target: Object) {
    val entity = target.asInstanceOf[Entity];
    val graphDB = session.graphDB

    var node: Node = null;

    if (entity.isNew)
      node = graphDB.createNode()
    else
      node = graphDB.getNodeById(entity.getId().longValue)

    entity.setId(node.getId().toInt)

    graphDB.index().forNodes("CLASS").add(node, "clazzName", entity.getClass().getCanonicalName());

    node.setProperty("clazzName", entity.getClass().getCanonicalName())

    session.registerEntity(entity)

    for (m <- mappings) {
      m.persist(session, node, target);
    }
  }

  def hidrate(session: Session, target: Object) {
    val entity = target.asInstanceOf[Entity];
    val graphDB = session.graphDB

    val node = graphDB.getNodeById(entity.getId().longValue)

    for (m <- mappings) {
      m.hidrate(session, node, target);
    }
  }

  def getAllFields(): Set[Mapping] = {
    this.mappings.filter({ x =>
      x match {
        case y: FieldMapping =>
          true
        case _ => false
      }
    })
  }

  def searchByExample(entity: T, session: Session): List[T] = {
    val graphDB = session.graphDB
    val queryBuilder = new QueryBuilder()

    for (m <- mappings) {
      m.query(queryBuilder, entity)
    }
    val queryString = queryBuilder.query

    val nodes = {
      if (!queryString.isEmpty())
        graphDB.index().forNodes("Attr-" + entity.getClass().toString()).query(queryString).iterator()
      else {
        graphDB.index().forNodes("CLASS").get("clazzName", clazz.getName()).iterator()
      }
    }
    session.convertTo(this.clazz, nodes);
  }
}

class QueryBuilder() {
  val builder = StringBuilder.newBuilder

  def add(name: String, v: String) {
    if (!builder.isEmpty)
      builder.append(" AND ")

    val nv = v.replaceAll("\\s", "\\\\ ");

    if (nv.startsWith("*") && nv.endsWith("*"))
      builder.append(name).append(":").append(nv)
    else
      builder.append(name).append(":\"").append(nv).append("\"")
  }

  def query(): String = {
    builder.toString
  }
}
