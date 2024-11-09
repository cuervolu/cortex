use ai_chat::keystore::manager::KeystoreManager;
use ai_chat::session::manager::AISessionManager;
use common::state::AppState;
use std::sync::Arc;
use std::sync::Mutex;
use tauri::Manager;
use tauri_plugin_log::fern::colors::ColoredLevelConfig;
use tauri_plugin_log::RotationStrategy;

fn setup_logger(app: &mut tauri::App) -> Result<(), Box<dyn std::error::Error>> {
    // Set base log level based on environment
    let base_log_level = if cfg!(debug_assertions) {
        log::LevelFilter::Debug
    } else {
        log::LevelFilter::Info
    };

    // Create builder with environment-specific configuration
    let builder = tauri_plugin_log::Builder::new()
        .max_file_size(1024 * 1024 * 10) // 10 MB
        .rotation_strategy(RotationStrategy::KeepAll)
        .timezone_strategy(tauri_plugin_log::TimezoneStrategy::UseLocal)
        .with_colors(ColoredLevelConfig::default())
        // Set default level based on environment
        .level(base_log_level)
        // Configure specific module levels
        .level_for("tauri", log::LevelFilter::Warn)
        .level_for("wry", log::LevelFilter::Warn)
        .level_for("hyper", log::LevelFilter::Warn)
        .level_for("hyper::proto", log::LevelFilter::Warn)
        .level_for("hyper_util::client::legacy", log::LevelFilter::Warn)
        .level_for("devtools_core", log::LevelFilter::Warn)
        .level_for("tracing", log::LevelFilter::Warn)
        // Set application specific level
        .level_for("cortex_lib", if cfg!(debug_assertions) {
            log::LevelFilter::Trace
        } else {
            log::LevelFilter::Info
        });

    #[cfg(debug_assertions)] {
        // For debug builds, setup DevTools with the logger
        let (plugin, _max_level, logger) = builder.split(app.handle())?;
        let mut devtools_builder = tauri_plugin_devtools::Builder::default();
        devtools_builder.attach_logger(logger);
        app.handle().plugin(devtools_builder.init())?;
        app.handle().plugin(plugin)?;
    }

    #[cfg(not(debug_assertions))] {
        // For release builds, just build and set up the logger
        app.handle().plugin(builder.build())?;
    }

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
    let builder =
        tauri::Builder::default().plugin(tauri_plugin_store::Builder::new().build()).setup(|app| {
            setup_logger(app)?;

            // Initialize AppState
            app.manage(AppState {
                user: Mutex::new(None),
                token: Mutex::new(None),
            });

            let keystore_manager = Arc::new(KeystoreManager::new());
            app.manage(keystore_manager.clone());

            let window = app.get_webview_window("main").expect("main window not found");
            let session_manager = Arc::new(AISessionManager::new(window)?);
            app.manage(session_manager);

            Ok(())
        });

    builder
        .plugin(tauri_plugin_notification::init())
        .plugin(tauri_plugin_oauth::init())
        .plugin(tauri_plugin_clipboard_manager::init())
        .plugin(tauri_plugin_updater::Builder::new().build())
        .plugin(tauri_plugin_shell::init())
        .plugin(tauri_plugin_process::init())
        .plugin(tauri_plugin_os::init())
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_fs::init())
        .invoke_handler(tauri::generate_handler![
            close_splashscreen_show_main,
            // Auth commands
            auth::commands::set_user,
            auth::commands::get_user,
            auth::commands::clear_user,
            auth::commands::get_token,
            auth::commands::start_oauth_flow,
            auth::commands::cancel_oauth_flow,
            auth::commands::handle_oauth_callback,
            // Education commands
            // Roadmaps
            education::roadmaps::commands::fetch_all_roadmaps,
            education::roadmaps::commands::fetch_roadmap_course,
            education::roadmaps::commands::get_roadmap,
            education::roadmaps::commands::create_new_roadmap,
            education::roadmaps::commands::update_roadmap_command,
            education::roadmaps::commands::upload_roadmap_image_command,
            education::roadmaps::commands::delete_roadmap_command,
            
            // Courses
            education::courses::commands::fetch_all_courses,
            education::courses::commands::get_course,
            education::courses::commands::create_new_course,
            education::courses::commands::update_course_command,
            education::courses::commands::delete_course_command,
            education::courses::commands::upload_course_image_command,
            
            // Modules
            education::modules::commands::fetch_all_modules,
            education::modules::commands::get_module,
            
            // Lessons
            education::lessons::commands::fetch_all_lessons,
            education::lessons::commands::get_lesson,
            
            // Exercises
            education::exercises::commands::get_exercises,
            education::exercises::commands::get_exercise_details,
            education::exercises::commands::execute_code,
            education::exercises::commands::get_code_execution_result,
            
            // AI Chat commands
            ai_chat::commands::start_exercise_session,
            ai_chat::commands::send_message,
            ai_chat::commands::end_exercise_session,
            ai_chat::commands::set_provider,
            ai_chat::commands::set_provider_api_key,
            ai_chat::commands::get_provider_api_key,
            ai_chat::commands::remove_provider_api_key,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
