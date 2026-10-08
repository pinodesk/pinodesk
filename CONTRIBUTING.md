# Contributing to Pinodesk

Thank you for your interest in contributing to Pinodesk! This document will help you get started with development, understand our workflow, and submit quality contributions.

**For coding standards and architecture guidelines**, refer to [AGENTS.md](AGENTS.md) — it's our authoritative technical reference.

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

## Project Architecture & Standards

For comprehensive documentation on project architecture, layer patterns, and our complete tech stack, 
see [AGENTS.md](AGENTS.md#architecture--layer-patterns).

**Key highlights:**
- Layered architecture: Entities → Repositories → Services → Controllers
- Entities extend `DataModel` with column constants (`C_` prefix)
- Repositories use Spring Data JDBC with soft deletes
- Services handle business logic with caching and transactions
- Controllers extend `BaseController` and manage UI interactions

For detailed patterns and code examples, refer to [AGENTS.md](AGENTS.md#essential-code-patterns).

---

## Coding Conventions & Standards

For our complete coding conventions and standards, including detailed code examples and best practices, 
see [AGENTS.md](AGENTS.md#code-style--conventions) and [AGENTS.md](AGENTS.md#essential-code-patterns).

**Key standards:**
- **Entities:** Extend `DataModel`, use Lombok `@Data`, define column constants with `C_` prefix
- **Repositories:** Use Spring Data JDBC naming, create `*Impl.java` for complex queries, filter soft deletes
- **Services:** Use constructor injection, apply `@Cacheable`/`@CacheEvict`, throw `DomainException` for business errors
- **Controllers:** Extend `BaseController`, override required methods, use `@FXML` for UI binding
- **Imports:** Never use wildcard imports, Spotless automatically organizes them
- **Build:** Always use `./script.sh` instead of direct Maven commands
- **Database:** Migrations use `V{number}__{description}.sql` format

For method ordering, naming conventions, and detailed patterns, refer to [AGENTS.md](AGENTS.md).

---

## Testing Guidelines

For comprehensive testing guidelines and patterns, see [AGENTS.md](AGENTS.md#testing).

**Quick reference:**
- Tests go in `src/test/java/` mirroring the main package structure
- Use JUnit 5 + TestFX + Mockito
- UI tests extend `JavaFXTestBase` with Monocle for headless testing
- Use a separate `application-test.properties` configuration
- New code should have corresponding tests

**Before submitting a PR, run:**
```bash
./script.sh test    # Runs all tests + PMD analysis
```

---

## Pull Request Process

### Before Submitting

1. **Run all quality checks and tests** (this runs PMD, Spotless, and tests)
   ```bash
   ./script.sh test
   ```

2. **Fix any formatting issues automatically**
   ```bash
   ./script.sh fix
   ```

3. **Test your changes manually**
   ```bash
   ./script.sh run
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
