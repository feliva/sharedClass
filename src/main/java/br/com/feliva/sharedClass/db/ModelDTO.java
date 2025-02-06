package br.com.feliva.sharedClass.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Transient;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

@SuppressWarnings("rawtypes") 
public abstract class ModelDTO<T> implements Serializable {

    @JsonIgnore
    public abstract T getMMId();

    @Override
    public int hashCode() {
        return Objects.hashCode(getMMId());
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
        if (!(obj instanceof ModelDTO)) {
            return false;
        }
        ModelDTO other = (ModelDTO) obj;
        return Objects.equals(getMMId(), other.getMMId());
    }

}
