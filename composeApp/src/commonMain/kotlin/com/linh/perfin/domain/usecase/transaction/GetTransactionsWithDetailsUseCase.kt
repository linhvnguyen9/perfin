package com.linh.perfin.domain.usecase.transaction

import com.linh.perfin.domain.model.category.Category
import com.linh.perfin.domain.model.transaction.TransactionWithDetails
import com.linh.perfin.domain.repository.account.AccountRepository
import com.linh.perfin.domain.repository.category.CategoryRepository
import com.linh.perfin.domain.repository.transaction.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Use case to get transactions with resolved account and category details for UI display
 */
class GetTransactionsWithDetailsUseCase(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) {
    /**
     * Returns a Flow of transactions with resolved details including:
     * - Account name from account ID
     * - Category name, icon, and color from transaction categories
     */
    operator fun invoke(): Flow<List<TransactionWithDetails>> {
        return combine(
            transactionRepository.getAllTransactions(),
            categoryRepository.getAllCategories()
        ) { transactions, categories ->
            
            // Get all accounts once for lookup
            val accounts = try {
                accountRepository.getAllAccounts()
            } catch (e: Exception) {
                emptyList()
            }
            
            // Create lookup maps for efficient access
            val accountMap = accounts.associateBy { it.id }
            val categoryMap = categories.associateBy { it.id }
            
            // Transform transactions to include resolved details
            transactions.map { transaction ->
                val account = accountMap[transaction.accountId]
                val accountName = account?.name ?: "Unknown Account"
                
                // For now, we'll use the transaction description to infer category
                // TODO: Implement proper transaction-category relationship lookup
                val inferredCategory = inferCategoryFromDescription(transaction.description, categories)
                
                TransactionWithDetails(
                    transaction = transaction,
                    accountName = accountName,
                    categoryName = inferredCategory?.name,
                    categoryIcon = inferredCategory?.icon,
                    categoryColor = inferredCategory?.color
                )
            }
        }
    }
    
    /**
     * Temporary method to infer category from transaction description
     * This will be replaced with proper transaction-category relationship lookup
     */
    private fun inferCategoryFromDescription(description: String, categories: List<Category>): Category? {
        val descriptionLower = description.lowercase()
        
        return categories.find { category ->
            val categoryLower = category.name.lowercase()
            descriptionLower.contains(categoryLower) ||
            categoryLower.contains(descriptionLower) ||
            // Check common keywords
            when (categoryLower) {
                "food", "restaurant" -> descriptionLower.contains("restaurant") || 
                                     descriptionLower.contains("food") || 
                                     descriptionLower.contains("lunch") ||
                                     descriptionLower.contains("dinner") ||
                                     descriptionLower.contains("chipotle") ||
                                     descriptionLower.contains("mcdonald")
                "transport", "transportation" -> descriptionLower.contains("uber") || 
                                               descriptionLower.contains("taxi") ||
                                               descriptionLower.contains("transport") ||
                                               descriptionLower.contains("gas") ||
                                               descriptionLower.contains("fuel")
                "shopping" -> descriptionLower.contains("amazon") || 
                             descriptionLower.contains("shop") ||
                             descriptionLower.contains("store")
                "bills", "utilities" -> descriptionLower.contains("bill") || 
                                       descriptionLower.contains("phone") ||
                                       descriptionLower.contains("verizon") ||
                                       descriptionLower.contains("utility")
                "salary", "income" -> descriptionLower.contains("salary") || 
                                     descriptionLower.contains("wage") ||
                                     descriptionLower.contains("income")
                "investment" -> descriptionLower.contains("dividend") || 
                              descriptionLower.contains("stock") ||
                              descriptionLower.contains("investment") ||
                              descriptionLower.contains("aapl")
                "coffee" -> descriptionLower.contains("coffee") ||
                           descriptionLower.contains("starbucks") ||
                           descriptionLower.contains("cafe")
                else -> false
            }
        }
    }
}