pub(crate) mod commands;

use crate::{API_BASE_URL, CLIENT};
use crate::education::models::{RoadmapDetail, RoadmapListResponse};
use crate::error::AppError;

// TODO: Replace this with a real token
const AUTH_TOKEN: &str = "eyJhbGciOiJIUzM4NCJ9.eyJmdWxsbmFtZSI6IsOBbmdlbCBDdWVydm8iLCJzdWIiOiJjdWVydm9sdSIsImlhdCI6MTcyNzkxMTAyNCwiZXhwIjoxNzI3OTk3NDI0LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.OjXGrVLsw-ghye4eRwy8skfpN-Fz8m-_7N4A_YrUPPXYFodXbHJfNTUhl15jxaBC";


pub async fn fetch_roadmaps() -> Result<RoadmapListResponse, AppError> {
    let response = CLIENT
        .get(format!("{}/education/roadmap", API_BASE_URL))
        .bearer_auth(AUTH_TOKEN)
        .send()
        .await
        .map_err(AppError::RequestError)? // If the request fails, return a RequestError
        .json::<RoadmapListResponse>()
        .await
        .map_err(AppError::RequestError)?;  // If the JSON parsing fails, return a RequestError

    Ok(response)
}


pub async fn fetch_roadmaps_details(slug: &str) -> Result<RoadmapDetail, AppError> {
    let response = CLIENT
        .get(format!("{}/education/roadmap/{}", API_BASE_URL, slug))
        .bearer_auth(AUTH_TOKEN)
        .send()
        .await
        .map_err(AppError::RequestError)? 
        .json::<RoadmapDetail>()
        .await
        .map_err(AppError::RequestError)?;  

    Ok(response)
}