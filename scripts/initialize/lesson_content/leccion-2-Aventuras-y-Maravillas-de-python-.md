# Introducción

![python](../images/python_la_serpiente_amigable.jpg)

¡Bienvenidos de vuelta, valientes exploradores del código! Hoy nos sumergiremos más profundo en el fascinante mundo de Python. Prepárense para descubrir nuevos poderes mágicos y desafiar sus mentes con ejercicios más avanzados. ¡Allá vamos!

## Nuevos conceptos mágicos

### 1. Las Listas Mágicas 📜

Las listas en Python son como baúles mágicos que pueden guardar muchos objetos diferentes. ¡Y lo mejor es que puedes agregar o quitar cosas cuando quieras!

```python
mi_baul_magico = ["varita", "sombrero", "poción", "libro de hechizos"]
mi_baul_magico.append("escoba voladora")  # Agregamos un nuevo objeto
print(mi_baul_magico)
```

### 2. Los Bucles Encantados 🔄

Los bucles son como hechizos que se repiten. El bucle `for` es especialmente útil cuando quieres hacer algo con cada objeto en tu baúl mágico.

```python
for objeto in mi_baul_magico:
    print(f"Sacando {objeto} del baúl...")
```

### 3. Las Funciones Mágicas ✨

Las funciones son como pequeños hechizos que puedes usar una y otra vez. ¡Incluso puedes crear tus propios hechizos!

```python
def saludo_magico(nombre):
    return f"¡Hola, {nombre}! Bienvenido al mundo mágico de Python."

print(saludo_magico("Harry"))
```

## Datos súper curiosos

* Python tiene una función llamada `len()` que puede contar la cantidad de objetos en una lista. ¡Es como tener un contador mágico!
* Existe algo llamado "comprensión de listas" en Python, que es como hacer magia para crear listas nuevas en una sola línea de código.
* Python usa sangría (espacios al principio de la línea) para organizar el código. Es como si cada hechizo tuviera su propio espacio mágico.

## Ejercicios avanzados

### Ejercicio 1: El Ordenador de Casas de Hogwarts

Crea un programa que ordene a los estudiantes en las casas de Hogwarts basándose en sus características. Usa diccionarios y condicionales.

```python
def ordenar_estudiante(nombre, valiente, inteligente, leal, ambicioso):
    if valiente:
        casa = "Gryffindor"
    elif inteligente:
        casa = "Ravenclaw"
    elif leal:
        casa = "Hufflepuff"
    elif ambicioso:
        casa = "Slytherin"
    else:
        casa = "Squib"
    
    return f"{nombre} pertenece a la casa {casa}."

# Prueba el ordenador
print(ordenar_estudiante("Luna", False, True, False, False))
print(ordenar_estudiante("Draco", False, False, False, True))
```

### Ejercicio 2: La Poción Mágica

Crea una función que mezcle una poción mágica. La poción necesita ciertos ingredientes en cantidades específicas. Usa un diccionario para los ingredientes y un bucle para verificar si tienes suficiente de cada uno.

```python
def crear_pocion(ingredientes_disponibles):
    receta = {"ojo de tritón": 2, "ala de murciélago": 4, "cola de rata": 1, "pata de araña": 3}
    pocion_exitosa = True
    
    for ingrediente, cantidad in receta.items():
        if ingrediente not in ingredientes_disponibles or ingredientes_disponibles[ingrediente] < cantidad:
            pocion_exitosa = False
            print(f"¡Oh no! No tienes suficiente {ingrediente}.")
    
    if pocion_exitosa:
        print("¡Felicidades! Has creado la poción mágica.")
    else:
        print("La poción ha fallado. Inténtalo de nuevo cuando tengas todos los ingredientes.")

# Prueba la función
mis_ingredientes = {"ojo de tritón": 5, "ala de murciélago": 3, "cola de rata": 2, "pata de araña": 4}
crear_pocion(mis_ingredientes)
```

### Ejercicio 3: El Generador de Contraseñas Mágicas

Crea un programa que genere contraseñas mágicas. Usa listas, el módulo `random`, y funciones.

```python
import random

def generar_contrasena_magica(longitud):
    caracteres = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                  '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*']
    
    contrasena = ''
    for _ in range(longitud):
        contrasena += random.choice(caracteres)
    
    return contrasena

# Genera una contraseña mágica de 12 caracteres
print(f"Tu nueva contraseña mágica es: {generar_contrasena_magica(12)}")
```

## Conclusión

¡Excelente trabajo, jóvenes magos de Python! Han demostrado gran valentía y astucia al enfrentarse a estos desafíos más avanzados. Recuerden, la práctica hace al maestro, así que sigan experimentando con estos nuevos conceptos. En nuestra próxima lección, nos adentraremos aún más en el bosque encantado de Python, donde descubriremos criaturas mágicas como las clases y los módulos.

 ¡Hasta entonces, que la magia del código los acompañe!

![gato](../images/Gatocelebrar.jpeg)
