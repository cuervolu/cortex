# Java para Niños - Lección 5: ¡El Parque de Diversiones Virtual! 🎡

![parque](https://res.cloudinary.com/dukgkrpft/image/upload/v1731877347/lessons/el-parque-de-diversiones-virtual-el-potencial-de-java/toivtadigtd0xznkee31.jpg)

¡Hola aventureros del código! 👋 En esta última lección, vamos a crear nuestro propio parque de diversiones virtual usando conceptos más avanzados de Java. ¡Prepárense para la diversión! 🎢

## Herencia: ¡Diferentes Tipos de Atracciones! 🎪

La herencia es como cuando diferentes juegos comparten características similares. Por ejemplo, todas las atracciones tienen un nombre y una altura mínima, ¡pero cada una es especial a su manera!

```java
// Clase padre para todas las atracciones
public class Atraccion {
    String nombre;
    int alturaMinima;
    
    public Atraccion(String nombre, int alturaMinima) {
        this.nombre = nombre;
        this.alturaMinima = alturaMinima;
    }
    
    public void mostrarInfo() {
        System.out.println("🎪 " + nombre);
        System.out.println("Altura mínima: " + alturaMinima + " cm");
    }
}

// Montaña rusa hereda de Atraccion
public class MontanaRusa extends Atraccion {
    int velocidadMaxima;
    
    public MontanaRusa(String nombre, int alturaMinima, int velocidadMaxima) {
        super(nombre, alturaMinima);
        this.velocidadMaxima = velocidadMaxima;
    }
    
    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("¡Velocidad máxima: " + velocidadMaxima + " km/h! 🏃‍♂️");
    }
}
```

### Ejercicio 1: Crea tu Propia Atracción 🎨

¡Crea una nueva atracción para nuestro parque!

```java
public class CarrosChocones extends Atraccion {
    int numerosDeCarros;
    
    public CarrosChocones(String nombre, int alturaMinima, int numerosDeCarros) {
        super(nombre, alturaMinima);
        this.numerosDeCarros = numerosDeCarros;
    }
    
    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Número de carros: " + numerosDeCarros + " 🚗");
    }
    
    public void chocar() {
        System.out.println("¡BUMP! 💥 ¡Choque de carros!");
    }
}
```

## Interfaces: ¡Reglas para las Atracciones! 📋

Las interfaces son como reglas que algunas atracciones deben seguir. Por ejemplo, algunas atracciones necesitan que te abroches el cinturón de seguridad.

```java
public interface Seguridad {
    void abrocharCinturon();
    void verificarAltura(int alturaVisitante);
}

public class MontanaRusa extends Atraccion implements Seguridad {
    // ... código anterior ...
    
    @Override
    public void abrocharCinturon() {
        System.out.println("🔐 ¡Cinturón abrochado! ¡Estás seguro!");
    }
    
    @Override
    public void verificarAltura(int alturaVisitante) {
        if (alturaVisitante >= alturaMinima) {
            System.out.println("✅ ¡Altura verificada! ¡Puedes subir!");
        } else {
            System.out.println("❌ Lo siento, necesitas crecer un poco más.");
        }
    }
}
```

## ArrayList: ¡Organizando Nuestro Parque! 📝

ArrayList es como una lista mágica que puede crecer o disminuir. ¡Perfecta para manejar todas nuestras atracciones!

```java
public class ParqueDeDiversiones {
    ArrayList<Atraccion> atracciones = new ArrayList<>();
    
    public void agregarAtraccion(Atraccion atraccion) {
        atracciones.add(atraccion);
        System.out.println("🎉 ¡Nueva atracción agregada: " + atraccion.nombre + "!");
    }
    
    public void mostrarAtracciones() {
        System.out.println("\n🎡 ¡Bienvenidos a nuestro Parque!");
        for (Atraccion atraccion : atracciones) {
            atraccion.mostrarInfo();
            System.out.println("---------------");
        }
    }
}
```

### Ejercicio 2: ¡Administra tu Parque! 🎯

Vamos a crear y administrar nuestro propio parque de diversiones.

```java
public class MiParque {
    public static void main(String[] args) {
        ParqueDeDiversiones parque = new ParqueDeDiversiones();
        
        // Creamos algunas atracciones
        MontanaRusa dragon = new MontanaRusa("El Dragón Veloz", 140, 80);
        CarrosChocones carros = new CarrosChocones("Carros Locos", 120, 10);
        
        // Agregamos las atracciones al parque
        parque.agregarAtraccion(dragon);
        parque.agregarAtraccion(carros);
        
        // Mostramos todas las atracciones
        parque.mostrarAtracciones();
    }
}
```

## Excepciones: ¡Manejando Situaciones Especiales! 🚨

Las excepciones nos ayudan a manejar situaciones inesperadas de manera segura.

```java
public class SeguridadParque extends Exception {
    public SeguridadParque(String mensaje) {
        super(mensaje);
    }
}

public class ControlSeguridad {
    public static void verificarEntrada(int edad) throws SeguridadParque {
        if (edad < 5) {
            throw new SeguridadParque("⚠️ ¡Lo siento! Eres muy pequeño para entrar al parque.");
        }
        System.out.println("✨ ¡Bienvenido al parque!");
    }
    
    public static void main(String[] args) {
        try {
            verificarEntrada(4);
        } catch (SeguridadParque e) {
            System.out.println(e.getMessage());
        }
    }
}
```

## ¡Proyecto Final: Sistema Completo del Parque! 🎪

Vamos a crear un sistema completo para nuestro parque de diversiones.

```java
public class SistemaParque {
    public static void main(String[] args) {
        ParqueDeDiversiones parque = new ParqueDeDiversiones();
        
        try {
            // Verificamos la entrada
            ControlSeguridad.verificarEntrada(10);
            
            // Creamos y agregamos atracciones
            MontanaRusa dragon = new MontanaRusa("El Dragón Veloz", 140, 80);
            CarrosChocones carros = new CarrosChocones("Carros Locos", 120, 10);
            
            parque.agregarAtraccion(dragon);
            parque.agregarAtraccion(carros);
            
            // Probamos las atracciones
            System.out.println("\n🎢 Probando la montaña rusa:");
            dragon.abrocharCinturon();
            dragon.verificarAltura(150);
            
            System.out.println("\n🚗 Probando los carros chocones:");
            carros.verificarAltura(130);
            carros.chocar();
            
            // Mostramos todas las atracciones
            parque.mostrarAtracciones();
            
        } catch (SeguridadParque e) {
            System.out.println(e.getMessage());
        }
    }
}
```

## Retos Extra: ¡Mejora tu Parque! 🌟

¿Puedes hacer que tu parque sea aún más divertido? Aquí hay algunas ideas:

1. Agrega más tipos de atracciones
2. Crea un sistema de tickets
3. Implementa horarios para las atracciones
4. Agrega un sistema de calificaciones
5. Crea juegos dentro del parque

## Conclusión 🎉

¡Felicitaciones! Has aprendido conceptos avanzados de Java:

- Herencia
- Interfaces
- ArrayList
- Excepciones
- Y cómo combinarlos todos en un proyecto divertido

¡Has completado todas las lecciones! Ahora eres un verdadero programador de Java. 🎓

![graduation](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)

¡Sigue programando y creando cosas increíbles! 🚀
