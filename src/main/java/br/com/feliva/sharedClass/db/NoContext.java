package br.com.feliva.sharedClass.db;

import jakarta.persistence.EntityManager;

public interface NoContext<T> {

    /**
     * Visivilidade default, somente pacote de DAOS podem ter acesso ao metodo
     * @param em
     */
    T noContext(EntityManager em);

}
