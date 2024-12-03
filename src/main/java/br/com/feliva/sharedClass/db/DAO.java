package br.com.feliva.sharedClass.db;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

public abstract class DAO <M extends Model<?>> implements Serializable{

	private static final long serialVersionUID = 22021991L;

	@Inject protected EntityManager em;
	@Inject protected Validator validador;
	
	public Set<ConstraintViolation<Object>> validate( M isValid){
		return this.validador.validate(isValid);
	} 
	
	public boolean validateAll(@Valid M isValid) throws ConstraintViolationException{
		return true;
	} 

    @SuppressWarnings("unchecked")
    public M findById(Integer id){
        return  (M) em.find(
                (Class<?>)((ParameterizedType)((Class<?>) getClass().getGenericSuperclass()).getGenericSuperclass()).getActualTypeArguments()[0], id );
    }

    @SuppressWarnings("unchecked")
    public M findById(Model<?> entity){
        return  (M) em.find( entity.getClass(), entity.getMMId() );
    }

    @SuppressWarnings("unchecked")
    public M findById(String id){
        return  (M) em.find(
                (Class<?>)((ParameterizedType)((Class<?>) getClass().getGenericSuperclass()).getGenericSuperclass()).getActualTypeArguments()[0], id );
    }

    public <T> void persist (T entity) throws RollbackException{
    	em.persist( entity );
    }
    
    public <T> void merge (T entity) throws RollbackException{
        em.merge(entity);
    }
    
    public void remove (Model<?> entity) throws RollbackException{
		if(!entity.isNovo()) {
			em.remove(em.contains(entity) ? entity : em.merge(entity));
		} else {
			System.out.println("é novo, method->remove");
		}
    }

    @Transactional
    public void persistT (M entity) throws RollbackException{
    	this.persist(entity);
    }

	@Transactional
    public <T> void mergeT(T entity) throws RollbackException{
        this.merge(entity);
    }
    
    @Transactional
    public void removeT (M entity)throws RollbackException{
		this.remove(entity);
    }
}
