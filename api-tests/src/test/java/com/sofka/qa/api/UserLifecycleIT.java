package com.sofka.qa.api;

import com.intuit.karate.junit5.Karate;

class UserLifecycleIT {
    @Karate.Test
    Karate userLifecycle() {
        return Karate.run("classpath:users/lifecycle.feature");
    }
}
