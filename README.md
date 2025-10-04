# iGo - PRESTO Ticket Vending Machine

A modern JavaFX-based ticket vending machine (TVM) system for the PRESTO transit payment platform.

## Overview

iGo is a comprehensive ticket vending machine application that provides an intuitive interface for purchasing transit tickets, recharging PRESTO cards, and checking card balances. The system features a beautiful, modern UI optimized for presentation and demonstration purposes, with support for both English and French languages.

## Features

- **Ticket Purchasing**: Buy single, return, and day pass tickets with zone-based fare calculation
- **PRESTO Card Recharge**: Add funds to PRESTO cards using multiple payment methods
- **Balance Checking**: View current PRESTO card balance
- **Multi-language Support**: Full support for English and French
- **Payment Processing**: Simulated payment processing for credit cards, debit cards, and cash
- **Maintenance Mode**: Technician access for system diagnostics and configuration
- **Session Management**: 30-second timeout for user sessions
- **Transaction Logging**: Comprehensive logging of all transactions

## Architecture Design

### System Architecture

The iGo application follows a **layered architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│                         (JavaFX UI)                          │
│                       IGoUI.java                             │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┼──────────────────────────────────┐
│                     Service Layer                            │
│  ┌────────────────┐  ┌──────────────┐  ┌─────────────────┐ │
│  │ PaymentService │  │FareCalculator│  │PrestoCardService│ │
│  └────────────────┘  └──────────────┘  └─────────────────┘ │
│  ┌────────────────┐  ┌──────────────┐  ┌─────────────────┐ │
│  │TicketPrinter  │  │SessionManager│  │MaintenanceService│ │
│  └────────────────┘  └──────────────┘  └─────────────────┘ │
│  ┌────────────────────────────────────────────────────────┐ │
│  │         TransactionRepository                          │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┼──────────────────────────────────┐
│                      Model Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌────────┐  ┌──────────────┐  │
│  │PrestoCard│  │Transaction│  │ Ticket │  │     Fare     │  │
│  └──────────┘  └──────────┘  └────────┘  └──────────────┘  │
│  ┌──────────┐  ┌──────────┐  ┌────────┐  ┌──────────────┐  │
│  │   Zone   │  │ TripType │  │PaymentM│  │TransactionSta│  │
│  └──────────┘  └──────────┘  └────────┘  └──────────────┘  │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┼──────────────────────────────────┐
│                   Utility & Exception Layer                  │
│  ┌────────────┐  ┌────────────────────────────────────────┐ │
│  │  Logger    │  │         Exception Hierarchy            │ │
│  │  Language  │  │  (IGoException, InvalidCardException)  │ │
│  └────────────┘  └────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
ca.concordia.igo/
├── IGoUI.java                      # Main JavaFX application entry point
├── model/                          # Domain models
│   ├── PrestoCard.java             # PRESTO card entity
│   ├── Transaction.java            # Transaction entity
│   ├── Ticket.java                 # Ticket entity
│   ├── Fare.java                   # Fare information
│   ├── Zone.java                   # Transit zones
│   ├── TripType.java               # Trip type enumeration
│   ├── PaymentMethod.java          # Payment method enumeration
│   └── TransactionStatus.java      # Transaction status enumeration
├── service/                        # Business logic layer
│   ├── PaymentService.java         # Payment processing
│   ├── FareCalculator.java         # Fare calculation logic
│   ├── PrestoCardService.java      # PRESTO card operations
│   ├── TicketPrinter.java          # Ticket printing simulation
│   ├── SessionManager.java         # User session management
│   ├── MaintenanceService.java     # System maintenance operations
│   └── TransactionRepository.java  # Transaction persistence
├── exception/                      # Custom exceptions
│   ├── IGoException.java           # Base exception
│   ├── InvalidCardException.java   # Invalid card error
│   ├── PaymentFailedException.java # Payment failure error
│   ├── InsufficientFundsException.java
│   ├── PrinterUnavailableException.java
│   └── ValidationException.java
└── util/                           # Utility classes
    ├── Logger.java                 # Logging utility
    └── Language.java               # Language enumeration
```

### Component Details

#### Presentation Layer
- **IGoUI**: Main JavaFX application class handling all UI screens and user interactions
- Implements modern, responsive UI with animations and transitions
- Screens: Language Selection, Main Menu, Buy Ticket, Recharge PRESTO, Check Balance, Maintenance Dashboard

#### Service Layer
- **PaymentService**: Processes payments with 95% simulated success rate
- **FareCalculator**: Zone-based fare calculation (base fare + zone multiplier)
- **PrestoCardService**: Manages PRESTO card operations and validation
- **TicketPrinter**: Simulates ticket printing with receipt generation
- **SessionManager**: 30-second session timeout management
- **MaintenanceService**: System diagnostics and configuration
- **TransactionRepository**: Transaction logging and retrieval

#### Model Layer
- **PrestoCard**: Card number, balance, expiry date, active status
- **Transaction**: Transaction ID, type, amount, status, payment method
- **Ticket**: Fare details, amount, validity period
- **Fare**: Origin, destination, trip type
- **Enumerations**: Zone, TripType, PaymentMethod, TransactionStatus

#### Exception Handling
- Custom exception hierarchy for domain-specific errors
- Bilingual error messages (English/French)
- User-friendly error reporting

### Design Patterns

1. **Layered Architecture**: Clear separation between UI, business logic, and data
2. **Service Pattern**: Business logic encapsulated in service classes
3. **Repository Pattern**: Transaction data access abstraction
4. **Singleton-like Services**: Stateful services managed by UI controller
5. **Enum Pattern**: Type-safe constants for domains (Zone, TripType, etc.)
6. **Builder Pattern**: Complex UI component construction
7. **Observer Pattern**: JavaFX property binding and event handling

### Key Technologies

- **JavaFX 21**: Modern UI framework
- **Java 21**: Latest LTS version with modern language features
- **Maven**: Build and dependency management
- **ControlsFX**: Enhanced JavaFX controls
- **FormsFX**: Form validation and handling
- **ValidatorFX**: Input validation
- **Ikonli**: Icon library integration
- **BootstrapFX**: Bootstrap-inspired styling
- **TilesFX**: Dashboard tiles and gauges
- **FXGL**: Game and graphics library

## Requirements

- Java 21 or higher
- Maven 3.8+
- JavaFX 21

## Building and Running

### Build the project
```bash
mvn clean install
```

### Run the application
```bash
mvn javafx:run
```

### Run tests
```bash
mvn test
```

## Configuration

### Color Scheme
- Primary Color: `#00A651` (GO Transit Green)
- Secondary Color: `#003DA5` (PRESTO Blue)
- Accent Color: `#FFB81C` (Warning Yellow)
- Background: `#F5F5F5` (Light Grey)

### Business Rules
- Base Fare: $3.25
- Zone Multiplier: 1.15
- Return Trip Discount: 10% (1.8x single fare)
- Day Pass: $13.50
- Session Timeout: 30 seconds
- Payment Success Rate: 95% (simulated)

### Maintenance Access
- Technician PIN: Configured in MaintenanceService
- Features: System diagnostics, printer toggle, network toggle, transaction log viewing

## Project Structure

```
iGo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ca/concordia/igo/
│   │   │       ├── IGoUI.java
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       ├── exception/
│   │   │       └── util/
│   │   └── resources/
│   │       └── images/
│   │           ├── ticket.png
│   │           └── presto-card.png
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## Use Cases

1. **Buy Single Ticket**: Select origin/destination zones, choose payment method, complete purchase
2. **Buy Return Ticket**: Similar to single ticket with 10% discount
3. **Buy Day Pass**: Fixed price unlimited travel
4. **Recharge PRESTO Card**: Tap card, select amount, process payment
5. **Check Balance**: Tap card to view current balance
6. **Maintenance Mode**: Technician login, view diagnostics, configure system

## Future Enhancements

- Integration with real PRESTO card readers
- Database persistence for transactions
- Network connectivity for real-time card validation
- Multi-screen kiosk support
- Accessibility features (screen reader, high contrast mode)
- Mobile app integration
- Real-time transit information
- Receipt email/SMS options

## Contributors

Developed at Concordia University

## License

All rights reserved.
