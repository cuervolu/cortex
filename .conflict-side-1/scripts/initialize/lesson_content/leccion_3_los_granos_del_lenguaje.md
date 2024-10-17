## Introducción

¡Bienvenidos, futuros maestros del código Java! Hoy vamos a explorar los ingredientes básicos de
nuestro lenguaje: los tipos de datos y las variables. Imagina que estás en una cafetería gourmet de
programación, donde cada tipo de dato es un grano de café diferente, y cada variable es una taza
esperando ser llenada con un delicioso código. ¡Vamos a sumergirnos en este aromático mundo!

## Tipos de datos primitivos: Nuestro menú de granos selectos

En Java, tenemos ocho tipos de datos primitivos. Son como nuestros granos de café básicos, cada uno
con sus propias características únicas:

1. **byte** \- Nuestro espresso concentrado. Pequeño pero potente, ocupa poco espacio (8 bits) y
   puede almacenar números entre -128 y 127.

2. **short** \- El café cortado. Un poco más grande que byte (16 bits), puede manejar números entre
   -32,768 y 32,767.

3. **int** \- Nuestro café americano estándar. Con 32 bits, es el tipo más común para números
   enteros.

4. **long** \- El café para largas noches de programación. Con 64 bits, puede manejar números
   realmente grandes.

5. **float** \- Café con leche. Para números con decimales (32 bits), pero con precisión limitada.

6. **double** \- Nuestro latte doble. También para decimales, pero con mayor precisión (64 bits).

7. **boolean** \- El interruptor de nuestra cafetera. Solo puede ser true (encendido) o false (
   apagado).

8. **char** \- La letra en el arte latte. Representa un solo carácter Unicode.

```java
byte tamañoShot = 1;
short temperaturaCafe = 85;
int granosUsados = 1000;
long clientesAtendidos = 1000000L;
float precioEspresso = 2.5f;
double piGramos = 3.14159265359;
boolean cafeteriaAbierta = true;
char calificacionCafe = 'A';
```

## Declaración y uso de variables: Preparando nuestras tazas

Declarar una variable es como preparar una taza para tu café. Necesitas especificar qué tipo de
café (dato) contendrá y darle un nombre:

```java
tipo_de_dato nombre_de_variable = valor;
```

Por ejemplo:

```java
int temperaturaIdeal = 92;
String nombreBarista = "Java Junkie";
```

Puedes cambiar el contenido de tu taza (el valor de la variable) en cualquier momento:

```java
temperaturaIdeal = 95; // Ups, nos gusta un poco más caliente
```

## Variables de referencia: Nuestros recipientes especiales

Además de los tipos primitivos, tenemos variables de referencia. Estas son como nuestros termos y
tazas reutilizables que pueden contener estructuras más complejas:

```java
String mezclaDelDia = "Blend Javanés";
StringBuilder pedidoPersonalizado = new StringBuilder("Latte con ");
```

### ¿Qué es un StringBuilder?

Imagina que `StringBuilder` es como una pizarra mágica en la cafetería donde puedes escribir y
modificar fácilmente el pedido de un cliente:

- Es una clase en Java que te permite crear y manipular cadenas de texto de forma más eficiente que
  usando la clase `String` normal.

- A diferencia de `String`, que es inmutable (no se puede cambiar una vez creado), `StringBuilder`
  te permite añadir, eliminar o modificar caracteres sin crear un nuevo objeto cada vez.

```java
StringBuilder pedido = new StringBuilder("Café ");
pedido.append("con leche ");  // Añade al final
pedido.insert(0, "Grande ");  // Inserta al principio
pedido.append("y caramelo");  // Añade más al final
System.out.println(pedido);  // Imprime: "Grande Café con leche y caramelo"
```

Es como si pudieras seguir añadiendo ingredientes a tu pedido sin tener que escribirlo todo de nuevo
cada vez.

## Comentarios en el código: Notas para el barista

Los comentarios son como notas que dejas en la receta de café para ti mismo o para otros baristas.
No afectan el "sabor" del código (su ejecución), pero son súper útiles para explicar qué está
pasando.

Hay tres tipos de comentarios en Java:

**Comentarios de una línea**:

```java
// Esto es un comentario de una línea
int temperatura = 92; // La temperatura ideal para el espresso
```

**Comentarios de múltiples líneas**:

```java
/* Este es un comentario
   que puede abarcar
   múltiples líneas */
```

**Comentarios de documentación (JavaDoc)**

```java
/**
 * Este es un comentario de documentación.
 * Se usa para generar documentación automática del código.
 */
```

## JavaDoc: El manual de tu máquina de café

JavaDoc es una herramienta que viene con el JDK y se usa para generar documentación en formato HTML
a partir de los comentarios en tu código Java. Es como crear un manual de usuario para tu máquina de
café personalizada.

### Cómo hacer JavaDocs:

1. Se colocan justo antes de clases, métodos o campos que quieres documentar.

2. Comienzan con `/**` y terminan con `*/`.

3. Pueden incluir "etiquetas" especiales que comienzan con `@`.

Ejemplo:

```java
/**
 * Representa una taza de café.
 * 
 * @author Barista Java
 * @version 1.0
 */
public class TazaDeCafe {
    private int temperatura;

    /**
     * Prepara la taza de café a una temperatura específica.
     *
     * @param temp La temperatura del café en grados Celsius.
     * @return true si el café se preparó correctamente, false en caso contrario.
     * @throws IllegalArgumentException si la temperatura es menor a 0 o mayor a 100.
     */
    public boolean prepararCafe(int temp) {
        // Código aquí
    }
}
```

### Importancia de JavaDoc:

1. **Documentación clara**: Ayuda a otros programadores (y a ti en el futuro) a entender cómo usar
   tu código.

2. **Generación automática de documentación**: Puedes crear páginas web de documentación con un solo
   comando.

3. **Estándar de la industria**: Muchas empresas y proyectos de código abierto usan JavaDoc.

4. **Mejora la mantenibilidad**: Facilita el mantenimiento y actualización del código a largo plazo.

Usar JavaDoc es como etiquetar claramente cada parte de tu máquina de café, para que cualquiera
pueda entender cómo funciona y cómo usarla correctamente.

## Conversiones de tipos: Cambiando de taza

A veces, necesitamos cambiar nuestro café de una taza a otra. Esto es lo que llamamos conversión de
tipos:

### Conversión implícita (Widening Casting)

Es como pasar café de una taza pequeña a una más grande. Java lo hace automáticamente:

```java
int shotEspresso = 1;
double cantidadEnOnzas = shotEspresso; // Automáticamente convierte int a double
```

### Conversión explícita (Narrowing Casting)

Es como intentar poner el contenido de una taza grande en una más pequeña. Debemos hacerlo
manualmente y con cuidado:

```java
double cantidadEnOnzas = 1.5;
int shotEspresso = (int) cantidadEnOnzas; // Convertimos explícitamente de double a int
```

¡Cuidado! Al hacer esto, podrías perder información (como los decimales en este caso).

## Constantes: Nuestra receta secreta

Si tienes una mezcla perfecta que no quieres cambiar, puedes usar una constante:

```java
final int TEMPERATURA_PERFECTA = 92;
```

Es como escribir tu receta secreta en piedra. Una vez declarada, no puede cambiar.

## Conclusión

¡Felicidades! Ahora conoces los ingredientes básicos para crear tus propias mezclas de código Java.
Recuerda, elegir el tipo de dato correcto es como elegir el grano de café perfecto para cada receta.
En nuestras próximas lecciones, aprenderemos a combinar estos ingredientes para crear programas más
complejos y sabrosos.

¡Hasta la próxima, futuros baristas del código!



