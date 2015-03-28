package uqbar.arena.persistence.mapping

import uqbar.arena.persistence.AbstractArenaPersistenceTest
import org.junit.Test
import uqbar.arena.persistence.SessionManager
import uqbar.arena.persistence.testDomain.Persona
import uqbar.arena.persistence.testDomain.Celular
import uqbar.arena.persistence.testDomain.Modelo
import java.util.Date
import org.junit.Assert
import org.neo4j.graphdb.Direction
import scala.collection.JavaConversions._

class SaveEntityTest extends AbstractArenaPersistenceTest {

  @Test
  def testCelular() {
    val nac = new Date();
    val per = new Persona("Juan Carlos", "Batman", nac, 125)
    val cel = new Celular(Modelo.MOTOROLA_1100, "12345", per, 1.25);

    SessionManager.runInSession({
      SessionManager.currentSession.save(cel);
    });

    val node = graphDb.getNodeById(1);
    var count = 0

    for (r <- node.getRelationships(Direction.OUTGOING)) {
      Assert.assertEquals("Juan Carlos", r.getEndNode().getProperty("nombre"))
      Assert.assertEquals("Batman", r.getEndNode().getProperty("apellido"))

      count = count + 1
    }

    Assert.assertEquals(1, count)
  }
}