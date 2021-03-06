package uk.co.boardgame.functional;

import com.jcabi.matchers.RegexMatchers;
import org.junit.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

public class GameFunctionalSpec extends BaseFunctionalSpec {

    @Test
    public void createANewGameShouldReturnTheLocationOfTheNewGame() throws Exception {
                post(url("/game"))
                .then()
                .statusCode(201)
                .header("Location", RegexMatchers.matchesPattern("/game/.+"));
    }

    @Test
    public void stateOfTheNewGameShouldBeInProgress() throws Exception {
        String newGameResource = post(url("/game"))
                                    .then()
                                    .statusCode(201).extract().header("Location");

        get(url(newGameResource + "/state"))
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("api-specification/schemas/game_state.json"))
                .body("state", equalTo("IN_PROGRESS"));
    }

    @Test
    public void gettingStateOfANonExistentGameShouldResultInFailure() throws Exception {
        get(url("/game/unknowngame/state"))
                .then()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("api-specification/schemas/error.json"))
                .body("errors.code", hasItem("GAME_NOT_FOUND"));
    }


}
