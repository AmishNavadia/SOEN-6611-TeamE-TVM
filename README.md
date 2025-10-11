# iGo - PRESTO Ticket Vending Machine

A modern JavaFX-based ticket vending machine (TVM) system for the PRESTO transit payment platform.

## Overview

iGo is a comprehensive ticket vending machine application that provides an intuitive interface for purchasing transit tickets, recharging PRESTO cards, and checking card balances. The system features a beautiful, modern UI optimized for presentation and demonstration purposes, with support for both English and French languages.

## Features

- **Ticket Purchasing**: Buy single, return, day pass, and monthly pass tickets with zone-based fare calculation
- **PRESTO Card Recharge**: Add funds to PRESTO cards using multiple payment methods
- **Balance Checking**: View current PRESTO card balance
- **Multi-language Support**: Full support for English and French (bilingual interface)
- **Payment Processing**: Simulated payment processing for credit cards, debit cards, contactless, and cash
- **Maintenance Mode**: Technician access for system diagnostics and hardware simulation
- **Session Management**: 60-second inactivity timeout for user sessions
- **Transaction Logging**: Comprehensive logging of all transactions
- **Modern UI**: Elegant interface with animations, transitions, and responsive design

## Architecture Design

### System Architecture

The iGo application follows a **layered architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                      │
│                         (JavaFX UI)                         │
│                       IGoApplication.java                   │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┼──────────────────────────────────┐
│                     Service Layer                           │
│  ┌────────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│  │ PaymentService │  │FareCalculator│  │PrestoCardService│  │
│  └────────────────┘  └──────────────┘  └─────────────────┘  │
│  ┌────────────────┐  ┌──────────────┐  ┌─────────────────-┐ │
│  │ TicketPrinter  │  │SessionManager│  │MaintenanceService│ │
│  └────────────────┘  └──────────────┘  └─────────────────-┘ │
│  ┌────────────────────────────────────────────────────────┐ │
│  │         TransactionRepository                          │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┼──────────────────────────────────┐
│                      Model Layer                            │
│  ┌──────────┐  ┌──────────-┐  ┌────────┐  ┌──────────────┐  │
│  │PrestoCard│  │Transaction│  │ Ticket │  │     Fare     │  │
│  └──────────┘  └──────────-┘  └────────┘  └──────────────┘  │
│  ┌──────────┐  ┌──────────┐  ┌────────┐  ┌──────────────┐   │
│  │   Zone   │  │ TripType │  │Payment │  │Transaction   │   │
│  └──────────┘  └──────────┘  └────────┘  └──────────────┘   │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┼──────────────────────────────────┐
│                   Utility & Exception Layer                 │
│  ┌────────────┐  ┌────────────────────────────────────────┐ │
│  │  Logger    │  │         Exception Hierarchy            │ │
│  │  Language  │  │  (IGoException, InvalidCardException)  │ │
│  └────────────┘  └────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
ca.concordia.igo/
├── IGoApplication.java             # Main JavaFX application entry point
├── model/                          # Domain models
│   ├── PrestoCard.java             # PRESTO card entity
│   ├── Transaction.java            # Transaction entity
│   ├── Ticket.java                 # Ticket entity
│   ├── Fare.java                   # Fare information (record)
│   ├── Zone.java                   # Transit zones (record)
│   ├── TripType.java               # Trip type enumeration
│   ├── PaymentMethod.java          # Payment method enumeration
│   ├── TransactionStatus.java      # Transaction status enumeration
│   └── TransactionType.java        # Transaction type enumeration
├── service/                        # Business logic layer
│   ├── PaymentService.java         # Payment processing with simulation
│   ├── FareCalculator.java         # Zone-based fare calculation
│   ├── PrestoCardService.java      # PRESTO card operations & validation
│   ├── TicketPrinter.java          # Ticket printing with bilingual receipts
│   ├── SessionManager.java         # User session timeout management
│   ├── MaintenanceService.java     # System diagnostics & hardware simulation
│   └── TransactionRepository.java  # Transaction logging & retrieval
├── exception/                      # Custom exception hierarchy
│   ├── IGoException.java           # Base exception class
│   ├── InvalidCardException.java   # Invalid card errors with error codes
│   ├── PaymentFailedException.java # Payment processing failures
│   ├── InsufficientFundsException.java  # Insufficient PRESTO balance
│   ├── PrinterUnavailableException.java # Printer hardware failures
│   └── ValidationException.java    # Input validation errors
├── ui/                             # UI components and utilities
│   ├── DialogHelper.java           # Overlay dialogs & toast messages
│   ├── ThemeConstants.java         # Centralized color scheme constants
│   ├── UIComponents.java           # Reusable UI component builders
│   └── screens/                    # Individual screen implementations
│       ├── LanguageScreen.java     # Language selection screen
│       ├── MainMenuScreen.java     # Main navigation menu
│       ├── BuyTicketScreen.java    # Ticket purchase flow
│       ├── RechargeScreen.java     # PRESTO card recharge flow
│       ├── BalanceScreen.java      # Balance inquiry screen
│       ├── MaintenanceLoginScreen.java      # Technician authentication
│       └── MaintenanceDashboardScreen.java  # System diagnostics dashboard
└── util/                           # Utility classes
├── Logger.java                 # Logging utility
└── Language.java               # Language enumeration (EN/FR)
```

### Component Details

#### Presentation Layer (UI)
- **IGoApplication**: Main JavaFX application coordinating services and navigation
- **Screen Classes**: Specialized screens for each user flow
  - `LanguageScreen`: Bilingual welcome screen with language selection
  - `MainMenuScreen`: Main navigation with animated menu tiles
  - `BuyTicketScreen`: Ticket purchase with real-time fare calculation
  - `RechargeScreen`: PRESTO card recharge with quick amount buttons
  - `BalanceScreen`: Card balance inquiry
  - `MaintenanceLoginScreen`: PIN-based technician authentication
  - `MaintenanceDashboardScreen`: System diagnostics and hardware toggles
- **DialogHelper**: Centralized overlay management for processing, success, and error dialogs
- **UIComponents**: Reusable UI component factory (buttons, headers, footers, forms)
- **ThemeConstants**: GO Transit/PRESTO color scheme constants

#### Service Layer
- **PaymentService**: Payment processing with 95% simulated success rate and transaction lifecycle management
- **FareCalculator**: Zone-based fare calculation (base fare $3.25 + 15% per zone)
- **PrestoCardService**: Card validation, recharge operations, and balance checking
- **TicketPrinter**: Bilingual receipt generation with formatted output
- **SessionManager**: 60-second inactivity timeout with JavaFX Timeline
- **MaintenanceService**: PIN authentication (demo: "9999") and hardware status simulation
- **TransactionRepository**: In-memory transaction logging and querying

#### Model Layer
- **PrestoCard**: Mutable card with balance, expiry date, and active status
- **Transaction**: Mutable transaction with status lifecycle (PENDING → AUTHORIZED → COMPLETED/FAILED)
- **Ticket**: Immutable ticket with fare, amount, and validity period
- **Fare**: Immutable record with origin, destination, and trip type
- **Zone**: Immutable record for transit zones (Z1-Toronto, Z2-Mississauga, Z3-Oakville, Z4-Hamilton)
- **Enumerations**: TripType (SINGLE, RETURN, DAY_PASS, MONTHLY_PASS), PaymentMethod, TransactionStatus, TransactionType

#### Exception Handling
- **IGoException**: Base exception with technical messages
- **InvalidCardException**: Card validation failures with error codes (CARD_NOT_DETECTED, CARD_EXPIRED, CARD_INACTIVE, CARD_READ_ERROR)
- **PaymentFailedException**: Payment processing failures with transaction ID tracking
- **InsufficientFundsException**: Balance validation with required/available amounts
- **PrinterUnavailableException**: Non-critical printer errors
- **ValidationException**: Input validation errors
- All exceptions provide bilingual user messages via `getUserMessage(Language)`

### Design Patterns

1. **Layered Architecture**: Clear separation between UI, business logic, and data
2. **Service Pattern**: Business logic encapsulated in stateless service classes
3. **Repository Pattern**: Transaction data access abstraction
4. **Dependency Injection**: Manual DI through constructor in IGoApplication
5. **Record Pattern**: Immutable data objects (Fare, Zone)
6. **Enum Pattern**: Type-safe constants for domains
7. **Factory Pattern**: UI component creation in UIComponents
8. **Observer Pattern**: JavaFX property binding and event handling
9. **State Pattern**: Transaction status lifecycle management
10. **Strategy Pattern**: Payment method handling

### Key Technologies

- **JavaFX 21**: Modern UI framework with animations and effects
- **Java 21**: Latest LTS version with records and pattern matching
- **Maven**: Build and dependency management

## Requirements

- Java 21 or higher
- Maven 3.8+
- JavaFX 21

## Building and Running
### Run the applicaiton
```
mvn javafx:run
```

## Configuration
### Color Scheme(Go Transit/PRESTO)
- Primary Color: #00A651 (GO Transit Green)
- Secondary Color: #003DA5 (PRESTO Blue)
- Accent Color: #FFB81C (Warning Yellow)
- Background: #F5F5F5 (Light Grey)
- Dark Text: #2C3E50 (Dark Blue-Grey)
- Success: #27AE60 (Green)
- Error: #E74C3C (Red)

### Business Rules

- Base Fare: $3.25
- Zone Multiplier: 15% per zone (1.15x)
- Return Trip Discount: 10% (1.8x single fare instead of 2.0x)
- Day Pass: $13.50 (flat rate)
- Monthly Pass: $150.00 (flat rate)
- Session Timeout: 60 seconds of inactivity
- Payment Success Rate: 95% (simulated)
- Card Recharge Limits: $0.01 minimum, $1000.00 maximum per transaction
- Card Validity: 5 years from issuance

### Predefined Transit Zones

- Zone 1 (Z1): Toronto
- Zone 2 (Z2): Mississauga
- Zone 3 (Z3): Oakville
- Zone 4 (Z4): Hamilton

### Maintenance Access

- Technician PIN: 9999 (demo purposes only)
- Features:
  - System diagnostics (Network, Printer, NFC Reader status)
  - Hardware simulation toggles (Printer, Network)
  - Recent transaction viewing (last 10 transactions)

### Demo PRESTO Cards
The system includes pre-populated demo cards for testing:

- `1234567890`: Balance $25.00
- `9876543210`: Balance $5.50
- `5555555555`: Balance $0.00

## Project Structure
```
iGo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ca/concordia/igo/
│   │   │       ├── IGoApplication.java
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       ├── ui/
│   │   │       │   ├── screens/
│   │   │       │   ├── DialogHelper.java
│   │   │       │   ├── ThemeConstants.java
│   │   │       │   └── UIComponents.java
│   │   │       └── util/
│   │   └── resources/
│   │       └── images/
│   │           ├── ticket.png
│   │           ├── presto-card.png
│   │           └── tool.png
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```
## User Flows
1. Buy Ticket Flow

Select language (English/French)
Choose "Buy Ticket" from main menu
Select origin zone
Select destination zone
Choose trip type (Single/Return/Day Pass/Monthly Pass)
Select payment method
View calculated fare amount
Confirm purchase
Process payment (2-second simulation)
View success overlay with formatted receipt
Option to print receipt
Return to main menu

2. Recharge PRESTO Card Flow

Select "Recharge PRESTO" from main menu
Tap/enter PRESTO card number
Select recharge amount (quick buttons: $10, $20, $50, $100 or custom)
Select payment method
Confirm recharge
Process payment
Update card balance
View new balance in success dialog
Return to main menu

3. Check Balance Flow

Select "Check Balance" from main menu
Tap/enter PRESTO card number
View current balance with animated display
Return to main menu

4. Maintenance Flow

Select "Maintenance" from main menu
Enter technician PIN (9999)
View system diagnostics dashboard
Toggle printer/network status for testing
View recent transaction log
Return to main menu

Error Handling
The system provides comprehensive error handling with bilingual messages:

Invalid Card: Card not detected, expired, or inactive
Insufficient Funds: Insufficient balance for fare
Payment Failed: Payment declined or network error
Printer Unavailable: Receipt printing failure (transaction still saved)
Validation Errors: Invalid input amounts or missing fields
Session Timeout: Automatic return to main menu after 60s inactivity

All errors display user-friendly messages in the selected language with recovery instructions.
Internationalization (i18n)
The application supports English and French with:

Complete UI translation
Bilingual error messages
Formatted receipts in both languages
Language toggle available on every screen
Persistent language selection throughout session