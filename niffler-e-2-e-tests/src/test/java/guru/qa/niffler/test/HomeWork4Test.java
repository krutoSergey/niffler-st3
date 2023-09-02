package guru.qa.niffler.test;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.db.dao.AuthUserDAOSpringJdbc;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.DBUser;
import guru.qa.niffler.jupiter.DbUserExtension;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;

@ExtendWith(DbUserExtension.class)
public class HomeWork4Test extends BaseWebTest {

    @DBUser
    @Test
    @AllureId("401")
    void dbUserExtensionBeforeTestGetUserWebTest1(UserEntity user) {
        final AtomicReference<String> usernameFromGUI = new AtomicReference<>();
        final SelenideElement usernameFromGUIElement = $(".avatar-container");
        AuthUserDAOSpringJdbc authUserDAO = new AuthUserDAOSpringJdbc();

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

        step("Изменить пароль пользователя через запрос в БД", () -> {
            String newPwd = "123456";
            user.setPassword(newPwd);
            authUserDAO.updateUser(user);
        });

        step("Проверить запросом в БД, что пароль у пользователя обновился", () -> {
            UserEntity getUser = authUserDAO.getUserById(user.getId());
            Assertions.assertEquals(user.getPassword(), getUser.getPassword());
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
    }
}
