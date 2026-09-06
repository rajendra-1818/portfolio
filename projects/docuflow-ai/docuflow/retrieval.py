from dataclasses import dataclass
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from .store import Document


@dataclass(frozen=True)
class SearchResult:
    document: Document
    score: float
    excerpt: str


class Retriever:
    def search(self, documents: list[Document], query: str, limit: int = 5) -> list[SearchResult]:
        if not documents or not query.strip():
            return []
        corpus = [f"{d.title}\n{d.text}" for d in documents]
        vectorizer = TfidfVectorizer(stop_words="english")
        try:
            matrix = vectorizer.fit_transform(corpus)
        except ValueError as exc:
            if "empty vocabulary" in str(exc):
                return []
            raise
        scores = cosine_similarity(vectorizer.transform([query]), matrix).ravel()
        ranked = sorted(zip(documents, scores), key=lambda pair: pair[1], reverse=True)
        results = []
        for document, score in ranked[:limit]:
            if score <= 0:
                continue
            results.append(SearchResult(document, float(score), self._excerpt(document.text, query)))
        return results

    def _excerpt(self, text: str, query: str) -> str:
        terms = [term.lower() for term in query.split() if len(term) > 2]
        sentences = [s.strip() for s in text.replace("\n", " ").split(".") if s.strip()]
        for sentence in sentences:
            if any(term in sentence.lower() for term in terms):
                return sentence[:240] + ("…" if len(sentence) > 240 else "")
        return text[:240] + ("…" if len(text) > 240 else "")
