package sk.janobono.wci;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseRestClient {

    protected final RestTemplate restTemplate;

    protected final ObjectMapper objectMapper;

    public BaseRestClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        this.restTemplate.getMessageConverters().add(0, converter);
        this.objectMapper = objectMapper;
    }

    protected <T> Page<T> getForPage(String pageUrl, Pageable pageable, Class<T> clazz) {
        Map<String, String> params = pageableToParams(pageable);
        String url = getUrl(pageUrl, params);
        ObjectNode objectNode = restTemplate.getForObject(url, ObjectNode.class, params);
        return getPage(objectNode, pageable, clazz);
    }

    protected <T> Page<T> postForPage(String pageUrl, Object searchCriteria, Pageable pageable, Class<T> clazz) {
        Map<String, String> params = pageableToParams(pageable);
        String url = getUrl(pageUrl, params);
        ObjectNode objectNode = restTemplate.postForObject(url, searchCriteria, ObjectNode.class, params);
        return getPage(objectNode, pageable, clazz);
    }

    protected <T> Page<T> getPage(ObjectNode objectNode, Pageable pageable, Class<T> clazz) {
        List<T> content = null;
        Long totalElements = null;
        if (Objects.nonNull(objectNode)) {
            totalElements = objectNode.get("totalElements").asLong();
            try {
                content = getListFromNode(objectNode.get("content"), clazz);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Page<T> result;
        if (Objects.nonNull(content) && !content.isEmpty()) {
            result = new PageImpl<>(content, pageable, totalElements);
        } else {
            result = new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        return result;
    }

    protected <T> List<T> getListFromNode(JsonNode node, Class<T> clazz) throws IOException {
        List<T> content = new ArrayList<>();
        for (JsonNode val : node) {
            content.add(objectMapper.readValue(val.traverse(), clazz));
        }
        return content;
    }

    protected Map<String, String> pageableToParams(Pageable pageable) {
        Map<String, String> result = new HashMap<>();
        if (pageable.isPaged()) {
            result.put("page", Integer.toString(pageable.getPageNumber()));
            result.put("size", Integer.toString(pageable.getPageSize()));
            if (pageable.getSort().isSorted()) {
                StringBuilder sb = new StringBuilder();
                List<Sort.Order> orderList = pageable.getSort().get().filter(Sort.Order::isAscending).collect(Collectors.toList());
                if (!orderList.isEmpty()) {
                    for (Sort.Order order : orderList) {
                        sb.append(order.getProperty()).append(',');
                    }
                    sb.append("ASC,");
                }
                orderList = pageable.getSort().get().filter(Sort.Order::isDescending).collect(Collectors.toList());
                if (!orderList.isEmpty()) {
                    for (Sort.Order order : orderList) {
                        sb.append(order.getProperty()).append(',');
                    }
                    sb.append("DESC,");
                }
                String sort = sb.toString();
                sort = sort.substring(0, sort.length() - 1);
                result.put("sort", sort);
            }
        }
        return result;
    }

    protected String getUrl(String baseUrl, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl).append('?');
        for (String key : params.keySet()) {
            sb.append(key).append("={").append(key).append("}&");
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    protected String localDateTimeToString(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return "";
        }
        return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    protected String localDateToString(LocalDate localDate) {
        if (Objects.isNull(localDate)) {
            return "";
        }
        return localDate.format(DateTimeFormatter.ISO_DATE);
    }
}
