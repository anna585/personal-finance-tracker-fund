# Personal Finance Tracker

## Overview

Personal Finance Tracker is a web application built with Spring Boot that helps users manage their personal finances. The application allows users to track income and expenses, create monthly budgets, and monitor saving goals in a simple and intuitive dashboard.

The project was developed as part of the Spring Fundamentals course.

---

## Features

### Authentication

* User registration
* User login

### Transactions

* Add new transactions
* View transaction history
* Delete transactions
* Categorize transactions as income or expense

### Budget Management

* Create monthly budgets
* Update budget limits
* Track remaining monthly budget

### Saving Goals

* Create saving goals
* Monitor progress toward financial goals

### Dashboard

* Current balance overview
* Monthly income summary
* Monthly expense summary
* Remaining budget calculation
* Recent transactions overview

### Administration

* View registered users
* Manage users
* View application reports and statistics

---

## Technologies Used

### Backend

* Java 22
* Spring Boot 3
* Spring MVC
* Spring Data JPA
* Hibernate

### Frontend

* Thymeleaf
* HTML5
* CSS3

### Database

* MySQL

### Build Tool

* Maven

---

## Database Structure

### User

Stores user account information and roles (USER/ADMIN).

### Transaction

Stores income and expense records, transaction type and category.

### Budget

Stores monthly budget information.

### SavingGoal

Stores user saving goals.

---

## Application Architecture

The application follows a layered architecture:

* Controllers
* Services
* Repositories
* Entities
* DTOs
* Mappers

This structure separates business logic from presentation and data access layers.

---

## Main Business Logic

### Current Balance

The current balance is calculated dynamically using:

Current Balance = Total Income − Total Expenses

### Remaining Budget

Remaining Budget = Monthly Budget − Monthly Expenses

### Saving Goals

Users can create financial goals.

### Role Management

The application supports two user roles:

- USER – manages personal finances
- ADMIN – manages users and system reports

---

## Future Improvements

* Transaction editing
* Charts and financial analytics
* Export reports to PDF
* Email notifications
* Spring Security authentication

---

## Author

Developed by Anna as a Spring Fundamentals course project.
