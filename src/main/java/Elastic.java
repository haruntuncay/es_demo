import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class Elastic {

    private static RestHighLevelClient client;
    private static String index = "deneme2";
    private static String HOST_PROP_NAME = "elastic_host";
    private static String PORT_PROP_NAME = "elastic_port";
    private static String SCHEME_PROP_NAME = "elastic_scheme";
    private static String host;
    private static String scheme;
    private static int port;

    static {
        init();
    }

    static void indexDataInBulk(Data data) {
        if(data == null || data.getRowSize() == 0)
            return;

        BulkRequest request = new BulkRequest();
        for(List<Object> dataRow : data) {
            Map<String, String> jsonMap = new LinkedHashMap<>();
            for(int i = 0; i < Data.fieldNames.length; i++)
                jsonMap.put(Data.fieldNames[i], dataRow.get(i).toString());
            request.add(new IndexRequest(index).source(jsonMap));
        }
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        try {
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                DocWriteResponse itemResponse = bulkItemResponse.getResponse();

                switch (bulkItemResponse.getOpType()) {
                    case INDEX:
                    case CREATE:
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        System.out.println(indexResponse);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Data getAll() {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        Data data = new Data();
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        if(searchHits.length == 0)
            return data;
        for (SearchHit hit : searchHits) {
            System.out.println(hit);
            data.appendRow(hit.getSourceAsMap());
        }

        return data;
    }

    static void init() {
        Properties properties = new Properties();

        try {
            properties.load(new FileReader("app.properties"));
        } catch (IOException e) {
            System.out.println("app.properties dosyası bulunamadı.");
            System.exit(1);
        }

        host = properties.getProperty(HOST_PROP_NAME, "localhost");
        port = Integer.parseInt(properties.getProperty(PORT_PROP_NAME, "9200"));
        scheme = properties.getProperty(SCHEME_PROP_NAME, "http");

        client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, scheme)));
    }

    public static void close() {
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("ElasticSearch istemcisini kapatırken hata oluştu.");
            e.printStackTrace();
        }
    }

    public static RestHighLevelClient client() {
        return client;
    }

    public static String getHost() {
        return host;
    }

    public static String getScheme() {
        return scheme;
    }

    public static int getPort() {
        return port;
    }
}
