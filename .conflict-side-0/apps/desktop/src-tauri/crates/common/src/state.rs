use std::sync::Mutex;
use serde::{Serialize, Deserialize};

#[derive(Default, Serialize, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct User {
    pub id: u64,
    pub username: String,
    pub email: String,
    pub first_name: String,
    pub last_name: String,
    pub full_name: String,
    pub avatar_url: Option<String>,
    pub date_of_birth: String,
    pub country_code: String,
    pub gender: String,
    pub account_locked: bool,
    pub enabled: bool,
    pub roles: Vec<String>,
}

#[derive(Default)]
pub struct AppState {
    pub user: Mutex<Option<User>>,
    pub token: Mutex<Option<String>>,
}