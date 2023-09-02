package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.util.TestDataGenerator;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class DbUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    private static final AuthUserDAO authUserDAO = AuthUserDAO.getInstance();
    private static final UserDataUserDAO userDataUserDAO = UserDataUserDAO.getInstance();
    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DbUserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        DBUser annotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);
        UserEntity userEntity = new UserEntity();
        if(annotation != null){
            userEntity.setUsername(annotation.username().equals("") ? TestDataGenerator.createLogin() : annotation.username());
            userEntity.setPassword(annotation.password().equals("") ? TestDataGenerator.createPassword() : annotation.password());
            userEntity.setEnabled(true);
            userEntity.setAccountNonLocked(true);
            userEntity.setAccountNonExpired(true);
            userEntity.setCredentialsNonExpired(true);
            userEntity.setAuthorities(Arrays.stream(Authority.values()).map(
                    authority -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(authority);
                        return ae;
                    }
            ).toList());

            authUserDAO.createUser(userEntity);
            userDataUserDAO.createUserInUserData(userEntity);
            context.getStore(NAMESPACE).put(getAllureId(context), userEntity); //Предполагаем, что разные потоки не будут гонять один тест
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(getAllureId(extensionContext), UserEntity.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        UserEntity userFromTest = context.getStore(NAMESPACE).get(getAllureId(context), UserEntity.class);
        authUserDAO.deleteUserById(userFromTest.getId());
        userDataUserDAO.deleteUserByNameInUserData(userFromTest.getUsername());
    }

    private String getAllureId(ExtensionContext context) {
        AllureId allureId = context.getRequiredTestMethod().getAnnotation(AllureId.class);
        if (allureId == null) {
            throw new IllegalStateException("Annotation AllureId must be present");
        }

        return allureId.value();
    }
}