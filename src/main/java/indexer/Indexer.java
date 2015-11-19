package indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;

/**
 * Created by hxiao on 2015/11/19.
 */
public class Indexer {
    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(new File(indexDir));
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_33);

        writer = new IndexWriter(dir, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

    }

    public static void main(String[] args) throws Exception {
        String indexDir = "H:\\indexDir";
        String dataDir = "H:\\dataDir";
        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new HtmlFilesFilter());
        } finally {
            indexer.close();
        }
        System.out.println("搜索到 " + numIndexed + "文件夹 using  " + (System.currentTimeMillis() - start));

    }

    public void close() throws IOException {
        writer.close();
    }

    public int index(String dataDIr, FileFilter filter) throws Exception {

        File[] files = new File(dataDIr).listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                index(f.getCanonicalPath(), filter);
            } else if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() && (filter == null || filter.accept(f))) {
                indexFile(f);
            }
        }
        return writer.numDocs();

    }

    private void indexFile(File f) throws Exception {
        System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }

    private Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new Field("文档", new FileReader(f)));
        doc.add(new Field("文件名", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("路径", f.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        return doc;
    }

    private static class HtmlFilesFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".html");
        }
    }
}
