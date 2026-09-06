package org.qiyu.live.living.provider.scripts;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Guards the Lua invariants that are otherwise easy to regress during refactors. */
class PkLuaScriptContractTest {

    @Test
    void scoreScriptOnlyAcceptsRunningPkAndDeduplicatesBizId() throws Exception {
        String script = read("scripts/pk_score.lua");
        assertTrue(script.contains("status ~= 'RUNNING'"));
        assertTrue(script.contains("'NX', 'EX'"));
        assertTrue(script.contains("return {-2}"));
    }

    @Test
    void matchScriptRemovesMatchedOpponentAndCreatesWaitingMarker() throws Exception {
        String script = read("scripts/pk_match.lua");
        assertTrue(script.contains("redis.call('ZREM', poolKey, candidate)"));
        assertTrue(script.contains("redis.call('SETEX', myWaitKey, ttl, myScore)"));
    }

    @Test
    void settlementScriptClaimsRunningPkBeforeFinishingIt() throws Exception {
        String script = read("scripts/pk_settling.lua");
        assertTrue(script.contains("status ~= 'RUNNING'"));
        assertTrue(script.contains("'SETTLING'"));
    }

    private String read(String path) throws Exception {
        try (var input = new ClassPathResource(path).getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
