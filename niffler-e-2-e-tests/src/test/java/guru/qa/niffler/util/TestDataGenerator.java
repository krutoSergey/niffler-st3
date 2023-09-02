package guru.qa.niffler.util;

import com.github.javafaker.Faker;

public class TestDataGenerator {
    private static final Faker faker = new Faker();

    public static String createLogin(){
        return faker.name().firstName();
    }

    public static String createPassword(){
        return faker.internet().password();
    }
}