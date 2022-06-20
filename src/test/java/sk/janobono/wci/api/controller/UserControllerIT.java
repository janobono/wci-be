package sk.janobono.wci.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sk.janobono.wci.BaseIntegrationTest;
import sk.janobono.wci.api.service.so.UserDataSO;
import sk.janobono.wci.api.service.so.UserProfileSO;
import sk.janobono.wci.api.service.so.UserSO;
import sk.janobono.wci.common.Authority;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerIT extends BaseIntegrationTest {

    @Test
    @WithMockUser(username = "test", authorities = {"lt-admin"})
    public void fullTest() throws Exception {
        List<UserSO> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(addUser(i));
        }

        for (UserSO userSO : users) {
            assertThat(userSO).usingRecursiveComparison().isEqualTo(getUser(userSO.id()));
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id", "username");
        Page<UserSO> page = getUsers(pageable);
        assertThat(page.getTotalElements()).isEqualTo(14);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent().size()).isEqualTo(5);

        pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "id", "username");
        page = getUsers(pageable);
        assertThat(page.getTotalElements()).isEqualTo(14);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent().size()).isEqualTo(5);

        pageable = PageRequest.of(0, 5);
        page = getUsers("0", "user0", "mail0@domain.com", pageable);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getContent().size()).isEqualTo(1);

        for (UserSO userSO : users) {
            setUser(userSO);
            setAuthorities(userSO);
            setConfirmed(userSO);
            setEnabled(userSO);
            deleteUser(userSO.id());
        }
    }

    private UserSO getUser(UUID id) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/users/{0}", id)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        return mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
    }

    private Page<UserSO> getUsers(Pageable pageable) throws Exception {
        Map<String, Object> params = pageableToParams(pageable);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(
                "/users?page={0}&size={1}&sort={2}", params.get("page"), params.get("size"), params.get("sort"))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        return mapPagedResponse(mvcResult.getResponse().getContentAsString(), UserSO.class);
    }

    private Page<UserSO> getUsers(String searchField, String username, String email, Pageable pageable) throws Exception {
        Map<String, Object> params = pageableToParams(pageable);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(
                "/users/by-search-criteria?search-field={0}&username={1}&email={2}&page={3}&size={4}&sort={5}",
                searchField, username, email, params.get("page"), params.get("size"), params.get("sort"))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        return mapPagedResponse(mvcResult.getResponse().getContentAsString(), UserSO.class);
    }

    private UserSO addUser(int index) throws Exception {
        UserDataSO userDataSO = new UserDataSO(
                "user" + index,
                "before" + index,
                "First" + index,
                "Mid" + index,
                "Last" + index,
                "after" + index,
                "mail" + index + "@domain.com",
                false,
                false,
                List.of(Authority.WCI_CUSTOMER)
        );
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(userDataSO))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        return mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
    }

    private void setUser(UserSO userSO) throws Exception {
        UserProfileSO userProfileSO = new UserProfileSO(
                userSO.titleBefore() + "changed",
                userSO.firstName() + "changed",
                null,
                userSO.lastName() + "changed",
                userSO.titleAfter() + "changed"
        );
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put("/users/{0}", userSO.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(userProfileSO))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        UserSO result = mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
        assertThat(result.midName()).isNull();
    }

    private void setAuthorities(UserSO userSO) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch("/users/{0}/authorities", userSO.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(Arrays.asList(Authority.WCI_CUSTOMER, Authority.WCI_EMPLOYEE)))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        UserSO result = mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
        assertThat(result.authorities().size()).isEqualTo(2);
    }

    private void setConfirmed(UserSO userSO) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch("/users/{0}/confirm", userSO.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(Boolean.TRUE))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        UserSO result = mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
        assertThat(result.confirmed()).isTrue();
    }

    private void setEnabled(UserSO userSO) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch("/users/{0}/enable", userSO.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(Boolean.TRUE))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        UserSO result = mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
        assertThat(result.enabled()).isTrue();
    }

    private void deleteUser(UUID id) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete("/users/{0}", id)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        mvcResult = mvc.perform(MockMvcRequestBuilders.get("/users/{0}", id)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
