pub(crate) mod commands;

use crate::education::models::{PaginatedRoadmaps, RoadmapDetails};
use crate::error::AppError;
use crate::{API_BASE_URL, CLIENT};

// TODO: Replace this with a real token
const AUTH_TOKEN: &str = "eyJhbGciOiJIUzM4NCJ9.eyJmdWxsbmFtZSI6IsOBbmdlbCBDdWVydm8iLCJzdWIiOiJjdWVydm9sdSIsImlhdCI6MTcyODEwMDYwOSwiZXhwIjoxNzI4MTg3MDA5LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.oSlebEYCww4G1qOdedElXW9qMWR2Eo2LNlvNq7EYFlr1EZvlekcr5_ShLf-HJWNM";

pub async fn fetch_roadmaps() -> Result<PaginatedRoadmaps, AppError> {
    let response = CLIENT
        .get(format!("{}/education/roadmap", API_BASE_URL))
        .bearer_auth(AUTH_TOKEN)
        .send()
        .await
        .map_err(AppError::RequestError)? // If the request fails, return a RequestError
        .json::<PaginatedRoadmaps>()
        .await
        .map_err(AppError::RequestError)?; // If the JSON parsing fails, return a RequestError

    Ok(response)
}

pub async fn fetch_roadmaps_details(slug: &str) -> Result<RoadmapDetails, AppError> {
    let response = CLIENT
        .get(format!("{}/education/roadmap/{}", API_BASE_URL, slug))
        .bearer_auth(AUTH_TOKEN)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<RoadmapDetails>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}
