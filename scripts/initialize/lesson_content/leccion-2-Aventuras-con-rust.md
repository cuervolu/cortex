# Introducción

![rust](../images/la_fábrica_de_robots_rust.png)

¡Bienvenidos de vuelta, valientes Rust Rangers! Hoy nos adentraremos más en las fascinantes cavernas de la Tierra del Cangrejo Oxidado. Prepárense para descubrir nuevos superpoderes Rust y enfrentarse a desafíos más emocionantes. ¡Allá vamos!

## Nuevos superpoderes Rust

### 1. Los Vectores Vectoriales 🏹

Los vectores en Rust son como carcajes mágicos que pueden contener muchas flechas diferentes. ¡Y lo mejor es que pueden crecer o encogerse según lo necesites!

```rust
let mut armas_de_superheroe = vec!["escudo", "martillo", "arco"];
armas_de_superheroe.push("lanza");  // Agregamos una nueva arma
println!("{:?}", armas_de_superheroe);
```

### 2. Los Bucles de Poder 🔄

Los bucles son como entrenamientos que tu superhéroe puede repetir. El bucle `for` es especialmente útil cuando quieres hacer algo con cada objeto en tu carcaj mágico.

```rust
for arma in &armas_de_superheroe {
    println!("Entrenando con: {}", arma);
}
```

### 3. Las Funciones Heroicas ✨

Las funciones son como movimientos especiales que tu superhéroe puede usar una y otra vez. ¡Incluso puedes crear tus propios movimientos!

```rust
fn saludo_heroico(nombre: &str) -> String {
    format!("¡Saludos, {}! Bienvenido a la Liga de Superhéroes Rust.", nombre)
}

println!("{}", saludo_heroico("Iron Crab"));
```

## Datos súper curiosos

* Rust tiene una característica llamada "pattern matching" que es como tener visión de rayos X para tus datos.
* En Rust, puedes crear tus propios tipos de datos llamados "enums", que son como tener un cinturón de herramientas multiusos.
* Rust usa un sistema llamado "borrowing" para manejar la memoria, que es como prestar tus juguetes a tus amigos pero asegurándote de que siempre te los devuelvan.

## Misiones heroicas

### Misión 1: El Generador de Nombres de Superhéroes

Crea un programa que genere nombres de superhéroes aleatorios combinando adjetivos y sustantivos. Usa vectores y la función `rand` del crate `rand`.

```rust
use rand::Rng;

fn generar_nombre_superheroe() -> String {
    let adjetivos = vec!["Veloz", "Poderoso", "Brillante", "Invencible", "Oxidado"];
    let sustantivos = vec!["Cangrejo", "Programador", "Compilador", "Depurador", "Borrow Checker"];
    
    let mut rng = rand::thread_rng();
    let adjetivo = adjetivos[rng.gen_range(0..adjetivos.len())];
    let sustantivo = sustantivos[rng.gen_range(0..sustantivos.len())];
    
    format!("{} {}", adjetivo, sustantivo)
}

fn main() {
    for _ in 0..5 {
        println!("Nuevo superhéroe: {}", generar_nombre_superheroe());
    }
}
```

### Misión 2: El Analizador de Superpoderes

Crea una función que analice los superpoderes de un héroe usando un HashMap. La función debe determinar si el héroe es apto para una misión basándose en la presencia y nivel de ciertos poderes.

```rust
use std::collections::HashMap;

fn analizar_superheroe(poderes: &HashMap<String, u32>) -> bool {
    let poderes_necesarios = vec!["fuerza", "velocidad", "inteligencia"];
    let mut es_apto = true;
    
    for poder in poderes_necesarios {
        match poderes.get(poder) {
            Some(&nivel) if nivel >= 50 => println!("{}: Nivel adecuado", poder),
            Some(&nivel) => {
                println!("{}: Nivel insuficiente ({})", poder, nivel);
                es_apto = false;
            },
            None => {
                println!("{}: Poder no encontrado", poder);
                es_apto = false;
            }
        }
    }
    
    es_apto
}

fn main() {
    let mut iron_crab = HashMap::new();
    iron_crab.insert(String::from("fuerza"), 75);
    iron_crab.insert(String::from("velocidad"), 60);
    iron_crab.insert(String::from("inteligencia"), 90);
    
    if analizar_superheroe(&iron_crab) {
        println!("¡Iron Crab está listo para la misión!");
    } else {
        println!("Iron Crab necesita más entrenamiento.");
    }
}
```

### Misión 3: El Comunicador de Superhéroes

Crea un programa que simule un comunicador de superhéroes. Usa hilos para enviar y recibir mensajes concurrentemente, y canales para la comunicación entre hilos.

```rust
use std::thread;
use std::sync::mpsc;
use std::time::Duration;

fn enviar_mensaje(tx: mpsc::Sender<String>, mensaje: String) {
    for _ in 0..5 {
        tx.send(mensaje.clone()).unwrap();
        thread::sleep(Duration::from_secs(1));
    }
}

fn main() {
    let (tx1, rx) = mpsc::channel();
    let tx2 = tx1.clone();

    thread::spawn(move || {
        enviar_mensaje(tx1, String::from("Mensaje de Iron Crab"));
    });

    thread::spawn(move || {
        enviar_mensaje(tx2, String::from("Respuesta de Rust Ranger"));
    });

    for recibido in rx {
        println!("Comunicador: {}", recibido);
    }
}
```

## Conclusión

¡Excelente trabajo, jóvenes Rust Rangers! Han demostrado gran valentía y astucia al enfrentarse a estas misiones más avanzadas. Recuerden, la práctica hace al maestro, así que sigan entrenando con estos nuevos superpoderes. En nuestra próxima lección, exploraremos los misterios de los traits y los lifetimes en Rust, herramientas poderosas para crear superhéroes aún más impresionantes.

 ¡Hasta entonces, que el Borrow Checker los acompañe!

![gato](../images/Gatocelebrar.jpeg)
