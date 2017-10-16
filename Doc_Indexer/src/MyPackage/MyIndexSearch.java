package MyPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.FileReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MyIndexSearch {
	private static StandardAnalyzer analyzer;
	private static final String INDEX_DIR = "D:\\RIIFT\\MyIndex";

	public static ArrayList<String> getStopWord(String FileName) {
		ArrayList<String> stop_words = new ArrayList();
		try {
			BufferedReader is = new BufferedReader(new FileReader(FileName));
			String inputLine;
			while ((inputLine = is.readLine()) != null) {
				stop_words.add(inputLine);
				System.out.println(inputLine);
			}
		} catch (IOException io) {

		}
		return stop_words;
	}

	private static TopDocs searchByFirstName(String firstName, IndexSearcher searcher) throws Exception {
		QueryParser qp = new QueryParser("firstName", new StandardAnalyzer());
		Query firstNameQuery = qp.parse(firstName);
		TopDocs hits = searcher.search(firstNameQuery, 10);
		return hits;
	}

	private static TopDocs searchById(Integer id, IndexSearcher searcher) throws Exception {
		QueryParser qp = new QueryParser("id", new StandardAnalyzer());
		Query idQuery = qp.parse(id.toString());
		TopDocs hits = searcher.search(idQuery, 10);
		return hits;
	}

	private static TopDocs searchBycontents(String contents, IndexSearcher searcher) throws Exception {
		QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
		Query idQuery = qp.parse(contents);
		TopDocs hits = searcher.search(idQuery, 10);
		return hits;
	}

	private static IndexSearcher createSearcher() throws IOException {
		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

	public static void main(String[] args) throws Exception {
		
		getStopWord("D:\\RIIFT\\MyCorpus\\1.txt");
		
		IndexSearcher searcher = createSearcher();

		// Search by Contents
		String content = "consolidation";
		TopDocs foundDocs3 = searchBycontents(content, searcher);

		System.out.println("Total Results :: " + foundDocs3.totalHits);

		for (ScoreDoc sd : foundDocs3.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			System.out.println(String.format(d.get("filename")));
		}

		// Search by ID
		TopDocs foundDocs = searchById(1, searcher);

		System.out.println("Total Results :: " + foundDocs.totalHits);

		for (ScoreDoc sd : foundDocs.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			System.out.println(String.format(d.get("firstName")));
		}

		// Search by firstName
		TopDocs foundDocs2 = searchByFirstName("Gayo", searcher);

		System.out.println("Total Results :: " + foundDocs2.totalHits);

		for (ScoreDoc sd : foundDocs2.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			System.out.println(String.format(d.get("id")));
		}
	}
}