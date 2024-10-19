# Introducción

![java](https://res.cloudinary.com/dukgkrpft/image/upload/v1729370701/lessons/Tu-primera-taza-de-java/ezk3tmgablr0xhixr07x.png)

¡Bienvenidos, futuros maestros del código! Hoy vamos a preparar nuestra primera taza de Java. Aunque
en Cortex todo está listo para que empieces a programar directamente en la web, es importante que
conozcas cómo se configura un entorno de desarrollo Java en tu propia computadora. ¡Vamos allá!

## Configuración del entorno de desarrollo

> **INFO**
>
> ### Nota importante sobre Cortex
>
> Antes de sumergirnos en la configuración, es crucial mencionar que en Cortex, todo el entorno de desarrollo está preparado para ti en nuestra aplicación web. No necesitas instalar nada en tu computadora para empezar a programar. Sin embargo, conocer el proceso de configuración te será útil si decides programar en Java fuera de Cortex.

### Versiones de Java

Java evoluciona constantemente, como un buen café que se perfecciona con el tiempo:

- La última versión oficial es Java 22.

- Java 23 está en camino.

- Muchas empresas siguen usando versiones anteriores, incluso Java 8 (¡es como seguir usando una
  cafetera de los años 90!).

- En Cortex, trabajamos con Java 21, una versión moderna y estable.

![cafe](https://res.cloudinary.com/dukgkrpft/image/upload/v1729370566/lessons/Tu-primera-taza-de-java/jkvciqtcgbblcp7kixi7.jpg)

### Pasos para configurar Java en tu computadora

1. Descarga el [JDK](<https://www.oracle.com/cl/java/technologies/downloads/>) (Java Development
   Kit) de la página oficial de Oracle o [https://jdk.java.net/](<https://jdk.java.net/>).

2. Instala el JDK siguiendo las instrucciones para tu sistema operativo.

3. Configura las variables de entorno `JAVA_HOME` y `PATH`.

### IDEs recomendados para Java

Un IDE (Entorno de Desarrollo Integrado) es como tu cocina personalizada para preparar código.
Algunos de los mejores para Java son:

1. **IntelliJ IDEA**: Es el campeón peso pesado y el recomendado por Cortex. Viene en versión
   Community (gratuita) y Ultimate (de pago).

2. **Eclipse**: Un clásico gratuito y de código abierto.

3. **NetBeans**: Otro IDE gratuito y fácil de usar.

4. **Visual Studio Code**: Con las extensiones adecuadas, este editor ligero puede ser un gran
   entorno para Java.

### SDKMAN: El mayordomo de tus SDKs

[SDKMAN](<https://sdkman.io/>) es como un mayordomo para tus Kits de Desarrollo de Software (SDKs).
Te permite manejar múltiples versiones de Java y otras herramientas relacionadas. Es completamente
gratuito y muy útil si necesitas trabajar con diferentes versiones de Java.

## Novedades emocionantes en Java 21

Antes de sumergirnos en el código, hay algunas novedades emocionantes en Java 21 que debes conocer.
¡Es como si hubieran inventado una nueva forma de preparar café!

### Adiós al `public static void main()`

Tradicionalmente, cada programa Java necesitaba un método `main()` como punto de entrada. Era como
la receta estricta que todos teníamos que seguir. Pero Java 21 ha cambiado las reglas del juego:

1. **Ya no es obligatorio el `public static void main()`**: Es como si ahora pudieras preparar tu
   café de formas más creativas.

2. **Mayor flexibilidad**: Esto permite explorar diferentes estilos de programación, como la
   programación funcional.

3. **Código más simple**: Puedes escribir programas Java con menos "ceremonias".

Aquí tienes un ejemplo de cómo se ve un programa simple en Java 21:

```java
class HolaMundo {
    void main() {
        System.out.println("¡Hola, Mundo del Café Java!");
    }
}
```

### Clases sin nombre: La nueva receta rápida

Java 21 introduce las "clases sin nombre", una característica que te permite escribir programas aún
más concisos. Es como preparar un espresso instantáneo, ¡rápido y directo al grano!

Ejemplo de una clase sin nombre:

```java
new {
   System.out.println("¡Hola, Mundo del Café Java!");
}
```

¿Ves lo simple que es? No necesitas declarar una clase ni un método `main()`. Es perfecto para
programas pequeños o para probar ideas rápidamente.

### ¿Por qué es importante?

1. **Accesibilidad**: Hace que Java sea más fácil de aprender y usar.

2. **Modernización**: Mantiene a Java al día con las tendencias actuales de programación.

3. **Flexibilidad**: Te permite elegir el estilo que mejor se adapte a tu proyecto.

En Cortex, aprovechamos estas nuevas características para hacer que tu experiencia de aprendizaje
sea más suave y moderna. Sin embargo, también te enseñaremos la forma tradicional, ya que muchos
proyectos existentes aún la utilizan.

## "Hola Mundo" en Java

Ahora, ¡vamos a servir nuestra primera taza de Java! El clásico programa "Hola Mundo" es como el
primer sorbo de café en la mañana: simple pero emocionante.

```java
public class HolaMundo {
    public static void main(String[] args) {
        System.out.println("¡Hola, Mundo del Café Java!");
    }
}
```

## Estructura básica de un programa Java

Vamos a desglosar nuestro "Hola Mundo" como si fuera una receta de café:

1. **Clase**: `public class HolaMundo { ... }`

    - Es como el recipiente que contiene todo tu café.

    - En Java, todo el código debe estar dentro de una clase.

2. **Método main**: `public static void main(String[] args) { ... }`

    - Es como el botón de "iniciar" en tu cafetera.

    - Todo programa Java comienza a ejecutarse desde aquí.

3. **Declaración de salida**: `System.out.println("¡Hola, Mundo del Café Java!");`

    - Es como verter el café en tu taza.

    - `System.out.println()` imprime texto en la consola.

4. **Punto y coma (;)**

    - Es como el punto final de cada instrucción en tu receta.

    - Cada declaración en Java debe terminar con un punto y coma.

5. **Llaves { }**

    - Son como los paréntesis que agrupan los pasos de tu receta.

    - Definen el inicio y fin de bloques de código.

## Conclusión

¡Felicidades! Has preparado tu primera taza de Java. Aunque en Cortex puedes empezar a programar de
inmediato, conocer estos fundamentos te ayudará a entender mejor cómo funciona Java detrás de
escenas.

En las próximas lecciones, exploraremos más a fondo el fascinante mundo de Java, añadiendo más sabor
y complejidad a nuestras creaciones. ¡Hasta la próxima, futuros baristos del código!
