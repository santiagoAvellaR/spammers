# Spammers: Módulo de Alertas y Notificaciones

## 📋 Tabla de Contenido

1. [📖 Acerca del Proyecto](#acerca-del-proyecto)
  - [🔔 Características Principales](#características-principales)
    - [Tipos de Notificaciones](#tipos-de-notificaciones)
  - [⚙️ Funcionalidades Técnicas](#funcionalidades-técnicas)

2. [🚀 Empezando](#empezando)
  - [💻 Requisitos de Sistema](#requisitos-de-sistema)
  - [📦 Dependencias del Proyecto](#dependencias-del-proyecto)

3. [🔧 Instalación del proyecto](#instalacion-del-proyecto)
  - [Clonar el Repositorio](#clonar-el-repositorio)
  - [Configurar Dependencias](#configuración-de-aplicación)
  - [Configuración de Aplicación](#configuración-de-aplicación)
  - [Ejecutar la Aplicación](#ejecutar-la-aplicación)

4. [🔍 Endpoints](#endpoints)
  - [Endpoints de Usuario](#endpoints-de-usuario)
  - [Endpoints de Administrador](#endpoints-de-administrador)

5. [🛠️ Herramientas de Desarrollo](#herramientas-de-desarrollo-adicionales)

6. [🔧 Resolución de Problemas](#resolución-de-problemas)

7. [👥 Colaboradores](#collaborators)

## Acerca del proyecto

El Módulo de Alertas y Notificaciones es un componente crítico del Sistema de Gestión Bibliotecaria desarrollado para el Colegio Nuestra Señora de la Sabiduría. Su objetivo principal es mantener informados a los responsables económicos sobre el estado de los préstamos de libros de los estudiantes, mejorando la comunicación y el seguimiento de las actividades bibliotecarias.

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

## Instalación del Proyecto

### Clonar el Repositorio
```bash  
git clone https://github.com/thesrcielos/spammerscd Alerts-Notification  
  
### Configurar Dependencias  
bash  
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
## Endpoints

El [`SpammersController`](#spammers-controller) y [`AdminController`](#admin-controller) son controladores REST en una   
aplicación Spring Boot diseñado para gestionar notificaciones   
y multas de libros. Este controlador proporciona   
endpoints para interactuar con las notificaciones de   
usuarios, multas y algunas operaciones de préstamos.

### Endpoints de Usuario

| Endpoint | Método | Descripción | Parámetros | Respuesta |
|----------|--------|-------------|------------|-----------|
| `/notifications/users/user/{userId}` | `GET` | Obtener notificaciones de usuario | `userId`: ID de usuario<br>`page`: Número de página<br>`size`: Elementos por página | Notificaciones paginadas |
| `/notifications/users/fines/{userId}` | `GET` | Obtener multas de usuario | `userId`: ID de usuario<br>`page`: Número de página<br>`size`: Elementos por página | Multas paginadas |
| `/notifications/users/mark-seen/{notificationId}` | `PUT` | Marcar notificación como vista | `notificationId`: ID de notificación | Número de filas actualizadas |
| `/notifications/users/count/{userId}` | `GET` | Obtener conteo de notificaciones no leídas | `userId`: ID de usuario | Información de notificaciones |

### Endpoints de Administrador

| Endpoint | Método | Descripción | Parámetros | Respuesta |
|----------|--------|-------------|------------|-----------|
| `/notifications/admin/loan/create` | `POST` | Notificar préstamo | Datos de préstamo (LoanDTO) | "Notification Sent!" |
| `/notifications/admin/loan/return` | `POST` | Devolución de libro | `bookId`: ID de libro<br>`returnedInBadCondition`: Estado del libro | "Book Returned" |
| `/notifications/admin/users/{userId}/fines/create` | `POST` | Crear multa | `userId`: ID de usuario<br>Datos de multa (FineInputDTO) | "Fine Created" |
| `/notifications/admin/users/fines/{fineId}/close` | `PUT` | Cerrar multa | `fineId`: ID de multa | "Fine Closed" |
| `/notifications/admin/fines/{newRate}/rate` | `PUT` | Modificar tasa de incremento de multa | `newRate`: Nueva tasa | "Fine updated Correctly" |
| `/notifications/admin/fines/rate` | `GET` | Consultar tasa de incremento de multa | Ninguno | Tasa de multa |
| `/notifications/admin/fines-pending` | `GET` | Consultar multas pendientes | `page`: Número de página<br>`size`: Elementos por página | Multas pendientes paginadas |
| `/notifications/admin/fines` | `GET` | Consultar multas por fecha | `date`: Fecha de búsqueda<br>`page`: Número de página<br>`size`: Elementos por página | Multas paginadas |

## Estructura de Modelos

### Loan DTO
```json
{
  "userId": "string",
  "emailGuardian": "string",
  "bookId": "string",
  "bookName": "string",
  "loanReturn": "LocalDate"
}
```

### Fine Input DTO
```json
{
  "amount": "number($float)",
  "fineType": "string (DAMAGE, RETARDMENT)",
  "bookId": "string",
  "userId": "string",
  "description": "string"
}
```

### Fine Output DTO
```json
{
  "fineId": "string",
  "description": "string",
  "amount": "number($float)",
  "fineStatus": "string (PENDING, PAID, FORGIVEN)",
  "fineType": "string (DAMAGE, RETARDMENT)",
  "expiredDate": "string($date)",
  "bookTitle": "string",
  "studentName": "string",
  "guardianEmail": "string"
}
```

## Resolución de Problemas
- Verificar versiones de Java y Maven
- Asegurar conexión a base de datos
- Revisar configuraciones de `application.properties`

## Herramientas de Desarrollo Adicionales
- **Documentación API**: Swagger UI (disponible en `/swagger-ui.html`)
- **Cobertura de Código**: JaCoCo
- **Análisis de Código**: SonarCloud configurado

## Colaboradores
[Perfiles de GitHub de los colaboradores]