package com.week4.lucas.comment.mapper;

import com.week4.lucas.article.entity.Article;
import com.week4.lucas.base.dto.response.AuthorRes;
import com.week4.lucas.comment.dto.request.CommentReq;
import com.week4.lucas.comment.dto.response.CommentRes;
import com.week4.lucas.comment.entity.Comment;
import com.week4.lucas.user.entity.User;

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
