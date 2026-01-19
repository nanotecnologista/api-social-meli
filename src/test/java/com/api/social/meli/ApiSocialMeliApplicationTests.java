package com.api.social.meli;

import com.api.social.meli.repository.mongo.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApiSocialMeliApplicationTests {

    @MockBean
    private PostRepository postRepository;

    @Test
    void contextLoads() {
    }

}
