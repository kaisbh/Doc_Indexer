package MyPackage;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import java.io.*;
import java.util.ArrayList;

public class MyIndexer {
	private static StandardAnalyzer analyzer = new StandardAnalyzer();
	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();

	MyIndexer(String indexDir) throws IOException {
		FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(dir, config);
	}

	public void indexFileOrDirectory(String fileName) throws IOException {
		addFiles(new File(fileName));
		int originalNumDocs = writer.numDocs();
		Integer i = 0;
		for (File f : queue) {
			FileReader fr = null;
			try {
				Document doc = new Document();
				fr = new FileReader(f);
				doc.add(new TextField("contents", fr));
				doc.add(new StringField("path", f.getPath(), Field.Store.YES));
				doc.add(new StringField("filename", f.getName(), Field.Store.YES));
				doc.add(new StringField("id", i.toString(), Field.Store.YES));
				writer.addDocument(doc);
				System.out.println("Ajouté: " + f);
				i++;
			} catch (Exception e) {
				System.out.println("Impossible d'ajouter: " + f);
			} finally {
				fr.close();
			}
		}
		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("############################");
		System.out.println((newNumDocs - originalNumDocs) + " documents ajoutés.");
		System.out.println("###########################");
		queue.clear();
	}

	private void addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " n'existe ps.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			if (filename.endsWith(".htm") || filename.endsWith(".html") || filename.endsWith(".xml")
					|| filename.endsWith(".txt")) {
				queue.add(file);
			} else {
				System.out.println("Ignoré " + filename);
			}
		}
	}
// 
	public void closeIndex() throws IOException {
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Entrez le chemin où  l'index doit être créé:");
		//String indexLocation = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		MyIndexer indexer = null;
		try {
			//indexLocation = s;
			indexer = new MyIndexer(s);
		} catch (Exception ex) {
			System.out.println("Ne peut créer l'index..." + ex.getMessage());
			System.exit(-1);
		}
		while (!s.equalsIgnoreCase("q")) {
			try {
				System.out.println(
						"Donner le chemin du corpus:");
				System.out.println("[Types de fichiers acceptés: .xml, .html, .html, .txt]");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					break;
				}

				// try to add file into the index
				indexer.indexFileOrDirectory(s);
			} catch (Exception e) {
				System.out.println("Erreur pour indexer " + s + " : " + e.getMessage());
			}
		}
		indexer.closeIndex();
		
	}
}
