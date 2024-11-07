# Lección 4: TypeScript Orientado a Objetos

¡Bienvenidos a otra emocionante lección de TypeScript, jóvenes chefs del código! En nuestra sesión anterior, exploramos proyectos prácticos que nos ayudaron a aplicar los conceptos básicos de TypeScript. Hoy, vamos a dar un paso más allá y sumergirnos en el mundo de la programación orientada a objetos (POO) con TypeScript.

## Clases y Objetos: Nuestros Utensilios Personalizados

En TypeScript, puedes crear tus propias clases para representar objetos del mundo real. Estas clases pueden tener propiedades y métodos, al igual que los objetos que representan.

```typescript
class Receta {
  nombre: string;
  ingredientes: string[];
  instrucciones: string[];

  constructor(nombre: string, ingredientes: string[], instrucciones: string[]) {
    this.nombre = nombre;
    this.ingredientes = ingredientes;
    this.instrucciones = instrucciones;
  }

  imprimirReceta(): void {
    console.log(`Receta: ${this.nombre}`);
    console.log("Ingredientes:");
    this.ingredientes.forEach(ing => console.log(`- ${ing}`));
    console.log("Instrucciones:");
    this.instrucciones.forEach((inst, index) => console.log(`${index + 1}. ${inst}`));
  }
}

// Uso de la clase Receta
const tartaDeManzana = new Receta(
  "Tarta de Manzana",
  ["manzanas", "azúcar", "canela", "masa para tarta"],
  ["Pelar y cortar las manzanas", "Mezclar con azúcar y canela", "Colocar en la masa", "Hornear por 45 minutos"]
);

tartaDeManzana.imprimirReceta();
```

En este ejemplo, hemos creado una clase `Receta` que representa una receta de cocina. La clase tiene propiedades para almacenar el nombre, los ingredientes y las instrucciones. También tiene un método `imprimirReceta()` que muestra los detalles de la receta.

## Herencia: Nuestros Platos Especiales

TypeScript también admite la herencia, lo que nos permite crear clases más especializadas a partir de otras. Esto es como tener platos especiales que se construyen sobre recetas más básicas.

```typescript
class RecetaPostre extends Receta {
  tiempoPreparacion: number;

  constructor(nombre: string, ingredientes: string[], instrucciones: string[], tiempoPreparacion: number) {
    super(nombre, ingredientes, instrucciones);
    this.tiempoPreparacion = tiempoPreparacion;
  }

  imprimirReceta(): void {
    super.imprimirReceta();
    console.log(`Tiempo de preparación: ${this.tiempoPreparacion} minutos`);
  }
}

// Uso de la clase RecetaPostre
const tartaDeQueso = new RecetaPostre(
  "Tarta de Queso",
  ["queso crema", "azúcar", "huevos", "galletas"],
  ["Batir los ingredientes", "Verter en el molde", "Hornear por 30 minutos"],
  45
);

tartaDeQueso.imprimirReceta();
```

En este ejemplo, hemos creado una clase `RecetaPostre` que hereda de la clase `Receta`. La clase `RecetaPostre` agrega una nueva propiedad `tiempoPreparacion` y sobrescribe el método `imprimirReceta()` para incluir el tiempo de preparación.

## Modificadores de Acceso: Nuestros Ingredientes Secretos

TypeScript también nos permite controlar el acceso a las propiedades y métodos de nuestras clases, similar a tener ingredientes y técnicas secretas en nuestra cocina.

```typescript
class Panaderia {
  private harina: number = 100;
  protected levadura: number = 10;
  public azucar: number = 20;

  private mezclarIngredientes(): void {
    console.log("Mezclando los ingredientes...");
  }

  protected hornear(): void {
    console.log("Horneando...");
  }

  public venderPan(): void {
    this.mezclarIngredientes();
    this.hornear();
    console.log("¡Pan recién horneado a la venta!");
  }
}

class Panaderia_Gourmet extends Panaderia {
  public venderPastel(): void {
    this.hornear(); // Acceso protegido
    // this.mezclarIngredientes(); // Error: mezclarIngredientes es privado
    console.log("¡Pastel recién horneado a la venta!");
  }
}
```

En este ejemplo, hemos utilizado los modificadores de acceso `private`, `protected` y `public` para controlar el acceso a las propiedades y métodos de la clase `Panaderia`. Esto nos permite ocultar los detalles de implementación y exponer solo lo necesario a otras clases.

## Desafío: La Tienda de Mascotas

¡Hora de poner en práctica lo que has aprendido! Crea una aplicación de una tienda de mascotas utilizando programación orientada a objetos en TypeScript.

Requisitos:

- Debes tener clases para representar diferentes tipos de mascotas (perros, gatos, pájaros, etc.)
- Cada mascota debe tener propiedades como nombre, especie, edad, salud y felicidad
- Debe haber métodos para alimentar, jugar y curar a las mascotas
- Crea una clase `TiendaDeMascotas` que pueda almacenar y manejar la lista de mascotas
- Agrega métodos a la clase `TiendaDeMascotas` para agregar, eliminar y buscar mascotas

¡Diviértete y no dudes en pedir ayuda si lo necesitas!

## Conclusión

¡Excelente trabajo, jóvenes chefs de TypeScript! En esta lección, hemos aprendido cómo utilizar la programación orientada a objetos en TypeScript. Hemos visto cómo crear clases, heredar de ellas y controlar el acceso a las propiedades y métodos.

Recuerden que la POO es una herramienta poderosa que les permitirá crear aplicaciones más organizadas y escalables. Sigan practicando y explorando más técnicas avanzadas de TypeScript, como los decoradores, que veremos en nuestra próxima lección.

¡Sigan cocinando, programando y creciendo como desarrolladores!

![Yipi](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)