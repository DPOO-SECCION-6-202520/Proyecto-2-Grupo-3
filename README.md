# Proyecto 2 - Unica
## Diseño y Programación Orientada a Objetos (DPO)

### Grupo 3

#### Integrantes:
- **Juan David Colorado Pérez** - Código: 202415548
- **Juan José Sánchez** - Código: 202325683  
- **Pedro Archila** - Código: 202421572

---

## Estructura del Proyecto

src/
├── main/
│   ├── MainComprador.java 
│   ├── MainOrganizador.java  
│   └── MainAdministrador.java 
├── interfaz/
│   ├── util/
│   │   └── ValidadorEntradas.java 
│   ├── MenuBase.java 
│   └── MenuComprador.java
├── Test/
└── modelo/
    ├── Eventos/
    ├── Pagos/
    ├── persistencia/
    ├── Tiquetes/
    ├── Usuarios/
    └── Aplicain.java

Conservamos el main original pero ademas se tienen los main especificos de cada tipo dentro de la carpeta main, para probarlo puede usar estos login y pasword:

-Comprador: cliente1 / cliente123
-Organizador: promotor1 / promo123
-Administrador: admin / admin123

## Características Técnicas

- **Lenguaje**: Java
- **Paradigma**: Programación Orientada a Objetos


# Ejecutar
java -cp src Main