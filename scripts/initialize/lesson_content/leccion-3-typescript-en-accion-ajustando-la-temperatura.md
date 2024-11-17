# Lección 3: Typescript en Acción: Ajustando la Temperatura

¡Bienvenidos de vuelta, jóvenes cocineros de código! En nuestras lecciones anteriores, hemos explorado los ingredientes y las técnicas básicas de TypeScript. Ahora es el momento de poner todo en práctica y cocinar algunos proyectos reales.

![type](https://res.cloudinary.com/dukgkrpft/image/upload/v1731876766/lessons/Typescript-en-accion-ajustando-la-temperatura/ce8vaiehi2vyhrzqxs1g.png)

## Proyecto 1: El Conversor de Monedas

Vamos a crear una aplicación de conversor de monedas utilizando TypeScript. Nuestro objetivo será tomar un monto en una moneda y convertirla a otras monedas, mostrando el resultado de manera clara y organizada.

```typescript
// Definimos los tipos de monedas y sus tipos de cambio
type Moneda = 'USD' | 'EUR' | 'GBP' | 'JPY';
const tiposDeCambio: Record<Moneda, number> = {
  'USD': 1,
  'EUR': 0.95,
  'GBP': 0.82,
  'JPY': 134.06
};

// Función para convertir de una moneda a otra
function convertirMoneda(cantidad: number, monedaOrigen: Moneda, monedaDestino: Moneda): number {
  const tasaDeConversion = tiposDeCambio[monedaDestino] / tiposDeCambio[monedaOrigen];
  return cantidad * tasaDeConversion;
}

// Ejemplo de uso
const montoDolares = 100;
const monedaOrigen: Moneda = 'USD';
const monedaDestino: Moneda = 'EUR';

const montoConvertido = convertirMoneda(montoDolares, monedaOrigen, monedaDestino);
console.log(`${montoDolares} ${monedaOrigen} = ${montoConvertido.toFixed(2)} ${monedaDestino}`);
```

En este proyecto, hemos utilizado:

- Tipos de unión para definir las monedas válidas
- Un objeto `Record` para almacenar los tipos de cambio
- Una función `convertirMoneda` que recibe las monedas de origen y destino, y devuelve el monto convertido

¡Inténtalo tú mismo, ajusta los tipos de cambio y prueba diferentes conversiones!

## Proyecto 2: El Organizador de Tareas

Vamos a crear una aplicación sencilla de organización de tareas utilizando TypeScript. Permitirá agregar, marcar como completadas y eliminar tareas.

```typescript
// Definimos el tipo de tarea
type Tarea = {
  id: string;
  descripcion: string;
  completada: boolean;
};

// Función para agregar una tarea
function agregarTarea(tareas: Tarea[], descripcion: string): Tarea[] {
  const nuevaTarea: Tarea = {
    id: crypto.randomUUID(),
    descripcion,
    completada: false
  };
  return [...tareas, nuevaTarea];
}

// Función para marcar una tarea como completada
function completarTarea(tareas: Tarea[], id: string): Tarea[] {
  return tareas.map(tarea =>
    tarea.id === id ? { ...tarea, completada: true } : tarea
  );
}

// Función para eliminar una tarea
function eliminarTarea(tareas: Tarea[], id: string): Tarea[] {
  return tareas.filter(tarea => tarea.id !== id);
}

// Ejemplo de uso
let listaDeTareas: Tarea[] = [];

listaDeTareas = agregarTarea(listaDeTareas, 'Limpiar la habitación');
listaDeTareas = agregarTarea(listaDeTareas, 'Hacer la tarea de matemáticas');
listaDeTareas = completarTarea(listaDeTareas, listaDeTareas[0].id);
listaDeTareas = eliminarTarea(listaDeTareas, listaDeTareas[1].id);

console.log(listaDeTareas);
```

En este proyecto, hemos utilizado:

- Un tipo de interfaz `Tarea` para definir la estructura de las tareas
- Funciones puras para agregar, completar y eliminar tareas, que devuelven nuevos arreglos de tareas
- El método `map` para actualizar el estado de una tarea
- El método `filter` para eliminar una tarea

¡Prueba estas funciones y crea tu propio organizador de tareas!

## Proyecto 3: El Conversor de Temperatura

Vamos a crear una aplicación que convierta temperaturas entre Celsius, Fahrenheit y Kelvin.

```typescript
// Definimos los tipos de unidad de temperatura
type UnidadTemperatura = 'C' | 'F' | 'K';

// Función para convertir temperatura
function convertirTemperatura(valor: number, unidadOrigen: UnidadTemperatura, unidadDestino: UnidadTemperatura): number {
  if (unidadOrigen === unidadDestino) return valor;

  let temperaturaEnCelsius: number;
  switch (unidadOrigen) {
    case 'C':
      temperaturaEnCelsius = valor;
      break;
    case 'F':
      temperaturaEnCelsius = (valor - 32) * 5 / 9;
      break;
    case 'K':
      temperaturaEnCelsius = valor - 273.15;
      break;
  }

  switch (unidadDestino) {
    case 'C':
      return temperaturaEnCelsius;
    case 'F':
      return (temperaturaEnCelsius * 9 / 5) + 32;
    case 'K':
      return temperaturaEnCelsius + 273.15;
  }
}

// Ejemplo de uso
const temperaturaEnFahrenheit = 68;
const unidadOrigen: UnidadTemperatura = 'F';
const unidadDestino: UnidadTemperatura = 'C';

const temperaturaConvertida = convertirTemperatura(temperaturaEnFahrenheit, unidadOrigen, unidadDestino);
console.log(`${temperaturaEnFahrenheit}°${unidadOrigen} = ${temperaturaConvertida.toFixed(2)}°${unidadDestino}`);
```

En este proyecto, hemos utilizado:

- Tipos de unión para definir las unidades de temperatura válidas
- Una función `convertirTemperatura` que recibe la temperatura, la unidad de origen y la unidad de destino, y devuelve la temperatura convertida
- Lógica de conversión basada en fórmulas matemáticas para pasar de una unidad a otra

¡Intenta convertir diferentes temperaturas y juega con las unidades!

## Conclusión

¡Felicitaciones, jóvenes chefs de TypeScript! Han demostrado su habilidad para crear aplicaciones prácticas utilizando los conceptos y técnicas que hemos aprendido en lecciones anteriores. Recuerden que la práctica los hará cada vez más expertos en el uso de TypeScript.

En nuestra próxima lección, exploraremos temas aún más avanzados, como la programación orientada a objetos y los decoradores en TypeScript. ¡Sigan cocinando y puliendo sus habilidades de programación!

![Yipi](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)
