package uqbar.arena.persistence.testDomain

class ButtonAction(page: PageLoca, name: String, action: () => Unit) {
  override def toString = name
}

abstract class Page {
  def setup(b: ButtonAction) {
    println(b)
  }
}

class PageLoca extends Page {
  val buttonAnular = new ButtonAction(this, "anular", { () => this.entradaAnular() })

  def entradaAnular() {

  }

  setup(buttonAnular)
}