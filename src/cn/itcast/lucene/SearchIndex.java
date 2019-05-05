package cn.itcast.lucene;

import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.swing.text.Document;
import java.io.File;

public class SearchIndex {

    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    @Before
    public void init() throws Exception {
        indexReader = DirectoryReader.open(FSDirectory.open(new File("D:\\java\\basic-code\\index").toPath()));
        indexSearcher = new IndexSearcher(indexReader);
    }

    private void printResult(Query query) throws Exception{
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("总记录数：" + topDocs.totalHits);
        ScoreDoc[] docs = topDocs.scoreDocs;
        for (ScoreDoc doc : docs) {
            int docId = doc.doc;
            org.apache.lucene.document.Document document = indexSearcher.doc(docId);
            System.out.println(document.get("name"));
            System.out.println(document.get("path"));
            System.out.println(document.get("size"));
            System.out.println(document.get("fileContext"));
        }
        indexReader.close();
    }

    @Test
    public void testRangeQuery() throws Exception{
        Query query = LongPoint.newRangeQuery("size", 0l, 100l);
        printResult(query);
    }

    @Test
    public void testQueryParser() throws Exception{
        //创建一个QueryPaser对象，两个参数
        QueryParser queryParser = new QueryParser("name",new IKAnalyzer());
        //参数1：默认搜索域，参数2：分析器对象
        //使用QueryPaser对象创建一个Query对象
        Query parse = queryParser.parse("lucene是一个Java开发的全文检索工具包");
        printResult(parse);
    }
}
