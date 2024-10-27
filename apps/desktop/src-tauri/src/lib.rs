use common::state::AppState;
use std::sync::Mutex;
use tauri::Manager;
use tauri_plugin_log::fern::colors::ColoredLevelConfig;
use tauri_plugin_log::RotationStrategy;

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
            let salt_path = app
                .path()
                .app_local_data_dir()
                .expect("could not resolve app local data path")
                .join("salt.txt");

            app.handle().plugin(tauri_plugin_stronghold::Builder::with_argon2(&salt_path).build())?;
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
            auth::commands::set_user,
            auth::commands::get_user,
            auth::commands::clear_user,
            auth::commands::get_token,
            ai_chat::commands::send_chat_prompt,
            ai_chat::anthropic::commands::chat_with_claude,
            ai_chat::anthropic::commands::chat_with_history,
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
