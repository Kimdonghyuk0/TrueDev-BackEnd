package com.kdh.truedev.comment.service;

import com.kdh.truedev.comment.dto.request.CommentReq;
import com.kdh.truedev.comment.dto.response.CommentPageRes;
import com.kdh.truedev.comment.dto.response.CommentRes;

public interface CommentService {

    CommentRes createComment(Long articleId, Long userId, CommentReq.CreateCommentReq req);

    CommentRes editComment(Long articleId, Long commentId, Long userId, CommentReq.EditCommentReq req);

    CommentPageRes getCommentList(Long articleId, Long userId, int page, int size);

    CommentPageRes getCommentList(Long userId, int page, int size);

    boolean deleteComment(Long articleId, Long commentId, Long userId);
}
