# Trae AI Rules for Pinodesk

> **Full Reference:** See [AGENTS.md](../../AGENTS.md) for complete documentation.

## Quick Overview

**Pinodesk** is a Java 21 + JavaFX + Spring Framework desktop POS application.

### Tech Stack
- Java 21, JavaFX 21, Spring Framework 6.0.7
- Spring Data JDBC, H2 Database, Flyway
- Maven, Lombok

### Build Commands (Use script.sh)
```bash
./script.sh build   # Build only
./script.sh run     # Run application
./script.sh test    # Full quality check + tests
./script.sh fix     # Fix formatting
```

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

// BAD
@Autowired
private UserRepository userRepository;  // Never do this
```

### 2. Always Filter Soft-Deleted Records
```java
// Repository
Optional<User> findByUsernameAndDeletedAtIsNull(String username);

// Service check
if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
    throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
}
```

### 3. Define Column Constants in Entities
```java
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends DataModel {
    public static final String C_FULL_NAME = "full_name";
    public static final String C_USERNAME = "username";
    
    private String fullName;
    private String username;
    private LocalDateTime deletedAt;
}
```

### 4. Error Handling Pattern
```java
if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
    throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
}
```

### 5. Caching Pattern
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

### 6. Always Use @Transactional for Writes
```java
@Transactional
public User createUser(UserAddVM userAdd) {
    // ...
}
```

### 7. Import Guidelines
Spotless automatically handles import ordering. Follow these guidelines:

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

### 8. Method Ordering

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

### Directory Structure

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
