# Pinodesk AI Coding Instructions

## Architecture Overview
Pinodesk is a desktop-based Point of Sale application with JavaFX and a layered architecture:
- **Entities** (`pinodesk.entity`): Data models extending `com.mudiatech.sequel.model.DataModel`, using Lombok `@Data`, with column name constants (e.g., `C_FULL_NAME`).
- **Repositories** (`pinodesk.repository`): Spring Data JDBC interfaces with custom implementations for complex queries, soft deletes via `deleted_at` field.
- **Services** (`pinodesk.service`): Business logic with caching (`@Cacheable`, `@CacheEvict`), transactions, and activity annotations (`@TargetActivity`).
- **Controllers** (`pinodesk.controller`): JavaFX FXML controllers using Pandora utilities (`PageLoader`, `StageUtils`) for UI navigation and modals.
- **ViewModels** (`pinodesk.viewmodel`): DTOs for UI data binding.
- **Constants** (`pinodesk.constant`): Centralized enums and strings (e.g., `Activity`, `DomainError`, `Page`).

Key integrations: H2 database with Flyway migrations, external API via Unirest, i18n with `ResourceBundle`, custom Mudiatech libraries (Sequel, Pandora, Toolbox).

## Developer Workflows
- **Build & Run**: Use `./mvnw clean javafx:run` or `./script.sh run` for development.
- **Test**: `./mvnw test` or `./script.sh test` (includes Spotless formatting and PMD checks).
- **Format**: `./mvnw spotless:apply` or `./script.sh fix` (Eclipse formatter via `default-formatter.xml`).
- **Package**: Maven profiles (`-Pexe` for Windows MSI, `-Pdeb` for Linux DEB, `-Prpm` for RPM, `-Ppkg` for macOS PKG) with bundled JRE.
- **Debug**: JavaFX apps require headless=false; use TestFX for UI tests with Monocle.
- **CI/CD**: GitLab CI with Maven; pushes to private registry for snapshots/releases.

## Project Conventions
- **Entities**: Extend `DataModel`, define column constants, use Lombok. Example: `User.java` with `C_USERNAME`.
- **Repositories**: Soft deletes with `deleted_at=now()`, custom queries in `*Impl.java`. Use `@Transactional` for modifications.
- **Services**: Inject repositories, use caching for read-heavy ops, throw `DomainException` for business errors.
- **Controllers**: JavaFX FXML controllers with inheritance hierarchy. Extend `BaseController` (abstract) or subclasses like `CommonContentPaneController`/`CommonDataSaveController` for shared functionality. Override abstract methods: `initServices()`, `initControlActions()`, `initControlValues()`, `getCurrentStage()`. Use `@FXML` fields for UI elements, event handlers (`@FXML void handleAction(ActionEvent event)`), load pages via `PageLoader.modal(Page.SPLASH)`. Example: `MainController extends BaseController` with `@Override protected void initControlValues() { ... }`.
- **Configuration**: `application.properties` with encrypted JDBC creds (Jasypt), profile-specific overrides.
- **Migrations**: Flyway SQL files in `src/main/resources/db/migration/` (e.g., `V0001__create_and_init_table_configuration.sql`).
- **I18n**: `lang_en.properties`, `lang_id.properties`; access via `ResourceBundleUtils.getDefaultResourceBundle()`.
- **Logging**: SLF4J with Logback; redirect stdout to logger in `Pinodesk.java`.
- **Security**: Password hashing via `PasswordUtils`, session management in `SessionService`.

## Integration Patterns
- **Database**: H2 in user home (`~/.pinodesk-snapshot/db/`), MySQL mode, encrypted connections.
- **API**: REST calls to `https://api-staging.pinodesk.com` using Unirest with Jackson serialization.
- **UI**: JavaFX with FXML templates in `src/main/resources/assets/templates/`, CSS in `assets/css/`, images in `assets/images/`.
- **External Libs**: Mudiatech Pandora for UI utils, Sequel for data models, Toolbox/Norway for utilities.
- **Cross-Platform**: Assets in `assets/linux/`, `assets/mac/`, `assets/windows/`; packaging via Javapackager.

Reference key files: `pom.xml` (dependencies/profiles), `Pinodesk.java` (app entry), `application.properties` (config), `UserService.java` (service pattern), `MainController.java` (controller pattern).