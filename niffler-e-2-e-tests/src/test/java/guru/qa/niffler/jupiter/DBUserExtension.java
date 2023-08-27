package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class DBUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    private static final AuthUserDAO authUserDAO = new AuthUserDAOJdbc(); //FIXME хотелось бы не хардкодить конкретную реализацию
    private static final UserDataUserDAO userDataUserDAO = new AuthUserDAOJdbc(); //FIXME хотелось бы не хардкодить конкретную реализацию
    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);


    @Override
    public void beforeEach(ExtensionContext context) {
        DBUser annotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);
        if (annotation != null) {
            UserEntity user = new UserEntity();
            user.setUsername(annotation.username());
            user.setPassword(annotation.password());
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setAuthorities(Arrays.stream(Authority.values())
                    .map(authority -> {
                        var ae = new AuthorityEntity();
                        ae.setAuthority(authority);
                        return ae;
                    }).toList()
            );
            context.getStore(NAMESPACE).put("user", user);

            authUserDAO.createUser(user);
            userDataUserDAO.createUserInUserData(user);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        UserEntity user = context.getStore(NAMESPACE).get("user", UserEntity.class);
        userDataUserDAO.deleteUserByUsernameInUserData(user.getUsername());
        authUserDAO.deleteUserByIdInAuth(user.getId());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(DBUserExtension.NAMESPACE).get("user", UserEntity.class);
    }
}
