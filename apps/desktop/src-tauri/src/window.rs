use tauri::{
    Manager, WebviewUrl, WebviewWindowBuilder,
    
};
use log::debug;

pub struct WindowManager;

impl WindowManager {
    pub fn create_windows(app: &tauri::App) -> Result<(), Box<dyn std::error::Error>> {
        debug!("Creating application windows");

        // Crear la ventana principal (inicialmente oculta)
        let main_window = WebviewWindowBuilder::new(
            app,
            "main",
            WebviewUrl::App("/".into())
        )
            .title("Cortex")
            .inner_size(1100.0, 650.0)
            .min_inner_size(800.0, 600.0)
            .resizable(true)
            .fullscreen(false)
            .center()
            .decorations(false)
            .transparent(true)
            .visible(false)
            .shadow(true)
            .build()?;

        // Configurar efectos de la ventana
        #[cfg(target_os = "windows")] {
            use tauri::{window::{Effect, EffectState, Color, EffectsBuilder}};
            debug!("Setting window effects for Windows");
            main_window.set_effects(
                EffectsBuilder::new()
                    .effect(Effect::Acrylic)
                    .state(EffectState::Active)
                    .radius(5.0)
                    .color(Color(0, 0, 0, 125)) 
                    .build()
            )?;
        }

        // Crear la ventana de splashscreen
        let splashscreen = WebviewWindowBuilder::new(
            app,
            "splashscreen",
            WebviewUrl::App("/splashscreen".into())
        )
            .inner_size(580.0, 400.0)
            .min_inner_size(580.0, 400.0)
            .max_inner_size(580.0, 400.0)
            .resizable(false)
            .maximizable(false)
            .minimizable(false)
            .center()
            .decorations(false)
            .transparent(true)
            .always_on_top(true)
            .focused(true)
            .shadow(true)
            .visible(true)
            .build()?;

        debug!("Windows created successfully");
        Ok(())
    }

    pub fn handle_window_transition(app_handle: &tauri::AppHandle) -> Result<(), Box<dyn std::error::Error>> {
        debug!("Handling window transition");

        // Obtener referencias a las ventanas
        let splash_window = app_handle
            .get_webview_window("splashscreen")
            .ok_or_else(|| "Splashscreen window not found".to_string())?;

        let main_window = app_handle
            .get_webview_window("main")
            .ok_or_else(|| "Main window not found".to_string())?;

        // Ocultar y cerrar splashscreen
        splash_window.hide()?;
        splash_window.close()?;

        // Mostrar ventana principal
        main_window.show()?;
        main_window.set_focus()?;

        debug!("Window transition completed successfully");
        Ok(())
    }
}