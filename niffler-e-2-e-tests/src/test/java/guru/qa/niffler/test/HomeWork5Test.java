package guru.qa.niffler.test;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.db.dao.impl.AuthUserDAOHibernate;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.extension.DBUserExtension;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;

@ExtendWith(DBUserExtension.class)
public class HomeWork5Test extends BaseWebTest {

    @DBUser
    @Test
    @AllureId("401")
    void dbUserExtensionBeforeTestGetUserWebTest1(AuthUserEntity user) {

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
        });

        step("Имя на странице профиля должно соответствовать логину", () -> {
            Assertions.assertEquals(user.getUsername(), usernameFromGUIElement.getText());
        });
    }
}
