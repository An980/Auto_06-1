package ru.netology.test;

import com.codeborne.selenide.Configuration;
import lombok.val;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;

class MoneyTransferTest {

    @BeforeEach
    void login() {
        open("http://localhost:9999");
        Configuration.holdBrowserOpen = true;
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.login(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.verify(verificationCode);
    }


    @Test
    void valueTransferFirstToSecond() {
        int value = 6300;
        String cardNumber = String.valueOf(DataHelper.getSecondCardNumber());
        val dashboardPage = new DashboardPage();
        var firstCardBalance = dashboardPage.getFirstCardBalance();
        var secondCardBalance = dashboardPage.getSecondCardBalance();
        dashboardPage.transferButtonSecondToFirst();

        val transferPage = new TransferPage();
        transferPage.transferData(value, cardNumber);
        var balanceOne = dashboardPage.getFirstCardBalance();
        var balanceTwo = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(secondCardBalance - value, balanceTwo);
        Assertions.assertEquals(firstCardBalance + value, balanceOne);

    }

    @Test
    void valueTransferSecondToFirst() {
        int value = 4700;
        String cardNumber = String.valueOf(DataHelper.getFirstCardNumber());
        val dashboardPage = new DashboardPage();
        var firstCardBalance = dashboardPage.getFirstCardBalance();
        var secondCardBalance = dashboardPage.getSecondCardBalance();
        dashboardPage.transferButtonFirstToSecond();

        val transferPage = new TransferPage();
        transferPage.transferData(value, cardNumber);
        var balanceOne = dashboardPage.getFirstCardBalance();
        var balanceTwo = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(firstCardBalance - value, balanceOne);
        Assertions.assertEquals(secondCardBalance + value, balanceTwo);

    }

    @Test
    void maxValueTransfer() {
        String cardNumber = String.valueOf(DataHelper.getSecondCardNumber());
        val dashboardPage = new DashboardPage();
        var balanceOne = dashboardPage.getFirstCardBalance();
        var balanceTwo = dashboardPage.getSecondCardBalance();
        dashboardPage.transferButtonSecondToFirst();

        val transferPage = new TransferPage();
        transferPage.transferData(balanceTwo, cardNumber);
        var balanceFinal = dashboardPage.getFirstCardBalance() + dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(balanceOne + balanceTwo, balanceFinal);
    }


    @Test
    void valueTransferAboveLimit() {
        int value = 300;
        String cardNumber = String.valueOf(DataHelper.getSecondCardNumber());
        val dashboardPage = new DashboardPage();
        var balanceTwo = dashboardPage.getSecondCardBalance();
        dashboardPage.transferButtonSecondToFirst();

        val transferPage = new TransferPage();
        transferPage.transferData(value + balanceTwo, cardNumber);
        transferPage.getNotification();
    }

}
