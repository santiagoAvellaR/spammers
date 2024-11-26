# Spammers: Módulo de Alertas y Notificaciones

## 📋 Tabla de Contenido

1. [📖 Acerca del Proyecto](#acerca-del-proyecto)
  - [🔔 Características Principales](#características-principales)
    - [Tipos de Notificaciones](#tipos-de-notificaciones)
  - [⚙️ Funcionalidades Técnicas](#funcionalidades-técnicas)

2. [🚀 Empezando](#empezando)
  - [💻 Requisitos de Sistema](#requisitos-de-sistema)
  - [📦 Dependencias del Proyecto](#dependencias-del-proyecto)

3. [🔧 Configuración del Entorno de Desarrollo](#configuración-del-entorno-de-desarrollo)
  - [Instalación de JDK](#1-instalación-de-jdk)
  - [Instalación de Maven](#2-instalación-de-maven)
  - [Configuración de Base de Datos](#3-configuración-de-base-de-datos)

4. [📂 Instalación del Proyecto](#instalación-del-proyecto)
  - [Clonar el Repositorio](#clonar-el-repositorio)
  - [Configurar Dependencias](#configurar-dependencias)
  - [Configuración de Aplicación](#configuración-de-aplicación)
  - [Ejecutar la Aplicación](#ejecutar-la-aplicación)

5. [🔍 Endpoints](#endpoints)
  - [1. Obtener Notificaciones](#1-obtener-notificaciones)
  - [2. Obtener Multas](#2-obtener-multas)
  - [3. Notificar Préstamo](#3-notificar-préstamo)
  - [4. Cerrar Préstamo](#4-cerrar-préstamo)
  - [5. Devolución de Libro](#5-devolución-de-libro)

6. [🛠️ Herramientas de Desarrollo](#herramientas-de-desarrollo-adicionales)

7. [🔧 Resolución de Problemas](#resolución-de-problemas)

8. [👥 Colaboradores](#collaborators)
## Acerca del proyecto
El Módulo de Alertas y Notificaciones es un componente crítico 
del Sistema de Gestión Bibliotecaria desarrollado para el 
Colegio Nuestra Señora de la Sabiduría. Su objetivo principal 
es mantener informados a los responsables económicos sobre 
el estado de los préstamos de libros de los estudiantes, 
mejorando la comunicación y el seguimiento de las actividades 
bibliotecarias.

## Características Principales

### Tipos de Notificaciones

1. **Notificación de Préstamo Realizado**
  - Enviada automáticamente al responsable económico
  - Incluye detalles del libro prestado
  - Indica la fecha límite de devolución

2. **Recordatorio de Préstamo por Vencer**
  - Enviado 3 días antes de la fecha límite de devolución
  - Alerta preventiva para evitar retrasos

3. **Notificación de Préstamo Vencido**
  - Generada el mismo día del vencimiento del préstamo
  - Informa sobre el incumplimiento de la fecha de devolución

4. **Notificación de Multa** (Opcional)
  - Se activa si se establece una política de multas
  - Detalla el monto adeudado por días de retraso

## Funcionalidades Técnicas

- Integración con el Módulo de Gestión de Estudiantes y Responsables
- Envío automático de notificaciones basado en eventos de préstamo
- Uso del correo electrónico como medio principal de comunicación
- Seguimiento detallado del estado de los préstamos

## Empezando
### Requisitos de Sistema
- **Java Development Kit (JDK)**: Versión 17
- **Maven**: Versión 3.6.3 o superior
- **IDE Recomendado**:
  - IntelliJ IDEA
  - Visual Studio Code
  - Spring Tool Suite
- **Base de Datos**: PostgreSQL (Versión compatible con la última versión del conector)

### Dependencias del Proyecto
El proyecto utiliza las siguientes tecnologías principales:
- Spring Boot 3.3.5
- Spring Cloud 2023.0.3
- PostgreSQL
- Spring Security
- Spring Data JPA
- Lombok
- JUnit 5
- SpringDoc OpenAPI

## Configuración del Entorno de Desarrollo

### 1. Instalación de JDK
1. Descargar e instalar OpenJDK 17
  - [Descargar OpenJDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. Configurar variable de entorno JAVA_HOME

### 2. Instalación de Maven
1. Descargar Maven
  - [Descargar Apache Maven](https://maven.apache.org/download.cgi)
2. Configurar variable de entorno PATH

### 3. Configuración de Base de Datos
- Instalar PostgreSQL
- Crear base de datos para el proyecto
- Configurar credenciales en `application.properties`

## Instalación del Proyecto

### Clonar el Repositorio
```bash
git clone https://github.com/thesrcielos/spammers
cd Alerts-Notification
```

### Configurar Dependencias
```bash
# Limpiar y compilar el proyecto
mvn clean install

# Instalar dependencias
mvn dependency:resolve
```

### Configuración de Aplicación

#### Archivo de Configuración
Crear/Editar `src/main/resources/application.properties`:
```properties
# Configuraciones de base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_base_datos
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Configuraciones de JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuraciones de correo servicio
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=tu_correo
spring.mail.password=tu_contraseña
```

### Ejecutar la Aplicación
```bash
# Ejecutar la aplicación
mvn spring-boot:run

# Generar archivo JAR
mvn package
```

## Perfiles de Ejecución
- **Desarrollo**: Usar configuración de desarrollo
- **Producción**: Configurar variables de entorno específicas

## Herramientas de Desarrollo Adicionales
- **Documentación API**: Swagger UI (disponible en `/swagger-ui.html`)
- **Cobertura de Código**: JaCoCo
- **Análisis de Código**: SonarCloud configurado

## Resolución de Problemas
- Verificar versiones de Java y Maven
- Asegurar conexión a base de datos
- Revisar configuraciones de `application.properties`

## Notas Adicionales
- Proyecto excluye de cobertura de código:
  - Clases de excepciones
  - Clases de modelo
  - Clases de configuración
  - Implementaciones de servicios específicos
## Uso
El `SpammersController` es un controlador REST en una 
aplicación Spring Boot diseñado para gestionar notificaciones 
y préstamos de libros. Este controlador proporciona 
endpoints para interactuar con las notificaciones de 
usuarios, multas y operaciones de préstamos.

##   Endpoints

### 1. Obtener Notificaciones
- **URL**: `/notifications/notifications`
- **Método**: GET
- **Parámetros**:
  - `userId` (String, requerido): Identificador del usuario
- **Respuesta**: Lista de `NotificationModel`
- **Código de Estado**: 200 OK

### 2. Obtener Multas
- **URL**: `/notifications/fines`
- **Método**: GET
- **Parámetros**:
  - `userId` (String, requerido): Identificador del usuario
- **Respuesta**: Lista de `FineModel`
- **Código de Estado**: 200 OK

### 3. Notificar Préstamo
- **URL**: `/notifications/notify-loan`
- **Método**: POST
- **Cuerpo de la Solicitud**: `LoanDTO`
  - Contiene información del préstamo:
    - ID de usuario
    - ID de libro
    - Correo electrónico del padre
    - Nombre del libro
    - Fecha de devolución
- **Respuesta**: Mensaje de texto "Notification Sent!"
- **Código de Estado**: 200 OK

### 4. Cerrar Préstamo
- **URL**: `/notifications/close-loan`
- **Método**: PUT
- **Parámetros**:
  - `bookId` (String, requerido): Identificador del libro
  - `userId` (String, requerido): Identificador del usuario
- **Respuesta**: Mensaje de texto "Loan Closed!"
- **Código de Estado**: 200 OK

### 5. Devolución de Libro
- **URL**: `/notifications/create-return`
- **Método**: POST
- **Parámetros**:
  - `bookId` (String, requerido): Identificador del libro
  - `returnedInBadCondition` (boolean, requerido): Indica si el libro fue devuelto en mal estado
- **Respuesta**: Mensaje de texto "Book Returned"
- **Código de Estado**: 200 OK
- **Excepción**: Lanza `SpammersPrivateExceptions` si no se encuentra el registro de préstamo

## Dependencias
- `NotificationService`: Servicio que implementa la lógica de negocio para notificaciones y préstamos

## Consideraciones
- Utiliza anotaciones de Spring Boot para definir endpoints REST
- Maneja diferentes escenarios de notificaciones relacionadas con préstamos de libros
- Proporciona endpoints para gestionar el ciclo de vida de un préstamo

## Ejemplos de Uso

### Obtener Notificaciones
```http
GET /notifications/notifications?userId=user123
```

### Notificar Préstamo
El `LoanDTO` (Data Transfer Object) es un objeto que encapsula la información necesaria para generar una notificación de préstamo de libro.

### Estructura del Modelo

| Campo | Tipo | Descripción | Ejemplo |
|-------|------|-------------|---------|
| `userId` | String | Identificador único del estudiante que realiza el préstamo | "user123" |
| `emailGuardian` | String | Correo electrónico del responsable económico | "parent@example.com" |
| `bookId` | String | Identificador único del libro prestado | "book456" |
| `bookName` | String | Nombre o título del libro | "Libro de Matemáticas" |
| `loanReturn` | LocalDate | Fecha límite para la devolución del libro | "2024-02-15" |

#### Ejemplo de Uso:
```http
POST /notifications/notify-loan
Content-Type: application/json

{
  "userId": "user123",
  "emailGuardian": "parent@example.com", 
  "bookId": "book456",
  "bookName": "Libro de Matemáticas",
  "loanReturn": "2024-02-15"
}
```

## Collaborators
Our GitHub profiles.