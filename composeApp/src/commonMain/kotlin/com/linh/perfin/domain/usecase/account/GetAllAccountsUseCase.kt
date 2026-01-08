package com.linh.perfin.domain.usecase.account

import com.linh.perfin.domain.model.account.Account
import com.linh.perfin.domain.repository.account.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetAllAccountsUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke() = accountRepository.getAllAccounts()
    fun observe(): Flow<List<Account>> = accountRepository.observeAllAccounts()
}