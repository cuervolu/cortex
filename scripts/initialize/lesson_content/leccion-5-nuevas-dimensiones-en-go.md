
# Lección 5: Nuevas Dimensiones en Go 🌠

![nova](https://res.cloudinary.com/dukgkrpft/image/upload/v1731877738/lessons/nuevas-dimensiones-en-go/r6moqowzrebwevpvdhm6.jpg)

¡Bienvenidos, valientes exploradores del código! Hoy daremos un gran salto hacia nuevas dimensiones en el Planeta Go. Prepárense para aprender sobre nuevos conceptos que les ayudarán a navegar por el espacio digital con aún más destreza. 🚀

## 1. Programación Concurrente 🚦

La programación concurrente nos permite realizar múltiples tareas al mismo tiempo, como si tuviéramos varios brazos ayudando en diferentes tareas de la nave. En Go, esto se logra mediante goroutines y canales.

### Goroutines 🛠️

Las goroutines son funciones que pueden ejecutarse de manera concurrente. Se inician con la palabra clave `go`:

```go

go func() {
    fmt.Println("Esta es una goroutine en acción!")
}()
```

Canales 🌌
Los canales son como tubos intergalácticos que permiten que nuestras goroutines se comuniquen entre sí.

```go

canal := make(chan string)

go func() {
    canal <- "Mensaje desde la goroutine!"
}()

mensaje := <-canal
fmt.Println(mensaje)
```

1. El Paquete sync para Sincronización 🕒
A veces, necesitamos asegurarnos de que ciertas partes de nuestro código se ejecuten en orden y no se superpongan. Para eso, utilizamos el paquete sync y sus estructuras como WaitGroup.

```go
import "sync"

var wg sync.WaitGroup

wg.Add(1) // Agregamos una tarea a esperar
go func() {
    defer wg.Done() // Marca la tarea como completada
    fmt.Println("Tarea concurrente finalizada!")
}()
wg.Wait() // Espera a que todas las tareas se completen
```

1. Reflexión Espacial 🪞
La reflexión nos permite examinar y manipular tipos y estructuras en el tiempo de ejecución. Esto es útil cuando queremos interactuar con tipos de datos de manera más dinámica.

```go

import "reflect"

tipo := reflect.TypeOf(miNave)
fmt.Println("El tipo de mi nave es:", tipo)
```

Nuevas Misiones Espaciales 🚀
Misión 1: Sincronización de Goroutines
Crea un programa que ejecute varias goroutines y espera a que todas terminen utilizando WaitGroup.

```go
var wg sync.WaitGroup
naves := []string{"Nave A", "Nave B", "Nave C"}

for _, nave := range naves {
    wg.Add(1)
    go func(nave string) {
        defer wg.Done()
        fmt.Printf("%s ha terminado su misión.\n", nave)
    }(nave)
}

wg.Wait()
fmt.Println("¡Todas las naves han completado sus misiones!")
```

Misión 2: Comunicador Concurrente
Crea un comunicador que envíe y reciba mensajes de manera concurrente usando canales.

```go

func comunicador(canal chan string) {
    for i := 0; i < 5; i++ {
        mensaje := fmt.Sprintf("Mensaje %d", i)
        canal <- mensaje
    }
    close(canal)
}

func main() {
    canal := make(chan string)
    go comunicador(canal)

    for mensaje := range canal {
        fmt.Println("Recibido:", mensaje)
    }
}
```

Misión 3: Reflección de Nave
Utiliza la reflexión para obtener y mostrar los campos de una estructura de nave espacial.

```go

import "reflect"

type NaveEspacial struct {
    nombre  string
    escudos int
}

func mostrarCampos(nave NaveEspacial) {
    tipo := reflect.TypeOf(nave)
    for i := 0; i < tipo.NumField(); i++ {
        campo := tipo.Field(i)
        fmt.Printf("Campo: %s, Tipo: %s\n", campo.Name, campo.Type)
    }
}

miNave := NaveEspacial{"Estrella Rápida", 100}
mostrarCampos(miNave)
```

¡Felicidades, jóvenes Gophers!
¡Han llegado a la quinta lección! Ahora tienen poder sobre la programación concurrente, la sincronización y la reflexión en Go. Con estas herramientas, están listos para enfrentar desafíos aún mayores en su viaje exploratorio. 🌌

Estén atentos, ya que en la próxima lección descubriremos los misterios del manejo de paquetes y bibliotecas en Go. ¡Hasta pronto, intrépidos aventureros espaciales!

![Yipi](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)
