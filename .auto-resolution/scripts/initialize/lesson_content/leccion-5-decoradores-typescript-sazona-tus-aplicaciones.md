# Lección 5: Decoradores TypeScript - Sazona tus Aplicaciones

¡Bienvenidos, jóvenes chefs de código, a nuestra última lección sobre TypeScript! En la sesión anterior, exploramos la programación orientada a objetos y cómo crear clases y herencias. Hoy, vamos a dar un paso más allá y aprender sobre los decoradores, una característica poderosa de TypeScript que les permitirá sazonar sus aplicaciones.

![egg](https://res.cloudinary.com/dukgkrpft/image/upload/v1731877429/lessons/decoradores-typescript-sazona-tus-aplicaciones/kpssfdcfqpybj0af1h39.jpg)

## ¿Qué son los Decoradores?

Los decoradores en TypeScript son una forma de agregar metadatos a las clases, propiedades, métodos y parámetros. Puedes pensar en ellos como "condimentos" que puedes aplicar a tus ingredientes de código para modificar su comportamiento.

```typescript
function MiDecorador(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  // Lógica del decorador
  console.log("¡Se llamó al decorador!");
  return descriptor;
}

class MiClase {
  @MiDecorador
  miMetodo(): void {
    console.log("¡Hola desde el método decorado!");
  }
}

const instancia = new MiClase();
instancia.miMetodo(); // Salida: "¡Se llamó al decorador!" "¡Hola desde el método decorado!"
```

En este ejemplo, hemos creado un decorador simple llamado `MiDecorador` que registra un mensaje en la consola cada vez que se llama al método decorado.

## Decoradores de Clases

Los decoradores de clases te permiten modificar el comportamiento de una clase completa.

```typescript
function LogConstructor(constructor: Function) {
  console.log(`Nueva instancia de ${constructor.name} creada.`);
}

@LogConstructor
class Usuario {
  nombre: string;
  edad: number;

  constructor(nombre: string, edad: number) {
    this.nombre = nombre;
    this.edad = edad;
  }
}

const user = new Usuario("Juan", 30);
// Salida: "Nueva instancia de Usuario creada."
```

En este ejemplo, hemos creado un decorador `LogConstructor` que registra un mensaje cada vez que se crea una nueva instancia de la clase `Usuario`.

## Decoradores de Métodos y Propiedades

Los decoradores también se pueden aplicar a métodos y propiedades individuales.

```typescript
function LogMethod(
  target: any,
  propertyKey: string,
  descriptor: PropertyDescriptor
) {
  const originalMethod = descriptor.value;
  descriptor.value = function (...args: any[]) {
    console.log(`Llamando a ${propertyKey} con argumentos:`, args);
    return originalMethod.apply(this, args);
  };
  return descriptor;
}

class Calculadora {
  @LogMethod
  sumar(a: number, b: number): number {
    return a + b;
  }
}

const calc = new Calculadora();
const resultado = calc.sumar(5, 3);
// Salida: "Llamando a sumar con argumentos: [5, 3]" "8"
```

En este ejemplo, hemos creado un decorador `LogMethod` que registra los argumentos utilizados al llamar al método decorado.

## Decoradores Compuestos

Los decoradores pueden ser combinados para aplicar múltiples modificaciones a una clase, método o propiedad.

```typescript
function Auditable(constructor: Function) {
  constructor.prototype.registrarActividad = function () {
    console.log(`Se ha realizado una actividad en ${this.constructor.name}`);
  };
}

function Loggable(
  target: any,
  propertyKey: string,
  descriptor: PropertyDescriptor
) {
  const originalMethod = descriptor.value;
  descriptor.value = function (...args: any[]) {
    console.log(`Llamando a ${propertyKey}`);
    this.registrarActividad();
    return originalMethod.apply(this, args);
  };
  return descriptor;
}

@Auditable
class Banco {
  @Loggable
  depositarDinero(cantidad: number): void {
    console.log(`Se ha depositado ${cantidad} en la cuenta.`);
  }
}

const banco = new Banco();
banco.depositarDinero(1000);
// Salida: "Llamando a depositarDinero"
//        "Se ha realizado una actividad en Banco"
//        "Se ha depositado 1000 en la cuenta."
```

En este ejemplo, hemos combinado los decoradores `Auditable` y `Loggable` para crear una clase `Banco` que registra actividades y registra llamadas a métodos.

## Desafío: Decorador de Caché

¡Es tu turno de experimentar con decoradores! Crea un decorador de caché que pueda almacenar y reutilizar los resultados de una función.

Requisitos:

- El decorador debe tener una opción para especificar el tiempo de expiración del caché
- El decorador debe funcionar tanto para funciones como para métodos de clases
- Demuestra el uso del decorador de caché en un ejemplo práctico

¡Diviértete explorando los decoradores y no dudes en pedir ayuda si la necesitas!

## Conclusión

¡Felicitaciones, jóvenes chefs de TypeScript! En esta última lección, hemos explorado una de las características más poderosas de TypeScript: los decoradores. Hemos aprendido cómo utilizar decoradores para modificar el comportamiento de nuestras clases, métodos y propiedades.

Los decoradores son una herramienta invaluable para crear aplicaciones más flexibles, escalables y mantenibles. Recuerden que, al igual que con cualquier técnica avanzada, es importante practicar y experimentar para dominar el uso de los decoradores.

Ahora tienen todas las herramientas necesarias para convertirse en maestros del código TypeScript. Sigan cocinando, programando y creciendo como desarrolladores.

¡Que tengan éxito en sus próximos proyectos!

![Yipi](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)
