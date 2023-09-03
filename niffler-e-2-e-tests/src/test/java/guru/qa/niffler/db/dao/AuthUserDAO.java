package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.dao.impl.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.impl.AuthUserDAOSpringJdbc;
import guru.qa.niffler.db.dao.impl.UserdataUserDAOHibernate;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public interface AuthUserDAO {
    static AuthUserDAO getInstance() {
        AuthUserDAO dao;
        if ("hibernate".equals(System.getProperty("db.impl"))) {
            dao = new AuthUserDAOHibernate();
        } else if ("spring".equals(System.getProperty("db.impl"))) {
            dao = new AuthUserDAOSpringJdbc();
        } else if ("jdbc".equals(System.getProperty("db.impl"))){
            dao = new AuthUserDAOJdbc();
        } else {
            dao = new AuthUserDAOHibernate();
        }
        return dao;
    }
    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    int createUser(AuthUserEntity user);

    AuthUserEntity updateUser(AuthUserEntity user);

    void deleteUser(AuthUserEntity userId);

    AuthUserEntity getUserById(UUID userId);
}
