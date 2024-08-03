package com.example.finance.Transactions;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;


    public Transaction getTransaction(String transactionId) {
        return transactionRepository.findById(transactionId).orElse(null);
    }

    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        transactionRepository.findAll().forEach(transactions::add);
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void updateTransaction(String transactionId, Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void deleteTransaction(String transactionId) {
        transactionRepository.deleteById(transactionId);
    }


}
