use serde::{Serialize, Deserialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct ChatContext {
    username: String,
    exercise_title: String,
    exercise_instructions: String,
    exercise_hints: Option<String>,
    editor_content: Option<String>,
    editor_language: Option<String>,
}

impl ChatContext {
    pub fn new(
        username: String,
        exercise_title: String,
        exercise_instructions: String,
        exercise_hints: Option<String>,
        editor_content: Option<String>,
        editor_language: Option<String>,
    ) -> Self {
        Self {
            username,
            exercise_title,
            exercise_instructions,
            exercise_hints,
            editor_content,
            editor_language,
        }
    }

    pub fn build_system_prompt(&self) -> String {
        let mut prompt = String::new();
        prompt.push_str(
            "Eres CORTEX-IA, un asistente educativo especializado en una plataforma de ejercicios de programación. \
            Tu objetivo principal es guiar a los estudiantes en su proceso de aprendizaje, siguiendo estas directrices:\n\n"
        );
        prompt.push_str("\
            1. NUNCA proporciones soluciones completas o código que resuelva directamente el ejercicio.\n\
            2. Enfócate en dar pistas constructivas y hacer preguntas que estimulen el pensamiento crítico.\n\
            3. Adapta tu nivel de ayuda según el progreso y comprensión del estudiante.\n\
            4. Si notas errores en el código, señálalos de manera constructiva y sugiere áreas de mejora.\n\
            5. Mantén un tono amigable y motivador, celebrando los avances del estudiante.\n\n"
        );
        prompt.push_str(&format!(
            "Contexto del ejercicio:\n\
            - Título: {}\n\
            - Instrucciones: {}\n",
            self.exercise_title,
            self.exercise_instructions
        ));

        if let Some(hints) = &self.exercise_hints {
            prompt.push_str(&format!("- Pistas disponibles: {}\n", hints));
        }
        prompt.push_str(&format!(
            "\nEstás interactuando con el estudiante: {}\n",
            self.username
        ));
        
        if let Some(content) = &self.editor_content {
            prompt.push_str("\nCódigo actual del estudiante ");
            if let Some(lang) = &self.editor_language {
                prompt.push_str(&format!("en {}:\n", lang));
            } else {
                prompt.push_str(":\n");
            }
            prompt.push_str(&format!("```\n{}\n```\n", content));
        }
        
        prompt.push_str("\nRecuerda:\n\
            - Guía sin resolver\n\
            - Fomenta el pensamiento independiente\n\
            - Celebra los logros\n\
            - Mantén un tono motivador\n"
        );

        prompt
    }
}