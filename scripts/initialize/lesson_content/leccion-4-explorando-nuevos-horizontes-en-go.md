
# Lección 4: Explorando Nuevos Horizontes en Go 🌌

¡Hola, valientes exploradores! Hoy continuaremos nuestra emocionante aventura en el Planeta Go. Prepárense para descubrir nuevas herramientas e ideas que los ayudarán a ser aún más poderosos programadores galácticos. 🚀

## 1. Las Estructuras Estelares 🌌

Las estructuras en Go son como naves espaciales que pueden tener varios componentes para mantener organizados nuestros datos. ¡Podemos almacenar diferentes tipos de información en un solo lugar!

```go
type NaveEspacial struct {
    nombre   string
    escudos  int
    tripulantes []string
}

miNave := NaveEspacial{
    nombre:   "Estrella Rápida",
    escudos:  100,
    tripulantes: []string{"Capitán Gopher", "Ingeniero Go", "Piloto Gopherito"},
}
fmt.Println(miNave)
```

- Interfaces Galácticas 🪐
Las interfaces son como las instrucciones para nuestras naves. Nos dicen que ciertos métodos deben ser implementados, aunque no especifican cómo:

```go


type Comunicador interface {
    enviarMensaje(mensaje string)
}

type NaveEspacial struct {
    // ...
}

func (n NaveEspacial) enviarMensaje(mensaje string) {
    fmt.Println("Mensaje enviado desde la nave:", mensaje)
}
```

- Manejo de Errores en el Espacio 🚨
Es importante manejar los errores cuando estamos volando por el universo para que nuestras naves no se estrellen. En Go, podemos hacer esto fácilmente:

```go

func realizarMisión() error {
    // Supongamos que algo sale mal
    return fmt.Errorf("error: la misión falló")
}

if err := realizarMisión(); err != nil {
    fmt.Println("¡Oh no! Tenemos un problema:", err)
}
```

Nuevas Misiones Espaciales 🚀
Misión 1: Creación de un Sistema de Clasificación de Planetas
Crea un programa que clasifique planetas según su habitabilidad utilizando estructuras y métodos.

```go


type Planeta struct {
    nombre   string
    habitabilidad bool
}

func clasificarPlaneta(p Planeta) {
    if p.habitabilidad {
        fmt.Printf("El planeta %s es habitable.\n", p.nombre)
    } else {
        fmt.Printf("El planeta %s no es habitable.\n", p.nombre)
    }
}
```

Misión 2: El Interrogador de Mensajes
Crea una interfaz que las naves de tu flota puedan utilizar para enviar mensajes y una implementación que muestre el mensaje en pantalla.

```go

type Comunicador interface {
    enviarMensaje(mensaje string)
}

// Implementación de la interfaz
type Nave struct {
    nombre string
}

func (n Nave) enviarMensaje(mensaje string) {
    fmt.Printf("%s envía: %s\n", n.nombre, mensaje)
}

// Uso
miNave := Nave{nombre: "Aventurera"}
miNave.enviarMensaje("¡Listos para la aventura!")
```

Misión 3: Reparador de Errores
Implementa un programa que simule la reparación de errores en tu nave, notificando al capitán si algo no funciona correctamente:

``` go


func reparar() error {
    // Simulación de una falla
    return fmt.Errorf("falla en el sistema de propulsión")
}

if err := reparar(); err != nil {
    fmt.Println("¡Error detectado! No se puede continuar:", err)
}
```

¡Felicidades, jóvenes Gophers!
Han completado su cuarta lección en el fascinante universo de Go. Ahora conocen más sobre estructuras, interfaces y manejo de errores. ¡Están un paso más cerca de convertirse en los mejores programadores galácticos! 🌟

Mantengan sus naves listas, ya que en la próxima lección exploraremos nuevas dimensiones y conceptos avanzados de programación en Go. ¡Hasta pronto, intrépidos exploradores!

![Yipi](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)
