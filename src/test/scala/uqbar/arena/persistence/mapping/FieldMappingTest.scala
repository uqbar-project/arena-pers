package uqbar.arena.persistence.mapping

import org.junit.Assert
import org.junit.Test
import uqbar.arena.persistence.AbstractArenaPersistenceTest
import uqbar.arena.persistence.Configuration
import uqbar.arena.persistence.testDomain.Celular
import uqbar.arena.persistence.testDomain.Modelo
import uqbar.arena.persistence.testDomain.Persona
import java.util.Date
import uqbar.arena.persistence.SessionManager
import uqbar.arena.persistence.testDomain.CelularCheto

class FieldMappingTest extends AbstractArenaPersistenceTest {

  @Test
  def testCelular {
    SessionManager.runInSession({
      var cel = new Celular(Modelo.MOTOROLA_1100, "12345", null, 1.25);
      var celMapping = Configuration.mappingFor(cel);

      Assert.assertNotNull(celMapping);

      var n = graphDb.createNode();
      for (m <- celMapping.getAllFields()) {
        m.persist(SessionManager.currentSession, n, cel);
      }

      var newCel = new Celular();

      for (m <- celMapping.getAllFields()) {
        m.hidrate(SessionManager.currentSession, n, newCel);
      }

      Assert.assertNotSame(cel, newCel);
      Assert.assertEquals(cel, newCel);
    })
  }

  @Test
  def testPersona {
    SessionManager.runInSession({
      var per = new Persona("Juan", "Perez", new Date(), 37);
      var perMapping = Configuration.mappingFor(per);

      Assert.assertNotNull(perMapping);

      var n = graphDb.createNode();
      for (m <- perMapping.getAllFields()) {
        m.persist(SessionManager.currentSession, n, per);
      }

      var newPer = new Persona()

      for (m <- perMapping.getAllFields()) {
        m.hidrate(SessionManager.currentSession, n, newPer);
      }

      Assert.assertNotSame(per, newPer);
      Assert.assertEquals(per, newPer);
    })
  }
   
  @Test
  def testCheto {
    SessionManager.runInSession({
      var cel = new CelularCheto(Modelo.MOTOROLA_1100, "12345", null, 1.25, 0.25);
      var celMapping = Configuration.mappingFor(cel);

      Assert.assertNotNull(celMapping);

      var n = graphDb.createNode();
      for (m <- celMapping.getAllFields()) {
        m.persist(SessionManager.currentSession, n, cel);
      }

      var newCel = new CelularCheto();

      for (m <- celMapping.getAllFields()) {
        m.hidrate(SessionManager.currentSession, n, newCel);
      }

      Assert.assertNotSame(cel, newCel);
      Assert.assertEquals(cel, newCel);
    })
  }
}