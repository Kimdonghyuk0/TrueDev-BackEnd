package com.week4.lucas.comment.service;

import com.week4.lucas.comment.dto.request.CommentReq;
import com.week4.lucas.comment.dto.response.CommentPageRes;
import com.week4.lucas.comment.dto.response.CommentRes;

public interface CommentService {

    CommentRes createComment(Long articleId, Long userId, CommentReq.CreateCommentReq req);

    CommentRes editComment(Long articleId, Long commentId, Long userId, CommentReq.EditCommentReq req);

    CommentPageRes getCommentList(Long articleId, Long userId, int page, int size);

    boolean deleteComment(Long articleId, Long commentId, Long userId);
}
