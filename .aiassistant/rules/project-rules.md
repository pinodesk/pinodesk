# Pinodesk Project Rules for JetBrains AI Assistant

> **Full Reference:** See [AGENTS.md](../../AGENTS.md)

This file defines the coding standards, architecture patterns, and best practices for the Pinodesk Point of Sale (POS) system.

---

## Project Overview

**Pinodesk** - Free, open-source desktop Point of Sale system for retail businesses

- **Language:** Java 21
- **UI Framework:** JavaFX 21 with FXML
- **Dependency Injection:** Spring Framework 6.0.7
- **Data Access:** Spring Data JDBC
- **Database:** H2 Embedded (MySQL mode)
- **Build Tool:** Maven (use `./script.sh` wrapper)

---

## Critical Rules (MUST Follow)

### 1. Constructor Injection Only

```java
// GOOD
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

// BAD - Never use field injection
@Autowired
private UserRepository userRepository;
```

**Rule:** Always use constructor injection. Never use `@Autowired` on fields.

### 2. Always Filter Soft-Deleted Records

```java
Optional<User> findByUsernameAndDeletedAtIsNull(String username);

if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
    throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
}
```

**Rule:** All queries must include `DeletedAtIsNull` to exclude soft-deleted records. Soft deletes use `deleted_at` field set to `now()` instead of actual deletion.

### 3. Define Column Constants in Entities

```java
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends DataModel {
    public static final String C_FULL_NAME = "full_name";
    public static final String C_USERNAME = "username";
    public static final String C_USER_GROUP_ID = "user_group_id";
    
    private String fullName;
    private String username;
    private Long userGroupId;
    private LocalDateTime deletedAt;
}
```

**Rule:** Define column constants with `C_` prefix for type-safe queries. Extend `DataModel`, use `@Data` and `@EqualsAndHashCode(callSuper = false)`.

### 4. Caching Pattern for Read Operations

```java
@Cacheable(CacheNameConstants.USERS_BY_FILTER)
public List<UserVM> searchUsersByFilter(UserFilterVM filter) {
    return userRepository.findByFilter(filter);
}

@CacheEvict(value = { CacheNameConstants.USERS_BY_FILTER }, allEntries = true)
@Transactional
public void removeUsers(List<Long> ids) {
    userRepository.deleteUpdateByIdIn(ids);
}
```

**Rule:** Use `@Cacheable` for read-heavy operations. Use `@CacheEvict` when modifying cached data.

### 5. Error Handling with DomainException

```java
if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
    throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
}
```

**Rule:** Always throw `DomainException` with `DomainError` enum for business logic errors, never generic exceptions.

### 6. Mark Modifications with @Transactional

```java
@Transactional
public User createUser(UserAddVM userAdd) {
    User user = new User();
    user.setFullName(userAdd.getFullName());
    return userRepository.save(user);
}
```

**Rule:** All methods that modify data must be annotated with `@Transactional`.

### 7. Import Guidelines

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

**Rule:** Never use wildcard imports. Spotless automatically handles import sorting. Always use complete import paths.

### 8. Method Ordering

Organize class members in this order:

1. **Static fields** (constants)
2. **Instance fields**
3. **Constructors**
4. **Public methods** (business logic)
5. **Package-private methods**
6. **Protected methods**
7. **Private methods** (helpers at the bottom)

---

## Layer Patterns

### Layered Architecture

```
Controllers → Services → Repositories → Entities
```

### Entity Pattern

```java
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends DataModel {
    public static final String C_USERNAME = "username";
    public static final String C_FULL_NAME = "full_name";
    
    private String username;
    private String fullName;
    private LocalDateTime deletedAt;
}
```

**Rule:** Entities extend `DataModel`, use Lombok `@Data`, define column constants.

### Repository Pattern

```java
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    
    @Transactional
    @Modifying
    @Query("update `user` set updated_at=now(), deleted_at=now() where id in (:ids)")
    void deleteUpdateByIdIn(@Param("ids") List<Long> ids);
}
```

**Rule:** Use Spring Data JDBC naming conventions for simple queries. Create `*Impl.java` for complex queries. Always filter soft-deleted records.

### Service Pattern

```java
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

**Rule:** Use constructor injection only. Apply caching, transactions, and activity logging appropriately.

### Controller Pattern

```java
public class UserMainController extends BaseController {
    @FXML
    private TableView<UserVM> tableView;
    
    @FXML
    private Button btnAdd;
    
    @Override
    protected void initServices() { }
    
    @Override
    protected void initControlActions() {
        btnAdd.setOnAction(event -> handleAdd());
    }
    
    @Override
    protected void initControlValues() {
        refreshTableData();
    }
    
    @Override
    protected Stage getCurrentStage() {
        return (Stage) tableView.getScene().getWindow();
    }
}
```

**Rule:** Extend `BaseController`, override required methods, use `@FXML` for UI binding.

---

## Build & Development Commands

**Always use `./script.sh` wrapper instead of direct Maven commands:**

```bash
./script.sh build   # Build only
./script.sh run     # Run application
./script.sh test    # Run quality checks + tests (PMD, Spotless, Tests)
./script.sh fix     # Fix formatting automatically
./script.sh clean   # Clean build
```

---

## File Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Entity | PascalCase | `User.java` |
| Repository | Entity + Repository | `UserRepository.java` |
| Service | Entity + Service | `UserService.java` |
| Controller | Entity + Action + Controller | `UserMainController.java` |
| ViewModel | Entity + Action + VM | `UserVM.java` |
| FXML | snake_case | `user_main.fxml` |
| Language Files | snake_case | `lang_en.properties` |
| CSS Files | snake_case | `pinodesk.css` |
| Database Migrations | V{number}__{description}.sql | `V0048__add_user_group_table.sql` |

---

## Code Quality & Formatting

- **Formatter:** Spotless with Eclipse formatter rules (line length: 120 characters)
- **Static Analysis:** PMD 7.13.0
- **Testing:** JUnit 5 + TestFX + Mockito
- **Configuration:** `default-formatter.xml` and `default-ruleset.xml` in project root

**Pre-commit checks:**
```bash
./script.sh test    # Runs PMD, Spotless check, and tests
./script.sh fix     # Auto-fixes formatting
```

---

## Database & Migrations

- **Location:** `src/main/resources/db/migration/`
- **Naming:** `V{number}__{description}.sql` (double underscore)
- **Pattern:** Flyway 9.16.3
- **Soft Deletes:** Use `deleted_at` field instead of actual deletion

Example:
```sql
V0048__add_user_group_table.sql
```

---

## References

- [AGENTS.md](../../AGENTS.md) - Full AI agent instructions and project standards
- [CONTRIBUTING.md](../../CONTRIBUTING.md) - Contribution workflow and PR guidelines
