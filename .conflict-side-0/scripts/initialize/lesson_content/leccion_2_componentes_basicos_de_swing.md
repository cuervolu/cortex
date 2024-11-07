# Componentes Básicos de Swing

¡Bienvenidos de nuevo, aprendices de la magia GUI! 🧙‍♂️ Hoy conoceremos a los habitantes más importantes del Reino GUI: ¡los componentes de Swing! Cada uno tiene sus propios superpoderes especiales.

## Los Componentes Básicos

### 1. Los Nobles Botones (JButton)

Los botones son como los caballeros del reino: ¡siempre listos para la acción!

```java
import javax.swing.*;

public class BotonMagico extends JFrame {
    public BotonMagico() {
        setTitle("Botón Mágico");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        JButton boton = new JButton("¡Haz clic para magia!");
        
        // Cuando se hace clic, muestra un mensaje
        boton.addActionListener(e -> 
            JOptionPane.showMessageDialog(null, "¡Magia!")
        );
        
        panel.add(boton);
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> 
            new BotonMagico().setVisible(true)
        );
    }
}
```

### 2. Los Sabios Campos de Texto

Tenemos tres tipos principales:

- **JTextField**: Para una línea de texto
- **JTextArea**: Para múltiples líneas
- **JPasswordField**: Para contraseñas secretas

```java
import javax.swing.*;
import java.awt.*;

public class CamposTexto extends JFrame {
    public CamposTexto() {
        setTitle("Campos de Texto Mágicos");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridLayout(3, 1));
        
        // Campo de una línea
        panel.add(new JTextField("Escribe aquí"));
        
        // Campo de contraseña
        panel.add(new JPasswordField("secreto"));
        
        // Área de texto
        panel.add(new JTextArea("Múltiples\nlíneas"));
        
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> 
            new CamposTexto().setVisible(true)
        );
    }
}
```

### 3. Las Etiquetas Mensajeras (JLabel)

¡Como heraldos que muestran mensajes al usuario!

```java
JLabel etiqueta = new JLabel("¡Bienvenido al Reino GUI!");
etiqueta.setForeground(Color.BLUE);  // Color del texto
etiqueta.setFont(new Font("Arial", Font.BOLD, 16));  // Fuente
```

### 4. Los Selectores Mágicos

Dos tipos principales:

- **JCheckBox**: Para opciones múltiples
- **JRadioButton**: Para elegir una opción de un grupo

```java
import javax.swing.*;

public class Selectores extends JFrame {
    public Selectores() {
        setTitle("Selectores Mágicos");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        
        // Checkbox
        JCheckBox check = new JCheckBox("¿Tienes poderes mágicos?");
        panel.add(check);
        
        // Radio buttons
        ButtonGroup grupo = new ButtonGroup();
        JRadioButton radio1 = new JRadioButton("Mago");
        JRadioButton radio2 = new JRadioButton("Hechicero");
        grupo.add(radio1);
        grupo.add(radio2);
        
        panel.add(radio1);
        panel.add(radio2);
        
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> 
            new Selectores().setVisible(true)
        );
    }
}
```

## Eventos Mágicos

Los eventos son como hechizos que hacen que nuestros componentes respondan a las acciones.

### Tipos Principales de Eventos

1. **ActionEvent**: Para clics y acciones
2. **MouseEvent**: Para el ratón
3. **KeyEvent**: Para el teclado

```java
import javax.swing.*;
import java.awt.*;

public class EventosMagicos extends JFrame {
    private JLabel estado;
    
    public EventosMagicos() {
        setTitle("Eventos Mágicos");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        estado = new JLabel("Esperando magia...");
        JButton boton = new JButton("¡Clic!");
        
        // Action Listener (para clics)
        boton.addActionListener(e -> 
            estado.setText("¡Magia activada!")
        );
        
        // Mouse Listener (para el ratón)
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                estado.setText("¡El ratón se acerca!");
            }
        });
        
        panel.add(estado);
        panel.add(boton);
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> 
            new EventosMagicos().setVisible(true)
        );
    }
}
```

## Tips Mágicos 🌟

1. **Mantén el Orden**
   - Agrupa componentes relacionados en paneles
   - Usa layouts apropiados

2. **Nombres Descriptivos**
   - Da nombres claros a tus variables
   - Usa comentarios cuando sea necesario

3. **Manejo de Eventos**
   - Mantén los event listeners cortos y simples
   - Considera usar clases separadas para eventos complejos

## Ejercicios Teóricos: ¡Pon a Prueba tu Conocimiento! 🎯

1. **Empareja el Componente**

   ```
   A. JButton     ___ Muestra texto no editable
   B. JTextField  ___ Permite hacer clic
   C. JLabel      ___ Permite escribir una línea
   ```

2. **Verdadero o Falso**
   - [ ] JPasswordField muestra el texto tal cual se escribe
   - [ ] Los JRadioButton permiten seleccionar múltiples opciones
   - [ ] ActionListener se usa principalmente para eventos de clic

3. **Pregunta de la Torre Mágica**
   ¿Qué componente usarías para...?
   - Mostrar un mensaje de error
   - Permitir seleccionar múltiples opciones
   - Crear un botón que cambie de color

¡La práctica hace al maestro! En la próxima lección, aprenderemos sobre layouts más avanzados. ¡Hasta entonces, sigue practicando tus hechizos GUI! 🧙‍♂️✨
