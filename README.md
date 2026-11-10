# Servlet/JSP Playground

A small Java web application for learning the fundamentals of Jakarta Servlets, HTML forms, JSP redirects, Maven, and WAR deployment to Apache Tomcat.

The application displays a customer form, accepts a first name, last name, and gender, and sends the submitted data to a Jakarta Servlet. The servlet validates the request and renders either an error page or a personalized welcome page.

> **Project name note:** The artifact is currently named `servelet-jsp-playground` (with “servelet” instead of “servlet”). The spelling is preserved in the Maven artifact ID, WAR name, and default URL.

## Features

- Jakarta Servlet-based request handling
- HTML form submission using `POST`
- Servlet URL mapping with `@WebServlet`
- Server-side validation of required fields
- Separate success and validation-error responses
- JSP entry point that redirects to the HTML form
- Maven WAR packaging
- Compatible with Jakarta-based Tomcat installations

## Technology Stack

| Technology | Version/configuration |
|---|---|
| Java | A JDK supported by your installed Maven and Tomcat |
| Maven | 3.x recommended |
| Jakarta Servlet API | 6.0.0 |
| Servlet container | Apache Tomcat 10.1+ or 11 |
| Packaging | WAR |
| Test dependency | JUnit 3.8.1, currently included for future tests |

The project uses the Jakarta namespace:

```java
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
```

It should not be deployed to a `javax.servlet`-based container such as Tomcat 9 without changing the dependency and imports. Tomcat 10 and later use `jakarta.servlet`.

## Application Flow

```text
Browser
  |
  | GET /servelet-jsp-playground/
  v
index.jsp
  |
  | Redirect to Main.html
  v
Main.html
  |
  | POST firstName, lastName, gender
  v
Main servlet
  |
  +-- Missing input? Render validation errors
  |
  +-- Valid input? Render personalized welcome page
```

## Project Structure

```text
servelet-jsp-playground/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── joao/
        │           └── Main.java
        └── webapp/
            ├── index.jsp
            ├── Main.html
            └── WEB-INF/
                └── web.xml
```

### Important files

- `pom.xml` — Maven project metadata, dependencies, and WAR configuration.
- `Main.java` — Jakarta Servlet mapped to `/Main`.
- `Main.html` — Customer form and browser-side entry page.
- `index.jsp` — Redirects the application root to `Main.html`.
- `WEB-INF/web.xml` — Deployment descriptor. Servlet mapping is provided by the `@WebServlet` annotation.

## Prerequisites

Install the following before running the project:

1. A JDK, not only a JRE.
2. Apache Maven.
3. Apache Tomcat 10.1+ or Tomcat 11.

Check the installed tools:

```bash
java -version
mvn -version
```

The `jakarta.servlet-api` dependency has `provided` scope because Tomcat supplies the Servlet API at runtime. Maven uses it to compile the application, but it is not bundled into the WAR.

## Build the Project

From the project directory:

```bash
mvn clean package
```

The generated WAR is:

```text
target/servelet-jsp-playground.war
```

For a faster build without tests:

```bash
mvn clean package -DskipTests
```

Validate only the Maven project model:

```bash
mvn validate
```

## Run with Tomcat

### Option 1: Deploy the WAR manually

1. Build the project:

   ```bash
   mvn clean package
   ```

2. Copy `target/servelet-jsp-playground.war` into Tomcat's `webapps` directory.
3. Start Tomcat.
4. Open the application:

   ```text
   http://localhost:8080/servelet-jsp-playground/
   ```

Tomcat derives the context path from the WAR filename. Because the WAR is named `servelet-jsp-playground.war`, the context path is `/servelet-jsp-playground`.

### Option 2: Run from IntelliJ IDEA

1. Add or select a local Tomcat server.
2. Use Tomcat 10.1+ or Tomcat 11.
3. Add the project as a deployable WAR artifact.
4. Set the application context to:

   ```text
   /servelet-jsp-playground
   ```

5. Start the server.
6. Open the URL shown in the server configuration.

After changing `pom.xml`, use **Reload All Maven Projects** in IntelliJ. After changing Java or web resources, redeploy or rebuild the artifact if the server is not running in exploded/development mode.

## URLs and Endpoints

| URL | Method | Purpose |
|---|---:|---|
| `/servelet-jsp-playground/` | GET | JSP entry point; redirects to `Main.html` |
| `/servelet-jsp-playground/index.jsp` | GET | Same JSP redirect explicitly |
| `/servelet-jsp-playground/Main.html` | GET | Displays the customer form |
| `/servelet-jsp-playground/Main` | GET | Delegates to the servlet's POST behavior |
| `/servelet-jsp-playground/Main` | POST | Validates form data and renders a response |

The servlet mapping is declared in `Main.java`:

```java
@WebServlet("/Main")
public class Main extends HttpServlet {
    // ...
}
```

URL paths are case-sensitive on many systems and should be entered exactly as shown. `/main` and `/hello-servlet` are different paths from `/Main`.

## Using the Form

Open:

```text
http://localhost:8080/servelet-jsp-playground/Main.html
```

Enter:

- **First Name**
- **Last Name**
- **Gender**: `Male`, `Female`, or `Other`

Click **Submit**. The form sends:

```http
POST /servelet-jsp-playground/Main
Content-Type: application/x-www-form-urlencoded
```

Example request body:

```text
firstName=Joao&lastName=Christiansen&gender=Other
```

The servlet trims the name fields before displaying them. It checks that all three values are present and non-blank.

## Request Examples

### Valid form submission with cURL

```bash
curl -i \
  -X POST \
  -d "firstName=Joao" \
  -d "lastName=Christiansen" \
  -d "gender=Other" \
  http://localhost:8080/servelet-jsp-playground/Main
```

### Request the form

```bash
curl -i http://localhost:8080/servelet-jsp-playground/Main.html
```

### Request the application root

```bash
curl -i http://localhost:8080/servelet-jsp-playground/
```

The root request should return a redirect to `Main.html`.

## Validation Behavior

The servlet performs server-side validation even though the form is submitted from a browser.

| Input condition | Result |
|---|---|
| First name, last name, and gender supplied | Personalized welcome response |
| First name missing | “Please enter your First Name.” |
| Last name missing | “Please enter your Last Name.” |
| Both names missing | “Please enter both your First Name and Last Name.” |
| Gender missing | “Please select a Gender.” |

The error response includes a **Back to Form** link pointing to `Main.html`.

For `Male`, the response uses the title `Mr.`. For `Female`, it uses `Ms.`. Other values receive no title.

## How `Main.java` Works

The servlet has two request handlers:

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    doPost(request, response);
}
```

Both `GET /Main` and `POST /Main` currently use the same response logic. The main processing method:

1. Sets the response content type to UTF-8 HTML.
2. Reads `firstName`, `lastName`, and `gender`.
3. Checks for null or blank values.
4. Renders validation errors when required values are missing.
5. Builds a personalized welcome message when the input is valid.

## Common Problems

### `Dependency 'jakarta.servlet:jakarta.servlet-api:6.0.0' not found`

Confirm that the dependency is inside the `<dependencies>` element and that Maven is not in offline mode. Then reload Maven projects or run:

```bash
mvn -U clean package
```

### 404 for `/Main`

Check all of the following:

- Tomcat is running.
- The application was deployed successfully.
- The context path is exactly `/servelet-jsp-playground`.
- The endpoint is `/Main` with a capital `M`.
- The deployed WAR is the latest build.
- Tomcat is version 10.1+ or 11.

The URL should be:

```text
http://localhost:8080/servelet-jsp-playground/Main
```

### `javax.servlet` or `ClassNotFoundException` errors

This project uses Jakarta imports and requires a Jakarta-compatible Tomcat:

- Use Tomcat 10.1+ or 11.
- Keep `jakarta.servlet:jakarta.servlet-api` in `pom.xml`.
- Do not mix `javax.servlet.*` imports with Jakarta dependencies.

### The browser opens the wrong context path

The context path normally comes from the deployed WAR filename. If the WAR is renamed, the URL changes too. In IntelliJ, check the Tomcat deployment configuration and set the application context explicitly.

### Changes are not visible

Rebuild and redeploy:

```bash
mvn clean package
```

Then restart Tomcat or redeploy the artifact. Also check Tomcat logs for deployment errors.

## Development Notes and Limitations

This is an instructional application, not a production-ready customer registration system.

- The servlet writes HTML directly with `PrintWriter`; a templating layer would be easier to maintain for a larger application.
- Input is validated for presence, but there is no persistence, authentication, authorization, CSRF protection, or database integration.
- User-provided values are inserted into the generated HTML without HTML escaping. Do not expose this implementation to untrusted users without adding output encoding.
- `doGet()` delegates to `doPost()`. In a production application, GET and POST should normally have distinct semantics.
- The current form offers only three gender values and the server accepts any non-blank value.
- The legacy `web.xml` descriptor contains a Servlet 2.3 doctype, while servlet discovery is handled by the annotation. It is retained as part of the Maven webapp structure.
- The class and project names contain the original spelling `Servelet`/`servelet`; renaming them would require updating URLs, artifact names, or deployment configuration.

## Suggested Next Improvements

Possible learning exercises and production-oriented improvements include:

1. Replace direct HTML generation with JSP or a templating engine.
2. Add HTML escaping for all user-controlled output.
3. Separate validation into a reusable service or validator class.
4. Add automated servlet tests using a mock servlet framework.
5. Add a Maven compiler configuration with an explicit Java release.
6. Add a standard logging framework instead of relying only on container logs.
7. Add persistence only after defining a data model and validation rules.
8. Rename the project from `servelet` to `servlet` in a dedicated change if backward compatibility is not required.

## License

No license is currently declared. Add a license before distributing the project outside its intended learning environment.
