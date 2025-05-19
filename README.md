# SensaziónApp 📍

Una aplicación móvil desarrollada en Android con Jetpack Compose que implementa un sistema de autenticación completo con Auth0.

## 📱 Descripción

SensaziónApp es una aplicación Android moderna construida con las mejores prácticas y tecnologías actuales. El proyecto se enfoca en proporcionar una experiencia de usuario fluida con autenticación segura y un diseño intuitivo.

## Video de prueba

https://youtube.com/shorts/B1rupNEHiKc 

## 🚀 Características

- ✅ Autenticación completa con Auth0
- ✅ Interfaz moderna con Jetpack Compose
- ✅ Arquitectura MVVM
- ✅ Navegación declarativa con Navigation Compose
- ✅ Diseño responsive y accesible
- ✅ Manejo de estados con StateFlow

## 🛠️ Tecnologías Utilizadas

- **Kotlin** - Lenguaje principal
- **Jetpack Compose** - UI moderna y declarativa
- **Auth0** - Autenticación y autorización
- **MVVM Architecture** - Patrón de arquitectura
- **Navigation Compose** - Navegación entre pantallas
- **StateFlow** - Manejo reactivo de estados
- **Material Design 3** - Diseño de componentes

## 📂 Estructura del Proyecto

```

```

## 🔐 Configuración de Auth0

### Requisitos previos

1. Cuenta en [Auth0](https://auth0.com/)
2. Aplicación configurada en Auth0 Dashboard
3. Valores de configuración personalizados

## 🎨 Personalización de Auth0

El proyecto incluye personalización del login de Auth0 con:

- Colores coherentes con la marca (#4287F5)
- Logo personalizado
- Bordes redondeados
- Efectos hover mejorados

Para personalizar la página de Auth0, usa el HTML personalizado incluido en la documentación.

## 📱 Pantallas Implementadas

### 1. Splash Screen

- Pantalla de carga inicial (prototipo)
- Diseño base para el branding

### 2. Login Screen

- Autenticación con Auth0
- Diseño moderno con Material Design 3
- Manejo de estados (Loading, Error, Success)

### 3. Register Screen (SignIn)

- Registro de nuevos usuarios
- Misma experiencia visual que Login
- Navegación fluida entre pantallas

### 4. Home Screen

- Pantalla principal post-autenticación
- Información del usuario autenticado
- Opción de cerrar sesión

## 🚧 Sprint Actual - Implementación del módulo de autenticación

### Objetivos del Sprint

✅ **Completado**: Implementación completa del sistema de autenticación

- Creación del Auth0Manager como servicio central
- Implementación de flujos de login y registro
- Desarrollo de screens con Jetpack Compose
- Integración con Auth0 mediante navegador externo

### Tareas Realizadas

1. **🔧 Infraestructura**

   - [x] Configuración de Auth0 en el proyecto
   - [x] Creación de Auth0Manager para centralizar la lógica
   - [x] Implementación de AuthViewModel con StateFlow

2. **🎨 Interfaz de Usuario**

   - [x] LoginScreen con diseño personalizado
   - [x] SignInScreen (registro) con navegación
   - [x] HomeScreen básica con información del usuario
   - [x] Implementación de navegación declarativa

3. **🔄 Flujos de Usuario**

   - [x] Flujo completo de login
   - [x] Flujo completo de registro
   - [x] Manejo de sesiones (login/logout)
   - [x] Navegación entre pantallas

4. **📐 Estados y Navegación**
   - [x] Manejo de estados de carga
   - [x] Manejo de errores de autenticación
   - [x] Navegación automática post-autenticación
   - [x] Persistencia de sesión

### 🎯 Próximos Pasos

- [ ] Conectar Splash Screen con el flujo principal
- [ ] Añadir validaciones adicionales
- [ ] Implementar refresh token automático
- [ ] Mejorar manejo de errores específicos
- [ ] Implementar tests unitarios
- [ ] Optimizar experiencia de usuario

## 🚀 Instalación y Uso

### Requisitos

- Android Studio Hedgehog | 2023.1.1 o superior
- Kotlin 1.9+
- Android API Level 24+

### Pasos de instalación

1. Clona el repositorio:

```bash
git clone [URL_DEL_REPOSITORIO]
cd sensazionapp
```

2. Configura Auth0:

   - Crea una aplicación en Auth0
   - Actualiza las configuraciones en `strings.xml`

3. Sincroniza el proyecto:

```bash
./gradlew sync
```

4. Ejecuta la aplicación:

```bash
./gradlew installDebug
```
