package search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by hxiao on 2015/11/19.
 */
public class Searcher {
    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "H:\\indexDir";
        search(indexDir, "class");


    }

    private static void search(String indexDir, String s) throws IOException, ParseException {
        Directory dir = FSDirectory.open(new File(indexDir));
        IndexSearcher searcher = new IndexSearcher(dir);
        QueryParser parser = new QueryParser(Version.LUCENE_33, "文档", new StandardAnalyzer(Version.LUCENE_33));
        Query query = parser.parse(s);
        long start = System.currentTimeMillis();
        TopDocs hits = searcher.search(query, 10);

        System.out.println("matched " + hits.totalHits + "  using  " + (System.currentTimeMillis() - start));

        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("路径"));
        }

        searcher.close();

    }


}
