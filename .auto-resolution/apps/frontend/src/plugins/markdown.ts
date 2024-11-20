import highlight from 'highlight.js/lib/core';
import typescript from 'highlight.js/lib/languages/typescript';
import python from 'highlight.js/lib/languages/python';
import go from 'highlight.js/lib/languages/go';
import java from 'highlight.js/lib/languages/java';
import rust from 'highlight.js/lib/languages/rust';
import 'highlight.js/styles/atom-one-dark.css';

highlight.registerLanguage('typescript', typescript);
highlight.registerLanguage('python', python);
highlight.registerLanguage('go', go);
highlight.registerLanguage('java', java);
highlight.registerLanguage('rust', rust);

export interface MarkdownPluginOptions {
  html?: boolean;
  linkify?: boolean;
  typographer?: boolean;
  breaks?: boolean;
}

const markdownStyles = `
.prose {
  width: 100%;
  color: inherit;
}

.prose pre {
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1rem 0;
}

.prose code {
  color: inherit;
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
  font-size: 0.875em;
}

.prose pre code {
  padding: 0;
  border-radius: 0;
  background: none;
}

/* Estilos específicos para el tema oscuro */
.dark .prose .hljs {
  background: #1e1e1e;
  color: #d4d4d4;
}

/* Ajustes adicionales para highlight.js */
.hljs {
  background: #282c34;
  color: #abb2bf;
  padding: 1em;
  border-radius: 0.5em;
}
`;

export default defineNuxtPlugin((nuxtApp) => {
  const markdownOptions = {
    html: true,
    linkify: true,
    typographer: true,
    breaks: true,
    highlight: function (str: string, lang: string) {
      if (lang && highlight.getLanguage(lang)) {
        try {
          const result = highlight.highlight(str, {
            language: lang,
            ignoreIllegals: true
          }).value;
          return `<pre class="hljs language-${lang}"><code>${result}</code></pre>`;
        } catch (error) {
          console.error('Error highlighting code:', error);
        }
      }
      return `<pre class="hljs"><code>${str}</code></pre>`;
    }
  };

  if (import.meta.client) {
    const styleSheet = document.createElement('style');
    styleSheet.textContent = markdownStyles;
    document.head.appendChild(styleSheet);
  }

  nuxtApp.vueApp.provide('markdownOptions', markdownOptions);

  const useMarkdown = () => {
    return {
      options: markdownOptions,
      configureOptions: (options: Partial<MarkdownPluginOptions>) => {
        Object.assign(markdownOptions, options);
      }
    };
  };

  return {
    provide: {
      markdown: useMarkdown()
    }
  };
});