package com.miranda.luna.lci.bdd.context;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class ScenarioContext {

    private ResultActions latestResult;
    private final Map<String, Object> context = new HashMap<>();

    public ResultActions getLatestResult() {
        return latestResult;
    }

    public void setLatestResult(ResultActions latestResult) {
        this.latestResult = latestResult;
    }

    public void set(String key, Object value) {
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    public boolean containsKey(String key) {
        return context.containsKey(key);
    }
}
