
# Skynet
Repositorio para mi proyecto, donde realizo el backend del mismo.
=======
<p align="center">
  <a href="http://nestjs.com/" target="blank"><img src="https://nestjs.com/img/logo-small.svg" width="120" alt="Nest Logo" /></a>
</p>
<br>
<br>
<br>

# <p align="center">**Skynet**</p>Sistema de acceso al Gimnasio, reservas, Salud y entrenamientos

##  **Descripción del Proyecto**


_**Skynet**_ es una aplicación **app móvil** completa para la gestión integral de **gimnasios**, desarrollada como **Trabajo Fin de Grado**. El sistema proporciona una solución escalable para el acceso al gimnasio, administrar usuarios, entrenamientos, reservas de clases, seguimiento de salud y bienestar, con un sistema de roles y permisos basado en JWT.

---


Pasos que hemos realizado para el proyecto.
----------------------------------------

```1.``` Crear un ```Dockerfile``` y un ```docker-compose.yaml```  con su configuración para la dockerización  
```2.``` Añadir dependencia de ```postgresql``` en el ```pom.xml```  
```3.``` Añadir dependencia de ```spring-boot-starter-web``` en el ```pom.xml```   
```4.``` Añadir dependencia de ```spring-boot-starter-validation``` en el ```pom.xml``` Validaciones  
```5.``` Añadir dependencia de ```spring-boot-starter-data-jpa``` en el ```pom.xml``` Para seguridad   
```6.``` Añadir dependencia de ```spring-boot-starter-websockets``` en el ```pom.xml``` Para websockets   
```7.``` Añadir dependencia de ```springdoc-openapi-starter-webmvc-ui``` en el ```pom.xml``` Para swagger  
```8.``` Configurar la conexión a la base de datos en el ```application.properties``` con variables de entorno.    
```9.``` Añadir dependencia de ``` MapStruct ``` en el ```pom.xml```  
```10.``` Generación del Esquema desde el Código". Usar entidades para crear la BBDD, la sincronizacion y el mapeo de codigo.    
```11.``` Crear arquitectura de carpetas con un patrón de diseño ```MVC```.  
```12.``` Añadir swagger para la documentacion  

Gran parte del proyecto ha sido realizado con la ayuda de este curso: https://cursos.devtalles.com/courses/spring-boot
```
Bcrypt para encriptar contraseñas: https://www.youtube.com/watch?v=9hMvY9nW3O0&t=25s  
Mvnrepository.com - MapStruct  
Nota : MapStruct es una biblioteca de mapeo de objetos que simplifica la copia de datos entre objetos de diferentes tipos. Lo utilizamos para mapear entre objetos de entidad y objetos de transferencia de datos (DTO).  
```

`notificaciones con websockets`: https://www.youtube.com/watch?v=sqYqyr6EpAU

`Testear websockets sin postman`: https://piehost.com/websocket-tester

`Logs con SLF4J y Logback ` - Sistema de logeo profesional con estas tecnologias.

`WorkoutX Exercise API en RapidAPI`

`Libreria ZXing para generar qr en android`: https://github.com/zxing/zxing-android-embedded

Probar notificaciones: Como postman no soporta Websockets con STOMP, uso un html personlizado para pegar token del usuario y probar notificaciones, que esta 
dentro del proyecto (test-ws.html). Introduces el token del usuario, creas un horario y haces una reserva y se creara la notificaión en el navegador, de prueba.


 ## DOCUMENTACION API RESTFUL SWAGGER `: http://localhost:8080/swagger-ui/index.html, https://gymcrush-tfg.onrender.com/swagger-ui/index.html `

# /Comandos

 ```0.``` Exportar extensiones VS Code ``` code --list-extensions > extensiones.txt```, reinstalar las extensiones ```Get-Content plugins.txt | ForEach-Object { code --install-extension $_ }```

 ```1.``` Iniciar todo el proyecto primera vez ```docker compose up --build ```. Descarga imagenes, etc.    

 ```2.``` Iniciar todo rapido ```docker compose up -d ```  
 ```3.``` Detener todo el proyecto ```docker compose down ```  
=======
 ```1.``` Iniciar todo el proyecto primera vez ```docker compose up --build ```. Descarga imagenes, etc.   
 ```2.``` Iniciar todo rapido ```docker compose up -d ```    
 ```3.``` Detener todo el proyecto ```docker compose down ```  
 ```4.``` Detener todo el proyecto y eliminar volúmenes ```docker compose down -v```  

## **Seguridad y Roles**

El sistema utiliza **Spring Security con JWT (JSON Web Tokens)**.

### **Roles Disponibles**
- **ADMIN**: Acceso total a gestión, horarios, actividades y perfiles de salud ajenos.
- **USER**: Acceso a sus propias reservas, su historial de salud y catálogo de actividades. Acceso individual al gimnasio.
- **ENTRENADOR**: Para staff

---

## **Tecnologías Principales**

- **Java 17** / **Spring Boot 3**
- **Spring Security** & **JWT**
- **JPA** / **Hibernate**
- **PostgreSQL**
- **MapStruct** & **Lombok**
- **Docker** para contenerización
- **Swagger** para documentación

**Nota**: Todas las respuestas exitosas siguen el formato de `ApiResponseDto` para facilitar la integración con la App de Android.



## **Instalación y Configuración**

### **1. Clonar el Repositorio**



```bash
git clone https://github.com/Javimendz/Skynet.git
cd Skynet
```

### **2. Configuración de Infraestructura (Docker)**
Asegúrate de tener Docker instalado para levantar la base de datos PostgreSQL.

```bash
# Levantar base de datos y proyecto
docker compose up --build 
```

### **3. Variables de Entorno**
Configura `application.properties` con las siguientes claves:

### USUARIO ADMIN:

```
  usuario: admin
  password: admin1234

```

```properties
# CORS
app.cors.origins: Orígenes permitidos (ej. http://localhost:8080)

# JWT
JWT_SECRET: Clave secreta para la firma de tokens
JWT_EXPIRATION: Tiempo de vida del token
```


# Levantar proyecto
El proyecto se puede desplegar fácilmente utilizando Docker. Asegúrate de tener Docker instalado y ejecuta el siguiente comando en la raíz del proyecto:

```bash
# Levantar base de datos y proyecto con docker
docker compose up --build 
```

### **4. Acceso a la API**
- **Backend API**: `http://localhost:8080`
- **Base de Datos**: `http://localhost:5432`
- **PgAdmin**: `http://localhost:5050`

# Iniciar sesión en Docker Hub

```
  - docker login

```

# Construir y subir la imagen (Backend)

```
  - cd gym-backend
  - docker build -t tu_usuario/gymc-backend:latest .
  - docker push tu_usuario/gym-backend:latest
```

# Construir y subir la imagen (BD)

```
  - cd gym-db
  - docker build -t tu_usuario/gym-db:latest .
  - docker push tu_usuario/gym-db:latest
```


2. Puesta en marcha (VPS)

En el servidor, no clonamos todo el código, solo necesitamos los archivos de configuración (docker-compose.yaml y .env):

Bash
# Crear red y descargar imagen

  - docker network create red_fichaje
  - docker pull tu_usuario/fichaday-backend:latest

---

## **Endpoints de la API**

### **AUTENTICACIÓN (AUTH)**

| Método | Endpoint | Descripción | Requerimientos / Body |
|--------|------------|-------------|----------------------|
| POST | `/api/v1/auth/login` | Inicio de sesión y obtención de JWT | `{ username, password }` |
| POST | `/api/v1/auth/register` | Registro público de nuevos usuarios | `RegisterDto` |

### **ACTIVIDADES (ACTIVITIES)**

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|------------|-------------|----------------------|
| GET | `/api/v1/actividades` | Listar todas las actividades disponibles | Token (USER/ADMIN) |
| GET | `/api/v1/actividades/{id}` | Ver detalle de una actividad específica | Token (USER/ADMIN) |
| POST | `/api/v1/actividades` | Crear nueva actividad (Crossfit, Yoga, etc.) | Token (ADMIN) + `ActividadRequestDto` |
| PUT | `/api/v1/actividades/{id}` | Actualizar datos de una actividad | Token (ADMIN) + Body |
| DELETE | `/api/v1/actividades/{id}` | Eliminar actividad del catálogo | Token (ADMIN) |
| GET | `/api/v1/actividades/buscar` | Buscar actividades por nombre | Query: `?nombre=zumba` |
| GET | `/api/v1/actividades/dia/{dia}` | Filtrar actividades por día de la semana | Path: `/dia/LUNES` |
| GET | `/api/v1/actividades/precio-max` | Filtrar por presupuesto máximo | Query: `?precio=20` |

### **HORARIOS (SCHEDULES)**

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|------------|-------------|----------------------|
| GET | `/api/v1/horarios` | Listar todos los horarios del gimnasio | Public / Token |
| GET | `/api/v1/horarios/dia/{dia}` | Listar clases disponibles para un día | Path: `/dia/MARTES` |
| POST | `/api/v1/horarios` | Programar una nueva clase | Token (ADMIN) + `HorarioRequestDto` |
| PUT | `/api/v1/horarios/{id}` | Modificar hora o aforo de una clase | Token (ADMIN) + Body |
| DELETE | `/api/v1/horarios/{id}` | Cancelar/Eliminar un horario | Token (ADMIN) |

### **RESERVAS (BOOKINGS)**

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|------------|-------------|----------------------|
| POST | `/api/v1/reservas/usuario/{uId}/horario/{hId}` | Realizar una reserva de clase | Token (USER/ADMIN) |
| GET | `/api/v1/reservas/usuario/{id}` | Ver historial de reservas del usuario | Token (USER/ADMIN) |
| PATCH | `/api/v1/reservas/{id}/cancelar` | Cancelar una reserva activa | Token (USER/ADMIN) |


### **SALUD Y PROGRESO (HEALTH)**

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|------------|-------------|----------------------|
| POST | `/api/v1/salud/usuario/{id}` | Registrar nuevas medidas (Peso, IMC, etc.) | Token (USER/ADMIN) + Body |
| GET | `/api/v1/salud/usuario/{id}` | Ver estado de salud actual/último registro | Token (USER/ADMIN) |
| GET | `/api/v1/salud/usuario/{id}/historial` | Ver evolución histórica del usuario | Token (USER/ADMIN) |
| DELETE | `/api/v1/salud/usuario/{id}` | Eliminar un registro médico/salud | Token (USER/ADMIN) |

### **SOPORTE (TICKETS)**

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|------------|-------------|----------------------|
| POST | `/api/v1/tickets/usuario/{usuarioId}` | Abrir un nuevo ticket de soporte/duda | Token (USER/ADMIN) + Body |
| GET | `/api/v1/tickets/usuario/{usuarioId}` | Ver mis tickets enviados | Token (USER/ADMIN) |
| GET | `/api/v1/tickets` | Panel de gestión de tickets | Token (ADMIN) |
| PATCH | `/api/v1/tickets/{id}/estado` | Cambiar estado (ABIERTO, CERRADO, etc.) | Token (ADMIN) + Query: `nuevoEstado` |

---


## **GESTIÓN DE USUARIOS (ACCOUNT MANAGEMENT)**

Control centralizado de cuentas de acceso, roles y credenciales.

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|------------|-------------|----------------------|
| GET | `/api/v1/usuario` | Lista todos los usuarios (IDs y usernames) | Token (ADMIN / USER) |
| POST | `/api/v1/usuario/crear` | Alta manual de usuario + Roles + Encriptación | Token (ADMIN) + `UsuarioRequestDto` |
| GET | `/api/v1/usuario/{id}` | Obtiene los detalles de cuenta de un usuario | Token (ADMIN / USER) |
| PUT | `/api/v1/usuario/{id}` | Actualiza credenciales, roles o contraseña | Token (ADMIN / USER) + Body |
| DELETE | `/api/v1/usuario/{id}` | Eliminación física del usuario de la base de datos | Token (ADMIN) |

---

## **PERFIL PERSONAL Y SALUD (BIO-PROFILE)**

Información detallada del deportista, vinculada automáticamente con sus últimas mediciones de salud.

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|------------|-------------|----------------------|
| GET | `/api/v1/perfil` | Listado pro de perfiles (incluye Peso, IMC y Actividad) | Token (ADMIN) |
| GET | `/api/v1/perfil/{id}` | Obtiene la ficha personal completa del usuario | Token (ADMIN / USER) |
| PUT | `/api/v1/perfil/{id}` | Actualiza datos personales (DNI, Teléfono, Dirección) | Token (ADMIN / USER) + `PerfilRequestDto` |

---

## **TUTORIALES Y TÉCNICAS (TRAINING GUIDES)**

Base de conocimientos con videos y guías paso a paso para los ejercicios.

## **API Endpoints**

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|----------|-------------|----------------------|
| GET | `/api/v1/tutoriales` | Listado de todos los videos de entrenamiento | Token (USER/ADMIN) |
| GET | `/api/v1/tutoriales/categoria/{id}` | Filtrar videos (ej: Pecho, Espalda, Cardio) | Token (USER/ADMIN) |
| POST | `/api/v1/tutoriales` | Subir nuevo video y guía técnica | Token (ADMIN) + TutorialRequestDto |
| GET | `/api/v1/tutoriales/categorias` | Listar categorías de entrenamiento | Token (USER/ADMIN) |




## **CATEGORÍAS DE TUTORIALES**

Organización de los videos por grupos musculares o tipos de entrenamiento.

## **API Endpoints**

| Método | Endpoint | Descripción | Requerimientos / Auth |
|--------|----------|-------------|----------------------|
| GET | `/api/v1/categorias-tutorial` | Listar todas las categorías con contador de vídeos | Token (USER/ADMIN) |
| POST | `/api/v1/categorias-tutorial` | Crear nueva categoría para organizar vídeos | Token (ADMIN) + CategoriaTutorialRequestDto |



## **CONTROL DE ACCESO (QR DINÁMICO)**

Gestión de entradas y salidas mediante tokens temporales para control de aforo y seguridad.

## **API Endpoints**

| Método | Endpoint | Descripción | Requerimientos / Auth |
|---|---|---|---|
| GET | `/api/v1/accesos/generar-qr` | Solicita un token JWT con TTL de 30s para generar el código QR. | Token (USER/ADMIN) |
| POST | `/api/v1/accesos/validar-entrada` | Valida el QR escaneado y registra el inicio de la sesión de entrenamiento. | Token (ADMIN) + AccesoRequestDto |
| POST | `/api/v1/accesos/validar-salida` | Valida el QR y cierra la sesión activa registrando la hora de salida. | Token (ADMIN) + AccesoRequestDto |
| GET | `/api/v1/accesos/historial` | Lista todas las sesiones (entrada/salida) del usuario autenticado. | Token (USER/ADMIN) |
| GET | `/api/v1/accesos/aforo` | Devuelve el número de usuarios con sesión activa (sin hora de salida). | Token (USER/ADMIN) |


### **MATERIAL USADO PARA WEBSOCKETS**

- `https://www.youtube.com/watch?v=KAm6I_iLXOI`

### **websockets en postman:** 
- `https://www.youtube.com/watch?v=aSPHr6dbMmo`

### **Tutorial para websockets y testeo:** 
- `https://pasksoftware.com/spring-boot-websocket/`

### **extension:  Simple Web Socket Client**
- `https://chromewebstore.google.com/detail/simple-websocket-client/pfdhoblngboilpfeibdedpjgfnlcodoo`

 para probar los websockets con stomp

### Para la sugerencia de dietas, se utilizara fastsecret(Pasos):

#### ir a la página de desarrolladores de FatSecret (FatSecret Platform API) y regístrarse.

    Crea una nueva aplicación allí.

    Me darán dos cosas clave: un Client ID y un Client Secret.

#### Configurar application.properties

    añadir esto :
    
      # Integración FatSecret
      fatsecret.client.id=${FATSECRET_CLIENT_ID:tu_client_id_aqui}
      fatsecret.client.secret=${FATSECRET_CLIENT_SECRET:tu_client_secret_aqui}
      fatsecret.oauth.url=https://oauth.fatsecret.com/connect/token
      fatsecret.api.url=https://platform.fatsecret.com/rest/server.api

---    

Tutoriales utilizados para la implementación del jwt: 
- `https://www.youtube.com/watch?v=KYNR5js2cXE`

- `https://www.youtube.com/watch?v=ZzpDyIJizjo`


Ciclo de vida del token QR:

```
  El usuario solicita su acceso, el método generateQrToken() crea un JWT 30s de vida, firmado con su ID. Al ser sin estado, el token no se guarda en la BD, lo que hace el proceso ultra rápido.

  La App convierte ese jwt en un código QR, el escáner del gimnasio lee el texto y lo envía mediante un POST al endpoint de Validación del servidor.

  El servicio, verifica la firma y la expiración del token. Si es válido, crea una fila en la tabla accesos con el usuario y la hora actual, para llevar el historial y control de usuarios.

  El backend responde con un JSON. Según la respuesta, la App muestra exito o fracaso.

```


#  
<br>
<br>
<br>
<p align="center">
  <img src="https://nestjs.com/img/logo-small.svg" width="80" alt="API Logo" />
</p>
<h1 align="center">📑 DOCUMENTACIÓN TÉCNICA: <b>Skynet-APP API v1</b></h1>

---

## **1.  SEGURIDAD Y ACCESO (AUTH)**

### **1.1 Inicio de Sesión**
- **Método**: `POST`
- **Endpoint**: `/api/v1/auth/login`
- **Descripción**: Autenticación de usuarios y generación de JWT.

```http
POST - http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "username": "admin_gym",
  "password": "password123"
}
```

### **1.2 Registro Público**
- **Método**: `POST`
- **Endpoint**: `/api/v1/auth/register`
- **Descripción**: Registro de nuevos clientes con creación automática de perfil.

```http
POST - http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "username": "usuario_nuevo",
  "password": "password123",
  "correo": "cliente@skynet.com",
  "nombre": "Carlos",
  "apellidos": "Ruiz",
  "telefono": "600111222",
  "dni": "12345678Z",
  "direccion": "Calle Mayor 1",
  "ciudad": "Madrid",
  "pais": "España",
  "codigoPostal": "28001",
  "fechaNacimiento": "1990-05-20",
  "genero": "MASCULINO",
  "rol": ["USER"]
}
```

---

## **2.  CATÁLOGO DE ACTIVIDADES**

### **2.1 Crear Actividad**
- **Método**: `POST`
- **Endpoint**: `/api/v1/actividades`
- **Descripción**: Alta de nueva disciplina (Solo ADMIN).

```http
POST - http://localhost:8080/api/v1/actividades
Content-Type: application/json
Authorization: Bearer {token}

{
  "nombre": "Boxeo",
  "descripcion": "Entrenamiento de contacto y cardio",
  "tipo": "COMBATE",
  "precio": 45
}
```

### **2.2 Búsquedas Filtradas**
- **Método**: `GET`
- **Endpoints**:
  - `/api/v1/actividades/buscar?nombre=zumba`
  - `/api/v1/actividades/dia/LUNES`
  - `/api/v1/actividades/precio-max?precio=30`

---

## **3.  PLANIFICACIÓN DE HORARIOS**

### **3.1 Crear Horario**
- **Método**: `POST`
- **Endpoint**: `/api/v1/horarios`
- **Descripción**: Asignar actividad a una franja temporal (Solo ADMIN).

```http
POST - http://localhost:8080/api/v1/horarios
Content-Type: application/json
Authorization: Bearer {token}

{
  "actividadId": 2,
  "diaSemana": "MIERCOLES",
  "horaInicio": "18:00:00",
  "horaFin": "19:00:00",
  "aforoMaximo": 15
}
```

---

## **4.  SISTEMA DE RESERVAS**

### **4.1 Realizar Reserva**
- **Método**: `POST`
- **Endpoint**: `/api/v1/reservas/usuario/{uId}/horario/{hId}`
- **Descripción**: Reserva de plaza en una clase.

```http
POST - http://localhost:8080/api/v1/reservas/usuario/1/horario/5
Authorization: Bearer {token}
```

---

## **5.  SEGUIMIENTO DE SALUD (BIO-METRICS)**

### **5.1 Registrar Mediciones**
- **Método**: `POST`
- **Endpoint**: `/api/v1/salud/usuario/{id}`
- **Descripción**: Guarda peso, estatura y calcula IMC automáticamente.

```http
POST - http://localhost:8080/api/v1/salud/usuario/1
Content-Type: application/json
Authorization: Bearer {token}

{
  "peso": 75.2,
  "estatura": 1.80,
  "nivelActividad": "MUY_ACTIVO",
  "objetivo": "GANAR_MUSCULO"
}
```

---

## **6.  SOPORTE Y TICKETS**

### **6.1 Abrir Ticket**
- **Método**: `POST`
- **Endpoint**: `/api/v1/tickets/usuario/{usuarioId}`
- **Descripción**: Reporte de dudas o problemas técnicos.

```http
POST - http://localhost:8080/api/v1/tickets/usuario/1
Content-Type: application/json
Authorization: Bearer {token}

{
  "asunto": "Problema con la App",
  "mensaje": "No visualizo mis reservas anteriores en el historial."
}
```

### **6.2 Resolver Ticket (ADMIN)**
- **Método**: `PATCH`
- **Endpoint**: `/api/v1/tickets/{id}/estado?nuevoEstado=RESUELTO`
- **Descripción**: Actualiza el estado de la incidencia.

---

## **7. GESTIÓN DE CUENTAS (USUARIOS)**

### **7.1 Crear Usuario Manualmente**
- **Método**: `POST`
- **Endpoint**: `/api/v1/usuario/crear`
- **Descripción**: Creación de cuenta con roles específicos (Solo ADMIN).

```http
POST - http://localhost:8080/api/v1/usuario/crear
Content-Type: application/json
Authorization: Bearer {token}

{
  "username": "entrenador01",
  "password": "securePass123",
  "rol": ["ROLE_ADMIN"],
  "nombre": "Felipe",
  "apellidos": "Mendoza"
}
```

---

## **8.  PERFIL PERSONAL**

### **8.1 Actualizar Perfil**
- **Método**: `PUT`
- **Endpoint**: `/api/v1/perfil/{id}`
- **Descripción**: Modifica datos de contacto y localización.

```http
PUT - http://localhost:8080/api/v1/perfil/1
Content-Type: application/json
Authorization: Bearer {token}

{
  "telefono": "677888999",
  "dni": "87654321X",
  "direccion": "Avenida de la Constitución 45",
  "ciudad": "Sevilla",
  "pais": "España",
  "codigoPostal": "41001"
}
```


## Crear Nuevo Tutorial

**Método:** POST  
**Endpoint:** `/api/v1/tutoriales`  
**Descripción:** Añade un nuevo video tutorial al catálogo (Solo ADMIN).

### Request

```http
POST http://localhost:8080/api/v1/tutoriales
```

**Body (JSON):**
```json
{
  "titulo": "Técnica Perfecta: Sentadilla Búlgara",
  "descripcion": "En este video explicamos cómo posicionar los pies y mantener la espalda recta para maximizar el trabajo de cuádriceps y glúteo.",
  "urlVideo": "https://youtube.com/watch?v=ejemplo123",
  "duracionMin": 5,
  "categoriaId": 2
}
```

## Crear Categoría de Tutorial

**Método:** POST  
**Endpoint:** `/api/v1/tutoriales/categorias`  
**Descripción:** Organizar los videos por grupos musculares o tipos de entreno.

### Request

```http
POST http://localhost:8080/api/v1/tutoriales/categorias
```

**Body (JSON):**
```json
{
  "nombre": "Tren Inferior",
  "descripcion": "Tutoriales enfocados en piernas, glúteos y pantorrillas."
}
```

## Categorías de Tutoriales

### Crear Categoría

**Método:** POST  
**Endpoint:** `/api/v1/categorias-tutorial`  
**Descripción:** Crea una nueva categoría para organizar los vídeos (Solo ADMIN).

#### Request

```http
POST http://localhost:8080/api/v1/categorias-tutorial
```

**Body (JSON):**
```json
{
  "nombre": "Entrenamiento Funcional",
  "descripcion": "Vídeos sobre ejercicios de movilidad y fuerza coordinada."
}
```

### Listar Categorías

**Método:** GET  
**Endpoint:** `/api/v1/categorias-tutorial`  
**Descripción:** Obtiene todas las categorías con el contador de vídeos incluidos.

#### Request

```http
GET http://localhost:8080/api/v1/categorias-tutorial
```

#### Response (JSON)

```json
[
  {
    "id": 1,
    "nombre": "Fuerza",
    "descripcion": "Ejercicios con pesas",
    "cantidadVideos": 12
  }
]
```


### Generación de Token QR (Móvil)

**Método:** GET

**Endpoint:** `/api/v1/accesos/generar-qr`

**Descripción:** Genera un JWT efímero (30 segundos) que contiene la identidad del socio para ser mostrado como código QR.

#### Request
```
GET - http://localhost:8080/api/v1/accesos/generar-qr

```
Authorization: Bearer <TOKEN_DE_SESIÓN_USUARIO>


### Validación de Acceso (Escáner/Torno)

**Método:** POST

**Endpoint:** `/api/v1/accesos/validar-entrada`

**Descripción:** El dispositivo escáner envía el contenido del QR leído para verificar su validez y registrar la entrada en la base de datos.

#### Request
```
POST - http://localhost:8080/api/v1/accesos/validar-entrada

```
Content-Type: application/json

Authorization: Bearer <TOKEN_ADMIN_ESCÁNER>

#### Response (JSON)

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbl9neW0iLCJ1c2VySWQiOjEsInB1cnBvc2UiOiJRUl9BQ0NFU1MiLCJpYXQiOjE3MTExMDU2MDAsImV4cCI6MTcxMTEwNTYzMH0..."
}
 ```





## CONTROL DE ACCESO (QR DINÁMICO)

Gestión de entradas y salidas mediante tokens temporales para control de aforo y seguridad.

---

### Registro de Salida (Escáner / Torno)

- **Método:** `POST`
- **Endpoint:** `/api/v1/accesos/validar-salida`
- **Descripción:** El dispositivo escáner envía el QR leído al finalizar el entrenamiento para cerrar la sesión activa del socio.
- **Headers:**
  - `Content-Type: application/json`
  - `Authorization: Bearer <TOKEN_ADMIN_ESCÁNER>`

**Ejemplo de cuerpo (JSON):**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbl9neW0iLCJ1c2VySWQiOjEsInB1cnBvc2UiOiJRUl9BQ0NFU1MiLCJpYXQiOjE3MTExMDU2MDAsImV4cCI6MTcxMTEwNTYzMH0..."
}
```

---

### Consulta de Historial (Socio / Admin)

- **Método:** `GET`
- **Endpoint:** `/api/v1/accesos/historial`
- **Descripción:** Obtiene la lista cronológica de todas las sesiones de entrenamiento (entradas y salidas) del usuario autenticado.
- **Headers:**
  - `Authorization: Bearer <TOKEN_DE_SESIÓN_USUARIO>`

**Rquest:**

`GET http://localhost:8080/api/v1/accesos/historial`

**Rquest (body):**

```json
{
  "mensaje": "Historial recuperado",
  "success": true,
  "datos": [
    {
      "id": 10,
      "username": "usuario_gym",
      "fechaHoraEntrada": "2026-03-24T18:00:00",
      "fechaHoraSalida": "2026-03-24T19:30:00",
      "tipo": "SALIDA"
    },
    {
      "id": 11,
      "username": "usuario_gym",
      "fechaHoraEntrada": "2026-03-25T10:15:00",
      "fechaHoraSalida": null,
      "tipo": "ENTRADA"
    }
  ]
}
```

---

### Consulta de Aforo en Tiempo Real

- **Método:** `GET`
- **Endpoint:** `/api/v1/accesos/aforo`
- **Descripción:** Calcula y devuelve el número total de socios que se encuentran actualmente dentro de las instalaciones (sesiones sin marcar salida).
- **Headers:**
  - `Authorization: Bearer <TOKEN_DE_SESIÓN_USUARIO>`

**Rquest:**

`GET http://localhost:8080/api/v1/accesos/aforo`

**Rquest (body):**

```json
{
  "mensaje": "Aforo en tiempo real",
  "success": true,
  "datos": 42
}
```



## **Códigos de Respuesta**

| Código | Descripción |
|---------|-------------|
| 200 | OK - Petición exitosa |
| 201 | Created - Recurso creado |
| 400 | Bad Request - Error de validación |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no existe |
| 500 | Internal Server Error |

---


**API optimizada para integración con app móvil Android**
