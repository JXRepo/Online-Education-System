
import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description Course search service implementation class
 * @date 2022/9/24 22:48
 */
@Slf4j
@Service
public class CourseSearchServiceImpl implements CourseSearchService {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;
    @Value("${elasticsearch.course.source_fields}")
    private String sourceFields;

    @Autowired
    RestHighLevelClient client;

    @Override
    public SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto courseSearchParam) {

        //Setting the index
        SearchRequest searchRequest = new SearchRequest(courseIndexStore);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //Source field filtering
        String[] sourceFieldsArray = sourceFields.split(",");
        searchSourceBuilder.fetchSource(sourceFieldsArray, new String[]{});
        if(courseSearchParam==null){
            courseSearchParam = new SearchCourseParamDto();
        }
        //Keywords
        if(StringUtils.isNotEmpty(courseSearchParam.getKeywords())){
            //Matching Keywords
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeywords(), "name", "description");
            //Set the matching ratio
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            //Increase the Boost value of another field
            multiMatchQueryBuilder.field("name",10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //Overthinking
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("mtName",courseSearchParam.getMt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("stName",courseSearchParam.getSt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
        //Pagination
        Long pageNo = pageParams.getPageNo();
        Long pageSize = pageParams.getPageSize();
        int start = (int) ((pageNo-1)*pageSize);
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(Math.toIntExact(pageSize));
        //Boolean Query
        searchSourceBuilder.query(boolQueryBuilder);
        //Highlight settings
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //Set the highlighted field
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //Request Search
        searchRequest.source(searchSourceBuilder);
        //Aggregation settings
        buildAggregation(searchRequest);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Course search exception：{}",e.getMessage());
            return new SearchPageResultDto<CourseIndex>(new ArrayList(),0,0,0);
        }

        //Result set processing
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        //Total number of records
        TotalHits totalHits = hits.getTotalHits();
        //Datasheets
        List<CourseIndex> list = new ArrayList<>();

        for (SearchHit hit : searchHits) {

            String sourceAsString = hit.getSourceAsString();
            CourseIndex courseIndex = JSON.parseObject(sourceAsString, CourseIndex.class);

            //Extract source
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //Course ID
            Long id = courseIndex.getId();
            //Take out the name
            String name = courseIndex.getName();
            //Extract the highlighted field content
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null){
                HighlightField nameField = highlightFields.get("name");
                if(nameField!=null){
                    Text[] fragments = nameField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.string());
                    }
                    name = stringBuffer.toString();

                }
            }
            courseIndex.setId(id);
            courseIndex.setName(name);

            list.add(courseIndex);

        }
        SearchPageResultDto<CourseIndex> pageResult = new SearchPageResultDto<>(list, totalHits.value,pageNo,pageSize);

        //Get aggregation results
        List<String> mtList= getAggregation(searchResponse.getAggregations(), "mtAgg");
        List<String> stList = getAggregation(searchResponse.getAggregations(), "stAgg");

        pageResult.setMtList(mtList);
        pageResult.setStList(stList);

        return pageResult;
    }


    private void buildAggregation(SearchRequest request) {
        request.source().aggregation(AggregationBuilders
                .terms("mtAgg")
                .field("mtName")
                .size(100)
        );
        request.source().aggregation(AggregationBuilders
                .terms("stAgg")
                .field("stName")
                .size(100)
        );

    }

    private List<String> getAggregation(Aggregations aggregations, String aggName) {
        // 4.1. Get the aggregation result according to the aggregation name
        Terms brandTerms = aggregations.get(aggName);
        // 4.2. Get buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3. Traversal
        List<String> brandList = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.4. Get the key
            String key = bucket.getKeyAsString();
            brandList.add(key);
        }
        return brandList;
    }
}
