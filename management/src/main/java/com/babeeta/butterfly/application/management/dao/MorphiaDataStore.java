package com.babeeta.butterfly.application.management.dao;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

public class MorphiaDataStore {

	private Mongo mongo;

	private String dbName;

	private String mappingPackage;

	public Datastore getDataStore() {

		Morphia morphia = new Morphia().mapPackage(getMappingPackage());

		Datastore ds = morphia.createDatastore(getMongo(), getDbName());

		// creates indexes from @Index annotations in your entities
		ds.ensureIndexes();
		// creates capped collections from @Entity
		ds.ensureCaps();

		return ds;
	}

	public String getDbName() {
		return dbName;
	}

	public String getMappingPackage() {
		return mappingPackage;
	}

	public Mongo getMongo() {
		return mongo;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setMappingPackage(String mappingPackage) {
		this.mappingPackage = mappingPackage;
	}

	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}

}
