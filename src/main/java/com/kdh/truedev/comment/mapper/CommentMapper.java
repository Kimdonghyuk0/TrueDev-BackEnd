package com.kdh.truedev.comment.mapper;

import com.kdh.truedev.article.entity.Article;
import com.kdh.truedev.base.dto.response.AuthorRes;
import com.kdh.truedev.comment.dto.request.CommentReq;
import com.kdh.truedev.comment.dto.response.CommentRes;
import com.kdh.truedev.comment.entity.Comment;
import com.kdh.truedev.user.entity.User;

public class CommentMapper {
    public static Comment toEntity(Article article, User user, CommentReq.CreateCommentReq req) {
        return Comment.builder()
                .article(article)
                .user(user)
                .content(req.content())
                .build();
    }


    public static CommentRes toRes(Comment c,Long userId){
        String userName = (c.getUser() != null) ? c.getUser().getName() : null;
        String userImg  = (c.getUser() != null) ? c.getUser().getProfileImage() : null;
        boolean isAuthor = c.getUser() != null
                && c.getUser().getId().equals(userId);
        return new CommentRes(
                c.getId(),
                c.getArticle() != null ? c.getArticle().getId() : null,
                c.getContent(),
                c.getCommentCreatedAt(),
                c.getCommentEditedAt(),
                new AuthorRes(userName,userImg),
                isAuthor //현재 유저가 작성한 글인지
        );
    }
}
