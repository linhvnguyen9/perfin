package com.linh.perfin.domain.usecase.account

import com.linh.perfin.domain.repository.account.AccountRepository

class GetAccountByIdUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(id: String) = accountRepository.getAccountById(id)
}