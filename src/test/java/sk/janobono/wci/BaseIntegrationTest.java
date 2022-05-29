package sk.janobono.wci;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>
            ("postgres:13-alpine")
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    public static GreenMail smtpServer = new GreenMail(new ServerSetup(2525, null, "smtp"));

    static {
        smtpServer.setUser("wci", "wci");
        smtpServer.start();
    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) throws Exception {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);

        registry.add("spring.mail.username", () -> {
            return "wci";
        });
        registry.add("spring.mail.password", () -> {
            return "wci";
        });

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());

        registry.add("app.jwt-private-key", () -> {
            return Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
        });

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());

        registry.add("app.jwt-public-key", () -> {
            return Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded());
        });
    }

    @Value("${local.server.port}")
    public int serverPort;

    @Autowired
    public MockMvc mvc;

    @Autowired
    public Flyway flyway;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @Autowired
    public ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        flyway.clean();
        flyway.migrate();
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    public String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    public <T> List<T> mapListFromJson(String json, Class<T> paramClazz) throws Exception {
        return getListFromNode(objectMapper.readTree(json), paramClazz);
    }

    public <T> Page<T> mapPagedResponse(String json, Class<T> paramClazz)
            throws IOException {
        JsonNode parent = objectMapper.readTree(json);
        return new PageImpl<>(
                getListFromNode(parent.get("content"), paramClazz),
                PageRequest.of(
                        parent.get("pageable").get("pageNumber").asInt(),
                        parent.get("pageable").get("pageSize").asInt()),
                parent.get("totalElements").asLong());
    }

    public <T> List<T> getListFromNode(JsonNode node, Class<T> clazz) throws IOException {
        List<T> content = new ArrayList<>();
        for (JsonNode val : node) {
            content.add(objectMapper.readValue(val.traverse(), clazz));
        }
        return content;
    }

    public Map<String, Object> pageableToParams(Pageable pageable) {
        Map<String, Object> result = new HashMap<>();
        if (pageable.isPaged()) {
            result.put("page", pageable.getPageNumber());
            result.put("size", pageable.getPageSize());
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
}
