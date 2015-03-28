package uqbar.arena.persistence.mapping

import org.neo4j.graphdb.Node

import uqbar.arena.persistence.Session

abstract trait Mapping {
  def persist(session:Session, node: Node, target: Object)
  def hidrate(session:Session, node: Node, target: Object)
  def query(queryBuilder:QueryBuilder, target:Object)
}
