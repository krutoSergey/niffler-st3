package guru.qa.niffler.test;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.DBUser;
import guru.qa.niffler.jupiter.Dao;
import guru.qa.niffler.jupiter.DaoExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;

@ExtendWith(DaoExtension.class)
public class HomeWork3DbTest extends BaseWebTest {

    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

//    private UserEntity userForTest;
//
//    @BeforeEach
//    void beforeEach(UserEntity user) {
//        this.userForTest = user;
//        System.out.println(userForTest.getUsername());
//    }

    @DBUser(username = "hw3test06", password = "12345")
    @Test
    void dbUserExtensionBeforeTestGetUserWebTest1(UserEntity user) {
        final AtomicReference<String> usernameFromGUI = new AtomicReference<>();
        final SelenideElement usernameFromGUIElement = $(".avatar-container");


        step(String.format("Залогиниться под юзером %s", user.getUsername()), () -> {
            Selenide.open("http://127.0.0.1:3000/main");
            $("a[href*='redirect']").click();
            $("input[name='username']").setValue(user.getUsername());
            $("input[name='password']").setValue(user.getPassword());
            $("button[type='submit']").click();
            $(".main-content__section-stats").should(visible);
        });

        step("Перейти на страницу профиля", () -> {
            $(Selectors.byAttribute("href", "/profile")).click();
            usernameFromGUIElement.shouldBe(visible);
            usernameFromGUI.set(usernameFromGUIElement.getText());
        });

        step("Имя на странице профиля должно соответствовать логину", () -> {
            Assertions.assertEquals(user.getUsername(), usernameFromGUI.get());
        });

        step("Изменить имя пользователя через запрос в БД", () -> {
            String newName = user.getUsername() + "_1";
            user.setUsername(newName);
            authUserDAO.updateUser(user);
        });

        step("Проверить запросом в БД, что имя у пользователя обновилось", () -> {
            UserEntity getUser = authUserDAO.getUserById(user.getId());
            Assertions.assertEquals(user.getUsername(), getUser.getUsername());
        });

        step(String.format("Залогиниться под юзером %s", user.getUsername()), () -> {
            Selenide.closeWindow();
            Selenide.open("http://127.0.0.1:3000/main");
            $("a[href*='redirect']").click();
            $("input[name='username']").setValue(user.getUsername());
            $("input[name='password']").setValue(user.getPassword());
            $("button[type='submit']").click();
            $(".main-content__section-stats").should(visible);
        });

        step("Перейти на страницу профиля", () -> {
            $(Selectors.byAttribute("href", "/profile")).click();
            usernameFromGUIElement.shouldBe(visible);
            usernameFromGUI.set(usernameFromGUIElement.getText());
        });

        step("Имя на странице профиля должно соответствовать логину", () -> {
            Assertions.assertEquals(user.getUsername(), usernameFromGUI.get());
        });
    }
}
