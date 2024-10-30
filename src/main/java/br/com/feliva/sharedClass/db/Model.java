package br.com.feliva.sharedClass.db;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlTransient;

@SuppressWarnings("rawtypes") 
public abstract class Model<T> implements Serializable {

    private static final long serialVersionUID = 22021991L;

    public static final String SEPARATIOR_KEY = "@";

    @JsonIgnore
    public abstract T getMMId();

	@JsonIgnore//fastxml
	@Transient
	public boolean isNovo() {
		return getMMId() == null;
	}

    @Transient
    @JsonIgnore
    public String getKeyConverter() {
        return this.getClass().getName().hashCode() + SEPARATIOR_KEY + this.getMMId().toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMMId());
    }

    @Transient
    @JsonIgnore
    public Class getClassParametized() {
    	if(this.getMMId() != null) {
    		return this.getMMId().getClass();
    	}
    	Class<?> generic = (Class<?>)  ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//    	System.out.println(generic.toString());
    	return generic;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        // se o id for algum tipo de int precisa verificar o tipo do objeto, mas se for somente uuid nao precisaria
        if (!(obj instanceof Model)) {
            return false;
        }
        Model other = (Model) obj;
        return Objects.equals(getMMId(), other.getMMId());
    }

}
