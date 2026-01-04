package com.linh.perfin.domain.model.usecase

import com.linh.perfin.domain.model.BankInVietnam
import com.linh.perfin.domain.usecase.GenerateVietQrCodeUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class GenerateVietQrCodeUseCaseTest {
    val sut = GenerateVietQrCodeUseCase()

    @Test
    fun givenTechcombankWithoutAmountAndTransactionMessage_whenGenerateQrCode_thenReturnsCorrectQrCodeData() {
        val bank = BankInVietnam.TECHCOMBANK
        val accountName = "testacc123"

        val expected = "00020101021138540010A000000727012400069704070110TESTACC1230208QRIBFTTA53037045802VN63046F66"
        val actual = sut(recipientAccount = accountName, bank = bank)

        assertEquals(expected, actual)
    }

    @Test
    fun givenVietcombankWithAmountAndTransactionMessage_whenGenerateQrCode_thenReturnsCorrectQrCodeData() {
        val bank = BankInVietnam.VIETCOMBANK
        val accountName = "testacc123"
        val amount = "10000"
        val transactionMessage = "hello world"

        val expected = "00020101021238540010A000000727012400069704360110TESTACC1230208QRIBFTTA53037045405100005802VN62150811hello world630456AD"

        val actual = sut(recipientAccount = accountName, bank = bank, amount = amount, transactionMessage = transactionMessage)

        assertEquals(expected, actual)
    }

    @Test
    fun givenHsbcWithAmount_whenGenerateQrCode_thenReturnsCorrectQrCodeData() {
        val bank = BankInVietnam.HSBC
        val accountName = "0943823878973939334"
        val amount = "10000"

        val expected = "00020101021238630010A00000072701330006458761011909438238789739393340208QRIBFTTA53037045405100005802VN6304C42D"

        val actual = sut(recipientAccount = accountName, bank = bank, amount = amount)

        assertEquals(expected, actual)
    }

    @Test
    fun givenKookminHnWithMessage_whenGenerateQrCode_thenReturnsCorrectQrCodeData() {
        val bank = BankInVietnam.KOOKMIN_HN
        val accountName = "KOOKMINTEST123"
        val message = "asdfasdfxzcvzxcv"

        val expected = "00020101021138580010A000000727012800069704620114KOOKMINTEST1230208QRIBFTTA53037045802VN62200816asdfasdfxzcvzxcv630411DB"

        val actual = sut(recipientAccount = accountName, bank = bank, transactionMessage = message)

        assertEquals(expected, actual)
    }
}