# LECCIÓN 2: RECETAS AVANZADAS EN LA COCINA DE TYPESCRIPT

## INTRODUCCIÓN

¡Bienvenidos de vuelta, jóvenes chefs del código! Hoy vamos a sumergirnos más profundo en la cocina gourmet de TypeScript. Prepárense para aprender algunas técnicas avanzadas y crear platos de código realmente sofisticados.

## NUEVOS UTENSILIOS DE COCINA TYPESCRIPT

### 1. Los Arrays Tipados: Nuestras Bandejas de Ingredientes 🍱

Los arrays tipados en TypeScript son como bandejas de ingredientes etiquetadas. Sabes exactamente qué tipo de elementos puedes poner en ellas:

```typescript
let ensalada: string[] = ["lechuga", "tomate", "pepino"];
ensalada.push("zanahoria");  // Esto está bien
ensalada.push(42);  // Error: No puedes poner un número en una ensalada de strings
```

### 2. Los Bucles Mejorados: Nuestras Batidoras de Código 🔄

TypeScript mejora los bucles de JavaScript, haciéndolos más seguros y eficientes:

```typescript
let ingredientes = ["harina", "huevos", "leche", "azúcar"];

for (let ingrediente of ingredientes) {
    console.log(`Añadiendo ${ingrediente} a la mezcla...`);
}
```

### 3. Las Funciones con Tipos: Nuestras Recetas Precisas ✨

En TypeScript, puedes especificar los tipos de los ingredientes (parámetros) y el plato final (valor de retorno) de tus funciones:

```typescript
function prepararPastel(sabor: string, capas: number): string {
    return `Un delicioso pastel de ${sabor} con ${capas} capas está listo!`;
}

console.log(prepararPastel("chocolate", 3));
```

## DATOS SÚPER CURIOSOS

* TypeScript tiene un modo estricto (`strict: true` en tsconfig.json) que es como activar todos los detectores de seguridad en tu cocina.
* Puedes usar "type assertions" en TypeScript, que es como decirle al compilador "Confía en mí, sé qué tipo de ingrediente es este".
* TypeScript tiene un operador de coalescencia nula (`??`) que es como tener un ingrediente de respaldo si tu ingrediente principal no está disponible.

## RECETAS AVANZADAS

### Receta 1: El Menú del Día Dinámico

Crea un programa que genere un menú del día usando tipos de unión e interfaces. Usa un enum para las categorías de platos.

```typescript
enum CategoriaPLato {
    Entrada = "Entrada",
    PlatoPrincipal = "Plato Principal",
    Postre = "Postre"
}

interface Plato {
    nombre: string;
    categoria: CategoriaPLato;
    calorias: number;
}

type MenuDelDia = [Plato, Plato, Plato];  // Una tupla de tres platos

function generarMenuDelDia(): MenuDelDia {
    const entradas: Plato[] = [
        { nombre: "Ensalada César", categoria: CategoriaPLato.Entrada, calorias: 150 },
        { nombre: "Sopa de Tomate", categoria: CategoriaPLato.Entrada, calorias: 120 }
    ];
    const principales: Plato[] = [
        { nombre: "Pasta Alfredo", categoria: CategoriaPLato.PlatoPrincipal, calorias: 600 },
        { nombre: "Salmón a la Parrilla", categoria: CategoriaPLato.PlatoPrincipal, calorias: 450 }
    ];
    const postres: Plato[] = [
        { nombre: "Tiramisú", categoria: CategoriaPLato.Postre, calorias: 300 },
        { nombre: "Frutas Frescas", categoria: CategoriaPLato.Postre, calorias: 100 }
    ];

    return [
        entradas[Math.floor(Math.random() * entradas.length)],
        principales[Math.floor(Math.random() * principales.length)],
        postres[Math.floor(Math.random() * postres.length)]
    ];
}

const menuHoy = generarMenuDelDia();
console.log("Menú del Día:");
menuHoy.forEach(plato => console.log(`${plato.categoria}: ${plato.nombre} (${plato.calorias} calorías)`));
```

### Receta 2: El Libro de Recetas Genérico

Crea una clase genérica para un libro de recetas que pueda manejar diferentes tipos de recetas.

```typescript
interface Receta {
    nombre: string;
    ingredientes: string[];
    instrucciones: string[];
}

class LibroDeRecetas<T extends Receta> {
    private recetas: T[] = [];

    agregarReceta(receta: T): void {
        this.recetas.push(receta);
    }

    buscarReceta(nombre: string): T | undefined {
        return this.recetas.find(receta => receta.nombre === nombre);
    }

    imprimirReceta(nombre: string): void {
        const receta = this.buscarReceta(nombre);
        if (receta) {
            console.log(`Receta: ${receta.nombre}`);
            console.log("Ingredientes:");
            receta.ingredientes.forEach(ing => console.log(`- ${ing}`));
            console.log("Instrucciones:");
            receta.instrucciones.forEach((inst, index) => console.log(`${index + 1}. ${inst}`));
        } else {
            console.log(`Receta "${nombre}" no encontrada.`);
        }
    }
}

// Uso del Libro de Recetas
interface RecetaPostre extends Receta {
    tiempoPreparacion: number;
}

const libroPostres = new LibroDeRecetas<RecetaPostre>();

libroPostres.agregarReceta({
    nombre: "Tarta de Manzana",
    ingredientes: ["manzanas", "azúcar", "canela", "masa para tarta"],
    instrucciones: ["Pelar y cortar las manzanas", "Mezclar con azúcar y canela", "Colocar en la masa", "Hornear por 45 minutos"],
    tiempoPreparacion: 60
});

libroPostres.imprimirReceta("Tarta de Manzana");
```

### Receta 3: El Asistente de Cocina Asíncrono

Crea un asistente de cocina que use promesas y async/await para simular la preparación de un plato paso a paso.

```typescript
interface PasoDeReceta {
    descripcion: string;
    tiempoEnSegundos: number;
}

async function ejecutarPaso(paso: PasoDeReceta): Promise<void> {
    console.log(`Iniciando: ${paso.descripcion}`);
    await new Promise(resolve => setTimeout(resolve, paso.tiempoEnSegundos * 1000));
    console.log(`Completado: ${paso.descripcion}`);
}

async function prepararPlato(pasos: PasoDeReceta[]): Promise<void> {
    console.log("¡Comenzando la preparación del plato!");
    for (const paso of pasos) {
        await ejecutarPaso(paso);
    }
    console.log("¡Plato terminado!");
}

// Uso del Asistente de Cocina
const pasosPasta: PasoDeReceta[] = [
    { descripcion: "Hervir agua", tiempoEnSegundos: 2 },
    { descripcion: "Cocinar pasta", tiempoEnSegundos: 3 },
    { descripcion: "Preparar salsa", tiempoEnSegundos: 2 },
    { descripcion: "Mezclar pasta y salsa", tiempoEnSegundos: 1 }
];

prepararPlato(pasosPasta).then(() => console.log("¡Buen provecho!"));
```

## CONCLUSIÓN

¡Excelente trabajo, jóvenes chefs de TypeScript! Han demostrado gran habilidad al manejar estas recetas más avanzadas. Recuerden, la práctica hace al maestro, así que sigan experimentando con estos nuevos conceptos en su cocina de código. En nuestra próxima lección, exploraremos técnicas aún más avanzadas como decoradores y módulos en TypeScript.

 ¡Hasta entonces, que sus compilaciones sean exitosas y sus errores de tipo sean pocos!
