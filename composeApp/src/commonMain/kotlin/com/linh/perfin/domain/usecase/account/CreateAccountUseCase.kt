package com.linh.perfin.domain.usecase.account

import com.linh.perfin.domain.model.account.Account
import com.linh.perfin.domain.repository.account.AccountRepository

class CreateAccountUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(account: Account) {
        accountRepository.createAccount(account)
    }
}