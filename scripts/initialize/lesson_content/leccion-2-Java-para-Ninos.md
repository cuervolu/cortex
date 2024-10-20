# Introducción

![flopa](https://res.cloudinary.com/dukgkrpft/image/upload/v1729456251/lessons/java-para-ninos/vz6yfftlzvxdv0rgonvx.gif)

¡Hola, pequeños programadores de Java! 🧑‍💻 Hoy vamos a aprender cómo se ve un programa en Java. ¡Es como una receta de cocina! Si sigues los pasos en orden, tu computadora hará exactamente lo que le pidas.

## ¿Qué es un programa en Java?

![programa](https://res.cloudinary.com/dukgkrpft/image/upload/v1729456321/lessons/java-para-ninos/fsred689zqicnckuf9xe.jpg)

Un programa en Java es como una carta que le escribes a la computadora. Debes seguir ciertas reglas para que entienda lo que le estás diciendo.

## Ejemplo

```java
public class MiPrograma {
    public static void main(String[] args) {
        System.out.println("¡Hola, mundo!");
    }
}
```

En este programa:

- **`public class MiPrograma`**: Es el nombre de tu programa.
- **`public static void main(String[] args)`**: Es el punto de inicio del programa, donde comienza a ejecutarse.
- **`System.out.println`**: Es el comando que le dice a la computadora que muestre un mensaje.

## Ejercicio 1: Crea tu propio programa

Ahora es tu turno de crear un programa en Java. Escribe un programa que le diga a la computadora que muestre tu nombre.

1. Abre un archivo nuevo y llámalo `MiNombre.java`.
2. Escribe este código:

   ```java
   public class MiNombre {
       public static void main(String[] args) {
           System.out.println("¡Hola, mi nombre es [tu nombre]!");
       }
   }
   ```

3. Reemplaza `[tu nombre]` por tu propio nombre.
4. Ejecuta el programa y mira el resultado en la pantalla.

## Lección 2: Operaciones básicas en Java

### Introducción de Operaciones

En Java, podemos hacer muchas cosas con números, como sumar, restar y multiplicar. Estas son las **operaciones básicas**, y Java es muy bueno en matemáticas.

### ¿Qué son las operaciones?

Una operación es como una tarea matemática que la computadora puede hacer. Puedes pedirle a Java que sume, reste, multiplique o divida.

```java
public class Operaciones {
    public static void main(String[] args) {
        int suma = 5 + 3;
        int resta = 5 - 3;
        int multiplicacion = 5 * 3;
        int division = 5 / 3;

        System.out.println("Suma: " + suma);
        System.out.println("Resta: " + resta);
        System.out.println("Multiplicación: " + multiplicacion);
        System.out.println("División: " + division);
    }
}
```

Este programa realiza operaciones con números y muestra los resultados.

## Ejercicio 2: Juega con los números

Escribe un programa en Java que sume tu edad y el número de hermanos que tienes.

1. Crea un archivo nuevo llamado `SumaEdades.java`.
2. Escribe este código:

   ```java
   public class SumaEdades {
       public static void main(String[] args) {
           int miEdad = [tu edad];
           int hermanos = [número de hermanos];
           int total = miEdad + hermanos;
           
           System.out.println("La suma de mi edad y mis hermanos es: " + total);
       }
   }
   ```

3. Reemplaza `[tu edad]` y `[número de hermanos]` por tus propios números.
4. Ejecuta el programa para ver el resultado.

## Lección 3: Condiciones en Java

### Introducción condiciones

En Java, usamos **condiciones** para tomar decisiones. Es como cuando decides si te pones un abrigo si hace frío. Las condiciones ayudan a que la computadora sepa qué hacer en cada situación.

### ¿Qué son las condiciones?

Una condición es una regla que la computadora sigue. En Java, podemos usar la palabra clave `if` para crear una condición.

### Ejemplo condición

```java
public class Condiciones {
    public static void main(String[] args) {
        int temperatura = 20;
        
        if (temperatura > 25) {
            System.out.println("Hace calor, usa una camiseta.");
        } else {
            System.out.println("Hace frío, usa un abrigo.");
        }
    }
}
```

## Ejercicio 3: Crea tus propias condiciones

Escribe un programa en Java que decida si puedes ir al parque dependiendo del clima. Si está soleado, imprime "Puedes ir al parque". Si está lluvioso, imprime "Quédate en casa".

1. Usa una variable para el clima (`String`).
2. Usa `if` y `else` para crear la decisión.

### Ejemplo condicion propia

```java
public class Clima {
    public static void main(String[] args) {
        String clima = "soleado";
        
        if (clima.equals("soleado")) {
            System.out.println("Puedes ir al parque.");
        } else {
            System.out.println("Quédate en casa.");
        }
    }
}
```

## Lección 4: Ciclos en Java

### Introducción de ciclos

A veces necesitamos repetir algo muchas veces, como contar del 1 al 10. En Java, usamos **ciclos** para hacer que la computadora repita acciones.

### ¿Qué son los ciclos?

Un ciclo es una forma de decirle a la computadora que repita algo varias veces. En Java, usamos `for` para crear un ciclo.

```java
public class Ciclos {
    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println("Número: " + i);
        }
    }
}
```

## Ejercicio 4: Usa ciclos para contar

Escribe un programa en Java que cuente del 1 al 5 y muestre los números en la pantalla.

1. Usa un ciclo `for` para contar.
2. Muestra cada número usando `System.out.println`.

```java
public class Contador {
    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Número: " + i);
        }
    }
}
```

## Lección 5: Tu primer proyecto en Java

Ahora que has aprendido sobre variables, condiciones y ciclos, vamos a crear un proyecto divertido. Vamos a hacer que la computadora adivine un número que estás pensando.

### Proyecto: Adivina el número

Crea un programa que pida a un jugador que piense en un número del 1 al 10, y luego haga que la computadora trate de adivinarlo.

1. Usa una variable para el número secreto.
2. Usa un ciclo `while` para que la computadora siga adivinando hasta que acierte.

![Yipi](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)
