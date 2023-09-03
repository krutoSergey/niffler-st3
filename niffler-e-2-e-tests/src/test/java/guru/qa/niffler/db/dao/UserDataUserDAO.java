package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.dao.impl.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.impl.AuthUserDAOSpringJdbc;
import guru.qa.niffler.db.dao.impl.UserdataUserDAOHibernate;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;

public interface UserDataUserDAO {
    static UserDataUserDAO getInstance() {
        UserDataUserDAO dao;
        if ("hibernate".equals(System.getProperty("db.impl"))) {
            dao = new UserdataUserDAOHibernate();
        } else if ("spring".equals(System.getProperty("db.impl"))) {
            dao = new AuthUserDAOSpringJdbc();
        } else if ("jdbc".equals(System.getProperty("db.impl"))){
            dao = new AuthUserDAOJdbc();
        } else {
            dao = new UserdataUserDAOHibernate();
        }
        return dao;
    }

    int createUserInUserData(UserDataUserEntity user);

//    void deleteUserByIdInUserData(UUID userId);

    void deleteUserByUsernameInUserData(String username);
}
