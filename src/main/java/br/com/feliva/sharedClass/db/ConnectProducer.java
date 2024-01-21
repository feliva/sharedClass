package br.com.feliva.sharedClass.db;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//import org.omnifaces.cdi.Eager;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;


//@Eager
@ApplicationScoped
public class ConnectProducer implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@PersistenceUnit(name = "authUnit")
	private static EntityManagerFactory emBU;

	@PersistenceContext // (unitName = "baseUnit")
    @Produces
	private static EntityManager em;
	
	@Resource(mappedName = "java:jboss/datasources/baseDS") // same JNDI used by Hibernate Persistence Unit
	private static DataSource dss;
	

	public void close(@Disposes Connection com) {
		try {
			if(!com.isClosed()) {
				com.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Produces
	public static Connection getConnection() throws SQLException {
		if(dss == null) {   
	        try {
	            Context ctx = new InitialContext();
	            dss = (DataSource) ctx.lookup("java:jboss/datasources/baseDS");
	        }catch (NamingException e) {
	            e.printStackTrace();
	        }
	    }
		
		
		return dss.getConnection();
	}
}