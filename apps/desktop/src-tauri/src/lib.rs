pub(crate) mod ai;
mod education;
mod error;
mod state;
mod auth;

use log::error;
use reqwest::Client;
use std::sync::{LazyLock, Mutex};
use tauri::Manager;
use tauri_plugin_log::fern::colors::ColoredLevelConfig;
use tauri_plugin_log::RotationStrategy;
use crate::state::AppState;
use window_vibrancy::apply_acrylic;

pub const API_BASE_URL: &str = "http://localhost:8088/api/v1";

pub static CLIENT: LazyLock<Client> = LazyLock::new(|| {
    Client::builder()
        .build()
        .expect("Failed to build reqwest client")
});

fn setup_logger(app: &mut tauri::App) -> Result<(), Box<dyn std::error::Error>> {
    // create the log plugin as usual, but call split() instead of build()
    let (tauri_plugin_log, _max_level, logger) = tauri_plugin_log::Builder::new()
        .max_file_size(1024 * 1024 * 10) // 10 MB
        .rotation_strategy(RotationStrategy::KeepAll) // keep all logs in the log directory
        .timezone_strategy(tauri_plugin_log::TimezoneStrategy::UseLocal)
        .level(log::LevelFilter::Info)
        .with_colors(ColoredLevelConfig::default())
        .level_for("tauri", log::LevelFilter::Warn)
        .level_for("wry", log::LevelFilter::Warn)
        .level_for("tracing", log::LevelFilter::Warn)
        .level_for("cortex_lib", log::LevelFilter::Debug)
        .split(app.handle())?;

    // on debug builds, set up the DevTools plugin and pipe the logger from tauri-plugin-log
    #[cfg(debug_assertions)]
    {
        let mut devtools_builder = tauri_plugin_devtools::Builder::default();
        devtools_builder.attach_logger(logger);
        app.handle().plugin(devtools_builder.init())?;
    }
    // on release builds, only attach the logger from tauri-plugin-log
    #[cfg(not(debug_assertions))]
    {
        tauri_plugin_log::attach_logger(_max_level, logger);
    }

    app.handle().plugin(tauri_plugin_log)?;

    Ok(())
}

async fn init_ollama_models(app_handle: tauri::AppHandle) {
    if let Err(e) = ai::ollama_models::init(&app_handle).await {
        error!("Failed to initialize Ollama models: {:?}", e);
    }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    let builder = tauri::Builder::default()
        .plugin(tauri_plugin_store::Builder::new().build())
        .setup(|app| {
            setup_logger(app)?;
            app.manage(AppState {
                user: Mutex::new(None),
                token: Mutex::new(None),
            });
            let app_handle = app.handle().clone();
            tauri::async_runtime::spawn(async move {
                init_ollama_models(app_handle).await;
            });

            #[cfg(target_os = "windows")]
            apply_acrylic(&window, Some((18, 18, 18, 125)))
                .expect("Unsupported platform! 'apply_acrylic' is only supported on Windows");
            
            
            Ok(())
        });

    builder
        .plugin(tauri_plugin_notification::init())
        .plugin(tauri_plugin_oauth::init())
        .plugin(tauri_plugin_clipboard_manager::init())
        .plugin(tauri_plugin_window_state::Builder::new().build())
        .plugin(tauri_plugin_updater::Builder::new().build())
        .plugin(tauri_plugin_shell::init())
        .plugin(tauri_plugin_process::init())
        .plugin(tauri_plugin_os::init())
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_fs::init())
        .invoke_handler(tauri::generate_handler![
            auth::set_user,
            auth::get_user,
            auth::clear_user,
            auth::get_token, 
            ai::commands::is_ollama_installed,
            ai::commands::send_prompt_to_ollama,
            ai::commands::list_local_models,
            ai::commands::get_ollama_models,
            ai::commands::refresh_ollama_models,
            ai::commands::list_ollama_models,
            ai::commands::show_ollama_model,
            ai::commands::pull_ollama_model,
            ai::commands::forced_update,
            ai::commands::delete_ollama_model,
            education::roadmaps::commands::fetch_all_roadmaps,
            education::roadmaps::commands::get_roadmap,
            education::exercises::commands::get_exercises,
            education::exercises::commands::get_exercise_details,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
