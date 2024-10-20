use log::error;
use std::sync::Mutex;
use tauri::Manager;
use tauri_plugin_log::fern::colors::ColoredLevelConfig;
use tauri_plugin_log::RotationStrategy;
use common::state::AppState;


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

#[tauri::command]
async fn init_ollama_models(app_handle: tauri::AppHandle) {
    if let Err(e) = ai_chat::ollama_models::init(&app_handle).await {
        error!("Failed to initialize Ollama models: {:?}", e);
    }
}

#[tauri::command]
async fn close_splashscreen_show_main(app_handle: tauri::AppHandle) -> Result<(), String> {
    let splash_window = app_handle.get_webview_window("splashscreen").unwrap();
    let main_window = app_handle.get_webview_window("main").unwrap();
    splash_window.close().unwrap();
    main_window.show().unwrap();

    Ok(())
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
            close_splashscreen_show_main,
            init_ollama_models,
            auth::commands::set_user,
            auth::commands::get_user,
            auth::commands::clear_user,
            auth::commands::get_token, 
            ai_chat::commands::is_ollama_installed,
            ai_chat::commands::send_prompt_to_ollama,
            ai_chat::commands::list_local_models,
            ai_chat::commands::get_ollama_models,
            ai_chat::commands::refresh_ollama_models,
            ai_chat::commands::list_ollama_models,
            ai_chat::commands::show_ollama_model,
            ai_chat::commands::pull_ollama_model,
            ai_chat::commands::forced_update,
            ai_chat::commands::delete_ollama_model,
            education::roadmaps::commands::fetch_all_roadmaps,
            education::roadmaps::commands::fetch_roadmap_course,
            education::roadmaps::commands::get_roadmap,
            education::exercises::commands::get_exercises,
            education::exercises::commands::get_exercise_details,
            education::courses::commands::get_courses,
            education::courses::commands::get_course_details,
            education::lessons::commands::fetch_all_lessons,
            education::lessons::commands::get_lesson,
            education::modules::commands::fetch_all_modules,
            education::modules::commands::get_module,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
