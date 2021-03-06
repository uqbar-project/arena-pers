package uqbar.arena.persistence.testDomain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.uqbar.commons.model.Entity;

import uqbar.arena.persistence.annotations.PersistentClass;
import uqbar.arena.persistence.annotations.PersistentField;
import uqbar.arena.persistence.annotations.Relation;

@PersistentClass
public class Persona extends Entity {
	private static final long serialVersionUID = 1L;

	@Relation private List<Celular> celulares;
	@PersistentField private String nombre;
	@PersistentField private String apellido;
	@PersistentField private Date fechaNacimiento;
	@PersistentField private int legajo;
	@Relation private Celular preferido;
	
	public Persona() {
		celulares = new ArrayList<Celular>();
	}

	public Persona(String nombre, String apellido, Date fechaNacimiento,
			int legajo) {
		this();
		this.nombre = nombre;
		this.apellido = apellido;
		this.fechaNacimiento = fechaNacimiento;
		this.legajo = legajo;
	}

	public List<Celular> getCelulares() {
		return celulares;
	}

	public void setCelulares(List<Celular> celulares) {
		this.celulares = celulares;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public int getLegajo() {
		return legajo;
	}

	public void setLegajo(int legajo) {
		this.legajo = legajo;
	}

	public Celular getPreferido() {
		return preferido;
	}

	public void setPreferido(Celular preferido) {
		this.preferido = preferido;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((apellido == null) ? 0 : apellido.hashCode());
		result = prime * result
				+ ((celulares == null) ? 0 : celulares.hashCode());
		result = prime * result
				+ ((fechaNacimiento == null) ? 0 : fechaNacimiento.hashCode());
		result = prime * result + legajo;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Persona other = (Persona) obj;
		if (apellido == null) {
			if (other.apellido != null)
				return false;
		} else if (!apellido.equals(other.apellido))
			return false;
		if (fechaNacimiento == null) {
			if (other.fechaNacimiento != null)
				return false;
		} else if (!fechaNacimiento.equals(other.fechaNacimiento))
			return false;
		if (legajo != other.legajo)
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

	public String getNombreCompleto() {
		StringBuffer result = new StringBuffer();
		if (nombre != null) {
			result.append(nombre);
		}
		if (apellido != null) {
			if (result.length() > 0) {
				result.append(" ");
			}
			result.append(apellido);	
		}
		return result.toString();
	}
	
}
