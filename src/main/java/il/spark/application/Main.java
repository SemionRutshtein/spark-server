package il.spark.application;

import il.spark.application.util.JsonTransformer;
import il.spark.application.util.MoodHolder;
import spark.debug.DebugScreen;

import java.util.Collections;
import java.util.HashMap;

import static il.spark.application.starter.Starter.gson;
import static il.spark.application.starter.Starter.render;
import static spark.Spark.*;
import static spark.Spark.notFound;

public class Main {

    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/public");

        DebugScreen.enableDebugScreen();

        get("/", (req, res) -> "Hello World");
        post("/:name", (req, res) -> {
            String name = req.params("name");
            MoodHolder mood = gson.fromJson(req.body(), MoodHolder.class);

            HashMap<String, String> map = new HashMap<>();

            map.put("name", name);
            map.put("device", req.queryParams("device"));
            map.put("user-age", req.queryMap().get("user").get("age").value());
            map.put("user-gender", req.queryMap().get("user").get("gender").value());
            map.put("mood", mood.getMood());
//            halt(403, "because");

            return map;
        }, new JsonTransformer());

        path("/temp", () -> {
            get("/", (request, response) -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("userName", "World");
                return render(map, "index.html");
            });
            get("/everybody", (request, response) -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("userName", "everybody!!");
                return render(map, "index.html");
            });
            get("/:name", (request, response) -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("userName", request.params("name"));
                return render(map, "index.html");
            });
        });

        after((request, response) -> {
            response.header("Content-Encoding", "gzip");
        });

        notFound((req, res) -> {
            res.type("application/json");
            return gson.toJson(Collections.singletonMap("error", "have not pages like this"));
        });
    }
}
