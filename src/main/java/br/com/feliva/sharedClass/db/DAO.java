package br.com.feliva.sharedClass.db;

import java.io.Serializable;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

public abstract class DAO <T extends Model<?>> implements Serializable{

	private static final long serialVersionUID = 22021991L;
	
	@Inject protected EntityManager em;
	@Inject protected Validator validador;
	
	
	public Set<ConstraintViolation<Object>> validate( T isValid){
		return this.validador.validate(isValid);
	} 
	
	public boolean validateAll(@Valid T isValid) throws ConstraintViolationException{
		return true;
	} 

    @SuppressWarnings("unchecked")
    public T findById(T entity){
        return  (T) em.find( entity.getClass(), entity.getMMId() );
    }
    
    public T persist (T entity) throws RollbackException{
    	em.persist( entity );
        return entity;//remover
    }

    public void refresh(T entity) {
        em.refresh( entity );
    }
    
    public T merge (T entity) throws RollbackException{
//    	System.out.println(this.em);
        return em.merge( entity );
    }
    
    public void remove (T entity) throws RollbackException{
		if(!entity.isNovo()) {
			em.remove(em.contains(entity) ? entity : em.merge(entity));
		} else {
			// TODO remover este else
			System.out.println("é novo, method->remove");
		}
    }
    
    public void detach(T entity) throws RollbackException{
    	this.em.detach(entity);
    }
    
    
    @Transactional
    public T persistT (T entity) throws RollbackException{
    	em.persist( entity );
        return entity;//remover
    }

//    @Transactional(value = TxType.REQUIRES_NEW)//retirada em correçã cardapio admin
	@Transactional
    public T updateT (T entity) throws RollbackException{
        return em.merge( entity );
    }
    
    @Transactional
    public void removeT (T entity)throws RollbackException{
		if(!entity.isNovo()) {
			this.em.remove(em.contains(entity) ? entity : em.merge(entity));
		} else {
			// TODO remover este else
			System.out.println("é novo, method->removeT");
		}
    }
}
