package br.com.feliva.sharedClass.db;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
//import org.wildfly.security.http.oidc.OidcPrincipal;
//
//import br.edu.iffar.fw.classBag.sec.OidcPrincipalContext;
//import jakarta.faces.context.FacesContext;
//import jakarta.servlet.http.HttpServletRequest;

public class HInterceptor implements Interceptor {

    private static final long serialVersionUID = 22021991L;

    private StringBuilder sql = new StringBuilder();

    public HInterceptor() {
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        String sql = "INSERT INTO public.log (tupla_id, entidade, operacao, usuario_id, dt_log)" + "VALUES('" + id
                + "', '" + entity.getClass().getName()
                + "', 'onSave(insert)', (select u.usuario_id from usuarios u where u.username ='" + this.getUser()
                + "'), CURRENT_TIMESTAMP);\n";

        this.sql.append(sql);
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        String sql = "INSERT INTO public.log (tupla_id, entidade, operacao, usuario_id, dt_log)" + "VALUES('" + id
                + "', '" + entity.getClass().getName()
                + "', 'onDelete(delite)', (select u.usuario_id from usuarios u where u.username ='" + this.getUser()
                + "'), CURRENT_TIMESTAMP);\n";
        this.sql.append(sql);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {
        String sql = "INSERT INTO public.log (tupla_id, entidade, operacao, usuario_id, dt_log)" + "VALUES('" + id
                + "', '" + entity.getClass().getName()
                + "', 'onFlushDirty(Update)', (select u.usuario_id from usuarios u where u.username ='"
                + this.getUser() + "'), CURRENT_TIMESTAMP);\n";
        this.sql.append(sql);

        return false;
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        try {
            //conteiner gerencia
            Connection connection = ConnectProducer.getConnection();

            Statement s = connection.createStatement();
            s.execute(sql.toString());
            s.close();
            connection.close();

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        this.sql = new StringBuilder();
    }

    public String getUser() {
//        FacesContext fc = FacesContext.getCurrentInstance();
//        OidcPrincipal<?> oidcPrincipal = null;
//
//        if(fc == null) {
//            //webservice
//            oidcPrincipal = OidcPrincipalContext.get();
//        }else {
//            HttpServletRequest r = (HttpServletRequest) fc.getExternalContext().getRequest();
//            oidcPrincipal = (OidcPrincipal<?>) r.getUserPrincipal();
//        }
//
//        if (oidcPrincipal == null) {
            return "TODO";
//        } else {
//            return oidcPrincipal.getOidcSecurityContext().getIDToken().getPreferredUsername();
//        }
    }
}