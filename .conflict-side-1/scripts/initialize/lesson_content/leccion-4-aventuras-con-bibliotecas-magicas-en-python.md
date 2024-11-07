
# Lección 4: Aventuras con Bibliotecas Mágicas 📚✨

¡Hola, intrépidos aventureros! Hoy nos embarcaremos en un nuevo viaje hacia el fascinante mundo de las bibliotecas en Python. Las bibliotecas son como hechizos poderosos que ya han sido creados por otros magos, y nos permiten hacer cosas asombrosas sin tener que escribir todo desde cero.

## Nuevos Conceptos Mágicos

### 1. ¿Qué son las Bibliotecas? 🎩

Las bibliotecas en Python son colecciones de funciones y métodos predefinidos que te ayudan a realizar tareas específicas. ¡Son como tener una caja de herramientas mágicas a tu disposición!

### 2. Importando Bibliotecas 🧙‍♂️

Para usar una biblioteca, primero debes importarla. Esto es como abrir la puerta a tu caja de herramientas.

```python
import math  # Importamos la biblioteca de matemáticas
print(math.sqrt(16))  # Usando la función para encontrar la raíz cuadrada

3. La Biblioteca random 🎲
La biblioteca random te permite crear números aleatorios. Esto puede ser útil para juegos o para simular decisiones mágicas.

 python


import random

def lanzar_moneda():
    resultado = random.choice(["cara", "seca"])
    print(f"El resultado del lanzamiento es: {resultado}")

lanzar_moneda()

4. La Biblioteca datetime ⏰
La biblioteca datetime te ayuda a trabajar con fechas y horas. Esto es como tener un reloj mágico que puede decirte cuándo es la hora adecuada para lanzar tus hechizos.

 python


import datetime

hoy = datetime.date.today()
print(f"Hoy es: {hoy}")

Ejercicios Mágicos 🔮
Ejercicio 1: Generador de Números Aleatorios
Crea un programa que genere un número aleatorio entre 1 y 10 y permita al usuario adivinarlo. ¡Celebra cuando lo adivine correctamente!

 python


import random

def adivina_el_numero():
    numero_secreto = random.randint(1, 10)
    adivinado = False

    while not adivinado:
        intento = int(input("Adivina el número entre 1 y 10: "))
        if intento == numero_secreto:
            print("¡Felicidades! Has adivinado el número mágico.")
            adivinado = True
        else:
            print("Sigue intentando, ¡no es el número correcto!")

adivina_el_numero()

Ejercicio 2: Cronómetro Mágico
Crea un programa que muestre la hora actual y espere 3 segundos antes de mostrar la hora nuevamente.

 python


import datetime
import time

def mostrar_hora():
    print("Esperando 3 segundos...")
    time.sleep(3)  # Espera por 3 segundos
    hora_actual = datetime.datetime.now()
    print(f"La hora actual es: {hora_actual.strftime('%H:%M:%S')}")

mostrar_hora()

Ejercicio 3: El Calendario Mágico
Usa la biblioteca calendar para mostrar el calendario del mes actual.

 python


import calendar

def mostrar_calendario():
    ano = datetime.datetime.now().year
    mes = datetime.datetime.now().month
    print(calendar.month(ano, mes))

mostrar_calendario()

Conclusión
¡Bravo! Han completado una lección llena de magia sobre bibliotecas en Python. Ahora tienen el poder de usar herramientas mágicas para hacer que su código sea más eficiente y divertido.

En la próxima lección, exploraremos cómo usar bibliotecas externas y crear nuestros propios módulos mágicos.

¡Hasta pronto, valientes magos y brujas del código! 🌌

