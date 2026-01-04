package com.linh.perfin.domain.usecase.account

import com.linh.perfin.domain.repository.account.AccountRepository

class GetAllAccountsUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke() = accountRepository.getAllAccounts()
}