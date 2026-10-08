# AI Agent Instructions for Pinodesk

> **This file is the source of truth for all AI instruction files.** When updating coding standards or patterns, edit this file first, then update all IDE-specific instruction files listed below.

## Quick Links to IDE Instructions

| IDE/Tool | Instruction File |
|----------|-----------------|
| Trae | [`.trae/rules/project-rules.md`](.trae/rules/project-rules.md) |
| VSCode/GitHub Copilot | [`.github/copilot-instructions.md`](.github/copilot-instructions.md) |
| Windsurf | [`.windsurfrules`](.windsurfrules) |
| Antigravity | [`.antigravity-rules`](.antigravity-rules) |
| JetBrains AI Assistant | [`.aiassistant/rules/pinodesk-project-rules.md`](.aiassistant/rules/pinodesk-project-rules.md) |
| Kiro | [`.kiro/kiro-rules.md`](.kiro/kiro-rules.md) |
| Cursor | [`.cursorrules`](.cursorrules) |

---

## Project Overview

**Pinodesk** is a free, open-source, desktop-based Point of Sale (POS) system designed for retail businesses. It's a Java desktop application built for managing retail operations including sales, purchases, inventory, customers, suppliers, and reporting.

### Technology Stack

| Layer | Technology |
|-------|------------|
| **Language** | Java 21 |
| **UI Framework** | JavaFX 21 with FXML |
| **Dependency Injection** | Spring Framework 6.0.7 |
| **Data Access** | Spring Data JDBC |
| **Database** | H2 Embedded (MySQL mode) |
| **Migrations** | Flyway 9.16.3 |
| **Build Tool** | Maven |

---

## Build Commands (Use script.sh)

**IMPORTANT:** Always use `./script.sh` wrapper instead of direct `./mvnw` commands. The script ensures quality checks run automatically.

```bash
# Build only
./script.sh build

# Run application
./script.sh run

# Run all quality checks + tests (PMD, Spotless, Tests)
./script.sh test

# Fix formatting automatically
./script.sh fix

# Clean build
./script.sh clean

# Direct Maven (not recommended - use script.sh instead)
./mvnw clean install
./mvnw javafx:run
./mvnw test
./mvnw spotless:check
./mvnw spotless:apply
./mvnw pmd:check
```

---

## Architecture & Layer Patterns

### Layered Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Controllers                          │
│         (JavaFX FXML controllers, UI event handlers)    │
├─────────────────────────────────────────────────────────┤
│                     Services                            │
│    (Business logic, caching, transactions, validation)  │
├─────────────────────────────────────────────────────────┤
│                   Repositories                          │
│     (Spring Data JDBC, custom queries, soft deletes)    │
├─────────────────────────────────────────────────────────┤
│                     Entities                            │
│        (Data models, Lombok, column constants)          │
└─────────────────────────────────────────────────────────┘
```

---

## Code Style & Conventions

### Import Guidelines

Spotless automatically organizes imports. Follow these guidelines:

1. **Never use wildcard imports (`*`)**
2. **Always use complete import paths**
3. **Spotless will automatically sort and remove unused imports**

```java
// GOOD
import java.util.List;
import java.util.Optional;
import com.pinodesk.entity.User;
import com.pinodesk.constant.DomainError;

// BAD - Don't use wildcards
import java.util.*;
import com.pinodesk.entity.*;
```

### Method Ordering

Organize class members in this order:

1. **Static fields** (constants)
2. **Instance fields**
3. **Constructors**
4. **Public methods** (business logic)
5. **Package-private methods**
6. **Protected methods**
7. **Private methods** (helpers at the bottom)

```java
public class UserService extends BaseService {
    // 1. Static fields
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    // 2. Instance fields
    private final UserRepository userRepository;
    private final SessionService sessionService;
    
    // 3. Constructor
    public UserService(UserRepository userRepository, SessionService sessionService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
    }
    
    // 4. Public methods
    @Cacheable(CacheNameConstants.USERS_BY_FILTER)
    public List<UserVM> searchUsersByFilter(UserFilterVM filter) {
        return userRepository.findByFilter(filter);
    }
    
    @Transactional
    public User createUser(UserAddVM userAdd) {
        validateConstraints(userAdd);
        User user = new User();
        user.setFullName(userAdd.getFullName());
        return userRepository.save(user);
    }
    
    // 7. Private methods (at the bottom)
    private void validateConstraints(UserAddVM userAdd) {
        // validation logic
    }
}
```

---

## Project Structure & Resources

### Source Directory Structure

```
src/main/
├── java/com/pinodesk/           # Java source code
│   ├── annotation/               # Custom annotations
│   ├── apimodel/                 # API models
│   ├── aspect/                   # AOP aspects
│   ├── constant/                 # Enums and constants
│   ├── controller/               # JavaFX controllers
│   ├── entity/                   # Data entities
│   ├── exception/                # Custom exceptions
│   ├── javafx/                   # JavaFX utilities
│   ├── misc/                     # Miscellaneous
│   ├── model/                    # Domain models
│   ├── properties/               # Configuration properties
│   ├── repository/               # Spring Data repositories
│   ├── service/                  # Business services
│   ├── util/                     # Utility classes
│   └── viewmodel/                # View models (DTOs)
└── resources/
    ├── assets/
    │   ├── css/                  # Stylesheets (snake_case)
    │   │   └── pinodesk.css
    │   ├── images/               # Image assets
    │   └── templates/            # FXML files (snake_case)
    │       ├── user_main.fxml
    │       ├── user_add.fxml
    │       └── product_main.fxml
    ├── db/migration/             # Flyway migrations
    │   ├── V0001__init.sql
    │   └── V0002__add_users.sql
    └── pinodesk/
        ├── lang_en.properties    # Language files (snake_case)
        ├── lang_id.properties
        └── misc/
```

### Naming Conventions by Location

| Location | Pattern | Example |
|----------|---------|---------|
| **Java Classes** | PascalCase | `UserService.java` |
| **FXML Files** | snake_case | `user_main.fxml` |
| **CSS Files** | snake_case | `pinodesk.css` |
| **Language Files** | snake_case | `lang_en.properties` |
| **DB Migrations** | V{number}__{desc}.sql | `V0001__init.sql` |
| **Images** | snake_case | `dialog_icon_error.png` |

### Resource Bundle (i18n)

Language files use **snake_case** with this pattern:
- `lang_{locale}.properties`
- Keys use uppercase with underscores: `LBL_USERNAME`, `BTN_SAVE`, `ERROR_USER_NOT_FOUND`

Example (`lang_en.properties`):
```properties
LBL_USERNAME=Username
LBL_PASSWORD=Password
BTN_SAVE=Save
BTN_CANCEL=Cancel
ERROR_USER_NOT_FOUND=User with ID {0} not found
```

---

## Essential Code Patterns

### Entity Pattern

```java
package com.pinodesk.entity;

import com.pinodesk.sequel.model.DataModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class User extends DataModel {

    // Column name constants for type-safe queries
    public static final String C_FULL_NAME = "full_name";
    public static final String C_USERNAME = "username";
    public static final String C_USER_GROUP_ID = "user_group_id";
    public static final String C_STATUS = "status";
    public static final String C_PASSWORD_HASH = "password_hash";

    private String fullName;
    private String username;
    private Long userGroupId;
    private String status;
    private String passwordHash;
}
```

**Entity Rules:**
- Extend `DataModel` from Pinodesk Sequel library
- Define column constants with `C_` prefix for type-safe queries
- Use Lombok `@Data` for getters/setters/equals/hashCode
- Use `@EqualsAndHashCode(callSuper = false)`
- Soft deletes use `deletedAt` field (set to `now()` instead of actual deletion)

### Repository Pattern

```java
package com.pinodesk.repository;

import com.pinodesk.entity.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, UserRepositoryCustom {

    // Spring Data JDBC generates the query from method name
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    // Custom query with @Query annotation
    @Transactional
    @Modifying
    @Query("update `user` set updated_at=now(), deleted_at=now() where id in (:ids)")
    void deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    // Complex queries go to *Impl.java
    List<User> findActiveUsers();
}
```

**Repository Rules:**
- Use Spring Data JDBC naming conventions for simple queries
- Create `*Impl.java` files for complex custom queries
- Mark modification methods with `@Transactional` and `@Modifying`
- Always filter out soft-deleted records (`deletedAtIsNull`)

### Service Pattern

```java
package com.pinodesk.service;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.constant.Activity;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.User;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.UserRepository;
import com.pinodesk.viewmodel.UserAddVM;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends BaseService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(CacheNameConstants.USERS_BY_FILTER)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username);
    }

    @TargetActivity(Activity.ADD_USER)
    @CacheEvict(value = { CacheNameConstants.USERS_BY_FILTER }, allEntries = true)
    @Transactional
    public User createUser(UserAddVM userAdd) {
        if (userRepository.existsByUsernameAndDeletedAtIsNull(userAdd.getUsername())) {
            throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
        }
        User user = new User();
        user.setFullName(userAdd.getFullName());
        user.setUsername(userAdd.getUsername());
        return userRepository.save(user);
    }
}
```

**Service Rules:**
- Use constructor injection for dependencies, never use `new`
- Use `@Cacheable` for read-heavy operations
- Use `@CacheEvict` when modifying cached data
- Use `@Transactional` for methods that modify data
- Use `@TargetActivity` for audit logging where appropriate
- Throw `DomainException` with `DomainError` enum for business errors

### Controller Pattern

```java
package com.pinodesk.controller;

import com.pinodesk.pandora.utility.StageUtils;
import com.pinodesk.viewmodel.UserFilterVM;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class UserMainController extends BaseController {

    @FXML
    private TableView<UserVM> tableView;

    @FXML
    private Button btnAdd, btnEdit, btnDelete;

    private final UserService userService;

    @Override
    protected void initServices() {
        // Initialize service dependencies here
    }

    @Override
    protected void initControlActions() {
        btnAdd.setOnAction(event -> handleAdd());
        btnEdit.setOnAction(event -> handleEdit());
        btnDelete.setOnAction(event -> handleDelete());
    }

    @Override
    protected void initControlValues() {
        refreshTableData();
    }

    @Override
    protected Stage getCurrentStage() {
        return (Stage) tableView.getScene().getWindow();
    }

    private void handleAdd() {
        StageUtils.navigate(Page.USER_ADD);
    }

    private void refreshTableData() {
        List<UserVM> users = userService.searchUsersByFilter(new UserFilterVM());
        tableView.getItems().setAll(users);
    }
}
```

**Controller Rules:**
- Extend `BaseController` or its subclasses
- Override abstract methods: `initServices()`, `initControlActions()`, `initControlValues()`, `getCurrentStage()`
- Use `@FXML` annotation for UI elements and event handlers
- Load pages via `PageLoader.modal(Page.SOME_PAGE)` or `PageLoader.navigate(Page.SOME_PAGE)`

---

## Code Quality & Formatting

### Spotless (Eclipse Formatter)

- **Configuration:** `default-formatter.xml` in project root
- **Line length:** 120 characters
- **Indentation:** 4 spaces (no tabs)
- **Brace style:** End of line

**Commands:**
```bash
./mvnw spotless:check    # Check formatting
./mvnw spotless:apply    # Apply formatting
```

### PMD (Static Analysis)

- **Configuration:** `default-ruleset.xml` in project root
- **Version:** 7.13.0

**Commands:**
```bash
./mvnw pmd:check         # Run PMD analysis
```

### Testing

- **Framework:** JUnit 5 + TestFX + Mockito
- **Coverage:** JaCoCo with SonarCloud integration
- **UI Tests:** Use `JavaFXTestBase` with Monocle for headless testing

**Commands:**
```bash
./mvnw test              # Run all tests
```

---

## Common Tasks & Patterns

### Adding a New Entity

1. Create entity class in `com.pinodesk.entity`
2. Create repository interface in `com.pinodesk.repository`
3. Create service class in `com.pinodesk.service`
4. Create controller(s) in `com.pinodesk.controller`
5. Create FXML file in `src/main/resources/assets/templates`
6. Add Flyway migration in `src/main/resources/db/migration`

### Database Migrations

- **Location:** `src/main/resources/db/migration/`
- **Naming:** `V{number}__{description}.sql` (double underscore)
- **Example:** `V0048__add_user_group_table.sql`

### Error Handling

Use `DomainException` with `DomainError` enum for business errors:

```java
if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
    throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
}
```

### Activity Logging

Use `@TargetActivity` annotation on service methods:

```java
@TargetActivity(Activity.USER_CREATED)
public User createUser(UserAddVM userAdd) {
    // ...
}
```

---

## Package Structure

```
com.pinodesk.
├── annotation/          # Custom annotations (TargetActivity)
├── apimodel/            # API request/response models
├── aspect/              # AOP aspects (ServiceAspect)
├── constant/            # Enums and constants
├── controller/          # JavaFX FXML controllers
│   ├── catalog/         # Product, customer, supplier, doctor controllers
│   ├── report/          # Report controllers
│   ├── settings/        # User, configuration controllers
│   └── transaction/     # Sale, purchase, payable/receivable controllers
├── entity/              # JPA/Data entities
├── exception/           # Custom exceptions
├── javafx/              # JavaFX utilities (converters, listeners)
├── misc/                # Miscellaneous utilities
├── model/               # Domain models
├── properties/          # Configuration properties
├── repository/          # Spring Data JDBC repositories
├── service/             # Business logic services
├── util/                # Utility classes
└── viewmodel/           # DTOs for UI binding
```

---

## File Naming Conventions

| Type | Naming Pattern | Example |
|------|---------------|---------|
| Entity | PascalCase noun | `User.java`, `Product.java` |
| Repository | Entity + Repository | `UserRepository.java` |
| Service | Entity + Service | `UserService.java` |
| Controller | Entity + Action + Controller | `UserMainController.java`, `UserAddController.java` |
| ViewModel | Entity + Action + VM | `UserVM.java`, `UserAddVM.java` |
| FXML | snake_case | `user_main.fxml`, `user_add.fxml` |

---

## Contributing

1. Fork the repository
2. Create a feature branch from `main`
3. Make your changes following the coding conventions
4. Run all quality checks: `./mvnw spotless:check` and `./mvnw test`
5. Submit a Pull Request

For detailed contribution guidelines, see [CONTRIBUTING.md](CONTRIBUTING.md).

---

## License

This project is open source. See [LICENSE](LICENSE) for details.
