Automated Web Testing Pipeline: Selenium, JUnit 5 & Formal QA Design

📌 Project Overview

This project implements an automated web testing pipeline for the SauceDemo e-commerce application using Java, Selenium WebDriver, and JUnit 5.

The project focuses on automating the end-to-end purchase journey and applying software testing techniques such as Equivalence Class Partitioning (ECP), Boundary Value Analysis (BVA), and Control Flow Graph (CFG) analysis.

Target Application: https://www.saucedemo.com/

Subject: Software Testing and Quality Assurance (ISE2500)

🎯 Project Objectives

- Automate the end-to-end shopping and checkout workflow.
- Test authentication using valid and invalid credentials.
- Verify product selection and shopping cart operations.
- Apply ECP and BVA to derive test inputs.
- Analyze validation logic using Control Flow Graphs.
- Automate test execution using Selenium and JUnit 5.

🛠️ Technologies Used

Technology| Purpose
Java 17| Programming language
Selenium WebDriver 4.25.0| Browser automation
JUnit 5.11.0| Test framework
Maven| Dependency management
WebDriverManager 5.9.2| Browser driver management
Eclipse IDE| Development environment
Git and GitHub| Version control

🏗️ Test Architecture – Page Object Model (POM)

The project follows the Page Object Model design pattern to separate page actions from test logic.

saucedemo-automation/
│
├── src/
│   ├── main/java/com/saucedemo/
│   │   └── SauceDemoApp.java
│   │       ├── BasePage
│   │       ├── LoginPage
│   │       ├── InventoryPage
│   │       ├── CartPage
│   │       ├── CheckoutPage
│   │       ├── FieldValidator
│   │       └── CheckoutCalculator
│   │
│   └── test/java/com/saucedemo/
│       └── SauceDemoTests.java
│
├── presentation/
│   └── SauceDemo_Automation_Presentation.pptx
│
├── pom.xml
├── .gitignore
└── README.md

🧪 Test Cases Implemented

The automation suite contains 10 test cases:

No.| Test Case| Description
1| Successful Login| Verify login using valid credentials
2| Invalid Login| Verify rejection of invalid credentials
3| Add Product| Verify adding a product to the cart
4| Open Cart| Verify cart navigation and contents
5| Valid Checkout| Verify checkout with valid customer information
6| Missing Checkout Information| Validate required checkout fields
7| Quantity Validation| Verify quantity boundaries
8| Name Validation| Validate customer name input
9| Postal Code Validation| Validate postal code input
10| Checkout Tax Calculation| Verify checkout tax calculation

📊 Software Testing Techniques

1. Equivalence Class Partitioning (ECP)

Inputs are divided into valid and invalid groups.

Examples:

- Quantity: 1–6 valid; below 1 or above 6 invalid.
- First name: 1–30 characters valid; empty or over 30 invalid.
- Postal code: exactly 5 digits valid.

2. Boundary Value Analysis (BVA)

Boundary values are tested to identify errors at the limits.

Field| Boundary Values| Expected Result
Quantity| 0, 1, 2, 3, 5, 6, 7| 1–6 valid
Name length| 0, 1, 2, 15, 29, 30, 31| 1–30 valid
Postal code length| 4, 5, 6| Only 5 valid

3. Control Flow Graph (CFG)

CFG analysis is applied to the "validateName()" method.

The validation checks:

- Null input.
- Empty input.
- Name length greater than 30 characters.
- Valid name input.

Cyclomatic Complexity:

VG = E − N + 2P

VG = 11 − 9 + 2(1)

Cyclomatic Complexity = 4

Four independent basis paths are identified.

📈 Test Execution Results

The presentation reports the following execution results:

Metric| Result
Total Tests| 10
Passed| 10
Failed| 0
Errors| 0
Skipped| 0
Execution Duration| 32.3 seconds

🚀 How to Run the Project

Prerequisites

- Java JDK 17 or later.
- Eclipse IDE.
- Maven.
- Google Chrome browser.

Installation

Clone the repository:

git clone https://github.com/zaid18-PU/saucedemo-automation.git

Run Using Eclipse

1. Open Eclipse IDE.
2. Import the project as an Existing Maven Project.
3. Wait for Maven dependencies to download.
4. Open "SauceDemoTests.java".
5. Right-click and select Run As → JUnit Test.

Run Using Maven

Execute:

mvn clean test

🔑 Test Credentials

SauceDemo demo credentials:

- Username: "standard_user"
- Password: "secret_sauce"

👨‍💻 Team Members

Mohammed Zaid

- Student A – Automation Architect
- Project setup and WebDriver lifecycle.
- Page Object classes and reusable actions.
- Explicit waits and browser automation.

Ankit A Hegde

- Student B – Test Designer
- ECP and BVA test matrices.
- Control Flow Graph and basis path analysis.
- JUnit 5 test design.

📂 Project Resources

- Source Code: Available in this repository.
- Presentation: Available in the "presentation/" folder.
- Setup Guide: Available in this README.

🔗 GitHub Repository

https://github.com/zaid18-PU/saucedemo-automation

---

Developed as part of Software Testing and Quality Assurance (ISE2500).
