package uqbar.arena.persistence.testDomain;

import uqbar.arena.persistence.annotations.PersistentField;

public class CelularCheto extends Celular {
	private static final long serialVersionUID = 1L;
	@PersistentField private Double bonus;

	public CelularCheto() {
	}

	public CelularCheto(Modelo modelo, String numero, Persona duenio,
			Double precioPorMinuto,Double bonus) {
		super(modelo,numero,duenio,precioPorMinuto);
		this.bonus = bonus;
	}

	public Double getBonus() {
		return bonus;
	}

	public void setBonus(Double bonus) {
		this.bonus = bonus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bonus == null) ? 0 : bonus.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CelularCheto other = (CelularCheto) obj;
		if (bonus == null) {
			if (other.bonus != null)
				return false;
		} else if (!bonus.equals(other.bonus))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + " - bonus: " + bonus; 
	}
	
}
