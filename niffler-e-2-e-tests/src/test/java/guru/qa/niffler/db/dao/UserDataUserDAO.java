package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public interface UserDataUserDAO {
    static UserDataUserDAO getInstance() {
        UserDataUserDAO dao;
        if ("hibernate".equals(System.getProperty("db.impl"))) {
            dao = new AuthUserDAOHibernate();
        } else if ("spring".equals(System.getProperty("db.impl"))) {
            dao = new AuthUserDAOSpringJdbc();
        } else {
            dao = new AuthUserDAOJdbc();
        }
        return dao;
    }

    int createUserInUserData(UserEntity user);

    void deleteUserByIdInUserData(UUID userId);

    void deleteUserByNameInUserData(String username);
}
