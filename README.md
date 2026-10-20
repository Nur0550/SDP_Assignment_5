# Assignment 5: Strategy Pattern — Car Rental System

## Domain Overview
This project implements an enterprise **Car Rental Management System** designed around the **Strategy Pattern** (GoF). The system addresses two core domain operations that require interchangeable algorithms selected dynamically at runtime:
1. **Family 1: Rental Pricing (`RentalPricingStrategy`)** — Computes customer billing based on different algorithmic structures (mileage-capped daily rates, tiered subscription volume models, external demand/hazard-adjusted pricing).
2. **Family 2: Vehicle Allocation (`VehicleAllocationStrategy`)** — Matches requested reservations to fleet inventory based on distinct optimization goals (budget-minimizing economy, carbon-minimizing eco-friendly electric priority, loyalty-driven VIP tier upgrade).

---

## 1. Class Responsibilities Table (Single Responsibility Principle)
*In strict accordance with Clean Code (Ch. 10, slides 38–39): Each responsibility is expressed in exactly one sentence, under 25 words, without the conjunctions `if`, `and`, `or`, `but`.*

| Class / Interface | Responsibility (1 sentence, < 25 words, no `if`/`and`/`or`/`but`) | Word Count |
| :--- | :--- | :--- |
| `Customer` | Encapsulates customer identity, loyalty status, along with environmental preferences. | 9 |
| `CustomerTier` | Enumerates customer loyalty levels. | 4 |
| `Vehicle` | Represents physical vehicle specifications, pricing, emissions, availability status. | 8 |
| `VehicleCategory` | Enumerates vehicle market classification categories. | 5 |
| `FuelType` | Enumerates vehicle engine propulsion fuel types. | 6 |
| `RentalBooking` | Represents an immutable reservation with temporal constraints, route distances. | 9 |
| `RentalCriteria` | Defines search constraints for filtering potential fleet vehicles. | 8 |
| `DemandMetrics` | Holds market surge multipliers, weather alerts, risk telemetry data. | 9 |
| `MarketDemandProvider` | Defines the contract for retrieving real-time market telemetry. | 8 |
| `SimulatedMarketDemandProvider` | Generates simulated calendar-based market demand metrics. | 6 |
| `FakeMarketDemandProvider` | Provides deterministic fake demand metrics for unit testing. | 8 |
| `RentalPricingStrategy` | Defines the algorithm contract for calculating rental prices. | 8 |
| `StandardDailyPricingStrategy` | Calculates rental charges using base rates, mileage limits, weekend fees. | 10 |
| `LongTermSubscriptionPricingStrategy` | Calculates rental charges applying tiered volume discounts plus maintenance credits. | 10 |
| `DynamicDemandPricingStrategy` | Calculates rental charges scaled by external market demand metrics. | 9 |
| `RentalPricingContext` | Maintains a pricing strategy reference, delegating execution directly to it. | 10 |
| `VehicleAllocationStrategy` | Defines the contract for matching vehicles to customer criteria. | 9 |
| `EconomyFirstAllocationStrategy` | Selects available vehicles minimizing daily rates plus fuel consumption. | 9 |
| `EcoFriendlyAllocationStrategy` | Selects available vehicles prioritizing zero emission electric powertrains. | 8 |
| `LoyaltyTierAllocationStrategy` | Selects available vehicles prioritizing luxury comfort score upgrades. | 8 |
| `VehicleAllocationContext` | Maintains an allocation strategy reference, delegating execution directly to it. | 10 |
| `PricingStrategySelector` | Selects appropriate pricing strategies based on booking parameters. | 8 |
| `VehicleAllocationStrategySelector` | Selects appropriate vehicle allocation strategies based on customer preferences. | 9 |
| `CarRentalDemo` | Executes console demonstrations displaying strategy operations, selection, runtime swapping. | 9 |

---

## 2. Architectural Analysis: Why Strategy (and Not State or Template Method)?

The Strategy pattern was chosen for this architecture because the business requirements demand interchangeable, independent algorithms that can be swapped dynamically at runtime through composition without coupling the context to concrete implementations.

### Strategy vs. State Pattern
While the Strategy and State patterns share similar UML class diagrams (both rely on a context delegating to an interface), their intent, lifecycle, and coupling are fundamentally distinct:
1. **Transition Autonomy vs. Independence:** In the **State pattern**, concrete states represent the internal status of an entity (e.g., `DraftBooking` $\rightarrow$ `ConfirmedBooking` $\rightarrow$ `CompletedBooking`). State transitions are typically triggered internally by state instances themselves based on lifecycle events. In contrast, in our **Strategy pattern** implementation, concrete strategies (`StandardDailyPricingStrategy`, `DynamicDemandPricingStrategy`) are completely independent, stateless algorithmic engines. They have zero awareness of each other, never trigger transitions between themselves, and can be invoked repeatedly in any arbitrary sequence.
2. **Context Ownership:** A State context changes its own state as it progresses through a business lifecycle. In Strategy, the client or an external `Selector` configures the context (`RentalPricingContext`) with the appropriate algorithmic strategy from the outside.

### Strategy vs. Template Method Pattern
Template Method and Strategy both allow algorithm customization, but achieve it through opposing mechanisms:
1. **Composition over Inheritance:** **Template Method** relies on class inheritance (`extends`), defining an invariant algorithmic skeleton in an abstract class while letting subclasses override specific primitive step hooks. This binds the algorithm to compile-time inheritance hierarchies and makes runtime algorithm replacement impossible for an existing instance.
2. **Runtime Flexibility and Open/Closed Principle:** In our **Strategy** design, algorithmic families are defined behind clean interfaces (`RentalPricingStrategy`, `VehicleAllocationStrategy`). New algorithms (e.g., `CorporateFleetPricingStrategy`, `ElectricSharedVehicleAllocationStrategy`) can be added without modifying a single line of existing context or client code, strictly satisfying the Open/Closed Principle (OCP). Furthermore, our contexts provide `setStrategy(...)`, enabling live switching of the active algorithm on existing context instances during execution—a capability strictly outside the scope of Template Method.

---

## 3. Git Refactoring History
As mandated in Section 2 (Requirement 4), Family 1 was initially implemented using a monolithic switch-case structure and subsequently refactored into the clean Strategy pattern with identical test pass rates on both commits:

- **Before Refactoring Commit:**
  - **Commit Hash:** `9352ada`
  - **Message:** `Initial monolithic implementation of rental pricing with switch/if-else`
  - **Description:** Encapsulated standard, long-term, and dynamic demand pricing in a single monolithic `RentalPriceCalculator` with conditional branching. Verified with unit tests in `RentalPricingRefactoringTest`.
- **After Refactoring Commit:**
  - **Commit Hash:** `156c09b`
  - **Message:** `Refactor rental pricing to Strategy pattern with clean Context and strategies`
  - **Description:** Extracted monolithic cases into `RentalPricingStrategy`, `StandardDailyPricingStrategy`, `LongTermSubscriptionPricingStrategy`, `DynamicDemandPricingStrategy`, and `RentalPricingContext`. The exact same test suite passed without regression.

---

## 4. Isolating from Change (Clean Code Ch. 10)
In accordance with slides 42–43 of Lecture 7, `DynamicDemandPricingStrategy` relies on volatile external telemetry (market surge multipliers, adverse weather warnings, regional hazard indices). This volatility is isolated behind the `MarketDemandProvider` interface:
- **Production / Demo:** `SimulatedMarketDemandProvider` produces calendar-aware seasonal and weather metrics.
- **Unit Testing:** `FakeMarketDemandProvider` acts as a test double, supplying deterministic, fixed metrics so that tests remain fast, robust, and repeatable.

---

## 5. How to Build, Test, and Run

### Running Tests (Zero Dependencies)
A cross-platform standalone script and bundled JUnit 5 runner are included in the repository:
```bash
python build_and_test.py
```
*(Executes all 29 tests across JUnit Jupiter with 100% success rate).*

### Running with Standard Maven
```bash
mvn clean test
```

### Running the Live Console Demo
```bash
python build_and_test.py
java -cp target/classes com.carrental.demo.CarRentalDemo
```
*Or via Maven:*
```bash
mvn compile exec:java -Dexec.mainClass="com.carrental.demo.CarRentalDemo"
```

---

## 6. Test Suite Summary
- **Strategies Tested:** All 6 strategies tested across both families (`StandardDailyPricingStrategy`, `LongTermSubscriptionPricingStrategy`, `DynamicDemandPricingStrategy`, `EconomyFirstAllocationStrategy`, `EcoFriendlyAllocationStrategy`, `LoyaltyTierAllocationStrategy`).
- **Selectors Tested:** All rules in `PricingStrategySelector` (duration $\ge 7$ days, demand surge $> 1.15$, weather warning, default fallback) and `VehicleAllocationStrategySelector` (eco preferences, VIP/GOLD tier upgrades, economy default).
- **Parameterized Invariants:**
  - `ParameterizedPricingTest`: verifies price non-negativity and monotonic duration scaling across all pricing strategies.
  - `ParameterizedAllocationTest`: verifies vehicle availability, seating capacity, and budget compliance across all allocation strategies.
