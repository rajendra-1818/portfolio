import unittest
from tempfile import TemporaryDirectory
from pathlib import Path

from docuflow.retrieval import Retriever
from docuflow.store import Document, DocumentStore


class RetrievalTest(unittest.TestCase):
    def setUp(self):
        self.docs = [Document(1, "Runbook", "Deployments require health checks and rollback plans."),
                     Document(2, "Database", "SQLite uses transactions to preserve data integrity.")]
        self.retriever = Retriever()

    def test_relevant_document_ranks_first(self):
        result = self.retriever.search(self.docs, "rollback health", 1)
        self.assertEqual([r.document.id for r in result], [1])
        self.assertIn("rollback", result[0].excerpt)

    def test_no_match_and_empty_collection(self):
        self.assertEqual(self.retriever.search(self.docs, "volcanoes"), [])
        self.assertEqual(self.retriever.search([], "rollback"), [])

    def test_stopwords_and_punctuation_do_not_crash(self):
        docs = [Document(1, "the", "and or the is a !!")]
        self.assertEqual(self.retriever.search(docs, "the and"), [])
        self.assertEqual(self.retriever.search(self.docs, "!!!"), [])

    def test_store_persists_literal_sql_text(self):
        with TemporaryDirectory() as folder:
            path = str(Path(folder) / "docs.db")
            text = "Robert'); DROP TABLE documents;--"
            saved = DocumentStore(path).add("Literal input", text)
            self.assertEqual(DocumentStore(path).all(), [saved])


if __name__ == "__main__":
    unittest.main()
