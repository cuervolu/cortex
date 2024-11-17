# Java para Niños - Lección 4: ¡Superhéroes y Métodos! 🦸‍♂️

![superhero](https://res.cloudinary.com/dukgkrpft/image/upload/v1731876846/lessons/superheroes-y-metodos-de-java/mcosmxd9jduvkafrqekr.webp)

¡Hola super programadores! 👋 Hoy vamos a aprender sobre métodos y objetos en Java. ¡Será como crear nuestros propios superhéroes con superpoderes! 🌟

## ¿Qué es un Método? 🎯

Un método es como un superpoder que le das a tu programa. Es algo que tu programa puede hacer una y otra vez cuando lo necesites.

```java
public class Superheroe {
    public static void saludar() {
        System.out.println("¡Hola! ¡Estoy aquí para salvar el día!");
    }
    
    public static void main(String[] args) {
        saludar(); // ¡Llamamos a nuestro método!
    }
}
```

### Ejercicio 1: Crea tu Primer Superpoder 💪

Vamos a crear un método que sea un superpoder. ¡Puede ser lo que tú quieras!

```java
public class MiSuperpoder {
    public static void rayoLaser() {
        System.out.println("¡Pew! ¡Pew! 🔫");
        System.out.println("¡Disparando rayos láser!");
    }
    
    public static void volar() {
        System.out.println("¡Whoosh! ✈️");
        System.out.println("¡Estoy volando por el cielo!");
    }
    
    public static void main(String[] args) {
        System.out.println("¡Activando superpoderes!");
        rayoLaser();
        volar();
    }
}
```

## Métodos con Parámetros: ¡Superpoderes Personalizables! 🎨

Los métodos pueden recibir información para hacerlos más interesantes. Es como darle instrucciones especiales a tu superpoder.

```java
public class SuperpoderPersonalizado {
    public static void superGrito(String mensaje) {
        System.out.println("¡" + mensaje.toUpperCase() + "! 📢");
    }
    
    public static void main(String[] args) {
        superGrito("al rescate");  // Imprime: ¡AL RESCATE! 📢
        superGrito("por la justicia");  // Imprime: ¡POR LA JUSTICIA! 📢
    }
}
```

## ¡Creando Objetos: Tu Propio Superhéroe! 🦹‍♀️

Ahora vamos a crear un superhéroe completo usando objetos. Un objeto es como un personaje que tiene sus propias características y superpoderes.

```java
public class Superheroe {
    // Características de nuestro superhéroe
    String nombre;
    String superpoder;
    int nivelDePoder;
    
    // Constructor: Así creamos un nuevo superhéroe
    public Superheroe(String nombre, String superpoder, int nivelDePoder) {
        this.nombre = nombre;
        this.superpoder = superpoder;
        this.nivelDePoder = nivelDePoder;
    }
    
    // Métodos (superpoderes) de nuestro superhéroe
    public void presentarse() {
        System.out.println("¡Hola! Soy " + nombre + " y mi superpoder es " + superpoder);
    }
    
    public void usarSuperpoder() {
        System.out.println("¡" + nombre + " usa " + superpoder + " con nivel " + nivelDePoder + "!");
    }
    
    public static void main(String[] args) {
        // Creamos un nuevo superhéroe
        Superheroe superChica = new Superheroe("Super Chica", "Vuelo Supersónico", 100);
        
        // ¡Usamos sus poderes!
        superChica.presentarse();
        superChica.usarSuperpoder();
    }
}
```

### Ejercicio 2: Crea tu Liga de Superhéroes 🦸‍♂️🦸‍♀️

¡Ahora es tu turno! Crea varios superhéroes y forma tu propia liga.

```java
public class LigaDeHeroes {
    public static void main(String[] args) {
        // Crea tu equipo de superhéroes
        Superheroe[] liga = new Superheroe[3];
        
        liga[0] = new Superheroe("Chica Veloz", "Super Velocidad", 95);
        liga[1] = new Superheroe("Chico Fuerte", "Super Fuerza", 90);
        liga[2] = new Superheroe("Niña Invisible", "Invisibilidad", 85);
        
        // ¡Presenta a tu equipo!
        System.out.println("¡Presentando a la Liga de Héroes!");
        for (Superheroe heroe : liga) {
            heroe.presentarse();
            heroe.usarSuperpoder();
            System.out.println("---------------");
        }
    }
}
```

## ¡Proyecto Final: Batalla de Superhéroes! ⚔️

Vamos a crear un pequeño juego donde dos superhéroes pueden enfrentarse en una batalla amistosa.

```java
public class BatallaDeHeroes {
    public static void batalla(Superheroe heroe1, Superheroe heroe2) {
        System.out.println("🏟️ ¡COMIENZA LA BATALLA! 🏟️");
        System.out.println(heroe1.nombre + " VS " + heroe2.nombre);
        
        // Calculamos quién gana basado en el nivel de poder
        if (heroe1.nivelDePoder > heroe2.nivelDePoder) {
            System.out.println("¡" + heroe1.nombre + " gana la batalla! 🏆");
        } else if (heroe2.nivelDePoder > heroe1.nivelDePoder) {
            System.out.println("¡" + heroe2.nombre + " gana la batalla! 🏆");
        } else {
            System.out.println("¡Es un empate! 🤝");
        }
    }
    
    public static void main(String[] args) {
        Superheroe superChica = new Superheroe("Super Chica", "Vuelo Supersónico", 100);
        Superheroe superChico = new Superheroe("Super Chico", "Super Fuerza", 95);
        
        batalla(superChica, superChico);
    }
}
```

## Retos Extra: ¡Mejora tu Liga de Superhéroes! 🌟

¿Puedes mejorar nuestro juego de superhéroes? Aquí hay algunas ideas:

1. Agrega más características a los superhéroes (edad, ciudad que protegen, etc.)
2. Crea diferentes tipos de batallas
3. Agrega un sistema de puntos o medallas
4. Crea un torneo de superhéroes

## Conclusión 🎉

¡Felicitaciones! Has aprendido sobre:

- Métodos y cómo crearlos
- Métodos con parámetros
- Objetos y cómo crearlos
- Cómo usar objetos para hacer juegos divertidos

En la próxima lección, ¡aprenderemos sobre más características increíbles de Java!

![celebration](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)

¡Sigue programando y siendo super! 🚀
