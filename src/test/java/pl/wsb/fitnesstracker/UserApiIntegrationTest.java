package pl.wsb.fitnesstracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class UserApiIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    public static User generateUser() {
        return new User(
                randomUUID().toString(),
                randomUUID().toString(),
                LocalDate.now(),
                randomUUID().toString()
        );
    }

    @Test
    void shouldReturnAllUsers_whenGettingAllUsers() throws Exception {

        User user1 = existingUser(generateUser());
        User user2 = existingUser(generateUser());

        mockMvc.perform(
                        get("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$[0].firstName")
                        .value(user1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName")
                        .value(user1.getLastName()))
                .andExpect(jsonPath("$[0].birthdate")
                        .value(ISO_DATE.format(user1.getBirthdate())))
                .andExpect(jsonPath("$[0].email")
                        .value(user1.getEmail()))

                .andExpect(jsonPath("$[1].firstName")
                        .value(user2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName")
                        .value(user2.getLastName()))
                .andExpect(jsonPath("$[1].birthdate")
                        .value(ISO_DATE.format(user2.getBirthdate())))
                .andExpect(jsonPath("$[1].email")
                        .value(user2.getEmail()));
    }

    @Test
    void shouldReturnDetailsAboutUser_whenGettingUserById() throws Exception {

        User user = existingUser(generateUser());

        mockMvc.perform(
                        get("/v1/users/{id}", user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(user.getId()))
                .andExpect(jsonPath("$.firstName")
                        .value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName")
                        .value(user.getLastName()))
                .andExpect(jsonPath("$.birthdate")
                        .value(ISO_DATE.format(user.getBirthdate())))
                .andExpect(jsonPath("$.email")
                        .value(user.getEmail()));
    }

    @Test
    void shouldPersistUser_whenCreatingUser() throws Exception {

        final String USER_NAME = "Mike";
        final String USER_LAST_NAME = "Scott";
        final String USER_BIRTHDATE = "1999-09-29";
        final String USER_EMAIL = "mike.scott@domain.com";

        String request = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "birthdate": "%s",
                  "email": "%s"
                }
                """.formatted(
                USER_NAME,
                USER_LAST_NAME,
                USER_BIRTHDATE,
                USER_EMAIL
        );

        mockMvc.perform(
                        post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk());

        List<User> users = getAllUsers();

        assertThat(users).hasSize(1);

        User user = users.get(0);

        assertThat(user.getFirstName()).isEqualTo(USER_NAME);
        assertThat(user.getLastName()).isEqualTo(USER_LAST_NAME);
        assertThat(user.getBirthdate())
                .isEqualTo(LocalDate.parse(USER_BIRTHDATE));
        assertThat(user.getEmail()).isEqualTo(USER_EMAIL);
    }
}