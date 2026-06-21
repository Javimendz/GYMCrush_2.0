GYMCrush - Android Frontend
GYMCrush es una aplicación móvil de fitness diseñada para ayudar a los usuarios a gestionar sus entrenamientos, seguir tutoriales en vídeo y obtener planes nutricionales personalizados basados en sus objetivos físicos.

✨ Características
🔐 Autenticación Segura: Sistema de Login y Registro con gestión de roles (Usuario y Entrenador) mediante JWT.

👤 Gestión de Perfil: Edición de datos personales, cálculo automático de IMC y carga de foto de perfil (almacenada en Base64).

📹 Tutoriales de Entrenamiento: Reproductor de vídeo integrado con ExoPlayer que guarda automáticamente el progreso de visualización.

🍎 Nutrición Inteligente: Generación de planes dietéticos basados en la fórmula de Mifflin-St Jeor, integrados con la API de FatSecret.

📅 Gestión de Clases: Visualización de horarios y reserva de actividades dirigidas en el gimnasio.

🔔 Notificaciones: Sistema de recordatorios para clases próximas.

🛠️ Stack Tecnológico
Lenguaje: Java 17+

Arquitectura: MVVM (Model-View-ViewModel)

Networking: Retrofit 2 & OkHttp3

Imágenes: Glide (Carga y transformación circular)

Video: ExoPlayer (Google Media3)

UI Components: Material Design 3, Navigation Component, ViewBinding.

🚀 Configuración e Instalación
1. Requisitos Previos
   Android Studio Jellyfish o superior.

Android SDK nivel 34 (UpsideDownCake).

Backend de GYMCrush en ejecución (Spring Boot).

2. Configuración de la API (Conexión con el Servidor)
   Por defecto, la aplicación apunta a localhost a través del túnel del emulador. Si usas un dispositivo físico, cambia la IP en la clase de configuración:

Archivo: data/remote/RetrofitClient.java

Java
// Para emulador Android
private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";

// Para dispositivo físico (ejemplo)
// private static final String BASE_URL = "http://192.168.1.50:8080/api/v1/";
3. Instalación
   Clona este repositorio.

Abre el proyecto en Android Studio.

Sincroniza los archivos de Gradle.

Ejecuta la aplicación en un emulador o dispositivo físico.

📂 Estructura del Proyecto
```
Plaintext
com.example.gymcrush
├── data
│   ├── remote          # Clientes API y Definición de Endpoints (Retrofit)
│   ├── dto             # Objetos de Transferencia de Datos (Request/Response)
│   └── repository      # Lógica de acceso a datos (Single Source of Truth)
├── ui
│   ├── auth            # Login y Registro
│   ├── perfil          # Gestión de perfil y salud
│   ├── nutricion       # Generador de planes y dietas
│   └── tutoriales      # Listado de videos y reproductor
└── utils               # Clases de ayuda (Base64, Validadores, Formateadores)

```