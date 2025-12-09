package com.kdh.truedev.article.controller;

import com.kdh.truedev.article.dto.response.ArticleStatRes;
import com.kdh.truedev.article.service.ArticleService;
import com.kdh.truedev.exception.ApiErrorHandler;
import com.kdh.truedev.user.support.AuthTokenResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleAiVerificationController.class)
@Import(com.kdh.truedev.exception.ApiErrorHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class ArticleAiVerificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ArticleService articleService;

    @MockitoBean
    AuthTokenResolver authTokenResolver;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void 게시글_AI_검증_요청_202() throws Exception {
        given(authTokenResolver.resolveUserIdIfPresent()).willReturn(1L);
        given(articleService.enqueueVerify(5L, 1L)).willReturn("job-123");

        mockMvc.perform(post("/articles/{id}/verify", 5L))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("verify_enqueued"))
                .andExpect(jsonPath("$.data").value("job-123"));

        verify(authTokenResolver).resolveUserIdIfPresent();
        verify(articleService).enqueueVerify(5L, 1L);
        verifyNoMoreInteractions(articleService);
    }

    @Test
    void 게시글_AI_검증_권한없음_403() throws Exception {
        given(authTokenResolver.resolveUserIdIfPresent()).willReturn(2L);
        given(articleService.enqueueVerify(anyLong(), anyLong()))
                .willThrow(new ArticleService.ForbiddenException());

        mockMvc.perform(post("/articles/{id}/verify", 10L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("FORBIDDEN - 게시글 검증 권한 없음"));

        verify(authTokenResolver).resolveUserIdIfPresent();
        verify(articleService).enqueueVerify(10L, 2L);
        verifyNoMoreInteractions(articleService);
    }

    @Test
    void AI_검증_통계_조회_200() throws Exception {
        ArticleStatRes stat = new ArticleStatRes(3, 2, 1, 6);
        given(articleService.stats()).willReturn(stat);

        mockMvc.perform(get("/articles/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("get_stats_success"))
                .andExpect(jsonPath("$.data.verified").value(3))
                .andExpect(jsonPath("$.data.pending").value(2))
                .andExpect(jsonPath("$.data.failed").value(1))
                .andExpect(jsonPath("$.data.totalArticle").value(6));

        verify(articleService).stats();
        verifyNoMoreInteractions(articleService);
    }
}
