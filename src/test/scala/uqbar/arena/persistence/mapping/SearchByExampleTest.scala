package uqbar.arena.persistence.mapping

import java.util.Date
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import uqbar.arena.persistence.AbstractArenaPersistenceTest
import uqbar.arena.persistence.SessionManager
import uqbar.arena.persistence.testDomain.Celular
import uqbar.arena.persistence.testDomain.Modelo
import uqbar.arena.persistence.testDomain.Persona
import java.util.ArrayList
import scala.collection.JavaConverters._

class SearchByExampleTest extends AbstractArenaPersistenceTest {

  var cel: Celular = new Celular(Modelo.MOTOROLA_1100, "12345", null, 1.25)
  var cel2: Celular = new Celular(Modelo.MOTOROLA_1100, "44444", null, 1.25)
  var cel3: Celular = new Celular(Modelo.MOTOROLA_1100, "55555", null, 2.25)
  var cel4: Celular = new Celular(Modelo.MOTOROLA_1100, "66666", null, 2.25)
  var cel5: Celular = new Celular(Modelo.IPHONE, "66666", null, 2.25)

  @Before
  def crearFixtureComun() {
    SessionManager.runInSession({
      SessionManager.currentSession.save(cel)
      SessionManager.currentSession.save(cel2)
      SessionManager.currentSession.save(cel3)
      SessionManager.currentSession.save(cel4)
      SessionManager.currentSession.save(cel5)
    })
  }

  @Test
  def testCelular() {
    var result: List[Celular] = null

    SessionManager.runInSession({
      val example = new Celular(Modelo.MOTOROLA_1100, null, null, null)
      result = SessionManager.currentSession.searchByExample(example)
    })

    Assert.assertEquals(4, result.size)
    Assert.assertTrue(result.contains(cel))
    Assert.assertTrue(result.contains(cel2))
    Assert.assertTrue(result.contains(cel3))
    Assert.assertTrue(result.contains(cel4))

    SessionManager.runInSession({
      val example = new Celular(Modelo.MOTOROLA_1100, null, null, 1.25)
      result = SessionManager.currentSession.searchByExample(example)
    })

    Assert.assertEquals(2, result.size)
    Assert.assertTrue(result.contains(cel))
    Assert.assertTrue(result.contains(cel2))

    SessionManager.runInSession({
      val example = new Celular(null, null, null, 2.25)
      result = SessionManager.currentSession.searchByExample(example)
    })

    Assert.assertEquals(3, result.size)
    Assert.assertTrue(result.contains(cel3))
    Assert.assertTrue(result.contains(cel4))
    Assert.assertTrue(result.contains(cel5))
  }

  @Test(expected = classOf[Exception])
  def busquedaByExampleCelularPorDuenio() {
    var result: List[Celular] = null

    val pepe = new Persona("Pepe", "Montero", new Date(2, 1, 91), 393)
    val celu1Pepe = new Celular(Modelo.MOTOROLA_1100, "12345", pepe, 1.25)
    val celu2Pepe = new Celular(Modelo.MOTOROLA_1100, "12345", pepe, 1.25)
    val celuAmaia = new Celular(Modelo.MOTOROLA_1100, "12345", new Persona("Amaia", "Montero", new Date(2, 2, 80), 552), 1.25)
    SessionManager.runInSession({
      SessionManager.currentSession.save(celu1Pepe)
      SessionManager.currentSession.save(celu2Pepe)
      SessionManager.currentSession.save(celuAmaia)
    })

    SessionManager.runInSession({
      val example = new Celular(null, null, new Persona("Pepe", null, null, 0), null)
      result = SessionManager.currentSession.searchByExample(example)
    })

    //    Cuando ande la búsqueda by example hay que descomentar estos tests
    //    Assert.assertEquals(2, result.size)
    //    Assert.assertTrue(result.contains(celu1Pepe))
    //    Assert.assertTrue(result.contains(celu2Pepe))
  }

  @Test
  def busquedaByExamplePorColeccionVaciaDebeFuncionar() {
    var result: List[Persona] = null

    val pepe = new Persona("Pepe", "Montero", new Date(2, 1, 91), 393)
    val celu1Pepe = new Celular(Modelo.MOTOROLA_1100, "12345", pepe, 1.25)
    val celu2Pepe = new Celular(Modelo.MOTOROLA_1100, "12345", pepe, 1.25)
    pepe.setCelulares(List(celu1Pepe, celu2Pepe).asJava)
    SessionManager.runInSession({
      SessionManager.currentSession.save(pepe)
    })

    SessionManager.runInSession({
      val example = new Persona(null, null, null, 0)
      example.setCelulares(new ArrayList())
      result = SessionManager.currentSession.searchByExample(example)
    })

    Assert.assertEquals(1, result.size)
  }

}
