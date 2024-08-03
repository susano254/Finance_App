package com.example.finance.Transactions;

import com.example.finance.Users.User;
import com.example.finance.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;



    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();


        List<Transaction> currentTransactions = transactionService.getTransactions();

        List<Transaction> userTransactions = new ArrayList<>();
        // get transactions with the same user id as the current user
        userTransactions = currentTransactions.stream().filter(transaction -> Objects.equals(transaction.getUser().getId(), currentUser.getId())).toList();

        return userTransactions;
    }

    @GetMapping("/transactions/all")
    public List<Transaction> getAllTransactions() {
        return transactionService.getTransactions();
    }

    @GetMapping("/transactions/{transactionId}")
    public Transaction getTransaction(@PathVariable String transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    @PostMapping("/transactions")
    public void addTransaction(@RequestBody Transaction transaction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        transaction.setUser(currentUser);

        transactionService.addTransaction(transaction);
    }

    @PostMapping("/transactions/{userId}")
    public void addTransaction(@PathVariable String userId, @RequestBody Transaction transaction) {
        // Get the user with the given id from the database
        User user = userService.allUsers().stream().filter(u -> u.getId().equals(Long.parseLong(userId))).findFirst().orElse(null);
        if(user == null) {
            return;
        }

        // get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();


        // if user is also the current user then return
        if(user.getId().equals(currentUser.getId())) {
            return;
        }

        transaction.setUser(currentUser);

        double transactionAmount = Double.parseDouble(transaction.getTransactionAmount());
        double currentUserBalance = currentUser.getBalance();

        if(transaction.getTransactionType().equals("Expense")) {
            if(currentUserBalance < transactionAmount) {
                return;
            }
            currentUser.setBalance(currentUserBalance - transactionAmount);
            userService.updateUser(currentUser);

            // update the user balance
            user.setBalance(user.getBalance() + transactionAmount);
            userService.updateUser(user);
        }

        transactionService.addTransaction(transaction);

        // add the transaction to the user's transactions with Income type
        Transaction incomeTransaction = new Transaction("Income", transaction.getTransactionDate(), transaction.getTransactionAmount(), transaction.getTransactionDescription());
        incomeTransaction.setUser(user);
        transactionService.addTransaction(incomeTransaction);
    }

    @PutMapping("/transactions/{transactionId}")
    public void updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        transactionService.updateTransaction(transactionId, transaction);
    }

    @DeleteMapping("/transactions/{transactionId}")
    public void deleteTransaction(@PathVariable String transactionId) {
        transactionService.deleteTransaction(transactionId);
    }
}
