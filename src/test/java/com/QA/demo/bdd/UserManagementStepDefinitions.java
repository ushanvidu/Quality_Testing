package com.QA.demo.bdd;

import com.QA.demo.model.User;
import com.QA.demo.service.UserService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserManagementStepDefinitions {

    @Autowired
    private UserService userService;

    private User user;
    private Exception exception;
    private User savedUser;
    private User existingUser; // Add this field

    @Given("I have a user with name {string}, email {string}, and age {int}")
    public void i_have_a_user_with_name_email_and_age(String name, String email, Integer age) {
        user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
    }

    @Given("a user with email {string} already exists in the system")
    public void a_user_with_email_already_exists_in_the_system(String email) {
        // Create the existing user but handle the duplicate email case
        existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail(email);
        existingUser.setAge(25);

        try {
            userService.createUser(existingUser);
        } catch (RuntimeException e) {
            // If user already exists, that's fine for our test setup
            // We just need to ensure a user with this email exists
            if (!e.getMessage().equals("Email already exists")) {
                throw e; // Re-throw if it's a different error
            }
            // Try to find the existing user
            existingUser = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Failed to setup test: user with email " + email + " should exist"));
        }
    }

    @Given("a user with ID exists in the system")
    public void a_user_with_id_exists_in_the_system() {
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setAge(25);
        savedUser = userService.createUser(testUser);
    }

    @Given("no user with ID {long} exists in the system")
    public void no_user_with_id_exists_in_the_system(Long id) {
        // Clean state - no setup needed
    }

    @When("I add the user to the system")
    public void i_add_the_user_to_the_system() {
        try {
            savedUser = userService.createUser(user);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("I try to add another user with email {string}")
    public void i_try_to_add_another_user_with_email(String email) {
        User newUser = new User();
        newUser.setName("Duplicate User");
        newUser.setEmail(email);
        newUser.setAge(30);

        try {
            savedUser = userService.createUser(newUser);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("I delete the user")
    public void i_delete_the_user() {
        try {
            userService.deleteUser(savedUser.getId());
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("I try to delete user with ID {long}")
    public void i_try_to_delete_user_with_id(Long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the user should be saved successfully")
    public void the_user_should_be_saved_successfully() {
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertNull(exception);
    }

    @Then("the response should contain the user details")
    public void the_response_should_contain_the_user_details() {
        assertNotNull(savedUser);
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getAge(), savedUser.getAge());
    }

    @Then("the system should return a duplicate email error")
    public void the_system_should_return_a_duplicate_email_error() {
        assertNotNull(exception);
        assertEquals("Email already exists", exception.getMessage());
        assertNull(savedUser);
    }

    @Then("the user should be removed from the system")
    public void the_user_should_be_removed_from_the_system() {
        assertNull(exception);

        // Verify the user is actually deleted by checking if getUserById returns empty
        Optional<User> deletedUser = userService.getUserById(savedUser.getId());
        assertFalse(deletedUser.isPresent(),
                "User should be deleted but still exists");
    }

    @Then("the system should return a user not found error")
    public void the_system_should_return_a_user_not_found_error() {
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("User not found"));
    }
}