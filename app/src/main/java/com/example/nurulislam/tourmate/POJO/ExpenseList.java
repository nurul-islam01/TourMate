package com.example.nurulislam.tourmate.POJO;

public class ExpenseList {
    private String expComment;
    private double amount;
    private String expenseId;

    public ExpenseList(String expComment, double amount, String expenseId) {
        this.expComment = expComment;
        this.amount = amount;
        this.expenseId = expenseId;
    }

    public ExpenseList() {
    }

    public String getExpComment() {
        return expComment;
    }

    public double getAmount() {
        return amount;
    }

    public String getExpenseId() {
        return expenseId;
    }
}
