package cn.itcast.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class LuceneFirst {
    @Test
    public void createIndex() throws Exception{
//        1.创建一个Directory对象，指定索引库保存的位置；
//            把索引库保存在内存中
//        Directory directory = new RAMDirectory();
//            把索引库保存在磁盘
        Directory directory = FSDirectory.open(new File("D:\\java\\basic-code\\index").toPath());
//        2.基于Dirctory对象创建一个IndexWriter对象
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory,config);
//        3.读取磁盘上的文件，对应每个文件创建一个文档对象
        File dir = new File("E:\\传智播客\\03_框架_项目\\课程\\Lucene\\Lucene\\资料\\searchsource");
        File[] files = dir.listFiles();
        for (File file : files) {
//            取文件名
            String name = file.getName();
//            文件的路径
            String path = file.getPath();
//            文件内容
            String fileContext = FileUtils.readFileToString(file,"utf-8");
//            文件大小
            long size = FileUtils.sizeOf(file);
//            创建field
//            参数1：域的名称；参数2：域的内容；参数3：是否存储
            Field fieldName = new TextField("name", name, Field.Store.YES);
//          Field fieldPath = new TextField("path", path, Field.Store.YES);
            Field fieldPath = new StoredField("path", path);
            Field fieldFileContext = new TextField("fileContext", fileContext, Field.Store.YES);
//          Field fieldSize = new TextField("size", size + "", Field.Store.YES);
            LongPoint fieldSizeValue = new LongPoint("size", size);
            StoredField fieldSizeStore = new StoredField("size", size);
//        创建文档对象
            Document document = new Document();

//            4.向文档对象中添加域
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldFileContext);
            document.add(fieldSizeValue);
            document.add(fieldSizeStore);
//        5.把文档对象写入索引库
            indexWriter.addDocument(document);
        }
//        6.关闭indexWriter对象
        indexWriter.close();
    }
    @Test
    public void searchIndex() throws Exception {
        //1、创建一个Director对象，指定索引库的位置
        Directory directory = FSDirectory.open(new File("D:\\java\\basic-code\\index").toPath());
        //2、创建一个IndexReader对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //3、创建一个IndexSearcher对象，构造方法中的参数indexReader对象。
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4、创建一个Query对象，TermQuery
        Query query = new TermQuery(new Term("fileContext", "spring"));
        //5、执行查询，得到一个TopDocs对象
        //参数1：查询对象 参数2：查询结果返回的最大记录数
        TopDocs topDocs = indexSearcher.search(query, 10);
        //6、取查询结果的总记录数
        System.out.println("查询总记录数：" + topDocs.totalHits);
        //7、取文档列表
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //8、打印文档中的内容
        for (ScoreDoc doc :
                scoreDocs) {
            //取文档id
            int docId = doc.doc;
            //根据id取文档对象
            Document document = indexSearcher.doc(docId);
            System.out.println(document.get("name"));
            System.out.println(document.get("path"));
            System.out.println(document.get("size"));
            //System.out.println(document.get("content"));
            System.out.println("-----------------寂寞的分割线");
        }
        //9、关闭IndexReader对象
        indexReader.close();
    }

    @Test
    public void testTokenStream()throws Exception{
        //1）创建一个Analyzer对象，StandardAnalyzer对象
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //2）使用分析器对象的tokenStream方法获得一个TokenStream对象
        TokenStream tokenStream = analyzer.tokenStream("",
                "2017年12月14日 - 传智播客Lucene概述公安局Lucene是一款高性能的、可扩展的信息检索(IR)工具库。信息检索是指文档搜索、文档内信息搜索或者文档相关的元数据搜索等操作。");
        //3）向TokenStream对象中设置一个引用，相当于数一个指针d
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //4）调用TokenStream对象的rest方法。如果不调用抛异常
        tokenStream.reset();
        //5）使用while循环遍历TokenStream对象
        while (tokenStream.incrementToken()){
            System.out.println(charTermAttribute.toString());
        }
        //6）关闭TokenStream对象
        tokenStream.close();
    }
}
