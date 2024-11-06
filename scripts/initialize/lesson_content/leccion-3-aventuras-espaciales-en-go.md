# Lección 3: Estructuras y Naves Espaciales en Go 🚀

![go](https://res.cloudinary.com/dukgkrpft/image/upload/v1729455197/lessons/aventuras-espaciales-en-go/bfi1ydwke2edvrqsyuoh.png)

¡Saludos, intrépidos exploradores del espacio! En esta emocionante lección, aprenderemos a construir nuestras propias naves espaciales utilizando las poderosas estructuras de Go. ¡Prepárense para una aventura llena de tipos personalizados y métodos especiales! 🌟

## Nuevos Tesoros Galácticos

![galaxia](https://res.cloudinary.com/dukgkrpft/image/upload/v1729455335/lessons/aventuras-espaciales-en-go/o2tvozzhzvd8amuktoic.png)

### 1. Estructuras: Los Planos de tu Nave Espacial 🛸

Las estructuras en Go son como planos para construir cosas increíbles. ¡Podemos crear nuestra propia nave espacial con todas las características que queramos!

```go
type NaveEspacial struct {
    Nombre      string
    Velocidad   int
    Tripulantes []string
    TieneLaser  bool
}

miNave := NaveEspacial{
    Nombre:      "Estrella Veloz",
    Velocidad:   1000,
    Tripulantes: []string{"Capitán Gopher", "Piloto Luna"},
    TieneLaser:  true,
}
```

### 2. Métodos: Superpoderes para tu Nave 💫

Los métodos son como botones mágicos que le dan superpoderes a tu nave. ¡Mira lo que pueden hacer!

```go
func (n *NaveEspacial) Despegar() string {
    return fmt.Sprintf("¡%s despegando a velocidad %d!", n.Nombre, n.Velocidad)
}

func (n *NaveEspacial) AgregarTripulante(nombre string) {
    n.Tripulantes = append(n.Tripulantes, nombre)
    fmt.Printf("¡%s se ha unido a la tripulación!\n", nombre)
}
```

### 3. Interfaces: El Poder de la Transformación ✨

Las interfaces son como disfraces mágicos que permiten que diferentes tipos de naves espaciales trabajen juntas.

```go
type VehiculoEspacial interface {
    Despegar() string
    Aterrizar() string
}
```

## ¿Sabías que...? 🤔

* Las estructuras en Go son como cajas de LEGO™: ¡puedes construir cosas increíbles combinándolas!
* Puedes crear estructuras dentro de otras estructuras, ¡como mini naves dentro de una nave gigante!
* Go tiene un campo especial llamado "tags" que son como etiquetas secretas para tus estructuras.

## Misiones Espaciales

![serio](https://res.cloudinary.com/dukgkrpft/image/upload/v1729455555/lessons/aventuras-espaciales-en-go/ebxb8f6v0pxvy8f6mtvt.gif)

### Misión 1: El Constructor de Flotas Espaciales

Crea un programa que construya una flota de diferentes tipos de naves espaciales.

```go
package main

import "fmt"

type NaveExploradora struct {
    NaveEspacial
    SensoresActivos int
}

func main() {
    flota := []NaveExploradora{
        {
            NaveEspacial: NaveEspacial{
                Nombre:    "Explorador Alpha",
                Velocidad: 800,
            },
            SensoresActivos: 5,
        },
        {
            NaveEspacial: NaveEspacial{
                Nombre:    "Explorador Beta",
                Velocidad: 900,
            },
            SensoresActivos: 7,
        },
    }

    for _, nave := range flota {
        fmt.Printf("Nave: %s, Sensores: %d\n", 
            nave.Nombre, nave.SensoresActivos)
    }
}
```

### Misión 2: El Sistema de Combate Espacial

Crea un juego simple donde las naves espaciales pueden luchar entre sí usando métodos.

```go
type NaveDeCombate struct {
    NaveEspacial
    Escudos    int
    PoderLaser int
}

func (n *NaveDeCombate) Atacar(objetivo *NaveDeCombate) {
    daño := n.PoderLaser
    objetivo.Escudos -= daño
    fmt.Printf("%s atacó a %s y causó %d de daño!\n",
        n.Nombre, objetivo.Nombre, daño)
}

func main() {
    nave1 := &NaveDeCombate{
        NaveEspacial: NaveEspacial{Nombre: "Halcón Estelar"},
        Escudos:      100,
        PoderLaser:   20,
    }
    
    nave2 := &NaveDeCombate{
        NaveEspacial: NaveEspacial{Nombre: "Águila Galáctica"},
        Escudos:      100,
        PoderLaser:   25,
    }
    
    nave1.Atacar(nave2)
    fmt.Printf("Escudos de %s: %d\n", nave2.Nombre, nave2.Escudos)
}
```

### Misión 3: La Academia Espacial

Crea un sistema para entrenar nuevos pilotos espaciales usando estructuras y métodos.

```go
type Piloto struct {
    Nombre     string
    Experiencia int
    Habilidades []string
}

func (p *Piloto) Entrenar() {
    p.Experiencia += 1
    fmt.Printf("¡%s ha completado un entrenamiento!\n", p.Nombre)
    if p.Experiencia == 5 {
        p.Habilidades = append(p.Habilidades, "Piloto Espacial Certificado")
        fmt.Printf("¡%s ha obtenido su certificación!\n", p.Nombre)
    }
}

func main() {
    cadetePiloto := &Piloto{
        Nombre:     "Luna Estrella",
        Experiencia: 0,
        Habilidades: []string{"Principiante"},
    }
    
    for i := 0; i < 5; i++ {
        cadetePiloto.Entrenar()
    }
    
    fmt.Println("Habilidades finales:", cadetePiloto.Habilidades)
}
```

## Conclusión

¡Fantástico trabajo, pequeños constructores espaciales! Han aprendido a crear sus propias naves y darles superpoderes usando estructuras y métodos. En nuestra próxima aventura, aprenderemos sobre el manejo de errores y cómo mantener nuestras naves seguras en el vasto espacio.

¡Hasta la próxima aventura espacial, valientes Gophers! 🚀

![Yipi](https://res.cloudinary.com/dukgkrpft/image/upload/v1729378761/lessons/felicidades-yipi/jczrx7hhw88cvrfnmiae.jpg)
