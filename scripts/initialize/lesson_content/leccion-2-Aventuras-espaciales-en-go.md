# Introducción

![go](../images/la_búsqueda_del_tesoro_go.png)

¡Bienvenidos de vuelta, valientes astronautas del código! Hoy nos adentraremos más en las fascinantes cavernas del Planeta Go. Prepárense para descubrir nuevos superpoderes Gopher y desafiar sus mentes con misiones más avanzadas. ¡Allá vamos!

## Nuevos conceptos estelares

### 1. Los Slices Estelares 🌠

Los slices en Go son como cinturones de asteroides flexibles que pueden contener muchos objetos diferentes. ¡Y lo mejor es que pueden crecer o encogerse según lo necesites!

```go
naveEspacial := []string{"cabina", "motor", "láser", "escudo"}
naveEspacial = append(naveEspacial, "propulsor")  // Agregamos un nuevo componente
fmt.Println(naveEspacial)
```

### 2. Los Bucles Orbitales 🔄

Los bucles son como órbitas que tu nave espacial puede seguir. El bucle `for` es especialmente útil cuando quieres hacer algo con cada objeto en tu cinturón de asteroides.

```go
for _, componente := range naveEspacial {
    fmt.Printf("Revisando el %s de la nave...\n", componente)
}
```

### 3. Las Funciones Estelares ✨

Las funciones son como pequeños robots ayudantes que puedes usar una y otra vez. ¡Incluso puedes crear tus propios robots!

```go
func saludoEspacial(nombre string) string {
    return fmt.Sprintf("¡Hola, %s! Bienvenido a la aventura galáctica de Go.", nombre)
}

fmt.Println(saludoEspacial("Buzz"))
```

## Datos súper curiosos

* Go tiene una función incorporada llamada `len()` que puede medir la longitud de un slice. ¡Es como tener un medidor de asteroides!
* En Go, puedes crear slices de slices, lo que es como tener galaxias dentro de galaxias.
* Go usa la palabra clave `defer` para asegurarse de que ciertas tareas se realicen al final de una función, como cerrar la compuerta de tu nave espacial antes de despegar.

## Misiones avanzadas

### Misión 1: El Generador de Nombres de Planetas

Crea un programa que genere nombres de planetas aleatorios combinando prefijos y sufijos. Usa slices y la función `rand` del paquete `math/rand`.

```go
package main

import (
    "fmt"
    "math/rand"
    "time"
)

func generarNombrePlaneta() string {
    prefijos := []string{"Neo", "Xeno", "Astro", "Cosmo", "Galacto"}
    sufijos := []string{"tron", "nix", "zar", "nova", "stella"}
    
    rand.Seed(time.Now().UnixNano())
    prefijoAleatorio := prefijos[rand.Intn(len(prefijos))]
    sufijoAleatorio := sufijos[rand.Intn(len(sufijos))]
    
    return prefijoAleatorio + sufijoAleatorio
}

func main() {
    for i := 0; i < 5; i++ {
        fmt.Printf("Planeta descubierto: %s\n", generarNombrePlaneta())
    }
}
```

### Misión 2: El Analizador de Recursos Espaciales

Crea una función que analice los recursos de un planeta usando un mapa (map). La función debe determinar si el planeta es habitable basándose en la presencia y cantidad de ciertos recursos.

```go
package main

import "fmt"

func analizarPlaneta(recursos map[string]int) bool {
    recursosCriticos := []string{"agua", "oxigeno", "vegetacion"}
    habitabilidad := true
    
    for _, recurso := range recursosCriticos {
        cantidad, existe := recursos[recurso]
        if !existe || cantidad < 50 {
            habitabilidad = false
            fmt.Printf("Advertencia: Nivel bajo de %s detectado.\n", recurso)
        }
    }
    
    return habitabilidad
}

func main() {
    planeta1 := map[string]int{
        "agua": 80,
        "oxigeno": 60,
        "vegetacion": 70,
        "minerales": 90,
    }
    
    if analizarPlaneta(planeta1) {
        fmt.Println("¡Planeta habitable encontrado!")
    } else {
        fmt.Println("Este planeta no es adecuado para la colonización.")
    }
}
```

### Misión 3: El Comunicador Intergaláctico

Crea un programa que simule un comunicador intergaláctico. Usa goroutines para enviar y recibir mensajes concurrentemente, y canales para la comunicación entre goroutines.

```go
package main

import (
    "fmt"
    "time"
)

func enviarMensaje(c chan string, mensaje string) {
    for i := 0; i < 5; i++ {
        c <- mensaje
        time.Sleep(time.Second)
    }
    close(c)
}

func main() {
    canal1 := make(chan string)
    canal2 := make(chan string)

    go enviarMensaje(canal1, "Saludos desde la Tierra")
    go enviarMensaje(canal2, "Mensaje de Marte")

    for {
        select {
        case mensaje1, abierto1 := <-canal1:
            if !abierto1 {
                canal1 = nil
            } else {
                fmt.Println("Recibido de canal 1:", mensaje1)
            }
        case mensaje2, abierto2 := <-canal2:
            if !abierto2 {
                canal2 = nil
            } else {
                fmt.Println("Recibido de canal 2:", mensaje2)
            }
        }

        if canal1 == nil && canal2 == nil {
            break
        }
    }

    fmt.Println("Comunicación finalizada")
}
```

## Conclusión

¡Excelente trabajo, jóvenes Gophers Galácticos! Han demostrado gran valentía y astucia al enfrentarse a estas misiones más avanzadas. Recuerden, la práctica hace al maestro, así que sigan experimentando con estos nuevos conceptos. En nuestra próxima lección, exploraremos los misterios de las estructuras y las interfaces en Go, herramientas poderosas para construir naves espaciales aún más impresionantes.

 ¡Hasta entonces, que las goroutines los acompañen!

![Yipi](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)
