package com.linh.perfin.domain.usecase

import com.linh.perfin.domain.model.BankInVietnam

// https://vietqr.net/portal-service/download/documents/QR_Format_T&C_v1.0_VN_092021.pdf
class GenerateVietQrCodeUseCase {
    operator fun invoke(recipientAccount: String, bank: BankInVietnam, amount: String? = null, transactionMessage: String? = null): String {
        val dataVersion = generateDataVersionSection()
        val isDynamic = amount != null
        val qrType = generateQrTypeSection(isDynamic = isDynamic)
        val paymentVendorId = generatePaymentVendorId(recipientAccount, bank)
        val transactionCurrency = generateQrCodeSection("53", "704") // 704 for VND
        val transactionAmount = amount?.let { generateQrCodeSection("54", it) }.orEmpty()
        val countryCode = generateQrCodeSection("58", "VN")
        val transactionNameSection = transactionMessage?.let { generateQrCodeSection("62", generateQrCodeSection("08", it)) }.orEmpty()

        val dataWithoutCrc = dataVersion + qrType + paymentVendorId + transactionCurrency + transactionAmount + countryCode + transactionNameSection + "6304"
        val crcValue = calculateCRC16(dataWithoutCrc)
        val crc = "6304$crcValue"

        return dataVersion + qrType + paymentVendorId + transactionCurrency + transactionAmount + countryCode + transactionNameSection + crc
    }

    private fun generatePaymentVendorId(recipientAccount: String, bank: BankInVietnam): String {
        val guiId = generateQrCodeSection("00", "A000000727")
        val recipientId = generateQrCodeSection("01", generateRecipientId(recipientAccount, bank))
        val serviceCode = generateQrCodeSection("02", "QRIBFTTA") // TODO: supports QRIBFTTC for card transfer
        return generateQrCodeSection("38", guiId + recipientId + serviceCode)
    }

    private fun generateRecipientId(recipientAccount: String, bank: BankInVietnam): String {
        val acquierId = generateQrCodeSection("00", bank.binCode)
        val consumerId = generateQrCodeSection("01", recipientAccount.uppercase())
        return acquierId + consumerId
    }

    private fun generateDataVersionSection(): String = generateQrCodeSection("00",   "01")

    private fun generateQrTypeSection(isDynamic: Boolean) = generateQrCodeSection("01", if (isDynamic) "12" else "11")

    private fun generateQrCodeSection(id: String, data: String): String {
        return id + data.length.toString().padStart(2, '0') + data
    }

    private fun calculateCRC16(input: String): String {
        val bytes = input.encodeToByteArray()
        var crc = 0xFFFF
        val polynomial = 0x1021

        for (byte in bytes) {
            crc = crc xor ((byte.toInt() and 0xFF) shl 8)
            repeat(8) {
                crc = if ((crc and 0x8000) != 0) {
                    (crc shl 1) xor polynomial
                } else {
                    crc shl 1
                }
            }
        }

        return (crc and 0xFFFF).toString(16).uppercase().padStart(4, '0')
    }
}