# Introducción: El Misterioso Baúl de las Preguntas

Imagina que has encontrado un baúl mágico lleno de preguntas misteriosas. Cada vez que lo abres, te
hace una pregunta diferente. ¿No sería genial crear tu propio baúl mágico de preguntas? ¡Pues hoy
vamos a hacerlo realidad con Python!

## La Receta Secreta del Éxito: La Planificación

![plan](https://res.cloudinary.com/dukgkrpft/image/upload/v1729375236/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/hizieaxsg4ygk6frb9vz.webp)

Antes de lanzarnos a escribir código como locos, vamos a hacer algo que todos los grandes magos del
código hacen: ¡planificar!

Imagina que vas a preparar el pastel más delicioso del mundo. No empezarías a mezclar ingredientes
al azar, ¿verdad? Primero necesitas una receta. En programación, nuestra receta se llama "plan".

### Nuestro Plan Mágico para Hoy

1. Crear un cofre de tesoros (lista) con nuestras preguntas y respuestas

2. Aprender a abrir el cofre y sacar preguntas (trabajar con listas)

3. Hacer que nuestro juego haga preguntas y compruebe respuestas (ciclos y condicionales)

4. Dar poderes especiales a nuestro juego (funciones)

5. ¡Hacer que nuestro juego sea único y divertido!

## Los Ingredientes Mágicos de Nuestro Juego

![ingrediente](https://res.cloudinary.com/dukgkrpft/image/upload/v1729375306/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/b3wgm4og58aqhh7tiscs.avif)

Antes de empezar a cocinar... ¡perdón, programar!, vamos a conocer los ingredientes mágicos que
usaremos:

### 1\. Listas: El Cofre del Tesoro

![cofre](https://res.cloudinary.com/dukgkrpft/image/upload/v1729375358/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/qi93u3gzwn5ei5xqnf5x.jpg)

Imagina una lista como un cofre del tesoro donde guardamos todas nuestras preguntas y respuestas.

```python
preguntas = ["¿Cuál es el animal que ladra?", "¿Cuántas patas tiene un gato?"]
```

### 2\. Ciclos: La Varita Mágica que Repite Hechizos

![varita](https://res.cloudinary.com/dukgkrpft/image/upload/v1729375411/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/eufdr8bi5bialz5ywujb.jpg)

Los ciclos son como una varita mágica que repite un hechizo (en este caso, hacer preguntas) hasta
que se acaben.

```python
for pregunta in preguntas:
  print(pregunta)
```

### 3\. Condicionales: El Libro de Hechizos que Toma Decisiones

![librohechizo](https://res.cloudinary.com/dukgkrpft/image/upload/v1729375565/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/m4ev1yhefshkvj0h2zoy.jpg)

Los condicionales son como un libro de hechizos que nos dice qué hacer en diferentes situaciones.

```python
if respuesta == respuesta_correcta:
  print("¡Correcto! Eres un mago del conocimiento")
else:
  print("Ups, parece que ese hechizo no funcionó. ¡Inténtalo de nuevo!")
```

### 4\. Funciones: Pociones Mágicas Reutilizables

![pocion](https://res.cloudinary.com/dukgkrpft/image/upload/v1729375608/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/qdpv8kyroalknbzi5vol.png)

Las funciones son como pociones mágicas que podemos usar una y otra vez para hacer cosas asombrosas.

```python
def hacer_pregunta(pregunta, respuesta_correcta):
# Código mágico aquí
```

### 5\. El Poder de la Claridad: Tipado en Python

Antes de sumergirnos en nuestro código mágico, vamos a aprender un truco especial que nos ayudará a
hacer nuestro código más claro y poderoso: ¡el tipado!

En Python, el tipado es como poner etiquetas mágicas a nuestras variables y funciones. Estas
etiquetas nos dicen qué tipo de dato contiene cada variable o qué tipo de dato devuelve cada
función.

#### Tipado de Variables

Cuando creamos una variable, podemos decirle a Python qué tipo de dato va a contener:

```python
nombre: str = "Harry Potter"  # str significa "string" (cadena de texto)
edad: int = 11  # int significa "integer" (número entero)
altura: float = 1.50  # float es para números con decimales
es_mago: bool = True  # bool es para valores True (verdadero) o False (falso)
```

#### Tipado de Listas

Para nuestras listas mágicas, también podemos especificar qué tipo de elementos contienen:

```python
hechizos: list[str] = ["Lumos", "Wingardium Leviosa", "Expecto Patronum"]
```

#### Tipado de Funciones

Cuando creamos funciones, podemos decirle a Python qué tipo de datos espera recibir y qué tipo de
dato va a devolver:

```python
def lanzar_hechizo(nombre: str, poder: int) -> str:
  return f"Has lanzado {nombre} con poder {poder}!"
```

Aquí, `-&gt; str` indica que la función retorna un string.

#### El Misterioso `-&gt; None`

A veces verás funciones con `-&gt; None` al final. Esto significa que la función hace algo, pero no
devuelve ningún valor. Es como un hechizo que hace efecto, pero no produce nada visible:

```python
def saludar(nombre: str) -> None:
  print(f"Hola, {nombre}!")
```

Esto significa que la función `saludar` no devuelve ningún valor. Es como decir "esta función hace
cosas, pero no te da nada de vuelta".

Es diferente de no poner nada:

```python
def saludar(nombre):
# código de la función
```

Aunque ambas funcionan igual, poner `-&gt; None` es más explícito y nos ayuda a entender mejor qué
hace la función.

#### ¿Por Qué Usar Tipado?

![pregunta](https://res.cloudinary.com/dukgkrpft/image/upload/v1729375645/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/nscklgrygu94cgwe5v7d.png)

1. **Claridad**: Hace que el código sea más fácil de entender.

2. **Prevención de Errores**: Nos ayuda a atrapar errores antes de que el código se ejecute.

3. **Mejor Documentación**: Actúa como una forma de documentación en el código.

4. **Ayuda del IDE**: Los entornos de desarrollo pueden ofrecer mejores sugerencias y detectar
   errores.

Recuerda, en Python, el tipado es una sugerencia, no una regla estricta. El código seguirá
funcionando si no coincide con los tipos especificados, pero es una buena práctica ser consistente
con el tipado.

## ¡Manos a la Obra! Creando Nuestro Juego Paso a Paso

![preparate](https://res.cloudinary.com/dukgkrpft/image/upload/v1729375800/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/hqsltnxv6lbfm3fs2s0g.jpg)

### Paso 1: Preparando Nuestro Cofre de Tesoros (Preguntas y Respuestas)

Primero, vamos a crear nuestro banco de preguntas y respuestas:

```python
preguntas = [
  "¿Qué animal hace 'muu'?",
  "¿Cuántas patas tiene una araña?",
  "¿De qué color es el cielo en un día soleado?"
]

respuestas = [
  "vaca",
  "8",
  "azul"
]
```

Explicación:

- Creamos dos listas: `preguntas` y `respuestas`.

- Usamos `list[str]` para decir que son listas que contienen strings.

- Cada pregunta en `preguntas` tiene su respuesta correspondiente en `respuestas`.

### Paso 2: Creando Nuestra Poción Mágica Principal (Función del Juego)

Ahora, vamos a crear la función principal de nuestro juego:

```python
def jugar_quiz() -> None:
  puntos: int = 0
  for i in range(len(preguntas)):
    print(preguntas[i])
    respuesta: str = input("Tu respuesta: ")
    if respuesta.lower() == respuestas[i].lower():
      print("¡Correcto! Eres un verdadero mago del conocimiento")
      puntos += 1
    else:
      print(f"Ups, la respuesta correcta era {respuestas[i]}. ¡Sigue practicando tus hechizos!")

  print(f"¡Juego terminado! Conseguiste {puntos} de {len(preguntas)} puntos mágicos")


# Lanzamos nuestro hechizo principal
jugar_quiz()
```

Explicación paso a paso:

1. Definimos la función `jugar_quiz()` que no devuelve nada (`-&gt; None`).

2. Inicializamos `puntos` en 0.

3. Usamos un ciclo `for` para recorrer todas las preguntas.

4. Mostramos cada pregunta y pedimos una respuesta al usuario.

5. Comparamos la respuesta del usuario con la respuesta correcta (ignorando mayúsculas/minúsculas).

6. Si es correcta, aumentamos los puntos y mostramos un mensaje de éxito.

7. Si es incorrecta, mostramos la respuesta correcta.

8. Al final, mostramos la puntuación total.

### Paso 3: Añadiendo Más Magia (Mejoras)

```python
import random  # Importamos el módulo random para mezclar las preguntas


def jugar_quiz() -> None:
  puntos: int = 0
  preguntas_y_respuestas: list[tuple[str, str]] = list(zip(preguntas, respuestas))
  random.shuffle(preguntas_y_respuestas)

  for pregunta, respuesta_correcta in preguntas_y_respuestas:
    print(pregunta)
    respuesta: str = input("Tu respuesta: ")
    if respuesta.lower() == respuesta_correcta.lower():
      print("¡Bravo! Tu varita mágica ha acertado")
      puntos += 1
    else:
      print(f"Oh no, tu hechizo falló. La respuesta correcta era {respuesta_correcta}")

  porcentaje: float = (puntos / len(preguntas)) * 100
  print(
    f"Has completado tu misión mágica con {puntos} de {len(preguntas)} puntos ({porcentaje:.2f}%)")

  if porcentaje == 100:
    print("¡Eres el Mago Supremo del Conocimiento!")
  elif porcentaje >= 70:
    print("¡Muy bien, aprendiz de mago! Estás en camino a la grandeza")
  else:
    print("Sigue practicando tus hechizos. ¡Pronto serás un gran mago!")


jugar_quiz()
```

Explicación de las mejoras:

1. Importamos el módulo `random`, que nos permite mezclar las preguntas.

2. Usamos `zip()` para juntar preguntas y respuestas, y `list()` para convertir el resultado en una
   lista.

3. `random.shuffle()` mezcla las preguntas y respuestas, haciendo el juego diferente cada vez.

4. Calculamos el porcentaje de respuestas correctas.

5. Añadimos mensajes personalizados según el desempeño del jugador.

## Desafío para Magos Avanzados

¿Te sientes con poderes para mejorar aún más nuestro juego mágico? Aquí tienes algunos desafíos:

1. Permite que el jugador elija cuántas preguntas quiere responder. *Pista: Usa `input()` al
   principio del juego y `int()` para convertir la respuesta a un número.*

2. Añade niveles de dificultad (aprendiz, mago, archimago). *Pista: Puedes tener diferentes listas
   de preguntas para cada nivel.*

3. Guarda el puntaje más alto en un "Libro de Records Mágicos". *Pista: Puedes usar un archivo de
   texto para guardar la puntuación más alta.*

## Conclusión: Tu Viaje Mágico Apenas Comienza

¡Felicidades, joven mago del código! Has creado tu primer juego en Python. Recuerda, cada línea de
código que escribes es como aprender un nuevo hechizo. Sigue practicando y pronto serás capaz de
crear mundos enteros con tu magia de programación.

¿Qué otras aventuras mágicas puedes imaginar crear con código? ¡El límite es tu imaginación!

![felicidades](https://res.cloudinary.com/dukgkrpft/image/upload/v1729376083/lessons/conviertete_en_un_mago_del_codigo_con_tu_propio_juego_de_preguntas_y_respuestas/iumkgutzqokumymtomuq.webp)
