package com.example.rappimoviewer

import io.restassured.response.Response
import org.hamcrest.Matchers.*
import com.example.rappimoviewer.api.ApiRoute
import com.example.rappimoviewer.models.Movie
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class RestApiTest {

    companion object {
        const val wait: Long = 60 * 1000
        const val TEST_PAGE = 1
        const val WRONG_QUERY_MOVIE_TO_FIND = "bad movie title 123"
    }

    @Test(timeout = wait)
    fun getUpcomingMovies() {
        System.out.println("RestApiTest --> Init test: getUpcomingMovies")
        val route = ApiRoute.GetUpcomingMovies(TEST_PAGE)
        val response = handleRequest(route)
        assert(response.statusCode == 200)
        response.then()
            .assertThat()
            .statusCode(200)
            .body("page", equalTo(1))
        val movies: List<Movie> = response.path("**.find { it.@type == 'results' }.item")
        assert(movies.isNotEmpty())
        RestAssured.reset()
    }

    @Test(timeout = wait)
    @Ignore("Site is down atm")
    fun getPopulates() {
        System.out.println("RestApiTest --> Init test: getPopulates")
        val route = ApiRoute.GetPopularMovies(TEST_PAGE)
        val response = handleRequest(route)
        assert(response.statusCode == 200)
        response.then()
            .assertThat()
            .statusCode(200)
            .body("page", equalTo(1))
        response.then().body("results.title", contains("Dark Phoenix", "Aladdin", "Captain Marvel"))
        val movies: List<Movie> = response.path("**.find { it.@type == 'results' }.item")
        assert(movies.isNotEmpty())
        RestAssured.reset()
    }

    @Test(timeout = wait)
    @Ignore("Site is down atm")
    fun getSortedMovies() {
        System.out.println("RestApiTest --> Init test: getSortedMovies")
        val route = ApiRoute.SortMovieBy(WRONG_QUERY_MOVIE_TO_FIND, TEST_PAGE)
        val response = handleRequest(route)

        assert(response.statusCode == 200)
        response.then()
            .assertThat()
            .statusCode(200)
        val movies: List<Movie> = response.path("**.find { it.@type == 'results' }.item")
        assert(movies.isEmpty())
        RestAssured.reset()
        Assert.assertTrue(false)
    }

    private fun handleRequest(route: ApiRoute): Response {
        val parameters = StringBuilder()
        var count = 0
        route.params.map {
            parameters.append("${it.key}=${it.value}")
            if (count < route.params.size) {
                parameters.append("&")
            }
            count++
        }

        val given = given().body(jsoMovienScheme)
        given.accept(ContentType.JSON)
        given.headers(route.headers)
        return given.get("${route.fullUrl}?$parameters")
    }

    private val jsoMovienScheme: String by lazy {
        "{\n" +
                "  \"'$'schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"page\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    },\n" +
                "    \"total_results\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    },\n" +
                "    \"total_pages\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    },\n" +
                "    \"results\": {\n" +
                "      \"type\": \"array\",\n" +
                "      \"items\": [\n" +
                "        {\n" +
                "          \"type\": \"object\",\n" +
                "          \"properties\": {\n" +
                "            \"vote_count\": {\n" +
                "              \"type\": \"integer\"\n" +
                "            },\n" +
                "            \"id\": {\n" +
                "              \"type\": \"integer\"\n" +
                "            },\n" +
                "            \"video\": {\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            \"vote_average\": {\n" +
                "              \"type\": \"number\"\n" +
                "            },\n" +
                "            \"title\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"popularity\": {\n" +
                "              \"type\": \"number\"\n" +
                "            },\n" +
                "            \"poster_path\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"original_language\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"original_title\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"genre_ids\": {\n" +
                "              \"type\": \"array\",\n" +
                "              \"items\": [\n" +
                "                {\n" +
                "                  \"type\": \"integer\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"integer\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            \"backdrop_path\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"adult\": {\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            \"overview\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"release_date\": {\n" +
                "              \"type\": \"string\"\n" +
                "            }\n" +
                "          },\n" +
                "          \"required\": [\n" +
                "            \"vote_count\",\n" +
                "            \"id\",\n" +
                "            \"video\",\n" +
                "            \"vote_average\",\n" +
                "            \"title\",\n" +
                "            \"popularity\",\n" +
                "            \"poster_path\",\n" +
                "            \"original_language\",\n" +
                "            \"original_title\",\n" +
                "            \"genre_ids\",\n" +
                "            \"backdrop_path\",\n" +
                "            \"adult\",\n" +
                "            \"overview\",\n" +
                "            \"release_date\"\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\n" +
                "    \"page\",\n" +
                "    \"total_results\",\n" +
                "    \"total_pages\",\n" +
                "    \"results\"\n" +
                "  ]\n" +
                "}"
    }

}