# Contributing to Pinodesk

Thank you for your interest in contributing to Pinodesk! This document will help you get started with development, understand our conventions, and submit quality contributions.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Development Setup](#development-setup)
- [Project Architecture](#project-architecture)
- [Coding Conventions](#coding-conventions)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)

---

## Code of Conduct

Be respectful, constructive, and inclusive. Treat others as you would like to be treated. We're all here to build great software together.

---

## Development Setup

### Prerequisites

- **Java 21** (required)
- **Maven**
- **Git**
- **IDE** (any Java IDE of your choice)

### Getting the Code

1. **Fork the repository** — Click the "Fork" button on GitHub to create your own copy

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/pinodesk.git
   cd pinodesk
   ```

3. **Add upstream remote** (to keep your fork updated)
   ```bash
   git remote add upstream https://github.com/pinodesk/pinodesk.git
   ```

### Building the Project

```bash
# Clean build
./mvnw clean install

# Or using the convenience script
./script.sh build
```

### Running the Application

```bash
# Run in development mode
./mvnw clean javafx:run

# Or using the script
./script.sh run
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Or using the script
./script.sh test
```

### Code Formatting

We use Spotless with Eclipse formatter rules. Always format before committing:

```bash
# Check formatting
./mvnw spotless:check

# Apply formatting
./mvnw spotless:apply

# Or using the script
./script.sh fix
```

---

## Project Architecture

Pinodesk follows a **layered architecture** with clear separation of concerns:

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

### Layer Details

| Layer | Package | Purpose |
|-------|---------|---------|
| **Entities** | `com.pinodesk.entity` | Data models extending `DataModel`, using Lombok `@Data`, with column name constants (e.g., `C_FULL_NAME`) |
| **Repositories** | `com.pinodesk.repository` | Spring Data JDBC interfaces. Complex queries go in `*Impl.java` files. Soft deletes use `deleted_at` field. |
| **Services** | `com.pinodesk.service` | Business logic layer. Uses `@Cacheable`/`@CacheEvict` for caching, `@Transactional` for modifications, `@TargetActivity` for activity logging. |
| **Controllers** | `com.pinodesk.controller` | JavaFX FXML controllers. Use `PageLoader` and `StageUtils` for navigation. Extend `BaseController` or its subclasses. |
| **ViewModels** | `com.pinodesk.viewmodel` | DTOs for UI data binding |
| **Constants** | `com.pinodesk.constant` | Centralized enums and strings (`Activity`, `DomainError`, `Page`, etc.) |

### Key Files to Reference

- `pom.xml` — Dependencies, build profiles, and plugin configuration
- `Pinodesk.java` — Application entry point
- `application.properties` — Configuration (encrypted credentials via Jasypt)
- `UserService.java` — Example service pattern
- `MainController.java` — Example controller pattern

---

## Coding Conventions

### Entities

```java
@Data
public class User extends DataModel {
    // Column name constants for type-safe queries
    public static final String C_USERNAME = "username";
    public static final String C_FULL_NAME = "full_name";
    
    private String username;
    private String fullName;
    private LocalDateTime deletedAt; // For soft deletes
}
```

**Rules:**
- Extend `DataModel` from Pinodesk Sequel library
- Define column constants with `C_` prefix
- Use Lombok `@Data` for getters/setters/equals/hashCode
- Soft deletes via `deleted_at` field (set to `now()` instead of actual deletion)

### Repositories

```java
public interface UserRepository extends CrudRepository<User, Long> {
    // Spring Data JDBC generates the query
    Optional<User> findByUsername(String username);
    
    // Complex queries go in *Impl.java
    List<User> findActiveUsers();
}

// In UserRepositoryImpl.java
@Transactional
public List<User> findActiveUsers() {
    // Custom query implementation
    // Use deleted_at IS NULL for active records
}
```

**Rules:**
- Use Spring Data JDBC naming conventions for simple queries
- Create `*Impl.java` files for complex queries
- Mark modification methods with `@Transactional`
- Always filter out soft-deleted records (`deleted_at IS NULL`)

### Services

```java
@Service
public class UserService {
    
    @Cacheable("users")
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    @TargetActivity(Activity.USER_CREATED)
    public User createUser(User user) {
        // Validation logic
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DomainException(DomainError.USER_ALREADY_EXISTS);
        }
        return userRepository.save(user);
    }
}
```

**Rules:**
- Inject repositories, never use `new` for dependencies
- Use `@Cacheable` for read-heavy operations
- Use `@CacheEvict` when modifying cached data
- Throw `DomainException` with `DomainError` enum for business errors
- Use `@TargetActivity` for audit logging where appropriate

### Controllers

```java
public class MainController extends BaseController {
    
    @FXML private TextField usernameField;
    
    @Override
    protected void initServices() {
        // Initialize service dependencies
    }
    
    @Override
    protected void initControlActions() {
        // Set up event handlers
        usernameField.setOnAction(this::handleUsernameAction);
    }
    
    @Override
    protected void initControlValues() {
        // Initialize UI values
    }
    
    @Override
    protected Stage getCurrentStage() {
        return (Stage) usernameField.getScene().getWindow();
    }
    
    @FXML
    private void handleUsernameAction(ActionEvent event) {
        // Handle event
    }
}
```

**Rules:**
- Extend `BaseController` or its subclasses (`CommonContentPaneController`, `CommonDataSaveController`)
- Override abstract methods: `initServices()`, `initControlActions()`, `initControlValues()`, `getCurrentStage()`
- Use `@FXML` annotation for UI elements and event handlers
- Load pages via `PageLoader.modal(Page.SOME_PAGE)` or `PageLoader.navigate(Page.SOME_PAGE)`

### Configuration

- `application.properties` contains main configuration
- Database credentials are encrypted with Jasypt
- Use profile-specific files: `application-dev.properties`, `application-prod.properties`

### Database Migrations

Migrations are in `src/main/resources/db/migration/`:

```
V0001__create_and_init_table_configuration.sql
V0002__create_table_user.sql
V0003__add_column_user_status.sql
```

**Naming convention:** `V{number}__{description}.sql` (double underscore)

---

## Testing Guidelines

### Test Structure

Tests go in `src/test/java/` mirroring the main package structure:

```
src/test/java/com/pinodesk/
├── service/
│   └── UserServiceTest.java
├── repository/
│   └── UserRepositoryTest.java
└── controller/
    └── MainControllerTest.java
```

### UI Testing

For JavaFX UI tests, extend `JavaFXTestBase` which uses TestFX with Monocle for headless testing:

```java
class MainControllerTest extends JavaFXTestBase {
    
    @Test
    void shouldDisplayUsername() {
        // Given
        interact(() -> {
            // Setup UI state
        });
        
        // When & Then
        verifyThat("#usernameField", hasText("expected"));
    }
}
```

**Important:** JavaFX tests require `headless=false` in configuration. Use Monocle for CI environments.

### Test Profile

Use a separate test configuration in `application-test.properties` to avoid affecting your development database.

---

## Pull Request Process

### Before Submitting

1. **Format your code**
   ```bash
   ./mvnw spotless:apply
   ```

2. **Run static analysis**
   ```bash
   ./mvnw pmd:check
   ```

3. **Run all tests**
   ```bash
   ./mvnw test
   ```

4. **Test your changes manually**
   ```bash
   ./mvnw javafx:run
   ```

### Submission Checklist

- [ ] Code follows the conventions above
- [ ] Tests pass locally
- [ ] New code has corresponding tests
- [ ] Documentation updated if needed
- [ ] Commit messages are clear and descriptive

### Commit Message Format

Use clear, descriptive commit messages:

```
feat: add product import from CSV

Add ability to import products from CSV files with automatic
category detection and duplicate handling.

Closes #123
```

**Prefixes:**
- `feat:` — New feature
- `fix:` — Bug fix
- `docs:` — Documentation changes
- `refactor:` — Code refactoring
- `test:` — Adding or updating tests
- `chore:` — Maintenance tasks

### Pull Request Guidelines

1. **Create a feature branch** from `develop`
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make focused commits** — Each commit should represent one logical change

3. **Keep PRs small** — Smaller PRs are reviewed faster and merged sooner

4. **Write a clear description** — Explain what, why, and how

5. **Link related issues** — Use "Closes #123" or "Fixes #456"

### After Submission

- Respond to review feedback promptly
- Keep your branch updated with `develop`
- Don't force push after review starts (unless requested)

### Keeping Your Fork Updated

Before starting new work, sync your fork with the upstream repository:

```bash
git fetch upstream
git checkout develop
git merge upstream/develop
```

---

## Getting Help

- Check existing issues before creating new ones
- Provide clear reproduction steps for bugs
- Include environment details (OS, Java version, steps to reproduce)

## License

By contributing to Pinodesk, you agree that your contributions will be licensed under the project's open source license.

---

Thank you for contributing! Every contribution, no matter how small, helps make Pinodesk better for everyone.
