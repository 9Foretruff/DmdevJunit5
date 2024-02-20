package com.foretruff.junit.service;

import com.foretruff.junit.TestBase;
import com.foretruff.junit.dao.UserDao;
import com.foretruff.junit.dao.UserDaoMock;
import com.foretruff.junit.dao.UserDaoSpy;
import com.foretruff.junit.dto.User;
import com.foretruff.junit.extension.ConditionalExtension;
import com.foretruff.junit.extension.GlobalExtension;
import com.foretruff.junit.extension.PostProcessingExtension;
import com.foretruff.junit.extension.UserServiceParamResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.RepeatedTest.LONG_DISPLAY_NAME;

//@Tag("fast") unit
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({
        UserServiceParamResolver.class,
        GlobalExtension.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
//        ThrowableExtension.class
})
class UserServiceTest extends TestBase {
    // ctrl + alt + v || ctrl + alt + c
    private static final User IVAN = User.of(1, "Ivan", "777");
    private static final User VASYA = User.of(2, "Vasya", "123");
    private UserService userService;

    private UserDao userDao;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    static void init() {
        System.out.println("Before all:");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this.toString());
        this.userDao = Mockito.spy(new UserDao());
        this.userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
//        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
        Mockito.doReturn(true).when(userDao).delete(Mockito.anyInt());

//        Mockito.when(userDao.delete(Mockito.anyInt())).thenReturn(true).thenReturn(false);

        var deleteResult = userService.delete(IVAN.getId());
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));

        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.atLeast(2)).delete(integerArgumentCaptor.capture());
//        Mockito.verifyNoInteractions(userDao);
        assertThat(integerArgumentCaptor.getValue()).isEqualTo(IVAN.getId());

        assertThat(deleteResult).isTrue();
    }

    @Test
    @Order(1)
    @DisplayName("users will be empty if no user added")
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this.toString());
        var users = userService.getAll();

        assertThat(users)
                .as("User list should be empty")
                .isEmpty();
//        assertTrue(users.isEmpty(), () -> "User list should be empty");
        // input -> [box == func] -> actual output
    }

    @Test
    @Order(2)
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this.toString());
        userService.add(IVAN);
        userService.add(VASYA);

        var users = userService.getAll();

        assertThat(users).hasSize(2);
//        assertEquals(2, users.size());
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, VASYA);

        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), VASYA.getId()),
                () -> assertThat(users).containsValues(IVAN, VASYA)
        );
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this.toString());
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all: ");
    }

    @Nested
    @Tag("login")
    @DisplayName("Test user login functionality")
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    class LoginTest {
        @Test
        @Tag("login")
        @Disabled("flaky , need to see")
        void loginFailedIfPasswordIsNotCorrect() {
            userService.add(IVAN);

            var maybeUser = userService.login(IVAN.getUsername(), "oops");

            assertTrue(maybeUser.isEmpty());
        }

        //        @Test
        @RepeatedTest(value = 10, name = LONG_DISPLAY_NAME)
        @Tag("login")
        void loginFailedIfUserDoesNotExist(RepetitionInfo repetitionInfo) {
            userService.add(IVAN);

            var maybeUser = userService.login("oops", "oops");

            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void checkLoginFunctionalityPerformance() {
            var maybeUser = assertTimeout(Duration.ofMillis(200), () -> {
//                Thread.sleep(100);
                return userService.login("oops", "oops");
            });
        }

        @Test
        @Tag("login")
        void loginSuccessIfUserExists() {
            userService.add(IVAN);

            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));

//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
        }

        @Test
        @Tag("login")
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "oops"));
                        assertThat(exception.getMessage()).isEqualTo("Username or password is null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("oops", null)),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, null))
            );
        }

        @ParameterizedTest()
        // @ArgumentsSources()
//        @EmptySource
//        @NullSource // Могуть быть использованы только если в методе один параметр
//        @NullAndEmptySource
//        @ValueSource(strings = {"Ivan", "Petya"})
//        @EnumSource()
        @MethodSource("com.foretruff.junit.service.UserServiceTest#getArgumentsForLoginTest")
//        @CsvFileSource(resources = "/login-test-data.csv", numLinesToSkip = 1)
//        @CsvSource({
//                "Ivan,123",
//                "Vasya,777"
//        })
        @DisplayName("login param test")
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, VASYA);

            var maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }

    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Vasya", "123", Optional.of(VASYA)),
                Arguments.of("Ivan", "777", Optional.of(IVAN)),
                Arguments.of("Ivan", "oppps", Optional.empty()),
                Arguments.of("oppps", "777", Optional.empty())
        );
    }

}
