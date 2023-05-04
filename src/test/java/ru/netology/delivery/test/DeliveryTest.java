package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.util.ScreenShooterReportPortalExtension;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.delivery.util.LoggingUtils.logInfo;

@ExtendWith({ScreenShooterReportPortalExtension.class})
 class DeliveryTest {

    @BeforeEach
    void setup() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $("[data-test-id='city'] input").setValue(validUser.getCity());
        logInfo("В поле введен город " + validUser.getCity());
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] input").doubleClick().setValue(firstMeetingDate);
        logInfo("В поле введена дата " + firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        logInfo("В поле введены ФИО " + validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        logInfo("В поле введен номер телефона " + validUser.getPhone());
        $("[data-test-id='agreement'] span").click();
        logInfo("Отмечено согласие в чек-боксе");
        $x("//*[contains(text(), 'Запланировать')]").click();
        logInfo("Нажатие кнопки 'Запланировать'");
        $("[data-test-id='success-notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate));
        logInfo("Встреча успешно запланирована на " + firstMeetingDate);

        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        logInfo("Стерта дата из поля ввода.");
        $("[data-test-id='date'] input").doubleClick().setValue(secondMeetingDate);
        logInfo("В поле введена дата " + secondMeetingDate);
        $x("//*[contains(text(), 'Запланировать')]").click();
        logInfo("Нажатие кнопки 'Запланировать'");
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldBe(visible)
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        logInfo("Предложение перепланировать дату встречи.");

        $(By.className("button")).click();
        $x("//span[contains(text(), 'Перепланировать')]").click();
        logInfo("Нажатие кнопки перепланирования даты встречи.");
        $("[data-test-id='success-notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate));
        logInfo("Встреча успешно запланирована на " + secondMeetingDate);
    }

}