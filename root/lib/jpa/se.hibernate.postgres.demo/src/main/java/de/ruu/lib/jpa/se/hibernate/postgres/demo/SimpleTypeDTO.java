package de.ruu.lib.jpa.se.hibernate.postgres.demo;

import de.ruu.lib.jpa.core.AbstractDTO;
import de.ruu.lib.jpa.core.Entity;

class SimpleTypeDTO extends AbstractDTO<SimpleTypeEntity> implements SimpleType
{
	private final String name;

	SimpleTypeDTO(String name) { this.name = name; }

	// neuer Hilfskonstruktor, der id/version übernimmt
	SimpleTypeDTO(String name, Entity<Long> source)
	{
		this(name);
		// innerhalb der DTO-Klasse Zugriff auf geschützte Methode erlaubt
		mapIdAndVersion(source);
	}

	@Override public String name() { return name; }

	public SimpleTypeEntity toSource()
	{
		SimpleTypeEntity entity = new SimpleTypeEntity(name);
		// übernimmt id/version aus dem DTO in die Entity
		entity.mapIdAndVersionFrom(this);
		return entity;
	}
}